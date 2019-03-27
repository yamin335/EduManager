package onair.onems.attendance;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import onair.onems.R;
import onair.onems.Services.RetrofitNetworkService;
import onair.onems.Services.StaticHelperClass;
import onair.onems.mainactivities.CommonToolbarParentActivity;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class AttendanceReportDaily extends CommonToolbarParentActivity {
    private CompositeDisposable finalDisposer = new CompositeDisposable();
    private long InstituteID, MediumID, ShiftID, ClassID, SectionID, DepartmentID, SessionID;
    private String date;
    private String isPresent = "", isAbsent = "", isLate = "";
    private ArrayList<JsonObject> attendanceList = new ArrayList<>();
    private AttendanceReportDailyAdapter mAdapter;
    private RecyclerView recycler;
    private TextView _totalStudent, _marginTime, _totalPresent, _totalAbsent, _totalLate;
    private String marginTime;
    private int totalStudent = 0, totalPresent = 0, totalAbsent = 0, totalLate = 0;


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!finalDisposer.isDisposed())
            finalDisposer.dispose();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View childActivityLayout = Objects.requireNonNull(inflater).inflate(R.layout.attendance_report_daily, null);
        LinearLayout parentActivityLayout = findViewById(R.id.contentMain);
        parentActivityLayout.addView(childActivityLayout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        Bundle StudentSelection = getIntent().getExtras();
        InstituteID = Objects.requireNonNull(StudentSelection).getLong("InstituteID",0);
        SessionID = StudentSelection.getLong("SessionID",0);
        MediumID = StudentSelection.getLong("MediumID",0);
        ShiftID = StudentSelection.getLong("ShiftID",0);
        ClassID = StudentSelection.getLong("ClassID",0);
        SectionID = StudentSelection.getLong("SectionID",0);
        DepartmentID = StudentSelection.getLong("DepartmentID",0);
        date = StudentSelection.getString("Date", "");

        recycler = findViewById(R.id.recycler);
        CheckBox present = findViewById(R.id.present);
        CheckBox absent = findViewById(R.id.absent);
        CheckBox late = findViewById(R.id.late);
        _totalStudent = findViewById(R.id.totalStudent);
        _marginTime = findViewById(R.id.marginTime);
        _totalPresent = findViewById(R.id.totalPresent);
        _totalAbsent = findViewById(R.id.totalAbsent);
        _totalLate = findViewById(R.id.totalLate);

        if (present.isChecked()) {
            isPresent = "P";
        }

        if (absent.isChecked()) {
            isAbsent = "A";
        }

        if (late.isChecked()) {
            isLate = "L";
        }

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recycler.setLayoutManager(mLayoutManager);
        recycler.setItemAnimator(new DefaultItemAnimator());

        present.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                isPresent = "P";
                mAdapter.Filter(isPresent, isAbsent, isLate);
            } else {
                isPresent = "";
                if (isPresent.equalsIgnoreCase("") && isAbsent.equalsIgnoreCase("") && isLate.equalsIgnoreCase("")) {
                    mAdapter.refreshToMainList();
                } else {
                    mAdapter.Filter(isPresent, isAbsent, isLate);
                }
            }

        });

        absent.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                isAbsent = "A";
                mAdapter.Filter(isPresent, isAbsent, isLate);
            } else {
                isAbsent = "";
                if (isPresent.equalsIgnoreCase("") && isAbsent.equalsIgnoreCase("") && isLate.equalsIgnoreCase("")) {
                    mAdapter.refreshToMainList();
                } else {
                    mAdapter.Filter(isPresent, isAbsent, isLate);
                }
            }

        });

        late.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                isLate = "L";
                mAdapter.Filter(isPresent, isAbsent, isLate);
            } else {
                isLate = "";
                if (isPresent.equalsIgnoreCase("") && isAbsent.equalsIgnoreCase("") && isLate.equalsIgnoreCase("")) {
                    mAdapter.refreshToMainList();
                } else {
                    mAdapter.Filter(isPresent, isAbsent, isLate);
                }
            }

        });

        attendanceDataGetRequest();
        totalAttendanceDataGetRequest();
    }

    private void totalAttendanceDataGetRequest() {
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
                    .getHrmTotalStudentAttendance(ShiftID, MediumID, ClassID, SectionID, DepartmentID, date, InstituteID, 3)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io());

            finalDisposer.add( observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io())
                    .subscribeWith(new DisposableObserver<String>() {

                        @Override
                        public void onNext(String response) {
                            dialog.dismiss();
                        }

                        @Override
                        public void onComplete() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                            Toast.makeText(AttendanceReportDaily.this,"Error: getting total attendance!!!",Toast.LENGTH_LONG).show();
                        }
                    }));
        } else {
            Toast.makeText(AttendanceReportDaily.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void attendanceDataGetRequest() {
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
                    .gethrmDeviceByParmsforStudent(ShiftID, MediumID, ClassID, SectionID, DepartmentID, date, 0, 0, InstituteID, 3, SessionID)
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
                            parseAttendanceData(response);
                        }

                        @Override
                        public void onComplete() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                            Toast.makeText(AttendanceReportDaily.this,"Error: getting attendance data!!!",Toast.LENGTH_LONG).show();
                        }
                    }));
        } else {
            Toast.makeText(AttendanceReportDaily.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void parseAttendanceData(String response) {
        attendanceList.clear();
        JsonParser parser = new JsonParser();
        JsonArray jsonArray = parser.parse(response).getAsJsonArray();
        for (JsonElement jsonObject : jsonArray) {
            if (jsonObject.getAsJsonObject().get("APLStatus").getAsString().equalsIgnoreCase("P")) {
                totalPresent++;
            } else if (jsonObject.getAsJsonObject().get("APLStatus").getAsString().equalsIgnoreCase("A")) {
                totalAbsent++;
            } else if (jsonObject.getAsJsonObject().get("APLStatus").getAsString().equalsIgnoreCase("L")) {
                totalLate++;
                totalPresent++;
            }
            attendanceList.add(jsonObject.getAsJsonObject());
        }
        totalStudent = attendanceList.size();
        marginTime = attendanceList.get(0).get("MarginTime").getAsString();
        _totalStudent.setText(Integer.toString(totalStudent));
        _totalPresent.setText(Integer.toString(totalPresent));
        _totalAbsent.setText(Integer.toString(totalAbsent));
        _totalLate.setText(Integer.toString(totalLate));
        _marginTime.setText(marginTime);
        mAdapter = new AttendanceReportDailyAdapter(this, attendanceList, isPresent, isAbsent, isLate);
        recycler.setAdapter(mAdapter);
    }
}
