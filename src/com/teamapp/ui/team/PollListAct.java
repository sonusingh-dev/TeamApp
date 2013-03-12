package com.teamapp.ui.team;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.ksoap2.serialization.SoapObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.teamapp.apis.chart.PieDetailsItem;
import com.teamapp.apis.chart.View_PieChart;
import com.teamapp.helper.WebServiceHelper;
import com.teamapp.helper.Utility;
import com.teamapp.ui.LoginAct;
import com.teamapp.ui.R;
import com.teamapp.ui.alerts.TeamAppAlerts;

public class PollListAct extends TeamMenuAct {

	private String mTeamId;
	private String mClubId;

	private HashMap<String, String> mPoll;
	private ArrayList<HashMap<String, String>> mPollList;

	private TextView lblPollQ;
	private TextView lblPollOption1;
	private TextView lblPollOption2;
	private TextView lblPollOption3;
	private TextView lblPollValue1;
	private TextView lblPollValue2;
	private TextView lblPollValue3;

	private ListView lstPoll;

	private Button btnAdd;

	private LinearLayout bodyLayout;
	private ProgressDialog pd;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.poll_list);

		mTeamId = LoginAct.teamId;
		mClubId = LoginAct.clubId;

		mPoll = new HashMap<String, String>();
		mPollList = new ArrayList<HashMap<String, String>>();

		lblPollQ = (TextView) findViewById(R.id.lblPollQ);

		lblPollOption1 = (TextView) findViewById(R.id.lblPollOption1);
		lblPollOption2 = (TextView) findViewById(R.id.lblPollOption2);
		lblPollOption3 = (TextView) findViewById(R.id.lblPollOption3);

		lblPollValue1 = (TextView) findViewById(R.id.lblPollValue1);
		lblPollValue2 = (TextView) findViewById(R.id.lblPollValue2);
		lblPollValue3 = (TextView) findViewById(R.id.lblPollValue3);

		lstPoll = (ListView) findViewById(R.id.lstPoll);

		btnAdd = (Button) findViewById(R.id.btnAddPoll);

		bodyLayout = (LinearLayout) findViewById(R.id.bodyLayout);

		if (LoginAct.roleId.equals("4")) {
			btnAdd.setVisibility(View.GONE);
		}

		lstPoll.setDividerHeight(0);
		lstPoll.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				HashMap<String, String> poll = mPollList.get(position);
				String isActive = poll.get("isActive");

				if (!Boolean.parseBoolean(isActive)
						&& LoginAct.roleId.equals("4")) {
					TeamAppAlerts.showMessageDialog(PollListAct.this,
							"Poll Is Inactive");
					return;
				}

				Intent intent = new Intent().setClass(PollListAct.this,
						PollDetailsAct.class);
				intent.putExtra("position", position);
				intent.putParcelableArrayListExtra("pollList",
						(ArrayList<? extends Parcelable>) mPollList);
				startActivityForResult(intent, 0);
			}
		});

		btnAdd.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent().setClass(PollListAct.this,
						AddPollAct.class);
				startActivityForResult(intent, 0);
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

	private void populateChart() {

		lblPollQ.setText(mPoll.get("pollQ"));

		lblPollOption1.setText(mPoll.get("option1"));
		lblPollOption2.setText(mPoll.get("option2"));
		lblPollOption3.setText(mPoll.get("option3"));

		lblPollValue1.setText(mPoll.get("optionValue1"));
		lblPollValue2.setText(mPoll.get("optionValue2"));
		lblPollValue3.setText(mPoll.get("optionValue3"));

		List<PieDetailsItem> piedata = new ArrayList<PieDetailsItem>(0);

		PieDetailsItem item;
		int maxCount = 0;
		int itemCount = 0;
		int item1 = (int) Double.parseDouble(mPoll.get("optionValue1"));
		int item2 = (int) Double.parseDouble(mPoll.get("optionValue2"));
		int item3 = (int) Double.parseDouble(mPoll.get("optionValue3"));
		int item4 = 100;
		int total = item1 + item2 + item3;

		if (total != 0) {
			item4 = 0;
		}

		int items[] = { item1, item2, item3, item4 };
		int colors[] = { 0xff7D7B7B, 0xff0DA10F, 0xff0D76C7, 0xff48bcd5 };
		String itemslabel[] = { mPoll.get("option1"), mPoll.get("option2"),
				mPoll.get("option3"), mPoll.get("option4") };
		for (int i = 0; i < items.length; i++) {
			itemCount = items[i];
			item = new PieDetailsItem();
			item.count = itemCount;
			item.label = itemslabel[i];
			item.color = colors[i];
			piedata.add(item);
			maxCount = maxCount + itemCount;
		}
		int size = 155;
		int BgColor = 0x00000000;
		Bitmap mBaggroundImage = Bitmap.createBitmap(size, size,
				Bitmap.Config.ARGB_8888);
		View_PieChart piechart = new View_PieChart(this);
		piechart.setLayoutParams(new LayoutParams(size, size));
		piechart.setGeometry(size, size, 2, 2, 2, 2, 2130837504);
		piechart.setSkinparams(BgColor);
		piechart.setData(piedata, maxCount);
		piechart.invalidate();
		piechart.draw(new Canvas(mBaggroundImage));
		piechart = null;
		ImageView mImageView = new ImageView(this);
		mImageView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		mImageView.setBackgroundColor(BgColor);
		mImageView.setImageBitmap(mBaggroundImage);
		LinearLayout finalLayout = (LinearLayout) findViewById(R.id.pie_container);
		finalLayout.removeAllViews();
		finalLayout.addView(mImageView);
	}

	private void populateList() {

		lstPoll.setAdapter(new SimpleAdapter(this, mPollList,
				R.layout.poll_list_item,
				new String[] { "day", "month", "pollQ" }, new int[] {
						R.id.lblDay, R.id.lblMonth, R.id.lblQuestion }));
		Utility.setListViewHeightBasedOnChildren(lstPoll);

	}

	private void getPolls() throws Exception {

		String METHOD_NAME = "GetPollListByTeamIdOrderByPollDate";

		mPollList.clear();
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss");

		SoapObject request = WebServiceHelper.getSOAPRequest(METHOD_NAME);
		request.addProperty("teamId", mTeamId);
		request.addProperty("clubId", mClubId);

		SoapObject response = (SoapObject) WebServiceHelper.getSOAPResponse(request,
				METHOD_NAME);

		if (response != null) {

			String pollId = null;
			String pollQ = null;
			String isActive = null;
			String optionId1 = null;
			String optionId2 = null;
			String optionId3 = null;

			String day = null;
			String month = null;
			String year = null;
			HashMap<String, String> poll = null;

			for (int i = 0; i < response.getPropertyCount(); i++) {

				SoapObject property = (SoapObject) response.getProperty(i);

				pollId = property.getProperty("PollId").toString();
				pollQ = property.getProperty("Question").toString();
				isActive = property.getProperty("IsActive").toString();
				optionId1 = property.getProperty("Option1Id").toString();
				optionId2 = property.getProperty("Option2Id").toString();
				optionId3 = property.getProperty("Option3Id").toString();

				String dateTime = property.getProperty("CreatedOn").toString();

				try {
					calendar.setTime(dateFormat.parse(dateTime));

					day = String.valueOf(calendar.get(Calendar.DATE));
					month = new DateUtils().getMonthString(
							calendar.get(Calendar.MONTH),
							DateUtils.LENGTH_MEDIUM);
					year = String.valueOf(calendar.get(Calendar.YEAR));

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				poll = new HashMap<String, String>();
				poll.put("pollId", pollId);
				poll.put("pollQ", pollQ);
				poll.put("isActive", isActive);
				poll.put("day", day);
				poll.put("month", month);
				poll.put("year", year);
				poll.put("optionId1", optionId1);
				poll.put("optionId2", optionId2);
				poll.put("optionId3", optionId3);
				mPollList.add(poll);

			}
		}
	}

	private void getLatestPoll() throws Exception {

		String METHOD_NAME = "GetLatestPoll";

		SoapObject request = WebServiceHelper.getSOAPRequest(METHOD_NAME);
		request.addProperty("teamId", mTeamId);
		request.addProperty("clubId", mClubId);

		SoapObject response = (SoapObject) WebServiceHelper.getSOAPResponse(request,
				METHOD_NAME);

		if (response != null) {
			String pollId = response.getProperty("PollId").toString();

			Log.d("Latest PollId", "response :: " + response.toString());
			if (pollId != null) {
				getLatestPollDetails(pollId);
			}
		}
	}

	private void getLatestPollDetails(String pollId) throws Exception {

		String METHOD_NAME = "GetPollResultInPercentage";

		mPoll.clear();
		SoapObject request = WebServiceHelper.getSOAPRequest(METHOD_NAME);
		request.addProperty("pollId", pollId);
		request.addProperty("clubId", mClubId);

		SoapObject response = (SoapObject) WebServiceHelper.getSOAPResponse(request,
				METHOD_NAME);

		if (response != null) {

			String option1 = response.getProperty("Option1").toString();
			if (option1.contains("anyType{}")) {
				option1 = null;
			}

			String option2 = response.getProperty("Option2").toString();
			if (option2.contains("anyType{}")) {
				option2 = null;
			}

			String option3 = response.getProperty("Option3").toString();
			if (option3.contains("anyType{}")) {
				option3 = null;
			}

			String pollQ = response.getProperty("Question").toString();

			String optionValue1 = response.getProperty("Option1Percenttage")
					.toString();

			String optionValue2 = response.getProperty("Option2Percenttage")
					.toString();

			String optionValue3 = response.getProperty("Option3Percenttage")
					.toString();

			mPoll.put("pollQ", pollQ);

			mPoll.put("option1", option1);
			mPoll.put("option2", option2);
			mPoll.put("option3", option3);

			mPoll.put("optionValue1", optionValue1);
			mPoll.put("optionValue2", optionValue2);
			mPoll.put("optionValue3", optionValue3);
		}
	}

	private class TheTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			bodyLayout.setVisibility(View.GONE);
			pd = ProgressDialog.show(PollListAct.this, "",
					getString(R.string.loading), true, false);
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				getPolls();
				getLatestPoll();
			} catch (Exception e) {
				cancel(false);
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (mPollList.isEmpty()) {
				pd.dismiss();
				TeamAppAlerts.showToast(PollListAct.this,
						getString(R.string.no) + " " + getString(R.string.poll)
								+ " " + getString(R.string.available));
				return;
			}
			populateChart();
			populateList();
			bodyLayout.setVisibility(View.VISIBLE);
			pd.dismiss();
		}

		@Override
		protected void onCancelled() {
			pd.dismiss();
			TeamAppAlerts.showToast(PollListAct.this,
					getString(R.string.service));
		}
	}

}
