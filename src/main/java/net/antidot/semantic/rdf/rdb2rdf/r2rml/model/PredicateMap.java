/***************************************************************************
 *
 * R2RML Model : Standard PredicateMap Interface
 *
 * (C) 2011 Antidot (http://www.antidot.net)
 *
 * Module			:	R2RMLModel
 * 
 * Fichier			:	PredicateMap.java
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

public interface PredicateMap extends TermMap {
	
	/**
	 * A Predicate Map knows in own Predicate Object container.
	 */
	public PredicateObjectMap getPredicateObjectMap();
	public void setPredicateObjectMap(PredicateObjectMap predicateObjectMap);

}
