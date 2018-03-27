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
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.model.TableColumnWeightModel;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import de.codecrafters.tableview.toolkit.TableDataRowBackgroundProviders;
import onair.onems.R;
import onair.onems.network.MySingleton;

public class AllStudentSubjectWiseAttendance extends AppCompatActivity {
    private ProgressDialog dialog;
    private Configuration config;
    private TableView tableView;
    private SimpleTableHeaderAdapter simpleTableHeaderAdapter;
    private String subjectWiseAttendanceUrl;
    long InstituteID=0, SectionID=0, ClassID=0, MediumID=0, ShiftID=0, DepartmentID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_subject_wise_attendance);

        SharedPreferences sharedPre = PreferenceManager.getDefaultSharedPreferences(this);
        InstituteID = sharedPre.getLong("InstituteID", 0);

        tableView = (TableView) findViewById(R.id.tableView);

        simpleTableHeaderAdapter = new SimpleTableHeaderAdapter(this, "Subject Name", "Code ", "Status", "Teacher");
        simpleTableHeaderAdapter.setTextColor(ContextCompat.getColor(this, R.color.table_header_text));
        tableView.setHeaderAdapter(simpleTableHeaderAdapter);

        // Loding Show
        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading...");
        dialog.setMessage("Please Wait...");
        dialog.setCancelable(false);
        dialog.setIcon(R.drawable.onair);
        dialog.show();

        //Loding show end code
        // get Internal Data using SharedPreferences
        Intent intent = getIntent();
        ShiftID = intent.getLongExtra("ShiftID", 0);
        MediumID = intent.getLongExtra("MediumID", 0);
        ClassID = intent.getLongExtra("ClassID", 0);
        DepartmentID = intent.getLongExtra("DepartmentID", 0);
        SectionID = intent.getLongExtra("SectionID", 0);
        String UserID = intent.getStringExtra("UserID");
        String Date = intent.getStringExtra("Date");

        // get Internal Data using SharedPreferences end
        subjectWiseAttendanceUrl = getString(R.string.baseUrl) + "/api/onEms/getHrmSubWiseAtdByStudentID/" + ShiftID + "/" + MediumID + "/" + ClassID + "/" + SectionID +"/"+DepartmentID+ "/" + UserID + "/" + Date+"/"+InstituteID;

        int colorEvenRows = getResources().getColor(R.color.table_data_row_even);
        int colorOddRows = getResources().getColor(R.color.table_data_row_odd);
        tableView.setDataRowBackgroundProvider(TableDataRowBackgroundProviders.alternatingRowColors(colorEvenRows, colorOddRows));

        TableColumnWeightModel columnModel = new TableColumnWeightModel(4);
        columnModel.setColumnWeight(3, 5);
        columnModel.setColumnWeight(1, 3);
        columnModel.setColumnWeight(2, 4);
        columnModel.setColumnWeight(0, 4);
        tableView.setColumnModel(columnModel);

        config = getResources().getConfiguration();
        if (config.smallestScreenWidthDp > 320) {
            simpleTableHeaderAdapter.setTextSize(14);

        } else {
            simpleTableHeaderAdapter.setTextSize(10);

        }
        AttendanceDataGetRequest();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Intent myIntent = new Intent(getApplicationContext(), AllStudentAttendanceShow.class);
        startActivityForResult(myIntent, 0);
        finish();
        return true;

    }

    void parseSubjectWiseAttendanceJsonData(String jsonString) {
        try {
            JSONArray subjectWiseAttendanceJsonArray = new JSONArray(jsonString);
            String[][] DATA_TO_SHOW = new String[subjectWiseAttendanceJsonArray.length()][4];
            for (int i = 0; i < subjectWiseAttendanceJsonArray.length(); ++i) {

                JSONObject subjectWiseAttendanceJsonObject = subjectWiseAttendanceJsonArray.getJSONObject(i);
                DATA_TO_SHOW[i][0] = subjectWiseAttendanceJsonObject.getString("Subject");
                DATA_TO_SHOW[i][1] = String.valueOf(subjectWiseAttendanceJsonObject.getInt("SubjectID"));
                int status = subjectWiseAttendanceJsonObject.getInt("Status");
                if (status == 1) {
                    DATA_TO_SHOW[i][2] = "Present";
                } else
                    DATA_TO_SHOW[i][2] = "Absent";
                DATA_TO_SHOW[i][3] = subjectWiseAttendanceJsonObject.getString("ClassTeacher");
            }

            SimpleTableDataAdapter simpleTabledataAdapter = new SimpleTableDataAdapter(this, DATA_TO_SHOW);
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
            if (config.smallestScreenWidthDp > 320) {
                simpleTableHeaderAdapter.setTextSize(14);
                simpleTabledataAdapter.setTextSize(12);
            } else {
                simpleTableHeaderAdapter.setTextSize(10);
                simpleTabledataAdapter.setTextSize(10);
            }
            dialog.dismiss();

        } catch (Exception e) {
            Toast.makeText(this, "" + e, Toast.LENGTH_LONG).show();
            dialog.dismiss();
        }
    }

    public void AttendanceDataGetRequest(){
        StringRequest stringSubjectWiseAttendanceRequest = new StringRequest(Request.Method.GET, subjectWiseAttendanceUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseSubjectWiseAttendanceJsonData(response);
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
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Authorization", "Request_From_onEMS_Android_app");
                return params;
            }
        };
        MySingleton.getInstance(this).addToRequestQueue(stringSubjectWiseAttendanceRequest);
    }
}
