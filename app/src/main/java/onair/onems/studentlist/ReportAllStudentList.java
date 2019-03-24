package onair.onems.studentlist;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import onair.onems.R;
import onair.onems.Services.RetrofitNetworkService;
import onair.onems.Services.StaticHelperClass;
import onair.onems.customised.MyDividerItemDecoration;
import onair.onems.mainactivities.CommonToolbarParentActivity;
import onair.onems.models.ReportAllStudentRowModel;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ReportAllStudentList extends CommonToolbarParentActivity implements ReportAllStudentShowListAdapter.ReportAllStudentShowListAdapterListener{

    private RecyclerView recyclerView;
    private ArrayList<ReportAllStudentRowModel> studentList;
    private ReportAllStudentShowListAdapter mAdapter;
    private SearchView searchView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private long InstituteID, MediumID, ShiftID, ClassID, SectionID, DepertmentID, SessionID;
    private Disposable finalDisposer;

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!finalDisposer.isDisposed())
            finalDisposer.dispose();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View childActivityLayout = Objects.requireNonNull(inflater).inflate(R.layout.report_all_student_list_activity_main, null);
        LinearLayout parentActivityLayout = findViewById(R.id.contentMain);
        parentActivityLayout.addView(childActivityLayout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        mSwipeRefreshLayout = findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            ClearStudentList();
            StudentDataGetRequest();
        });

        recyclerView = findViewById(R.id.recycler_view);
        studentList = new ArrayList<>();
        mAdapter = new ReportAllStudentShowListAdapter(this, studentList, this);

        Bundle StudentSelection = getIntent().getExtras();
        InstituteID = Objects.requireNonNull(StudentSelection).getLong("InstituteID",0);
        MediumID = StudentSelection.getLong("MediumID",0);
        ShiftID = StudentSelection.getLong("ShiftID",0);
        ClassID = StudentSelection.getLong("ClassID",0);
        SectionID = StudentSelection.getLong("SectionID",0);
        DepertmentID = StudentSelection.getLong("DepertmentID",0);
        SessionID = StudentSelection.getLong("SessionID",0);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 0));
        recyclerView.setAdapter(mAdapter);

        StudentDataGetRequest();
    }

    void parseStudentJsonData(String jsonString) {
        try {
            JSONArray studentJsonArray = new JSONArray(jsonString);
            for(int i = 0; i < studentJsonArray.length(); ++i) {
                JSONObject studentJsonObject = studentJsonArray.getJSONObject(i);
                ReportAllStudentRowModel reportAllStudentRowModel = new ReportAllStudentRowModel();
                reportAllStudentRowModel.setUserID(studentJsonObject.getString("UserID"));
                reportAllStudentRowModel.setRollNo(studentJsonObject.getString("RollNo"));
                reportAllStudentRowModel.setUserName(studentJsonObject.getString("UserName"));
                reportAllStudentRowModel.setImageUrl(studentJsonObject.getString("ImageUrl"));
                reportAllStudentRowModel.setClassName(studentJsonObject.getString("ClassName"));
                reportAllStudentRowModel.setSectionName(studentJsonObject.getString("SectionName"));
                reportAllStudentRowModel.setDepartmentName(studentJsonObject.getString("DepartmentName"));
                reportAllStudentRowModel.setRFID(studentJsonObject.getString("RFID"));
                studentList.add(reportAllStudentRowModel);
            }
            // refreshing recycler view
            mAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(Objects.requireNonNull(searchManager)
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
//        NavUtils.navigateUpFromSameTask(this);
        super.onBackPressed();
    }

    @Override
    public void onStudentSelected(ReportAllStudentRowModel reportAllStudentRowModel) {
        Intent intent = new Intent(ReportAllStudentList.this, ReportAllStudentListDetails.class);
        intent.putExtra("UserID", reportAllStudentRowModel.getUserID());
        intent.putExtra("RFID", reportAllStudentRowModel.getRFID());
        intent.putExtra("ImageUrl", reportAllStudentRowModel.getImageUrl());
        intent.putExtra("UserName", reportAllStudentRowModel.getUserName());
        startActivity(intent);
    }

    public void StudentDataGetRequest(){
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
                    .getStudent(InstituteID,Long.toString(ClassID), Long.toString(SectionID), Long.toString(DepertmentID),
                            Long.toString(MediumID), Long.toString(ShiftID),"0", Long.toString(SessionID))
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
                            dialog.dismiss();
                            mSwipeRefreshLayout.setRefreshing(false);
                            parseStudentJsonData(response);
                        }

                        @Override
                        public void onComplete() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                            mSwipeRefreshLayout.setRefreshing(false);
                            Toast.makeText(ReportAllStudentList.this,"Error getting student list !!!",Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            Toast.makeText(ReportAllStudentList.this,"Please check your internet connection!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void ClearStudentList(){
        int size = studentList.size();
        studentList.clear();
        mAdapter.notifyItemRangeRemoved(0, size);
    }
    //testing
}
