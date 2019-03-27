package onair.onems.attendance;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

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

public class SelfAttendanceReport extends Fragment implements DateWiseAttendanceAdapter.DateWiseAttendanceAdapterListener {
    private Spinner spinnerMonth;
    private String UserID="";
    private long SectionID, ClassID, ShiftID, MediumID, DepartmentID, InstituteID, SessionID;
    private ArrayList<MonthModel> allMonthArrayList;
    private String[] tempMonthArray = {"Select Month"};
    private MonthModel selectedMonth = null;
    private ArrayList<DailyAttendanceModel> dailyAttendanceList;
    private DailyAttendanceModel selectedDay;
    private TextView totalClass, totalPresent;
    private String ImageUrl = "";
    private CompositeDisposable finalDisposer = new CompositeDisposable();
    public CommonProgressDialog dialog;
    private DateWiseAttendanceAdapter adapter;
    private RecyclerView recyclerView;

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

        totalClass = rootView.findViewById(R.id.totalClass);
        totalPresent = rootView.findViewById(R.id.totalPresent);
        recyclerView = rootView.findViewById(R.id.recycler);
        ImageView studentImage = rootView.findViewById(R.id.studentImage);
        TextView name = rootView.findViewById(R.id.teacherName);
        TextView roll = rootView.findViewById(R.id.roll);
        TextView id = rootView.findViewById(R.id.Id);

        dailyAttendanceList = new ArrayList<>();
        adapter = new DateWiseAttendanceAdapter(getContext(), dailyAttendanceList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

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
            UserID = sharedPre.getString("UserID", "");
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
                    .asBitmap()
                    .error(getResources().getDrawable(R.drawable.profileavater))
                    .load(getString(R.string.baseUrl)+"/"+ImageUrl.replace("\\","/"))
                    .apply(RequestOptions.circleCropTransform())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, Transition<? super Bitmap> transition) {
                            if(resource != null) {
                                studentImage.setImageBitmap(resource);
                            }
                        }
                        @Override
                        public void onLoadFailed(Drawable errorDrawable) {
                            super.onLoadFailed(errorDrawable);
                            studentImage.setImageDrawable(errorDrawable);
                            Toast.makeText(getActivity(),"No image found!!!",Toast.LENGTH_LONG).show();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

        selectedMonth = new MonthModel();

        spinnerMonth = rootView.findViewById(R.id.spinnerMonth);
        ArrayAdapter<String> month_spinner_adapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()), R.layout.spinner_item, tempMonthArray);
        month_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(month_spinner_adapter);

        MonthDataGetRequest();

        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    if (allMonthArrayList.size()>0) {
                        selectedMonth = allMonthArrayList.get(position);
                        MonthlyAttendanceDataGetRequest(selectedMonth.getMonthID());
                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity(),"No month found !!!",Toast.LENGTH_LONG).show();
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
            dailyAttendanceList.clear();
            JSONArray dailyAttendanceJsonArray = new JSONArray(jsonString);
            for(int i = 0; i < dailyAttendanceJsonArray.length(); ++i) {
                JSONObject dailyAttendanceJsonObject = dailyAttendanceJsonArray.getJSONObject(i);
                if(i == 0) {
                    totalClass.setText("Total class: "+dailyAttendanceJsonObject.getString("TotalClassDay"));
                    totalPresent.setText("Total present: "+dailyAttendanceJsonObject.getJSONArray("TotalPresent").optString(0));
                }
                DailyAttendanceModel perDayAttendance = new DailyAttendanceModel();
                perDayAttendance.setDate(dailyAttendanceJsonObject.getString("Date"));
                perDayAttendance.setPresent(dailyAttendanceJsonObject.getString("Present"));
                if (dailyAttendanceJsonObject.getString("Late").charAt(0) == '-') {
                    StringBuilder lateTime = new StringBuilder(dailyAttendanceJsonObject.getString("Late"));
                    lateTime.deleteCharAt(0);
                    perDayAttendance.setLate(lateTime.toString());
                }
                perDayAttendance.setTotalClassDay(dailyAttendanceJsonObject.getString("TotalClassDay"));
                perDayAttendance.setTotalPresent(dailyAttendanceJsonObject.getJSONArray("TotalPresent").optString(0));
                dailyAttendanceList.add(perDayAttendance);
                adapter.notifyDataSetChanged();
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
                            totalClass.setText("Total class: ");
                            totalPresent.setText("Total present: ");
                            dailyAttendanceList.clear();
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

    @Override
    public void onDateSelected(DailyAttendanceModel object) {
        selectedDay = object;
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
    }
}