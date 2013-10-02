package com.lifangmoler.lab1;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.lifangmoler.lab1.fragment.BannerFragment;
import com.lifangmoler.lab2.db.MobileAdEventTracker;

/**
 * The activity for the "Base App".
 * 
 * This is the app that contains a banner ad which users can click on
 * that will take them to the Mobile Ad Displayer (the AdDisplayActivity)
 * It contains some placeholder picture at the bottom that would be
 * corresponding to whatever the base app is in a real mobile app.
 * It has two child fragments: BannerFragment and PlaceholderFragment.
 * 
 * @author Leah
 *
 */
public class BaseAppActivity extends FragmentActivity implements
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener {	
    private final static int
            CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    AdCollectionPagerAdapter adCollectionPagerAdapter;
    ViewPager adViewPager;
    LocationClient mLocationClient;
    boolean initialImpressionShown = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base_app);
		
        mLocationClient = new LocationClient(this, this, this);
		
		// Get the banner ads from the resource file
		Resources res = this.getResources();
		XmlResourceParser xrp = res.getXml(R.xml.ads);

        // ViewPager and its adapters use support library
        // fragments, so use getSupportFragmentManager.
        adCollectionPagerAdapter =
                new AdCollectionPagerAdapter(getBaseContext(), getSupportFragmentManager(), xrp);
        adViewPager = (ViewPager) findViewById(R.id.pager);
        adViewPager.setAdapter(adCollectionPagerAdapter);
        adViewPager.setOnPageChangeListener(
    		new ViewPager.SimpleOnPageChangeListener() {
	            @Override
	            public void onPageSelected(int position) {
	            	// track this as an impression
	        		new MobileAdEventTracker(getBaseContext(), mLocationClient.getLastLocation()).trackImpressionEvent(adCollectionPagerAdapter.getItem(position).
	        				getArguments().getString(BannerFragment.ARG_NAME));
	            }
    		}
    	);
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
	 * Start the mobile ad displayer.
	 * 
	 * @param view the current view (includes data of the currently displayed ad)
	 */
	public void openAdDisplay(View view) {
		Intent intent = new Intent(this, AdDisplayActivity.class);
		//get the information from each page
		Bundle args = adCollectionPagerAdapter.getItem(adViewPager.getCurrentItem()).getArguments();
		//Explicit intents explicitly defines the component which should be called 
		//by the Android system
		intent.putExtra(BannerFragment.ARG_NAME, args.getString(BannerFragment.ARG_NAME));
		intent.putExtra(BannerFragment.ARG_IMAGE2, args.getString(BannerFragment.ARG_IMAGE2));
		intent.putExtra(BannerFragment.ARG_SHARETEXT, args.getString(BannerFragment.ARG_SHARETEXT));
		intent.putExtra(BannerFragment.ARG_ADDRESS, args.getString(BannerFragment.ARG_ADDRESS));
		intent.putExtra(BannerFragment.ARG_VIDEO, args.getString(BannerFragment.ARG_VIDEO));
		intent.putExtra(BannerFragment.ARG_PHONE, args.getString(BannerFragment.ARG_PHONE));
		intent.putExtra(BannerFragment.ARG_URL, args.getString(BannerFragment.ARG_URL));
		
		new MobileAdEventTracker(getBaseContext(), mLocationClient.getLastLocation()).trackClickEvent(args.getString(BannerFragment.ARG_NAME)); // track the click
		
		startActivity(intent); // start the activity
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
    	if (!initialImpressionShown) {
	        // add the initial impression from the ad displaying on startup
	        new MobileAdEventTracker(getBaseContext(), mLocationClient.getLastLocation()).trackImpressionEvent(
					adCollectionPagerAdapter.getItem(0).
					getArguments().getString(BannerFragment.ARG_NAME));
	        initialImpressionShown = true;
    	}
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

