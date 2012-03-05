/***************************************************************************
 *
 * R2RML Vocabulary 
 *
 * (C) 2011 Antidot (http://www.antidot.net)
 *
 * Module			:	R2RML Core
 * 
 * Fichier			:	R2RMLVocabulary.java
 *
 * Description		:	The R2RML vocabulary is the set of IRIs defined
 * 						in this specification that start with
 * 						the rr: namespace IRI: http://www.w3.org/ns/r2rml#
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
package net.antidot.semantic.rdf.rdb2rdf.r2rml.core;

import java.util.ArrayList;
import java.util.List;

public class R2RMLVocabulary {
	
	// In this document, examples assume the following namespace 
	// prefix bindings unless otherwise stated:
	public static String R2RML_NAMESPACE = "http://www.w3.org/ns/r2rml#";
	public static String RDF_NAMESPACE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	public static String RDFS_NAMESPACE = "http://www.w3.org/2000/01/rdf-schema#";
	public static String XSD_NAMESPACE = "http://www.w3.org/2001/XMLSchema#";
	public static String EX_NAMESPACE = "http://example.com/ns#"; // By convention
	
	public enum R2RMLTerm {
		// CLASSES
		GRAPH_MAP_CLASS("GraphMap"),
		JOIN_CLASS("Join"), 
		LOGICAL_TABLE_CLASS("LogicalTable"), 
		OBJECT_MAP_CLASS("ObjectMap"), 
		PREDICATE_MAP_CLASS("PredicateMap"), 
		PREDICATE_OBJECT_MAP_CLASS("PredicateObjectMap"), 
		REF_OBJECT_MAP_CLASS("RefObjectMap"), 
		SUBJECT_MAP_CLASS("SubjectMap"), 
		TRIPLES_MAP_CLASS("TriplesMap"), 
		
		// PROPERTIES
		CLASS("class"),
		CHILD("child"),
		COLUMN("column"),
		DATATYPE("datatype"),
		CONSTANT("constant"),
		GRAPH("graph"),
		GRAPH_MAP("graphMap"),
		INVERSE_EXPRESSION("inverseExpression"),
		JOIN_CONDITION("joinCondition"),
		LANGUAGE("language"),
		LOGICAL_TABLE("logicalTable"),
		OBJECT("object"),
		OBJECT_MAP("objectMap"),
		PARENT("parent"),
		PARENT_TRIPLES_MAP("parentTriplesMap"),
		PREDICATE("predicate"),
		PREDICATE_MAP("predicateMap"),
		PREDICATE_OBJECT_MAP("predicateObjectMap"),
		SQL_QUERY("sqlQuery"),
		SQL_VERSION("sqlVersion"),
		SUBJECT("subject"),
		SUBJECT_MAP("subjectMap"),
		TABLE_NAME("tableName"),
		TEMPLATE("template"),
		TERM_TYPE("termType"),
		
		// SPECIAL
		DEFAULT_GRAPH("defaultGraph"),
		IRI("IRI"),
		BLANK_NODE("BlankNode"),
		LITERAL("Literal");
	
		private String displayName;
	
		private R2RMLTerm(String displayName) {
			this.displayName = displayName;
		}
	
		public String toString() {
			return displayName;
		}
	}
	
	// Each property is included in a particular type of parent
	public static List<R2RMLTerm> triplesMapProperties = new ArrayList<R2RMLTerm>(); 
	static {
		triplesMapProperties.add(R2RMLTerm.SUBJECT);
		triplesMapProperties.add(R2RMLTerm.SUBJECT_MAP);
		triplesMapProperties.add(R2RMLTerm.LOGICAL_TABLE);
		triplesMapProperties.add(R2RMLTerm.PREDICATE_OBJECT_MAP);
	}
	public static List<R2RMLTerm> subjectProperties = new ArrayList<R2RMLTerm>(); 
	static {
		subjectProperties.add(R2RMLTerm.CLASS);
		subjectProperties.add(R2RMLTerm.GRAPH);
		subjectProperties.add(R2RMLTerm.GRAPH_MAP);
	}
	public static List<R2RMLTerm> predicateObjectMapProperties = new ArrayList<R2RMLTerm>(); 
	static {
		predicateObjectMapProperties.add(R2RMLTerm.OBJECT);
		predicateObjectMapProperties.add(R2RMLTerm.OBJECT_MAP);
		predicateObjectMapProperties.add(R2RMLTerm.PREDICATE);
		predicateObjectMapProperties.add(R2RMLTerm.PREDICATE_MAP);
		predicateObjectMapProperties.add(R2RMLTerm.GRAPH);
		predicateObjectMapProperties.add(R2RMLTerm.GRAPH_MAP);
	}
	public static List<R2RMLTerm> termMapProperties = new ArrayList<R2RMLTerm>(); 
	static {
		termMapProperties.add(R2RMLTerm.COLUMN);
		termMapProperties.add(R2RMLTerm.CONSTANT);
		termMapProperties.add(R2RMLTerm.INVERSE_EXPRESSION);
		termMapProperties.add(R2RMLTerm.LANGUAGE);
		termMapProperties.add(R2RMLTerm.TEMPLATE);
		termMapProperties.add(R2RMLTerm.TERM_TYPE);
	}
	public static List<R2RMLTerm> referencingObjectMapProperties = new ArrayList<R2RMLTerm>(); 
	static {
		referencingObjectMapProperties.add(R2RMLTerm.JOIN_CONDITION);
		referencingObjectMapProperties.add(R2RMLTerm.PARENT_TRIPLES_MAP);
	}
	public static List<R2RMLTerm> joinConditionProperties = new ArrayList<R2RMLTerm>(); 
	static {
		joinConditionProperties.add(R2RMLTerm.CHILD);
		joinConditionProperties.add(R2RMLTerm.PARENT);
	}
	public static List<R2RMLTerm> r2rmlViewProperties = new ArrayList<R2RMLTerm>(); 
	static {
		r2rmlViewProperties.add(R2RMLTerm.SQL_VERSION);
		r2rmlViewProperties.add(R2RMLTerm.SQL_QUERY);
	}
	public static List<R2RMLTerm> sqlBaseTableOrViewProperties = new ArrayList<R2RMLTerm>(); 
	static {
		sqlBaseTableOrViewProperties.add(R2RMLTerm.TABLE_NAME);
	}

}
