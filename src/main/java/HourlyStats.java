import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class HourlyStats {
	static Connection conn = DB.getInstance().getConnection();
	int id;
	int customer_id;
	Timestamp timestamp;
	int request_count;
	int invalid_count;

	public HourlyStats(int id, int customer_id, Timestamp timestamp, int request_count, int invalid_count) {
		this.id = id;
		this.customer_id = customer_id;
		this.timestamp = timestamp;
		this.request_count = request_count;
		this.invalid_count = invalid_count;
	}

	public HourlyStats(ResultSet rs) throws SQLException {
		this.id = rs.getInt(1);
		this.customer_id = rs.getInt(2);
		this.timestamp = rs.getTimestamp(3);
		this.request_count = rs.getInt(4);
		this.invalid_count = rs.getInt(5);
	}

	public int getId() {
		return id;
	}

}
