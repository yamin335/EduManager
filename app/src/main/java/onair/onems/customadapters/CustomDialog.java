package onair.onems.customadapters;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import onair.onems.R;

/**
 * Created by onAir on 13-Feb-18.
 */

public class CustomDialog extends Dialog implements View.OnClickListener {
    public Activity c;
    public Dialog d;
    public Button ok;

    public CustomDialog(Activity a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.subject_details_result_dialog);
        ok = (Button) findViewById(R.id.btn_ok);
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
