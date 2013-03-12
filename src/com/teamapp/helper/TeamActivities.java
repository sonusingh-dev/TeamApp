package com.teamapp.helper;

import java.util.ArrayList;
import java.util.HashMap;

import org.ksoap2.serialization.SoapObject;

import com.teamapp.ui.LoginAct;

public class TeamActivities {

	public static ArrayList<HashMap<String, String>> getTeamActivities(
			boolean tasks, boolean training, boolean events, boolean game)
			throws Exception {

		String METHOD_NAME = "GetActivitiesListByTeamIdOrderByMonth";
		ArrayList<HashMap<String, String>> teamActivitiesList = new ArrayList<HashMap<String, String>>();

		SoapObject request = WebServiceHelper.getSOAPRequest(METHOD_NAME);

		request.addProperty("bTask", tasks);
		request.addProperty("bTraining", training);
		request.addProperty("bEvent", events);
		request.addProperty("bMatch", game);

		request.addProperty("myUserId", LoginAct.userId);
		request.addProperty("teamId", LoginAct.teamId);
		request.addProperty("clubId", LoginAct.clubId);

		SoapObject response = (SoapObject) WebServiceHelper.getSOAPResponse(
				request, METHOD_NAME);

		if (response != null) {

			for (int i = 0; i < response.getPropertyCount(); i++) {

				SoapObject property = (SoapObject) response.getProperty(i);
				String month = property.getProperty("Month").toString();
				SoapObject activities = (SoapObject) property
						.getProperty("Activities");
				if (activities != null) {

					for (int j = 0; j < activities.getPropertyCount(); j++) {

						SoapObject soapActivity = (SoapObject) activities
								.getProperty(j);
						String id = soapActivity.getProperty("Id").toString();

						String location = null;
						if (soapActivity.getProperty("Location") != null) {
							location = soapActivity.getProperty("Location")
									.toString();
						}

						String result = null;
						if (soapActivity.getProperty("Result") != null) {
							result = soapActivity.getProperty("Result")
									.toString();
						}

						String attedanceId = soapActivity.getProperty(
								"MyAttedanceId").toString();
						String dateTime = soapActivity.getProperty(
								"ActivityDateTime").toString();
						String name = soapActivity.getProperty("Name")
								.toString();
						String attendees = soapActivity.getProperty(
								"PresentCount").toString();
						String type = soapActivity.getProperty("Type")
								.toString();
						String threshold = soapActivity
								.getProperty("Threshold").toString();
						HashMap<String, String> activity = new HashMap<String, String>();

						activity.put("id", id);
						activity.put("location", location);
						activity.put("name", name);
						activity.put("type", type);
						activity.put("month", month);
						activity.put("dateTime", dateTime);
						activity.put("attendees", attendees);
						activity.put("attendanceId", attedanceId);
						activity.put("threshold", threshold);
						activity.put("result", result);

						teamActivitiesList.add(activity);

					}
				}

			}

		}

		return teamActivitiesList;
	}

}
