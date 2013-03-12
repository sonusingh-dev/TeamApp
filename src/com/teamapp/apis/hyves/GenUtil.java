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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;


public final class GenUtil 
{
  public static Bundle decodeUrl(String s) 
  {
    Bundle params = new Bundle();
    if (s != null) 
    {
      String array[] = s.split("&");
      for (String parameter : array) 
      {
        String v[] = parameter.split("=");
        params.putString(v[0], v[1]);
      }
    }
    return params;
  }

  public static Bundle parseUrl(String url, String prefix) 
  {
    url = url.replace(prefix, "http"); 
    try 
    {
      URL u = new URL(url);
      Bundle b = decodeUrl(u.getQuery());
      b.putAll(decodeUrl(u.getRef()));
      return b;
    } 
    catch (MalformedURLException e) 
    {
      return new Bundle();
    }
  }

  public static void clearCookies(Context context) 
  {
    @SuppressWarnings("unused")
    CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(context);
    CookieManager cookieManager = CookieManager.getInstance();
    cookieManager.removeAllCookie();
  }

  public static String convertStreamToString(InputStream is) 
  {
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    StringBuilder sb = new StringBuilder();

    String line = null;
    try 
    {
      while ((line = reader.readLine()) != null) 
      {
        sb.append(line + "\n");
      }
    } 
    catch (IOException e) 
    {
      e.printStackTrace();
    } 
    finally 
    {
      try
      {
        is.close();
      } 
      catch (IOException e) 
      {
        e.printStackTrace();
      }
    }
    return sb.toString();
  }

  public static JSONObject getJsonObject(String response)
  {
    try 
    {
      return new JSONObject(response);
    } 
    catch (JSONException e) { }

    return null;
  }

  public static JSONArray getJsonArray(JSONObject json, String keyVal)
  {
    try 
    {
      return json.getJSONArray(keyVal);
    } 
    catch (JSONException e) { }

    return null;
  }

  public static String getJsonKeyString(JSONArray jsonArr, int ind)
  {
    try 
    {
      return jsonArr.getString(ind);
    } 
    catch (JSONException e) { }

    return null;
  }

  public static String getJsonKeyString(JSONArray jsonArr, int ind, String keyVal)
  {
    try 
    {
      return jsonArr.getJSONObject(ind).getString(keyVal);
    } 
    catch (JSONException e) { }

    return null;
  }

  public static String encodeForURL(String url)
  {
    StringBuffer encode = new StringBuffer("");
    char chr;
    for (int i = 0; i < url.length(); ++i)
    {
      chr = url.charAt(i);
      if ( (chr >= '0' && chr <= '9') || (chr >= 'A' && chr <= 'Z') || (chr >= 'a' && chr <= 'z'))
      {
        encode.append(chr);
        continue;
      }

      if (chr <= 15)
      {
        encode.append("%0" + Integer.toHexString((int)chr)) ;
      }
      else
      {
        encode.append("%" + Integer.toHexString((int)chr));
      }
    }

    return encode.toString();
  }

}
