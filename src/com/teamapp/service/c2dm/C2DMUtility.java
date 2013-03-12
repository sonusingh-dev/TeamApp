package com.teamapp.service.c2dm;

import org.ksoap2.serialization.SoapObject;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.teamapp.helper.WebServiceHelper;
import com.teamapp.ui.LoginAct;
import com.teamapp.ui.R;
import com.teamapp.ui.alerts.TeamAppAlerts;

/**
 * Utilities for device registration.
 * 
 * Will keep track of the registration token in a private preference.
 */
public class C2DMUtility {

	public static final String EXTRA_SENDER = "sender";
	public static final String EXTRA_APPLICATION_PENDING_INTENT = "app";
	public static final String REQUEST_UNREGISTRATION_INTENT = "com.google.android.c2dm.intent.UNREGISTER";
	public static final String REQUEST_REGISTRATION_INTENT = "com.google.android.c2dm.intent.REGISTER";

	/**
	 * Initiate c2d messaging registration for the current application
	 */
	public static void register(Context context, String senderId) {

		Intent registrationIntent = new Intent(REQUEST_REGISTRATION_INTENT);
		registrationIntent.putExtra(EXTRA_APPLICATION_PENDING_INTENT,
				PendingIntent.getBroadcast(context, 0, new Intent(), 0));
		registrationIntent.putExtra(EXTRA_SENDER, senderId);
		context.startService(registrationIntent);
		// TODO: if intent not found, notification on need to have GSF
	}

	/**
	 * Unregister the application. New messages will be blocked by server.
	 */
	public static void unregister(Context context) {

		Intent unregIntent = new Intent(REQUEST_UNREGISTRATION_INTENT);
		unregIntent.putExtra(EXTRA_APPLICATION_PENDING_INTENT,
				PendingIntent.getBroadcast(context, 0, new Intent(), 0));
		context.startService(unregIntent);
	}

	public static void runRegistrationIdToServer(final Context context,
			final String registrationId) {

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case 0:
					TeamAppAlerts.showToast(context, context
							.getString(R.string.send_registration_to_server));
					break;
				case 1:
					TeamAppAlerts
							.showToast(
									context,
									context.getString(R.string.send_registration_to_server_failed));
					break;
				case 2:
					TeamAppAlerts.showToast(context,
							context.getString(R.string.service));
					break;
				}
			}
		};

		new Thread() {
			@Override
			public void run() {

				int what = 0;
				try {
					if (!sendRegistrationIdToServer(LoginAct.userId,
							registrationId)) {
						what = 1;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					what = 2;
					e.printStackTrace();
				}
				handler.sendEmptyMessage(what);
			}
		}.start();

	}

	// Incorrect usage as the receiver may be canceled at any time
	// do this in an service and in an own thread
	private static boolean sendRegistrationIdToServer(String userId,
			String registrationId) throws Exception {

		String METHOD_NAME = "GetAndroidPinUserByUserIdandRegistrationId";

		SoapObject request = WebServiceHelper.getSOAPRequest(METHOD_NAME);
		request.addProperty("userId", userId);
		request.addProperty("registrationId", registrationId);
		Object response = WebServiceHelper.getSOAPResponse(request, METHOD_NAME);

		String status = response.toString();
		return Boolean.parseBoolean(status);
	}
}
