/***************************************************************************
 *
 * R2RML : R2RML Engine
 *
 * (C) 2011 Antidot (http://www.antidot.net)
 *
 * Module			:	R2RML
 * 
 * Fichier			:	R2RMLEngine.java
 *
 * Description		:	The R2RML engine is the link between a R2RML Mapping object
 * 						and a database connection. It constructs the final RDF graph
 * 						from the R2RML mapping document by extracting data in database.
 * 
 *
 * Auteurs(s)			:	JHO
 *
 *
 ****************************************************************************/
package net.antidot.semantic.rdf.rdb2rdf.r2rml.core;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.antidot.semantic.rdf.model.impl.sesame.SesameDataSet;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.model.R2RMLMapping;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

public class R2RMLEngine {

	// Log
	private static Log log = LogFactory.getLog(R2RMLEngine.class);

	// SQL Connection
	private Connection conn;

	// Current logical table
	private ResultSet logicalTable;

	// Value factory
	private static ValueFactory vf = new ValueFactoryImpl();

	public R2RMLEngine(Connection conn) {
		super();
		if (conn == null)
			throw new IllegalStateException(
					"[R2RMLEngine:R2RMLEngine] SQL connection does not exists.");
		this.conn = conn;
		logicalTable = null;
	}

	/**
	 * Execute R2RML Mapping from a R2RML file in order to generate a RDF
	 * dataset. This dataset is built with Sesame API.
	 * 
	 * @param r2rmlMapping
	 * @return
	 * @throws SQLException
	 */
	public SesameDataSet runR2RMLMapping(R2RMLMapping r2rmlMapping,
			String pathToNativeStore) throws SQLException {
		if (log.isDebugEnabled())
			log.debug("[R2RMLEngine:runR2RMLMapping] Run R2RML mapping... ");
		SesameDataSet sesameDataSet = null;
		// Check if use of native store is required
		if (pathToNativeStore != null) {
			if (log.isDebugEnabled())
				log.debug("[R2RMLEngine:runR2RMLMapping] Use native store "
						+ pathToNativeStore);
			sesameDataSet = new SesameDataSet(pathToNativeStore, false);
		} else {
			sesameDataSet = new SesameDataSet();
		}

		// Explore R2RML Mapping TriplesMap objects
		extractRDFFromTriplesMap(sesameDataSet, r2rmlMapping);

		// Close connection to logical table
		if (logicalTable != null) {

			logicalTable.getStatement().close();
			logicalTable.close();
		}
		if (log.isDebugEnabled())
			log.debug("[R2RMLEngine:runR2RMLMapping] R2RML mapping done. ");
		return sesameDataSet;
	}

	/**
	 * Extract all RDF triples which are contained in triplesMap of a R2RML
	 * Mapping object.
	 * 
	 * @param sesameDataSet
	 * @param r2rmlMapping
	 * @throws SQLException
	 */
	private void extractRDFFromTriplesMap(SesameDataSet sesameDataSet,
			R2RMLMapping r2rmlMapping) throws SQLException {
		
	}
}
