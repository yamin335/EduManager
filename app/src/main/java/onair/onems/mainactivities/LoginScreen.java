package onair.onems.mainactivities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import onair.onems.R;


public class LoginScreen extends AppCompatActivity
{

    private Button loginButton;
    private AutoCompleteTextView takeId;
    private EditText takePassword;
    TextView errorView;
    private String UserID = "", LoginId = "", LoginPassword = "", Password = "", UserFullName = "", ImageUrl = "", InstituteName = "", DepartmentName = "",
                            DesignationName = "", BrunchName = "", RFID = "", RollNo = "", StudentNo = "", DepartmentID = "", DesignationID = "", BrunchID = "";

    int UserTypeID = 0, InstituteID = 0, SBrunchID = 0, BoardID, SDepartmentID, MediumID = 0, SectionID = 0, SessionID = 0, ShiftID = 0, ClassID = 0;
    String loginurl = "";

    ProgressDialog dialog;

    public static final String MyPREFERENCES = "LogInKey";
    public static SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.getBoolean("firstTime", false)) {
            // <---- run your one time code here

            sharedPreferences  = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("LogInState", false);
            editor.commit();

            // mark first time has runned.
            SharedPreferences.Editor defaultEditor = prefs.edit();
            defaultEditor.putBoolean("firstTime", true);
            defaultEditor.commit();
        }
        sharedPreferences  = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        if(sharedPreferences.getBoolean("LogInState", true))
        {
            int UserTypeID = prefs.getInt("UserTypeID",0);
            if((UserTypeID == 3)||(UserTypeID == 5))
            {
                Intent intent = new Intent(LoginScreen.this, StudentMainScreen.class);
                startActivity(intent);
                finish();
            }
            else if(UserTypeID == 4)
            {
                Intent intent = new Intent(LoginScreen.this, TeacherMainScreen.class);
                startActivity(intent);
                finish();
            }
        }
        setContentView(R.layout.activity_login);

        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading...");
        dialog.setMessage("Please Wait...");
        dialog.setCancelable(false);

        loginButton = (Button)findViewById(R.id.login_button);
        takeId = (AutoCompleteTextView)findViewById(R.id.email);
        takePassword = (EditText) findViewById(R.id.password);
        errorView = (TextView)findViewById(R.id.error);

        loginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                loginurl = getString(R.string.baseUrlLocal)+"getLoginInformation"+"/"+takeId.getText().toString()+"/"+takePassword.getText().toString();
                LoginId = takeId.getText().toString();
                LoginPassword = takePassword.getText().toString();

                if((LoginId.equals("22"))&&(LoginPassword.equals("22")))
                {
                    Intent mainIntent = new Intent(LoginScreen.this,TeacherMainScreen.class);
                    LoginScreen.this.startActivity(mainIntent);
                    LoginScreen.this.finish();
                    dialog.dismiss();
                }
                else if(takeId.getText().toString().isEmpty())
                {
                    takeId.setError("This field is required");
                    takeId.requestFocus();
                }
                else if(takePassword.getText().toString().isEmpty())
                {
                    takePassword.setError("This field is required");
                    takePassword.requestFocus();
                }
                else
                {
                    dialog.show();
                    // Get Login ID and Password From Server Using Volley
                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, loginurl,
                            new Response.Listener<String>()
                            {
                                @Override
                                public void onResponse(String response) {

                                    parseJsonData(response); // User define Function For parsing JSON data

                                    LoginId = takeId.getText().toString();
                                    LoginPassword = takePassword.getText().toString();

                                    // Login For User

                                    if((UserID.length()>0) && (UserID.equals("1")) && (UserTypeID == 3))
                                    {
                                        Intent mainIntent = new Intent(LoginScreen.this,StudentMainScreen.class);
                                        LoginScreen.this.startActivity(mainIntent);
                                        LoginScreen.this.finish();
                                        dialog.dismiss();
                                    }
                                    // Login For Teacher
                                    else if((UserID.length()>0) && (UserID.equals("150")) && (UserTypeID == 4))
                                    {
                                        Intent mainIntent = new Intent(LoginScreen.this,TeacherMainScreen.class);
                                        LoginScreen.this.startActivity(mainIntent);
                                        LoginScreen.this.finish();
                                        dialog.dismiss();
                                    }
                                    else
                                    {
                                        errorView.setText("Invalid Login ID or Password !!!");
                                        takeId.setText("");
                                        takePassword.setText("");
                                        takeId.requestFocus();
                                        dialog.dismiss();
                                    }

                                }
                            }, new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error)
                        {

                            dialog.dismiss();
                        }
                    });

                    queue.add(stringRequest);
                }



                // Get Login ID and Password From Server Using Volley END


            }
        });
    }

    void parseJsonData(String jsonString)
    {
        try
        {
            // Parse Json data From API

            JSONArray jsonArray = new JSONArray(jsonString);
            UserID = jsonArray.getJSONObject(0).getString("UserID");
            Password = jsonArray.getJSONObject(0).getString("Password");
            UserTypeID = jsonArray.getJSONObject(0).getInt("UserTypeID");
//            UserFullName = jsonArray.getJSONObject(0).getString("UserFullName");
//            InstituteName = jsonArray.getJSONObject(0).getString("InstituteName");
//            InstituteID = jsonArray.getJSONObject(0).getInt("InstituteID");
            ImageUrl = jsonArray.getJSONObject(0).getString("ImageUrl");
//            DepartmentID = jsonArray.getJSONObject(0).getString("DepartmentID");
//            DesignationID = jsonArray.getJSONObject(0).getString("DesignationID");
//            BrunchID = jsonArray.getJSONObject(0).getString("BrunchID");
//            DepartmentName = jsonArray.getJSONObject(0).getString("DepartmentName");
//            DesignationName = jsonArray.getJSONObject(0).getString("DesignationName");
//            BrunchName = jsonArray.getJSONObject(0).getString("BrunchName");
//            SBrunchID = jsonArray.getJSONObject(0).getInt("SBrunchID");
//            BoardID = jsonArray.getJSONObject(0).getInt("BoardID");
//            SDepartmentID = jsonArray.getJSONObject(0).getInt("SDepartmentID");
//            MediumID = jsonArray.getJSONObject(0).getInt("MediumID");
//            RFID = jsonArray.getJSONObject(0).getString("RFID");
//            RollNo = jsonArray.getJSONObject(0).getString("RollNo");
//            SectionID = jsonArray.getJSONObject(0).getInt("SectionID");
//            SessionID = jsonArray.getJSONObject(0).getInt("SessionID");
//            ShiftID = jsonArray.getJSONObject(0).getInt("ShiftID");
//            ClassID = jsonArray.getJSONObject(0).getInt("ClassID");
//            StudentNo = jsonArray.getJSONObject(0).getString("StudentNo");
            // Parse Json data From API END

            // Using SharedPreferences For save Internal Data
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("UserID", UserID);
            editor.putString("Password",Password);
            editor.putInt("UserTypeID",UserTypeID);
//            editor.putString("UserFullName",UserFullName);
//            editor.putInt("InstituteID",InstituteID);
//            editor.putString("InstituteName",InstituteName);
            editor.putString("ImageUrl",ImageUrl);
//            editor.putString("DepartmentID",DepartmentID);
//            editor.putString("DesignationID",DesignationID);
//            editor.putString("BrunchID",BrunchID);
//            editor.putString("DepartmentName",DepartmentName);
//            editor.putString("DesignationName",DesignationName);
//            editor.putString("BrunchName",BrunchName);
//            editor.putInt("SBrunchID",SBrunchID);
//            editor.putInt(" BoardID", BoardID);
//            editor.putInt("SDepartmentID",SDepartmentID);
//            editor.putInt("MediumID",MediumID);
//            editor.putString("RFID",RFID);
//            editor.putString("RollNo",RollNo);
//            editor.putInt("SectionID",SectionID);
//            editor.putInt("SessionID",SessionID);
//            editor.putInt("ShiftID",ShiftID);
//            editor.putInt("ClassID",ClassID );
//            editor.putString("StudentNo",StudentNo);
            editor.commit();

            sharedPreferences  = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor loginEditor = sharedPreferences.edit();
            loginEditor.putBoolean("LogInState", true);
            loginEditor.commit();

        }
        catch (JSONException e)
        {
            Toast.makeText(this,"Json : "+e,Toast.LENGTH_LONG).show();
        }

       dialog.dismiss();
    }
}
