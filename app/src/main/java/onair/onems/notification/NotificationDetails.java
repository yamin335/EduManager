package onair.onems.notification;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import onair.onems.R;
import onair.onems.mainactivities.CommonToolbarParentActivity;

public class NotificationDetails extends CommonToolbarParentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View childActivityLayout = Objects.requireNonNull(inflater).inflate(R.layout.notification_details_screen, null);
        LinearLayout parentActivityLayout = findViewById(R.id.contentMain);
        parentActivityLayout.addView(childActivityLayout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        TextView notificationTitle = findViewById(R.id.title);
        TextView notificationDetails = findViewById(R.id.detail);

        try {
            JSONObject jsonObject = new JSONObject(getIntent().getStringExtra("notification"));
            notificationTitle.setText(jsonObject.getString("title"));
            notificationDetails.setText(jsonObject.getString("body"));
            int id = jsonObject.getInt("id");
            int unseen = 0;
            String string = getSharedPreferences("PUSH_NOTIFICATIONS", Context.MODE_PRIVATE)
                    .getString("notifications", "[]");
            JSONArray jsonArray = new JSONArray(string);
            for(int i = 0; i<jsonArray.length(); i++) {
                if (jsonArray.getJSONObject(i).getInt("id") == id) {
                    jsonArray.getJSONObject(i).put("seen", 1);
                }

                if (jsonArray.getJSONObject(i).getInt("seen") == 0) {
                    unseen++;
                }
            }
            getSharedPreferences("UNSEEN_NOTIFICATIONS", Context.MODE_PRIVATE)
                    .edit()
                    .putInt("unseen", unseen)
                    .apply();

            getSharedPreferences("PUSH_NOTIFICATIONS", Context.MODE_PRIVATE)
                    .edit()
                    .putString("notifications", jsonArray.toString())
                    .apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
