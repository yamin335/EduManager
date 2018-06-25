package onair.onems.result;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
import onair.onems.network.MySingleton;

public class SelfResult extends Fragment implements View.OnTouchListener,
        ResultExamAdapter.ExamAdapterListener, SubjectWiseResultAdapter.SubjectWiseResultsAdapterListener{
    private ProgressDialog mExamDialog;
    public int UserTypeID;
    public long InstituteID, UserShiftID, UserMediumID, UserClassID,
            UserDepartmentID, UserSectionID, UserSessionID;
    public String UserID;
    private float dX;
    private float dY;
    private int lastAction;
    private List<JSONObject> subjectWiseResultList;
    private SubjectWiseResultAdapter mAdapter;
    private ProgressDialog mResultDialog, mGradeDialog;
    private boolean isFail = false;
    private JSONArray resultGradingSystem;
    private String ExamID = "";
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        UserTypeID = prefs.getInt("UserTypeID",0);
        InstituteID = prefs.getLong("InstituteID",0);
        UserID = prefs.getString("UserID", "0");
        UserShiftID = prefs.getLong("ShiftID",0);
        UserMediumID = prefs.getLong("MediumID",0);
        UserClassID = prefs.getLong("ClassID",0);
        UserDepartmentID = prefs.getLong("SDepartmentID",0);
        UserSectionID = prefs.getLong("SectionID",0);
        UserSessionID = prefs.getLong("SessionID",0);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.result_subject_wise_self, container, false);
        FloatingActionButton floatingActionButton = rootView.findViewById(R.id.selectExam);
        floatingActionButton.setOnTouchListener(this);

        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);
        subjectWiseResultList = new ArrayList<>();
        mAdapter = new SubjectWiseResultAdapter(getActivity(), subjectWiseResultList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL, 10));
        recyclerView.setAdapter(mAdapter);

        examDataGetRequest();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onExamSelected(JSONObject exam) {
        try {
            ExamID = exam.getString("ExamID");
            ResultGradeDataGetRequest();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                dX = view.getX() - event.getRawX();
                dY = view.getY() - event.getRawY();
                lastAction = MotionEvent.ACTION_DOWN;
                break;

            case MotionEvent.ACTION_MOVE:
                view.setY(event.getRawY() + dY);
                view.setX(event.getRawX() + dX);
                lastAction = MotionEvent.ACTION_MOVE;
                break;

            case MotionEvent.ACTION_UP:
                if (lastAction == MotionEvent.ACTION_DOWN){
                    examDataGetRequest();
                }
                break;

            default:
                return false;
        }
        return true;
    }

    @Override
    public void onSubjectWiseResultSelected(JSONObject subjectWiseResul) {
        try {
            if(!subjectWiseResul.getString("SubjectName").equalsIgnoreCase("Total")) {
                ResultDetailsDialog customDialog = new ResultDetailsDialog(getActivity(), subjectWiseResul);
                customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                customDialog.setCancelable(false);
                customDialog.show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void examDataGetRequest() {
        if (StaticHelperClass.isNetworkAvailable(getActivity())) {
            String examUrl = "";
            if(UserTypeID == 3){
            examUrl = getString(R.string.baseUrl)+"/api/onEms/getInsExamforDDL/"+InstituteID+
                    "/"+UserMediumID+"/"+UserClassID;
            } else if(UserTypeID == 5) {
                try {
                    JSONObject selectedStudent = new JSONObject(getActivity().getSharedPreferences("CURRENT_STUDENT", Context.MODE_PRIVATE)
                            .getString("guardianSelectedStudent", "{}"));
                    examUrl = getString(R.string.baseUrl)+"/api/onEms/getInsExamforDDL/"+InstituteID+
                            "/"+selectedStudent.getString("MediumID")+"/"+selectedStudent.getString("ClassID");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            mExamDialog = new ProgressDialog(getActivity());
            mExamDialog.setTitle("Loading exam...");
            mExamDialog.setMessage("Please Wait...");
            mExamDialog.setCancelable(false);
            mExamDialog.setIcon(R.drawable.onair);
            mExamDialog.show();

            //Preparing exam
            StringRequest request = new StringRequest(Request.Method.GET, examUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            parseExamData(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mExamDialog.dismiss();
                    Toast.makeText(getActivity(),"Exam data not found!!! ",
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
            MySingleton.getInstance(getActivity()).addToRequestQueue(request);
        } else {
            Toast.makeText(getActivity(),"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void parseExamData(String examData) {
        if(!examData.equalsIgnoreCase("[]")) {
            ResultExamSelectionDialog resultExamSelectionDialog = new ResultExamSelectionDialog(getActivity(), this, examData, getActivity());
            resultExamSelectionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            resultExamSelectionDialog.setCancelable(false);
            resultExamSelectionDialog.show();
        } else {
            Toast.makeText(getActivity(),"Exam data not found!!! ",
                    Toast.LENGTH_LONG).show();
        }
        mExamDialog.dismiss();
    }

    private void ResultDataGetRequest(String ExamID) {
        if (StaticHelperClass.isNetworkAvailable(getActivity())) {
            String resultDataGetUrl = "";
            if(UserTypeID == 3){
                resultDataGetUrl = getString(R.string.baseUrl)+"/api/onEms/SubjectWiseMarksByStudent/"
                        +UserID+"/"+InstituteID+"/"+UserClassID+"/"+UserSectionID+"/"+UserDepartmentID+"/"+UserMediumID
                        +"/"+UserShiftID+"/"+UserSessionID+"/"+ExamID;
            } else if(UserTypeID == 5) {
                try {
                    JSONObject selectedStudent = new JSONObject(getActivity().getSharedPreferences("CURRENT_STUDENT", Context.MODE_PRIVATE)
                            .getString("guardianSelectedStudent", "{}"));
                    resultDataGetUrl = getString(R.string.baseUrl)+"/api/onEms/SubjectWiseMarksByStudent/"
                            +selectedStudent.getString("UserID")+"/"+InstituteID+"/"+
                            selectedStudent.getString("ClassID")+"/"+selectedStudent.getString("SectionID")+"/"+
                            selectedStudent.getString("DepartmentID")+"/"+selectedStudent.getString("MediumID")
                            +"/"+selectedStudent.getString("ShiftID")+"/"+selectedStudent.getString("SessionID")+"/"+ExamID;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            mResultDialog = new ProgressDialog(getActivity());
            mResultDialog.setTitle("Loading result...");
            mResultDialog.setMessage("Please Wait...");
            mResultDialog.setCancelable(false);
            mResultDialog.setIcon(R.drawable.onair);
            mResultDialog.show();

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
                    Toast.makeText(getActivity(),"Result not found!!! ",
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
            MySingleton.getInstance(getActivity()).addToRequestQueue(stringResultRequest);
        } else {
            Toast.makeText(getActivity(),"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void prepareResult(String result) {
        try {
            subjectWiseResultList.clear();
            JSONArray resultJsonArray = new JSONArray(result);
            double totalMarks = 0.0;
            double totalGradePoint = 0.0;
            String totalGrade = "";
            int totalSubject = 0;

            for(int i = 0; i<resultJsonArray.length(); i++) {
                if(resultJsonArray.getJSONObject(i).getString("IsOptional").equalsIgnoreCase("1")
                        && (resultJsonArray.getJSONObject(i).getDouble("GradePoint")>2.0)) {

                    subjectWiseResultList.add(resultJsonArray.getJSONObject(i));
                    totalGradePoint += resultJsonArray.getJSONObject(i).getDouble("GradePoint")-2.0;
                    totalMarks+= resultJsonArray.getJSONObject(i).getDouble("Total");
                    if(resultJsonArray.getJSONObject(i).getString("Grade").equalsIgnoreCase("F")) {
                        isFail = true;
                    }
                } else if(resultJsonArray.getJSONObject(i).getString("IsOptional").equalsIgnoreCase("0")){

                    totalSubject++;
                    subjectWiseResultList.add(resultJsonArray.getJSONObject(i));
                    totalGradePoint += resultJsonArray.getJSONObject(i).getDouble("GradePoint");
                    totalMarks+= resultJsonArray.getJSONObject(i).getDouble("Total");
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
            if(resultJsonArray.length()>0) {
                totalResult.put("SubjectName", "Total");
                totalResult.put("Total", Double.toString(totalMarks));
                totalResult.put("Grade", totalGrade);
                totalResult.put("GradePoint", totalGradePoint);
                subjectWiseResultList.add(totalResult);
                mAdapter.notifyDataSetChanged();
            }
            else
            {
                totalResult.put("SubjectName", "");
                totalResult.put("Total", "");
                totalResult.put("Grade", "");
                totalResult.put("GradePoint", "");
                subjectWiseResultList.add(totalResult);
                mAdapter.notifyDataSetChanged();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        mResultDialog.dismiss();
    }

    private void ResultGradeDataGetRequest(){
        if (StaticHelperClass.isNetworkAvailable(getActivity())) {
            String resultGradeDataGetUrl = "";
            if(UserTypeID == 3){
                resultGradeDataGetUrl = getString(R.string.baseUrl)+"/api/onEms/getinsGradeForReport/"+InstituteID+"/"+UserMediumID+"/"+UserClassID;
            } else if(UserTypeID == 5) {
                try {
                    JSONObject selectedStudent = new JSONObject(getActivity().getSharedPreferences("CURRENT_STUDENT", Context.MODE_PRIVATE)
                            .getString("guardianSelectedStudent", "{}"));
                    resultGradeDataGetUrl = getString(R.string.baseUrl)+"/api/onEms/getinsGradeForReport/"+InstituteID+"/"+selectedStudent.getString("MediumID")+"/"+selectedStudent.getString("ClassID");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            mGradeDialog = new ProgressDialog(getActivity());
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
                    Toast.makeText(getActivity(),"Grade sheet not found!!! ",
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
            MySingleton.getInstance(getActivity()).addToRequestQueue(stringResultRequest);
        } else {
            Toast.makeText(getActivity(),"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void prepareResultGradeSheet(String result) {
        mGradeDialog.dismiss();
        try {
            resultGradingSystem = new JSONArray(result);
            ResultDataGetRequest(ExamID);
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
