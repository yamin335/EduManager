package onair.onems.icard;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import onair.onems.R;
import onair.onems.Services.RetrofitNetworkService;
import onair.onems.Services.StaticHelperClass;
import onair.onems.attendance.ShowAttendance;
import onair.onems.mainactivities.SideNavigationMenuParentActivity;
import onair.onems.mainactivities.TeacherMainScreen;
import onair.onems.models.ClassModel;
import onair.onems.models.DepartmentModel;
import onair.onems.models.MediumModel;
import onair.onems.models.SectionModel;
import onair.onems.models.SessionModel;
import onair.onems.models.ShiftModel;
import onair.onems.models.SpinnerStudentInformation;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class StudentiCardMain extends SideNavigationMenuParentActivity {

    private Spinner spinnerClass, spinnerShift, spinnerSection, spinnerMedium, spinnerDepartment, spinnerStudent, spinnerSession;

    private ArrayList<SessionModel> allSessionArrayList;
    private ArrayList<ClassModel> allClassArrayList;
    private ArrayList<ShiftModel> allShiftArrayList;
    private ArrayList<SectionModel> allSectionArrayList;
    private ArrayList<MediumModel> allMediumArrayList;
    private ArrayList<DepartmentModel> allDepartmentArrayList;
    private ArrayList<SpinnerStudentInformation> allStudentArrayList;

    private String[] tempSessionArray = {"Select Session"};
    private String[] tempClassArray = {"Select Class"};
    private String[] tempShiftArray = {"Select Shift"};
    private String[] tempSectionArray = {"Select Section"};
    private String[] tempDepartmentArray = {"Select Department"};
    private String[] tempMediumArray = {"Select Medium"};
    private String[] tempStudentArray = {"Select Student"};

    private SessionModel selectedSession = null;
    private ClassModel selectedClass;
    private ShiftModel selectedShift;
    private SectionModel selectedSection;
    private MediumModel selectedMedium;
    private DepartmentModel selectedDepartment;
    private SpinnerStudentInformation selectedStudent = null;

    private long InstituteID;

    private int firstClass = 0, firstShift = 0, firstSection = 0, firstMedium = 0,
            firstDepartment = 0;
    private CompositeDisposable finalDisposer = new CompositeDisposable();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!finalDisposer.isDisposed())
            finalDisposer.dispose();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityName = StudentiCardMain.class.getName();

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View childActivityLayout = Objects.requireNonNull(inflater).inflate(R.layout.icard_main_screen, null);
        LinearLayout parentActivityLayout = findViewById(R.id.contentMain);
        parentActivityLayout.addView(childActivityLayout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        InstituteID = prefs.getLong("InstituteID",0);

        selectedSession = new SessionModel();
        selectedClass = new ClassModel();
        selectedShift = new ShiftModel();
        selectedSection = new SectionModel();
        selectedMedium = new MediumModel();
        selectedDepartment = new DepartmentModel();

        Button showStudentData, newEntry, editStudentData;

        spinnerSession = findViewById(R.id.spinnerSession);
        spinnerClass = findViewById(R.id.spinnerClass);
        spinnerShift = findViewById(R.id.spinnerShift);
        spinnerSection = findViewById(R.id.spinnerSection);
        spinnerMedium = findViewById(R.id.spinnerMedium);
        spinnerDepartment = findViewById(R.id.spinnerDepartment);
        spinnerStudent =  findViewById(R.id.spinnerStudent);

        showStudentData = findViewById(R.id.showStudentData);
        editStudentData = findViewById(R.id.editStudentInfo);
        newEntry = findViewById(R.id.newEntry);

        ArrayAdapter<String> session_spinner_adapter = new ArrayAdapter<>(this, R.layout.spinner_item, tempSessionArray);
        session_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSession.setAdapter(session_spinner_adapter);

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

        SessionDataGetRequest();
        ShiftDataGetRequest();
        MediumDataGetRequest();

        spinnerSession.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != 0) {
                    try {
                        selectedSession = allSessionArrayList.get(position-1);
                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(StudentiCardMain.this,"No session found !!!",Toast.LENGTH_LONG).show();
                    }
                } else {
                    selectedSession = new SessionModel();
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
                        Toast.makeText(StudentiCardMain.this,"No shift found !!!",Toast.LENGTH_LONG).show();
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
//                        StudentDataGetRequest();
                        selectedStudent = null;
                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(StudentiCardMain.this,"No medium found !!!",Toast.LENGTH_LONG).show();
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
                        StudentDataGetRequest();
                        selectedStudent = null;
                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(StudentiCardMain.this,"No class found !!!",Toast.LENGTH_LONG).show();
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
                        StudentDataGetRequest();
                        selectedStudent = null;
                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(StudentiCardMain.this,"No shift found !!!",Toast.LENGTH_LONG).show();
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
                        StudentDataGetRequest();
                        selectedStudent = null;
                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(StudentiCardMain.this,"No section found !!!",Toast.LENGTH_LONG).show();
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

        spinnerStudent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != 0) {
                    try {
                        selectedStudent = allStudentArrayList.get(position-1);
                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(StudentiCardMain.this,"No student found !!!",Toast.LENGTH_LONG).show();
                    }
                } else {
                    selectedStudent = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        newEntry.setOnClickListener(view -> {
            if(StaticHelperClass.isNetworkAvailable(StudentiCardMain.this)) {
                Intent intent = new Intent(StudentiCardMain.this, StudentiCardNewEntry.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(StudentiCardMain.this,"Please check your internet connection !!! ",Toast.LENGTH_LONG).show();
            }
        });

        editStudentData.setOnClickListener(v -> {
            if(allSessionArrayList.size()>0 && selectedSession.getSessionID() == 0){
                Toast.makeText(StudentiCardMain.this,"Please select session!!! ",
                        Toast.LENGTH_LONG).show();
            } else if(StaticHelperClass.isNetworkAvailable(StudentiCardMain.this)) {
                if(selectedStudent != null) {
                    CheckSelectedData();
                    Bundle bundle = new Bundle();
                    bundle.putString("UserID", selectedStudent.getUserID());
                    bundle.putString("SessionID", Long.toString(selectedSession.getSessionID()));
                    bundle.putString("SectionID", Long.toString(selectedSection.getSectionID()));
                    bundle.putString("ClassID", Long.toString(selectedClass.getClassID()));
                    bundle.putString("ShiftID", Long.toString(selectedShift.getShiftID()));
                    bundle.putString("MediumID", Long.toString(selectedMedium.getMediumID()));
                    bundle.putString("DepartmentID", Long.toString(selectedDepartment.getDepartmentID()));
                    Intent intent = new Intent(StudentiCardMain.this, StudentiCardDetailsEdit.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else if(selectedStudent == null) {
                    Toast.makeText(StudentiCardMain.this,"Please select a student !!! ",Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(StudentiCardMain.this,"Please check your internet connection!!!",Toast.LENGTH_LONG).show();
            }
        });

        showStudentData.setOnClickListener(v -> {
            if(StaticHelperClass.isNetworkAvailable(StudentiCardMain.this)) {
                if(allSessionArrayList.size()>0 && selectedSession.getSessionID() == 0){
                    Toast.makeText(StudentiCardMain.this,"Please select session!!! ",
                            Toast.LENGTH_LONG).show();
                } else if(selectedStudent != null) {
                    CheckSelectedData();
                    Bundle bundle = new Bundle();
                    bundle.putString("UserID", selectedStudent.getUserID());
                    bundle.putString("SessionID", Long.toString(selectedSession.getSessionID()));
                    bundle.putString("SectionID", Long.toString(selectedSection.getSectionID()));
                    bundle.putString("ClassID", Long.toString(selectedClass.getClassID()));
                    bundle.putString("ShiftID", Long.toString(selectedShift.getShiftID()));
                    bundle.putString("MediumID", Long.toString(selectedMedium.getMediumID()));
                    bundle.putString("DepartmentID", Long.toString(selectedDepartment.getDepartmentID()));
                    Intent intent = new Intent(StudentiCardMain.this, StudentiCardDetails.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else if(selectedStudent == null) {
                    Toast.makeText(StudentiCardMain.this,"Please select a student !!! ",Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(StudentiCardMain.this,"Please check your internet connection!!!",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void SessionDataGetRequest() {
        if (StaticHelperClass.isNetworkAvailable(this)) {
            dialog.show();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(getString(R.string.baseUrl))
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();

            Observable<String> observable = retrofit
                    .create(RetrofitNetworkService.class)
                    .getallsession()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io());

            finalDisposer.add( observable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io())
                    .subscribeWith(new DisposableObserver<String>() {

                        @Override
                        public void onNext(String response) {
                            dialog.dismiss();
                            parseSessionJsonData(response);
                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                            Toast.makeText(StudentiCardMain.this,"Session not found!!! ",
                                    Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    }));
        } else {
            Toast.makeText(this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    void parseSessionJsonData(String jsonString) {
        try {
            String year = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
            int yearIndex = 0;
            allSessionArrayList = new ArrayList<>();
            JSONArray sessionJsonArray = new JSONArray(jsonString);
            ArrayList<String> sessionArrayList = new ArrayList<>();
            sessionArrayList.add("Select Session");
            for(int i = 0; i < sessionJsonArray.length(); ++i) {
                JSONObject sessionJsonObject = sessionJsonArray.getJSONObject(i);
                SessionModel sessionModel = new SessionModel(sessionJsonObject.getString("SessionID"), sessionJsonObject.getString("SessionName"));
                allSessionArrayList.add(sessionModel);
                sessionArrayList.add(sessionModel.getSessionName());
                if (year.equalsIgnoreCase(sessionModel.getSessionName())) {
                    yearIndex = i;
                }
            }
            try {
                String[] strings = new String[sessionArrayList.size()];
                strings = sessionArrayList.toArray(strings);
                ArrayAdapter<String> session_spinner_adapter = new ArrayAdapter<>(this,R.layout.spinner_item, strings);
                session_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerSession.setAdapter(session_spinner_adapter);
                spinnerSession.setSelection(yearIndex+1);
                selectedSession = allSessionArrayList.get(yearIndex);
            } catch (IndexOutOfBoundsException e) {
                Toast.makeText(this,"No session found !!!",Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
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
            } catch (IndexOutOfBoundsException e) {
                Toast.makeText(this,"No shift found !!!",Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
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
            } catch (IndexOutOfBoundsException e) {
                Toast.makeText(this,"No medium found !!!",Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
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
            } catch (IndexOutOfBoundsException e) {
                Toast.makeText(this,"No class found !!!",Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
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
            } catch (IndexOutOfBoundsException e) {
                Toast.makeText(this,"No department found !!!",Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
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
            } catch (IndexOutOfBoundsException e) {
                Toast.makeText(this,"No section found !!!",Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
        }
    }

    void parseStudentJsonData(String jsonString) {
        try {
            allStudentArrayList = new ArrayList<>();
            JSONArray studentJsonArray = new JSONArray(jsonString);
            ArrayList<String> studentArrayList = new ArrayList<>();
            studentArrayList.add("Select Student");
            for(int i = 0; i < studentJsonArray.length(); ++i) {
                JSONObject studentJsonObject = studentJsonArray.getJSONObject(i);
                SpinnerStudentInformation spinnerStudentInformation = new SpinnerStudentInformation();
                spinnerStudentInformation.setRollNo(studentJsonObject.getString("RollNo"));
                spinnerStudentInformation.setUserName(studentJsonObject.getString("UserName"));
                spinnerStudentInformation.setUserID(studentJsonObject.getString("UserID"));
                spinnerStudentInformation.setIsImageCaptured(studentJsonObject.getBoolean("IsImageCaptured"));
                allStudentArrayList.add(spinnerStudentInformation);
                String s = "";
                if(spinnerStudentInformation.getIsImageCaptured()) {
                    s = " (Done)";
                }
                studentArrayList.add(spinnerStudentInformation.getRollNo()+" - "+spinnerStudentInformation.getUserName()+s);
            }
            try {
                String[] strings = new String[studentArrayList.size()];
                strings = studentArrayList.toArray(strings);
                ArrayAdapter<String> student_spinner_adapter = new ArrayAdapter<>(this,R.layout.spinner_item, strings);
                student_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerStudent.setAdapter(student_spinner_adapter);
            } catch (IndexOutOfBoundsException e) {
                Toast.makeText(this,"No student found !!!",Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent mainIntent = new Intent(StudentiCardMain.this,TeacherMainScreen.class);
            startActivity(mainIntent);
            finish();
        }
    }

    private void ShiftDataGetRequest() {
        if (StaticHelperClass.isNetworkAvailable(this)) {
            dialog.show();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(getString(R.string.baseUrl))
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();

            Observable<String> observable = retrofit
                    .create(RetrofitNetworkService.class)
                    .getInsShift(InstituteID)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io());

            finalDisposer.add( observable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io())
                    .subscribeWith(new DisposableObserver<String>() {

                        @Override
                        public void onNext(String response) {
                            dialog.dismiss();
                            parseShiftJsonData(response);
                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                        }

                        @Override
                        public void onComplete() {

                        }
                    }));
        } else {
            Toast.makeText(StudentiCardMain.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void MediumDataGetRequest() {
        if(StaticHelperClass.isNetworkAvailable(this)) {
            dialog.show();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(getString(R.string.baseUrl))
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();

            Observable<String> observable = retrofit
                    .create(RetrofitNetworkService.class)
                    .getInstituteMediumDdl(InstituteID)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io());

            finalDisposer.add( observable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io())
                    .subscribeWith(new DisposableObserver<String>() {

                        @Override
                        public void onNext(String response) {
                            dialog.dismiss();
                            parseMediumJsonData(response);
                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                        }

                        @Override
                        public void onComplete() {

                        }
                    }));
        } else {
            Toast.makeText(StudentiCardMain.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void ClassDataGetRequest() {
        if(StaticHelperClass.isNetworkAvailable(this)) {
            dialog.show();
            CheckSelectedData();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(getString(R.string.baseUrl))
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();

            Observable<String> observable = retrofit
                    .create(RetrofitNetworkService.class)
                    .MediumWiseClassDDL(InstituteID, selectedMedium.getMediumID())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io());

            finalDisposer.add( observable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io())
                    .subscribeWith(new DisposableObserver<String>() {

                        @Override
                        public void onNext(String response) {
                            dialog.dismiss();
                            parseClassJsonData(response);
                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                        }

                        @Override
                        public void onComplete() {

                        }
                    }));
        } else {
            Toast.makeText(StudentiCardMain.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void DepartmentDataGetRequest() {
        if(StaticHelperClass.isNetworkAvailable(this)) {
            dialog.show();
            CheckSelectedData();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(getString(R.string.baseUrl))
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();

            Observable<String> observable = retrofit
                    .create(RetrofitNetworkService.class)
                    .ClassWiseDepartmentDDL(InstituteID, selectedClass.getClassID(), selectedMedium.getMediumID())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io());

            finalDisposer.add( observable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io())
                    .subscribeWith(new DisposableObserver<String>() {

                        @Override
                        public void onNext(String response) {
                            dialog.dismiss();
                            parseDepartmentJsonData(response);
                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                        }

                        @Override
                        public void onComplete() {

                        }
                    }));
        } else {
            Toast.makeText(StudentiCardMain.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void SectionDataGetRequest() {
        if(StaticHelperClass.isNetworkAvailable(this)) {
            dialog.show();
            CheckSelectedData();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(getString(R.string.baseUrl))
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();

            Observable<String> observable = retrofit
                    .create(RetrofitNetworkService.class)
                    .getInsSection(InstituteID, selectedClass.getClassID(), selectedDepartment.getDepartmentID())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io());

            finalDisposer.add( observable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io())
                    .subscribeWith(new DisposableObserver<String>() {

                        @Override
                        public void onNext(String response) {
                            dialog.dismiss();
                            parseSectionJsonData(response);
                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                        }

                        @Override
                        public void onComplete() {

                        }
                    }));
        } else {
            Toast.makeText(StudentiCardMain.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void StudentDataGetRequest() {
        if(StaticHelperClass.isNetworkAvailable(this)) {
            dialog.show();
            CheckSelectedData();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(getString(R.string.baseUrl))
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();

            Observable<String> observable = retrofit
                    .create(RetrofitNetworkService.class)
                    .getStudent(InstituteID,
                            Long.toString(selectedClass.getClassID()), Long.toString(selectedSection.getSectionID()),
                            Long.toString(selectedDepartment.getDepartmentID()), Long.toString(selectedMedium.getMediumID()),
                            Long.toString(selectedShift.getShiftID()), "0", Long.toString(selectedSession.getSessionID()))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io());

            finalDisposer.add( observable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io())
                    .subscribeWith(new DisposableObserver<String>() {

                        @Override
                        public void onNext(String response) {
                            dialog.dismiss();
                            parseStudentJsonData(response);
                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                        }

                        @Override
                        public void onComplete() {

                        }
                    }));
        } else {
            Toast.makeText(StudentiCardMain.this,"Please check your internet connection and select again!!! ",
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
