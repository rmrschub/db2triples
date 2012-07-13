
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;

import junit.framework.TestCase;
import net.antidot.semantic.rdf.model.impl.sesame.SesameDataSet;
import net.antidot.semantic.rdf.rdb2rdf.dm.core.DirectMapper;
import net.antidot.semantic.rdf.rdb2rdf.dm.core.DirectMappingEngine;
import net.antidot.semantic.rdf.rdb2rdf.r2rml.core.R2RMLProcessor;
import net.antidot.sql.model.core.SQLConnector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.openrdf.rio.RDFFormat;

public class MainIT extends TestCase {

    // Log
    private static Log log = LogFactory.getLog(MainIT.class);

    // Database TEST settings
    private static String userName = Settings.userName;
    private static String password = Settings.password;
    private static String url = Settings.url;
    private static String driver = Settings.driver;

    private Connection conn = null;

    private static String[] r2rmlSuffix = { "", "a", "b", "c", "d", "e", "f",
	    "g", "h", "i", "j", "k" };

    private static boolean doDirectMaping = true;
    private static boolean doR2RML = false;

    // Base URI
    private static String baseURI = "http://example.com/base/";

    @Override
    protected void setUp() throws Exception {
	conn = SQLConnector.connect(userName, password, url, driver,
		Settings.testDbName);
    }

    @Test
    public void testExecute() {
	File[] files = listFiles("src/test/resources/rdb2rdf-ts_20120713");
	for (File f : files) {
	    runDirectory(f);
	}
    }

    private void runDirectory(File f) {
	if (!(f.isDirectory() && f.getName().startsWith("D002"))) {
	    return;
	}

	log.info("[W3CTester:run] Run test from : " + f.getName());
	try {
	    runDirTest(f.getAbsolutePath());
	}
	catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    private void runDirTest(String testAbsolutePath) throws SQLException {
	log.info("[W3CTester:runDirectMapping] Run tests from : "
		+ testAbsolutePath);

	// Clean TEST table
	log.info("[W3CTester:runDirectMapping] Clean test database...");
	SQLConnector.resetMySQLDatabase(conn, driver);
	// Load TEST database
	log.info("[W3CTester:runDirectMapping] Load new tables...");
	SQLConnector.updateDatabase(conn, testAbsolutePath + "/create.sql");
	// Run Direct Mapping
	runDirectMapping(testAbsolutePath);
	// Run R2RML
	runR2RML(testAbsolutePath);
    }

    private void runDirectMapping(String testAbsolutePath) {
	if (!doDirectMaping)
	    return;

	// Create Direct Mapping
	log.info("[W3CTester:runDirectMapping] Create mapping...");
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
	System.out.println(testAbsolutePath);
	result.dumpRDF(testAbsolutePath + "/directGraph-db2triples.ttl",
		RDFFormat.TURTLE);
    }

    private void runR2RML(String testAbsolutePath) {
	if (!doR2RML)
	    return;

	// Create Direct Mapping
	log.info("[W3CTester:runR2RML] Working on dir: " + testAbsolutePath);

	for (String suffix : r2rmlSuffix) {
	    // Create R2RML Mapping
	    final String test_filepath = testAbsolutePath + "/r2rml" + suffix
		    + ".ttl";
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
		System.out.println(testAbsolutePath);
		result.dumpRDF(testAbsolutePath + "/mapped" + suffix
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
