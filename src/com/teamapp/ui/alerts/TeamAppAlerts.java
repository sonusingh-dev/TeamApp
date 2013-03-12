package com.teamapp.ui.alerts;

import com.teamapp.ui.R;
import com.teamapp.ui.R.string;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

public class TeamAppAlerts {

	public static void showConnetionDialog(Context context) {

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(context.getString(R.string.network_title));
		builder.setMessage(context.getString(R.string.network_msg));

		builder.setNeutralButton(context.getString(R.string.ok),
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// Do nothing
					}
				});

		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}

	public static void showMessageDialog(Context context, String message) {

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(message);

		builder.setNeutralButton(context.getString(R.string.ok),
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// Do nothing
					}
				});

		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}

	public static void showMessageDialog(Context context, String title,
			String message) {

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setMessage(message);

		builder.setNeutralButton(context.getString(R.string.ok),
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// Do nothing
					}
				});

		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}

	public static void showToast(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
	}

}
