package onair.onems.mainactivities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

/**
 * Created by User on 12/5/2017.
 */

public class LoginScreen extends AppCompatActivity {

    private Button loginButton;
    private EditText takeId;
    private EditText takePassword;
    private String UserID = "",LoginId="",LoginPassword="",Password = "",UserFullName="",ImageUrl="",InstituteName="",DepartmentName="",DesignationName="",BrunchName="",RFID="",RollNo="",StudentNo="";
    int UserTypeID=0,InstituteID=0,DepartmentID,DesignationID,BrunchID,SBrunchID=0,BoardID,SDepartmentID,MediumID=0,
            SectionID=0,SessionID=0,ShiftID=0,ClassID=0;

    String loginurl = "http://192.168.1.82:4000/api/onEms/getLoginInformation/1/1";
    ProgressDialog dialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginButton = (Button)findViewById(R.id.login_button);
        takeId = (EditText)findViewById(R.id.id);
        takePassword = (EditText) findViewById(R.id.password);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading....");
        dialog.show();
        RequestQueue queue = Volley.newRequestQueue(this);
        // String url ="http://192.168.1.105:4000";
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, loginurl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        parseJsonData(response);
                        //mTextView.setText("Response is: "+ response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"error:"+error,Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });

        queue.add(stringRequest);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LoginId = takeId.getText().toString();
                LoginPassword = takePassword.getText().toString();
                if((LoginId.equals(UserID))&&(LoginPassword.equals(Password)))
                {
                    Intent mainIntent = new Intent(LoginScreen.this,StudentMainScreen.class);
                    LoginScreen.this.startActivity(mainIntent);
                    LoginScreen.this.finish();
                }
                else if((LoginId.equals("22"))&&(LoginPassword.equals("22")))
                {
                    Intent mainIntent = new Intent(LoginScreen.this,TeacherMainScreen.class);
                    LoginScreen.this.startActivity(mainIntent);
                    LoginScreen.this.finish();
                }
                else
                {
                    Toast.makeText(LoginScreen.this,"Please enter valid id",Toast.LENGTH_LONG).show();
                }

//                Intent mainIntent = new Intent(LoginScreen.this,TeacherMainScreen.class);
//                LoginScreen.this.startActivity(mainIntent);
//                LoginScreen.this.finish();
            }
        });



    }

    void parseJsonData(String jsonString) {
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            UserID=jsonArray.getJSONObject(0).getString("UserID");
            Password=jsonArray.getJSONObject(0).getString("Password");
            UserTypeID=jsonArray.getJSONObject(0).getInt("UserTypeID");
            UserFullName=jsonArray.getJSONObject(0).getString("UserFullName");
            InstituteID=jsonArray.getJSONObject(0).getInt("InstituteID");
            //ImageUrl=jsonArray.getJSONObject(0).getString("ImageUrl");
           // DepartmentID=jsonArray.getJSONObject(0).getInt("DepartmentID");
           // DesignationID =jsonArray.getJSONObject(0).getInt("DesignationID");
           // BrunchID =jsonArray.getJSONObject(0).getInt("BrunchID");
            InstituteName=jsonArray.getJSONObject(0).getString("InstituteName");
           // DepartmentName=jsonArray.getJSONObject(0).getString("DepartmentName");
           // DesignationName=jsonArray.getJSONObject(0).getString("DesignationName");
            //BrunchName=jsonArray.getJSONObject(0).getString("BrunchName");
            SBrunchID=jsonArray.getJSONObject(0).getInt("SBrunchID");
            BoardID =jsonArray.getJSONObject(0).getInt("BoardID");
            SDepartmentID =jsonArray.getJSONObject(0).getInt("SDepartmentID");
            MediumID =jsonArray.getJSONObject(0).getInt("MediumID");
            RFID=jsonArray.getJSONObject(0).getString("RFID");
            RollNo=jsonArray.getJSONObject(0).getString("RollNo");
            SectionID =jsonArray.getJSONObject(0).getInt("SectionID");
            SessionID =jsonArray.getJSONObject(0).getInt("SessionID");
            ShiftID =jsonArray.getJSONObject(0).getInt("ShiftID");
            ClassID =jsonArray.getJSONObject(0).getInt("ClassID");
            StudentNo=jsonArray.getJSONObject(0).getString("StudentNo");
            SharedPreferences sharedPre = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = sharedPre.edit();
            editor.putString("UserID", UserID);
            editor.putString("Password",Password);
            editor.putInt("UserTypeID",UserTypeID);
            editor.putString("UserFullName",UserFullName);
            editor.putInt("InstituteID",InstituteID);
            //editor.putString("ImageUrl",ImageUrl);
            //editor.putInt("DepartmentID",DepartmentID);
            //editor.putInt("DesignationID",DesignationID);
            //editor.putInt("BrunchID",BrunchID);
            editor.putString("InstituteName",InstituteName);
           // editor.putString("DepartmentName",DepartmentName);
          //  editor.putString("DesignationName",DesignationName);
           // editor.putString("BrunchName",BrunchName);
            editor.putInt("SBrunchID",SBrunchID);
            editor.putInt(" BoardID", BoardID);
            editor.putInt("SDepartmentID",SDepartmentID);
            editor.putInt("MediumID",MediumID);
            editor.putString("RFID",RFID);
            editor.putString("RollNo",RollNo);
            editor.putInt("SectionID",SectionID);
            editor.putInt("SessionID",SessionID);
            editor.putInt("ShiftID",ShiftID);
            editor.putInt("ClassID",ClassID );
            editor.putString("StudentNo",StudentNo);
            editor.commit();

        } catch (JSONException e) {
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
        }

        dialog.dismiss();
    }
}
