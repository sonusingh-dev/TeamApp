package com.teamapp.ui.team;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import org.ksoap2.serialization.SoapObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.teamapp.apis.anim.ActivitySwitcher;
import com.teamapp.helper.Utility;
import com.teamapp.helper.WebServiceHelper;
import com.teamapp.ui.CommentsAdapter;
import com.teamapp.ui.LoginAct;
import com.teamapp.ui.R;
import com.teamapp.ui.alerts.TeamAppAlerts;

/*
 * Called once user logged in successfully and see the game details. 
 * Using android menu user can navigate to another screen 
 */
public class GameDetailsAct extends TeamMenuAct {

	private static final int ADD = 0;
	private static final int EDIT = 1;

	private static final int ACTIVITY = 3;

	private static final int ALWAYS_IN_CALENDER = 0;
	private static final int ONLY_IF_PRESENT = 1;
	private static final int NEVER_IN_CALENDER = 2;

	public static final String PREF_FILE_NAME = "voetbal";

	private boolean isCompleted;

	private int mAnim;
	private int mActIndex;

	private String mId;
	private String mType;

	private String mUserId;
	private String mTeamId;
	private String mClubId;
	private String mRoleId;

	private Calendar mCalendar;
	private DateFormat mDateFormat;

	private HashMap<String, String> mImages;
	private HashMap<String, String> mActivity;

	private ArrayList<String> mActivityList;
	private ArrayList<HashMap<String, String>> mPresentPlayerList;
	private ArrayList<HashMap<String, String>> mAbsentPlayerList;
	private ArrayList<HashMap<String, String>> mSupporterPlayerList;
	private ArrayList<HashMap<String, String>> mNotRespondedPlayerList;
	private ArrayList<HashMap<String, String>> mCommentsList;

	private TextView lblTitle;

	private TextView lblOppoClub;
	private TextView lblOppoTeam;
	private TextView lblLocation;
	private TextView lblDate;
	private TextView lblTime;
	private TextView lblAssembly;
	private TextView lblDateTimeAssembly;
	private TextView lblDetails;

	private TextView lblPresent;
	private TextView lblAbsent;
	private TextView lblSupporting;
	private TextView lblNotResponded;

	private EditText txtComments;

	private ListView lstPresent;
	private ListView lstAbsent;
	private ListView lstSupporting;
	private ListView lstNotResponded;
	private ListView lstComments;

	private ImageView btnGmap;
	private ImageView btnAGmap;
	private ImageView btnCalender;

	private ImageView btnLeft;
	private ImageView btnRight;
	private ImageView btnAdd;
	private ImageView btnEdit;
	private ImageView btnDelete;

	private Button btnAll;
	private Button btnPost;

	private ProgressBar progressBar;
	private LinearLayout bodyLayout;

	private GameDetailsAct mGame;
	private SharedPreferences mPreferences;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_details);

		mId = getIntent().getStringExtra("id");
		mAnim = getIntent().getIntExtra("anim", 0);

		mGame = this;
		mPreferences = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);

		isCompleted = false;

		sMnuIndex = 0;
		mActIndex = 0;
		mType = "Match";

		mUserId = LoginAct.userId;
		mTeamId = LoginAct.teamId;
		mClubId = LoginAct.clubId;
		mRoleId = LoginAct.roleId;

		mCalendar = Calendar.getInstance();
		mDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

		mImages = new HashMap<String, String>();
		mActivity = new HashMap<String, String>();
		mActivityList = new ArrayList<String>();
		mPresentPlayerList = new ArrayList<HashMap<String, String>>();
		mAbsentPlayerList = new ArrayList<HashMap<String, String>>();
		mSupporterPlayerList = new ArrayList<HashMap<String, String>>();
		mNotRespondedPlayerList = new ArrayList<HashMap<String, String>>();
		mCommentsList = new ArrayList<HashMap<String, String>>();

		lblTitle = (TextView) findViewById(R.id.lblTitle);

		lblOppoClub = (TextView) findViewById(R.id.lblOppoClub);
		lblOppoTeam = (TextView) findViewById(R.id.lblOppoTeam);
		lblLocation = (TextView) findViewById(R.id.lblLocation);
		lblDate = (TextView) findViewById(R.id.lblDate);
		lblTime = (TextView) findViewById(R.id.lblTime);
		lblAssembly = (TextView) findViewById(R.id.lblAssembly);
		lblDateTimeAssembly = (TextView) findViewById(R.id.lblDateTimeAssembly);
		lblDetails = (TextView) findViewById(R.id.lblDetails);

		lblPresent = (TextView) findViewById(R.id.lblPresent);
		lblAbsent = (TextView) findViewById(R.id.lblAbsent);
		lblSupporting = (TextView) findViewById(R.id.lblSupporting);
		lblNotResponded = (TextView) findViewById(R.id.lblNotResponded);

		txtComments = (EditText) findViewById(R.id.txtComments);

		lstPresent = (ListView) findViewById(R.id.lstPresent);
		lstAbsent = (ListView) findViewById(R.id.lstAbsent);
		lstSupporting = (ListView) findViewById(R.id.lstSupporting);
		lstNotResponded = (ListView) findViewById(R.id.lstNotResponded);
		lstComments = (ListView) findViewById(R.id.lstComments);

		btnGmap = (ImageView) findViewById(R.id.btnGmap);
		btnAGmap = (ImageView) findViewById(R.id.btnAGmap);
		btnCalender = (ImageView) findViewById(R.id.btnCalender);
		btnLeft = (ImageView) findViewById(R.id.btnLeft);
		btnRight = (ImageView) findViewById(R.id.btnRight);
		btnAdd = (ImageView) findViewById(R.id.btnAdd);
		btnEdit = (ImageView) findViewById(R.id.btnEdit);
		btnDelete = (ImageView) findViewById(R.id.btnDelete);

		btnAll = (Button) findViewById(R.id.btnAll);
		// btnSwitch = (Button) findViewById(R.id.btnSwitch);
		btnPost = (Button) findViewById(R.id.btnPost);

		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		bodyLayout = (LinearLayout) findViewById(R.id.bodyLayout);

		lstPresent.setDividerHeight(0);
		lstAbsent.setDividerHeight(0);
		lstSupporting.setDividerHeight(0);
		lstNotResponded.setDividerHeight(0);

		lstComments.setDivider(new ColorDrawable(0xffE6E6E6));
		lstComments.setDividerHeight(0);

		if (mRoleId.equals("4")) {
			btnAdd.setVisibility(View.INVISIBLE);
			btnEdit.setVisibility(View.INVISIBLE);
			btnDelete.setVisibility(View.INVISIBLE);
		}

		lblPresent.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {
				// TODO Auto-generated method stub
				if (!mPresentPlayerList.isEmpty() && !mRoleId.equals("4")) {
					Intent intent = new Intent().setClass(mGame,
							SendMessageAct.class);
					intent.putExtra("key", ACTIVITY);
					intent.putExtra("type", mType);
					intent.putExtra("typeId", mId);
					intent.putExtra("attendance", "Present");
					intent.putExtra("method", "NotifyPresentPlayers");
					startActivity(intent);
				}
			}
		});

		lblAbsent.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {
				// TODO Auto-generated method stub
				if (!mAbsentPlayerList.isEmpty() && !mRoleId.equals("4")) {
					Intent intent = new Intent().setClass(mGame,
							SendMessageAct.class);
					intent.putExtra("key", ACTIVITY);
					intent.putExtra("type", mType);
					intent.putExtra("typeId", mId);
					intent.putExtra("attendance", "Absent");
					intent.putExtra("method", "NotifyAbsentPlayers");
					startActivity(intent);
				}
			}
		});

		lblSupporting.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {
				// TODO Auto-generated method stub
				if (!mSupporterPlayerList.isEmpty() && !mRoleId.equals("4")) {
					Intent intent = new Intent().setClass(mGame,
							SendMessageAct.class);
					intent.putExtra("key", ACTIVITY);
					intent.putExtra("type", mType);
					intent.putExtra("typeId", mId);
					intent.putExtra("attendance", "PresentButWontPlay");
					intent.putExtra("method", "NotifySupportingPlayers");
					startActivity(intent);
				}
			}
		});

		lblNotResponded.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {
				// TODO Auto-generated method stub
				if (!mNotRespondedPlayerList.isEmpty() && !mRoleId.equals("4")) {
					Intent intent = new Intent().setClass(mGame,
							SendMessageAct.class);
					intent.putExtra("key", ACTIVITY);
					intent.putExtra("type", mType);
					intent.putExtra("typeId", mId);
					intent.putExtra("attendance", "NotResponded");
					intent.putExtra("method", "NotifyNonRespondedPlayers");
					startActivity(intent);
				}
			}
		});

		lstPresent.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				String method = "NotifyPresentPlayers";
				HashMap<String, String> player = mPresentPlayerList
						.get(position);
				showAttendancePopup(view, player, method, position);
			}
		});

		lstAbsent.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				String method = "NotifyAbsentPlayers";
				HashMap<String, String> player = mAbsentPlayerList
						.get(position);
				showAttendancePopup(view, player, method, position);
			}
		});

		lstSupporting.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				String method = "NotifySupportingPlayers";
				HashMap<String, String> player = mSupporterPlayerList
						.get(position);
				showAttendancePopup(view, player, method, position);
			}
		});

		lstNotResponded.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				String method = "NotifyNonRespondedPlayers";
				HashMap<String, String> player = mNotRespondedPlayerList
						.get(position);
				showAttendancePopup(view, player, method, position);
			}
		});

		lstComments.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				HashMap<String, String> comments = mCommentsList.get(position);
				String userId = comments.get("userId");
				String message = comments.get("message");

				if (userId.equals(mUserId)) {
					showCommentsOptionPopup(view, comments, position);
				} else {
					TeamAppAlerts.showMessageDialog(mGame, message);
				}
			}
		});

		btnAll.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {
				// TODO Auto-generated method stub
				Intent intent = new Intent().setClass(mGame,
						ActivitiesListAct.class);
				intent.putExtra("type", mType);
				startActivityForResult(intent, EDIT);
			}
		});

		btnGmap.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				String location = mActivity.get("location");
				Intent intent = new Intent().setClass(mGame, GMapAct.class);
				intent.putExtra("location", location);
				startActivity(intent);
			}
		});

		btnAGmap.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				String assemblyLocation = mActivity.get("assemblyLocation");
				Intent intent = new Intent().setClass(mGame, GMapAct.class);
				intent.putExtra("location", assemblyLocation);
				startActivity(intent);
			}
		});

		btnCalender.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				checkCalSetting();
				// addToCalendar();
			}
		});

		btnPost.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub

				// Hide android keyboard
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(txtComments.getWindowToken(), 0);

				String message = txtComments.getText().toString();
				if (message.length() == 0) {
					// VoetBalAlerts.showToast(mGame, "Enter some comments...");
					return;
				}

				runPostComments(message);
			}
		});

		btnLeft.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isCompleted) {
					if (mActIndex > 0) {
						mActIndex--;
					} else {
						mActIndex = mActivityList.size() - 1;
					}
					runActivity();
				}
			}
		});

		btnRight.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isCompleted) {
					if (mActIndex < mActivityList.size() - 1) {
						mActIndex++;
					} else {
						mActIndex = 0;
					}
					runActivity();
				}
			}
		});

		btnAdd.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isCompleted) {
					Intent intent = new Intent().setClass(mGame,
							AddGameAct.class);
					startActivityForResult(intent, ADD);
				}
			}
		});

		btnEdit.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isCompleted) {
					Intent intent = new Intent().setClass(mGame,
							EditGameAct.class);
					intent.putExtra("id", mId);
					startActivityForResult(intent, EDIT);
				}
			}
		});

		btnDelete.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isCompleted) {
					showDeletionDialog(null, 0, 0);
				}
			}
		});

		runDefault();

	}

	// @Override
	// public void finish() {
	// // we need to override this to performe the animtationOut on each
	// // finish.
	// ActivitySwitcher.animationOut(findViewById(R.id.container),
	// getWindowManager(),
	// new ActivitySwitcher.AnimationFinishedListener() {
	// public void onAnimationFinished() {
	// super.finish();
	// // disable default animation
	// overridePendingTransition(0, 0);
	// }
	// });
	// }

	@Override
	protected void onResume() {
		// animateIn this activity
		if (mAnim == 1) {
			ActivitySwitcher.animationLeftOut(findViewById(R.id.mainLayout),
					getWindowManager());
		}
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		mAnim = 0;
		super.onPause();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case ADD:
				runDefault();
				break;
			case EDIT:
				runActivity();
				break;
			}
		}
	}

	// populate the fields with data.
	private void populateActivity() {

		String title = mActivity.get("title");
		String oppoClub = mActivity.get("oppoClub");
		String oppoTeam = mActivity.get("oppoTeam");
		String dateTime = mActivity.get("dateTime");
		String location = mActivity.get("location");
		String assemblyDateTime = mActivity.get("assemblyDateTime");
		String assemblyLocation = mActivity.get("assemblyLocation");
		String details = mActivity.get("details");

		lblTitle.setText(title);

		lblOppoClub.setText(oppoClub);

		lblOppoTeam.setText(oppoTeam);

		try {

			mCalendar.setTime(mDateFormat.parse(dateTime));

			int year = mCalendar.get(Calendar.YEAR);
			int day = mCalendar.get(Calendar.DAY_OF_MONTH);
			int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
			int minute = mCalendar.get(Calendar.MINUTE);

			String month = String.format("%tb", mCalendar);

			lblDate.setText(new StringBuilder().append(Utility.pad(day))
					.append(" ").append(month).append(" ").append(year));
			lblTime.setText(new StringBuilder().append(hour).append(":")
					.append(Utility.pad(minute)));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		lblLocation.setText(location);

		lblAssembly.setText(assemblyLocation);

		try {

			mCalendar.setTime(mDateFormat.parse(assemblyDateTime));

			// int year = mCalendar.get(Calendar.YEAR);
			// int day = mCalendar.get(Calendar.DAY_OF_MONTH);
			int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
			int minute = mCalendar.get(Calendar.MINUTE);

			// String month = String.format("%tb", mCalendar);

			lblDateTimeAssembly.setText(new StringBuilder().append(hour)
					.append(":").append(Utility.pad(minute)));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		lblDetails.setText(Utility.stipHtml(details));

	}

	// populate the attendance list with data
	private void populateAttendance() {

		lblPresent.setText(getString(R.string.activity_present) + "("
				+ mPresentPlayerList.size() + ")");
		lblAbsent.setText(getString(R.string.activity_absent) + "("
				+ mAbsentPlayerList.size() + ")");
		lblSupporting.setText(getString(R.string.activity_supporter) + "("
				+ mSupporterPlayerList.size() + ")");
		lblNotResponded.setText(getString(R.string.activity_not_responded)
				+ "(" + mNotRespondedPlayerList.size() + "): ");

		lstPresent.setAdapter(new SimpleAdapter(mGame, mPresentPlayerList,
				R.layout.player_present, new String[] { "userName" },
				new int[] { R.id.lblPresent }));

		lstAbsent.setAdapter(new SimpleAdapter(mGame, mAbsentPlayerList,
				R.layout.player_absent, new String[] { "userName" },
				new int[] { R.id.lblAbsent }));

		lstSupporting.setAdapter(new SimpleAdapter(mGame, mSupporterPlayerList,
				R.layout.player_supporting, new String[] { "userName" },
				new int[] { R.id.lblSupporting }));

		lstNotResponded
				.setAdapter(new SimpleAdapter(mGame, mNotRespondedPlayerList,
						R.layout.player_not_responded,
						new String[] { "userName" },
						new int[] { R.id.lblNotResponded }));

		Utility.setListViewHeightBasedOnChildren(lstPresent);
		Utility.setListViewHeightBasedOnChildren(lstAbsent);
		Utility.setListViewHeightBasedOnChildren(lstSupporting);
		Utility.setListViewHeightBasedOnChildren(lstNotResponded);

	}

	private void populateComments() {

		HashMap<String, String> comment = null;
		CommentsAdapter mAdapter = new CommentsAdapter(mGame, mImages);

		for (int i = 0; i < mCommentsList.size(); i++) {

			comment = mCommentsList.get(i);
			String userId = comment.get("userId");

			if (userId.equals(mUserId)) {
				mAdapter.addItem(comment);
			} else {
				mAdapter.addSeparatorItem(comment);
			}
		}

		lstComments.setAdapter(mAdapter);

		Utility.setListViewHeightBasedOnChildren(lstComments);
	}

	// check the calendar setting and accordingly add to the calendar
	private void checkCalSetting() {

		int calSetting = mPreferences.getInt("Game", 0);

		switch (calSetting) {

		case ALWAYS_IN_CALENDER:
			break;
		case ONLY_IF_PRESENT:
			boolean isPresent = false;
			for (int i = 0; i < mPresentPlayerList.size(); i++) {
				HashMap<String, String> player = mPresentPlayerList.get(i);
				String userId = player.get("userId");
				if (mUserId.equals(userId)) {
					isPresent = true;
				}
			}

			if (!isPresent) {
				TeamAppAlerts.showMessageDialog(mGame,
						getString(R.string.calendar_add),
						getString(R.string.calendar_change));
				return;
			}
			break;
		case NEVER_IN_CALENDER:
			TeamAppAlerts.showMessageDialog(mGame,
					getString(R.string.calendar_add),
					getString(R.string.calendar_change));
			return;
		}

		if (Utility.getEventFromCalendar(mGame, mActivity)) {
			TeamAppAlerts.showMessageDialog(mGame,
					getString(R.string.calendar_already));
			return;
		}

		if (Utility.addEventToCalendar(mGame, mActivity)) {
			TeamAppAlerts.showMessageDialog(mGame,
					getString(R.string.calendar_success));
		} else {
			TeamAppAlerts.showMessageDialog(mGame,
					getString(R.string.calendar_failed));
		}

	}

	// Calls webservice to get the activities list.
	private void getActivities() throws Exception {

		String METHOD_NAME = "GetActivitiesListByTeamIdOrderByMonth";

		mActivityList.clear();
		SoapObject request = WebServiceHelper.getSOAPRequest(METHOD_NAME);
		request.addProperty("bTask", false);
		request.addProperty("bTraining", false);
		request.addProperty("bEvent", false);
		request.addProperty("bMatch", true);
		request.addProperty("myUserId", mUserId);
		request.addProperty("teamId", mTeamId);
		request.addProperty("clubId", mClubId);

		SoapObject response = (SoapObject) WebServiceHelper.getSOAPResponse(
				request, METHOD_NAME);

		if (response != null) {

			Calendar current = Calendar.getInstance();

			for (int i = 0; i < response.getPropertyCount(); i++) {

				SoapObject property = (SoapObject) response.getProperty(i);
				SoapObject activities = (SoapObject) property
						.getProperty("Activities");

				if (activities != null) {

					for (int j = 0; j < activities.getPropertyCount(); j++) {

						SoapObject soapActivity = (SoapObject) activities
								.getProperty(j);
						String id = soapActivity.getProperty("Id").toString();
						String dateTime = soapActivity.getProperty(
								"ActivityDateTime").toString();

						mActivityList.add(id);

						if (mId != null) {
							if (mId.equals(id)) {
								mActIndex = mActivityList.size() - 1;
							}
						} else {
							mCalendar.setTime(mDateFormat.parse(dateTime));
							if (current.after(mCalendar)) {
								mActIndex = mActIndex + 1;
							}
						}
					}
				}
			}
		}
	}

	// calls webservice to get the complete details of an activity.
	private void getActivityDetails() throws Exception {

		String METHOD_NAME = "GetMatchDetailsByMatchIdTeamId";

		if (mActIndex >= mActivityList.size()) {
			mActIndex = mActivityList.size() - 1;
		}

		mId = mActivityList.get(mActIndex);

		mActivity.clear();
		mPresentPlayerList.clear();
		mAbsentPlayerList.clear();
		mSupporterPlayerList.clear();
		mNotRespondedPlayerList.clear();
		SoapObject request = WebServiceHelper.getSOAPRequest(METHOD_NAME);
		request.addProperty("matchId", mId);
		request.addProperty("teamId", mTeamId);
		request.addProperty("clubId", mClubId);
		SoapObject response = (SoapObject) WebServiceHelper.getSOAPResponse(
				request, METHOD_NAME);

		if (response != null) {

			String subType = response.getProperty("MatchType").toString()
					.trim();

			String assemblyDateTime = response.getProperty("AssemblyDateTime")
					.toString();
			String assemblyLocation = null;
			if (response.getProperty("AssemblyLocation") != null) {
				assemblyLocation = response.getProperty("AssemblyLocation")
						.toString().trim();
			}

			String location = null;
			if (response.getProperty("Location") != null) {
				location = response.getProperty("Location").toString().trim();
			}
			String dateTime = response.getProperty("MatchDateTime").toString();

			String details = null;
			if (response.getProperty("Details") != null) {
				details = response.getProperty("Details").toString().trim();
			}

			String result = null;
			if (response.getProperty("Result") != null) {
				result = response.getProperty("Result").toString().trim();
			}

			String season = response.getProperty("SeasonName").toString()
					.trim();

			String oppoClub = response.getProperty("OppositeClubName")
					.toString().trim();

			String oppoTeam = response.getProperty("OppositeTeamName")
					.toString().trim();

			String title = null;
			if (response.getProperty("Title") != null) {
				title = response.getProperty("Title").toString().trim();
			}

			mActivity.put("subType", subType);
			mActivity.put("assemblyDateTime", assemblyDateTime);
			mActivity.put("assemblyLocation", assemblyLocation);
			mActivity.put("location", location);
			mActivity.put("dateTime", dateTime);
			mActivity.put("details", details);
			mActivity.put("result", result);
			mActivity.put("season", season);
			mActivity.put("oppoClub", oppoClub);
			mActivity.put("oppoTeam", oppoTeam);
			mActivity.put("title", title);

			SoapObject soapPlayers = (SoapObject) response
					.getProperty("Players");

			if (soapPlayers != null) {

				for (int j = 0; j < soapPlayers.getPropertyCount(); j++) {

					SoapObject soapPlayer = (SoapObject) soapPlayers
							.getProperty(j);
					String userId = soapPlayer.getProperty("UserId").toString();
					String userName = soapPlayer.getProperty("Name").toString();
					String attendanceId = soapPlayer
							.getProperty("AttendanceId").toString();

					HashMap<String, String> player = new HashMap<String, String>();
					player.put("userId", userId);
					player.put("userName", userName);
					player.put("attendanceId", attendanceId);

					if (attendanceId.equals("0")) {
						mNotRespondedPlayerList.add(player);
					} else if (attendanceId.equals("1")) {
						mPresentPlayerList.add(player);
					} else if (attendanceId.equals("2")) {
						mSupporterPlayerList.add(player);
					} else if (attendanceId.equals("3")) {
						mAbsentPlayerList.add(player);
					}
				}
			}
		}
	}

	// calls webservice to get comments for the activity.
	private void getComments() throws Exception {

		String METHOD_NAME = "GetComments";

		mCommentsList.clear();
		SoapObject request = WebServiceHelper.getSOAPRequest(METHOD_NAME);
		request.addProperty("type", mType);
		request.addProperty("CommentTypeId", mId);
		request.addProperty("clubId", mClubId);
		SoapObject response = (SoapObject) WebServiceHelper.getSOAPResponse(
				request, METHOD_NAME);

		if (response != null) {

			SoapObject sopComment = null;

			for (int i = 0; i < response.getPropertyCount(); i++) {
				sopComment = (SoapObject) response.getProperty(i);

				String userName = sopComment.getProperty("Name").toString();
				String userId = sopComment.getProperty("UserId").toString();

				if (sopComment.getProperty("Image") != null) {
					String image = sopComment.getProperty("Image").toString();
					mImages.put(userId, image);
				}

				String message = sopComment.getProperty("Comment").toString();
				if (message.contains("anyType{}")) {
					message = null;
				}

				String commentId = sopComment.getProperty("CommentId")
						.toString();
				String commentTypeId = sopComment.getProperty("CommentTypeId")
						.toString();
				String dateTime = sopComment.getProperty("CommentedOn")
						.toString();
				String type = sopComment.getProperty("Type").toString();

				HashMap<String, String> comment = new HashMap<String, String>();
				comment.put("userName", userName);
				comment.put("userId", userId);
				comment.put("message", message);
				comment.put("commentId", commentId);
				comment.put("dateTime", dateTime);
				comment.put("commentTypeId", commentTypeId);
				comment.put("type", type);
				mCommentsList.add(comment);
			}
		}
	}

	private void setAttendance(HashMap<String, String> player, String attTo,
			int index) {

		String userId = player.get("userId");
		String attFrom = player.get("attendanceId");

		String METHOD_NAME = "SetActivityAttendance";

		SoapObject request = WebServiceHelper.getSOAPRequest(METHOD_NAME);
		request.addProperty("activityId", mId);
		request.addProperty("activityType", mType);
		request.addProperty("attendanceId", attTo);
		request.addProperty("userId", userId);
		request.addProperty("teamId", mTeamId);
		request.addProperty("clubId", mClubId);

		try {
			Object response = WebServiceHelper.getSOAPResponse(request,
					METHOD_NAME);
			boolean status = Boolean.parseBoolean(response.toString());
			if (status) {

				if (attFrom.equals("0")) {
					mNotRespondedPlayerList.remove(index);
				} else if (attFrom.equals("1")) {
					mPresentPlayerList.remove(index);
				} else if (attFrom.equals("2")) {
					mSupporterPlayerList.remove(index);
				} else if (attFrom.equals("3")) {
					mAbsentPlayerList.remove(index);
				}

				player.put("attendanceId", attTo);

				if (attTo.equals("1")) {
					mPresentPlayerList.add(player);
				} else if (attTo.equals("2")) {
					mSupporterPlayerList.add(player);
				} else if (attTo.equals("3")) {
					mAbsentPlayerList.add(player);
				}

				populateAttendance();

			} else {
				TeamAppAlerts.showMessageDialog(mGame,
						getString(R.string.update_failed) + " "
								+ getString(R.string.attendance));
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			TeamAppAlerts.showMessageDialog(mGame,
					getString(R.string.update_failed) + " "
							+ getString(R.string.attendance));
			e.printStackTrace();
		}
	}

	private boolean deleteActivity() throws Exception {

		String METHOD_NAME = "DeleteActivity";

		SoapObject request = WebServiceHelper.getSOAPRequest(METHOD_NAME);
		request.addProperty("activityId", mId);
		request.addProperty("activityType", mType);
		request.addProperty("clubId", mClubId);
		Object response = WebServiceHelper
				.getSOAPResponse(request, METHOD_NAME);

		String status = response.toString();
		return Boolean.parseBoolean(status);
	}

	private boolean sendNotification(String message, String method)
			throws Exception {

		SoapObject request = WebServiceHelper.getSOAPRequest(method);
		request.addProperty("message", message);
		request.addProperty("type", mType);
		request.addProperty("typeId", mId);
		request.addProperty("userId", mUserId);
		request.addProperty("teamId", mTeamId);
		request.addProperty("clubId", mClubId);

		Object response = WebServiceHelper.getSOAPResponse(request, method);

		String status = response.toString();
		return Boolean.parseBoolean(status);
	}

	private boolean postComments(String message) throws Exception {

		String METHOD_NAME = "InsertComment";
		String envelope = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\" xmlns:voet=\"http://schemas.datacontract.org/2004/07/VoetBall.Entities.DataObjects\">"
				+ "<soapenv:Header/>"
				+ "<soapenv:Body>"
				+ "<tem:InsertComment>"
				+ "<tem:comment>"
				+ "<voet:UserId>%s</voet:UserId>"
				+ "<voet:Comment>%s</voet:Comment>"
				+ "<voet:CommentId>%s</voet:CommentId>"
				+ "<voet:CommentTypeId>%s</voet:CommentTypeId>"
				+ "<voet:Type>%s</voet:Type>"
				+ "</tem:comment>"
				+ "<tem:clubId>%s</tem:clubId>"
				+ "</tem:InsertComment>"
				+ "</soapenv:Body>" + "</soapenv:Envelope>";

		String request = String.format(envelope, mUserId, message, "0", mId,
				mType, mClubId);

		String response = (String) WebServiceHelper.callWebService(METHOD_NAME,
				request);

		return WebServiceHelper.getStatus(response);

	}

	private boolean editComments(HashMap<String, String> comments)
			throws Exception {

		String METHOD_NAME = "UpdateComment";
		String envelope = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\" xmlns:voet=\"http://schemas.datacontract.org/2004/07/VoetBall.Entities.DataObjects\">"
				+ "<soapenv:Header/>"
				+ "<soapenv:Body>"
				+ "<tem:UpdateComment>"
				+ "<tem:comment>"
				+ "<voet:UserId>%s</voet:UserId>"
				+ "<voet:Comment>%s</voet:Comment>"
				+ "<voet:CommentId>%s</voet:CommentId>"
				+ "<voet:CommentTypeId>%s</voet:CommentTypeId>"
				+ "<voet:CommentedOn>%s</voet:CommentedOn>"
				+ "<voet:Type>%s</voet:Type>"
				+ "</tem:comment>"
				+ "<tem:clubId>%s</tem:clubId>"
				+ "</tem:UpdateComment>"
				+ "</soapenv:Body>" + "</soapenv:Envelope>";

		String request = String.format(envelope, comments.get("userId"),
				comments.get("message"), comments.get("commentId"),
				comments.get("commentTypeId"), comments.get("dateTime"),
				comments.get("type"), mClubId);

		String response = (String) WebServiceHelper.callWebService(METHOD_NAME,
				request);

		return WebServiceHelper.getStatus(response);

	}

	private boolean deleteComments(String commentId) throws Exception {

		String METHOD_NAME = "DeleteComment";

		SoapObject request = WebServiceHelper.getSOAPRequest(METHOD_NAME);
		request.addProperty("commentId", commentId);
		request.addProperty("type", mType);
		request.addProperty("clubId", mClubId);
		Object response = WebServiceHelper
				.getSOAPResponse(request, METHOD_NAME);

		String status = response.toString();
		return Boolean.parseBoolean(status);

	}

	private void showDeletionDialog(final String id, final int index,
			final int key) {

		AlertDialog.Builder optionBuilder = new AlertDialog.Builder(mGame);
		optionBuilder.setMessage(getString(R.string.delete_sure));
		optionBuilder.setPositiveButton(getString(R.string.yes),
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// Do nothing but close the dialog
						switch (key) {
						case 0:
							runDeleteActivity();
							break;
						case 1:
							runDeleteComments(index, id);
							break;
						}

					}
				});

		optionBuilder.setNegativeButton(getString(R.string.no),
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// Do nothing
					}
				});

		// Remember, create doesn't show the dialog
		AlertDialog helpDialog = optionBuilder.create();
		helpDialog.show();
	}

	private void showEditCommentsDialog(final HashMap<String, String> comments,
			final int index) {

		final EditText txtComments = new EditText(mGame);
		txtComments.setLines(4);
		txtComments.setGravity(Gravity.TOP);
		txtComments.setText(comments.get("message"));

		AlertDialog.Builder builder = new AlertDialog.Builder(mGame);
		builder.setView(txtComments);
		builder.setTitle(getString(R.string.edit) + " "
				+ getString(R.string.comments));

		builder.setPositiveButton(getString(R.string.update),
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// Do nothing but close the dialog
						String message = txtComments.getText().toString();
						if (message.length() == 0) {
							// VoetBalAlerts.showToast(mEvents,
							// "Enter comments");
							return;
						}

						comments.put("message", message);
						runEditComments(comments, index);
					}
				});

		builder.setNegativeButton(getString(R.string.cancel),
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// Do nothing
					}
				});

		// Remember, create doesn't show the dialog
		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}

	private void showSendNotificationDialog(final String method) {

		if (mRoleId.equals("4")) {
			return;
		}

		final EditText txtComments = new EditText(mGame);
		txtComments.setLines(4);
		txtComments.setGravity(Gravity.TOP);

		AlertDialog.Builder builder = new AlertDialog.Builder(mGame);
		builder.setView(txtComments);
		builder.setTitle("Send Message");

		builder.setPositiveButton("Send",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// Do nothing but close the dialog
						String message = txtComments.getText().toString();
						if (message.length() == 0) {
							TeamAppAlerts.showToast(mGame, "Enter Message");
							return;
						}

						runSendNotification(message, method);
					}
				});

		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// Do nothing
					}
				});

		// Remember, create doesn't show the dialog
		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}

	// PopupWindow for Activity Attendance can be set by Captain
	private void showAttendancePopup(final View view,
			final HashMap<String, String> player, final String method,
			final int index) {

		String userId = player.get("userId");

		if (mRoleId.equals("4") && !userId.equals(mUserId)) {
			return;
		}

		Calendar calendar = Calendar.getInstance();
		if (calendar.after(mCalendar)) {
			return;
		}

		LayoutInflater inflater = LayoutInflater.from(mGame);
		View popupView = inflater.inflate(R.layout.activity_attendance_popup,
				null);
		popupView.measure(View.MeasureSpec.UNSPECIFIED,
				View.MeasureSpec.UNSPECIFIED);

		Display display = getWindowManager().getDefaultDisplay();
		int width = display.getWidth();
		int height = popupView.getMeasuredHeight() + 50;

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
				setAttendance(player, "1", index);
			}
		});

		btnAbsent.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				popupWindow.dismiss();
				setAttendance(player, "3", index);
			}
		});

		btnSupporting.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				popupWindow.dismiss();
				setAttendance(player, "2", index);
			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				popupWindow.dismiss();
			}
		});
	}

	private void showCommentsOptionPopup(View view,
			final HashMap<String, String> comments, final int index) {

		LayoutInflater inflater = LayoutInflater.from(mGame);
		View popupView = inflater.inflate(R.layout.comments_option_popup, null);
		popupView.measure(View.MeasureSpec.UNSPECIFIED,
				View.MeasureSpec.UNSPECIFIED);

		Display display = getWindowManager().getDefaultDisplay();
		int width = display.getWidth();
		int height = popupView.getMeasuredHeight() + 50;

		final PopupWindow popupWindow = new PopupWindow(popupView, width,
				height);
		popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setTouchable(true);

		Button btnDelete = (Button) popupView.findViewById(R.id.btnDelete);
		Button btnEdit = (Button) popupView.findViewById(R.id.btnEdit);
		Button btnCancel = (Button) popupView.findViewById(R.id.btnCancel);

		btnDelete.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				popupWindow.dismiss();
				showDeletionDialog(comments.get("commentId"), index, 1);
			}
		});

		btnEdit.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				popupWindow.dismiss();
				showEditCommentsDialog(comments, index);
			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				popupWindow.dismiss();
			}
		});
	}

	private void runDefault() {

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case 0:
					runActivity();
					break;
				case 1:
					progressBar.setVisibility(View.GONE);
					TeamAppAlerts.showToast(mGame, getString(R.string.service));
					break;
				}
			}
		};

		bodyLayout.setVisibility(View.GONE);
		progressBar.setVisibility(View.VISIBLE);
		new Thread(new Runnable() {
			public void run() {
				// TODO Auto-generated method stub
				int what = 0;
				try {
					getActivities();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					what = 1;
					e.printStackTrace();
				}

				handler.sendMessage(handler.obtainMessage(what));
			}

		}).start();
	}

	private void runActivity() {

		isCompleted = false;
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case 0:
					runComments();
					populateActivity();
					populateAttendance();
					bodyLayout.setVisibility(View.VISIBLE);
					break;
				case 1:
					progressBar.setVisibility(View.GONE);
					TeamAppAlerts.showToast(mGame, getString(R.string.no) + " "
							+ getString(R.string.game) + " "
							+ getString(R.string.available));
					isCompleted = true;
					break;
				case 2:
					progressBar.setVisibility(View.GONE);
					TeamAppAlerts.showToast(mGame, getString(R.string.service));
					isCompleted = true;
					break;
				}

			}
		};

		bodyLayout.setVisibility(View.GONE);
		progressBar.setVisibility(View.VISIBLE);
		new Thread(new Runnable() {
			public void run() {
				// TODO Auto-generated method stub
				int what = 0;
				try {
					if (mActivityList.isEmpty()) {
						what = 1;
					} else {
						getActivityDetails();
						getComments();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					what = 2;
					e.printStackTrace();
				}

				handler.sendMessage(handler.obtainMessage(what));
			}
		}).start();
	}

	private void runComments() {

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case 0:
					populateComments();
					progressBar.setVisibility(View.GONE);
					isCompleted = true;
					break;
				case 1:
					progressBar.setVisibility(View.GONE);
					TeamAppAlerts.showToast(mGame, getString(R.string.service));
					isCompleted = true;
					break;
				}

			}
		};

		new Thread() {
			@Override
			public void run() {

				int what = 0;

				try {
					getComments();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					what = 1;
					e.printStackTrace();
				}

				handler.sendEmptyMessage(what);

			}
		}.start();

	}

	private void runDeleteActivity() {

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case 0:
					mActivityList.remove(mActIndex);
					runActivity();
					break;
				case 1:
					progressBar.setVisibility(View.GONE);
					TeamAppAlerts.showToast(mGame,
							getString(R.string.delete_failed) + " "
									+ getString(R.string.game));
					break;
				case 2:
					progressBar.setVisibility(View.GONE);
					TeamAppAlerts.showToast(mGame, getString(R.string.service));
					break;
				}
			}
		};

		TeamAppAlerts.showToast(mGame, getString(R.string.deleting));
		progressBar.setVisibility(View.VISIBLE);
		new Thread() {
			@Override
			public void run() {

				int what = 0;

				try {
					if (!deleteActivity()) {
						what = 1;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					what = 2;
					e.printStackTrace();
				}

				handler.sendMessage(handler.obtainMessage(what));

			}
		}.start();

	}

	private void runSendNotification(final String message, final String method) {

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case 1:
					TeamAppAlerts.showToast(mGame,
							getString(R.string.send_failed) + " "
									+ getString(R.string.notification));
					break;
				case 2:
					TeamAppAlerts.showToast(mGame, getString(R.string.service));
					break;
				}
			}
		};

		TeamAppAlerts.showToast(mGame, getString(R.string.sending));
		new Thread() {
			@Override
			public void run() {

				int what = 0;

				try {
					if (!sendNotification(message, method)) {
						what = 1;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					what = 2;
					e.printStackTrace();
				}

				handler.sendMessage(handler.obtainMessage(what));

			}
		}.start();

	}

	private void runPostComments(final String message) {

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case 0:
					txtComments.setText(null);
					runComments();
					break;
				case 1:
					TeamAppAlerts.showToast(mGame,
							getString(R.string.post_failed) + " "
									+ getString(R.string.comments));
					break;
				case 2:
					TeamAppAlerts.showToast(mGame, getString(R.string.service));
					break;
				}

			}
		};

		TeamAppAlerts.showToast(mGame, getString(R.string.posting));
		new Thread() {
			@Override
			public void run() {

				int what = 0;

				try {
					if (!postComments(message)) {
						what = 1;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					what = 2;
					e.printStackTrace();
				}

				handler.sendMessage(handler.obtainMessage(what));

			}
		}.start();

	}

	private void runEditComments(final HashMap<String, String> comments,
			final int index) {

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case 0:
					mCommentsList.set(index, comments);
					populateComments();
					break;
				case 1:
					TeamAppAlerts.showToast(mGame,
							getString(R.string.update_failed) + " "
									+ getString(R.string.comments));
					break;
				case 2:
					TeamAppAlerts.showToast(mGame, getString(R.string.service));
					break;
				}
			}
		};

		new Thread() {
			@Override
			public void run() {

				int what = 0;

				try {
					if (!editComments(comments)) {
						what = 1;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					what = 2;
					e.printStackTrace();
				}

				handler.sendMessage(handler.obtainMessage(what));

			}
		}.start();

	}

	private void runDeleteComments(final int index, final String id) {

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case 0:
					mCommentsList.remove(index);
					populateComments();
					break;
				case 1:
					TeamAppAlerts.showToast(mGame,
							getString(R.string.delete_failed) + " "
									+ getString(R.string.comments));
					break;
				case 2:
					TeamAppAlerts.showToast(mGame, getString(R.string.service));
					break;
				}
			}
		};

		TeamAppAlerts.showToast(mGame, getString(R.string.deleting));
		new Thread() {
			@Override
			public void run() {

				int what = 0;
				try {
					if (!deleteComments(id)) {
						what = 1;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					what = 2;
					e.printStackTrace();
				}

				handler.sendMessage(handler.obtainMessage(what));

			}
		}.start();

	}

}