package com.teamapp.apis.twitter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import twitter4j.TwitterException;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.http.AccessToken;
import twitter4j.http.OAuthAuthorization;
import twitter4j.util.ImageUpload;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.teamapp.apis.twitter.TwitterApp.TwDialogListener;
import com.teamapp.ui.R;

public class ShareOnTwitter {

	private static final String twitpic_api_key = "43e1e903b9a7e9e903b2926c642a5949";
	// private static final String twitter_consumer_key =
	// "XZShR743BY66He4iXyOEA";
	// private static final String twitter_secret_key =
	// "PE4nsPDTzIc0sSWawdtQigT3VlyihHfoObl2zSkATPo";

	private static final String TAG = "AndroidTwitpic";

	private static final String twitter_consumer_key = "FSHc45s8suszGDIek4JSzw";
	private static final String twitter_secret_key = "CzBIFPRb7hOyScZQ9wVTASOwACNh2SsIrYm0b8s04";

	// private static final String twitter_consumer_key =
	// "XZShR743BY66He4iXyOEA";
	// private static final String twitter_secret_key =
	// "PE4nsPDTzIc0sSWawdtQigT3VlyihHfoObl2zSkATPo";

	private byte[] imageToPost;
	private String messageToPost;
	private TwitterApp mTwitter;

	private ProgressDialog pd;
	private Context mContext;

	public ShareOnTwitter(Context context, String message, byte[] image) {
		mContext = context;
		messageToPost = message;
		imageToPost = image;
		mTwitter = new TwitterApp(context, twitter_consumer_key,
				twitter_secret_key);
		mTwitter.setListener(mTwLoginDialogListener);
	}

	public void share() {
		if (!mTwitter.hasAccessToken()) {
			mTwitter.authorize();
		} else {
			postToTwitter(messageToPost);
		}
	}

	private void postToTwitter(final String review) {

		pd = ProgressDialog.show(mContext, "",
				mContext.getString(R.string.posting), true, true);
		new Thread() {
			@Override
			public void run() {
				int what = 0;

				try {

					String message = review + " (Source: www.TeamApp.co.uk) ";
					if (imageToPost != null) {
						String url = twitpic();
						message = message + url;
					}

					// Post to Twitter
					mTwitter.updateStatus(message);

				} catch (Exception e) {
					what = 1;
					Log.e(TAG, "Failed to send image");

					e.printStackTrace();
				}

				mHandler.sendMessage(mHandler.obtainMessage(what));
			}
		}.start();
	}

	/**
	 * @throws TwitterException
	 * @see <a href="http://twitpic.com/api.do">Twitpic api</a>
	 */
	private String twitpic() throws TwitterException {

		TwitterSession twitterSession = new TwitterSession(mContext);
		AccessToken accessToken = twitterSession.getAccessToken();

		Configuration conf = new ConfigurationBuilder()
				.setOAuthConsumerKey(twitter_consumer_key)
				.setOAuthConsumerSecret(twitter_secret_key)
				.setOAuthAccessToken(accessToken.getToken())
				.setOAuthAccessTokenSecret(accessToken.getTokenSecret())
				.build();

		OAuthAuthorization auth = new OAuthAuthorization(conf,
				conf.getOAuthConsumerKey(), conf.getOAuthConsumerSecret(),
				new AccessToken(conf.getOAuthAccessToken(),
						conf.getOAuthAccessTokenSecret()));

		ImageUpload uploader = ImageUpload.getTwitpicUploader(twitpic_api_key,
				auth);

		Log.d(TAG, "Start sending image...");
		// convert byte array back to BufferedImage
		InputStream in = new ByteArrayInputStream(imageToPost);
		String url = uploader.upload("TeamApp", in);
		Log.d(TAG, "Image uploaded, Twitpic url is " + url);
		return url;
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			pd.dismiss();
			String text = (msg.what == 0) ? "Posted to Twitter"
					: "Post to Twitter failed";

			Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
		}
	};

	private final TwDialogListener mTwLoginDialogListener = new TwDialogListener() {
		public void onComplete(String value) {
			postToTwitter(messageToPost);
		}

		public void onError(String value) {
			Toast.makeText(mContext, "Connection with Twitter failed",
					Toast.LENGTH_LONG).show();
		}
	};

}
