package com.phonegap.demo;

import java.util.HashMap;

import android.content.Context;
import android.webkit.WebView;
import android.util.Log;

/*
 * This class is the interface to the Geolocation.  It's bound to the geo object.
 * 
 * This class only starts and stops various GeoListeners, which consist of a GPS and a Network Listener
 */

public class GeoBroker {
	private WebView mAppView;
	private Context mCtx;
	private HashMap<String, GeoListener> geoListeners;
	
	GeoBroker(Context ctx, WebView view)
	{
		mCtx = ctx;
		mAppView = view;
		geoListeners = new HashMap<String, GeoListener>();
	}
	
	public void getCurrentLocation()
	{
		GeoListener listener = new GeoListener("global", mCtx, 10000, mAppView);
	}
	
	public String start(int freq, String key)
	{
//		Log.d("GeoBroker start", "Making new GeoListener with freq " + freq + " and key " + key);
		GeoListener listener = new GeoListener(key, mCtx, freq, mAppView);
		geoListeners.put(key, listener);
		return key;
	}
	
	public void stop(String key)
	{
		GeoListener geo = geoListeners.remove(key);
		if (geo != null) {
			geo.stop();
		}
	}
}
