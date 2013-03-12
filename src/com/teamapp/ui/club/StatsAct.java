package com.teamapp.ui.club;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.teamapp.ui.R;

public class StatsAct extends ClubMenuAct {

	private int index;
	private String title;
	private String METHOD_NAME;

	private Button btnTopScores;
	private Button btnAttendance;
	private Button btnGoalsPerGame;
	private Button btnAssists;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stats_club);

		btnTopScores = (Button) findViewById(R.id.btnTopScores);
		btnAttendance = (Button) findViewById(R.id.btnAttendance);
		btnGoalsPerGame = (Button) findViewById(R.id.btnGoalsPerGame);
		btnAssists = (Button) findViewById(R.id.btnAssists);

		btnTopScores.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {
				// TODO Auto-generated method stub
				index = 0;
				title = "Top Scores";
				METHOD_NAME = "GetTopScorersListInClubByMatchTypeBySeason";
				btnOnClick(view);
			}
		});

		btnAttendance.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {
				// TODO Auto-generated method stub
				index = 1;
				title = "Attendance %";
				METHOD_NAME = "GetAttendancePercentageInMatchesBySeasonByMatchtypeInClub";
				btnOnClick(view);
			}
		});

		btnGoalsPerGame.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {
				// TODO Auto-generated method stub
				index = 2;
				title = "Goals Per Game";
				METHOD_NAME = "GetGoalsPerGameByPlayersInMatchesBySeasonByMatchtypeInClub";
				btnOnClick(view);
			}
		});

		btnAssists.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {
				// TODO Auto-generated method stub
				index = 3;
				title = "Assists";
				METHOD_NAME = "GetTopAssistListInClubByMatchTypeBySeasonId";
				btnOnClick(view);
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

	public void btnOnClick(View view) {
		// TODO Auto-generated method stub
		Intent intent = new Intent().setClass(this, StatsListAct.class);
		intent.putExtra("index", index);
		intent.putExtra("title", title);
		intent.putExtra("METHOD_NAME", METHOD_NAME);
		startActivity(intent);
	}

}
