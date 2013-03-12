package com.teamapp.ui.team;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.teamapp.ui.R;

public class PlayerListAdapter extends BaseAdapter {

	private ArrayList<HashMap<String, String>> itemList;

	private LayoutInflater mInflater;

	public PlayerListAdapter(Context context) {
		mInflater = LayoutInflater.from(context);
	}

	public static class ViewHolder {
		public TextView lblName;
		public CheckBox chkStatus;
	}

	public int getCount() {
		return itemList.size();
	}

	public HashMap<String, String> getItem(int position) {
		return itemList.get(position);
	}

	public ArrayList<HashMap<String, String>> getItemList() {
		return itemList;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_item_with_checkbox,
					null);
			holder = new ViewHolder();
			holder.lblName = (TextView) convertView.findViewById(R.id.lblName);
			holder.chkStatus = (CheckBox) convertView
					.findViewById(R.id.chkStatus);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		HashMap<String, String> item = getItem(position);
		String userName = item.get("userName");
		String assigned = item.get("assigned");
		holder.lblName.setText(userName);
		holder.chkStatus.setChecked(Boolean.parseBoolean(assigned));

		holder.chkStatus
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						HashMap<String, String> item = getItem(position);
						item.put("assigned", String.valueOf(isChecked));
						itemList.set(position, item);
					}
				});

		return convertView;
	}

}
