package onair.onems.login;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
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

import onair.onems.R;
import onair.onems.Services.StaticHelperClass;
import onair.onems.network.MySingleton;

public class ForgotPassChangePassDialog extends Dialog implements View.OnClickListener {
    private Activity parentActivity;
    private Context context;
    private EditText newPassword, confirmPassword;
    private ProgressDialog dialog;
    private String returnValue, passwordChangeUrl;

    ForgotPassChangePassDialog(Activity activity, Context context, String returnValue) {
        super(activity);
        this.parentActivity = activity;
        this.context = context;
        this.returnValue = returnValue;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.forgot_pass_change_pass_dialog);
        Button cross = findViewById(R.id.cross);
        Button done = findViewById(R.id.done);
        newPassword = findViewById(R.id.newPassword);
        confirmPassword = findViewById(R.id.confirmPassword);
        cross.setOnClickListener(this);
        done.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cross:
                dismiss();
                break;
            case R.id.done:
                changePassword();
                break;
            default:
                break;
        }
    }

    private void changePassword() {
        String newP = newPassword.getText().toString();
        String confirmP = confirmPassword.getText().toString();

        if(newP.equals("")) {
            newPassword.requestFocus();
            Toast.makeText(context,"Please enter new password !!!",Toast.LENGTH_LONG).show();
        } else if(confirmP.equals("")) {
            confirmPassword.requestFocus();
            Toast.makeText(context,"Please enter confirm password !!!",Toast.LENGTH_LONG).show();
        } else if(newP.equals(confirmP)) {
            if(StaticHelperClass.isNetworkAvailable(context)) {
                dialog = new ProgressDialog(context);
                dialog.setTitle("Changing Password...");
                dialog.setMessage("Please Wait...");
                dialog.setCancelable(false);
                dialog.setIcon(R.drawable.onair);
                dialog.show();

                try {
                    JSONObject jsonObject = new JSONObject(returnValue);
                    passwordChangeUrl = context.getString(R.string.baseUrl)+"/api/onEms/resetPassword/"
                            +jsonObject.getString("UserID")+"/"+jsonObject.getString("LoginID")
                            +"/"+confirmP+"/"+jsonObject.getString("InstituteID");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                StringRequest request = new StringRequest(Request.Method.GET, passwordChangeUrl,
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
                        Toast.makeText(context,"Error changing password !!!",Toast.LENGTH_LONG).show();
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
        } else {
            newPassword.setText("");
            confirmPassword.setText("");
            newPassword.requestFocus();
            Toast.makeText(context,"Password does not match !!!",Toast.LENGTH_LONG).show();
        }
    }

    private void parseReturnData(String string) {
        try {
            int result = new JSONArray(string).getJSONObject(0).getInt("result");
            if(result == 1) {
                dialog.dismiss();
                dismiss();
                Toast.makeText(context,"Password changed successfully !!!",Toast.LENGTH_LONG).show();
            } else {
                dialog.dismiss();
                dismiss();
                Toast.makeText(context,"Error changing password !!!",Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            dialog.dismiss();
            dismiss();
            Toast.makeText(context,"Error in json parsing !!!",Toast.LENGTH_LONG).show();
        }
    }
}
