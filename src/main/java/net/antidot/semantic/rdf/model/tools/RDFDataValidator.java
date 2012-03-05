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

public abstract class RDFDataValidator {

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
		boolean isValid = true;
		try {
			java.net.URI.create(strURI);
		} catch (IllegalArgumentException e) {
			isValid = false;
		}
		return isValid;
	}

	/**
	 * TODO
	 * @param languageTag
	 * @return
	 */
	public static boolean isValidLanguageTag(String languageTag) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public static boolean isValidLiteral(String stringValue) {
		// TODO
		return false;
	}

}
