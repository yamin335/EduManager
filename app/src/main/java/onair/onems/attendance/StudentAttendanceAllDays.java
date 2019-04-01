package onair.onems.attendance;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import onair.onems.mainactivities.CommonToolbarParentActivity;
import onair.onems.models.DailyAttendanceModel;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class StudentAttendanceAllDays extends CommonToolbarParentActivity implements DateWiseAttendanceAdapter.DateWiseAttendanceAdapterListener {
    private ArrayList<DailyAttendanceModel> dailyAttendanceList;
    private DailyAttendanceModel selectedDay;
    private String UserID = "";
    private long SectionID=0;
    private long ClassID=0;
    private long MediumID=0;
    private long ShiftID=0;
    private long DepartmentID = 0;
    private long SessionID = 0;
    private TextView totalClass, totalPresent;
    private int MonthID = 0;
    private CompositeDisposable finalDisposer = new CompositeDisposable();
    private DateWiseAttendanceAdapter adapter;
    private RecyclerView recyclerView;

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
        final View childActivityLayout = Objects.requireNonNull(inflater).inflate(R.layout.attendance_report_all_days, null);
        LinearLayout parentActivityLayout = findViewById(R.id.contentMain);
        parentActivityLayout.addView(childActivityLayout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        recyclerView = findViewById(R.id.recycler);
        totalClass = findViewById(R.id.totalClass);
        totalPresent = findViewById(R.id.totalPresent);
        ImageView studentImage = findViewById(R.id.studentImage);
        TextView name = findViewById(R.id.teacherName);
        TextView roll = findViewById(R.id.roll);
        TextView id = findViewById(R.id.Id);

        dailyAttendanceList = new ArrayList<>();
        selectedDay = new DailyAttendanceModel();
        adapter = new DateWiseAttendanceAdapter(this, dailyAttendanceList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        SharedPreferences sharedPre = PreferenceManager.getDefaultSharedPreferences(this);
        InstituteID = sharedPre.getLong("InstituteID", 0);

        Intent intent = getIntent();
        ShiftID = intent.getLongExtra("ShiftID", 0);
        MediumID = intent.getLongExtra("MediumID", 0);
        ClassID = intent.getLongExtra("ClassID", 0);
        DepartmentID = intent.getLongExtra("DepartmentID", 0);
        SectionID = intent.getLongExtra("SectionID", 0);
        MonthID = intent.getIntExtra("MonthID", 0);
        SessionID = intent.getLongExtra("SessionID", 0);
        String RFID = intent.getStringExtra("RFID");
        String userFullName = intent.getStringExtra("UserFullName");
        String rollNo = intent.getStringExtra("RollNo");
        UserID = intent.getStringExtra("UserID");
        String ImageUrl = intent.getStringExtra("ImageUrl");

      try {
          GlideApp.with(this)
                  .asBitmap()
                  .error(getResources().getDrawable(R.drawable.profileavater))
                  .load(getString(R.string.baseUrl) + "/" + ImageUrl.replace("\\", "/"))
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
                          Toast.makeText(StudentAttendanceAllDays.this,"No image found!!!",Toast.LENGTH_LONG).show();
                      }
                  });
      } catch (Exception e) {
          e.printStackTrace();
      }

        name.setText(""+ userFullName);
        roll.setText("Roll: "+ rollNo);
        id.setText("ID: "+ RFID);

        // Loding http Monthly Attendance string file using volley
        AttendanceDataGetRequest();

    }
    // Back parent Page code
    public boolean onOptionsItemSelected(MenuItem item){
//        Intent myIntent = new Intent(getApplicationContext(), StudentAttendanceShow.class);
//        startActivityForResult(myIntent, 0);
        onBackPressed();
        finish();
        return true;

    }

    // Back parent Page code end

    void parseMonthlyAttendanceJsonData(String jsonString) {
        try {
            dailyAttendanceList.clear();
            dailyAttendanceList.add(new DailyAttendanceModel());
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
            Toast.makeText(StudentAttendanceAllDays.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDateSelected(DailyAttendanceModel object) {
        selectedDay = object;
        Intent intent = new Intent(StudentAttendanceAllDays.this, StudentSubjectWiseAttendance.class);
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
