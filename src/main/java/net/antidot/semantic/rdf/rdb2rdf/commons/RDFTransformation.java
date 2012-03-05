/***************************************************************************
 *
 * RDB2RDF commons : RDF Transformations
 *
 * (C) 2011 Antidot (http://www.antidot.net)
 *
 * Module			:	RDB2RDF Commons
 * 
 * Fichier			:	RDFTransformation.java
 *
 * Description		:	The RDF transformation of a SQL datatype is a 
 * 						transformation rule given in the table below, or
 * 						conversion to string if the SQL datatype does not
 * 						occur in the table.
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

public class RDFTransformation {
	
	public enum Transformation {
		BASE_64_ENCODING,
		TO_STRING,
		TO_BOOLEAN,
		TO_DATETIME,
		UNDEFINED
	}
	
	/**
	 * The RDF transformation of a SQL datatype is a transformation rule given in the table below.
	 */
	private static Map<XSDType, Transformation> correspondingTransformation = new HashMap<XSDType, Transformation>();
	
	static {
		correspondingTransformation.put(XSDType.BASE_64_BINARY, Transformation.BASE_64_ENCODING);
		correspondingTransformation.put(XSDType.DECIMAL, Transformation.TO_STRING);
		correspondingTransformation.put(XSDType.INTEGER, Transformation.TO_STRING);
		correspondingTransformation.put(XSDType.DOUBLE, Transformation.TO_STRING);
		correspondingTransformation.put(XSDType.BOOLEAN, Transformation.TO_BOOLEAN);
		correspondingTransformation.put(XSDType.DATE, Transformation.TO_DATETIME);
		correspondingTransformation.put(XSDType.TIME, Transformation.TO_DATETIME);
		correspondingTransformation.put(XSDType.DATETIME, Transformation.TO_DATETIME);
	}
	
	/**
	 * Get the corresponding transformation or conversion to string if the SQL datatype does not occur in the table.
	 */
	public static Transformation getCorrespondingTransformation(XSDType xsdType){
		Transformation t = correspondingTransformation.get(xsdType);
		if (t == null){
			// Any types not appearing in the table, including all character string
			// types and vendor-specific types, will default to producing RDF plain
			// literals by using conversion to string.
			return Transformation.TO_STRING;
		}
		return t;
	}

}
