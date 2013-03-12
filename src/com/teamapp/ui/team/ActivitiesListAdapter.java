package com.teamapp.ui.team;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TreeSet;

import org.ksoap2.serialization.SoapObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.teamapp.apis.facebook.ShareOnFacebook;
import com.teamapp.apis.hyves.ShareOnHyves;
import com.teamapp.apis.twitter.ShareOnTwitter;
import com.teamapp.helper.Utility;
import com.teamapp.helper.WebServiceHelper;
import com.teamapp.ui.LoginAct;
import com.teamapp.ui.R;
import com.teamapp.ui.alerts.TeamAppAlerts;

public class ActivitiesListAdapter extends BaseAdapter {

	private int mResultCode;

	private static final int TYPE_ITEM = 0;
	private static final int TYPE_SEPARATOR = 1;
	private static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;

	private Calendar mCalendar;
	private SimpleDateFormat dateFormat;
	private ArrayList<HashMap<String, String>> itemList;

	private Context mContext;
	private LayoutInflater mInflater;
	private TreeSet mSeparatorsSet = new TreeSet();

	public ActivitiesListAdapter(Context context, boolean isEditable) {

		mResultCode = 0;

		mCalendar = Calendar.getInstance();
		dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

		mContext = context;
		mSeparatorsSet = new TreeSet();
		mInflater = LayoutInflater.from(mContext);
		itemList = new ArrayList<HashMap<String, String>>();
	}

	static class ViewHolder {

		public TextView lblTitle;

		public TextView lblDay;
		public TextView lblMonth;
		public TextView lblActivity;
		public TextView lblLocation;
		public TextView lblTime;
		public ImageView imgAttendance;
		public ImageView imgDelete;
	}

	public int getResultCode() {
		return mResultCode;
	}

	public void addItem(HashMap<String, String> item) {

		itemList.add(item);
		notifyDataSetChanged();
	}

	public void addSeparatorItem(HashMap<String, String> item) {
		itemList.add(item);
		// save separator position
		mSeparatorsSet.add(itemList.size() - 1);
		notifyDataSetChanged();
	}

	@Override
	public int getItemViewType(int position) {
		return mSeparatorsSet.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
	}

	@Override
	public int getViewTypeCount() {
		return TYPE_MAX_COUNT;
	}

	public int getCount() {
		return itemList.size();
	}

	public HashMap<String, String> getItem(int position) {
		return itemList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public ArrayList<HashMap<String, String>> getItemList() {
		return itemList;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		// final int position = position;
		ViewHolder holder = null;
		int viewType = getItemViewType(position);
		if (convertView == null) {
			holder = new ViewHolder();
			switch (viewType) {
			case TYPE_ITEM:
				convertView = mInflater.inflate(R.layout.activities_list_item,
						null);
				holder.lblDay = (TextView) convertView
						.findViewById(R.id.lblDay);
				holder.lblMonth = (TextView) convertView
						.findViewById(R.id.lblMonth);
				holder.lblActivity = (TextView) convertView
						.findViewById(R.id.lblActivity);
				holder.lblLocation = (TextView) convertView
						.findViewById(R.id.lblLocation);
				holder.lblTime = (TextView) convertView
						.findViewById(R.id.lblTime);
				holder.imgAttendance = (ImageView) convertView
						.findViewById(R.id.imgAttendance);
				holder.imgDelete = (ImageView) convertView
						.findViewById(R.id.imgDelete);
				break;
			case TYPE_SEPARATOR:
				convertView = mInflater.inflate(R.layout.item_separator, null);
				holder.lblTitle = (TextView) convertView
						.findViewById(R.id.lblTitle);
				break;
			}
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		switch (viewType) {
		case TYPE_ITEM:
			populateItem(position, holder);
			setListener(position, holder, convertView);
			break;

		case TYPE_SEPARATOR:
			HashMap<String, String> activity = itemList.get(position);
			String month = activity.get("month");
			holder.lblTitle.setText(month);
			break;
		}

		return convertView;
	}

	// Populate item with data
	private void populateItem(int index, ViewHolder holder) {

		HashMap<String, String> activity = getItem(index);
		String type = activity.get("type");
		String name = activity.get("name");
		String month = activity.get("month");
		String dateTime = activity.get("dateTime");
		String location = activity.get("location");
		String attendanceId = activity.get("attendanceId");

		// set text color according to activity type
		int color = mContext.getResources().getColor(R.color.dkgray);
		if (type.equals("Match")) {
			color = mContext.getResources().getColor(R.color.game);
		} else if (type.equals("Training")) {
			color = mContext.getResources().getColor(R.color.training);
		} else if (type.equals("Event")) {
			color = mContext.getResources().getColor(R.color.event);
		} else if (type.equals("Task")) {
			color = mContext.getResources().getColor(R.color.task);
		}

		try {
			mCalendar.setTime(dateFormat.parse(dateTime));
			int day = mCalendar.get(Calendar.DAY_OF_MONTH);
			int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
			int minute = mCalendar.get(Calendar.MINUTE);

			holder.lblDay.setText(new StringBuilder().append(Utility.pad(day)));
			holder.lblTime.setText(new StringBuilder().append(" at ")
					.append(hour).append(":").append(Utility.pad(minute)));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (month != null && month.length() != 0) {
			month = month.substring(0, 3);
		}

		holder.lblMonth.setText(month);

		holder.lblActivity.setText(name);
		holder.lblActivity.setTextColor(color);

		if (location == null) {
			location = "";
		}
		holder.lblLocation.setText(location + " ");

		if (attendanceId.equals("0")) {
			holder.imgAttendance.setImageResource(R.drawable.chk_no_response);
		} else if (attendanceId.equals("1")) {
			holder.imgAttendance.setImageResource(R.drawable.chk_present);
		} else if (attendanceId.equals("2")) {
			holder.imgAttendance.setImageResource(R.drawable.chk_supporting);
		} else if (attendanceId.equals("3")) {
			holder.imgAttendance.setImageResource(R.drawable.chk_absent);
		}

	}

	//
	private void setListener(final int position, final ViewHolder holder,
			View convertView) {

		holder.imgAttendance.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {
				// TODO Auto-generated method stub
				HashMap<String, String> activity = getItem(position);
				String result = activity.get("result");
				String message = activity.get("name");
				if (result == null) {
					showAttendancePopup(view, holder, activity, position);
				} else {
					showSocialMediaPopup(message, view);
				}
			}
		});

		convertView.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				// TODO Auto-generated method stub
				showActivity(position);
			}
		});

	}

	private void showActivity(int index) {

		HashMap<String, String> activity = getItem(index);
		String id = activity.get("id");
		String type = activity.get("type");
		Intent intent = null;
		if (type.equals("Match")) {
			intent = new Intent().setClass(mContext, GameDetailsAct.class);
			intent.putExtra("id", id);
			mContext.startActivity(intent);
		} else if (type.equals("Training")) {
			intent = new Intent().setClass(mContext, TrainingDetailsAct.class);
			intent.putExtra("id", id);
			mContext.startActivity(intent);
		} else if (type.equals("Event")) {
			intent = new Intent().setClass(mContext, EventsDetailsAct.class);
			intent.putExtra("id", id);
			mContext.startActivity(intent);
		} else if (type.equals("Task")) {
			intent = new Intent().setClass(mContext, TasksDetailsAct.class);
			intent.putExtra("id", id);
			mContext.startActivity(intent);
		}

	}

	private void setAttendance(ViewHolder holder,
			HashMap<String, String> activity, String attendanceId, int index) {

		String METHOD_NAME = "SetActivityAttendance";

		String id = activity.get("id");
		String type = activity.get("type");

		SoapObject request = WebServiceHelper.getSOAPRequest(METHOD_NAME);
		request.addProperty("activityId", id);
		request.addProperty("activityType", type);
		request.addProperty("attendanceId", attendanceId);
		request.addProperty("userId", LoginAct.userId);
		request.addProperty("teamId", LoginAct.teamId);
		request.addProperty("clubId", LoginAct.clubId);

		try {
			Object response = WebServiceHelper.getSOAPResponse(request,
					METHOD_NAME);
			boolean status = Boolean.parseBoolean(response.toString());
			if (status) {

				if (attendanceId.equals("1")) {
					holder.imgAttendance
							.setImageResource(R.drawable.chk_present);
				} else if (attendanceId.equals("2")) {
					holder.imgAttendance
							.setImageResource(R.drawable.chk_supporting);
				} else if (attendanceId.equals("3")) {
					holder.imgAttendance
							.setImageResource(R.drawable.chk_absent);
				}
				activity.put("attendanceId", attendanceId);
				itemList.set(index, activity);
				mResultCode = -1;
			} else {
				TeamAppAlerts
						.showToast(mContext, "Failed to update Attendance");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// PopupWindow for Activity Attendance can be set by Captain
	private void showAttendancePopup(final View view, final ViewHolder holder,
			final HashMap<String, String> activity, final int index) {

		String dateTime = activity.get("dateTime");
		try {
			mCalendar.setTime(dateFormat.parse(dateTime));
			Calendar calendar = Calendar.getInstance();
			if (calendar.after(mCalendar)) {
				return;
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		LayoutInflater inflater = LayoutInflater.from(mContext);
		View popupView = inflater.inflate(R.layout.activity_attendance_popup,
				null);
		popupView.measure(View.MeasureSpec.UNSPECIFIED,
				View.MeasureSpec.UNSPECIFIED);

		Display display = ((Activity) mContext).getWindowManager()
				.getDefaultDisplay();
		int width = display.getWidth();
		int height = popupView.getMeasuredHeight() + 10;

		final PopupWindow popupWindow = new PopupWindow(popupView, width,
				height);
		popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setTouchable(true);

		Button btnPresent = (Button) popupView.findViewById(R.id.btnPresent);
		Button btnAbsent = (Button) popupView.findViewById(R.id.btnAbsent);
		Button btnSupporting = (Button) popupView
				.findViewById(R.id.btnSupporting);
		Button btnCancel = (Button) popupView.findViewById(R.id.btnCancel);

		btnPresent.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				popupWindow.dismiss();
				setAttendance(holder, activity, "1", index);
			}
		});

		btnAbsent.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				popupWindow.dismiss();
				setAttendance(holder, activity, "3", index);
			}
		});

		btnSupporting.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				popupWindow.dismiss();
				setAttendance(holder, activity, "2", index);
			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				popupWindow.dismiss();
			}
		});
	}

	// PopupWindow for Article sharing
	private void showSocialMediaPopup(final String message, View view) {

		LayoutInflater inflater = LayoutInflater.from(mContext);
		View popupView = inflater.inflate(R.layout.share_article_popup, null);
		popupView.measure(View.MeasureSpec.UNSPECIFIED,
				View.MeasureSpec.UNSPECIFIED);

		Display display = ((Activity) mContext).getWindowManager()
				.getDefaultDisplay();
		int width = display.getWidth();
		int height = popupView.getMeasuredHeight() + 10;

		final PopupWindow popupWindow = new PopupWindow(popupView, width,
				height, true);
		popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setTouchable(true);

		Button btnFacebook = (Button) popupView.findViewById(R.id.btnFacebook);
		Button btnTwitter = (Button) popupView.findViewById(R.id.btnTwitter);
		Button btnHyves = (Button) popupView.findViewById(R.id.btnHyves);
		Button btnCancel = (Button) popupView.findViewById(R.id.btnCancel);
		
		Log.d("ActivitiesListAdapter", "Match :: " + message);

		btnFacebook.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				popupWindow.dismiss();
				ShareOnFacebook shareOnFacebook = new ShareOnFacebook(mContext,
						message, null);
				shareOnFacebook.share();
			}
		});

		btnTwitter.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				popupWindow.dismiss();
				ShareOnTwitter shareOnTwitter = new ShareOnTwitter(mContext,
						message, null);
				shareOnTwitter.share();
			}
		});

		btnHyves.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				popupWindow.dismiss();
				ShareOnHyves shareOnHyves = new ShareOnHyves(mContext, message);
				shareOnHyves.share();
			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				popupWindow.dismiss();
			}
		});
	}
}