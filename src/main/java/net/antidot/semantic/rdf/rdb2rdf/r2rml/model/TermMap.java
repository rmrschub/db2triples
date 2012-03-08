/***************************************************************************
 *
 * R2RML Model : TermMap Interface
 *
 * (C) 2011 Antidot (http://www.antidot.net)
 *
 * Module			:	R2RMLModel
 * 
 * Fichier			:	TermMap.java
 *
 * Description		:	A term map is a function that generates an RDF term
 * 						from a logical table row. The result of that function
 * 						is known as the term map's generated RDF term.
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

import java.util.Map;
import java.util.Set;

import net.antidot.semantic.rdf.rdb2rdf.commons.RDFTransformation;
import net.antidot.semantic.xmls.xsd.XSDType;

import org.openrdf.model.Value;

public interface TermMap {

	/**
	 * A term map must be exactly one of the following types.
	 */
	public enum TermMapType {
		// A constant-valued term map is a term map that ignores the logical
		// table row and always generates the same RDF term
		CONSTANT_VALUED,
		// A column-valued term map is a term map that is represented by a
		// resource that has exactly one rr:column property.
		COLUMN_VALUED,
		// A template-valued term map is a term map that is represented by a
		// resource that has exactly one rr:template property
		TEMPLATE_VALUED,
		//  In db2triples and contrary to the R2RML norm, we accepts
		// auto-assignments of blank nodes.
		NO_VALUE_FOR_BNODE
	}

	public TermMapType getTermMapType();

	/**
	 * The referenced columns of a term map are the set of column names
	 * referenced in the term map and depend on the type of term map.
	 */
	public Set<String> getReferencedColumns();

	/**
	 * The constant value of a constant-valued term map is the RDF term that is
	 * the value of its rr:constant property. Only if CONSTANT_VALUED type.
	 */
	public Value getConstantValue();

	/**
	 * The column value of the term map is the data value of that column in a
	 * given logical table row. Only if COLUMN_VALUED type.
	 */
	public String getColumnValue();

	/**
	 * The value of the rr:template property MUST be a valid string template. A
	 * string template is a format string that can be used to build strings from
	 * multiple components. It can reference column names by enclosing them in
	 * curly braces. Only if TEMPLATE_VALUED type.
	 */
	public String getStringTemplate();

	/**
	 * If the term map has an optional rr:termType property, then its term type
	 * is the value of that property.
	 */
	public TermType getTermType();

	/**
	 * A term map with a term type of rr:Literal MAY have a specified language
	 * tag. It must be valid too.
	 */
	public String getLanguageTag();

	/**
	 * A typeable term map is a term map with a term type of rr:Literal that
	 * does not have a specified langauge tag.
	 */
	public boolean isTypeable();

	/**
	 * Typeable term maps may generate typed literals. The datatype of these
	 * literals can be explicitly specified using rr:datatype.
	 */
	public XSDType getDataType();

	/**
	 * A typeable term map has an implicit datatype. If the term map is a
	 * column-valued term map, then the implicit datatype is the corresponding
	 * RDF datatype of the respective column in the logical table row.
	 * Otherwise, the term map must be a template-valued term map and its
	 * implicit datatype is empty
	 */
	public XSDType getImplicitDataType();

	/**
	 * A datatype override is in effect on a typeable term map if it has a
	 * specified datatype, and the specified datatype is different from its
	 * implicit datatype.
	 */
	public boolean isOveridden();

	/**
	 * A typeable term map has an implicit datatype and an implicit transform.
	 */
	public RDFTransformation.Transformation getImplicitTransformation();

	/**
	 * An inverse expression is a string template associated with a
	 * column-valued term map or template-value term map. It is represented by
	 * the value of the rr:inverseExpression property.
	 * 
	 * Inverse expressions are useful for optimizing term maps that reference
	 * derived columns in R2RML views.
	 * 
	 * An inverse expression MUST satisfy some conditions. (see ref.)
	 */
	public String getInverseExpression();

	/**
	 * The generated RDF term of a term map for a given logical table row is
	 * determined as follows: If the term map is a constant-valued term map,
	 * then the generated RDF term is the term map's constant value. If the term
	 * map is a column-valued term map, then the generated RDF term is
	 * determined by applying the term generation rules to its column value. If
	 * the term map is a template-valued term map, then the generated RDF term
	 * is determined by applying the term generation rules to its template
	 * value.
	 * 
	 * @param dbValues
	 * @return
	 */
	public String getValue(Map<String, String> dbValues);

}
