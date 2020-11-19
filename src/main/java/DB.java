import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DB {
	private static final String username = "root";
	private static final String password = "dina123";
	private static final String database = "customers";
	private static final String server = "localhost";

	static char c = '/';
	private static final String connectionUrl = "jdbc:mysql://" + server + c + database + "?user=" + username
			+ "&password=" + password;

	private Connection connection;

	public Connection getConnection() {
		return connection;
	}

	private DB() {
		try {
			connection = DriverManager.getConnection(connectionUrl);
		} catch (SQLException ex) {
			Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private static DB db = null;

	public static DB getInstance() {
		if (db == null)
			db = new DB();
		return db;
	}
}
