package com.teamapp.ui.team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.ksoap2.serialization.SoapObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.teamapp.apis.chart.PieDetailsItem;
import com.teamapp.apis.chart.View_PieChart;
import com.teamapp.helper.WebServiceHelper;
import com.teamapp.ui.LoginAct;
import com.teamapp.ui.R;
import com.teamapp.ui.alerts.TeamAppAlerts;

public class PollDetailsAct extends Activity {

	private int mResultCode;

	private int mIndex;

	private String mRoleId;
	private String mUserId;
	private String mClubId;

	private String mPollId;

	private String optionId;

	private HashMap<String, String> mPoll;
	private ArrayList<Parcelable> mPollList;

	private TextView lblTitle;
	private TextView lblPollQ;

	private TextView lblPollOption1;
	private TextView lblPollOption2;
	private TextView lblPollOption3;

	private TextView lblPollValue1;
	private TextView lblPollValue2;
	private TextView lblPollValue3;

	private RadioButton radOption1;
	private RadioButton radOption2;
	private RadioButton radOption3;

	private ImageView btnLeft;
	private ImageView btnRight;
	private ImageView btnEdit;
	private ImageView btnDelete;

	private Button btnSubmit;

	private ProgressBar progressBar;
	private LinearLayout bodyLayout;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.poll_details);

		mResultCode = RESULT_CANCELED;

		mRoleId = LoginAct.roleId;
		mUserId = LoginAct.userId;
		mClubId = LoginAct.clubId;

		mIndex = getIntent().getIntExtra("position", 0);

		mPollList = getIntent().getParcelableArrayListExtra("pollList");

		lblTitle = (TextView) findViewById(R.id.lblTitle);
		lblPollQ = (TextView) findViewById(R.id.lblPollQ);

		lblPollOption1 = (TextView) findViewById(R.id.lblPollOption1);
		lblPollOption2 = (TextView) findViewById(R.id.lblPollOption2);
		lblPollOption3 = (TextView) findViewById(R.id.lblPollOption3);

		lblPollValue1 = (TextView) findViewById(R.id.lblPollValue1);
		lblPollValue2 = (TextView) findViewById(R.id.lblPollValue2);
		lblPollValue3 = (TextView) findViewById(R.id.lblPollValue3);

		radOption1 = (RadioButton) findViewById(R.id.radOption1);
		radOption2 = (RadioButton) findViewById(R.id.radOption2);
		radOption3 = (RadioButton) findViewById(R.id.radOption3);

		btnLeft = (ImageView) findViewById(R.id.btnLeft);
		btnRight = (ImageView) findViewById(R.id.btnRight);
		btnEdit = (ImageView) findViewById(R.id.btnEdit);
		btnDelete = (ImageView) findViewById(R.id.btnDelete);

		btnSubmit = (Button) findViewById(R.id.btnSubmit);

		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		bodyLayout = (LinearLayout) findViewById(R.id.bodyLayout);

		if (mRoleId.equals("4")) {
			btnEdit.setVisibility(View.INVISIBLE);
			btnDelete.setVisibility(View.INVISIBLE);
		}

		radOption1.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					optionId = mPoll.get("optionId1");
				}

			}
		});

		radOption2.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					optionId = mPoll.get("optionId2");
				}
			}
		});

		radOption3.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					optionId = mPoll.get("optionId3");
				}
			}
		});

		btnLeft.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {
				// TODO Auto-generated method stub

				if (mIndex > 0) {
					mIndex--;
				} else {
					mIndex = mPollList.size() - 1;
				}
				runPoll();
			}
		});

		btnRight.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {
				// TODO Auto-generated method stub
				if (mIndex < mPollList.size() - 1) {
					mIndex++;
				} else {
					mIndex = 0;
				}
				runPoll();
			}
		});

		btnEdit.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mPollList.isEmpty()) {
					return;
				}

				Intent intent = new Intent().setClass(PollDetailsAct.this,
						EditPollAct.class);
				intent.putExtra("id", mPollId);
				startActivityForResult(intent, 0);
			}
		});

		btnDelete.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (mPollList.isEmpty()) {
					return;
				}

				showDeletionDialog();
			}
		});

		btnSubmit.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				runSubmit();
			}
		});

		runPoll();

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			setResult(mResultCode);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			mResultCode = RESULT_OK;
			runPoll();
		}
	}

	private void populatePoll() {

		// lblTitle.setText(mPoll.get("pollQ"));

		radOption1.setText(mPoll.get("option1"));
		radOption2.setText(mPoll.get("option2"));
		radOption3.setText(mPoll.get("option3"));

		if (mPoll.get("optionId1").equals(optionId)) {
			radOption1.setChecked(true);
		} else if (mPoll.get("optionId2").equals(optionId)) {
			radOption2.setChecked(true);
		} else if (mPoll.get("optionId3").equals(optionId)) {
			radOption3.setChecked(true);
		}

		if (optionId != null) {
			radOption1.setClickable(false);
			radOption2.setClickable(false);
			radOption3.setClickable(false);
			btnSubmit.setVisibility(View.GONE);
		}

	}

	private void populateChart() {

		lblPollQ.setText(mPoll.get("pollQ"));

		lblPollOption1.setText(mPoll.get("option1"));
		lblPollOption2.setText(mPoll.get("option2"));
		lblPollOption3.setText(mPoll.get("option3"));

		lblPollValue1.setText(mPoll.get("optionValue1"));
		lblPollValue2.setText(mPoll.get("optionValue2"));
		lblPollValue3.setText(mPoll.get("optionValue3"));

		List<PieDetailsItem> piedata = new ArrayList<PieDetailsItem>(0);

		PieDetailsItem item;
		int maxCount = 0;
		int itemCount = 0;
		int item1 = (int) Double.parseDouble(mPoll.get("optionValue1"));
		int item2 = (int) Double.parseDouble(mPoll.get("optionValue2"));
		int item3 = (int) Double.parseDouble(mPoll.get("optionValue3"));
		int item4 = 100;
		int total = item1 + item2 + item3;

		if (total != 0) {
			item4 = 0;
		}

		int items[] = { item1, item2, item3, item4 };
		int colors[] = { 0xff7D7B7B, 0xff0DA10F, 0xff0D76C7, 0xff48bcd5 };
		String itemslabel[] = { mPoll.get("option1"), mPoll.get("option2"),
				mPoll.get("option3"), mPoll.get("option4") };
		for (int i = 0; i < items.length; i++) {
			itemCount = items[i];
			item = new PieDetailsItem();
			item.count = itemCount;
			item.label = itemslabel[i];
			item.color = colors[i];
			piedata.add(item);
			maxCount = maxCount + itemCount;
		}
		int size = 155;
		int BgColor = 0x00000000;
		Bitmap mBaggroundImage = Bitmap.createBitmap(size, size,
				Bitmap.Config.ARGB_8888);
		View_PieChart piechart = new View_PieChart(this);
		piechart.setLayoutParams(new LayoutParams(size, size));
		piechart.setGeometry(size, size, 2, 2, 2, 2, 2130837504);
		piechart.setSkinparams(BgColor);
		piechart.setData(piedata, maxCount);
		piechart.invalidate();
		piechart.draw(new Canvas(mBaggroundImage));
		piechart = null;
		ImageView mImageView = new ImageView(this);
		mImageView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		mImageView.setBackgroundColor(BgColor);
		mImageView.setImageBitmap(mBaggroundImage);
		LinearLayout finalLayout = (LinearLayout) findViewById(R.id.pie_container);
		finalLayout.removeAllViews();
		finalLayout.addView(mImageView);
	}

	private void getPoll() throws Exception {

		String METHOD_NAME = "GetPollResultInPercentage";

		if (mIndex >= mPollList.size()) {
			mIndex = mPollList.size() - 1;
		}

		mPoll = (HashMap<String, String>) mPollList.get(mIndex);
		mPollId = mPoll.get("pollId");

		SoapObject request = WebServiceHelper.getSOAPRequest(METHOD_NAME);
		request.addProperty("pollId", mPollId);
		request.addProperty("clubId", mClubId);

		SoapObject response = (SoapObject) WebServiceHelper.getSOAPResponse(
				request, METHOD_NAME);

		if (response != null) {

			String option1 = response.getProperty("Option1").toString();
			if (option1.contains("anyType{}")) {
				option1 = null;
			}

			String option2 = response.getProperty("Option2").toString();
			if (option2.contains("anyType{}")) {
				option2 = null;
			}

			String option3 = response.getProperty("Option3").toString();
			if (option3.contains("anyType{}")) {
				option3 = null;
			}

			String optionValue1 = response.getProperty("Option1Percenttage")
					.toString();

			String optionValue2 = response.getProperty("Option2Percenttage")
					.toString();

			String optionValue3 = response.getProperty("Option3Percenttage")
					.toString();

			mPoll.put("option1", option1);
			mPoll.put("option2", option2);
			mPoll.put("option3", option3);

			mPoll.put("optionValue1", optionValue1);
			mPoll.put("optionValue2", optionValue2);
			mPoll.put("optionValue3", optionValue3);
		}
	}

	private void getUserPollOption() throws Exception {

		String METHOD_NAME = "GetPollResultByPollIdUserId";

		SoapObject request = WebServiceHelper.getSOAPRequest(METHOD_NAME);
		request.addProperty("pollId", mPollId);
		request.addProperty("userId", mUserId);
		request.addProperty("clubId", mClubId);

		SoapObject response = (SoapObject) WebServiceHelper.getSOAPResponse(
				request, METHOD_NAME);

		if (response != null) {
			optionId = response.getProperty("PollOptionId").toString();
		}

	}

	private boolean submitPoll() throws Exception {

		String METHOD_NAME = "SubmitPoll";
		String envelope = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\" xmlns:voet=\"http://schemas.datacontract.org/2004/07/VoetBall.Entities.DataObjects\">"
				+ "<soapenv:Header/>"
				+ "<soapenv:Body>"
				+ "<tem:SubmitPoll>"
				+ "<tem:result>"
				+ "<voet:PollId>%s</voet:PollId>"
				+ "<voet:PollOptionId>%s</voet:PollOptionId>"
				+ "<voet:SubmittedByUserId>%s</voet:SubmittedByUserId>"
				+ "</tem:result>"
				+ "<tem:clubId>%s</tem:clubId>"
				+ "</tem:SubmitPoll>"
				+ "</soapenv:Body>"
				+ "</soapenv:Envelope>";

		String request = String.format(envelope, mPollId, optionId, mUserId,
				mClubId);

		Log.d("request", request);

		String response = (String) WebServiceHelper.callWebService(METHOD_NAME,
				request);

		Log.d("response", response);

		return WebServiceHelper.getStatus(response);

	}

	private void showDeletionDialog() {

		AlertDialog.Builder optionBuilder = new AlertDialog.Builder(
				PollDetailsAct.this);
		optionBuilder.setMessage(getString(R.string.delete_sure));
		optionBuilder.setPositiveButton(getString(R.string.yes),
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// Do nothing but close the dialog
						runDeletePoll();
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

	private boolean deletePoll() throws Exception {

		String METHOD_NAME = "DeletePoll";

		SoapObject request = WebServiceHelper.getSOAPRequest(METHOD_NAME);
		request.addProperty("pollId", mPollId);
		request.addProperty("clubId", LoginAct.clubId);
		Object response = WebServiceHelper
				.getSOAPResponse(request, METHOD_NAME);

		String status = response.toString();

		return Boolean.parseBoolean(status);

	}

	private void runPoll() {

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case 0:
					populateChart();
					populatePoll();
					progressBar.setVisibility(View.GONE);
					bodyLayout.setVisibility(View.VISIBLE);
					break;
				case 1:
					populateChart();
					populatePoll();
					progressBar.setVisibility(View.GONE);
					TeamAppAlerts.showToast(PollDetailsAct.this,
							getString(R.string.no) + " "
									+ getString(R.string.poll) + " "
									+ getString(R.string.available));
					break;
				case 2:
					progressBar.setVisibility(View.GONE);
					TeamAppAlerts.showToast(PollDetailsAct.this,
							getString(R.string.service));
					break;
				}

			}
		};

		bodyLayout.setVisibility(View.GONE);
		progressBar.setVisibility(View.VISIBLE);
		new Thread(new Runnable() {
			public void run() {
				// TODO Auto-generated method stub
				int what = 0;
				try {
					if (mPollList.isEmpty()) {
						what = 1;
					} else {
						getPoll();
						getUserPollOption();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					what = 2;
					e.printStackTrace();
				}

				handler.sendMessage(handler.obtainMessage(what));
			}
		}).start();
	}

	private void runDeletePoll() {

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case 0:
					mResultCode = RESULT_OK;
					mPollList.remove(mIndex);
					runPoll();
					break;
				case 1:
					progressBar.setVisibility(View.GONE);
					TeamAppAlerts.showToast(PollDetailsAct.this,
							getString(R.string.delete_failed) + " "
									+ getString(R.string.poll));
					break;
				case 2:
					progressBar.setVisibility(View.GONE);
					TeamAppAlerts.showToast(PollDetailsAct.this,
							getString(R.string.service));
					break;
				}
			}
		};

		TeamAppAlerts.showToast(PollDetailsAct.this,
				getString(R.string.deleting));
		progressBar.setVisibility(View.VISIBLE);
		new Thread() {
			@Override
			public void run() {

				int what = 0;

				try {
					if (!deletePoll()) {
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

	private void runSubmit() {

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case 0:
					mResultCode = RESULT_OK;
					runPoll();
					break;
				case 1:
					progressBar.setVisibility(View.GONE);
					TeamAppAlerts.showToast(PollDetailsAct.this,
							getString(R.string.submit_failed) + " "
									+ getString(R.string.poll));
					break;
				case 2:
					progressBar.setVisibility(View.GONE);
					TeamAppAlerts.showToast(PollDetailsAct.this,
							getString(R.string.service));
					break;
				}
			}
		};

		progressBar.setVisibility(View.VISIBLE);
		new Thread() {
			@Override
			public void run() {

				int what = 0;

				// Make request and handle exceptions
				try {
					if (!submitPoll()) {
						what = 1;
					}
				} catch (Exception e) {
					e.printStackTrace();
					what = 2;
				}

				handler.sendEmptyMessage(what);
			}
		}.start();
	}

}
