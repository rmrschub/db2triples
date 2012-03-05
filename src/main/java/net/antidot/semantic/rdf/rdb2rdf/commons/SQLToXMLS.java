/***************************************************************************
 *
 * SQLToRDF Toolkit
 *
 * (C) 2011 Antidot (http://www.antidot.net)
 *
 * Module			:	Main
 * 
 * Fichier			:	SQLToRDFToolkit.java
 *
 * Description		:   Collection of useful tool-methods used for conversion 
 * 						between SQL and RDF 
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
package net.antidot.semantic.rdf.rdb2rdf.commons;

import java.util.HashMap;
import java.util.Map;

import net.antidot.semantic.xmls.xsd.XSDType;
import net.antidot.sql.model.type.SQLType;

public abstract class SQLToXMLS {

	/**
	 * Equivalence datatype between standard SQL types and XSD types.
	 */
	private static Map<SQLType, XSDType> equivalentTypes = new HashMap<SQLType, XSDType>();
	
	static {
		equivalentTypes.put(SQLType.BINARY, XSDType.BASE_64_BINARY);
		equivalentTypes.put(SQLType.BINARY_VARYING, XSDType.BASE_64_BINARY);
		equivalentTypes.put(SQLType.NUMERIC, XSDType.DECIMAL);
		equivalentTypes.put(SQLType.DECIMAL, XSDType.DECIMAL);
		equivalentTypes.put(SQLType.SMALLINT, XSDType.INTEGER);
		equivalentTypes.put(SQLType.INTEGER, XSDType.INTEGER);
		equivalentTypes.put(SQLType.BIGINT, XSDType.INTEGER);
		equivalentTypes.put(SQLType.FLOAT, XSDType.DOUBLE);
		equivalentTypes.put(SQLType.REAL, XSDType.DOUBLE);
		equivalentTypes.put(SQLType.DOUBLE_PRECISION, XSDType.DOUBLE);
		equivalentTypes.put(SQLType.BOOLEAN, XSDType.BOOLEAN);
		equivalentTypes.put(SQLType.DATE, XSDType.DATE);
		equivalentTypes.put(SQLType.TIME, XSDType.TIME);
		equivalentTypes.put(SQLType.TIMESTAMP, XSDType.DATETIME);
	}
	
	public static XSDType getEquivalentType(SQLType sqlType){
		return equivalentTypes.get(sqlType);
	}
	
	public static XSDType getEquivalentType(int sqlType){
		return equivalentTypes.get(SQLType.toSQLType(sqlType));
	}
	
	public static boolean isValidSQLDatatype(int datatype){
		return equivalentTypes.keySet().contains(SQLType.toSQLType(datatype));
	}

}
