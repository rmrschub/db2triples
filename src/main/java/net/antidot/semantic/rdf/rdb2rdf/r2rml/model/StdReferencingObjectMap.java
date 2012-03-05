package net.antidot.semantic.rdf.rdb2rdf.r2rml.model;

import java.util.HashSet;
import java.util.Set;

public class StdReferencingObjectMap implements ReferencingObjectMap {

	private TriplesMap parentTriplesMap;
	private HashSet<JoinCondition> joinConditions;
	private PredicateObjectMap predicateObjectMap;

	public StdReferencingObjectMap(PredicateObjectMap predicateObjectMap,
			TriplesMap parentTriplesMap, Set<JoinCondition> joinConditions) {
		setPredicateObjectMap(predicateObjectMap);
		this.parentTriplesMap = parentTriplesMap;
		setJoinConditions(joinConditions);

	}

	private void setJoinConditions(Set<JoinCondition> joinConditions) {
		this.joinConditions = new HashSet<JoinCondition>();
		this.joinConditions.addAll(joinConditions);
	}

	public String getChildQuery() {
		return predicateObjectMap.getOwnTriplesMap().getLogicalTable()
				.getEffectiveSQLQuery();
	}

	public Set<JoinCondition> getJoinConditions() {
		return joinConditions;
	}

	public String getJointSQLQuery() {
		String jointSQLQuery = "SELECT * FROM (" + getChildQuery()
				+ ") AS child, (" + getParentQuery() + ")";
		// If the referencing object map has no join condition
		if (joinConditions.isEmpty())
			jointSQLQuery = "SELECT * FROM (" + getChildQuery() + ") AS tmp";
		// If the referencing object map has at least one join condition
		else {
			String whereClause = " WHERE ";
			int i = 0;
			for (JoinCondition j : joinConditions) {
				whereClause += "child." + j.getChild() + "=parent."
						+ j.getParent();
				i++;
				if (i < joinConditions.size())
					whereClause += " AND ";
			}
			jointSQLQuery += whereClause;
		}
		return jointSQLQuery;
	}

	public String getParentQuery() {
		return parentTriplesMap.getLogicalTable().getEffectiveSQLQuery();
	}

	public TriplesMap getParentTriplesMap() {
		return parentTriplesMap;
	}

	public PredicateObjectMap getPredicateObjectMap() {
		return predicateObjectMap;
	}

	public void setPredicateObjectMap(PredicateObjectMap predicateObjectMap) {
		if (predicateObjectMap.getObjectMap() != null) {
			if (predicateObjectMap.getObjectMap() != this)
				throw new IllegalStateException(
						"[StdObjectMap:setPredicateObjectMap] "
								+ "The predicateObject map parent "
								+ "already contains another Object Map !");
		} else {
			// Update predicateObjectMap if not contains this object map
			predicateObjectMap.setReferencingObjectMap(this);
		}
		this.predicateObjectMap = predicateObjectMap;
	}

}
