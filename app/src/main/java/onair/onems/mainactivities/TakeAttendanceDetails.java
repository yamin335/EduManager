package onair.onems.mainactivities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

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
import java.util.List;
import onair.onems.R;
import onair.onems.Services.AttendanceDataPoster;
import onair.onems.customadapters.CustomRequest;
import onair.onems.customadapters.TakeAttendanceAdapter;
import onair.onems.models.ApiResponseValue;
import onair.onems.models.AttendanceSheetModel;
import onair.onems.models.AttendanceStudentModel;
import onair.onems.models.StudentModel;
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
    String date, StudentDataGetUrl, postUrl;
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

        StudentDataGetUrl = getString(R.string.baseUrlLocal)+"getHrmSubWiseAtdDetail/"+InstituteID+"/"+MediumID+"/"+ShiftID+"/"+ClassID+"/"+SectionID+"/"+SubjectID+"/"+DepertmentID;

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        attendanceSheetArrayList = new ArrayList<AttendanceStudentModel>();
        adapter = new TakeAttendanceAdapter(this, attendanceSheetArrayList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        //Preparing claas data from server
        RequestQueue queueStudentData = Volley.newRequestQueue(this);
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
        });
        queueStudentData.add(stringStudentDataRequest);
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
                AttendanceStudentModel attendanceStudentModel = new AttendanceStudentModel(studentJsonObject.getString("UserFullName"),
                        studentJsonObject.getString("UserID"), studentJsonObject.getString("RFID"),
                        studentJsonObject.getString("RollNo"), studentJsonObject.getString("SubjectID"),
                        studentJsonObject.getString("DepartmentID"), studentJsonObject.getString("SectionID"),
                        studentJsonObject.getString("MediumID"), studentJsonObject.getString("ShiftID"),"",
                        studentJsonObject.getString("ClassID"), studentJsonObject.getString("BoardID"),
                        studentJsonObject.getString("BrunchID"), studentJsonObject.getString("SessionID"),
                        studentJsonObject.getString("IsPresent"), studentJsonObject.getString("Islate"),
                        studentJsonObject.getString("LateTime"), studentJsonObject.getString("IsLeave"),
                        studentJsonObject.getString("IsAbsent"));
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
            AttendanceSheetModel attendanceSheetModel = new AttendanceSheetModel();
            attendanceSheetModel.setSubAtdID("0");
            attendanceSheetModel.setSubjectID(Long.toString(SubjectID));
            attendanceSheetModel.setSectionID(Long.toString(SectionID));
            attendanceSheetModel.setDepartmentID(Long.toString(DepertmentID));
            attendanceSheetModel.setMediumID(Long.toString(MediumID));
            attendanceSheetModel.setShiftID(Long.toString(ShiftID));
            attendanceSheetModel.setClassID(Long.toString(ClassID));
            attendanceSheetModel.setAtdUserID("1");
            attendanceSheetModel.setAtdDate(date);
            attendanceSheetModel.setInstituteID(Long.toString(InstituteID));
            attendanceSheetModel.setattendenceArr(attendanceSheetArrayList);
            Gson gson = new Gson();
            String json = gson.toJson(attendanceSheetModel);
            dialog.show();
            postUsingVolley(json);
//            Toast.makeText(this,json,Toast.LENGTH_LONG).show();
//            postAttendanceSheetData(json);
//            sharedPreferences  = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putBoolean("LogInState", false);
//            editor.commit();
//            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//            startActivity(intent);
//            finish();
            return true;
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
        RequestQueue queuePost = Volley.newRequestQueue(this);
        CustomRequest customRequest = new CustomRequest (Request.Method.POST, postUrl, postDatajsonObject,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        dialog.dismiss();
                        try {
                            Toast.makeText(TakeAttendanceDetails.this,"Data Successfully Updated with Response: "+response.getJSONObject(0).get("ReturnValue"),Toast.LENGTH_LONG).show();
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
        });
        queuePost.add(customRequest);
    }
}
