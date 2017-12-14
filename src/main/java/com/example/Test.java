package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Servlet implementation class Test
 */
public class Test extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(Test.class.getName());

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		log.info("inside test get");
		String accessToken = "omWXb77W6HrQ78xOAJKJNTlTNEpH";
		JSONObject responseData = getHolidays(accessToken);
		response.getWriter().write(responseData.toJSONString());
	}

	@SuppressWarnings({ "unused", "deprecation", "unchecked" })
	public static JSONObject getHolidays(String accessToken) {
		// TODO Auto-generated method stub

		log.info("inside get holidays");
		JSONObject employeeDetails = getResponseFromAPI(accessToken);

		JSONArray leaveTypes = (JSONArray) employeeDetails.get("LeavesList");
		log.info(leaveTypes.toString());

		JSONObject leave = (JSONObject) leaveTypes.get(0);
		log.info(leave.toString());
		float privillage_leave = Float.parseFloat(leave.get(
				"AvailableLeaveCount").toString());

		leave = (JSONObject) leaveTypes.get(1);
		float ol = Float
				.parseFloat(leave.get("AvailableLeaveCount").toString());

		leave = (JSONObject) leaveTypes.get(2);
		float oh = Float
				.parseFloat(leave.get("AvailableLeaveCount").toString());

		leave = (JSONObject) leaveTypes.get(3);
		float cf = Float
				.parseFloat(leave.get("AvailableLeaveCount").toString());

		JSONObject responseObject = new JSONObject();

		String event_date = "22/05/1995";
		responseObject.put("birthday", event_date);

		JSONObject holidays = new JSONObject();
		event_date = "25/12/2017";
		holidays.put(event_date, "christmas");

		event_date = "31/12/2017";
		holidays.put(event_date, "new year eve");

		responseObject.put("holidays", holidays);
		responseObject.put("optional_leave", ol);
		responseObject.put("optional_holiday", oh);
		responseObject.put("compensatiory_off", cf);
		responseObject.put("privillage_leave", privillage_leave);

		log.info(responseObject.toString());
		return responseObject;

	}

	static JSONObject getResponseFromAPI(String accessToken) {
		JSONObject responseData = null;
		try {
			log.info("inside getting response of api for leave balance");
			String apiurl = "https://api.persistent.com:9020/hr/leaveattendanceself";
			URL url = new URL(apiurl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Length", "0");
			conn.setRequestProperty("Authorization", "Bearer " + accessToken);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-Type", "application/json");

			BufferedReader bufferedReaderObject = new BufferedReader(
					new InputStreamReader(conn.getInputStream(), "UTF-8"));
			StringBuilder output = new StringBuilder();

			String op;
			while ((op = bufferedReaderObject.readLine()) != null) {
				output.append(op);
			}

			JSONParser parser = new JSONParser();
			responseData = (JSONObject) parser.parse(output.toString());
			conn.disconnect();
			log.info(responseData.toString());
		} catch (Exception e) {
			log.info("error accessing leave balance :" + e);
		}

		return responseData;
	}

	@SuppressWarnings("unchecked")
	public static JSONObject applyLeave(String empName, int leaveTypeCid,
			String fromDate, String toDate, boolean isHalfDaySession,
			boolean isAfterNoon, int leaveYearCid, boolean isAdvancedLeave,
			String approverID, String Reason, String accessToken) {
		JSONObject responseData = null;
		try {

			JSONObject requestBody = new JSONObject();
			requestBody.put("EmployeeUserName", empName);
			requestBody.put("LeaveTypeCid", leaveTypeCid);
			requestBody.put("FromDate", fromDate);
			requestBody.put("ToDate", toDate);
			requestBody.put("IsHalfDaySession", isHalfDaySession);
			requestBody.put("IsAfterNoon", isAfterNoon);
			requestBody.put("LeaveYearCid", leaveYearCid);
			requestBody.put("IsAdvanceLeave", isAdvancedLeave);
			requestBody.put("ApproverId", approverID);
			requestBody.put("Reason", Reason);
			log.info("raw post data :"+requestBody);
			byte[] out = requestBody.toJSONString().getBytes(StandardCharsets.UTF_8);
			
			URL u = new URL("https://api.persistent.com:9020/hr/leave");
			HttpURLConnection conn = (HttpURLConnection) u.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty( "Content-Type", "application/json" );
			conn.setRequestProperty("Authorization", "Bearer " + accessToken);
			conn.setRequestProperty( "Content-Length", String.valueOf(out.length));
			OutputStream os = conn.getOutputStream();
			os.write(out);
			os.flush();
			
			BufferedReader bufferedReaderObject = new BufferedReader(new InputStreamReader((conn.getInputStream())));			
			StringBuilder output = new StringBuilder();			
			
			String op;
			while ((op = bufferedReaderObject.readLine()) != null) {
				output.append(op);
			}

			JSONParser parser = new JSONParser();
			responseData = (JSONObject) parser.parse(output.toString());
			log.info("resposne from apply leave API:"+responseData);
			conn.disconnect();
		} catch (Exception e) {
			log.info("exception in applying leave" + e);
		}
		return responseData;
	}
}
