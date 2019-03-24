package onair.onems.routine;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import onair.onems.R;
import onair.onems.Services.RetrofitNetworkService;
import onair.onems.Services.StaticHelperClass;
import onair.onems.mainactivities.CommonProgressDialog;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Monday extends Fragment {
    private long UserTypeID;
    private RoutineAdapter mAdapter;
    private RecyclerView recyclerView;
    private Disposable finalDisposer;

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (finalDisposer != null && !finalDisposer.isDisposed())
            finalDisposer.dispose();
    }

    public Monday() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.routine_day_pager_item, container, false);
        recyclerView = rootView.findViewById(R.id.routinePeriods);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        long instituteID = prefs.getLong("InstituteID", 0);
        long shiftID = prefs.getLong("ShiftID", 0);
        long mediumID = prefs.getLong("MediumID", 0);
        long classID = prefs.getLong("ClassID", 0);
        long sectionID = prefs.getLong("SectionID", 0);
        UserTypeID = prefs.getInt("UserTypeID",0);
        long departmentID;
        if(UserTypeID == 3) {
            departmentID = prefs.getLong("SDepartmentID",0);
        } else {
            departmentID = prefs.getLong("DepartmentID",0);
        }

        if(UserTypeID==1||UserTypeID==2||UserTypeID==4) {
            Bundle bundle = getArguments();
            String routineData = "";
            if(bundle != null) {
                routineData = bundle.getString("mondayJsonArray");
            }
            mAdapter = new RoutineAdapter(getActivity(), routineData, UserTypeID);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(mAdapter);
        } else if(UserTypeID==3||UserTypeID==5) {
            if(UserTypeID == 3) {
                mondayRoutineDataGetRequest(Long.toString(shiftID), Long.toString(mediumID), Long.toString(classID), Long.toString(sectionID), Long.toString(departmentID), Long.toString(3), Long.toString(instituteID));
            } else if(UserTypeID == 5) {
                try {
                    JSONObject selectedStudent = new JSONObject(Objects.requireNonNull(getActivity()).getSharedPreferences("CURRENT_STUDENT", Context.MODE_PRIVATE)
                            .getString("guardianSelectedStudent", "{}"));
                    mondayRoutineDataGetRequest(selectedStudent.getString("ShiftID"), selectedStudent.getString("MediumID")
                            ,selectedStudent.getString("ClassID"),selectedStudent.getString("SectionID")
                            ,selectedStudent.getString("DepartmentID"),"3", Long.toString(instituteID));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        return  rootView;
    }

    void mondayRoutineDataGetRequest(String ShiftID, String MediumID, String ClassID, String SectionID,
                                     String DepartmentID, String DayID, String InstituteID) {
        if(StaticHelperClass.isNetworkAvailable(Objects.requireNonNull(getActivity()))) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(getString(R.string.baseUrl))
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();

            Observable<String> observable = retrofit
                    .create(RetrofitNetworkService.class)
                    .spGetDashClassRoutine(ShiftID, MediumID, ClassID, SectionID, DepartmentID, DayID, InstituteID)
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
                            mAdapter = new RoutineAdapter(getActivity(), response, UserTypeID);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                            recyclerView.setLayoutManager(mLayoutManager);
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            recyclerView.setAdapter(mAdapter);
                        }

                        @Override
                        public void onComplete() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(getActivity() ,"Error getting routine !!!",Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            Toast.makeText(getActivity(),"Please check your internet connection !!!",Toast.LENGTH_LONG).show();
        }
    }
}
