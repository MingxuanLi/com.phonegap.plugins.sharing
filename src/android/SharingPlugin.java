package com.phonegap.plugins.sharing;

import android.util.Log;
import android.content.Intent;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.apache.cordova.*;


public class SharingPlugin extends CordovaPlugin{

    public static final String TAG = "SharingPlugin";
    public static final String GET_CONTENT = "get_content";

    private static CordovaWebView gWebView;
    private static boolean gForeground = false;
    private static JSONObject objCache = null;

    public SharingPlugin(){

    }

    public Context getApplicationContext(){
        return this.cordova.getActivity().getApplicationContext();
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        boolean result = false;

        if(GET_CONTENT.equals(action)){
            gWebView = this.webView;
            result = true;
            callbackContext.success();

            if ( objCache != null) {
                Log.v(TAG, "sending cached object");
                sendJavascript(objCache);
            	objCache = null;
            }
        } else {
            result = false;
            Log.e(TAG, "Invalid action: " + action);
            callbackContext.error("Invalid action : " + action);
        }

        return result;
    }

    public static void sendJavascript(JSONObject _json){
        Log.v(TAG, "send Javascript: " + _json.toString());

        String message = "javascript: getSharingContentSuccess(" + _json.toString() + ")";

        if(gWebView != null){
            gWebView.sendJavascript(message);
        }else{
            // If coldstart, cache it
            objCache = _json;
        }
    }

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        gForeground = true;
    }

    @Override
    public void onPause(boolean multitasking){
        super.onPause(multitasking);
        gForeground = false;
    }

    @Override
    public void onResume(boolean multitasking){
        super.onResume(multitasking);
        gForeground = true;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        gForeground = false;
        gWebView = null;
    }

    public static boolean isInForeground()
    {
        return gForeground;
    }

    public static boolean isActive()
    {
        return gWebView != null;
    }
}