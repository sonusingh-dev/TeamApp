package com.teamapp.ui.team;

import java.io.ByteArrayOutputStream;
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
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.teamapp.apis.facebook.ShareOnFacebook;
import com.teamapp.apis.hyves.ShareOnHyves;
import com.teamapp.apis.twitter.ShareOnTwitter;
import com.teamapp.helper.WebServiceHelper;
import com.teamapp.helper.XmlParser;
import com.teamapp.ui.LoginAct;
import com.teamapp.ui.PlayerDetailsAct;
import com.teamapp.ui.R;
import com.teamapp.ui.StatsListAdapter;
import com.teamapp.ui.alerts.TeamAppAlerts;

public class StatsListAct extends Activity {

	private static final int COUNT = 0;
	private static final int TROPHY = 1;

	private boolean isInit = true;

	private int key = 0;
	private int seasonIndex;
	
	private int index;
	private int league;
	private int cup;
	private int tournament;
	private int friendlyMatches;
	
	private String mSeasonId;
	private String title;
	private String METHOD_NAME;

	private ArrayList<HashMap<String, String>> mPlayerList;
	private ArrayList<HashMap<String, String>> seasonList;

	private TextView lblTitle;

	private Spinner spnSeason;

	private CheckBox chkLeague;
	private CheckBox chkCup;
	private CheckBox chkTournament;
	private CheckBox chkFriendlyMatches;

	private Button btnShare;

	private ListView lstStats;

	private ProgressDialog pd;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stats_list);

		index = getIntent().getIntExtra("index", 0);
		title = getIntent().getStringExtra("title");
		METHOD_NAME = getIntent().getStringExtra("METHOD_NAME");
		seasonList = new ArrayList<HashMap<String, String>>();
		mPlayerList = new ArrayList<HashMap<String, String>>();

		lblTitle = (TextView) findViewById(R.id.lblTitle);

		spnSeason = (Spinner) findViewById(R.id.spnSeason);

		chkLeague = (CheckBox) findViewById(R.id.chkLeague);
		chkCup = (CheckBox) findViewById(R.id.chkCup);
		chkTournament = (CheckBox) findViewById(R.id.chkTournament);
		chkFriendlyMatches = (CheckBox) findViewById(R.id.chkFriendlyMatches);

		btnShare = (Button) findViewById(R.id.btnShare);

		lstStats = (ListView) findViewById(R.id.lstStats);

		lblTitle.setText(title);

		lstStats.setDividerHeight(1);
		lstStats.setDivider(new ColorDrawable(0xffE6E6E6));

		lstStats.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				filterPlayerList();
				Intent intent = new Intent().setClass(StatsListAct.this,
						PlayerDetailsAct.class);
				intent.putExtra("index", position);
				intent.putParcelableArrayListExtra("playerList",
						(ArrayList<? extends Parcelable>) mPlayerList);
				startActivity(intent);
			}
		});

		spnSeason.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				// TODO Auto-generated method stub
				if (!isInit) {
					mSeasonId = seasonList.get(pos).get("seasonId");
					if (mSeasonId != null) {
						runTeamStats();
					}
				}
			}

			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
			}
		});

		league = 1;
		chkLeague.setChecked(true);
		chkLeague.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					league = 1;
				} else {
					league = 0;
				}
				runTeamStats();
			}
		});

		cup = 2;
		chkCup.setChecked(true);
		chkCup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					cup = 2;
				} else {
					cup = 0;
				}
				runTeamStats();
			}
		});

		tournament = 3;
		chkTournament.setChecked(true);
		chkTournament.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					tournament = 3;
				} else {
					tournament = 0;
				}
				runTeamStats();
			}
		});

		friendlyMatches = 4;
		chkFriendlyMatches.setChecked(true);
		chkFriendlyMatches
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						if (isChecked) {
							friendlyMatches = 4;
						} else {
							friendlyMatches = 0;
						}
						runTeamStats();
					}
				});

		btnShare.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {
				// TODO Auto-generated method stub
				if (!mPlayerList.isEmpty()) {
					showSocialMediaPopup(view);
				} else {
					TeamAppAlerts.showMessageDialog(StatsListAct.this,
							"Nothing to share");
				}
			}
		});

		runDefault();

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	private void filterPlayerList() {

		HashMap<String, String> item = null;
		for (int i = 0; i < mPlayerList.size(); i++) {
			item = mPlayerList.get(i);
			item.remove("image");
			mPlayerList.set(i, item);
		}
	}

	private void populateSeason() {

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
		spnSeason.setPrompt(getString(R.string.select_season));
		spnSeason.setAdapter(seasonAdapter);
		spnSeason.setSelection(seasonIndex);

	}

	private void sortList() {

		// Sort by trophy
		Collections.sort(mPlayerList, new Comparator<Map<String, String>>() {
			public int compare(Map<String, String> one, Map<String, String> two) {
				String firstValue = one.get("trophy");
				String secondValue = two.get("trophy");
				return firstValue.compareTo(secondValue);
			}
		});

		// Sort by count
		Comparator<Map<String, String>> comparator = Collections
				.reverseOrder(new Comparator<Map<String, String>>() {
					public int compare(Map<String, String> one,
							Map<String, String> two) {
						String firstValue = one.get("count");
						String secondValue = two.get("count");
						if (firstValue == null) {
							firstValue = "00";
						} else if (secondValue == null) {
							secondValue = "00";
						}
						return new Double(firstValue).compareTo(new Double(
								secondValue));
					}
				});
		Collections.sort(mPlayerList, comparator);
	}

	private void populateList() {
		lstStats.setAdapter(new StatsListAdapter(StatsListAct.this, mPlayerList));
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

	private void getTeamStats() throws Exception {

		String envelope = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\" xmlns:arr=\"http://schemas.microsoft.com/2003/10/Serialization/Arrays\">"
				+ "<soapenv:Header/>"
				+ "<soapenv:Body>"
				+ "<tem:%s>"
				+ "<tem:seasonId>%s</tem:seasonId>"
				+ "<tem:teamId>%s</tem:teamId>"
				+ "<tem:matchTypeIds>"
				+ "<arr:int>%s</arr:int>"
				+ "<arr:int>%s</arr:int>"
				+ "<arr:int>%s</arr:int>"
				+ "<arr:int>%s</arr:int>"
				+ "</tem:matchTypeIds>"
				+ "<tem:clubId>%s</tem:clubId>"
				+ "</tem:%s>" + "</soapenv:Body>" + "</soapenv:Envelope>";

		String request = String.format(envelope, METHOD_NAME, mSeasonId,
				LoginAct.teamId, league, cup, tournament, friendlyMatches,
				LoginAct.clubId, METHOD_NAME);

		String response = (String) WebServiceHelper.callWebService(METHOD_NAME,
				request);

		switch (index) {
		case 0:
			mPlayerList = XmlParser.getTopScorersList(response);
			break;
		case 1:
			mPlayerList = XmlParser.getAttendaceList(response);
			break;
		case 2:
			key = 1;
			mPlayerList = XmlParser.getGoalsList(response);
			break;
		case 3:
			mPlayerList = XmlParser.getAssistList(response);
			break;
		default:
			break;

		}

	}

	// PopupWindow for Article sharing
	private void showSocialMediaPopup(View view) {

		LayoutInflater inflater = LayoutInflater.from(this);
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

		final String message = "Sharing the " + title + " table of "
				+ LoginAct.teamName;

		btnFacebook.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				popupWindow.dismiss();
				byte[] imageBytes = getScreen();
				ShareOnFacebook shareOnFacebook = new ShareOnFacebook(
						StatsListAct.this, message, imageBytes);
				shareOnFacebook.share();
			}
		});

		btnTwitter.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				popupWindow.dismiss();
				byte[] imageBytes = getScreen();
				ShareOnTwitter shareOnTwitter = new ShareOnTwitter(
						StatsListAct.this, message, imageBytes);
				shareOnTwitter.share();
			}
		});

		btnHyves.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				popupWindow.dismiss();
				ShareOnHyves shareOnHyves = new ShareOnHyves(StatsListAct.this,
						message);
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

	private byte[] getScreen() {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		lstStats.setDrawingCacheEnabled(true);
		Bitmap bitmap = lstStats.getDrawingCache();
		if (bitmap != null) {
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
			return baos.toByteArray();
		}
		return null;
	}

	private void runDefault() {

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case 0:					
					populateSeason();
					runTeamStats();
					// pd.dismiss();
					break;
				case 1:
					pd.dismiss();
					TeamAppAlerts.showToast(StatsListAct.this,
							getString(R.string.service));
					break;
				}
			}
		};

		pd = ProgressDialog.show(StatsListAct.this, "",
				getString(R.string.loading), true, false);

		new Thread(new Runnable() {
			public void run() {
				// TODO Auto-generated method stub
				int what = 0;
				try {
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

	private void runTeamStats() {

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				isInit = false;
				switch (msg.what) {
				case 0:
					sortList();
					populateList();
					pd.dismiss();
					break;
				case 1:
					pd.dismiss();
					TeamAppAlerts.showToast(StatsListAct.this,
							getString(R.string.season_not));
					break;
				case 2:
					pd.dismiss();
					TeamAppAlerts.showToast(StatsListAct.this,
							getString(R.string.service));
					break;
				}
			}
		};

		if (!isInit) {
			pd = ProgressDialog.show(StatsListAct.this, "",
					getString(R.string.loading), true, false);
		}

		new Thread(new Runnable() {
			public void run() {
				// TODO Auto-generated method stub
				int what = 0;
				try {
					if (seasonList.isEmpty()) {
						what = 1;
					} else {
						getTeamStats();
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
