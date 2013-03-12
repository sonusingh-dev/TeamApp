package com.teamapp.apis.facebook;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.teamapp.apis.facebook.Facebook.DialogListener;
import com.teamapp.ui.R;

public class ShareOnFacebook {

	private static final String APP_ID = "250331738348068";
	private static final String[] PERMISSIONS = new String[] { "publish_stream" };

	private static final String TOKEN = "access_token";
	private static final String EXPIRES = "expires_in";
	private static final String KEY = "facebook-credentials";

	private byte[] imageToPost;
	private String messageToPost;
	private Facebook facebook;

	private ProgressDialog pd;
	private Context mContext;

	public ShareOnFacebook(Context context, String message, byte[] image) {
		mContext = context;
		messageToPost = message;
		imageToPost = image;
		facebook = new Facebook(APP_ID);
		restoreCredentials(facebook);
	}

	public void share() {
		if (!facebook.isSessionValid()) {
			loginAndPostToWall();
		} else {
			postToWall(imageToPost, messageToPost);
		}
	}

	private void loginAndPostToWall() {
		facebook.authorize((Activity) mContext, PERMISSIONS,
				new LoginPostDialogListener());
	}

	private void postToWall(final byte[] picture, final String message) {

		pd = ProgressDialog.show(mContext, "",
				mContext.getString(R.string.posting), true, true);
		new Thread() {
			@Override
			public void run() {

				int what = 0;
				Bundle parameters = new Bundle();
				
				parameters.putString("message", message
						+ "\n Source: www.TeamApp.co.uk");
												
				try {
					
					if (picture != null) {
						parameters.putByteArray("picture", picture);
						facebook.request("me/photos", parameters, "POST");
					} else {
						facebook.request("me/feed", parameters, "POST");
					}

				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					what = 1;
					e.printStackTrace();
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					what = 1;
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					what = 1;
					e.printStackTrace();
				}

				mHandler.sendMessage(mHandler.obtainMessage(what));
			}
		}.start();

	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			pd.dismiss();
			String message = (msg.what == 0) ? "Posted to Facebook"
					: "Post to Facebook failed";

			showToast(message);
		}
	};

	private boolean saveCredentials(Facebook facebook) {
		Editor editor = mContext
				.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
		editor.putString(TOKEN, facebook.getAccessToken());
		editor.putLong(EXPIRES, facebook.getAccessExpires());
		return editor.commit();
	}

	private boolean restoreCredentials(Facebook facebook) {
		SharedPreferences sharedPreferences = mContext.getSharedPreferences(
				KEY, Context.MODE_PRIVATE);
		facebook.setAccessToken(sharedPreferences.getString(TOKEN, null));
		facebook.setAccessExpires(sharedPreferences.getLong(EXPIRES, 0));
		return facebook.isSessionValid();
	}

	private class LoginPostDialogListener implements DialogListener {
		public void onComplete(Bundle values) {
			saveCredentials(facebook);
			postToWall(imageToPost, messageToPost);
		}

		public void onFacebookError(FacebookError e) {
			showToast("Post to Facebook failed!");
			e.printStackTrace();
		}

		public void onError(DialogError e) {
			showToast("Post to Facebook failed!");
			e.printStackTrace();
		}

		public void onCancel() {
			showToast("Post to Facebook cancelled!");

		}
	}

	private void showToast(String message) {
		Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
	}
}