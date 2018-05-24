package onair.onems.login;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

public class ForgotPassInputDialog extends Dialog implements View.OnClickListener {
    private Activity parentActivity;
    private Context context;
    private EditText input;
    private ProgressDialog dialog;

    ForgotPassInputDialog(Activity activity, Context context) {
        super(activity);
        this.parentActivity = activity;
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.forgot_pass_input_email);
        input = findViewById(R.id.input);
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
                verifyInput(input.getText().toString());
                break;
            default:
                break;
        }
    }

    private void verifyInput(String input) {
        if(input.equals("")) {
            Toast.makeText(context,"Please input email, phone or username !!!",Toast.LENGTH_LONG).show();
        } else {
            if(StaticHelperClass.isNetworkAvailable(context)) {
                dialog = new ProgressDialog(context);
                dialog.setTitle("Verifying...");
                dialog.setMessage("Please Wait...");
                dialog.setCancelable(false);
                dialog.setIcon(R.drawable.onair);
                dialog.show();

                String verifyUrl = context.getString(R.string.baseUrl)+"/api/onEms/getUserEmailPhoneIfExist/"
                        +input;

                StringRequest request = new StringRequest(Request.Method.GET, verifyUrl,
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
    }

    private void parseReturnData(String string) {
        try {
            JSONObject returnValue = new JSONArray(string).getJSONObject(0);
            int result = returnValue.getInt("result");
            if(result == 1) {
                dialog.dismiss();
                dismiss();
                ForgotPassSelectDialog forgotPassSelectDialog = new ForgotPassSelectDialog(parentActivity, context, returnValue.toString());
                forgotPassSelectDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                forgotPassSelectDialog.setCancelable(false);
                forgotPassSelectDialog.show();
            } else {
                dialog.dismiss();
                dismiss();
                Toast.makeText(context,"please input valid email, phone or username !!!", Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            dialog.dismiss();
            dismiss();
            Toast.makeText(context,"Error in JsonParsing !!!", Toast.LENGTH_LONG).show();
        }
    }
}
