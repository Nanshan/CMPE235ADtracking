package com.lifangmoler.lab2.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Helper class for creating, upgrading, opening or closing the local tracking
 * database, which is an instance of SQLiteDatabase.
 * 
 * @author Leah
 *
 */
public class LocalTrackingOpenHelper extends SQLiteOpenHelper {
	private static final int DB_VERSION = 1;
	private static final String DB_NAME = "adtracking.db";
	private static final String[] TABLES = new String[6];
	private static final ContentValues[] EVENT_TYPES = new ContentValues[6];
	
	/**
	 * Populate this helper class with the table schemas and event types.
	 * @param context the context
	 */
	public LocalTrackingOpenHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		populateTableSchemas();
		populateEventTypes();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// create tables
		for (String table : TABLES) {
			db.execSQL(table);
		}
		
		// insert event types
        for (ContentValues values : EVENT_TYPES) {
            db.insert("event_type", null, values);
        }
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// do nothing... no need to upgrade
	}
	
	/**
	 * Populate the schemas for creating the different tables.
	 */
	private void populateTableSchemas() {
		TABLES[0] = "CREATE TABLE advert (advert_id INTEGER PRIMARY KEY, advert_name TEXT, advert_url TEXT )";
		TABLES[1] = "CREATE TABLE event (event_id INTEGER PRIMARY KEY, event_type_id INTEGER, advert_id INTEGER, " +
				    "timestamp TEXT, user_phone_id TEXT, user_location TEXT )";
		TABLES[2] = "CREATE TABLE event_type (event_type_id INTEGER PRIMARY KEY, event_type_desc TEXT )";
		TABLES[3] = "CREATE TABLE sms_event (event_id INTEGER PRIMARY KEY, receiver TEXT, message TEXT )";
		TABLES[4] = "CREATE TABLE call_event (event_id INTEGER PRIMARY KEY, receiver TEXT )";
		TABLES[5] = "CREATE TABLE map_event (event_id INTEGER PRIMARY KEY, address TEXT )";
	}

	/**
	 * Populate the six different tracking event types.
	 */
	private void populateEventTypes() {
		putEventType(0, "impression");
		putEventType(1, "click");
		putEventType(2, "clickThrough");
		putEventType(3, "sms");
		putEventType(4, "call");
		putEventType(5, "map");
	}

	/**
	 * Put an event type into the event type array.
	 */
	private void putEventType(int id, String desc) {
		ContentValues values = new ContentValues();
		values.put("event_type_id", id);
		values.put("event_type_desc", desc);
		EVENT_TYPES[id] = values;
	}
	
}
