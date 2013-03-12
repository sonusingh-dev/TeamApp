package com.teamapp.ui.team;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.teamapp.ui.R;

public class MessagingAct extends TeamMenuAct {

	private static final int PLAYERS = 1;
	private static final int CAPTAINS = 2;

	private Button btnPlayers;
	private Button btnCaptains;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.messaging);

		btnPlayers = (Button) findViewById(R.id.btnPlayers);
		btnCaptains = (Button) findViewById(R.id.btnCaptains);

		btnPlayers.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent().setClass(MessagingAct.this,
						RecipientListAct.class);
				intent.putExtra("key", PLAYERS);
				startActivity(intent);
			}
		});

		btnCaptains.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent().setClass(MessagingAct.this,
						RecipientListAct.class);
				intent.putExtra("key", CAPTAINS);
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
