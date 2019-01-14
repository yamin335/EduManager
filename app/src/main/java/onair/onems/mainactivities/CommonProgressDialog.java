package onair.onems.mainactivities;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Window;

import onair.onems.R;

public class CommonProgressDialog extends Dialog {
    public CommonProgressDialog(Activity activity) {
        super(activity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.progress_dialog);
    }
}

