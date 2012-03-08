/***************************************************************************
 *
 * R2RML Model : GraphtMap Interface
 *
 * (C) 2011 Antidot (http://www.antidot.net)
 *
 * Module			:	R2RMLModel
 * 
 * Fichier			:	GraphMap.java
 *
 * Description		:	Any subject map or predicate-object map may have one
 * 						or more associated graph maps. Graph maps are 
 * 						themselves term maps. When RDF triples are generated,
 * 						the set of target graphs is determined by taking into
 * 						account any graph maps associated with the subject map
 * 						or predicate-object map.
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

import org.openrdf.model.URI;

public interface GraphMap extends TermMap {

	/**
	 * A graph map is associated with a graph URI.
	 */
	//public URI getGraph();
}
