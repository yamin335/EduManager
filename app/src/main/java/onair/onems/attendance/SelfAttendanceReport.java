package onair.onems.attendance;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.model.TableColumnWeightModel;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import de.codecrafters.tableview.toolkit.TableDataRowBackgroundProviders;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import onair.onems.R;
import onair.onems.Services.GlideApp;
import onair.onems.Services.RetrofitNetworkService;
import onair.onems.Services.StaticHelperClass;
import onair.onems.mainactivities.CommonProgressDialog;
import onair.onems.models.MonthModel;
import onair.onems.models.DailyAttendanceModel;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class SelfAttendanceReport extends Fragment {
    private TableView tableView;
    private Spinner spinnerMonth;
    private String UserID="";
    private Configuration config;
    private long SectionID, ClassID, ShiftID, MediumID, DepartmentID, InstituteID, SessionID;
    private SimpleTableHeaderAdapter simpleTableHeaderAdapter;
    private ArrayList<MonthModel> allMonthArrayList;
    private String[] tempMonthArray = {"Select Month"};
    private MonthModel selectedMonth = null;
    private ArrayList<DailyAttendanceModel> dailyAttendanceList;
    private DailyAttendanceModel selectedDay;
    private TextView totalClass, totalPresent;
    private String ImageUrl = "";
    private CompositeDisposable finalDisposer = new CompositeDisposable();
    public CommonProgressDialog dialog;
    public SelfAttendanceReport() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!finalDisposer.isDisposed())
            finalDisposer.dispose();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.attendance_report_self_attendance, container, false);
        dialog = new CommonProgressDialog(getActivity());
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        tableView = rootView.findViewById(R.id.tableView);
        totalClass = rootView.findViewById(R.id.totalClass);
        totalPresent = rootView.findViewById(R.id.totalPresent);
        ImageView studentImage = rootView.findViewById(R.id.studentImage);
        TextView name = rootView.findViewById(R.id.name);
        TextView roll = rootView.findViewById(R.id.roll);
        TextView id = rootView.findViewById(R.id.Id);
        tableView.setColumnCount(4);

        SharedPreferences sharedPre = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int userTypeID = sharedPre.getInt("UserTypeID", 0);
        InstituteID=sharedPre.getLong("InstituteID",0);
        String userFullName = "";
        String rollNo = "";
        String RFID = "";
        if(userTypeID == 3) {
            DepartmentID=sharedPre.getLong("SDepartmentID",0);
            ShiftID=sharedPre.getLong("ShiftID",0);
            MediumID=sharedPre.getLong("MediumID",0);
            ClassID=sharedPre.getLong("ClassID",0);
            SectionID=sharedPre.getLong("SectionID",0);
            SessionID = sharedPre.getLong("SessionID",0);
            userFullName = sharedPre.getString("UserFullName","");
            rollNo = sharedPre.getString("RollNo","");
            RFID = sharedPre.getString("RFID","");
            ImageUrl = sharedPre.getString("ImageUrl","");
            name.setText(userFullName);
            roll.setText("Roll: "+ rollNo);
            id.setText("ID: "+ RFID);
        } else if(userTypeID == 5) {
            try {
                JSONObject selectedStudent = new JSONObject(getActivity().getSharedPreferences("CURRENT_STUDENT", Context.MODE_PRIVATE)
                        .getString("guardianSelectedStudent", "{}"));
                if(selectedStudent.getString("SessionID").equalsIgnoreCase("null")||selectedStudent.getString("SessionID").equalsIgnoreCase("")) {
                    SessionID = 0;
                } else {
                    SessionID = Long.parseLong(selectedStudent.getString("SessionID"));
                }
                if(selectedStudent.getString("ShiftID").equalsIgnoreCase("null")||selectedStudent.getString("ShiftID").equalsIgnoreCase("")) {
                    ShiftID = 0;
                } else {
                    ShiftID = Long.parseLong(selectedStudent.getString("ShiftID"));
                }
                if(selectedStudent.getString("MediumID").equalsIgnoreCase("null")||selectedStudent.getString("MediumID").equalsIgnoreCase("")) {
                    MediumID = 0;
                } else {
                    MediumID = Long.parseLong(selectedStudent.getString("MediumID"));
                }
                if(selectedStudent.getString("ClassID").equalsIgnoreCase("null")||selectedStudent.getString("ClassID").equalsIgnoreCase("")) {
                    ClassID = 0;
                } else {
                    ClassID = Long.parseLong(selectedStudent.getString("ClassID"));
                }
                if(selectedStudent.getString("SectionID").equalsIgnoreCase("null")||selectedStudent.getString("SectionID").equalsIgnoreCase("")) {
                    SectionID = 0;
                } else {
                    SectionID = Long.parseLong(selectedStudent.getString("SectionID"));
                }
                if(selectedStudent.getString("DepartmentID").equalsIgnoreCase("null")||selectedStudent.getString("DepartmentID").equalsIgnoreCase("")) {
                    DepartmentID = 0;
                } else {
                    DepartmentID = Long.parseLong(selectedStudent.getString("DepartmentID"));
                }
                UserID = selectedStudent.getString("UserID");
                userFullName = selectedStudent.getString("UserFullName");
                rollNo = selectedStudent.getString("RollNo");
                RFID = selectedStudent.getString("RFID");
                ImageUrl = selectedStudent.getString("ImageUrl");
                name.setText(userFullName);
                roll.setText("Roll: "+ rollNo);
                id.setText("ID: "+ RFID);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        try {
            GlideApp.with(this)
                    .load(getString(R.string.baseUrl)+"/"+ImageUrl.replace("\\","/")).apply(RequestOptions.circleCropTransform())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(studentImage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        selectedMonth = new MonthModel();

        spinnerMonth = rootView.findViewById(R.id.spinnerMonth);
        ArrayAdapter<String> month_spinner_adapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()), R.layout.spinner_item, tempMonthArray);
        month_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(month_spinner_adapter);

        simpleTableHeaderAdapter = new SimpleTableHeaderAdapter(getActivity(),"SI","Date","Present", "Late(min)");

        simpleTableHeaderAdapter.setTextColor(ContextCompat.getColor(getActivity(), R.color.table_header_text ));
        tableView.setHeaderAdapter(simpleTableHeaderAdapter);
        config = getResources().getConfiguration();

        if (config.smallestScreenWidthDp >320) {
            simpleTableHeaderAdapter.setTextSize(14);
        } else {
            simpleTableHeaderAdapter.setTextSize(10);
        }
        int colorEvenRows = getResources().getColor(R.color.table_data_row_even);
        int colorOddRows = getResources().getColor(R.color.table_data_row_odd);
        tableView.setDataRowBackgroundProvider(TableDataRowBackgroundProviders.alternatingRowColors(colorEvenRows, colorOddRows));
        TableColumnWeightModel columnModel = new TableColumnWeightModel(4);
        columnModel.setColumnWeight(1, 2);
        tableView.setColumnModel(columnModel);

        tableView.addDataClickListener((rowIndex, clickedData) -> {
            selectedDay = dailyAttendanceList.get(rowIndex);
            Intent intent = new Intent(getActivity(), StudentSubjectWiseAttendance.class);
            intent.putExtra("SessionID", SessionID);
            intent.putExtra("UserID", UserID);
            intent.putExtra("ShiftID", ShiftID);
            intent.putExtra("MediumID", MediumID);
            intent.putExtra("ClassID", ClassID);
            intent.putExtra("DepartmentID", DepartmentID);
            intent.putExtra("SectionID", SectionID);
            intent.putExtra("Date", selectedDay.getDate());
            startActivity(intent);
        });

        MonthDataGetRequest();

        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                try {
                    selectedMonth = allMonthArrayList.get(position);
                    MonthlyAttendanceDataGetRequest(selectedMonth.getMonthID());
                } catch (Exception e) {
                    Toast.makeText(getActivity(),"No section found !!!",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return rootView;
    }

    void parseMonthJsonData(String jsonString) {

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
                ArrayAdapter<String> month_spinner_adapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()),R.layout.spinner_item, strings);
                month_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerMonth.setAdapter(month_spinner_adapter);
                Calendar c = Calendar.getInstance();
                spinnerMonth.setSelection(c.get(Calendar.MONTH));
            } catch (IndexOutOfBoundsException e) {
                Toast.makeText(getActivity(),"No class found !!!",Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {
            Toast.makeText(getActivity(),""+e,Toast.LENGTH_LONG).show();
        }

    }

    void parseMonthlyAttendanceJsonData(String jsonString) {
        try {
            dailyAttendanceList = new ArrayList<>();
            JSONArray dailyAttendanceJsonArray = new JSONArray(jsonString);
            String[][] DATA_TO_SHOW = new String[dailyAttendanceJsonArray.length()][4];
            for(int i = 0; i < dailyAttendanceJsonArray.length(); ++i) {
                JSONObject dailyAttendanceJsonObject = dailyAttendanceJsonArray.getJSONObject(i);
                if(i == 0) {
                    totalClass.setText("Total class: "+dailyAttendanceJsonObject.getString("TotalClassDay"));
                    totalPresent.setText("Total present: "+dailyAttendanceJsonObject.getJSONArray("TotalPresent").optString(0));
                }
                DailyAttendanceModel perDayAttendance = new DailyAttendanceModel();
                perDayAttendance.setDate(dailyAttendanceJsonObject.getString("Date"));
                perDayAttendance.setPresent(dailyAttendanceJsonObject.getString("Present"));
                perDayAttendance.setLate(dailyAttendanceJsonObject.getString("Late"));
                perDayAttendance.setTotalClassDay(dailyAttendanceJsonObject.getString("TotalClassDay"));
                perDayAttendance.setTotalPresent(dailyAttendanceJsonObject.getJSONArray("TotalPresent").optString(0));
                DATA_TO_SHOW[i][0] = String.valueOf((i+1));
                DATA_TO_SHOW [i][1] = perDayAttendance.getDate();
                DATA_TO_SHOW[i][2] = perDayAttendance.getPresent() == 1 ? "YES" : "NO";
                DATA_TO_SHOW [i][3] = Integer.toString(perDayAttendance.getLate());
                dailyAttendanceList.add(perDayAttendance);
            }

            SimpleTableDataAdapter simpleTabledataAdapter = new SimpleTableDataAdapter(getActivity(),DATA_TO_SHOW);
            tableView.setDataAdapter(simpleTabledataAdapter);
            if (config.smallestScreenWidthDp >320) {
                simpleTableHeaderAdapter.setTextSize(14);
                simpleTabledataAdapter.setTextSize(12);
            } else {
                simpleTableHeaderAdapter.setTextSize(10);
                simpleTabledataAdapter.setTextSize(10);
            }
        } catch (JSONException e) {
            Toast.makeText(getActivity(),""+e,Toast.LENGTH_LONG).show();
        }
    }

    void MonthDataGetRequest(){
        if(StaticHelperClass.isNetworkAvailable(Objects.requireNonNull(getActivity()))) {
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
                            dialog.dismiss();
                        }

                        @Override
                        public void onComplete() {

                        }
                    }));
        } else {
            Toast.makeText(getActivity(),"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    void MonthlyAttendanceDataGetRequest(int MonthID){
        if(StaticHelperClass.isNetworkAvailable(Objects.requireNonNull(getActivity()))) {
            dialog.show();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(getString(R.string.baseUrl))
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();

            Observable<String> observable = retrofit
                    .create(RetrofitNetworkService.class)
                    .getStudentMonthlyDeviceAttendance(ShiftID, MediumID, ClassID, SectionID, DepartmentID, MonthID, UserID, InstituteID, SessionID)
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
                            parseMonthlyAttendanceJsonData(response);
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
            Toast.makeText(getActivity(),"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }
}