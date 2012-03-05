/***************************************************************************
 *
 * R2RML : R2RML Mapping Factory absctract class 
 *
 * (C) 2011 Antidot (http://www.antidot.net)
 *
 * Module			:	R2RML
 * 
 * Fichier			:	R2RMLMappingFactory.java
 *
 * Description		:	Factory responsible of R2RML Mapping generation.
 * 
 *
 * Auteurs(s)			:	JHO
 *
 *
 ****************************************************************************/
package net.antidot.semantic.rdf.rdb2rdf.r2rml.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.antidot.semantic.rdf.model.impl.sesame.SesameDataSet;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.core.R2RMLVocabulary.R2RMLTerm;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.InvalidR2RMLStructureException;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.InvalidR2RMLSyntaxException;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.R2RMLDataError;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.model.GraphMap;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.model.LogicalTable;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.model.PredicateObjectMap;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.model.R2RMLMapping;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.model.R2RMLView.SQLVersion;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.model.StdR2RMLView;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.model.StdSQLBaseTableOrView;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.model.StdSubjectMap;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.model.StdTriplesMap;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.model.SubjectMap;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.model.TriplesMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openrdf.model.BNode;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.rio.RDFFormat;

public abstract class R2RMLMappingFactory {

	// Log
	private static Log log = LogFactory.getLog(R2RMLMappingFactory.class);

	// Value factory
	private static ValueFactory vf = new ValueFactoryImpl();

	/**
	 * Extract R2RML Mapping object from a R2RML file written with Turtle
	 * syntax. This syntax is recommanded in R2RML : RDB to RDF Mapping Language
	 * (W3C Working Draft 20 September 2011)"An R2RML mapping document is any
	 * document written in the Turtle [TURTLE] RDF syntax that encodes an R2RML
	 * mapping graph."
	 * 
	 * Important : The R2RML vocabulary also includes the following R2RML
	 * classes, which represent various R2RML mapping constructs. Using these
	 * classes is optional in a mapping graph. The applicable class of a
	 * resource can always be inferred from its properties. Consequently, in
	 * order to identify each triple type, a rule will be used to extract the
	 * applicable class of a resource.
	 * 
	 * @param fileToR2RMLFile
	 * @return
	 * @throws InvalidR2RMLSyntaxException
	 * @throws InvalidR2RMLStructureException
	 * @throws R2RMLDataError
	 */
	public static R2RMLMapping extractR2RMLMapping(String fileToR2RMLFile)
			throws InvalidR2RMLStructureException, InvalidR2RMLSyntaxException,
			R2RMLDataError {
		// Load RDF data from R2RML Mapping document
		SesameDataSet r2rmlMappingGraph = new SesameDataSet();
		r2rmlMappingGraph.loadDataFromFile(fileToR2RMLFile, RDFFormat.TURTLE);
		log.debug("[R2RMLFactory:extractR2RMLMapping] Number of R2RML triples in file "
				+ fileToR2RMLFile + " : " + r2rmlMappingGraph.getSize());

		// Transform RDF with replacement shortcuts
		replaceShortcuts(r2rmlMappingGraph);
		// Construct R2RML Mapping object
		List<Resource> triplesMapResources = extractTripleMapResources(r2rmlMappingGraph);

		log.debug("[R2RMLFactory:extractR2RMLMapping] Number of R2RML triples with "
				+ " type "
				+ R2RMLTerm.TRIPLES_MAP_CLASS
				+ " in file "
				+ fileToR2RMLFile + " : " + triplesMapResources.size());

		List<TriplesMap> storedTriplesMaps = new ArrayList<TriplesMap>();
		// Fill each triplesMap object
		for (Resource triplesMapResource : triplesMapResources) {
			// Extract each triplesMap

			TriplesMap triplesMap = extractTriplesMap(r2rmlMappingGraph,
					triplesMapResource);
			/*
			 * if (log.isDebugEnabled()) log.debug(
			 * "[R2RMLFactory:extractR2RMLMapping] New TriplesMap object : " +
			 * triplesMap); storedTriplesMaps.add(triplesMap);
			 */
		}
		// Generate R2RMLMapping object
		// R2RMLMapping result = new R2RMLMapping(storedTriplesMaps);
		return null;
	}

	/**
	 * Constant-valued term maps can be expressed more concisely using the
	 * constant shortcut properties rr:subject, rr:predicate, rr:object and
	 * rr:graph. Occurrances of these properties must be treated exactly as if
	 * the following triples were present in the mapping graph instead.
	 * 
	 * @param r2rmlMappingGraph
	 */
	private static void replaceShortcuts(SesameDataSet r2rmlMappingGraph) {
		Map<URI, URI> shortcutPredicates = new HashMap<URI, URI>();
		shortcutPredicates.put(
				vf.createURI(R2RMLVocabulary.R2RML_NAMESPACE
						+ R2RMLTerm.SUBJECT),
				vf.createURI(R2RMLVocabulary.R2RML_NAMESPACE
						+ R2RMLTerm.SUBJECT_MAP));
		shortcutPredicates.put(
				vf.createURI(R2RMLVocabulary.R2RML_NAMESPACE
						+ R2RMLTerm.PREDICATE),
				vf.createURI(R2RMLVocabulary.R2RML_NAMESPACE
						+ R2RMLTerm.PREDICATE_MAP));
		shortcutPredicates.put(vf.createURI(R2RMLVocabulary.R2RML_NAMESPACE
				+ R2RMLTerm.OBJECT), vf
				.createURI(R2RMLVocabulary.R2RML_NAMESPACE
						+ R2RMLTerm.OBJECT_MAP));
		shortcutPredicates
				.put(vf.createURI(R2RMLVocabulary.R2RML_NAMESPACE
						+ R2RMLTerm.GRAPH),
						vf.createURI(R2RMLVocabulary.R2RML_NAMESPACE
								+ R2RMLTerm.GRAPH_MAP));
		for (URI u : shortcutPredicates.keySet()) {
			List<Statement> shortcutTriples = r2rmlMappingGraph.tuplePattern(
					null, u, null);
			log.debug("[R2RMLFactory:replaceShortcuts] Number of R2RML shortcuts found "
					+ "for "
					+ u.getLocalName()
					+ " : "
					+ shortcutTriples.size());
			for (Statement shortcutTriple : shortcutTriples) {
				r2rmlMappingGraph.remove(shortcutTriple.getSubject(),
						shortcutTriple.getPredicate(),
						shortcutTriple.getObject());
				BNode blankMap = vf.createBNode();
				URI pMap = vf.createURI(R2RMLVocabulary.R2RML_NAMESPACE
						+ shortcutPredicates.get(u));
				URI pConstant = vf.createURI(R2RMLVocabulary.R2RML_NAMESPACE
						+ R2RMLTerm.CONSTANT);
				r2rmlMappingGraph.add(shortcutTriple.getSubject(), pMap,
						blankMap);
				r2rmlMappingGraph.add(blankMap, pConstant,
						shortcutTriple.getObject());
			}
		}
	}

	/**
	 * Construct TriplesMap objects rule. A triples map is represented by a
	 * resource that references the following other resources : - It must have
	 * exactly one subject map * using the rr:subjectMap property.
	 * 
	 * @param r2rmlMappingGraph
	 * @return
	 * @throws InvalidR2RMLStructureException
	 */
	private static List<Resource> extractTripleMapResources(
			SesameDataSet r2rmlMappingGraph)
			throws InvalidR2RMLStructureException {
		// A triples map is represented by a resource that references the
		// following other resources :
		// - It must have exactly one subject map
		List<Resource> triplesMapResources = new ArrayList<Resource>();
		URI p = r2rmlMappingGraph.URIref(R2RMLVocabulary.R2RML_NAMESPACE
				+ R2RMLTerm.SUBJECT_MAP);
		List<Statement> statements = r2rmlMappingGraph.tuplePattern(null, p,
				null);
		if (statements.isEmpty())
			throw new InvalidR2RMLStructureException(
					"[R2RMLMappingFactory:extractR2RMLMapping]"
							+ " One subject statement is required.");
		else
			// No subject map, Many shortcuts subjects
			for (Statement s : statements) {
				List<Statement> otherStatements = r2rmlMappingGraph
						.tuplePattern(s.getSubject(), p, null);
				if (otherStatements.size() > 1)
					throw new InvalidR2RMLStructureException(
							"[R2RMLMappingFactory:extractR2RMLMapping] "
									+ s.getSubject() + " has many subjectMap "
									+ "(or subject) but only one is required.");
				else
					triplesMapResources.add(s.getSubject());
			}
		return triplesMapResources;
	}

	/**
	 * Extract triplesMap contents.
	 * 
	 * @param triplesMap
	 * @param r2rmlMappingGraph
	 * @param triplesMapSubject
	 * @param storedTriplesMaps
	 * @throws InvalidR2RMLStructureException
	 * @throws InvalidR2RMLSyntaxException
	 * @throws R2RMLDataError
	 */
	private static TriplesMap extractTriplesMap(
			SesameDataSet r2rmlMappingGraph, Resource triplesMapSubject)
			throws InvalidR2RMLStructureException, InvalidR2RMLSyntaxException,
			R2RMLDataError {

		if (log.isDebugEnabled())
			log.debug("[R2RMLFactory:extractTriplesMap] Found TriplesMap subject : "
					+ triplesMapSubject.stringValue());

		TriplesMap result = new StdTriplesMap(null, null, null);

		// Extract TriplesMap properties
		LogicalTable logicalTable = extractLogicalTable(r2rmlMappingGraph,
				triplesMapSubject);

		// Extract subject
		// Create a graph maps storage to save all met graph uri during parsing.
		Set<GraphMap> graphMaps = new HashSet<GraphMap>();
		if (log.isDebugEnabled())
			log.debug("[R2RMLFactory:extractTriplesMap] Current number of created graphMaps : "
					+ graphMaps.size());
		SubjectMap subjectMap = extractSubjectMap(r2rmlMappingGraph,
				triplesMapSubject, graphMaps, result);
		if (log.isDebugEnabled())
			log.debug("[R2RMLFactory:extractTriplesMap] Current number of created graphMaps : "
					+ graphMaps.size());

		// Extract predicate-object maps
		Set<PredicateObjectMap> predicateObjectMaps = extractPredicateObjectMaps(
				r2rmlMappingGraph, triplesMapSubject, graphMaps, result);

		if (log.isDebugEnabled())
			log.debug("[R2RMLFactory:extractTriplesMap] Done.");
		return null;

	}

	private static Set<PredicateObjectMap> extractPredicateObjectMaps(
			SesameDataSet r2rmlMappingGraph, Resource triplesMapSubject,
			Set<GraphMap> graphMaps, TriplesMap result)
			throws InvalidR2RMLStructureException {
		if (log.isDebugEnabled())
			log.debug("[R2RMLFactory:extractPredicateObjectMaps] Extract predicate-object maps...");
		// Extract predicate-object maps
		URI p = r2rmlMappingGraph.URIref(R2RMLVocabulary.R2RML_NAMESPACE
				+ R2RMLTerm.PREDICATE_OBJECT_MAP);
		List<Statement> statements = r2rmlMappingGraph.tuplePattern(
				triplesMapSubject, p, null);

		if (statements.isEmpty())
			throw new InvalidR2RMLStructureException(
					"[R2RMLMappingFactory:extractPredicateObjectMaps] "
							+ triplesMapSubject
							+ " has no predicate-object map defined.");
		
		Set<PredicateObjectMap> predicateObjectMaps = new HashSet<PredicateObjectMap>();
		for (Statement statement : statements) {
			PredicateObjectMap predicateObjectMap = extractPredicateObjectMap(
					r2rmlMappingGraph, statement.getObject(),graphMaps, result);
			predicateObjectMaps.add(predicateObjectMap);
		}
		return predicateObjectMaps;
		
	}

	private static PredicateObjectMap extractPredicateObjectMap(
			SesameDataSet r2rmlMappingGraph, Value object, Set<GraphMap> graphMaps, TriplesMap result) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Extract subjectMap contents
	 * 
	 * @param r2rmlMappingGraph
	 * @param triplesMapSubject
	 * @return
	 * @throws InvalidR2RMLStructureException
	 * @throws InvalidR2RMLSyntaxException
	 * @throws R2RMLDataError
	 */
	private static SubjectMap extractSubjectMap(
			SesameDataSet r2rmlMappingGraph, Resource triplesMapSubject,
			Set<GraphMap> savedGraphMaps, TriplesMap ownTriplesMap)
			throws InvalidR2RMLStructureException, R2RMLDataError,
			InvalidR2RMLSyntaxException {
		if (log.isDebugEnabled())
			log.debug("[R2RMLFactory:extractPredicateObjectMaps] Extract subject map...");
		// Extract subject map
		URI p = r2rmlMappingGraph.URIref(R2RMLVocabulary.R2RML_NAMESPACE
				+ R2RMLTerm.SUBJECT_MAP);
		List<Statement> statements = r2rmlMappingGraph.tuplePattern(
				triplesMapSubject, p, null);

		if (statements.isEmpty())
			throw new InvalidR2RMLStructureException(
					"[R2RMLMappingFactory:extractSubjectMap] "
							+ triplesMapSubject
							+ " has no subject map defined.");
		if (statements.size() > 1)
			throw new InvalidR2RMLStructureException(
					"[R2RMLMappingFactory:extractSubjectMap] "
							+ triplesMapSubject
							+ " has too many subject map defined.");

		Resource subjectMap = (Resource) statements.get(0).getObject();
		if (log.isDebugEnabled())
			log.debug("[R2RMLFactory:extractTriplesMap] Found subject map : "
					+ subjectMap.stringValue());

		Value constantValue = extractValueFromTermMap(r2rmlMappingGraph,
				subjectMap, R2RMLTerm.CONSTANT);
		String stringTemplate = extractLiteralFromTermMap(r2rmlMappingGraph,
				subjectMap, R2RMLTerm.TEMPLATE);
		URI termType = (URI) extractValueFromTermMap(r2rmlMappingGraph,
				subjectMap, R2RMLTerm.TERM_TYPE);
		String inverseExpression = extractLiteralFromTermMap(r2rmlMappingGraph,
				subjectMap, R2RMLTerm.INVERSE_EXPRESSION);
		String columnValue = extractLiteralFromTermMap(r2rmlMappingGraph,
				subjectMap, R2RMLTerm.COLUMN);
		Set<URI> classIRIs = extractURIsFromTermMap(r2rmlMappingGraph,
				subjectMap, R2RMLTerm.CLASS);
		Set<Value> graphMapValues = extractValuesFromResource(r2rmlMappingGraph,
				subjectMap, R2RMLTerm.GRAPH_MAP);
		Set<GraphMap> graphMaps = new HashSet<GraphMap>();
		if (graphMapValues != null)
			for (Value graphMap : graphMapValues) {
				// Create associated graphMap if it has not already created
				boolean found = false;
				GraphMap graphMapFound = null;
				for (GraphMap savedGraphMap : savedGraphMaps)
					if (savedGraphMap.getGraph().equals(graphMap)) {
						found = true;
						graphMapFound = savedGraphMap;
					}
				if (found)
					graphMaps.add(graphMapFound);
				else {
					GraphMap newGraphMap = extractGraphMap(r2rmlMappingGraph,
							graphMap);
					savedGraphMaps.add(newGraphMap);
					graphMaps.add(newGraphMap);
				}
			}
		SubjectMap result = new StdSubjectMap(ownTriplesMap, constantValue,
				stringTemplate, termType, inverseExpression, columnValue,
				classIRIs, graphMaps);
		if (log.isDebugEnabled())
			log.debug("[R2RMLFactory:extractSubjectMap] Subject map extracted : "
					+ result);
		return result;
	}

	private static GraphMap extractGraphMap(SesameDataSet r2rmlMappingGraph,
			Value graphMap) {

		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Extract content literal from a term type resource.
	 * 
	 * @param r2rmlMappingGraph
	 * @param termType
	 * @param term
	 * @return
	 * @throws InvalidR2RMLStructureException
	 */
	private static String extractLiteralFromTermMap(
			SesameDataSet r2rmlMappingGraph, Resource termType, R2RMLTerm term)
			throws InvalidR2RMLStructureException {
		URI p = r2rmlMappingGraph
				.URIref(R2RMLVocabulary.R2RML_NAMESPACE + term);
		List<Statement> statements = r2rmlMappingGraph.tuplePattern(termType,
				p, null);
		if (statements.isEmpty())
			return null;
		if (statements.size() > 1)
			throw new InvalidR2RMLStructureException(
					"[R2RMLMappingFactory:extractValueFromTermMap] " + termType
							+ " has too many " + term + " predicate defined.");
		String result = statements.get(0).getObject().stringValue();
		if (log.isDebugEnabled())
			log.debug("[R2RMLFactory:extractLiteralFromTermMap] Extracted "
					+ term + " : " + result);
		return result;
	}

	/**
	 * Extract content value from a term type resource.
	 * 
	 * @return
	 * @throws InvalidR2RMLStructureException
	 */
	private static Value extractValueFromTermMap(
			SesameDataSet r2rmlMappingGraph, Resource termType,
			R2RMLVocabulary.R2RMLTerm term)
			throws InvalidR2RMLStructureException {
		URI p = r2rmlMappingGraph
				.URIref(R2RMLVocabulary.R2RML_NAMESPACE + term);
		List<Statement> statements = r2rmlMappingGraph.tuplePattern(termType,
				p, null);
		if (statements.isEmpty())
			return null;
		if (statements.size() > 1)
			throw new InvalidR2RMLStructureException(
					"[R2RMLMappingFactory:extractValueFromTermMap] " + termType
							+ " has too many " + term + " predicate defined.");
		Value result = statements.get(0).getObject();
		if (log.isDebugEnabled())
			log.debug("[R2RMLFactory:extractValueFromTermMap] Extracted "
					+ term + " : " + result.stringValue());
		return result;
	}

	/**
	 * Extract content values from a term type resource.
	 * 
	 * @return
	 * @throws InvalidR2RMLStructureException
	 */
	private static Set<Value> extractValuesFromResource(
			SesameDataSet r2rmlMappingGraph, Resource termType,
			R2RMLVocabulary.R2RMLTerm term)
			throws InvalidR2RMLStructureException {
		URI p = r2rmlMappingGraph
				.URIref(R2RMLVocabulary.R2RML_NAMESPACE + term);
		List<Statement> statements = r2rmlMappingGraph.tuplePattern(termType,
				p, null);
		if (statements.isEmpty())
			return null;
		Set<Value> values = new HashSet<Value>();
		for (Statement statement : statements) {
			Value value = statement.getObject();
			if (log.isDebugEnabled())
				log.debug("[R2RMLFactory:extractURIsFromTermMap] Extracted "
						+ term + " : " + value.stringValue());
			values.add(value);
		}
		return values;
	}

	/**
	 * Extract content URIs from a term type resource.
	 * 
	 * @return
	 * @throws InvalidR2RMLStructureException
	 */
	private static Set<URI> extractURIsFromTermMap(
			SesameDataSet r2rmlMappingGraph, Resource termType,
			R2RMLVocabulary.R2RMLTerm term)
			throws InvalidR2RMLStructureException {
		URI p = r2rmlMappingGraph
				.URIref(R2RMLVocabulary.R2RML_NAMESPACE + term);
		List<Statement> statements = r2rmlMappingGraph.tuplePattern(termType,
				p, null);
		if (statements.isEmpty())
			return null;
		Set<URI> uris = new HashSet<URI>();
		for (Statement statement : statements) {
			URI uri = (URI) statement.getObject();
			if (log.isDebugEnabled())
				log.debug("[R2RMLFactory:extractURIsFromTermMap] Extracted "
						+ term + " : " + uri.stringValue());
			uris.add(uri);
		}
		return uris;
	}

	/**
	 * Extract logicalTable contents.
	 * 
	 * @param r2rmlMappingGraph
	 * @param triplesMapSubject
	 * @return
	 * @throws InvalidR2RMLStructureException
	 * @throws InvalidR2RMLSyntaxException
	 */
	private static LogicalTable extractLogicalTable(
			SesameDataSet r2rmlMappingGraph, Resource triplesMapSubject)
			throws InvalidR2RMLStructureException, InvalidR2RMLSyntaxException {

		// Extract logical table blank node
		URI p = r2rmlMappingGraph.URIref(R2RMLVocabulary.R2RML_NAMESPACE
				+ R2RMLTerm.LOGICAL_TABLE);
		List<Statement> statements = r2rmlMappingGraph.tuplePattern(
				triplesMapSubject, p, null);
		if (statements.isEmpty())
			throw new InvalidR2RMLStructureException(
					"[R2RMLMappingFactory:extractLogicalTable] "
							+ triplesMapSubject
							+ " has no logical table defined.");
		if (statements.size() > 1)
			throw new InvalidR2RMLStructureException(
					"[R2RMLMappingFactory:extractLogicalTable] "
							+ triplesMapSubject
							+ " has too many logical table defined.");
		Resource blankLogicalTable = (Resource) statements.get(0).getObject();

		// Check SQL base table or view
		URI pName = r2rmlMappingGraph.URIref(R2RMLVocabulary.R2RML_NAMESPACE
				+ R2RMLTerm.TABLE_NAME);
		List<Statement> statementsName = r2rmlMappingGraph.tuplePattern(
				blankLogicalTable, pName, null);
		URI pView = r2rmlMappingGraph.URIref(R2RMLVocabulary.R2RML_NAMESPACE
				+ R2RMLTerm.SQL_QUERY);
		List<Statement> statementsView = r2rmlMappingGraph.tuplePattern(
				blankLogicalTable, pView, null);
		LogicalTable logicalTable = null;
		if (!statementsName.isEmpty()) {
			if (statementsName.size() > 1)
				throw new InvalidR2RMLStructureException(
						"[R2RMLMappingFactory:extractLogicalTable] "
								+ triplesMapSubject
								+ " has too many logical table name defined.");
			if (!statementsView.isEmpty())
				throw new InvalidR2RMLStructureException(
						"[R2RMLMappingFactory:extractLogicalTable] "
								+ triplesMapSubject
								+ " can't have a logical table and sql query defined"
								+ " at the ame time.");
			// Table name defined
			logicalTable = new StdSQLBaseTableOrView(statementsName.get(0)
					.getObject().stringValue());
		} else {
			// Logical table defined by R2RML View
			if (statementsView.size() > 1)
				throw new InvalidR2RMLStructureException(
						"[R2RMLMappingFactory:extractLogicalTable] "
								+ triplesMapSubject
								+ " has too many logical table defined.");
			if (statementsView.isEmpty())
				throw new InvalidR2RMLStructureException(
						"[R2RMLMappingFactory:extractLogicalTable] "
								+ triplesMapSubject
								+ " has no logical table defined.");
			// Check SQL versions
			URI pVersion = r2rmlMappingGraph
					.URIref(R2RMLVocabulary.R2RML_NAMESPACE
							+ R2RMLTerm.SQL_VERSION);
			List<Statement> statementsVersion = r2rmlMappingGraph.tuplePattern(
					triplesMapSubject, pVersion, null);
			String sqlQuery = statementsView.get(0).getObject().stringValue();
			if (statementsVersion.isEmpty())
				logicalTable = new StdR2RMLView(sqlQuery);
			Set<SQLVersion> versions = new HashSet<SQLVersion>();
			for (Statement statementVersion : statementsVersion) {
				SQLVersion sqlVersion = SQLVersion
						.getSQLVersion(statementVersion.getObject()
								.stringValue());
				versions.add(sqlVersion);
			}
			if (versions.isEmpty()) {
				// SQL 2008 by default
				if (log.isDebugEnabled())
					log.debug("[R2RMLMappingFactory:extractLogicalTable] "
							+ triplesMapSubject
							+ " has no SQL version defined : SQL 2008 by default");
			}
			logicalTable = new StdR2RMLView(sqlQuery, versions);
		}
		if (log.isDebugEnabled())
			log.debug("[R2RMLFactory:extractLogicalTable] Logical table extracted : "
					+ logicalTable);
		return logicalTable;
	}

}
