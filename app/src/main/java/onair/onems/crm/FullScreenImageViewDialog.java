package onair.onems.crm;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import onair.onems.R;

public class FullScreenImageViewDialog extends Dialog implements View.OnClickListener {
    private Bitmap bitmap;
    private Activity activity;
    private Context context;

    FullScreenImageViewDialog(Context context, Activity activity, Bitmap bitmap) {
        super(activity, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        this.activity = activity;
        this.bitmap = bitmap;
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.image_view);
        ImageView imageView = findViewById(R.id.image);
        imageView.setImageBitmap(bitmap);
        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image:
//                dismiss();
                break;
            case R.id.back:
                dismiss();
                break;
            default:
                break;
        }
//        dismiss();
    }
}

