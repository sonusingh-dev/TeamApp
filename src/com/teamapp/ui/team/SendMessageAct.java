package com.teamapp.ui.team;

import java.util.ArrayList;
import java.util.HashMap;

import org.ksoap2.serialization.SoapObject;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.teamapp.helper.WebServiceHelper;
import com.teamapp.ui.LoginAct;
import com.teamapp.ui.R;
import com.teamapp.ui.alerts.TeamAppAlerts;

public class SendMessageAct extends Activity {

	private static final int PLAYERS = 1;
	private static final int CAPTAINS = 2;
	private static final int ACTIVITY = 3;

	private int mKey;

	private String mUserId;
	private String mTeamId;
	private String mClubId;
	private String mMethod;
	private String mType;
	private String mTypeId;
	private String mAttendance;

	private ArrayList<? extends Parcelable> mRecipientList;

	private TextView txvEmail;
	private TextView txvDevice;

	private EditText edtMessage;

	private Button btnSend;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.send_message);
		
		mKey = getIntent().getIntExtra("key", 0);
		mMethod = getIntent().getStringExtra("method");
		mRecipientList = getIntent().getParcelableArrayListExtra(
				"recipientList");

		mUserId = LoginAct.userId;
		mTeamId = LoginAct.teamId;
		mClubId = LoginAct.clubId;

		edtMessage = (EditText) findViewById(R.id.edtMessage);

		btnSend = (Button) findViewById(R.id.btnSend);
		btnSend.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {
				// TODO Auto-generated method stub
				String message = edtMessage.getText().toString();
				if (edtMessage != null && message.length() == 0) {
					TeamAppAlerts.showToast(SendMessageAct.this,
							"Enter Message");
					return;
				}

				runNotification(message);

				// if (mMethod != null) {
				// runNotification(null);
				// } else {
				// String message = edtMessage.getText().toString();
				// if (edtMessage != null && message.length() == 0) {
				// VoetBalAlerts.showToast(SendMessageAct.this,
				// "Enter Message");
				// return;
				// }
				//
				// runNotification(message);
				// }
			}
		});

		if (mMethod != null) {
			mType = getIntent().getStringExtra("type");
			mTypeId = getIntent().getStringExtra("typeId");
			mAttendance = getIntent().getStringExtra("attendance");
			// edtMessage.setVisibility(View.GONE);
			runGetUsers();
		} else {
			populateItem();
		}
	}

	private void populateItem() {

		for (int i = 0; i < mRecipientList.size(); i++) {

			HashMap<String, String> item = (HashMap<String, String>) mRecipientList
					.get(i);
			String name = item.get("name");
			String status = item.get("status");
			String deviceToken = item.get("deviceToken");

			if (Boolean.parseBoolean(status) && deviceToken != null) {

				if (deviceToken.equals("Email")) {
					if (txvEmail == null) {
						txvEmail = (TextView) findViewById(R.id.txvEmail);
						txvEmail.setText(name);
					} else {
						txvEmail.append(", " + name);
					}
				} else if (deviceToken.equals("Push")) {
					if (txvDevice == null) {
						txvDevice = (TextView) findViewById(R.id.txvDevice);
						txvDevice.setText(name);
					} else {
						txvDevice.append(", " + name);
					}
				}
			}
		}
	}

	private void getGetUsers() throws Exception {

		String METHOD_NAME = "GetUsersNotificationResults";
		ArrayList<HashMap<String, String>> tempList = null;
		tempList = new ArrayList<HashMap<String, String>>();

		SoapObject request = WebServiceHelper.getSOAPRequest(METHOD_NAME);
		request.addProperty("type", mType);
		request.addProperty("attendanceType", mAttendance);
		request.addProperty("typeId", mTypeId);
		request.addProperty("teamId", mTeamId);
		request.addProperty("clubId", mClubId);
		
		SoapObject response = (SoapObject) WebServiceHelper.getSOAPResponse(request,
				METHOD_NAME);

		if (response != null) {

			SoapObject sopUsers = null;
			sopUsers = (SoapObject) response.getProperty("UserListDevice");
			for (int i = 0; i < sopUsers.getPropertyCount(); i++) {
				String name = sopUsers.getProperty(i).toString();

				HashMap<String, String> item = new HashMap<String, String>();
				item.put("name", name);
				item.put("status", "true");
				item.put("deviceToken", "Push");
				tempList.add(item);
			}

			sopUsers = (SoapObject) response.getProperty("UserListEmail");
			for (int i = 0; i < sopUsers.getPropertyCount(); i++) {
				String name = sopUsers.getProperty(i).toString();

				HashMap<String, String> item = new HashMap<String, String>();
				item.put("name", name);
				item.put("status", "true");
				item.put("deviceToken", "Email");
				tempList.add(item);
			}

			mRecipientList = (ArrayList<? extends Parcelable>) tempList;
		}
	}

	private boolean sendNotification(String message) throws Exception {

		SoapObject request = WebServiceHelper.getSOAPRequest(mMethod);
		request.addProperty("message", message);
		request.addProperty("type", mType);
		request.addProperty("typeId", mTypeId);
		request.addProperty("userId", mUserId);
		request.addProperty("teamId", mTeamId);
		request.addProperty("clubId", mClubId);
		
		Object response = WebServiceHelper.getSOAPResponse(request, mMethod);

		String status = response.toString();
		Log.d("notification", status);
		return Boolean.parseBoolean(status);
	}

	private boolean notifyOtherTeamCaptains(String message) throws Exception {

		StringBuilder selectedItem = new StringBuilder();
		for (int i = 0; i < mRecipientList.size(); i++) {

			HashMap<String, String> item = (HashMap<String, String>) mRecipientList
					.get(i);
			String itemId = item.get("itemId");
			String status = item.get("status");

			if (Boolean.parseBoolean(status)) {
				selectedItem.append("<arr:int>").append(itemId)
						.append("</arr:int>");
			}

		}

		Log.d("notifyOtherTeamCaptains", selectedItem.toString());

		String METHOD_NAME = "NotifyOtherTeamCaptains";
		String envelope = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\" xmlns:arr=\"http://schemas.microsoft.com/2003/10/Serialization/Arrays\">"
				+ "<soapenv:Header/>"
				+ "<soapenv:Body>"
				+ "<tem:NotifyOtherTeamCaptains>"
				+ "<tem:message>%s</tem:message>"
				+ "<tem:teamIds>%s</tem:teamIds>"
				+ "<tem:clubId>%s</tem:clubId>"
				+ "</tem:NotifyOtherTeamCaptains>"
				+ "</soapenv:Body>"
				+ "</soapenv:Envelope>";

		String request = String
				.format(envelope, message, selectedItem, mClubId);

		Log.d("request", request);

		String response = (String) WebServiceHelper.callWebService(METHOD_NAME,
				request);

		Log.d("response", response);

		return WebServiceHelper.getStatus(response);
	}

	private boolean notifySelectedTeamMembers(String message) throws Exception {

		StringBuilder selectedItem = new StringBuilder();
		for (int i = 0; i < mRecipientList.size(); i++) {

			HashMap<String, String> item = (HashMap<String, String>) mRecipientList
					.get(i);
			String itemId = item.get("itemId");
			String status = item.get("status");

			if (Boolean.parseBoolean(status)) {
				selectedItem.append("<arr:int>").append(itemId)
						.append("</arr:int>");
			}

		}

		Log.d("notifySelectedTeamMembers", selectedItem.toString());

		String METHOD_NAME = "NotifySelectedTeamMembers";
		String envelope = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\" xmlns:arr=\"http://schemas.microsoft.com/2003/10/Serialization/Arrays\">"
				+ "<soapenv:Header/>"
				+ "<soapenv:Body>"
				+ "<tem:NotifySelectedTeamMembers>"
				+ "<tem:message>%s</tem:message>"
				+ "<tem:userIds>%s</tem:userIds>"
				+ "<tem:clubId>%s</tem:clubId>"
				+ "</tem:NotifySelectedTeamMembers>"
				+ "</soapenv:Body>"
				+ "</soapenv:Envelope>";

		String request = String
				.format(envelope, message, selectedItem, mClubId);

		Log.d("request", request);

		String response = (String) WebServiceHelper.callWebService(METHOD_NAME,
				request);

		Log.d("response", response);

		return WebServiceHelper.getStatus(response);

	}

	private void runGetUsers() {

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case 0:
					populateItem();
					break;
				case 1:
					TeamAppAlerts.showToast(SendMessageAct.this,
							getString(R.string.service));
					break;
				}
			}
		};

		new Thread() {
			@Override
			public void run() {

				int what = 0;

				try {
					getGetUsers();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					what = 1;
					e.printStackTrace();
				}

				handler.sendMessage(handler.obtainMessage(what));

			}
		}.start();
	}

	private void runNotification(final String message) {

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case 0:
					TeamAppAlerts.showToast(SendMessageAct.this,
							getString(R.string.notification) + " "
									+ getString(R.string.sent_successfully));
					finish();
					break;
				case 1:
					TeamAppAlerts.showToast(SendMessageAct.this,
							getString(R.string.send_failed) + " "
									+ getString(R.string.notification));
					break;
				case 2:
					TeamAppAlerts.showToast(SendMessageAct.this,
							getString(R.string.service));
					break;
				}
			}
		};

		TeamAppAlerts.showToast(SendMessageAct.this,
				getString(R.string.sending));
		new Thread() {
			@Override
			public void run() {

				int what = 0;

				try {
					switch (mKey) {
					case PLAYERS:
						if (!notifySelectedTeamMembers(message)) {
							what = 1;
						}
						break;
					case CAPTAINS:
						if (!notifyOtherTeamCaptains(message)) {
							what = 1;
						}
						break;
					case ACTIVITY:
						if (!sendNotification(message)) {
							what = 1;
						}
						break;
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
