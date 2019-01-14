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
import org.json.JSONObject;

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
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class StudentSubjectWiseAttendance extends CommonToolbarParentActivity {
    private Configuration config;
    private TableView tableView;
    private SimpleTableHeaderAdapter simpleTableHeaderAdapter;
    private long InstituteID=0, SectionID=0, ClassID=0, MediumID=0, ShiftID=0, DepartmentID = 0;
    private Disposable disposable;
    private String UserID, Date;

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
        final View childActivityLayout = Objects.requireNonNull(inflater).inflate(R.layout.attendance_report_subjectwise, null);
        LinearLayout parentActivityLayout = findViewById(R.id.contentMain);
        parentActivityLayout.addView(childActivityLayout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        SharedPreferences sharedPre = PreferenceManager.getDefaultSharedPreferences(this);
        InstituteID = sharedPre.getLong("InstituteID", 0);

        tableView = (TableView) findViewById(R.id.tableView);

        simpleTableHeaderAdapter = new SimpleTableHeaderAdapter(this, "Subject Name", "Code ", "Status", "Teacher");
        simpleTableHeaderAdapter.setTextColor(ContextCompat.getColor(this, R.color.table_header_text));
        tableView.setHeaderAdapter(simpleTableHeaderAdapter);

        //Loding show end code
        // get Internal Data using SharedPreferences
        Intent intent = getIntent();
        ShiftID = intent.getLongExtra("ShiftID", 0);
        MediumID = intent.getLongExtra("MediumID", 0);
        ClassID = intent.getLongExtra("ClassID", 0);
        DepartmentID = intent.getLongExtra("DepartmentID", 0);
        SectionID = intent.getLongExtra("SectionID", 0);
        UserID = intent.getStringExtra("UserID");
        Date = intent.getStringExtra("Date");

        int colorEvenRows = getResources().getColor(R.color.table_data_row_even);
        int colorOddRows = getResources().getColor(R.color.table_data_row_odd);
        tableView.setDataRowBackgroundProvider(TableDataRowBackgroundProviders.alternatingRowColors(colorEvenRows, colorOddRows));

        TableColumnWeightModel columnModel = new TableColumnWeightModel(4);
        columnModel.setColumnWeight(3, 5);
        columnModel.setColumnWeight(1, 3);
        columnModel.setColumnWeight(2, 4);
        columnModel.setColumnWeight(0, 4);
        tableView.setColumnModel(columnModel);

        config = getResources().getConfiguration();
        if (config.smallestScreenWidthDp > 320) {
            simpleTableHeaderAdapter.setTextSize(14);

        } else {
            simpleTableHeaderAdapter.setTextSize(10);

        }
        AttendanceDataGetRequest();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        finish();
        return true;

    }

    void parseSubjectWiseAttendanceJsonData(String jsonString) {
        try {
            JSONArray subjectWiseAttendanceJsonArray = new JSONArray(jsonString);
            String[][] DATA_TO_SHOW = new String[subjectWiseAttendanceJsonArray.length()][4];
            for (int i = 0; i < subjectWiseAttendanceJsonArray.length(); ++i) {

                JSONObject subjectWiseAttendanceJsonObject = subjectWiseAttendanceJsonArray.getJSONObject(i);
                DATA_TO_SHOW[i][0] = subjectWiseAttendanceJsonObject.getString("Subject");
                DATA_TO_SHOW[i][1] = String.valueOf(subjectWiseAttendanceJsonObject.getInt("SubjectID"));
                int status = subjectWiseAttendanceJsonObject.getInt("Status");
                if (status == 1) {
                    DATA_TO_SHOW[i][2] = "Present";
                } else if(status == 2) {
                    DATA_TO_SHOW[i][2] = "Absent";
                } else if(status == 3) {
                    DATA_TO_SHOW[i][2] = "Late";
                } else if(status == 0) {
                    DATA_TO_SHOW[i][2] = "Leave";
                }
                DATA_TO_SHOW[i][3] = subjectWiseAttendanceJsonObject.getString("ClassTeacher");
            }

            SimpleTableDataAdapter simpleTabledataAdapter = new SimpleTableDataAdapter(this, DATA_TO_SHOW);
            tableView.setDataAdapter(simpleTabledataAdapter);
            int colorEvenRows = getResources().getColor(R.color.table_data_row_even);
            int colorOddRows = getResources().getColor(R.color.table_data_row_odd);
            tableView.setDataRowBackgroundProvider(TableDataRowBackgroundProviders.alternatingRowColors(colorEvenRows, colorOddRows));
            TableColumnWeightModel columnModel = new TableColumnWeightModel(4);
            columnModel.setColumnWeight(3, 5);
            columnModel.setColumnWeight(1, 3);
            columnModel.setColumnWeight(2, 4);
            columnModel.setColumnWeight(0, 4);
            tableView.setColumnModel(columnModel);
            if (config.smallestScreenWidthDp > 320) {
                simpleTableHeaderAdapter.setTextSize(14);
                simpleTabledataAdapter.setTextSize(12);
            } else {
                simpleTableHeaderAdapter.setTextSize(10);
                simpleTabledataAdapter.setTextSize(10);
            }

        } catch (Exception e) {
            Toast.makeText(this, "" + e, Toast.LENGTH_LONG).show();
        }
    }

    public void AttendanceDataGetRequest(){
        if(StaticHelperClass.isNetworkAvailable(this)) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(getString(R.string.baseUrl))
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();

            Observable<String> observable = retrofit
                    .create(RetrofitNetworkService.class)
                    .getHrmSubWiseAtdByStudentID(ShiftID, MediumID, ClassID, SectionID, DepartmentID, UserID, Date, InstituteID)
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
                            parseSubjectWiseAttendanceJsonData(response);
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            Toast.makeText(StudentSubjectWiseAttendance.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }
}
