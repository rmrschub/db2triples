/***************************************************************************
 *
 * R2RML Model : TermType class
 *
 * (C) 2011 Antidot (http://www.antidot.net)
 *
 * Module			:	R2RMLModel
 * 
 * Fichier			:	TermType.java
 *
 * Description		:	The term type of a column-valued term map or 
 * 						template-valued term map determines the kind 
 * 						of generated RDF term (IRIs, blank nodes or literals).
 * 
 * References		:	[R2RML] R2RML: RDB to RDF Mapping Language
 * 						W3C Working Draft 20 September 2011
 * 
 * 						[RDF] Resource Description Framework (RDF): 
 * 						Concepts and Abstract Syntax W3C 
 * 						Recommendation 10 February 2004
 *
 * Options de compilation:
 *
 * Auteurs(s)			:	JHO
 *
 *
 ****************************************************************************/
package net.antidot.semantic.rdf.rdb2rdf.r2rml.model;

import net.antidot.semantic.rdf.rdb2rdf.r2rml.core.R2RMLVocabulary;

public enum TermType {

	IRI(R2RMLVocabulary.R2RMLTerm.IRI.toString()), 
	BLANK_NODE(R2RMLVocabulary.R2RMLTerm.BLANK_NODE.toString()),
	LITERAL(R2RMLVocabulary.R2RMLTerm.LITERAL.toString());

	private String displayName;

	private TermType(String displayName) {
		// The value MUST be an IRI
		this.displayName = R2RMLVocabulary.R2RML_NAMESPACE + displayName;
	}

	public String toString() {
		return displayName;
	}

	public String getDisplayName() {
		return displayName;
	}

	/**
	 * Converts a termType from its display name.
	 * 
	 * @param displayName
	 * @return
	 */
	public static TermType toTermType(String displayName) {
		for (TermType termType : TermType.values()) {
			if (termType.getDisplayName().equals(displayName))
				return termType;
		}
		return null;
	}

}
