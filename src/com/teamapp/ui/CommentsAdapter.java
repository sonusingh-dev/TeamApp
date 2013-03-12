package com.teamapp.ui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.TreeSet;

import org.kobjects.base64.Base64;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.teamapp.helper.Utility;
import com.teamapp.ui.R;

public class CommentsAdapter extends BaseAdapter {

	private static final int TYPE_ITEM = 0;
	private static final int TYPE_SEPARATOR = 1;
	private static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;

	private HashMap<String, String> mImages;
	private ArrayList<HashMap<String, String>> mItemList;

	private Calendar mCalendar;
	private DateFormat mDateFormat;

	private LayoutInflater mInflater;
	private TreeSet mSeparatorsSet;

	public CommentsAdapter(Context context, HashMap<String, String> images) {

		mImages = images;
		mItemList = new ArrayList<HashMap<String, String>>();

		// mCalendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+01:00"));
		mCalendar = Calendar.getInstance(TimeZone.getDefault());
		mCalendar.setTimeZone(TimeZone.getTimeZone(TimeZone.getDefault()
				.getID()));
		mDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

		mSeparatorsSet = new TreeSet();
		mInflater = LayoutInflater.from(context);
	}

	private static class ViewHolder {

		public TextView lblName;
		public TextView lblTime;
		public TextView lblDate;
		public TextView lblMessage;
		public ImageView image;

		public ViewHolder(View view) {
			lblName = (TextView) view.findViewById(R.id.lblName);
			lblTime = (TextView) view.findViewById(R.id.lblTime);
			lblDate = (TextView) view.findViewById(R.id.lblDate);
			lblMessage = (TextView) view.findViewById(R.id.lblMessage);
			image = (ImageView) view.findViewById(R.id.image);
		}
	}

	public void addItem(HashMap<String, String> item) {
		mItemList.add(item);
		notifyDataSetChanged();
	}

	public void addSeparatorItem(HashMap<String, String> item) {
		mItemList.add(item);
		// save separator position
		mSeparatorsSet.add(mItemList.size() - 1);
		notifyDataSetChanged();
	}

	public int getCount() {
		return mItemList.size();
	}

	@Override
	public int getItemViewType(int position) {
		return mSeparatorsSet.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
	}

	@Override
	public int getViewTypeCount() {
		return TYPE_MAX_COUNT;
	}

	public HashMap<String, String> getItem(int position) {
		return mItemList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		// final int position = position;
		ViewHolder holder = null;
		int viewType = getItemViewType(position);
		if (convertView == null) {
			switch (viewType) {
			case TYPE_ITEM:
				convertView = mInflater.inflate(R.layout.comment_two, null);
				break;
			case TYPE_SEPARATOR:
				convertView = mInflater.inflate(R.layout.comment_one, null);
				break;
			}

			holder = new ViewHolder(convertView);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		populateItem(position, holder);
		return convertView;
	}

	// Populate item with data
	private void populateItem(int position, ViewHolder holder) {

		HashMap<String, String> comment = getItem(position);
		String userId = comment.get("userId");
		String name = comment.get("userName");
		String message = comment.get("message");
		String dateTime = comment.get("dateTime");

		holder.lblName.setText(name);
		holder.lblMessage.setText(message);

		try {

			mCalendar.setTime(mDateFormat.parse(dateTime));
			
			int year = mCalendar.get(Calendar.YEAR);
			int day = mCalendar.get(Calendar.DAY_OF_MONTH);
			int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
			int minute = mCalendar.get(Calendar.MINUTE);

			String month = String.format("%tb", mCalendar);

			holder.lblTime.setText(new StringBuilder().append(hour)
					.append(":").append(Utility.pad(minute)));

			holder.lblDate.setText(new StringBuilder().append(Utility.pad(day))
					.append(" ").append(month).append(" ").append(year));

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		try {
			String image = mImages.get(userId);
			byte[] temp = Base64.decode(image);
			Bitmap bmpImage = BitmapFactory.decodeByteArray(temp, 0,
					temp.length);
			holder.image.setImageBitmap(bmpImage);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}	
}
