import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;

public class CustomerRequest {
	static Connection conn = DB.getInstance().getConnection();
	int customerID;
	int tagID;
	String userID;
	String remoteIP;
	Timestamp timestamp;

	public CustomerRequest(int customerId, int tagID, String userID, String remoteIP, Timestamp timestamp) {
		this.customerID = customerId;
		this.tagID = tagID;
		this.userID = userID;
		this.remoteIP = remoteIP;
		this.timestamp = timestamp;
	}

	public CustomerRequest(JSONObject obj) {
		this.customerID = obj.getInt("customerID");
		this.remoteIP = obj.getString("remoteIP");
		this.userID = obj.getString("userID");
		this.tagID = obj.getInt("tagID");
		this.timestamp = new Timestamp(obj.getLong("timestamp"));
	}
}
