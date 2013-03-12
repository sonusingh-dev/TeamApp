package com.teamapp.ui.club;

import java.util.ArrayList;
import java.util.HashMap;

import org.kobjects.base64.Base64;
import org.ksoap2.serialization.SoapObject;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.teamapp.helper.WebServiceHelper;
import com.teamapp.helper.Utility;
import com.teamapp.ui.LoginAct;
import com.teamapp.ui.R;

public class TeamDetailsAct extends Activity {

	private int index;

	private HashMap<String, String> mTeam;
	private ArrayList<Parcelable> mTeamList;

	private TextView lblTitle;

	private ViewPager viewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_pager_layout);

		index = getIntent().getIntExtra("index", 0);
		mTeamList = getIntent().getParcelableArrayListExtra("teamList");

		mTeam = new HashMap<String, String>();

		lblTitle = (TextView) findViewById(R.id.lblTitle);

		viewPager = (ViewPager) findViewById(R.id.viewPager);
		viewPager.setAdapter(new TeamPagerAdapter());
		viewPager.setCurrentItem(index);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			// This method will be invoked when a new page becomes selected.
			public void onPageSelected(int position) {
				mTeam = (HashMap<String, String>) mTeamList.get(position);
				lblTitle.setText(mTeam.get("name"));
			}

			// This method will be invoked when the current page is scrolled,
			// either as part of a programmatically initiated smooth scroll or a
			// user initiated touch scroll.
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {
				// TODO Auto-generated method stub
			}

			// Called when the scroll state changes.
			public void onPageScrollStateChanged(int state) {

			}
		});

		mTeam = (HashMap<String, String>) mTeamList.get(index);
		lblTitle.setText(mTeam.get("name"));

	}

	private void getTeamDetails(int index) throws Exception {

		String METHOD_NAME = "GetTeamStatisticsByTeamIdInCurrentSeason";

		mTeam = (HashMap<String, String>) mTeamList.get(index);

		SoapObject request = WebServiceHelper.getSOAPRequest(METHOD_NAME);
		request.addProperty("teamId", mTeam.get("id"));
		request.addProperty("clubId", LoginAct.clubId);

		SoapObject response = (SoapObject) WebServiceHelper.getSOAPResponse(request,
				METHOD_NAME);

		if (response != null) {

			String temp = null;
			
			String avgGoals = null;
			if (response.getProperty("AvgGoalsPerGame") != null) {
				temp = response.getProperty("AvgGoalsPerGame").toString();
				avgGoals = Utility.anyTypeConversion(temp);
			}

			String ranking = null;
			if (response.getProperty("ClubRanking") != null) {
				temp = response.getProperty("ClubRanking").toString();
				ranking = Utility.anyTypeConversion(temp);
			}

			String assists = null;
			if (response.getProperty("KingOfAssists") != null) {
				temp = response.getProperty("KingOfAssists").toString();
				assists = Utility.anyTypeConversion(temp);
			}

			String image = null;
			if (response.getProperty("TeamImage") != null) {
				temp = response.getProperty("TeamImage").toString();
				image = Utility.anyTypeConversion(temp);
			}

			String scorer = null;
			if (response.getProperty("TopScorer") != null) {
				temp = response.getProperty("TopScorer").toString();
				scorer = Utility.anyTypeConversion(temp);
			}

			String games = null;
			if (response.getProperty("TotalGames") != null) {
				temp = response.getProperty("TotalGames").toString();
				games = Utility.anyTypeConversion(temp);
			}

			String goals = null;
			if (response.getProperty("TotalGoals") != null) {
				temp = response.getProperty("TotalGoals").toString();
				goals = Utility.anyTypeConversion(temp);
			}

			mTeam.put("avgGoals", avgGoals);
			mTeam.put("ranking", ranking);
			mTeam.put("assists", assists);
			mTeam.put("image", image);
			mTeam.put("scorer", scorer);
			mTeam.put("games", games);
			mTeam.put("goals", goals);
		}
	}

	private void populateItem(int index, ViewHolder holder) {

		try {
			byte[] temp = Base64.decode(mTeam.get("image"));
			Bitmap bmpImage = BitmapFactory.decodeByteArray(temp, 0,
					temp.length);
			holder.imgTeam.setImageBitmap(bmpImage);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		holder.lblGames.setText(mTeam.get("games"));

		holder.lblGoals.setText(mTeam.get("goals"));

		holder.lblRanking.setText(mTeam.get("ranking"));

		holder.lblAvgGoals.setText(mTeam.get("avgGoals"));

		holder.lblScorer.setText(mTeam.get("scorer"));

		holder.lblAssists.setText(mTeam.get("assists"));

	}

	private void doInBackground(final int index, final ViewHolder holder,
			final View view) {

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				view.setVisibility(View.VISIBLE);
				populateItem(index, holder);
			}
		};

		view.setVisibility(View.GONE);
		new Thread(new Runnable() {
			public void run() {
				// TODO Auto-generated method stub
				int what = 0;
				try {
					getTeamDetails(index);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					what = 1;
					e.printStackTrace();
				}
				handler.sendMessage(handler.obtainMessage(what));
			}
		}).start();

	}

	private static class ViewHolder {

		public TextView lblGames;
		public TextView lblGoals;
		public TextView lblRanking;
		public TextView lblAvgGoals;
		public TextView lblScorer;
		public TextView lblAssists;

		public ImageView imgTeam;

		ViewHolder(View view) {

			lblGames = (TextView) view.findViewById(R.id.lblGames);
			lblGoals = (TextView) view.findViewById(R.id.lblGoals);
			lblRanking = (TextView) view.findViewById(R.id.lblRanking);
			lblAvgGoals = (TextView) view.findViewById(R.id.lblAvgGoals);
			lblScorer = (TextView) view.findViewById(R.id.lblScorer);
			lblAssists = (TextView) view.findViewById(R.id.lblAssists);
			imgTeam = (ImageView) view.findViewById(R.id.imgTeam);
		}
	}

	private class TeamPagerAdapter extends PagerAdapter {

		private LayoutInflater mInflater;

		public TeamPagerAdapter() {
			mInflater = LayoutInflater.from(TeamDetailsAct.this);
		}

		@Override
		public int getCount() {
			return mTeamList.size();
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public Object instantiateItem(View collection, int position) {

			View view = mInflater.inflate(R.layout.team_details_pager_item,
					null);
			ViewHolder holder = new ViewHolder(view);

			doInBackground(position, holder, view);

			((ViewPager) collection).addView(view);

			return view;
		}

		@Override
		public void destroyItem(View collection, int position, Object object) {
			((ViewPager) collection).removeView((View) object);
		}

		@Override
		public boolean isViewFromObject(View collection, Object object) {
			return collection == (View) object;
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void restoreState(Parcelable parcelable, ClassLoader loader) {

		}

		@Override
		public void startUpdate(View collection) {

		}

		@Override
		public void finishUpdate(View collection) {

		}

	}

}
