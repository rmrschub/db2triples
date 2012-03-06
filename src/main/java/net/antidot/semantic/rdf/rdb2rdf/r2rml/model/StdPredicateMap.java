/***************************************************************************
 *
 * R2RML Model : Standard PredicateMap Class
 *
 * (C) 2011 Antidot (http://www.antidot.net)
 *
 * Module			:	R2RMLModel
 * 
 * Fichier			:	StdPredicateMap.java
 *
 * Description		:	A predicate map is a specific term map used for 
 * 						representing RDF predicate. 
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

import net.antidot.semantic.rdf.model.tools.RDFDataValidator;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.InvalidR2RMLStructureException;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.InvalidR2RMLSyntaxException;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.R2RMLDataError;

import org.openrdf.model.Value;

public class StdPredicateMap extends AbstractTermMap implements TermMap,
		PredicateMap {

	private PredicateObjectMap predicateObjectMap;

	public StdPredicateMap(PredicateObjectMap predicateObjectMap,
			Value constantValue, String stringTemplate,
			String inverseExpression, String columnValue)
			throws R2RMLDataError, InvalidR2RMLStructureException,
			InvalidR2RMLSyntaxException {
		// No Literal term type
		// ==> No datatype
		// ==> No specified language tag
		// Only termType possible : IRI => by default
		// No class IRI
		super(constantValue, null, null, stringTemplate, null,
				inverseExpression, columnValue);
		setPredicateObjectMap(predicateObjectMap);
	}

	protected void checkSpecificTermType(TermType tt)
			throws InvalidR2RMLStructureException {
		// If the term map is a predicate map: rr:IRI
		if (tt != TermType.IRI) {
			throw new InvalidR2RMLStructureException(
					"[StdPredicateMap:checkSpecificTermType] If the term map is a "
							+ "predicate map: only rr:IRI  is required");
		}
	}

	protected void checkConstantValue(Value constantValue)
			throws R2RMLDataError {
		// If the constant-valued term map is a predicate map then its constant
		// value must be an IRI.
		if (!RDFDataValidator.isValidURI(constantValue.stringValue()))
			throw new R2RMLDataError(
					"[StdPredicateMap:checkConstantValue] Not a valid URI : "
							+ constantValue);
	}

	public PredicateObjectMap getPredicateObjectMap() {
		return predicateObjectMap;
	}

	public void setPredicateObjectMap(PredicateObjectMap predicateObjectMap) {
		/*
		 * if (predicateObjectMap.getPredicateMaps() != null) { if
		 * (!predicateObjectMap.getPredicateMaps().contains(this)) throw new
		 * IllegalStateException(
		 * "[StdPredicateObjectMap:setPredicateObjectMap] " +
		 * "The predicateObject map parent " +
		 * "already contains another Predicate Map !"); } else {
		 */
		if (predicateObjectMap != null) {
			// Update predicateObjectMap if not contains this object map
			if (predicateObjectMap.getPredicateMaps() == null)
				predicateObjectMap
						.setPredicateMaps(new HashSet<PredicateMap>());
			predicateObjectMap.getPredicateMaps().add(this);
		}
		// }
		this.predicateObjectMap = predicateObjectMap;

	}

}
