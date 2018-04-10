package onair.onems.notice;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import onair.onems.R;
import onair.onems.Services.GlideApp;
import onair.onems.Services.StaticHelperClass;
import onair.onems.attendance.TakeAttendance;
import onair.onems.contacts.ContactsMainScreen;
import onair.onems.customadapters.ExpandableListAdapter;
import onair.onems.customadapters.NoticeAdapter;
import onair.onems.attendance.ShowAttendance;
import onair.onems.icard.StudentiCardMain;
import onair.onems.mainactivities.LoginScreen;
import onair.onems.mainactivities.SideNavigationMenuParentActivity;
import onair.onems.mainactivities.StudentMainScreen;
import onair.onems.mainactivities.TeacherMainScreen;
import onair.onems.models.ExpandedMenuModel;
import onair.onems.models.NoticeModel;
import onair.onems.studentlist.ReportAllStudentMain;

public class NoticeMainScreen extends SideNavigationMenuParentActivity implements NoticeAdapter.NoticeAdapterListener{

    private RecyclerView recyclerView;
    private ArrayList<NoticeModel> noticeList;
    private NoticeAdapter mAdapter;
    private long InstituteID;
    private int UserTypeID;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityName = NoticeMainScreen.class.getName();

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View childActivityLayout = inflater.inflate(R.layout.notice_content_main, null);
        LinearLayout parentActivityLayout = (LinearLayout) findViewById(R.id.contentMain);
        parentActivityLayout.addView(childActivityLayout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        InstituteID = prefs.getLong("InstituteID",0);
        UserTypeID = prefs.getInt("UserTypeID",0);

        if(!StaticHelperClass.isNetworkAvailable(this))
        {
            Toast.makeText(NoticeMainScreen.this,"Please check your internet connection and open app again!!! ",Toast.LENGTH_LONG).show();
        }

        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        noticeList = new ArrayList<>();
        mAdapter = new NoticeAdapter(this, noticeList, this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        prepareNoticeList();
    }

    private void prepareNoticeList() {
        for(int i = 0; i < 20; ++i) {
            NoticeModel noticeModel = new NoticeModel();
            noticeModel.setTitle(noticeModel.getTitle()+"--"+i);
            noticeList.add(noticeModel);
        }
        // refreshing recycler view
        mAdapter.notifyDataSetChanged();
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
    public void onNoticeSelected(NoticeModel noticeModel) {
        Toast.makeText(getApplicationContext(), "Selected: " + noticeModel.getTitle() + ", " + noticeModel.getDate(), Toast.LENGTH_LONG).show();
    }
}
