package com.lifangmoler.adtracker.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.lifangmoler.adtracker.fragment.BannerFragment;
import com.lifangmoler.adtracker.tracking.MobileAdEventTracker;
import com.lifangmoler.adtracker.util.AdCollectionPagerAdapter;
import com.lifangmoler.adtracker.util.AdvertisementScroller;
import com.lifangmoler.lab1.R;

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
public class BaseAppActivity extends FragmentActivity {	

    AdCollectionPagerAdapter adCollectionPagerAdapter;
    ViewPager adViewPager;
    LocationManager locationManager;
    boolean initialImpressionShown = false;
    private Handler adScrollHandler;
    private AdvertisementScroller adScroller;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_base_app);
		
		locationManager = (LocationManager) getBaseContext().getSystemService(Context.LOCATION_SERVICE);

        initViewPager();
        
        showInitialImpression();
	}

    // ViewPager and its adapters use support library
    // fragments, so use getSupportFragmentManager.
	private void initViewPager() {
		Resources res = this.getResources();
		XmlResourceParser xrp = res.getXml(R.xml.ads);
		
		adCollectionPagerAdapter = new AdCollectionPagerAdapter(getBaseContext(), getSupportFragmentManager(), xrp);
        adViewPager = (ViewPager) findViewById(R.id.pager);
        adViewPager.setAdapter(adCollectionPagerAdapter);
        adViewPager.setOnPageChangeListener(
    		new ViewPager.SimpleOnPageChangeListener() {
	            @Override
	            public void onPageSelected(int position) {
	            	// track this as an impression
	        		new MobileAdEventTracker(getBaseContext(), locationManager.getLastKnownLocation("network")).trackImpressionEvent(adCollectionPagerAdapter.getItem(position).
	        				getArguments().getString(BannerFragment.ARG_NAME));
	            }
    		}
    	);
	}
	
	private void showInitialImpression() {
		if (!initialImpressionShown) {
	        // add the initial impression from the ad displaying on startup
	        new MobileAdEventTracker(getBaseContext(), locationManager.getLastKnownLocation("network")).trackImpressionEvent(
					adCollectionPagerAdapter.getItem(0).
					getArguments().getString(BannerFragment.ARG_NAME));
	        initialImpressionShown = true;
    	}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		// init the ad scroller
		adScrollHandler = new Handler();
        adScroller = new AdvertisementScroller(adScrollHandler, adViewPager);
        adScrollHandler.postDelayed(adScroller, 5000);
	}

	@Override
	protected void onStop() {
		adScrollHandler.removeCallbacks(adScroller);
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
		
		new MobileAdEventTracker(getBaseContext(), locationManager.getLastKnownLocation("network")).trackClickEvent(args.getString(BannerFragment.ARG_NAME)); // track the click
		
		startActivity(intent); // start the activity
	}
}

