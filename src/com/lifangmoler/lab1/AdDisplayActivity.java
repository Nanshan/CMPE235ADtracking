package com.lifangmoler.lab1;

import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.lifangmoler.lab1.fragment.BannerFragment;
import com.lifangmoler.lab2.db.TrackingDatabaseUtil;

public class AdDisplayActivity extends FragmentActivity implements
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener {
    private final static int
            CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	
	private String name, img, share, video, address, phone, url;

    LocationClient mLocationClient;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ad_display);
		setupActionBar();
		
        mLocationClient = new LocationClient(this, this, this);
		
		// Get the message from the intent
		Intent intent = getIntent();
		name = intent.getStringExtra(BannerFragment.ARG_NAME);
		img = intent.getStringExtra(BannerFragment.ARG_IMAGE2);
		share = intent.getStringExtra(BannerFragment.ARG_SHARETEXT);
		address = intent.getStringExtra(BannerFragment.ARG_ADDRESS);
		video = intent.getStringExtra(BannerFragment.ARG_VIDEO);
		phone = intent.getStringExtra(BannerFragment.ARG_PHONE);
		url = intent.getStringExtra(BannerFragment.ARG_URL);
		
		//find the image for the view page
	    int resourceId = this.getResources().getIdentifier("com.lifangmoler.lab1:drawable/"+img, null, null);
	    ((ImageView) findViewById(R.id.ad_image_2)).setImageResource(resourceId);
	}

	/*
     * Called when the Activity becomes visible.
     */
    @Override
    protected void onStart() {
        super.onStart();
        // Connect the client.
        mLocationClient.connect();
    }
    
    /*
     * Called when the Activity is no longer visible.
     */
    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        mLocationClient.disconnect();
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
		
		TrackingDatabaseUtil.addCallEvent(getBaseContext(), name, phone, mLocationClient.getLastLocation()); // track the call

		startActivity(callIntent);
	}

	/**
	 * This method will try to send an SMS message using the android operating system.
	 */
	private void openSMS() {
	    String dest = ((EditText) findViewById(R.id.edit_destination)).getText().toString();
        SmsManager smsManager = SmsManager.getDefault();
        PendingIntent piSent = PendingIntent.getBroadcast(getBaseContext(), 0, new Intent("SMS_SENT"), 0);
        PendingIntent piDelivered = PendingIntent.getBroadcast(getBaseContext(), 0,new Intent("SMS_DELIVERED"), 0);
        smsManager.sendTextMessage(dest, null, share, piSent, piDelivered);
		TrackingDatabaseUtil.addSMSEvent(getBaseContext(), name, dest, share, mLocationClient.getLastLocation()); // track the sms
	}

	private void openMap() {
		// Map point based on address
		Uri location = Uri.parse("geo:0,0?q="+address); 

		Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);
		
		PackageManager packageManager = getPackageManager();
		List<ResolveInfo> activities = packageManager.queryIntentActivities(mapIntent, 0);
		boolean isIntentSafe = activities.size() > 0;
		
		if (isIntentSafe) {
			TrackingDatabaseUtil.addMapEvent(getBaseContext(), name, address, mLocationClient.getLastLocation()); // track the map
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
		TrackingDatabaseUtil.addClickThroughEvent(getBaseContext(), name, mLocationClient.getLastLocation()); // track the click

		Uri website = Uri.parse(url);
		Intent intent = new Intent(Intent.ACTION_VIEW, website);
		startActivity(intent);
	}

	// google play services stuff (for getting user location)
	/*
     * Handle results returned to the FragmentActivity
     * by Google Play services
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        // Decide what to do based on the original request code
        switch (requestCode) {
            case CONNECTION_FAILURE_RESOLUTION_REQUEST :
            /*
             * If the result code is Activity.RESULT_OK, try
             * to connect again
             */
                switch (resultCode) {
                    case Activity.RESULT_OK :
                    	
                    /*
                     * Try the request again
                     */
                    break;
                }
        }
     }

    /*
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    @Override
    public void onConnected(Bundle dataBundle) {
		// TODO Auto-generated method stub
    }

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		/*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        }
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
	}
}
