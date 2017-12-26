package onair.onems.mainactivities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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

/**
 * Created by User on 12/26/2017.
 */

public class ShowAttendance extends AppCompatActivity {
    MaterialSpinner classSpinner,sectionSpinner,mediumSpinner,monthSpinner,shiftSpinner;
    TableView tableView;
    String classUrl = "",mediumUrl="",monthUrl="",sectionUrl="",shiftUrl;
    int ClassID[],ShiftID[],MediumID[],MonthID[],SectionID[];
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
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_attendance);
        classSpinner = (MaterialSpinner) findViewById(R.id.spinner_Class);
        shiftSpinner = (MaterialSpinner) findViewById(R.id.spinner_Shift);
        mediumSpinner= (MaterialSpinner) findViewById(R.id.spinner_medium);
        sectionSpinner = (MaterialSpinner) findViewById(R.id.spinner_Section);
        monthSpinner = (MaterialSpinner) findViewById(R.id.spinner_Month);
        sharedPre = PreferenceManager.getDefaultSharedPreferences(this);
        classUrl=getString(R.string.baseUrl)+"getInsClass/1";
        mediumUrl=getString(R.string.baseUrl)+"getInsMedium/1";
        monthUrl=getString(R.string.baseUrl)+"getMonth";
        shiftUrl=getString(R.string.baseUrl)+"getInsShift/1";
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading....");
        dialog.show();
        student_find_button=(Button) findViewById(R.id.show_button);
        student_find_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                dialog.show();
                SharedPreferences.Editor editor = sharedPre.edit();
                editor.putInt("ShiftSelectID",ShiftSelectID);
                editor.putInt("ClassSelectID",ClassSelectID);
                editor.putInt("MediumSelectID",MediumSelectID);
                editor.putInt("SectionSelectID",SectionSelectID);
                editor.putInt("MonthSelectID",MonthSelectID);
                editor.commit();
                //Toast.makeText(getActivity(),"ShiftID="+ShiftSelectID+" ClassID= "+ClassSelectID+ "MediumID= "+MediumSelectID+" MonthID="+MediumSelectID+" SectionID= "+SectionSelectID+"MonthID= "+MonthSelectID,Toast.LENGTH_LONG).show();
                Intent i = new Intent(ShowAttendance.this, StudentAttendanceShow.class);
                startActivity(i);


            }
        });
        RequestQueue queueClass = Volley.newRequestQueue(this);

        StringRequest stringClassRequest = new StringRequest(Request.Method.GET, classUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        parseClassJsonData(response);
                        RequestQueue queueSection = Volley.newRequestQueue(getApplicationContext());
                        sectionUrl=getString(R.string.baseUrl)+"getInsSection/"+sharedPre.getInt("InstituteID",0)+"/"+ClassSelectID;
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
        sectionSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                SectionSelectID=SectionID[position];


            }
        });


    }


    void parseClassJsonData(String jsonString) {
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            ArrayList al = new ArrayList();
            ClassID=new int[jsonArray.length()];
            for(int i = 0; i < jsonArray.length(); ++i)
            {
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                String name=jsonObject.getString("ClassName");
                ClassID[i]=jsonObject.getInt("ClassID");
                al.add(name);
            }
            ClassSelectID=ClassID[0];
            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, al);
            classSpinner.setAdapter(adapter);
            //spinner.setSelectedIndex(1);
        } catch (JSONException e) {
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
            dialog.dismiss();
        }


    }
    void parseMediumJsonData(String jsonString) {
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            ArrayList al = new ArrayList();
            MediumID=new int[jsonArray.length()];
            for(int i = 0; i < jsonArray.length(); ++i) {
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                String name=jsonObject.getString("MameName");
                MediumID[i]=jsonObject.getInt("MediumID");
                al.add(name);
            }
            MediumSelectID=MediumID[0];
            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, al);
            mediumSpinner.setAdapter(adapter);
            //spinner.setSelectedIndex(1);
        } catch (JSONException e) {
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
            dialog.dismiss();

        }

    }
    void parseMonthJsonData(String jsonString) {
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            ArrayList al = new ArrayList();
            MonthID=new int[jsonArray.length()];
            for(int i = 0; i < jsonArray.length(); ++i) {
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                String name=jsonObject.getString("MonthName");
                MonthID[i]=jsonObject.getInt("MonthID");
                al.add(name);
            }
            MonthSelectID=MonthID[0];
            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, al);
            monthSpinner.setAdapter(adapter);

            //spinner.setSelectedIndex(1);
        } catch (JSONException e) {
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
            dialog.dismiss();

        }

    }
    void parseSectionJsonData(String jsonString) {
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            ArrayList al = new ArrayList();
            SectionID=new int[jsonArray.length()];
            for(int i = 0; i < jsonArray.length(); ++i) {
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                String name=jsonObject.getString("SectionName");
                SectionID[i]=jsonObject.getInt("SectionID");
                al.add(name);
            }
            SectionSelectID= SectionID[0];
            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, al);
            sectionSpinner.setAdapter(adapter);

        } catch (JSONException e) {
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
            dialog.dismiss();

        }

    }
    void parseShiftJsonData(String jsonString) {
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            ShiftID=new int[jsonArray.length()];
            ArrayList al = new ArrayList();
            for(int i = 0; i < jsonArray.length(); ++i) {
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                String name=jsonObject.getString("ShiftName");
                ShiftID[i]=jsonObject.getInt("ShiftID");
                al.add(name);
            }
            ShiftSelectID=ShiftID[0];
            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, al);
            shiftSpinner.setAdapter(adapter);
            //spinner.setSelectedIndex(1);
        } catch (JSONException e) {
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();


        }
        dialog.dismiss();
    }
}
