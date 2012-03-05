/***************************************************************************
 *
 * R2RML Model : R2RMLView Interface
 *
 * (C) 2011 Antidot (http://www.antidot.net)
 *
 * Module			:	R2RMLModel
 * 
 * Fichier			:	R2RMLView.java
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

import java.util.Set;

public interface R2RMLView extends LogicalTable {
	
	public enum SQLVersion {
		SQL2008("SQL2008");
		// The RDB2RDF Working Group intends to maintain a non-normative 
		// list of identifiers for other SQL versions
		
		private String version;
		private SQLVersion(String version){
			this.version = version;
		}
		
		public static SQLVersion getSQLVersion(String version){
			for (SQLVersion sqlVersion : SQLVersion.values())
				if (sqlVersion.toString().equals(version))
					return sqlVersion;
			return null;
		}
		
		public String toString() {
			return version;
		}
	}
	
	/**
	 * A SQL query is a SELECT query in the SQL language that 
	 * can be executed over the input database.  
	 */
	public String getSQLQuery();
	
	/**
	 * An R2RML view may have one or more SQL version identifiers.
	 * The absence of a SQL version identifier indicates that no claim 
	 * to Core SQL 2008 conformance is made.
	 */
	public Set<SQLVersion> getSQLVersion();

}
