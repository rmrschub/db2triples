/***************************************************************************
 *
 * R2RML Model : Join Condition Interface
 *
 * (C) 2011 Antidot (http://www.antidot.net)
 *
 * Module			:	R2RMLModel
 * 
 * Fichier			:	StdObjectMap.java
 *
 * Description		:	A join condition is a resource that has 
 * 						exactly two properties: 
 * 						- rr:child, whose value is known as the
 * 						  join condition's child column
 * 						- rr:parent, whose value is known as the
 * 						  join condition's parent column 
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

public interface JoinCondition {
	
	/**
	 * Child column name must be a column name that exists in the logical table
	 * of the triples map that contains the referencing object map
	 */
	public String getChild();
	
	/**
	 * Parent column name must be a column name that exists in the logical table of the
	 * referencing object map's parent triples map.
	 */
	public String getParent();

}
