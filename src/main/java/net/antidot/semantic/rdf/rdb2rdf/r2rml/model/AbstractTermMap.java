package net.antidot.semantic.rdf.rdb2rdf.r2rml.model;

import java.util.HashSet;
import java.util.Set;

import net.antidot.semantic.rdf.model.tools.RDFDataValidator;
import net.antidot.semantic.rdf.rdb2rdf.commons.RDFTransformation;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.InvalidR2RMLStructureException;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.InvalidR2RMLSyntaxException;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.R2RMLDataError;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.tools.R2RMLToolkit;
import net.antidot.semantic.xmls.xsd.XSDType;
import net.antidot.sql.model.tools.SQLDataValidator;

import org.openrdf.model.URI;
import org.openrdf.model.Value;

public abstract class AbstractTermMap implements TermMap {

	
	private Value constantValue;
	private XSDType dataType;
	private TermType termType;
	private XSDType implicitDataType;
	private String languageTag;
	private String stringTemplate;
	private String columnValue;
	private String inverseExpression;

	protected AbstractTermMap(Value constantValue, URI dataType,
			String languageTag, String stringTemplate, URI termType,
			String inverseExpression, String columnValue)
			throws R2RMLDataError, InvalidR2RMLStructureException,
			InvalidR2RMLSyntaxException {

		setConstantValue(constantValue);
		setColumnValue(columnValue);
		setDataType(dataType);
		setLanguageTag(languageTag);
		setStringTemplate(stringTemplate);
		setTermType(termType);
		setInversionExpression(inverseExpression);
		checkGlobalConsistency();
	}

	
	/**
	 * Check if the global structure of this TermMap is consistent and valid
	 * according to R2RML standard.
	 * 
	 * @throws InvalidR2RMLStructureException
	 */
	private void checkGlobalConsistency() throws InvalidR2RMLStructureException {
		// A term map must be exactly one term map type
		if (getTermMapType() == null)
			throw new InvalidR2RMLStructureException(
					"[AbstractTermMap:checkGlobalConsistency] A constant RDF Term,"
							+ " a column name or a string template must be specified.");
	
	}

	private void setInversionExpression(String inverseExpression)
			throws InvalidR2RMLSyntaxException {
		// This property is optional
		if (inverseExpression != null)
			checkInverseExpression(inverseExpression);
		this.inverseExpression = inverseExpression;
	}

	private void checkInverseExpression(String inverseExpression)
			throws InvalidR2RMLSyntaxException {
		// An inverse expression must satisfy a lot of conditions
		if (!R2RMLToolkit.checkInverseExpression(inverseExpression))
			throw new InvalidR2RMLSyntaxException(
					"[AbstractTermMap:checkInverseExpression] Not a valid inverse "
							+ "expression : " + stringTemplate);

	}

	private void setColumnValue(String columnValue)
			throws InvalidR2RMLSyntaxException, InvalidR2RMLStructureException {
		// The value of the rr:column property MUST be a valid column name.
		if (columnValue != null)
			checkColumnValue(columnValue);
		this.columnValue = columnValue;
	}

	private void checkColumnValue(String columnValue)
			throws InvalidR2RMLSyntaxException {
		if (!SQLDataValidator.isValidSQLIdentifier(columnValue))
			throw new InvalidR2RMLSyntaxException(
					"[AbstractTermMap:checkColumnValue] Not a valid column "
							+ "value : " + termType);
	}

	protected void setTermType(URI termType) throws InvalidR2RMLSyntaxException,
			R2RMLDataError, InvalidR2RMLStructureException {
		if (termType == null) {
			// If the term map does not have a rr:termType property
			// then its term type is IRI (expect for object Map)
			this.termType = TermType.IRI;
		}
		TermType tt = null;
		if (termType != null)
			tt = checkTermType(termType);
		this.termType = tt;
	}

	private TermType checkTermType(URI termType)
			throws InvalidR2RMLSyntaxException, InvalidR2RMLStructureException,
			R2RMLDataError {
		// Its value MUST be an IRI
		if (!RDFDataValidator.isValidURI(termType.stringValue()))
			throw new R2RMLDataError(
					"[AbstractTermMap:checkTermType] Not a valid URI : "
							+ termType);
		// (IRIs, blank nodes or literals)
		TermType tt = TermType.toTermType(termType.stringValue());
		if (tt == null)
			throw new InvalidR2RMLSyntaxException(
					"[AbstractTermMap:checkTermType] Not a valid term type : "
							+ termType);
		// Check rules in function of term map nature (subject, predicate ...)
		checkSpecificTermType(tt);
		return tt;
	}

	protected abstract void checkSpecificTermType(TermType tt)
			throws InvalidR2RMLStructureException;

	private void setStringTemplate(String stringTemplate)
			throws InvalidR2RMLSyntaxException, InvalidR2RMLStructureException {
		// he value of the rr:template property MUST be a
		// valid string template.
		if (stringTemplate != null)
			checkStringTemplate(stringTemplate);
		
		this.stringTemplate = stringTemplate;
	}

	/**
	 * A string template is a format string that can be used to build strings
	 * from multiple components. It can reference column names by enclosing them
	 * in curly braces.
	 * 
	 * @throws R2RMLDataError
	 */
	private void checkStringTemplate(String stringTemplate)
			throws InvalidR2RMLSyntaxException {
		// Its value MUST be an IRI
		if (!R2RMLToolkit.checkStringTemplate(stringTemplate))
			throw new InvalidR2RMLSyntaxException(
					"[AbstractTermMap:checkStringTemplate] Not a valid string "
							+ "template : " + stringTemplate);
	}

	private void setLanguageTag(String languageTag) throws R2RMLDataError {
		// its value MUST be a valid language tag
		if (languageTag != null)
			checkLanguageTag(languageTag);
		this.languageTag = languageTag;
	}

	/**
	 * Check if language tag is valid, as defined by [RFC-3066]
	 * 
	 * @throws R2RMLDataError
	 */
	private void checkLanguageTag(String languageTag) throws R2RMLDataError {
		// Its value MUST be an IRI
		if (!RDFDataValidator.isValidLanguageTag(languageTag))
			throw new R2RMLDataError(
					"[AbstractTermMap:checkLanguageTag] Not a valid language tag : "
							+ languageTag);
	}

	/**
	 * Check if constant value is correctly defined. Constant value is an IRI or
	 * literal in function of this term map type.
	 */
	protected abstract void checkConstantValue(Value constantValue)
			throws R2RMLDataError;

	public void setConstantValue(Value constantValue) throws R2RMLDataError,
			InvalidR2RMLStructureException {
		// Check if constant value is valid
		if (constantValue != null)
			checkConstantValue(constantValue);
		this.constantValue = constantValue;
	}

	/**
	 * Check if datatype is correctly defined.
	 * 
	 * @throws R2RMLDataError
	 */
	public void checkDataType(URI dataType) throws R2RMLDataError {
		// Its value MUST be an IRI
		if (!RDFDataValidator.isValidURI(dataType.stringValue()))
			throw new R2RMLDataError(
					"[AbstractTermMap:checkDataType] Not a valid URI : "
							+ dataType);
	}

	public void setDataType(URI dataType) throws R2RMLDataError,
			InvalidR2RMLStructureException {
		if (!isTypeable() && dataType != null) 
			throw new InvalidR2RMLStructureException(
					"[AbstractTermMap:setDataType] A term map that is not "
							+ "a typeable term map MUST NOT have an rr:datatype"
							+ " property.");
		if (dataType != null) {
			// Check if datatype is valid
			checkDataType(dataType);
			this.dataType = XSDType.toXSDType(dataType.stringValue());
		}
	}

	public Value getConstantValue() {
		return constantValue;
	}

	public XSDType getDataType() {
		return dataType;
	}

	public XSDType getImplicitDataType() {
		return implicitDataType;
	}

	public RDFTransformation.Transformation getImplicitTransformation() {
		if (implicitDataType == null)
			return null;
		else
			return RDFTransformation
					.getCorrespondingTransformation(implicitDataType);
	}

	public String getInverseExpression() {
		return inverseExpression;
	}

	public String getLanguageTag() {
		return languageTag;
	}

	public Set<String> getReferencedColumns() {
		Set<String> referencedColumns = new HashSet<String>();
		switch (getTermMapType()) {
		case CONSTANT_VALUED:
			// The referenced columns of a constant-valued term map is the
			// empty set.
			break;

		case COLUMN_VALUED:
			// The referenced columns of a column-valued term map is
			// the singleton set containing the value of rr:column.
			referencedColumns.add(columnValue);

		case TEMPLATE_VALUED:
			// The referenced columns of a template-valued term map is
			// the set of column names enclosed in unescaped curly braces
			// in the template string.
			referencedColumns.addAll(R2RMLToolkit
					.extractColumnNamesFromStringTemplate(stringTemplate));

		default:
			break;
		}
		return referencedColumns;
	}

	public String getStringTemplate() {
		return stringTemplate;
	}

	public TermMapType getTermMapType() {
		if (constantValue != null)
			return TermMapType.CONSTANT_VALUED;
		else if (columnValue != null)
			return TermMapType.COLUMN_VALUED;
		else if (stringTemplate != null)
			return TermMapType.TEMPLATE_VALUED;
		return null;
	}

	public TermType getTermType() {
		return termType;
	}

	public String getColumnValue() {
		return columnValue;
	}

	public boolean isOveridden() {
		if (implicitDataType == null) {
			// No implicit datatype extracted for yet
			// Return false by default
			return false;
		}
		return dataType != implicitDataType;
	}

	public boolean isTypeable() {
		return (termType == TermType.LITERAL) && (languageTag == null);
	}

	public void setImplicitDataType(XSDType implicitDataType) {
		this.implicitDataType = implicitDataType;
	}

}
