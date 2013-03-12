package com.teamapp.ui.club;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.teamapp.ui.R;

public class ClubMenuAct extends Activity {

	public static int sMnuIndex = 0;

	// Create the menu based on the XML defintion
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.club_menu, menu);
		menu.getItem(sMnuIndex).setEnabled(false);
		return true;
	}

	// Reaction to the menu selection
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem menuItem) {
		// TODO Auto-generated method stub
		Intent intent = null;
		switch (menuItem.getItemId()) {
		case R.id.mniNews:
			sMnuIndex = 0;
			intent = new Intent().setClass(this, NewsListAct.class);
			startActivity(intent);
			break;
		case R.id.mniResultsFixtures:
			sMnuIndex = 1;
			intent = new Intent().setClass(this, ResultsFixturesAct.class);
			startActivity(intent);
			return true;
		case R.id.mniStats:
			sMnuIndex = 2;
			intent = new Intent().setClass(this, StatsAct.class);
			startActivity(intent);
			return true;
		case R.id.mniClubden:
			sMnuIndex = 3;
			intent = new Intent().setClass(this, ClubdenAct.class);
			startActivity(intent);
			break;
		}
		return true;
	}
}
