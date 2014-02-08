package com.lifangmoler.adtracker.util;

import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.lifangmoler.adtracker.fragment.BannerFragment;

/**
 * Adapter to populate view pager with different ad fragments and switch between them.
 * Refer to http://developer.android.com/reference/android/support/v4/app/FragmentPagerAdapter.html
 * 
 * @author Leah
 *
 */
public class AdCollectionPagerAdapter extends FragmentStatePagerAdapter {
	private List<Fragment> adFragments; // a list of fragments for the activity
	
	public AdCollectionPagerAdapter(Context c, FragmentManager fm, XmlResourceParser xrp) {
		super(fm);
		//get the ad data from adx.xml
		parseAdsFromXml(c, xrp);
	}

	@Override
	public Fragment getItem(int i) {
		return adFragments.get(i);// retrieve specifical fragment from the list
	}

	@Override
	public int getCount() {
		return adFragments.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return "OBJECT " + (position + 1);
	}

	private void parseAdsFromXml(Context c, XmlResourceParser xrp) {
		adFragments = new ArrayList<Fragment>();
		try {
			xrp.next();
			int eventType = xrp.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG) {
					if (xrp.getName().equals("ad")) {//find <ad> tag
						adFragments.add(parseAdIntoFragment(c, xrp));//add information under <ad> tag
					}
				}
				eventType = xrp.next();
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Fragment parseAdIntoFragment(Context c, XmlResourceParser xrp) throws Exception {
		Fragment ad = new BannerFragment();
		Bundle args = new Bundle();
		xrp.nextTag();
		args.putString(BannerFragment.ARG_NAME, xrp.nextText());//advertisement name
		xrp.nextTag();
		args.putString(BannerFragment.ARG_IMAGE, xrp.nextText());//image
		xrp.nextTag();			
		args.putString(BannerFragment.ARG_IMAGE2, xrp.nextText());
		xrp.nextTag();			
		args.putString(BannerFragment.ARG_SHARETEXT, xrp.nextText());//message
		xrp.nextTag();		
		args.putString(BannerFragment.ARG_ADDRESS, xrp.nextText());//address for map
		xrp.nextTag();	
		args.putString(BannerFragment.ARG_PHONE, xrp.nextText());//number to call
		xrp.nextTag();		
		args.putString(BannerFragment.ARG_VIDEO, xrp.nextText());//youtube
		xrp.nextTag();		
		args.putString(BannerFragment.ARG_URL, xrp.nextText());//website
		ad.setArguments(args);
		
		return ad;
	}
}