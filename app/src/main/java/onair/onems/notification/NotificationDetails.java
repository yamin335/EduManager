package onair.onems.notification;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import onair.onems.R;
import onair.onems.mainactivities.CommonToolbarParentActivity;

public class NotificationDetails extends CommonToolbarParentActivity {

    private String from = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View childActivityLayout = inflater.inflate(R.layout.notification_details_screen, null);
        LinearLayout parentActivityLayout = findViewById(R.id.contentMain);
        parentActivityLayout.addView(childActivityLayout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        TextView notificationTitle = findViewById(R.id.title);
        TextView notificationDetails = findViewById(R.id.detail);

        try {
            JSONObject jsonObject = new JSONObject(getIntent().getStringExtra("notification"));
            notificationTitle.setText(jsonObject.getString("title"));
            notificationDetails.setText(jsonObject.getString("body"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

//    @Override
//    public void onBackPressed() {
//        if(from.equals("tray")) {
//            super.onBackPressed();
//            Intent intent = new Intent(getApplicationContext(), NotificationMainScreen.class);
//            startActivity(intent);
//            finish();
//            moveTaskToBack(true);
//        } else if(from.equals("activity")) {
//            super.onBackPressed();
//        }
//    }
}
