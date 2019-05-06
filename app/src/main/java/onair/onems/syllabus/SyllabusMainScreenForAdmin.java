package onair.onems.syllabus;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import onair.onems.mainactivities.SideNavigationMenuParentActivity;
import onair.onems.mainactivities.TeacherMainScreen;
import onair.onems.models.ClassModel;
import onair.onems.models.DepartmentModel;
import onair.onems.models.ExamModel;
import onair.onems.models.MediumModel;
import onair.onems.models.SectionModel;
import onair.onems.models.SubjectModel;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class SyllabusMainScreenForAdmin extends SideNavigationMenuParentActivity {

    private Spinner spinnerMedium, spinnerClass, spinnerDepartment, spinnerSection, spinnerSubject, spinnerExam;

    private ArrayList<MediumModel> allMediumArrayList;
    private ArrayList<ClassModel> allClassArrayList;
    private ArrayList<DepartmentModel> allDepartmentArrayList;
    private ArrayList<SectionModel> allSectionArrayList;
    private ArrayList<SubjectModel> allSubjectArrayList;
    private ArrayList<ExamModel> allExamArrayList;

    private String[] tempMediumArray = {"Select Medium"};
    private String[] tempClassArray = {"Select Class"};
    private String[] tempDepartmentArray = {"Select Department"};
    private String[] tempSectionArray = {"Select Section"};
    private String[] tempSubjectArray = {"Select Subject"};
    private String[] tempExamArray = {"Select Exam"};

    private MediumModel selectedMedium;
    private ClassModel selectedClass;
    private DepartmentModel selectedDepartment;
    private SectionModel selectedSection;
    private SubjectModel selectedSubject;
    private ExamModel selectedExam;
    private boolean hasDepartment = false;
    private CompositeDisposable finalDisposer = new CompositeDisposable();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!finalDisposer.isDisposed())
            finalDisposer.dispose();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityName = SyllabusMainScreenForAdmin.class.getName();

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = Objects.requireNonNull(inflater).inflate(R.layout.syllabus_main_screen_for_admin, null);
        LinearLayout parent = findViewById(R.id.contentMain);
        parent.addView(rowView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        allMediumArrayList = new ArrayList<>();
        allClassArrayList = new ArrayList<>();
        allDepartmentArrayList = new ArrayList<>();
        allSectionArrayList = new ArrayList<>();
        allSubjectArrayList = new ArrayList<>();
        allExamArrayList = new ArrayList<>();

        selectedMedium = new MediumModel();
        selectedClass = new ClassModel();
        selectedDepartment = new DepartmentModel();
        selectedSection = new SectionModel();
        selectedSubject = new SubjectModel();
        selectedExam = new ExamModel();

        spinnerMedium = findViewById(R.id.spinnerMedium);
        spinnerClass = findViewById(R.id.spinnerClass);
        spinnerDepartment = findViewById(R.id.spinnerDepartment);
        spinnerSection = findViewById(R.id.spinnerSection);
        spinnerSubject = findViewById(R.id.spinnerSubject);
        spinnerExam = findViewById(R.id.spinnerExam);

        Button showSyllabus = findViewById(R.id.showSyllabus);

        ArrayAdapter<String> medium_spinner_adapter = new ArrayAdapter<>(this, R.layout.spinner_item, tempMediumArray);
        medium_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMedium.setAdapter(medium_spinner_adapter);

        ArrayAdapter<String> class_spinner_adapter = new ArrayAdapter<>(this, R.layout.spinner_item, tempClassArray);
        class_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClass.setAdapter(class_spinner_adapter);

        ArrayAdapter<String> department_spinner_adapter = new ArrayAdapter<>(this, R.layout.spinner_item, tempDepartmentArray);
        department_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDepartment.setAdapter(department_spinner_adapter);

        ArrayAdapter<String> section_spinner_adapter = new ArrayAdapter<>(this, R.layout.spinner_item, tempSectionArray);
        section_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSection.setAdapter(section_spinner_adapter);

        ArrayAdapter<String> subject_spinner_adapter = new ArrayAdapter<>(this, R.layout.spinner_item, tempSubjectArray);
        subject_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSubject.setAdapter(subject_spinner_adapter);

        ArrayAdapter<String> exam_spinner_adapter = new ArrayAdapter<>(this, R.layout.spinner_item, tempExamArray);
        exam_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerExam.setAdapter(exam_spinner_adapter);

        MediumDataGetRequest();

        spinnerMedium.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != 0) {
                    try {
                        selectedMedium = allMediumArrayList.get(position-1);
                        selectedClass = new ClassModel();
                        selectedDepartment = new DepartmentModel();
                        selectedSection = new SectionModel();
                        selectedSubject = new SubjectModel();
                        selectedExam = new ExamModel();
                        ClassDataGetRequest();
                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(SyllabusMainScreenForAdmin.this,"No medium found !!!",Toast.LENGTH_LONG).show();
                    }
                } else {
                    selectedMedium = new MediumModel();
                    selectedClass = new ClassModel();
                    selectedDepartment = new DepartmentModel();
                    selectedSection = new SectionModel();
                    selectedSubject = new SubjectModel();
                    selectedExam = new ExamModel();
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
                        selectedSubject = new SubjectModel();
                        selectedExam = new ExamModel();
                        DepartmentDataGetRequest();
                        SubjectDataGetRequest();
                        ExamDataGetRequest();
                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(SyllabusMainScreenForAdmin.this,"No class found !!!",Toast.LENGTH_LONG).show();
                    }
                } else {
                    selectedClass = new ClassModel();
                    selectedDepartment = new DepartmentModel();
                    selectedSection = new SectionModel();
                    selectedSubject = new SubjectModel();
                    selectedExam = new ExamModel();
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
                        if(hasDepartment){
                            SubjectDataGetRequest();
                            selectedSubject = new SubjectModel();
                        }
                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(SyllabusMainScreenForAdmin.this,"No department found !!!",Toast.LENGTH_LONG).show();
                    }
                } else {
                    selectedDepartment = new DepartmentModel();
                    selectedSection = new SectionModel();
                    if(hasDepartment){
                        selectedSubject = new SubjectModel();
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
                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(SyllabusMainScreenForAdmin.this,"No section found !!!",Toast.LENGTH_LONG).show();
                    }
                } else {
                    selectedSection = new SectionModel();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != 0) {
                    try {
                        selectedSubject = allSubjectArrayList.get(position-1);
                    }
                    catch (IndexOutOfBoundsException e)
                    {
                        Toast.makeText(SyllabusMainScreenForAdmin.this,"No subject found !!!",Toast.LENGTH_LONG).show();
                    }
                } else {
                    selectedSubject = null;
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
                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(SyllabusMainScreenForAdmin.this,"No Exam found !!!",Toast.LENGTH_LONG).show();
                    }
                } else {
                    selectedExam = new ExamModel();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        showSyllabus.setOnClickListener(v -> {
            if(StaticHelperClass.isNetworkAvailable(SyllabusMainScreenForAdmin.this)) {
                if(allMediumArrayList.size()>0 && (selectedMedium.getMediumID() == -2 || selectedMedium.getMediumID() == 0)) {
                    Toast.makeText(SyllabusMainScreenForAdmin.this,"Please select medium!!! ",
                            Toast.LENGTH_LONG).show();
                } else if(allClassArrayList.size()>0 && (selectedClass.getClassID() == -2 || selectedClass.getClassID() == 0)) {
                    Toast.makeText(SyllabusMainScreenForAdmin.this,"Please select class!!! ",
                            Toast.LENGTH_LONG).show();
                } else if(allDepartmentArrayList.size()>0 && (selectedDepartment.getDepartmentID() == -2 || selectedDepartment.getDepartmentID() == 0)) {
                    Toast.makeText(SyllabusMainScreenForAdmin.this,"Please select department!!! ",
                            Toast.LENGTH_LONG).show();
                } else if(allSectionArrayList.size()>0 && (selectedSection.getSectionID() == -2 || selectedSection.getSectionID() == 0)) {
                    Toast.makeText(SyllabusMainScreenForAdmin.this,"Please select section!!! ",
                            Toast.LENGTH_LONG).show();
                } else if(allSubjectArrayList.size()>0 && selectedSubject.getSubjectID() == 0) {
                    Toast.makeText(SyllabusMainScreenForAdmin.this,"Please select subject!!! ",
                            Toast.LENGTH_LONG).show();
                } else if(allExamArrayList.size()>0 && selectedExam.getExamID() == 0) {
                    Toast.makeText(SyllabusMainScreenForAdmin.this,"Please select exam!!! ",
                            Toast.LENGTH_LONG).show();
                } else {
                    CheckSelectedData();
                    Intent intent = new Intent(SyllabusMainScreenForAdmin.this, SyllabusMainScreen.class);
                    intent.putExtra("MediumID", selectedMedium.getMediumID());
                    intent.putExtra("ClassID", selectedClass.getClassID());
                    intent.putExtra("DepartmentID", selectedDepartment.getDepartmentID());
                    intent.putExtra("SectionID", selectedSection.getSectionID());
                    intent.putExtra("SubjectID", selectedSubject.getSubjectID());
                    intent.putExtra("ExamID", selectedExam.getExamID());
                    startActivity(intent);
                    finish();
                }
            } else {
                Toast.makeText(SyllabusMainScreenForAdmin.this,"Please check your internet connection and select again!!! ",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(UserTypeID == 1) {
            Intent mainIntent = new Intent(SyllabusMainScreenForAdmin.this,TeacherMainScreen.class);
            startActivity(mainIntent);
            finish();
        } else if(UserTypeID == 2) {
            Intent mainIntent = new Intent(SyllabusMainScreenForAdmin.this,TeacherMainScreen.class);
            startActivity(mainIntent);
            finish();
        } else if(UserTypeID == 4) {
            Intent mainIntent = new Intent(SyllabusMainScreenForAdmin.this,TeacherMainScreen.class);
            startActivity(mainIntent);
            finish();
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
            Toast.makeText(SyllabusMainScreenForAdmin.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
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
            Toast.makeText(SyllabusMainScreenForAdmin.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
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
            Toast.makeText(SyllabusMainScreenForAdmin.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
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
            hasDepartment = allDepartmentArrayList.size() > 0;
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
            Toast.makeText(SyllabusMainScreenForAdmin.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
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

    private void SubjectDataGetRequest() {
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
                    .getInsSubject(InstituteID, selectedDepartment.getDepartmentID(), selectedMedium.getMediumID(), selectedClass.getClassID())
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
                            parseSubjectJsonData(response);
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
            Toast.makeText(SyllabusMainScreenForAdmin.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
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
                        subjectJsonObject.getString("IsActive"), subjectJsonObject.getString("IsCombined"), subjectJsonObject.getString("ParentID"));
                allSubjectArrayList.add(subjectModel);
                subjectArrayList.add(subjectModel.getSubjectName());
            }
            try {
                String[] strings = new String[subjectArrayList.size()];
                strings = subjectArrayList.toArray(strings);
                ArrayAdapter<String> subject_spinner_adapter = new ArrayAdapter<>(this,R.layout.spinner_item, strings);
                subject_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerSubject.setAdapter(subject_spinner_adapter);
            } catch (IndexOutOfBoundsException e) {
                Toast.makeText(this,"No subject found !!!",Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
        }
    }

    private void ExamDataGetRequest() {
        if (StaticHelperClass.isNetworkAvailable(this)) {
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
                    .getClassWiseInsExame(InstituteID, selectedMedium.getMediumID(), selectedClass.getClassID())
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
                            parseExamJsonData(response);
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
            Toast.makeText(SyllabusMainScreenForAdmin.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
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
                            examJsonObject.getString("InsExamID"), examJsonObject.getString("Sequence")
                            , examJsonObject.getString("IsActive"));
                    allExamArrayList.add(examModel);
                    examArrayList.add(examModel.getExamName());
                }
            }

            try {
                String[] strings = new String[examArrayList.size()];
                strings = examArrayList.toArray(strings);
                ArrayAdapter<String> exam_spinner_adapter = new ArrayAdapter<>(this,R.layout.spinner_item, strings);
                exam_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerExam.setAdapter(exam_spinner_adapter);
            } catch (IndexOutOfBoundsException e) {
                Toast.makeText(this,"No section found !!!",Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
        }
    }

    private void CheckSelectedData(){
        if(selectedMedium.getMediumID() == -2) {
            selectedMedium.setMediumID("0");
        }
        if(selectedClass.getClassID() == -2) {
            selectedClass.setClassID("0");
        }
        if(selectedDepartment.getDepartmentID() == -2) {
            selectedDepartment.setDepartmentID("0");
        }
        if(selectedSection.getSectionID() == -2) {
            selectedSection.setSectionID("0");
        }
    }
}
