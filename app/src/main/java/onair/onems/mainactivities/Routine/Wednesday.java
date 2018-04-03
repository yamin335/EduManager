package onair.onems.mainactivities.Routine;

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
import java.util.HashMap;
import java.util.Map;
import onair.onems.R;
import onair.onems.customadapters.RoutineAdapter;
import onair.onems.network.MySingleton;

public class Wednesday extends Fragment {

    private long InstituteID, ShiftID, MediumID, ClassID, DepartmentID, SectionID;
    private RoutineAdapter mAdapter;
    private RecyclerView recyclerView;

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
        DepartmentID = prefs.getLong("DepartmentID",0);
        SectionID = prefs.getLong("SectionID",0);

        wednesdayRoutineDataGetRequest();

        return  rootView;
    }

    void wednesdayRoutineDataGetRequest() {
        if(isNetworkAvailable()) {

            String wednesdayRoutineDataGetUrl = getString(R.string.baseUrl)+"/api/onEms/spGetDashClassRoutine/"+ShiftID
                    +"/"+MediumID+"/"+ClassID+"/"+SectionID+"/"+DepartmentID+"/5/"+InstituteID;

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

                            mAdapter = new RoutineAdapter(getActivity(), response);
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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
