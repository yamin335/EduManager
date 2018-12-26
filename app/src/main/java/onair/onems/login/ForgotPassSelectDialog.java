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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import onair.onems.R;
import onair.onems.Services.StaticHelperClass;
import onair.onems.network.MySingleton;

public class ForgotPassSelectDialog extends Dialog implements View.OnClickListener {
    private Activity parentActivity;
    private Context context;
    private ProgressDialog tokenDialog, mailDialog, smsDialog;
    private String returnValue, selectedWay, smail, sphone;
    private boolean isPhoneOk = false, isMailOk = false;
    private long InstituteID;
    private boolean isPhone = false, isMail = false;

    ForgotPassSelectDialog(Activity activity, Context context, String returnValue) {
        super(activity);
        this.parentActivity = activity;
        this.context = context;
        this.returnValue = returnValue;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.forgot_pass_select_dialog);
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        RadioButton mail = findViewById(R.id.mail);
        RadioButton phone = findViewById(R.id.phone);
        try {
            JSONObject jsonObject = new JSONObject(returnValue);
            smail = jsonObject.getString("LoginEmail");
            sphone = jsonObject.getString("LoginPhone");
            InstituteID = jsonObject.getLong("InstituteID");
            if(Patterns.EMAIL_ADDRESS.matcher(smail).matches()) {
                int a = smail.indexOf("@");
                int b = smail.lastIndexOf(".");
                String c = smail.substring(a+1, b);
                String radio = smail.replace(c, "***");
                String radioMail;
                if(a<2) {
                    String d = smail.substring(0, 0);
                    radioMail = radio.replace(d, "*****");
                } else if(a<3) {
                    String e = smail.substring(1, 1);
                    radioMail = radio.replace(e, "*****");
                } else if(a<4) {
                    String f = smail.substring(1, 2);
                    radioMail = radio.replace(f, "*****");
                } else {
                    String g = smail.substring(2, a);
                    radioMail = radio.replace(g, "*****");
                }
                mail.setText(radioMail);
                isMailOk = true;
            }

            if(sphone.length()>10) {
                String s = sphone.substring(3, 9);
                String radioPhone = sphone.replace(s, "******");
                phone.setText(radioPhone);
                isPhoneOk = true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.mail:
                        selectedWay = smail;
                        isMail = true;
                        isPhone = false;
                        break;
                    case R.id.phone:
                        selectedWay = sphone;
                        isMail = false;
                        isPhone = true;
                        break;
                    case R.id.dont:
                        selectedWay = "don't";
                        break;
                }
            }
        });
        Button next = findViewById(R.id.next);
        Button cross = findViewById(R.id.cross);
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
                sendVerifyCode();
                break;
            default:
                break;
        }
    }

    private void sendVerifyCode() {
        if(isMailOk && isMail) {
            sendVerifyCodeViaMail();
        } else if(isPhoneOk && isPhone) {
            getInstituteSmsToken();
        } else if(selectedWay.equals("don't")) {
            Toast.makeText(context,"Please try a valid authentication !!!",Toast.LENGTH_LONG).show();
            dismiss();
        }
    }

    private void sendVerifyCodeViaMail(){
        if(StaticHelperClass.isNetworkAvailable(context)) {
            int verificationCode = new Random().nextInt(900000)+100000;
            mailDialog = new ProgressDialog(context);
            mailDialog.setTitle("Sending verification code...");
            mailDialog.setMessage("Please Wait...");
            mailDialog.setCancelable(false);
            mailDialog.setIcon(R.drawable.onair);
            mailDialog.show();

            String verificationUrl = context.getString(R.string.baseUrl)+"/api/onEms/sendCodeThrouMail/"
                    +selectedWay+"/"+verificationCode;

            StringRequest request = new StringRequest(Request.Method.GET, verificationUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            parseMailReturnData(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mailDialog.dismiss();
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

    private void parseMailReturnData(String string) {
        try {
            JSONObject jsonObject = new JSONObject(string);
            String RandomNo = jsonObject.getString("RandomNo");
            String status = jsonObject.getString("Message");
            if(RandomNo.length()>3&&status.equalsIgnoreCase("sent")) {
                mailDialog.dismiss();
                ForgotPassVerCodeInputDialog forgotPassVerCodeInputDialog = new ForgotPassVerCodeInputDialog(parentActivity, context, returnValue, selectedWay, RandomNo);
                forgotPassVerCodeInputDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                forgotPassVerCodeInputDialog.setCancelable(false);
                forgotPassVerCodeInputDialog.show();
                dismiss();
            } else {
                mailDialog.dismiss();
                dismiss();
                Toast.makeText(context,"Error sending verification code !!!",Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            mailDialog.dismiss();
            dismiss();
            Toast.makeText(context,"Error in json parsing !!!",Toast.LENGTH_LONG).show();
        }
    }

    private void getInstituteSmsToken(){
        if(StaticHelperClass.isNetworkAvailable(context)) {
            tokenDialog = new ProgressDialog(context);
            tokenDialog.setTitle("Getting sms token...");
            tokenDialog.setMessage("Please Wait...");
            tokenDialog.setCancelable(false);
            tokenDialog.setIcon(R.drawable.onair);
            tokenDialog.show();

            String smsTokenUrl = context.getString(R.string.baseUrl)+"/api/onEms/getInstituteAvailableSMS/"
                    +InstituteID;

            StringRequest request = new StringRequest(Request.Method.GET, smsTokenUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            parseTokenData(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    tokenDialog.dismiss();
                    dismiss();
                    Toast.makeText(context,"Error getting sms token !!!",Toast.LENGTH_LONG).show();
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

    private void parseTokenData(String token) {
        try {
            JSONArray jsonArray = new JSONArray(token);
            token = jsonArray.getJSONObject(0).getString("InsToken");
            if (!token.equalsIgnoreCase("null")) {
                sendVerifyCodeViaSMS(token);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        tokenDialog.dismiss();
    }

    private void sendVerifyCodeViaSMS(String token){
        if(StaticHelperClass.isNetworkAvailable(context)) {
            int verificationCode = new Random().nextInt(900000)+100000;
            smsDialog = new ProgressDialog(context);
            smsDialog.setTitle("Sending verification code...");
            smsDialog.setMessage("Please Wait...");
            smsDialog.setCancelable(false);
            smsDialog.setIcon(R.drawable.onair);
            smsDialog.show();

            String verificationUrl = "http://sms.greenweb.com.bd/api.php?token="
                    +token+"&to="+selectedWay+"&message="+verificationCode;

            StringRequest request = new StringRequest(Request.Method.GET, verificationUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            parseSmsReturnData(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    smsDialog.dismiss();
                    dismiss();
                    Toast.makeText(context,"Error sending verification code!!!",Toast.LENGTH_LONG).show();
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

    private void parseSmsReturnData(String string) {
        String returnMessage = string;
        smsDialog.dismiss();
    }

}
