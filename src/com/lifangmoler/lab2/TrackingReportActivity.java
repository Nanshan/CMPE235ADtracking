package com.lifangmoler.lab2;

import java.util.ArrayList;

import android.app.ListActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.lifangmoler.lab1.R;
import com.lifangmoler.lab2.db.LocalTrackingOpenHelper;

public class TrackingReportActivity extends ListActivity implements OnItemSelectedListener {
	ArrayAdapter<String> listAdapter; // can handle a list or an array as input
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		listAdapter = new ArrayAdapter<String>(this, 
		        android.R.layout.simple_list_item_1, populateForCounts());
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_tracking_report);
		
		createReportSelector();
		
		getListView().setAdapter(listAdapter);
	}

	private void createReportSelector() {
		Spinner spinner = (Spinner) findViewById(R.id.reports_spinner);
		spinner.setOnItemSelectedListener(this);
		ArrayAdapter<CharSequence> spinAdapter = ArrayAdapter.createFromResource(this,
		        R.array.reports_array, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(spinAdapter);
	}
	
	public void onItemSelected(AdapterView<?> parent, View view, 
            int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
		String item = parent.getItemAtPosition(pos).toString();
		ArrayList<String> items;
		if (item.equals("Users")) {
			items = populateForUsers();
		}
		else if (item.equals("SMS")) {
			items = populateForSMS();
		}
		else if (item.equals("Call")) {
			items = populateForCall();
		}
		else if (item.equals("Map")) {
			items = populateForMap();
		}
		else { // default
			items = populateForCounts();
		}
		listAdapter.clear();
		listAdapter.addAll(items);
    }

    private ArrayList<String> populateForCounts() {
    	LocalTrackingOpenHelper mDbHelper = new LocalTrackingOpenHelper(getBaseContext());
		
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		Cursor c = db.rawQuery("SELECT a.advert_name AS adname, et.event_type_desc AS eventdesc, COUNT(*) AS eventcount " +
				"FROM advert AS a, event AS e, event_type AS et " +
				"WHERE a.advert_id = e.advert_id AND e.event_type_id = et.event_type_id GROUP BY a.advert_name, e.event_type_id", null);

		c.moveToFirst();
		ArrayList<String> items = new ArrayList<String>();
		items.add("Ad Name\t\tEvent Type\t\tEvent Count");
		while (!c.isAfterLast()) {
			if(c.getString(0).equals("Heat")){
			items.add(c.getString(0) + "\t\t\t\t\t\t\t" + c.getString(1) + "\t\t\t\t\t\t\t\t\t" + c.getLong(2));
			c.moveToNext();
		}else{
			items.add(c.getString(0) + "\t\t\t\t" + c.getString(1) + "\t\t\t\t\t\t\t\t\t" + c.getLong(2));
			c.moveToNext();
		}
		}
		c.close();
		
		return items;
	}
    
    private ArrayList<String> populateForUsers() {
    	LocalTrackingOpenHelper mDbHelper = new LocalTrackingOpenHelper(getBaseContext());
		
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		Cursor c = db.rawQuery("SELECT e.user_phone_id, e.user_location, e.timestamp, a.advert_name, et.event_type_desc FROM event AS e, advert AS a, event_type AS et WHERE e.advert_id = a.advert_id AND e.event_type_id = et.event_type_id", null);

		c.moveToFirst();
		ArrayList<String> items = new ArrayList<String>();
		//items.add("Device ID\tLocation\tTimestamp\tAd Name\tEvent Type");
		items.add("Ad Name\t\tEvent Type\t\t\t\tDevice ID\t\tLocation\t\tTimestamp");
		
		while (!c.isAfterLast()) {
			items.add(c.getString(3) +"\t\t\t"+c.getString(4)+"\t\t\t\t\t\t"+c.getString(0) + "\t\t\t" + c.getString(1) + "\t\t\t" + c.getString(2) + "\t"  );
			c.moveToNext();
		}
		c.close();
		
		return items;
	}
    
    private ArrayList<String> populateForSMS() {
    	LocalTrackingOpenHelper mDbHelper = new LocalTrackingOpenHelper(getBaseContext());
		
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		Cursor c = db.rawQuery("SELECT a.advert_name, et.event_type_desc, se.receiver, se.message " +
				"FROM event AS e, event_type AS et, advert AS a, sms_event AS se " +
				"WHERE e.advert_id = a.advert_id AND e.event_type_id = et.event_type_id AND e.event_id = se.event_id", null);

		c.moveToFirst();
		ArrayList<String> items = new ArrayList<String>();
		items.add("Ad Name\t\tReceiver\t\tSMS Message");
		while (!c.isAfterLast()) {
			items.add(c.getString(0) + "\t\t\t" + c.getString(2)+"\t"+c.getString(3));
			c.moveToNext();
		}
		c.close();
		
		return items;
	}
    
    private ArrayList<String> populateForCall() {
    	LocalTrackingOpenHelper mDbHelper = new LocalTrackingOpenHelper(getBaseContext());
		
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		Cursor c = db.rawQuery("SELECT a.advert_name, et.event_type_desc, ce.receiver FROM event AS e, event_type AS et, advert AS a, call_event AS ce WHERE e.advert_id = a.advert_id AND e.event_type_id = et.event_type_id AND e.event_id = ce.event_id", null);

		c.moveToFirst();
		ArrayList<String> items = new ArrayList<String>();
		items.add("Ad Name\t\tCall Recipient");
		while (!c.isAfterLast()) {
			items.add(c.getString(0) + "\t\t\t" + c.getString(2));
			c.moveToNext();
		}
		c.close();
		
		return items;
	}
    
    private ArrayList<String> populateForMap() {
    	LocalTrackingOpenHelper mDbHelper = new LocalTrackingOpenHelper(getBaseContext());
		
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		Cursor c = db.rawQuery("SELECT a.advert_name, et.event_type_desc, me.address FROM event AS e, event_type AS et, advert AS a, map_event AS me WHERE e.advert_id = a.advert_id AND e.event_type_id = et.event_type_id AND e.event_id = me.event_id", null);

		c.moveToFirst();
		ArrayList<String> items = new ArrayList<String>();
		items.add("Ad Name\t\tMapped Address");
		while (!c.isAfterLast()) {
			items.add(c.getString(0) + "\t\t\t\t" + c.getString(2));
			c.moveToNext();
		}
		c.close();
		
		return items;
	}

	public void onNothingSelected(AdapterView<?> parent) {
        // Stay with what we currently have
    }
}
