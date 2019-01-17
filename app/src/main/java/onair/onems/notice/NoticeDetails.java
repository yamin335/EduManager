package onair.onems.notice;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import onair.onems.R;
import onair.onems.mainactivities.CommonToolbarParentActivity;

public class NoticeDetails extends CommonToolbarParentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View childActivityLayout = Objects.requireNonNull(inflater).inflate(R.layout.notice_details, null);
        LinearLayout parentActivityLayout = findViewById(R.id.contentMain);
        parentActivityLayout.addView(childActivityLayout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        TextView title = findViewById(R.id.title);
        TextView detail = findViewById(R.id.detail);

        try {
            JSONObject notice = new JSONObject(getIntent().getStringExtra("notice"));
            title.setText(notice.getString("NoticeHead"));
            detail.setText(notice.getString("NoticeBody"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
