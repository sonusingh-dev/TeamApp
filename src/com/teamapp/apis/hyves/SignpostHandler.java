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

import java.io.IOException;
import java.io.InputStream;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;


public final class SignpostHandler
{
  public static String retrieveRequestTokenWithSignpost(OAuthProvider provider, OAuthConsumer consumer, String uri)
  {
    try
    {
      return provider.retrieveRequestToken(consumer, uri);
    }
    catch (OAuthCommunicationException e) { }
    catch (OAuthNotAuthorizedException e) { }
    catch (OAuthExpectationFailedException e) { }
    catch (OAuthMessageSignerException e) { }

    return null;
  }

  public static boolean retrieveAccessTokenWithSignpost(OAuthProvider provider, OAuthConsumer consumer, String sCode)
  {
    try
    {
      provider.retrieveAccessToken(consumer, sCode);
      return true;
    }
    catch (OAuthExpectationFailedException e) { }
    catch (OAuthCommunicationException e) { }
    catch (OAuthMessageSignerException e) { }
    catch (OAuthNotAuthorizedException e) { }

    return false;
  }

  public static boolean signWithSignpost(OAuthConsumer consumer, HttpGet request)
  {
    try
    {
      consumer.sign(request);
      return true;
    }
    catch (OAuthExpectationFailedException e) { }
    catch (OAuthCommunicationException e) { }
    catch (OAuthMessageSignerException e) { }
    
    return false;
  }

  public static boolean signWithSignpost(OAuthConsumer consumer, HttpPost request)
  {
    try
    {
      consumer.sign(request);
      return true;
    }
    catch (OAuthExpectationFailedException e) { }
    catch (OAuthCommunicationException e) { }
    catch (OAuthMessageSignerException e) { }
    
    return false;
  }

  public static HttpResponse executeHttpGetWithSignpost(String uri, OAuthConsumer consumer)
  {
    HttpGet request = new HttpGet(uri);
    if (false == SignpostHandler.signWithSignpost(consumer, request))
      return null;

    HttpClient httpClient = new DefaultHttpClient();
    try
    {
      return httpClient.execute(request);
    }
    catch (IOException e) { }

    return null;
  }

  public static HttpResponse executeHttpPostWithSignpost(String uri, OAuthConsumer consumer)
  {
    HttpPost request = new HttpPost(uri);
    if (false == SignpostHandler.signWithSignpost(consumer, request))
      return null;

    HttpClient httpClient = new DefaultHttpClient();
    try
    {
      return httpClient.execute(request);
    }
    catch (IOException e) { }

    return null;
  }

  public static String getResposeResult(HttpResponse response)
  {
    HttpEntity entity = response.getEntity();
    if (null == entity)
      return null;

    try
    {
      InputStream instream = entity.getContent();
      return GenUtil.convertStreamToString(instream);
    }
    catch (IOException e) { }

    return null;
  }

}
