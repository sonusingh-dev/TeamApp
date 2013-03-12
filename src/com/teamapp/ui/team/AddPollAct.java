package com.teamapp.ui.team;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.teamapp.helper.WebServiceHelper;
import com.teamapp.ui.LoginAct;
import com.teamapp.ui.R;
import com.teamapp.ui.alerts.TeamAppAlerts;

public class AddPollAct extends Activity {

	private boolean isActive;

	private String question;
	private String option1;
	private String option2;
	private String option3;

	private TextView lblTitle;
	private EditText txtQuestion;
	private EditText txtOption1;
	private EditText txtOption2;
	private EditText txtOption3;

	private RadioButton radActiveNo;
	private RadioButton radActiveYes;

	private Button btnCancel;
	private Button btnSubmit;

	private ProgressDialog pd;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_edit_poll);

		lblTitle = (TextView) findViewById(R.id.lblTitle);

		txtQuestion = (EditText) findViewById(R.id.txtQuestion);
		txtOption1 = (EditText) findViewById(R.id.txtOption1);
		txtOption2 = (EditText) findViewById(R.id.txtOption2);
		txtOption3 = (EditText) findViewById(R.id.txtOption3);

		radActiveNo = (RadioButton) findViewById(R.id.radActiveNo);
		radActiveYes = (RadioButton) findViewById(R.id.radActiveYes);

		btnCancel = (Button) findViewById(R.id.btnCancel);
		btnSubmit = (Button) findViewById(R.id.btnSubmit);

		lblTitle.setText(getString(R.string.add) + " "
				+ getString(R.string.poll));

		radActiveYes.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					isActive = true;
				}
			}
		});

		radActiveNo.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					isActive = false;
				}
			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		btnSubmit.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				validate();
			}
		});

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	private void validate() {

		question = txtQuestion.getText().toString();
		if (question == null || question.length() == 0) {
			TeamAppAlerts.showMessageDialog(AddPollAct.this,
					getString(R.string.enter) + " "
							+ getString(R.string.enter_question));
			return;
		}

		option1 = txtOption1.getText().toString();
		if (option1 == null || option1.length() == 0) {
			TeamAppAlerts.showMessageDialog(AddPollAct.this,
					getString(R.string.enter) + " "
							+ getString(R.string.enter_option) + "1");
			return;
		}

		option2 = txtOption2.getText().toString();
		if (option2 == null || option2.length() == 0) {
			TeamAppAlerts.showMessageDialog(AddPollAct.this,
					getString(R.string.enter) + " "
							+ getString(R.string.enter_option) + "2");
			return;
		}

		option3 = txtOption3.getText().toString();
		if (option3 == null || option3.length() == 0) {
			TeamAppAlerts.showMessageDialog(AddPollAct.this,
					getString(R.string.enter) + " "
							+ getString(R.string.enter_option) + "3");
			return;
		}

		runInsert();
	}

	private boolean insertPoll() throws Exception {

		String METHOD_NAME = "InsertPoll";
		String envelope = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\" xmlns:voet=\"http://schemas.datacontract.org/2004/07/VoetBall.Entities.DataObjects\">"
				+ "<soapenv:Header/>"
				+ "<soapenv:Body>"
				+ "<tem:InsertPoll>"
				+ "<tem:dPoll>"
				+ "<voet:Option1>%s</voet:Option1>"
				+ "<voet:Option2>%s</voet:Option2>"
				+ "<voet:Option3>%s</voet:Option3>"
				+ "<voet:Question>%s</voet:Question>"
				+ "<voet:CreatedByUserId>%s</voet:CreatedByUserId>"
				+ "<voet:IsActive>%s</voet:IsActive>"
				+ "<voet:Option1Id>0</voet:Option1Id>"
				+ "<voet:Option2Id>0</voet:Option2Id>"
				+ "<voet:Option3Id>0</voet:Option3Id>"
				+ "<voet:PollId>0</voet:PollId>"
				+ "</tem:dPoll>"
				+ "<tem:clubId>%s</tem:clubId>"
				+ "</tem:InsertPoll>"
				+ "</soapenv:Body>" + "</soapenv:Envelope>";

		String request = String.format(envelope, option1, option2, option3,
				question, LoginAct.userId, isActive, LoginAct.clubId);

		Log.d("request", request);

		String response = (String) WebServiceHelper.callWebService(METHOD_NAME,
				request);

		Log.d("response", response);

		return WebServiceHelper.getStatus(response);

	}

	private void runInsert() {

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case 0:
					pd.dismiss();
					setResult(RESULT_OK);
					finish();
					break;
				case 1:
					pd.dismiss();
					TeamAppAlerts.showToast(AddPollAct.this,
							getString(R.string.insert_failed) + ""
									+ getString(R.string.poll));
					break;
				case 2:
					pd.dismiss();
					TeamAppAlerts.showToast(AddPollAct.this,
							getString(R.string.service));
					break;
				}
			}
		};

		pd = ProgressDialog.show(AddPollAct.this, "", getString(R.string.wait),
				true, false);
		new Thread() {
			@Override
			public void run() {

				int what = 0;

				// Make request and handle exceptions
				try {
					if (!insertPoll()) {
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
