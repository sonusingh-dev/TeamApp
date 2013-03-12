package com.teamapp.ui.club;

import java.util.ArrayList;
import java.util.HashMap;

import org.ksoap2.serialization.SoapObject;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.teamapp.helper.WebServiceHelper;
import com.teamapp.ui.LoginAct;
import com.teamapp.ui.R;
import com.teamapp.ui.alerts.TeamAppAlerts;

public class ResultsFixturesAct extends ClubMenuAct {

	private boolean isInit = true;
	private int seasonIndex;

	private String mTeamId;
	private String mSeasonId;
	private ArrayList<HashMap<String, String>> teamList;
	private ArrayList<HashMap<String, String>> seasonList;
	private ArrayList<HashMap<String, String>> gameList;

	private Spinner spnTeam;
	private Spinner spnSeason;

	private ListView lstGames;

	private ProgressDialog pd;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.results_fixtures_club);

		teamList = new ArrayList<HashMap<String, String>>();
		seasonList = new ArrayList<HashMap<String, String>>();
		gameList = new ArrayList<HashMap<String, String>>();

		spnTeam = (Spinner) findViewById(R.id.spnTeam);
		spnSeason = (Spinner) findViewById(R.id.spnSeason);

		lstGames = (ListView) findViewById(R.id.lstGames);

		lstGames.setDividerHeight(0);

		spnSeason.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				// TODO Auto-generated method stub
				if (!isInit) {
					mSeasonId = seasonList.get(pos).get("seasonId");
					if (mSeasonId != null && mTeamId != null) {
						runGameResults();
					}
				}
			}

			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
			}
		});

		spnTeam.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				// TODO Auto-generated method stub
				if (!isInit) {
					mTeamId = teamList.get(pos).get("teamId");
					if (mSeasonId != null && mTeamId != null) {
						runGameResults();
					}
				}
			}

			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
			}
		});

		runDefault();

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void populateSpinner() {

		SimpleAdapter.ViewBinder viewBinder = new SimpleAdapter.ViewBinder() {

			public boolean setViewValue(View view, Object data,
					String textRepresentation) {
				// We configured the SimpleAdapter to create TextViews (see
				// the 'to' array, above), so this cast should be safe:
				TextView textView = (TextView) view;
				textView.setText(textRepresentation);
				return true;
			}
		};

		SimpleAdapter seasonAdapter = new SimpleAdapter(this, seasonList,
				android.R.layout.simple_spinner_item,
				new String[] { "seasonName" }, new int[] { android.R.id.text1 });
		seasonAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		seasonAdapter.setViewBinder(viewBinder);
		spnSeason.setAdapter(seasonAdapter);
		spnSeason.setPrompt("Select Season");
		spnSeason.setSelection(seasonIndex);

		SimpleAdapter teamAdapter = new SimpleAdapter(this, teamList,
				android.R.layout.simple_spinner_item,
				new String[] { "teamName" }, new int[] { android.R.id.text1 });
		teamAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		teamAdapter.setViewBinder(viewBinder);
		spnTeam.setAdapter(teamAdapter);
		spnTeam.setPrompt("Select Team");
		
	}

	private void populateList() {

		try {

			String temp = null;
			String month = null;
			HashMap<String, String> game = null;
			ResultsFixturesAdapter mAdapter = new ResultsFixturesAdapter(this);

			for (int i = 0; i < gameList.size(); i++) {

				game = gameList.get(i);
				month = game.get("month");
				if (!month.equals(temp)) {
					mAdapter.addSeparatorItem(game);
					temp = month;
				}

				mAdapter.addItem(game);

			}

			lstGames.setAdapter(mAdapter);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void getSeasons() throws Exception {

		String METHOD_NAME = "GetAllSeasons";

		seasonList.clear();
		SoapObject request = WebServiceHelper.getSOAPRequest(METHOD_NAME);
		request.addProperty("clubId", LoginAct.clubId);
		SoapObject response = (SoapObject) WebServiceHelper.getSOAPResponse(
				request, METHOD_NAME);

		if (response != null) {

			// HashMap<String, String> season = new HashMap<String, String>();
			// season.put("seasonName", getString(R.string.select));
			// seasonList.add(season);

			for (int i = 0; i < response.getPropertyCount(); i++) {

				SoapObject property = (SoapObject) response.getProperty(i);
				String seasonName = property.getProperty("Name").toString();
				String seasonId = property.getProperty("SeasonId").toString();
				String seasonStatus = property.getProperty("IsCurrentSeason")
						.toString();

				HashMap<String, String> season = new HashMap<String, String>();
				season.put("seasonId", seasonId);
				season.put("seasonName", seasonName);
				seasonList.add(season);

				if (Boolean.parseBoolean(seasonStatus)) {
					mSeasonId = seasonId;
					seasonIndex = i;
				}
			}
		}
	}

	private void getTeams() throws Exception {

		String METHOD_NAME = "GetTeamListForClub";

		SoapObject request = WebServiceHelper.getSOAPRequest(METHOD_NAME);
		request.addProperty("clubId", LoginAct.clubId);
		SoapObject response = (SoapObject) WebServiceHelper.getSOAPResponse(
				request, METHOD_NAME);

		if (response != null) {

			// HashMap<String, String> team = new HashMap<String, String>();
			// team.put("teamName", getString(R.string.select));
			// teamList.add(team);

			for (int i = 0; i < response.getPropertyCount(); i++) {

				SoapObject property = (SoapObject) response.getProperty(i);
				String teamId = property.getProperty("TeamId").toString();
				String teamName = property.getProperty("TeamName").toString();

				HashMap<String, String> team = new HashMap<String, String>();
				team.put("teamId", teamId);
				team.put("teamName", teamName);
				teamList.add(team);
			}
		}
	}

	private void getGameResults() throws Exception {

		String METHOD_NAME = "GetMonthWiseMatchListPlayedBySeasonIdTeamId";

		gameList.clear();
		SoapObject request = WebServiceHelper.getSOAPRequest(METHOD_NAME);
		request.addProperty("seasonId", mSeasonId);
		request.addProperty("teamId", mTeamId);
		request.addProperty("clubId", LoginAct.clubId);

		SoapObject response = (SoapObject) WebServiceHelper.getSOAPResponse(
				request, METHOD_NAME);

		if (response != null) {

			for (int i = 0; i < response.getPropertyCount(); i++) {

				SoapObject property = (SoapObject) response.getProperty(i);
				String month = property.getProperty("Month").toString();
				SoapObject statistics = (SoapObject) property
						.getProperty("statistics");

				if (statistics != null) {

					for (int j = 0; j < statistics.getPropertyCount(); j++) {

						SoapObject sopGame = (SoapObject) statistics
								.getProperty(j);

						String dateTime = sopGame.getProperty("MatchDateTime")
								.toString();
						String id = sopGame.getProperty("MatchId").toString();
						String type = sopGame.getProperty("MatchType")
								.toString();
						String name = sopGame.getProperty("Name").toString();

						String result = null;
						if (sopGame.getProperty("Result") != null) {
							result = sopGame.getProperty("Result").toString();
						}

						String teamOne = sopGame.getProperty("Team1Id")
								.toString();
						String teamTwo = sopGame.getProperty("Team2Id")
								.toString();

						HashMap<String, String> game = new HashMap<String, String>();
						game.put("month", month);
						game.put("dateTime", dateTime);
						game.put("id", id);
						game.put("type", type);
						game.put("name", name);
						game.put("result", result);
						game.put("teamOne", teamOne);
						game.put("teamTwo", teamTwo);
						gameList.add(game);
					}
				}
			}
		}
	}

	private void runDefault() {

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case 0:					
					mTeamId = teamList.get(0).get("teamId");
					populateSpinner();
					runGameResults();
					// pd.dismiss();
					break;
				case 1:
					pd.dismiss();
					TeamAppAlerts.showToast(ResultsFixturesAct.this,
							getString(R.string.service));
					break;
				}
			}
		};

		pd = ProgressDialog.show(ResultsFixturesAct.this, "",
				getString(R.string.loading), true, false);

		new Thread(new Runnable() {
			public void run() {
				// TODO Auto-generated method stub
				int what = 0;
				try {
					getTeams();
					getSeasons();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					what = 1;
					e.printStackTrace();
				}

				handler.sendMessage(handler.obtainMessage(what));
			}
		}).start();
	}

	private void runGameResults() {

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				isInit = false;
				switch (msg.what) {
				case 0:
					populateList();
					pd.dismiss();
					break;
				case 1:
					pd.dismiss();
					TeamAppAlerts.showToast(ResultsFixturesAct.this,
							getString(R.string.season_not));
					break;
				case 2:
					pd.dismiss();
					TeamAppAlerts.showToast(ResultsFixturesAct.this,
							getString(R.string.service));
					break;
				}
			}
		};

		if (!isInit) {
			pd = ProgressDialog.show(ResultsFixturesAct.this, "",
					getString(R.string.loading), true, false);
		}

		new Thread(new Runnable() {
			public void run() {
				// TODO Auto-generated method stub
				int what = 0;
				try {
					if (teamList.isEmpty() && seasonList.isEmpty()) {
						what = 1;
					} else {
						getGameResults();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					what = 2;
					e.printStackTrace();
				}

				handler.sendMessage(handler.obtainMessage(what));
			}
		}).start();
	}

}
