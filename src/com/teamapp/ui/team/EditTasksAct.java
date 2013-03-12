package com.teamapp.ui.team;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import org.ksoap2.serialization.SoapObject;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.teamapp.helper.WebServiceHelper;
import com.teamapp.helper.Utility;
import com.teamapp.ui.LoginAct;
import com.teamapp.ui.R;
import com.teamapp.ui.alerts.TeamAppAlerts;

public class EditTasksAct extends Activity {

	private static final int TIME_DIALOG_ID = 1;
	private static final int DATE_DIALOG_ID = 0;

	private int mYear;
	private int mMonth;
	private int mDay;
	private int mHour;
	private int mMinute;

	private int typeIndex;

	private String id;
	private String typeName;
	private String typeId;
	private String dateTime;
	private String details;
	private String threshold;
	private String clubId;
	private String teamId;
	private String userId;

	private Calendar calendar;
	private SimpleDateFormat dateFormat;

	private ArrayList<String> assignedList;
	private ArrayList<HashMap<String, String>> typeList;
	private ArrayList<HashMap<String, String>> playerList;

	private TextView lblTitle;

	private EditText txtType;
	private EditText txtTimePicker;
	private EditText txtDatePicker;
	private EditText txtDetails;
	private EditText txtThreshold;

	private Spinner spnType;

	private ListView lstPlayer;

	private Button btnSubmit;

	private TableRow rowOtherType;

	private ProgressDialog pd;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_edit_tasks);

		teamId = LoginAct.teamId;
		clubId = LoginAct.clubId;
		userId = LoginAct.userId;

		calendar = Calendar.getInstance();
		dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

		assignedList = new ArrayList<String>();
		typeList = new ArrayList<HashMap<String, String>>();
		playerList = new ArrayList<HashMap<String, String>>();

		id = getIntent().getStringExtra("id");

		lblTitle = (TextView) findViewById(R.id.lblTitle);

		txtType = (EditText) findViewById(R.id.txtType);
		txtTimePicker = (EditText) findViewById(R.id.txtTimePicker);
		txtDatePicker = (EditText) findViewById(R.id.txtDatePicker);
		txtDetails = (EditText) findViewById(R.id.txtDetails);
		txtThreshold = (EditText) findViewById(R.id.txtThreshold);

		spnType = (Spinner) findViewById(R.id.spnType);

		lstPlayer = (ListView) findViewById(R.id.lstPlayer);

		btnSubmit = (Button) findViewById(R.id.btnSubmit);

		rowOtherType = (TableRow) findViewById(R.id.rowOtherType);

		lblTitle.setText(getString(R.string.edit) + " "
				+ getString(R.string.task));

		lstPlayer.setDivider(new ColorDrawable(0xffE6E6E6));
		lstPlayer.setDividerHeight(1);

		spnType.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				// TODO Auto-generated method stub
				typeName = (String) typeList.get(pos).get("typeName");
				typeId = (String) typeList.get(pos).get("typeId");
				if (typeName.equals("Other")) {
					rowOtherType.setVisibility(View.VISIBLE);
				} else {
					rowOtherType.setVisibility(View.GONE);
				}
			}

			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
			}
		});

		txtDatePicker.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog(DATE_DIALOG_ID);
			}
		});

		txtTimePicker.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog(TIME_DIALOG_ID);
			}
		});

		btnSubmit.setText("Update");
		btnSubmit.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {
				// TODO Auto-generated method stub
				validate();
			}
		});

		new TheTask().execute();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case TIME_DIALOG_ID:
			return new TimePickerDialog(this, mTimeSetListener, mHour, mMinute,
					false);

		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth,
					mDay);
		}
		return null;
	}

	private void populateItem() {

		// Add a ViewBinder to display a color name in a TextView within the
		// Spinner. (This isn't needed in AndroidOS 2.2. In earlier releases,
		// when we're displaying text data within a Spinner, and no ViewBinder
		// is set in the SimpleAdapter, an IllegalStateException is thrown.)
		SimpleAdapter.ViewBinder viewBinder = new SimpleAdapter.ViewBinder() {

			public boolean setViewValue(View view, Object data,
					String textRepresentation) {
				// We configured the SimpleAdapter to create TextViews (see
				// the 'to' array, above), so this cast should be safe:
				TextView textView = (TextView) view;
				textView.setText(textRepresentation);
				return true;
			}
		};

		SimpleAdapter typeAdapter = new SimpleAdapter(this, typeList,
				android.R.layout.simple_spinner_item,
				new String[] { "typeName" }, new int[] { android.R.id.text1 });

		typeAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		typeAdapter.setViewBinder(viewBinder);
		spnType.setAdapter(typeAdapter);
		spnType.setPrompt(getString(R.string.select_type));
		spnType.setSelection(typeIndex);

		lstPlayer.setAdapter(new PlayerListAdapter(this));
		Utility.setListViewHeightBasedOnChildren(lstPlayer);

		try {

			calendar.setTime(dateFormat.parse(dateTime));

			mYear = calendar.get(Calendar.YEAR);
			mMonth = calendar.get(Calendar.MONTH);
			mDay = calendar.get(Calendar.DATE);
			mHour = calendar.get(Calendar.HOUR_OF_DAY);
			mMinute = calendar.get(Calendar.MINUTE);

			updateDate();
			updateTime();

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		txtDetails.setText(Utility.stipHtml(details));

		txtThreshold.setText(threshold.trim());

	}

	private void getTypeList() throws Exception {

		String METHOD_NAME = "GetTaskTypeList";

		SoapObject request = WebServiceHelper.getSOAPRequest(METHOD_NAME);
		request.addProperty("clubId", LoginAct.clubId);
		SoapObject response = (SoapObject) WebServiceHelper.getSOAPResponse(request,
				METHOD_NAME);

		if (response != null) {

			String typeName = null;
			String typeId = null;
			HashMap<String, String> type = null;

			for (int i = 0; i < response.getPropertyCount(); i++) {

				SoapObject property = (SoapObject) response.getProperty(i);
				typeName = property.getProperty("TaskType").toString();
				typeId = property.getProperty("TaskTypeId").toString();
				type = new HashMap<String, String>();
				type.put("typeName", typeName);
				type.put("typeId", typeId);
				typeList.add(type);

				if (this.typeId.equals(typeId)) {
					typeIndex = i;
				}
			}
		}
	}

	// Get the details of activity from web service
	private void getActivityDetail() throws Exception {

		String METHOD_NAME = "GetTaskById";

		SoapObject request = WebServiceHelper.getSOAPRequest(METHOD_NAME);
		request.addProperty("taskId", id);
		request.addProperty("clubId", LoginAct.clubId);
		SoapObject response = (SoapObject) WebServiceHelper.getSOAPResponse(request,
				METHOD_NAME);

		if (response != null) {

			typeName = response.getProperty("TaskType").toString();
			typeId = response.getProperty("TaskTypeId").toString();
			dateTime = response.getProperty("TaskDateTime").toString();

			if (response.getProperty("Details") != null) {
				details = response.getProperty("Details").toString();
			}

			if (response.getProperty("ThresholdMembers") != null) {
				threshold = response.getProperty("ThresholdMembers").toString();
			}

			SoapObject soapPlayers = (SoapObject) response
					.getProperty("AssignedToUsers");

			if (soapPlayers != null) {
				for (int i = 0; i < soapPlayers.getPropertyCount(); i++) {

					SoapObject soapPlayer = (SoapObject) soapPlayers
							.getProperty(i);
					String userId = soapPlayer.getProperty("UserId").toString();
					assignedList.add(userId);

				}
			}
		}
	}

	private void getPlayers() throws Exception {

		String METHOD_NAME = "GetPlayersByTeamId";

		SoapObject request = WebServiceHelper.getSOAPRequest(METHOD_NAME);
		request.addProperty("clubId", LoginAct.clubId);
		request.addProperty("teamId", LoginAct.teamId);

		SoapObject response = (SoapObject) WebServiceHelper.getSOAPResponse(request,
				METHOD_NAME);

		if (response != null) {

			String userId = null;
			String userName = null;
			String isAssigned = null;
			HashMap<String, String> player = null;

			for (int i = 0; i < response.getPropertyCount(); i++) {

				SoapObject property = (SoapObject) response.getProperty(i);
				userId = property.getProperty("UserId").toString();
				userName = property.getProperty("Name").toString();

				isAssigned = "false";
				for (int j = 0; j < assignedList.size(); j++) {
					String temp = assignedList.get(j);
					if (userId.equals(temp)) {
						isAssigned = "true";
						break;
					}
				}

				player = new HashMap<String, String>();
				player.put("userId", userId);
				player.put("userName", userName);
				player.put("isAssigned", isAssigned);
				playerList.add(player);
			}
		}
	}

	private void validate() {

		if (rowOtherType.isShown()) {
			typeId = "0";
			typeName = txtType.getText().toString();
			if (typeName == null || typeName.length() == 0) {
				TeamAppAlerts.showMessageDialog(EditTasksAct.this,
						getString(R.string.enter) + " "
								+ getString(R.string.task) + " "
								+ getString(R.string.enter_type));
				return;
			}
		}

		String date = txtDatePicker.getText().toString();
		if (date == null || date.length() == 0) {
			TeamAppAlerts.showMessageDialog(EditTasksAct.this,
					getString(R.string.set) + " " + getString(R.string.task)
							+ " " + getString(R.string.set_date));
			return;
		}

		String time = txtTimePicker.getText().toString();
		if (time == null || time.length() == 0) {
			TeamAppAlerts.showMessageDialog(EditTasksAct.this,
					getString(R.string.set) + " " + getString(R.string.task)
							+ " " + getString(R.string.set_time));
			return;
		}

		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss");
		calendar.set(mYear, mMonth, mDay, mHour, mMinute);
		dateTime = dateFormat.format(calendar.getTime());

		Calendar current = Calendar.getInstance();
		if (current.after(calendar)) {
			TeamAppAlerts.showMessageDialog(EditTasksAct.this,
					getString(R.string.date_gone));
			return;
		}

		details = txtDetails.getText().toString();
		if (details == null || details.length() == 0) {
			TeamAppAlerts.showMessageDialog(EditTasksAct.this,
					getString(R.string.enter) + " " + getString(R.string.task)
							+ " " + getString(R.string.enter_details));
			return;
		}

		threshold = txtThreshold.getText().toString();
		if (threshold == null || threshold.length() == 0) {
			TeamAppAlerts.showMessageDialog(EditTasksAct.this,
					getString(R.string.enter) + " " + getString(R.string.task)
							+ " " + getString(R.string.enter_threshold));
			return;
		}

		runUpdate();

	}

	private boolean updateTask() throws Exception {

		StringBuilder assignedUsers = new StringBuilder();
		for (int i = 0; i < playerList.size(); i++) {

			HashMap<String, String> player = playerList.get(i);
			String userId = player.get("userId");
			String isAssigned = player.get("isAssigned");

			if (Boolean.parseBoolean(isAssigned)) {
				assignedUsers
						.append("<voet:Player><voet:Image></voet:Image><voet:Name></voet:Name><voet:UserId>")
						.append(userId).append("</voet:UserId></voet:Player>");
			}

		}

		Log.d("AfterassignedToUsers", assignedUsers.toString());

		String METHOD_NAME = "UpdateTask";
		String envelope = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\" xmlns:voet=\"http://schemas.datacontract.org/2004/07/VoetBall.Entities.DataObjects\">"
				+ "<soapenv:Header/>"
				+ "<soapenv:Body>"
				+ "<tem:UpdateTask>"
				+ "<tem:task>"
				+ "<voet:TaskType>%s</voet:TaskType>"
				+ "<voet:TaskTypeId>%s</voet:TaskTypeId>"
				+ "<voet:AssignedToUsers>%s</voet:AssignedToUsers>"
				+ "<voet:CreatedByUserId>%s</voet:CreatedByUserId>"
				+ "<voet:Details>%s</voet:Details>"
				+ "<voet:TaskDateTime>%s</voet:TaskDateTime>"
				+ "<voet:TaskId>%s</voet:TaskId>"
				+ "<voet:TeamId>%s</voet:TeamId>"
				+ "<voet:ThresholdMembers>%s</voet:ThresholdMembers>"
				+ "</tem:task>"
				+ "<tem:clubId>%s</tem:clubId>"
				+ "</tem:UpdateTask>"
				+ "</soapenv:Body>"
				+ "</soapenv:Envelope>";

		String request = String.format(envelope, typeName, typeId,
				assignedUsers, userId, details.trim(), dateTime, id, teamId,
				threshold, clubId);

		Log.d("request", request);

		String response = (String) WebServiceHelper.callWebService(METHOD_NAME,
				request);

		Log.d("response", response);

		return WebServiceHelper.getStatus(response);

	}

	private void showToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}

	private void updateDate() {

		txtDatePicker.setText(new StringBuilder()
				// Month is 0 based so add 1
				.append(pad(mDay)).append("-").append(pad(mMonth + 1))
				.append("-").append(mYear).append(" "));

	}

	private void updateTime() {
		txtTimePicker.setText(new StringBuilder().append(pad(mHour))
				.append(".").append(pad(mMinute)));
	}

	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			updateDate();
		}
	};

	private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			mHour = hourOfDay;
			mMinute = minute;
			updateTime();
		}
	};

	private static String pad(int c) {
		if (c >= 10)
			return String.valueOf(c);
		else
			return "0" + String.valueOf(c);
	}

	private void runUpdate() {

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case 0:
					pd.dismiss();
					setResult(RESULT_OK);
					finish();
					break;
				case 1:
					pd.dismiss();
					TeamAppAlerts.showToast(EditTasksAct.this,
							getString(R.string.update_failed) + ""
									+ getString(R.string.task));
					break;
				case 2:
					pd.dismiss();
					TeamAppAlerts.showToast(EditTasksAct.this,
							getString(R.string.service));
					break;
				}
			}
		};

		pd = ProgressDialog.show(EditTasksAct.this, "",
				getString(R.string.wait), true, false);
		new Thread() {
			@Override
			public void run() {

				int what = 0;

				// Make request and handle exceptions
				try {
					if (!updateTask()) {
						what = 1;
					}
				} catch (Exception e) {
					e.printStackTrace();
					what = 2;
				}

				handler.sendEmptyMessage(what);
			}
		}.start();
	}

	private class TheTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			pd = ProgressDialog.show(EditTasksAct.this, "",
					getString(R.string.loading), true, false);
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				getActivityDetail();
				getTypeList();
				getPlayers();
			} catch (Exception e) {
				cancel(false);
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			populateItem();
			pd.dismiss();
		}

		@Override
		protected void onCancelled() {
			pd.dismiss();
			TeamAppAlerts.showToast(EditTasksAct.this,
					getString(R.string.service));
		}
	}

	public static class ViewHolder {
		public TextView lblName;
		public CheckBox chkStatus;
	}

	private class PlayerListAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		public PlayerListAdapter(Context context) {
			mInflater = LayoutInflater.from(context);
		}

		public int getCount() {
			return playerList.size();
		}

		public HashMap<String, String> getItem(int position) {
			return playerList.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = mInflater.inflate(
						R.layout.list_item_with_checkbox, null);
				holder = new ViewHolder();
				holder.lblName = (TextView) convertView
						.findViewById(R.id.lblName);
				holder.chkStatus = (CheckBox) convertView
						.findViewById(R.id.chkStatus);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			HashMap<String, String> player = getItem(position);
			String userName = player.get("userName");
			String assigned = player.get("isAssigned");

			holder.lblName.setText(userName);
			holder.chkStatus.setChecked(Boolean.parseBoolean(assigned));

			holder.chkStatus
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							// TODO Auto-generated method stub
							HashMap<String, String> player = getItem(position);
							player.put("isAssigned", String.valueOf(isChecked));
							playerList.set(position, player);
						}
					});

			return convertView;
		}
	}

}
