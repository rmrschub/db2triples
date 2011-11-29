/* 
 * Copyright 2011 Antidot opensource@antidot.net
 * https://github.com/antidot/db2triples
 * 
 * DB2Triples is free software; you can redistribute it and/or 
 * modify it under the terms of the GNU General Public License as 
 * published by the Free Software Foundation; either version 2 of 
 * the License, or (at your option) any later version.
 * 
 * DB2Triples is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 *
 * SQLToRDF Toolkit
 *
 * Collection of useful tool-methods used for conversion between SQL and RDF. 
 *
 * @author jhomo
 *
 */
package antidot.rdf.tools;

import java.util.HashMap;
import java.util.Map;

import antidot.sql.type.SQLType;
import antidot.xmls.type.XSDType;

public abstract class SQLToRDFToolkit {

	/**
	 * Equivalence datatype between standard SQL types and XSD types according
	 * to XML Schema Part 2: Datatypes Second Edition (W3C Recommendation 28
	 * October 2004) See : http://www.w3.org/TR/xmlschema-2/
	 */
	private static Map<SQLType, XSDType> equivalentTypes = new HashMap<SQLType, XSDType>();
	
	static {
		// Text types
		equivalentTypes.put(SQLType.CHAR, XSDType.STRING); // A fixed
		// section
		// from
		// 0 to
		// 255
		// characters long.
		equivalentTypes.put(SQLType.VARCHAR, XSDType.STRING); // A
		// variable
		// section
		// from
		// 0
		// to 255 characters long.
		equivalentTypes.put(SQLType.TINYTEXT, XSDType.STRING); // A
		// string
		// with
		// a
		// maximum
		// length of 255 characters.
		equivalentTypes.put(SQLType.TEXT, XSDType.STRING); // A string
		// with
		// a
		// maximum
		// length
		// of 65535 characters.
		equivalentTypes.put(SQLType.BLOB, XSDType.STRING); // A string
		// with
		// a
		// maximum
		// length
		// of 65535 characters.
		equivalentTypes.put(SQLType.MEDIUMTEXT, XSDType.STRING); // A
		// string
		// with
		// a
		// maximum
		// length of 16777215
		// characters.
		equivalentTypes.put(SQLType.MEDIUMBLOB, XSDType.STRING); // A
		// string
		// with
		// a
		// maximum
		// length of 16777215
		// characters.
		equivalentTypes.put(SQLType.LONGTEXT, XSDType.STRING); // A
		// string
		// with
		// a
		// maximum
		// length of 4294967295
		// characters.
		equivalentTypes.put(SQLType.LONGBLOB, XSDType.STRING); // A
		// string
		// with
		// a
		// maximum
		// length of 4294967295
		// characters.

		// Number types
		equivalentTypes.put(SQLType.BIT, XSDType.BYTE); // 0 or 1
		equivalentTypes.put(SQLType.TINYINT, XSDType.BYTE); // -128 to
		// 127
		// normal
		equivalentTypes.put(SQLType.UNSIGNED_TINYINT, XSDType.UNSIGNED_BYTE); // 0
																				// to
																				// 255
		// UNSIGNED.
		equivalentTypes.put(SQLType.SMALLINT, XSDType.SHORT); // -32768
		// to
		// 32767
		// normal
		equivalentTypes
				.put(SQLType.UNSIGNED_SMALLINT, XSDType.UNSIGNED_SHORT); // 0
																			// to
																			// 65535
		// UNSIGNED.
		equivalentTypes.put(SQLType.MEDIUMINT, XSDType.INT); // -8388608
		// to
		// 8388607
		// normal.
		// // TODO : find another
		// equivalent ?
		equivalentTypes.put(SQLType.UNSIGNED_MEDIUMINT, XSDType.INT); // 0
		// to
		// 16777215
		// UNSIGNED. // TODO
		// : find another
		// equivalent ?
		equivalentTypes.put(SQLType.INT, XSDType.INT); // -2147483648
		// to
		// 2147483647
		// normal.
		equivalentTypes.put(SQLType.UNSIGNED_INT, XSDType.UNSIGNED_INT); // 0
																			// to
																			// 4294967295
		// UNSIGNED.
		equivalentTypes.put(SQLType.BIGINT, XSDType.LONG); // -9223372036854775808
		// to
		// 9223372036854775807 normal.
		equivalentTypes.put(SQLType.UNSIGNED_BIGINT, XSDType.UNSIGNED_LONG); // 0
																				// to
		// 18446744073709551615
		// UNSIGNED.
		equivalentTypes.put(SQLType.FLOAT, XSDType.FLOAT); // A small
		// number
		// with
		// a
		// floating
		// decimal point.
		equivalentTypes.put(SQLType.UNSIGNED_FLOAT, XSDType.FLOAT); // A
		// small
		// number
		// with
		// a
		// floating
		// decimal point.
		equivalentTypes.put(SQLType.DOUBLE, XSDType.DOUBLE); // A
		// small
		// number
		// with
		// a
		// floating
		// decimal point.
		equivalentTypes.put(SQLType.UNSIGNED_DOUBLE, XSDType.DOUBLE); // A
		// large
		// number
		// with
		// a
		// floating decimal point.
		equivalentTypes.put(SQLType.DECIMAL, XSDType.DECIMAL); // A
		// large
		// number
		// with
		// a
		// floating decimal point.
		equivalentTypes.put(SQLType.UNSIGNED_DECIMAL, XSDType.DECIMAL); // A
		// DOUBLE
		// stored
		// as
		// a
		// string , allowing for a
		// fixed decimal point.

		// Date types
		equivalentTypes.put(SQLType.DATE, XSDType.DATE); // YYYY-MM-DD
		// ("1000-01-01"
		// -
		// "9999-12-31").
		equivalentTypes.put(SQLType.DATETIME, XSDType.DATETIME); // YYYY-MM-DD
		// HH:MM:SS
		// ("1000-01-01 00:00:00" - "9999-12-31 23:59:59").
		// xsd:datetime doesn't match because the letter T is required ?
		// No, it's valid. See W3C Working Draft (24/03/2011), Section 2.3.4.
		equivalentTypes.put(SQLType.TIMESTAMP, XSDType.DATETIME); // YYYYMMDDHHMMSS
		// (19700101000000 - 2037+).
		// TODO : equivalent ?
		equivalentTypes.put(SQLType.TIME, XSDType.TIME); // HH:MM:SS
		// ("-838:59:59"
		// -
		// "838:59:59").
		equivalentTypes.put(SQLType.YEAR, XSDType.GYEAR); // YYYY
		// (1900 -
		// 2155).

		// Misc types
		// TODO : equivalent ?
		equivalentTypes.put(SQLType.ENUM, XSDType.ENUMERATION); // Short
		// for
		// ENUMERATION which
		// means
		// that each column may have one of
		// a specified possible values.
		equivalentTypes.put(SQLType.SET, XSDType.ENUMERATION); // Similar
		// to
		// ENUM
		// except each
		// column
		// may have more than one of the
		// specified possible values.
		
		/**
		 * PostGreSQL equivalences.
		 */
		
		equivalentTypes.put(SQLType.INT4, XSDType.INTEGER);
		equivalentTypes.put(SQLType.FLOAT4, XSDType.FLOAT);
		equivalentTypes.put(SQLType.POINT, XSDType.STRING);
		equivalentTypes.put(SQLType.BIGSERIAL, XSDType.INTEGER);
		equivalentTypes.put(SQLType.VARBIT, XSDType.INT);
		equivalentTypes.put(SQLType.BIT_VARYING, XSDType.INT);
		equivalentTypes.put(SQLType.BOOL, XSDType.BYTE);
		equivalentTypes.put(SQLType.BPCHAR, XSDType.STRING);
		equivalentTypes.put(SQLType.BOOLEAN, XSDType.BYTE);
		equivalentTypes.put(SQLType.BOX, XSDType.STRING);
		equivalentTypes.put(SQLType.BYTEA, XSDType.STRING);
		equivalentTypes.put(SQLType.CHARACTER_VARYING, XSDType.STRING);
		equivalentTypes.put(SQLType.CHARACTER, XSDType.STRING);
		equivalentTypes.put(SQLType.CIDR, XSDType.STRING);
		equivalentTypes.put(SQLType.CIRCLE, XSDType.STRING);
		equivalentTypes.put(SQLType.DOUBLE_PRECISION, XSDType.DOUBLE);
		equivalentTypes.put(SQLType.FLOAT8, XSDType.FLOAT);
		equivalentTypes.put(SQLType.INET, XSDType.STRING);
		equivalentTypes.put(SQLType.INT2, XSDType.INTEGER);
		equivalentTypes.put(SQLType.INT8, XSDType.INTEGER);
		equivalentTypes.put(SQLType.INTERVAL, XSDType.STRING);
		equivalentTypes.put(SQLType.LINE, XSDType.STRING);
		equivalentTypes.put(SQLType.LSEG, XSDType.STRING);
		equivalentTypes.put(SQLType.MACADDR, XSDType.STRING);
		equivalentTypes.put(SQLType.MONEY, XSDType.STRING);
		equivalentTypes.put(SQLType.NUMERIC, XSDType.DECIMAL);
		equivalentTypes.put(SQLType.PATH, XSDType.STRING);
		equivalentTypes.put(SQLType.POINT, XSDType.STRING);
		equivalentTypes.put(SQLType.POLYGON, XSDType.STRING);
		equivalentTypes.put(SQLType.REAL, XSDType.FLOAT);
		equivalentTypes.put(SQLType.SERIAL, XSDType.STRING);
		equivalentTypes.put(SQLType.SERIAL4, XSDType.INTEGER);
		equivalentTypes.put(SQLType.TIMETZ, XSDType.FLOAT);
		equivalentTypes.put(SQLType.TIMESTAMPTZ, XSDType.STRING);
		equivalentTypes.put(SQLType.POLYGON, XSDType.INTEGER);
		equivalentTypes.put(SQLType.REAL, XSDType.FLOAT);
		equivalentTypes.put(SQLType.SERIAL, XSDType.STRING);
		
		
		equivalentTypes.put(SQLType.UNKNOW, XSDType.STRING);
	}
	
	public static XSDType getEquivalentType(SQLType SQLType){
		return equivalentTypes.get(SQLType);
	}
	
	public static XSDType getEquivalentType(String sqlType){
		return equivalentTypes.get(SQLType.toSQLType(sqlType));
	}
	
	public static boolean isValidSQLDatatype(String datatype){
		return equivalentTypes.keySet().contains(SQLType.toSQLType(datatype));
	}

}
