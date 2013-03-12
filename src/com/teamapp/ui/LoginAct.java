package com.teamapp.ui;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ksoap2.serialization.SoapObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.teamapp.helper.WebServiceHelper;
import com.teamapp.helper.Utility;
import com.teamapp.service.c2dm.C2DMUtility;
import com.teamapp.ui.alerts.TeamAppAlerts;
import com.teamapp.ui.team.GameDetailsAct;

/*
 * This activity start after splash screen and valid user can login from here.
 */
public class LoginAct extends Activity {

	private static final int LOGOUT = 1;

	private static String REGISTRATION_KEY = "registrationKey";
	private static final String KEY_EMAIL = "email";
	private static final String KEY_PASSWORD = "password";

	private static final String LANG_ENGLISH = "en";
	private static final String LANG_DUTCH = "nl";

	private static final String PREF_FILE_NAME = "voetbal";
	private static final String REGISTRATION_URI = "http://voetball.aspnetdevelopment.in/UserRegistration.aspx";

	public static boolean isClubFree;

	public static String roleId;
	public static String roleName;
	public static String userId;
	public static String userName;
	public static String teamId;
	public static String teamName;
	public static String clubId;
	public static String emailId;
	public static String password;

	private String lang = "1";
	private String registrationId;

	private LoginAct mLoginAct;

	private EditText txtEmailId;
	private EditText txtPassword;

	private Button btnLogin;
	private Button btnFAQ;
	private Button btnForgotPassword;
	private Button btnRegister;

	private ProgressDialog pd;

	private SharedPreferences mPreferences;
	private SharedPreferences.Editor mEditor;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		localize(LANG_DUTCH);

		setContentView(R.layout.login);

		mLoginAct = this;
		
		// stores user name and password in SharedPreferences.
		mPreferences = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
		mEditor = mPreferences.edit();

		emailId = mPreferences.getString(KEY_EMAIL, null);
		password = mPreferences.getString(KEY_PASSWORD, null);
		registrationId = mPreferences.getString(REGISTRATION_KEY, null);

		txtEmailId = (EditText) findViewById(R.id.txtEmailId);
		txtPassword = (EditText) findViewById(R.id.txtPassword);

		btnLogin = (Button) findViewById(R.id.btnLogin);
		btnFAQ = (Button) findViewById(R.id.btnFAQ);
		btnForgotPassword = (Button) findViewById(R.id.btnForgotPassword);
		btnRegister = (Button) findViewById(R.id.btnRegister);

		btnLogin.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {
				// TODO Auto-generated method stub
				if (Utility.isNetworkAvailable(mLoginAct)) {
					validate();
				}
			}
		});

		btnFAQ.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {
				// TODO Auto-generated method stub
				if (Utility.isNetworkAvailable(mLoginAct)) {

				}
			}
		});

		btnForgotPassword.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {
				// TODO Auto-generated method stub
				if (Utility.isNetworkAvailable(mLoginAct)) {
					forgotPasswordDialog();
				}
			}
		});

		btnRegister.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {
				// TODO Auto-generated method stub
				if (Utility.isNetworkAvailable(mLoginAct)) {
					Intent browse = new Intent(Intent.ACTION_VIEW, Uri
							.parse(REGISTRATION_URI));
					startActivity(browse);
				}

				// Intent intent = new Intent().setClass(mLoginAct,
				// RegistrationAct.class);
				// startActivity(intent);
			}
		});

		txtEmailId.setText(emailId);
		txtPassword.setText(password);
		
		int session = getIntent().getIntExtra("session", 0);
		if (session != LOGOUT) {
			if (emailId != null && password != null) {
				runAuthenticate();
			}
		} 
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	// Validate the entered data by user
	private void validate() {

		emailId = txtEmailId.getText().toString();
		if (!isEmailIdValid(emailId)) {
			TeamAppAlerts.showMessageDialog(mLoginAct,
					getString(R.string.email_invalid));
			return;
		}

		password = txtPassword.getText().toString();
		if (txtPassword.length() < 1) {
			TeamAppAlerts.showMessageDialog(mLoginAct,
					getString(R.string.password_invalid));
			return;
		}

		runAuthenticate();
	}

	// Android Localization
	private void localize(String lang) {

		Locale locale = new Locale(lang);
		Locale.setDefault(locale);
		Configuration config = new Configuration();
		config.locale = locale;
		getApplicationContext().getResources().updateConfiguration(config,
				getBaseContext().getResources().getDisplayMetrics());
	}

	/*
	 * Calls webservice to validate the user
	 * 
	 * @return boolean true for valid false for invalid users
	 */
	private boolean authenticateUser() throws Exception {

		boolean isValid = false;
		String METHOD_NAME = "AuthenticateUser";

		SoapObject request = WebServiceHelper.getSOAPRequest(METHOD_NAME);
		request.addProperty("emailId", emailId);
		request.addProperty("password", password);
		SoapObject response = (SoapObject) WebServiceHelper.getSOAPResponse(request,
				METHOD_NAME);
		if (response != null) {

			if (response.getProperty("RoleId") != null) {
				roleId = response.getProperty("RoleId").toString();
			}

			if (response.getProperty("RoleName") != null) {
				roleName = response.getProperty("RoleName").toString();
			}

			if (response.getProperty("UserId") != null) {
				userId = response.getProperty("UserId").toString();
			}

			if (response.getProperty("Name") != null) {
				userName = response.getProperty("Name").toString();
			}

			if (response.getProperty("IsClubFree") != null) {
				String temp = response.getProperty("IsClubFree").toString();
				isClubFree = Boolean.parseBoolean(temp);
			}

			if (response.getProperty("TeamId") != null) {
				teamId = response.getProperty("TeamId").toString();
			}

			if (response.getProperty("TeamName") != null) {
				teamName = response.getProperty("TeamName").toString();
			}

			if (response.getProperty("ClubId") != null) {
				clubId = response.getProperty("ClubId").toString();
			}

			if (response.getProperty("Language") != null) {
				lang = response.getProperty("Language").toString();
			}

			isValid = true;
		}

		return isValid;

	}

	/*
	 * Calls webservice if user forgot the password and password is maile to
	 * their emailId
	 * 
	 * @return boolean true for valid false for invalid users
	 */
	private boolean forgotPassword(String emailId) throws Exception {

		String METHOD_NAME = "ForgotPassword";

		SoapObject request = WebServiceHelper.getSOAPRequest(METHOD_NAME);
		request.addProperty("Email", emailId);

		Object response = WebServiceHelper.getSOAPResponse(request, METHOD_NAME);

		return Boolean.parseBoolean(response.toString());
	}

	// Shows dialog to enter email address if user click on forgot password
	// button.
	private void forgotPasswordDialog() {

		final EditText txtEmailId = new EditText(mLoginAct);
		txtEmailId.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

		AlertDialog.Builder builder = new AlertDialog.Builder(mLoginAct);
		builder.setView(txtEmailId);
		builder.setTitle(getString(R.string.login_forgot_password));
		builder.setMessage(getString(R.string.reset_password));
		builder.setPositiveButton(getString(R.string.send),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// Do nothing but close the dialog
						String emailId = txtEmailId.getText().toString();
						if (emailId.length() == 0) {
							TeamAppAlerts.showMessageDialog(mLoginAct,
									getString(R.string.email_require));
							return;
						}

						if (!isEmailIdValid(emailId)) {
							TeamAppAlerts.showMessageDialog(mLoginAct,
									getString(R.string.login_failed));
							return;
						}

						try {
							if (!forgotPassword(emailId)) {
								TeamAppAlerts.showMessageDialog(mLoginAct,
										getString(R.string.email_invalid));

							} else {
								TeamAppAlerts.showMessageDialog(mLoginAct,
										getString(R.string.email_password));
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							TeamAppAlerts.showToast(mLoginAct,
									getString(R.string.service));
							e.printStackTrace();
						}
					}
				});

		builder.setNegativeButton(getString(R.string.cancel),
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// Do nothing
					}
				});

		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}

	/**
	 * method is used for checking valid email id format.
	 * 
	 * @param email
	 * @return boolean true for valid false for invalid
	 */
	public static boolean isEmailIdValid(String email) {
		boolean isValid = false;

		String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
		CharSequence inputStr = email;

		Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputStr);
		if (matcher.matches()) {
			isValid = true;
		}
		return isValid;
	}

	private void runAuthenticate() {

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case 0:
					pd.dismiss();					
					if (lang != null && lang.equals("1")) {
						localize(LANG_ENGLISH);
					}

					mEditor.putString(KEY_EMAIL, emailId);
					mEditor.putString(KEY_PASSWORD, password);
					mEditor.commit();

					// Register to C2DM server					
					if (registrationId == null) {						
						C2DMUtility.register(mLoginAct, "androidwala@gmail.com");
					} else {						
						C2DMUtility.runRegistrationIdToServer(mLoginAct, registrationId);
					}
					
					Intent intent = new Intent().setClass(mLoginAct,
							GameDetailsAct.class);
					startActivity(intent);
					break;
				case 1:
					pd.dismiss();
					TeamAppAlerts.showMessageDialog(mLoginAct,
							getString(R.string.login_failed));
					break;
				case 2:
					pd.dismiss();
					TeamAppAlerts.showToast(getApplicationContext(),
							getString(R.string.service));
					break;
				}
			}
		};

		pd = ProgressDialog.show(mLoginAct, "", getString(R.string.wait), true,
				false);
		new Thread() {
			@Override
			public void run() {

				int what = 0;

				try {
					if (!authenticateUser()) {
						what = 1;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					what = 2;
					e.printStackTrace();
				}

				handler.sendEmptyMessage(what);

			}
		}.start();

	}

}
