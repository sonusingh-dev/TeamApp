package com.teamapp.ui.team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.ksoap2.serialization.SoapObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.teamapp.apis.facebook.ShareOnFacebook;
import com.teamapp.apis.hyves.ShareOnHyves;
import com.teamapp.apis.twitter.ShareOnTwitter;
import com.teamapp.helper.Utility;
import com.teamapp.helper.WebServiceHelper;
import com.teamapp.ui.LoginAct;
import com.teamapp.ui.R;
import com.teamapp.ui.alerts.TeamAppAlerts;

public class GameResultAct extends Activity {

	private int mResultCode;

	private String name;
	private String teamOne;
	private String teamTwo;
	private String gameId;
	private String review;
	private String result;
	private String winnerTeamId;

	private ArrayList<HashMap<String, String>> goalsList;

	private TextView lblTitle;
	private TextView lblReview;

	private RadioButton radWe;
	private RadioButton radThey;
	private RadioButton radDraw;

	private ListView lstGoals;
	private Button btnShare;

	private LinearLayout bodyLayout;

	private ProgressDialog pd;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_result);

		mResultCode = RESULT_CANCELED;

		name = getIntent().getStringExtra("name");
		gameId = getIntent().getStringExtra("id");
		result = getIntent().getStringExtra("result");
		teamOne = getIntent().getStringExtra("teamOne");
		teamTwo = getIntent().getStringExtra("teamTwo");

		lblTitle = (TextView) findViewById(R.id.lblTitle);
		lblReview = (TextView) findViewById(R.id.lblReview);

		radWe = (RadioButton) findViewById(R.id.radWe);
		radThey = (RadioButton) findViewById(R.id.radThey);
		radDraw = (RadioButton) findViewById(R.id.radDraw);

		btnShare = (Button) findViewById(R.id.btnShare);

		bodyLayout = (LinearLayout) findViewById(R.id.bodyLayout);

		goalsList = new ArrayList<HashMap<String, String>>();

		lstGoals = (ListView) findViewById(R.id.lstGoals);
		lstGoals.setDivider(new ColorDrawable(0xffE6E6E6));
		lstGoals.setDividerHeight(1);

		if (result == null) {
			lblTitle.setText(getString(R.string.results_write));
			bodyLayout.setVisibility(View.GONE);
		} else {
			new TheTask().execute();
		}

		btnShare.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {
				// TODO Auto-generated method stub
				share(view);
				// showSocialMediaPopup(view);
			}
		});

		Log.d("GameResultAct", "GameId :: " + gameId);
		Log.d("GameResultAct", "Result :: " + result);
		Log.d("GameResultAct", "Match :: " + name);

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (!LoginAct.roleId.equals("4")) {
			menu.clear();
			if (winnerTeamId == null) {
				menu.add("Add");
			} else {
				menu.add("Edit");
			}
		}
		return true;
	}

	// Reaction to the menu selection
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem menuItem) {
		// TODO Auto-generated method stub
		Intent intent = getIntent();
		if (menuItem.getTitle().equals("Add")) {
			intent.setClass(GameResultAct.this, AddGameResultAct.class);
			startActivityForResult(intent, 0);
		} else if (menuItem.getTitle().equals("Edit")) {
			intent.setClass(GameResultAct.this, EditGameResultAct.class);
			startActivityForResult(intent, 0);
		}
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			setResult(mResultCode);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			mResultCode = resultCode;
			new TheTask().execute();
		}
	}

	private void populateItem() {

		if (winnerTeamId != null) {

			bodyLayout.setVisibility(View.VISIBLE);

			if (winnerTeamId.equals("0")) {
				radDraw.setChecked(true);
			} else if (winnerTeamId.equals(teamOne)) {
				radWe.setChecked(true);
			} else if (winnerTeamId.equals(teamTwo)) {
				radThey.setChecked(true);
			}
		}

		lblTitle.setText(result);

		lblReview.setText(Utility.stipHtml(review));

		lstGoals.setAdapter(new SimpleAdapter(this, goalsList,
				R.layout.goals_stats_list_item, new String[] { "goalBy",
						"assistedBy", "goalTime" }, new int[] { R.id.lblGolaBy,
						R.id.lblAssistedBy, R.id.lblTime }));

		Utility.setListViewHeightBasedOnChildren(lstGoals);

	}

	private void share(View view) {

		// Sort by trophy
		Collections.sort(goalsList, new Comparator<Map<String, String>>() {
			public int compare(Map<String, String> one, Map<String, String> two) {
				String firstValue = one.get("goalBy");
				String secondValue = two.get("goalBy");
				return firstValue.compareTo(secondValue);
			}
		});
		
		String temp = null;
		StringBuilder message = new StringBuilder();
		message.append(name).append(", ");
		message.append("Result: ").append(result).append(", ");
		message.append("Goals: ");
		for (int i = 0; i < goalsList.size(); i++) {
			Map<String, String> goal = goalsList.get(i);
			String player = goal.get("goalBy");
			if (!player.equals(temp)) {
				if (i != 0) {
					message.append("), ");
				}
				message.append(player).append(" (");
				temp = player;
			} else {
				message.append(", ");
			}
			String time = goal.get("goalTime");			
			message.append(time).append("'");			
		}

		if (!goalsList.isEmpty()) {
			message.append(")");
		}		
		showSocialMediaPopup(message.toString(), view);
	}

	private void getGameStats() throws Exception {

		goalsList.clear();
		String METHOD_NAME = "GetMatchResult";

		SoapObject request = WebServiceHelper.getSOAPRequest(METHOD_NAME);
		request.addProperty("matchId", gameId);
		request.addProperty("teamId", LoginAct.teamId);
		request.addProperty("clubId", LoginAct.clubId);

		SoapObject response = (SoapObject) WebServiceHelper.getSOAPResponse(
				request, METHOD_NAME);

		if (response != null) {

			if (response.getProperty("Review") != null) {
				review = response.getProperty("Review").toString();
			}

			if (response.getProperty("Result") != null) {
				result = response.getProperty("Result").toString();
			}

			if (response.getProperty("WinnerTeamId") != null) {
				winnerTeamId = response.getProperty("WinnerTeamId").toString();
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
					goalsList.add(goals);
				}
			}
		}
	}

	// PopupWindow for Article sharing
	private void showSocialMediaPopup(final String message, View view) {

		LayoutInflater inflater = LayoutInflater.from(GameResultAct.this);
		View popupView = inflater.inflate(R.layout.share_article_popup, null);
		popupView.measure(View.MeasureSpec.UNSPECIFIED,
				View.MeasureSpec.UNSPECIFIED);

		Display display = getWindowManager().getDefaultDisplay();
		int width = display.getWidth();
		int height = popupView.getMeasuredHeight() + 10;

		final PopupWindow popupWindow = new PopupWindow(popupView, width,
				height, true);
		popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setTouchable(true);

		Button btnFacebook = (Button) popupView.findViewById(R.id.btnFacebook);
		Button btnTwitter = (Button) popupView.findViewById(R.id.btnTwitter);
		Button btnHyves = (Button) popupView.findViewById(R.id.btnHyves);
		Button btnCancel = (Button) popupView.findViewById(R.id.btnCancel);

		btnFacebook.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				popupWindow.dismiss();
				ShareOnFacebook shareOnFacebook = new ShareOnFacebook(
						GameResultAct.this, message, null);
				shareOnFacebook.share();
			}
		});

		btnTwitter.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				popupWindow.dismiss();
				ShareOnTwitter shareOnTwitter = new ShareOnTwitter(
						GameResultAct.this, message, null);
				shareOnTwitter.share();
			}
		});

		btnHyves.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				popupWindow.dismiss();
				ShareOnHyves shareOnHyves = new ShareOnHyves(
						GameResultAct.this, message);
				shareOnHyves.share();
			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				popupWindow.dismiss();
			}
		});
	}

	private class TheTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			bodyLayout.setVisibility(View.GONE);
			pd = ProgressDialog.show(GameResultAct.this, "",
					getString(R.string.loading), true, false);
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
