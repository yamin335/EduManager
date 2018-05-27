package onair.onems.routine;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import onair.onems.R;
import onair.onems.Services.StaticHelperClass;
import onair.onems.network.MySingleton;

public class Wednesday extends Fragment {

    private long InstituteID, ShiftID, MediumID, ClassID, SDepartmentID, SectionID, UserTypeID;
    private RoutineAdapter mAdapter;
    private RecyclerView recyclerView;
    private String UserID;

    public Wednesday() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.routine_day_pager_item, container, false);
        recyclerView = rootView.findViewById(R.id.routinePeriods);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        InstituteID = prefs.getLong("InstituteID",0);
        ShiftID = prefs.getLong("ShiftID",0);
        MediumID = prefs.getLong("MediumID",0);
        ClassID = prefs.getLong("ClassID",0);
        SDepartmentID = prefs.getLong("SDepartmentID",0);
        SectionID = prefs.getLong("SectionID",0);
        UserTypeID = prefs.getInt("UserTypeID",0);
        UserID = prefs.getString("UserID","0");

        if(UserTypeID==1||UserTypeID==2||UserTypeID==4) {
            String routineData = getArguments().getString("wednesdayJsonArray");
            mAdapter = new RoutineAdapter(getActivity(), routineData, UserTypeID);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(mAdapter);
        } else if(UserTypeID==3||UserTypeID==5) {
            wednesdayRoutineDataGetRequest();
        }

        return  rootView;
    }

    void wednesdayRoutineDataGetRequest() {
        if(StaticHelperClass.isNetworkAvailable(getActivity())) {

            String wednesdayRoutineDataGetUrl = "";
            if(UserTypeID == 3) {
                wednesdayRoutineDataGetUrl = getString(R.string.baseUrl)+"/api/onEms/spGetDashClassRoutine/"+ShiftID
                        +"/"+MediumID+"/"+ClassID+"/"+SectionID+"/"+SDepartmentID+"/5/"+InstituteID;
            } else if(UserTypeID == 5) {
                try {
                    JSONObject selectedStudent = new JSONObject(getActivity().getSharedPreferences("CURRENT_STUDENT", Context.MODE_PRIVATE)
                            .getString("guardianSelectedStudent", "{}"));
                    wednesdayRoutineDataGetUrl = getString(R.string.baseUrl)+"/api/onEms/spGetDashClassRoutine/"+
                            selectedStudent.getString("ShiftID")+"/"+selectedStudent.getString("MediumID")
                            +"/"+selectedStudent.getString("ClassID")+"/"+selectedStudent.getString("SectionID")
                            +"/"+selectedStudent.getString("DepartmentID")+"/5/"+InstituteID;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            final ProgressDialog wednesdayRoutineGetDialog = new ProgressDialog(getActivity());
            wednesdayRoutineGetDialog.setTitle("Loading...");
            wednesdayRoutineGetDialog.setMessage("Please Wait...");
            wednesdayRoutineGetDialog.setCancelable(false);
            wednesdayRoutineGetDialog.setIcon(R.drawable.onair);
            wednesdayRoutineGetDialog.show();

            StringRequest wednesdayRoutineRequest = new StringRequest(Request.Method.GET, wednesdayRoutineDataGetUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            mAdapter = new RoutineAdapter(getActivity(), response, UserTypeID);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                            recyclerView.setLayoutManager(mLayoutManager);
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            recyclerView.setAdapter(mAdapter);

                            wednesdayRoutineGetDialog.dismiss();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    wednesdayRoutineGetDialog.dismiss();
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
            MySingleton.getInstance(getActivity()).addToRequestQueue(wednesdayRoutineRequest);
        } else {
            Toast.makeText(getActivity(),"Please check your internet connection !!!",Toast.LENGTH_LONG).show();
        }
    }
}
