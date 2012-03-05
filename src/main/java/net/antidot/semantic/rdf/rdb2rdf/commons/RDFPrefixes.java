package net.antidot.semantic.rdf.rdb2rdf.commons;

import java.util.HashMap;

public abstract class RDFPrefixes {
	
	// Namespaces
	public static HashMap<String, String> prefix = new HashMap<String, String>();
	
	static {
		prefix.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		prefix.put("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
		prefix.put("xsd", "http://www.w3.org/2001/XMLSchema#");
	}

}
