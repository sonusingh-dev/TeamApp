package com.teamapp.ui.club;

import java.util.ArrayList;
import java.util.HashMap;

import org.ksoap2.serialization.SoapObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.teamapp.helper.WebServiceHelper;
import com.teamapp.ui.CommentsAdapter;
import com.teamapp.ui.LoginAct;
import com.teamapp.ui.R;
import com.teamapp.ui.alerts.TeamAppAlerts;

public class CommentsListAct extends Activity {

	private String mUserId;
	private String type;
	private String commentTypeId;

	private CommentsListAct mCommentsListAct;

	private HashMap<String, String> mImages;

	private ArrayList<HashMap<String, String>> mCommentsList;

	private EditText txtComments;

	private ListView lstComments;

	private Button btnPost;

	private ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comments_list);

		mUserId = LoginAct.userId;
		type = getIntent().getStringExtra("type");
		commentTypeId = getIntent().getStringExtra("id");

		mCommentsListAct = this;

		mImages = new HashMap<String, String>();

		mCommentsList = new ArrayList<HashMap<String, String>>();

		txtComments = (EditText) findViewById(R.id.txtComments);

		lstComments = (ListView) findViewById(R.id.lstComments);

		btnPost = (Button) findViewById(R.id.btnPost);

		// lstComments.setStackFromBottom(true);
		// lstComments.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

		lstComments.setDivider(new ColorDrawable(0xffE6E6E6));
		lstComments.setDividerHeight(0);

		lstComments.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				HashMap<String, String> comments = mCommentsList.get(position);
				String userId = comments.get("userId");

				if (userId.equals(LoginAct.userId)) {
					showCommentsOptionPopup(view, comments, position);
				}
			}
		});

		btnPost.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				String message = txtComments.getText().toString();
				if (message.length() == 0) {
					// VoetBalAlerts.showMessageDialog(mCommentsListAct,
					// "Enter your comments...");
					return;
				}

				runPostComments(message);
			}
		});

		runComments();

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	private void populateComments() {

		HashMap<String, String> comment = null;
		CommentsAdapter mAdapter = new CommentsAdapter(mCommentsListAct,
				mImages);

		for (int i = 0; i < mCommentsList.size(); i++) {

			comment = mCommentsList.get(i);
			String userId = comment.get("userId");

			if (userId.equals(mUserId)) {
				mAdapter.addItem(comment);
			} else {
				mAdapter.addSeparatorItem(comment);
			}
		}

		lstComments.setAdapter(mAdapter);
	}

	private void getComments() throws Exception {

		String METHOD_NAME = "GetComments";

		mCommentsList.clear();
		SoapObject request = WebServiceHelper.getSOAPRequest(METHOD_NAME);
		request.addProperty("type", type);
		request.addProperty("CommentTypeId", commentTypeId);
		request.addProperty("clubId", LoginAct.clubId);
		SoapObject response = (SoapObject) WebServiceHelper.getSOAPResponse(request,
				METHOD_NAME);

		if (response != null) {

			SoapObject sopComment = null;

			for (int i = 0; i < response.getPropertyCount(); i++) {
				sopComment = (SoapObject) response.getProperty(i);

				String userName = sopComment.getProperty("Name").toString();
				String userId = sopComment.getProperty("UserId").toString();

				if (sopComment.getProperty("Image") != null) {
					String image = sopComment.getProperty("Image").toString();
					mImages.put(userId, image);
				}

				String message = sopComment.getProperty("Comment").toString();
				if (message.equals("anyType{}")) {
					message = null;
				}

				String commentId = sopComment.getProperty("CommentId")
						.toString();
				String commentTypeId = sopComment.getProperty("CommentTypeId")
						.toString();
				String dateTime = sopComment.getProperty("CommentedOn")
						.toString();
				String type = sopComment.getProperty("Type").toString();

				HashMap<String, String> comment = new HashMap<String, String>();
				comment.put("userName", userName);
				comment.put("userId", userId);
				comment.put("message", message);
				comment.put("commentId", commentId);
				comment.put("dateTime", dateTime);
				comment.put("commentTypeId", commentTypeId);
				comment.put("type", type);
				mCommentsList.add(comment);
			}
		}

		Log.d("getComments", "finish...");
	}

	private boolean postComments(String message) throws Exception {

		String METHOD_NAME = "InsertComment";
		String envelope = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\" xmlns:voet=\"http://schemas.datacontract.org/2004/07/VoetBall.Entities.DataObjects\">"
				+ "<soapenv:Header/>"
				+ "<soapenv:Body>"
				+ "<tem:InsertComment>"
				+ "<tem:comment>"
				+ "<voet:UserId>%s</voet:UserId>"
				+ "<voet:Comment>%s</voet:Comment>"
				+ "<voet:CommentId>%s</voet:CommentId>"
				+ "<voet:CommentTypeId>%s</voet:CommentTypeId>"
				+ "<voet:Type>%s</voet:Type>"
				+ "</tem:comment>"
				+ "<tem:clubId>%s</tem:clubId>"
				+ "</tem:InsertComment>"
				+ "</soapenv:Body>" + "</soapenv:Envelope>";

		String request = String.format(envelope, LoginAct.userId, message, "0",
				commentTypeId, type, LoginAct.clubId);

		String response = (String) WebServiceHelper.callWebService(METHOD_NAME,
				request);

		return WebServiceHelper.getStatus(response);

	}

	private boolean editComments(HashMap<String, String> comments)
			throws Exception {

		String METHOD_NAME = "UpdateComment";
		String envelope = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\" xmlns:voet=\"http://schemas.datacontract.org/2004/07/VoetBall.Entities.DataObjects\">"
				+ "<soapenv:Header/>"
				+ "<soapenv:Body>"
				+ "<tem:UpdateComment>"
				+ "<tem:comment>"
				+ "<voet:UserId>%s</voet:UserId>"
				+ "<voet:Comment>%s</voet:Comment>"
				+ "<voet:CommentId>%s</voet:CommentId>"
				+ "<voet:CommentTypeId>%s</voet:CommentTypeId>"
				+ "<voet:CommentedOn>%s</voet:CommentedOn>"
				+ "<voet:Type>%s</voet:Type>"
				+ "</tem:comment>"
				+ "<tem:clubId>%s</tem:clubId>"
				+ "</tem:UpdateComment>"
				+ "</soapenv:Body>" + "</soapenv:Envelope>";

		String request = String.format(envelope, comments.get("userId"),
				comments.get("message"), comments.get("commentId"),
				comments.get("commentTypeId"), comments.get("dateTime"),
				comments.get("type"), LoginAct.clubId);

		String response = (String) WebServiceHelper.callWebService(METHOD_NAME,
				request);

		return WebServiceHelper.getStatus(response);

	}

	private boolean deleteComments(String commentId) throws Exception {

		String METHOD_NAME = "DeleteComment";

		SoapObject request = WebServiceHelper.getSOAPRequest(METHOD_NAME);
		request.addProperty("commentId", commentId);
		request.addProperty("type", type);
		request.addProperty("clubId", LoginAct.clubId);
		Object response = WebServiceHelper.getSOAPResponse(request, METHOD_NAME);

		String status = response.toString();
		return Boolean.parseBoolean(status);

	}

	private void showEditCommentsDialog(final HashMap<String, String> comments,
			final int index) {

		final EditText txtComments = new EditText(mCommentsListAct);
		txtComments.setLines(4);
		txtComments.setGravity(Gravity.TOP);
		txtComments.setText(comments.get("message"));

		AlertDialog.Builder builder = new AlertDialog.Builder(mCommentsListAct);
		builder.setView(txtComments);
		builder.setTitle(getString(R.string.edit) + " "
				+ getString(R.string.comments));

		builder.setPositiveButton(getString(R.string.update),
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// Do nothing but close the dialog
						String message = txtComments.getText().toString();
						if (message.length() == 0) {
							// VoetBalAlerts.showMessageDialog(mCommentsListAct,
							// "Enter your comments...");
							return;
						}

						comments.put("message", message);
						runEditComments(comments, index);
					}
				});

		builder.setNegativeButton(getString(R.string.cancel),
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// Do nothing
					}
				});

		// Remember, create doesn't show the dialog
		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}

	private void showDeletionDialog(final String id, final int index) {

		AlertDialog.Builder optionBuilder = new AlertDialog.Builder(
				mCommentsListAct);
		optionBuilder.setMessage(getString(R.string.delete_sure));
		optionBuilder.setPositiveButton(getString(R.string.yes),
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// Do nothing but close the dialog
						runDeleteComments(index, id);
					}
				});

		optionBuilder.setNegativeButton(getString(R.string.no),
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// Do nothing
					}
				});

		// Remember, create doesn't show the dialog
		AlertDialog helpDialog = optionBuilder.create();
		helpDialog.show();
	}

	private void showCommentsOptionPopup(View view,
			final HashMap<String, String> comments, final int index) {

		LayoutInflater inflater = LayoutInflater.from(mCommentsListAct);
		View popupView = inflater.inflate(R.layout.comments_option_popup, null);
		popupView.measure(View.MeasureSpec.UNSPECIFIED,
				View.MeasureSpec.UNSPECIFIED);

		Display display = getWindowManager().getDefaultDisplay();
		int width = display.getWidth();
		int height = popupView.getMeasuredHeight() + 50;

		final PopupWindow popupWindow = new PopupWindow(popupView, width,
				height);
		popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setTouchable(true);

		Button btnDelete = (Button) popupView.findViewById(R.id.btnDelete);
		Button btnEdit = (Button) popupView.findViewById(R.id.btnEdit);
		Button btnCancel = (Button) popupView.findViewById(R.id.btnCancel);

		btnDelete.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				popupWindow.dismiss();
				showDeletionDialog(comments.get("commentId"), index);
			}
		});

		btnEdit.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				popupWindow.dismiss();
				showEditCommentsDialog(comments, index);
			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				popupWindow.dismiss();
			}
		});
	}

	private void runComments() {

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case 0:
					populateComments();
					pd.dismiss();
					break;
				case 1:
					pd.dismiss();
					TeamAppAlerts.showToast(mCommentsListAct,
							getString(R.string.service));
					break;
				}

			}
		};

		pd = ProgressDialog.show(mCommentsListAct, "",
				getString(R.string.loading), true, false);
		new Thread() {
			@Override
			public void run() {

				int what = 0;

				try {
					getComments();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					what = 1;
					e.printStackTrace();
				}

				handler.sendEmptyMessage(what);

			}
		}.start();

	}

	private void runPostComments(final String message) {

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case 0:
					txtComments.setText(null);
					runComments();
					break;
				case 1:
					TeamAppAlerts.showToast(mCommentsListAct,
							getString(R.string.post_failed) + " "
									+ getString(R.string.comments));
					break;
				case 2:
					TeamAppAlerts.showToast(mCommentsListAct,
							getString(R.string.service));
					break;
				}

			}
		};

		TeamAppAlerts.showToast(mCommentsListAct, getString(R.string.posting));
		new Thread() {
			@Override
			public void run() {

				int what = 0;

				try {
					if (!postComments(message)) {
						what = 1;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					what = 2;
					e.printStackTrace();
				}

				handler.sendMessage(handler.obtainMessage(what));

			}
		}.start();

	}

	private void runEditComments(final HashMap<String, String> comments,
			final int index) {

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case 0:
					mCommentsList.set(index, comments);
					populateComments();
					break;
				case 1:
					TeamAppAlerts.showToast(mCommentsListAct,
							getString(R.string.update_failed) + " "
									+ getString(R.string.comments));
					break;
				case 2:
					TeamAppAlerts.showToast(mCommentsListAct,
							getString(R.string.service));
					break;
				}
			}
		};

		new Thread() {
			@Override
			public void run() {

				int what = 0;

				try {
					if (!editComments(comments)) {
						what = 1;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					what = 2;
					e.printStackTrace();
				}

				handler.sendMessage(handler.obtainMessage(what));

			}
		}.start();

	}

	private void runDeleteComments(final int index, final String id) {

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case 0:
					mCommentsList.remove(index);
					populateComments();
					break;
				case 1:
					TeamAppAlerts.showToast(mCommentsListAct,
							getString(R.string.delete_failed) + " "
									+ getString(R.string.comments));
					break;
				case 2:
					TeamAppAlerts.showToast(mCommentsListAct,
							getString(R.string.service));
					break;
				}
			}
		};

		TeamAppAlerts.showToast(mCommentsListAct, getString(R.string.deleting));
		new Thread() {
			@Override
			public void run() {

				int what = 0;
				try {
					if (!deleteComments(id)) {
						what = 1;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					what = 2;
					e.printStackTrace();
				}

				handler.sendMessage(handler.obtainMessage(what));

			}
		}.start();

	}

}
