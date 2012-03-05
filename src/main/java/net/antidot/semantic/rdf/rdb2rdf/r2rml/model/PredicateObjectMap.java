/***************************************************************************
 *
 * R2RML Model : PredicateObjectMap Interface
 *
 * (C) 2011 Antidot (http://www.antidot.net)
 *
 * Module			:	R2RMLModel
 * 
 * Fichier			:	PredicateObjectMap.java
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

public interface PredicateObjectMap {

	/**
	 * A predicate-object map is represented by a resource that references
	 * exactly one predicate map.
	 */
	public PredicateMap getPredicateMap();

	public void setPredicateMap(PredicateMap predicateMap);

	/**
	 * A predicate-object map is represented by a resource that references
	 * exactly one object map or one referencing object map. If this method
	 * returns NULL therefore getReferencingObjectMap method will not.
	 */
	public ObjectMap getObjectMap();

	public void setObjectMap(ObjectMap objectMap);

	/**
	 * A predicate-object map is represented by a resource that references
	 * exactly one object map or one referencing object map. If this method
	 * returns NULL therefore getObjectMap method will not.
	 */
	public ReferencingObjectMap getReferencingObjectMap();

	public void setReferencingObjectMap(ReferencingObjectMap referencingOjectMap);

	/**
	 * Indicates if a ReferencingObjectMap is associated with this
	 * PredicateObjectMap. If true, it is a ReferencingObjectMap, a "simple"
	 * ObjectMap otherwise.
	 */
	public boolean hasReferencingObjectMap();

	/**
	 * A Predicate Object Map knows in own Triples Map container.
	 */
	public TriplesMap getOwnTriplesMap();

	public void setOwnTriplesMap(TriplesMap ownTriplesMap);

}
