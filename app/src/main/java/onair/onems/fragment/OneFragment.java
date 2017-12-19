package onair.onems.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
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
    String monthUrl = "";
    ProgressDialog dialog;
    JSONArray monthJsonArray;
    private static final String[][] DATA_TO_SHOW = {{ "01", "1/10/10", "Yes", "" },{ "02", "2/10/10", "Yes", "20" } ,{ "01", "12/10/10(weekend)", "", "" },{ "02", "13/10/10", "No", " " },{ "03", "14/10/10", "Yes", "20" },{ "04", "15/11/10", "Yes", "" },{ "05", "15/10/11", "Yes", "5" },{ "01", "12/10/10(weekend)", "Yes", "" },{ "02", "13/10/10", "No", " " },{ "03", "14/10/10", "Yes", "20" },{ "04", "15/11/10", "Yes", "" },{ "05", "15/10/11(Holiday)", "", "" },{ "01", "12/10/10(weekend)", "Yes", "" },{ "02", "13/10/10", "No", " " },{ "03", "14/10/10", "Yes", "20" },{ "04", "15/11/10", "Yes", "" },{ "05", "15/10/11", "Yes", "5" },{ "01", "12/10/10(weekend)", "Yes", "" },{ "02", "13/10/10", "No", " " },{ "03", "14/10/10", "Yes", "20" },{ "04", "15/11/10", "Yes", "" },{ "05", "15/10/11", "Yes", "5" },{ "01", "12/10/10(weekend)", "Yes", "" },{ "02", "13/10/10", "No", " " },
            { "03", "14/10/10", "Yes", "20" },{ "04", "15/11/10", "Yes", "" },{ "05", "15/10/11", "Yes", "5" },{ "01", "12/10/10(weekend)", "", "" },{ "02", "13/10/10", "No", " " },{ "03", "14/10/10", "Yes", "20" },{ "04", "15/11/10", "Yes", "" },{ "05", "15/10/11", "Yes", "5" }};
    public OneFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        rootView = inflater.inflate(R.layout.self_attendance, container, false);
        tableView = (TableView)rootView.findViewById(R.id.tableView);
        tableView.setColumnCount(4);
        monthUrl=monthUrl+getString(R.string.baseUrl)+"getMonth";

        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Loading....");
        dialog.show();
        final SimpleTableHeaderAdapter simpleTableHeaderAdapter = new SimpleTableHeaderAdapter(getActivity(),"SI","Date","Present", "Late(m)");
        simpleTableHeaderAdapter.setTextColor(ContextCompat.getColor(getActivity(), R.color.table_header_text ));
        tableView.setHeaderAdapter(simpleTableHeaderAdapter);
        final SimpleTableDataAdapter simpleTabledataAdapter = new SimpleTableDataAdapter(getActivity(),DATA_TO_SHOW);
        tableView.setDataAdapter(simpleTabledataAdapter);
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
                Intent i = new Intent(getActivity(), SubjectWiseAttendance.class);
                startActivity(i);
            }
        });
        Configuration config = getResources().getConfiguration();
         if (config.smallestScreenWidthDp >320) {
            simpleTableHeaderAdapter.setTextSize(14);
            simpleTabledataAdapter.setTextSize(12);
        } else {
            simpleTableHeaderAdapter.setTextSize(10);
            simpleTabledataAdapter.setTextSize(10);
        }
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, monthUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        parseJsonData(response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.dismiss();
            }
        });

        queue.add(stringRequest);
        spinner = (MaterialSpinner) rootView.findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
               // Snackbar.make(view, "Clicked " + item, Snackbar.LENGTH_LONG).show();
            }
        });

        return rootView;
    }
    void parseJsonData(String jsonString) {
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            ArrayList al = new ArrayList();
            for(int i = 0; i < jsonArray.length(); ++i) {
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                String name=jsonObject.getString("MonthName");
                al.add(name);
            }
            ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, al);
            spinner.setAdapter(adapter);
            spinner.setSelectedIndex(1);
        } catch (JSONException e) {
            Toast.makeText(getActivity(),""+e,Toast.LENGTH_LONG).show();
        }

        dialog.dismiss();
    }
}