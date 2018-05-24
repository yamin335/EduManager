package onair.onems.notice;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
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
import onair.onems.mainactivities.SideNavigationMenuParentActivity;
import onair.onems.mainactivities.StudentMainScreen;
import onair.onems.mainactivities.TeacherMainScreen;
import onair.onems.network.MySingleton;

public class NoticeMainScreen extends SideNavigationMenuParentActivity implements NoticeAdapter.NoticeAdapterListener{

    private RecyclerView recyclerView;
    private ArrayList<JSONObject> noticeList;
    private NoticeAdapter mAdapter;
    private ProgressDialog mNoticeDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityName = NoticeMainScreen.class.getName();

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View childActivityLayout = inflater.inflate(R.layout.notice_content_main, null);
        LinearLayout parentActivityLayout = findViewById(R.id.contentMain);
        parentActivityLayout.addView(childActivityLayout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        recyclerView = findViewById(R.id.recycler_view);
        noticeList = new ArrayList<>();

        NoticeDataGetRequest();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(UserTypeID == 1) {
            Intent mainIntent = new Intent(NoticeMainScreen.this,TeacherMainScreen.class);
            startActivity(mainIntent);
            finish();
        } else if(UserTypeID == 2) {
            Intent mainIntent = new Intent(NoticeMainScreen.this,TeacherMainScreen.class);
            startActivity(mainIntent);
            finish();
        } else if(UserTypeID == 3) {
            Intent mainIntent = new Intent(NoticeMainScreen.this,StudentMainScreen.class);
            startActivity(mainIntent);
            finish();
        } else if(UserTypeID == 4) {
            Intent mainIntent = new Intent(NoticeMainScreen.this,TeacherMainScreen.class);
            startActivity(mainIntent);
            finish();
        } else if(UserTypeID == 5) {
            Intent mainIntent = new Intent(NoticeMainScreen.this,StudentMainScreen.class);
            startActivity(mainIntent);
            finish();
        }
    }

    @Override
    public void onNoticeSelected(JSONObject notice) {
        CustomNoticeDialog customNoticeDialog = new CustomNoticeDialog(this, notice);
        customNoticeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        customNoticeDialog.setCancelable(false);
        customNoticeDialog.show();
    }

    private void NoticeDataGetRequest() {
        if (StaticHelperClass.isNetworkAvailable(this)) {

            String noticeDataGetUrl = getString(R.string.baseUrl)+"/api/onEms/spGetDashNotice/"
                    +UserTypeID+"/"+InstituteID;

            mNoticeDialog = new ProgressDialog(this);
            mNoticeDialog.setTitle("Loading session...");
            mNoticeDialog.setMessage("Please Wait...");
            mNoticeDialog.setCancelable(false);
            mNoticeDialog.setIcon(R.drawable.onair);

            //Preparing Shift data from server
            StringRequest stringNoticeRequest = new StringRequest(Request.Method.GET, noticeDataGetUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            prepareNoticeList(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mNoticeDialog.dismiss();
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
            MySingleton.getInstance(this).addToRequestQueue(stringNoticeRequest);
        } else {
            Toast.makeText(NoticeMainScreen.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void prepareNoticeList(String result) {
        try {
            noticeList = new ArrayList<>();
            JSONArray noticeJsonArray = new JSONArray(result);
            for(int i = 0; i<noticeJsonArray.length(); i++) {
                noticeList.add(noticeJsonArray.getJSONObject(i));
            }

            mAdapter = new NoticeAdapter(this, noticeList, this);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
            mNoticeDialog.dismiss();
        } catch (JSONException e) {
            e.printStackTrace();
            mNoticeDialog.dismiss();
        }
    }
}
