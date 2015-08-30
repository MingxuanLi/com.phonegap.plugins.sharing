package com.phonegap.plugins.sharing;

import org.apache.cordova.*;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.widget.ImageView;
import android.widget.Toast;
import android.R;
import android.util.Log;
import java.lang.reflect.*;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

public class SharingContentHandlerActivity extends Activity {


    public static final String TAG = "SharingContentHandlerActivity";
    private ImageView imageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate");

        boolean isSharingPluginActive = SharingPlugin.isActive();

        // Get the intent that started this activity
        Intent intent = getIntent();
        // Get the action of the intent
        String action = intent.getAction();
        // Get the type of intent (Text or Image)
        String type = intent.getType();
        // When Intent's action is 'ACTION+SEND' and Tyoe is not null
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            // When tyoe is 'text/plain'
            if ("text/plain".equals(type)) {
                handleSendText(intent, isSharingPluginActive); // Handle text being sent
            } else if (type.startsWith("image/")) { // When type is 'image/*'
                handleSendImage(intent, isSharingPluginActive); // Handle single image being sent
            }
        }

        finish();

        forceMainActivityReload();

        //if(!isSharingPluginActive){
        //    forceMainActivityReload();
        //}
    }

    /**
     * Method to hanlde incoming text data
     * @param intent
     */
    private void handleSendText(Intent intent, boolean isSharingPluginActive) {
        // Get the text from intent
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);

        Log.v(SharingPlugin.TAG, sharedText);
        // When Text is not null
        if (sharedText != null) {

            // Show the text as Toast message
            //Toast.makeText(this, sharedText, Toast.LENGTH_LONG).show();

            try{
                JSONObject obj = new JSONObject();
                obj.put("coldstart", !isSharingPluginActive);
                obj.put("type", "text");
                obj.put("is_valid", true);
                obj.put("data", sharedText);

                SharingPlugin.sendJavascript(obj);
            } catch (JSONException e){
                Log.e(SharingPlugin.TAG, "JSON Exception: " + e.getMessage());
            }
        }
    }

    /**
     * Method to handle incoming Image
     * @param intent
     */
    private void handleSendImage(Intent intent, boolean isSharingPluginActive) {
        // Get the image URI from intent
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);

        Log.v(SharingPlugin.TAG, imageUri.toString());

        try{
            JSONObject obj = new JSONObject();

            obj.put("type", "image");
            obj.put("coldstart", !isSharingPluginActive);

            // When image URI is not null
            if (imageUri != null) {
                obj.put("is_valid", true);
                obj.put("data", imageUri.toString());

            } else{
                Log.e(SharingPlugin.TAG, "invalid image uri");

                obj.put("is_valid", false);
                obj.put("data", "");
            }

            SharingPlugin.sendJavascript(obj);
        } catch (JSONException e){
            Log.e(SharingPlugin.TAG, "JSON Exception: " + e.getMessage());
        }
    }

    private void forceMainActivityReload(){
        PackageManager pm = getPackageManager();
        Intent launchIntent = pm.getLaunchIntentForPackage(getApplicationContext().getPackageName());
        startActivity(launchIntent);
    }
}