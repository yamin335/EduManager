package onair.onems.result;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
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
import onair.onems.mainactivities.CommonToolbarParentActivity;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class SubjectWiseResult extends CommonToolbarParentActivity implements SubjectWiseResultAdapter.SubjectWiseResultsAdapterListener{

    private List<JSONObject> subjectWiseResultList;
    private SubjectWiseResultAdapter mAdapter;
    private String UserID, ShiftID, MediumID, ClassID, DepartmentID, SectionID, SessionID, ExamID;
    private boolean isFail = false;
    private JSONArray resultGradingSystem;
    private CompositeDisposable finalDisposer = new CompositeDisposable();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!finalDisposer.isDisposed())
            finalDisposer.dispose();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View childActivityLayout = Objects.requireNonNull(inflater).inflate(R.layout.result_subject_wise, null);
        LinearLayout parentActivityLayout = findViewById(R.id.contentMain);
        parentActivityLayout.addView(childActivityLayout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        Intent intent = getIntent();
        UserID = intent.getStringExtra("UserID");
        ShiftID = intent.getStringExtra("ShiftID");
        MediumID = intent.getStringExtra("MediumID");
        ClassID = intent.getStringExtra("ClassID");
        DepartmentID = intent.getStringExtra("DepartmentID");
        SectionID = intent.getStringExtra("SectionID");
        SessionID = intent.getStringExtra("SessionID");
        ExamID = intent.getStringExtra("ExamID");

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        subjectWiseResultList = new ArrayList<>();
        mAdapter = new SubjectWiseResultAdapter(this, subjectWiseResultList, this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 10));
        recyclerView.setAdapter(mAdapter);

        ResultGradeDataGetRequest();
    }

    @Override
    public void onSubjectWiseResultSelected(JSONObject subjectWiseResul) {
        try {
            if(!subjectWiseResul.getString("SubjectName").equalsIgnoreCase("Total")) {
                ResultDetailsDialog customDialog = new ResultDetailsDialog(this, subjectWiseResul);
                Objects.requireNonNull(customDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                customDialog.setCancelable(false);
                customDialog.show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void ResultDataGetRequest() {
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
                    .SubjectWiseMarksByStudent(UserID, Long.toString(InstituteID), ClassID, SectionID, DepartmentID,
                            MediumID, ShiftID, SessionID, ExamID)
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
                            prepareResult(response);
                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                            Toast.makeText(SubjectWiseResult.this,"Result not found!!! ",
                                    Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    }));
        } else {
            Toast.makeText(SubjectWiseResult.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void prepareResult(String result) {
        try {
            subjectWiseResultList.clear();
            JSONArray resultJsonArray = new JSONArray(result);
            double totalMarks = 0;
            double totalGradePoint = 0.0;
            String totalGrade = "";
            int totalSubject = 0;

            for(int i = 0; i<resultJsonArray.length(); i++) {
                if(resultJsonArray.getJSONObject(i).getString("IsOptional").equalsIgnoreCase("1")
                        && (resultJsonArray.getJSONObject(i).getDouble("GradePoint")>2.0)) {

                    subjectWiseResultList.add(resultJsonArray.getJSONObject(i));
                    totalGradePoint += resultJsonArray.getJSONObject(i).getDouble("GradePoint")-2.0;
                    totalMarks+= resultJsonArray.getJSONObject(i).getInt("Total");
                    if(resultJsonArray.getJSONObject(i).getString("Grade").equalsIgnoreCase("F")) {
                        isFail = true;
                    }
                } else if(resultJsonArray.getJSONObject(i).getString("IsOptional").equalsIgnoreCase("0")){

                    totalSubject++;
                    subjectWiseResultList.add(resultJsonArray.getJSONObject(i));
                    if (!resultJsonArray.getJSONObject(i).getString("GradePoint").equalsIgnoreCase("")
                            && !resultJsonArray.getJSONObject(i).getString("GradePoint").equalsIgnoreCase("null")) {
                        totalGradePoint += resultJsonArray.getJSONObject(i).getDouble("GradePoint");
                    }

                    if (!resultJsonArray.getJSONObject(i).getString("Total").equalsIgnoreCase("")
                            && !resultJsonArray.getJSONObject(i).getString("Total").equalsIgnoreCase("null")) {
                        totalMarks+= resultJsonArray.getJSONObject(i).getDouble("Total");
                    }

                    if(resultJsonArray.getJSONObject(i).getString("Grade").equalsIgnoreCase("F")) {
                        isFail = true;
                    }
                }
            }

            if(totalSubject!=0) {
                if (isFail) {
                    totalGradePoint = 0;
                } else {
                    totalGradePoint/=totalSubject;
                    totalGradePoint = new BigDecimal(totalGradePoint).setScale(2, RoundingMode.HALF_UP).doubleValue();
                }
            }

            if(isFail) {
                totalGrade = "F";
            } else {
                totalGrade = getTotalGrade(totalGradePoint);
            }

            JSONObject totalResult = new JSONObject();
            if(resultJsonArray.length()>0) {
                totalResult.put("SubjectName", "Total");
                totalResult.put("Total", Double.toString(totalMarks));
                totalResult.put("Grade", totalGrade);
                totalResult.put("GradePoint", totalGradePoint);
                subjectWiseResultList.add(totalResult);
                mAdapter.notifyDataSetChanged();
            } else {
                totalResult.put("SubjectName", "");
                totalResult.put("Total", "");
                totalResult.put("Grade", "");
                totalResult.put("GradePoint", "");
                subjectWiseResultList.add(totalResult);
                mAdapter.notifyDataSetChanged();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void ResultGradeDataGetRequest(){
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
                    .getinsGradeForReport(InstituteID, MediumID, ClassID)
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
                            prepareResultGradeSheet(response);
                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                            Toast.makeText(SubjectWiseResult.this,"Grade sheet not found!!! ",
                                    Toast.LENGTH_LONG).show();
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

    private void prepareResultGradeSheet(String result) {
        try {
            resultGradingSystem = new JSONArray(result);
            ResultDataGetRequest();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getTotalGrade(double totalGradePoint){
        String totalGrade = "F";
        for (int i = 0; i<resultGradingSystem.length(); i++) {
            try {
                if(i==0 && totalGradePoint>=resultGradingSystem.getJSONObject(i).getDouble("GPA")) {
                    return resultGradingSystem.getJSONObject(i).getString("GradeName");
                } else if(i>0 && i<(resultGradingSystem.length()-1) &&
                        totalGradePoint>=resultGradingSystem.getJSONObject(i).getDouble("GPA") &&
                        totalGradePoint<resultGradingSystem.getJSONObject(i-1).getDouble("GPA")) {
                    return resultGradingSystem.getJSONObject(i).getString("GradeName");
                } else if(i==(resultGradingSystem.length()-1) &&
                        totalGradePoint<=resultGradingSystem.getJSONObject(i).getDouble("GPA") &&
                        totalGradePoint<resultGradingSystem.getJSONObject(i-1).getDouble("GPA")) {
                    return resultGradingSystem.getJSONObject(i).getString("GradeName");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return totalGrade;
    }
}
