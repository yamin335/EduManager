package onair.onems.mainactivities;

/**
 * Created by User on 12/3/2017.
 */

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import onair.onems.R;
import onair.onems.Services.GlideApp;
import onair.onems.customadapters.ExpandableListAdapter;
import onair.onems.mainactivities.TeacherAttendanceShow.ShowAttendance;
import onair.onems.models.ClassModel;
import onair.onems.models.DepartmentModel;
import onair.onems.models.ExpandedMenuModel;
import onair.onems.models.MediumModel;
import onair.onems.models.SectionModel;
import onair.onems.models.ShiftModel;
import onair.onems.models.SpinnerStudentInformation;
import onair.onems.models.SubjectModel;

public class TakeAttendance extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ExpandableListAdapter mMenuAdapter;
    private ExpandableListView expandableList;
    private List<ExpandedMenuModel> listDataHeader;
    private HashMap<ExpandedMenuModel, List<String>> listDataChild;
    private Spinner spinnerClass, spinnerShift,spinnerSection,spinnerMedium, spinnerDepartment, spinnerSubject;
    private Button takeAttendance, datePicker;
    private ProgressDialog dialog;
    private String classUrl, shiftUrl, sectionUrl, mediumUrl, departmentUrl, subjectUrl, selectedDate = "";
    private ArrayList<ClassModel> allClassArrayList;
    private ArrayList<ShiftModel> allShiftArrayList;
    private ArrayList<SectionModel> allSectionArrayList;
    private ArrayList<MediumModel> allMediumArrayList;
    private ArrayList<DepartmentModel> allDepartmentArrayList;
    private ArrayList<SubjectModel> allSubjectArrayList;

    private String[] tempClassArray = {"Select Class"};
    private String[] tempShiftArray = {"Select Shift"};
    private String[] tempSectionArray = {"Select Section"};
    private String[] tempDepartmentArray = {"Select Department"};
    private String[] tempMediumArray = {"Select Medium"};
    private String[] tempSubjectArray = {"Select Subject"};

    private ClassModel selectedClass = null;
    private ShiftModel selectedShift = null;
    private SectionModel selectedSection = null;
    private MediumModel selectedMedium = null;
    private SubjectModel selectedSubject = null;
    private DepartmentModel selectedDepartment = null;

    long InstituteID;

    private DatePickerDialog datePickerDialog;

    public static final String MyPREFERENCES = "LogInKey";
    public static SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_attendance);

        spinnerClass = (Spinner)findViewById(R.id.spinnerClass);
        spinnerShift = (Spinner)findViewById(R.id.spinnerShift);
        spinnerSection = (Spinner)findViewById(R.id.spinnerSection);
        spinnerMedium =(Spinner)findViewById(R.id.spinnerMedium);
        spinnerDepartment =(Spinner)findViewById(R.id.spinnerDepartment);
        spinnerSubject = (Spinner)findViewById(R.id.spinnerSubject);

        takeAttendance = (Button)findViewById(R.id.takeAttendance);

        datePicker = (Button)findViewById(R.id.pickDate);

        ArrayAdapter<String> class_spinner_adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, tempClassArray);
        class_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClass.setAdapter(class_spinner_adapter);

        ArrayAdapter<String> shift_spinner_adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, tempShiftArray);
        shift_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerShift.setAdapter(shift_spinner_adapter);

        ArrayAdapter<String> section_spinner_adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, tempSectionArray);
        section_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSection.setAdapter(section_spinner_adapter);

        ArrayAdapter<String> department_spinner_adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, tempDepartmentArray);
        department_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDepartment.setAdapter(department_spinner_adapter);

        ArrayAdapter<String> medium_spinner_adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, tempMediumArray);
        medium_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMedium.setAdapter(medium_spinner_adapter);

        ArrayAdapter<String> subject_spinner_adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, tempSubjectArray);
        subject_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSubject.setAdapter(subject_spinner_adapter);

        allClassArrayList = new ArrayList<>();
        allShiftArrayList = new ArrayList<>();
        allSectionArrayList = new ArrayList<>();
        allMediumArrayList = new ArrayList<>();
        allDepartmentArrayList = new ArrayList<>();
        allSubjectArrayList = new ArrayList<>();

        selectedClass = new ClassModel();
        selectedShift = new ShiftModel();
        selectedSection = new SectionModel();
        selectedMedium = new MediumModel();
        selectedDepartment = new DepartmentModel();
        selectedSubject = new SubjectModel();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        InstituteID = prefs.getLong("InstituteID",0);

        classUrl = getString(R.string.baseUrlLocal)+"getInsClass/"+InstituteID;
        shiftUrl = getString(R.string.baseUrlLocal)+"getInsShift/"+InstituteID;
        mediumUrl = getString(R.string.baseUrlLocal)+"getInsMedium/"+InstituteID;
        departmentUrl = getString(R.string.baseUrlLocal)+"getInsDepertment/"+InstituteID;

        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading...");
        dialog.setMessage("Please Wait...");
        dialog.setCancelable(false);
        dialog.show();

        if(!isNetworkAvailable())
        {
            Toast.makeText(TakeAttendance.this,"Please check your internet connection and open app again!!! ",Toast.LENGTH_LONG).show();
        }

        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(TakeAttendance.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
//                                selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                selectedDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                                datePicker.setText(selectedDate);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        //Preparing claas data from server
        RequestQueue queueClass = Volley.newRequestQueue(this);
        StringRequest stringClassRequest = new StringRequest(Request.Method.GET, classUrl,
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
        });
        queueClass.add(stringClassRequest);

        dialog.show();
        //Preparing Shift data from server
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

        dialog.show();
        //Preparing Medium data from server
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

        dialog.show();
        //Preparing Department data from server
        RequestQueue queueDepartment = Volley.newRequestQueue(this);
        StringRequest stringDepartmentRequest = new StringRequest(Request.Method.GET, departmentUrl,
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
        });
        queueDepartment.add(stringDepartmentRequest);

        takeAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable())
                {
                    if((selectedClass.getClassID() != -2)&&(!selectedDate.isEmpty())&&(selectedSubject != null))
                    {
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

                        Bundle bundle = new Bundle();
                        bundle.putLong("InstituteID", InstituteID);
                        bundle.putLong("MediumID",selectedMedium.getMediumID());
                        bundle.putLong("ShiftID",selectedShift.getShiftID());
                        bundle.putLong("ClassID",selectedClass.getClassID());
                        bundle.putLong("SectionID",selectedSection.getSectionID());
                        bundle.putLong("SubjectID",selectedSubject.getSubjectID());
                        bundle.putLong("DepertmentID",selectedDepartment.getDepartmentID());
                        bundle.putString("Date",selectedDate);

                        Intent intent = new Intent(TakeAttendance.this, TakeAttendanceDetails.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                    else if(selectedClass.getClassID() == -2)
                    {
                        Toast.makeText(TakeAttendance.this,"Please select a class !!! ",Toast.LENGTH_LONG).show();
                    }
                    else if(selectedDate.isEmpty())
                    {
                        Toast.makeText(TakeAttendance.this,"Please select a date !!! ",Toast.LENGTH_LONG).show();
                    }
                    else if(selectedSubject == null)
                    {
                        Toast.makeText(TakeAttendance.this,"Please select a subject !!! ",Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    Toast.makeText(TakeAttendance.this,"Please check your internet connection!!!",Toast.LENGTH_LONG).show();
                }
            }
        });

        spinnerClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != 0)
                {
                    selectedSubject = null;
                    try {
                        selectedClass = allClassArrayList.get(position-1);
                    }
                    catch (IndexOutOfBoundsException e)
                    {
                        Toast.makeText(TakeAttendance.this,"No class found !!!",Toast.LENGTH_LONG).show();
                    }
                    sectionUrl = getString(R.string.baseUrlLocal)+"getInsSection/"+InstituteID+"/"+selectedClass.getClassID();
                    dialog.show();
                    //Preparing section data from server
                    RequestQueue queueSection = Volley.newRequestQueue(TakeAttendance.this);
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
                    subjectUrl = getString(R.string.baseUrlLocal)+"getInsSubject"+"/"+InstituteID+"/"+
                            selectedDepartment.getDepartmentID()+"/"+selectedMedium.getMediumID()+"/"+selectedClass.getClassID();
                    dialog.show();
                    //Preparing subject data from server
                    RequestQueue queueSubject = Volley.newRequestQueue(TakeAttendance.this);
                    StringRequest stringSubjectRequest = new StringRequest(Request.Method.GET, subjectUrl,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    parseSubjectJsonData(response);

                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            dialog.dismiss();
                        }
                    });
                    queueSubject.add(stringSubjectRequest);

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
                        Toast.makeText(TakeAttendance.this,"No shift found !!!",Toast.LENGTH_LONG).show();
                    }

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
                    subjectUrl = getString(R.string.baseUrlLocal)+"getInsSubject"+"/"+InstituteID+"/"+
                            selectedDepartment.getDepartmentID()+"/"+selectedMedium.getMediumID()+"/"+selectedClass.getClassID();
                    dialog.show();
                    //Preparing subject data from server
                    RequestQueue queueSubject = Volley.newRequestQueue(TakeAttendance.this);
                    StringRequest stringSubjectRequest = new StringRequest(Request.Method.GET, subjectUrl,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    parseSubjectJsonData(response);

                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            dialog.dismiss();
                        }
                    });
                    queueSubject.add(stringSubjectRequest);
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

//        spinnerClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//                if(position != classSpinnerPosition)
//                {
//
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });



        spinnerSection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != 0)
                {
                    selectedSubject = null;
                    try {
                        selectedSection = allSectionArrayList.get(position-1);
                    }
                    catch (IndexOutOfBoundsException e)
                    {
                        Toast.makeText(TakeAttendance.this,"No section found !!!",Toast.LENGTH_LONG).show();
                    }

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
                    subjectUrl = getString(R.string.baseUrlLocal)+"getInsSubject"+"/"+InstituteID+"/"+
                            selectedDepartment.getDepartmentID()+"/"+selectedMedium.getMediumID()+"/"+selectedClass.getClassID();
                    dialog.show();
                    //Preparing subject data from server
                    RequestQueue queueSubject = Volley.newRequestQueue(TakeAttendance.this);
                    StringRequest stringSubjectRequest = new StringRequest(Request.Method.GET, subjectUrl,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    parseSubjectJsonData(response);

                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            dialog.dismiss();
                        }
                    });
                    queueSubject.add(stringSubjectRequest);
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

        spinnerMedium.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != 0)
                {
                    selectedSubject = null;
                    try {
                        selectedMedium = allMediumArrayList.get(position-1);
                    }
                    catch (IndexOutOfBoundsException e)
                    {
                        Toast.makeText(TakeAttendance.this,"No medium found !!!",Toast.LENGTH_LONG).show();
                    }

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

                    subjectUrl = getString(R.string.baseUrlLocal)+"getInsSubject"+"/"+InstituteID+"/"+
                            selectedDepartment.getDepartmentID()+"/"+selectedMedium.getMediumID()+"/"+selectedClass.getClassID();

                    dialog.show();
                    //Preparing subject data from server
                    RequestQueue queueSubject = Volley.newRequestQueue(TakeAttendance.this);
                    StringRequest stringSubjectRequest = new StringRequest(Request.Method.GET, subjectUrl,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    parseSubjectJsonData(response);

                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            dialog.dismiss();
                        }
                    });
                    queueSubject.add(stringSubjectRequest);
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

        spinnerDepartment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != 0)
                {
                    selectedSubject = null;
                    try {
                        selectedDepartment = allDepartmentArrayList.get(position-1);
                    }
                    catch (IndexOutOfBoundsException e)
                    {
                        Toast.makeText(TakeAttendance.this,"No department found !!!",Toast.LENGTH_LONG).show();
                    }

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

                    subjectUrl = getString(R.string.baseUrlLocal)+"getInsSubject"+"/"+InstituteID+"/"+
                            selectedDepartment.getDepartmentID()+"/"+selectedMedium.getMediumID()+"/"+selectedClass.getClassID();

                    dialog.show();
                    //Preparing subject data from server
                    RequestQueue queueSubject = Volley.newRequestQueue(TakeAttendance.this);
                    StringRequest stringSubjectRequest = new StringRequest(Request.Method.GET, subjectUrl,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    parseSubjectJsonData(response);

                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            dialog.dismiss();
                        }
                    });
                    queueSubject.add(stringSubjectRequest);
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

        spinnerSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != 0)
                {
                    try {
                        selectedSubject = allSubjectArrayList.get(position-1);
                    }
                    catch (IndexOutOfBoundsException e)
                    {
                        Toast.makeText(TakeAttendance.this,"No subject found !!!",Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    selectedSubject = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        /* to set the menu icon image*/
        ab.setHomeAsUpIndicator(android.R.drawable.ic_menu_add);
        ab.setDisplayHomeAsUpEnabled(true);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        expandableList = (ExpandableListView) findViewById(R.id.navigationmenu);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View view = getLayoutInflater().inflate(R.layout.nav_header_main,null);
        ImageView profilePicture = (ImageView)view.findViewById(R.id.profilePicture);
        TextView userType = (TextView)view.findViewById(R.id.userType);
        TextView userName = (TextView)view.findViewById(R.id.userName);
        String imageUrl = prefs.getString("ImageUrl","");
        String name = prefs.getString("UserFullName","");
        long user = prefs.getLong("UserTypeID",0);
        userName.setText(name);
        if(user == 4)
        {
            userType.setText("Teacher");
        }
        GlideApp.with(this)
                .load(getString(R.string.baseUrlRaw)+imageUrl.replace("\\","/")).apply(RequestOptions.circleCropTransform())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(profilePicture);
       // profilePicture.setImageDrawable(getResources().getDrawable(R.drawable.album1));
        navigationView.addHeaderView(view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        prepareListData();
        mMenuAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild, expandableList);

        // setting list adapter
        expandableList.setAdapter(mMenuAdapter);

        expandableList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                if((i == 2) && (i1 == 1) && (l == 1))
                {
                    Intent intent = new Intent(TakeAttendance.this, ShowAttendance.class);
                    startActivity(intent);
                }
                if((i == 2) && (i1 == 0) && (l == 0))
                {
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                }

                return false;
            }
        });
        expandableList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
//                Log.d("DEBUG", "heading clicked"+i+"--"+l);
                if((i == 7) && (l == 7))
                {
                    Intent intent = new Intent(TakeAttendance.this, StudentiCardMain.class);
                    startActivity(intent);
                    finish();
                }

                if((i == 8) && (l == 8))
                {
                    Intent intent = new Intent(TakeAttendance.this, ReportAllStudentMain.class);
                    startActivity(intent);
                    finish();
                }
                return false;
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
//        navigationView.setNavigationItemSelectedListener(this);
    }

//    void parseClassJsonData(String jsonString) {
//        try {
//            JSONArray classJsonArray = new JSONArray(jsonString);
//            ArrayList classArrayList = new ArrayList();
//            for(int i = 0; i < classJsonArray.length(); ++i) {
//                JSONObject classJsonObject = classJsonArray.getJSONObject(i);
//                ClassModel classModel = new ClassModel(classJsonObject.getString("ClassID"), classJsonObject.getString("ClassName"));
////                Toast.makeText(this,classJsonObject.getString("ClassID")+classJsonObject.getString("ClassName"),Toast.LENGTH_LONG).show();
//                allClassArrayList.add(classModel);
//                classArrayList.add(classModel.getClassName());
//            }
//            classArrayList.add("Select Class");
//            classSpinnerPosition = classArrayList.indexOf("Select Class");
//            try {
//                selectedClass = allClassArrayList.get(0);
//            }
//            catch (IndexOutOfBoundsException e)
//            {
//                Toast.makeText(TakeAttendance.this,"No class found !!!",Toast.LENGTH_LONG).show();
//                Toast.makeText(TakeAttendance.this,"Please select all options again !!!",Toast.LENGTH_LONG).show();
//            }
//            ArrayAdapter<ArrayList> class_spinner_adapter = new ArrayAdapter<ArrayList>(this,R.layout.spinner_item, classArrayList){
//
//                @Override
//                public View getView(int position, View convertView, ViewGroup parent) {
//
//                    View v = super.getView(position, convertView, parent);
//                    if (position == getCount()) {
////                    ((TextView)v.findViewById(android.R.id.text1)).setText("");
////                    ((TextView)v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
//                    }
//
//                    return v;
//                }
//
//                @Override
//                public int getCount() {
//                    return super.getCount()-1; // you dont display last item. It is used as hint.
//                }
//
//            };
//            class_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            spinnerClass.setAdapter(class_spinner_adapter);
//            spinnerClass.setSelection(class_spinner_adapter.getCount());
//            //spinner.setSelectedIndex(1);
//        } catch (JSONException e) {
//            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
//            dialog.dismiss();
//        }
//    }

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
            try {
                String[] strings = new String[classArrayList.size()];
                strings = classArrayList.toArray(strings);
                ArrayAdapter<String> class_spinner_adapter = new ArrayAdapter<>(this,R.layout.spinner_item, strings);
                class_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerClass.setAdapter(class_spinner_adapter);
                dialog.dismiss();
            }
            catch (IndexOutOfBoundsException e)
            {
                dialog.dismiss();
                Toast.makeText(this,"No class found !!!",Toast.LENGTH_LONG).show();
            }
            //spinner.setSelectedIndex(1);
        } catch (JSONException e) {
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
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
            try {
                String[] strings = new String[shiftArrayList.size()];
                strings = shiftArrayList.toArray(strings);
                ArrayAdapter<String> shift_spinner_adapter = new ArrayAdapter<>(this,R.layout.spinner_item, strings);
                shift_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerShift.setAdapter(shift_spinner_adapter);
                dialog.dismiss();
            }
            catch (IndexOutOfBoundsException e)
            {
                dialog.dismiss();
                Toast.makeText(this,"No shift found !!!",Toast.LENGTH_LONG).show();
            }
            //spinner.setSelectedIndex(1);
        } catch (JSONException e) {
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
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
            try {
                String[] strings = new String[sectionArrayList.size()];
                strings = sectionArrayList.toArray(strings);
                ArrayAdapter<String> section_spinner_adapter = new ArrayAdapter<>(this,R.layout.spinner_item, strings);
                section_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerSection.setAdapter(section_spinner_adapter);
                dialog.dismiss();
            }
            catch (IndexOutOfBoundsException e)
            {
                dialog.dismiss();
                Toast.makeText(this,"No section found !!!",Toast.LENGTH_LONG).show();
            }
            //spinner.setSelectedIndex(1);
        } catch (JSONException e) {
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
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
            try {
                String[] strings = new String[mediumnArrayList.size()];
                strings = mediumnArrayList.toArray(strings);
                ArrayAdapter<String> medium_spinner_adapter = new ArrayAdapter<>(this,R.layout.spinner_item, strings);
                medium_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerMedium.setAdapter(medium_spinner_adapter);
                dialog.dismiss();
            }
            catch (IndexOutOfBoundsException e)
            {
                dialog.dismiss();
                Toast.makeText(this,"No medium found !!!",Toast.LENGTH_LONG).show();
            }
            //spinner.setSelectedIndex(1);
        } catch (JSONException e) {
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
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
            try {
                String[] strings = new String[departmentArrayList.size()];
                strings = departmentArrayList.toArray(strings);
                ArrayAdapter<String> department_spinner_adapter = new ArrayAdapter<>(this,R.layout.spinner_item, strings);
                department_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerDepartment.setAdapter(department_spinner_adapter);
                dialog.dismiss();
            }
            catch (IndexOutOfBoundsException e)
            {
                dialog.dismiss();
                Toast.makeText(this,"No department found !!!",Toast.LENGTH_LONG).show();
            }
            //spinner.setSelectedIndex(1);
        } catch (JSONException e) {
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
            dialog.dismiss();
        }
    }
    void parseSubjectJsonData(String jsonString) {
        try {
            allSubjectArrayList = new ArrayList<>();
            JSONArray subjectJsonArray = new JSONArray(jsonString);
            ArrayList<String> subjectArrayList = new ArrayList<>();
            subjectArrayList.add("Select Subject");
            for(int i = 0; i < subjectJsonArray.length(); ++i) {
                JSONObject subjectJsonObject = subjectJsonArray.getJSONObject(i);
                SubjectModel subjectModel = new SubjectModel(subjectJsonObject.getString("SubjectID"), subjectJsonObject.getString("SubjectNo"),
                        subjectJsonObject.getString("SubjectName"), subjectJsonObject.getString("InsSubjectID"), subjectJsonObject.getString("InstituteID"),
                        subjectJsonObject.getString("DepartmentID"), subjectJsonObject.getString("MediumID"), subjectJsonObject.getString("ClassID"),
                        subjectJsonObject.getString("IsActive"), subjectJsonObject.getString("IsCombined"), subjectJsonObject.getString("ParentID"),
                        subjectJsonObject.getString("ParentSubject"));
//                Toast.makeText(this,classJsonObject.getString("ClassID")+classJsonObject.getString("ClassName"),Toast.LENGTH_LONG).show();
                allSubjectArrayList.add(subjectModel);
                subjectArrayList.add(subjectModel.getSubjectName());
            }
            try {
                String[] strings = new String[subjectArrayList.size()];
                strings = subjectArrayList.toArray(strings);
                ArrayAdapter<String> subject_spinner_adapter = new ArrayAdapter<>(this,R.layout.spinner_item, strings);
                subject_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerSubject.setAdapter(subject_spinner_adapter);
                dialog.dismiss();
            }
            catch (IndexOutOfBoundsException e)
            {
                dialog.dismiss();
                Toast.makeText(this,"No subject found !!!",Toast.LENGTH_LONG).show();
            }
            //spinner.setSelectedIndex(1);
        } catch (JSONException e) {
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
            dialog.dismiss();
        }
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<ExpandedMenuModel>();
        listDataChild = new HashMap<ExpandedMenuModel, List<String>>();

        ExpandedMenuModel menuNotice = new ExpandedMenuModel();
        menuNotice.setIconName("Notice");
        menuNotice.setIconImg(R.drawable.nav_notice);
        // Adding data header
        listDataHeader.add(menuNotice);

        ExpandedMenuModel menuRoutine = new ExpandedMenuModel();
        menuRoutine.setIconName("Routine");
        menuRoutine.setIconImg(R.drawable.nav_routine);
        listDataHeader.add(menuRoutine);

        ExpandedMenuModel menuAttendance = new ExpandedMenuModel();
        menuAttendance.setIconName("Atendance");
        menuAttendance.setIconImg(R.drawable.nav_attendance);
        listDataHeader.add(menuAttendance);

        ExpandedMenuModel menuSyllabus = new ExpandedMenuModel();
        menuSyllabus.setIconName("Syllabus");
        menuSyllabus.setIconImg(R.drawable.nav_syllabus);
        listDataHeader.add(menuSyllabus);

        ExpandedMenuModel menuExam = new ExpandedMenuModel();
        menuExam.setIconName("Exam");
        menuExam.setIconImg(R.drawable.nav_exam);
        listDataHeader.add(menuExam);

        ExpandedMenuModel menuResult = new ExpandedMenuModel();
        menuResult.setIconName("Result");
        menuResult.setIconImg(R.drawable.nav_result);
        listDataHeader.add(menuResult);

        ExpandedMenuModel menuContact = new ExpandedMenuModel();
        menuContact.setIconName("Contact");
        menuContact.setIconImg(R.drawable.nav_contact);
        listDataHeader.add(menuContact);

        ExpandedMenuModel menuiCard = new ExpandedMenuModel();
        menuiCard.setIconName("iCard");
        menuiCard.setIconImg(R.drawable.ic_person);
        listDataHeader.add(menuiCard);

        ExpandedMenuModel menuStudentList = new ExpandedMenuModel();
        menuStudentList.setIconName("Student List");
        menuStudentList.setIconImg(R.drawable.ic_action_users);
        listDataHeader.add(menuStudentList);

        // Adding child data
        List<String> headingNotice = new ArrayList<>();
        headingNotice.add("New Notice");
        headingNotice.add("Old Notice");

        List<String> headingRoutine = new ArrayList<>();
        headingRoutine.add("Mid Term Exam");
        headingRoutine.add("Final Exam");

        List<String> headingAttendance = new ArrayList<>();
        headingAttendance.add("Take Attendance");
        headingAttendance.add("Show Attendance");

        List<String> headingSyllabus = new ArrayList<>();
        List<String> headingExam = new ArrayList<>();
        List<String> headingResult = new ArrayList<>();
        List<String> headingContact = new ArrayList<>();
        List<String> headingiCard = new ArrayList<>();
        List<String> headingStudentList = new ArrayList<>();

        listDataChild.put(listDataHeader.get(0), headingNotice);// Header, Child data
        listDataChild.put(listDataHeader.get(1), headingRoutine);
        listDataChild.put(listDataHeader.get(2), headingAttendance);
        listDataChild.put(listDataHeader.get(3), headingSyllabus);
        listDataChild.put(listDataHeader.get(4), headingExam);
        listDataChild.put(listDataHeader.get(5), headingResult);
        listDataChild.put(listDataHeader.get(6), headingContact);
        listDataChild.put(listDataHeader.get(7), headingiCard);
        listDataChild.put(listDataHeader.get(8), headingStudentList);

    }

    private void setupDrawerContent(NavigationView navigationView) {
        //revision: this don't works, use setOnChildClickListener() and setOnGroupClickListener() above instead
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent mainIntent = new Intent(TakeAttendance.this,TeacherMainScreen.class);
            startActivity(mainIntent);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            sharedPreferences  = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("LogInState", false);
            editor.commit();
            Intent intent = new Intent(TakeAttendance.this, LoginScreen.class);
            startActivity(intent);
            finish();
            return true;
        }

        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
