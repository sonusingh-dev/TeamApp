package com.teamapp.ui.club;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.kobjects.base64.Base64;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.teamapp.apis.facebook.ShareOnFacebook;
import com.teamapp.apis.hyves.ShareOnHyves;
import com.teamapp.apis.twitter.ShareOnTwitter;
import com.teamapp.ui.R;

public class NewsDetailsAct extends Activity {

	private int index;

	private String id;
	private String title;

	private ArrayList<Parcelable> newsList;

	private TextView lblTitle;

	private ImageView btnWriteMsg;

	private Button btnShare;

	private ViewPager viewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news_details);

		index = getIntent().getIntExtra("index", 0);
		newsList = getIntent().getParcelableArrayListExtra("newsList");

		lblTitle = (TextView) findViewById(R.id.lblTitle);

		btnWriteMsg = (ImageView) findViewById(R.id.btnWriteMsg);

		btnShare = (Button) findViewById(R.id.btnShare);

		viewPager = (ViewPager) findViewById(R.id.viewPager);
		viewPager.setAdapter(new NewsPagerAdapter());
		viewPager.setCurrentItem(index);

		HashMap<String, String> news = (HashMap<String, String>) newsList
				.get(index);
		id = news.get("id");
		title = news.get("title");
		lblTitle.setText(title);

		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			public void onPageSelected(int page) {
				// TODO Auto-generated method stub
				HashMap<String, String> news = (HashMap<String, String>) newsList
						.get(page);
				id = news.get("id");
				title = news.get("title");
				lblTitle.setText(title);
			}

			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});

		btnShare.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {
				// TODO Auto-generated method stub
				showPopupWindow(view);
			}
		});

		btnWriteMsg.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent().setClass(NewsDetailsAct.this,
						CommentsListAct.class);
				intent.putExtra("id", id);
				intent.putExtra("type", "ClubNews");
				startActivity(intent);
			}
		});

	}

	// PopupWindow for Article sharing
	private void showPopupWindow(View view) {

		LayoutInflater inflater = LayoutInflater.from(this);
		View popupView = inflater.inflate(R.layout.share_article_popup, null);
		popupView.measure(View.MeasureSpec.UNSPECIFIED,
				View.MeasureSpec.UNSPECIFIED);

		Display display = getWindowManager().getDefaultDisplay();
		int width = display.getWidth();
		int height = popupView.getMeasuredHeight() + 50;

		final PopupWindow popupWindow = new PopupWindow(popupView, width,
				height, true);
		popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setTouchable(true);

		Button btnFacebook = (Button) popupView.findViewById(R.id.btnFacebook);
		Button btnTwitter = (Button) popupView.findViewById(R.id.btnTwitter);
		Button btnHyves = (Button) popupView.findViewById(R.id.btnHyves);
		Button btnCancel = (Button) popupView.findViewById(R.id.btnCancel);

		final String message = "News: " + title;

		btnFacebook.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				popupWindow.dismiss();
				byte[] imageBytes = getScreen();
				ShareOnFacebook shareOnFacebook = new ShareOnFacebook(
						NewsDetailsAct.this, message, imageBytes);
				shareOnFacebook.share();
			}
		});

		btnTwitter.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				popupWindow.dismiss();
				byte[] imageBytes = getScreen();
				ShareOnTwitter shareOnTwitter = new ShareOnTwitter(
						NewsDetailsAct.this, message, imageBytes);
				shareOnTwitter.share();
			}
		});

		btnHyves.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				popupWindow.dismiss();
				ShareOnHyves shareOnHyves = new ShareOnHyves(
						NewsDetailsAct.this, message);
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
		viewPager.setDrawingCacheEnabled(true);
		Bitmap bitmap = viewPager.getDrawingCache();
		if (bitmap != null) {
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
			return baos.toByteArray();
		}
		return null;
	}

	private class NewsPagerAdapter extends PagerAdapter {

		private LayoutInflater mInflater;

		public NewsPagerAdapter() {
			mInflater = LayoutInflater.from(NewsDetailsAct.this);
		}

		@Override
		public int getCount() {
			return newsList.size();
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public Object instantiateItem(View collection, int position) {

			View view = mInflater.inflate(R.layout.news_pager_item, null);
			// TextView lblContent = (TextView)
			// view.findViewById(R.id.lblContent);
			ImageView imgNews = (ImageView) view.findViewById(R.id.imgNews);
			WebView webView = (WebView) view.findViewById(R.id.webView);

			HashMap<String, String> news = (HashMap<String, String>) newsList
					.get(position);

			String content = news.get("content");
			String image = news.get("image");

			if (content != null) {
				webView.loadData(content, "text/html", "utf-8");
				// lblContent.setText(content);
			}

			try {
				byte[] temp = Base64.decode(image);
				Bitmap bmpImage = BitmapFactory.decodeByteArray(temp, 0,
						temp.length);
				imgNews.setImageBitmap(bmpImage);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}

			((ViewPager) collection).addView(view);
			return view;
		}

		@Override
		public void destroyItem(View collection, int position, Object object) {

			((ViewPager) collection).removeView((View) object);
		}

		@Override
		public void finishUpdate(View collection) {

		}

		@Override
		public boolean isViewFromObject(View collection, Object object) {
			return collection == (View) object;
		}

		@Override
		public void restoreState(Parcelable parcelable, ClassLoader loader) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View collection) {

		}

	}

}
