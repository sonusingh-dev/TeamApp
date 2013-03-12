package com.teamapp.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.teamapp.ui.R;

public class MessageReceivedActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.push_notification);
		String message = getIntent().getStringExtra("payload");
		Log.d("Push notification", "" + message);
		TextView view = (TextView) findViewById(R.id.txtPushMsg);
		view.setText(message);
	}
}