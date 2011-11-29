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
 * R2RML Main
 *
 * Interface between user and console.
 * 
 * @author jhomo
 *
 */
package antidot.r2rml.main;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

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

import antidot.r2rml.core.R2RMLMapper;
import antidot.rdf.impl.sesame.SesameDataSet;
import antidot.sql.core.SQLConnector;

@SuppressWarnings("static-access")
public class R2RML {

	// Log
	private static Log log = LogFactory.getLog(R2RML.class);

	private static Option userNameOpt = OptionBuilder.withArgName("user_name")
			.hasArg().withDescription("Database user name").withLongOpt("user")
			.create("u");

	private static Option passwordOpt = OptionBuilder.withArgName("password")
			.hasArg().withDescription("Database password").withLongOpt("pass")
			.create("p");

	private static Option URLOpt = OptionBuilder.withArgName("url").hasArg()
			.withDescription(
					"Database URL (default : jdbc:mysql://localhost/)")
			.withLongOpt("url").create("l");

	private static Option driverOpt = OptionBuilder.withArgName("driver")
			.hasArg().withDescription(
					"Driver to use (default : com.mysql.jdbc.Driver)")
			.withLongOpt("driver").create("d");

	private static Option dbOpt = OptionBuilder.withArgName("database_name")
			.hasArg().withDescription("Database name").withLongOpt("database")
			.create("b");

	private static Option forceOpt = new Option("f",
			"Force loading of existing repository (without remove data)");

	private static Option removeOpt = new Option("r",
			"Force removing of old output file");

	private static Option nativeOpt = new Option("n",
			"Use native store (store in output directory path)");

	private static Option nativeStoreNameOpt = OptionBuilder.withArgName(
			"native_output").hasArg().withDescription(
			"Native store output directory").withLongOpt("native_output")
			.create("n");

	private static Option outputOpt = OptionBuilder.withArgName("output")
			.hasArg().withDescription("Output RDF filename (default : output)")
			.withLongOpt("output").create("o");

	private static Option r2rmlFileOpt = OptionBuilder.withArgName("r2rml")
			.hasArg().withDescription("R2RML configuration file").withLongOpt(
					"r2rml").create("c");

	private static String projectName = "R2RML - db2triples v0.9 - See https://github.com/antidot/db2triples for more informations.";

	public static void main(String[] args) {

		// Get all options
		Options options = new Options();
		options.addOption(userNameOpt);
		options.addOption(passwordOpt);
		options.addOption(URLOpt);
		options.addOption(driverOpt);
		options.addOption(dbOpt);
		options.addOption(forceOpt);
		options.addOption(nativeOpt);
		options.addOption(nativeStoreNameOpt);
		options.addOption(outputOpt);
		options.addOption(r2rmlFileOpt);
		options.addOption(removeOpt);

		// Init parameters
		String userName = null;
		String password = null;
		String url = null;
		String driver = null;
		String dbName = null;
		String r2rmlFile = null;
		boolean useNativeStore = false;
		boolean forceExistingRep = false;
		boolean forceRemovingOld = false;
		String nativeOutput = null;
		String output = null;

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
			driver = line.getOptionValue("driver", "com.mysql.jdbc.Driver");
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
			// r2rml instance
			if (!line.hasOption("r2rml")) {
				// automatically generate the help statement
				if (log.isErrorEnabled())
					log
							.error("R2RML configuration file is required. Use -r option to set it.");
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp(projectName, options);
				System.exit(-1);
			} else {
				try {
					r2rmlFile = line.getOptionValue("r2rml");
					File r2rmlFileTest = new File(r2rmlFile);
					if (!r2rmlFileTest.exists()) {
						log.error("[R2RML:main] R2RML file does not exists.");
						System.exit(-1);
					}
				} catch (Exception e) {
					log.error(String.format(
							"[R2RML:main] Error reading R2RML file '%0$s'.",
							r2rmlFile));
					System.exit(-1);
				}
			}
			// Use of native store ?
			useNativeStore = line.hasOption("n");
			// Name of native store
			if (useNativeStore && !line.hasOption("native_output")) {
				// automatically generate the help statement
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp(projectName, options);
				System.exit(-1);
			} else {
				nativeOutput = line.getOptionValue("native_output");
			}
			// Force loading of repository
			forceExistingRep = line.hasOption("f");
			// Force removing of old output file
			forceRemovingOld = line.hasOption("r");
			// Output
			output = line.getOptionValue("output", "output.n3");

		} catch (ParseException exp) {
			// oops, something went wrong
			log.error("[DirectMapping:main] Parsing failed. Reason : "
					+ exp.getMessage());
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(projectName, options);
			System.exit(-1);
		}

		// DB connection
		Connection conn = null;
		try {
			conn = SQLConnector
					.connect(userName, password, url, driver, dbName);

			// Check nature of storage (memory by default)
			if (useNativeStore) {
				File pathToNativeOutputDir = new File(nativeOutput);
				if (pathToNativeOutputDir.exists() && !forceExistingRep) {
					if (log.isErrorEnabled())
						log.error("Directory " + pathToNativeOutputDir
								+ "  already exists. Use -f"
								+ " option to force loading of "
								+ "existing repository.");
				}
				R2RMLMapper.convertMySQLDatabase(conn, r2rmlFile, nativeOutput);
			} else {
				File outputFile = new File(output);
				if (outputFile.exists() && !forceRemovingOld) {
					if (log.isErrorEnabled())
						log.error("Output file " + outputFile.getAbsolutePath()
								+ " already exists. Please remove it or "
								+ "modify ouput name option.");
					System.exit(-1);
				} else {
					if (log.isInfoEnabled())
						log.info("Output file " + outputFile.getAbsolutePath()
								+ " already exists. It will be removed"
								+ " during operation (option -r)..");
				}
				SesameDataSet sesameDataSet = R2RMLMapper.convertMySQLDatabase(
						conn, r2rmlFile, nativeOutput);
				// Dump graph
				sesameDataSet.dumpRDF(output, RDFFormat.N3);
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
