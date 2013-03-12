package com.teamapp.apis.hyves;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;

import org.apache.http.HttpResponse;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.Toast;

import com.teamapp.ui.R;

public class ShareOnHyves {

	private String messageToPost;
	private OAuthConsumer consumer = null;
	private OAuthProvider provider = null;

	private Context mContext;
	private ProgressDialog pd;

	public ShareOnHyves(Context context, String message) {
		mContext = context;
		messageToPost = message;
	}

	public void share() {
		checkLoggedIn();
	}

	private void login() {
		retrieveRequestToken();
	}

	private void postToHyves() {
		((Activity) mContext).runOnUiThread(new Runnable() {
			public void run() {
				pd = ProgressDialog.show(mContext, "",
						mContext.getString(R.string.posting), true, true);
				executePostWWW(messageToPost + " (Source: www.TeamApp.co.uk)");
			}
		});

	}

	private void showToast(final String message) {
		((Activity) mContext).runOnUiThread(new Runnable() {
			public void run() {
				if (pd != null) {
					pd.dismiss();
				}
				Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void checkLoggedIn() {
		// Do these operations in a thread so that the main thread is not
		// blocked.
		new Thread() {
			@Override
			public void run() {
				SharedPreferences savedSession = mContext.getSharedPreferences(
						Globals.SharedPref.SHARED_PREF_TAG,
						Context.MODE_PRIVATE);
				String token = savedSession.getString(
						Globals.SavedSessions.TOKEN_LABEL, null);
				String secret = savedSession.getString(
						Globals.SavedSessions.TOKEN_SECRET, null);

				if ((null == token) || (null == secret)) {
					login();
					return;
				}

				consumer = new CommonsHttpOAuthConsumer(
						Globals.Hyves.CONSUMER_KEY,
						Globals.Hyves.CONSUMER_SECRET);

				consumer.setTokenWithSecret(token, secret);

				HttpResponse response = SignpostHandler
						.executeHttpGetWithSignpost(Globals.Hyves.FRIENDS_GET,
								consumer);

				if ((null != response)
						&& (200 == response.getStatusLine().getStatusCode())) {
					postToHyves();
					return;
				}

				login();
			}
		}.run();

	}

	private void retrieveRequestToken() {
		// Do these operations in a thread so that the main thread is not
		// blocked.
		new Thread() {
			@Override
			public void run() {
				consumer = new CommonsHttpOAuthConsumer(
						Globals.Hyves.CONSUMER_KEY,
						Globals.Hyves.CONSUMER_SECRET);
				provider = new CommonsHttpOAuthProvider(
						Globals.Hyves.REQTOKEN_LINK,
						Globals.Hyves.REQACCESSTOKEN_LINK,
						Globals.Hyves.AUTHORIZATION_LINK);

				String authUrl = SignpostHandler
						.retrieveRequestTokenWithSignpost(provider, consumer,
								Globals.Hyves.REDIRECT_URI);
				if (null == authUrl) {
					showToast("Connection with Hyves failed!");
					return;
				}

				new HyvesDialog(mContext, authUrl, new LoginCompleteCmd())
						.show();

			}
		}.run();
	}

	private void executePostWWW(final String www) {
		// Do these operations in a thread so that the main thread is not
		// blocked.
		new Thread() {
			@Override
			public void run() {
				String wwwPost = Globals.Hyves.WWW_POST_BASE + "&emotion="
						+ GenUtil.encodeForURL(www);

				if (null != SignpostHandler.executeHttpPostWithSignpost(
						wwwPost, consumer)) {
					showToast("Posted to Hyves");
				} else {
					showToast("Post to Hyves failed");
				}

			}
		}.start();
	}

	// private void executeLogout() {
	// // Do these operations in a thread so that the main thread is not
	// // blocked.
	// new Thread() {
	// @Override
	// public void run() {
	// if (null == SignpostHandler.executeHttpGetWithSignpost(
	// Globals.Hyves.REVOKE_SELF, consumer))
	// return;
	//
	// GenUtil.clearCookies(ShareOnHyves.this);
	//
	// Editor editor = getSharedPreferences(
	// Globals.SharedPref.SHARED_PREF_TAG,
	// Context.MODE_PRIVATE).edit();
	// editor.remove(Globals.SavedSessions.TOKEN_LABEL);
	// editor.remove(Globals.SavedSessions.TOKEN_SECRET);
	// editor.commit();
	// }
	// }.start();
	//
	// }

	class LoginCompleteCmd {
		public void LoginComplete(Bundle values) {
			final String sCode = values.getString("oauth_verifier");

			new Thread() {
				@Override
				public void run() {
					if (false == SignpostHandler
							.retrieveAccessTokenWithSignpost(provider,
									consumer, sCode))
						return;

					Editor editor = mContext.getSharedPreferences(
							Globals.SharedPref.SHARED_PREF_TAG,
							Context.MODE_PRIVATE).edit();
					editor.putString(Globals.SavedSessions.TOKEN_LABEL,
							consumer.getToken());
					editor.putString(Globals.SavedSessions.TOKEN_SECRET,
							consumer.getTokenSecret());
					editor.putString(Globals.SavedSessions.USER_ID, provider
							.getResponseParameters().getAsQueryString("userid"));
					editor.commit();
					postToHyves();

				}
			}.start();

		}
	}

}
