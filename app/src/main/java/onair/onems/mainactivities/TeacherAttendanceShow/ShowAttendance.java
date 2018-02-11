package onair.onems.mainactivities.TeacherAttendanceShow;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import onair.onems.R;
import onair.onems.mainactivities.StudentAttendanceShow;

/**
 * Created by User on 12/26/2017.
 */

public class ShowAttendance extends AppCompatActivity {
    MaterialSpinner classSpinner,sectionSpinner,mediumSpinner,monthSpinner,shiftSpinner;
    TableView tableView;
    String classUrl = "",mediumUrl="",monthUrl="",sectionUrl="",shiftUrl;
    int ClassID[]={0},ShiftID[]={0},MediumID[]={0},MonthID[]={0},SectionID[]={0};
    ProgressDialog dialog;
    Fragment fragment;
    FragmentManager fragmentManager;
    Handler handler;
    SharedPreferences sharedPre;
    Button student_find_button;
    private boolean shouldRefreshOnResume = false;
    private static int SPLASH_TIME_OUT = 1000;
    Runnable refresh;
    int SectionSelectID=0,ShiftSelectID=0,MediumSelectID=0,ClassSelectID=0,MonthSelectID=0;
    long InstituteID;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_attendance);
        classSpinner = (MaterialSpinner) findViewById(R.id.spinner_Class);
        shiftSpinner = (MaterialSpinner) findViewById(R.id.spinner_Shift);
        mediumSpinner= (MaterialSpinner) findViewById(R.id.spinner_medium);
        sectionSpinner =(MaterialSpinner) findViewById(R.id.spinner_Section);
        monthSpinner = (MaterialSpinner) findViewById(R.id.spinner_Month);
        sharedPre = PreferenceManager.getDefaultSharedPreferences(this);
        InstituteID = sharedPre.getLong("InstituteID",0);
        classUrl=getString(R.string.baseUrlLocal)+"getInsClass/"+InstituteID;
        mediumUrl=getString(R.string.baseUrlLocal)+"getInsMedium/"+InstituteID;
        monthUrl=getString(R.string.baseUrlLocal)+"getMonth";
        shiftUrl=getString(R.string.baseUrlLocal)+"getInsShift/"+InstituteID;
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading....");
        dialog.show();
        student_find_button=(Button) findViewById(R.id.show_button);
        student_find_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPre.edit();
                editor.putInt("ShiftSelectID",ShiftSelectID);
                editor.putInt("ClassSelectID",ClassSelectID);
                editor.putInt("MediumSelectID",MediumSelectID);
                editor.putInt("SectionSelectID",SectionSelectID);
                editor.putInt("MonthSelectID",MonthSelectID);
                editor.commit();
                // Toast.makeText(getActivity(),"ShiftID="+ShiftSelectID+" ClassID= "+ClassSelectID+ "MediumID= "+MediumSelectID+" MonthID="+MediumSelectID+" SectionID= "+SectionSelectID+"MonthID= "+MonthSelectID,Toast.LENGTH_LONG).show();
                if(ClassSelectID>0&&MonthSelectID>0)
                {

                    Intent i = new Intent(ShowAttendance.this, ShowAllStudent.class);
                    startActivity(i);
                }
                else
                {
                    final AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(getApplicationContext());

                    dlgAlert.setTitle(" Please Select Class and Month !");
                    dlgAlert.setIcon(R.drawable.error);
                    dlgAlert.setPositiveButton("OK", null);
                    dlgAlert.setCancelable(true);
                    dlgAlert.create().show();
                    dlgAlert.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                }


            }
        });
        RequestQueue queueClass = Volley.newRequestQueue(this);

        StringRequest stringClassRequest = new StringRequest(Request.Method.GET, classUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {

                        parseClassJsonData(response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.dismiss();
            }
        });

        queueClass.add(stringClassRequest);
        RequestQueue queueMedium = Volley.newRequestQueue(this);

        StringRequest stringMediumRequest = new StringRequest(Request.Method.GET, mediumUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        parseMediumJsonData(response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.dismiss();
            }
        });

        queueMedium.add(stringMediumRequest);
        RequestQueue queueMonth = Volley.newRequestQueue(this);

        StringRequest stringMonthRequest = new StringRequest(Request.Method.GET, monthUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        parseMonthJsonData(response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.dismiss();
            }
        });

        queueMonth.add(stringMonthRequest);

        RequestQueue queueShift = Volley.newRequestQueue(this);

        StringRequest stringShiftRequest = new StringRequest(Request.Method.GET, shiftUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        parseShiftJsonData(response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.dismiss();
            }
        });
        queueShift.add(stringShiftRequest);

        tableView = (TableView)findViewById(R.id.tableView);
        shiftSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                ShiftSelectID=ShiftID[position];
            }
        });
        mediumSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                MediumSelectID=MediumID[position];
            }
        });
        monthSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                MonthSelectID=MonthID[position];
            }
        });
        sectionSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>()
        {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item)
            {
                if (position > 0 &&item.length()>0)
                {
                    SectionSelectID=SectionID[position] ;
                }
                else
                    SectionSelectID=0;

            }
        });
        classSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>()
        {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {

                if (position > 0 &&item.length()>0)
                {
                    ClassSelectID = ClassID[position];
                    RequestQueue queueSection = Volley.newRequestQueue(getApplicationContext());
                    sectionUrl = getString(R.string.baseUrlLocal) + "getInsSection/" + sharedPre.getLong("InstituteID", 0) + "/" + ClassSelectID;
                    StringRequest stringSectionRequest = new StringRequest(Request.Method.GET, sectionUrl,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    parseSectionJsonData(response);

                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            dialog.dismiss();
                        }
                    });

                    queueSection.add(stringSectionRequest);

                }
                else
                {
                    ClassSelectID = ClassID[position];
                    ArrayList al = new ArrayList();
                    al.add("Section");
                    al.add("");
                    SectionSelectID = SectionID[0];
                    ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, al);
                    sectionSpinner.setAdapter(adapter);
                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        NavUtils.navigateUpFromSameTask(this);
        super.onBackPressed();
    }
    void parseClassJsonData(String jsonString)
    {
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            if(jsonArray.length()>0) {
                ArrayList al = new ArrayList();
                ClassID = new int[jsonArray.length() + 1];
                al.add("Class");
                ClassID[0] = 0;
                for (int i = 0; i < jsonArray.length(); ++i) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String name = jsonObject.getString("ClassName");
                    ClassID[i + 1] = jsonObject.getInt("ClassID");
                    al.add(name);
                }
                ClassSelectID = ClassID[0];
                ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, al);
                classSpinner.setAdapter(adapter);
            }

        }
        catch (JSONException e)
        {
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
            dialog.dismiss();
        }


    }
    void parseMediumJsonData(String jsonString)
    {
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            if(jsonArray.length()>0)
            {
                ArrayList al = new ArrayList();
                MediumID = new int[jsonArray.length()+1];
                MediumID[0]=0;
                al.add("Medium");
                for (int i = 0; i < jsonArray.length(); ++i) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String name = jsonObject.getString("MameName");
                    MediumID[i+1] = jsonObject.getInt("MediumID");
                    al.add(name);
                }
                MediumSelectID = MediumID[0];
                ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, al);
                mediumSpinner.setAdapter(adapter);
            }
            //spinner.setSelectedIndex(1);
        } catch (JSONException e)
        {
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
            dialog.dismiss();

        }

    }
    void parseMonthJsonData(String jsonString)
    {
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            if(jsonArray.length()>0)
            {
                ArrayList al = new ArrayList();
                al.add("Month");
                MediumID[0]=0;
                MonthID = new int[jsonArray.length() + 1];
                for (int i = 0; i < jsonArray.length(); ++i) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String name = jsonObject.getString("MonthName");
                    MonthID[i+1] = jsonObject.getInt("MonthID");
                    al.add(name);
                }
                MonthSelectID = MonthID[0];
                ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, al);
                monthSpinner.setAdapter(adapter);
            }

        } catch (JSONException e) {
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
            dialog.dismiss();

        }

    }
    void parseSectionJsonData(String jsonString) {
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            if (jsonArray.length() > 0)
            {
                ArrayList al = new ArrayList();
                al.add("Section");
                SectionID = new int[jsonArray.length()+1];
                SectionID[0]=0;
                for (int i = 0; i < jsonArray.length(); ++i) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String name = jsonObject.getString("SectionName");
                    SectionID[i+1] = jsonObject.getInt("SectionID");
                    al.add(name);
                }
                SectionSelectID = SectionID[0];
                ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, al);
                sectionSpinner.setAdapter(adapter);

            }
        }
        catch (JSONException e) {
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
            dialog.dismiss();

        }

    }
    void parseShiftJsonData(String jsonString)
    {
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            if(jsonArray.length()>0)
            {
                ShiftID = new int[jsonArray.length() + 1];
                ArrayList al = new ArrayList();
                al.add("Shift");
                for (int i = 0; i < jsonArray.length(); ++i)
                {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String name = jsonObject.getString("ShiftName");
                    if(jsonObject.getString("ShiftID").equals("null"))
                    {
                        ShiftID[i + 1]=0;
                    }
                    else
                        ShiftID[i + 1] = jsonObject.getInt("ShiftID");
                    al.add(name);
                }
                //ShiftSelectID=ShiftID[0];
                ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, al);
                shiftSpinner.setAdapter(adapter);
            }
            //spinner.setSelectedIndex(1);
        }
        catch (JSONException e)
        {
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();


        }
        dialog.dismiss();
    }
}
