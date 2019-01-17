package onair.onems.result;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import onair.onems.mainactivities.CommonProgressDialog;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

//implements View.OnTouchListener,

public class SelfResult extends Fragment implements
        ResultExamAdapter.ExamAdapterListener, SubjectWiseResultAdapter.SubjectWiseResultsAdapterListener{
    public int UserTypeID;
    public long InstituteID, UserShiftID, UserMediumID, UserClassID,
            UserDepartmentID, UserSectionID, UserSessionID;
    public String UserID;
//    private float dX;
//    private float dY;
//    private int lastAction;
    private List<JSONObject> subjectWiseResultList;
    private SubjectWiseResultAdapter mAdapter;
    private boolean isFail = false;
    private JSONArray resultGradingSystem;
    private String ExamID = "";
    private CompositeDisposable finalDisposer = new CompositeDisposable();
    public CommonProgressDialog dialog;

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!finalDisposer.isDisposed())
            finalDisposer.dispose();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        UserTypeID = prefs.getInt("UserTypeID",0);
        InstituteID = prefs.getLong("InstituteID",0);
        UserID = prefs.getString("UserID", "0");
        UserShiftID = prefs.getLong("ShiftID",0);
        UserMediumID = prefs.getLong("MediumID",0);
        UserClassID = prefs.getLong("ClassID",0);
        UserDepartmentID = prefs.getLong("SDepartmentID",0);
        UserSectionID = prefs.getLong("SectionID",0);
        UserSessionID = prefs.getLong("SessionID",0);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.result_subject_wise_self, container, false);
        dialog = new CommonProgressDialog(getActivity());
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        FloatingActionButton floatingActionButton = rootView.findViewById(R.id.selectExam);
        floatingActionButton.setOnClickListener(view -> {
            if(UserTypeID == 3){
                examDataGetRequest(rootView.getContext(), InstituteID, UserMediumID, UserClassID);
            } else if(UserTypeID == 5) {
                try {
                    JSONObject selectedStudent = new JSONObject(rootView.getContext().getSharedPreferences("CURRENT_STUDENT", Context.MODE_PRIVATE)
                            .getString("guardianSelectedStudent", "{}"));
                    examDataGetRequest(rootView.getContext(), InstituteID, Long.parseLong(selectedStudent.getString("MediumID")),
                            Long.parseLong(selectedStudent.getString("ClassID")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

//        floatingActionButton.setOnTouchListener(this);

        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);
        subjectWiseResultList = new ArrayList<>();
        mAdapter = new SubjectWiseResultAdapter(getActivity(), subjectWiseResultList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(rootView.getContext(), DividerItemDecoration.VERTICAL, 10));
        recyclerView.setAdapter(mAdapter);

        if(UserTypeID == 3){
            examDataGetRequest(rootView.getContext(), InstituteID, UserMediumID, UserClassID);
        } else if(UserTypeID == 5) {
            try {
                JSONObject selectedStudent = new JSONObject(rootView.getContext().getSharedPreferences("CURRENT_STUDENT", Context.MODE_PRIVATE)
                        .getString("guardianSelectedStudent", "{}"));
                examDataGetRequest(rootView.getContext(), InstituteID, Long.parseLong(selectedStudent.getString("MediumID")),
                        Long.parseLong(selectedStudent.getString("ClassID")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onExamSelected(JSONObject exam) {
        try {
            ExamID = exam.getString("ExamID");
            if(UserTypeID == 3){
                ResultGradeDataGetRequest(InstituteID, Long.toString(UserMediumID), Long.toString(UserClassID));
            } else if(UserTypeID == 5) {
                try {
                    JSONObject selectedStudent = new JSONObject(Objects.requireNonNull(getActivity()).getSharedPreferences("CURRENT_STUDENT", Context.MODE_PRIVATE)
                            .getString("guardianSelectedStudent", "{}"));
                    ResultGradeDataGetRequest(InstituteID, selectedStudent.getString("MediumID"), selectedStudent.getString("ClassID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

//    @Override
//    public boolean onTouch(View view, MotionEvent event) {
//        switch (event.getActionMasked()) {
//            case MotionEvent.ACTION_DOWN:
//                dX = view.getX() - event.getRawX();
//                dY = view.getY() - event.getRawY();
//                lastAction = MotionEvent.ACTION_DOWN;
//                break;
//
//            case MotionEvent.ACTION_MOVE:
//                view.setY(event.getRawY() + dY);
//                view.setX(event.getRawX() + dX);
//                lastAction = MotionEvent.ACTION_MOVE;
//                break;
//
//            case MotionEvent.ACTION_UP:
//                if (lastAction == MotionEvent.ACTION_DOWN){
//
//                }
//                break;
//
//            default:
//                return false;
//        }
//        return true;
//    }

    @Override
    public void onSubjectWiseResultSelected(JSONObject subjectWiseResult) {
        try {
            if(!subjectWiseResult.getString("SubjectName").equalsIgnoreCase("Total")) {
                ResultDetailsDialog customDialog = new ResultDetailsDialog(getActivity(), subjectWiseResult);
                Objects.requireNonNull(customDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                customDialog.setCancelable(false);
                customDialog.show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void examDataGetRequest(Context context, long InstituteID, long MediumID, long ClassID) {
        if (StaticHelperClass.isNetworkAvailable(context)) {
            dialog.show();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(getString(R.string.baseUrl))
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();

            Observable<String> observable = retrofit
                    .create(RetrofitNetworkService.class)
                    .getClassWiseInsExame(InstituteID, MediumID, ClassID)
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
                            parseExamData(response);
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

    private void parseExamData(String examData) {
        if(!examData.equalsIgnoreCase("[]")) {
            ResultExamSelectionDialog resultExamSelectionDialog = new ResultExamSelectionDialog(getActivity(), this, examData, getActivity());
            Objects.requireNonNull(resultExamSelectionDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            resultExamSelectionDialog.setCancelable(false);
            resultExamSelectionDialog.show();
        } else {
            Toast.makeText(getActivity(),"Exam data not found!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void ResultDataGetRequest(String ExamID, String UserID, String InstituteID,String ClassID,
                                      String SectionID, String DepartmentID, String MediumID,
                                      String ShiftID, String SessionID) {
        if (StaticHelperClass.isNetworkAvailable(Objects.requireNonNull(getActivity()))) {
            dialog.show();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(getString(R.string.baseUrl))
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();

            Observable<String> observable = retrofit
                    .create(RetrofitNetworkService.class)
                    .SubjectWiseMarksByStudent(UserID, InstituteID, ClassID, SectionID, DepartmentID,
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
                            Toast.makeText(getActivity(),"Result not found!!! ",
                                    Toast.LENGTH_LONG).show();
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

    private void prepareResult(String result) {
        try {
            subjectWiseResultList.clear();
            JSONArray resultJsonArray = new JSONArray(result);
            double totalMarks = 0.0;
            double totalGradePoint = 0.0;
            String totalGrade = "";
            int totalSubject = 0;

            for(int i = 0; i<resultJsonArray.length(); i++) {
                if(resultJsonArray.getJSONObject(i).getString("IsOptional").equalsIgnoreCase("1")
                        && (resultJsonArray.getJSONObject(i).getDouble("GradePoint")>2.0)) {

                    subjectWiseResultList.add(resultJsonArray.getJSONObject(i));
                    totalGradePoint += resultJsonArray.getJSONObject(i).getDouble("GradePoint")-2.0;
                    totalMarks+= resultJsonArray.getJSONObject(i).getDouble("Total");
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

    private void ResultGradeDataGetRequest(long InstituteID, String MediumID, String ClassID){
        if (StaticHelperClass.isNetworkAvailable(Objects.requireNonNull(getActivity()))) {
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
                            Toast.makeText(getActivity(),"Grade sheet not found!!! ",
                                    Toast.LENGTH_LONG).show();
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

    private void prepareResultGradeSheet(String result) {
        try {
            resultGradingSystem = new JSONArray(result);
            if(UserTypeID == 3){
                ResultDataGetRequest(ExamID, UserID, Long.toString(InstituteID), Long.toString(UserClassID),
                        Long.toString(UserSectionID), Long.toString(UserDepartmentID), Long.toString(UserMediumID),
                        Long.toString(UserShiftID), Long.toString(UserSessionID));
            } else if(UserTypeID == 5) {
                try {
                    JSONObject selectedStudent = new JSONObject(Objects.requireNonNull(getActivity()).getSharedPreferences("CURRENT_STUDENT", Context.MODE_PRIVATE)
                            .getString("guardianSelectedStudent", "{}"));
                    ResultDataGetRequest(ExamID, selectedStudent.getString("UserID"), Long.toString(InstituteID),
                            selectedStudent.getString("ClassID"), selectedStudent.getString("SectionID"),
                            selectedStudent.getString("DepartmentID"), selectedStudent.getString("MediumID"),
                            selectedStudent.getString("ShiftID"), selectedStudent.getString("SessionID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
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
