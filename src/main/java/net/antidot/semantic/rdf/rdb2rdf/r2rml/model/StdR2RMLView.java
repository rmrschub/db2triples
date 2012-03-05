/***************************************************************************
 *
 * R2RML Model : R2RMLView Class
 *
 * (C) 2011 Antidot (http://www.antidot.net)
 *
 * Module			:	R2RMLModel
 * 
 * Fichier			:	StdR2RMLView.java
 *
 * Description		:	An R2RML view is a logical table whose contents
 * 						are the result of executing a SQL query against
 * 						the input database.
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

import net.antidot.sql.model.tools.SQLDataValidator;

public class StdR2RMLView implements R2RMLView {

	private String sqlQuery;
	private Set<SQLVersion> sqlVersion;

	public StdR2RMLView(String sqlQuery) {
		this(sqlQuery, null);
	}

	public StdR2RMLView(String sqlQuery, Set<SQLVersion> sqlVersions) {
		if (sqlQuery == null)
			throw new IllegalArgumentException(
					"[StdStdSQLBaseTableOrView:construct] Query must not have to be NULL.");
		if (!SQLDataValidator.isValidSQLQuery(sqlQuery))
			throw new IllegalArgumentException(
					"[StdStdSQLBaseTableOrView:construct] Query must be SQL valid.");
		this.sqlQuery = sqlQuery;
		this.sqlVersion = new HashSet<SQLVersion>();
		if (sqlVersions == null || sqlVersions.isEmpty())
			// The absence of a SQL version identifier indicates that no claim
			// to
			// Core SQL 2008 conformance is made.
			this.sqlVersion.add(SQLVersion.SQL2008);
		else
			this.sqlVersion.addAll(sqlVersions);
	}

	public String getEffectiveSQLQuery() {
		// The effective SQL query of an R2RML view is the value of its
		// rr:sqlQuery property.
		return sqlQuery;
	}

	public String getSQLQuery() {
		return sqlQuery;
	}

	public Set<SQLVersion> getSQLVersion() {
		return sqlVersion;
	}

	public String toString() {
		return "[StdSQLBaseTableOrView : sqlVersion = " + sqlVersion
				+ "; sqlQuery = " + sqlQuery + "]";
	}
}
