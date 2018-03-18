package onair.onems.mainactivities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import onair.onems.R;
import onair.onems.Services.AttendanceDataPoster;
import onair.onems.customadapters.CustomRequest;
import onair.onems.customadapters.TakeAttendanceAdapter;
import onair.onems.models.ApiResponseValue;
import onair.onems.models.AttendanceSheetModel;
import onair.onems.models.AttendanceStudentModel;
import onair.onems.models.StudentModel;
import onair.onems.network.MySingleton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by User on 12/20/2017.
 */

public class TakeAttendanceDetails extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TakeAttendanceAdapter adapter;
    private ArrayList<AttendanceStudentModel> attendanceSheetArrayList;
    private long InstituteID,MediumID,ShiftID,ClassID,SectionID,SubjectID,DepertmentID;
    String date, StudentDataGetUrl, postUrl, UserID, SubAtdID;
    ProgressDialog dialog;
    JSONArray StudentJsonArray, InstituteIDJsonArray;
    JSONObject postDatajsonObject;

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
        setContentView(R.layout.activity_take_attendance_screen);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading...");
        dialog.setMessage("Please Wait...");
        dialog.setCancelable(false);
        dialog.show();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        UserID = prefs.getString("UserID", "0");

        Bundle StudentSelection = getIntent().getExtras();
        InstituteID = StudentSelection.getLong("InstituteID",0);
        MediumID = StudentSelection.getLong("MediumID",0);
        ShiftID = StudentSelection.getLong("ShiftID",0);
        ClassID = StudentSelection.getLong("ClassID",0);
        SectionID = StudentSelection.getLong("SectionID",0);
        SubjectID = StudentSelection.getLong("SubjectID",0);
        DepertmentID = StudentSelection.getLong("DepertmentID",0);
        date = StudentSelection.getString("Date", "");

        postUrl = getString(R.string.baseUrlLocal)+"setHrmSubWiseAtd";

        StudentDataGetUrl = getString(R.string.baseUrlLocal)+"getHrmSubWiseAtdDetail/"+InstituteID+"/"+MediumID+"/"+ShiftID+"/"+ClassID+"/"+SectionID+"/"+SubjectID+"/"+DepertmentID+"/"+date;

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        attendanceSheetArrayList = new ArrayList<AttendanceStudentModel>();
        adapter = new TakeAttendanceAdapter(this, attendanceSheetArrayList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        StudentDataGetRequest();
//        prepareAlbums();

    }

    private void prepareAlbums(String jsonString) {

        try {
            StudentJsonArray = new JSONArray(jsonString);
//            ArrayList StudentArrayList = new ArrayList();
            for(int i = 0; i < StudentJsonArray.length(); ++i) {
                JSONObject studentJsonObject = StudentJsonArray.getJSONObject(i);
//                InstituteIDJsonArray = studentJsonObject.getJSONArray("InstituteID");
//                String[] strings = new String[InstituteIDJsonArray.length()];
//                for(int j = 0; j<InstituteIDJsonArray.length(); ++j)
//                {
//                    strings[j] = InstituteIDJsonArray.getString(j);
//                }
                AttendanceStudentModel attendanceStudentModel = new AttendanceStudentModel(
                        studentJsonObject.getString("SubAtdDetailID"),
                        studentJsonObject.getString("SubAtdID"),
                        studentJsonObject.getString("UserFullName"),
                        studentJsonObject.getString("UserID"),
                        studentJsonObject.getString("RFID"),
                        studentJsonObject.getString("RollNo"),
                        studentJsonObject.getString("SubjectID"),
                        studentJsonObject.getString("DepartmentID"),
                        studentJsonObject.getString("SectionID"),
                        studentJsonObject.getString("Section"),
                        studentJsonObject.getString("MediumID"),
                        studentJsonObject.getString("ShiftID"),
                        studentJsonObject.getString("ClassID"),
                        studentJsonObject.getString("BoardID"),
                        studentJsonObject.getString("Board"),
                        studentJsonObject.getString("Brunch"),
                        studentJsonObject.getString("Session"),
                        studentJsonObject.getString("BrunchID"),
                        studentJsonObject.getString("SessionID"),
                        studentJsonObject.getString("IsPresent"),
                        studentJsonObject.getString("Islate"),
                        studentJsonObject.getString("LateTime"),
                        studentJsonObject.getString("IsLeave"),
                        studentJsonObject.getString("IsAbsent"),
                        studentJsonObject.getString("Remarks"),
                        studentJsonObject.getString("Subject"),
                        studentJsonObject.getString("Department"),
                        studentJsonObject.getString("Medium"),
                        studentJsonObject.getString("Shift"),
                        studentJsonObject.getString("Class"),
                        studentJsonObject.getString("DisplayDate"),
                        "true",
                        "0");
//                ArrayList<String> arrayList = new ArrayList<String>();
//                JSONArray jsonArray = studentJsonObject.getJSONArray("InstituteID");
//                for(int j = 0; j<jsonArray.length(); ++i)
//                {
//                    arrayList.add(jsonArray.get(i).toString());
//                }
//                String[] InstituteID = new String[arrayList.size()];
//                InstituteID = arrayList.toArray(InstituteID);
//                StudentModel studentModel = new StudentModel(studentJsonObject.getString("UserID"), studentJsonObject.getString("UserFullName"),
//                        studentJsonObject.getString("RFID"),studentJsonObject.getString("RollNo"),studentJsonObject.getString("SubjectID"),
//                        studentJsonObject.getString("DepartmentID"),studentJsonObject.getString("SectionID"),InstituteID,
//                        studentJsonObject.getString("MediumID"),studentJsonObject.getString("ShiftID"),studentJsonObject.getString("ClassID"),
//                        studentJsonObject.getString("IsPresent"),studentJsonObject.getString("Islate"),studentJsonObject.getString("LateTime"),
//                        studentJsonObject.getString("IsLeave"),studentJsonObject.getString("IsAbsent"),studentJsonObject.getString("Remarks"),
//                        studentJsonObject.getString("Subject"),studentJsonObject.getString("Department"),studentJsonObject.getString("Medium"),
//                        studentJsonObject.getString("Shift"),studentJsonObject.getString("Class"));
//                Toast.makeText(this,classJsonObject.getString("ClassID")+classJsonObject.getString("ClassName"),Toast.LENGTH_LONG).show();
                attendanceSheetArrayList.add(attendanceStudentModel);
//                classArrayList.add(classModel.getClassName());
                SubAtdID = studentJsonObject.getString("SubAtdID");
            }
            adapter.notifyDataSetChanged();
            dialog.dismiss();

        } catch (JSONException e) {
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
            dialog.dismiss();
        }

//        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.done, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.done) {
            if(isNetworkAvailable())
            {
                AttendanceSheetModel attendanceSheetModel = new AttendanceSheetModel();
                attendanceSheetModel.setSubAtdID(SubAtdID);
                attendanceSheetModel.setSubjectID(Long.toString(SubjectID));
                attendanceSheetModel.setSectionID(Long.toString(SectionID==0 ? -2 : SectionID));
                attendanceSheetModel.setDepartmentID(Long.toString(DepertmentID==0 ? -2 : DepertmentID));
                attendanceSheetModel.setMediumID(Long.toString(MediumID==0 ? -2 : MediumID));
                attendanceSheetModel.setShiftID(Long.toString(ShiftID==0 ? -2 : ShiftID));
                attendanceSheetModel.setClassID(Long.toString(ClassID==0 ? -2 : ClassID));
                attendanceSheetModel.setAtdUserID(UserID);
                attendanceSheetModel.setAtdDate(date);
                attendanceSheetModel.setInstituteID(Long.toString(InstituteID));
                attendanceSheetModel.setLoggedUserID(UserID);
                attendanceSheetModel.setattendenceArr(attendanceSheetArrayList);
                Gson gson = new Gson();
                String json = gson.toJson(attendanceSheetModel);
                dialog.show();
                postUsingVolley(json);
                return true;
            }
            else
            {
                Toast.makeText(TakeAttendanceDetails.this,"Please check your internet connection!!!",Toast.LENGTH_LONG).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void postUsingVolley(String json)
    {
        try {
            postDatajsonObject = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        CustomRequest customRequest = new CustomRequest (Request.Method.POST, postUrl, postDatajsonObject,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        dialog.dismiss();
                        try {
                            Toast.makeText(TakeAttendanceDetails.this,"Data Successfully Updated with Response: "+response.getJSONObject(0).get("ReturnValue"),Toast.LENGTH_LONG).show();
                            finish();
                        }
                        catch (Exception e)
                        {

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(TakeAttendanceDetails.this,"Not Response: "+error.toString(),Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Authorization", "Request_From_onEMS_Android_app");
                return params;
            }
        };
        MySingleton.getInstance(this).addToRequestQueue(customRequest);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onBackPressed() {
//        NavUtils.navigateUpFromSameTask(this);
        super.onBackPressed();
    }

    public void StudentDataGetRequest(){
        //Preparing claas data from server
        StringRequest stringStudentDataRequest = new StringRequest(Request.Method.GET, StudentDataGetUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        prepareAlbums(response);

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
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Authorization", "Request_From_onEMS_Android_app");
                return params;
            }
        };
        MySingleton.getInstance(this).addToRequestQueue(stringStudentDataRequest);
    }
}
