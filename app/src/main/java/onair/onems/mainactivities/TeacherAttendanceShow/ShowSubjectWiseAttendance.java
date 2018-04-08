package onair.onems.mainactivities.TeacherAttendanceShow;

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

/**
 * Created by hp on 1/2/2018.
 */

public class ShowSubjectWiseAttendance extends AppCompatActivity
{

    ProgressDialog dialog;
    Configuration config;
    SharedPreferences sharedPre;
    TableView tableView;
    SimpleTableHeaderAdapter simpleTableHeaderAdapter;
    SimpleTableDataAdapter simpleTabledataAdapter;
    String[][] DATA_TO_SHOW;
    String subjectWiseAttendanceUrl;
    String RFID = "", monthUrl = "", monthAttendanceUrl = "", studentName = "", studentRFID = "", studentRoll = "", UserID = "", day = "";
    long InstituteID = 0;
    int SectionID = 0, ClassID = 0, MediumID = 0, ShiftID = 0, MonthID = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance_report_subjectwise);
        tableView = (TableView) findViewById(R.id.tableView);

        simpleTableHeaderAdapter = new SimpleTableHeaderAdapter(this, "Subject Name", "Code ", "Status", "Teacher");
        simpleTableHeaderAdapter.setTextColor(ContextCompat.getColor(this, R.color.table_header_text));
        tableView.setHeaderAdapter(simpleTableHeaderAdapter);

        // Loding Show
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading....");
        dialog.show();
        //Loding show end code

        // get Internal Data using SharedPreferences

        sharedPre = PreferenceManager.getDefaultSharedPreferences(this);
        InstituteID = sharedPre.getLong("InstituteID", 0);
        ShiftID = sharedPre.getInt("ShiftSelectID", 0);
        MediumID = sharedPre.getInt("MediumSelectID", 0);
        ClassID = sharedPre.getInt("ClassSelectID", 0);
        SectionID = sharedPre.getInt("SectionSelectID", 0);
        UserID = sharedPre.getString("SelectUserID", "");
        day = sharedPre.getString("SelectDate", "");

        // get Internal Data using SharedPreferences end
        subjectWiseAttendanceUrl = getString(R.string.baseUrl) + "getHrmSubWiseAtdByStudentID/" + ShiftID + "/" + MediumID + "/" + ClassID + "/" + SectionID + "/" + UserID + "/" + day;


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
                dialog.dismiss();
            }
        });

        queueSubjectWiseAttendance.add(stringSubjectWiseAttendanceRequest);


    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Intent myIntent = new Intent(getApplicationContext(), DateAttendance.class);
        startActivityForResult(myIntent, 0);
        finish();
        return true;

    }

    void parseSubjectWiseAttendanceJsonData(String jsonString) {
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            ArrayList al = new ArrayList();
            DATA_TO_SHOW = new String[jsonArray.length()][4];
            for (int i = 0; i < jsonArray.length(); ++i) {

                JSONObject jsonObject = jsonArray.getJSONObject(i);
                DATA_TO_SHOW[i][0] = jsonObject.getString("Subject");
                DATA_TO_SHOW[i][1] = String.valueOf(jsonObject.getInt("SubjectID"));
                int status = jsonObject.getInt("Status");
                if (status == 1) {
                    DATA_TO_SHOW[i][2] = "Present";
                } else
                    DATA_TO_SHOW[i][2] = "Absent";
                DATA_TO_SHOW[i][3] = jsonObject.getString("ClassTeacher");

                al.add(DATA_TO_SHOW[i][0]);
                al.add(DATA_TO_SHOW[i][1]);
                al.add(DATA_TO_SHOW[i][2]);
            }

            simpleTabledataAdapter = new SimpleTableDataAdapter(this, DATA_TO_SHOW);
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
}
