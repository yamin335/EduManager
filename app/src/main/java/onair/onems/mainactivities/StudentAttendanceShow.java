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
import onair.onems.R;

/**
 * Created by hp on 12/4/2017.
 */

public class StudentAttendanceShow extends AppCompatActivity {
    TableView tableView;
    SharedPreferences sharedPre;
    ProgressDialog dialog;
    Configuration config;

    String[][] DATA_TO_SHOW;
    String AllStudentUrl = "";
    String studentNameList[],studentRFIDList[],studentRollList[], studentUserList[];;
    String selectStudentName="",selectStudentRFID="",getSelectRoll="";
    double percent[];
    long InstituteID=0;
    int SectionID=0,ClassID=0,MediumID=0,ShiftID=0;
    SimpleTableHeaderAdapter simpleTableHeaderAdapter;
    SimpleTableDataAdapter simpleTabledataAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_student_attendance_show);
        tableView = (TableView) findViewById(R.id.tableView);

        sharedPre = PreferenceManager.getDefaultSharedPreferences(this);
        InstituteID=sharedPre.getLong("InstituteID",0);
        ShiftID=sharedPre.getInt("ShiftSelectID",0);
        MediumID=sharedPre.getInt("MediumSelectID",0);
        ClassID=sharedPre.getInt("ClassSelectID",0);
        SectionID=sharedPre.getInt("SectionSelectID",0);

        AllStudentUrl=getString(R.string.baseUrl)+"getHrmSubWiseAtdDetail/"+InstituteID+"/"+MediumID+"/"+ShiftID+"/"+ClassID+"/"+SectionID;
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading....");
        dialog.show();

        simpleTableHeaderAdapter = new SimpleTableHeaderAdapter(this, "SI", "Student Name", "ID", "Roll");
        simpleTableHeaderAdapter.setTextColor(ContextCompat.getColor(this, R.color.table_header_text));
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


        RequestQueue queue = Volley.newRequestQueue(this);

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
        });

        queue.add(stringRequest);
        tableView.addDataClickListener(new TableDataClickListener() {
            @Override
            public void onDataClicked(int rowIndex, Object clickedData)
            {
                SharedPreferences.Editor editor = sharedPre.edit();
                editor.putString("SelectStudentName",studentNameList[rowIndex]);
                editor.putString("SelectStudentRFID",studentRFIDList[rowIndex]);
                editor.putString("SelectStudentRoll",studentRollList[rowIndex]);
                editor.putString("SelectUserID",studentUserList[rowIndex]);
                editor.commit();
                Intent i = new Intent(StudentAttendanceShow.this, AllStudentAttendanceShow.class);
                startActivity(i);
            }
        });

    }
    void parseAllStudentShowJsonData(String jsonString) {
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            ArrayList al = new ArrayList();
            DATA_TO_SHOW = new String[jsonArray.length()][4];
            studentNameList=new String[jsonArray.length()];
            studentRFIDList=new String[jsonArray.length()];
            studentRollList=new String[jsonArray.length()];
            studentUserList=new String[jsonArray.length()];
            for(int i = 0; i < jsonArray.length(); ++i) {
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                DATA_TO_SHOW[i][0]= String.valueOf((i+1));
                DATA_TO_SHOW[i][1]=jsonObject.getString("UserFullName");
                DATA_TO_SHOW[i][2]=jsonObject.getString("RFID");
                DATA_TO_SHOW [i][3]=jsonObject.getString("RollNo");
                studentUserList[i]=jsonObject.getString("UserID");
                studentNameList[i]=DATA_TO_SHOW[i][1];
                studentRFIDList[i]=DATA_TO_SHOW[i][2];
                studentRollList[i]=DATA_TO_SHOW[i][3];

                studentUserList[i]=jsonObject.getString("UserID");

                al.add(DATA_TO_SHOW[i][0]);
                al.add(DATA_TO_SHOW[i][1]);
                al.add(DATA_TO_SHOW[i][2]);
                al.add(DATA_TO_SHOW[i][3]);
            }

            simpleTabledataAdapter = new SimpleTableDataAdapter(this,DATA_TO_SHOW);
            tableView.setDataAdapter(simpleTabledataAdapter);
            if (config.smallestScreenWidthDp >320)
            {
                simpleTableHeaderAdapter.setTextSize(14);
                simpleTabledataAdapter.setTextSize(12);
            }
            else
                {
                simpleTableHeaderAdapter.setTextSize(10);
                simpleTabledataAdapter.setTextSize(10);
            }



        } catch (JSONException e)
        {
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
        }

        dialog.dismiss();
    }
//    public boolean onOptionsItemSelected(MenuItem item){
//        Intent myIntent = new Intent(getApplicationContext(), StudentAttendance.class);
//        startActivityForResult(myIntent, 0);
//        finish();
//        return true;
//
//    }


}
