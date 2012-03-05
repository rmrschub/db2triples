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

public class StdPredicateObjectMap implements PredicateObjectMap {

	private ObjectMap objectMap;
	private ReferencingObjectMap refObjectMap;
	private PredicateMap predicateMap;
	protected TriplesMap ownTriplesMap;

	private StdPredicateObjectMap(PredicateMap predicateMap) {
		this.predicateMap = predicateMap;
	}

	public StdPredicateObjectMap(PredicateMap predicateMap, ObjectMap objectMap) {
		this(predicateMap);
		setObjectMap(objectMap);
	}

	public StdPredicateObjectMap(PredicateMap predicateMap,
			ReferencingObjectMap refObjectMap) {
		this(predicateMap);
		setReferencingObjectMap(refObjectMap);
	}

	public void setReferencingObjectMap(ReferencingObjectMap refObjectMap) {
		if (refObjectMap.getPredicateObjectMap() != null) {
			if (refObjectMap.getPredicateObjectMap() != this)
				throw new IllegalStateException(
						"[StdPredicateObjectMap:setRefObjectMap] "
								+ "The referencing object map child "
								+ "already contains another PredicateObject Map !");
		} else 
			refObjectMap.setPredicateObjectMap(this);
		this.refObjectMap = refObjectMap;
		
	}

	public ObjectMap getObjectMap() {
		return objectMap;
	}

	public PredicateMap getPredicateMap() {
		return predicateMap;
	}

	public ReferencingObjectMap getReferencingObjectMap() {
		return refObjectMap;
	}

	public boolean hasReferencingObjectMap() {
		return refObjectMap != null;
	}

	public TriplesMap getOwnTriplesMap() {
		return ownTriplesMap;
	}

	public void setObjectMap(ObjectMap objectMap) {
		if (objectMap.getPredicateObjectMap() != null) {
			if (objectMap.getPredicateObjectMap() != this)
				throw new IllegalStateException(
						"[StdPredicateObjectMap:setObjectMap] "
								+ "The object map child "
								+ "already contains another PredicateObject Map !");
		} else 
			objectMap.setPredicateObjectMap(this);
		this.objectMap = objectMap;
	}

	public void setOwnTriplesMap(TriplesMap ownTriplesMap) {
		// Update triples map if not contains this subject map
		if (ownTriplesMap.getSubjectMap() != null)
			if (!ownTriplesMap.getPredicateObjectMaps().contains(this))
				ownTriplesMap.addPredicateObjectMap(this);
		this.ownTriplesMap = ownTriplesMap;
	}

	public void setPredicateMap(PredicateMap predicateMap) {
		if (predicateMap.getPredicateObjectMap() != null) {
			if (predicateMap.getPredicateObjectMap() != this)
				throw new IllegalStateException(
						"[StdPredicateObjectMap:setPredicateMap] "
								+ "The predicate map child "
								+ "already contains another PredicateObject Map !");
		} else 
			predicateMap.setPredicateObjectMap(this);
		this.predicateMap = predicateMap;
	}

}
