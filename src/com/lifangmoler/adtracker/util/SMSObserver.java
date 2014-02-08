package com.lifangmoler.adtracker.util;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Handler;

import com.lifangmoler.adtracker.tracking.MobileAdEventTracker;

public class SMSObserver extends ContentObserver {
	
	private Context context;
	private LocationManager locationManager;
	private String adName;
	Long lastId = -1L;
	
	private static final Uri smsUri = Uri.parse("content://sms");

	public SMSObserver(Context context, LocationManager locationManager, String adName) {
		super(new Handler());

		this.context = context;
		this.locationManager = locationManager;
		this.adName = adName;
	}
	
	@Override
	public void onChange(boolean selfChange, Uri uri) {
		onChange(selfChange);
	}

    @Override
    public void onChange(boolean selfChange) {
    	super.onChange(selfChange);
    	String[] smsProjection = new String[] {"_id", "address", "date", "type", "body"};
        Cursor cur = context.getContentResolver().query(smsUri, smsProjection, "type = ?",new String[]{Integer.toString(2)}, null);
        cur.moveToNext();

        // guarantee only one event will be tracked per SMS sent
        if (lastId == cur.getLong(cur.getColumnIndex("_id"))) {
            return;
        }
        lastId = cur.getLong(cur.getColumnIndex("_id"));
        
        String address = cur.getString(cur.getColumnIndex("address"));
        String body = cur.getString(cur.getColumnIndex("body"));
        
        cur.close();
        
		new MobileAdEventTracker(context, locationManager.getLastKnownLocation("network")).trackSMSEvent(adName, address, body);
    }
}
