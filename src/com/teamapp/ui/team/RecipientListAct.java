package com.teamapp.ui.team;

import java.util.ArrayList;
import java.util.HashMap;

import org.kobjects.base64.Base64;
import org.ksoap2.serialization.SoapObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.teamapp.helper.WebServiceHelper;
import com.teamapp.ui.LoginAct;
import com.teamapp.ui.R;
import com.teamapp.ui.alerts.TeamAppAlerts;

public class RecipientListAct extends Activity {

	private static final int PLAYERS = 1;
	private static final int CAPTAINS = 2;

	private int mKey;
	private int mCount;

	private String mUserId;
	private String mTeamId;
	private String mClubId;

	private ArrayList<HashMap<String, String>> mRecipientList;

	private TextView lblTitle;

	private CheckBox chkAll;

	private ListView lstMessaging;

	private Button btnDone;

	private ProgressDialog pd;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recipient_list);

		mKey = getIntent().getIntExtra("key", 0);

		mUserId = LoginAct.userId;
		mTeamId = LoginAct.teamId;
		mClubId = LoginAct.clubId;

		mRecipientList = new ArrayList<HashMap<String, String>>();

		lblTitle = (TextView) findViewById(R.id.lblTitle);
		chkAll = (CheckBox) findViewById(R.id.chkAll);
		lstMessaging = (ListView) findViewById(R.id.lstMessaging);
		btnDone = (Button) findViewById(R.id.btnDone);

		switch (mKey) {
		case PLAYERS:
			lblTitle.setText("Select Player");
			break;
		case CAPTAINS:
			lblTitle.setText("Select Team");
		default:
			break;
		}

		lstMessaging.setDivider(new ColorDrawable(0xffE6E6E6));
		lstMessaging.setDividerHeight(1);

		btnDone.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {
				// TODO Auto-generated method stub
												
				if (mCount > 0) {
					
					if (mKey == PLAYERS) {
						filterRecipientList();
					}
										
					Intent intent = new Intent().setClass(
							RecipientListAct.this, SendMessageAct.class);
					intent.putExtra("key", mKey);
					intent.putParcelableArrayListExtra("recipientList",
							(ArrayList<? extends Parcelable>) mRecipientList);
					startActivity(intent);
				}
			}
		});

		chkAll.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				String status = "false";
				if (chkAll.isChecked()) {
					status = "true";
				}

				for (int i = 0; i < mRecipientList.size(); i++) {
					HashMap<String, String> item = mRecipientList.get(i);
					item.put("status", status);
					mRecipientList.set(i, item);
				}

				isAllChecked();
				populateList();
			}
		});

		new TheTask().execute();

	}
	
	private void filterRecipientList() {
		
		HashMap<String, String> item = null;		
		for (int i = 0; i < mRecipientList.size(); i++) {
			item = mRecipientList.get(i);
			item.remove("image");
			mRecipientList.set(i, item);
		}		
	}

	private void populateList() {
		lstMessaging.setAdapter(new MessagingListAdapter(this));
	}

	private boolean isAllChecked() {

		boolean isAll = false;
		mCount = 0;

		for (int i = 0; i < mRecipientList.size(); i++) {
			HashMap<String, String> item = mRecipientList.get(i);
			String status = item.get("status");

			if (Boolean.parseBoolean(status)) {
				mCount++;
			}
		}

		if (mRecipientList.size() == mCount) {
			isAll = true;
		}

		return isAll;
	}

	private void showSendNotificationDialog() {

		final EditText txtComments = new EditText(this);
		txtComments.setLines(4);
		txtComments.setGravity(Gravity.TOP);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(txtComments);
		builder.setTitle("Send Message");

		builder.setPositiveButton("Send",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// Do nothing but close the dialog
						String message = txtComments.getText().toString();
						if (message.length() == 0) {
							// showToast("Enter Message");
							return;
						}

						int key = 0;
						if (mKey == 0) {
							key = 1;
						} else {
							key = 2;
						}
						
						runNotification(message, key);
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

	private void showToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}

	private void getPlayerList() throws Exception {

		String METHOD_NAME = "GetTeamPlayerByTeamId";

		SoapObject request = WebServiceHelper.getSOAPRequest(METHOD_NAME);
		request.addProperty("clubId", mClubId);
		request.addProperty("teamId", mTeamId);
		SoapObject response = (SoapObject) WebServiceHelper.getSOAPResponse(request,
				METHOD_NAME);

		if (response != null) {

			String deviceToken = null;
			String itemId = null;
			String name = null;
			String image = null;
			HashMap<String, String> item = null;

			for (int i = 0; i < response.getPropertyCount(); i++) {

				SoapObject property = (SoapObject) response.getProperty(i);
				deviceToken = property.getProperty("DeviceToken").toString();
				itemId = property.getProperty("Id").toString();
				name = property.getProperty("PlayerName").toString();
				image = property.getProperty("Image").toString();

				item = new HashMap<String, String>();
				item.put("deviceToken", deviceToken);
				item.put("itemId", itemId);
				item.put("name", name);
				item.put("image", image);
				item.put("status", "false");

				if (!mUserId.equals(itemId)) {
					mRecipientList.add(item);
				}
			}
		}
	}

	private void getTeamList() throws Exception {

		String METHOD_NAME = "GetTeamListForClub";

		SoapObject request = WebServiceHelper.getSOAPRequest(METHOD_NAME);
		request.addProperty("clubId", mClubId);
		SoapObject response = (SoapObject) WebServiceHelper.getSOAPResponse(request,
				METHOD_NAME);

		if (response != null) {

			for (int i = 0; i < response.getPropertyCount(); i++) {

				SoapObject property = (SoapObject) response.getProperty(i);

				String deviceToken = null;
				if (property.getProperty("CaptainsDeviceToken") != null) {
					deviceToken = property.getProperty("CaptainsDeviceToken")
							.toString();
				}

				String itemId = property.getProperty("TeamId").toString();
				String name = property.getProperty("TeamName").toString();

				HashMap<String, String> item = new HashMap<String, String>();
				item.put("deviceToken", deviceToken);
				item.put("itemId", itemId);
				item.put("name", name);
				item.put("status", "false");

				if (!mTeamId.equals(itemId)) {
					mRecipientList.add(item);
				}
			}
		}
	}

	private boolean notifyAllTeamMembers(String message) throws Exception {

		String METHOD_NAME = "NotifyAllTeamMembers";

		SoapObject request = WebServiceHelper.getSOAPRequest(METHOD_NAME);
		request.addProperty("message", message);
		request.addProperty("senderId", mUserId);
		request.addProperty("teamId", mTeamId);
		request.addProperty("clubId", mClubId);

		Object response = WebServiceHelper.getSOAPResponse(request, METHOD_NAME);

		String status = response.toString();
		return Boolean.parseBoolean(status);
	}

	private boolean notifyOtherTeamCaptains(String message) throws Exception {

		StringBuilder selectedItem = new StringBuilder();
		for (int i = 0; i < mRecipientList.size(); i++) {

			HashMap<String, String> item = mRecipientList.get(i);
			String itemId = item.get("itemId");
			String status = item.get("status");

			if (Boolean.parseBoolean(status)) {
				selectedItem.append("<arr:int>").append(itemId)
						.append("</arr:int>");
			}

		}
		
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

			HashMap<String, String> item = mRecipientList.get(i);
			String itemId = item.get("itemId");
			String status = item.get("status");

			if (Boolean.parseBoolean(status)) {
				selectedItem.append("<arr:int>").append(itemId)
						.append("</arr:int>");
			}

		}
		
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

	private void runNotification(final String message, final int key) {

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case 0:
					showToast("Notification Sent Successfully");
					break;
				case 1:
					showToast("Failed to send Notification");
					break;
				case 2:
					showToast("Service Not Available");
					break;
				}
			}
		};

		showToast("Sending Notification...");
		new Thread() {
			@Override
			public void run() {

				int what = 0;

				try {
					switch (key) {
					case 0:
						if (!notifyAllTeamMembers(message)) {
							what = 1;
						}
						break;
					case 1:
						if (!notifySelectedTeamMembers(message)) {
							what = 1;
						}
						break;
					case 2:
						if (!notifyOtherTeamCaptains(message)) {
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

	private class TheTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			pd = ProgressDialog.show(RecipientListAct.this, "",
					getString(R.string.loading), true, false);
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {

				switch (mKey) {
				case PLAYERS:
					getPlayerList();
					break;
				case CAPTAINS:
					getTeamList();
				default:
					break;
				}

			} catch (Exception e) {
				cancel(false);
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			populateList();
			pd.dismiss();
		}

		@Override
		protected void onCancelled() {
			pd.dismiss();
			TeamAppAlerts.showToast(RecipientListAct.this,
					getString(R.string.service));
		}
	}

	public static class ViewHolder {
		public TextView lblName;
		public ImageView imgPlayer;
		public CheckBox chkStatus;
	}

	private class MessagingListAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		public MessagingListAdapter(Context context) {
			mInflater = LayoutInflater.from(context);
		}

		public int getCount() {
			return mRecipientList.size();
		}

		public HashMap<String, String> getItem(int position) {
			return mRecipientList.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				switch (mKey) {
				case PLAYERS:
					convertView = mInflater.inflate(
							R.layout.player_list_item_with_checkbox, null);
					holder.imgPlayer = (ImageView) convertView
							.findViewById(R.id.imgPlayer);
					holder.lblName = (TextView) convertView
							.findViewById(R.id.lblName);
					holder.chkStatus = (CheckBox) convertView
							.findViewById(R.id.chkStatus);
					break;
				case CAPTAINS:
					convertView = mInflater.inflate(
							R.layout.list_item_with_checkbox, null);
					holder = new ViewHolder();
					holder.lblName = (TextView) convertView
							.findViewById(R.id.lblName);
					holder.chkStatus = (CheckBox) convertView
							.findViewById(R.id.chkStatus);
					break;
				}

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			HashMap<String, String> item = getItem(position);
			String name = item.get("name");
			String status = item.get("status");

			if (mKey == PLAYERS) {

				String image = item.get("image");

				try {
					byte[] temp = Base64.decode(image);
					Bitmap bmpImage = BitmapFactory.decodeByteArray(temp, 0,
							temp.length);
					holder.imgPlayer.setImageBitmap(bmpImage);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}

			holder.lblName.setText(name);
			holder.chkStatus.setChecked(Boolean.parseBoolean(status));

			holder.chkStatus
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							// TODO Auto-generated method stub
							HashMap<String, String> item = getItem(position);
							item.put("status", String.valueOf(isChecked));
							mRecipientList.set(position, item);

							if (isAllChecked()) {
								chkAll.setChecked(true);
							} else {
								chkAll.setChecked(false);
							}
						}
					});

			return convertView;
		}
	}
}
