package onair.onems.routine;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import onair.onems.R;
import onair.onems.Services.StaticHelperClass;
import onair.onems.customised.MyDividerItemDecoration;
import onair.onems.mainactivities.CommonToolbarParentActivity;
import onair.onems.network.MySingleton;

public class SubjectWiseExamRoutine extends CommonToolbarParentActivity {

    private String InstituteID;
    private String ExamRoutineID;
    private ProgressDialog mDialog;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View childActivityLayout = inflater.inflate(R.layout.exam_routine_subject_wise, null);
        LinearLayout parentActivityLayout = findViewById(R.id.contentMain);
        parentActivityLayout.addView(childActivityLayout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        InstituteID = getIntent().getStringExtra("InstituteID");
        ExamRoutineID = getIntent().getStringExtra("ExamRoutineID");

        recyclerView = findViewById(R.id.recycler_view);
        RoutineDataGetRequest();
    }

    private void RoutineDataGetRequest() {
        if (StaticHelperClass.isNetworkAvailable(this)) {

            String routineUrl = getString(R.string.baseUrl)+"/api/onEms/getExameRoutineDetail/"
                    +InstituteID+"/"+ExamRoutineID;

            mDialog = new ProgressDialog(this);
            mDialog.setTitle("Loading session...");
            mDialog.setMessage("Please Wait...");
            mDialog.setCancelable(false);
            mDialog.setIcon(R.drawable.onair);

            //Preparing Shift data from server
            StringRequest routineRequest = new StringRequest(Request.Method.GET, routineUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            prepareRoutine(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mDialog.dismiss();
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
            MySingleton.getInstance(this).addToRequestQueue(routineRequest);
        } else {
            Toast.makeText(SubjectWiseExamRoutine.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void prepareRoutine(String string) {
        try {
            ArrayList<JSONObject> subjectList = new ArrayList<>();
            JSONArray subjects = new JSONArray(string);
            for(int i = 0; i < subjects.length(); i++){
                JSONObject jsonObject = subjects.getJSONObject(i);
                if(jsonObject.getString("IsActive").equalsIgnoreCase("true")) {
                    subjectList.add(jsonObject);
                }
            }

            SubjectWiseExamRoutineAdapter mAdapter = new SubjectWiseExamRoutineAdapter(this, subjectList);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.addItemDecoration(new MyDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 10));
            recyclerView.setAdapter(mAdapter);
            mDialog.dismiss();

        } catch (JSONException e) {
            mDialog.dismiss();
            e.printStackTrace();
        }
    }
}
