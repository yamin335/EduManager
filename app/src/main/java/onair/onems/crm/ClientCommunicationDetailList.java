package onair.onems.crm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import onair.onems.customised.MyDividerItemDecoration;
import onair.onems.mainactivities.SideNavigationMenuParentActivity;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ClientCommunicationDetailList extends SideNavigationMenuParentActivity implements DetailListAdapter.DetailListAdapterListener {

    private FloatingActionButton addDetails;
    private String clientData = "";
    private RecyclerView recyclerView;
    private TextView empty;
    private DetailListAdapter mAdapter;
    private ArrayList<JSONObject> detailList;
    private int NewClientID = 0;
    private ProgressBar progressBar;
    private View whiteBackground;
    private CompositeDisposable finalDisposer = new CompositeDisposable();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!finalDisposer.isDisposed())
            finalDisposer.dispose();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDetailListData(NewClientID);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        activityName = ClientCommunicationDetailList.class.getName();

        super.onCreate(savedInstanceState);

        Intent extraIntent = getIntent();
        if (extraIntent.hasExtra("clientData")) {
            clientData = extraIntent.getStringExtra("clientData");
        }

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View childActivityLayout = inflater.inflate(R.layout.client_communication_detail_list, null);
        LinearLayout parentActivityLayout = (LinearLayout) findViewById(R.id.contentMain);
        parentActivityLayout.addView(childActivityLayout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        detailList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        mAdapter = new DetailListAdapter(this, detailList, this);
        empty = findViewById(R.id.empty);

        whiteBackground = findViewById(R.id.whiteBackground);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        whiteBackground.setVisibility(View.INVISIBLE);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 8));
        recyclerView.setAdapter(mAdapter);

        addDetails = findViewById(R.id.addDetails);
        addDetails.setOnClickListener(view->{
            Intent intent = new Intent(ClientCommunicationDetailList.this, ClientCommunicationDetail.class);
            intent.putExtra("clientData", clientData);
            startActivity(intent);
        });

        try {
            NewClientID = new JSONObject(clientData).getInt("NewClientID");
            getDetailListData(NewClientID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Button button = findViewById(R.id.button);
        button.setOnClickListener( view -> {
            WorkOrderGetRequest();
        });

    }

    private void getDetailListData(int NewClientID) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.baseUrl))
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        Observable<String> detailListObservable = retrofit
                .create(RetrofitNetworkService.class)
                .getDetailList(NewClientID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());

        finalDisposer.add( detailListObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<String>() {

                    @Override
                    public void onNext(String detailListData) {
                        if (detailListData!= null) {
                            if (!detailListData.equals("")&&!detailListData.equals("[]")) {
                                empty.setVisibility(View.GONE);
                                detailList.clear();
                                try {
                                    JSONArray jsonArray = new JSONArray(detailListData);
                                    for (int i = 0; i<jsonArray.length(); i++) {
                                        detailList.add(i, jsonArray.getJSONObject(i));
                                    }
                                    mAdapter.notifyDataSetChanged();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                    }
                }));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(UserTypeID == 1) {
            Intent intent = new Intent(ClientCommunicationDetailList.this, NewClientEntry.class);
            intent.putExtra("clientData", clientData);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onDetailSelected(JSONObject detail) {
        Intent mainIntent = new Intent(ClientCommunicationDetailList.this, ClientCommunicationDetail.class);
        mainIntent.putExtra("forUpdate", detail.toString());
        startActivity(mainIntent);
    }

    private void WorkOrderGetRequest() {
        if(StaticHelperClass.isNetworkAvailable(this)) {

            progressBar.setVisibility(View.VISIBLE);
            whiteBackground.setVisibility(View.VISIBLE);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(getString(R.string.baseUrl))
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();

            Observable<String> paymentTypeObservable = retrofit
                    .create(RetrofitNetworkService.class)
                    .getWorkOrder(NewClientID)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io());

            finalDisposer.add(paymentTypeObservable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io())
                    .subscribeWith(new DisposableObserver<String>() {

                        @Override
                        public void onNext(String workOrderReturnValue) {
                            progressBar.setVisibility(View.INVISIBLE);
                            whiteBackground.setVisibility(View.INVISIBLE);
                            Intent workOrderIntent = new Intent(ClientCommunicationDetailList.this, WorkOrder.class);
                            if (!workOrderReturnValue.equalsIgnoreCase("") && !workOrderReturnValue.equalsIgnoreCase("null")
                                    && !workOrderReturnValue.equalsIgnoreCase("[]")) {

                                workOrderIntent.putExtra("clientData", clientData);
                                workOrderIntent.putExtra("forUpdate", workOrderReturnValue);
                                startActivity(workOrderIntent);

                            } else {
                                workOrderIntent.putExtra("clientData", clientData);
                                startActivity(workOrderIntent);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            progressBar.setVisibility(View.INVISIBLE);
                            whiteBackground.setVisibility(View.INVISIBLE);
                            Intent workOrderIntent = new Intent(ClientCommunicationDetailList.this, WorkOrder.class);
                            workOrderIntent.putExtra("clientData", clientData);
                            startActivity(workOrderIntent);
                        }

                        @Override
                        public void onComplete() {

                        }
                    }));
        } else {
            Toast.makeText(ClientCommunicationDetailList.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }
}
