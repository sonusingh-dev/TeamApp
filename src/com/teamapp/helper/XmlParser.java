package com.teamapp.helper;

import java.io.IOException;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class XmlParser {

	private static HashMap<String, String> item = null;
	private static ArrayList<HashMap<String, String>> itemList = null;

	public static ArrayList<HashMap<String, String>> getCommentsList(
			String response) throws XmlPullParserException, IOException {

		item = null;
		itemList = null;

		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser parser = factory.newPullParser();

		parser.setInput(new StringReader(response));
		int eventType = parser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			String name = null;
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				itemList = new ArrayList<HashMap<String, String>>();
				break;
			case XmlPullParser.START_TAG:
				name = parser.getName();
				if (name.equalsIgnoreCase("Comments")) {
					item = new HashMap<String, String>();
				} else if (item != null) {
					if (name.equalsIgnoreCase("Image")) {
						item.put("image", parser.nextText());
					} else if (name.equalsIgnoreCase("Name")) {
						item.put("name", parser.nextText());
					} else if (name.equalsIgnoreCase("UserId")) {
						item.put("userId", parser.nextText());
					} else if (name.equalsIgnoreCase("Comment")) {
						item.put("comment", parser.nextText());
					} else if (name.equalsIgnoreCase("CommentId")) {
						item.put("commentId", parser.nextText());
					} else if (name.equalsIgnoreCase("CommentTypeId")) {
						item.put("commentTypeId", parser.nextText());
					} else if (name.equalsIgnoreCase("Type")) {
						item.put("type", parser.nextText());
					}
				}
				break;
			case XmlPullParser.END_TAG:
				name = parser.getName();
				if (name.equalsIgnoreCase("Comments") && item != null) {
					itemList.add(item);
				}
				break;
			}
			eventType = parser.next();
		}

		return itemList;
	}

	public static ArrayList<HashMap<String, String>> getTopScorersList(
			String response) throws XmlPullParserException, IOException {

		item = null;
		itemList = null;

		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser parser = factory.newPullParser();

		parser.setInput(new StringReader(response));
		int eventType = parser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			String name = null;
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				itemList = new ArrayList<HashMap<String, String>>();
				break;
			case XmlPullParser.START_TAG:
				name = parser.getName();
				if (name.equalsIgnoreCase("TopScorers")) {
					item = new HashMap<String, String>();
				} else if (item != null) {
					if (name.equalsIgnoreCase("Image")) {
						item.put("image", parser.nextText());
					} else if (name.equalsIgnoreCase("Name")) {
						item.put("name", parser.nextText());
					} else if (name.equalsIgnoreCase("NickName")) {
						item.put("nickName", parser.nextText());
					} else if (name.equalsIgnoreCase("Trophy")) {
						item.put("trophy", parser.nextText());
					} else if (name.equalsIgnoreCase("UserId")) {
						item.put("id", parser.nextText());
					} else if (name.equalsIgnoreCase("Score")) {
						item.put("count", parser.nextText());
					}
				}
				break;
			case XmlPullParser.END_TAG:
				name = parser.getName();
				if (name.equalsIgnoreCase("TopScorers") && item != null) {
					itemList.add(item);
				}
				break;
			}
			eventType = parser.next();
		}

		return itemList;
	}

	public static ArrayList<HashMap<String, String>> getAttendaceList(
			String response) throws XmlPullParserException, IOException {

		item = null;
		itemList = null;

		double count = 0.0;
		DecimalFormat df = new DecimalFormat("#.##");

		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser parser = factory.newPullParser();

		parser.setInput(new StringReader(response));
		int eventType = parser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			String name = null;
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				itemList = new ArrayList<HashMap<String, String>>();
				break;
			case XmlPullParser.START_TAG:
				name = parser.getName();
				if (name.equalsIgnoreCase("AttendaceStatistics")) {
					item = new HashMap<String, String>();
				} else if (item != null) {
					if (name.equalsIgnoreCase("Image")) {
						item.put("image", parser.nextText());
					} else if (name.equalsIgnoreCase("Name")) {
						item.put("name", parser.nextText());
					} else if (name.equalsIgnoreCase("NickName")) {
						item.put("nickName", parser.nextText());
					} else if (name.equalsIgnoreCase("Trophy")) {
						item.put("trophy", parser.nextText());
					} else if (name.equalsIgnoreCase("UserId")) {
						item.put("id", parser.nextText());
					} else if (name.equalsIgnoreCase("PercentageAttendance")) {
						count = Double.parseDouble(parser.nextText());
						item.put("count", df.format(count));
					}
				}
				break;
			case XmlPullParser.END_TAG:
				name = parser.getName();
				if (name.equalsIgnoreCase("AttendaceStatistics")
						&& item != null) {
					itemList.add(item);
				}
				break;
			}
			eventType = parser.next();
		}

		return itemList;
	}

	public static ArrayList<HashMap<String, String>> getGoalsList(
			String response) throws XmlPullParserException, IOException {

		item = null;
		itemList = null;

		double temp = 0.0;
		double count = 0.0;
		DecimalFormat df = new DecimalFormat("#.##");

		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser parser = factory.newPullParser();

		parser.setInput(new StringReader(response));
		int eventType = parser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {

			String name = null;
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				itemList = new ArrayList<HashMap<String, String>>();
				break;
			case XmlPullParser.START_TAG:
				name = parser.getName();
				if (name.equalsIgnoreCase("AverageGoals")) {
					item = new HashMap<String, String>();
				} else if (item != null) {
					if (name.equalsIgnoreCase("Image")) {
						item.put("image", parser.nextText());
					} else if (name.equalsIgnoreCase("Name")) {
						item.put("name", parser.nextText());
					} else if (name.equalsIgnoreCase("NickName")) {
						item.put("nickName", parser.nextText());
					} else if (name.equalsIgnoreCase("Trophy")) {
						item.put("trophy", parser.nextText());
					} else if (name.equalsIgnoreCase("UserId")) {
						item.put("id", parser.nextText());
					} else if (name.equalsIgnoreCase("Scores")) {
						temp = 0.0;
						count = 0.0;
					} else if (name.equalsIgnoreCase("Goals")) {						
						temp++;
						count = count + Double.parseDouble(parser.nextText());
					}
				}
				break;
			case XmlPullParser.END_TAG:
				name = parser.getName();
				if (name.equalsIgnoreCase("AverageGoals") && item != null) {
					itemList.add(item);
				} else if (name.equalsIgnoreCase("Scores")) {
					// count = count / temp;
					item.put("count", df.format(count));
				}
				break;
			}
			eventType = parser.next();
		}

		return itemList;
	}

	public static ArrayList<HashMap<String, String>> getAssistList(
			String response) throws XmlPullParserException, IOException {

		item = null;
		itemList = null;

		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser parser = factory.newPullParser();

		parser.setInput(new StringReader(response));
		int eventType = parser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			String name = null;
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				itemList = new ArrayList<HashMap<String, String>>();
				break;
			case XmlPullParser.START_TAG:
				name = parser.getName();
				if (name.equalsIgnoreCase("TopAssist")) {
					item = new HashMap<String, String>();
				} else if (item != null) {
					if (name.equalsIgnoreCase("Image")) {
						item.put("image", parser.nextText());
					} else if (name.equalsIgnoreCase("Name")) {
						item.put("name", parser.nextText());
					} else if (name.equalsIgnoreCase("NickName")) {
						item.put("nickName", parser.nextText());
					} else if (name.equalsIgnoreCase("Trophy")) {
						item.put("trophy", parser.nextText());
					} else if (name.equalsIgnoreCase("UserId")) {
						item.put("id", parser.nextText());
					} else if (name.equalsIgnoreCase("Score")) {
						item.put("count", parser.nextText());
					}
				}
				break;
			case XmlPullParser.END_TAG:
				name = parser.getName();
				if (name.equalsIgnoreCase("TopAssist") && item != null) {
					itemList.add(item);
				}
				break;
			}
			eventType = parser.next();
		}

		return itemList;
	}

	public static String getMediaUrl(String response)
			throws XmlPullParserException, IOException {

		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser parser = factory.newPullParser();

		String result = null;

		parser.setInput(new StringReader(response));
		int eventType = parser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			String name = null;
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				break;
			case XmlPullParser.START_TAG:
				name = parser.getName();
				if (name.equalsIgnoreCase("mediaurl")) {
					result = parser.nextText();
				}
				break;
			case XmlPullParser.END_TAG:
				break;
			}
			eventType = parser.next();
		}

		return result;
	}

}
