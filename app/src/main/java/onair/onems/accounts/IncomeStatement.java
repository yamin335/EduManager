package onair.onems.accounts;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import onair.onems.R;
import onair.onems.Services.RetrofitNetworkService;
import onair.onems.Services.StaticHelperClass;
import onair.onems.mainactivities.SideNavigationMenuParentActivity;
import onair.onems.mainactivities.TeacherMainScreen;
import onair.onems.models.BranchModel;
import onair.onems.models.YearModel;
import onair.onems.routine.RoutineMainScreen;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class IncomeStatement extends SideNavigationMenuParentActivity {

    private Spinner spinnerBranch, spinnerYear;
    private ArrayList<BranchModel> allBranchArrayList;
    private ArrayList<YearModel> allYearArrayList;
    private String[] tempBranchArray = {"Select Branch"};
    private String[] tempYearArray = {"Select Year"};
    private BranchModel selectedBranch;
    private YearModel selectedYear;
    private TextView fromDateView, toDateView;
    private String fromDate = "", toDate = "";
    private RecyclerView recyclerView;
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

        activityName = RoutineMainScreen.class.getName();

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View childActivityLayout = Objects.requireNonNull(inflater).inflate(R.layout.acc_income_statemnt, null);
        LinearLayout parentActivityLayout = (LinearLayout) findViewById(R.id.contentMain);
        parentActivityLayout.addView(childActivityLayout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        spinnerBranch = findViewById(R.id.spinnerBranch);
        spinnerYear = findViewById(R.id.spinnerYear);
        ImageView fromDateIcon = findViewById(R.id.dateIcon1);
        ImageView toDateIcon = findViewById(R.id.dateIcon2);
        fromDateView = findViewById(R.id.comDate1);
        toDateView = findViewById(R.id.comDate2);
        recyclerView = findViewById(R.id.recycler);

        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
        fromDate = df.format(date);
        toDate = fromDate;

        fromDateView.setText(fromDate);
        toDateView.setText(toDate);

        fromDateIcon.setOnClickListener(v ->
            dateChooser(1)
        );

        toDateIcon.setOnClickListener(v ->
            dateChooser(2)
        );

        selectedBranch = new BranchModel();
        selectedYear = new YearModel();

        ArrayAdapter<String> branch_spinner_adapter = new ArrayAdapter<>(this, R.layout.spinner_item, tempBranchArray);
        branch_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBranch.setAdapter(branch_spinner_adapter);

        ArrayAdapter<String> year_spinner_adapter = new ArrayAdapter<>(this, R.layout.spinner_item, tempYearArray);
        year_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBranch.setAdapter(year_spinner_adapter);

        spinnerBranch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != 0) {
                    try {
                        selectedBranch = allBranchArrayList.get(position-1);
                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(IncomeStatement.this,"No shift found !!!",Toast.LENGTH_LONG).show();
                    }
                } else {
                    selectedBranch = new BranchModel();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != 0) {
                    try {
                        selectedYear = allYearArrayList.get(position-1);
                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(IncomeStatement.this,"No year found !!!",Toast.LENGTH_LONG).show();
                    }
                } else {
                    selectedYear = new YearModel();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        BranchDataGetRequest();
        YearDataGetRequest();

        Button show = findViewById(R.id.button3);
        show.setOnClickListener(view-> {
             if (fromDate.equals("")) {
                 Toast.makeText(IncomeStatement.this,"Please check your internet connection and select again!!! ",
                         Toast.LENGTH_LONG).show();
             } else if (toDate.equals("")) {
                 Toast.makeText(IncomeStatement.this,"Please check your internet connection and select again!!! ",
                         Toast.LENGTH_LONG).show();
             } else {
                 IncomeStatementDataGetRequest();
             }
        });
    }

    private void BranchDataGetRequest() {
        if (StaticHelperClass.isNetworkAvailable(this)) {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(getString(R.string.baseUrl))
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();

            Observable<String> observable = retrofit
                    .create(RetrofitNetworkService.class)
                    .getBranchByInsID(InstituteID)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io());

            finalDisposer.add( observable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io())
                    .subscribeWith(new DisposableObserver<String>() {

                        @Override
                        public void onNext(String BranchDataReturnValue) {
                            parseBranchData(BranchDataReturnValue);
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    }));

        } else {
            Toast.makeText(IncomeStatement.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    void parseBranchData(String jsonString) {
        ArrayList<String> branchArrayList = new ArrayList<>();
        try {
            allBranchArrayList = new ArrayList<>();
            JSONArray branchJsonArray = new JSONArray(jsonString);
            branchArrayList.add("Select Branch");
            for(int i = 0; i < branchJsonArray.length(); ++i) {
                JSONObject branchJsonObject = branchJsonArray.getJSONObject(i);
                BranchModel branchModel = new BranchModel(branchJsonObject.getString("BrunchID"), branchJsonObject.getString("BrunchNo")
                        , branchJsonObject.getString("BrunchName"), branchJsonObject.getString("ParentID"), branchJsonObject.getString("InstituteID")
                        , branchJsonObject.getString("ParentBranch"), branchJsonObject.getString("Institute"));
                allBranchArrayList.add(branchModel);
                branchArrayList.add(branchModel.getBrunchName());
            }
        } catch (JSONException e) {
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
        }
        try {
            String[] strings = new String[branchArrayList.size()];
            strings = branchArrayList.toArray(strings);
            ArrayAdapter<String> branch_spinner_adapter = new ArrayAdapter<>(this,R.layout.spinner_item, strings);
            branch_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerBranch.setAdapter(branch_spinner_adapter);
        } catch (IndexOutOfBoundsException e) {
            Toast.makeText(this,"No branch found !!!",Toast.LENGTH_LONG).show();
        }
    }

    private void YearDataGetRequest() {
        if (StaticHelperClass.isNetworkAvailable(this)) {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(getString(R.string.baseUrl))
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();

            Observable<String> observable = retrofit
                    .create(RetrofitNetworkService.class)
                    .getCmnYear()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io());

            finalDisposer.add( observable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io())
                    .subscribeWith(new DisposableObserver<String>() {

                        @Override
                        public void onNext(String YearDataReturnValue) {
                            parseYearData(YearDataReturnValue);
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    }));

        } else {
            Toast.makeText(IncomeStatement.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    void parseYearData(String jsonString) {
        ArrayList<String> yearArrayList = new ArrayList<>();
        try {
            allYearArrayList = new ArrayList<>();
            JSONArray yearJsonArray = new JSONArray(jsonString);
            yearArrayList.add("Select Year");
            for(int i = 0; i < yearJsonArray.length(); ++i) {
                JSONObject yearJsonObject = yearJsonArray.getJSONObject(i);
                YearModel yearModel = new YearModel(yearJsonObject.getString("YearID"), yearJsonObject.getString("YearName"));
                allYearArrayList.add(yearModel);
                yearArrayList.add(yearModel.getYearName());
            }
        } catch (JSONException e) {
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
        }
        try {
            String[] strings = new String[yearArrayList.size()];
            strings = yearArrayList.toArray(strings);
            ArrayAdapter<String> year_spinner_adapter = new ArrayAdapter<>(this,R.layout.spinner_item, strings);
            year_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerYear.setAdapter(year_spinner_adapter);
        } catch (IndexOutOfBoundsException e) {
            Toast.makeText(this,"No year found !!!",Toast.LENGTH_LONG).show();
        }
    }

    private void dateChooser(final int dateType) {
        // calender class's instance and get current date , month and year from calender
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH); // current month
        int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
        // date picker dialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(IncomeStatement.this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    if (dateType == 1) {
                        fromDate = (monthOfYear + 1) + "-" + dayOfMonth + "-" + year;
                        fromDateView.setText(fromDate);
                    } else if (dateType == 2) {
                        toDate = (monthOfYear + 1) + "-" + dayOfMonth + "-" + year;
                        toDateView.setText(toDate);
                    }

                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void IncomeStatementDataGetRequest() {
        if (StaticHelperClass.isNetworkAvailable(this)) {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(getString(R.string.baseUrl))
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();

            Observable<String> incomeStatementObservable = retrofit
                    .create(RetrofitNetworkService.class)
                    .getIncomeStatementReport(fromDate, toDate, InstituteID, selectedBranch.getBrunchID(), selectedYear.getYearID())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io());

            finalDisposer.add( incomeStatementObservable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io())
                    .subscribeWith(new DisposableObserver<String>() {

                        @Override
                        public void onNext(String incomeStatementReturnValue) {
                            parseIncomeStatement(incomeStatementReturnValue);
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    }));

        } else {
            Toast.makeText(IncomeStatement.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void parseIncomeStatement(String incomeStatement) {
        IncomeStatementAdapter mAdapter = new IncomeStatementAdapter(this, incomeStatement);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(UserTypeID == 1) {
            Intent mainIntent = new Intent(IncomeStatement.this, TeacherMainScreen.class);
            startActivity(mainIntent);
            finish();
        } else if(UserTypeID == 2) {
            Intent mainIntent = new Intent(IncomeStatement.this, TeacherMainScreen.class);
            startActivity(mainIntent);
            finish();
        } else if(UserTypeID == 4) {
            Intent mainIntent = new Intent(IncomeStatement.this, TeacherMainScreen.class);
            startActivity(mainIntent);
            finish();
        }
    }
}
