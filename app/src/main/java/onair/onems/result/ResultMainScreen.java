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
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import onair.onems.R;
import onair.onems.Services.StaticHelperClass;
import onair.onems.customadapters.MyDividerItemDecoration;
import onair.onems.customadapters.ResultAdapter;
import onair.onems.mainactivities.SideNavigationMenuParentActivity;
import onair.onems.mainactivities.StudentMainScreen;
import onair.onems.mainactivities.TeacherMainScreen;
import onair.onems.models.ClassModel;
import onair.onems.models.DepartmentModel;
import onair.onems.models.ExamModel;
import onair.onems.models.MediumModel;
import onair.onems.models.ResultModel;
import onair.onems.models.SectionModel;
import onair.onems.models.SessionModel;
import onair.onems.models.ShiftModel;
import onair.onems.models.SpinnerStudentInformation;

public class ResultMainScreen extends SideNavigationMenuParentActivity{

    private Spinner spinnerClass, spinnerShift, spinnerSection, spinnerMedium, spinnerDepartment, spinnerStudent, spinnerSession, spinnerExam;
    private ProgressDialog mShiftDialog, mMediumDialog, mClassDialog, mDepartmentDialog, mSectionDialog, mStudentListGetDialog, mSessionDialog, mExamDialog;

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

    private long InstituteID;

    private int firstClass = 0, firstShift = 0, firstSection = 0, firstMedium = 0,
            firstDepartment = 0, firstSession = 0, firstExam = 0;

    private int UserTypeID;

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

        if(!StaticHelperClass.isNetworkAvailable(this))
        {
            Toast.makeText(this,"Please check your internet connection and open app again!!! ",Toast.LENGTH_LONG).show();
        }

        selectedClass = new ClassModel();
        selectedShift = new ShiftModel();
        selectedSection = new SectionModel();
        selectedMedium = new MediumModel();
        selectedDepartment = new DepartmentModel();
        selectedSession = new SessionModel();
        selectedExam = new ExamModel();

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

}
