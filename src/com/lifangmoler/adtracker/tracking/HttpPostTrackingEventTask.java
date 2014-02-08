package com.lifangmoler.adtracker.tracking;

import org.apache.http.client.methods.HttpPost;

import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;

/**
 * Task for asynchronous HTTP requests, since Android will not allow
 * synchronous requests on the UI thread.
 * 
 * @author Leah
 *
 */
class HttpPostTrackingEventTask extends AsyncTask<HttpPost, Void, Void> {
	protected Void doInBackground(HttpPost... posts) {
		AndroidHttpClient client = AndroidHttpClient.newInstance("remote_app");
		try {
			for (HttpPost post : posts) {
				client.execute(post);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		client.close();
		return null;
	}
}