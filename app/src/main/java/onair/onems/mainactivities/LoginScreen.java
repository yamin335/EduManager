package onair.onems.mainactivities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.HashMap;
import java.util.Map;
import onair.onems.R;
import onair.onems.Services.StaticHelperClass;
import onair.onems.customadapters.GuardianStudentSelectionDialog;
import onair.onems.network.MySingleton;

public class LoginScreen extends AppCompatActivity {
    private Button loginButton;
    private EditText takeId;
    private EditText takePassword;
    private TextView errorView;
    private String UserID = "", Password = "", UserFullName = "",
            ImageUrl = "", InstituteName = "", DepartmentName = "",
            DesignationName = "", BrunchName = "", RFID = "", RollNo = "",
            StudentNo = "";

    private long InstituteID = 0, SBrunchID = 0, BoardID = 0, SDepartmentID = 0,
            DepartmentID = 0, MediumID = 0, SectionID = 0, SessionID = 0, ShiftID = 0, ClassID = 0,
            DesignationID = 0, BrunchID = 0;
    private int UserTypeID = 0;
    private String loginUrl = "";
    private ProgressDialog dialog, mStudentDialog;
    public static final String MyPREFERENCES = "LogInKey";
    public static SharedPreferences sharedPreferences;

    @Override
    public void onResume() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        super.onResume();
    }

    @Override
    public void onStart() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        super.onStart();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.getBoolean("firstTime", false)) {
            // <---- run your one time code here
            sharedPreferences  = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("LogInState", false);
            editor.apply();

            // mark first time has runned.
            SharedPreferences.Editor defaultEditor = prefs.edit();
            defaultEditor.putBoolean("firstTime", true);
            defaultEditor.apply();
        }
        sharedPreferences  = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        if(sharedPreferences.getBoolean("LogInState", true)) {
            int UserTypeID = prefs.getInt("UserTypeID",0);

            if(UserTypeID == 1){
                Intent intent = new Intent(LoginScreen.this, TeacherMainScreen.class);
                startActivity(intent);
                finish();
            } else if(UserTypeID == 2) {
                Intent intent = new Intent(LoginScreen.this, TeacherMainScreen.class);
                startActivity(intent);
                finish();
            } else if(UserTypeID == 3) {
                Intent intent = new Intent(LoginScreen.this, StudentMainScreen.class);
                startActivity(intent);
                finish();
            } else if(UserTypeID == 4) {
                Intent intent = new Intent(LoginScreen.this, TeacherMainScreen.class);
                startActivity(intent);
                finish();
            } else if(UserTypeID == 5) {
                Intent intent = new Intent(LoginScreen.this, StudentMainScreen.class);
                startActivity(intent);
                finish();
            }
        }
        setContentView(R.layout.login_activity);

        loginButton = (Button)findViewById(R.id.login_button);
        takeId = (EditText)findViewById(R.id.email);
        takePassword = (EditText) findViewById(R.id.password);
        errorView = (TextView)findViewById(R.id.error);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginScreen.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                if(StaticHelperClass.isNetworkAvailable(LoginScreen.this)) {

                    loginUrl = getString(R.string.baseUrl)+"/api/onEms/getLoginInformation"+"/"+
                            takeId.getText().toString()+"/"+takePassword.getText().toString();
                    if(takeId.getText().toString().isEmpty()) {
                        takeId.setError("This field is required");
                        takeId.requestFocus();
                    } else if(takePassword.getText().toString().isEmpty()) {
                        takePassword.setError("This field is required");
                        takePassword.requestFocus();
                    } else {
                        getLoginData();
                    }
                } else {
                    Toast.makeText(LoginScreen.this,"Please check your internet connection!!!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    void parseLoginJsonData(String jsonString) {
        try {
            // Parse Json data From API
            JSONArray jsonArray = new JSONArray(jsonString);
            UserID = jsonArray.getJSONObject(0).getString("UserID");
            Password = jsonArray.getJSONObject(0).getString("Password");
            String UserTypeIDTemp = jsonArray.getJSONObject(0).getString("UserTypeID");
            if(UserTypeIDTemp.equals("null")) {
                UserTypeID = 0;
            } else {
                UserTypeID = Integer.parseInt(UserTypeIDTemp);
            }
            UserFullName = jsonArray.getJSONObject(0).getString("UserFullName");
            InstituteName = jsonArray.getJSONObject(0).getString("InstituteName");
            String InstituteIDTemp = jsonArray.getJSONObject(0).getString("InstituteID");
            if(InstituteIDTemp.equals("null")) {
                InstituteID = 0;
            } else {
                InstituteID = Long.parseLong(InstituteIDTemp);
            }
            ImageUrl = jsonArray.getJSONObject(0).getString("ImageUrl");
            String DepartmentIDTemp = jsonArray.getJSONObject(0).getString("DepartmentID");
            if(DepartmentIDTemp.equals("null")) {
                DepartmentID = 0;
            } else {
                DepartmentID = Long.parseLong(DepartmentIDTemp);
            }
            String DesignationIDTemp = jsonArray.getJSONObject(0).getString("DesignationID");
            if(DesignationIDTemp.equals("null")) {
                DesignationID = 0;
            } else {
                DesignationID = Long.parseLong(DesignationIDTemp);
            }
            String BrunchIDTemp = jsonArray.getJSONObject(0).getString("BrunchID");
            if(BrunchIDTemp.equals("null")) {
                BrunchID = 0;
            } else {
                BrunchID = Long.parseLong(BrunchIDTemp);
            }
            DepartmentName = jsonArray.getJSONObject(0).getString("DepartmentName");
            DesignationName = jsonArray.getJSONObject(0).getString("DesignationName");
            BrunchName = jsonArray.getJSONObject(0).getString("BrunchName");
            String SBrunchIDTemp = jsonArray.getJSONObject(0).getString("SBrunchID");
            if(SBrunchIDTemp.equals("null")) {
                SBrunchID = 0;
            } else {
                SBrunchID = Long.parseLong(SBrunchIDTemp);
            }
            String BoardIDTemp = jsonArray.getJSONObject(0).getString("BoardID");
            if(BoardIDTemp.equals("null")) {
                BoardID = 0;
            } else {
                BoardID = Long.parseLong(BoardIDTemp);
            }
            String SDepartmentIDTemp = jsonArray.getJSONObject(0).getString("SDepartmentID");
            if(SDepartmentIDTemp.equals("null")) {
                SDepartmentID = 0;
            } else {
                SDepartmentID = Long.parseLong(SDepartmentIDTemp);
            }
            String MediumIDTemp = jsonArray.getJSONObject(0).getString("MediumID");
            if(MediumIDTemp.equals("null")) {
                MediumID = 0;
            } else {
                MediumID = Long.parseLong(MediumIDTemp);
            }
            RFID = jsonArray.getJSONObject(0).getString("RFID");
            RollNo = jsonArray.getJSONObject(0).getString("RollNo");
            String SectionIDTemp = jsonArray.getJSONObject(0).getString("SectionID");
            if(SectionIDTemp.equals("null")) {
                SectionID = 0;
            } else {
                SectionID = Long.parseLong(SectionIDTemp);
            }
            String SessionIDTemp = jsonArray.getJSONObject(0).getString("SessionID");
            if(SessionIDTemp.equals("null")) {
                SessionID = 0;
            } else {
                SessionID = Long.parseLong(SessionIDTemp);
            }
            String ShiftIDTemp = jsonArray.getJSONObject(0).getString("ShiftID");
            if(ShiftIDTemp.equals("null")) {
                ShiftID = 0;
            } else {
                ShiftID = Long.parseLong(ShiftIDTemp);
            }
            String ClassIDTemp = jsonArray.getJSONObject(0).getString("ClassID");
            if(ClassIDTemp.equals("null")) {
                ClassID = 0;
            } else {
                ClassID = Long.parseLong(ClassIDTemp);
            }
            StudentNo = jsonArray.getJSONObject(0).getString("StudentNo");
            // Parse Json data From API END

            // Using SharedPreferences For save Internal Data
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("UserID", UserID);
            editor.putString("Password",Password);
            editor.putInt("UserTypeID",UserTypeID);
            editor.putString("UserFullName",UserFullName);
            editor.putLong("InstituteID",InstituteID);
            editor.putString("InstituteName",InstituteName);
            editor.putString("ImageUrl",ImageUrl);
            editor.putLong("DepartmentID",DepartmentID);
            editor.putLong("DesignationID",DesignationID);
            editor.putLong("BrunchID",BrunchID);
            editor.putString("DepartmentName",DepartmentName);
            editor.putString("DesignationName",DesignationName);
            editor.putString("BrunchName",BrunchName);
            editor.putLong("SBrunchID",SBrunchID);
            editor.putLong(" BoardID", BoardID);
            editor.putLong("SDepartmentID",SDepartmentID);
            editor.putLong("MediumID",MediumID);
            editor.putString("RFID",RFID);
            editor.putString("RollNo",RollNo);
            editor.putLong("SectionID",SectionID);
            editor.putLong("SessionID",SessionID);
            editor.putLong("ShiftID",ShiftID);
            editor.putLong("ClassID",ClassID );
            editor.putString("StudentNo",StudentNo);
            editor.apply();

            sharedPreferences  = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor loginEditor = sharedPreferences.edit();
            loginEditor.putBoolean("LogInState", true);
            loginEditor.apply();


            // Login For User
            if((UserID.length()>0) && (UserTypeID == 1)) {
                Intent mainIntent = new Intent(LoginScreen.this, TeacherMainScreen.class);
                startActivity(mainIntent);
                finish();
                dialog.dismiss();
            } else if((UserID.length()>0) && (UserTypeID == 2)) {
                Intent mainIntent = new Intent(LoginScreen.this, TeacherMainScreen.class);
                startActivity(mainIntent);
                finish();
                dialog.dismiss();
            } else if((UserID.length()>0) && (UserTypeID == 3)) {
                Intent mainIntent = new Intent(LoginScreen.this, StudentMainScreen.class);
                startActivity(mainIntent);
                finish();
                dialog.dismiss();
            } else if((UserID.length()>0) && (UserTypeID == 4)) {
                Intent mainIntent = new Intent(LoginScreen.this,TeacherMainScreen.class);
                startActivity(mainIntent);
                finish();
                dialog.dismiss();
            } else if((UserID.length()>0) && (UserTypeID == 5)) {
                dialog.dismiss();
                getStudents(UserID, InstituteID);
            } else {
                errorView.setText("Invalid Login ID or Password !!!");
                takeId.setText("");
                takePassword.setText("");
                takeId.requestFocus();
                dialog.dismiss();
            }

        } catch (JSONException e) {
            Toast.makeText(this,"Json : "+e,Toast.LENGTH_LONG).show();
        }

       dialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name);
        builder.setIcon(R.drawable.onair);
        builder.setMessage("Do you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void getLoginData() {

        if (StaticHelperClass.isNetworkAvailable(this)) {

            dialog = new ProgressDialog(this);
            dialog.setTitle("Login...");
            dialog.setMessage("Please Wait...");
            dialog.setCancelable(false);
            dialog.setIcon(R.drawable.onair);
            dialog.show();
            //Preparing Shift data from server
            StringRequest loginRequest = new StringRequest(Request.Method.GET, loginUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            parseLoginJsonData(response);

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dialog.dismiss();
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
            MySingleton.getInstance(this).addToRequestQueue(loginRequest);
        } else {
            Toast.makeText(LoginScreen.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }

    }

    private void getStudents(String UserID, long InstituteID) {

        if (StaticHelperClass.isNetworkAvailable(this)) {
            String studentUrl = getString(R.string.baseUrl)+"/api/onEms/spGetDashUserDetail/"+UserID+"/"+InstituteID;

            mStudentDialog = new ProgressDialog(this);
            mStudentDialog.setTitle("Loading Students...");
            mStudentDialog.setMessage("Please Wait...");
            mStudentDialog.setCancelable(false);
            mStudentDialog.setIcon(R.drawable.onair);
            mStudentDialog.show();
            //Preparing Shift data from server
            StringRequest stringStudentRequest = new StringRequest(Request.Method.GET, studentUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            parseStudentJsonData(response);

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mStudentDialog.dismiss();
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
            MySingleton.getInstance(this).addToRequestQueue(stringStudentRequest);
        } else {
            Toast.makeText(LoginScreen.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }

    }

    private void parseStudentJsonData(String string) {
        try {
            JSONArray students = new JSONArray(string);
            getSharedPreferences("GUARDIAN_STUDENTS", Context.MODE_PRIVATE)
                    .edit()
                    .putString("guardianStudentList", students.toString())
                    .apply();
            GuardianStudentSelectionDialog selectStudent = new GuardianStudentSelectionDialog(this, students, this);
            selectStudent.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            selectStudent.setCancelable(false);
            selectStudent.show();
            mStudentDialog.dismiss();
        } catch (JSONException e) {
            mStudentDialog.dismiss();
            e.printStackTrace();
        }
    }


}
