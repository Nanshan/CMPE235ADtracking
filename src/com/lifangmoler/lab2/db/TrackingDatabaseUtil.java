package com.lifangmoler.lab2.db;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.net.http.AndroidHttpClient;
import android.telephony.TelephonyManager;
import android.text.format.Time;

/**
 * Utility class for adding event tracking records to the database.
 * 
 * @author Leah
 *
 */
public final class TrackingDatabaseUtil {
	
	/**
	 * Add an advertisement to the database.
	 * @param c AS the context
	 * @param name AS the ad name
	 * @param url AS the ad URL
	 * @return advert_id AS id in the database. -1 if failed
	 */
	public static long addAdvert(Context c, String name, String url) {
		SQLiteDatabase db = getDbFromContext(c);

		ContentValues values = new ContentValues();
		values.put("advert_name", name);
		values.put("advert_url", url);
		return db.insert("advert", null, values);
	}
	
	/**
	 * Add an impression tracking event to the database.
	 * @param c the context
	 * @param advertName name of the ad having the impression
	 * @param loc location of the user
	 * @return event_id in the database. -1 if failed
	 */
	public static long addImpressionEvent(Context c, String advertName, Location loc) {
		try {
			HttpPost post = new HttpPost("http://cmpe235project.herokuapp.com/events/impression");
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			//pairs.add(new BasicNameValuePair("ad_name", advertName));
			post.setEntity(new UrlEncodedFormEntity(pairs));
			new PostEventTask().execute(post);
			//return addEvent(c, advertName, 0, loc);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * Add a click tracking event to the database.
	 * @param c the context
	 * @param advertName name of the ad having the click
	 * @param loc location of the user
	 * @return event_id in the database. -1 if failed
	 */
	public static long addClickEvent(Context c, String advertName, Location loc) {
		return addEvent(c, advertName, 1, loc);
	}

	/**
	 * Add a click-through tracking event to the database.
	 * @param c the context
	 * @param advertName name of the ad having the click-through
	 * @param loc location of the user
	 * @return event_id in the database. -1 if failed
	 */
	public static long addClickThroughEvent(Context c, String advertName, Location loc) {
		return addEvent(c, advertName, 2, loc);
	}

	/**
	 * Add an SMS tracking event to the database.
	 * @param c the context
	 * @param advertName name of the ad having the SMS sent
	 * @param receiver receiver of the SMS
	 * @param message SMS message that was sent
	 * @param loc location of the user
	 * @return event_id in the database. -1 if failed
	 */
	public static long addSMSEvent(Context c, String advertName, 
			String receiver, String message, Location loc) {
		long eventId = addEvent(c, advertName, 3, loc);
		addSMSEventData(c, eventId, receiver, message);
		return eventId;
	}

	/**
	 * Add a call tracking event to the database.
	 * @param c the context
	 * @param advertName name of the ad having the call
	 * @param receiver receiver of the call
	 * @param loc location of the user
	 * @return event_id in the database. -1 if failed
	 */
	public static long addCallEvent(Context c, String advertName, 
			String receiver, Location loc) {
		long eventId = addEvent(c, advertName, 4, loc);
		addCallEventData(c, eventId, receiver);
		return eventId;
	}

	/**
	 * Add a map tracking event to the database.
	 * @param c the context
	 * @param advertName name of the ad having the map event
	 * @param address address found on the map
	 * @param loc location of the user
	 * @return event_id in the database. -1 if failed
	 */
	public static long addMapEvent(Context c, String advertName, 
			String address, Location loc) {
		long eventId = addEvent(c, advertName, 5, loc);
		addMapEventData(c, eventId, address);
		return eventId;
	}

	/**
	 * Helper method to add an event to the database.
	 * @param c the context
	 * @param advertName the ad name
	 * @param eventType the event type ID
	 * @param loc the location of the user
	 * @return event_id in the database. -1 if failed
	 */
	private static long addEvent(Context c, String advertName, 
			int eventType, Location loc) {
		SQLiteDatabase db = getDbFromContext(c);
		TelephonyManager tm = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
		String deviceId = tm.getDeviceId();
				
		ContentValues values = new ContentValues();
		values.put("event_type_id", eventType);
		values.put("advert_id", getAdvertIdByName(db, advertName));
		values.put("timestamp", getTimeStamp());
		values.put("user_phone_id", deviceId);
		putUserLocationIfAvailable(values, loc);
		long result = db.insert("event", null, values);
		db.close();
		return result;
	}

	/**
	 * Helper method to add the user location if it is available (not null).
	 * @param values the DB values object
	 * @param loc the user location; may be null
	 */
	private static void putUserLocationIfAvailable(ContentValues values,
			Location loc) {
		if (loc != null) {
			values.put("user_location", loc.getLatitude()+ " "+loc.getLongitude());
		}
		else {
			values.put("user_location", "n/a");
		}
	}

	/**
	 * Helper method to add SMS-specific event data to the sms_event table.
	 */
	private static void addSMSEventData(Context c, long eventId,
			String receiver, String message) {
		SQLiteDatabase db = getDbFromContext(c);

		ContentValues values = new ContentValues();
		values.put("event_id", eventId);
		values.put("receiver", receiver);
		values.put("message", message);
		db.insert("sms_event", null, values);
		db.close();
		//return result;
	}

	/**
	 * Helper method to add call-specific event data to the call_event table.
	 */
	private static void addCallEventData(Context c, long eventId, String receiver) {
		SQLiteDatabase db = getDbFromContext(c);

		ContentValues values = new ContentValues();
		values.put("event_id", eventId);
		values.put("receiver", receiver);
		db.insert("call_event", null, values);
		db.close();
		//return result;
	}

	/**
	 * Helper method to add map-specific event data to the map_event table.
	 */
	private static void addMapEventData(Context c, long eventId, String address) {
		SQLiteDatabase db = getDbFromContext(c);

		ContentValues values = new ContentValues();
		values.put("event_id", eventId);
		values.put("address", address);
		db.insert("map_event", null, values);
		db.close();
	//	return result;
	}

	/**
	 * Helper method to query the db and fetch the ad ID for the given ad name.
	 * @param db the database
	 * @param advertName the ad name
	 * @return the ad ID for the given name
	 */
	private static long getAdvertIdByName(SQLiteDatabase db, String advertName) {
		String[] projection = {"advert_id"};
		String[] selectionArgs = {advertName};
		Cursor c = db.query("advert", projection, "advert_name = ?", selectionArgs, null, null, null);
		c.moveToFirst();
		long result = c.getLong(c.getColumnIndex("advert_id"));
		c.close();
		return result;
	}

	/**
	 * Helper method to get a writeable database handle from the context.
	 * @param c the context
	 * @return a writeable SQLite DB handle
	 */
	private static SQLiteDatabase getDbFromContext(Context c) {
		LocalTrackingOpenHelper mDbHelper = new LocalTrackingOpenHelper(c);
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		return db;
	}

	/**
	 * Helper method to get a timestamp for the current time.
	 * @return current time as a formatted string
	 */
	private static String getTimeStamp() {
		Time t = new Time();
		t.setToNow();
		return t.format2445();
	}
}
