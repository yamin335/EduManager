package onair.onems.exam;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import onair.onems.R;
import onair.onems.Services.StaticHelperClass;
import onair.onems.mainactivities.CommonToolbarParentActivity;
import onair.onems.models.ShiftModel;
import onair.onems.network.MySingleton;

public class SubjectWiseMarksEntryStudentList extends CommonToolbarParentActivity implements StudentListAdapter.StudentListAdapterListener {

    private ProgressDialog mStudentDialog;
    private long SubjectID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View childActivityLayout = inflater.inflate(R.layout.exam_subject_wise_marks_entry_student_list, null);
        LinearLayout parentActivityLayout = findViewById(R.id.contentMain);
        parentActivityLayout.addView(childActivityLayout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        Intent intent = getIntent();
        SubjectID = intent.getLongExtra("SubjectID", 0);
        String url = getString(R.string.baseUrl)+"/api/onEms/getSubjectWiseMarks/"+0+"/"+
                intent.getLongExtra("InstituteID", 0)+"/"+intent.getLongExtra("ClassID", 0)+"/"
                +intent.getLongExtra("SectionID", 0)+"/"+intent.getLongExtra("DepartmentID", 0)+"/"
                +intent.getLongExtra("MediumID", 0)+"/"+intent.getLongExtra("ShiftID", 0)+"/"
                +intent.getLongExtra("SubjectID", 0)+"/"+intent.getLongExtra("ExamID", 0);
        StudentDataGetRequest(url);
    }

    private void StudentDataGetRequest(String url) {
        if (StaticHelperClass.isNetworkAvailable(this)) {

            mStudentDialog = new ProgressDialog(this);
            mStudentDialog.setTitle("Loading Student List...");
            mStudentDialog.setMessage("Please Wait...");
            mStudentDialog.setCancelable(false);
            mStudentDialog.setIcon(R.drawable.onair);
            mStudentDialog.show();
            //Preparing Shift data from server
            StringRequest stringShiftRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            parseStudentData(response);

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
            MySingleton.getInstance(this).addToRequestQueue(stringShiftRequest);
        } else {
            Toast.makeText(SubjectWiseMarksEntryStudentList.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    void parseStudentData(String jsonString) {
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            StudentListAdapter mAdapter = new  StudentListAdapter(this, jsonArray, this);
            RecyclerView recyclerView = findViewById(R.id.recycler);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(mAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mStudentDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onStudentSelected(JSONObject result) {
        Intent intent = new Intent(SubjectWiseMarksEntryStudentList.this, SubjectWiseMarksEntryInputs.class);
        intent.putExtra("student", result.toString());
        intent.putExtra("SubjectID", SubjectID);
        startActivity(intent);
    }
}
