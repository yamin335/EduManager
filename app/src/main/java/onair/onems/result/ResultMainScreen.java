package onair.onems.result;

import android.app.ProgressDialog;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.preference.PreferenceManager;

import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;

import android.view.LayoutInflater;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
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
import onair.onems.Services.StaticHelperClass;
import onair.onems.mainactivities.SideNavigationMenuParentActivity;
import onair.onems.mainactivities.StudentMainScreen;
import onair.onems.mainactivities.TeacherMainScreen;
import onair.onems.models.ClassModel;
import onair.onems.models.DepartmentModel;
import onair.onems.models.ExamModel;
import onair.onems.models.MediumModel;
import onair.onems.models.SectionModel;
import onair.onems.models.SessionModel;
import onair.onems.models.ShiftModel;
import onair.onems.network.MySingleton;

public class ResultMainScreen extends SideNavigationMenuParentActivity{

    private Spinner spinnerClass, spinnerShift, spinnerSection,
            spinnerMedium, spinnerDepartment, spinnerStudent,
            spinnerSession, spinnerExam;
    private ProgressDialog mShiftDialog, mMediumDialog, mClassDialog,
            mDepartmentDialog, mSectionDialog, mStudentListGetDialog,
            mSessionDialog, mExamDialog;

    private ArrayList<ClassModel> allClassArrayList;
    private ArrayList<ShiftModel> allShiftArrayList;
    private ArrayList<SectionModel> allSectionArrayList;
    private ArrayList<MediumModel> allMediumArrayList;
    private ArrayList<DepartmentModel> allDepartmentArrayList;
    private ArrayList<SessionModel> allSessionArrayList;
    private ArrayList<ExamModel> allExamArrayList;

    private String[] tempClassArray = {"Select Class"};
    private String[] tempShiftArray = {"Select Shift"};
    private String[] tempSectionArray = {"Select Section"};
    private String[] tempDepartmentArray = {"Select Department"};
    private String[] tempMediumArray = {"Select Medium"};
    private String[] tempStudentArray = {"Select Student"};
    private String[] tempSessionArray = {"Select Session"};
    private String[] tempExamArray = {"Select Exam"};

    private ClassModel selectedClass;
    private ShiftModel selectedShift;
    private SectionModel selectedSection;
    private MediumModel selectedMedium;
    private DepartmentModel selectedDepartment;
    private SessionModel selectedSession;
    private ExamModel selectedExam;
    private JSONObject selectedStudent;

    private long InstituteID;

    private int firstClass = 0, firstShift = 0, firstSection = 0, firstMedium = 0,
            firstDepartment = 0, firstSession = 0, firstExam = 0;

    private int UserTypeID;

    private JSONArray allStudentJsonArray;
    private String UserID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityName = ResultMainScreen.class.getName();

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View childActivityLayout = inflater.inflate(R.layout.result_content_main, null);
        LinearLayout parentActivityLayout = (LinearLayout) findViewById(R.id.contentMain);
        parentActivityLayout.addView(childActivityLayout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        InstituteID = prefs.getLong("InstituteID",0);
        UserTypeID = prefs.getInt("UserTypeID",0);
        UserID = prefs.getString("UserID","");

        selectedClass = new ClassModel();
        selectedShift = new ShiftModel();
        selectedSection = new SectionModel();
        selectedMedium = new MediumModel();
        selectedDepartment = new DepartmentModel();
        selectedSession = new SessionModel();
        selectedExam = new ExamModel();
        selectedStudent = null;

        spinnerClass = (Spinner)findViewById(R.id.spinnerClass);
        spinnerShift = (Spinner)findViewById(R.id.spinnerShift);
        spinnerSection = (Spinner)findViewById(R.id.spinnerSection);
        spinnerMedium =(Spinner)findViewById(R.id.spinnerMedium);
        spinnerDepartment =(Spinner)findViewById(R.id.spinnerDepartment);
        spinnerStudent = (Spinner)findViewById(R.id.spinnerStudent);
        spinnerSession = (Spinner)findViewById(R.id.spinnerSession);
        spinnerExam = (Spinner)findViewById(R.id.spinnerExam);

        Button showResult = (Button)findViewById(R.id.showResult);

        ArrayAdapter<String> class_spinner_adapter = new ArrayAdapter<>(this, R.layout.spinner_item, tempClassArray);
        class_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClass.setAdapter(class_spinner_adapter);

        ArrayAdapter<String> shift_spinner_adapter = new ArrayAdapter<>(this, R.layout.spinner_item, tempShiftArray);
        shift_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerShift.setAdapter(shift_spinner_adapter);

        ArrayAdapter<String> section_spinner_adapter = new ArrayAdapter<>(this, R.layout.spinner_item, tempSectionArray);
        section_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSection.setAdapter(section_spinner_adapter);

        ArrayAdapter<String> department_spinner_adapter = new ArrayAdapter<>(this, R.layout.spinner_item, tempDepartmentArray);
        department_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDepartment.setAdapter(department_spinner_adapter);

        ArrayAdapter<String> medium_spinner_adapter = new ArrayAdapter<>(this, R.layout.spinner_item, tempMediumArray);
        medium_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMedium.setAdapter(medium_spinner_adapter);

        ArrayAdapter<String> student_spinner_adapter = new ArrayAdapter<>(this, R.layout.spinner_item, tempStudentArray);
        student_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStudent.setAdapter(student_spinner_adapter);

        ArrayAdapter<String> session_spinner_adapter = new ArrayAdapter<>(this, R.layout.spinner_item, tempSessionArray);
        session_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSession.setAdapter(session_spinner_adapter);

        ArrayAdapter<String> exam_spinner_adapter = new ArrayAdapter<>(this, R.layout.spinner_item, tempExamArray);
        exam_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerExam.setAdapter(exam_spinner_adapter);

        spinnerSession.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != 0) {
                    try {
                        selectedSession = allSessionArrayList.get(position-1);
                        selectedStudent = null;
                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(ResultMainScreen.this,"No shift found !!!",Toast.LENGTH_LONG).show();
                    }
                } else {
                    if(firstSession++>1) {
                        selectedSession = new SessionModel();
                        selectedStudent = null;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerShift.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != 0) {
                    try {
                        selectedShift = allShiftArrayList.get(position-1);
                        selectedStudent = null;
                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(ResultMainScreen.this,"No shift found !!!",Toast.LENGTH_LONG).show();
                    }
                } else {
                    if(firstShift++>1) {
                        selectedShift = new ShiftModel();
                        selectedStudent = null;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerMedium.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != 0) {
                    try {
                        selectedMedium = allMediumArrayList.get(position-1);
                        selectedClass = new ClassModel();
                        selectedDepartment = new DepartmentModel();
                        selectedSection = new SectionModel();
                        ClassDataGetRequest();
                        selectedStudent = null;
                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(ResultMainScreen.this,"No medium found !!!",Toast.LENGTH_LONG).show();
                    }
                } else {
                    if(firstMedium++>1) {
                        selectedMedium = new MediumModel();
                        selectedClass = new ClassModel();
                        selectedDepartment = new DepartmentModel();
                        selectedSection = new SectionModel();
                        selectedStudent = null;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != 0) {
                    try {
                        selectedClass = allClassArrayList.get(position-1);
                        selectedDepartment = new DepartmentModel();
                        selectedSection = new SectionModel();
                        DepartmentDataGetRequest();
                        selectedStudent = null;
                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(ResultMainScreen.this,"No class found !!!",Toast.LENGTH_LONG).show();
                    }
                } else {
                    if(firstClass++>1) {
                        selectedClass = new ClassModel();
                        selectedDepartment = new DepartmentModel();
                        selectedSection = new SectionModel();
                        selectedStudent = null;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerDepartment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != 0) {
                    try {
                        selectedDepartment = allDepartmentArrayList.get(position-1);
                        selectedSection = new SectionModel();
                        SectionDataGetRequest();
                        selectedStudent = null;
                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(ResultMainScreen.this,"No shift found !!!",Toast.LENGTH_LONG).show();
                    }
                } else {
                    if(firstDepartment++>1) {
                        selectedDepartment = new DepartmentModel();
                        selectedSection = new SectionModel();
                        selectedStudent = null;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        spinnerSection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != 0) {
                    try {
                        selectedSection = allSectionArrayList.get(position-1);
                        selectedStudent = null;
                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(ResultMainScreen.this,"No section found !!!",Toast.LENGTH_LONG).show();
                    }
                } else {
                    if(firstSection++>1) {
                        selectedSection = new SectionModel();
                        selectedStudent = null;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerExam.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != 0) {
                    try {
                        selectedExam = allExamArrayList.get(position-1);
                        GetStudentListPostRequest();
                        selectedStudent = null;
                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(ResultMainScreen.this,"No Exam found !!!",Toast.LENGTH_LONG).show();
                    }
                } else {
                    if(firstExam++>1) {
                        selectedExam = new ExamModel();
                        selectedStudent = null;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerStudent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != 0) {
                    try {
                        try {
                            selectedStudent = allStudentJsonArray.getJSONObject(position-1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(ResultMainScreen.this,"No student found !!!",Toast.LENGTH_LONG).show();
                    }
                } else {
                    selectedStudent = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        showResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(StaticHelperClass.isNetworkAvailable(ResultMainScreen.this)) {
                    if(selectedStudent != null){
                        try {
                            Intent intent = new Intent(ResultMainScreen.this, SubjectWiseResult.class);
                            intent.putExtra("UserID", selectedStudent.getString("UserID"));
                            intent.putExtra("ShiftID", Long.toString(selectedShift.getShiftID()));
                            intent.putExtra("MediumID", Long.toString(selectedMedium.getMediumID()));
                            intent.putExtra("ClassID", Long.toString(selectedClass.getClassID()));
                            intent.putExtra("DepartmentID", Long.toString(selectedDepartment.getDepartmentID()));
                            intent.putExtra("SectionID", Long.toString(selectedSection.getSectionID()));
                            intent.putExtra("SessionID", Long.toString(selectedSession.getSessionID()));
                            intent.putExtra("ExamID", Long.toString(selectedExam.getExamID()));
                            intent.putExtra("InstituteID", Long.toString(InstituteID));
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(ResultMainScreen.this,"Please select a student!!! ",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(ResultMainScreen.this,"Please check your internet connection and select again!!! ",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        SessionDataGetRequest();
        ShiftDataGetRequest();
        MediumDataGetRequest();
        ExamDataGetRequest();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(UserTypeID == 1) {
            Intent mainIntent = new Intent(ResultMainScreen.this,TeacherMainScreen.class);
            startActivity(mainIntent);
            finish();
        } else if(UserTypeID == 2) {
            Intent mainIntent = new Intent(ResultMainScreen.this,TeacherMainScreen.class);
            startActivity(mainIntent);
            finish();
        } else if(UserTypeID == 3) {
            Intent mainIntent = new Intent(ResultMainScreen.this,StudentMainScreen.class);
            startActivity(mainIntent);
            finish();
        } else if(UserTypeID == 4) {
            Intent mainIntent = new Intent(ResultMainScreen.this,TeacherMainScreen.class);
            startActivity(mainIntent);
            finish();
        } else if(UserTypeID == 5) {
            Intent mainIntent = new Intent(ResultMainScreen.this,StudentMainScreen.class);
            startActivity(mainIntent);
            finish();
        }
    }

    void parseSessionJsonData(String jsonString) {
        try {
            allSessionArrayList = new ArrayList<>();
            JSONArray sessionJsonArray = new JSONArray(jsonString);
            ArrayList<String> sessionArrayList = new ArrayList<>();
            sessionArrayList.add("Select Session");
            for(int i = 0; i < sessionJsonArray.length(); ++i) {
                JSONObject sessionJsonObject = sessionJsonArray.getJSONObject(i);
                SessionModel sessionModel = new SessionModel(sessionJsonObject.getString("SessionID"), sessionJsonObject.getString("SessionName"));
                allSessionArrayList.add(sessionModel);
                sessionArrayList.add(sessionModel.getSessionName());
            }
            if(allSessionArrayList.size() == 1){
                selectedSession = allSessionArrayList.get(0);
            }
            try {
                String[] strings = new String[sessionArrayList.size()];
                strings = sessionArrayList.toArray(strings);
                ArrayAdapter<String> session_spinner_adapter = new ArrayAdapter<>(this,R.layout.spinner_item, strings);
                session_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerSession.setAdapter(session_spinner_adapter);
                mSessionDialog.dismiss();
            } catch (IndexOutOfBoundsException e) {
                mSessionDialog.dismiss();
                Toast.makeText(this,"No section found !!!",Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            mSessionDialog.dismiss();
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
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
                allShiftArrayList.add(shiftModel);
                shiftArrayList.add(shiftModel.getShiftName());
            }
            if(allShiftArrayList.size() == 1){
                selectedShift = allShiftArrayList.get(0);
            }
            try {
                String[] strings = new String[shiftArrayList.size()];
                strings = shiftArrayList.toArray(strings);
                ArrayAdapter<String> shift_spinner_adapter = new ArrayAdapter<>(this,R.layout.spinner_item, strings);
                shift_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerShift.setAdapter(shift_spinner_adapter);
                mShiftDialog.dismiss();
            } catch (IndexOutOfBoundsException e) {
                mShiftDialog.dismiss();
                Toast.makeText(this,"No shift found !!!",Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            mShiftDialog.dismiss();
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
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
                        mediumJsonObject.getString("IsDefault"));
                allMediumArrayList.add(mediumModel);
                mediumnArrayList.add(mediumModel.getMameName());
            }
            if(allMediumArrayList.size() == 1){
                selectedMedium = allMediumArrayList.get(0);
                ClassDataGetRequest();
            }
            try {
                String[] strings = new String[mediumnArrayList.size()];
                strings = mediumnArrayList.toArray(strings);
                ArrayAdapter<String> medium_spinner_adapter = new ArrayAdapter<>(this,R.layout.spinner_item, strings);
                medium_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerMedium.setAdapter(medium_spinner_adapter);
                mMediumDialog.dismiss();
            } catch (IndexOutOfBoundsException e) {
                mMediumDialog.dismiss();
                Toast.makeText(this,"No medium found !!!",Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            mMediumDialog.dismiss();
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
        }
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
                allClassArrayList.add(classModel);
                classArrayList.add(classModel.getClassName());
            }
            if(allClassArrayList.size() == 1) {
                selectedClass = allClassArrayList.get(0);
                DepartmentDataGetRequest();
            }
            try {
                String[] strings = new String[classArrayList.size()];
                strings = classArrayList.toArray(strings);
                ArrayAdapter<String> class_spinner_adapter = new ArrayAdapter<>(this,R.layout.spinner_item, strings);
                class_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerClass.setAdapter(class_spinner_adapter);
                mClassDialog.dismiss();
            } catch (IndexOutOfBoundsException e) {
                mClassDialog.dismiss();
                Toast.makeText(this,"No class found !!!",Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            mClassDialog.dismiss();
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
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
                allDepartmentArrayList.add(departmentModel);
                departmentArrayList.add(departmentModel.getDepartmentName());
            }
            if(allDepartmentArrayList.size() == 1){
                selectedDepartment = allDepartmentArrayList.get(0);
                SectionDataGetRequest();
            }
            if(allDepartmentArrayList.size() == 0){
                SectionDataGetRequest();
            }
            try {
                String[] strings = new String[departmentArrayList.size()];
                strings = departmentArrayList.toArray(strings);
                ArrayAdapter<String> department_spinner_adapter = new ArrayAdapter<>(this,R.layout.spinner_item, strings);
                department_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerDepartment.setAdapter(department_spinner_adapter);
                mDepartmentDialog.dismiss();
            } catch (IndexOutOfBoundsException e) {
                mDepartmentDialog.dismiss();
                Toast.makeText(this,"No department found !!!",Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            mDepartmentDialog.dismiss();
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
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
                allSectionArrayList.add(sectionModel);
                sectionArrayList.add(sectionModel.getSectionName());
            }
            if(allSectionArrayList.size() == 1){
                selectedSection = allSectionArrayList.get(0);
            }
            try {
                String[] strings = new String[sectionArrayList.size()];
                strings = sectionArrayList.toArray(strings);
                ArrayAdapter<String> section_spinner_adapter = new ArrayAdapter<>(this,R.layout.spinner_item, strings);
                section_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerSection.setAdapter(section_spinner_adapter);
                mSectionDialog.dismiss();
            } catch (IndexOutOfBoundsException e) {
                mSectionDialog.dismiss();
                Toast.makeText(this,"No section found !!!",Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            mSectionDialog.dismiss();
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
        }
    }

    void parseExamJsonData(String jsonString) {
        try {
            allExamArrayList = new ArrayList<>();
            JSONArray examJsonArray = new JSONArray(jsonString);
            ArrayList<String> examArrayList = new ArrayList<>();
            examArrayList.add("Select Exam");
            for(int i = 0; i < examJsonArray.length(); ++i) {
                JSONObject examJsonObject = examJsonArray.getJSONObject(i);
                if(Integer.parseInt(examJsonObject.getString("IsActive")) == 1) {
                    ExamModel examModel = new ExamModel(examJsonObject.getString("ExamID"), examJsonObject.getString("ExamName"),
                            examJsonObject.getString("InsExamID"), examJsonObject.getString("CustomName")
                            , examJsonObject.getString("IsActive"));
                    allExamArrayList.add(examModel);
                    examArrayList.add(examModel.getExamName());
                }
            }
            if(allExamArrayList.size() == 1){
                selectedExam = allExamArrayList.get(0);
            }
            try {
                String[] strings = new String[examArrayList.size()];
                strings = examArrayList.toArray(strings);
                ArrayAdapter<String> exam_spinner_adapter = new ArrayAdapter<>(this,R.layout.spinner_item, strings);
                exam_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerExam.setAdapter(exam_spinner_adapter);
                mExamDialog.dismiss();
            } catch (IndexOutOfBoundsException e) {
                mExamDialog.dismiss();
                Toast.makeText(this,"No section found !!!",Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            mExamDialog.dismiss();
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
        }
    }

    void parseStudentJsonData(String jsonString) {
        try {
            allStudentJsonArray = new JSONArray();
            JSONArray studentJsonArray = new JSONArray(jsonString);
            ArrayList<String> studentArrayList = new ArrayList<>();
            studentArrayList.add("Select Student");
            for(int i = 0; i < studentJsonArray.length(); ++i) {
                JSONObject student = new JSONObject();
                JSONObject studentJsonObject = studentJsonArray.getJSONObject(i);
                studentArrayList.add(studentJsonObject.getString("UserName"));
                student.put("UserID", studentJsonObject.getString("UserID"));
                allStudentJsonArray.put(student);
            }
            try {
                String[] strings = new String[studentArrayList.size()];
                strings = studentArrayList.toArray(strings);
                ArrayAdapter<String> student_spinner_adapter = new ArrayAdapter<>(this,R.layout.spinner_item, strings);
                student_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerStudent.setAdapter(student_spinner_adapter);
                mStudentListGetDialog.dismiss();
            } catch (IndexOutOfBoundsException e) {
                mStudentListGetDialog.dismiss();
                Toast.makeText(this,"No student found !!!",Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            mStudentListGetDialog.dismiss();
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
        }
    }

    private void SessionDataGetRequest() {
        if (StaticHelperClass.isNetworkAvailable(this)) {
            String sessionUrl = getString(R.string.baseUrl)+"/api/onEms/getallsession";

            mSessionDialog = new ProgressDialog(this);
            mSessionDialog.setTitle("Loading session...");
            mSessionDialog.setMessage("Please Wait...");
            mSessionDialog.setCancelable(false);
            mSessionDialog.setIcon(R.drawable.onair);
            mSessionDialog.show();
            //Preparing Shift data from server
            StringRequest stringSessionRequest = new StringRequest(Request.Method.GET, sessionUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            parseSessionJsonData(response);

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mSessionDialog.dismiss();
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
            MySingleton.getInstance(this).addToRequestQueue(stringSessionRequest);
        } else {
            Toast.makeText(ResultMainScreen.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void ShiftDataGetRequest() {
        if (StaticHelperClass.isNetworkAvailable(this)) {
            String shiftUrl = getString(R.string.baseUrl)+"/api/onEms/getInsShift/"+InstituteID;

            mShiftDialog = new ProgressDialog(this);
            mShiftDialog.setTitle("Loading shift...");
            mShiftDialog.setMessage("Please Wait...");
            mShiftDialog.setCancelable(true);
            mShiftDialog.setIcon(R.drawable.onair);
            mShiftDialog.show();
            //Preparing Shift data from server
            StringRequest stringShiftRequest = new StringRequest(Request.Method.GET, shiftUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            parseShiftJsonData(response);

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mShiftDialog.dismiss();
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
            MySingleton.getInstance(this).addToRequestQueue(stringShiftRequest);
        } else {
            Toast.makeText(ResultMainScreen.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void MediumDataGetRequest() {
        if(StaticHelperClass.isNetworkAvailable(this)) {
            String mediumUrl = getString(R.string.baseUrl)+"/api/onEms/getInstituteMediumDdl/"+InstituteID;

            mMediumDialog = new ProgressDialog(this);
            mMediumDialog.setTitle("Loading medium...");
            mMediumDialog.setMessage("Please Wait...");
            mMediumDialog.setCancelable(true);
            mMediumDialog.setIcon(R.drawable.onair);
            mMediumDialog.show();
            //Preparing Medium data from server
            StringRequest stringMediumRequest = new StringRequest(Request.Method.GET, mediumUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            parseMediumJsonData(response);

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mMediumDialog.dismiss();
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
            MySingleton.getInstance(this).addToRequestQueue(stringMediumRequest);
        } else {
            Toast.makeText(ResultMainScreen.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void ClassDataGetRequest() {
        if(StaticHelperClass.isNetworkAvailable(this)) {

            CheckSelectedData();

            String classUrl = getString(R.string.baseUrl)+"/api/onEms/MediumWiseClassDDL/"+InstituteID+"/"+selectedMedium.getMediumID();

            mClassDialog = new ProgressDialog(this);
            mClassDialog.setTitle("Loading class...");
            mClassDialog.setMessage("Please Wait...");
            mClassDialog.setCancelable(true);
            mClassDialog.setIcon(R.drawable.onair);
            mClassDialog.show();
            //Preparing claas data from server
            StringRequest stringClassRequest = new StringRequest(Request.Method.GET, classUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            parseClassJsonData(response);

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mClassDialog.dismiss();
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
            MySingleton.getInstance(this).addToRequestQueue(stringClassRequest);
        } else {
            Toast.makeText(ResultMainScreen.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void DepartmentDataGetRequest() {
        if(StaticHelperClass.isNetworkAvailable(this)) {

            CheckSelectedData();

            String departmentUrl = getString(R.string.baseUrl)+"/api/onEms/ClassWiseDepartmentDDL/"+InstituteID+"/"+
                    selectedClass.getClassID()+"/"+selectedMedium.getMediumID();

            mDepartmentDialog = new ProgressDialog(this);
            mDepartmentDialog.setTitle("Loading department...");
            mDepartmentDialog.setMessage("Please Wait...");
            mDepartmentDialog.setCancelable(true);
            mDepartmentDialog.setIcon(R.drawable.onair);
            mDepartmentDialog.show();
            //Preparing Department data from server
            StringRequest stringDepartmentRequest = new StringRequest(Request.Method.GET, departmentUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            parseDepartmentJsonData(response);

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mDepartmentDialog.dismiss();
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
            MySingleton.getInstance(this).addToRequestQueue(stringDepartmentRequest);
        } else {
            Toast.makeText(ResultMainScreen.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void SectionDataGetRequest() {
        if(StaticHelperClass.isNetworkAvailable(this)) {

            CheckSelectedData();

            String sectionUrl = getString(R.string.baseUrl)+"/api/onEms/getInsSection/"+InstituteID+"/"+
                    selectedClass.getClassID()+"/"+selectedDepartment.getDepartmentID();

            mSectionDialog = new ProgressDialog(this);
            mSectionDialog.setTitle("Loading section...");
            mSectionDialog.setMessage("Please Wait...");
            mSectionDialog.setCancelable(true);
            mSectionDialog.setIcon(R.drawable.onair);
            mSectionDialog.show();
            //Preparing section data from server
            StringRequest stringSectionRequest = new StringRequest(Request.Method.GET, sectionUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            parseSectionJsonData(response);

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mSectionDialog.dismiss();
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
            MySingleton.getInstance(this).addToRequestQueue(stringSectionRequest);
        } else {
            Toast.makeText(ResultMainScreen.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void ExamDataGetRequest() {
        if (StaticHelperClass.isNetworkAvailable(this)) {

            String examUrl = getString(R.string.baseUrl)+"/api/onEms/getinsExameID/"+InstituteID;

            mExamDialog = new ProgressDialog(this);
            mExamDialog.setTitle("Loading session...");
            mExamDialog.setMessage("Please Wait...");
            mExamDialog.setCancelable(false);
            mExamDialog.setIcon(R.drawable.onair);
            mExamDialog.show();
            //Preparing Shift data from server
            StringRequest stringExamRequest = new StringRequest(Request.Method.GET, examUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            parseExamJsonData(response);

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mExamDialog.dismiss();
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
            MySingleton.getInstance(this).addToRequestQueue(stringExamRequest);
        } else {
            Toast.makeText(ResultMainScreen.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void GetStudentListPostRequest() {
        if(StaticHelperClass.isNetworkAvailable(this)) {
            mStudentListGetDialog = new ProgressDialog(this);
            mStudentListGetDialog.setTitle("Loading session...");
            mStudentListGetDialog.setMessage("Please Wait...");
            mStudentListGetDialog.setCancelable(false);
            mStudentListGetDialog.setIcon(R.drawable.onair);
            mStudentListGetDialog.show();

            String getStudentDataPostUrl = getString(R.string.baseUrl)+"/api/onEms/getStudentList";

            //Preparing Student data from server
            StringRequest stringStudentListPostRequest = new StringRequest(Request.Method.POST, getStudentDataPostUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            parseStudentJsonData(response);

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mStudentListGetDialog.dismiss();
                }
            })
            {
                @Override
                protected Map<String, String> getParams()
                {
                    CheckSelectedData();
                    Map<String, String>  params = new HashMap<>();
                    params.put("pageNumber", "0");
                    params.put("pageSize", "10000");
                    params.put("IsPaging", "0");
                    params.put("InstituteID", Long.toString(InstituteID));
                    params.put("LoggedUserID", UserID);
                    params.put("UserID", "0");
                    params.put("ClassID", Long.toString(selectedClass.getClassID()));
                    params.put("SectionID", Long.toString(selectedSection.getSectionID()));
                    params.put("DepartmentID", Long.toString(selectedDepartment.getDepartmentID()));
                    params.put("MediumID", Long.toString(selectedMedium.getMediumID()));
                    params.put("ShiftID", Long.toString(selectedShift.getShiftID()));

                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String>  params = new HashMap<>();
                    params.put("Authorization", "Request_From_onEMS_Android_app");
                    return params;
                }
            };
            MySingleton.getInstance(this).addToRequestQueue(stringStudentListPostRequest);

        } else {
            Toast.makeText(ResultMainScreen.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void CheckSelectedData(){
        if(selectedClass.getClassID() == -2) {
            selectedClass.setClassID("0");
        }
        if(selectedShift.getShiftID() == -2) {
            selectedShift.setShiftID("0");
        }
        if(selectedSection.getSectionID() == -2) {
            selectedSection.setSectionID("0");
        }
        if(selectedMedium.getMediumID() == -2) {
            selectedMedium.setMediumID("0");
        }
        if(selectedDepartment.getDepartmentID() == -2) {
            selectedDepartment.setDepartmentID("0");
        }
    }


}
