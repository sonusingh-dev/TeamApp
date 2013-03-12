package com.teamapp.ui.club;

import java.util.ArrayList;
import java.util.HashMap;

import org.ksoap2.serialization.SoapObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.teamapp.helper.WebServiceHelper;
import com.teamapp.ui.LoginAct;
import com.teamapp.ui.R;
import com.teamapp.ui.alerts.TeamAppAlerts;

public class ClubdenAct extends ClubMenuAct {

	private ClubdenAct mClubdenAct;

	private ArrayList<HashMap<String, String>> mItemList;

	private TextView lblTitle;

	private ListView lstClubMembers;

	private Button btnSearch;

	private ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.clubden);

		mClubdenAct = this;
		mItemList = new ArrayList<HashMap<String, String>>();

		lblTitle = (TextView) findViewById(R.id.lblTitle);

		lstClubMembers = (ListView) findViewById(R.id.lstView);

		btnSearch = (Button) findViewById(R.id.btnSearch);

		lstClubMembers.setDivider(new ColorDrawable(0xff48bcd5));
		lstClubMembers.setDividerHeight(1);

		lstClubMembers.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent().setClass(mClubdenAct,
						TeamAlbumAct.class);
				HashMap<String, String> team = mItemList.get(position);

				intent.putExtra("id", team.get("id"));
				intent.putExtra("name", team.get("name"));
				intent.putExtra("index", position);
				intent.putParcelableArrayListExtra("teamList",
						(ArrayList<? extends Parcelable>) mItemList);
				intent.putExtra("position", position);
				startActivity(intent);

			}
		});

		btnSearch.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {
				// TODO Auto-generated method stub
				Intent intent = new Intent().setClass(mClubdenAct,
						SearchPlayerAct.class);
				startActivity(intent);
			}
		});

		new TheTask().execute();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void getClubMembersList() throws Exception {

		String METHOD_NAME = "GetTeamListForClub";

		SoapObject request = WebServiceHelper.getSOAPRequest(METHOD_NAME);
		request.addProperty("clubId", LoginAct.clubId);

		SoapObject response = (SoapObject) WebServiceHelper.getSOAPResponse(request,
				METHOD_NAME);

		if (response != null) {

			String teamId = null;
			String teamName = null;
			HashMap<String, String> clubMember = null;
			for (int i = 0; i < response.getPropertyCount(); i++) {

				SoapObject property = (SoapObject) response.getProperty(i);
				teamId = property.getProperty("TeamId").toString();
				teamName = property.getProperty("TeamName").toString();

				clubMember = new HashMap<String, String>();
				clubMember.put("id", teamId);
				clubMember.put("name", teamName);
				mItemList.add(clubMember);
			}
		}
	}

	private void populateList() {
		lstClubMembers.setAdapter(new SimpleAdapter(mClubdenAct, mItemList,
				R.layout.rounded_border_list_view_item,
				new String[] { "name" }, new int[] { R.id.lblItem }));
	}

	private class TheTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			pd = ProgressDialog.show(mClubdenAct, "",
					getString(R.string.loading), true, false);
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				getClubMembersList();
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
			TeamAppAlerts.showToast(mClubdenAct, getString(R.string.service));
		}
	}
}
