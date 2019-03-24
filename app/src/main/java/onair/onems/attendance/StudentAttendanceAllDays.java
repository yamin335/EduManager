package onair.onems.attendance;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
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
import onair.onems.mainactivities.CommonToolbarParentActivity;
import onair.onems.models.DailyAttendanceModel;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class StudentAttendanceAllDays extends CommonToolbarParentActivity {
    private ArrayList<DailyAttendanceModel> dailyAttendanceList;
    private DailyAttendanceModel selectedDay;
    private TableView tableView;
    private SimpleTableHeaderAdapter simpleTableHeaderAdapter;
    private Configuration config;
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

        tableView = findViewById(R.id.tableView);
        totalClass = findViewById(R.id.totalClass);
        totalPresent = findViewById(R.id.totalPresent);
        ImageView studentImage = findViewById(R.id.studentImage);
        TextView name = findViewById(R.id.teacherName);
        TextView roll = findViewById(R.id.roll);
        TextView id = findViewById(R.id.Id);

        dailyAttendanceList = new ArrayList<>();
        selectedDay = new DailyAttendanceModel();

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

        simpleTableHeaderAdapter = new SimpleTableHeaderAdapter(this, "SI","Date","Present", "Late(m)");
        simpleTableHeaderAdapter.setTextColor(ContextCompat.getColor(this, R.color.table_header_text));
        tableView.setHeaderAdapter(simpleTableHeaderAdapter);

        // Header of The Table End

        // Set colour of the Even and Odd row of the table
        int colorEvenRows = getResources().getColor(R.color.table_data_row_even);
        int colorOddRows = getResources().getColor(R.color.table_data_row_odd);
        tableView.setDataRowBackgroundProvider(TableDataRowBackgroundProviders.alternatingRowColors(colorEvenRows, colorOddRows));

        // colour of the Even and Odd row of the table End

        // Fixed Weight of the Column
        TableColumnWeightModel columnModel = new TableColumnWeightModel(4);

        columnModel.setColumnWeight(1, 2);

        tableView.setColumnModel(columnModel);

        // Fixed Weight of the Column End

        // Configure Size of different Mobile Device

        config = getResources().getConfiguration();
        if (config.smallestScreenWidthDp > 320) {
            simpleTableHeaderAdapter.setTextSize(14);

        } else {
            simpleTableHeaderAdapter.setTextSize(10);

        }

        // Configure Size of different Mobile Device End

       //Table View Click Event
        tableView.addDataClickListener((rowIndex, clickedData) -> {
            selectedDay = dailyAttendanceList.get(rowIndex);
            Intent intent1 = new Intent(StudentAttendanceAllDays.this, StudentSubjectWiseAttendance.class);
            intent1.putExtra("SessionID", SessionID);
            intent1.putExtra("UserID", UserID);
            intent1.putExtra("ShiftID", ShiftID);
            intent1.putExtra("MediumID", MediumID);
            intent1.putExtra("ClassID", ClassID);
            intent1.putExtra("DepartmentID", DepartmentID);
            intent1.putExtra("SectionID", SectionID);
            intent1.putExtra("Date", selectedDay.getDate());
            startActivity(intent1);
        });
        //Table View Click Event  End

        // Loding http Monthly Attendance string file using volley

        AttendanceDataGetRequest();

        // Loding http Monthly Attendance string file using volley

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
                if (dailyAttendanceJsonObject.getString("Late").charAt(0) == '-') {
                    StringBuilder lateTime = new StringBuilder(dailyAttendanceJsonObject.getString("Late"));
                    lateTime.deleteCharAt(0);
                    perDayAttendance.setLate(lateTime.toString());
                }
                perDayAttendance.setTotalClassDay(dailyAttendanceJsonObject.getString("TotalClassDay"));
                perDayAttendance.setTotalPresent(dailyAttendanceJsonObject.getJSONArray("TotalPresent").optString(0));
                DATA_TO_SHOW[i][0] = String.valueOf((i+1));
                DATA_TO_SHOW [i][1] = perDayAttendance.getDate();
                DATA_TO_SHOW[i][2] = perDayAttendance.getPresent() == 1 ? "YES" : "NO";
                DATA_TO_SHOW [i][3] = Integer.toString(perDayAttendance.getLate());
                dailyAttendanceList.add(perDayAttendance);
            }

            SimpleTableDataAdapter simpleTabledataAdapter = new SimpleTableDataAdapter(this,DATA_TO_SHOW);
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

}
