package com.teamapp.ui.team;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.teamapp.ui.LoginAct;
import com.teamapp.ui.R;

public class TeamMenuAct extends Activity {

	public static int sMnuIndex = 0;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
	}

	// Create the menu based on the XML defintion
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		if (LoginAct.roleId.equals("4")) {
			inflater.inflate(R.menu.team_menu_1, menu);
		} else {
			inflater.inflate(R.menu.team_menu_2, menu);
		}

		menu.getItem(sMnuIndex).setEnabled(false);
		return true;
	}

	// Reaction to the menu selection
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem menuItem) {
		// TODO Auto-generated method stub
		Intent intent = null;
		switch (menuItem.getItemId()) {
		case R.id.mniGame:
			sMnuIndex = 0;
			intent = new Intent().setClass(this, GameDetailsAct.class);
			startActivity(intent);
			break;
		case R.id.mniTraining:
			sMnuIndex = 1;
			intent = new Intent().setClass(this, TrainingDetailsAct.class);
			startActivity(intent);
			break;
		case R.id.mniTasks:
			sMnuIndex = 2;
			intent = new Intent().setClass(this, TasksDetailsAct.class);
			startActivity(intent);
			break;
		case R.id.mniStats:
			sMnuIndex = 3;
			intent = new Intent().setClass(this, StatsAct.class);
			startActivity(intent);
			break;
		case R.id.mniEvents:
			sMnuIndex = 4;
			intent = new Intent().setClass(this, EventsDetailsAct.class);
			startActivity(intent);
			break;
		case R.id.mniPoll:
			sMnuIndex = 5;
			intent = new Intent().setClass(this, PollListAct.class);
			startActivity(intent);
			break;
		case R.id.mniProfile:
			sMnuIndex = 6;
			intent = new Intent().setClass(this, MyProfileAct.class);
			startActivity(intent);
			break;
		case R.id.mniTeamAlbum:
			sMnuIndex = 7;
			intent = new Intent().setClass(this, TeamAlbumAct.class);
			startActivity(intent);
			break;
		case R.id.mniCalendar:
			sMnuIndex = 8;
			intent = new Intent().setClass(this, CalSettingAct.class);
			startActivity(intent);
			break;
		case R.id.mniResultsFixtures:
			sMnuIndex = 9;
			intent = new Intent().setClass(this, ResultsFixturesAct.class);
			startActivity(intent);
			break;
		case R.id.mniSendMsg:
			sMnuIndex = 10;
			intent = new Intent().setClass(this, MessagingAct.class);
			startActivity(intent);
			break;
		case R.id.mniLogout:
			intent = new Intent().setClass(this, LoginAct.class);
			intent.putExtra("session", 1);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			break;
		}
		return true;
	}
}
