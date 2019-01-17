package onair.onems.fees_report;

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

import java.util.Objects;

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

public class FeeCollectionReportList extends CommonToolbarParentActivity {

    private int  ReportType;
    private Disposable finalDisposer;

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
        final View childActivityLayout = Objects.requireNonNull(inflater).inflate(R.layout.fee_collection_report_list, null);
        LinearLayout parentActivityLayout = findViewById(R.id.contentMain);
        parentActivityLayout.addView(childActivityLayout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        Intent intent = getIntent();
        ReportType = intent.getIntExtra("Report Type", 0);

        if (StaticHelperClass.isNetworkAvailable(this)) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(getString(R.string.baseUrl))
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();

            switch (ReportType) {
                case 1:
                    dialog.show();
                    Observable<String> collectionObservable = retrofit
                            .create(RetrofitNetworkService.class)
                            .GetAccFeesCollectionReport(intent.getStringExtra("InstituteID")
                                    ,intent.getStringExtra("BranchID"), intent.getStringExtra("MediumID")
                                    ,intent.getStringExtra("ClassID"), intent.getStringExtra("DepartmentID")
                                    ,intent.getStringExtra("SectionID"), intent.getStringExtra("ShiftID")
                                    ,"0","0", intent.getStringExtra("MonthID")
                                    , intent.getStringExtra("StatusID"), intent.getStringExtra("SessionID"))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .unsubscribeOn(Schedulers.io());

                    collectionObservable.subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .unsubscribeOn(Schedulers.io())
                            .subscribe(new Observer<String>() {
                                @Override
                                public void onSubscribe(Disposable d) {
                                    finalDisposer = d;
                                }

                                @Override
                                public void onNext(String response) {
                                    dialog.dismiss();
                                    if (response!= null) {
                                        if (!response.equals("")&&!response.equals("[]")) {
                                            parseJsonData(response);
                                        }
                                    }
                                }

                                @Override
                                public void onError(Throwable e) {
                                    dialog.dismiss();
                                }

                                @Override
                                public void onComplete() {
                                }
                            });
                    Objects.requireNonNull(getSupportActionBar()).setTitle("Fee Collection Report");
                    break;
                case 2:
                    dialog.show();
                    Observable<String> summaryObservable = retrofit
                            .create(RetrofitNetworkService.class)
                            .GetAccFeesCollectionReportTopSheet(intent.getStringExtra("InstituteID")
                                    ,intent.getStringExtra("BranchID"), intent.getStringExtra("MediumID")
                                    ,intent.getStringExtra("ClassID"), intent.getStringExtra("DepartmentID")
                                    ,intent.getStringExtra("SectionID"), intent.getStringExtra("ShiftID")
                                    ,"0","0", intent.getStringExtra("MonthID")
                                    , intent.getStringExtra("StatusID"), intent.getStringExtra("SessionID"))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .unsubscribeOn(Schedulers.io());

                    summaryObservable.subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .unsubscribeOn(Schedulers.io())
                            .subscribe(new Observer<String>() {
                                @Override
                                public void onSubscribe(Disposable d) {
                                    finalDisposer = d;
                                }

                                @Override
                                public void onNext(String response) {
                                    dialog.dismiss();
                                    if (response!= null) {
                                        if (!response.equals("")&&!response.equals("[]")) {
                                            parseJsonData(response);
                                        }
                                    }
                                }

                                @Override
                                public void onError(Throwable e) {
                                    dialog.dismiss();
                                }

                                @Override
                                public void onComplete() {
                                }
                            });
                    Objects.requireNonNull(getSupportActionBar()).setTitle("Fee Summary Report");
                    break;
            }
        } else {
            Toast.makeText(this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    void parseJsonData(String jsonString) {
        RecyclerView recyclerView = findViewById(R.id.recycler);
        FeeCollectionReportListAdapter mAdapter = new FeeCollectionReportListAdapter(this, jsonString, ReportType);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
    }
}
