package onair.onems.mainactivities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
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
import java.util.List;
import onair.onems.R;
import onair.onems.customadapters.AttendanceSheetAdapter;
import onair.onems.customadapters.TakeAttendanceAdapter;
import onair.onems.models.ClassModel;
import onair.onems.models.Student;
import onair.onems.models.StudentModel;

/**
 * Created by User on 12/20/2017.
 */

public class TakeAttendanceDetails extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TakeAttendanceAdapter adapter;
    private List<Student> attendanceSheetList;
    private int InstituteID,MediumID,ShiftID,ClassID,SectionID,SubjectID,DepertmentID;
    String date, StudentDataGetUrl;
    ProgressDialog dialog;

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
        InstituteID = StudentSelection.getInt("InstituteID",0);
        MediumID = StudentSelection.getInt("MediumID",0);
        ShiftID = StudentSelection.getInt("ShiftID",0);
        ClassID = StudentSelection.getInt("ClassID",0);
        SectionID = StudentSelection.getInt("SectionID",0);
        SubjectID = StudentSelection.getInt("SubjectID",0);
        DepertmentID = StudentSelection.getInt("DepertmentID",0);
        date = StudentSelection.getString("Date", "");

        StudentDataGetUrl = getString(R.string.baseUrlLocal)+"getHrmSubWiseAtdDetail/"+InstituteID+"/"+MediumID+"/"+ShiftID+"/"+ClassID+"/"+SectionID+"/"+SubjectID+"/"+DepertmentID;

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        attendanceSheetList = new ArrayList<Student>();
        adapter = new TakeAttendanceAdapter(this, attendanceSheetList);

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
            JSONArray StudentJsonArray = new JSONArray(jsonString);

//            ArrayList StudentArrayList = new ArrayList();
            for(int i = 0; i < StudentJsonArray.length(); ++i) {
                JSONObject studentJsonObject = StudentJsonArray.getJSONObject(i);
                Student student = new Student(studentJsonObject.getString("UserFullName"), studentJsonObject.getString("RollNo"));
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
                attendanceSheetList.add(student);
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
}
