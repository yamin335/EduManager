package onair.onems.fees_report;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import onair.onems.R;
import onair.onems.Services.StaticHelperClass;
import onair.onems.mainactivities.CommonToolbarParentActivity;
import onair.onems.network.MySingleton;

public class FeeCollectionReportList extends CommonToolbarParentActivity {

    private int  ReportType;
    private String url = "";

    private ProgressDialog mDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View childActivityLayout = inflater.inflate(R.layout.fee_collection_report_list, null);
        LinearLayout parentActivityLayout = findViewById(R.id.contentMain);
        parentActivityLayout.addView(childActivityLayout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        Intent intent = getIntent();
        ReportType = intent.getIntExtra("Report Type", 0);
        switch (ReportType) {
            case 1: url = getString(R.string.baseUrl)+"/api/onEms/GetAccFeesCollectionReport/"+intent.getStringExtra("InstituteID")
                    +"/"+intent.getStringExtra("BranchID")+"/"+intent.getStringExtra("MediumID")
                    +"/"+intent.getStringExtra("ClassID")+"/"+intent.getStringExtra("DepartmentID")
                    +"/"+intent.getStringExtra("SectionID")+"/"+intent.getStringExtra("ShiftID")
                    +"/"+"0"+"/"+"0"+"/"+intent.getStringExtra("MonthID")+"/"+intent.getStringExtra("StatusID")
                    +"/"+intent.getStringExtra("SessionID");
                Objects.requireNonNull(getSupportActionBar()).setTitle("Fee Collection Report");
                break;
            case 2: url = getString(R.string.baseUrl)+"/api/onEms/GetAccFeesCollectionReportTopSheet/"+intent.getStringExtra("InstituteID")
                    +"/"+intent.getStringExtra("BranchID")+"/"+intent.getStringExtra("MediumID")
                    +"/"+intent.getStringExtra("ClassID")+"/"+intent.getStringExtra("DepartmentID")
                    +"/"+intent.getStringExtra("SectionID")+"/"+intent.getStringExtra("ShiftID")
                    +"/"+"0"+"/"+"0"+"/"+intent.getStringExtra("MonthID")+"/"+intent.getStringExtra("StatusID")
                    +"/"+intent.getStringExtra("SessionID");
                Objects.requireNonNull(getSupportActionBar()).setTitle("Fee Summary Report");
                break;
        }
        ReportDataGetRequest(url);
    }

    private void ReportDataGetRequest(String url) {
        if (StaticHelperClass.isNetworkAvailable(this)) {

            mDialog = new ProgressDialog(this);
            mDialog.setTitle("Loading Data...");
            mDialog.setMessage("Please Wait...");
            mDialog.setCancelable(false);
            mDialog.setIcon(R.drawable.onair);
            mDialog.show();
            //Preparing Shift data from server
            StringRequest stringShiftRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            parseJsonData(response);

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
            MySingleton.getInstance(this).addToRequestQueue(stringShiftRequest);
        } else {
            Toast.makeText(FeeCollectionReportList.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    void parseJsonData(String jsonString) {
        RecyclerView recyclerView = findViewById(R.id.recycler);
        FeeCollectionReportListAdapter mAdapter = new FeeCollectionReportListAdapter(this, jsonString, ReportType);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        mDialog.dismiss();
    }
}
