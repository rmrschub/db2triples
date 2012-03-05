/***************************************************************************
 *
 * R2RML Model : SubjectMap Interface
 *
 * (C) 2011 Antidot (http://www.antidot.net)
 *
 * Module			:	R2RMLModel
 * 
 * Fichier			:	SubjectMap.java
 *
 * Description		:	A subject map is a term map. It specifies a rule
 * 						for generating the subjects of the RDF triples generated 
 * 						by a triples map.
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

import java.util.Set;

import net.antidot.semantic.rdf.rdb2rdf.r2rml.exception.InvalidR2RMLStructureException;

import org.openrdf.model.URI;

public interface SubjectMap extends TermMap {

	/**
	 * A subject map may have one or more class IRIs.
	 */
	public Set<URI> getClassIRIs();
	
	/**
	 * Any subject map may have one or more associated graph maps.
	 */
	public Set<GraphMap> getGraphMaps();
	
	/**
	 * A Term Map knows in own Triples Map container.
	 * In 7.7 Inverse Expressions : "Let t be the logical table
	 * associated with this term map" suggests this feature.
	 */
	public TriplesMap getOwnTriplesMap();
	public void setOwnTriplesMap(TriplesMap ownTriplesMap) throws InvalidR2RMLStructureException;

}
