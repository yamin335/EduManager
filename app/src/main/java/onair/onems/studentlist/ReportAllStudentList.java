package onair.onems.studentlist;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import onair.onems.R;
import onair.onems.Services.StaticHelperClass;
import onair.onems.models.ReportAllStudentRowModel;
import onair.onems.network.MySingleton;

/**
 * Created by User on 1/24/2018.
 */

public class ReportAllStudentList extends AppCompatActivity implements ReportAllStudentShowListAdapter.ReportAllStudentShowListAdapterListener{

    private ProgressDialog dialog;
    private RecyclerView recyclerView;
    private ArrayList<ReportAllStudentRowModel> studentList;
    private ReportAllStudentShowListAdapter mAdapter;
    private static String studentUrl = "";
    private SearchView searchView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_all_student_list_activity_main);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RefreshStudentList();
            }
        });

        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        studentList = new ArrayList<>();
        mAdapter = new ReportAllStudentShowListAdapter(this, studentList, this);

        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading...");
        dialog.setMessage("Please Wait...");
        dialog.setCancelable(false);
        dialog.setIcon(R.drawable.onair);
        dialog.show();

        Bundle StudentSelection = getIntent().getExtras();
        long InstituteID = StudentSelection.getLong("InstituteID",0);
        long MediumID = StudentSelection.getLong("MediumID",0);
        long ShiftID = StudentSelection.getLong("ShiftID",0);
        long ClassID = StudentSelection.getLong("ClassID",0);
        long SectionID = StudentSelection.getLong("SectionID",0);
        long DepertmentID = StudentSelection.getLong("DepertmentID",0);

        studentUrl = getString(R.string.baseUrl)+"/api/onEms/getStudent"+"/"+InstituteID+"/"+
                ClassID+"/"+SectionID+"/"+DepertmentID+"/"+MediumID+"/"+ShiftID+"/"+"0";

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        StudentDataGetRequest();
    }

    void parseStudentJsonData(String jsonString) {
        try {
            JSONArray studentJsonArray = new JSONArray(jsonString);
            for(int i = 0; i < studentJsonArray.length(); ++i) {
                JSONObject studentJsonObject = studentJsonArray.getJSONObject(i);
                ReportAllStudentRowModel reportAllStudentRowModel = new ReportAllStudentRowModel();
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
            dialog.dismiss();
        } catch (JSONException e) {
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
            dialog.dismiss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
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
        Toast.makeText(getApplicationContext(), "Selected: " + reportAllStudentRowModel.getUserName() + ", " + reportAllStudentRowModel.getRollNo(), Toast.LENGTH_LONG).show();
    }

    public void RefreshStudentList(){
        if(StaticHelperClass.isNetworkAvailable(this)) {
            ClearStudentList();
            //Preparing Student data from server
            StringRequest stringStudentRequest = new StringRequest(Request.Method.GET, studentUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            parseStudentJsonData(response);
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    mSwipeRefreshLayout.setRefreshing(false);
                }
            })
            {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String>  params = new HashMap<>();
                    params.put("Authorization", "Request_From_onEMS_Android_app");
                    return params;
                }
            };
            MySingleton.getInstance(this).addToRequestQueue(stringStudentRequest);
        } else {
            Toast.makeText(ReportAllStudentList.this,"Please check your internet connection!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void StudentDataGetRequest(){
        if(StaticHelperClass.isNetworkAvailable(this)) {
            //Preparing Student data from server
            StringRequest stringStudentRequest = new StringRequest(Request.Method.GET, studentUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            parseStudentJsonData(response);

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    dialog.dismiss();
                }
            })
            {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String>  params = new HashMap<>();
                    params.put("Authorization", "Request_From_onEMS_Android_app");
                    return params;
                }
            };
            MySingleton.getInstance(this).addToRequestQueue(stringStudentRequest);
        } else {
            Toast.makeText(ReportAllStudentList.this,"Please check your internet connection!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void ClearStudentList(){
        int size = studentList.size();
        studentList.clear();
        mAdapter.notifyItemRangeRemoved(0, size+0);
    }
}
