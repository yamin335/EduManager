package onair.onems.customadapters;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import onair.onems.R;

public class CustomNoticeDialog extends Dialog implements View.OnClickListener {
    private JSONObject notice;

    public CustomNoticeDialog(Activity a, JSONObject notice) {
        super(a);
        this.notice = notice;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.notice_details_dialog);
        TextView noticeTitle = (TextView)findViewById(R.id.noticeTitle);
        TextView noticeDetails = (TextView)findViewById(R.id.noticeShow);
        try {
            noticeTitle.setText(notice.getString("NoticeHead"));
            noticeDetails.setText(notice.getString("NoticeBody"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Button ok = (Button) findViewById(R.id.btn_ok);
        ok.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
}

