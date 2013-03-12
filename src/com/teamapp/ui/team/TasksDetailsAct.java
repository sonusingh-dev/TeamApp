package com.teamapp.ui.team;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import org.ksoap2.serialization.SoapObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.teamapp.helper.WebServiceHelper;
import com.teamapp.helper.Utility;
import com.teamapp.ui.LoginAct;
import com.teamapp.ui.R;
import com.teamapp.ui.alerts.TeamAppAlerts;

public class TasksDetailsAct extends TeamMenuAct {

	private static final int ADD = 0;
	private static final int EDIT = 1;

	private static final int ALWAYS_IN_CALENDER = 0;
	private static final int ONLY_IF_PRESENT = 1;
	private static final int NEVER_IN_CALENDER = 2;

	public static final String PREF_FILE_NAME = "voetbal";

	private boolean isCompleted;

	private int mIndex;

	private String mId;
	private String mType;

	private String mUserId;
	private String mTeamId;
	private String mClubId;
	private String mRoleId;

	private Calendar mCalendar;
	private SimpleDateFormat mDateFormat;

	private HashMap<String, String> mActivity;
	private ArrayList<String> mActivityList;
	private ArrayList<HashMap<String, String>> mPresentPlayerList;

	private TextView lblTitle;

	private TextView lblType;
	private TextView lblDate;
	private TextView lblTime;
	private TextView lblDetails;
	private TextView lblPresent;

	private ListView lstPresent;

	private ImageView btnCalender;

	private ImageView btnLeft;
	private ImageView btnRight;
	private ImageView btnAdd;
	private ImageView btnEdit;
	private ImageView btnDelete;

	private Button btnAll;

	private ProgressBar progressBar;
	private LinearLayout bodyLayout;

	private TasksDetailsAct mTasks;
	private SharedPreferences mPreferences;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tasks_details);

		mTasks = this;
		mPreferences = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);

		isCompleted = false;

		sMnuIndex = 2;
		mIndex = 0;
		mType = "Task";

		mUserId = LoginAct.userId;
		mTeamId = LoginAct.teamId;
		mClubId = LoginAct.clubId;
		mRoleId = LoginAct.roleId;

		mId = getIntent().getStringExtra("id");

		mCalendar = Calendar.getInstance();
		mDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

		mActivity = new HashMap<String, String>();
		mActivityList = new ArrayList<String>();
		mPresentPlayerList = new ArrayList<HashMap<String, String>>();

		lblTitle = (TextView) findViewById(R.id.lblTitle);

		lblType = (TextView) findViewById(R.id.lblType);
		lblDate = (TextView) findViewById(R.id.lblDate);
		lblTime = (TextView) findViewById(R.id.lblTime);
		lblDetails = (TextView) findViewById(R.id.lblDetails);
		lblPresent = (TextView) findViewById(R.id.lblPresent);

		lstPresent = (ListView) findViewById(R.id.lstPresent);

		btnCalender = (ImageView) findViewById(R.id.btnCalender);
		btnLeft = (ImageView) findViewById(R.id.btnLeft);
		btnRight = (ImageView) findViewById(R.id.btnRight);
		btnAdd = (ImageView) findViewById(R.id.btnAdd);
		btnEdit = (ImageView) findViewById(R.id.btnEdit);
		btnDelete = (ImageView) findViewById(R.id.btnDelete);

		btnAll = (Button) findViewById(R.id.btnAll);

		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		bodyLayout = (LinearLayout) findViewById(R.id.bodyLayout);

		lstPresent.setDividerHeight(0);

		if (mRoleId.equals("4")) {
			btnAdd.setVisibility(View.INVISIBLE);
			btnEdit.setVisibility(View.INVISIBLE);
			btnDelete.setVisibility(View.INVISIBLE);
		}

		btnAll.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {
				// TODO Auto-generated method stub
				Intent intent = new Intent().setClass(TasksDetailsAct.this,
						ActivitiesListAct.class);
				intent.putExtra("type", mType);
				startActivityForResult(intent, EDIT);
			}
		});

		btnCalender.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				checkCalSetting();
				// addToCalendar();
			}
		});

		btnLeft.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isCompleted) {
					if (mIndex > 0) {
						mIndex--;
					} else {
						mIndex = mActivityList.size() - 1;
					}
					runActivity();
				}
			}
		});

		btnRight.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isCompleted) {
					if (mIndex < mActivityList.size() - 1) {
						mIndex++;
					} else {
						mIndex = 0;
					}
					runActivity();
				}
			}
		});

		btnAdd.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isCompleted) {
					Intent intent = new Intent().setClass(TasksDetailsAct.this,
							AddTasksAct.class);
					startActivityForResult(intent, ADD);
				}
			}
		});

		btnEdit.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isCompleted) {
					Intent intent = new Intent().setClass(TasksDetailsAct.this,
							EditTasksAct.class);
					intent.putExtra("id", mId);
					startActivityForResult(intent, EDIT);
				}
			}
		});

		btnDelete.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isCompleted) {
					showDeletionDialog();
				}
			}
		});

		runDefault();

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
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

	private void checkCalSetting() {

		int calSetting = mPreferences.getInt("Task", 0);

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
				TeamAppAlerts.showMessageDialog(mTasks,
						getString(R.string.calendar_add),
						getString(R.string.calendar_change));
				return;
			}
			break;
		case NEVER_IN_CALENDER:
			TeamAppAlerts.showMessageDialog(mTasks,
					getString(R.string.calendar_add),
					getString(R.string.calendar_change));
			return;
		}

		if (Utility.getEventFromCalendar(mTasks, mActivity)) {
			TeamAppAlerts.showMessageDialog(mTasks,
					getString(R.string.calendar_already));
			return;
		}

		if (Utility.addEventToCalendar(mTasks, mActivity)) {
			TeamAppAlerts.showMessageDialog(mTasks,
					getString(R.string.calendar_success));
		} else {
			TeamAppAlerts.showMessageDialog(mTasks,
					getString(R.string.calendar_failed));
		}

	}

	private void populateActivity() {

		String type = mActivity.get("subType");
		String dateTime = mActivity.get("dateTime");
		String details = mActivity.get("details");

		try {

			mCalendar.setTime(mDateFormat.parse(dateTime));

			int year = mCalendar.get(Calendar.YEAR);
			int day = mCalendar.get(Calendar.DAY_OF_MONTH);
			int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
			int minute = mCalendar.get(Calendar.MINUTE);

			String month = String.format("%tb", mCalendar);

			lblDate.setText(new StringBuilder().append(Utility.pad(day))
					.append(" ").append(month).append(" ")
					.append(year));
			lblTime.setText(new StringBuilder().append(hour).append(":")
					.append(Utility.pad(minute)));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		lblType.setText(type);

		lblDetails.setText(Utility.stipHtml(details));

	}

	private void populateAttendance() {

		lblPresent.setText(getString(R.string.activity_accepted) + "("
				+ mPresentPlayerList.size() + ")");

		lstPresent.setAdapter(new SimpleAdapter(this, mPresentPlayerList,
				R.layout.player_present, new String[] { "userName" },
				new int[] { R.id.lblPresent }));

		Utility.setListViewHeightBasedOnChildren(lstPresent);
	}

	private void getActivities() throws Exception {

		String METHOD_NAME = "GetActivitiesListByTeamIdOrderByMonth";

		mActivityList.clear();
		SoapObject request = WebServiceHelper.getSOAPRequest(METHOD_NAME);
		request.addProperty("bTask", true);
		request.addProperty("bTraining", false);
		request.addProperty("bEvent", false);
		request.addProperty("bMatch", false);
		request.addProperty("myUserId", LoginAct.userId);
		request.addProperty("teamId", LoginAct.teamId);
		request.addProperty("clubId", LoginAct.clubId);

		SoapObject response = (SoapObject) WebServiceHelper.getSOAPResponse(request,
				METHOD_NAME);

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
								mIndex = mActivityList.size() - 1;
							}
						} else {
							mCalendar.setTime(mDateFormat.parse(dateTime));
							if (current.after(mCalendar)) {
								mIndex = mIndex + 1;
							}
						}
					}
				}

			}
		}

	}

	// Get the details of activity from web service
	private void getActivityDetails() throws Exception {

		String METHOD_NAME = "GetTaskById";

		if (mIndex >= mActivityList.size()) {
			mIndex = mActivityList.size() - 1;
		}

		mId = mActivityList.get(mIndex);

		mActivity.clear();
		mPresentPlayerList.clear();
		SoapObject request = WebServiceHelper.getSOAPRequest(METHOD_NAME);
		request.addProperty("taskId", mId);
		request.addProperty("clubId", mClubId);
		SoapObject response = (SoapObject) WebServiceHelper.getSOAPResponse(request,
				METHOD_NAME);

		if (response != null) {

			String subType = response.getProperty("TaskType").toString();
			String dateTime = response.getProperty("TaskDateTime").toString();

			String details = null;
			if (response.getProperty("Details") != null) {
				details = response.getProperty("Details").toString();
			}

			mActivity.put("title", subType);
			mActivity.put("subType", subType);
			mActivity.put("dateTime", dateTime);
			mActivity.put("details", details);

			SoapObject soapPlayers = (SoapObject) response
					.getProperty("AssignedToUsers");

			if (soapPlayers != null) {

				for (int j = 0; j < soapPlayers.getPropertyCount(); j++) {

					SoapObject soapPlayer = (SoapObject) soapPlayers
							.getProperty(j);
					String userId = soapPlayer.getProperty("UserId").toString();
					String userName = soapPlayer.getProperty("Name").toString();

					HashMap<String, String> player = new HashMap<String, String>();
					player.put("userId", userId);
					player.put("userName", userName);
					mPresentPlayerList.add(player);
				}
			}
		}

	}

	private boolean deleteActivity() throws Exception {

		String METHOD_NAME = "DeleteActivity";

		SoapObject request = WebServiceHelper.getSOAPRequest(METHOD_NAME);
		request.addProperty("activityId", mId);
		request.addProperty("activityType", mType);
		request.addProperty("clubId", LoginAct.clubId);
		Object response = WebServiceHelper.getSOAPResponse(request, METHOD_NAME);

		String status = response.toString();
		return Boolean.parseBoolean(status);
	}

	private void showDeletionDialog() {

		AlertDialog.Builder optionBuilder = new AlertDialog.Builder(this);
		optionBuilder.setMessage(getString(R.string.delete_sure));
		optionBuilder.setPositiveButton(getString(R.string.yes),
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// Do nothing but close the dialog
						runDeleteActivity();
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
					TeamAppAlerts
							.showToast(mTasks, getString(R.string.service));
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

		Log.d("runActivity", "start...");
		isCompleted = false;
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case 0:
					populateActivity();
					populateAttendance();
					progressBar.setVisibility(View.GONE);
					bodyLayout.setVisibility(View.VISIBLE);
					isCompleted = true;
					break;
				case 1:
					progressBar.setVisibility(View.GONE);
					TeamAppAlerts.showToast(mTasks, getString(R.string.no)
							+ " " + getString(R.string.task) + " "
							+ getString(R.string.available));
					isCompleted = true;
					break;
				case 2:
					progressBar.setVisibility(View.GONE);
					TeamAppAlerts
							.showToast(mTasks, getString(R.string.service));
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

	private void runDeleteActivity() {

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case 0:
					mActivityList.remove(mIndex);
					runActivity();
					break;
				case 1:
					TeamAppAlerts.showToast(mTasks,
							getString(R.string.delete_failed) + " "
									+ getString(R.string.task));
					progressBar.setVisibility(View.GONE);
					break;
				case 2:
					TeamAppAlerts
							.showToast(mTasks, getString(R.string.service));
					progressBar.setVisibility(View.GONE);
					break;
				}
			}
		};

		TeamAppAlerts.showToast(mTasks, getString(R.string.deleting));
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

}