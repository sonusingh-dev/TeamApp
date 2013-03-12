package com.teamapp.ui.club;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.ksoap2.serialization.SoapObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;

import com.teamapp.helper.WebServiceHelper;
import com.teamapp.ui.LoginAct;
import com.teamapp.ui.PlayerDetailsAct;
import com.teamapp.ui.R;
import com.teamapp.ui.alerts.TeamAppAlerts;

public class SearchPlayerAct extends Activity implements OnKeyListener {

	private String methodName;

	private ArrayList<HashMap<String, String>> mPlayerList;

	private EditText txtSearch;

	private RadioButton radPlayer;
	private RadioButton radTeam;

	private ListView lstSearchResult;

	private Drawable drawable;

	private ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_player);

		mPlayerList = new ArrayList<HashMap<String, String>>();

		txtSearch = (EditText) findViewById(R.id.txtSearch);

		radPlayer = (RadioButton) findViewById(R.id.radPlayer);
		radTeam = (RadioButton) findViewById(R.id.radTeam);

		lstSearchResult = (ListView) findViewById(R.id.lstSearchResult);
		lstSearchResult.setDividerHeight(0);

		drawable = getResources().getDrawable(R.drawable.clear);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());

		lstSearchResult.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				filterPlayerList();
				Intent intent = new Intent().setClass(SearchPlayerAct.this,
						PlayerDetailsAct.class);
				intent.putExtra("index", position);
				intent.putParcelableArrayListExtra("playerList",
						(ArrayList<? extends Parcelable>) mPlayerList);
				startActivity(intent);
			}
		});

		txtSearch.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View view, MotionEvent event) {
				// TODO Auto-generated method stub
				// txtSearchOnTouch(view, event);
				return txtSearchOnTouch(view, event);
			}
		});

		txtSearch.setOnKeyListener(this);

	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {

		if (event.getAction() == KeyEvent.ACTION_DOWN) {

			switch (keyCode) {

			case KeyEvent.KEYCODE_ENTER:

				Log.d("KeyEvent", "KEYCODE_ENTER :: " + KeyEvent.KEYCODE_ENTER);

				if (radPlayer.isChecked()) {
					methodName = "SearchPlayersByPlayerName";
				} else if (radTeam.isChecked()) {
					methodName = "SearchPlayersByTeamName";
				}
				
				// Hide android keyboard
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(txtSearch.getWindowToken(), 0);

				String search = txtSearch.getText().toString();
				runSearchResult(search);
				return true;

			case KeyEvent.KEYCODE_BACK:
				Log.d("KeyEvent", "KEYCODE_BACK :: " + KeyEvent.KEYCODE_BACK);
				finish();
				return true;
			}

		}

		return false;
	}

	private void filterPlayerList() {

		HashMap<String, String> item = null;
		for (int i = 0; i < mPlayerList.size(); i++) {
			item = mPlayerList.get(i);
			item.remove("image");
			mPlayerList.set(i, item);
		}
	}
	
	public boolean txtSearchOnTouch(View view, MotionEvent event) {

		if (txtSearch.getCompoundDrawables() == null) {
			// cross is not being shown so no need to handle
			return false;
		}
		if (event.getAction() != MotionEvent.ACTION_DOWN) {
			// only respond to the down type
			return false;
		}

		if (event.getX() > txtSearch.getMeasuredWidth()
				- txtSearch.getPaddingRight() - drawable.getIntrinsicWidth()) {
			txtSearch.setText("");
			return true;
		} else {
			return false;
		}
	}

	private void sortList() {

		Collections.sort(mPlayerList, new Comparator<Map<String, String>>() {
			public int compare(Map<String, String> one, Map<String, String> two) {
				String firstValue = one.get("name");
				String secondValue = two.get("name");
				return firstValue.compareTo(secondValue);
			}
		});

	}

	private void populateList() {

		lstSearchResult.setAdapter(new SearchPlayerAdapter(
				SearchPlayerAct.this, mPlayerList));
	}

	private void getSearchResult(String search) throws Exception {

		mPlayerList.clear();
		SoapObject request = WebServiceHelper.getSOAPRequest(methodName);
		request.addProperty("searchText", search);
		request.addProperty("clubId", LoginAct.clubId);

		SoapObject response = (SoapObject) WebServiceHelper.getSOAPResponse(request,
				methodName);

		if (response != null) {

			String userId = null;
			String userName = null;
			String userImage = null;
			String userTeam = null;
			HashMap<String, String> user = null;

			for (int i = 0; i < response.getPropertyCount(); i++) {

				SoapObject property = (SoapObject) response.getProperty(i);
				userId = property.getProperty("UserId").toString();
				userName = property.getProperty("UserName").toString();
				userImage = property.getProperty("Image").toString();
				userTeam = property.getProperty("TeamName").toString();

				user = new HashMap<String, String>();
				user.put("id", userId);
				user.put("name", userName);
				user.put("image", userImage);
				user.put("team", userTeam);
				mPlayerList.add(user);
			}
		}

	}

	private void runSearchResult(final String search) {

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case 0:
					sortList();
					populateList();
					pd.dismiss();
					break;
				case 1:
					pd.dismiss();
					populateList();
					TeamAppAlerts.showMessageDialog(SearchPlayerAct.this,
							getString(R.string.search_not));
					break;
				case 2:
					pd.dismiss();
					TeamAppAlerts.showToast(SearchPlayerAct.this,
							getString(R.string.service));
					break;
				}
			}
		};

		pd = ProgressDialog.show(SearchPlayerAct.this, "",
				getString(R.string.loading), true, true);
		new Thread(new Runnable() {
			public void run() {
				// TODO Auto-generated method stub
				int what = 0;
				try {
					getSearchResult(search);
					if (mPlayerList.isEmpty()) {
						what = 1;
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
