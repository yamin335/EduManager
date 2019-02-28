package onair.onems.service;

import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import onair.onems.R;
import onair.onems.Services.RetrofitNetworkService;
import onair.onems.Services.StaticHelperClass;
import onair.onems.app.Config;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MyFirebaseInstanceIDService extends FirebaseMessagingService {
    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();
    private String returnValue = "[]";
    private String LoggedUserID = "";
    private String uuid = "";
    private long ID = 0;
    private String previousToken = "";
    private JSONObject tokenJsonObject;
    private CompositeDisposable finalDisposer = new CompositeDisposable();

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!finalDisposer.isDisposed())
            finalDisposer.dispose();
    }

    @Override
    public void onNewToken(String token) {
        // Saving reg id to shared preferences
        storeRegIdInPref(token);
        LoggedUserID = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("UserID", "0");
        uuid = getApplicationContext().getSharedPreferences("UNIQUE_ID", Context.MODE_PRIVATE)
                .getString("uuid", "");
        previousToken = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, Context.MODE_PRIVATE)
                .getString("regId", "");
//        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
//        FirebaseInstanceId.getInstance().getInstanceId();

        // Saving reg id to shared preferences
//        storeRegIdInPref(token);

//        Log.d("Token: ", token);

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
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(getString(R.string.baseUrl))
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();

            Observable<String> observable = retrofit
                    .create(RetrofitNetworkService.class)
                    .getFcmTokenByUserID(LoggedUserID)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io());

            finalDisposer.add( observable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io())
                    .subscribeWith(new DisposableObserver<String>() {
                        @Override
                        public void onNext(String response) {
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
                                    tokenPostRequest(tokenJsonObject.toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onComplete() {

                        }
                    }));
        } else {
            Toast.makeText(getApplicationContext(),"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void tokenPostRequest(String object) {
        if(StaticHelperClass.isNetworkAvailable(this)) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(getString(R.string.baseUrl))
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();

            JsonParser parser = new JsonParser();
            JsonObject postDataJsonObject = parser.parse(object).getAsJsonObject();

            Observable<String> observable = retrofit
                    .create(RetrofitNetworkService.class)
                    .setFcmToken(postDataJsonObject)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io());

            finalDisposer.add( observable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io())
                    .subscribeWith(new DisposableObserver<String>() {
                        @Override
                        public void onNext(String response) {
                            Toast.makeText(getApplicationContext(),"Successfully posted token",Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(getApplicationContext(),"ERROR posting token",Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    }));
        } else {
            Toast.makeText(getApplicationContext(),"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void storeRegIdInPref(String token) {
        getApplicationContext().getSharedPreferences(Config.SHARED_PREF, Context.MODE_PRIVATE)
        .edit()
        .putString("regId", token)
        .apply();
    }
}