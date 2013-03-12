package com.teamapp.ui.team;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.teamapp.ui.R;

public class GameGoalsStatsAdapter extends BaseAdapter {

	private ArrayList<HashMap<String, String>> itemList;

	private LayoutInflater mInflater;

	public GameGoalsStatsAdapter(Context context) {
		mInflater = LayoutInflater.from(context);
		itemList = new ArrayList<HashMap<String, String>>();
	}

	static class ViewHolder {
		public TextView lblTime;
		public TextView lblGolaBy;
		public TextView lblAssistedBy;
		public ImageView imgDelete;
	}

	public void addItem(HashMap<String, String> item) {
		itemList.add(item);
		notifyDataSetChanged();
	}

	public int getCount() {
		return itemList.size();
	}

	public long getItemId(int position) {
		return position;
	}

	public HashMap<String, String> getItem(int position) {
		return itemList.get(position);
	}

	public ArrayList<HashMap<String, String>> getGoalsStatsList() {
		return itemList;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		// final int position = position;
		final int index = position;
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.goals_stats_list_item,
					null);
			holder.lblTime = (TextView) convertView.findViewById(R.id.lblTime);
			holder.lblGolaBy = (TextView) convertView
					.findViewById(R.id.lblGolaBy);
			holder.lblAssistedBy = (TextView) convertView
					.findViewById(R.id.lblAssistedBy);
			holder.imgDelete = (ImageView) convertView
					.findViewById(R.id.imgDelete);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.lblGolaBy.setText(null);
		holder.lblAssistedBy.setText("");
		holder.lblTime.setText("");
		// holder.imgDelete.setVisibility(View.INVISIBLE);

		holder.imgDelete.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				itemList.remove(index);
				notifyDataSetChanged();
			}
		});

		populate(index, holder);

		return convertView;
	}

	private void populate(int index, ViewHolder holder) {

		HashMap<String, String> item = getItem(index);
		String goalBy = item.get("goalBy");
		String assistedBy = item.get("assistedBy");
		String goalTime = item.get("goalTime");

		if (goalBy != null) {
			holder.lblGolaBy.setText(goalBy);
		}

		if (assistedBy != null) {
			holder.lblAssistedBy.setText(assistedBy);
		}

		if (goalTime != null) {
			holder.lblTime.setText(goalTime);
		}

	}
}
