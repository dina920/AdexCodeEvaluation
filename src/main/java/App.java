import java.sql.Timestamp;
import static spark.Spark.*;

import org.apache.log4j.BasicConfigurator;
import org.json.JSONObject;

public class App {

	static boolean all_FieldCheck(JSONObject obj) {
		if (obj.isNull("customerID") || obj.isNull("tagID") || obj.isNull("userID") || obj.isNull("remoteIP")
				|| obj.isNull("timestamp")) {
			return false;
		}
		return true;
	}

	static boolean customerID_timestamp_FieldCheck(JSONObject obj) {
		if (obj.isNull("customerID") || obj.isNull("timestamp")) {
			return false;
		}
		return true;
	}

	public static void main(String[] args) {
		BasicConfigurator.configure();

		post("/customerRequest", "application/json", (request, response) -> {
			Model model = new Model();
			try {
				JSONObject obj = new JSONObject(request.body());
				// condition for valid requests
				if (all_FieldCheck(obj)) {
					CustomerRequest customerReq = new CustomerRequest(obj);
					// activeCustomer checks if customerID is an existing and active customer
					if (model.customer_active(customerReq.customerID)
							&& !model.remoteIP_in_blaclist(customerReq.remoteIP)
							&& !model.ua_in_blaclist(customerReq.userID)) {
						return model.validRequest(customerReq);
						// STUB FUNCTION for all valid request
					}
				}
				// other requests-invalid,update invalid_req if customerID and timestamp exist
				if (customerID_timestamp_FieldCheck(obj)) {
					try {
						CustomerRequest customerReq = new CustomerRequest(obj.getInt("customerID"), 0, "", "",
								new Timestamp(obj.getLong("timestamp")));
						// customer with customerID exists, doesn't have to be active
						if (model.customer_with_id(customerReq.customerID))
							return model.invalidRequest(customerReq);
					} catch (Exception e) {
						response.body("Invalid JSON format.");
						response.status(400);
						return response.body();
					}
				}
				response.body("Request rejected.");
				response.status(400);
				return response.body();
			} catch (Exception e) {
				response.body("Invalid JSON format.");
				response.status(400);
				return response.body();
			}
		}, JsonUtil.json());

		// statistics for a specific customer
		get("/customer_statistics", "application/json", (request, response) -> {
			Model model = new Model();
			try {
				JSONObject obj = new JSONObject(request.body());
				if (!obj.isNull("customerID")) {
					try {
						int id = obj.getInt("customerID");
						model.getCustomer_Statistic(id);
						response.status(201); // 201 Created
						return model.getHss().getAllHourlyStats();
					} catch (Exception e) {
						response.body("Invalid JSON format.");
						response.status(400);
						return response.body();
					}
				} else {
					response.body("Missing customerID.");
					response.status(400);
					return response.body();
				}
			} catch (Exception e) {
				response.body("Invalid JSON format.");
				response.status(400);
				return response.body();
			}
		}, JsonUtil.json());

		// statistics for a specific day
		get("/day_statistics", "application/json", (request, response) -> {
			Model model = new Model();
			try {
				JSONObject obj = new JSONObject(request.body());
				if (!obj.isNull("timestamp")) {
					try {
						Timestamp timestamp = new Timestamp(obj.getLong("timestamp"));
						model.getDay_Statistic(timestamp);
						response.status(201); // 201 Created
						if (!model.getHss().empty()) {
							return model.getHss().getAllHourlyStats();
						}
						response.body("List is emtpy");
					} catch (Exception e) {
						response.body("Invalid JSON format.");
						response.status(400);
						return response.body();
					}
				} else {
					response.body("Missing timestamp.");
					response.status(400);
				}
				return response.body();
			} catch (Exception e) {
				response.body("Invalid JSON format.");
				response.status(400);
				return response.body();
			}
		}, JsonUtil.json());

		// statistics for a specific customer for specific day
		get("/customer_day_statistics", "application/json", (request, response) -> {
			Model model = new Model();
			try {
				JSONObject obj = new JSONObject(request.body());
				if (customerID_timestamp_FieldCheck(obj)) {

					try {
						Timestamp timestamp = new Timestamp(obj.getLong("timestamp"));

						int id = obj.getInt("customerID");
						model.getCustomerDay_Statistic(id, timestamp);
						int regNumber = model.getDay_Count(timestamp);
						response.status(201); // 201 Created
						JSONObject jo = new JSONObject();
						jo.put("NumberPerDay", regNumber);
						jo.put("List", model.getHss().getAllHourlyStats());
						return jo;
					} catch (Exception e) {
						response.body("Invalid JSON format.");
						response.status(400);
						return response.body();
					}
				} else {
					response.body("Missing customerID or timestamp.");
					response.status(400);
					return response.body();
				}
			} catch (Exception e) {
				response.body("Invalid JSON format.");
				response.status(400);
				return response.body();
			}
		}, JsonUtil.json());

		// number of requests for a specific day
		get("/requests_per_day", "application/json", (request, response) -> {
			Model model = new Model();
			try {
				JSONObject obj = new JSONObject(request.body());
				if (!obj.isNull("timestamp")) {
					try {
						Timestamp timestamp = new Timestamp(obj.getLong("timestamp"));
						int regNumber = model.getDay_Count(timestamp);
						response.status(201); // 201 Created
						return regNumber;
					} catch (Exception e) {
						response.body("Invalid JSON format.");
						response.status(400);
						return response.body();
					}
				} else {
					response.body("Missing timestamp.");
					response.status(400);
					return response.body();
				}
			} catch (Exception e) {
				response.body("Invalid JSON format.");
				response.status(400);
				return response.body();
			}
		}, JsonUtil.json());
	}
}
