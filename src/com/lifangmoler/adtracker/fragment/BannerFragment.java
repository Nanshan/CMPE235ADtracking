package com.lifangmoler.adtracker.fragment;

import com.lifangmoler.lab1.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Fragment to hold a banner ad image and its arguments and behavior.
 * 
 * @author Leah
 *
 */
public class BannerFragment extends Fragment {
	public static final String ARG_NAME = "name";
	public static final String ARG_IMAGE = "image";
	public static final String ARG_IMAGE2 = "image2";
	public static final String ARG_SHARETEXT = "shareText";
	public static final String ARG_ADDRESS = "address";
	public static final String ARG_VIDEO = "video";
	public static final String ARG_PHONE = "phone";
	public static final String ARG_URL = "url";

    @Override
    public View onCreateView(LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated properly.
        View rootView = inflater.inflate(
                R.layout.banner_view, container, false);
        Bundle args = getArguments();
        
        // get the int ID in R, corresponding to the name of the image we want
        int resourceId = this.getResources().getIdentifier("com.lifangmoler.lab1:drawable/"+args.getString(ARG_IMAGE), null, null);
        
        // set the image resource in the view equal to the image we want (android:src in XML)
        ((ImageView) rootView.findViewById(R.id.ad_image)).setImageResource(resourceId);
        return rootView;
    }

}
