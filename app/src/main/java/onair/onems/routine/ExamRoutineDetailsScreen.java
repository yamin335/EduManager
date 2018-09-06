package onair.onems.routine;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
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

import java.util.HashMap;
import java.util.Map;
import onair.onems.R;
import onair.onems.Services.StaticHelperClass;
import onair.onems.customised.MyDividerItemDecoration;
import onair.onems.mainactivities.CommonToolbarParentActivity;
import onair.onems.network.MySingleton;

public class ExamRoutineDetailsScreen extends CommonToolbarParentActivity {

    private ProgressDialog mExamRoutineDialog;
    private RecyclerView recyclerView;
    private long MediumID = 0, ClassID = 0, SessionID = 0, ExamID = 0, InstituteID = 0;
    private JSONArray routine;
    private TextView className;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View childActivityLayout = inflater.inflate(R.layout.exam_routine_details_screen, null);
        LinearLayout parentActivityLayout = findViewById(R.id.contentMain);
        parentActivityLayout.addView(childActivityLayout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        Intent intent = getIntent();
        InstituteID = intent.getLongExtra("InstituteID", 0);
        MediumID = intent.getLongExtra("MediumID", 0);
        ClassID = intent.getLongExtra("ClassID", 0);;
        SessionID = intent.getLongExtra("SessionID", 0);;
        ExamID = intent.getLongExtra("ExamID", 0);;

        recyclerView = findViewById(R.id.routineClasses);
        className = findViewById(R.id.className);

        ExamRoutineDataGetRequest();
    }

    private void ExamRoutineDataGetRequest() {
        if (StaticHelperClass.isNetworkAvailable(this)) {
            String examRoutineUrl = getString(R.string.baseUrl)+"/api/onEms/spGetCommonClassExamRoutine/"+InstituteID
                    +"/"+ExamID+"/"+MediumID+"/"+ClassID+"/"+"0"+"/"+SessionID;

            mExamRoutineDialog = new ProgressDialog(this);
            mExamRoutineDialog.setTitle("Loading session...");
            mExamRoutineDialog.setMessage("Please Wait...");
            mExamRoutineDialog.setCancelable(false);
            mExamRoutineDialog.setIcon(R.drawable.onair);
            mExamRoutineDialog.show();
            //Preparing Shift data from server
            StringRequest stringExamRoutineRequest = new StringRequest(Request.Method.GET, examRoutineUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            parseExamRoutineJsonData(response);

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mExamRoutineDialog.dismiss();
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
            MySingleton.getInstance(this).addToRequestQueue(stringExamRoutineRequest);
        } else {
            Toast.makeText(ExamRoutineDetailsScreen.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    void parseExamRoutineJsonData(String examRoutine) {
        try {
            routine = new JSONArray(examRoutine);
            className.setText("Class: "+routine.getJSONObject(0).getString("ClassName"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ExamRoutineAdapter mAdapter = new ExamRoutineAdapter(this, routine);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 0));
        recyclerView.setAdapter(mAdapter);
        mExamRoutineDialog.dismiss();
    }
}
