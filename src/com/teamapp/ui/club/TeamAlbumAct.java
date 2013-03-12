package com.teamapp.ui.club;

import java.util.ArrayList;
import java.util.HashMap;

import org.kobjects.base64.Base64;
import org.ksoap2.serialization.SoapObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.teamapp.helper.WebServiceHelper;
import com.teamapp.ui.LoginAct;
import com.teamapp.ui.PlayerDetailsAct;
import com.teamapp.ui.R;
import com.teamapp.ui.TeamAlbumAdapter;
import com.teamapp.ui.alerts.TeamAppAlerts;

public class TeamAlbumAct extends Activity {

	private String logo;
	private String teamPic;
	private String teamId;
	private String teamName;

	private ArrayList<HashMap<String, String>> mPlayerList;

	private TextView lblTitle;

	private ImageView imgLogo;

	private GridView grdTeamAlbulm;

	private ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.album_club);

		teamId = getIntent().getStringExtra("id");
		teamName = getIntent().getStringExtra("name");
		mPlayerList = new ArrayList<HashMap<String, String>>();

		lblTitle = (TextView) findViewById(R.id.lblTitle);

		imgLogo = (ImageView) findViewById(R.id.imgLogo);

		grdTeamAlbulm = (GridView) findViewById(R.id.grdTeamAlbum);

		lblTitle.setText(teamName);

		grdTeamAlbulm.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				Intent intent = null;
				if (position == 0) {
					intent = getIntent().setClass(TeamAlbumAct.this,
							TeamDetailsAct.class);
					startActivity(intent);
				} else {
					filterPlayerList();
					intent = new Intent().setClass(TeamAlbumAct.this,
							PlayerDetailsAct.class);
					intent.putExtra("index", position - 1);
					intent.putParcelableArrayListExtra("playerList",
							(ArrayList<? extends Parcelable>) mPlayerList);
					startActivity(intent);
				}
			}
		});

		new TheTask().execute();

	}

	private void filterPlayerList() {

		HashMap<String, String> item = null;
		for (int i = 0; i < mPlayerList.size(); i++) {
			item = mPlayerList.get(i);
			item.remove("image");
			mPlayerList.set(i, item);
		}
	}

	private void getTeamAlbum() throws Exception {

		String METHOD_NAME = "GetTeamAlbumByTeamId";

		SoapObject request = WebServiceHelper.getSOAPRequest(METHOD_NAME);
		request.addProperty("teamId", teamId);
		request.addProperty("clubId", LoginAct.clubId);

		SoapObject response = (SoapObject) WebServiceHelper.getSOAPResponse(request,
				METHOD_NAME);

		if (response.getProperty("logo") != null) {
			SoapObject soapLogo = (SoapObject) response.getProperty("logo");
			logo = soapLogo.getProperty("Image").toString();
		}

		if (response.getProperty("teamPicture") != null) {
			SoapObject soapTeamPic = (SoapObject) response
					.getProperty("teamPicture");
			teamPic = soapTeamPic.getProperty("Image").toString();
		}

		if (response.getProperty("players") != null) {

			String playerId = null;
			String playerName = null;
			String playerImage = null;
			SoapObject soapPlayers = (SoapObject) response
					.getProperty("players");

			for (int i = 0; i < soapPlayers.getPropertyCount(); i++) {

				SoapObject property = (SoapObject) soapPlayers.getProperty(i);

				if (property.getProperty("Id") != null) {
					playerId = property.getProperty("Id").toString();
				}

				if (property.getProperty("PlayerName") != null) {
					playerName = property.getProperty("PlayerName").toString();
				}

				if (property.getProperty("Image") != null) {
					playerImage = property.getProperty("Image").toString();
				}

				HashMap<String, String> player = new HashMap<String, String>();
				player.put("id", playerId);
				player.put("name", playerName);
				player.put("image", playerImage);
				mPlayerList.add(player);
			}
		}
	}

	private void populateTeamAlbum() {

		try {
			byte[] temp = Base64.decode(logo);
			Bitmap bmpImage = BitmapFactory.decodeByteArray(temp, 0,
					temp.length);
			imgLogo.setImageBitmap(bmpImage);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		TeamAlbumAdapter adapter = new TeamAlbumAdapter(this);
		adapter.addItem(teamPic);

		for (int index = 0; index < mPlayerList.size(); index++) {
			HashMap<String, String> player = mPlayerList.get(index);
			adapter.addItem(player.get("image"));
		}
		grdTeamAlbulm.setAdapter(adapter);

	}

	private class TheTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			pd = ProgressDialog.show(TeamAlbumAct.this, "",
					getString(R.string.loading), true, true);
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				getTeamAlbum();
			} catch (Exception e) {
				cancel(false);
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			populateTeamAlbum();
			pd.dismiss();
		}

		@Override
		protected void onCancelled() {
			pd.dismiss();
			TeamAppAlerts.showToast(TeamAlbumAct.this,
					getString(R.string.service));
		}
	}

}
