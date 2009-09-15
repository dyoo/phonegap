package com.phonegap.demo;
/* License (MIT)
 * Copyright (c) 2008 Nitobi
 * website: http://phonegap.com
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * Software), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import java.lang.reflect.Field;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.content.Intent;


import plt.playlist.PlaylistRecord;

public class DroidGap extends Activity {


    static final int PLAYLIST_PICKED = 8024;


	
    private static final String LOG_TAG = "DroidGap";
    private WebView appView;
    private String uri;

    private PhoneGap gap;
    private GeoBroker geo;
    private AccelListener accel;
    private ConsoleOutput console;
    private ArgTable arguments;
    private PlaylistPicker playlistPicker;
	
    /** Called when the activity is first created. */
    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE); 
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
			     WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN); 
        setContentView(R.layout.main);        
         
        appView = (WebView) findViewById(R.id.appView);
        
        /* This changes the setWebChromeClient to log alerts to LogCat!  Important for Javascript Debugging */
        
        appView.setWebChromeClient(new GapClient(this));
        appView.getSettings().setJavaScriptEnabled(true);
        appView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);        
        
        /* Bind the appView object to the gap class methods */
        bindBrowser(appView);
        
        /* Load a URI from the strings.xml file */
        Class<R.string> c = R.string.class;
        Field f;
        
        int i = 0;
        
        try {
	    f = c.getField("url");
	    i = f.getInt(f);
	    this.uri = this.getResources().getString(i);
        } catch (Exception e)
	    {
		this.uri = "http://www.phonegap.com";
	    }
        appView.loadUrl(this.uri);        
    }
	

    @Override
	public void onConfigurationChanged(Configuration newConfig) {
	//don't reload the current page when the orientation is changed
	super.onConfigurationChanged(newConfig);
    } 
    
    private void bindBrowser(WebView appView)
    {
    	// The PhoneGap class handles the Notification and Android Specific crap
	arguments = new ArgTable();
    	gap = new PhoneGap(this, appView, getAssets(), arguments);
    	geo = new GeoBroker(this, appView, arguments);
    	accel = new AccelListener(this, appView, arguments);
    	console = new ConsoleOutput(this, appView);
	playlistPicker = new PlaylistPicker();

    	// This creates the new javascript interfaces for PhoneGap
    	appView.addJavascriptInterface(gap, "Device");
    	appView.addJavascriptInterface(geo, "Geo");
    	appView.addJavascriptInterface(accel, "Accel");
	appView.addJavascriptInterface(console, "Console");
	appView.addJavascriptInterface(arguments, "Args");
	appView.addJavascriptInterface(playlistPicker, "PlaylistPicker");
    }


    public void onPause() {
	System.out.println("Being paused");
	super.onPause();
    }


    public void onResume() {
	System.out.println("Being resumed");
	super.onResume();
    }



    public void onStop() {
    	System.out.println("Being stopping");
    	gap.stop();
    	geo.stop();
    	accel.stop();
    	super.onStop();
    }


    public void onRestart() {
	System.out.println("Being restarted");
	gap.restart();
	geo.restart();
	accel.restart();
	super.onRestart();
    }




    public void onDestroy() {
	System.out.println("Being destroyed");
	appView.destroy();

	gap.destroy();
	geo.destroy();
	accel.destroy();
	super.onDestroy();
    }


        
    /**
     * Provides a hook for calling "alert" from javascript. Useful for
     * debugging your javascript.
     */
    final class GapClient extends WebChromeClient {
    	
    	Context mCtx;
    	GapClient(Context ctx)
    	{
	    mCtx = ctx;
    	}
    	
    	@Override
	    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            Log.d(LOG_TAG, message);
            // This shows the dialog box.  This can be commented out for dev
            AlertDialog.Builder alertBldr = new AlertDialog.Builder(mCtx);
            alertBldr.setMessage(message);
            alertBldr.setTitle("Alert");
            alertBldr.show();
            result.confirm();
            return true;
        }
    }




    public class PlaylistPicker {
	private PlaylistRecord record;
	public PlaylistPicker() {}

	public void requestPickPlaylist() {
	    bindPlaylistPicker();
	}
	
	public PlaylistRecord getPlaylistRecord() {
	    return this.record;
	}

	public void setPlaylistRecord(PlaylistRecord record) {
	    this.record = record;
	}

    }


    private void bindPlaylistPicker() {
	Intent intent = new Intent();
	intent.setClass(this, plt.playlist.PickPlaylist.class);
	this.startActivityForResult(intent, PLAYLIST_PICKED);
    }



    protected void onActivityResult(int requestCode, 
				    int resultCode,
				    Intent intent) {
	if (resultCode == RESULT_OK) {
	    switch (requestCode) {
	    case PLAYLIST_PICKED:
		System.out.println("I got a playlist selected.");
		plt.playlist.PlaylistRecord record = 
		    (plt.playlist.PlaylistRecord) intent.getSerializableExtra("value");
		playlistPicker.setPlaylistRecord(record);
		appView.loadUrl("javascript:navigator.dialogPickers.notifyPlaylistPicked()");
		break;
	    default:
	    };
	} else {
	}
    }
}
