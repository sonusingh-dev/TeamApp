package com.teamapp.ui.club;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TreeSet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.teamapp.ui.R;

public class ResultsFixturesAdapter extends BaseAdapter {
	private static final int TYPE_ITEM = 0;
	private static final int TYPE_SEPARATOR = 1;
	private static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;

	private Calendar calendar;
	private SimpleDateFormat dateFormat;
	private ArrayList<HashMap<String, String>> itemList;

	private Context mContext;
	private TreeSet mSeparatorsSet;

	public ResultsFixturesAdapter(Context context) {

		calendar = Calendar.getInstance();
		dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

		mContext = context;
		mSeparatorsSet = new TreeSet();
		itemList = new ArrayList<HashMap<String, String>>();
	}

	static class ViewHolder {

		public TextView lblTitle;

		public TextView lblDay;
		public TextView lblMonth;
		public TextView lblName;
		public TextView lblResult;
	}

	public void addItem(HashMap<String, String> item) {
		itemList.add(item);
		notifyDataSetChanged();
	}

	public void addSeparatorItem(HashMap<String, String> item) {
		itemList.add(item);
		// save separator position
		mSeparatorsSet.add(itemList.size() - 1);
		notifyDataSetChanged();
	}

	@Override
	public int getItemViewType(int position) {
		return mSeparatorsSet.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
	}

	@Override
	public int getViewTypeCount() {
		return TYPE_MAX_COUNT;
	}

	public int getCount() {
		return itemList.size();
	}

	public HashMap<String, String> getItem(int position) {
		return itemList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		// final int position = position;
		ViewHolder holder = null;
		int viewType = getItemViewType(position);
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(mContext);
			holder = new ViewHolder();
			switch (viewType) {
			case TYPE_ITEM:
				convertView = inflater.inflate(R.layout.results_fixtures_item,
						null);
				holder.lblDay = (TextView) convertView
						.findViewById(R.id.lblDay);
				holder.lblMonth = (TextView) convertView
						.findViewById(R.id.lblMonth);
				holder.lblName = (TextView) convertView
						.findViewById(R.id.lblName);
				holder.lblResult = (TextView) convertView
						.findViewById(R.id.lblResult);
				break;
			case TYPE_SEPARATOR:
				convertView = inflater.inflate(R.layout.item_separator, null);
				holder.lblTitle = (TextView) convertView
						.findViewById(R.id.lblTitle);
				break;
			}
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		switch (viewType) {
		case TYPE_ITEM:
			populateItem(position, holder);
			convertView.setOnClickListener(new OnClickListener() {

				public void onClick(View view) {
					// TODO Auto-generated method stub
					HashMap<String, String> game = getItem(position);
					String id = game.get("id");
					String teamOne = game.get("teamOne");
					String teamTwo = game.get("teamTwo");
					String result = game.get("result");

					if (result == null) {
						return;
					}

					Intent intent = new Intent().setClass(mContext,
							GameResultAct.class);
					intent.putExtra("id", id);
					intent.putExtra("teamOne", teamOne);
					intent.putExtra("teamTwo", teamTwo);
					intent.putExtra("result", result);
					((Activity) mContext).startActivityForResult(intent, 0);
				}
			});
			break;

		case TYPE_SEPARATOR:
			HashMap<String, String> game = itemList.get(position);
			String strMonth = game.get("month");
			holder.lblTitle.setText(strMonth);
			break;
		}

		return convertView;
	}

	// Populate item with data
	private void populateItem(int position, ViewHolder holder) {

		HashMap<String, String> game = getItem(position);
		String strMonth = game.get("month");
		String strDateTime = game.get("dateTime");
		String strName = game.get("name");
		String strResult = game.get("result");

		try {
			calendar.setTime(dateFormat.parse(strDateTime));
			holder.lblDay.setText(String.valueOf(calendar.get(Calendar.DATE)));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (strMonth != null && strMonth.length() != 0) {
			strMonth = strMonth.substring(0, 3);
		}

		holder.lblMonth.setText(strMonth);

		holder.lblName.setText(strName);

		if (strResult == null) {
			strResult = mContext.getString(R.string.results_write);
		}
		holder.lblResult.setText(strResult);

	}

}