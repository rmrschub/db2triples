/***************************************************************************
 *
 * R2RML Model : LogicalTable Interface
 *
 * (C) 2011 Antidot (http://www.antidot.net)
 *
 * Module			:	R2RMLModel
 * 
 * Fichier			:	LogicalTable.java
 *
 * Description		:	A logical table is a possibly virtual database
 * 						table that is to be mapped to RDF triples. 
 * 						A logical table is either a SQL base table or view,
 *  					or a R2RML view.
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

public interface LogicalTable {

	/**
	 * Every logical table has an effective SQL query that,
	 * if executed over the SQL connection, produces as its
	 * result the contents of the logical table.
	 */
	public String getEffectiveSQLQuery();

}
