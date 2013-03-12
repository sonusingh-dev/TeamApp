package com.teamapp.ui.club;

import java.util.ArrayList;
import java.util.HashMap;

import org.ksoap2.serialization.SoapObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.teamapp.helper.WebServiceHelper;
import com.teamapp.helper.Utility;
import com.teamapp.ui.LoginAct;
import com.teamapp.ui.R;
import com.teamapp.ui.alerts.TeamAppAlerts;

public class GameResultAct extends Activity {
	
	private String mTeamOne;
	private String mTeamTwo;
	private String mGameId;
	private String mReview;
	private String mResult;
	private String mWinnerTeamId;

	private ArrayList<HashMap<String, String>> mGoalsList;

	private TextView lblTitle;
	private TextView lblReview;

	private RadioButton radWe;
	private RadioButton radThey;
	private RadioButton radDraw;

	private ListView lstGoals;

	private ProgressDialog pd;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_result);
		
		mGameId = getIntent().getStringExtra("id");
		mResult = getIntent().getStringExtra("result");
		mTeamOne = getIntent().getStringExtra("teamOne");
		mTeamTwo = getIntent().getStringExtra("teamTwo");
		
		lblTitle = (TextView) findViewById(R.id.lblTitle);
		lblReview = (TextView) findViewById(R.id.lblReview);

		radWe = (RadioButton) findViewById(R.id.radWe);
		radThey = (RadioButton) findViewById(R.id.radThey);
		radDraw = (RadioButton) findViewById(R.id.radDraw);

		mGoalsList = new ArrayList<HashMap<String, String>>();

		lstGoals = (ListView) findViewById(R.id.lstGoals);
		lstGoals.setDivider(new ColorDrawable(0xffE6E6E6));
		lstGoals.setDividerHeight(1);

		new TheTask().execute();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	private void populateItem() {

		if (mWinnerTeamId != null) {

			if (mWinnerTeamId.equals("0")) {
				radDraw.setChecked(true);
			} else if (mWinnerTeamId.equals(mTeamOne)) {
				radWe.setChecked(true);
			} else if (mWinnerTeamId.equals(mTeamTwo)) {
				radThey.setChecked(true);
			}

		}

		lblTitle.setText(mResult);

		lblReview.setText(Utility.stipHtml(mReview));

		lstGoals.setAdapter(new SimpleAdapter(this, mGoalsList,
				R.layout.goals_stats_list_item, new String[] { "goalBy",
						"assistedBy", "goalTime" }, new int[] { R.id.lblGolaBy,
						R.id.lblAssistedBy, R.id.lblTime }));

		Utility.setListViewHeightBasedOnChildren(lstGoals);

	}

	private void getGameStats() throws Exception {

		mGoalsList.clear();
		String METHOD_NAME = "GetMatchResult";

		SoapObject request = WebServiceHelper.getSOAPRequest(METHOD_NAME);
		request.addProperty("matchId", mGameId);
		request.addProperty("teamId", LoginAct.teamId);
		request.addProperty("clubId", LoginAct.clubId);

		SoapObject response = (SoapObject) WebServiceHelper.getSOAPResponse(request,
				METHOD_NAME);

		if (response != null) {

			if (response.getProperty("Review") != null) {
				mReview = response.getProperty("Review").toString();
			}

			if (response.getProperty("Result") != null) {
				mResult = response.getProperty("Result").toString();
			}

			if (response.getProperty("WinnerTeamId") != null) {
				mWinnerTeamId = response.getProperty("WinnerTeamId").toString();
			}

			SoapObject sopGoals = (SoapObject) response.getProperty("Goals");

			if (sopGoals != null) {

				for (int i = 0; i < sopGoals.getPropertyCount(); i++) {
					SoapObject sopStats = (SoapObject) sopGoals.getProperty(i);

					String assistedBy = null;
					if (sopStats.getProperty("AssistedBy") != null) {
						assistedBy = sopStats.getProperty("AssistedBy")
								.toString();
					}

					String assistedById = null;
					if (sopStats.getProperty("AssistedByUserId") != null) {
						assistedById = sopStats.getProperty("AssistedByUserId")
								.toString();
					}

					String goalBy = sopStats.getProperty("GoalBy").toString();
					String goalById = sopStats.getProperty("GoalByPlayerId")
							.toString();
					String goalTime = sopStats.getProperty("GoalInMinutes")
							.toString();
					String statsId = sopStats.getProperty("StatisticsId")
							.toString();

					HashMap<String, String> goals = new HashMap<String, String>();
					goals.put("assistedBy", assistedBy);
					goals.put("assistedById", assistedById);
					goals.put("goalBy", goalBy);
					goals.put("goalById", goalById);
					goals.put("goalTime", goalTime);
					goals.put("statsId", statsId);
					mGoalsList.add(goals);
				}
			}

		}
	}

	private class TheTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			pd = ProgressDialog.show(GameResultAct.this, "",
					getString(R.string.loading), true, true);
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				getGameStats();
			} catch (Exception e) {
				cancel(false);
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			populateItem();
			pd.dismiss();
		}

		@Override
		protected void onCancelled() {
			pd.dismiss();
			TeamAppAlerts.showToast(GameResultAct.this,
					getString(R.string.service));
		}
	}
}
