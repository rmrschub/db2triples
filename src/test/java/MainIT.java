import java.io.File;
import java.io.FilenameFilter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Vector;

import junit.framework.TestCase;
import net.antidot.semantic.rdf.model.impl.sesame.SesameDataSet;
import net.antidot.semantic.rdf.rdb2rdf.dm.core.DirectMapper;
import net.antidot.semantic.rdf.rdb2rdf.dm.core.DirectMappingEngine;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.core.R2RMLProcessor;
import net.antidot.sql.model.core.SQLConnector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.openrdf.rio.RDFFormat;

@RunWith(Parameterized.class)
public class MainIT extends TestCase {

    public enum NormTested {
	DirectMapping, R2RML;
    }

    // Log
    private static Log log = LogFactory.getLog(MainIT.class);

    public static final File w3cDefinitionSearchPath = new File(
	    "src/test/resources/");
    public static final String w3cDirPrefix = "rdb2rdf-ts";

    // Base URI
    private static final String baseURI = "http://example.com/base/";

    // R2RML suffix. To be improved!
    private static final String[] r2rmlSuffix = { "", "a", "b", "c", "d", "e",
	    "f", "g", "h", "i", "j", "k" };

    // Database TEST settings
    private static String userName = Settings.userName;
    private static String password = Settings.password;
    private static String url = Settings.url;
    private static String driver = Settings.driver;

    // Instance field

    private NormTested tested = null;
    private String directory = null;

    public MainIT(NormTested tested, String directory) throws SQLException,
	    InstantiationException, IllegalAccessException,
	    ClassNotFoundException {
	this.tested = tested;
	this.directory = directory;
    }

    @Parameters
    public static Collection<Object[]> getTestsFiles() throws Exception {
	Collection<Object[]> parameters = new Vector<Object[]>();
	File[] w3cDirs = w3cDefinitionSearchPath
		.listFiles(new FilenameFilter() {
		    public boolean accept(File dir, String name) {
			return name.startsWith(w3cDirPrefix);
		    }
		});

	for (File w3cDir : w3cDirs) {
	    File[] files = w3cDir.listFiles();
	    for (File f : files) {
		if (f.isFile()) {
		    continue;
		}
		final String file_path = f.getAbsolutePath();
		parameters.add(new Object[] { NormTested.DirectMapping,
			file_path });
		parameters.add(new Object[] { NormTested.R2RML, file_path });
	    }
	}
	return parameters;
    }

    @Test
    public void testNorm() throws SQLException, InstantiationException,
	    IllegalAccessException, ClassNotFoundException {
	log.info("[W3CTester:test] Run tests from : " + directory);

	log.info("[W3CTester:setUp] Initialize DB connection");
	Connection conn = SQLConnector.connect(userName, password, url, driver,
		Settings.testDbName);

	// Clean TEST table
	log.info("[W3CTester:test] Clean test database...");
	SQLConnector.resetMySQLDatabase(conn, driver);
	// Load TEST database
	log.info("[W3CTester:test] Load new tables...");
	SQLConnector.updateDatabase(conn, directory + "/create.sql");

	switch (tested) {
	case DirectMapping:
	    // Run Direct Mapping
	    runDirectMapping(conn);
	    break;
	case R2RML:
	    // Run R2RML
	    runR2RML(conn);
	    break;
	default:
	    fail("Norm is not recognized!!!!");
	}

	conn.close();
    }

    private void runDirectMapping(Connection conn) {
	// Create Direct Mapping
	SesameDataSet result;
	try {
	    result = DirectMapper.generateDirectMapping(conn,
		    DirectMappingEngine.Version.WD_20120529, driver, baseURI,
		    null, null);
	}
	catch (UnsupportedEncodingException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	    return;
	}
	// Serialize result
	result.dumpRDF(directory + "/directGraph-db2triples.ttl",
		RDFFormat.TURTLE);
    }

    private void runR2RML(Connection conn) {
	// Create Direct Mapping

	for (String suffix : r2rmlSuffix) {
	    // Create R2RML Mapping
	    final String test_filepath = directory + "/r2rml" + suffix + ".ttl";
	    File r2rml_def_file = new File(test_filepath);
	    if (r2rml_def_file.exists()) {
		log.info("[W3CTester:runR2RML] Working on test: "
			+ test_filepath);
		SesameDataSet result;
		try {
		    result = R2RMLProcessor.convertDatabase(conn,
			    r2rml_def_file.getAbsolutePath(), baseURI, driver);
		}
		catch (Exception e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		    continue;
		}
		// Serialize result
		result.dumpRDF(directory + "/mapped" + suffix
			+ "-db2triples.nq", RDFFormat.TURTLE);
	    }
	}
    }

    public static File[] listFiles(String directoryPath) {
	File[] files = null;
	File directoryToScan = new File(directoryPath);
	files = directoryToScan.listFiles();
	return files;
    }

}
