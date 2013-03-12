package com.teamapp.ui;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.teamapp.ui.R;

public class Balloon extends View {

	private static final int WC = LinearLayout.LayoutParams.WRAP_CONTENT;
	private static final int FP = LinearLayout.LayoutParams.FILL_PARENT;
	
	private Calendar mCalendar;
	private SimpleDateFormat mDateFormat;
	
	final Calendar calendar = Calendar.getInstance();
	final int hour = calendar.get(Calendar.HOUR_OF_DAY);
	final int minute = calendar.get(Calendar.MINUTE);
	final int second = calendar.get(Calendar.SECOND);

	private TextView balloonbody;
	private TextView time;
	private LinearLayout balloonset;
	private Drawable fukidasi = getResources().getDrawable(R.drawable.green);
	private Drawable fukidasir = getResources().getDrawable(R.drawable.grey);
	private ImageView face;
	private LinearLayout.LayoutParams layoutParams;

	public Balloon(Context context) {
		super(context);
		// TODO Auto-generated constructor stub

		mCalendar = Calendar.getInstance();
		mDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		
		final Calendar calendar = Calendar.getInstance();
		final int hour = calendar.get(Calendar.HOUR_OF_DAY);
		final int minute = calendar.get(Calendar.MINUTE);

		this.layoutParams = new LinearLayout.LayoutParams(WC, WC);
		this.balloonset = new LinearLayout(context);
		this.balloonset.setLayoutParams(layoutParams);
		this.balloonset.setOrientation(LinearLayout.HORIZONTAL);

		this.face = new ImageView(context);

		this.balloonbody = new TextView(context);
		this.balloonbody.setLayoutParams(layoutParams);

		this.time = new TextView(context);
		this.time.setTextColor(Color.WHITE);
		// this.time.setWidth(time.getWidth());

	}

	// Populate item with data
	private void populateItem(HashMap<String, String> comment) {

		String userId = comment.get("userId");
		String name = comment.get("userName");
		String message = comment.get("message");
		String dateTime = comment.get("dateTime");
		
		balloonbody.setText(message);

		try {

			mCalendar.setTime(mDateFormat.parse(dateTime));

			int year = mCalendar.get(Calendar.YEAR);
			int day = mCalendar.get(Calendar.DAY_OF_MONTH);
			int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
			int minute = mCalendar.get(Calendar.MINUTE);

			String month = String.format("%tb", mCalendar);

			time.setText(new StringBuilder().append(pad(day))
					.append(" ").append(month).append(" ").append(pad(year))
					.append(" ").append(pad(hour)).append(":").append(minute));

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		// try {
		// String image = mImages.get(userId);
		// byte[] temp = Base64.decode(image);
		// Bitmap bmpImage = BitmapFactory.decodeByteArray(temp, 0,
		// temp.length);
		// holder.image.setImageBitmap(bmpImage);
		// } catch (Exception e) {
		// // TODO: handle exception
		// e.printStackTrace();
		// }

	}
	
	private static String pad(int c) {
		if (c >= 10)
			return String.valueOf(c);
		else
			return "0" + String.valueOf(c);
	}
	
	public void populateItem(Bitmap bitmap, Calendar calendar, String message) {

		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		balloonbody.setText(message);
		time.setText(String.format("%02d:%02d", hour, minute));
	}

	public LinearLayout createBalloonSended() {

		balloonbody.setBackgroundDrawable(fukidasir);

		balloonset.setGravity(Gravity.RIGHT | Gravity.BOTTOM);

		balloonset.addView(face);
		balloonset.addView(balloonbody);
		balloonset.addView(time);

		return balloonset;

	}

	public LinearLayout createBalloonReceived() {
		// the message received
		balloonbody.setBackgroundDrawable(fukidasi);

		balloonset.setGravity(Gravity.LEFT);

		balloonset.addView(face);
		balloonset.addView(balloonbody);
		balloonset.addView(time);

		return balloonset;

	}

}
