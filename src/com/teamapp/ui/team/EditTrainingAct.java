package com.teamapp.ui.team;

import java.text.ParseException;
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
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;

import com.teamapp.helper.WebServiceHelper;
import com.teamapp.helper.Utility;
import com.teamapp.ui.LoginAct;
import com.teamapp.ui.R;
import com.teamapp.ui.alerts.TeamAppAlerts;

public class EditTrainingAct extends Activity {

	private static final int TIME_DIALOG_ID = 0;
	private static final int DATE_DIALOG_ID = 1;

	private int typeIndex;
	private int seasonIndex;
	private int statusIndex;

	private int mYear;
	private int mMonth;
	private int mDay;
	private int mHour;
	private int mMinute;

	private String id;
	private String seasonId;
	private String seasonName;
	private String typeName;
	private String typeId;
	private String location;
	private String dateTime;
	private String statusId;
	private String details;
	private String clubId;
	private String teamId;

	private Calendar calendar;
	private SimpleDateFormat dateFormat;

	private ArrayList<HashMap<String, String>> typeList;
	private ArrayList<HashMap<String, String>> seasonList;
	private ArrayList<HashMap<String, String>> statusList;

	private TextView lblTitle;

	private EditText txtType;
	private EditText txtDetails;
	private EditText txtLocation;
	private EditText txtTimePicker;
	private EditText txtDatePicker;

	private Spinner spnType;
	private Spinner spnSeason;
	private Spinner spnStatus;

	private Button btnUpdate;

	private TableRow rowOtherType;

	private ProgressDialog pd;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_edit_training);

		teamId = LoginAct.teamId;
		clubId = LoginAct.clubId;
		calendar = Calendar.getInstance();
		dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

		typeList = new ArrayList<HashMap<String, String>>();
		seasonList = new ArrayList<HashMap<String, String>>();
		statusList = new ArrayList<HashMap<String, String>>();

		lblTitle = (TextView) findViewById(R.id.lblTitle);

		txtType = (EditText) findViewById(R.id.txtType);
		txtDetails = (EditText) findViewById(R.id.txtDetails);
		txtLocation = (EditText) findViewById(R.id.txtLocation);
		txtTimePicker = (EditText) findViewById(R.id.txtTimePicker);
		txtDatePicker = (EditText) findViewById(R.id.txtDatePicker);

		spnType = (Spinner) findViewById(R.id.spnType);
		spnSeason = (Spinner) findViewById(R.id.spnSeason);
		spnStatus = (Spinner) findViewById(R.id.spnStatus);

		btnUpdate = (Button) findViewById(R.id.btnSubmit);

		rowOtherType = (TableRow) findViewById(R.id.rowOtherType);

		lblTitle.setText(getString(R.string.edit) + " "
				+ getString(R.string.training));

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

		txtTimePicker.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog(TIME_DIALOG_ID);
			}
		});

		txtDatePicker.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog(DATE_DIALOG_ID);
			}
		});

		btnUpdate.setText(getString(R.string.update));
		btnUpdate.setOnClickListener(new OnClickListener() {

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

	private void getTrainingItem() throws Exception {

		getTrainingDetails();
		getTypeList();
		getSeasons();
		getStatus();
	}

	private void populateTrainingDetails() {

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

		SimpleAdapter seasonAdapter = new SimpleAdapter(this, seasonList,
				android.R.layout.simple_spinner_item,
				new String[] { "seasonName" }, new int[] { android.R.id.text1 });
		seasonAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		seasonAdapter.setViewBinder(viewBinder);
		spnSeason.setAdapter(seasonAdapter);
		spnSeason.setPrompt(getString(R.string.select_season));
		spnSeason.setSelection(seasonIndex);

		SimpleAdapter typeAdapter = new SimpleAdapter(this, typeList,
				android.R.layout.simple_spinner_item,
				new String[] { "typeName" }, new int[] { android.R.id.text1 });
		typeAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		typeAdapter.setViewBinder(viewBinder);
		spnType.setAdapter(typeAdapter);
		spnType.setPrompt(getString(R.string.select_type));
		spnType.setSelection(typeIndex);

		SimpleAdapter statusAdapter = new SimpleAdapter(this, statusList,
				android.R.layout.simple_spinner_item,
				new String[] { "statusName" }, new int[] { android.R.id.text1 });
		statusAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		statusAdapter.setViewBinder(viewBinder);
		spnStatus.setAdapter(statusAdapter);
		spnStatus.setPrompt(getString(R.string.select_status));
		spnStatus.setSelection(statusIndex);

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
		}

		txtLocation.setText(location.trim());

		txtDetails.setText(Utility.stipHtml(details));

	}

	private void getSeasons() throws Exception {

		String METHOD_NAME = "GetAllSeasons";

		SoapObject request = WebServiceHelper.getSOAPRequest(METHOD_NAME);
		request.addProperty("clubId", LoginAct.clubId);
		SoapObject response = (SoapObject) WebServiceHelper.getSOAPResponse(request,
				METHOD_NAME);

		if (response != null) {

			String seasonName = null;
			String seasonId = null;
			String startDate = null;
			String endDate = null;
			HashMap<String, String> season = null;

			for (int i = 0; i < response.getPropertyCount(); i++) {

				SoapObject property = (SoapObject) response.getProperty(i);
				seasonName = property.getProperty("Name").toString();
				seasonId = property.getProperty("SeasonId").toString();
				startDate = property.getProperty("StartDate").toString();
				endDate = property.getProperty("EndDate").toString();

				season = new HashMap<String, String>();
				season.put("seasonId", seasonId);
				season.put("seasonName", seasonName);
				season.put("startDate", startDate);
				season.put("endDate", endDate);
				seasonList.add(season);

				if (this.seasonId.equals(seasonId)) {
					seasonIndex = i;
				}
			}
		}
	}

	private void getTypeList() throws Exception {

		String METHOD_NAME = "GetTrainingTypeList";

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
				typeName = property.getProperty("TrainingType").toString();
				typeId = property.getProperty("TrainingTypeId").toString();

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

	private void getStatus() throws Exception {

		String METHOD_NAME = "GetActivityStatusList";

		SoapObject request = WebServiceHelper.getSOAPRequest(METHOD_NAME);
		request.addProperty("clubId", LoginAct.clubId);
		SoapObject response = (SoapObject) WebServiceHelper.getSOAPResponse(request,
				METHOD_NAME);

		if (response != null) {

			String statusName = null;
			String statusId = null;
			HashMap<String, String> status = null;
			for (int i = 0; i < response.getPropertyCount(); i++) {

				SoapObject property = (SoapObject) response.getProperty(i);
				statusName = property.getProperty("Status").toString();
				statusId = property.getProperty("StatusId").toString();
				status = new HashMap<String, String>();
				status.put("statusId", statusId);
				status.put("statusName", statusName);
				statusList.add(status);

				if (this.statusId.equals(statusId)) {
					statusIndex = i;
				}
			}
		}

	}

	private void getTrainingDetails() throws Exception {

		id = getIntent().getStringExtra("id");
		String METHOD_NAME = "GetTrainingDetailsByTrainingIdTeamId";

		SoapObject request = WebServiceHelper.getSOAPRequest(METHOD_NAME);
		request.addProperty("trainingId", id);
		request.addProperty("teamId", LoginAct.teamId);
		request.addProperty("clubId", LoginAct.clubId);
		SoapObject response = (SoapObject) WebServiceHelper.getSOAPResponse(request,
				METHOD_NAME);

		if (response != null) {

			typeId = response.getProperty("TrainingTypeId").toString();

			if (response.getProperty("Details") != null) {
				details = response.getProperty("Details").toString();
			}

			details = response.getProperty("Details").toString();
			dateTime = response.getProperty("TrainingDateTime").toString();

			if (response.getProperty("Location") != null) {
				location = response.getProperty("Location").toString();
			}

			seasonId = response.getProperty("SeasonId").toString();
			statusId = response.getProperty("StatusId").toString();
		}

	}

	private void validate() {

		HashMap<String, String> season = (HashMap<String, String>) spnSeason
				.getSelectedItem();
		seasonId = season.get("seasonId");
		seasonName = season.get("seasonName");

		String startDate = season.get("startDate");
		String endDate = season.get("endDate");

		if (rowOtherType.isShown()) {
			typeId = "0";
			typeName = txtType.getText().toString();
			if (typeName == null || typeName.length() == 0) {
				TeamAppAlerts.showMessageDialog(EditTrainingAct.this,
						getString(R.string.enter) + " "
								+ getString(R.string.training) + " "
								+ getString(R.string.enter_type));
				return;
			}
		}

		location = txtLocation.getText().toString();
		if (location == null || location.length() == 0) {
			TeamAppAlerts.showMessageDialog(EditTrainingAct.this,
					getString(R.string.enter) + " "
							+ getString(R.string.training) + " "
							+ getString(R.string.enter_location));
			return;
		}

		String date = txtDatePicker.getText().toString();
		if (date == null || date.length() == 0) {
			TeamAppAlerts.showMessageDialog(EditTrainingAct.this,
					getString(R.string.set) + " "
							+ getString(R.string.training) + " "
							+ getString(R.string.set_date));
			return;
		}

		String time = txtTimePicker.getText().toString();
		if (time == null || time.length() == 0) {
			TeamAppAlerts.showMessageDialog(EditTrainingAct.this,
					getString(R.string.set) + " "
							+ getString(R.string.training) + " "
							+ getString(R.string.set_time));
			return;
		}

		calendar.set(mYear, mMonth, mDay, mHour, mMinute);
		dateTime = dateFormat.format(calendar.getTime());

		Calendar sDate = Calendar.getInstance();
		Calendar eDate = Calendar.getInstance();

		try {

			sDate.setTime(dateFormat.parse(startDate));
			eDate.setTime(dateFormat.parse(endDate));

			if (calendar.before(sDate) || calendar.after(eDate)) {
				TeamAppAlerts.showMessageDialog(EditTrainingAct.this,
						getString(R.string.training) + " "
								+ getString(R.string.out_of_season));
				return;
			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		HashMap<String, String> status = (HashMap<String, String>) spnStatus
				.getSelectedItem();
		statusId = status.get("statusId");

		details = txtDetails.getText().toString();
		if (details == null || details.length() == 0) {
			TeamAppAlerts.showMessageDialog(EditTrainingAct.this,
					getString(R.string.enter) + " "
							+ getString(R.string.training) + " "
							+ getString(R.string.enter_details));
			return;
		}

		runUpdate();

	}

	private boolean updateTraining() throws Exception {

		String METHOD_NAME = "UpdateTraining";
		String envelope = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\" xmlns:voet=\"http://schemas.datacontract.org/2004/07/VoetBall.Entities.DataObjects\">"
				+ "<soapenv:Header/>"
				+ "<soapenv:Body>"
				+ "<tem:UpdateTraining>"
				+ "<tem:dTraining>"
				+ "<voet:TrainingType>%s</voet:TrainingType>"
				+ "<voet:TrainingTypeId>%s</voet:TrainingTypeId>"
				+ "<voet:Details>%s</voet:Details>"
				+ "<voet:Location>%s</voet:Location>"
				+ "<voet:SeasonId>%s</voet:SeasonId>"
				+ "<voet:SeasonName>%s</voet:SeasonName>"
				+ "<voet:StatusId>%s</voet:StatusId>"
				+ "<voet:TeamId>%s</voet:TeamId>"
				+ "<voet:TrainingDateTime>%s</voet:TrainingDateTime>"
				+ "<voet:TrainingId>%s</voet:TrainingId>"
				+ "</tem:dTraining>"
				+ "<tem:clubId>%s</tem:clubId>"
				+ "</tem:UpdateTraining>"
				+ "</soapenv:Body>" + "</soapenv:Envelope>";

		String request = String.format(envelope, typeName.trim(), typeId,
				details.trim(), location.trim(), seasonId, seasonName,
				statusId, teamId, dateTime, id, clubId);

		Log.d("request", request);

		String response = (String) WebServiceHelper.callWebService(METHOD_NAME,
				request);

		Log.d("response", response);

		return WebServiceHelper.getStatus(response);

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
					TeamAppAlerts.showToast(EditTrainingAct.this,
							getString(R.string.update_failed) + ""
									+ getString(R.string.training));
					break;
				case 2:
					pd.dismiss();
					TeamAppAlerts.showToast(EditTrainingAct.this,
							getString(R.string.service));
					break;
				}
			}
		};

		pd = ProgressDialog.show(EditTrainingAct.this, "",
				getString(R.string.wait), true, false);
		new Thread() {
			@Override
			public void run() {

				int what = 0;

				// Make request and handle exceptions
				try {
					if (!updateTraining()) {
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
			pd = ProgressDialog.show(EditTrainingAct.this, "",
					getString(R.string.loading), true, true);
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				getTrainingItem();
			} catch (Exception e) {
				cancel(false);
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			populateTrainingDetails();
			pd.dismiss();
		}

		@Override
		protected void onCancelled() {
			pd.dismiss();
			TeamAppAlerts.showToast(EditTrainingAct.this,
					getString(R.string.service));
		}
	}

}
