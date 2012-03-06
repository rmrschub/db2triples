/***************************************************************************
 *
 * R2RML Model : Standard PredicateObjectMap Class
 *
 * (C) 2011 Antidot (http://www.antidot.net)
 *
 * Module			:	R2RMLModel
 * 
 * Fichier			:	StdPredicateObjectMap.java
 *
 * Description		:	A predicate-object map is a function
 * 						that creates predicate-object pairs from logical 
 * 						table rows. It is used in conjunction with a subject
 * 						map to generate RDF triples in a triples map.
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

public class StdPredicateObjectMap implements PredicateObjectMap {

	private Set<ObjectMap> objectMaps;
	private Set<ReferencingObjectMap> refObjectMaps;
	private Set<PredicateMap> predicateMaps;
	protected TriplesMap ownTriplesMap;
	private HashSet<GraphMap> graphMaps;

	private StdPredicateObjectMap(Set<PredicateMap> predicateMaps) {
		setPredicateMaps(predicateMaps);
	}

	public StdPredicateObjectMap(Set<PredicateMap> predicateMaps,
			Set<ObjectMap> objectMaps) {
		this(predicateMaps);
		setObjectMaps(objectMaps);
	}
	
	public StdPredicateObjectMap(Set<PredicateMap> predicateMaps,
			Set<ObjectMap> objectMaps, Set<ReferencingObjectMap> referencingObjectMaps) {
		this(predicateMaps, objectMaps);
		setReferencingObjectMap(referencingObjectMaps);
	}

	public void setReferencingObjectMap(Set<ReferencingObjectMap> refObjectMaps) {
		if (refObjectMaps == null)
			this.refObjectMaps = new HashSet<ReferencingObjectMap>();
		else {
			for (ReferencingObjectMap refObjectMap : refObjectMaps) {
				if (refObjectMap != null)
					refObjectMap.setPredicateObjectMap(this);
			}
			this.refObjectMaps = refObjectMaps;
		}
	}

	public Set<ObjectMap> getObjectMaps() {
		return objectMaps;
	}

	public Set<PredicateMap> getPredicateMaps() {
		return predicateMaps;
	}

	public Set<ReferencingObjectMap> getReferencingObjectMaps() {
		return refObjectMaps;
	}

	public boolean hasReferencingObjectMaps() {
		return refObjectMaps != null && !refObjectMaps.isEmpty();
	}

	public TriplesMap getOwnTriplesMap() {
		return ownTriplesMap;
	}

	public void setObjectMaps(Set<ObjectMap> objectMaps) {
		if (objectMaps == null)
			this.objectMaps = new HashSet<ObjectMap>();
		else {
			for (ObjectMap objectMap : objectMaps) {
				if (objectMap != null)
					objectMap.setPredicateObjectMap(this);
			}
			this.objectMaps = objectMaps;
		}
	}

	public void setOwnTriplesMap(TriplesMap ownTriplesMap) {
		// Update triples map if not contains this subject map
		if (ownTriplesMap.getSubjectMap() != null)
			if (!ownTriplesMap.getPredicateObjectMaps().contains(this))
				ownTriplesMap.addPredicateObjectMap(this);
		this.ownTriplesMap = ownTriplesMap;
	}

	public void setPredicateMaps(Set<PredicateMap> predicateMaps) {
		if (predicateMaps == null)
			this.predicateMaps = new HashSet<PredicateMap>();
		else {
			for (PredicateMap predicateMap : predicateMaps) {
				if (predicateMap != null)
					predicateMap.setPredicateObjectMap(this);
			}
			this.predicateMaps = predicateMaps;
		}
	}
	
	public Set<GraphMap> getGraphMaps() {
		return graphMaps;
	}
	
	public void setGraphMaps(Set<GraphMap> graphMaps) {
		this.graphMaps = new HashSet<GraphMap>();
		graphMaps.addAll(graphMaps);
	}

}
