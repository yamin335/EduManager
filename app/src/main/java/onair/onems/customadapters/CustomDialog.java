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

public class CustomDialog extends Dialog implements View.OnClickListener {
    private JSONObject result;

    public CustomDialog(Activity a, JSONObject result) {
        super(a);
        this.result = result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.result_details_dialog);
        TextView mcqMark = (TextView)findViewById(R.id.mcq);
        TextView writtenMark = (TextView)findViewById(R.id.written);
        TextView attendanceMark = (TextView)findViewById(R.id.attendance);
        TextView practicalMark = (TextView)findViewById(R.id.practical);
        TextView totalMark = (TextView)findViewById(R.id.total);
        try {
            mcqMark.setText(result.getString("MCQ"));
            writtenMark.setText(result.getString("Written"));
            attendanceMark.setText(result.getString("Attendance"));
            practicalMark.setText(result.getString("Precticle"));
            totalMark.setText(result.getString("Total"));
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
