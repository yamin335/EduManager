package onair.onems.mainactivities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import onair.onems.R;
import onair.onems.customadapters.ExpandableListAdapter;
import onair.onems.mainactivities.TeacherAttendanceShow.ShowAttendance;
import onair.onems.models.ClassModel;
import onair.onems.models.ExpandedMenuModel;
import onair.onems.models.MediumModel;
import onair.onems.models.SectionModel;
import onair.onems.models.ShiftModel;
import onair.onems.models.StudentInformation;
import onair.onems.models.SubjectModel;

/**
 * Created by User on 1/7/2018.
 */

public class StudentiCard extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    ExpandableListAdapter mMenuAdapter;
    ExpandableListView expandableList;
    List<ExpandedMenuModel> listDataHeader;
    HashMap<ExpandedMenuModel, List<String>> listDataChild;
    Spinner spinnerClass, spinnerShift,spinnerSection,spinnerMedium,spinnerStudent;
    Button showStudentData;
    ProgressDialog dialog;
    String classUrl, shiftUrl, sectionUrl, mediumUrl, studentUrl;
    ArrayList<ClassModel> allClassArrayList;
    ArrayList<ShiftModel> allShiftArrayList;
    ArrayList<SectionModel> allSectionArrayList;
    ArrayList<MediumModel> allMediumArrayList;
    ArrayList<StudentInformation> allStudentArrayList;

    String[] tempSectionArray = {"Select Section"};
    String[] tempStudentArray = {"Select Roll"};
    ClassModel selectedClass = null;
    ShiftModel selectedShift = null;
    SectionModel selectedSection = null;
    MediumModel selectedMedium = null;
    StudentInformation selectedStudent = null;
    int classSpinnerPosition, shiftSpinnerPosition, sectionSpinnerPosition, mediumSpinnerPosition, studentSpinnerPosition;

    long InstituteID;

    public static final String MyPREFERENCES = "LogInKey";
    public static SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.icard_activity_main);

        spinnerClass = (Spinner)findViewById(R.id.spinnerClass);
        spinnerShift = (Spinner)findViewById(R.id.spinnerShift);
        spinnerSection = (Spinner)findViewById(R.id.spinnerSection);
        spinnerMedium =(Spinner)findViewById(R.id.spinnerMedium);
        spinnerStudent = (Spinner)findViewById(R.id.spinnerStudent);
        showStudentData = (Button)findViewById(R.id.showStudentData);

        ArrayAdapter<String> section_spinner_adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, tempSectionArray);
        section_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSection.setAdapter(section_spinner_adapter);

        ArrayAdapter<String> student_spinner_adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, tempStudentArray);
        student_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStudent.setAdapter(student_spinner_adapter);

        allClassArrayList = new ArrayList<ClassModel>();
        allShiftArrayList = new ArrayList<ShiftModel>();
        allSectionArrayList = new ArrayList<SectionModel>();
        allMediumArrayList = new ArrayList<MediumModel>();
        allStudentArrayList = new ArrayList<StudentInformation>();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        InstituteID = prefs.getLong("InstituteID",0);

        classUrl = getString(R.string.baseUrlLocal)+"getInsClass/"+InstituteID;
        shiftUrl = getString(R.string.baseUrlLocal)+"getInsShift/"+InstituteID;
        mediumUrl = getString(R.string.baseUrlLocal)+"getInsMedium/"+InstituteID;

        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading...");
        dialog.setMessage("Please Wait...");
        dialog.setCancelable(false);
        dialog.show();

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

        spinnerClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != classSpinnerPosition)
                {
                    selectedSection = null;
                    selectedStudent = null;
                    selectedClass = allClassArrayList.get(position);
                    long classId = selectedClass.getClassID();
                    sectionUrl = getString(R.string.baseUrlLocal)+"getInsSection/"+InstituteID+"/"+classId;
                    dialog.show();
                    //Preparing section data from server
                    RequestQueue queueSection = Volley.newRequestQueue(StudentiCard.this);
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
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerShift.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != shiftSpinnerPosition)
                {
                    selectedShift = allShiftArrayList.get(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerSection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != sectionSpinnerPosition)
                {
                    selectedSection = allSectionArrayList.get(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerMedium.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != mediumSpinnerPosition)
                {
                    selectedStudent = null;
                    selectedMedium = allMediumArrayList.get(position);
                    long mediumId = selectedMedium.getMediumID();
                    studentUrl = getString(R.string.baseUrlLocal)+"getStudent"+"/"+InstituteID+"/"+
                            selectedClass.getClassID()+"/"+selectedSection.getSectionID()+"/"+
                            "0"+"/"+mediumId+"/"+selectedShift.getShiftID()+"/"+"0";
                    dialog.show();
                    //Preparing subject data from server
                    RequestQueue queueStudent = Volley.newRequestQueue(StudentiCard.this);
                    StringRequest stringStudentRequest = new StringRequest(Request.Method.GET, studentUrl,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    parseStudentJsonData(response);

                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            dialog.dismiss();
                        }
                    });
                    queueStudent.add(stringStudentRequest);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerStudent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != studentSpinnerPosition)
                {
                    selectedStudent = allStudentArrayList.get(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        showStudentData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((selectedClass == null)||(selectedMedium == null)||(selectedSection == null)||(selectedShift == null)||(selectedStudent == null))
                {
                    Toast.makeText(StudentiCard.this,"Please select all options !!! ",Toast.LENGTH_LONG).show();
                }
                else
                {
                    Bundle bundle = new Bundle();
                    bundle.putString("UserName", selectedStudent.getUserName());
                    bundle.putString("RollNo", selectedStudent.getRollNo());
                    bundle.putString("ImageUrl", selectedStudent.getImageUrl());
                    bundle.putString("ClassName", selectedStudent.getClassName());
                    bundle.putString("SectionName", selectedStudent.getSectionName());
                    bundle.putString("MameName", selectedStudent.getMameName());
                    bundle.putString("DepartmentName", selectedStudent.getDepartmentName());
                    bundle.putString("SessionName", selectedStudent.getSessionName());
                    bundle.putString("BoardName", selectedStudent.getBoardName());
                    bundle.putString("Guardian", selectedStudent.getGuardian());
                    bundle.putString("GuardianPhone", selectedStudent.getGuardianPhone());
                    bundle.putString("GuardianEmailID", selectedStudent.getGuardianEmailID());
                    bundle.putString("DOB", selectedStudent.getDOB());
                    bundle.putString("Gender", selectedStudent.getGender());
                    bundle.putString("Religion", selectedStudent.getReligion());
                    bundle.putString("PreAddress", selectedStudent.getPreAddress());

                    Intent intent = new Intent(StudentiCard.this, StudentDetails.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
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
        View view = getLayoutInflater().inflate(R.layout.icard_nav_header_main,null);
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
        Glide.with(this).load(getString(R.string.baseUrlRaw)+imageUrl.replace("\\","/"))
                .apply(RequestOptions.circleCropTransform()).into(profilePicture);
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
                    Intent intent = new Intent(StudentiCard.this, ShowAttendance.class);
                    startActivity(intent);
                }
                if((i == 2) && (i1 == 0) && (l == 0))
                {
                    Intent intent = new Intent(StudentiCard.this, TakeAttendance.class);
                    startActivity(intent);
                    finish();
//                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//                    if (drawer.isDrawerOpen(GravityCompat.START)) {
//                        drawer.closeDrawer(GravityCompat.START);
//                    }
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
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                }
                return false;
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    void parseClassJsonData(String jsonString) {
        try {
            JSONArray classJsonArray = new JSONArray(jsonString);
            ArrayList classArrayList = new ArrayList();
            for(int i = 0; i < classJsonArray.length(); ++i) {
                JSONObject classJsonObject = classJsonArray.getJSONObject(i);
                ClassModel classModel = new ClassModel(classJsonObject.getString("ClassID"), classJsonObject.getString("ClassName"));
//                Toast.makeText(this,classJsonObject.getString("ClassID")+classJsonObject.getString("ClassName"),Toast.LENGTH_LONG).show();
                allClassArrayList.add(classModel);
                classArrayList.add(classModel.getClassName());
            }
            classArrayList.add("Select Class");
            classSpinnerPosition = classArrayList.indexOf("Select Class");
            selectedClass = allClassArrayList.get(0);
            ArrayAdapter<ArrayList> class_spinner_adapter = new ArrayAdapter<ArrayList>(this,R.layout.spinner_item, classArrayList){

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {

                    View v = super.getView(position, convertView, parent);
                    if (position == getCount()) {
//                    ((TextView)v.findViewById(android.R.id.text1)).setText("");
//                    ((TextView)v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
                    }

                    return v;
                }

                @Override
                public int getCount() {
                    return super.getCount()-1; // you dont display last item. It is used as hint.
                }

            };
            class_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerClass.setAdapter(class_spinner_adapter);
            spinnerClass.setSelection(class_spinner_adapter.getCount());
            //spinner.setSelectedIndex(1);
        } catch (JSONException e) {
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
            dialog.dismiss();
        }
    }

    void parseShiftJsonData(String jsonString) {
        try {
            JSONArray shiftJsonArray = new JSONArray(jsonString);
            ArrayList shiftArrayList = new ArrayList();
            for(int i = 0; i < shiftJsonArray.length(); ++i) {
                JSONObject shiftJsonObject = shiftJsonArray.getJSONObject(i);
                ShiftModel shiftModel = new ShiftModel(shiftJsonObject.getString("ShiftID"), shiftJsonObject.getString("ShiftName"));
//                Toast.makeText(this,classJsonObject.getString("ClassID")+classJsonObject.getString("ClassName"),Toast.LENGTH_LONG).show();
                allShiftArrayList.add(shiftModel);
                shiftArrayList.add(shiftModel.getShiftName());
            }
            shiftArrayList.add("Select Shift");
            shiftSpinnerPosition = shiftArrayList.indexOf("Select Shift");
            selectedShift = allShiftArrayList.get(0);
            ArrayAdapter<ArrayList> shift_spinner_adapter = new ArrayAdapter<ArrayList>(this,R.layout.spinner_item, shiftArrayList){

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {

                    View v = super.getView(position, convertView, parent);
                    if (position == getCount()) {
//                    ((TextView)v.findViewById(android.R.id.text1)).setText("");
//                    ((TextView)v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
                    }

                    return v;
                }

                @Override
                public int getCount() {
                    return super.getCount()-1; // you dont display last item. It is used as hint.
                }

            };
            shift_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerShift.setAdapter(shift_spinner_adapter);
            spinnerShift.setSelection(shift_spinner_adapter.getCount());
            dialog.dismiss();
            //spinner.setSelectedIndex(1);
        } catch (JSONException e) {
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
            dialog.dismiss();
        }
    }

    void parseSectionJsonData(String jsonString) {
        try {
            JSONArray sectionJsonArray = new JSONArray(jsonString);
            ArrayList sectionArrayList = new ArrayList();
            for(int i = 0; i < sectionJsonArray.length(); ++i) {
                JSONObject sectionJsonObject = sectionJsonArray.getJSONObject(i);
                SectionModel sectionModel = new SectionModel(sectionJsonObject.getString("SectionID"), sectionJsonObject.getString("SectionName"));
//                Toast.makeText(this,classJsonObject.getString("ClassID")+classJsonObject.getString("ClassName"),Toast.LENGTH_LONG).show();
                allSectionArrayList.add(sectionModel);
                sectionArrayList.add(sectionModel.getSectionName());
            }
            sectionArrayList.add("Select Section");
            sectionSpinnerPosition = sectionArrayList.indexOf("Select Section");
            selectedSection = allSectionArrayList.get(0);
            ArrayAdapter<ArrayList> section_spinner_adapter = new ArrayAdapter<ArrayList>(this,R.layout.spinner_item, sectionArrayList){

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {

                    View v = super.getView(position, convertView, parent);
                    if (position == getCount()) {
//                    ((TextView)v.findViewById(android.R.id.text1)).setText("");
//                    ((TextView)v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
                    }

                    return v;
                }

                @Override
                public int getCount() {
                    return super.getCount()-1; // you dont display last item. It is used as hint.
                }

            };
            section_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerSection.setAdapter(section_spinner_adapter);
            spinnerSection.setSelection(section_spinner_adapter.getCount());
            dialog.dismiss();
            //spinner.setSelectedIndex(1);
        } catch (JSONException e) {
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
            dialog.dismiss();
        }
    }

    void parseMediumJsonData(String jsonString) {
        try {
            JSONArray mediumJsonArray = new JSONArray(jsonString);
            ArrayList mediumnArrayList = new ArrayList();
            for(int i = 0; i < mediumJsonArray.length(); ++i) {
                JSONObject mediumJsonObject = mediumJsonArray.getJSONObject(i);
                MediumModel mediumModel = new MediumModel(mediumJsonObject.getString("MediumID"), mediumJsonObject.getString("MameName"),
                        mediumJsonObject.getString("InstMediumID"), mediumJsonObject.getString("InstituteID"), mediumJsonObject.getString("IsActive"));
//                Toast.makeText(this,classJsonObject.getString("ClassID")+classJsonObject.getString("ClassName"),Toast.LENGTH_LONG).show();
                allMediumArrayList.add(mediumModel);
                mediumnArrayList.add(mediumModel.getMameName());
            }
            mediumnArrayList.add("Select Medium");
            mediumSpinnerPosition = mediumnArrayList.indexOf("Select Medium");
            selectedMedium = allMediumArrayList.get(0);
            ArrayAdapter<ArrayList> medium_spinner_adapter = new ArrayAdapter<ArrayList>(this,R.layout.spinner_item, mediumnArrayList){

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {

                    View v = super.getView(position, convertView, parent);
                    if (position == getCount()) {
//                    ((TextView)v.findViewById(android.R.id.text1)).setText("");
//                    ((TextView)v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
                    }

                    return v;
                }

                @Override
                public int getCount() {
                    return super.getCount()-1; // you dont display last item. It is used as hint.
                }

            };
            medium_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerMedium.setAdapter(medium_spinner_adapter);
            spinnerMedium.setSelection(medium_spinner_adapter.getCount());
            dialog.dismiss();
            //spinner.setSelectedIndex(1);
        } catch (JSONException e) {
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
            dialog.dismiss();
        }
    }

    void parseStudentJsonData(String jsonString) {
        try {
            JSONArray studentJsonArray = new JSONArray(jsonString);
            ArrayList studentArrayList = new ArrayList();
            for(int i = 0; i < studentJsonArray.length(); ++i) {
                JSONObject studentJsonObject = studentJsonArray.getJSONObject(i);
                StudentInformation studentInformation = new StudentInformation();
                studentInformation.setRollNo(studentJsonObject.getString("RollNo"));
                studentInformation.setUserName(studentJsonObject.getString("UserName"));
                studentInformation.setImageUrl(studentJsonObject.getString("ImageUrl"));
                studentInformation.setClassName(studentJsonObject.getString("ClassName"));
                studentInformation.setSectionName(studentJsonObject.getString("SectionName"));
                studentInformation.setMameName(studentJsonObject.getString("MameName"));
                studentInformation.setDepartmentName(studentJsonObject.getString("DepartmentName"));
                studentInformation.setSessionName(studentJsonObject.getString("SessionName"));
                studentInformation.setBoardName(studentJsonObject.getString("BoardName"));
                studentInformation.setGuardian(studentJsonObject.getString("Guardian"));
                studentInformation.setGuardianPhone(studentJsonObject.getString("GuardianPhone"));
                studentInformation.setGuardianEmailID(studentJsonObject.getString("GuardianEmailID"));
                studentInformation.setDOB(studentJsonObject.getString("DOB"));
                studentInformation.setGender(studentJsonObject.getString("Gender"));
                studentInformation.setReligion(studentJsonObject.getString("Religion"));
                studentInformation.setPreAddress(studentJsonObject.getString("PreAddress"));
//                Toast.makeText(this,classJsonObject.getString("ClassID")+classJsonObject.getString("ClassName"),Toast.LENGTH_LONG).show();
                allStudentArrayList.add(studentInformation);
                studentArrayList.add("Roll: "+studentInformation.getRollNo()+" ( "+studentInformation.getUserName()+" )");
            }
            studentArrayList.add("Select Roll");
            studentSpinnerPosition = studentArrayList.indexOf("Select Roll");
            selectedStudent = allStudentArrayList.get(0);
            ArrayAdapter<ArrayList> student_spinner_adapter = new ArrayAdapter<ArrayList>(this,R.layout.spinner_item, studentArrayList){

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {

                    View v = super.getView(position, convertView, parent);
                    if (position == getCount()) {
//                    ((TextView)v.findViewById(android.R.id.text1)).setText("");
//                    ((TextView)v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
                    }

                    return v;
                }

                @Override
                public int getCount() {
                    return super.getCount()-1; // you dont display last item. It is used as hint.
                }

            };
            student_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerStudent.setAdapter(student_spinner_adapter);
            spinnerStudent.setSelection(student_spinner_adapter.getCount());
            dialog.dismiss();
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
        menuiCard.setIconName("Student Information (iCard)");
        menuiCard.setIconImg(R.drawable.ic_perm_identity_menu);
        listDataHeader.add(menuiCard);

        // Adding child data
        List<String> headingNotice = new ArrayList<String>();
        headingNotice.add("New Notice");
        headingNotice.add("Old Notice");

        List<String> headingRoutine = new ArrayList<String>();
        headingRoutine.add("Mid Term Exam");
        headingRoutine.add("Final Exam");

        List<String> headingAttendance = new ArrayList<String>();
        headingAttendance.add("Take Attendance");
        headingAttendance.add("Show Attendance");

        List<String> headingSyllabus = new ArrayList<String>();
        List<String> headingExam = new ArrayList<String>();
        List<String> headingResult = new ArrayList<String>();
        List<String> headingContact = new ArrayList<String>();
        List<String> headingiCard = new ArrayList<String>();

        listDataChild.put(listDataHeader.get(0), headingNotice);// Header, Child data
        listDataChild.put(listDataHeader.get(1), headingRoutine);
        listDataChild.put(listDataHeader.get(2), headingAttendance);
        listDataChild.put(listDataHeader.get(3), headingSyllabus);
        listDataChild.put(listDataHeader.get(4), headingExam);
        listDataChild.put(listDataHeader.get(5), headingResult);
        listDataChild.put(listDataHeader.get(6), headingContact);
        listDataChild.put(listDataHeader.get(7), headingiCard);

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
            super.onBackPressed();
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
            Intent intent = new Intent(StudentiCard.this, LoginScreen.class);
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
}