import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class HourlyStatsService {
	private Map<Integer, HourlyStats> list = new HashMap<>();

	public List<HourlyStats> getAllHourlyStats() {
		return new ArrayList<>(list.values());
	}

	public boolean empty() {
		return list.size() == 0;
	}

	public HourlyStats getUser(int id) {
		return list.get(id);
	}

	public HourlyStats createHourlyStats(int id, int customer_id, Timestamp timestamp, int request_count,
			int invalid_count) {
		HourlyStats hs = new HourlyStats(id, customer_id, timestamp, request_count, invalid_count);
		list.put(hs.getId(), hs);
		return hs;
	}

}
