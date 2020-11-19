import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Model {
	static Connection conn = DB.getInstance().getConnection();
	private HourlyStatsService hss;

	public Model() {
		hss = new HourlyStatsService();
	}

	public HourlyStatsService getHss() {
		return hss;
	}

	public void get_hourly_stats(ResultSet rs) throws SQLException {
		do {
			hss.createHourlyStats(rs.getInt("id"), rs.getInt("customer_id"), rs.getTimestamp("time"),
					rs.getInt("request_count"), rs.getInt("invalid_count"));
		} while (rs.next());
	}

	public Timestamp strip_time_portition(Timestamp timestamp, boolean day) {
		long ms = 1000 * 60 * 60; // Number of milliseconds in an hour
		if (day)
			ms = ms * 24; // Number of milliseconds in a day
		long msPortion = timestamp.getTime() % ms;
		return new Timestamp(timestamp.getTime() - msPortion);
	}

	public boolean customer_with_id(int customerID) {
		String query = "SELECT * FROM customer WHERE id=?";
		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setInt(1, customerID);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return true;
			} catch (SQLException ex) {
				Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (SQLException ex) {
			Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
		}
		return false;
	}

	public boolean customer_active(int customerID) {
		String query = "SELECT * FROM customer WHERE id=?";
		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setInt(1, customerID);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					int active = rs.getInt("active");
					if (active == 1)
						return true;
				}
			} catch (SQLException ex) {
				Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (SQLException ex) {
			Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
		}
		return false;
	}

	public HourlyStats hourly_stats_with_id(int id) {
		String query = "SELECT * FROM hourly_stats WHERE id=?";
		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setInt(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return new HourlyStats(rs);
				}
			} catch (SQLException ex) {
				Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (SQLException ex) {
			Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	public boolean remoteIP_in_blaclist(String remoteIP) {
		StringBuilder sb = new StringBuilder();
		String[] parts = remoteIP.split("\\.");
		for (int i = 0; i < 4; i++) {
			sb.append(parts[i]);
		}
		int ip = Integer.parseInt(sb.toString());

		String query = "SELECT * FROM ip_blacklist WHERE ip=?";
		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setInt(1, ip);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return true;
			} catch (SQLException ex) {
				Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (SQLException ex) {
			Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
		}
		return false;
	}

	public boolean ua_in_blaclist(String userID) {
		String query = "SELECT * FROM ua_blacklist WHERE ua=?";
		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setString(1, userID);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return true;
			} catch (SQLException ex) {
				Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (SQLException ex) {
			Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
		}
		return false;
	}

	public int compare_timestamps(Timestamp a, Timestamp b) {
		String query = "SELECT * FROM hourly_stats WHERE ? BETWEEN ? AND date_add(?, interval 1 hour)";
		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setTimestamp(1, b);
			ps.setTimestamp(2, a);
			ps.setTimestamp(3, a);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return rs.getInt("id");
			} catch (SQLException ex) {
				Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (SQLException ex) {
			Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
		}
		return 0;
	}

	public int customer_timestamp_pair(int customerID, Timestamp timestamp) {
		String query = "SELECT * FROM hourly_stats WHERE customer_id=?";
		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setInt(1, customerID);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					int compare = compare_timestamps(rs.getTimestamp("time"), timestamp);
					if (compare != 0)
						return rs.getInt("id");
				}
			} catch (SQLException ex) {
				Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (SQLException ex) {
			Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
		}
		return 0;
	}

	public void update_hourly_stats_table(int id, boolean valid) {
		String query = "";
		if (valid) {
			query = "UPDATE hourly_stats SET request_count=request_count+1 WHERE id=?";
		} else {
			query = "UPDATE hourly_stats SET invalid_count=invalid_count+1 WHERE id=?";
		}
		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setInt(1, id);
			ps.executeUpdate();
		} catch (SQLException ex) {
			Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public int insert_hourly_stats_table(CustomerRequest customerReg, boolean valid) {
		Timestamp timestamp = customerReg.timestamp;
		// reset the timestamp to full hour
		timestamp = strip_time_portition(timestamp, false);

		String query = "";
		if (valid) {
			query = "insert into hourly_stats (customer_id, time, request_count) values(?, ?, ?)";
		} else {
			query = "insert into hourly_stats (customer_id, time, invalid_count) values(?, ?, ?)";
		}
		try (PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
			ps.setInt(1, customerReg.customerID);
			ps.setTimestamp(2, customerReg.timestamp);
			ps.setInt(3, 1);
			ps.executeUpdate();
			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next())
				return (rs.getInt(1));
		} catch (SQLException ex) {
			Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
		}
		return 0;
	}

	public HourlyStats validRequest(CustomerRequest customerReq) throws SQLException {
		int id = customer_timestamp_pair(customerReq.customerID, customerReq.timestamp);
		if (id != 0) {
			update_hourly_stats_table(id, true);
			return hourly_stats_with_id(id);
		} else {
			int insert_id = insert_hourly_stats_table(customerReq, true);
			return hourly_stats_with_id(insert_id);
		}
	}

	public HourlyStats invalidRequest(CustomerRequest customerReq) {
		int id = 0;
		boolean exist = customer_with_id(customerReq.customerID);
		if (exist)
			id = customer_timestamp_pair(customerReq.customerID, customerReq.timestamp);
		if (id != 0) {
			update_hourly_stats_table(id, false);
			return hourly_stats_with_id(id);
		} else {
			int inesrt_id = insert_hourly_stats_table(customerReq, false);
			return hourly_stats_with_id(inesrt_id);
		}
	}

	public void getCustomer_Statistic(int id) {
		String query = "SELECT * FROM hourly_stats WHERE customer_id=?";
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setInt(1, id);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next())
					get_hourly_stats(rs);
			} catch (SQLException ex) {
				Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (SQLException ex) {
			Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void getDay_Statistic(Timestamp timestamp) {
		// reset timestamp to the begging of the day (00:00)
		timestamp = strip_time_portition(timestamp, true);
		String query = "SELECT * FROM hourly_stats WHERE time BETWEEN ? and date_add(?, interval 1 day)";
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setTimestamp(1, timestamp);
			stmt.setTimestamp(2, timestamp);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					get_hourly_stats(rs);
				}
			} catch (SQLException ex) {
				Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (SQLException ex) {
			Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void getCustomerDay_Statistic(int id, Timestamp timestamp) {
		// reset timestamp to the begging of the day (00:00)
		timestamp = strip_time_portition(timestamp, true);
		String query = "SELECT * FROM hourly_stats WHERE time BETWEEN ? and date_add(?, interval 1 day) AND customer_id=?";
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setTimestamp(1, timestamp);
			stmt.setTimestamp(2, timestamp);
			stmt.setInt(3, id);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					get_hourly_stats(rs);
				}
			} catch (SQLException ex) {
				Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (SQLException ex) {
			Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public int getDay_Count(Timestamp timestamp) {
		// reset timestamp to the begging of the day (00:00)
		timestamp = strip_time_portition(timestamp, true);
		String query = "SELECT COUNT(*) FROM hourly_stats WHERE time BETWEEN ? and date_add(?, interval 1 day)";
		try (PreparedStatement stmt = conn.prepareStatement(query)) {
			stmt.setTimestamp(1, timestamp);
			stmt.setTimestamp(2, timestamp);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(1);
				}
			} catch (SQLException ex) {
				Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (SQLException ex) {
			Logger.getLogger(Model.class.getName()).log(Level.SEVERE, null, ex);
		}
		return 0;
	}
}
