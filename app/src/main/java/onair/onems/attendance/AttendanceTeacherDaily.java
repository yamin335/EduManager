package onair.onems.attendance;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import onair.onems.customised.MyDividerItemDecoration;
import onair.onems.mainactivities.SideNavigationMenuParentActivity;
import onair.onems.mainactivities.TeacherMainScreen;
import onair.onems.models.BranchModel;
import onair.onems.models.InstituteDepartmentModel;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class AttendanceTeacherDaily extends SideNavigationMenuParentActivity {
    private Spinner spinnerBranch, spinnerDepartment;
    private ArrayList<BranchModel> allBranchArrayList;
    private ArrayList<InstituteDepartmentModel> allDepartmentArrayList;
    private String[] tempBranchArray = {"Select Branch"};
    private String[] tempDepartmentArray = {"Select Department"};
    private BranchModel selectedBranch;
    private InstituteDepartmentModel selectedDepartment;
    private DatePickerDialog datePickerDialog;
    private CompositeDisposable finalDisposer = new CompositeDisposable();
    private Button datePicker, show;
    private RecyclerView recyclerView;
    private String selectedDate = "";
    private TextView totalTeacher, totalLeave, totalPresent, totalAbsent, totalLate;
    private AttendanceTeacherDailyAdapter mAdapter;
    private ArrayList<JsonObject> attendanceReportList;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!finalDisposer.isDisposed())
            finalDisposer.dispose();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityName = AttendanceTeacherDaily.class.getName();

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View childActivityLayout = Objects.requireNonNull(inflater).inflate(R.layout.attendance_teacher_daily, null);
        LinearLayout parentActivityLayout = findViewById(R.id.contentMain);
        parentActivityLayout.addView(childActivityLayout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        spinnerBranch = findViewById(R.id.spinnerBranch);
        spinnerDepartment = findViewById(R.id.spinnerDepartment);
        datePicker = findViewById(R.id.date);
        show = findViewById(R.id.show);
        recyclerView = findViewById(R.id.recycler);
        totalTeacher = findViewById(R.id.totalTeacher);
        totalLeave = findViewById(R.id.totalLeave);
        totalPresent = findViewById(R.id.totalPresent);
        totalAbsent = findViewById(R.id.totalAbsent);
        totalLate = findViewById(R.id.totalLate);

        selectedBranch = new BranchModel();
        selectedDepartment = new InstituteDepartmentModel();

        allBranchArrayList = new ArrayList<>();
        allDepartmentArrayList = new ArrayList<>();
        attendanceReportList = new ArrayList<>();

        ArrayAdapter<String> branch_spinner_adapter = new ArrayAdapter<>(this, R.layout.spinner_item, tempBranchArray);
        branch_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBranch.setAdapter(branch_spinner_adapter);

        ArrayAdapter<String> department_spinner_adapter = new ArrayAdapter<>(this, R.layout.spinner_item, tempDepartmentArray);
        department_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDepartment.setAdapter(department_spinner_adapter);

        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");
        selectedDate = df.format(date);
        datePicker.setText(selectedDate);

        datePicker.setOnClickListener(v -> {
            // calender class's instance and get current date , month and year from calender
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR); // current year
            int mMonth = c.get(Calendar.MONTH); // current month
            int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
            // date picker dialog
            datePickerDialog = new DatePickerDialog(AttendanceTeacherDaily.this,
                    (view, year, monthOfYear, dayOfMonth) -> {
                        selectedDate = (monthOfYear + 1)+"-"+dayOfMonth+"-"+year;
                        datePicker.setText(selectedDate);

                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        });

        spinnerBranch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != 0) {
                    try {
                        selectedBranch = allBranchArrayList.get(position-1);
                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(AttendanceTeacherDaily.this,"No branch found !!!",Toast.LENGTH_LONG).show();
                    }
                } else {
                    selectedBranch = new BranchModel();
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
                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(AttendanceTeacherDaily.this,"No department found !!!",Toast.LENGTH_LONG).show();
                    }
                } else {
                    selectedDepartment = new InstituteDepartmentModel();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        show.setOnClickListener(view-> AttendanceDataGetRequest());

        mAdapter = new AttendanceTeacherDailyAdapter(this, attendanceReportList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        BranchDataGetRequest();
        InstituteDepartmentDataGetRequest();
    }

    private void BranchDataGetRequest() {
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
                    .getBranchByInsID(InstituteID)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io());

            finalDisposer.add( observable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io())
                    .subscribeWith(new DisposableObserver<String>() {

                        @Override
                        public void onNext(String BranchDataReturnValue) {
                            dialog.dismiss();
                            parseBranchData(BranchDataReturnValue);
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
            Toast.makeText(AttendanceTeacherDaily.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    void parseBranchData(String jsonString) {
        ArrayList<String> branchArrayList = new ArrayList<>();
        try {
            allBranchArrayList = new ArrayList<>();
            JSONArray branchJsonArray = new JSONArray(jsonString);
            branchArrayList.add("Select Branch");
            for(int i = 0; i < branchJsonArray.length(); ++i) {
                JSONObject branchJsonObject = branchJsonArray.getJSONObject(i);
                BranchModel branchModel = new BranchModel(branchJsonObject.getString("BrunchID"), branchJsonObject.getString("BrunchNo")
                        , branchJsonObject.getString("BrunchName"), branchJsonObject.getString("ParentID"), branchJsonObject.getString("InstituteID")
                        , branchJsonObject.getString("ParentBranch"), branchJsonObject.getString("Institute"));
                allBranchArrayList.add(branchModel);
                branchArrayList.add(branchModel.getBrunchName());
            }
        } catch (JSONException e) {
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
        }
        try {
            String[] strings = new String[branchArrayList.size()];
            strings = branchArrayList.toArray(strings);
            ArrayAdapter<String> branch_spinner_adapter = new ArrayAdapter<>(this,R.layout.spinner_item, strings);
            branch_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerBranch.setAdapter(branch_spinner_adapter);
        } catch (IndexOutOfBoundsException e) {
            Toast.makeText(this,"No branch found !!!",Toast.LENGTH_LONG).show();
        }
    }

    private void InstituteDepartmentDataGetRequest() {
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
                    .getInsDepertment(InstituteID)
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
            Toast.makeText(AttendanceTeacherDaily.this,"Please check your internet connection and select again!!! ",
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
                InstituteDepartmentModel departmentModel = new InstituteDepartmentModel(departmentJsonObject.getString("DepartmentID"), departmentJsonObject.getString("DepartmentName"));
                allDepartmentArrayList.add(departmentModel);
                departmentArrayList.add(departmentModel.getDepartmentName());
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

    private void AttendanceDataGetRequest() {
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
                    .gethrmDeviceByParmsforTeacher(selectedBranch.getBrunchID(), selectedDepartment.getDepartmentID(), selectedDate, InstituteID, 4)
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
                        public void onError(Throwable e) {
                            dialog.dismiss();
                        }

                        @Override
                        public void onComplete() {

                        }
                    }));
        } else {
            Toast.makeText(AttendanceTeacherDaily.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void parseAttendanceData(String response) {
        if (!response.equalsIgnoreCase("null") && !response.equalsIgnoreCase("[]")) {
            try {
                int leave = 0, present = 0, late = 0;
                JsonParser parser = new JsonParser();
                JsonArray jsonArray = parser.parse(response).getAsJsonArray();
                if (jsonArray.size() > 0) {
                    attendanceReportList.clear();
                    attendanceReportList.add(new JsonObject());
                    for (JsonElement jsonElement : jsonArray) {
                        attendanceReportList.add(jsonElement.getAsJsonObject());
                        if (!jsonElement.getAsJsonObject().get("Leave").isJsonNull()) {
                            if (jsonElement.getAsJsonObject().get("Leave").getAsInt()==1) {
                                leave++;
                            }
                        }

                        if (!jsonElement.getAsJsonObject().get("Present").isJsonNull()) {
                            if (jsonElement.getAsJsonObject().get("Present").getAsInt()==1) {
                                present++;
                            }
                        }

                        if (!jsonElement.getAsJsonObject().get("LateInMin").isJsonNull()) {
                            if (jsonElement.getAsJsonObject().get("LateInMin").getAsInt()>=1) {
                                late++;
                            }
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                    totalTeacher.setText(Integer.toString(attendanceReportList.size()-1));
                    totalLeave.setText(Integer.toString(leave));
                    totalPresent.setText(Integer.toString(present));
                    totalAbsent.setText(Integer.toString(attendanceReportList.size()-(present+1)));
                    totalLate.setText(Integer.toString(late));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(AttendanceTeacherDaily.this,"Attendance data not found!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent mainIntent = new Intent(AttendanceTeacherDaily.this, TeacherMainScreen.class);
            startActivity(mainIntent);
            finish();
        }
    }
}
