

import net.antidot.sql.model.core.SQLConnector;

public interface Settings {
	
	// Path to rdb2rdf workspace
	public static String pathToRDB2RDF = "src/main/java/net/antidot/semantic/rdf/rdb2rdf/";
		
	// MySQL Database TEST settings
	/*public static String userName = "root";
	public static String password = "root";
	public static String url = "jdbc:mysql://localhost/";
	public static String driver = SQLConnector.mysqlDriver;
	public static String testDbName = "TEST";
	public static String dbName = "mysql";*/

	// PostgreSQL Database TEST settings
	public static String userName = "root";
	public static String password = "root";
	public static String driver = SQLConnector.postgresqlDriver;
	public static String testDbName = "test";
	public static String url = "jdbc:postgresql://127.0.0.1:5432/";
	public static String dbName = "postgresql";
}
