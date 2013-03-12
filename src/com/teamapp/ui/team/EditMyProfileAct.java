package com.teamapp.ui.team;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.kobjects.base64.Base64;
import org.ksoap2.serialization.SoapObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;

import com.teamapp.helper.WebServiceHelper;
import com.teamapp.ui.LoginAct;
import com.teamapp.ui.R;
import com.teamapp.ui.alerts.TeamAppAlerts;

public class EditMyProfileAct extends Activity {

	private static final int CAMERA_PIC_REQUEST = 0;
	private static final int SELECT_PICTURE = 1;

	private int mYear;
	private int mMonth;
	private int mDay;

	private String roleId;
	private String roleName;
	private String userId;
	private String gameAttendance;
	private String trainingAttendance;
	private String taskAttendance;
	private String eventAttendance;
	private String notification;
	private String name;
	private String email;
	private String dateOfBirth;
	private String nickName;
	private String password;
	private String myImage;
	private String teamImage;
	private Calendar calendar;
	private Bitmap bmpImage;

	private EditText txtName;
	private EditText txtEmail;
	private EditText txtDateOfBirth;
	private EditText txtNickName;

	private CheckBox chkGame;
	private CheckBox chkTraining;
	private CheckBox chkTask;
	private CheckBox chkEvent;
	private CheckBox chkNotification;

	private ImageButton btnImage;
	private ImageButton btnMyImage;
	private ImageButton btnTeamImage;

	private Button btnUpdate;
	private Button btnPassword;

	private ProgressDialog pd;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_my_profile);

		txtName = (EditText) findViewById(R.id.txtName);
		txtEmail = (EditText) findViewById(R.id.txtEmail);
		txtDateOfBirth = (EditText) findViewById(R.id.txtDateOfBirth);
		txtNickName = (EditText) findViewById(R.id.txtNickName);

		chkGame = (CheckBox) findViewById(R.id.chkGame);
		chkTraining = (CheckBox) findViewById(R.id.chkTraining);
		chkTask = (CheckBox) findViewById(R.id.chkTask);
		chkEvent = (CheckBox) findViewById(R.id.chkEvent);
		chkNotification = (CheckBox) findViewById(R.id.chkNotification);

		btnMyImage = (ImageButton) findViewById(R.id.btnMyImage);
		btnTeamImage = (ImageButton) findViewById(R.id.btnTeamImage);

		btnUpdate = (Button) findViewById(R.id.btnUpdate);
		btnPassword = (Button) findViewById(R.id.btnPassword);

		chkGame.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				gameAttendance = String.valueOf(isChecked);
			}
		});

		chkTraining.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				trainingAttendance = String.valueOf(isChecked);
			}
		});

		chkTask.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				taskAttendance = String.valueOf(isChecked);
			}
		});

		chkEvent.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				eventAttendance = String.valueOf(isChecked);
			}
		});

		chkNotification
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						notification = String.valueOf(isChecked);
					}
				});

		btnMyImage.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {
				// TODO Auto-generated method stub
				btnImage = btnMyImage;
				showImagePopup(view);
			}
		});

		btnTeamImage.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {
				// TODO Auto-generated method stub
				if (!LoginAct.roleId.equals("4")) {
					btnImage = btnTeamImage;
					showImagePopup(view);
				}
			}
		});

		txtDateOfBirth.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog(0);
			}
		});

		btnUpdate.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				validate();
			}
		});

		btnPassword.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				changePasswordDialog();
			}
		});

		runDefault();

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected Dialog onCreateDialog(int id) {

		DatePickerDialog datePickerDialog = new DatePickerDialog(this,
				mDateSetListener, mYear, mMonth, mDay);

		// remove year field from DatePicker
		// Field fieldDatePicker;
		// try {
		//
		// fieldDatePicker = datePickerDialog.getClass().getDeclaredField(
		// "mDatePicker");
		// fieldDatePicker.setAccessible(true);
		// try {
		//
		// DatePicker datePicker = (DatePicker) fieldDatePicker
		// .get(datePickerDialog);
		// Field fieldMonthPicker = datePicker.getClass()
		// .getDeclaredField("mYearPicker");
		// fieldMonthPicker.setAccessible(true);
		// Object dayPicker = new Object();
		// dayPicker = fieldMonthPicker.get(datePicker);
		// ((View) dayPicker).setVisibility(View.GONE);
		// Log.d("Field", "year :: " + fieldDatePicker.getName());
		//
		// } catch (IllegalArgumentException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (IllegalAccessException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// } catch (SecurityException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (NoSuchFieldException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		return datePickerDialog;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == RESULT_OK) {
			Bitmap bmpImage = null;
			switch (requestCode) {
			case CAMERA_PIC_REQUEST:
				bmpImage = (Bitmap) data.getExtras().get("data");
				break;
			case SELECT_PICTURE:
				Uri selectedImageUri = data.getData();
				// MEDIA GALLERY
				String imagePath = getPath(selectedImageUri);
				try {
					bmpImage = BitmapFactory.decodeFile(imagePath);
				} catch (OutOfMemoryError e) {
					// TODO: handle exception
					TeamAppAlerts.showMessageDialog(EditMyProfileAct.this,
							getString(R.string.image_large));
					e.printStackTrace();
				}
				break;
			}
			if (bmpImage != null) {
				btnImage.setImageBitmap(bmpImage);
			}
		}
	}

	private String getPath(Uri uri) {

		String[] projection = { MediaColumns.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	private void populateMyProfile() {

		byte[] temp = null;

		try {
			temp = Base64.decode(myImage);
			bmpImage = BitmapFactory.decodeByteArray(temp, 0, temp.length);
			btnMyImage.setImageBitmap(bmpImage);

			temp = Base64.decode(teamImage);
			bmpImage = BitmapFactory.decodeByteArray(temp, 0, temp.length);
			btnTeamImage.setImageBitmap(bmpImage);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss");

		calendar = Calendar.getInstance();

		try {

			calendar.setTime(dateFormat.parse(dateOfBirth));
			mYear = calendar.get(Calendar.YEAR);
			mMonth = calendar.get(Calendar.MONTH);
			mDay = calendar.get(Calendar.DATE);

			updateDate();

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		txtName.setText(name);

		txtEmail.setText(email);

		txtNickName.setText(nickName);

		if (gameAttendance != null && gameAttendance.length() != 0) {
			chkGame.setChecked(Boolean.parseBoolean(gameAttendance));
		}

		if (trainingAttendance != null && trainingAttendance.length() != 0) {
			chkTraining.setChecked(Boolean.parseBoolean(trainingAttendance));
		}

		if (taskAttendance != null && taskAttendance.length() != 0) {
			chkTask.setChecked(Boolean.parseBoolean(taskAttendance));
		}

		if (eventAttendance != null && eventAttendance.length() != 0) {
			chkEvent.setChecked(Boolean.parseBoolean(eventAttendance));
		}

		if (notification != null && notification.length() != 0) {
			chkNotification.setChecked(Boolean.parseBoolean(notification));
		}

	}

	private void getMyProfile() throws Exception {

		String METHOD_NAME = "GetPlayerByPlayerId";

		SoapObject request = WebServiceHelper.getSOAPRequest(METHOD_NAME);
		request.addProperty("playerId", LoginAct.userId);

		SoapObject response = (SoapObject) WebServiceHelper.getSOAPResponse(request,
				METHOD_NAME);

		if (response != null) {

			roleId = response.getProperty("RoleId").toString();
			roleName = response.getProperty("RoleName").toString();
			name = response.getProperty("Name").toString();
			userId = response.getProperty("UserId").toString();

			if (response.getProperty("Email") != null) {
				email = response.getProperty("Email").toString();
			}

			if (response.getProperty("AutoEventAttendance") != null) {
				eventAttendance = response.getProperty("AutoEventAttendance")
						.toString();
			}

			if (response.getProperty("AutoMatchAttendance") != null) {
				gameAttendance = response.getProperty("AutoMatchAttendance")
						.toString();
			}

			if (response.getProperty("AutoTaskAttendance") != null) {
				taskAttendance = response.getProperty("AutoTaskAttendance")
						.toString();
			}

			if (response.getProperty("AutoTrainingAttendance") != null) {
				trainingAttendance = response.getProperty(
						"AutoTrainingAttendance").toString();
			}

			if (response.getProperty("DateOfBirth") != null) {
				dateOfBirth = response.getProperty("DateOfBirth").toString();
			}

			if (response.getProperty("NickName") != null) {
				nickName = response.getProperty("NickName").toString();
			}

			if (response.getProperty("Image") != null) {
				myImage = response.getProperty("Image").toString();
			}

			if (response.getProperty("ReceiveNotification") != null) {
				notification = response.getProperty("ReceiveNotification")
						.toString();
			}

			if (response.getProperty("TeamPicture") != null) {
				teamImage = response.getProperty("TeamPicture").toString();
			}

			password = LoginAct.password;
		}
	}

	private void validate() {

		byte[] temp = null;
		ByteArrayOutputStream baos = null;
		BitmapDrawable bitmapDrawable = null;

		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss");
		calendar.set(mYear, mMonth, mDay);
		dateOfBirth = dateFormat.format(calendar.getTime());

		Log.d("dateOfBirth", dateOfBirth);

		try {

			// MyImage
			baos = new ByteArrayOutputStream();
			bitmapDrawable = (BitmapDrawable) btnMyImage.getDrawable();
			bmpImage = bitmapDrawable.getBitmap();
			bmpImage.compress(Bitmap.CompressFormat.PNG, 100, baos);
			temp = baos.toByteArray();
			myImage = Base64.encode(temp);
			Log.d("myImage", myImage);

			// TeamImage
			baos = new ByteArrayOutputStream();
			bitmapDrawable = (BitmapDrawable) btnTeamImage.getDrawable();
			bmpImage = bitmapDrawable.getBitmap();
			bmpImage.compress(Bitmap.CompressFormat.PNG, 100, baos);
			temp = baos.toByteArray();
			teamImage = Base64.encode(temp);
			Log.d("teamImage", teamImage);

		} catch (OutOfMemoryError e) {
			// TODO: handle exception
			TeamAppAlerts.showMessageDialog(EditMyProfileAct.this,
					getString(R.string.image_large));
			e.printStackTrace();
			return;
		}

		name = txtName.getText().toString();
		if (name == null || name.length() == 0) {
			TeamAppAlerts.showMessageDialog(EditMyProfileAct.this,
					getString(R.string.enter) + " "
							+ getString(R.string.enter_name));
			return;
		}

		email = txtEmail.getText().toString();
		if (!isEmailIdValid(email)) {
			TeamAppAlerts.showMessageDialog(EditMyProfileAct.this,
					getString(R.string.email_invalid));
			return;
		}

		nickName = txtNickName.getText().toString();
		if (nickName == null || nickName.length() == 0) {
			TeamAppAlerts.showMessageDialog(EditMyProfileAct.this,
					getString(R.string.enter) + " "
							+ getString(R.string.enter_nick));
			return;
		}

		runUpdate();
	}

	private boolean updateMyProfile() throws Exception {

		String METHOD_NAME = "UpdateUserProfile";
		String envelope = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tem=\"http://tempuri.org/\" xmlns:voet=\"http://schemas.datacontract.org/2004/07/VoetBall.Entities.DataObjects\">"
				+ "<soapenv:Header/>"
				+ "<soapenv:Body>"
				+ "<tem:UpdateUserProfile>"
				+ "<tem:user>"
				+ "<voet:RoleId>%s</voet:RoleId>"
				+ "<voet:RoleName>%s</voet:RoleName>"
				+ "<voet:Email>%s</voet:Email>"
				+ "<voet:Name>%s</voet:Name>"
				+ "<voet:UserId>%s</voet:UserId>"
				+ "<voet:AutoEventAttendance>%s</voet:AutoEventAttendance>"
				+ "<voet:AutoMatchAttendance>%s</voet:AutoMatchAttendance>"
				+ "<voet:AutoTaskAttendance>%s</voet:AutoTaskAttendance>"
				+ "<voet:AutoTrainingAttendance>%s</voet:AutoTrainingAttendance>"
				+ "<voet:DateOfBirth>%s</voet:DateOfBirth>"
				+ "<voet:Image>%s</voet:Image>"
				+ "<voet:NickName>%s</voet:NickName>"
				+ "<voet:Password>%s</voet:Password>"
				+ "<voet:ReceiveNotification>%s</voet:ReceiveNotification>"
				+ "<voet:TeamPicture>%s</voet:TeamPicture>"
				+ "</tem:user>"
				+ "</tem:UpdateUserProfile>"
				+ "</soapenv:Body>"
				+ "</soapenv:Envelope>";

		String request = String.format(envelope, roleId, roleName, email, name,
				userId, eventAttendance, gameAttendance, taskAttendance,
				trainingAttendance, dateOfBirth, myImage, nickName, password,
				notification, teamImage);

		Log.d("request", "request :: " + request);

		String response = (String) WebServiceHelper.callWebService(METHOD_NAME,
				request);

		Log.d("response", "response :: " + response);

		return WebServiceHelper.getStatus(response);
	}

	private boolean changePassword(String password) throws Exception {

		String METHOD_NAME = "ChangePassword";

		SoapObject request = WebServiceHelper.getSOAPRequest(METHOD_NAME);
		request.addProperty("newPassword", password);
		request.addProperty("userId", userId);

		Object response = WebServiceHelper.getSOAPResponse(request, METHOD_NAME);

		Log.d("changePassword", "status :: " + response.toString());

		return Boolean.parseBoolean(response.toString());

	}

	private void changePasswordDialog() {

		LayoutInflater inflater = LayoutInflater.from(EditMyProfileAct.this);
		View view = inflater.inflate(R.layout.edit_password_dialog, null);
		final EditText txtCurrentPassword = (EditText) view
				.findViewById(R.id.txtCurrentPassword);
		final EditText txtNewPassword = (EditText) view
				.findViewById(R.id.txtNewPassword);
		final EditText txtConfirmNewPassword = (EditText) view
				.findViewById(R.id.txtConfirmNewPassword);

		AlertDialog.Builder builder = new AlertDialog.Builder(
				EditMyProfileAct.this);
		builder.setView(view);
		builder.setTitle(getString(R.string.profile_password));
		builder.setPositiveButton(getString(R.string.save),
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// Do nothing but close the dialog
						String currentPassword = txtCurrentPassword.getText()
								.toString();

						if (!password.equals(currentPassword)) {
							TeamAppAlerts.showMessageDialog(
									EditMyProfileAct.this,
									getString(R.string.password_incorrect));
							return;
						}

						String newPassword = txtNewPassword.getText()
								.toString();
						String confirmNewPassword = txtConfirmNewPassword
								.getText().toString();

						if (newPassword.length() < 6
								|| newPassword.length() > 10) {
							TeamAppAlerts.showMessageDialog(
									EditMyProfileAct.this,
									getString(R.string.password_limit));
							return;
						}

						if (!newPassword.equals(confirmNewPassword)) {
							TeamAppAlerts.showMessageDialog(
									EditMyProfileAct.this,
									getString(R.string.password_match));
							return;
						}

						try {
							if (!changePassword(newPassword)) {
								TeamAppAlerts.showMessageDialog(
										EditMyProfileAct.this,
										getString(R.string.password_failed));
							} else {
								password = newPassword;
								LoginAct.password = newPassword;
								TeamAppAlerts.showMessageDialog(
										EditMyProfileAct.this,
										getString(R.string.password_success));
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							TeamAppAlerts.showToast(EditMyProfileAct.this,
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

		// Remember, create doesn't show the dialog
		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}

	// PopupWindow for Photo and Video
	private void showImagePopup(View view) {

		LayoutInflater inflater = LayoutInflater.from(this);
		View popupView = inflater.inflate(R.layout.image_chooser_popup, null);
		popupView.measure(View.MeasureSpec.UNSPECIFIED,
				View.MeasureSpec.UNSPECIFIED);

		Display display = getWindowManager().getDefaultDisplay();
		int width = display.getWidth();
		int height = popupView.getMeasuredHeight() + 10;

		final PopupWindow popupWindow = new PopupWindow(popupView, width,
				height);
		popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setTouchable(true);

		Button btnCamera = (Button) popupView.findViewById(R.id.btnCamera);
		Button btnGallery = (Button) popupView.findViewById(R.id.btnGallery);
		Button btnCancel = (Button) popupView.findViewById(R.id.btnCancel);

		btnCamera.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				popupWindow.dismiss();
				Intent cameraIntent = new Intent(
						android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
			}
		});

		btnGallery.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				popupWindow.dismiss();
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(
						Intent.createChooser(intent, "Select Picture"),
						SELECT_PICTURE);
			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				popupWindow.dismiss();
				btnImage = null;
			}
		});

	}

	// DatePicker dialog generation
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			updateDate();
		}
	};

	private void updateDate() {

		txtDateOfBirth.setText(new StringBuilder()
				// Month is 0 based so add 1
				.append(pad(mDay)).append("-").append(pad(mMonth + 1))
				.append("-").append(mYear));
	}

	private String pad(int c) {
		if (c >= 10)
			return String.valueOf(c);
		else
			return "0" + String.valueOf(c);
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

	private void runDefault() {

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case 0:
					populateMyProfile();
					pd.dismiss();
					break;
				case 1:
					pd.dismiss();
					TeamAppAlerts.showToast(EditMyProfileAct.this,
							getString(R.string.service));
					break;
				}
			}
		};

		pd = ProgressDialog.show(EditMyProfileAct.this, "",
				getString(R.string.loading), true, false);
		new Thread() {
			@Override
			public void run() {

				int what = 0;

				// Make request and handle exceptions
				try {
					getMyProfile();
				} catch (Exception e) {
					e.printStackTrace();
					what = 1;
				}

				handler.sendEmptyMessage(what);
			}
		}.start();
	}

	private void runUpdate() {

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
					TeamAppAlerts.showToast(EditMyProfileAct.this,
							getString(R.string.update_failed) + " "
									+ getString(R.string.profile_header));
					break;
				case 2:
					pd.dismiss();
					TeamAppAlerts.showToast(EditMyProfileAct.this,
							getString(R.string.service));
					break;
				}
			}
		};

		pd = ProgressDialog.show(EditMyProfileAct.this, "",
				getString(R.string.wait), true, false);

		new Thread() {
			@Override
			public void run() {

				int what = 0;

				// Make request and handle exceptions
				try {
					if (!updateMyProfile()) {
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
