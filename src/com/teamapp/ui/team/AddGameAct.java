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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;

import com.teamapp.helper.WebServiceHelper;
import com.teamapp.ui.LoginAct;
import com.teamapp.ui.R;
import com.teamapp.ui.alerts.TeamAppAlerts;

public class AddGameAct extends Activity {

	private static final int ADATE_DIALOG_ID = 0;
	private static final int ATIME_DIALOG_ID = 1;
	private static final int DATE_DIALOG_ID = 2;
	private static final int TIME_DIALOG_ID = 3;

	private boolean atHome;

	private int mAssemblyYear;
	private int mAssemblyMonth;
	private int mAssemblyDay;
	private int mAssemblyHour;
	private int mAssemblyMinute;

	private int mGameYear;
	private int mGameMonth;
	private int mGameDay;
	private int mGameHour;
	private int mGameMinute;

	private String startDate;
	private String endDate;

	private String seasonId;
	private String seasonName;
	private String clubId;
	private String teamOne;
	private String teamTwo;
	private String typeName;
	private String typeId;
	private String location;
	private String locationId;
	private String assembly;
	private String assemblyDateTime;
	private String gameDateTime;
	private String statusId;
	private String details;
	private String myClub;

	private Calendar calendar;
	private SimpleDateFormat dateFormat;

	private ArrayList<HashMap<String, String>> clubList;
	private ArrayList<HashMap<String, String>> teamList;
	private ArrayList<HashMap<String, String>> typeList;
	private ArrayList<HashMap<String, String>> seasonList;
	private ArrayList<HashMap<String, String>> statusList;
	private ArrayList<HashMap<String, String>> locationList;

	private TextView lblTitle;

	private EditText txtType;
	private EditText txtAssembly;
	private EditText txtAssemblyTime;
	private EditText txtDetails;
	private EditText txtLocation;
	private EditText txtGameDate;
	private EditText txtGameTime;

	private RadioButton radHome;
	private RadioButton radAway;

	private Spinner spnClub;
	private Spinner spnType;
	private Spinner spnSeason;
	private Spinner spnStatus;
	private Spinner spnOpponent;
	private Spinner spnLocation;

	private Button btnSubmit;

	private TableRow rowOtherType;
	private TableRow rowOtherLocation;

	private SimpleAdapter.ViewBinder viewBinder;

	private ProgressDialog pd;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_edit_game);

		teamOne = LoginAct.teamId;
		myClub = LoginAct.clubId;
		calendar = Calendar.getInstance();
		dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

		clubList = new ArrayList<HashMap<String, String>>();
		teamList = new ArrayList<HashMap<String, String>>();
		typeList = new ArrayList<HashMap<String, String>>();
		seasonList = new ArrayList<HashMap<String, String>>();
		statusList = new ArrayList<HashMap<String, String>>();
		locationList = new ArrayList<HashMap<String, String>>();

		lblTitle = (TextView) findViewById(R.id.lblTitle);

		txtType = (EditText) findViewById(R.id.txtType);
		txtAssembly = (EditText) findViewById(R.id.txtAssembly);
		txtAssemblyTime = (EditText) findViewById(R.id.txtAssemblyTime);
		txtLocation = (EditText) findViewById(R.id.txtLocation);
		txtGameTime = (EditText) findViewById(R.id.txtGameTime);
		txtGameDate = (EditText) findViewById(R.id.txtGameDate);
		txtDetails = (EditText) findViewById(R.id.txtDetails);

		radHome = (RadioButton) findViewById(R.id.radHome);
		radAway = (RadioButton) findViewById(R.id.radAway);

		spnClub = (Spinner) findViewById(R.id.spnClub);
		spnType = (Spinner) findViewById(R.id.spnType);
		spnSeason = (Spinner) findViewById(R.id.spnSeason);
		spnStatus = (Spinner) findViewById(R.id.spnStatus);
		spnOpponent = (Spinner) findViewById(R.id.spnOpponent);
		spnLocation = (Spinner) findViewById(R.id.spnLocation);

		btnSubmit = (Button) findViewById(R.id.btnSubmit);

		rowOtherType = (TableRow) findViewById(R.id.rowOtherType);
		rowOtherLocation = (TableRow) findViewById(R.id.rowOtherLocation);

		lblTitle.setText(getString(R.string.add) + " "
				+ getString(R.string.game));

		spnSeason.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				// TODO Auto-generated method stub
				seasonId = (String) seasonList.get(pos).get("seasonId");
				seasonName = (String) seasonList.get(pos).get("seasonName");
				startDate = (String) seasonList.get(pos).get("startDate");
				endDate = (String) seasonList.get(pos).get("endDate");

				try {

					Calendar current = Calendar.getInstance();
					mAssemblyYear = current.get(Calendar.YEAR);
					mAssemblyMonth = current.get(Calendar.MONTH);
					mAssemblyDay = current.get(Calendar.DATE);
					mAssemblyHour = current.get(Calendar.HOUR_OF_DAY);
					mAssemblyMinute = current.get(Calendar.MINUTE);

					calendar.setTime(dateFormat.parse(startDate));
					if (current.after(calendar)) {
						calendar = current;
					}

					mGameYear = calendar.get(Calendar.YEAR);
					mGameMonth = calendar.get(Calendar.MONTH);
					mGameDay = calendar.get(Calendar.DATE);
					mGameHour = calendar.get(Calendar.HOUR_OF_DAY);
					mGameMinute = calendar.get(Calendar.MINUTE);

					calendar.setTime(dateFormat.parse(endDate));
					if (current.after(calendar)) {
						TeamAppAlerts.showToast(AddGameAct.this,
								getString(R.string.season_gone));
					}

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				txtAssemblyTime.setText(null);
				txtGameDate.setText(null);
				txtGameTime.setText(null);
			}

			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
			}
		});

		spnClub.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				// TODO Auto-generated method stub
				clubId = (String) clubList.get(pos).get("clubId");
				runTeamList();
				if (radAway.isChecked()) {
					runLocationList();
				}
				Log.d("spnClub", "finish...");
			}

			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
			}
		});

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

		spnLocation.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				// TODO Auto-generated method stub
				location = (String) locationList.get(pos).get("locationName");
				locationId = (String) locationList.get(pos).get("locationId");

				if (location.equals("Other")) {
					rowOtherLocation.setVisibility(View.VISIBLE);
				} else {
					rowOtherLocation.setVisibility(View.GONE);
				}
			}

			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
			}
		});

		txtAssemblyTime.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog(ATIME_DIALOG_ID);
			}
		});

		txtGameDate.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog(DATE_DIALOG_ID);
			}
		});

		txtGameTime.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog(TIME_DIALOG_ID);
			}
		});

		radHome.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					atHome = true;
					clubId = LoginAct.clubId;
					runLocationList();
				}
			}
		});

		radAway.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					atHome = false;
					HashMap<String, String> club = (HashMap<String, String>) spnClub
							.getSelectedItem();
					clubId = club.get("clubId");
					runLocationList();
				}
			}
		});

		btnSubmit.setText(getString(R.string.submit));
		btnSubmit.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {
				// TODO Auto-generated method stub
				validate();
			}
		});

		runDefault();

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case ADATE_DIALOG_ID:
			return new DatePickerDialog(this, mAssemblyDateSetListener,
					mAssemblyYear, mAssemblyMonth, mAssemblyDay);
		case ATIME_DIALOG_ID:
			return new TimePickerDialog(this, mAssemblyTimeSetListener,
					mAssemblyHour, mAssemblyMinute, false);
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, mGameDateSetListener, mGameYear,
					mGameMonth, mGameDay);
		case TIME_DIALOG_ID:
			return new TimePickerDialog(this, mGameTimeSetListener, mGameHour,
					mGameMinute, false);
		}
		return null;
	}

	private void getGameItem() throws Exception {

		getSeasons();
		getClubList();
		getTypeList();
		getStatus();
	}

	private void populateItem() {

		viewBinder = new SimpleAdapter.ViewBinder() {

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

		SimpleAdapter clubAdapter = new SimpleAdapter(this, clubList,
				android.R.layout.simple_spinner_item,
				new String[] { "clubName" }, new int[] { android.R.id.text1 });
		clubAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		clubAdapter.setViewBinder(viewBinder);
		spnClub.setAdapter(clubAdapter);
		spnClub.setPrompt(getString(R.string.select_club));

		SimpleAdapter typeAdapter = new SimpleAdapter(this, typeList,
				android.R.layout.simple_spinner_item,
				new String[] { "typeName" }, new int[] { android.R.id.text1 });
		typeAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		typeAdapter.setViewBinder(viewBinder);
		spnType.setAdapter(typeAdapter);
		spnType.setPrompt(getString(R.string.select_type));

		SimpleAdapter statusAdapter = new SimpleAdapter(this, statusList,
				android.R.layout.simple_spinner_item,
				new String[] { "statusName" }, new int[] { android.R.id.text1 });
		statusAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		statusAdapter.setViewBinder(viewBinder);
		spnStatus.setAdapter(statusAdapter);
		spnStatus.setPrompt(getString(R.string.select_status));

		radHome.setChecked(true);

	}

	private void populateOpponent() {

		SimpleAdapter teamAdapter = new SimpleAdapter(this, teamList,
				android.R.layout.simple_spinner_item,
				new String[] { "teamName" }, new int[] { android.R.id.text1 });
		teamAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		teamAdapter.setViewBinder(viewBinder);
		spnOpponent.setAdapter(teamAdapter);
		spnOpponent.setPrompt(getString(R.string.select_opponent));
	}

	private void populateLocation() {

		SimpleAdapter typeAdapter = new SimpleAdapter(this, locationList,
				android.R.layout.simple_spinner_item,
				new String[] { "locationName" },
				new int[] { android.R.id.text1 });
		typeAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		typeAdapter.setViewBinder(viewBinder);
		spnLocation.setAdapter(typeAdapter);
		spnLocation.setPrompt(getString(R.string.select_club));
	}

	private void getSeasons() throws Exception {

		String METHOD_NAME = "GetAllSeasons";

		seasonList.clear();
		SoapObject request = WebServiceHelper.getSOAPRequest(METHOD_NAME);
		request.addProperty("clubId", LoginAct.clubId);
		SoapObject response = (SoapObject) WebServiceHelper.getSOAPResponse(
				request, METHOD_NAME);

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
			}
		}
	}

	private void getClubList() throws Exception {

		String METHOD_NAME = "GetOppositeClubsListForMatch";

		clubList.clear();
		SoapObject request = WebServiceHelper.getSOAPRequest(METHOD_NAME);
		request.addProperty("clubId", LoginAct.clubId);
		SoapObject response = (SoapObject) WebServiceHelper.getSOAPResponse(
				request, METHOD_NAME);

		if (response != null) {

			String clubId = null;
			String clubName = null;
			HashMap<String, String> club = null;

			for (int i = 0; i < response.getPropertyCount(); i++) {

				SoapObject property = (SoapObject) response.getProperty(i);
				clubId = property.getProperty("ClubId").toString();
				clubName = property.getProperty("ClubName").toString();

				club = new HashMap<String, String>();
				club.put("clubId", clubId);
				club.put("clubName", clubName);
				clubList.add(club);
			}
		}
	}

	private void getTeamList() throws Exception {

		String METHOD_NAME = "GetOppositeTeamListForMyMatch";

		teamList.clear();
		SoapObject request = WebServiceHelper.getSOAPRequest(METHOD_NAME);
		request.addProperty("myTeamId", LoginAct.teamId);
		request.addProperty("oppositeClubId", clubId);
		request.addProperty("myClubId", LoginAct.clubId);
		SoapObject response = (SoapObject) WebServiceHelper.getSOAPResponse(
				request, METHOD_NAME);

		if (response != null) {

			String teamId = null;
			String teamName = null;
			HashMap<String, String> team = null;

			for (int i = 0; i < response.getPropertyCount(); i++) {

				SoapObject property = (SoapObject) response.getProperty(i);
				teamId = property.getProperty("TeamId").toString();
				teamName = property.getProperty("TeamName").toString();

				team = new HashMap<String, String>();
				team.put("teamId", teamId);
				team.put("teamName", teamName);
				teamList.add(team);
			}
		}
	}

	private void getTypeList() throws Exception {

		String METHOD_NAME = "GetMatchTypeList";

		typeList.clear();
		SoapObject request = WebServiceHelper.getSOAPRequest(METHOD_NAME);
		request.addProperty("clubId", LoginAct.clubId);
		SoapObject response = (SoapObject) WebServiceHelper.getSOAPResponse(
				request, METHOD_NAME);

		if (response != null) {

			String typeName = null;
			String typeId = null;
			HashMap<String, String> type = null;

			for (int i = 0; i < response.getPropertyCount(); i++) {

				SoapObject property = (SoapObject) response.getProperty(i);
				typeName = property.getProperty("MatchType").toString();
				typeId = property.getProperty("MatchTypeId").toString();

				type = new HashMap<String, String>();
				type.put("typeName", typeName);
				type.put("typeId", typeId);
				typeList.add(type);
			}
		}
	}

	private void getLocationList() throws Exception {

		String METHOD_NAME = "GetMatchLocationsByClubId";

		locationList.clear();
		SoapObject request = WebServiceHelper.getSOAPRequest(METHOD_NAME);
		request.addProperty("clubId", clubId);
		SoapObject response = (SoapObject) WebServiceHelper.getSOAPResponse(
				request, METHOD_NAME);

		if (response != null) {

			String locationName = null;
			String locationId = null;
			HashMap<String, String> location = null;

			for (int i = 0; i < response.getPropertyCount(); i++) {

				SoapObject property = (SoapObject) response.getProperty(i);
				locationName = property.getProperty("Location").toString();
				locationId = property.getProperty("LocationId").toString();

				location = new HashMap<String, String>();
				location.put("locationName", locationName);
				location.put("locationId", locationId);
				locationList.add(location);
			}
		}
	}

	private void getStatus() throws Exception {

		String METHOD_NAME = "GetActivityStatusList";

		statusList.clear();
		SoapObject request = WebServiceHelper.getSOAPRequest(METHOD_NAME);
		request.addProperty("clubId", LoginAct.clubId);
		SoapObject response = (SoapObject) WebServiceHelper.getSOAPResponse(
				request, METHOD_NAME);

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
			}
		}
	}

	private void validate() {

		HashMap<String, String> team = (HashMap<String, String>) spnOpponent
				.getSelectedItem();
		teamTwo = team.get("teamId");

		if (rowOtherType.isShown()) {
			typeId = "0";
			typeName = txtType.getText().toString();
			if (typeName == null || typeName.length() == 0) {
				TeamAppAlerts.showMessageDialog(AddGameAct.this,
						getString(R.string.enter) + " "
								+ getString(R.string.game) + " "
								+ getString(R.string.enter_type));
				return;
			}
		}

		if (rowOtherLocation.isShown()) {
			locationId = "0";
			location = txtLocation.getText().toString();
			Log.d("rowOtherLocation", ":: " + rowOtherLocation.isShown());
			if (location == null || location.length() == 0) {
				TeamAppAlerts.showMessageDialog(AddGameAct.this,
						getString(R.string.enter) + " "
								+ getString(R.string.game) + " "
								+ getString(R.string.enter_location));
				return;
			}
		}

		assembly = txtAssembly.getText().toString();
		if (assembly == null || assembly.length() == 0) {
			TeamAppAlerts.showMessageDialog(AddGameAct.this,
					getString(R.string.enter) + " "
							+ getString(R.string.assembly) + " "
							+ getString(R.string.enter_location));
			return;
		}

		String assemblyTime = txtAssemblyTime.getText().toString();
		if (assemblyTime == null || assemblyTime.length() == 0) {
			TeamAppAlerts.showMessageDialog(AddGameAct.this,
					getString(R.string.set) + " "
							+ getString(R.string.assembly) + " "
							+ getString(R.string.set_time));
			return;
		}

		calendar.set(mGameYear, mGameMonth, mGameDay, mAssemblyHour,
				mAssemblyMinute);
		assemblyDateTime = dateFormat.format(calendar.getTime());

		Calendar current = Calendar.getInstance();
		if (current.after(calendar)) {
			TeamAppAlerts.showMessageDialog(AddGameAct.this,
					getString(R.string.assembly_before));
			return;
		}

		String date = txtGameDate.getText().toString();
		if (date == null || date.length() == 0) {
			TeamAppAlerts.showMessageDialog(AddGameAct.this,
					getString(R.string.set) + " " + getString(R.string.game)
							+ " " + getString(R.string.set_date));
			return;
		}

		String time = txtGameTime.getText().toString();
		if (time == null || time.length() == 0) {
			TeamAppAlerts.showMessageDialog(AddGameAct.this,
					getString(R.string.set) + " " + getString(R.string.game)
							+ " " + getString(R.string.set_time));
			return;
		}

		calendar.set(mGameYear, mGameMonth, mGameDay, mGameHour, mGameMinute);
		gameDateTime = dateFormat.format(calendar.getTime());

		Calendar sDate = Calendar.getInstance();
		Calendar eDate = Calendar.getInstance();

		try {

			sDate.setTime(dateFormat.parse(startDate));
			eDate.setTime(dateFormat.parse(endDate));

			if (current.after(eDate)) {
				TeamAppAlerts.showToast(AddGameAct.this,
						getString(R.string.season_gone));
				return;
			}

			if (calendar.before(sDate) || calendar.after(eDate)) {
				TeamAppAlerts.showMessageDialog(AddGameAct.this,
						getString(R.string.game) + " "
								+ getString(R.string.out_of_season));
				return;
			}

			sDate.setTime(dateFormat.parse(assemblyDateTime));
			eDate.setTime(dateFormat.parse(gameDateTime));

			if (sDate.after(eDate)) {
				TeamAppAlerts.showMessageDialog(AddGameAct.this,
						getString(R.string.assembly_after));
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
			TeamAppAlerts.showMessageDialog(AddGameAct.this,
					getString(R.string.enter) + " " + getString(R.string.game)
							+ " " + getString(R.string.enter_details));
			return;
		}

		runInsert();

	}

	private boolean insertGame() throws Exception {

		String METHOD_NAME = "InsertMatch";
		String envelope = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\" xmlns:voet=\"http://schemas.datacontract.org/2004/07/VoetBall.Entities.DataObjects\">"
				+ "<soapenv:Header/>"
				+ "<soapenv:Body>"
				+ "<tem:InsertMatch>"
				+ "<tem:dMatch>"
				+ "<voet:MatchType>%s</voet:MatchType>"
				+ "<voet:MatchTypeId>%s</voet:MatchTypeId>"
				+ "<voet:AssemblyDateTime>%s</voet:AssemblyDateTime>"
				+ "<voet:AssemblyLocation>%s</voet:AssemblyLocation>"
				+ "<voet:AtHome>%s</voet:AtHome>"
				+ "<voet:Details>%s</voet:Details>"
				+ "<voet:Location>%s</voet:Location>"
				+ "<voet:LocationId>%s</voet:LocationId>"
				+ "<voet:MatchDateTime>%s</voet:MatchDateTime>"
				+ "<voet:MatchId>0</voet:MatchId>"
				+ "<voet:SeasonId>%s</voet:SeasonId>"
				+ "<voet:SeasonName>%s</voet:SeasonName>"
				+ "<voet:StatusId>%s</voet:StatusId>"
				+ "<voet:Team1Id>%s</voet:Team1Id>"
				+ "<voet:Team2Id>%s</voet:Team2Id>"
				+ "</tem:dMatch>"
				+ "<tem:clubId>%s</tem:clubId>"
				+ "</tem:InsertMatch>"
				+ "</soapenv:Body>" + "</soapenv:Envelope>";

		String request = String.format(envelope, typeName, typeId,
				assemblyDateTime, assembly.trim(), String.valueOf(atHome),
				details.trim(), location.trim(), locationId, gameDateTime,
				seasonId, seasonName, statusId, teamOne, teamTwo, myClub);

		Log.d("request", request);

		String response = (String) WebServiceHelper.callWebService(METHOD_NAME,
				request);

		Log.d("response", response);

		return WebServiceHelper.getStatus(response);

	}
	
	private void updateAssemblyTime() {
		txtAssemblyTime.setText(new StringBuilder().append(pad(mAssemblyHour))
				.append(".").append(pad(mAssemblyMinute)));
	}

	private void updateGameDate() {

		txtGameDate.setText(new StringBuilder()
				// Month is 0 based so add 1
				.append(pad(mGameDay)).append("-").append(pad(mGameMonth + 1))
				.append("-").append(mGameYear).append(" "));

	}

	private void updateGameTime() {
		txtGameTime.setText(new StringBuilder().append(pad(mGameHour))
				.append(".").append(pad(mGameMinute)));
	}

	private DatePickerDialog.OnDateSetListener mAssemblyDateSetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mAssemblyYear = year;
			mAssemblyMonth = monthOfYear;
			mAssemblyDay = dayOfMonth;
			// updateAssemblyDate();
		}
	};

	private TimePickerDialog.OnTimeSetListener mAssemblyTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			mAssemblyHour = hourOfDay;
			mAssemblyMinute = minute;
			updateAssemblyTime();
		}
	};

	private DatePickerDialog.OnDateSetListener mGameDateSetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mGameYear = year;
			mGameMonth = monthOfYear;
			mGameDay = dayOfMonth;
			updateGameDate();
		}
	};

	private TimePickerDialog.OnTimeSetListener mGameTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			mGameHour = hourOfDay;
			mGameMinute = minute;
			updateGameTime();
		}
	};

	private static String pad(int c) {
		if (c >= 10)
			return String.valueOf(c);
		else
			return "0" + String.valueOf(c);
	}

	private void runDefault() {

		pd = ProgressDialog.show(this, "", getString(R.string.initializing),
				true, false);

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case 0:
					populateItem();
					break;
				case 1:
					pd.dismiss();
					TeamAppAlerts.showToast(AddGameAct.this,
							getString(R.string.service));
					break;
				}
			}
		};

		new Thread(new Runnable() {
			public void run() {
				// TODO Auto-generated method stub
				int what = 0;
				try {
					getGameItem();
				} catch (Exception e) {
					what = 1;
					e.printStackTrace();
				}

				handler.sendEmptyMessage(what);
			}
		}).start();
	}

	private void runTeamList() {

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case 0:
					populateOpponent();
					break;
				case 1:
					TeamAppAlerts.showToast(AddGameAct.this,
							getString(R.string.service));
					break;
				}
			}
		};

		new Thread(new Runnable() {
			public void run() {
				// TODO Auto-generated method stub
				int what = 0;
				try {
					getTeamList();
				} catch (Exception e) {
					what = 1;
					e.printStackTrace();
				}

				handler.sendEmptyMessage(what);
			}
		}).start();
	}

	private void runLocationList() {

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case 0:
					pd.dismiss();
					populateLocation();
					break;
				case 1:
					TeamAppAlerts.showToast(AddGameAct.this,
							getString(R.string.service));
					break;
				}
			}
		};

		new Thread(new Runnable() {
			public void run() {
				// TODO Auto-generated method stub
				int what = 0;
				try {
					getLocationList();
				} catch (Exception e) {
					what = 1;
					e.printStackTrace();
				}

				handler.sendEmptyMessage(what);
			}
		}).start();
	}

	private void runInsert() {

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
					TeamAppAlerts.showToast(AddGameAct.this,
							getString(R.string.insert_failed) + ""
									+ getString(R.string.game));
					break;
				case 2:
					pd.dismiss();
					TeamAppAlerts.showToast(AddGameAct.this,
							getString(R.string.service));
					break;
				}
			}
		};

		pd = ProgressDialog.show(this, "", getString(R.string.wait), true,
				false);
		new Thread() {
			@Override
			public void run() {

				int what = 0;

				// Make request and handle exceptions
				try {
					if (!insertGame()) {
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

}
