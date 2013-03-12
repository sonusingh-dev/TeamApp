package com.teamapp.ui;

import java.util.ArrayList;
import java.util.HashMap;

import org.kobjects.base64.Base64;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.teamapp.ui.R;

public class StatsListAdapter extends BaseAdapter {

	private HashMap<String, String> item;
	private ArrayList<HashMap<String, String>> mItemList;

	private LayoutInflater mInflater;

	public StatsListAdapter(Context context,
			ArrayList<HashMap<String, String>> itemList) {
		mItemList = itemList;
		mInflater = LayoutInflater.from(context);
	}

	private static class ViewHolder {
		private TextView lblIndex;
		private TextView lblCount;
		private TextView lblName;
		private TextView lblNickName;
		private ImageView imgPlayer;
		private ImageView imgTrophy;
	}

	public int getCount() {
		return mItemList.size();
	}

	public HashMap<String, String> getItem(int position) {
		return mItemList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.stats_list_item, null);
			holder = new ViewHolder();
			holder.lblIndex = (TextView) convertView
					.findViewById(R.id.lblIndex);
			holder.lblCount = (TextView) convertView
					.findViewById(R.id.lblCount);
			holder.lblName = (TextView) convertView.findViewById(R.id.lblName);
			holder.lblNickName = (TextView) convertView
					.findViewById(R.id.lblNickName);
			holder.imgPlayer = (ImageView) convertView
					.findViewById(R.id.imgPlayer);
			holder.imgTrophy = (ImageView) convertView
					.findViewById(R.id.imgTrophy);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		populateItem(position, holder);
		return convertView;
	}

	// Populate item with data
	private void populateItem(int position, ViewHolder holder) {

		item = getItem(position);

		String image = item.get("image");
		String title = item.get("name");
		String trophy = item.get("trophy");
		String count = item.get("count");
		String description = item.get("nickName");
				
		if (trophy.equals("Golden")) {
			holder.imgTrophy.setVisibility(View.VISIBLE);
			holder.imgTrophy.setImageResource(R.drawable.star_gold);
		} else if (trophy.equals("Silver")) {
			holder.imgTrophy.setVisibility(View.VISIBLE);
			holder.imgTrophy.setImageResource(R.drawable.star_silver);
		} else if (trophy.equals("Undefined")) {
			holder.imgTrophy.setVisibility(View.GONE);
		}

		holder.lblIndex.setText(String.valueOf(position + 1));

		try {
			byte[] temp = Base64.decode(image);
			Bitmap bmpImage = BitmapFactory.decodeByteArray(temp, 0,
					temp.length);
			holder.imgPlayer.setImageBitmap(bmpImage);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		if (count != null && count.length() != 0) {
			holder.lblCount.setText(count);
		}

		if (title != null && title.length() != 0) {
			holder.lblName.setText(title);
		}

		if (description != null && description.length() != 0) {
			holder.lblNickName.setText(description);
		}

	}

}