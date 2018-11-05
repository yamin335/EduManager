package onair.onems.attendance;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import onair.onems.R;
import onair.onems.Services.StaticHelperClass;
import onair.onems.mainactivities.SideNavigationMenuParentActivity;
import onair.onems.mainactivities.TeacherMainScreen;
import onair.onems.models.SectionModel;
import onair.onems.network.MySingleton;

public class AttendanceAdminDashboard extends SideNavigationMenuParentActivity {

    private long InstituteID;
    private int UserTypeID;
    private ProgressDialog mTeacherDialog, mStudentDialog;
    private String today;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityName = AttendanceAdminDashboard.class.getName();

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View childActivityLayout = inflater.inflate(R.layout.attendance_admin_dashboard, null);
        LinearLayout parentActivityLayout = (LinearLayout) findViewById(R.id.contentMain);
        parentActivityLayout.addView(childActivityLayout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        InstituteID = prefs.getLong("InstituteID",0);
        UserTypeID = prefs.getInt("UserTypeID",0);

        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        today = df.format(date);

        teacherAttendanceDataGetRequest();
        studentAttendanceDataGetRequest();

        Button takeAttendance, showAttendance;
        takeAttendance = findViewById(R.id.takeAttendance);
        showAttendance = findViewById(R.id.showAttendance);

        takeAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(AttendanceAdminDashboard.this, TakeAttendance.class);
                startActivity(mainIntent);
                finish();
            }
        });

        showAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(AttendanceAdminDashboard.this, ShowAttendance.class);
                mainIntent.putExtra("fromDashBoard", true);
                mainIntent.putExtra("fromSideMenu", false);
                startActivity(mainIntent);
                finish();
            }
        });

    }

    private void teacherAttendanceDataGetRequest() {
        if(StaticHelperClass.isNetworkAvailable(this)) {

            String teacherUrl = getString(R.string.baseUrl)+"/api/onEms/getTotalTeacherAndTotalAttendence/"+today+"/"+
                    InstituteID+"/"+"4";

            mTeacherDialog = new ProgressDialog(this);
            mTeacherDialog.setTitle("Loading...");
            mTeacherDialog.setMessage("Please Wait...");
            mTeacherDialog.setCancelable(false);
            mTeacherDialog.setIcon(R.drawable.onair);
            mTeacherDialog.show();
            //Preparing section data from server
            StringRequest stringTeacherRequest = new StringRequest(Request.Method.GET, teacherUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            parseTeacherAttendanceJsonData(response);

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mTeacherDialog.dismiss();
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
            MySingleton.getInstance(this).addToRequestQueue(stringTeacherRequest);
        } else {
            Toast.makeText(AttendanceAdminDashboard.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void studentAttendanceDataGetRequest() {
        if(StaticHelperClass.isNetworkAvailable(this)) {

            String studentUrl = getString(R.string.baseUrl)+"/api/onEms/getTotalStudentAndTotalAttendenceDashBoard/"+today+"/"+
                    InstituteID+"/"+3;

            mStudentDialog = new ProgressDialog(this);
            mStudentDialog.setTitle("Loading...");
            mStudentDialog.setMessage("Please Wait...");
            mStudentDialog.setCancelable(false);
            mStudentDialog.setIcon(R.drawable.onair);
            mStudentDialog.show();
            //Preparing section data from server
            StringRequest stringSectionRequest = new StringRequest(Request.Method.GET, studentUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            parseStudentAttendanceJsonData(response);

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
            MySingleton.getInstance(this).addToRequestQueue(stringSectionRequest);
        } else {
            Toast.makeText(AttendanceAdminDashboard.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    void parseTeacherAttendanceJsonData(String jsonString) {
        try {
            JSONObject teacherAttendance = new JSONArray(jsonString).getJSONObject(0);
            int total = teacherAttendance.getInt("TotalStudent");
            int present = teacherAttendance.getInt("Present");
            int absent = total - present;
            double attendancePercentage = 0.0;
            if(total!=0){
                attendancePercentage = (double)(present*100)/total;
            }
            TextView totalTeacher, presentTeacher, absentTeacher, percentageTeacher;
            totalTeacher = findViewById(R.id.totalTeacher);
            presentTeacher = findViewById(R.id.attendedTeacher);
            absentTeacher = findViewById(R.id.absentTeacher);
            percentageTeacher = findViewById(R.id.attendancePercentageTeacher);
            totalTeacher.setText(Integer.toString(total));
            presentTeacher.setText(Integer.toString(present));
            absentTeacher.setText(Integer.toString(absent));
            percentageTeacher.setText(Double.toString(attendancePercentage)+"%");
            mTeacherDialog.dismiss();
        } catch (JSONException e) {
            mTeacherDialog.dismiss();
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
        }
    }

    void parseStudentAttendanceJsonData(String jsonString) {
        try {
            JSONObject studentAttendance = new JSONArray(jsonString).getJSONObject(0);
            int total = studentAttendance.getInt("TotalStudent");
            int present = studentAttendance.getInt("Present");
            int absent = total - present;
            double attendancePercentage = 0.0;
            if(total!=0){
                attendancePercentage = (double)(present*100)/total;
            }
            TextView totalStudent, presentStudent, absentStudent, percentageStudent;
            totalStudent = findViewById(R.id.totalStudent);
            presentStudent = findViewById(R.id.attendedStudent);
            absentStudent = findViewById(R.id.absentStudent);
            percentageStudent = findViewById(R.id.studentPercentage);
            totalStudent.setText(Integer.toString(total));
            presentStudent.setText(Integer.toString(present));
            absentStudent.setText(Integer.toString(absent));
            percentageStudent.setText(Double.toString(attendancePercentage)+"%");
            mStudentDialog.dismiss();
        } catch (JSONException e) {
            mStudentDialog.dismiss();
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent mainIntent = new Intent(AttendanceAdminDashboard.this, TeacherMainScreen.class);
            startActivity(mainIntent);
            finish();
        }
    }
}
