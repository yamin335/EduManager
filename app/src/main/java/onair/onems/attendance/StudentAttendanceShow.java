package onair.onems.attendance;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
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

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.model.TableColumnWeightModel;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import de.codecrafters.tableview.toolkit.TableDataRowBackgroundProviders;
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
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class StudentAttendanceShow extends CommonToolbarParentActivity {
    private ArrayList<AllStudentAttendanceModel> studentList;
    private AllStudentAttendanceModel selectedStudent = null;
    private TableView tableView;
    private Configuration config;
    private int MonthID = 0;
    private SimpleTableHeaderAdapter simpleTableHeaderAdapter;
    private Disposable disposable;
    private long instituteID, shiftID, mediumID, classID, departmentID, sectionID, SessionID;

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

        tableView = findViewById(R.id.tableView);
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

        simpleTableHeaderAdapter = new SimpleTableHeaderAdapter(this, "SI", "Student Name", "ID", "Roll");
        simpleTableHeaderAdapter.setTextColor(ContextCompat.getColor(this, R.color.table_header_text));
        tableView.setHeaderAdapter(simpleTableHeaderAdapter);
        config = getResources().getConfiguration();
        int colorEvenRows = getResources().getColor(R.color.table_data_row_even);
        int colorOddRows = getResources().getColor(R.color.table_data_row_odd);
        tableView.setDataRowBackgroundProvider(TableDataRowBackgroundProviders.alternatingRowColors(colorEvenRows, colorOddRows));
        TableColumnWeightModel columnModel = new TableColumnWeightModel(4);
        columnModel.setColumnWeight(1, 2);
        tableView.setColumnModel(columnModel);

        AttendanceDataGetRequest();

        tableView.addDataClickListener((rowIndex, clickedData) -> {
            selectedStudent = studentList.get(rowIndex);
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
        });

    }
    void parseAllStudentShowJsonData(String jsonString) {
        try {
            studentList = new ArrayList<>();
            JSONArray studentListJsonArray = new JSONArray(jsonString);
            JSONArray jsonArray = new JSONArray(jsonString);
            String[][] DATA_TO_SHOW = new String[jsonArray.length()][4];
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
                DATA_TO_SHOW[i][0]= String.valueOf((i+1));
                DATA_TO_SHOW[i][1]= singleStudent.getUserFullName();
                DATA_TO_SHOW[i][2]= singleStudent.getRFID();
                DATA_TO_SHOW [i][3]= singleStudent.getRollNo();
            }
            SimpleTableDataAdapter simpleTabledataAdapter = new SimpleTableDataAdapter(this, DATA_TO_SHOW);
            tableView.setDataAdapter(simpleTabledataAdapter);
            if (config.smallestScreenWidthDp >320) {
                simpleTableHeaderAdapter.setTextSize(14);
                simpleTabledataAdapter.setTextSize(12);
            } else {
                simpleTableHeaderAdapter.setTextSize(10);
                simpleTabledataAdapter.setTextSize(10);
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
}
