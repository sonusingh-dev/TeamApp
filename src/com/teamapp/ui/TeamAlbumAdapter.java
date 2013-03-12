package com.teamapp.ui;

import java.util.ArrayList;

import org.kobjects.base64.Base64;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import com.teamapp.ui.R;

public class TeamAlbumAdapter extends BaseAdapter {

	private ArrayList<String> itemList;

	private Context context;

	public TeamAlbumAdapter(Context context) {
		this.context = context;
		itemList = new ArrayList<String>();
	}

	public void addItem(final String item) {
		itemList.add(item);
		notifyDataSetChanged();
	}

	public int getCount() {
		return itemList.size();
	}

	public String getItem(int position) {
		return itemList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	// create a new ImageView for each item referenced by the Adapter
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView;

		if (convertView == null) {
			imageView = new ImageView(context);
			imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
			// imageView.setAdjustViewBounds(true);
			imageView.setScaleType(ImageView.ScaleType.FIT_XY);
			imageView.setPadding(5, 5, 5, 5);
			
		} else {
			imageView = (ImageView) convertView;
		}

		try {
			String image = getItem(position);
			byte[] temp = Base64.decode(image);
			Bitmap bmpImage = BitmapFactory.decodeByteArray(temp, 0,
					temp.length);
			imageView.setImageBitmap(bmpImage);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return imageView;
	}

}
