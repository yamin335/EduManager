package onair.onems.attendance;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import onair.onems.R;
import onair.onems.Services.RetrofitNetworkService;
import onair.onems.Services.StaticHelperClass;
import onair.onems.mainactivities.CommonToolbarParentActivity;
import onair.onems.models.AttendanceSheetModel;
import onair.onems.models.AttendanceStudentModel;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class TakeAttendanceDetails extends CommonToolbarParentActivity {

    private TakeAttendanceAdapter adapter;
    private ArrayList<AttendanceStudentModel> attendanceSheetArrayList;
    private long InstituteID, MediumID, ShiftID, ClassID, SectionID, SubjectID, DepertmentID;
    private String date, UserID, SubAtdID;
    private JSONArray StudentJsonArray;
    private JsonObject postDataJsonObject;
    private CompositeDisposable finalDisposer = new CompositeDisposable();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!finalDisposer.isDisposed())
            finalDisposer.dispose();
    }

    @Override
    public void onResume() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        super.onResume();
    }

    @Override
    public void onStart() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        super.onStart();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View childActivityLayout = inflater.inflate(R.layout.attendance_taking_activity_details, null);
        LinearLayout parentActivityLayout = findViewById(R.id.contentMain);
        parentActivityLayout.addView(childActivityLayout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        UserID = prefs.getString("UserID", "0");

        Bundle StudentSelection = getIntent().getExtras();
        InstituteID = StudentSelection.getLong("InstituteID",0);
        MediumID = StudentSelection.getLong("MediumID",0);
        ShiftID = StudentSelection.getLong("ShiftID",0);
        ClassID = StudentSelection.getLong("ClassID",0);
        SectionID = StudentSelection.getLong("SectionID",0);
        SubjectID = StudentSelection.getLong("SubjectID",0);
        DepertmentID = StudentSelection.getLong("DepertmentID",0);
        date = StudentSelection.getString("Date", "");

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        attendanceSheetArrayList = new ArrayList<AttendanceStudentModel>();
        adapter = new TakeAttendanceAdapter(this, attendanceSheetArrayList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        StudentDataGetRequest();
    }

    private void prepareAlbums(String jsonString) {

        try {
            StudentJsonArray = new JSONArray(jsonString);
            for(int i = 0; i < StudentJsonArray.length(); ++i) {
                JSONObject studentJsonObject = StudentJsonArray.getJSONObject(i);
                AttendanceStudentModel attendanceStudentModel = new AttendanceStudentModel(
                        studentJsonObject.getString("SubAtdDetailID"),
                        studentJsonObject.getString("SubAtdID"),
                        studentJsonObject.getString("UserFullName"),
                        studentJsonObject.getString("UserID"),
                        studentJsonObject.getString("RFID"),
                        studentJsonObject.getString("RollNo"),
                        studentJsonObject.getString("SubjectID"),
                        studentJsonObject.getString("DepartmentID"),
                        studentJsonObject.getString("SectionID"),
                        studentJsonObject.getString("Section"),
                        studentJsonObject.getString("MediumID"),
                        studentJsonObject.getString("ShiftID"),
                        studentJsonObject.getString("ClassID"),
                        studentJsonObject.getString("BoardID"),
                        studentJsonObject.getString("Board"),
                        studentJsonObject.getString("Brunch"),
                        studentJsonObject.getString("Session"),
                        studentJsonObject.getString("BrunchID"),
                        studentJsonObject.getString("SessionID"),
                        studentJsonObject.getString("IsPresent"),
                        studentJsonObject.getString("Islate"),
                        studentJsonObject.getString("LateTime"),
                        studentJsonObject.getString("IsLeave"),
                        studentJsonObject.getString("IsAbsent"),
                        studentJsonObject.getString("Remarks"),
                        studentJsonObject.getString("Subject"),
                        studentJsonObject.getString("Department"),
                        studentJsonObject.getString("Medium"),
                        studentJsonObject.getString("Shift"),
                        studentJsonObject.getString("Class"),
                        studentJsonObject.getString("DisplayDate"),
                        "true",
                        "0");
                attendanceSheetArrayList.add(attendanceStudentModel);
                SubAtdID = studentJsonObject.getString("SubAtdID");
            }
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.done, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.done) {
            if(StaticHelperClass.isNetworkAvailable(this)) {
                AttendanceSheetModel attendanceSheetModel = new AttendanceSheetModel();
                attendanceSheetModel.setSubAtdID(SubAtdID);
                attendanceSheetModel.setSubjectID(Long.toString(SubjectID));
                attendanceSheetModel.setSectionID(Long.toString(SectionID==0 ? -2 : SectionID));
                attendanceSheetModel.setDepartmentID(Long.toString(DepertmentID==0 ? -2 : DepertmentID));
                attendanceSheetModel.setMediumID(Long.toString(MediumID==0 ? -2 : MediumID));
                attendanceSheetModel.setShiftID(Long.toString(ShiftID==0 ? -2 : ShiftID));
                attendanceSheetModel.setClassID(Long.toString(ClassID==0 ? -2 : ClassID));
                attendanceSheetModel.setAtdUserID(UserID);
                attendanceSheetModel.setAtdDate(date);
                attendanceSheetModel.setInstituteID(Long.toString(InstituteID));
                attendanceSheetModel.setLoggedUserID(UserID);
                attendanceSheetModel.setattendenceArr(attendanceSheetArrayList);
                Gson gson = new Gson();
                String json = gson.toJson(attendanceSheetModel);
                postUsingVolley(json);
                return true;
            }
            else
            {
                Toast.makeText(TakeAttendanceDetails.this,"Please check your internet connection!!!",Toast.LENGTH_LONG).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void postUsingVolley(String json) {
        try {
            postDataJsonObject = new JsonObject();
            JsonParser parser = new JsonParser();
            postDataJsonObject = parser.parse(json).getAsJsonObject();

        } catch (JsonIOException e) {
            e.printStackTrace();
        }

        if(StaticHelperClass.isNetworkAvailable(this)) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(getString(R.string.baseUrl))
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();

            Observable<String> observable = retrofit
                    .create(RetrofitNetworkService.class)
                    .setHrmSubWiseAtd(postDataJsonObject)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());

            finalDisposer.add( observable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableObserver<String>() {

                        @Override
                        public void onNext(String response) {
                            Toast.makeText(TakeAttendanceDetails.this,"Attendance successfully taken",Toast.LENGTH_LONG).show();
                            finish();
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    }));
        } else {
            Toast.makeText(TakeAttendanceDetails.this,"Please check your internet connection!!!",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void StudentDataGetRequest(){
        if(StaticHelperClass.isNetworkAvailable(this)) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(getString(R.string.baseUrl))
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();

            Observable<String> observable = retrofit
                    .create(RetrofitNetworkService.class)
                    .getHrmSubWiseAtdDetail(InstituteID, MediumID, ShiftID, ClassID, SectionID, SubjectID, DepertmentID, date)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());

            finalDisposer.add( observable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableObserver<String>() {

                        @Override
                        public void onNext(String response) {
                            prepareAlbums(response);
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    }));
        } else {
            Toast.makeText(TakeAttendanceDetails.this,"Please check your internet connection!!!",Toast.LENGTH_LONG).show();
        }
    }
}
