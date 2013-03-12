/*
 * Copyright (C) 2011 Hyves
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.teamapp.apis.hyves;

import java.util.ArrayList;

public class Globals {
	public static class Friends {
		public String name = null;
		public String location = null;

		public Friends(String name, String location) {
			this.name = new String(name);
			this.location = new String(location);
		}
	}

	public static ArrayList<Friends> lsFriends = new ArrayList<Friends>();

	public static class Hyves {
		public static final String CONSUMER_KEY = "MTAwMTZfzfrNbkPd_4fmn0FQUboMfw==";
		public static final String CONSUMER_SECRET = "MTAwMTZfmrxopkfQibNWqgj3q2HHkw==";

		public static final String REQTOKEN_LINK = "http://data.hyves-api.nl/?strict_oauth_spec_response=true&methods=users.get,friends.get,wwws.create&ha_method=auth.requesttoken&ha_version=2.0";
		public static final String AUTHORIZATION_LINK = "http://www.hyves.nl/mini/api/authorize/";
		public static final String REQACCESSTOKEN_LINK = "http://data.hyves-api.nl/?strict_oauth_spec_response=true&ha_method=auth.accesstoken&ha_version=2.0";

		public static final String REDIRECT_PRE = "connect";
		public static final String REDIRECT_URI = REDIRECT_PRE + "://success";

		public static final String FRIENDS_GET = "http://data.hyves-api.nl/?ha_method=friends.get&ha_version=2.0&ha_format=json&ha_fancylayout=false";
		public static final String FRIENDS_GET_DATA = "http://data.hyves-api.nl/?ha_method=users.get&ha_version=2.0&ha_format=json&ha_fancylayout=false&ha_returnfields=/user/userid,/user/firstname,/user/lastname&ha_responsefields=cityname,countryname";

		public static final String WWW_POST_BASE = "http://data.hyves-api.nl/?ha_method=wwws.create&visibility=friends_of_friends&ha_version=2.0&ha_format=json&ha_fancylayout=false";

		public static final String REVOKE_SELF = "http://data.hyves-api.nl/?ha_method=auth.revokeSelf&ha_version=2.0&ha_format=json";
	}

	public static class SharedPref {
		public static final String SHARED_PREF_TAG = "hyves-sessions";
	}

	public static class SavedSessions {
		public static final String TOKEN_LABEL = "access_token";
		public static final String TOKEN_SECRET = "access_secret";
		public static final String USER_ID = "user_id";
	}

	public static class LoginDialogGUI {
		public static final float[] DIMENSIONS_LANDSCAPE = { 460, 260 };
		public static final float[] DIMENSIONS_PORTRAIT = { 280, 420 };
	}
}
