package onair.onems.attendance;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;
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
import onair.onems.models.DailyAttendanceModel;
import onair.onems.network.MySingleton;

public class StudentAttendanceAllDays extends AppCompatActivity
{
    private ArrayList<DailyAttendanceModel> dailyAttendanceList;
    private DailyAttendanceModel selectedDay;
    private TableView tableView;
    private SimpleTableHeaderAdapter simpleTableHeaderAdapter;
    private Configuration config;
    private ProgressDialog dialog;
    private String RFID="", monthAttendanceUrl="", UserFullName="", RollNo="", UserID = "";
    private long InstituteID=0, SectionID=0, ClassID=0, MediumID=0, ShiftID=0, DepartmentID = 0;
    private int MonthID=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance_report_all_days);
        tableView = (TableView) findViewById(R.id.tableView);
        TextView name=(TextView) findViewById(R.id.name);
        TextView roll=(TextView) findViewById(R.id.roll);
        TextView id=(TextView) findViewById(R.id.Id);

        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading...");
        dialog.setMessage("Please Wait...");
        dialog.setCancelable(false);
        dialog.setIcon(R.drawable.onair);
        dialog.show();

        //Loding show end code

        dailyAttendanceList = new ArrayList<>();
        selectedDay = new DailyAttendanceModel();
        // get Internal Data using SharedPreferences
        SharedPreferences sharedPre = PreferenceManager.getDefaultSharedPreferences(this);
        InstituteID = sharedPre.getLong("InstituteID",0);

        Intent intent = getIntent();
        ShiftID = intent.getLongExtra("ShiftID", 0);
        MediumID = intent.getLongExtra("MediumID", 0);
        ClassID = intent.getLongExtra("ClassID", 0);
        DepartmentID = intent.getLongExtra("DepartmentID", 0);
        SectionID = intent.getLongExtra("SectionID", 0);
        MonthID = intent.getIntExtra("MonthID", 0);
        RFID = intent.getStringExtra("RFID");
        UserFullName = intent.getStringExtra("UserFullName");
        RollNo = intent.getStringExtra("RollNo");
        UserID = intent.getStringExtra("UserID");
        // get Internal Data using SharedPreferences end
        name.setText(""+UserFullName);
        roll.setText("Roll: "+RollNo);
        id.setText("ID: "+RFID);

        monthAttendanceUrl = getString(R.string.baseUrl)+"/api/onEms/getStudentMonthlyDeviceAttendance/"+
                ShiftID+"/"+MediumID+"/"+ClassID+"/"+SectionID+"/"+DepartmentID+"/"+MonthID+"/"+UserID+"/"+InstituteID;
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
            public void onDataClicked(int rowIndex, Object clickedData) {
                selectedDay = dailyAttendanceList.get(rowIndex);
                Intent intent = new Intent(StudentAttendanceAllDays.this, StudentSubjectWiseAttendance.class);
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
        //Table View Click Event  End

        // Loding http Monthly Attendance string file using volley

        AttendanceDataGetRequest();

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

            SimpleTableDataAdapter simpleTabledataAdapter = new SimpleTableDataAdapter(this,DATA_TO_SHOW);
            tableView.setDataAdapter(simpleTabledataAdapter);
            if (config.smallestScreenWidthDp >320) {
                simpleTableHeaderAdapter.setTextSize(14);
                simpleTabledataAdapter.setTextSize(12);
            } else {
                simpleTableHeaderAdapter.setTextSize(10);
                simpleTabledataAdapter.setTextSize(10);
            }
        } catch (JSONException e) {
            dialog.dismiss();
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
        }
        dialog.dismiss();
    }

    public void AttendanceDataGetRequest(){
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
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

}
