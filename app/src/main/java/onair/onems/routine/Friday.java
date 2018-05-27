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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import onair.onems.R;
import onair.onems.Services.StaticHelperClass;
import onair.onems.network.MySingleton;

public class Friday extends Fragment {

    private long InstituteID, ShiftID, MediumID, ClassID, SDepartmentID, SectionID, UserTypeID;
    private RoutineAdapter mAdapter;
    private RecyclerView recyclerView;
    private String UserID;

    public Friday() {

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
            String routineData = getArguments().getString("fridayJsonArray");
            mAdapter = new RoutineAdapter(getActivity(), routineData, UserTypeID);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(mAdapter);
        } else if(UserTypeID==3||UserTypeID==5) {
            fridayRoutineDataGetRequest();
        }

        return  rootView;
    }

    void fridayRoutineDataGetRequest() {
        if(StaticHelperClass.isNetworkAvailable(getActivity())) {

            String fridayRoutineDataGetUrl = "";
            if(UserTypeID == 3) {
                fridayRoutineDataGetUrl = getString(R.string.baseUrl)+"/api/onEms/spGetDashClassRoutine/"+ShiftID
                        +"/"+MediumID+"/"+ClassID+"/"+SectionID+"/"+SDepartmentID+"/7/"+InstituteID;
            } else if(UserTypeID == 5) {
                try {
                    JSONObject selectedStudent = new JSONObject(getActivity().getSharedPreferences("CURRENT_STUDENT", Context.MODE_PRIVATE)
                            .getString("guardianSelectedStudent", "{}"));
                    fridayRoutineDataGetUrl = getString(R.string.baseUrl)+"/api/onEms/spGetDashClassRoutine/"+
                            selectedStudent.getString("ShiftID")+"/"+selectedStudent.getString("MediumID")
                            +"/"+selectedStudent.getString("ClassID")+"/"+selectedStudent.getString("SectionID")
                            +"/"+selectedStudent.getString("DepartmentID")+"/7/"+InstituteID;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            final ProgressDialog fridayRoutineGetDialog = new ProgressDialog(getActivity());
            fridayRoutineGetDialog.setTitle("Loading...");
            fridayRoutineGetDialog.setMessage("Please Wait...");
            fridayRoutineGetDialog.setCancelable(false);
            fridayRoutineGetDialog.setIcon(R.drawable.onair);
            fridayRoutineGetDialog.show();

            StringRequest fridayRoutineRequest = new StringRequest(Request.Method.GET, fridayRoutineDataGetUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            mAdapter = new RoutineAdapter(getActivity(), response, UserTypeID);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                            recyclerView.setLayoutManager(mLayoutManager);
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            recyclerView.setAdapter(mAdapter);

                            fridayRoutineGetDialog.dismiss();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    fridayRoutineGetDialog.dismiss();
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
            MySingleton.getInstance(getActivity()).addToRequestQueue(fridayRoutineRequest);
        } else {
            Toast.makeText(getActivity(),"Please check your internet connection !!!",Toast.LENGTH_LONG).show();
        }
    }
}
