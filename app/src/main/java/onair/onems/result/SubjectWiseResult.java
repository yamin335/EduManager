package onair.onems.result;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import java.util.List;
import java.util.Map;

import onair.onems.R;
import onair.onems.Services.StaticHelperClass;
import onair.onems.customadapters.CustomDialog;
import onair.onems.customadapters.MyDividerItemDecoration;
import onair.onems.customadapters.SubjectWiseResultAdapter;
import onair.onems.network.MySingleton;

public class SubjectWiseResult extends AppCompatActivity implements SubjectWiseResultAdapter.SubjectWiseResultsAdapterListener{

    private RecyclerView recyclerView;
    private List<JSONObject> subjectWiseResultList;
    private SubjectWiseResultAdapter mAdapter;
    private ProgressDialog mResultDialog;
    private String UserID, ShiftID, MediumID, ClassID, DepartmentID, SectionID, SessionID, ExamID, InstituteID;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_subject_wise);

        Intent intent = getIntent();
        UserID = intent.getStringExtra("UserID");
        ShiftID = intent.getStringExtra("ShiftID");
        MediumID = intent.getStringExtra("MediumID");
        ClassID = intent.getStringExtra("ClassID");
        DepartmentID = intent.getStringExtra("DepartmentID");
        SectionID = intent.getStringExtra("SectionID");
        SessionID = intent.getStringExtra("SessionID");
        ExamID = intent.getStringExtra("ExamID");
        InstituteID = intent.getStringExtra("InstituteID");

        if(!StaticHelperClass.isNetworkAvailable(this))
        {
            Toast.makeText(this,"Please check your internet connection and open app again!!! ",Toast.LENGTH_LONG).show();
        }

        recyclerView = findViewById(R.id.recycler_view);


        ResultDataGetRequest();

    }

    @Override
    public void onSubjectWiseResultSelected(JSONObject subjectWiseResul) {
        try {
            if(!subjectWiseResul.getString("SubjectName").equalsIgnoreCase("Total")) {
                CustomDialog customDialog = new CustomDialog(this, subjectWiseResul);
                customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                customDialog.setCancelable(false);
                customDialog.show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void ResultDataGetRequest() {
        if (StaticHelperClass.isNetworkAvailable(this)) {

            String resultDataGetUrl = getString(R.string.baseUrl)+"/api/onEms/SubjectWiseMarksByStudent/"
                    +UserID+"/"+InstituteID+"/"+ClassID+"/"+SectionID+"/"+DepartmentID+"/"+MediumID
                    +"/"+ShiftID+"/"+SessionID+"/"+ExamID;

            mResultDialog = new ProgressDialog(this);
            mResultDialog.setTitle("Loading session...");
            mResultDialog.setMessage("Please Wait...");
            mResultDialog.setCancelable(false);
            mResultDialog.setIcon(R.drawable.onair);

            //Preparing Shift data from server
            StringRequest stringResultRequest = new StringRequest(Request.Method.GET, resultDataGetUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            prepareResult(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mResultDialog.dismiss();
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
            MySingleton.getInstance(this).addToRequestQueue(stringResultRequest);
        } else {
            Toast.makeText(SubjectWiseResult.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void prepareResult(String result) {
        try {
            subjectWiseResultList = new ArrayList<>();
            JSONArray resultJsonArray = new JSONArray(result);
            int totalMarks = 0;
            double totalGradePoint = 0.0;
            String totalGrade = "";
            int totalSubject = 0;

            for(int i = 0; i<resultJsonArray.length(); i++) {
                if(resultJsonArray.getJSONObject(i).getString("IsOptional").equalsIgnoreCase("true")
                        && (resultJsonArray.getJSONObject(i).getDouble("GradePoint")>2.0)) {

                    subjectWiseResultList.add(resultJsonArray.getJSONObject(i));
                    totalGradePoint += resultJsonArray.getJSONObject(i).getDouble("GradePoint")-2.0;
                    totalMarks+= resultJsonArray.getJSONObject(i).getInt("Total");

                } else if(resultJsonArray.getJSONObject(i).getString("IsOptional").equalsIgnoreCase("false")){

                    totalSubject++;
                    subjectWiseResultList.add(resultJsonArray.getJSONObject(i));
                    totalGradePoint += resultJsonArray.getJSONObject(i).getDouble("GradePoint");
                    totalMarks+= resultJsonArray.getJSONObject(i).getInt("Total");

                }
            }

            totalGradePoint/=totalSubject;

            if((totalGradePoint>5.0)||(totalGradePoint==5.0)) {
                totalGrade = "A+";
            } else if((totalGradePoint>=4.0)&&(totalGradePoint<5.0)) {
                totalGrade = "A";
            }else if((totalGradePoint>=3.5)&&(totalGradePoint<4.0)) {
                totalGrade = "A-";
            }else if((totalGradePoint>=3.0)&&(totalGradePoint<3.5)) {
                totalGrade = "B";
            }else if((totalGradePoint>=2.0)&&(totalGradePoint<3.0)) {
                totalGrade = "C";
            }else if((totalGradePoint>=1.0)&&(totalGradePoint<2.0)) {
                totalGrade = "D";
            }else {
                totalGrade = "F";
            }
            JSONObject totalResult = new JSONObject();
            totalResult.put("SubjectName", "Total");
            totalResult.put("Total",Integer.toString(totalMarks));
            totalResult.put("Grade", totalGrade);
            totalResult.put("GradePoint", totalGradePoint);
            subjectWiseResultList.add(totalResult);

            mAdapter = new SubjectWiseResultAdapter(this, subjectWiseResultList, this);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.addItemDecoration(new MyDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 10));
            recyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
            mResultDialog.dismiss();
        } catch (JSONException e) {
            e.printStackTrace();
            mResultDialog.dismiss();
        }
    }
}