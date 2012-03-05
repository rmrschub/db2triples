/***************************************************************************
 *
 * R2RML Model : Standard ObjectMap Interface
 *
 * (C) 2011 Antidot (http://www.antidot.net)
 *
 * Module			:	R2RMLModel
 * 
 * Fichier			:	StdObjectMap.java
 *
 * Description		:	An object map is a specific term map used for 
 * 						representing RDF object. 
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

public interface ObjectMap extends TermMap {
	
	/**
	 * A object map knows in own Predicate Object container.
	 */
	public PredicateObjectMap getPredicateObjectMap();
	public void setPredicateObjectMap(PredicateObjectMap predicateObjectMap);

}
