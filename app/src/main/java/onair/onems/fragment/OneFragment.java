package onair.onems.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.listeners.TableDataClickListener;
import de.codecrafters.tableview.model.TableColumnWeightModel;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import de.codecrafters.tableview.toolkit.TableDataRowBackgroundProviders;
import onair.onems.R;
import onair.onems.mainactivities.SubjectWiseAttendance;
//import onair.onems.Subject_Wish_Attendance;


public class OneFragment extends Fragment {
    private View rootView;
    TableView tableView;
    MaterialSpinner spinner;
    ListView monthList;
    String monthUrl = "",monthAttendanceUrl="";
    String RFID;
    ProgressDialog dialog;
    Configuration config;
    SharedPreferences sharedPre;
    int sectionID,classID,shiftID,mediumID,monthselectindex;
    JSONArray monthJsonArray;
    SimpleTableHeaderAdapter simpleTableHeaderAdapter;
    SimpleTableDataAdapter simpleTabledataAdapter;

    int MonthID[];
    String[][] DATA_TO_SHOW;

    public OneFragment()
    {

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

        rootView = inflater.inflate(R.layout.self_attendance, container, false);
        tableView = (TableView)rootView.findViewById(R.id.tableView);
        tableView.setColumnCount(4);

         monthUrl=getString(R.string.baseUrl)+"getMonth";

         sharedPre = PreferenceManager.getDefaultSharedPreferences(getActivity());

         RFID=sharedPre.getString("RFID","");
         shiftID=sharedPre.getInt("ShiftID",0);
         mediumID=sharedPre.getInt("MediumID",0);
         classID=sharedPre.getInt("ClassID",0);
         sectionID=sharedPre.getInt("SectionID",0);
         monthselectindex=sharedPre.getInt("monthselectindex",0);

        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Loading....");
        dialog.show();

        simpleTableHeaderAdapter = new SimpleTableHeaderAdapter(getActivity(),"SI","Date","Present", "Late(m)");

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
                SharedPreferences.Editor editor = sharedPre.edit();
                editor.putString("Date", DATA_TO_SHOW[rowIndex][1]);
                editor.putInt("monthselectindex",monthselectindex);
                editor.commit();
                //Toast.makeText(getActivity(),""+DATA_TO_SHOW[rowIndex][1],Toast.LENGTH_LONG).show();
                Intent i = new Intent(getActivity(), SubjectWiseAttendance.class);
                startActivity(i);

            }
        });


        RequestQueue queueMonth = Volley.newRequestQueue(getActivity());
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
        });

        queueMonth.add(stringMonthRequest);

        spinner = (MaterialSpinner) rootView.findViewById(R.id.spinner);

        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>()
        {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item)
            {
                int monthid=MonthID[position];
                monthselectindex=position;
                SharedPreferences.Editor editor = sharedPre.edit();
                editor.putInt("monthselectindex",monthselectindex);
                editor.commit();
                monthAttendanceUrl=getString(R.string.baseUrl)+"getStudentMonthlyDeviceAttendance/"+shiftID+"/"+mediumID+"/"+classID+"/"+sectionID+"/"+monthid+"/"+RFID;
                RequestQueue queue = Volley.newRequestQueue(getActivity());

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
                });

                queue.add(stringRequest);

            }
        });

        return rootView;
    }
    void parseMonthJsonData(String jsonString) {
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            ArrayList al = new ArrayList();
            MonthID=new int[jsonArray.length()];
            for(int i = 0; i < jsonArray.length(); ++i)
            {
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                String name=jsonObject.getString("MonthName");
                MonthID[i]=jsonObject.getInt("MonthID");
                al.add(name);
            }

            ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, al);
            spinner.setAdapter(adapter);
            spinner.setSelectedIndex(monthselectindex);

            monthAttendanceUrl=getString(R.string.baseUrl)+"getStudentMonthlyDeviceAttendance/"+shiftID+"/"+mediumID+"/"+classID+"/"+sectionID+"/"+ MonthID[monthselectindex]+"/"+RFID;
            RequestQueue queue = Volley.newRequestQueue(getActivity());

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
            });

            queue.add(stringRequest);
            dialog.dismiss();
        } catch (JSONException e) {
            Toast.makeText(getActivity(),""+e,Toast.LENGTH_LONG).show();
            dialog.dismiss();

        }

    }
    void parseMonthlyAttendanceJsonData(String jsonString) {
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            ArrayList al = new ArrayList();
            DATA_TO_SHOW = new String[jsonArray.length()][4];
            for(int i = 0; i < jsonArray.length(); ++i) {
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                DATA_TO_SHOW[i][0]= String.valueOf((i+1));
                DATA_TO_SHOW [i][1]=jsonObject.getString("Date");
                int status=jsonObject.getInt("Present");
                if(status==1)
                {
                    DATA_TO_SHOW[i][2]="YES";
                }
                else
                    DATA_TO_SHOW[i][2]="NO";
                DATA_TO_SHOW [i][3]=jsonObject.getString("Late");

                al.add(DATA_TO_SHOW[i][0]);
                al.add(DATA_TO_SHOW[i][1]);
                al.add(DATA_TO_SHOW[i][2]);
                al.add(DATA_TO_SHOW[i][3]);
            }

            simpleTabledataAdapter = new SimpleTableDataAdapter(getActivity(),DATA_TO_SHOW);
            tableView.setDataAdapter(simpleTabledataAdapter);
            if (config.smallestScreenWidthDp >320) {
                simpleTableHeaderAdapter.setTextSize(14);
                simpleTabledataAdapter.setTextSize(12);
            } else {
                simpleTableHeaderAdapter.setTextSize(10);
                simpleTabledataAdapter.setTextSize(10);
            }



        }
        catch (JSONException e)
        {
            Toast.makeText(getActivity(),""+e,Toast.LENGTH_LONG).show();
        }

        dialog.dismiss();
    }
}