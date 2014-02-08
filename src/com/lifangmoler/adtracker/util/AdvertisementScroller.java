package com.lifangmoler.adtracker.util;

import android.os.Handler;
import android.support.v4.view.ViewPager;

public class AdvertisementScroller implements Runnable {
	
	private Handler handler;
	private ViewPager adViewPager;
	
	public AdvertisementScroller(Handler handler, ViewPager adViewPager) {
		this.handler = handler;
		this.adViewPager = adViewPager;
	}
	
	@Override
	public void run() {
		// scroll the ad view pager
		int scrollTo = adViewPager.getCurrentItem() + 1;
		if (scrollTo == adViewPager.getChildCount()) {
			scrollTo = 0;
		}
        adViewPager.setCurrentItem(scrollTo, true);
        
		// re-post task
	    handler.postDelayed(this, 5000);
	}

}
