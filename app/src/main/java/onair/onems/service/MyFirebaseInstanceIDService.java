package onair.onems.service;

import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.messaging.FirebaseMessagingService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import onair.onems.R;
import onair.onems.Services.StaticHelperClass;
import onair.onems.app.Config;
import onair.onems.customised.CustomRequest;
import onair.onems.network.MySingleton;

public class MyFirebaseInstanceIDService extends FirebaseMessagingService {
    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();
    private String returnValue = "[]";
    private String LoggedUserID = "";
    private String uuid = "";
    private long ID = 0;
    private String previousToken = "";
    private JSONObject tokenJsonObject;

    @Override
    public void onNewToken(String token) {
        LoggedUserID = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("UserID", "0");
        uuid = getApplicationContext().getSharedPreferences("UNIQUE_ID", Context.MODE_PRIVATE)
                .getString("uuid", "");
        previousToken = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, Context.MODE_PRIVATE)
                .getString("regId", "");
//        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
//        FirebaseInstanceId.getInstance().getInstanceId();

        // Saving reg id to shared preferences
        storeRegIdInPref(token);

        Log.d("Token: ", token);

        if(!LoggedUserID.equals("0")){
            // sending reg id to your server
            sendRegistrationToServer(token);
        }

        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", token);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void sendRegistrationToServer(final String token) {
        if(StaticHelperClass.isNetworkAvailable(this)) {
            String url = getString(R.string.baseUrl)+"/api/onEms/getFcmTokenByUserID/"+LoggedUserID;
            //Preparing Medium data from server
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            returnValue = response;
                            if (returnValue.equals("[]")) {
                                ID = 0;
                            } else {
                                try {
                                    JSONArray jsonArray = new JSONArray(returnValue);
                                    for (int i = 0; i<jsonArray.length(); i++) {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        if (jsonObject.getString("UserID").equals(LoggedUserID)
                                                &&jsonObject.getString("Browser").equals("android")
                                                &&jsonObject.getString("Token").equals(previousToken)
                                                &&jsonObject.getString("DeviceID").equals(uuid)) {
                                            ID = jsonObject.getLong("ID");
                                        }
                                    }
                                    tokenJsonObject = new JSONObject();
                                    tokenJsonObject.put("ID", ID);
                                    tokenJsonObject.put("UserID", LoggedUserID);
                                    tokenJsonObject.put("Browser", "android");
                                    tokenJsonObject.put("Token", token);
                                    tokenJsonObject.put("DeviceID", uuid);
                                    // sending fcm token to server
                                    tokenPostRequest(tokenJsonObject);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            })
            {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String>  params = new HashMap<>();
                    params.put("Authorization", "Request_From_onEMS_Android_app");
                    return params;
                }
            };
            MySingleton.getInstance(this).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(getApplicationContext(),"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void tokenPostRequest(JSONObject postDataJsonObject) {
        String url = getString(R.string.baseUrl)+"/api/onEms/setFcmToken";
        CustomRequest customRequest = new CustomRequest (Request.Method.POST, url, postDataJsonObject,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Toast.makeText(getApplicationContext(),"Successfully posted token",Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"ERROR posting token",Toast.LENGTH_LONG).show();
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<>();
                params.put("Authorization", "Request_From_onEMS_Android_app");
                return params;
            }
        };
        MySingleton.getInstance(this).addToRequestQueue(customRequest);
    }

    private void storeRegIdInPref(String token) {
        getApplicationContext().getSharedPreferences(Config.SHARED_PREF, Context.MODE_PRIVATE)
        .edit()
        .putString("regId", token)
        .commit();
    }
}