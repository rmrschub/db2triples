/***************************************************************************
 *
 * R2RML Model : Standard TriplesMap Class
 *
 * (C) 2011 Antidot (http://www.antidot.net)
 *
 * Module			:	R2RMLModel
 * 
 * Fichier			:	StdTriplesMap.java
 *
 * Description		:	A triples map specifies a rule for translating each
 * 						row of a logical table to zero or more RDF triples.
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

import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.InvalidR2RMLStructureException;

public class StdTriplesMap implements TriplesMap {

	private Set<PredicateObjectMap> predicateObjectMaps;
	private SubjectMap subjectMap;
	private LogicalTable logicalTable;

	public StdTriplesMap(LogicalTable logicalTable,
			Set<StdPredicateObjectMap> predicateObjectMaps,
			StdSubjectMap subjectMap) throws InvalidR2RMLStructureException {
		setSubjectMap(subjectMap);
		setLogicalTable(logicalTable);
		setPredicateObjectMap(predicateObjectMaps);

	}

	public void setLogicalTable(LogicalTable logicalTable) {
		/*if (logicalTable == null)
			throw new InvalidR2RMLStructureException(
					"[StdTriplesMap:setLogicalTable] A logical table is required.");*/
		this.logicalTable = logicalTable;
	}

	public void setPredicateObjectMap(
			Set<StdPredicateObjectMap> predicateObjectMaps) {
		this.predicateObjectMaps = new HashSet<PredicateObjectMap>();
		if (predicateObjectMaps == null) return;
		this.predicateObjectMaps.addAll(predicateObjectMaps);
		// Update prediacte object map
		for (PredicateObjectMap pom : predicateObjectMaps) {
			if (pom.getOwnTriplesMap() != null) {
				if (pom.getOwnTriplesMap() != this)
					throw new IllegalStateException(
							"[StdTriplesMap:setPredicateObjectMap] "
									+ "The predicateObject map child "
									+ "already contains another triples Map !");
			} else
				pom.setOwnTriplesMap(this);
		}
	}

	public LogicalTable getLogicalTable() {
		return logicalTable;
	}

	public Set<PredicateObjectMap> getPredicateObjectMaps() {
		return predicateObjectMaps;
	}

	public SubjectMap getSubjectMap() {
		return subjectMap;
	}

	public void setSubjectMap(SubjectMap subjectMap) throws InvalidR2RMLStructureException {
		/*if (subjectMap == null)
			throw new InvalidR2RMLStructureException(
					"[StdTriplesMap:setLogicalTable] A subject map is required.");*/
		this.subjectMap = subjectMap;

	}

	public void addPredicateObjectMap(PredicateObjectMap predicateObjectMap) {
		if (predicateObjectMap != null)
			predicateObjectMaps.add(predicateObjectMap);
	}

}
