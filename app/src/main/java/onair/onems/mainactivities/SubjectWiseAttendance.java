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
import org.json.JSONObject;

import java.util.ArrayList;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.model.TableColumnWeightModel;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import de.codecrafters.tableview.toolkit.TableDataRowBackgroundProviders;
import onair.onems.R;
import onair.onems.attendance.StudentAttendanceReport;

public class SubjectWiseAttendance extends AppCompatActivity
{
    TableView tableView;
    ProgressDialog dialog;
    Configuration config;
    SharedPreferences sharedPre;
    long sectionID,classID,shiftID,mediumID;
    String userID,day,subjectWiseAttendanceUrl;
    SimpleTableDataAdapter simpleTabledataAdapter;
    SimpleTableHeaderAdapter simpleTableHeaderAdapter;
    String[][] DATA_TO_SHOW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subject_wise_attendance);
        tableView = (TableView) findViewById(R.id.tableView);
        sharedPre = PreferenceManager.getDefaultSharedPreferences(this);
        userID=sharedPre.getString("UserID","");
        shiftID=sharedPre.getLong("ShiftID",0);
        mediumID=sharedPre.getLong("MediumID",0);
        classID=sharedPre.getLong("ClassID",0);
        sectionID=sharedPre.getLong("SectionID",0);
        day=sharedPre.getString("Date","");

        subjectWiseAttendanceUrl=getString(R.string.baseUrl)+"getHrmSubWiseAtdByStudentID/"+shiftID+"/"+mediumID+"/"+classID+"/"+sectionID+"/"+userID+"/"+day;
        simpleTableHeaderAdapter = new SimpleTableHeaderAdapter(this, "Subject ", "Code ", "Status", "Teacher");
        simpleTableHeaderAdapter.setTextColor(ContextCompat.getColor(this, R.color.table_header_text));
        tableView.setHeaderAdapter(simpleTableHeaderAdapter);
        config = getResources().getConfiguration();
        RequestQueue queueSubjectWiseAttendance = Volley.newRequestQueue(this);
        StringRequest stringSubjectWiseAttendanceRequest = new StringRequest(Request.Method.GET, subjectWiseAttendanceUrl,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {

                       parseSubjectWiseAttendanceJsonData(response);



                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queueSubjectWiseAttendance.add(stringSubjectWiseAttendanceRequest);



    }
    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), StudentAttendanceReport.class);
        startActivityForResult(myIntent, 0);
        finish();
        return true;

    }
    void parseSubjectWiseAttendanceJsonData(String jsonString) {
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            ArrayList al = new ArrayList();
            DATA_TO_SHOW = new String[jsonArray.length()][4];
            for(int i = 0; i < jsonArray.length(); ++i)
            {

                JSONObject jsonObject=jsonArray.getJSONObject(i);
                DATA_TO_SHOW [i][0]=jsonObject.getString("Subject");
                DATA_TO_SHOW [i][1]= String.valueOf(jsonObject.getInt("SubjectID"));
                int status=jsonObject.getInt("Status");
                if(status==1)
                {
                    DATA_TO_SHOW[i][2]="Present";
                }
                else
                    DATA_TO_SHOW[i][2]="Absent";
                DATA_TO_SHOW [i][3]=jsonObject.getString("ClassTeacher");

                al.add(DATA_TO_SHOW[i][0]);
                al.add(DATA_TO_SHOW[i][1]);
                al.add(DATA_TO_SHOW[i][2]);
            }

            simpleTabledataAdapter = new SimpleTableDataAdapter(this,DATA_TO_SHOW);
            tableView.setDataAdapter(simpleTabledataAdapter);
            int colorEvenRows = getResources().getColor(R.color.table_data_row_even);
            int colorOddRows = getResources().getColor(R.color.table_data_row_odd);
            tableView.setDataRowBackgroundProvider(TableDataRowBackgroundProviders.alternatingRowColors(colorEvenRows, colorOddRows));
            TableColumnWeightModel columnModel = new TableColumnWeightModel(4);
            columnModel.setColumnWeight(3, 5);
            columnModel.setColumnWeight(1, 3);
            columnModel.setColumnWeight(2, 4);
            columnModel.setColumnWeight(0, 4);
            tableView.setColumnModel(columnModel);
            if (config.smallestScreenWidthDp >320) {
                simpleTableHeaderAdapter.setTextSize(14);
                simpleTabledataAdapter.setTextSize(12);
            } else {
                simpleTableHeaderAdapter.setTextSize(10);
                simpleTabledataAdapter.setTextSize(10);
            }



        } catch (Exception e) {
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
        }


    }


}
