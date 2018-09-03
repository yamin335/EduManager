package onair.onems.result;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.view.LayoutInflater;
import android.view.View;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import onair.onems.R;
import onair.onems.Services.StaticHelperClass;
import onair.onems.customised.MyDividerItemDecoration;
import onair.onems.mainactivities.CommonToolbarParentActivity;
import onair.onems.network.MySingleton;

public class SubjectWiseResult extends CommonToolbarParentActivity implements SubjectWiseResultAdapter.SubjectWiseResultsAdapterListener{

    private List<JSONObject> subjectWiseResultList;
    private SubjectWiseResultAdapter mAdapter;
    private ProgressDialog mResultDialog, mGradeDialog;
    private String UserID, ShiftID, MediumID, ClassID, DepartmentID, SectionID, SessionID, ExamID, InstituteID;
    private boolean isFail = false;
    private JSONArray resultGradingSystem;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View childActivityLayout = inflater.inflate(R.layout.result_subject_wise, null);
        LinearLayout parentActivityLayout = findViewById(R.id.contentMain);
        parentActivityLayout.addView(childActivityLayout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

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

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        subjectWiseResultList = new ArrayList<>();
        mAdapter = new SubjectWiseResultAdapter(this, subjectWiseResultList, this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 10));
        recyclerView.setAdapter(mAdapter);

        ResultGradeDataGetRequest();
    }

    @Override
    public void onSubjectWiseResultSelected(JSONObject subjectWiseResul) {
        try {
            if(!subjectWiseResul.getString("SubjectName").equalsIgnoreCase("Total")) {
                ResultDetailsDialog customDialog = new ResultDetailsDialog(this, subjectWiseResul);
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
            subjectWiseResultList.clear();
            JSONArray resultJsonArray = new JSONArray(result);
            int totalMarks = 0;
            double totalGradePoint = 0.0;
            String totalGrade = "";
            int totalSubject = 0;

            for(int i = 0; i<resultJsonArray.length(); i++) {
                if(resultJsonArray.getJSONObject(i).getString("IsOptional").equalsIgnoreCase("1")
                        && (resultJsonArray.getJSONObject(i).getDouble("GradePoint")>2.0)) {

                    subjectWiseResultList.add(resultJsonArray.getJSONObject(i));
                    totalGradePoint += resultJsonArray.getJSONObject(i).getDouble("GradePoint")-2.0;
                    totalMarks+= resultJsonArray.getJSONObject(i).getInt("Total");
                    if(resultJsonArray.getJSONObject(i).getString("Grade").equalsIgnoreCase("F")) {
                        isFail = true;
                    }
                } else if(resultJsonArray.getJSONObject(i).getString("IsOptional").equalsIgnoreCase("0")){

                    totalSubject++;
                    subjectWiseResultList.add(resultJsonArray.getJSONObject(i));
                    totalGradePoint += resultJsonArray.getJSONObject(i).getDouble("GradePoint");
                    totalMarks+= resultJsonArray.getJSONObject(i).getInt("Total");
                    if(resultJsonArray.getJSONObject(i).getString("Grade").equalsIgnoreCase("F")) {
                        isFail = true;
                    }
                }
            }

            if(totalSubject!=0) {
                totalGradePoint/=totalSubject;
                totalGradePoint = new BigDecimal(totalGradePoint).setScale(2, RoundingMode.HALF_UP).doubleValue();
            }

            if(isFail) {
                totalGrade = "F";
            } else {
                totalGrade = getTotalGrade(totalGradePoint);
            }

            JSONObject totalResult = new JSONObject();
            totalResult.put("SubjectName", "Total");
            totalResult.put("Total",Integer.toString(totalMarks));
            totalResult.put("Grade", totalGrade);
            totalResult.put("GradePoint", totalGradePoint);
            if(resultJsonArray.length()!=0){
                subjectWiseResultList.add(totalResult);
            }
            mAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mResultDialog.dismiss();
    }

    private void ResultGradeDataGetRequest(){
        if (StaticHelperClass.isNetworkAvailable(this)) {
            String resultGradeDataGetUrl = getString(R.string.baseUrl)+"/api/onEms/getinsGradeForReport/"+InstituteID+"/"+MediumID+"/"+ClassID;
            mGradeDialog = new ProgressDialog(this);
            mGradeDialog.setTitle("Loading Grade Sheet...");
            mGradeDialog.setMessage("Please Wait...");
            mGradeDialog.setCancelable(false);
            mGradeDialog.setIcon(R.drawable.onair);
            mGradeDialog.show();

            //Preparing Shift data from server
            StringRequest stringResultRequest = new StringRequest(Request.Method.GET, resultGradeDataGetUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            prepareResultGradeSheet(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mGradeDialog.dismiss();
                    Toast.makeText(SubjectWiseResult.this,"Grade sheet not found!!! ",
                            Toast.LENGTH_LONG).show();
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
            Toast.makeText(this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void prepareResultGradeSheet(String result) {
        mGradeDialog.dismiss();
        try {
            resultGradingSystem = new JSONArray(result);
            ResultDataGetRequest();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getTotalGrade(double totalGradePoint){
        String totalGrade = "F";
        for (int i = 0; i<resultGradingSystem.length(); i++) {
            try {
                if(i==0 && totalGradePoint>=resultGradingSystem.getJSONObject(i).getDouble("GPA")) {
                    return resultGradingSystem.getJSONObject(i).getString("GradeName");
                } else if(i>0 && i<(resultGradingSystem.length()-1) &&
                        totalGradePoint>=resultGradingSystem.getJSONObject(i).getDouble("GPA") &&
                        totalGradePoint<resultGradingSystem.getJSONObject(i-1).getDouble("GPA")) {
                    return resultGradingSystem.getJSONObject(i).getString("GradeName");
                } else if(i==(resultGradingSystem.length()-1) &&
                        totalGradePoint<=resultGradingSystem.getJSONObject(i).getDouble("GPA") &&
                        totalGradePoint<resultGradingSystem.getJSONObject(i-1).getDouble("GPA")) {
                    return resultGradingSystem.getJSONObject(i).getString("GradeName");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return totalGrade;
    }
}
