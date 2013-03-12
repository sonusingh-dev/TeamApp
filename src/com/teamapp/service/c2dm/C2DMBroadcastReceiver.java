package com.teamapp.service.c2dm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.provider.Settings.Secure;
import android.util.Log;

import com.teamapp.ui.MessageReceivedActivity;

/**
 * Helper class to handle BroadcastReciver behavior. - can only run for a
 * limited amount of time - it must start a real service for longer activity -
 * must get the power lock, must make sure it's released when all done.
 * 
 */
public class C2DMBroadcastReceiver extends BroadcastReceiver {

	private static String KEY = "voetbal";
	private static String REGISTRATION_KEY = "registrationKey";

	@Override
	public void onReceive(Context context, Intent intent) {
		// setResult(Activity.RESULT_OK, null /* data */, null /* extra */);
		if (intent.getAction().equals(
				"com.google.android.c2dm.intent.REGISTRATION")) {
			handleRegistration(context, intent);
		} else if (intent.getAction().equals(
				"com.google.android.c2dm.intent.RECEIVE")) {
			handleMessage(context, intent);
		}
	}

	private void handleRegistration(Context context, Intent intent) {

		String registration = intent.getStringExtra("registration_id");
		if (intent.getStringExtra("error") != null) {
			// Registration failed, should try again later.
			Log.e("c2dm", "registration failed");
			String error = intent.getStringExtra("error");
			if (error == "SERVICE_NOT_AVAILABLE") {
				Log.e("c2dm", "SERVICE_NOT_AVAILABLE");
			} else if (error == "ACCOUNT_MISSING") {
				Log.e("c2dm", "ACCOUNT_MISSING");
			} else if (error == "AUTHENTICATION_FAILED") {
				Log.e("c2dm", "AUTHENTICATION_FAILED");
			} else if (error == "TOO_MANY_REGISTRATIONS") {
				Log.e("c2dm", "TOO_MANY_REGISTRATIONS");
			} else if (error == "INVALID_SENDER") {
				Log.d("c2dm", "INVALID_SENDER");
			} else if (error == "PHONE_REGISTRATION_ERROR") {
				Log.e("c2dm", "PHONE_REGISTRATION_ERROR");
			}
		} else if (intent.getStringExtra("unregistered") != null) {
			// unregistration done, new messages from the authorized sender will
			// be rejected
			Log.e("c2dm", "unregistered");

		} else if (registration != null) {
			String deviceId = Secure.getString(context.getContentResolver(),
					Secure.ANDROID_ID);
			Log.e("c2dm", "device_Id  :: " + deviceId);
			Log.e("c2dm", "registration_id  :: " + registration);
			SharedPreferences preferences = context.getSharedPreferences(KEY,
					Context.MODE_PRIVATE);
			String registrationId = preferences.getString(REGISTRATION_KEY,
					null);
			if (registrationId == null) {
				Editor editor = preferences.edit();
				editor.putString(REGISTRATION_KEY, registration);
				editor.commit();
				C2DMUtility.runRegistrationIdToServer(context, registration);
			}
		}
	}

	private void handleMessage(Context context, Intent intent) {

		String payload = intent.getStringExtra("payload");
		Log.e("recieved", "payload :: " + payload);

		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(
				android.R.drawable.btn_default_small, "Message received",
				System.currentTimeMillis());

		notification.defaults |= Notification.DEFAULT_ALL;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		intent.setClass(context, MessageReceivedActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				intent, Intent.FLAG_ACTIVITY_NEW_TASK);
		notification.setLatestEventInfo(context, "Message",
				"New message received", pendingIntent);
		notificationManager.notify(0, notification);

	}

}
