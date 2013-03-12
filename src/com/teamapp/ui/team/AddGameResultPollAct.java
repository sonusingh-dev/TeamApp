package com.teamapp.ui.team;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;

import com.teamapp.helper.Utility;
import com.teamapp.helper.WebServiceHelper;
import com.teamapp.ui.LoginAct;
import com.teamapp.ui.R;
import com.teamapp.ui.alerts.TeamAppAlerts;

public class AddGameResultPollAct extends Activity {

	private String question;
	private String dateTime;
	private String name;
	private List<String> selectedPlayers;
	private ArrayList<? extends Parcelable> playersList;

	private TextView txvQuestion;
	private ListView lsvPlayer;
	private Button btnSubmit;

	private ProgressDialog pd;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_game_result_poll);

		selectedPlayers = new ArrayList<String>();
		dateTime = getIntent().getStringExtra("dateTime");
		name = getIntent().getStringExtra("name");
		playersList = getIntent().getParcelableArrayListExtra("players");

		txvQuestion = (TextView) findViewById(R.id.txvQuestion);
		lsvPlayer = (ListView) findViewById(R.id.lsvPlayer);
		btnSubmit = (Button) findViewById(R.id.btnSubmit);

		btnSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				validate();
			}
		});

		populateItem();
	}

	private void populateItem() {

		StringBuilder date = null;
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss");

		try {
			calendar.setTime(dateFormat.parse(dateTime));
			int year = calendar.get(Calendar.YEAR);
			int day = calendar.get(Calendar.DAY_OF_MONTH);

			String month = String.format("%tb", calendar);

			date = new StringBuilder();
			date.append(Utility.pad(day)).append(" ").append(month).append(" ")
					.append(year);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		question = "Who will be Man Of The Match for " + name + " played on "
				+ date;
		txvQuestion.setText(question);
		lsvPlayer.setAdapter(new PlayerListAdapter(this));
	}

	private void validate() {

		for (int i = 0; i < selectedPlayers.size(); i++) {
			Log.d("AddGameResultPollAct", "selectedPlayer :: "
					+ selectedPlayers.get(i));
		}

		if (selectedPlayers.size() != 3) {
			TeamAppAlerts.showMessageDialog(AddGameResultPollAct.this,
					"Select atleast 3 players");
			return;
		}

		runInsert();
	}

	private boolean insertPoll() throws Exception {

		StringBuilder options = new StringBuilder();
		for (int i = 0; i < selectedPlayers.size(); i++) {
			options.append("<voet:Option").append(i + 1).append(">")
					.append(selectedPlayers.get(i)).append("</voet:Option")
					.append(i + 1).append(">");
		}

		String METHOD_NAME = "InsertPoll";
		String envelope = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\" xmlns:voet=\"http://schemas.datacontract.org/2004/07/VoetBall.Entities.DataObjects\">"
				+ "<soapenv:Header/>"
				+ "<soapenv:Body>"
				+ "<tem:InsertPoll>"
				+ "<tem:dPoll>"
				+ "%s"
				+ "<voet:Question>%s</voet:Question>"
				+ "<voet:CreatedByUserId>%s</voet:CreatedByUserId>"
				+ "<voet:IsActive>%s</voet:IsActive>"
				+ "<voet:Option1Id>0</voet:Option1Id>"
				+ "<voet:Option2Id>0</voet:Option2Id>"
				+ "<voet:Option3Id>0</voet:Option3Id>"
				+ "<voet:PollId>0</voet:PollId>"
				+ "</tem:dPoll>"
				+ "<tem:clubId>%s</tem:clubId>"
				+ "</tem:InsertPoll>"
				+ "</soapenv:Body>" + "</soapenv:Envelope>";

		String request = String.format(envelope, options, question,
				LoginAct.userId, true, LoginAct.clubId);

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
					finish();
					break;
				case 1:
					pd.dismiss();
					TeamAppAlerts.showToast(AddGameResultPollAct.this,
							getString(R.string.insert_failed) + ""
									+ getString(R.string.poll));
					break;
				case 2:
					pd.dismiss();
					TeamAppAlerts.showToast(AddGameResultPollAct.this,
							getString(R.string.service));
					break;
				}
			}
		};

		pd = ProgressDialog.show(AddGameResultPollAct.this, "",
				getString(R.string.wait), true, false);
		new Thread() {
			@Override
			public void run() {

				int what = 0;

				// Make request and handle exceptions
				try {
					if (!insertPoll()) {
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

	public static class ViewHolder {
		public TextView lblName;
		public CheckBox chkStatus;
	}

	private class PlayerListAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		public PlayerListAdapter(Context context) {
			mInflater = LayoutInflater.from(context);
		}

		public int getCount() {
			return playersList.size();
		}

		public HashMap<String, String> getItem(int position) {
			return (HashMap<String, String>) playersList.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(
						R.layout.list_item_with_checkbox, null);
				holder = new ViewHolder();
				holder.lblName = (TextView) convertView
						.findViewById(R.id.lblName);
				holder.chkStatus = (CheckBox) convertView
						.findViewById(R.id.chkStatus);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			HashMap<String, String> item = getItem(position);
			String name = item.get("userName");
			// String status = item.get("status");

			holder.lblName.setText(name);
			// holder.chkStatus.setChecked(Boolean.parseBoolean(status));

			holder.chkStatus
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							// TODO Auto-generated method stub
							HashMap<String, String> item = getItem(position);
							// item.put("status", String.valueOf(isChecked));
							// playersList.set(position, item);
							if (isChecked) {
								selectedPlayers.add(item.get("userName"));
							} else {
								selectedPlayers.remove(item.get("userName"));
							}
						}
					});

			return convertView;
		}
	}

}
