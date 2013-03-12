package com.teamapp.ui.team;

import java.util.ArrayList;
import java.util.HashMap;

import org.ksoap2.serialization.SoapObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.teamapp.helper.Utility;
import com.teamapp.helper.WebServiceHelper;
import com.teamapp.ui.LoginAct;
import com.teamapp.ui.R;
import com.teamapp.ui.alerts.TeamAppAlerts;

public class AddGameResultAct extends Activity {

	private int mResultCode;

	private String teamOne;
	private String teamTwo;
	private String gameId;
	private String result;
	private String review;
	private String winnerTeamId;
	private String teamId;
	private String clubId;
	private String userId;

	private ArrayList<HashMap<String, String>> playersList;
	private ArrayList<HashMap<String, String>> goalsStatsList;

	private EditText txtScore1;
	private EditText txtScore2;
	private EditText txtReview;
	private EditText txtGoalsTime;

	private RadioButton radWe;
	private RadioButton radThey;
	private RadioButton radDraw;

	private Spinner spnGoalBy;
	private Spinner spnAssistedBy;

	private ListView lstGoals;

	private Button btnAdd;
	private Button btnClear;
	private Button btnSubmit;

	private ProgressDialog pd;
	private GameGoalsStatsAdapter mAdapter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_edit_game_result);

		teamId = LoginAct.teamId;
		clubId = LoginAct.clubId;
		userId = LoginAct.userId;

		gameId = getIntent().getStringExtra("id");
		teamOne = getIntent().getStringExtra("teamOne");
		teamTwo = getIntent().getStringExtra("teamTwo");

		playersList = new ArrayList<HashMap<String, String>>();
		goalsStatsList = new ArrayList<HashMap<String, String>>();

		mAdapter = new GameGoalsStatsAdapter(this);

		txtScore1 = (EditText) findViewById(R.id.txtScore1);
		txtScore2 = (EditText) findViewById(R.id.txtScore2);
		txtReview = (EditText) findViewById(R.id.txtReview);
		txtGoalsTime = (EditText) findViewById(R.id.txtGoalsTime);

		radWe = (RadioButton) findViewById(R.id.radWe);
		radThey = (RadioButton) findViewById(R.id.radThey);
		radDraw = (RadioButton) findViewById(R.id.radDraw);

		spnGoalBy = (Spinner) findViewById(R.id.spnGoalBy);
		spnAssistedBy = (Spinner) findViewById(R.id.spnAssistedBy);

		lstGoals = (ListView) findViewById(R.id.lstGoals);

		btnAdd = (Button) findViewById(R.id.btnAdd);
		btnClear = (Button) findViewById(R.id.btnClear);
		btnSubmit = (Button) findViewById(R.id.btnSubmit);

		lstGoals.setAdapter(mAdapter);
		lstGoals.setDivider(new ColorDrawable(0xffE6E6E6));
		lstGoals.setDividerHeight(1);

		lstGoals.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				showDeletePopup(view, position);
			}
		});

		radWe.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					winnerTeamId = teamOne;
				}
			}
		});

		radThey.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					winnerTeamId = teamTwo;
				}
			}
		});

		radDraw.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					winnerTeamId = "0";
				}
			}
		});

		btnAdd.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				add();
			}
		});

		btnClear.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				clear();
			}
		});

		btnSubmit.setText("Proceed");
		btnSubmit.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				validate();
			}
		});

		new TheTask().execute();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		finish();
	}

	private void showDeletePopup(final View view, final int position) {

		LayoutInflater inflater = LayoutInflater.from(this);
		View popupView = inflater.inflate(R.layout.delete_option_popup, null);
		popupView.measure(View.MeasureSpec.UNSPECIFIED,
				View.MeasureSpec.UNSPECIFIED);

		Display display = getWindowManager().getDefaultDisplay();
		int width = display.getWidth();
		int height = popupView.getMeasuredHeight() - 10;

		final PopupWindow popupWindow = new PopupWindow(popupView, width,
				height);
		popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setTouchable(true);

		Button btnDelete = (Button) popupView.findViewById(R.id.btnDelete);
		Button btnCancel = (Button) popupView.findViewById(R.id.btnCancel);

		btnDelete.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				popupWindow.dismiss();
				mAdapter.getGoalsStatsList().remove(position);
				mAdapter.notifyDataSetChanged();
			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				popupWindow.dismiss();
			}
		});
	}

	private void populateItem() {

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

		SimpleAdapter playerAdapter = new SimpleAdapter(this, playersList,
				android.R.layout.simple_spinner_item,
				new String[] { "userName" }, new int[] { android.R.id.text1 });
		playerAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		playerAdapter.setViewBinder(viewBinder);

		spnGoalBy.setAdapter(playerAdapter);
		spnGoalBy.setPrompt(getString(R.string.select_player));

		spnAssistedBy.setAdapter(playerAdapter);
		spnAssistedBy.setPrompt(getString(R.string.select_player));

	}

	private void clear() {

		spnGoalBy.setSelection(0);
		spnAssistedBy.setSelection(0);
		txtGoalsTime.setText("");
	}

	private void add() {

		int index = spnGoalBy.getSelectedItemPosition();
		HashMap<String, String> player = playersList.get(index);
		String goalBy = player.get("userName");
		String goalById = player.get("userId");
		if (goalById == null) {
			TeamAppAlerts.showMessageDialog(AddGameResultAct.this,
					getString(R.string.select_player));
			return;
		}

		index = spnAssistedBy.getSelectedItemPosition();
		player = playersList.get(index);
		String assistedBy = player.get("userName");
		String assistedById = player.get("userId");
		if (assistedById == null) {
			assistedBy = null;
		}

		String goalTime = txtGoalsTime.getText().toString();
		if (goalTime == null || goalTime.length() == 0) {
			TeamAppAlerts.showMessageDialog(AddGameResultAct.this,
					getString(R.string.enter) + " "
							+ getString(R.string.goal_mins));
			return;
		}

		HashMap<String, String> goalStats = new HashMap<String, String>();
		goalStats.put("goalBy", goalBy);
		goalStats.put("goalById", goalById);
		goalStats.put("assistedBy", assistedBy);
		goalStats.put("assistedById", assistedById);
		goalStats.put("goalTime", goalTime);
		mAdapter.addItem(goalStats);

		Utility.setListViewHeightBasedOnChildren(lstGoals);
		clear();
	}

	private void getPlayers() throws Exception {

		String METHOD_NAME = "GetPlayerDetailsPresentInMatch";

		SoapObject request = WebServiceHelper.getSOAPRequest(METHOD_NAME);
		request.addProperty("teamId", teamId);
		request.addProperty("matchId", gameId);
		request.addProperty("clubId", clubId);

		Log.d("teamId", "" + teamId);
		Log.d("matchId", "" + gameId);
		Log.d("clubId", "" + clubId);

		SoapObject response = (SoapObject) WebServiceHelper.getSOAPResponse(
				request, METHOD_NAME);

		if (response != null) {

			HashMap<String, String> item = new HashMap<String, String>();
			item.put("userName", getString(R.string.select_player));
			playersList.add(item);

			for (int i = 0; i < response.getPropertyCount(); i++) {

				SoapObject property = (SoapObject) response.getProperty(i);
				String userName = property.getProperty("Name").toString();
				String userId = property.getProperty("UserId").toString();

				item = new HashMap<String, String>();
				item.put("userId", userId);
				item.put("userName", userName);
				playersList.add(item);
			}
		}
	}

	private void validate() {

		String score1 = txtScore1.getText().toString();
		String score2 = txtScore2.getText().toString();
		if (score1.length() == 0 && score2.length() == 0) {
			TeamAppAlerts.showMessageDialog(AddGameResultAct.this,
					getString(R.string.results_add_edit_score));
			return;
		}
		
		review = txtReview.getText().toString();
		if (review == null) {
			TeamAppAlerts.showMessageDialog(AddGameResultAct.this,
					getString(R.string.enter) + " "
							+ getString(R.string.enter_review));
			return;
		}

		if (winnerTeamId == null) {
			TeamAppAlerts.showMessageDialog(AddGameResultAct.this,
					getString(R.string.set_result));
			return;
		}

		result = score1 + "-" + score2;
		runInsert();
	}

	private boolean insertGameResult() throws Exception {

		goalsStatsList = mAdapter.getGoalsStatsList();
		StringBuilder goals = new StringBuilder();
		for (int i = 0; i < goalsStatsList.size(); i++) {
			HashMap<String, String> goalsStats = goalsStatsList.get(i);
			goals.append("<voet:GoalStatistics>");

			if (goalsStats.get("assistedById") != null) {
				goals.append("<voet:AssistedByUserId>")
						.append(goalsStats.get("assistedById"))
						.append("</voet:AssistedByUserId>");
			}

			goals.append("<voet:GoalByPlayerId>")
					.append(goalsStats.get("goalById"))
					.append("</voet:GoalByPlayerId><voet:GoalInMinutes>")
					.append(goalsStats.get("goalTime"))
					.append("</voet:GoalInMinutes>")
					.append("</voet:GoalStatistics>");

		}

		String METHOD_NAME = "InsertMatchResult";
		String envelope = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\" xmlns:voet=\"http://schemas.datacontract.org/2004/07/VoetBall.Entities.DataObjects\">"
				+ "<soapenv:Header/>"
				+ "<soapenv:Body>"
				+ "<tem:InsertMatchResult>"
				+ "<tem:result>"
				+ "<voet:CreatedByUserId>%s</voet:CreatedByUserId>"
				+ "<voet:Goals>%s</voet:Goals>"
				+ "<voet:MatchId>%s</voet:MatchId>"
				+ "<voet:Result>%s</voet:Result>"
				+ "<voet:Review>%s</voet:Review>"
				+ "<voet:ReviewId>0</voet:ReviewId>"
				+ "<voet:WinnerTeamId>%s</voet:WinnerTeamId>"
				+ "</tem:result>"
				+ "<tem:clubId>%s</tem:clubId>"
				+ "</tem:InsertMatchResult>"
				+ "</soapenv:Body></soapenv:Envelope>";

		String request = String.format(envelope, userId, goals, gameId, result,
				review, winnerTeamId, clubId);

		Log.d("request", request);

		String response = (String) WebServiceHelper.callWebService(METHOD_NAME,
				request);

		Log.d("response", response);

		return WebServiceHelper.getStatus(response);

	}

	private void runInsert() {

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case 0:
					pd.dismiss();
					setResult(RESULT_OK);
					Intent intent = getIntent();
					playersList.remove(0);
					intent.putParcelableArrayListExtra("players",
							(ArrayList<? extends Parcelable>) playersList);
					intent.setClass(AddGameResultAct.this,
							AddGameResultPollAct.class);
					startActivityForResult(intent, 0);
					break;
				case 1:
					pd.dismiss();
					TeamAppAlerts.showToast(AddGameResultAct.this,
							getString(R.string.insert_failed) + " "
									+ getString(R.string.game_result));
					break;
				case 2:
					pd.dismiss();
					TeamAppAlerts.showToast(AddGameResultAct.this,
							getString(R.string.service));
					break;
				}
			}
		};

		pd = ProgressDialog.show(this, "", getString(R.string.wait), true,
				false);
		new Thread() {
			@Override
			public void run() {

				int what = 0;

				// Make request and handle exceptions
				try {
					if (!insertGameResult()) {
						what = 1;
					}
				} catch (Exception e) {
					e.printStackTrace();
					what = 2;
				}

				handler.sendEmptyMessage(what);
			}
		}.start();
	}

	private class TheTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			pd = ProgressDialog.show(AddGameResultAct.this, "",
					getString(R.string.initializing), true, false);
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				getPlayers();
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
			TeamAppAlerts.showToast(AddGameResultAct.this,
					getString(R.string.service));
		}
	}

}
