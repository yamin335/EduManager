package onair.onems.attendance;

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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
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
import onair.onems.mainactivities.SideNavigationMenuParentActivity;
import onair.onems.mainactivities.TeacherMainScreen;
import onair.onems.models.MonthModel;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class AttendanceTeacherMonthly extends SideNavigationMenuParentActivity {
    private Spinner spinnerMonth;
    private TableLayout tableLayout;
    private ArrayList<MonthModel> allMonthArrayList;
    private String[] tempMonthArray = {"Select Month"};
    private MonthModel selectedMonth = null;
    private DatePickerDialog datePickerDialog;
    private String selectedFromDate = "", selectedToDate = "";
    private CompositeDisposable finalDisposer = new CompositeDisposable();
    private Button fromDate, toDate;
    private int MonthID = -1;
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!finalDisposer.isDisposed())
            finalDisposer.dispose();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityName = AttendanceTeacherMonthly.class.getName();

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View childActivityLayout = Objects.requireNonNull(inflater).inflate(R.layout.attendance_teacher_monthly, null);
        LinearLayout parentActivityLayout = findViewById(R.id.contentMain);
        parentActivityLayout.addView(childActivityLayout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        spinnerMonth = findViewById(R.id.spinnerMonth);
        Button show = findViewById(R.id.show);
        fromDate = findViewById(R.id.name);
        toDate = findViewById(R.id.toDate);
        tableLayout = findViewById(R.id.tableLayout);

        show.setOnClickListener(view-> {
            if (selectedMonth != null) {
                TeacherAttendanceDataGetRequest();
            } else {
                Toast.makeText(this,"Please select a month!!! ",
                        Toast.LENGTH_LONG).show();
            }
        });

        fromDate.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int mYear = 0, mMonth = 0, mDay = 0;
            if (MonthID == -1) {
                mYear = calendar.get(Calendar.YEAR); // current year
                mMonth = calendar.get(Calendar.MONTH); // current month
                calendar.set(Calendar.MONTH, mMonth);
                int firstDateValue = calendar.getActualMinimum(Calendar.DATE);
                calendar.set(Calendar.DATE, firstDateValue);
                mDay = calendar.get(Calendar.DAY_OF_MONTH); // current day
            } else {
                mYear = calendar.get(Calendar.YEAR); // current year
                mMonth = MonthID; // current month
                calendar.set(Calendar.MONTH, mMonth);
                int firstDateValue = calendar.getActualMinimum(Calendar.DATE);
                calendar.set(Calendar.DATE, firstDateValue);
                mDay = calendar.get(Calendar.DAY_OF_MONTH); // current day
            }
            // date picker dialog
            datePickerDialog = new DatePickerDialog(AttendanceTeacherMonthly.this,
                    (view, year, monthOfYear, dayOfMonth) -> {
                        selectedFromDate = (monthOfYear + 1)+"-"+dayOfMonth+"-"+year;
                        fromDate.setText(selectedFromDate);

                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        });

        toDate.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int mYear = 0, mMonth = 0, mDay = 0;
            if (MonthID == -1) {
                mYear = calendar.get(Calendar.YEAR); // current year
                mMonth = calendar.get(Calendar.MONTH); // current month
                calendar.set(Calendar.MONTH, mMonth);
                int lastDateValue = calendar.getActualMaximum(Calendar.DATE);
                calendar.set(Calendar.DATE, lastDateValue);
                mDay = calendar.get(Calendar.DAY_OF_MONTH); // current day
            } else {
                mYear = calendar.get(Calendar.YEAR); // current year
                mMonth = MonthID; // current month
                calendar.set(Calendar.MONTH, mMonth);
                int lastDateValue = calendar.getActualMaximum(Calendar.DATE);
                calendar.set(Calendar.DATE, lastDateValue);
                mDay = calendar.get(Calendar.DAY_OF_MONTH); // current day
            }
            // date picker dialog
            datePickerDialog = new DatePickerDialog(AttendanceTeacherMonthly.this,
                    (view, year, monthOfYear, dayOfMonth) -> {
                        selectedToDate = (monthOfYear + 1)+"-"+dayOfMonth+"-"+year;
                        toDate.setText(selectedToDate);

                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        });

        allMonthArrayList = new ArrayList<>();
        ArrayAdapter<String> month_spinner_adapter = new ArrayAdapter<>(this, R.layout.spinner_item, tempMonthArray);
        month_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(month_spinner_adapter);

        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (allMonthArrayList.size()>0) {
                    selectedMonth = allMonthArrayList.get(position);
                    MonthID = Integer.parseInt(Long.toString(selectedMonth.getMonthID()))-1;
                    prepareFromDateButton(MonthID);
                    prepareToDateButton(MonthID);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        MonthDataGetRequest();
    }

    void MonthDataGetRequest() {
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
                    .getMonth()
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
                            parseMonthJsonData(response);
                        }

                        @Override
                        public void onError(Throwable e) {
                            final Calendar calendar = Calendar.getInstance();
                            prepareFromDateButton(calendar.get(Calendar.MONTH));
                            prepareToDateButton(calendar.get(Calendar.MONTH));
                            dialog.dismiss();
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

    void parseMonthJsonData(String jsonString) {
        if (!jsonString.equalsIgnoreCase("[]")) {
            try {
                allMonthArrayList = new ArrayList<>();
                JSONArray MonthJsonArray = new JSONArray(jsonString);
                ArrayList<String> monthArrayList = new ArrayList<>();
                //monthArrayList.add("Select Month");
                for(int i = 0; i < MonthJsonArray.length(); ++i) {
                    JSONObject monthJsonObject = MonthJsonArray.getJSONObject(i);
                    MonthModel monthModel = new MonthModel(monthJsonObject.getString("MonthID"), monthJsonObject.getString("MonthName"));
                    allMonthArrayList.add(monthModel);
                    monthArrayList.add(monthModel.getMonthName());
                }

                try {
                    String[] strings = new String[monthArrayList.size()];
                    strings = monthArrayList.toArray(strings);
                    ArrayAdapter<String> month_spinner_adapter = new ArrayAdapter<>(this,R.layout.spinner_item, strings);
                    month_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerMonth.setAdapter(month_spinner_adapter);
                    Calendar c = Calendar.getInstance();
                    spinnerMonth.setSelection(c.get(Calendar.MONTH));
                } catch (IndexOutOfBoundsException e) {
                    Toast.makeText(this,"No class found !!!",Toast.LENGTH_LONG).show();
                }

            } catch (JSONException e) {
                Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
            }
        } else {
            final Calendar calendar = Calendar.getInstance();
            prepareFromDateButton(calendar.get(Calendar.MONTH));
            prepareToDateButton(calendar.get(Calendar.MONTH));
        }

    }

    void TeacherAttendanceDataGetRequest() {
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
                    .getMonthlyTeacherAttendance(InstituteID, 4, selectedMonth.getMonthID(), selectedFromDate, selectedToDate)
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
            Toast.makeText(this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void parseAttendanceData(String response) {
        if (!response.equalsIgnoreCase("null") && !response.equalsIgnoreCase("[]")) {
            try {
                JsonParser parser = new JsonParser();
                JsonArray jsonArray = parser.parse(response).getAsJsonArray();
                if (jsonArray.size() > 0) {
                    LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View headerView = Objects.requireNonNull(inflater).inflate(R.layout.attendance_teacher_monthly_header, null);
                    tableLayout.addView(headerView);
                    for (int i=0; i<jsonArray.size(); i++) {
                        int classes = 0, present = 0;
                        View rowView = Objects.requireNonNull(inflater).inflate(R.layout.attendance_teacher_monthly_row, null);
                        TextView serial = rowView.findViewById(R.id.serial);
                        serial.setText(Integer.toString(i+1));
                        JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();
                        if (!jsonObject.get("Name").isJsonNull()) {
                            TextView name = rowView.findViewById(R.id.teacherName);
                            name.setText(jsonObject.get("Name").getAsString());
                        }
                        if (!jsonObject.get("IDNO").isJsonNull()) {
                            TextView employeeId = rowView.findViewById(R.id.employeeId);
                            employeeId.setText(jsonObject.get("IDNO").getAsString());
                        }
                        if (!jsonObject.get("DesignationName").isJsonNull()) {
                            TextView designation = rowView.findViewById(R.id.designation);
                            designation.setText(jsonObject.get("DesignationName").getAsString());
                        }
                        if (!jsonObject.get("totalClassDay").isJsonNull()) {
                            TextView totalClass = rowView.findViewById(R.id.totalClass);
                            totalClass.setText(jsonObject.get("totalClassDay").getAsString());
                            classes = Integer.parseInt(jsonObject.get("totalClassDay").getAsString());
                        }
                        if (!jsonObject.get("TotalPresent").isJsonNull()) {
                            TextView totalPresent = rowView.findViewById(R.id.totalPresent);
                            totalPresent.setText(jsonObject.get("TotalPresent").getAsString());
                            present = Integer.parseInt(jsonObject.get("TotalPresent").getAsString());
                            TextView totalAbsent = rowView.findViewById(R.id.totalAbsent);
                            if (classes>present) {
                                totalAbsent.setText(Integer.toString(classes-present));
                            }
                        }
                        if (!jsonObject.get("TotalLeave").isJsonNull()) {
                            TextView totalLeave = rowView.findViewById(R.id.totalLeave);
                            totalLeave.setText(jsonObject.get("TotalLeave").getAsString());
                        }

                        tableLayout.addView(rowView, i+1);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(AttendanceTeacherMonthly.this,"Attendance data not found!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void prepareFromDateButton(int MonthID) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, MonthID);
        int firstDateValue = calendar.getActualMinimum(Calendar.DATE);
        calendar.set(Calendar.DATE, firstDateValue);
        Date firstDateOfMonth = calendar.getTime();
        SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");
        selectedFromDate= df.format(firstDateOfMonth);
        fromDate.setText(selectedFromDate);
    }

    private void prepareToDateButton(int MonthID) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, MonthID);
        int lastDateValue = calendar.getActualMaximum(Calendar.DATE);
        calendar.set(Calendar.DATE, lastDateValue);
        Date lastDateOfMonth = calendar.getTime();
        SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");
        selectedToDate = df.format(lastDateOfMonth);
        toDate.setText(selectedToDate);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent mainIntent = new Intent(AttendanceTeacherMonthly.this, TeacherMainScreen.class);
            startActivity(mainIntent);
            finish();
        }
    }
}
