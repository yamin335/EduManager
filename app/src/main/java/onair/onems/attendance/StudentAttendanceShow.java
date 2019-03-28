package onair.onems.attendance;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import onair.onems.R;
import onair.onems.Services.RetrofitNetworkService;
import onair.onems.Services.StaticHelperClass;
import onair.onems.mainactivities.CommonToolbarParentActivity;
import onair.onems.models.AllStudentAttendanceModel;
import onair.onems.models.DailyAttendanceModel;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class StudentAttendanceShow extends CommonToolbarParentActivity implements AttendanceMonthlyStudentListAdapter.AttendanceMonthlyStudentListAdapterListener {
    private ArrayList<AllStudentAttendanceModel> studentList;
    private AllStudentAttendanceModel selectedStudent = null;
    private int MonthID = 0;
    private Disposable disposable;
    private long instituteID, shiftID, mediumID, classID, departmentID, sectionID, SessionID;
    private RecyclerView recyclerView;
    private AttendanceMonthlyStudentListAdapter adapter;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!disposable.isDisposed())
            disposable.dispose();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View childActivityLayout = Objects.requireNonNull(inflater).inflate(R.layout.attendance_report_student_list, null);
        LinearLayout parentActivityLayout = findViewById(R.id.contentMain);
        parentActivityLayout.addView(childActivityLayout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        recyclerView = findViewById(R.id.recycler);
        SharedPreferences sharedPre = PreferenceManager.getDefaultSharedPreferences(this);
        instituteID = sharedPre.getLong("InstituteID", 0);
        Intent intent = getIntent();
        shiftID = intent.getLongExtra("ShiftID", 0);
        mediumID = intent.getLongExtra("MediumID", 0);
        classID = intent.getLongExtra("ClassID", 0);
        departmentID = intent.getLongExtra("DepartmentID", 0);
        sectionID = intent.getLongExtra("SectionID", 0);
        MonthID = intent.getIntExtra("MonthID", 0);
        SessionID = intent.getLongExtra("SessionID", 0);

        studentList = new ArrayList<>();
        adapter = new AttendanceMonthlyStudentListAdapter(this, studentList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        AttendanceDataGetRequest();

    }
    void parseAllStudentShowJsonData(String jsonString) {
        try {
            studentList.clear();
            studentList.add(new AllStudentAttendanceModel());
            JSONArray studentListJsonArray = new JSONArray(jsonString);
            JSONArray jsonArray = new JSONArray(jsonString);
            for(int i = 0; i < jsonArray.length(); ++i) {
                JSONObject studentJsonObject = studentListJsonArray.getJSONObject(i);
                AllStudentAttendanceModel singleStudent = new AllStudentAttendanceModel();
                singleStudent.setUserID(studentJsonObject.getString("UserID"));
                singleStudent.setUserFullName(studentJsonObject.getString("UserFullName"));
                singleStudent.setRFID(studentJsonObject.getString("RFID"));
                singleStudent.setRollNo(studentJsonObject.getString("RollNo"));
                singleStudent.setShiftID(studentJsonObject.getString("ShiftID"));
                singleStudent.setMediumID(studentJsonObject.getString("MediumID"));
                singleStudent.setClassID(studentJsonObject.getString("ClassID"));
                singleStudent.setDepartmentID(studentJsonObject.getString("DepartmentID"));
                singleStudent.setSectionID(studentJsonObject.getString("SectionID"));
                singleStudent.setImageUrl(studentJsonObject.getString("ImageUrl"));
                studentList.add(singleStudent);
                adapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
        }
    }

    public void AttendanceDataGetRequest(){
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
                    .getHrmSubWiseAtdDetail(instituteID, mediumID, shiftID, classID, sectionID, departmentID, SessionID)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io());

            observable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io())
                    .subscribe(new Observer<String>() {

                        @Override
                        public void onSubscribe(Disposable d) {
                            disposable = d;
                        }

                        @Override
                        public void onNext(String response) {
                            dialog.dismiss();
                            parseAllStudentShowJsonData(response);
                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            Toast.makeText(StudentAttendanceShow.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        finish();
        return true;

    }

    @Override
    public void onStudentSelected(AllStudentAttendanceModel object) {
        selectedStudent = object;
        Intent intent1 = new Intent(StudentAttendanceShow.this, StudentAttendanceAllDays.class);
        intent1.putExtra("UserID", selectedStudent.getUserID());
        intent1.putExtra("UserFullName", selectedStudent.getUserFullName());
        intent1.putExtra("RollNo", selectedStudent.getRollNo());
        intent1.putExtra("RFID", selectedStudent.getRFID());
        intent1.putExtra("ShiftID", selectedStudent.getShiftID());
        intent1.putExtra("MediumID", selectedStudent.getMediumID());
        intent1.putExtra("ClassID", selectedStudent.getClassID());
        intent1.putExtra("DepartmentID", selectedStudent.getDepartmentID());
        intent1.putExtra("SectionID", selectedStudent.getSectionID());
        intent1.putExtra("MonthID", MonthID);
        intent1.putExtra("ImageUrl", selectedStudent.getImageUrl());
        intent1.putExtra("SessionID", SessionID);
        startActivity(intent1);
    }
}
