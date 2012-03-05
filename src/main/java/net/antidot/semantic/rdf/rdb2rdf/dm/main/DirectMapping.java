/* 
 * Copyright 2011 Antidot opensource@antidot.net
 * https://github.com/antidot/db2triples
 * 
 * DB2Triples is free software; you can redistribute it and/or 
 * modify it under the terms of the GNU General Public License as 
 * published by the Free Software Foundation; either version 2 of 
 * the License, or (at your option) any later version.
 * 
 * DB2Triples is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * 
 * Direct Mapping Main
 *
 * Interface between user and console.
 * 
 *
 */
package net.antidot.semantic.rdf.rdb2rdf.dm.main;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

import net.antidot.semantic.rdf.model.impl.sesame.SesameDataSet;
import net.antidot.semantic.rdf.rdb2rdf.dm.core.DirectMapper;
import net.antidot.semantic.rdf.rdb2rdf.dm.core.DirectMappingEngine.Version;
import net.antidot.sql.model.core.SQLConnector;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openrdf.rio.RDFFormat;


@SuppressWarnings("static-access")
public class DirectMapping {

	// Log
	private static Log log = LogFactory.getLog(DirectMapping.class);
	
	private static String MYSQL_DRIVER = "com.mysql.jdbc.Driver";
	private static String POSTGRE_DRIVER = "org.postgresql.Driver";

	private static Option userNameOpt = OptionBuilder.withArgName("user_name")
			.hasArg().withDescription("Database user name").withLongOpt(
					"user").create("u");

	private static Option passwordOpt = OptionBuilder.withArgName("password")
			.hasArg().withDescription("Database password").withLongOpt(
					"pass").create("p");

	private static Option URLOpt = OptionBuilder.withArgName("url").hasArg()
			.withDescription(
					"Database URL (default : jdbc:mysql://localhost/)")
			.withLongOpt("url").create("l");

	private static Option driverOpt = OptionBuilder.withArgName("driver")
			.hasArg().withDescription(
					"Driver to use (default : " + MYSQL_DRIVER + " )")
			.withLongOpt("driver").create("d");
	
	private static Option versionOpt = OptionBuilder.withArgName("version")
	.hasArg().withDescription(
			"Version of norm to use (1 = Working Draft 20 September 2011 (default), 2 = Working Draft 23 March 2011)")
	.withLongOpt("version").create("v");

	private static Option dbOpt = OptionBuilder.withArgName("database_name")
			.hasArg().withDescription("database name").withLongOpt(
					"database").create("b");

	private static Option baseURIOpt = OptionBuilder.withArgName("base_uri")
			.hasArg().withDescription(
					"Base URI (default : http://foo.example/DB/)").withLongOpt(
					"base_uri").create("i");

	private static Option nativeOpt = new Option("n",
			"Use native store (store in output directory path)");

	private static Option nativeStoreNameOpt = OptionBuilder.withArgName(
			"nativeOutput").hasArg().withDescription(
			"Native store output directory").withLongOpt("native_output")
			.create("n");

	private static Option forceOpt = new Option("f",
			"Force loading of existing repository (without remove data)");

	private static Option outputOpt = OptionBuilder.withArgName("output")
			.hasArg().withDescription("Output RDF filename (default : output)").withLongOpt(
					"output").create("o");

	private static Option transformSPARQLFile = OptionBuilder.withArgName(
			"sparql").hasArg().withDescription(
			"Sparql transform request file (optionnal)").withLongOpt("sparql")
			.create("s");

	private static Option transformOutputFile = OptionBuilder
			.withArgName("sparql_output")
			.hasArg()
			.withDescription(
					"Transformed graph output file (optionnal if sparql option is not specified, default : sparql_output otherwise)")
			.withLongOpt("sparql_output").create("q");

	private static Option rdfFormat = OptionBuilder
			.withArgName("format")
			.hasArg()
			.withDescription(
					"RDF syntax output format ('RDFXML', 'N3', 'NTRIPLES' or 'TURTLE')")
			.withLongOpt("format").create("m");
	
	private static String projectName = "Direct Mapping - db2triples v0.9 - See https://github.com/antidot/db2triples for more informations.";

	public static void main(String[] args) {
		// Get all options
		Options options = new Options();
		options.addOption(userNameOpt);
		options.addOption(passwordOpt);
		options.addOption(URLOpt);
		options.addOption(driverOpt);
		options.addOption(dbOpt);
		options.addOption(baseURIOpt);
		options.addOption(forceOpt);
		options.addOption(nativeOpt);
		options.addOption(nativeStoreNameOpt);
		options.addOption(outputOpt);
		options.addOption(transformSPARQLFile);
		options.addOption(transformOutputFile);
		options.addOption(rdfFormat);
		options.addOption(versionOpt);
		
		// Init parameters
		String userName = null;
		String password = null;
		String url = null;
		String driver = null;
		String dbName = null;
		String baseURI = null;
		boolean useNativeStore = false;
		boolean forceExistingRep = false;
		String nativeOutput = null;
		String output = null;
		String sparql = null;
		String sparqlOutput = null;
		String format = null;
		int int_version = 1;

		// RDF Format output
		RDFFormat rdfFormat = RDFFormat.N3; // N3 by default
		// Norm version
		Version version = Version.WD_20110920;

		// Option parsing
		// Create the parser
		CommandLineParser parser = new GnuParser();
		try {
			// parse the command line arguments
			CommandLine line = parser.parse(options, args);
			// Database settings
			// user name
			if (!line.hasOption("user")) {
				// automatically generate the help statement
				if (log.isErrorEnabled())
					log
							.error("User name is required. Use -u option to set it.");
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp(projectName, options);
				System.exit(-1);
			} else {
				userName = line.getOptionValue("user");
			}
			// password
			if (!line.hasOption("pass")) {
				// automatically generate the help statement
				if (log.isErrorEnabled())
					log
							.error("Password is required. Use -p option to set it.");
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp(projectName, options);
				System.exit(-1);
			} else {
				password = line.getOptionValue("pass");
			}
			// Database URL
			url = line.getOptionValue("url", "jdbc:mysql://localhost/");
			// driver
			driver = line.getOptionValue("driver", MYSQL_DRIVER);
			// Database name
			if (!line.hasOption("database")) {
				// automatically generate the help statement
				if (log.isErrorEnabled())
					log
							.error("Database name is required. Use -b option to set it.");
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp(projectName, options);
				System.exit(-1);
			} else {
				dbName = line.getOptionValue("database");
			}
			// Base URI
			baseURI = line.getOptionValue("base_uri", "http://foo.example/DB/");
			// Use of native store ?
			useNativeStore = line.hasOption("n");
			// Name of native store
			if (useNativeStore && !line.hasOption("native_output")) {
				// automatically generate the help statement
				if (log.isErrorEnabled())
					log
							.error("Native triplestore path is required. Use -n option to set it.");
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp(projectName, options);
				System.exit(-1);
			} else {
				nativeOutput = line.getOptionValue("native_output");
			}
			// Force loading of repository
			forceExistingRep = line.hasOption("f");
			// Output
			output = line.getOptionValue("output", "output.n3");
			// SPARQL transformation
			if (line.hasOption("sparql")) {
				sparql = line.getOptionValue("sparql");
				sparqlOutput = line.getOptionValue("sparql_output",
						"output_sparql.n3");
			}
			// RDF Format
			if (line.hasOption("format")) {
				format = line.getOptionValue("format");
				if (format.equals("TURTLE"))
					rdfFormat = RDFFormat.TURTLE;
				else if (format.equals("RDFXML"))
					rdfFormat = RDFFormat.RDFXML;
				else if (format.equals("NTRIPLES"))
					rdfFormat = RDFFormat.NTRIPLES;
				else if (!format.equals("N3")) {
					if (log.isErrorEnabled())
						log
								.error("Unknown RDF format. Please use RDFXML, TURTLE, N3 or NTRIPLES.");
					System.exit(-1);
				}
			}
			// Norm version
			if (line.hasOption("version")) {
				switch (int_version) {
				case 1:
					version = Version.WD_20110920;
					break;
				case 2:
					version = Version.WD_20110324;
					// Check DB compatibilities
					if (!(driver.equals(MYSQL_DRIVER) || driver.equals(POSTGRE_DRIVER))){
						log.error("[DirectMapping:main] Db2triples does'nt support this driver for the Working Draft" +
								" of 23 March 2011 (only MySQL and PostGreSQL for this time). " +
								"You can set the version option to select Working Draft of 20 September 2011.");
						System.exit(-1);
					}
					break;
				default:
					break;
				}
			}

		} catch (ParseException exp) {
			// oops, something went wrong
			log.error("[DirectMapping:main] Parsing failed. Reason : "
					+ exp.getMessage());
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(projectName, options);
			System.exit(-1);
		}

		// Open test database
		Connection conn = null;
		try {
			// Connect database
			conn = SQLConnector
					.connect(userName, password, url, driver, dbName);
			
			// Generate RDF graph
			SesameDataSet g = null;
			// Check nature of storage (memory by default)
			if (useNativeStore) {
				File pathToNativeOutputDir = new File(nativeOutput);
				if (pathToNativeOutputDir.exists() && !forceExistingRep) {
					if (log.isErrorEnabled())
						log
								.error("Directory "
										+ pathToNativeOutputDir
										+ "  already exists. Use -f option to force loading of existing repository.");
					System.exit(-1);
				}
				// Extract database model
				/**Database db = SQLExtractor.extractMySQLDatabase(conn, null, driver);
				g = DirectMapper.generateDirectMapping(db, baseURI,
						nativeOutput);**/
				g = DirectMapper.generateDirectMapping(conn, version, driver, baseURI, null, nativeOutput);
			} else {
				File outputFile = new File(output);
				if (outputFile.exists() && !forceExistingRep) {
					if (log.isErrorEnabled())
						log
								.error("Output file "
										+ outputFile.getAbsolutePath()
										+ " already exists. Please remove it or modify ouput name option.");
					System.exit(-1);
				}
				// Extract database model
				/**Database db = SQLExtractor.extractMySQLDatabase(conn, null, driver);
				g = DirectMapper.generateDirectMapping(db, baseURI);**/
				g = DirectMapper.generateDirectMapping(conn, version, driver, baseURI, null, null);
				// Dump graph
				g.dumpRDF(output, rdfFormat);
			}
			if (sparql != null) {

				Long start = System.currentTimeMillis();
				String result = g.runSPARQLFromFile(sparql, rdfFormat);
				SesameDataSet gResult = new SesameDataSet();
				gResult.addString(result, rdfFormat);
				gResult.dumpRDF(sparqlOutput, rdfFormat);

				Float stop = Float.valueOf(System.currentTimeMillis() - start) / 1000;
				if (log.isInfoEnabled())
					log.info("[DirectMapping:main] SPARQL query executed in "
							+ stop + " seconds.");
				if (log.isInfoEnabled())
					log
							.info("[DirectMapping:main] Number of triples after transformation : "
									+ gResult.getSize());
				System.out.println("==========" + log.isInfoEnabled());

				/*
				 * Create Jena Graph String tmpFileName = "tmp_sesame_" +
				 * g.hashCode() + ".n3"; g.dumpRDF(tmpFileName, RDFFormat.N3);
				 * JenaGraph jDump = new JenaGraph(); // Load dumped RDF data
				 * jDump.loadDataFromFile(tmpFileName, "N3"); // Transform graph
				 * with SPARQL queries JenaGraph transformGraph = jDump
				 * .runSPARQLFromFile(sparql, "N3");
				 * transformGraph.dumpRDF(sparqlOutput, "N3");
				 */
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// Close db connection
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
