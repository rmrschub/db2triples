/***************************************************************************
 *
 * R2RML Model : Standard GraphtMap Class
 *
 * (C) 2011 Antidot (http://www.antidot.net)
 *
 * Module			:	R2RMLModel
 * 
 * Fichier			:	StdGraphMap.java
 *
 * Description		:	Any subject map or predicate-object map may have one
 * 						or more associated graph maps. Graph maps are 
 * 						themselves term maps. When RDF triples are generated,
 * 						the set of target graphs is determined by taking into
 * 						account any graph maps associated with the subject map
 * 						or predicate-object map.
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

import net.antidot.semantic.rdf.model.tools.RDFDataValidator;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.InvalidR2RMLStructureException;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.InvalidR2RMLSyntaxException;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.R2RMLDataError;

import org.openrdf.model.URI;
import org.openrdf.model.Value;

public class StdGraphMap extends AbstractTermMap implements GraphMap {


	public StdGraphMap(Value constantValue,
			String stringTemplate, String inverseExpression,
			String columnValue) throws R2RMLDataError,
			InvalidR2RMLStructureException, InvalidR2RMLSyntaxException {
		// No Literal term type
		// ==> No datatype
		// ==> No specified language tag
		// Only termType possible : IRI => by default
		super(constantValue, null, null, stringTemplate,
				null, inverseExpression, columnValue);
		
	}

	private void checkGraph(URI graph) throws R2RMLDataError {
		if (!RDFDataValidator.isValidURI(graph.stringValue()))
			throw new R2RMLDataError(
					"[StdGraphMap:checkClassIRIs] Not a valid URI : " + graph);
	}

	protected void checkSpecificTermType(TermType tt)
			throws InvalidR2RMLStructureException {
		// If the term map is a predicate map: rr:IRI
		if (tt != TermType.IRI) {
			throw new InvalidR2RMLStructureException(
					"[StdGraphMap:checkSpecificTermType] If the term map is a "
							+ "graph map: only rr:IRI  is required");
		}
	}

	protected void checkConstantValue(Value constantValue)
			throws R2RMLDataError {
		// If the constant-valued term map is a graph map then its constant
				// value must be an IRI.
		if (!RDFDataValidator.isValidURI(constantValue.stringValue()))
			throw new R2RMLDataError(
					"[StdGraphMap:checkConstantValue] Not a valid URI : "
					+ constantValue);
	}
	
	/*public URI getGraph(){
		return graph;
	}*/
	
	
}
