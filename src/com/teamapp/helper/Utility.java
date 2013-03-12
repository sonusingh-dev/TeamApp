package com.teamapp.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.teamapp.ui.alerts.TeamAppAlerts;

public class Utility {

	public static boolean isNetworkAvailable(Context context) {

		boolean status = false;

		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnected()) {
			return true;
		}
		
		TeamAppAlerts.showConnetionDialog(context);
		return status;
	}

	/**
	 * method is used for checking valid email id format.
	 * 
	 * @param email
	 * @return boolean true for valid false for invalid
	 */
	public static boolean isEmailIdValid(String email) {
		boolean isValid = false;

		String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
		CharSequence inputStr = email;

		Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputStr);
		if (matcher.matches()) {
			isValid = true;
		}
		return isValid;
	}
	
	/**
	 * method is used to parse html tag
	 * 
	 * @param HTML
	 * @return String
	 */
	public static String stipHtml(String html) {
		String temp = Html.fromHtml(html).toString();
		if (temp != null) {
			temp = temp.trim();
		}
		return temp;
	}

	public static String pad(int c) {
		if (c >= 10)
			return String.valueOf(c);
		else
			return "0" + String.valueOf(c);
	}

	public static String anyTypeConversion(String data) {

		if (data.contains("anyType{}")) {
			data = null;
		}
		return data;
	}

	public static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			// pre-condition
			return;
		}

		int totalHeight = 0;
		int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(),
				MeasureSpec.AT_MOST);
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
		listView.requestLayout();
	}

	private static String getCalendarUriBase() {

		String contentProvider = null;

		if (Build.VERSION.SDK_INT >= 8) {
			contentProvider = "content://com.android.calendar/";
		} else {
			contentProvider = "content://calendar/";
		}

		return contentProvider;
	}

	public static boolean getEventFromCalendar(Context context,
			HashMap<String, String> item) {

		boolean status = false;

		String selection = null;
		String[] selectionArgs = null;

		String title = item.get("title");
		String details = item.get("details");
		String dateTime = item.get("dateTime");
		String location = item.get("location");

		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss");

		try {
			calendar.setTime(dateFormat.parse(dateTime));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Uri events = Uri.parse(getCalendarUriBase() + "events");
		String[] projection = new String[] { "_id", "title", "eventLocation",
				"description", "dtstart" };

		if (location != null) {
			selection = "dtstart=? AND title=? AND description=? AND eventLocation=?";
			selectionArgs = new String[] {
					String.valueOf(calendar.getTimeInMillis()), title, details,
					location };
		} else {
			selection = "dtstart=? AND title=? AND description=?";
			selectionArgs = new String[] {
					String.valueOf(calendar.getTimeInMillis()), title, details };
		}

		Cursor cursor = context.getContentResolver().query(events, projection,
				selection, selectionArgs, null);

		if (cursor != null && cursor.moveToFirst()) {

			for (int i = 0; i < cursor.getCount(); i++) {
				// retrieve the calendar names and ids
				Log.d("events", "Id :: " + cursor.getInt(0));
				Log.d("events", "title :: " + cursor.getString(1));
				Log.d("events", "eventLocation :: " + cursor.getString(2));
				Log.d("events", "description :: " + cursor.getString(3));
				Log.d("events", "dtstart :: " + cursor.getString(4));
				status = true;
				break;
				// cursor.moveToNext();
			}
			cursor.close();
		}

		return status;
	}

	public static boolean addEventToCalendar(Context context,
			HashMap<String, String> item) {

		boolean status = false;

		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss");

		String[] projection = new String[] { "_id", "displayName" };
		Uri calendars = Uri.parse(getCalendarUriBase() + "calendars");

		Cursor cursor = context.getContentResolver().query(calendars,
				projection, "selected=1", null, null);

		if (cursor != null && cursor.moveToFirst()) {

			String[] calNames = new String[cursor.getCount()];
			int[] calIds = new int[cursor.getCount()];
			for (int i = 0; i < calNames.length; i++) {
				// retrieve the calendar names and ids
				calIds[i] = cursor.getInt(0);
				calNames[i] = cursor.getString(1);
				cursor.moveToNext();
			}

			cursor.close();
			if (calIds.length > 0) {
				// further work
			}

			// note the URI
			int cal_id = calIds[0];

			String title = item.get("title");
			String location = item.get("location");
			String details = item.get("details");
			String dateTime = item.get("dateTime");

			try {
				calendar.setTime(dateFormat.parse(dateTime));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			ContentValues event = new ContentValues();
			event.put("calendar_id", cal_id);
			event.put("title", title);
			event.put("description", details);
			event.put("eventLocation", location);
			event.put("eventStatus", 1);
			event.put("visibility", 0);
			event.put("transparency", 0);
			event.put("hasAlarm", 1);

			event.put("dtstart", calendar.getTimeInMillis());
			event.put("dtend", calendar.getTimeInMillis() + 60 * 60 * 1000);

			Uri events = Uri.parse(getCalendarUriBase() + "events");
			Uri uri = context.getContentResolver().insert(events, event);

			// get the event ID that is the last element in the Uri
			long eventID = Long.parseLong(uri.getLastPathSegment());
			Log.d("addToCalendar", "eventID :: " + eventID);
			status = true;
		}

		return status;
	}

	private void addToCalendar(Context context, HashMap<String, String> item) {

		String title = item.get("title");
		String location = item.get("location");
		String details = item.get("details");

		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss");

		try {
			Intent intent = new Intent(Intent.ACTION_EDIT);
			intent.setType("vnd.android.cursor.item/event");
			intent.putExtra("beginTime", calendar.getTimeInMillis());
			intent.putExtra("allDay", false);
			intent.putExtra("rrule", "FREQ=DAILY");
			intent.putExtra("endTime",
					calendar.getTimeInMillis() + 60 * 60 * 1000);
			intent.putExtra("title", title);
			intent.putExtra("eventLocation", location);
			intent.putExtra("description", details);
			context.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}