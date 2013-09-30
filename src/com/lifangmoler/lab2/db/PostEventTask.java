package com.lifangmoler.lab2.db;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;

import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;

class PostEventTask extends AsyncTask<HttpPost, Void, Void> {
	protected Void doInBackground(HttpPost... posts) {
		HttpClient client = AndroidHttpClient.newInstance("remote_app");
		try {
			for (HttpPost post : posts) {
				client.execute(post);
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}