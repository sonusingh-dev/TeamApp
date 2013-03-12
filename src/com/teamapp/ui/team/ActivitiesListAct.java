package com.teamapp.ui.team;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;

import com.teamapp.helper.TeamActivities;
import com.teamapp.ui.R;
import com.teamapp.ui.alerts.TeamAppAlerts;

public class ActivitiesListAct extends Activity {

	private static boolean isEditable;

	private boolean game;
	private boolean training;
	private boolean events;
	private boolean tasks;

	private String type;

	private ActivitiesListAdapter mAdapter;
	private ArrayList<HashMap<String, String>> teamActivitiesList;

	private TextView lblTitle;
	private CheckBox chkGame;
	private CheckBox chkTraining;
	private CheckBox chkEvents;
	private CheckBox chkTasks;
	private ListView lstTeamActivities;
	private ProgressDialog pd;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activities_list);

		type = getIntent().getStringExtra("type");
		mAdapter = new ActivitiesListAdapter(this, isEditable);
		teamActivitiesList = new ArrayList<HashMap<String, String>>();

		lblTitle = (TextView) findViewById(R.id.lblTitle);
		chkGame = (CheckBox) findViewById(R.id.chkGame);
		chkTraining = (CheckBox) findViewById(R.id.chkTraining);
		chkEvents = (CheckBox) findViewById(R.id.chkEvents);
		chkTasks = (CheckBox) findViewById(R.id.chkTasks);
		lstTeamActivities = (ListView) findViewById(R.id.lstTeamActivities);

		lblTitle.setText(type);
		lstTeamActivities.setDividerHeight(0);

		if (type.equals("Match")) {
			game = true;
			chkGame.setChecked(true);
		} else if (type.equals("Training")) {
			training = true;
			chkTraining.setChecked(true);
		} else if (type.equals("Event")) {
			events = true;
			chkEvents.setChecked(true);
		} else if (type.equals("Task")) {
			tasks = true;
			chkTasks.setChecked(true);
		}

		chkGame.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				game = isChecked;
				new TheTask().execute();
			}
		});

		chkTraining.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				training = isChecked;
				new TheTask().execute();
			}
		});

		chkEvents.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				events = isChecked;
				new TheTask().execute();
			}
		});

		chkTasks.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				tasks = isChecked;
				new TheTask().execute();
			}
		});
		
		new TheTask().execute();

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			setResult(mAdapter.getResultCode());
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			new TheTask().execute();
		}
	}
	
	private void populateList() {

		try {

			String temp = null;
			String month = null;
			HashMap<String, String> activity = null;
			mAdapter = new ActivitiesListAdapter(this, isEditable);

			for (int i = 0; i < teamActivitiesList.size(); i++) {

				activity = teamActivitiesList.get(i);
				month = activity.get("month");
				if (!month.equals(temp)) {					
					mAdapter.addSeparatorItem(activity);
					temp = month;
				}				
				mAdapter.addItem(activity);
			}
			lstTeamActivities.setAdapter(mAdapter);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private class TheTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			pd = ProgressDialog.show(ActivitiesListAct.this, "",
					getString(R.string.loading), true, false);
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				teamActivitiesList = TeamActivities.getTeamActivities(tasks,
						training, events, game);
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
			TeamAppAlerts.showToast(ActivitiesListAct.this,
					getString(R.string.service));

		}
	}

}
