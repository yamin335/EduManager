package onair.onems.exam;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

public class SubjectWiseMarksEntryStudentList extends CommonToolbarParentActivity implements StudentListAdapter.StudentListAdapterListener {

    private long SubjectID;
    private Disposable finalDisposer;
    private Intent intent;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!finalDisposer.isDisposed())
            finalDisposer.dispose();
    }

    @Override
    protected void onRestart() {
        StudentDataGetRequest();
        super.onRestart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View childActivityLayout = inflater.inflate(R.layout.exam_subject_wise_marks_entry_student_list, null);
        LinearLayout parentActivityLayout = findViewById(R.id.contentMain);
        parentActivityLayout.addView(childActivityLayout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        intent = getIntent();
        SubjectID = intent.getLongExtra("SubjectID", 0);
        StudentDataGetRequest();
    }

    private void StudentDataGetRequest() {
        if (StaticHelperClass.isNetworkAvailable(this)) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(getString(R.string.baseUrl))
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();

            Observable<String> observable = retrofit
                    .create(RetrofitNetworkService.class)
                    .getSubjectWiseMarks(intent.getLongExtra("InstituteID", 0), intent.getLongExtra("ClassID", 0),
                            intent.getLongExtra("SectionID", 0), intent.getLongExtra("DepartmentID", 0),
                            intent.getLongExtra("MediumID", 0), intent.getLongExtra("ShiftID", 0),
                            intent.getLongExtra("SubjectID", 0), intent.getLongExtra("ExamID", 0))
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
                            finalDisposer = d;
                        }

                        @Override
                        public void onNext(String response) {
                            parseStudentData(response);
                        }

                        @Override
                        public void onComplete() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(getApplicationContext(),"ERROR posting token",Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            Toast.makeText(SubjectWiseMarksEntryStudentList.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    void parseStudentData(String jsonString) {
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            StudentListAdapter mAdapter = new  StudentListAdapter(this, jsonArray, this);
            RecyclerView recyclerView = findViewById(R.id.recycler);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(mAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onStudentSelected(JSONObject result) {
        Intent intent = new Intent(SubjectWiseMarksEntryStudentList.this, SubjectWiseMarksEntryInputs.class);
        intent.putExtra("student", result.toString());
        intent.putExtra("SubjectID", SubjectID);
        startActivity(intent);
    }
}
