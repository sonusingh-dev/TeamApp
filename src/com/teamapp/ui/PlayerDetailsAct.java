package com.teamapp.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import com.teamapp.ui.R;

public class PlayerDetailsAct extends Activity {

	private int index;
	private Calendar mCalendar;
	private SimpleDateFormat mDateFormat;

	private HashMap<String, String> mPlayer;
	private ArrayList<Parcelable> mPlayerList;

	private TextView lblTitle;

	private ViewPager viewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_pager_layout);

		index = getIntent().getIntExtra("index", 0);
		mPlayerList = getIntent().getParcelableArrayListExtra("playerList");

		mCalendar = Calendar.getInstance();
		mDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

		lblTitle = (TextView) findViewById(R.id.lblTitle);

		viewPager = (ViewPager) findViewById(R.id.viewPager);
		viewPager.setAdapter(new PlayerPagerAdapter());
		viewPager.setCurrentItem(index);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			// This method will be invoked when a new page becomes selected.
			public void onPageSelected(int position) {
				mPlayer = (HashMap<String, String>) mPlayerList.get(position);
				lblTitle.setText(mPlayer.get("name"));
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

		mPlayer = (HashMap<String, String>) mPlayerList.get(index);
		lblTitle.setText(mPlayer.get("name"));
	}

	private void getPlayerDetails(int index) throws Exception {

		String METHOD_NAME = "GetPlayerStatisticByUserIdInCurrentSeason";

		mPlayer = (HashMap<String, String>) mPlayerList.get(index);

		SoapObject request = WebServiceHelper.getSOAPRequest(METHOD_NAME);
		request.addProperty("userId", mPlayer.get("id"));
		request.addProperty("clubId", LoginAct.clubId);

		SoapObject response = (SoapObject) WebServiceHelper.getSOAPResponse(request,
				METHOD_NAME);

		if (response != null) {

			String image = null;
			String nickName = null;
			String dateOfBirth = null;
			String assists = null;
			String goals = null;
			String position = null;
			String positionInClub = null;
			String temp = null;

			if (response.getProperty("Image") != null) {
				temp = response.getProperty("Image").toString();
				image = Utility.anyTypeConversion(temp);
			}

			if (response.getProperty("Name") != null) {
				temp = response.getProperty("Name").toString();
				nickName = Utility.anyTypeConversion(temp);
			}

			if (response.getProperty("Assisted") != null) {
				temp = response.getProperty("Assisted").toString();
				assists = Utility.anyTypeConversion(temp);
			}

			if (response.getProperty("DOB") != null) {
				temp = response.getProperty("DOB").toString();
				dateOfBirth = Utility.anyTypeConversion(temp);
			}

			if (response.getProperty("Position") != null) {
				temp = response.getProperty("Position").toString();
				position = Utility.anyTypeConversion(temp);
			}

			if (response.getProperty("PositionInclubRanking") != null) {
				temp = response.getProperty("PositionInclubRanking").toString();
				positionInClub = Utility.anyTypeConversion(temp);
			}

			if (response.getProperty("TotalGoals") != null) {
				temp = response.getProperty("TotalGoals").toString();
				goals = Utility.anyTypeConversion(temp);
			}

			mPlayer.put("image", image);
			mPlayer.put("nickName", nickName);
			mPlayer.put("dateOfBirth", dateOfBirth);
			mPlayer.put("assists", assists);
			mPlayer.put("position", position);
			mPlayer.put("positionInClub", positionInClub);
			mPlayer.put("goals", goals);
		}
	}

	private void populateItem(int index, ViewHolder holder) {

		try {
			byte[] temp = Base64.decode(mPlayer.get("image"));
			Bitmap bmpImage = BitmapFactory.decodeByteArray(temp, 0,
					temp.length);
			holder.image.setImageBitmap(bmpImage);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		holder.lblNickName.setText(mPlayer.get("nickName"));

		try {

			mCalendar.setTime(mDateFormat.parse(mPlayer.get("dateOfBirth")));

			int day = mCalendar.get(Calendar.DAY_OF_MONTH);
			int month = mCalendar.get(Calendar.MONTH) + 1;
			int year = mCalendar.get(Calendar.YEAR);

			holder.lblDateOfBirth.setText(new StringBuilder().append(pad(day))
					.append("-").append(pad(month)).append("-")
					.append(pad(year)));

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		holder.lblPosition.setText(mPlayer.get("position"));
		holder.lblGoals.setText(mPlayer.get("goals"));
		holder.lblAssists.setText(mPlayer.get("assists"));
		holder.lblPositionInClub.setText(mPlayer.get("positionInClub"));
	}

	private String pad(int c) {
		if (c >= 10)
			return String.valueOf(c);
		else
			return "0" + String.valueOf(c);
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
					getPlayerDetails(index);
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

		public TextView lblNickName;
		public TextView lblDateOfBirth;
		public TextView lblPosition;
		public TextView lblGoals;
		public TextView lblAssists;
		public TextView lblPositionInClub;

		public ImageView image;

		public ViewHolder(View view) {

			lblNickName = (TextView) view.findViewById(R.id.lblNickName);
			lblDateOfBirth = (TextView) view.findViewById(R.id.lblDateOfBirth);
			lblPosition = (TextView) view.findViewById(R.id.lblPosition);
			lblGoals = (TextView) view.findViewById(R.id.lblGoals);
			lblAssists = (TextView) view.findViewById(R.id.lblAssists);
			lblPositionInClub = (TextView) view
					.findViewById(R.id.lblPositionInClub);
			image = (ImageView) view.findViewById(R.id.imgPlayer);
		}
	}

	private class PlayerPagerAdapter extends PagerAdapter {

		private LayoutInflater mInflater;

		public PlayerPagerAdapter() {
			mInflater = LayoutInflater.from(PlayerDetailsAct.this);
		}

		@Override
		public int getCount() {
			return mPlayerList.size();
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public Object instantiateItem(View collection, int position) {

			View view = mInflater.inflate(R.layout.player_details_pager_item,
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
