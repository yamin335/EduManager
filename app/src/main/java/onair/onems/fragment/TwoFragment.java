package onair.onems.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dd.morphingbutton.MorphingButton;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import de.codecrafters.tableview.TableView;
import onair.onems.R;
import onair.onems.mainactivities.StudentAttendanceShow;
//import onair.onems.Student_Attendance_Show;


public class TwoFragment extends Fragment {
    private View rootView;
    MaterialSpinner classSpinner,sectionSpinner,mediumSpinner,monthSpinner,shiftSpinner;
    TableView tableView;
    String classUrl = "",mediumUrl="",monthUrl="",sectionUrl="",shiftUrl;
    ProgressDialog dialog;
    Fragment fragment;
    FragmentManager fragmentManager;
    Handler handler;
    Button student_find_button;
    private boolean shouldRefreshOnResume = false;
    private static int SPLASH_TIME_OUT = 1000;
    Runnable refresh;

    private static final String[][] DATA_TO_SHOW = { { "01", "Hasnat", "01", "01" },{ "01", "Hasnat", "01", "01" },{ "01", "Hasnat", "01", "01" },{ "01", "Hasnat", "01", "01" },{ "01", "Hasnat", "01", "01" },
            { "02", "Bony", "02", "02" },{"03","Jony","03","03"},{"04","Yamin","04","04"},{"05","Alamin","05","05"},{"05","Alamin","05","05"},{"05","Alamin","05","05"},{"05","Alamin","05","05"},{"05","Alamin","05","05"},{"05","Alamin","05","05"},{"05","Alamin","05","05"},{"05","Alamin","05","05"},{"05","Alamin","05","05"} };
    public TwoFragment() {
        // Required empty public constructor
    }
    public static TwoFragment newInstance() {
        TwoFragment fragment = new TwoFragment();
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {


        rootView = inflater.inflate(R.layout.others_attendence, container, false);
        classSpinner = (MaterialSpinner) rootView.findViewById(R.id.spinner_Class);
        shiftSpinner = (MaterialSpinner) rootView.findViewById(R.id.spinner_Shift);
        mediumSpinner= (MaterialSpinner) rootView.findViewById(R.id.spinner_medium);
        sectionSpinner = (MaterialSpinner) rootView.findViewById(R.id.spinner_Section);
        monthSpinner = (MaterialSpinner) rootView.findViewById(R.id.spinner_Month);

        classUrl=getString(R.string.baseUrl)+"getAllClasses";
        mediumUrl=getString(R.string.baseUrl)+"getMedium";
        monthUrl=getString(R.string.baseUrl)+"getMonth";
        sectionUrl=getString(R.string.baseUrl)+"getCmnSection";
        shiftUrl=getString(R.string.baseUrl)+"getShift";
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Loading....");
        dialog.show();
        student_find_button=(Button) rootView.findViewById(R.id.show_button);
               student_find_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), StudentAttendanceShow.class);
                startActivity(i);
                getActivity().finish();

            }
        });
        RequestQueue queueClass = Volley.newRequestQueue(getActivity());

        StringRequest stringClassRequest = new StringRequest(Request.Method.GET, classUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        parseClassJsonData(response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.dismiss();
            }
        });

         queueClass.add(stringClassRequest);
        RequestQueue queueMedium = Volley.newRequestQueue(getActivity());

        StringRequest stringMediumRequest = new StringRequest(Request.Method.GET, mediumUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        parseMediumJsonData(response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.dismiss();
            }
        });

        queueMedium.add(stringMediumRequest);
        RequestQueue queueMonth = Volley.newRequestQueue(getActivity());

        StringRequest stringMonthRequest = new StringRequest(Request.Method.GET, monthUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        parseMonthJsonData(response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.dismiss();
            }
        });

        queueMonth.add(stringMonthRequest);
        RequestQueue queueSection = Volley.newRequestQueue(getActivity());

        StringRequest stringSectionRequest = new StringRequest(Request.Method.GET, sectionUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        parseSectionJsonData(response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.dismiss();
            }
        });

        queueSection.add(stringSectionRequest);
        RequestQueue queueShift = Volley.newRequestQueue(getActivity());

        StringRequest stringShiftRequest = new StringRequest(Request.Method.GET, shiftUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        parseShiftJsonData(response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.dismiss();
            }
        });
        queueShift.add(stringShiftRequest);
        tableView = (TableView)rootView.findViewById(R.id.tableView);
        classSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
            }
        });
        return rootView;
    }

    void parseClassJsonData(String jsonString) {
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            ArrayList al = new ArrayList();
            for(int i = 0; i < jsonArray.length(); ++i) {
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                String name=jsonObject.getString("ClassName");
                al.add(name);
            }
            ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, al);
            classSpinner.setAdapter(adapter);
            //spinner.setSelectedIndex(1);
        } catch (JSONException e) {
            Toast.makeText(getActivity(),""+e,Toast.LENGTH_LONG).show();
            dialog.dismiss();
        }


    }
    void parseMediumJsonData(String jsonString) {
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            ArrayList al = new ArrayList();
            for(int i = 0; i < jsonArray.length(); ++i) {
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                String name=jsonObject.getString("MameName");
                al.add(name);
            }
            ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, al);
            mediumSpinner.setAdapter(adapter);
            //spinner.setSelectedIndex(1);
        } catch (JSONException e) {
            Toast.makeText(getActivity(),""+e,Toast.LENGTH_LONG).show();
            dialog.dismiss();

        }

    }
    void parseMonthJsonData(String jsonString) {
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            ArrayList al = new ArrayList();
            for(int i = 0; i < jsonArray.length(); ++i) {
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                String name=jsonObject.getString("MonthName");
                al.add(name);
            }
            ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, al);
            monthSpinner.setAdapter(adapter);

            //spinner.setSelectedIndex(1);
        } catch (JSONException e) {
            Toast.makeText(getActivity(),""+e,Toast.LENGTH_LONG).show();
            dialog.dismiss();

        }

    }
    void parseSectionJsonData(String jsonString) {
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            ArrayList al = new ArrayList();
            for(int i = 0; i < jsonArray.length(); ++i) {
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                String name=jsonObject.getString("SectionName");
                al.add(name);
            }
            ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, al);
            sectionSpinner.setAdapter(adapter);

        } catch (JSONException e) {
            Toast.makeText(getActivity(),""+e,Toast.LENGTH_LONG).show();
            dialog.dismiss();

        }

    }
    void parseShiftJsonData(String jsonString) {
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            ArrayList al = new ArrayList();
            for(int i = 0; i < jsonArray.length(); ++i) {
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                String name=jsonObject.getString("ShiftName");
                al.add(name);
            }
            ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, al);
            shiftSpinner.setAdapter(adapter);
            //spinner.setSelectedIndex(1);
        } catch (JSONException e) {
            Toast.makeText(getActivity(),""+e,Toast.LENGTH_LONG).show();


        }
        dialog.dismiss();
    }
    @Override
    public void onStop() {
        super.onStop();
        shouldRefreshOnResume = true;
    }

}
