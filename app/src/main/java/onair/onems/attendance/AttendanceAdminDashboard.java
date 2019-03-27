package onair.onems.attendance;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class AttendanceAdminDashboard extends SideNavigationMenuParentActivity {

    private long InstituteID;
    private String today;
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

        activityName = AttendanceAdminDashboard.class.getName();

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View childActivityLayout = Objects.requireNonNull(inflater).inflate(R.layout.attendance_admin_dashboard, null);
        LinearLayout parentActivityLayout = findViewById(R.id.contentMain);
        parentActivityLayout.addView(childActivityLayout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        InstituteID = prefs.getLong("InstituteID",0);
        UserTypeID = prefs.getInt("UserTypeID",0);

        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        today = df.format(date);

        teacherAttendanceDataGetRequest();
        studentAttendanceDataGetRequest();

        Button takeAttendance, showAttendance;
        takeAttendance = findViewById(R.id.takeAttendance);
        showAttendance = findViewById(R.id.showAttendance);

        takeAttendance.setOnClickListener(v -> {
            Intent mainIntent = new Intent(AttendanceAdminDashboard.this, TakeAttendance.class);
            mainIntent.putExtra("fromDashBoard", true);
            mainIntent.putExtra("fromSideMenu", false);
            startActivity(mainIntent);
            finish();
        });

        showAttendance.setOnClickListener(v -> {
            Intent mainIntent = new Intent(AttendanceAdminDashboard.this, ShowAttendance.class);
            mainIntent.putExtra("fromDashBoard", true);
            mainIntent.putExtra("fromSideMenu", false);
            startActivity(mainIntent);
            finish();
        });

    }

    private void teacherAttendanceDataGetRequest() {
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
                    .getTotalTeacherAndTotalAttendence(today, InstituteID, "4")
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
                            parseTeacherAttendanceJsonData(response);
                        }

                        @Override
                        public void onComplete() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                            Toast.makeText(AttendanceAdminDashboard.this,"Error occurred!!! ",
                                    Toast.LENGTH_LONG).show();
                        }
                    }));
        } else {
            Toast.makeText(AttendanceAdminDashboard.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void studentAttendanceDataGetRequest() {
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
                    .gethrmDeviceByParmsforStudent(0, 0, 0, 0, 0, today, 0, 0, InstituteID, 3, 0)
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
                            parseStudentAttendanceJsonData(response);
                        }

                        @Override
                        public void onComplete() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                            Toast.makeText(AttendanceAdminDashboard.this,"Error: getting attendance data!!!",Toast.LENGTH_LONG).show();
                        }
                    }));
        } else {
            Toast.makeText(AttendanceAdminDashboard.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    void parseTeacherAttendanceJsonData(String jsonString) {
        try {
            JSONObject teacherAttendance = new JSONArray(jsonString).getJSONObject(0);
            int total = teacherAttendance.getInt("TotalStudent");
            int present = teacherAttendance.getInt("Present");
            int absent = total - present;
            double attendancePercentage = 0.0;
            if(total!=0){
                attendancePercentage = (double)(present*100)/total;
                attendancePercentage = new BigDecimal(attendancePercentage).setScale(2, RoundingMode.HALF_UP).doubleValue();
            }
            TextView totalTeacher, presentTeacher, absentTeacher, percentageTeacher;
            totalTeacher = findViewById(R.id.totalTeacher);
            presentTeacher = findViewById(R.id.attendedTeacher);
            absentTeacher = findViewById(R.id.absentTeacher);
            percentageTeacher = findViewById(R.id.attendancePercentageTeacher);
            totalTeacher.setText(Integer.toString(total));
            presentTeacher.setText(Integer.toString(present));
            absentTeacher.setText(Integer.toString(absent));
            percentageTeacher.setText(Double.toString(attendancePercentage)+"%");
        } catch (JSONException e) {
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
        }
    }

    void parseStudentAttendanceJsonData(String response) {

        JsonParser parser = new JsonParser();
        JsonArray jsonArray = parser.parse(response).getAsJsonArray();
        int total = jsonArray.size(), present = 0, absent = 0;
        for (JsonElement jsonObject : jsonArray) {
            if (jsonObject.getAsJsonObject().get("APLStatus").getAsString().equalsIgnoreCase("P")) {
                present++;
            } else if (jsonObject.getAsJsonObject().get("APLStatus").getAsString().equalsIgnoreCase("A")) {
                absent++;
            } else if (jsonObject.getAsJsonObject().get("APLStatus").getAsString().equalsIgnoreCase("L")) {
                present++;
            }
        }

        double attendancePercentage = 0.0;
        if(total!=0){
            attendancePercentage = (double)(present*100)/total;
            attendancePercentage = new BigDecimal(attendancePercentage).setScale(2, RoundingMode.HALF_UP).doubleValue();
        }

        TextView totalStudent, presentStudent, absentStudent, percentageStudent;
        totalStudent = findViewById(R.id.totalStudent);
        presentStudent = findViewById(R.id.attendedStudent);
        absentStudent = findViewById(R.id.absentStudent);
        percentageStudent = findViewById(R.id.studentPercentage);
        totalStudent.setText(Integer.toString(total));
        presentStudent.setText(Integer.toString(present));
        absentStudent.setText(Integer.toString(absent));
        percentageStudent.setText(Double.toString(attendancePercentage)+"%");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent mainIntent = new Intent(AttendanceAdminDashboard.this, TeacherMainScreen.class);
            startActivity(mainIntent);
            finish();
        }
    }
}
