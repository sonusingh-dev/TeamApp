package com.teamapp.ui.club;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

import org.kobjects.base64.Base64;
import org.ksoap2.serialization.SoapObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.teamapp.apis.anim.ActivitySwitcher;
import com.teamapp.helper.Utility;
import com.teamapp.helper.WebServiceHelper;
import com.teamapp.ui.LoginAct;
import com.teamapp.ui.R;
import com.teamapp.ui.alerts.TeamAppAlerts;

public class NewsListAct extends ClubMenuAct {

	private int mAnim;

	private Calendar mCalendar;
	private DateFormat mDateFormat;
	private ArrayList<HashMap<String, String>> newsList;

	private ListView lstNews;

	private ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news_list);

		mAnim = getIntent().getIntExtra("anim", 0);

		sMnuIndex = 0;

		// mCalendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+01:00"));
		mCalendar = Calendar.getInstance(TimeZone.getDefault());
		mDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		newsList = new ArrayList<HashMap<String, String>>();

		lstNews = (ListView) findViewById(R.id.lstNews);

		lstNews.setDivider(new ColorDrawable(0xffE6E6E6));
		lstNews.setDividerHeight(1);

		lstNews.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent().setClass(NewsListAct.this,
						NewsDetailsAct.class);
				intent.putExtra("index", position);
				intent.putParcelableArrayListExtra("newsList",
						(ArrayList<? extends Parcelable>) newsList);
				startActivity(intent);
			}
		});

		runDefault();
	}
	
	@Override
	protected void onResume() {
		// animateIn this activity
		if (mAnim == 1) {
			ActivitySwitcher.animationRightOut(findViewById(R.id.mainLayout),
					getWindowManager());
		}
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		mAnim = 0;
		super.onPause();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void getNewsList() throws Exception {

		String METHOD_NAME = "GetClubNewsList";

		newsList.clear();
		SoapObject request = WebServiceHelper.getSOAPRequest(METHOD_NAME);
		request.addProperty("clubId", LoginAct.clubId);

		SoapObject response = (SoapObject) WebServiceHelper.getSOAPResponse(
				request, METHOD_NAME);

		for (int i = 0; i < response.getPropertyCount(); i++) {

			SoapObject property = (SoapObject) response.getProperty(i);
			String id = property.getProperty("ClubNewsId").toString();
			String content = property.getProperty("NewsContent").toString();
			String dateTime = property.getProperty("NewsDate").toString();
			String image = property.getProperty("NewsImage").toString();
			String title = property.getProperty("NewsTitle").toString();

			HashMap<String, String> news = new HashMap<String, String>();
			news.put("id", id);
			news.put("content", content);
			news.put("dateTime", dateTime);
			news.put("image", image);
			news.put("title", title);
			newsList.add(news);
		}
	}

	public String stipHtml(String html) {
		return Html.fromHtml(html).toString();
	}

	// Populate item with data
	private void populateItem(int position, ViewHolder holder) {

		HashMap<String, String> news = newsList.get(position);
		String title = news.get("title");
		String content = news.get("content");
		String dateTime = news.get("dateTime");
		String image = news.get("image");

		if (title != null) {
			holder.lblTitle.setText(title);
		}

		if (content != null) {
			holder.lblContent.setText(Utility.stipHtml(content));
		}

		try {

			mCalendar.setTime(mDateFormat.parse(dateTime));

			int year = mCalendar.get(Calendar.YEAR);
			int day = mCalendar.get(Calendar.DAY_OF_MONTH);
			int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
			int minute = mCalendar.get(Calendar.MINUTE);

			String month = String.format("%tb", mCalendar);

			holder.lblDateTime.setText(new StringBuilder()
					.append(Utility.pad(day)).append(" ").append(month)
					.append(" ").append(year).append(" at ").append(hour)
					.append(":").append(Utility.pad(minute)));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		try {
			byte[] temp = Base64.decode(image);
			Bitmap bmpImage = BitmapFactory.decodeByteArray(temp, 0,
					temp.length);
			holder.imgNews.setImageBitmap(bmpImage);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private String pad(int c) {
		if (c >= 10)
			return String.valueOf(c);
		else
			return "0" + String.valueOf(c);
	}

	private void runDefault() {

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case 0:
					lstNews.setAdapter(new NewsListAdapter());
					pd.dismiss();
					break;
				case 1:
					pd.dismiss();
					TeamAppAlerts.showToast(NewsListAct.this,
							getString(R.string.no) + " "
									+ getString(R.string.news) + " "
									+ getString(R.string.available));
					break;
				case 2:
					pd.dismiss();
					TeamAppAlerts.showToast(NewsListAct.this,
							getString(R.string.service));
					break;
				}
			}
		};

		pd = ProgressDialog.show(NewsListAct.this, "",
				getString(R.string.loading), true, false);
		new Thread() {
			@Override
			public void run() {

				int what = 0;

				try {
					getNewsList();
					if (newsList.isEmpty()) {
						what = 1;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					what = 2;
					e.printStackTrace();
				}

				handler.sendEmptyMessage(what);

			}
		}.start();

	}

	private static class ViewHolder {
		public TextView lblTitle;
		public TextView lblContent;
		public TextView lblDateTime;

		public ImageView imgNews;
	}

	private class NewsListAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		public NewsListAdapter() {
			mInflater = LayoutInflater.from(NewsListAct.this);
		}

		public int getCount() {
			return newsList.size();
		}

		public HashMap<String, String> getItem(int position) {
			return newsList.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.news_list_item, null);
				holder = new ViewHolder();
				holder.lblTitle = (TextView) convertView
						.findViewById(R.id.lblTitle);
				holder.lblContent = (TextView) convertView
						.findViewById(R.id.lblContent);
				holder.lblDateTime = (TextView) convertView
						.findViewById(R.id.lblDateTime);
				holder.imgNews = (ImageView) convertView
						.findViewById(R.id.imgNews);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			populateItem(position, holder);
			return convertView;
		}

	}

}
