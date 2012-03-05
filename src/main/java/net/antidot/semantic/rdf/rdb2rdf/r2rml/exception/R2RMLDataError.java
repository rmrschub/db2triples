/***************************************************************************
 *
 * R2RML Exception : R2RML Data Error
 *
 * (C) 2011 Antidot (http://www.antidot.net)
 *
 * Module			:	Exception
 * 
 * Fichier			:	R2RMLDataError.java
 *
 * Description		:	A data error is a condition of the data in the input 
 * 						database that would lead to the generation of an
 * 						invalid RDF term, such as an invalid IRI or an
 * 						ill-typed literal.
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
package net.antidot.semantic.rdf.rdb2rdf.r2rml.exception;

public class R2RMLDataError extends Exception {

	private static final long serialVersionUID = 1L;
	
	public R2RMLDataError(String message) {
		super(message);
	}

}
