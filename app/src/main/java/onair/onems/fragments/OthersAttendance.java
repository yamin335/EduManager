package onair.onems.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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
import onair.onems.R;
import onair.onems.mainactivities.StudentAttendanceShow;
import onair.onems.models.ClassModel;
import onair.onems.models.DepartmentModel;
import onair.onems.models.MediumModel;
import onair.onems.models.MonthModel;
import onair.onems.models.SectionModel;
import onair.onems.models.ShiftModel;
import onair.onems.network.MySingleton;

public class OthersAttendance extends Fragment {
    private View rootView;
    private Spinner spinnerClass, spinnerShift, spinnerSection, spinnerMedium, spinnerDepartment, spinnerMonth;
    private String classUrl, shiftUrl, sectionUrl, mediumUrl, monthUrl, departmentUrl;
    private ArrayList<ClassModel> allClassArrayList;
    private ArrayList<ShiftModel> allShiftArrayList;
    private ArrayList<SectionModel> allSectionArrayList;
    private ArrayList<MediumModel> allMediumArrayList;
    private ArrayList<DepartmentModel> allDepartmentArrayList;
    private ArrayList<MonthModel> allMonthArrayList;
    private String[] tempClassArray = {"Select Class"};
    private String[] tempShiftArray = {"Select Shift"};
    private String[] tempSectionArray = {"Select Section"};
    private String[] tempDepartmentArray = {"Select Department"};
    private String[] tempMediumArray = {"Select Medium"};
    private String[] tempMonthArray = {"Select Month"};
    private ClassModel selectedClass = null;
    private ShiftModel selectedShift = null;
    private SectionModel selectedSection = null;
    private MediumModel selectedMedium = null;
    private DepartmentModel selectedDepartment = null;
    private MonthModel selectedMonth = null;
    private long InstituteID;
    ProgressDialog dialog;
    Button student_find_button;
    public OthersAttendance()
    {

    }
    public static OthersAttendance newInstance()
    {
        OthersAttendance fragment = new OthersAttendance();
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.student_attendance_others_attendence, container, false);
        spinnerClass = (Spinner)rootView.findViewById(R.id.spinnerClass);
        spinnerShift = (Spinner)rootView.findViewById(R.id.spinnerShift);
        spinnerSection = (Spinner)rootView.findViewById(R.id.spinnerSection);
        spinnerMedium =(Spinner)rootView.findViewById(R.id.spinnerMedium);
        spinnerDepartment =(Spinner)rootView.findViewById(R.id.spinnerDepartment);
        spinnerMonth = (Spinner)rootView.findViewById(R.id.spinnerMonth);
        student_find_button=(Button) rootView.findViewById(R.id.show_button);

        selectedClass = new ClassModel();
        selectedShift = new ShiftModel();
        selectedSection = new SectionModel();
        selectedMedium = new MediumModel();
        selectedDepartment = new DepartmentModel();
        selectedMonth = new MonthModel();

        ArrayAdapter<String> class_spinner_adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, tempClassArray);
        class_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClass.setAdapter(class_spinner_adapter);

        ArrayAdapter<String> shift_spinner_adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, tempShiftArray);
        shift_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerShift.setAdapter(shift_spinner_adapter);

        ArrayAdapter<String> section_spinner_adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, tempSectionArray);
        section_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSection.setAdapter(section_spinner_adapter);

        ArrayAdapter<String> department_spinner_adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, tempDepartmentArray);
        department_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDepartment.setAdapter(department_spinner_adapter);

        ArrayAdapter<String> medium_spinner_adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, tempMediumArray);
        medium_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMedium.setAdapter(medium_spinner_adapter);

        ArrayAdapter<String> month_spinner_adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, tempMonthArray);
        month_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(month_spinner_adapter);

        allClassArrayList = new ArrayList<>();
        allShiftArrayList = new ArrayList<>();
        allSectionArrayList = new ArrayList<>();
        allMediumArrayList = new ArrayList<>();
        allDepartmentArrayList = new ArrayList<>();
        allMonthArrayList = new ArrayList<>();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        InstituteID = prefs.getLong("InstituteID",0);

        monthUrl=getString(R.string.baseUrlLocal)+"getMonth";
        shiftUrl = getString(R.string.baseUrlLocal)+"getInsShift/"+InstituteID;
        mediumUrl = getString(R.string.baseUrlLocal)+"getInsMedium/"+InstituteID;

        dialog = new ProgressDialog(getActivity());
        dialog.setTitle("Loading...");
        dialog.setMessage("Please Wait...");
        dialog.setCancelable(false);
        dialog.show();

        if(!isNetworkAvailable())
        {
            Toast.makeText(getActivity(),"Please check your internet connection and open app again!!! ",Toast.LENGTH_LONG).show();
        }

        ShiftDataGetRequest(shiftUrl);

        MediumDataGetRequest(mediumUrl);

        MonthDataGetRequest();

        spinnerShift.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != 0)
                {
                    try {
                        selectedShift = allShiftArrayList.get(position-1);
                    }
                    catch (IndexOutOfBoundsException e)
                    {
                        Toast.makeText(getActivity(),"No shift found !!!",Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    selectedShift = new ShiftModel();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        spinnerMedium.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != 0)
                {
                    try {
                        selectedMedium = allMediumArrayList.get(position-1);
                        selectedClass = new ClassModel();
                        selectedDepartment = new DepartmentModel();
                        selectedSection = new SectionModel();

                        CheckSelectedData();
                        classUrl = getString(R.string.baseUrlLocal)+"MediumWiseClassDDL/"+InstituteID+"/"+selectedMedium.getMediumID();
                        ClassDataGetRequest(classUrl);
                    }
                    catch (IndexOutOfBoundsException e)
                    {
                        Toast.makeText(getActivity(),"No medium found !!!",Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    selectedMedium = new MediumModel();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != 0)
                {
                    try {
                        selectedClass = allClassArrayList.get(position-1);
                        selectedDepartment = new DepartmentModel();
                        selectedSection = new SectionModel();

                        CheckSelectedData();
                        departmentUrl = getString(R.string.baseUrlLocal)+"ClassWiseDepartmentDDL/"+InstituteID+"/"+
                                selectedClass.getClassID()+"/"+selectedMedium.getMediumID();
                        DepartmentDataGetRequest(departmentUrl);
                    }
                    catch (IndexOutOfBoundsException e)
                    {
                        Toast.makeText(getActivity(),"No class found !!!",Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    selectedClass = new ClassModel();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerDepartment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != 0)
                {
                    try {
                        selectedDepartment = allDepartmentArrayList.get(position-1);
                        selectedSection = new SectionModel();

                        CheckSelectedData();
                        sectionUrl = getString(R.string.baseUrlLocal)+"getInsSection/"+InstituteID+"/"+
                                selectedClass.getClassID()+"/"+selectedDepartment.getDepartmentID();
                        SectionDataGetRequest(sectionUrl);
                    }
                    catch (IndexOutOfBoundsException e)
                    {
                        Toast.makeText(getActivity(),"No shift found !!!",Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    selectedDepartment = new DepartmentModel();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerSection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != 0)
                {
                    try {
                        selectedSection = allSectionArrayList.get(position-1);
                    }
                    catch (IndexOutOfBoundsException e)
                    {
                        Toast.makeText(getActivity(),"No section found !!!",Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    selectedSection = new SectionModel();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != 0)
                {
                    try {
                        selectedMonth = allMonthArrayList.get(position-1);
                    }
                    catch (IndexOutOfBoundsException e)
                    {
                        Toast.makeText(getActivity(),"No section found !!!",Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    selectedMonth = new MonthModel();
//                    tableView.removeAllViews();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        student_find_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(selectedMedium.getMediumID() == -2)
                {
                    Toast.makeText(getActivity(),"Please select a medium!!!",Toast.LENGTH_LONG).show();
                }
                else if(selectedClass.getClassID() == -2)
                {
                    Toast.makeText(getActivity(),"Please select a class!!!",Toast.LENGTH_LONG).show();
                }
                else if((selectedDepartment.getDepartmentID() == -2)&&(allDepartmentArrayList.size()>0))
                {
                    Toast.makeText(getActivity(),"Please select a department!!!",Toast.LENGTH_LONG).show();
                }
                else if((selectedSection.getSectionID() == -2)&&(allSectionArrayList.size()>0))
                {
                    Toast.makeText(getActivity(),"Please select a section!!!",Toast.LENGTH_LONG).show();
                }
                else if(selectedMonth.getMonthID() == 0)
                {
                    Toast.makeText(getActivity(),"Please select a month!!!",Toast.LENGTH_LONG).show();
                }
                else
                {
                    Intent intent = new Intent(getActivity(), StudentAttendanceShow.class);
                    intent.putExtra("ShiftID", selectedShift.getShiftID());
                    intent.putExtra("MediumID", selectedMedium.getMediumID());
                    intent.putExtra("ClassID", selectedClass.getClassID());
                    intent.putExtra("DepartmentID", selectedDepartment.getDepartmentID());
                    intent.putExtra("SectionID", selectedSection.getSectionID());
                    intent.putExtra("MonthID", selectedMonth.getMonthID());
                    startActivity(intent);
                }
            }
        });

        return rootView;
    }

    void parseClassJsonData(String jsonString) {
        try {
            allClassArrayList = new ArrayList<>();
            JSONArray classJsonArray = new JSONArray(jsonString);
            ArrayList<String> classArrayList = new ArrayList<>();
            classArrayList.add("Select Class");
            for(int i = 0; i < classJsonArray.length(); ++i) {
                JSONObject classJsonObject = classJsonArray.getJSONObject(i);
                ClassModel classModel = new ClassModel(classJsonObject.getString("ClassID"), classJsonObject.getString("ClassName"));
//                Toast.makeText(this,classJsonObject.getString("ClassID")+classJsonObject.getString("ClassName"),Toast.LENGTH_LONG).show();
                allClassArrayList.add(classModel);
                classArrayList.add(classModel.getClassName());
            }
            if(allClassArrayList.size() == 1){
                selectedClass = allClassArrayList.get(1);
            }
            try {
                String[] strings = new String[classArrayList.size()];
                strings = classArrayList.toArray(strings);
                ArrayAdapter<String> class_spinner_adapter = new ArrayAdapter<>(getActivity(),R.layout.spinner_item, strings);
                class_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerClass.setAdapter(class_spinner_adapter);
                dialog.dismiss();
            }
            catch (IndexOutOfBoundsException e)
            {
                dialog.dismiss();
                Toast.makeText(getActivity(),"No class found !!!",Toast.LENGTH_LONG).show();
            }
            //spinner.setSelectedIndex(1);
        } catch (JSONException e) {
            Toast.makeText(getActivity(),""+e,Toast.LENGTH_LONG).show();
            dialog.dismiss();
        }
    }

    void parseShiftJsonData(String jsonString) {
        try {
            allShiftArrayList = new ArrayList<>();
            JSONArray shiftJsonArray = new JSONArray(jsonString);
            ArrayList<String> shiftArrayList = new ArrayList<>();
            shiftArrayList.add("Select Shift");
            for(int i = 0; i < shiftJsonArray.length(); ++i) {
                JSONObject shiftJsonObject = shiftJsonArray.getJSONObject(i);
                ShiftModel shiftModel = new ShiftModel(shiftJsonObject.getString("ShiftID"), shiftJsonObject.getString("ShiftName"));
//                Toast.makeText(this,classJsonObject.getString("ClassID")+classJsonObject.getString("ClassName"),Toast.LENGTH_LONG).show();
                allShiftArrayList.add(shiftModel);
                shiftArrayList.add(shiftModel.getShiftName());
            }
            if(allShiftArrayList.size() == 1){
                selectedShift = allShiftArrayList.get(1);
            }
            try {
                String[] strings = new String[shiftArrayList.size()];
                strings = shiftArrayList.toArray(strings);
                ArrayAdapter<String> shift_spinner_adapter = new ArrayAdapter<>(getActivity(),R.layout.spinner_item, strings);
                shift_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerShift.setAdapter(shift_spinner_adapter);
                dialog.dismiss();
            }
            catch (IndexOutOfBoundsException e)
            {
                dialog.dismiss();
                Toast.makeText(getActivity(),"No shift found !!!",Toast.LENGTH_LONG).show();
            }
            //spinner.setSelectedIndex(1);
        } catch (JSONException e) {
            Toast.makeText(getActivity(),""+e,Toast.LENGTH_LONG).show();
            dialog.dismiss();
        }
    }

    void parseSectionJsonData(String jsonString) {
        try {
            allSectionArrayList = new ArrayList<>();
            JSONArray sectionJsonArray = new JSONArray(jsonString);
            ArrayList<String> sectionArrayList = new ArrayList<>();
            sectionArrayList.add("Select Section");
            for(int i = 0; i < sectionJsonArray.length(); ++i) {
                JSONObject sectionJsonObject = sectionJsonArray.getJSONObject(i);
                SectionModel sectionModel = new SectionModel(sectionJsonObject.getString("SectionID"), sectionJsonObject.getString("SectionName"));
//                Toast.makeText(this,classJsonObject.getString("ClassID")+classJsonObject.getString("ClassName"),Toast.LENGTH_LONG).show();
                allSectionArrayList.add(sectionModel);
                sectionArrayList.add(sectionModel.getSectionName());
            }
            if(allSectionArrayList.size() == 1){
                selectedSection = allSectionArrayList.get(1);
            }
            try {
                String[] strings = new String[sectionArrayList.size()];
                strings = sectionArrayList.toArray(strings);
                ArrayAdapter<String> section_spinner_adapter = new ArrayAdapter<>(getActivity(),R.layout.spinner_item, strings);
                section_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerSection.setAdapter(section_spinner_adapter);
                dialog.dismiss();
            }
            catch (IndexOutOfBoundsException e)
            {
                dialog.dismiss();
                Toast.makeText(getActivity(),"No section found !!!",Toast.LENGTH_LONG).show();
            }
            //spinner.setSelectedIndex(1);
        } catch (JSONException e) {
            Toast.makeText(getActivity(),""+e,Toast.LENGTH_LONG).show();
            dialog.dismiss();
        }
    }

    void parseMediumJsonData(String jsonString) {
        try {
            allMediumArrayList = new ArrayList<>();
            JSONArray mediumJsonArray = new JSONArray(jsonString);
            ArrayList<String> mediumnArrayList = new ArrayList<>();
            mediumnArrayList.add("Select Medium");
            for(int i = 0; i < mediumJsonArray.length(); ++i) {
                JSONObject mediumJsonObject = mediumJsonArray.getJSONObject(i);
                MediumModel mediumModel = new MediumModel(mediumJsonObject.getString("MediumID"), mediumJsonObject.getString("MameName"),
                        mediumJsonObject.getString("InstMediumID"), mediumJsonObject.getString("InstituteID"), mediumJsonObject.getString("IsActive"));
//                Toast.makeText(this,classJsonObject.getString("ClassID")+classJsonObject.getString("ClassName"),Toast.LENGTH_LONG).show();
                allMediumArrayList.add(mediumModel);
                mediumnArrayList.add(mediumModel.getMameName());
            }
            if(allMediumArrayList.size() == 1){
                selectedMedium = allMediumArrayList.get(1);
            }
            try {
                String[] strings = new String[mediumnArrayList.size()];
                strings = mediumnArrayList.toArray(strings);
                ArrayAdapter<String> medium_spinner_adapter = new ArrayAdapter<>(getActivity(),R.layout.spinner_item, strings);
                medium_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerMedium.setAdapter(medium_spinner_adapter);
                dialog.dismiss();
            }
            catch (IndexOutOfBoundsException e)
            {
                dialog.dismiss();
                Toast.makeText(getActivity(),"No medium found !!!",Toast.LENGTH_LONG).show();
            }
            //spinner.setSelectedIndex(1);
        } catch (JSONException e) {
            Toast.makeText(getActivity(),""+e,Toast.LENGTH_LONG).show();
            dialog.dismiss();
        }
    }

    void parseDepartmentJsonData(String jsonString) {
        try {
            allDepartmentArrayList = new ArrayList<>();
            JSONArray departmentJsonArray = new JSONArray(jsonString);
            ArrayList<String> departmentArrayList = new ArrayList<>();
            departmentArrayList.add("Select Department");
            for(int i = 0; i < departmentJsonArray.length(); ++i) {
                JSONObject departmentJsonObject = departmentJsonArray.getJSONObject(i);
                DepartmentModel departmentModel = new DepartmentModel(departmentJsonObject.getString("DepartmentID"), departmentJsonObject.getString("DepartmentName"));
//                Toast.makeText(this,classJsonObject.getString("ClassID")+classJsonObject.getString("ClassName"),Toast.LENGTH_LONG).show();
                allDepartmentArrayList.add(departmentModel);
                departmentArrayList.add(departmentModel.getDepartmentName());
            }
            if(allDepartmentArrayList.size() == 1){
                selectedDepartment = allDepartmentArrayList.get(1);
            }
            if(allDepartmentArrayList.size() == 0){
                CheckSelectedData();
                sectionUrl = getString(R.string.baseUrlLocal)+"getInsSection/"+InstituteID+"/"+
                        selectedClass.getClassID()+"/"+selectedDepartment.getDepartmentID();
                SectionDataGetRequest(sectionUrl);
            }
            try {
                String[] strings = new String[departmentArrayList.size()];
                strings = departmentArrayList.toArray(strings);
                ArrayAdapter<String> department_spinner_adapter = new ArrayAdapter<>(getActivity(),R.layout.spinner_item, strings);
                department_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerDepartment.setAdapter(department_spinner_adapter);
                dialog.dismiss();
            }
            catch (IndexOutOfBoundsException e)
            {
                dialog.dismiss();
                Toast.makeText(getActivity(),"No department found !!!",Toast.LENGTH_LONG).show();
            }
            //spinner.setSelectedIndex(1);
        } catch (JSONException e) {
            Toast.makeText(getActivity(),""+e,Toast.LENGTH_LONG).show();
            dialog.dismiss();
        }
    }

    void parseMonthJsonData(String jsonString) {

        try {
            allMonthArrayList = new ArrayList<>();
            JSONArray MonthJsonArray = new JSONArray(jsonString);
            ArrayList<String> monthArrayList = new ArrayList<>();
            monthArrayList.add("Select Month");
            for(int i = 0; i < MonthJsonArray.length(); ++i)
            {
                JSONObject monthJsonObject = MonthJsonArray.getJSONObject(i);
                MonthModel monthModel = new MonthModel(monthJsonObject.getString("MonthID"), monthJsonObject.getString("MonthName"));
//                Toast.makeText(this,classJsonObject.getString("ClassID")+classJsonObject.getString("ClassName"),Toast.LENGTH_LONG).show();
                allMonthArrayList.add(monthModel);
                monthArrayList.add(monthModel.getMonthName());
            }

            if(allMonthArrayList.size() == 1){
                selectedMonth = allMonthArrayList.get(1);
            }

            try {
                String[] strings = new String[monthArrayList.size()];
                strings = monthArrayList.toArray(strings);
                ArrayAdapter<String> month_spinner_adapter = new ArrayAdapter<>(getActivity(),R.layout.spinner_item, strings);
                month_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerMonth.setAdapter(month_spinner_adapter);
                dialog.dismiss();
            }
            catch (IndexOutOfBoundsException e)
            {
                dialog.dismiss();
                Toast.makeText(getActivity(),"No class found !!!",Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {
            Toast.makeText(getActivity(),""+e,Toast.LENGTH_LONG).show();
            dialog.dismiss();

        }

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void ShiftDataGetRequest(String url)
    {
        dialog.show();
        //Preparing Shift data from server
        StringRequest stringShiftRequest = new StringRequest(Request.Method.GET, url,
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
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Authorization", "Request_From_onEMS_Android_app");
                return params;
            }
        };
        MySingleton.getInstance(getActivity()).addToRequestQueue(stringShiftRequest);
    }

    private void MediumDataGetRequest(String url)
    {
        dialog.show();
        //Preparing Medium data from server
        StringRequest stringMediumRequest = new StringRequest(Request.Method.GET, url,
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
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Authorization", "Request_From_onEMS_Android_app");
                return params;
            }
        };
        MySingleton.getInstance(getActivity()).addToRequestQueue(stringMediumRequest);
    }

    private void ClassDataGetRequest(String url)
    {
        dialog.show();
        //Preparing claas data from server
        StringRequest stringClassRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        parseClassJsonData(response);

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
        MySingleton.getInstance(getActivity()).addToRequestQueue(stringClassRequest);
    }

    private void DepartmentDataGetRequest(String url)
    {
        dialog.show();
        //Preparing Department data from server
        StringRequest stringDepartmentRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        parseDepartmentJsonData(response);

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
        MySingleton.getInstance(getActivity()).addToRequestQueue(stringDepartmentRequest);
    }

    private void SectionDataGetRequest(String url)
    {
        dialog.show();
        //Preparing section data from server
        StringRequest stringSectionRequest = new StringRequest(Request.Method.GET, url,
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
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Authorization", "Request_From_onEMS_Android_app");
                return params;
            }
        };
        MySingleton.getInstance(getActivity()).addToRequestQueue(stringSectionRequest);
    }

    void MonthDataGetRequest(){
        dialog.show();
        StringRequest stringMonthRequest = new StringRequest(Request.Method.GET, monthUrl,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {

                        parseMonthJsonData(response);

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
        MySingleton.getInstance(getActivity()).addToRequestQueue(stringMonthRequest);
    }

    private void CheckSelectedData(){
        if(selectedClass.getClassID() == -2)
        {
            selectedClass.setClassID("0");
        }
        if(selectedShift.getShiftID() == -2)
        {
            selectedShift.setShiftID("0");
        }
        if(selectedSection.getSectionID() == -2)
        {
            selectedSection.setSectionID("0");
        }
        if(selectedMedium.getMediumID() == -2)
        {
            selectedMedium.setMediumID("0");
        }
        if(selectedDepartment.getDepartmentID() == -2)
        {
            selectedDepartment.setDepartmentID("0");
        }
    }
    @Override
    public void onStop()
    {
        super.onStop();
    }

}
