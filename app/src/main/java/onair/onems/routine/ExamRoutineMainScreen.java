package onair.onems.routine;

import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
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
import onair.onems.customised.MyDividerItemDecoration;
import onair.onems.mainactivities.SideNavigationMenuParentActivity;
import onair.onems.mainactivities.StudentMainScreen;
import onair.onems.mainactivities.TeacherMainScreen;
import onair.onems.models.ClassModel;
import onair.onems.models.ExamModel;
import onair.onems.models.MediumModel;
import onair.onems.models.SessionModel;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ExamRoutineMainScreen extends SideNavigationMenuParentActivity {
    private Spinner spinnerSession, spinnerMedium, spinnerClass, spinnerExam;

    private ArrayList<SessionModel> allSessionArrayList;
    private ArrayList<MediumModel> allMediumArrayList;
    private ArrayList<ClassModel> allClassArrayList;
    private ArrayList<ExamModel> allExamArrayList;

    private String[] tempSessionArray = {"Select Session"};
    private String[] tempMediumArray = {"Select Medium"};
    private String[] tempClassArray = {"Select Class"};
    private String[] tempExamArray = {"Select Exam"};

    private SessionModel selectedSession;
    private MediumModel selectedMedium;
    private ClassModel selectedClass;
    private ExamModel selectedExam;

    private RecyclerView recyclerView;
    private JSONArray routine;
    private TextView className;

    private long MediumID = 0, ClassID = 0, SessionID = 0, ExamID = 0;
    private CompositeDisposable finalDisposer = new CompositeDisposable();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (finalDisposer != null && !finalDisposer.isDisposed())
            finalDisposer.dispose();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityName = ExamRoutineMainScreen.class.getName();

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View childActivityLayout = Objects.requireNonNull(inflater).inflate(R.layout.exam_routine_main_screen, null);
        LinearLayout parentActivityLayout = findViewById(R.id.contentMain);
        parentActivityLayout.addView(childActivityLayout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        allSessionArrayList = new ArrayList<>();
        allMediumArrayList = new ArrayList<>();
        allClassArrayList = new ArrayList<>();
        allExamArrayList = new ArrayList<>();

        selectedSession = new SessionModel();
        selectedMedium = new MediumModel();
        selectedClass = new ClassModel();
        selectedExam = new ExamModel();

        spinnerSession = findViewById(R.id.spinnerSession);
        spinnerMedium = findViewById(R.id.spinnerMedium);
        spinnerClass = findViewById(R.id.spinnerClass);
        spinnerExam = findViewById(R.id.spinnerExam);
        recyclerView = findViewById(R.id.routineClasses);
        className = findViewById(R.id.className);

        ArrayAdapter<String> session_spinner_adapter = new ArrayAdapter<>(this, R.layout.spinner_item, tempSessionArray);
        session_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSession.setAdapter(session_spinner_adapter);

        ArrayAdapter<String> medium_spinner_adapter = new ArrayAdapter<>(this, R.layout.spinner_item, tempMediumArray);
        medium_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMedium.setAdapter(medium_spinner_adapter);

        ArrayAdapter<String> class_spinner_adapter = new ArrayAdapter<>(this, R.layout.spinner_item, tempClassArray);
        class_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClass.setAdapter(class_spinner_adapter);

        ArrayAdapter<String> exam_spinner_adapter = new ArrayAdapter<>(this, R.layout.spinner_item, tempExamArray);
        exam_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerExam.setAdapter(exam_spinner_adapter);

        if (UserTypeID == 1 || UserTypeID == 2 || UserTypeID == 4) {
            SessionDataGetRequest();
            MediumDataGetRequest();
        } else if (UserTypeID == 3){
            spinnerMedium.setVisibility(View.GONE);
            spinnerClass.setVisibility(View.GONE);
            MediumID = PreferenceManager.getDefaultSharedPreferences(this).getLong("MediumID", 0);
            ClassID = PreferenceManager.getDefaultSharedPreferences(this).getLong("ClassID", 0);
            SessionDataGetRequest();
            ExamDataGetRequest(InstituteID, MediumID, ClassID);
        } else if (UserTypeID == 5) {
            spinnerMedium.setVisibility(View.GONE);
            spinnerClass.setVisibility(View.GONE);
            try {
                JSONObject selectedStudent = new JSONObject(getSharedPreferences("CURRENT_STUDENT", Context.MODE_PRIVATE)
                        .getString("guardianSelectedStudent", "{}"));
                MediumID = selectedStudent.getLong("MediumID");
                ClassID = selectedStudent.getLong("ClassID");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            SessionDataGetRequest();
            ExamDataGetRequest(InstituteID, MediumID, ClassID);
        }

        spinnerSession.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != 0) {
                    try {
                        selectedSession = allSessionArrayList.get(position-1);
                        SessionID = selectedSession.getSessionID();
                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(ExamRoutineMainScreen.this,"No shift found !!!",Toast.LENGTH_LONG).show();
                    }
                } else {
                    selectedSession = new SessionModel();
                    SessionID = selectedSession.getSessionID();
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
                        MediumID = selectedMedium.getMediumID();
                        selectedClass = new ClassModel();
                        ClassID = selectedClass.getClassID();
                        ClassDataGetRequest();
                        selectedExam = new ExamModel();
                        ExamID = selectedExam.getExamID();
                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(ExamRoutineMainScreen.this,"No medium found !!!",Toast.LENGTH_LONG).show();
                    }
                } else {
                    selectedMedium = new MediumModel();
                    MediumID = selectedMedium.getMediumID();
                    selectedClass = new ClassModel();
                    ClassID = selectedClass.getClassID();
                    selectedExam = new ExamModel();
                    ExamID = selectedExam.getExamID();
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
                        ClassID = selectedClass.getClassID();
                        ExamDataGetRequest(InstituteID, selectedMedium.getMediumID(), selectedClass.getClassID());
                        selectedExam = new ExamModel();
                        ExamID = selectedExam.getExamID();
                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(ExamRoutineMainScreen.this,"No class found !!!",Toast.LENGTH_LONG).show();
                    }
                } else {
                    selectedClass = new ClassModel();
                    ClassID = selectedClass.getClassID();
                    selectedExam = new ExamModel();
                    ExamID = selectedExam.getExamID();
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
                        ExamID = selectedExam.getExamID();

                        if(StaticHelperClass.isNetworkAvailable(ExamRoutineMainScreen.this)) {
                            if(allSessionArrayList.size()>0 && selectedSession.getSessionID() == 0){
                                Toast.makeText(ExamRoutineMainScreen.this,"Please select session!!! ",
                                        Toast.LENGTH_LONG).show();
                            } else if(allMediumArrayList.size()>0 && (selectedMedium.getMediumID() == -2 || selectedMedium.getMediumID() == 0) && UserTypeID != 3 && UserTypeID != 5) {
                                Toast.makeText(ExamRoutineMainScreen.this,"Please select medium!!! ",
                                        Toast.LENGTH_LONG).show();
                            } else if(allClassArrayList.size()>0 && (selectedClass.getClassID() == -2 || selectedClass.getClassID() == 0) && UserTypeID != 3 && UserTypeID != 5) {
                                Toast.makeText(ExamRoutineMainScreen.this,"Please select class!!! ",
                                        Toast.LENGTH_LONG).show();
                            } else if(allExamArrayList.size()>0 && selectedExam.getExamID() == 0) {
                                Toast.makeText(ExamRoutineMainScreen.this,"Please select exam!!! ",
                                        Toast.LENGTH_LONG).show();
                            } else {
                                ExamRoutineDataGetRequest();
                            }
                        } else {
                            Toast.makeText(ExamRoutineMainScreen.this,"Please check your internet connection and select again!!! ",
                                    Toast.LENGTH_LONG).show();
                        }

                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(ExamRoutineMainScreen.this,"No Exam found !!!",Toast.LENGTH_LONG).show();
                    }
                } else {
                    selectedExam = new ExamModel();
                    ExamID = selectedExam.getExamID();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
                            Toast.makeText(ExamRoutineMainScreen.this,"Session not found!!! ",
                                    Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    }));
        } else {
            Toast.makeText(ExamRoutineMainScreen.this,"Please check your internet connection and select again!!! ",
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
                            Toast.makeText(ExamRoutineMainScreen.this,"Medium not found!!! ",
                                    Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    }));
        } else {
            Toast.makeText(ExamRoutineMainScreen.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    void parseMediumJsonData(String jsonString) {
        try {
            allMediumArrayList = new ArrayList<>();
            JSONArray mediumJsonArray = new JSONArray(jsonString);
            ArrayList<String> mediumArrayList = new ArrayList<>();
            mediumArrayList.add("Select Medium");
            for(int i = 0; i < mediumJsonArray.length(); ++i) {
                JSONObject mediumJsonObject = mediumJsonArray.getJSONObject(i);
                MediumModel mediumModel = new MediumModel(mediumJsonObject.getString("MediumID"), mediumJsonObject.getString("MameName"),
                        mediumJsonObject.getString("IsDefault"));
                allMediumArrayList.add(mediumModel);
                mediumArrayList.add(mediumModel.getMameName());
            }
            try {
                String[] strings = new String[mediumArrayList.size()];
                strings = mediumArrayList.toArray(strings);
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
                            Toast.makeText(ExamRoutineMainScreen.this,"Class not found!!! ",
                                    Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    }));
        } else {
            Toast.makeText(ExamRoutineMainScreen.this,"Please check your internet connection and select again!!! ",
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

    private void ExamDataGetRequest(long InstituteID, long MediumID, long ClassID) {
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
                    .getClassWiseInsExame(InstituteID, MediumID, ClassID)
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
                            Toast.makeText(ExamRoutineMainScreen.this,"Exam not found!!! ",
                                    Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    }));
        } else {
            Toast.makeText(ExamRoutineMainScreen.this,"Please check your internet connection and select again!!! ",
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
                            "", ""
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

    private void ExamRoutineDataGetRequest() {
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
                    .spGetCommonClassExamRoutine(InstituteID, ExamID, MediumID, ClassID, 0, SessionID)
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
                            className.setText("");
                            parseExamRoutineJsonData(response);
                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                            className.setText("");
                            Toast.makeText(ExamRoutineMainScreen.this,"Routine not found!!! ",
                                    Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    }));
        } else {
            Toast.makeText(ExamRoutineMainScreen.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    void parseExamRoutineJsonData(String examRoutine) {
        try {
            routine = new JSONArray(examRoutine);
            className.setText("Class: "+routine.getJSONObject(0).getString("ClassName"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ExamRoutineAdapter mAdapter = new ExamRoutineAdapter(this, routine);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 0));
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(UserTypeID == 1) {
            Intent mainIntent = new Intent(ExamRoutineMainScreen.this,TeacherMainScreen.class);
            startActivity(mainIntent);
            finish();
        } else if(UserTypeID == 2) {
            Intent mainIntent = new Intent(ExamRoutineMainScreen.this,TeacherMainScreen.class);
            startActivity(mainIntent);
            finish();
        } else if(UserTypeID == 3) {
            Intent mainIntent = new Intent(ExamRoutineMainScreen.this,StudentMainScreen.class);
            startActivity(mainIntent);
            finish();
        } else if(UserTypeID == 4) {
            Intent mainIntent = new Intent(ExamRoutineMainScreen.this,TeacherMainScreen.class);
            startActivity(mainIntent);
            finish();
        } else if(UserTypeID == 5) {
            Intent mainIntent = new Intent(ExamRoutineMainScreen.this,StudentMainScreen.class);
            startActivity(mainIntent);
            finish();
        }
    }

    private void CheckSelectedData(){
        if(selectedClass.getClassID() == -2) {
            selectedClass.setClassID("0");
        }
        if(selectedMedium.getMediumID() == -2) {
            selectedMedium.setMediumID("0");
        }
    }
}
