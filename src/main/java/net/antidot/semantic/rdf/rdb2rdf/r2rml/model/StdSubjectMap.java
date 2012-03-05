/***************************************************************************
 *
 * R2RML Model : Standard SubjectMap Class
 *
 * (C) 2011 Antidot (http://www.antidot.net)
 *
 * Module			:	R2RMLModel
 * 
 * Fichier			:	StdSubjectMap.java
 *
 * Description		:	A subject map is a term map. It specifies a rule
 * 						for generating the subjects of the RDF triples generated 
 * 						by a triples map.
 * 
 * Reference		:	R2RML: RDB to RDF Mapping Language
 * 						W3C Working Draft 20 September 2011
 *
 * Options de compilation:
 *
 * Auteurs(s)			:	JHO
 *
 *
 ****************************************************************************/
package net.antidot.semantic.rdf.rdb2rdf.r2rml.model;

import java.util.HashSet;
import java.util.Set;

import net.antidot.semantic.rdf.model.tools.RDFDataValidator;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.InvalidR2RMLStructureException;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.InvalidR2RMLSyntaxException;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.R2RMLDataError;

import org.openrdf.model.URI;
import org.openrdf.model.Value;

public class StdSubjectMap extends AbstractTermMap implements SubjectMap {

	private Set<URI> classIRIs;
	private HashSet<GraphMap> graphMaps;
	protected TriplesMap ownTriplesMap;

	public StdSubjectMap(TriplesMap ownTriplesMap, Value constantValue,
			String stringTemplate, URI termType, String inverseExpression,
			String columnValue, Set<URI> classIRIs, Set<GraphMap> graphMaps)
			throws R2RMLDataError, InvalidR2RMLStructureException,
			InvalidR2RMLSyntaxException {
		// No Literal term type
		// ==> No datatype
		// ==> No specified language tag
		super(constantValue, null, null, stringTemplate, termType,
				inverseExpression, columnValue);
		setClassIRIs(classIRIs);
		setGraphMaps(graphMaps);
		setOwnTriplesMap(ownTriplesMap);
	}

	public void setOwnTriplesMap(TriplesMap ownTriplesMap)
			throws InvalidR2RMLStructureException {
		// Update triples map if not contains this subject map
		if (ownTriplesMap.getSubjectMap() != null)
			if (ownTriplesMap.getSubjectMap() != this)
				throw new IllegalStateException(
						"[StdSubjectMap:setSubjectMap] "
								+ "The own triples map "
								+ "already contains another Subject Map !");
			else
				ownTriplesMap.setSubjectMap(this);
		this.ownTriplesMap = ownTriplesMap;
	}

	private void setGraphMaps(Set<GraphMap> graphMaps) {
		this.graphMaps = new HashSet<GraphMap>();
		graphMaps.addAll(graphMaps);
	}

	private void setClassIRIs(Set<URI> classIRIs2) throws R2RMLDataError {
		this.classIRIs = new HashSet<URI>();
		checkClassIRIs(classIRIs);
		classIRIs.addAll(classIRIs2);
	}

	private void checkClassIRIs(Set<URI> classIRIs2) throws R2RMLDataError {
		// The values of the rr:class property must be IRIs.
		for (URI classIRI : classIRIs) {
			if (!RDFDataValidator.isValidURI(classIRI.stringValue()))
				throw new R2RMLDataError(
						"[AbstractTermMap:checkClassIRIs] Not a valid URI : "
								+ classIRI);
		}
	}

	public Set<URI> getClassIRIs() {
		return classIRIs;
	}

	protected void checkSpecificTermType(TermType tt)
			throws InvalidR2RMLStructureException {
		// If the term map is a subject map: rr:IRI or rr:BlankNode
		if ((tt != TermType.IRI) && (tt != TermType.BLANK_NODE)) {
			throw new InvalidR2RMLStructureException(
					"[StdSubjectMap:checkSpecificTermType] If the term map is a "
							+ "subject map: only rr:IRI or rr:BlankNode is required");
		}
	}

	protected void checkConstantValue(Value constantValue)
			throws R2RMLDataError {
		// If the constant-valued term map is a subject map then its constant
		// value must be an IRI.
		if (!RDFDataValidator.isValidURI(constantValue.stringValue()))
			throw new R2RMLDataError(
					"[StdSubjectMap:checkConstantValue] Not a valid URI : "
							+ constantValue);
	}

	public Set<GraphMap> getGraphMaps() {
		return graphMaps;
	}

	public TriplesMap getOwnTriplesMap() {
		return ownTriplesMap;
	}

	public String toString() {
		String result = super.toString() + " [StdSubjectMap : classIRIs = [";
		for (URI uri : classIRIs)
			result += uri.getLocalName() + ",";
		result += "], graphMaps = [";
		for (GraphMap graphMap : graphMaps)
			result += graphMap.getGraph().getLocalName() + ",";
		result += "]]";
		return result;
	}

}
