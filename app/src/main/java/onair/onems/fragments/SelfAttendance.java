package onair.onems.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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
import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.listeners.TableDataClickListener;
import de.codecrafters.tableview.model.TableColumnWeightModel;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import de.codecrafters.tableview.toolkit.TableDataRowBackgroundProviders;
import onair.onems.R;
import onair.onems.mainactivities.AllStudentSubjectWiseAttendance;
import onair.onems.models.MonthModel;
import onair.onems.models.StudentAttendanceReportModels.DailyAttendanceModel;
import onair.onems.network.MySingleton;

public class SelfAttendance extends Fragment {
    private TableView tableView;
    private Spinner spinnerMonth;
    private String RFID="", UserID="";
    private ProgressDialog dialog;
    private Configuration config;
    private long SectionID, ClassID, ShiftID, MediumID, DepartmentID, InstituteID;
    private SimpleTableHeaderAdapter simpleTableHeaderAdapter;
    private ArrayList<MonthModel> allMonthArrayList;
    private String[] tempMonthArray = {"Select Month"};
    private MonthModel selectedMonth = null;
    private ArrayList<DailyAttendanceModel> dailyAttendanceList;
    private DailyAttendanceModel selectedDay;

    public SelfAttendance()
    {

    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.student_attendance_self_attendance, container, false);
        tableView = (TableView)rootView.findViewById(R.id.tableView);
        tableView.setColumnCount(4);

        SharedPreferences sharedPre = PreferenceManager.getDefaultSharedPreferences(getActivity());
        RFID=sharedPre.getString("RFID","");
        ShiftID=sharedPre.getLong("ShiftID",0);
        MediumID=sharedPre.getLong("MediumID",0);
        ClassID=sharedPre.getLong("ClassID",0);
        SectionID=sharedPre.getLong("SectionID",0);
        DepartmentID=sharedPre.getLong("DepartmentID",0);
        InstituteID=sharedPre.getLong("InstituteID",0);
        UserID = sharedPre.getString("UserID","");

        selectedMonth = new MonthModel();

        spinnerMonth = (Spinner)rootView.findViewById(R.id.spinnerMonth);
        ArrayAdapter<String> month_spinner_adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, tempMonthArray);
        month_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(month_spinner_adapter);
        if(!isNetworkAvailable())
        {
            Toast.makeText(getActivity(),"Please check your internet connection and open app again!!! ",Toast.LENGTH_LONG).show();
        }

        dialog = new ProgressDialog(getActivity());
        dialog.setTitle("Loading...");
        dialog.setMessage("Please Wait...");
        dialog.setCancelable(false);
        dialog.setIcon(R.drawable.onair);
        dialog.show();

        simpleTableHeaderAdapter = new SimpleTableHeaderAdapter(getActivity(),"SI","Date","Present", "Late(min)");

        simpleTableHeaderAdapter.setTextColor(ContextCompat.getColor(getActivity(), R.color.table_header_text ));
        tableView.setHeaderAdapter(simpleTableHeaderAdapter);
        config = getResources().getConfiguration();

        if (config.smallestScreenWidthDp >320) {
            simpleTableHeaderAdapter.setTextSize(14);
        } else {
            simpleTableHeaderAdapter.setTextSize(10);
        }
        int colorEvenRows = getResources().getColor(R.color.table_data_row_even);
        int colorOddRows = getResources().getColor(R.color.table_data_row_odd);
        tableView.setDataRowBackgroundProvider(TableDataRowBackgroundProviders.alternatingRowColors(colorEvenRows, colorOddRows));
        TableColumnWeightModel columnModel = new TableColumnWeightModel(4);
        columnModel.setColumnWeight(1, 2);
        tableView.setColumnModel(columnModel);

        tableView.addDataClickListener(new TableDataClickListener() {
            @Override
            public void onDataClicked(int rowIndex, Object clickedData)
            {
                selectedDay = dailyAttendanceList.get(rowIndex);
                Intent intent = new Intent(getActivity(), AllStudentSubjectWiseAttendance.class);
                intent.putExtra("UserID", UserID);
                intent.putExtra("ShiftID", ShiftID);
                intent.putExtra("MediumID", MediumID);
                intent.putExtra("ClassID", ClassID);
                intent.putExtra("DepartmentID", DepartmentID);
                intent.putExtra("SectionID", SectionID);
                intent.putExtra("Date", selectedDay.getDate());
                startActivity(intent);
            }
        });

        MonthDataGetRequest();

        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != 0)
                {
                    try {
                        selectedMonth = allMonthArrayList.get(position-1);
                        MonthlyAttendanceDataGetRequest(selectedMonth.getMonthID());
                    }
                    catch (IndexOutOfBoundsException e)
                    {
                        Toast.makeText(getActivity(),"No section found !!!",Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    selectedMonth = new MonthModel();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return rootView;
    }

    void parseMonthJsonData(String jsonString) {

        try {
            allMonthArrayList = new ArrayList<>();
            JSONArray MonthJsonArray = new JSONArray(jsonString);
            ArrayList<String> monthArrayList = new ArrayList<>();
            monthArrayList.add("Select Month");
            for(int i = 0; i < MonthJsonArray.length(); ++i)
            {
                JSONObject monthJsonObject = MonthJsonArray.getJSONObject(i);
                MonthModel monthModel = new MonthModel(monthJsonObject.getString("MonthID"), monthJsonObject.getString("MonthName"));
                allMonthArrayList.add(monthModel);
                monthArrayList.add(monthModel.getMonthName());
            }

            if(allMonthArrayList.size() >= 1){
                selectedMonth = allMonthArrayList.get(0);
                MonthlyAttendanceDataGetRequest(selectedMonth.getMonthID());
            }

            try {
                String[] strings = new String[monthArrayList.size()];
                strings = monthArrayList.toArray(strings);
                ArrayAdapter<String> month_spinner_adapter = new ArrayAdapter<>(getActivity(),R.layout.spinner_item, strings);
                month_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerMonth.setAdapter(month_spinner_adapter);
                dialog.dismiss();
            }
            catch (IndexOutOfBoundsException e)
            {
                dialog.dismiss();
                Toast.makeText(getActivity(),"No class found !!!",Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {
            Toast.makeText(getActivity(),""+e,Toast.LENGTH_LONG).show();
            dialog.dismiss();

        }

    }

    void parseMonthlyAttendanceJsonData(String jsonString) {
        try {
            dailyAttendanceList = new ArrayList<>();
            JSONArray dailyAttendanceJsonArray = new JSONArray(jsonString);
            String[][] DATA_TO_SHOW = new String[dailyAttendanceJsonArray.length()][4];
            for(int i = 0; i < dailyAttendanceJsonArray.length(); ++i) {
                JSONObject dailyAttendanceJsonObject = dailyAttendanceJsonArray.getJSONObject(i);
                DailyAttendanceModel perDayAttendance = new DailyAttendanceModel();
                perDayAttendance.setDate(dailyAttendanceJsonObject.getString("Date"));
                perDayAttendance.setPresent(dailyAttendanceJsonObject.getString("Present"));
                perDayAttendance.setLate(dailyAttendanceJsonObject.getString("Late"));
                perDayAttendance.setTotalClassDay(dailyAttendanceJsonObject.getString("TotalClassDay"));
                perDayAttendance.setTotalPresent(dailyAttendanceJsonObject.getString("TotalPresent"));
                DATA_TO_SHOW[i][0] = String.valueOf((i+1));
                DATA_TO_SHOW [i][1] = perDayAttendance.getDate();
                DATA_TO_SHOW[i][2] = perDayAttendance.getPresent() == 1 ? "YES" : "NO";
                DATA_TO_SHOW [i][3] = Integer.toString(perDayAttendance.getLate());
                dailyAttendanceList.add(perDayAttendance);
            }

            SimpleTableDataAdapter simpleTabledataAdapter = new SimpleTableDataAdapter(getActivity(),DATA_TO_SHOW);
            tableView.setDataAdapter(simpleTabledataAdapter);
            if (config.smallestScreenWidthDp >320) {
                simpleTableHeaderAdapter.setTextSize(14);
                simpleTabledataAdapter.setTextSize(12);
            } else {
                simpleTableHeaderAdapter.setTextSize(10);
                simpleTabledataAdapter.setTextSize(10);
            }
            dialog.dismiss();
        } catch (JSONException e) {
            dialog.dismiss();
            Toast.makeText(getActivity(),""+e,Toast.LENGTH_LONG).show();
        }
    }

    void MonthDataGetRequest(){
        if(isNetworkAvailable()) {
            dialog.show();
            String monthUrl=getString(R.string.baseUrl)+"/api/onEms/getMonth";
            StringRequest stringMonthRequest = new StringRequest(Request.Method.GET, monthUrl,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {

                            parseMonthJsonData(response);

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dialog.dismiss();
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
            MySingleton.getInstance(getActivity()).addToRequestQueue(stringMonthRequest);
        } else {
            Toast.makeText(getActivity(),"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    void MonthlyAttendanceDataGetRequest(int MonthID){
        if(isNetworkAvailable()) {
            dialog.show();
            String monthAttendanceUrl = getString(R.string.baseUrl)+"/api/onEms/getStudentMonthlyDeviceAttendance/"+
                    ShiftID+"/"+MediumID+"/"+ClassID+"/"+SectionID+"/"+DepartmentID+"/"+ MonthID+"/"+RFID+"/"+InstituteID;
            StringRequest stringRequest = new StringRequest(Request.Method.GET, monthAttendanceUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            parseMonthlyAttendanceJsonData(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dialog.dismiss();
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
            MySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(getActivity(),"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}