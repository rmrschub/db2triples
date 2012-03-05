/***************************************************************************
 *
 * R2RML Model : SQLBaseTableOrView Class
 *
 * (C) 2011 Antidot (http://www.antidot.net)
 *
 * Module			:	R2RMLModel
 * 
 * Fichier			:	StdSQLBaseTableOrView.java
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

import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.InvalidR2RMLSyntaxException;
import net.antidot.sql.model.tools.SQLDataValidator;

public class StdSQLBaseTableOrView implements SQLBaseTableOrView {

	private String tableName;

	public StdSQLBaseTableOrView(String tableName)
			throws InvalidR2RMLSyntaxException {
		if (tableName == null)
			throw new IllegalArgumentException(
					"[StdStdSQLBaseTableOrView:construct] Table name must not have to be NULL.");
		if (!SQLDataValidator.isValidSQLIdentifier(tableName))
			throw new InvalidR2RMLSyntaxException(
					"[StdStdSQLBaseTableOrView:construct] Table name must be a valid schema-qualified"
							+ " name.");
		this.tableName = tableName;
	}

	public String getEffectiveSQLQuery() {
		// The effective SQL query of a SQL base table or view is SELECT * FROM
		// {table}
		// with {table} replaced with the table or view name.
		return "SELECT * FROM " + tableName;
	}
	
	public String getTableName() {
		return tableName;
	}
	
	public String toString(){
		return "[StdSQLBaseTableOrView : tableName = " + tableName + "]";
	}

}
