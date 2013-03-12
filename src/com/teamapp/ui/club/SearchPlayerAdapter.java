package com.teamapp.ui.club;

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

public class SearchPlayerAdapter extends BaseAdapter {

	private ArrayList<HashMap<String, String>> mItemList;

	private LayoutInflater mInflater;

	public SearchPlayerAdapter(Context context,
			ArrayList<HashMap<String, String>> itemList) {
		mItemList = itemList;
		mInflater = LayoutInflater.from(context);
	}

	private static class ViewHolder {

		private TextView lblName;
		private TextView lblDescription;
		private ImageView imgUser;
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
			convertView = mInflater.inflate(R.layout.search_player_item, null);
			holder = new ViewHolder();
			holder.lblName = (TextView) convertView.findViewById(R.id.lblName);
			holder.lblDescription = (TextView) convertView
					.findViewById(R.id.lblDescription);
			holder.imgUser = (ImageView) convertView.findViewById(R.id.imgUser);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		populateItem(position, holder);
		return convertView;
	}

	// Populate item with data
	private void populateItem(int position, ViewHolder holder) {

		HashMap<String, String> item = getItem(position);

		String image = item.get("image");
		String title = item.get("name");
		String description = item.get("team");

		try {
			byte[] temp = Base64.decode(image);
			Bitmap bmpImage = BitmapFactory.decodeByteArray(temp, 0,
					temp.length);
			holder.imgUser.setImageBitmap(bmpImage);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		holder.lblName.setText(title);

		holder.lblDescription.setText(description);

	}

}