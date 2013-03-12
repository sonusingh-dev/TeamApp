package com.teamapp.ui.team;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import org.kobjects.base64.Base64;
import org.ksoap2.serialization.SoapObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.teamapp.helper.WebServiceHelper;
import com.teamapp.ui.LoginAct;
import com.teamapp.ui.R;
import com.teamapp.ui.alerts.TeamAppAlerts;

public class MyProfileAct extends TeamMenuAct {

	private Calendar mCalendar;
	private SimpleDateFormat mDateFormat;
	private HashMap<String, String> myProfile;

	private TextView lblName;
	private TextView lblEmail;
	private TextView lblDateOfBirth;
	private TextView lblNickName;

	private CheckBox chkGame;
	private CheckBox chkTraining;
	private CheckBox chkTask;
	private CheckBox chkEvent;
	private CheckBox chkNotification;

	private ImageView imgMy;
	private ImageView imgTeam;

	private Button btnEdit;

	private LinearLayout bodyLayout;

	private ProgressDialog pd;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_profile);

		mCalendar = Calendar.getInstance();
		mDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		myProfile = new HashMap<String, String>();

		lblName = (TextView) findViewById(R.id.lblName);
		lblEmail = (TextView) findViewById(R.id.lblEmail);
		lblDateOfBirth = (TextView) findViewById(R.id.lblDateOfBirth);
		lblNickName = (TextView) findViewById(R.id.lblNickName);

		chkGame = (CheckBox) findViewById(R.id.chkGame);
		chkTraining = (CheckBox) findViewById(R.id.chkTraining);
		chkTask = (CheckBox) findViewById(R.id.chkTask);
		chkEvent = (CheckBox) findViewById(R.id.chkEvent);
		chkNotification = (CheckBox) findViewById(R.id.chkNotification);

		imgMy = (ImageView) findViewById(R.id.imgMy);
		imgTeam = (ImageView) findViewById(R.id.imgTeam);

		btnEdit = (Button) findViewById(R.id.btnEdit);

		bodyLayout = (LinearLayout) findViewById(R.id.bodyLayout);

		btnEdit.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {
				// TODO Auto-generated method stub
				Intent intent = new Intent().setClass(MyProfileAct.this,
						EditMyProfileAct.class);
				startActivityForResult(intent, 0);
			}
		});

		new TheTask().execute();
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
		if (resultCode == RESULT_OK) {
			new TheTask().execute();
		}
	}

	private void getMyProfile() throws Exception {

		String METHOD_NAME = "GetPlayerByPlayerId";

		myProfile.clear();
		SoapObject request = WebServiceHelper.getSOAPRequest(METHOD_NAME);
		request.addProperty("playerId", LoginAct.userId);

		SoapObject response = (SoapObject) WebServiceHelper.getSOAPResponse(request,
				METHOD_NAME);

		if (response != null) {

			String name = null;
			String email = null;
			String eventAttendance = null;
			String gameAttendance = null;
			String taskAttendance = null;
			String trainingAttendance = null;
			String dateOfBirth = null;
			String nickName = null;
			String myImage = null;
			String notification = null;
			String teamImage = null;

			name = response.getProperty("Name").toString();

			if (response.getProperty("Email") != null) {
				email = response.getProperty("Email").toString();
			}

			if (response.getProperty("AutoEventAttendance") != null) {
				eventAttendance = response.getProperty("AutoEventAttendance")
						.toString();
			}

			if (response.getProperty("AutoMatchAttendance") != null) {
				gameAttendance = response.getProperty("AutoMatchAttendance")
						.toString();
			}

			if (response.getProperty("AutoTaskAttendance") != null) {
				taskAttendance = response.getProperty("AutoTaskAttendance")
						.toString();
			}

			if (response.getProperty("AutoTrainingAttendance") != null) {
				trainingAttendance = response.getProperty(
						"AutoTrainingAttendance").toString();
			}

			if (response.getProperty("DateOfBirth") != null) {
				dateOfBirth = response.getProperty("DateOfBirth").toString();
			}

			if (response.getProperty("NickName") != null) {
				nickName = response.getProperty("NickName").toString();
			}

			if (response.getProperty("Image") != null) {
				myImage = response.getProperty("Image").toString();
			}

			if (response.getProperty("ReceiveNotification") != null) {
				notification = response.getProperty("ReceiveNotification")
						.toString();
			}

			if (response.getProperty("TeamPicture") != null) {
				teamImage = response.getProperty("TeamPicture").toString();
			}

			myProfile.put("name", name);
			myProfile.put("email", email);
			myProfile.put("eventAttendance", eventAttendance);
			myProfile.put("gameAttendance", gameAttendance);
			myProfile.put("taskAttendance", taskAttendance);
			myProfile.put("trainingAttendance", trainingAttendance);
			myProfile.put("dateOfBirth", dateOfBirth);
			myProfile.put("nickName", nickName);
			myProfile.put("myImage", myImage);
			myProfile.put("notification", notification);
			myProfile.put("teamImage", teamImage);

		}

	}

	private void populateItem() {

		String name = myProfile.get("name");
		String email = myProfile.get("email");
		String eventAttendance = myProfile.get("eventAttendance");
		String gameAttendance = myProfile.get("gameAttendance");
		String taskAttendance = myProfile.get("taskAttendance");
		String trainingAttendance = myProfile.get("trainingAttendance");
		String dateOfBirth = myProfile.get("dateOfBirth");
		String nickName = myProfile.get("nickName");
		String myImage = myProfile.get("myImage");
		String notification = myProfile.get("notification");
		String teamImage = myProfile.get("teamImage");

		byte[] temp = null;
		Bitmap bmpImage = null;

		try {
			temp = Base64.decode(myImage);
			bmpImage = BitmapFactory.decodeByteArray(temp, 0, temp.length);
			imgMy.setImageBitmap(bmpImage);

			temp = Base64.decode(teamImage);
			bmpImage = BitmapFactory.decodeByteArray(temp, 0, temp.length);
			imgTeam.setImageBitmap(bmpImage);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		try {

			mCalendar.setTime(mDateFormat.parse(dateOfBirth));

			int day = mCalendar.get(Calendar.DAY_OF_MONTH);
			int month = mCalendar.get(Calendar.MONTH) + 1;
			int year = mCalendar.get(Calendar.YEAR);

			lblDateOfBirth.setText(new StringBuilder().append(pad(day))
					.append("-").append(month).append("-").append(pad(year)));

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		if (name != null && name.length() != 0) {
			lblName.setText(name);
		}

		if (email != null && email.length() != 0) {
			lblEmail.setText(email);
		}

		if (nickName != null && nickName.length() != 0) {
			lblNickName.setText(nickName);
		}

		if (gameAttendance != null && gameAttendance.length() != 0) {
			chkGame.setChecked(Boolean.parseBoolean(gameAttendance));
		}

		if (trainingAttendance != null && trainingAttendance.length() != 0) {
			chkTraining.setChecked(Boolean.parseBoolean(trainingAttendance));
		}

		if (taskAttendance != null && taskAttendance.length() != 0) {
			chkTask.setChecked(Boolean.parseBoolean(taskAttendance));
		}

		if (eventAttendance != null && eventAttendance.length() != 0) {
			chkEvent.setChecked(Boolean.parseBoolean(eventAttendance));
		}

		if (notification != null && notification.length() != 0) {
			chkNotification.setChecked(Boolean.parseBoolean(notification));
		}

	}

	private String pad(int c) {
		if (c >= 10)
			return String.valueOf(c);
		else
			return "0" + String.valueOf(c);
	}

	private class TheTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			bodyLayout.setVisibility(View.GONE);
			pd = ProgressDialog.show(MyProfileAct.this, "",
					getString(R.string.loading), true, true);
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				getMyProfile();
			} catch (Exception e) {
				cancel(false);
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			populateItem();
			bodyLayout.setVisibility(View.VISIBLE);
			pd.dismiss();
		}

		@Override
		protected void onCancelled() {
			pd.dismiss();
			TeamAppAlerts.showToast(MyProfileAct.this,
					getString(R.string.service));
		}
	}

}
