/***************************************************************************
 *
 * RDF Toolkit
 *
 * (C) 2011 Antidot (http://www.antidot.net)
 *
 * Module			:	Main
 * 
 * Fichier			:	RDFToolkit.java
 *
 * Description		:   Collection of useful tool-methods used for valid RDF
 * 						data. 
 *
 * Options de compilation:
 *
 * Auteurs(s)			:	JHO
 *
 *
 ****************************************************************************/
package net.antidot.semantic.rdf.model.tools;

import java.util.Locale;
import java.util.MissingResourceException;

import net.antidot.semantic.xmls.xsd.XSDType;

import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

public abstract class RDFDataValidator {
	
	// Value factory
	private static ValueFactory vf = new ValueFactoryImpl();

	/**
	 * Check if URI is valid. The URI syntax consists of a URI scheme name (such
	 * as "http", "ftp", "mailto" or "file") followed by a colon character, and
	 * then by a scheme-specific part. The specifications that govern the
	 * schemes determine the syntax and semantics of the scheme-specific part,
	 * although the URI syntax does force all schemes to adhere to a certain
	 * generic syntax that, among other things, reserves certain characters for
	 * special purposes (without always identifying those purposes). The syntax
	 * respects the standard RFC 3986. Currently, this function uses the Java
	 * checking of URI object which throws an IllegalArgumentException if the
	 * given string violates RFC 2396 (ancestor of RDF 3986). // TODO : adapt
	 * this method for RFC 3986. Reference : http://tools.ietf.org/html/rfc3986
	 */
	public static boolean isValidURI(String strURI) {
		boolean isValid = false;
		try {
			// java.net.URI.create(strURI);
			// All cases are not take into account... use openRDF library
			vf.createURI(strURI);
			isValid = true;
		} catch (IllegalArgumentException e) {
			// Nothing
		}
		return isValid;
	}

	/**
	 * TODO
	 * 
	 * @param languageTag
	 * @return
	 */
	public static boolean isValidLanguageTag(String languageTag) {
		try {
			String[] split = languageTag.split("-");
			if (split.length > 2)
				return false;
			else {
				if (split.length == 1) {
					// Simple language code
					Locale l = new Locale(languageTag);
					l.getISO3Language();
				} else {
					// Extract language code
					String languageCode = split[0];
					// Extract country code
					String countryCode = split[1];
					Locale l = new Locale(languageCode, countryCode);
					l.getISO3Language();
					l.getISO3Country();
				}
			}
		} catch (MissingResourceException e) {
			return false;
		}
		return true;
	}

	public static boolean isValidLiteral(String stringValue) {
		// TODO
		return false;
	}

	/**
	 * The set of validatable RDF datatypes includes all datatypes 
	 * in the RDF datatype column of the table of natural datatype
	 * mappings, as defined in [XMLSCHEMA2]. 
	 * @param string
	 * @return
	 */
	public static boolean isValidDatatype(String datatype) {
		boolean isValid = true;
		if (!isValidURI(datatype)) return false;
		try {
			XSDType.toXSDType(datatype);
		} catch (IllegalArgumentException e) {
			isValid = false;
		}
		return isValid;
	}
	
	/**
	 * A typed literal is ill-typed in R2RML if its datatype IRI denotes 
	 * a validatable RDF datatype and its lexical form is not in the 
	 * lexical space of the RDF datatype identified by its datatype IRI. 
	 * (See also: Summary of XSD Lexical Forms)
	 * @param dataype
	 * @param Value
	 * @return
	 */
	public static boolean isIllTyped(XSDType dataype, Object Value){
		// TODO
		return false;
	}

}
