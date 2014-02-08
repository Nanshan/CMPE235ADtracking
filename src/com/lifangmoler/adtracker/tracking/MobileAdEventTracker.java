package com.lifangmoler.adtracker.tracking;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.location.Location;
import android.telephony.TelephonyManager;

/**
 * Utility class for adding event tracking records to the database.
 * 
 * @author Leah
 *
 */
public class MobileAdEventTracker {
	
	private String locationStr = "unavailable";
	
	private String deviceId;
	
	private String sender;
	
	public MobileAdEventTracker(Context context, Location location) {
		updateLocationIfAvailable(location);
		
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		deviceId = tm.getDeviceId();
		sender = tm.getLine1Number();
	}
	
	public void updateLocationIfAvailable(Location location) {
		if (location != null) {
			locationStr = Double.toString(location.getLatitude()) + ":" + Double.toString(location.getLongitude());
		}
	}
	
	public void trackImpressionEvent(String advertName) {
		trackBasicEvent("impression", advertName);
	}

	public void trackClickEvent(String advertName) {
		trackBasicEvent("click", advertName);
	}

	public void trackClickThroughEvent(String advertName) {
		trackBasicEvent("clickthru", advertName);
	}

	private void trackBasicEvent(String urlPath, String advertName) {
		try {
			HttpPost post = new HttpPost("http://cmpe235project.herokuapp.com/events/"+urlPath);
			List<NameValuePair> pairs = getBaseEventParameters(advertName);
			post.setEntity(new UrlEncodedFormEntity(pairs));
			new HttpPostTrackingEventTask().execute(post);
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	private List<NameValuePair> getBaseEventParameters(String advertName) {
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("ad_name", advertName));
		pairs.add(new BasicNameValuePair("user_location", locationStr));
		pairs.add(new BasicNameValuePair("user_phone_id", deviceId));
		return pairs;
	}

	public void trackSMSEvent(String advertName, String receiver, String message) {
		try {
			HttpPost post = new HttpPost("http://cmpe235project.herokuapp.com/events/sms");
			List<NameValuePair> pairs = getBaseEventParameters(advertName);
			pairs.add(new BasicNameValuePair("sender", sender));
			pairs.add(new BasicNameValuePair("receiver", receiver));
			pairs.add(new BasicNameValuePair("message", message));
			post.setEntity(new UrlEncodedFormEntity(pairs));
			new HttpPostTrackingEventTask().execute(post);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public void trackCallEvent(String advertName, String receiver) {
		try {
			HttpPost post = new HttpPost("http://cmpe235project.herokuapp.com/events/call");
			List<NameValuePair> pairs = getBaseEventParameters(advertName);
			pairs.add(new BasicNameValuePair("sender", sender));
			pairs.add(new BasicNameValuePair("receiver", receiver));
			post.setEntity(new UrlEncodedFormEntity(pairs));
			new HttpPostTrackingEventTask().execute(post);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public void trackMapEvent(String advertName, String address) {
		try {
			HttpPost post = new HttpPost("http://cmpe235project.herokuapp.com/events/map");
			List<NameValuePair> pairs = getBaseEventParameters(advertName);
			pairs.add(new BasicNameValuePair("address", address));
			post.setEntity(new UrlEncodedFormEntity(pairs));
			new HttpPostTrackingEventTask().execute(post);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}
