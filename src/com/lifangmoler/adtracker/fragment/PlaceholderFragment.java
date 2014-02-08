package com.lifangmoler.adtracker.fragment;

import com.lifangmoler.lab1.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Simple placeholder fragment for a simple image with no behavior
 * 
 * @author Leah
 *
 */
public class PlaceholderFragment extends Fragment {
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.placeholder_view, container, false);
    }
}
