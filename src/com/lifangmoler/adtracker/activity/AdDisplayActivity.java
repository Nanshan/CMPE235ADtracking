package com.lifangmoler.adtracker.activity;

import java.util.List;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.lifangmoler.adtracker.fragment.BannerFragment;
import com.lifangmoler.adtracker.tracking.MobileAdEventTracker;
import com.lifangmoler.adtracker.util.SMSObserver;
import com.lifangmoler.lab1.R;

/**
 * Activity for the "Ad Display"
 * This is the detail info page for the advertisement
 * which allows click through, SMS, video, map, and phone call actions
 * using an Android action bar.
 * 
 * @author Leah
 *
 */
public class AdDisplayActivity extends FragmentActivity {
	
	private String name, img, share, video, address, phone, url;

    LocationManager locationManager;

	private SMSObserver myObserver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ad_display);
		setupActionBar();
		        
		// Get the message from the intent
		Intent intent = getIntent();
		name = intent.getStringExtra(BannerFragment.ARG_NAME);
		img = intent.getStringExtra(BannerFragment.ARG_IMAGE2);
		share = intent.getStringExtra(BannerFragment.ARG_SHARETEXT);
		address = intent.getStringExtra(BannerFragment.ARG_ADDRESS);
		video = intent.getStringExtra(BannerFragment.ARG_VIDEO);
		phone = intent.getStringExtra(BannerFragment.ARG_PHONE);
		url = intent.getStringExtra(BannerFragment.ARG_URL);
		
		// set up the location and SMS observers
		locationManager = (LocationManager) getBaseContext().getSystemService(Context.LOCATION_SERVICE);
		myObserver = new SMSObserver(getBaseContext(), locationManager, name);
        getBaseContext().getContentResolver().registerContentObserver(Uri.parse("content://sms"), true, myObserver);
		
		//find the image for the view page
	    int resourceId = this.getResources().getIdentifier("com.lifangmoler.lab1:drawable/"+img, null, null);
	    ((ImageView) findViewById(R.id.ad_image_2)).setImageResource(resourceId);
	}
	
	@Override
	protected void onDestroy() {
		System.err.println("UNREGISTERING");
		// unregister the content observer for SMSs
        getBaseContext().getContentResolver().unregisterContentObserver(myObserver);
		super.onStop();
	}
	
	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.display_message, menu);
	    return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				// This ID represents the Home or Up button. In the case of this
				// activity, the Up button is shown. Use NavUtils to allow users
				// to navigate up one level in the application structure. For
				// more details, see the Navigation pattern on Android Design:
				//
				// http://developer.android.com/design/patterns/navigation.html#up-vs-back
				//
				NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.action_sms:
				openSMS();//send a message
	            return true;
			case R.id.action_map:
		        openMap();//view the map
		        return true;
			case R.id.action_call:
	            openCall();//make a phone call
	            return true;
			case R.id.action_video:
				openVideo();//watch a vedio
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
		}
	}

	private void openVideo() {
		Intent videoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + video));
		
		PackageManager packageManager = getPackageManager();
		List<ResolveInfo> activities = packageManager.queryIntentActivities(videoIntent, 0);
		boolean isIntentSafe = activities.size() > 0;
		
		if (isIntentSafe) {
			startActivity(videoIntent);
		}
		else {
			Context context = getApplicationContext();
			CharSequence text = "Video App is not available on your device";
			int duration = Toast.LENGTH_SHORT;

			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
		}
	}

	private void openCall() {
		Uri number = Uri.parse("tel:"+phone);
		Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
		startActivity(callIntent);
		
		new MobileAdEventTracker(getBaseContext(), locationManager.getLastKnownLocation("network")).trackCallEvent(name, phone); // track the call
	}

	/**
	 * This method will try to send an SMS message using the android operating system.
	 */
	private void openSMS() { 
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
		sendIntent.putExtra("sms_body", share); 
		sendIntent.setType("vnd.android-dir/mms-sms");
		startActivity(sendIntent);
	}

	private void openMap() {
		// Map point based on address
		Uri location = Uri.parse("geo:0,0?q="+address); 

		Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);
		
		PackageManager packageManager = getPackageManager();
		List<ResolveInfo> activities = packageManager.queryIntentActivities(mapIntent, 0);
		boolean isIntentSafe = activities.size() > 0;
		
		if (isIntentSafe) {
			new MobileAdEventTracker(getBaseContext(), locationManager.getLastKnownLocation("network")).trackMapEvent(name, address); // track the map
			startActivity(mapIntent);
		}
		else {
			// let the user know something happened and why
			Context context = getApplicationContext();
			CharSequence text = "Maps App is not available on your device";
			int duration = Toast.LENGTH_SHORT;

			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
		}
	}

	public void openURL(View view) {
		Uri website = Uri.parse(url);
		Intent intent = new Intent(Intent.ACTION_VIEW, website);
		startActivity(intent);
		
		new MobileAdEventTracker(getBaseContext(), locationManager.getLastKnownLocation("network")).trackClickThroughEvent(name); 
	}
}
