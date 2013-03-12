package com.teamapp.ui.team;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.teamapp.ui.R;
import com.teamapp.ui.alerts.TeamAppAlerts;

public class CalSettingAct extends TeamMenuAct {

	private String[] items;

	private Spinner spnGame;
	private Spinner spnTraining;
	private Spinner spnEvents;
	private Spinner spnTasks;

	private Button btnSave;

	private SharedPreferences mPreferences;
	private SharedPreferences.Editor mEditor;

	public static final String PREF_FILE_NAME = "voetbal";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calendar_setting);

		items = getResources().getStringArray(R.array.calendar);
		mPreferences = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
		mEditor = mPreferences.edit();

		spnGame = (Spinner) findViewById(R.id.spnGame);
		spnTraining = (Spinner) findViewById(R.id.spnTraining);
		spnEvents = (Spinner) findViewById(R.id.spnEvents);
		spnTasks = (Spinner) findViewById(R.id.spnTasks);

		btnSave = (Button) findViewById(R.id.btnSave);

		btnSave.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				saveCalSetting();
			}
		});

		populateItem();

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void populateItem() {

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, items);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spnGame.setAdapter(adapter);
		spnGame.setSelection(mPreferences.getInt("Game", 0));

		spnTraining.setAdapter(adapter);
		spnTraining.setSelection(mPreferences.getInt("Training", 0));

		spnEvents.setAdapter(adapter);
		spnEvents.setSelection(mPreferences.getInt("Event", 0));

		spnTasks.setAdapter(adapter);
		spnTasks.setSelection(mPreferences.getInt("Task", 0));
	}

	private void saveCalSetting() {

		mEditor.putInt("Game", spnGame.getSelectedItemPosition());
		mEditor.putInt("Training", spnTraining.getSelectedItemPosition());
		mEditor.putInt("Event", spnEvents.getSelectedItemPosition());
		mEditor.putInt("Task", spnTasks.getSelectedItemPosition());
		mEditor.commit();

		TeamAppAlerts.showMessageDialog(this,
				getString(R.string.calendar_header) + " "
						+ getString(R.string.saved));
	}
}
