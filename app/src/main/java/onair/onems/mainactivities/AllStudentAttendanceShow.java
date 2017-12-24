package onair.onems.mainactivities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

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
import de.hdodenhof.circleimageview.CircleImageView;
import onair.onems.R;

/**
 * Created by hp on 12/5/2017.
 */

public class AllStudentAttendanceShow extends AppCompatActivity
{
    TableView tableView;
    SimpleTableHeaderAdapter simpleTableHeaderAdapter;
    SimpleTableDataAdapter simpleTabledataAdapter;
    String[][] DATA_TO_SHOW;
    SharedPreferences sharedPre;
    Configuration config;
    ProgressDialog dialog;
    String RFID="",monthUrl = "",monthAttendanceUrl="",studentName="",studentRFID="",studentRoll="";
    int InstituteID=0,SectionID=0,ClassID=0,MediumID=0,ShiftID=0,MonthID=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.other_attendance_show);
        tableView = (TableView) findViewById(R.id.tableView);

        // Loding Show
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading....");
        dialog.show();
        //Loding show end code

        // get Internal Data using SharedPreferences

        sharedPre = PreferenceManager.getDefaultSharedPreferences(this);
        InstituteID=sharedPre.getInt("InstituteID",0);
        ShiftID=sharedPre.getInt("ShiftSelectID",0);
        MediumID=sharedPre.getInt("MediumSelectID",0);
        ClassID=sharedPre.getInt("ClassSelectID",0);
        SectionID=sharedPre.getInt("SectionSelectID",0);
        MonthID=sharedPre.getInt("MonthSelectID",0);
        RFID=sharedPre.getString("SelectStudentRFID","");

        // get Internal Data using SharedPreferences end

        monthAttendanceUrl=getString(R.string.baseUrl)+"getStudentMonthlyDeviceAttendance/"+ShiftID+"/"+MediumID+"/"+ClassID+"/"+SectionID+"/"+MonthID+"/"+RFID;
       // Toast.makeText(this,""+monthAttendanceUrl,Toast.LENGTH_LONG).show();
        // Add Header of The Table

        simpleTableHeaderAdapter = new SimpleTableHeaderAdapter(this, "SI","Date","Present", "Late(m)");
        simpleTableHeaderAdapter.setTextColor(ContextCompat.getColor(this, R.color.table_header_text));
        tableView.setHeaderAdapter(simpleTableHeaderAdapter);

        // Header of The Table End

        // Set colour of the Even and Odd row of the table
        int colorEvenRows = getResources().getColor(R.color.table_data_row_even);
        int colorOddRows = getResources().getColor(R.color.table_data_row_odd);
        tableView.setDataRowBackgroundProvider(TableDataRowBackgroundProviders.alternatingRowColors(colorEvenRows, colorOddRows));

        // colour of the Even and Odd row of the table End

        // Fixed Weight of the Column
        TableColumnWeightModel columnModel = new TableColumnWeightModel(4);

        columnModel.setColumnWeight(1, 2);

        tableView.setColumnModel(columnModel);

        // Fixed Weight of the Column End

        // Configure Size of different Mobile Device

        config = getResources().getConfiguration();
        if (config.smallestScreenWidthDp > 320) {
            simpleTableHeaderAdapter.setTextSize(14);

        } else {
            simpleTableHeaderAdapter.setTextSize(10);

        }

        // Configure Size of different Mobile Device End

       //Table View Click Event
        tableView.addDataClickListener(new TableDataClickListener() {
            @Override
            public void onDataClicked(int rowIndex, Object clickedData)
            {
                SharedPreferences.Editor editor = sharedPre.edit();
                editor.putString("SelectDate", DATA_TO_SHOW[rowIndex][1]);
                editor.commit();
                Intent i = new Intent(AllStudentAttendanceShow.this,AllStudentSubjectWiseAttendance.class);
                startActivity(i);
            }
        });
        //Table View Click Event  End


        // Loding http Monthly Attendance string file using volley

        RequestQueue queue = Volley.newRequestQueue(this);

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

        // Loding http Monthly Attendance string file using volley

    }
    // Back parent Page code
    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), StudentAttendanceShow.class);
        startActivityForResult(myIntent, 0);
        finish();
        return true;

    }

    // Back parent Page code end

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

            simpleTabledataAdapter = new SimpleTableDataAdapter(this,DATA_TO_SHOW);
            tableView.setDataAdapter(simpleTabledataAdapter);
            if (config.smallestScreenWidthDp >320) {
                simpleTableHeaderAdapter.setTextSize(14);
                simpleTabledataAdapter.setTextSize(12);
            } else {
                simpleTableHeaderAdapter.setTextSize(10);
                simpleTabledataAdapter.setTextSize(10);
            }



        } catch (JSONException e) {
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
        }

        dialog.dismiss();
    }

}
