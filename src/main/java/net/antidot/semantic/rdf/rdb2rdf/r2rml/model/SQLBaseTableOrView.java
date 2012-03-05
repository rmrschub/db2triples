/***************************************************************************
 *
 * R2RML Model : SQLBaseTableOrView Interface
 *
 * (C) 2011 Antidot (http://www.antidot.net)
 *
 * Module			:	R2RMLModel
 * 
 * Fichier			:	SQLBaseTableOrView.java
 *
 * Description		:	A SQL base table or view is a logical table
 * 						containing SQL data from a base table or view
 * 						in the input database.
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

public interface SQLBaseTableOrView extends LogicalTable {
	
	/**
	 * A SQL base tables or views is represented by a resource that has exactly
	 * one rr:tableName property. 
	 */
	public String getTableName();

}
