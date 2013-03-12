package com.teamapp.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import com.teamapp.ui.R;

public class SplashScreen extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// The activity is being created.
		setContentView(R.layout.splash_screen);

		Thread splashThread = new Thread() {
			public void run() {
				try {
					// Register to C2DM server
					// C2DMessaging.register(SplashScreen.this,
					// "androidwala@gmail.com");
					sleep(5000);
				} catch (Exception exc) {
					Log.d("Exception", exc.toString());
				} finally {					
					Intent intent = new Intent(SplashScreen.this,
							LoginAct.class);
					startActivity(intent);
				}
			}
		};
		splashThread.start();
	}

	@Override
	protected void onStart() {
		super.onStart();
		// The activity is about to become visible.
	}

	@Override
	protected void onResume() {
		super.onResume();
		// The activity has become visible (it is now "resumed").
	}

	@Override
	protected void onPause() {
		super.onPause();
		// Another activity is taking focus (this activity is about to be
		// "paused").
	}

	@Override
	protected void onStop() {
		super.onStop();
		// The activity is no longer visible (it is now "stopped")
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// The activity is about to be destroyed.
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
