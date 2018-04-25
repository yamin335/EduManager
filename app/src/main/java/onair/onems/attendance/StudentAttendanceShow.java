package onair.onems.attendance;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
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
import onair.onems.models.AllStudentAttendanceModel;
import onair.onems.network.MySingleton;

public class StudentAttendanceShow extends AppCompatActivity {
    private ArrayList<AllStudentAttendanceModel> studentList;
    private AllStudentAttendanceModel selectedStudent = null;
    private TableView tableView;
    private ProgressDialog dialog;
    private Configuration config;
    private String AllStudentUrl = "";
    long InstituteID=0, ShiftID=0, MediumID=0, ClassID=0, DepartmentID=0, SectionID=0;
    int MonthID = 0;
    private SimpleTableHeaderAdapter simpleTableHeaderAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance_report_student_list);
        tableView = (TableView) findViewById(R.id.tableView);
        SharedPreferences sharedPre = PreferenceManager.getDefaultSharedPreferences(this);
        InstituteID=sharedPre.getLong("InstituteID",0);
        Intent intent = getIntent();
        ShiftID = intent.getLongExtra("ShiftID", 0);
        MediumID = intent.getLongExtra("MediumID", 0);
        ClassID = intent.getLongExtra("ClassID", 0);
        DepartmentID = intent.getLongExtra("DepartmentID", 0);
        SectionID = intent.getLongExtra("SectionID", 0);
        MonthID = intent.getIntExtra("MonthID", 0);

        AllStudentUrl=getString(R.string.baseUrl)+"/api/onEms/getHrmSubWiseAtdDetail/"+
                InstituteID+"/"+MediumID+"/"+ShiftID+"/"+ClassID+"/"+SectionID+"/"+DepartmentID;

        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading...");
        dialog.setMessage("Please Wait...");
        dialog.setCancelable(false);
        dialog.setIcon(R.drawable.onair);
        dialog.show();

        simpleTableHeaderAdapter = new SimpleTableHeaderAdapter(this, "SI", "Student Name", "ID", "Roll");
        simpleTableHeaderAdapter.setTextColor(ContextCompat.getColor(this, R.color.table_header_text));
        tableView.setHeaderAdapter(simpleTableHeaderAdapter);
        config = getResources().getConfiguration();
        int colorEvenRows = getResources().getColor(R.color.table_data_row_even);
        int colorOddRows = getResources().getColor(R.color.table_data_row_odd);
        tableView.setDataRowBackgroundProvider(TableDataRowBackgroundProviders.alternatingRowColors(colorEvenRows, colorOddRows));
        TableColumnWeightModel columnModel = new TableColumnWeightModel(4);
        columnModel.setColumnWeight(1, 2);
        tableView.setColumnModel(columnModel);

        AttendanceDataGetRequest();

        tableView.addDataClickListener(new TableDataClickListener() {
            @Override
            public void onDataClicked(int rowIndex, Object clickedData) {
                selectedStudent = studentList.get(rowIndex);
                Intent intent = new Intent(StudentAttendanceShow.this, StudentAttendanceAllDays.class);
                intent.putExtra("UserID", selectedStudent.getUserID());
                intent.putExtra("UserFullName", selectedStudent.getUserFullName());
                intent.putExtra("RollNo", selectedStudent.getRollNo());
                intent.putExtra("RFID", selectedStudent.getRFID());
                intent.putExtra("ShiftID", selectedStudent.getShiftID());
                intent.putExtra("MediumID", selectedStudent.getMediumID());
                intent.putExtra("ClassID", selectedStudent.getClassID());
                intent.putExtra("DepartmentID", selectedStudent.getDepartmentID());
                intent.putExtra("SectionID", selectedStudent.getSectionID());
                intent.putExtra("MonthID", MonthID);
                intent.putExtra("ImageUrl", selectedStudent.getImageUrl());
                startActivity(intent);
            }
        });

    }
    void parseAllStudentShowJsonData(String jsonString) {
        try {
            studentList = new ArrayList<>();
            JSONArray studentListJsonArray = new JSONArray(jsonString);
            JSONArray jsonArray = new JSONArray(jsonString);
            String[][] DATA_TO_SHOW = new String[jsonArray.length()][4];
            for(int i = 0; i < jsonArray.length(); ++i) {
                JSONObject studentJsonObject = studentListJsonArray.getJSONObject(i);
                AllStudentAttendanceModel singleStudent = new AllStudentAttendanceModel();
                singleStudent.setUserID(studentJsonObject.getString("UserID"));
                singleStudent.setUserFullName(studentJsonObject.getString("UserFullName"));
                singleStudent.setRFID(studentJsonObject.getString("RFID"));
                singleStudent.setRollNo(studentJsonObject.getString("RollNo"));
                singleStudent.setShiftID(studentJsonObject.getString("ShiftID"));
                singleStudent.setMediumID(studentJsonObject.getString("MediumID"));
                singleStudent.setClassID(studentJsonObject.getString("ClassID"));
                singleStudent.setDepartmentID(studentJsonObject.getString("DepartmentID"));
                singleStudent.setSectionID(studentJsonObject.getString("SectionID"));
                singleStudent.setImageUrl(studentJsonObject.getString("ImageUrl"));
                studentList.add(singleStudent);
                DATA_TO_SHOW[i][0]= String.valueOf((i+1));
                DATA_TO_SHOW[i][1]= singleStudent.getUserFullName();
                DATA_TO_SHOW[i][2]= singleStudent.getRFID();
                DATA_TO_SHOW [i][3]= singleStudent.getRollNo();
            }
            SimpleTableDataAdapter simpleTabledataAdapter = new SimpleTableDataAdapter(this, DATA_TO_SHOW);
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
        StringRequest stringRequest = new StringRequest(Request.Method.GET,AllStudentUrl ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        parseAllStudentShowJsonData(response);

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
