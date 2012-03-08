/***************************************************************************
 *
 * R2RML : R2RML Engine
 *
 * (C) 2011 Antidot (http://www.antidot.net)
 *
 * Module			:	R2RML
 * 
 * Fichier			:	R2RMLEngine.java
 *
 * Description		:	The R2RML engine is the link between a R2RML Mapping object
 * 						and a database connection. It constructs the final RDF graph
 * 						from the R2RML mapping document by extracting data in database.
 * 
 *
 * Auteurs(s)			:	JHO
 *
 *
 ****************************************************************************/
package net.antidot.semantic.rdf.rdb2rdf.r2rml.core;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.antidot.semantic.rdf.model.impl.sesame.SesameDataSet;
import net.antidot.semantic.rdf.model.tools.RDFDataValidator;
import net.antidot.semantic.rdf.rdb2rdf.commons.SQLToXMLS;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.core.R2RMLVocabulary.R2RMLTerm;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.R2RMLDataError;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.model.GraphMap;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.model.ObjectMap;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.model.PredicateMap;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.model.PredicateObjectMap;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.model.R2RMLMapping;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.model.ReferencingObjectMap;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.model.SubjectMap;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.model.TermMap;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.model.TermType;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.model.TriplesMap;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.model.TermMap.TermMapType;
import net.antidot.semantic.xmls.xsd.XSDType;
import net.antidot.sql.model.type.SQLType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openrdf.model.BNode;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

public class R2RMLEngine {

	// Log
	private static Log log = LogFactory.getLog(R2RMLEngine.class);

	// SQL Connection
	private Connection conn;
	// Current logical table
	private ResultSet rows;
	// Current referencing table
	private ResultSet referencingRows;
	// Current meta data of logical table
	private ResultSetMetaData meta;
	// A base IRI used in resolving relative IRIs produced by the R2RML mapping.
	private String baseIRI;

	// Value factory
	private static ValueFactory vf = new ValueFactoryImpl();

	public R2RMLEngine(Connection conn) {
		super();
		if (conn == null)
			throw new IllegalStateException(
					"[R2RMLEngine:R2RMLEngine] SQL connection does not exists.");
		this.conn = conn;
		rows = null;
	}

	/**
	 * Execute R2RML Mapping from a R2RML file in order to generate a RDF
	 * dataset. This dataset is built with Sesame API.
	 * 
	 * @param r2rmlMapping
	 * @param baseIRI
	 * @return
	 * @throws SQLException
	 * @throws R2RMLDataError
	 */
	public SesameDataSet runR2RMLMapping(R2RMLMapping r2rmlMapping,
			String baseIRI, String pathToNativeStore) throws SQLException,
			R2RMLDataError {
		log.debug("[R2RMLEngine:runR2RMLMapping] Run R2RML mapping... ");
		if (r2rmlMapping == null)
			throw new IllegalArgumentException(
					"[R2RMLEngine:runR2RMLMapping] No R2RML Mapping object found.");
		if (baseIRI == null)
			throw new IllegalArgumentException(
					"[R2RMLEngine:runR2RMLMapping] No base IRI found.");

		SesameDataSet sesameDataSet = null;
		// Update baseIRI
		this.baseIRI = baseIRI;
		// Check if use of native store is required
		if (pathToNativeStore != null) {
			log.debug("[R2RMLEngine:runR2RMLMapping] Use native store "
					+ pathToNativeStore);
			sesameDataSet = new SesameDataSet(pathToNativeStore, false);
		} else {
			sesameDataSet = new SesameDataSet();
		}

		// Explore R2RML Mapping TriplesMap objects
		generateRDFTriples(sesameDataSet, r2rmlMapping);

		// Close connection to logical table
		if (rows != null) {
			rows.getStatement().close();
			rows.close();
		}
		log.debug("[R2RMLEngine:runR2RMLMapping] R2RML mapping done. ");
		return sesameDataSet;
	}

	/**
	 * This process adds RDF triples to the output dataset. Each generated
	 * triple is placed into one or more graphs of the output dataset. The
	 * generated RDF triples are determined by the following algorithm.
	 * 
	 * @param sesameDataSet
	 * @param r2rmlMapping
	 * @throws SQLException
	 * @throws R2RMLDataError
	 */
	private void generateRDFTriples(SesameDataSet sesameDataSet,
			R2RMLMapping r2rmlMapping) throws SQLException, R2RMLDataError {
		log.debug("[R2RMLEngine:generateRDFTriples] Generate RDF triples... ");
		for (TriplesMap triplesMap : r2rmlMapping.getTriplesMaps())
			genereateRDFTriplesFromTriplesMap(sesameDataSet, triplesMap);
	}

	private void genereateRDFTriplesFromTriplesMap(SesameDataSet sesameDataSet,
			TriplesMap triplesMap) throws SQLException, R2RMLDataError {
		log
				.debug("[R2RMLEngine:genereateRDFTriplesFromTriplesMap] Generate RDF triples from triples map... ");
		// 1. Let sm be the subject map of the triples map
		SubjectMap sm = triplesMap.getSubjectMap();
		// 2. Let rows be the result of evaluating the effective SQL query
		rows = constructLogicalTable(triplesMap);
		meta = rows.getMetaData();
		// 3. Let classes be the class IRIs of sm
		Set<URI> classes = sm.getClassIRIs();
		// 4. Let sgm be the set of graph maps of sm
		Set<GraphMap> sgm = sm.getGraphMaps();
		// 5. For each logical table row in rows, apply the following method
		while (rows.next()) {
			generateRDFTriplesFromRow(sesameDataSet, triplesMap, sm, classes,
					sgm);
		}
		// 6. For each referencing object map of a predicate-object map of the
		// triples map,
		// apply the following method
		for (PredicateObjectMap predicateObjectMap : triplesMap
				.getPredicateObjectMaps())
			for (ReferencingObjectMap referencingObjectMap : predicateObjectMap
					.getReferencingObjectMaps()) {
				generateRDFTriplesFromReferencingObjectMap(sesameDataSet,
						triplesMap, sm, sgm, predicateObjectMap,
						referencingObjectMap);
			}
	}

	private void generateRDFTriplesFromReferencingObjectMap(
			SesameDataSet sesameDataSet, TriplesMap triplesMap, SubjectMap sm,
			Set<GraphMap> sgm, PredicateObjectMap predicateObjectMap,
			ReferencingObjectMap referencingObjectMap) throws SQLException,
			R2RMLDataError {
		// 1. Let psm be the subject map of the parent triples map of the
		// referencing object map
		SubjectMap psm = referencingObjectMap.getParentTriplesMap()
				.getSubjectMap();
		// 2. Let pogm be the set of graph maps of the predicate-object map
		Set<GraphMap> pogm = predicateObjectMap.getGraphMaps();
		// 3. Let n be the number of columns in the logical table of the triples
		// map
		int n = meta.getColumnCount();
		// 4. Let rows be the result of evaluating the joint SQL query of the
		// referencing object map
		referencingRows = constructJointTable(referencingObjectMap);
		// 5. For each row in rows, apply the following method
		while (referencingRows.next())
			generateRDFTriplesFromReferencingRow(sesameDataSet, triplesMap, sm,
					psm, pogm, sgm, predicateObjectMap, n);
	}

	private void generateRDFTriplesFromReferencingRow(
			SesameDataSet sesameDataSet, TriplesMap triplesMap, SubjectMap sm,
			SubjectMap psm, Set<GraphMap> pogm,
			Set<GraphMap> sgm, PredicateObjectMap predicateObjectMap, int n) throws SQLException,
			R2RMLDataError {
		// 1. Let child_row be the logical table row derived by
		// taking the first n columns of row : see step 3, 4, 6 and 7 
		// 2. Let parent_row be the logical table row derived by taking all
		// but the first n columns of row : see step 5
		// 3. Let subject be the generated RDF term that results from
		// applying sm to child_row
		Map<String, String> smFromRow = applyValueToChildRow(sm, n);
		String value = sm.getValue(smFromRow);
		Resource subject = (Resource) generateRDFTerm(sm, value);
		log
				.debug("[R2RMLEngine:generateRDFTriplesFromReferencingRow] Generate subject : "
						+ value);
		// 4. Let predicates be the set of generated RDF terms that result from
		// applying each of the predicate-object map's predicate maps to
		// child_row
		Set<URI> predicates = new HashSet<URI>();
		for (PredicateMap pm : predicateObjectMap.getPredicateMaps()) {
			Map<String, String> pmFromRow = applyValueToChildRow(pm, n);
			String predicate_value = pm.getValue(pmFromRow);
			URI predicate = (URI) generateRDFTerm(pm, predicate_value);
			log
					.debug("[R2RMLEngine:generateRDFTriplesFromReferencingRow] Generate predicate : "
							+ predicate);
			predicates.add(predicate);
		}
		// 5. Let object be the generated RDF term that results from applying
		// psm to parent_row
		Map<String, String> omFromRow = applyValueToParentRow(psm, n);
		String psm_value = psm.getValue(omFromRow);
		Resource object = (Resource) generateRDFTerm(psm, psm_value);
		log
		.debug("[R2RMLEngine:generateRDFTriplesFromReferencingRow] Generate object : "
				+ object);
		// 6. Let subject_graphs be the set of generated RDF terms that result
		// from applying each graph map of sgm to child_row
		Set<URI> subject_graphs = new HashSet<URI>();
		for (GraphMap graphMap : sgm) {
			
			Map<String, String> sgmFromRow = applyValueToChildRow(graphMap, n);
			String graph_value = graphMap.getValue(sgmFromRow);
			URI subject_graph = (URI) generateRDFTerm(graphMap, graph_value);
			log
					.debug("[R2RMLEngine:generateRDFTriplesFromReferencingRow] Generate subject graph : "
							+ subject_graph);
			subject_graphs.add(subject_graph);
		}
		// 7. Let predicate-object_graphs be the set of generated RDF terms 
		// that result from applying each graph map in pogm to child_row
		Set<URI> predicate_object_graphs = new HashSet<URI>();
		for (GraphMap graphMap : pogm) {
			Map<String, String> pogmFromRow = applyValueToChildRow(graphMap, n);
			String graph_value = graphMap.getValue(pogmFromRow);
			URI predicate_object_graph = (URI) generateRDFTerm(graphMap,
					graph_value);
			log
					.debug("[R2RMLEngine:generateRDFTriplesFromReferencingRow] Generate predicate object graph : "
							+ predicate_object_graph);
			predicate_object_graphs.add(predicate_object_graph);
		}
		// 8. For each predicate in predicates, add triples to the output dataset
		for (URI predicate : predicates){
			// If neither sgm nor pogm has any graph maps: rr:defaultGraph; 
			// otherwise: union of subject_graphs and predicate-object_graphs
			Set<URI> targetGraphs = new HashSet<URI>();
			targetGraphs.addAll(subject_graphs);
			targetGraphs.addAll(predicate_object_graphs);
			addTriplesToTheOutputDataset(sesameDataSet, subject, predicate, object, targetGraphs);
		}
	}

	/**
	 * Parent_row is the logical table row derived by taking all but the first n
	 * columns of row.
	 * 
	 * @param tm
	 * @param n
	 * @return
	 * @throws SQLException
	 * @throws R2RMLDataError
	 */
	private Map<String, String> applyValueToParentRow(TermMap tm, int n)
			throws SQLException, R2RMLDataError {
		Map<String, String> result = new HashMap<String, String>();
		Set<String> columns = tm.getReferencedColumns();
		ResultSetMetaData referencingMetas = referencingRows.getMetaData();
		for (String column : columns) {
			int m = -1;
			for (int i = 1; i <= referencingMetas.getColumnCount(); i++) {
				if (referencingMetas.getColumnName(i).equals(column))
					m = i;
			}
			if (m == -1)
				throw new R2RMLDataError(
						"[R2RMLEngine:applyValueToChildRow] Unknown " + column
								+ "in child row.");
			if (m <= n) {
				String value = referencingRows.getString(column);
				result.put(column, value);
			}
		}
		return result;
	}

	/**
	 * Child_row is the logical table row derived by taking the first n columns
	 * of row.
	 * 
	 * @param tm
	 * @param n
	 * @return
	 * @throws SQLException
	 * @throws R2RMLDataError
	 */
	private Map<String, String> applyValueToChildRow(TermMap tm, int n)
			throws SQLException, R2RMLDataError {
		Map<String, String> result = new HashMap<String, String>();
		Set<String> columns = tm.getReferencedColumns();
		ResultSetMetaData referencingMetas = referencingRows.getMetaData();
		for (String column : columns) {
			int m = -1;
			for (int i = 1; i <= referencingMetas.getColumnCount(); i++) {
				if (referencingMetas.getColumnName(i).equals(column))
					m = i;
			}
			if (m == -1)
				throw new R2RMLDataError(
						"[R2RMLEngine:applyValueToChildRow] Unknown " + column
								+ "in child row.");
			if (m <= n) {
				String value = referencingRows.getString(column);
				result.put(column, value);
			}
		}
		return result;
	}

	private void generateRDFTriplesFromRow(SesameDataSet sesameDataSet,
			TriplesMap triplesMap, SubjectMap sm, Set<URI> classes,
			Set<GraphMap> sgm) throws SQLException, R2RMLDataError {
		// 1. Let subject be the generated RDF term that results from applying
		// sm to row
		Map<String, String> smFromRow = applyValueToRow(sm);
		String value = sm.getValue(smFromRow);
		Resource subject = (Resource) generateRDFTerm(sm, value);
		log
				.debug("[R2RMLEngine:genereateRDFTriplesFromRow] Generate subject : "
						+ value);
		// 2. Let subject_graphs be the set of the generated RDF terms
		// that result from applying each term map in sgm to row
		Set<URI> subject_graphs = new HashSet<URI>();
		for (GraphMap graphMap : sgm) {
			Map<String, String> sgmFromRow = applyValueToRow(graphMap);
			String graph_value = graphMap.getValue(sgmFromRow);
			URI subject_graph = (URI) generateRDFTerm(graphMap, graph_value);
			log
					.debug("[R2RMLEngine:genereateRDFTriplesFromRow] Generate subject graph : "
							+ subject_graph);
			subject_graphs.add(subject_graph);
		}
		// 3. For each classIRI in classes, add triples to the output dataset
		for (URI classIRI : sm.getClassIRIs()) {
			URI predicate = vf.createURI(R2RMLVocabulary.RDF_NAMESPACE
					+ R2RMLTerm.TYPE);
			addTriplesToTheOutputDataset(sesameDataSet, subject, predicate,
					classIRI, subject_graphs);
		}
		// 4. For each predicate-object map of the triples map, apply the
		// following method
		for (PredicateObjectMap predicateObjectMap : triplesMap
				.getPredicateObjectMaps())
			generateRDFTriplesFromPredicateObjectMap(sesameDataSet, triplesMap,
					subject, predicateObjectMap);

	}

	private void generateRDFTriplesFromPredicateObjectMap(
			SesameDataSet sesameDataSet, TriplesMap triplesMap,
			Resource subject, PredicateObjectMap predicateObjectMap)
			throws SQLException, R2RMLDataError {
		// 1. Let predicates be the set of generated RDF terms that result
		// from applying each of the predicate-object map's predicate maps to
		// row
		Set<URI> predicates = new HashSet<URI>();
		for (PredicateMap pm : predicateObjectMap.getPredicateMaps()) {
			Map<String, String> pmFromRow = applyValueToRow(pm);
			String value = pm.getValue(pmFromRow);
			URI predicate = (URI) generateRDFTerm(pm, value);
			log
					.debug("[R2RMLEngine:genereateRDFTriplesFromRow] Generate predicate : "
							+ predicate);
			predicates.add(predicate);
		}
		// 2. Let objects be the set of generated RDF terms that result from
		// applying each of
		// the predicate-object map's object maps (but not referencing object
		// maps) to row
		Set<Value> objects = new HashSet<Value>();
		for (ObjectMap om : predicateObjectMap.getObjectMaps()) {
			Map<String, String> pmFromRow = applyValueToRow(om);
			String value = om.getValue(pmFromRow);
			Value object = (Value) generateRDFTerm(om, value);
			log
					.debug("[R2RMLEngine:genereateRDFTriplesFromRow] Generate object : "
							+ object);
			objects.add(object);
		}
		// 3. Let pogm be the set of graph maps of the predicate-object map
		Set<GraphMap> pogm = predicateObjectMap.getGraphMaps();
		// 4. Let predicate-object_graphs be the set of generated RDF
		// terms that result from applying each graph map in pogm to row
		Set<URI> predicate_object_graphs = new HashSet<URI>();
		for (GraphMap graphMap : pogm) {
			Map<String, String> pogmFromRow = applyValueToRow(graphMap);
			String graph_value = graphMap.getValue(pogmFromRow);
			URI predicate_object_graph = (URI) generateRDFTerm(graphMap,
					graph_value);
			log
					.debug("[R2RMLEngine:genereateRDFTriplesFromRow] Generate predicate object graph : "
							+ predicate_object_graph);
			predicate_object_graphs.add(predicate_object_graph);
		}
		// 5. For each possible combination <predicate, object> where predicate
		// is a member
		// of predicates and object is a member of objects,
		// add triples to the output dataset
		for (URI predicate : predicates) {
			for (Value object : objects) {
				addTriplesToTheOutputDataset(sesameDataSet, subject, predicate,
						object, predicate_object_graphs);
			}
		}
	}

	/**
	 * “Add triples to the output dataset” is a process that takes the following
	 * inputs: Subject, an IRI or blank node or empty Predicate, an IRI or empty
	 * Object, an RDF term or empty Target graphs, a set of zero or more IRIs
	 * 
	 * @param sesameDataSet
	 * @param subject
	 * @param predicate
	 * @param object
	 * @param targetGraphs
	 */
	private void addTriplesToTheOutputDataset(SesameDataSet sesameDataSet,
			Resource subject, URI predicate, Value object, Set<URI> targetGraphs) {
		// 1. If Subject, Predicate or Object is empty, then abort these steps.
		if (subject == null || predicate == null || object == null) return;
		// 2. Otherwise, generate an RDF triple <Subject, Predicate, Object>
		Statement triple = null;
		if (targetGraphs.isEmpty()) {
			// add the triple to the default graph of the output dataset.
			triple = vf.createStatement(subject, predicate, object);
			sesameDataSet.addStatement(triple);
		}
		for (URI targetGraph : targetGraphs) {
			if (targetGraph.stringValue().equals(
					R2RMLVocabulary.R2RML_NAMESPACE + R2RMLTerm.DEFAULT_GRAPH)) {
				// 3. If the set of target graphs includes rr:defaultGraph,
				// add the triple to the default graph of the output dataset.
				triple = vf.createStatement(subject, predicate, object);
			} else {
				// 4. For each IRI in the set of target graphs that is not equal
				// to rr:defaultGraph,
				// add the triple to a named graph of that name in the output
				// dataset.
				triple = vf.createStatement(subject, predicate, object,
						targetGraph);
			}
			sesameDataSet.addStatement(triple);
		}
		log.debug("[R2RMLEngine:addStatement] Added new statement : " + triple);
	}

	private Map<String, String> applyValueToRow(TermMap tm) throws SQLException {
		Map<String, String> result = new HashMap<String, String>();
		Set<String> columns = tm.getReferencedColumns();
		for (String column : columns) {
			String value = rows.getString(column);
			result.put(column, value);
		}
		return result;
	}

	/**
	 * A term map is a function that generates an RDF term from a logical table
	 * row. The result of that function can be: Empty – if any of the referenced
	 * columns of the term map has a NULL value, An RDF term – the common case,
	 * A data error.
	 * 
	 * The term generation rules, applied to a value, are described in this
	 * algorithm.
	 * 
	 * @param termMap
	 * @return
	 * @throws R2RMLDataError
	 * @throws SQLException
	 */
	private Value generateRDFTerm(TermMap termMap, String value)
			throws R2RMLDataError, SQLException {
		// 1. If value is NULL, then no RDF term is generated.
		if (termMap == null)
			return null;
		switch (termMap.getTermType()) {
		case IRI:
			// 2. Otherwise, if the term map's term type is rr:IRI
			URI iri = generateIRITermType(termMap, value);
			log.debug("R2RMLEngine:generateRDFTerm] Generated RDF Term : "
					+ iri);
			return (Value) iri;

		case BLANK_NODE:
			// 3. Otherwise, if the term map's term type is rr:BlankNode
			BNode bnode = generateBlankNodeTermType(termMap, value);
			log.debug("R2RMLEngine:generateRDFTerm] Generated RDF Term : "
					+ bnode);
			return (Value) bnode;

		case LITERAL:
			// 4. Otherwise, if the term map's term type is rr:Literal
			Value valueObj = generateLiteralTermType(termMap, value);
			return valueObj;

		default:
			// Unknow Term type
			throw new IllegalStateException(
					"[R2RMLEngine:generateRDFTerm] Unkonw term type : no rule define for this case.");
		}
	}

	private Value generateLiteralTermType(TermMap termMap, String value)
			throws R2RMLDataError, SQLException {
		// 1. If the term map has a specified language tag, then return a plain
		// literal
		// with that language tag and with the natural RDF lexical form
		// corresponding to value.
		if (termMap.getLanguageTag() != null) {
			if (!RDFDataValidator.isValidLanguageTag(termMap.getLanguageTag()))
				throw new R2RMLDataError(
						"[R2RMLEngine:generateLiteralTermType] This language tag is not valid : "
								+ value);
			return vf.createLiteral(value, termMap.getLanguageTag());
		} else if (termMap.getDataType() != null) {
			// 2. Otherwise, if the term map has a non-empty specified datatype
			// that is different from the natural RDF datatype corresponding to
			// the term map's
			// implicit SQL datatype,
			// then return the datatype-override RDF literal corresponding to
			// value and the specified datatype.
			if (!RDFDataValidator.isValidDatatype(termMap.getDataType()
					.getAbsoluteStringURI()))
				throw new R2RMLDataError(
				// If the typed literal is ill-typed, then a data error is
						// raised.
						"[R2RMLEngine:generateLiteralTermType] This datatype is not valid : "
								+ value);
			SQLType implicitDatatype = extractImplicitDatatype((ObjectMap) termMap);
			// Convert implicit datatype into XSD
			XSDType implicitXSDType = SQLToXMLS
					.getEquivalentType(implicitDatatype);
			if (implicitXSDType != termMap.getDataType()) {
				// Type overidden
				log
						.debug("[R2RMLEngine:generateLiteralTermType] Type will be overidden : "
								+ termMap.getDataType()
								+ " != "
								+ implicitXSDType);
				URI datatype = vf.createURI(termMap.getDataType()
						.getAbsoluteStringURI());
				return vf.createLiteral(value, datatype);
			}
		} else {
			// 3. Otherwise, return the natural RDF literal corresponding to
			// value.
			return extractNaturalRDFFormFrom(termMap, value);
		}
		return null;

	}

	private BNode generateBlankNodeTermType(TermMap termMap, String value) {
		// 1. Return a blank node whose blank node identifier is
		// the natural RDF lexical form corresponding to value. (Note: scope of
		// blank nodes)
		// In db2triples and contrary to the R2RML norm, we accepts
		// auto-assignments of blank nodes.
		if (value == null)
			return vf.createBNode();
		else
			return vf.createBNode(value);
	}

	private URI generateIRITermType(TermMap termMap, String value)
			throws R2RMLDataError {
		// 1. Let value be the natural RDF lexical form corresponding to value.
		// 2. If value is a valid absolute IRI [RFC3987], then return an IRI
		// generated from value.
		if (RDFDataValidator.isValidURI(value)) {
			URI result = vf.createURI(value);
			return result;
		} else {
			String prependedValue = baseIRI + value;
			if (RDFDataValidator.isValidURI(prependedValue)) {
				// Otherwise, prepend value with the base IRI. If the result is
				// a valid absolute IRI [RFC3987], then return an IRI generated
				// from the result.
				URI result = vf.createURI(prependedValue);
				return result;
			} else {
				// 4. Otherwise, raise a data error.
				throw new R2RMLDataError(
						"[R2RMLEngine:generateIRITermType] This relative URI "
								+ value + " or this absolute URI " + baseIRI
								+ value + " is not valid.");
			}
		}
	}

	private SQLType extractImplicitDatatype(ObjectMap objectMap)
			throws SQLException {
		SQLType result = null;
		if (objectMap.getTermMapType() != TermMapType.TEMPLATE_VALUED
				&& objectMap.getTermType() == TermType.LITERAL) {
			for (int i = 1; i <= meta.getColumnCount(); i++) {
				if (meta.getColumnName(i).equals(
						objectMap.getReferencedColumns().iterator().next())) {
					result = SQLType.toSQLType(meta.getColumnType(i));
					log
							.debug("[R2RMLEngine:extractImplicitDatatype] Extracted implicit datatype :  "
									+ result);
				}
			}
		}
		return result;
	}

	/**
	 * The natural RDF lexical form corresponding to a SQL data value is the
	 * lexical form of its corresponding natural RDF literal, with the
	 * additional constraint that the canonical lexical representation SHOULD be
	 * chosen.
	 * 
	 * @param termMap
	 * 
	 * @param value
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings("unused")
	private Value extractNaturalRDFLexicalFormFrom(TermMap termMap, String value)
			throws SQLException {
		// TODO ?
		return null;
	}

	/**
	 * The natural RDF literal corresponding to a SQL data value is the result
	 * of applying the following method.
	 * 
	 * @param termMap
	 * @param value
	 * @return
	 * @throws SQLException
	 */
	private Value extractNaturalRDFFormFrom(TermMap termMap, String value)
			throws SQLException {
		// 1. Let dt be the SQL datatype of the SQL data value.
		SQLType dt = extractImplicitDatatype((ObjectMap) termMap);
		// 2. If dt is a character string type, then the result is a plain
		// literal without
		// language tag whose lexical form is the SQL data value.
		XSDType xsdType = SQLToXMLS.getEquivalentType(dt);
		if (dt.isStringType()) {
			if (xsdType == null) {
				// 4. Otherwise, the result is a plain literal without language
				// tag whose lexical form is the SQL data value CAST TO STRING.

				// CAST TO STRING is implicit is this treatment
				return vf.createLiteral(value);
			}
			return vf.createLiteral(value);
		} else if (xsdType != null) {
			// 3. Otherwise, if dt is listed in the table below: The result is a
			// typed literal whose datatype IRI is the IRI indicated in the RDF
			// datatype column in the same row as dt
			return vf.createLiteral(value, xsdType.getAbsoluteStringURI());
		} else {
			// 4. Otherwise, the result is a plain literal without language
			// tag whose lexical form is the SQL data value CAST TO STRING.

			// CAST TO STRING is implicit is this treatment
			return vf.createLiteral(value);
		}
	}

	/**
	 * Construct logical table. Note : Run SQL Query against database calling
	 * the method Connection.commit can close the ResultSet objects that have
	 * been created during the current transaction. In some cases, however, this
	 * may not be the desired behavior. The ResultSet property holdability gives
	 * the application control over whether ResultSet objects (cursors) are
	 * closed when commit is called.
	 * 
	 * @param triplesMap
	 * @throws SQLException
	 */
	private ResultSet constructLogicalTable(TriplesMap triplesMap)
			throws SQLException {
		log
				.debug("[R2RMLEngine:constructLogicalTable] Run effective SQL Query : "
						+ triplesMap.getLogicalTable().getEffectiveSQLQuery());
		ResultSet rs = null;
		java.sql.Statement s = conn.createStatement(
				ResultSet.HOLD_CURSORS_OVER_COMMIT, ResultSet.CONCUR_READ_ONLY);
		if (triplesMap.getLogicalTable().getEffectiveSQLQuery() != null) {
			s.executeQuery(triplesMap.getLogicalTable().getEffectiveSQLQuery());
			rs = s.getResultSet();
			if (rs == null)
				throw new IllegalStateException(
						"[R2RMLEngine:constructLogicalTable] SQL request "
								+ "failed : result of effective SQL query is null.");

		} else {
			throw new IllegalStateException(
					"[R2RMLEngine:constructLogicalTable] No effective SQL query has been found.");
		}
		// Commit to held logical table (read-only)
		conn.setAutoCommit(false);
		conn.commit();
		return rs;
	}

	private ResultSet constructJointTable(ReferencingObjectMap refObjectMap)
			throws SQLException {
		log.debug("[R2RMLEngine:constructJointTable] Run joint SQL Query : "
				+ refObjectMap.getJointSQLQuery());
		ResultSet rs = null;
		java.sql.Statement s = conn.createStatement(
				ResultSet.HOLD_CURSORS_OVER_COMMIT, ResultSet.CONCUR_READ_ONLY);
		if (refObjectMap.getJointSQLQuery() != null) {
			s.executeQuery(refObjectMap.getJointSQLQuery());
			rs = s.getResultSet();
			if (rs == null)
				throw new IllegalStateException(
						"[R2RMLEngine:constructLogicalTable] SQL request "
								+ "failed : result of effective SQL query is null.");
		} else {
			throw new IllegalStateException(
					"[R2RMLEngine:constructLogicalTable] No effective SQL query has been found.");
		}
		return rs;

	}
}
