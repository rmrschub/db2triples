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
/***************************************************************************
 *
 * RDB2RDF Commons : RDF Transformations
 *
 * The RDF transformation of a SQL datatype is a 
 * transformation rule given in the table below, or
 * conversion to string if the SQL datatype does not
 * occur in the table.
 *
 ****************************************************************************/
package net.antidot.semantic.rdf.rdb2rdf.commons;

import java.util.HashMap;
import java.util.Map;

import net.antidot.semantic.xmls.xsd.XSDType;

public class RDFTransformation {

	public enum Transformation {
		BASE_64_ENCODING, TO_STRING, TO_BOOLEAN, TO_DATETIME, UNDEFINED
	}

	/**
	 * The RDF transformation of a SQL datatype is a transformation rule given
	 * in the table below.
	 */
	private static Map<XSDType, Transformation> correspondingTransformation = new HashMap<XSDType, Transformation>();

	static {
		correspondingTransformation.put(XSDType.BASE_64_BINARY,
				Transformation.BASE_64_ENCODING);
		correspondingTransformation.put(XSDType.DECIMAL,
				Transformation.TO_STRING);
		correspondingTransformation.put(XSDType.INTEGER,
				Transformation.TO_STRING);
		correspondingTransformation.put(XSDType.DOUBLE,
				Transformation.TO_STRING);
		correspondingTransformation.put(XSDType.BOOLEAN,
				Transformation.TO_BOOLEAN);
		correspondingTransformation.put(XSDType.DATE,
				Transformation.TO_DATETIME);
		correspondingTransformation.put(XSDType.TIME,
				Transformation.TO_DATETIME);
		correspondingTransformation.put(XSDType.DATETIME,
				Transformation.TO_DATETIME);
	}

	/**
	 * Get the corresponding transformation or conversion to string if the SQL
	 * datatype does not occur in the table.
	 */
	public static Transformation getCorrespondingTransformation(XSDType xsdType) {
		Transformation t = correspondingTransformation.get(xsdType);
		if (t == null) {
			// Any types not appearing in the table, including all character
			// string
			// types and vendor-specific types, will default to producing RDF
			// plain
			// literals by using conversion to string.
			return Transformation.TO_STRING;
		}
		return t;
	}

}
