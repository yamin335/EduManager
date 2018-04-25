package onair.onems.result;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
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
import java.util.List;
import java.util.Map;

import onair.onems.R;
import onair.onems.Services.StaticHelperClass;
import onair.onems.customadapters.MyDividerItemDecoration;
import onair.onems.customadapters.ResultStructureAdapter;
import onair.onems.mainactivities.SideNavigationMenuParentActivity;
import onair.onems.mainactivities.StudentMainScreen;
import onair.onems.mainactivities.TeacherMainScreen;
import onair.onems.network.MySingleton;

public class ResultGradeStructure extends SideNavigationMenuParentActivity {

    private RecyclerView recyclerView;
    private ProgressDialog mResultDialog;
    private int UserTypeID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityName = ResultGradeStructure.class.getName();

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View childActivityLayout = inflater.inflate(R.layout.result_grade_structure, null);
        LinearLayout parentActivityLayout = (LinearLayout) findViewById(R.id.contentMain);
        parentActivityLayout.addView(childActivityLayout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        recyclerView = findViewById(R.id.recycler_view);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        long InstituteID = prefs.getLong("InstituteID",0);
        UserTypeID = prefs.getInt("UserTypeID",0);

        if (StaticHelperClass.isNetworkAvailable(this)) {

            String resultGradeDataGetUrl = getString(R.string.baseUrl)+"/api/onEms/getinsGradeForReport/"+InstituteID;

            mResultDialog = new ProgressDialog(this);
            mResultDialog.setTitle("Loading Grade Sheet...");
            mResultDialog.setMessage("Please Wait...");
            mResultDialog.setCancelable(false);
            mResultDialog.setIcon(R.drawable.onair);

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
            Toast.makeText(ResultGradeStructure.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void prepareResultGradeSheet(String result) {
        try {
            List<JSONObject> ResultGradeSheet = new ArrayList<>();
            JSONArray resultJsonArray = new JSONArray(result);
            for(int i = 0; i<resultJsonArray.length(); i++) {
                ResultGradeSheet.add(resultJsonArray.getJSONObject(i));
            }

            ResultStructureAdapter mAdapter = new ResultStructureAdapter(this, ResultGradeSheet);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.addItemDecoration(new MyDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 0));
            recyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
            mResultDialog.dismiss();
        } catch (JSONException e) {
            e.printStackTrace();
            mResultDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(UserTypeID == 1) {
            Intent mainIntent = new Intent(ResultGradeStructure.this,TeacherMainScreen.class);
            startActivity(mainIntent);
            finish();
        } else if(UserTypeID == 2) {
            Intent mainIntent = new Intent(ResultGradeStructure.this,TeacherMainScreen.class);
            startActivity(mainIntent);
            finish();
        } else if(UserTypeID == 3) {
            Intent mainIntent = new Intent(ResultGradeStructure.this,StudentMainScreen.class);
            startActivity(mainIntent);
            finish();
        } else if(UserTypeID == 4) {
            Intent mainIntent = new Intent(ResultGradeStructure.this,TeacherMainScreen.class);
            startActivity(mainIntent);
            finish();
        } else if(UserTypeID == 5) {
            Intent mainIntent = new Intent(ResultGradeStructure.this,StudentMainScreen.class);
            startActivity(mainIntent);
            finish();
        }
    }
}
