package com.teamapp.helper;

import java.io.IOException;
import java.io.StringReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class WebServiceHelper {

	private static final String NAMESPACE = "http://tempuri.org/";
	private static final String SOAP_ACTION = "http://tempuri.org/IVoetBallService/";
	private static final String URL = "http://voetball.aspnetdevelopment.in/VoetBallService.svc";

	public static SoapObject getSOAPRequest(String METHOD_NAME) {
		return new SoapObject(NAMESPACE, METHOD_NAME);
	}

	public static Object getSOAPResponse(SoapObject request, String METHOD_NAME)
			throws Exception {

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.dotNet = true;		
		envelope.setOutputSoapObject(request);

		AndroidHttpTransport transport = new AndroidHttpTransport(URL);
		transport.debug = true;

		transport.call(SOAP_ACTION + METHOD_NAME, envelope);

		return envelope.getResponse();
	}

	public static Object callWebService(String METHOD_NAME, String request)
			throws Exception {

		DefaultHttpClient httpClient = new DefaultHttpClient();
		// request parameters
		HttpParams params = httpClient.getParams();
		HttpConnectionParams.setConnectionTimeout(params, 10000);
		HttpConnectionParams.setSoTimeout(params, 15000);
		// set parameter
		HttpProtocolParams.setUseExpectContinue(httpClient.getParams(), true);

		// POST the envelope
		HttpPost httppost = new HttpPost(URL);
		// add headers
		httppost.setHeader("SOAPAction", SOAP_ACTION + METHOD_NAME);
		httppost.setHeader("Content-Type", "text/xml; charset=utf-8");

		Object response = "";

		// the entity holds the request
		HttpEntity entity = new StringEntity(request);
		httppost.setEntity(entity);

		// Response handler
		ResponseHandler<Object> rh = new ResponseHandler<Object>() {
			// invoked when client receives response
			public String handleResponse(HttpResponse response)
					throws ClientProtocolException, IOException {

				// get response entity
				HttpEntity entity = response.getEntity();

				// read the response as byte array
				StringBuffer out = new StringBuffer();
				byte[] b = EntityUtils.toByteArray(entity);

				// write the response byte array to a string buffer
				out.append(new String(b, 0, b.length));
				return out.toString();
			}
		};

		response = httpClient.execute(httppost, rh);

		// close the connection
		httpClient.getConnectionManager().shutdown();
		return response;
	}

	public static boolean getStatus(String response)
			throws XmlPullParserException, IOException {

		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser xpp = factory.newPullParser();

		xpp.setInput(new StringReader(response));
		int eventType = xpp.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			if (eventType == XmlPullParser.TEXT) {
				boolean status = Boolean.parseBoolean(xpp.getText());
				return status;
			}
			eventType = xpp.next();
		}
		return false;
	}

}