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
 * XMLS XSD datatype : Lexical transformation
 *
 * Lexical transformation used to convert SQL datatype to RDF datatype.
 *
 ****************************************************************************/
package net.antidot.semantic.xmls.xsd;

import java.util.HashMap;
import java.util.Map;

import sun.misc.BASE64Encoder;

public class XSDLexicalTransformation{
	
	public enum Transformation {
		BASE_64_ENCODING, NONE_REQUIRED, ENSURE_LOWERCASE, REPLACE_SPACE_CHARACTER, UNDEFINED
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
				Transformation.NONE_REQUIRED);
		correspondingTransformation.put(XSDType.INTEGER,
				Transformation.NONE_REQUIRED);
		correspondingTransformation.put(XSDType.DOUBLE,
				Transformation.NONE_REQUIRED);
		correspondingTransformation.put(XSDType.BOOLEAN,
				Transformation.ENSURE_LOWERCASE);
		correspondingTransformation.put(XSDType.DATE,
				Transformation.NONE_REQUIRED);
		correspondingTransformation.put(XSDType.TIME,
				Transformation.NONE_REQUIRED);
		correspondingTransformation.put(XSDType.DATETIME,
				Transformation.REPLACE_SPACE_CHARACTER);
	}

	/**
	 * Get the corresponding transformation or conversion to string if the SQL
	 * datatype does not occur in the table.
	 */
	public static Transformation getLexicalTransformation(XSDType xsdType) {
		Transformation t = correspondingTransformation.get(xsdType);
		if (t == null) {
			// Any types not appearing in the table, including all character
			// string
			// types and vendor-specific types, will default to producing RDF
			// plain
			// literals by using conversion to string.
			return Transformation.NONE_REQUIRED;
		}
		return t;
	}
	
	/**
	 * Transform a given value to its form obtained by its corresponding lexical 
	 * transformation.
	 * @param value
	 * @param transformation
	 * @return
	 */
	public static String transform(String value, Transformation transformation){
		String result = value;
		
		switch (transformation) {
		case NONE_REQUIRED:
			return value;
		case UNDEFINED:
			throw new IllegalArgumentException("[SQLLexicalTransformation:transform] Unkonw lexical transformation.");
		case ENSURE_LOWERCASE:
			return ensureLowercase(value);
		case REPLACE_SPACE_CHARACTER:
			return replaceSpaceCharacter(value);
		case BASE_64_ENCODING:
			return base64Encoding(value);
		default:
			break;
		}
		return result;
	}
	
	private static String base64Encoding(String value) {
		BASE64Encoder encoder = new BASE64Encoder();
		byte[] bytes = value.getBytes();
		String result = encoder.encode(bytes);
		result = result.replaceAll("[\r\n]+", "");
		return result;
	}

	private static String replaceSpaceCharacter(String value) {
		return value.replace(' ', 'T');
	}

	private static String ensureLowercase(String value) {
		return value.toLowerCase();
	}
	
	public static String extractNaturalRDFFormFrom(XSDType xsdType, String value) {
		String result = value;
		if (xsdType != null) {
			// 3. Otherwise, if dt is listed in the table below: The result
			// is a
			// typed literal whose datatype IRI is the IRI indicated in the
			// RDF
			// Lexical transformation
			result = XSDLexicalTransformation.transform(value,
					XSDLexicalTransformation.getLexicalTransformation(xsdType));
			// Canonical lexical form
			result = XSDLexicalForm.getCanonicalLexicalForm(result, xsdType);
		}
		return result;

	}

}
