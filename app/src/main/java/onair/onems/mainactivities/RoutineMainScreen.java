package onair.onems.mainactivities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import onair.onems.R;
import onair.onems.Services.GlideApp;
import onair.onems.customadapters.ExpandableListAdapter;
import onair.onems.mainactivities.Routine.Friday;
import onair.onems.mainactivities.Routine.Monday;
import onair.onems.mainactivities.Routine.Saturday;
import onair.onems.mainactivities.Routine.Sunday;
import onair.onems.mainactivities.Routine.Thursday;
import onair.onems.mainactivities.Routine.Tuesday;
import onair.onems.mainactivities.Routine.Wednesday;
import onair.onems.mainactivities.TeacherAttendanceShow.ShowAttendance;
import onair.onems.models.ExpandedMenuModel;
import onair.onems.network.MySingleton;

public class RoutineMainScreen extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private List<ExpandedMenuModel> listDataHeader;
    private HashMap<ExpandedMenuModel, List<String>> listDataChild;
    private static final String MyPREFERENCES = "LogInKey";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.routine_activity_main);

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        ExpandableListAdapter mMenuAdapter;
        ExpandableListView expandableList;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /* to set the menu icon image*/
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(android.R.drawable.ic_menu_add);
        actionBar.setDisplayHomeAsUpEnabled(true);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        expandableList = (ExpandableListView) findViewById(R.id.navigationmenu);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View view = getLayoutInflater().inflate(R.layout.nav_header_main,null);
        ImageView profilePicture = (ImageView)view.findViewById(R.id.profilePicture);
        TextView userType = (TextView)view.findViewById(R.id.userType);
        TextView userName = (TextView)view.findViewById(R.id.userName);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String imageUrl = prefs.getString("ImageUrl","");
        String name = prefs.getString("UserFullName","");
        long user = prefs.getLong("UserTypeID",0);
        userName.setText(name);
        if(user == 4) {
            userType.setText("Teacher");
        }
        if(user == 1) {
            userType.setText("Admin");
        }
        GlideApp.with(this)
                .load(getString(R.string.baseUrl)+"/"+imageUrl.replace("\\","/")).apply(RequestOptions.circleCropTransform())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(profilePicture);

        navigationView.addHeaderView(view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        prepareListData();
        mMenuAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild, expandableList);

        // setting list adapter
        expandableList.setAdapter(mMenuAdapter);

        expandableList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                if((i == 2) && (i1 == 1) && (l == 1))
                {
                    Intent intent = new Intent(RoutineMainScreen.this, ShowAttendance.class);
                    startActivity(intent);
                }
                if((i == 2) && (i1 == 0) && (l == 0))
                {
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                    }
                }

                return false;
            }
        });
        expandableList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
//                Log.d("DEBUG", "heading clicked"+i+"--"+l);
                if((i == 7) && (l == 7))
                {
                    Intent intent = new Intent(RoutineMainScreen.this, StudentiCardMain.class);
                    startActivity(intent);
                    finish();
                }

                if((i == 8) && (l == 8))
                {
                    Intent intent = new Intent(RoutineMainScreen.this, ReportAllStudentMain.class);
                    startActivity(intent);
                    finish();
                }
                return false;
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<ExpandedMenuModel>();
        listDataChild = new HashMap<ExpandedMenuModel, List<String>>();

        ExpandedMenuModel menuNotice = new ExpandedMenuModel();
        menuNotice.setIconName("Notice");
        menuNotice.setIconImg(R.drawable.nav_notice);
        // Adding data header
        listDataHeader.add(menuNotice);

        ExpandedMenuModel menuRoutine = new ExpandedMenuModel();
        menuRoutine.setIconName("Routine");
        menuRoutine.setIconImg(R.drawable.nav_routine);
        listDataHeader.add(menuRoutine);

        ExpandedMenuModel menuAttendance = new ExpandedMenuModel();
        menuAttendance.setIconName("Atendance");
        menuAttendance.setIconImg(R.drawable.nav_attendance);
        listDataHeader.add(menuAttendance);

        ExpandedMenuModel menuSyllabus = new ExpandedMenuModel();
        menuSyllabus.setIconName("Syllabus");
        menuSyllabus.setIconImg(R.drawable.nav_syllabus);
        listDataHeader.add(menuSyllabus);

        ExpandedMenuModel menuExam = new ExpandedMenuModel();
        menuExam.setIconName("Exam");
        menuExam.setIconImg(R.drawable.nav_exam);
        listDataHeader.add(menuExam);

        ExpandedMenuModel menuResult = new ExpandedMenuModel();
        menuResult.setIconName("Result");
        menuResult.setIconImg(R.drawable.nav_result);
        listDataHeader.add(menuResult);

        ExpandedMenuModel menuContact = new ExpandedMenuModel();
        menuContact.setIconName("Contact");
        menuContact.setIconImg(R.drawable.nav_contact);
        listDataHeader.add(menuContact);

        ExpandedMenuModel menuiCard = new ExpandedMenuModel();
        menuiCard.setIconName("iCard");
        menuiCard.setIconImg(R.drawable.ic_person);
        listDataHeader.add(menuiCard);

        ExpandedMenuModel menuStudentList = new ExpandedMenuModel();
        menuStudentList.setIconName("Student List");
        menuStudentList.setIconImg(R.drawable.ic_action_users);
        listDataHeader.add(menuStudentList);

        // Adding child data
        List<String> headingNotice = new ArrayList<>();
        headingNotice.add("New Notice");
        headingNotice.add("Old Notice");

        List<String> headingRoutine = new ArrayList<>();
        headingRoutine.add("Mid Term Exam");
        headingRoutine.add("Final Exam");

        List<String> headingAttendance = new ArrayList<>();
        headingAttendance.add("Take Attendance");
        headingAttendance.add("Show Attendance");

        List<String> headingSyllabus = new ArrayList<>();
        List<String> headingExam = new ArrayList<>();
        List<String> headingResult = new ArrayList<>();
        List<String> headingContact = new ArrayList<>();
        List<String> headingiCard = new ArrayList<>();
        List<String> headingStudentList = new ArrayList<>();

        listDataChild.put(listDataHeader.get(0), headingNotice);// Header, Child data
        listDataChild.put(listDataHeader.get(1), headingRoutine);
        listDataChild.put(listDataHeader.get(2), headingAttendance);
        listDataChild.put(listDataHeader.get(3), headingSyllabus);
        listDataChild.put(listDataHeader.get(4), headingExam);
        listDataChild.put(listDataHeader.get(5), headingResult);
        listDataChild.put(listDataHeader.get(6), headingContact);
        listDataChild.put(listDataHeader.get(7), headingiCard);
        listDataChild.put(listDataHeader.get(8), headingStudentList);

    }

    private void setupDrawerContent(NavigationView navigationView) {
        //revision: this don't works, use setOnChildClickListener() and setOnGroupClickListener() above instead
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent mainIntent = new Intent(RoutineMainScreen.this,TeacherMainScreen.class);
            startActivity(mainIntent);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            SharedPreferences sharedPreferences  = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("LogInState", false);
            editor.apply();
            Intent intent = new Intent(RoutineMainScreen.this, LoginScreen.class);
            startActivity(intent);
            finish();
            return true;
        }

        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new Saturday(), "Sat");
        adapter.addFragment(new Sunday(), "Sun");
        adapter.addFragment(new Monday(), "Mon");
        adapter.addFragment(new Tuesday(), "Tue");
        adapter.addFragment(new Wednesday(), "Wed");
        adapter.addFragment(new Thursday(), "Thu");
        adapter.addFragment(new Friday(), "Fri");
        viewPager.setAdapter(adapter);

    }
}
