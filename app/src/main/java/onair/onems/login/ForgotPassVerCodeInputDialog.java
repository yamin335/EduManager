package onair.onems.login;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import onair.onems.R;
import onair.onems.Services.StaticHelperClass;
import onair.onems.network.MySingleton;

public class ForgotPassVerCodeInputDialog extends Dialog implements View.OnClickListener {
    private Activity parentActivity;
    private Context context;
    private ProgressDialog dialog;
    private String returnValue, selectedWay, sentVerificationCode;
    private EditText input;

    ForgotPassVerCodeInputDialog(Activity activity, Context context, String returnValue, String selectedWay, String sentVerificationCode) {
        super(activity);
        this.parentActivity = activity;
        this.context = context;
        this.returnValue = returnValue;
        this.selectedWay = selectedWay;
        this.sentVerificationCode = sentVerificationCode;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.forgot_pass_verification);
        input = findViewById(R.id.input);
        TextView resendTextView = findViewById(R.id.resend);
        Button next = findViewById(R.id.next);
        Button cross = findViewById(R.id.cross);
        resendTextView.setOnClickListener(this);
        next.setOnClickListener(this);
        cross.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cross:
                dismiss();
                break;
            case R.id.next:
                verifyCode();
                break;
            case R.id.resend:
                resendVerifyCode();
            default:
                break;
        }
    }

    private void verifyCode() {
        if(sentVerificationCode.equals(input.getText().toString())) {
            dismiss();
            ForgotPassChangePassDialog forgotPassChangePassDialog = new ForgotPassChangePassDialog(parentActivity, context, returnValue);
            forgotPassChangePassDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            forgotPassChangePassDialog.setCancelable(false);
            forgotPassChangePassDialog.show();
        } else {
            Toast.makeText(context,"Invalid verification code !!!",Toast.LENGTH_LONG).show();
        }
    }

    private void resendVerifyCode() {
        if(StaticHelperClass.isNetworkAvailable(context)) {
            int verificationCode = new Random().nextInt(900000)+100000;
            dialog = new ProgressDialog(context);
            dialog.setTitle("Sending verify code...");
            dialog.setMessage("Please Wait...");
            dialog.setCancelable(false);
            dialog.setIcon(R.drawable.onair);
            dialog.show();

            String verificationUrl = context.getString(R.string.baseUrl)+"/api/onEms/sendCodeThrouMail/"
                    +selectedWay+"/"+verificationCode;

            StringRequest request = new StringRequest(Request.Method.GET, verificationUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            parseReturnData(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dialog.dismiss();
                    dismiss();
                    Toast.makeText(context,"SERVER Error !!!",Toast.LENGTH_LONG).show();
                }
            })
            {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String>  params = new HashMap<>();
                    params.put("Authorization", "Request_From_onEMS_Android_app");
                    return params;
                }
            };
            MySingleton.getInstance(context).addToRequestQueue(request);
        } else {
            Toast.makeText(context,"Please check your INTERNET connection !!!",Toast.LENGTH_LONG).show();
        }
    }

    private void parseReturnData(String string) {
        try {
            JSONObject jsonObject = new JSONObject(string);
            String RandomNo = jsonObject.getString("RandomNo");
            String status = jsonObject.getString("Message");
            if(RandomNo.length()>3&&status.equalsIgnoreCase("sent")) {
                sentVerificationCode = RandomNo;
                dialog.dismiss();
                Toast.makeText(context,"Verification code sent !!!",Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            dialog.dismiss();
            dismiss();
            Toast.makeText(context,"Error in json parsing !!!",Toast.LENGTH_LONG).show();
        }
    }
}
