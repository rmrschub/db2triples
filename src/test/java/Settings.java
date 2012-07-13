

import net.antidot.sql.model.core.SQLConnector;

public interface Settings {
			
	// MySQL Database TEST settings
	/*public static String userName = "root";
	public static String password = "root";
	public static String url = "jdbc:mysql://localhost/";
	public static String driver = SQLConnector.mysqlDriver;
	public static String testDbName = "TEST";
	public static String dbName = "mysql";*/

	// PostgreSQL Database TEST settings
	public static final String userName = "root";
	public static final String password = "root";
	public static final String driver = SQLConnector.postgresqlDriver;
	public static final String testDbName = "test";
	public static final String url = "jdbc:postgresql://127.0.0.1:5432/";
	public static final String dbName = "postgresql";
}
