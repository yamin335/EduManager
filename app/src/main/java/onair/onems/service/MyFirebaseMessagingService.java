package onair.onems.service;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import onair.onems.app.Config;
import onair.onems.notification.NotificationDetails;
import onair.onems.utils.NotificationUtils;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    private NotificationUtils notificationUtils;
    private int notificationNo = 0;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
//        String s = remoteMessage.toString();
//        Log.e(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage == null)
            return;

        notificationNo = new Random().nextInt(900000)+100000;

        // Check if message contains a notification payload.
//        if (remoteMessage.getNotification() != null) {
//            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
//            handleNotification(remoteMessage.getNotification().getBody());
//        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
//            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
            try {
                JSONObject notification = new JSONObject(remoteMessage.getData().toString());
                handleDataMessage(notification, remoteMessage.getSentTime());
//                handleNotification(notification.toString());
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    private void handleNotification(String notification) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("notification", notification);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();
        }else{
            // If the app is in background, firebase itself handles the notification
        }
    }

    private void handleDataMessage(JSONObject jsonObject, long timestamp) {
        try {
            JSONObject notification = jsonObject.getJSONObject("notification");
            notification.put("seen", 0);
            notification.put("id", notificationNo);
            String string = getSharedPreferences("PUSH_NOTIFICATIONS", Context.MODE_PRIVATE)
                    .getString("notifications", "[]");
            JSONArray jsonArray = new JSONArray(string);
            jsonArray.put(notification);
            getSharedPreferences("PUSH_NOTIFICATIONS", Context.MODE_PRIVATE)
                    .edit()
                    .putString("notifications", jsonArray.toString())
                    .apply();
            int unseen = 0;
            for(int i = 0; i<jsonArray.length(); i++) {
                if (jsonArray.getJSONObject(i).getInt("seen") == 0) {
                    unseen++;
                }
            }
            getSharedPreferences("UNSEEN_NOTIFICATIONS", Context.MODE_PRIVATE)
                    .edit()
                    .putInt("unseen", unseen)
                    .apply();
            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                pushNotification.putExtra("notification", notification.toString());
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();
            } else {
                // app is in background, show the notification in notification tray
                Intent resultIntent = new Intent(getApplicationContext(), NotificationDetails.class);
                resultIntent.putExtra("notification", notification.toString());

                String imageUrl = "";
                // check for image attachment
                if (TextUtils.isEmpty(imageUrl)) {
                    showNotificationMessage(getApplicationContext(), notification.getString("title"), notification.getString("body"), timestamp, resultIntent);
                } else {
                    // image is present, show notification with image
                    showNotificationMessageWithBigImage(getApplicationContext(),  notification.getString("title"), notification.getString("body"), timestamp, resultIntent, imageUrl);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, long timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, long timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }
}