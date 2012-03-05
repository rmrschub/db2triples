package net.antidot.semantic.rdf.rdb2rdf.r2rml.model;

import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.InvalidR2RMLStructureException;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.InvalidR2RMLSyntaxException;
import net.antidot.sql.model.tools.SQLDataValidator;

public class StdJoinCondition implements JoinCondition {

	private String child;
	private String parent;

	public StdJoinCondition(String child, String parent)
			throws InvalidR2RMLStructureException, InvalidR2RMLSyntaxException {
		setChild(child);
		setParent(parent);
	}

	private void setParent(String parent)
			throws InvalidR2RMLStructureException, InvalidR2RMLSyntaxException {
		if (parent == null)
			throw new InvalidR2RMLStructureException(
					"[StdJoinCondition:setParent] A join condition must "
							+ "have a parent column name.");
		// Must a valid SQL identifier
		if (!SQLDataValidator.isValidSQLIdentifier(parent))
			throw new InvalidR2RMLSyntaxException(
					"[StdJoinCondition:setParent] Not a valid column "
							+ "value : " + parent);
		this.parent = parent;
	}

	private void setChild(String child) throws InvalidR2RMLStructureException,
			InvalidR2RMLSyntaxException {
		if (child == null)
			throw new InvalidR2RMLStructureException(
					"[StdJoinCondition:construct] A join condition must "
							+ "have a child column name.");
		// Must a valid SQL identifier
		if (!SQLDataValidator.isValidSQLIdentifier(child))
			throw new InvalidR2RMLSyntaxException(
					"[StdJoinCondition:setParent] Not a valid column "
							+ "value : " + child);
		this.child = child;

	}

	public String getChild() {
		return child;
	}

	public String getParent() {
		return parent;
	}

}
