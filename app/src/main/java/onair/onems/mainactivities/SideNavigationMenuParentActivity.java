package onair.onems.mainactivities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
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

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import onair.onems.R;
import onair.onems.routine.RoutineMainScreen;
import onair.onems.Services.GlideApp;
import onair.onems.attendance.StudentAttendanceReport;
import onair.onems.attendance.TakeAttendance;
import onair.onems.customadapters.ExpandableListAdapter;
import onair.onems.attendance.ShowAttendance;
import onair.onems.icard.StudentiCardMain;
import onair.onems.icard.StudentiCardNewEntry;
import onair.onems.Fee.Fee;
import onair.onems.models.ExpandedMenuModel;

public class SideNavigationMenuParentActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private List<ExpandedMenuModel> listDataHeader;
    private HashMap<ExpandedMenuModel, List<String>> listDataChild;
    private static final String MyPREFERENCES = "LogInKey";
    private ExpandableListAdapter mMenuAdapter;
    private ExpandableListView expandableList;
    private int UserTypeID;
    public String activityName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onems_activity_main);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        UserTypeID = prefs.getInt("UserTypeID",0);

        expandableList = (ExpandableListView) findViewById(R.id.navigationmenu);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(android.R.drawable.ic_menu_add);
        actionBar.setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View view = getLayoutInflater().inflate(R.layout.onems_nav_header_main,null);
        navigationView.addHeaderView(view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        ImageView profilePicture = (ImageView)view.findViewById(R.id.profilePicture);
        TextView userType = (TextView)view.findViewById(R.id.userType);
        TextView userName = (TextView)view.findViewById(R.id.userName);
        String imageUrl = prefs.getString("ImageUrl","");
        String name = prefs.getString("UserFullName","");
        userName.setText(name);

        if(UserTypeID == 1) {
            userType.setText("Super Admin");
        } else if(UserTypeID == 2) {
            userType.setText("Institute Admin");
        } else if(UserTypeID == 3) {
            userType.setText("Student");
        } else if(UserTypeID == 4) {
            userType.setText("Teacher");
        } else if(UserTypeID == 5) {
            userType.setText("Guardian");
        }

        GlideApp.with(this)
                .load(getString(R.string.baseUrl)+"/"+imageUrl.replace("\\","/")).apply(RequestOptions.circleCropTransform())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(profilePicture);

        switch (UserTypeID) {
            case 1: prepareSuperAdminSideMenu();
            break;

            case 2: prepareInstituteAdminSideMenu();
            break;

            case 3: prepareStudentSideMenu();
            break;

            case 4: prepareTeacherSideMenu();
            break;

            case 5: prepareGuardianSideMenu();
            break;
        }
    }

    private void setupDrawerContent(NavigationView navigationView) {
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
            Intent intent = new Intent(getApplicationContext(), LoginScreen.class);
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

    private void prepareSuperAdminSideMenu() {

        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        // Adding data header
        ExpandedMenuModel menuDashboard = new ExpandedMenuModel();
        menuDashboard.setIconName("Dashboard");
        menuDashboard.setIconImg(R.drawable.ic_dashboard);
        listDataHeader.add(menuDashboard);

        ExpandedMenuModel menuNotice = new ExpandedMenuModel();
        menuNotice.setIconName("Notice");
        menuNotice.setIconImg(R.drawable.nav_notice);
        listDataHeader.add(menuNotice);

        ExpandedMenuModel menuRoutine = new ExpandedMenuModel();
        menuRoutine.setIconName("Routine");
        menuRoutine.setIconImg(R.drawable.nav_routine);
        listDataHeader.add(menuRoutine);

        ExpandedMenuModel menuAttendance = new ExpandedMenuModel();
        menuAttendance.setIconName("Attendance");
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
        List<String> headingDashboard = new ArrayList<>();
        List<String> headingNotice = new ArrayList<>();
        List<String> headingRoutine = new ArrayList<>();

        List<String> headingAttendance = new ArrayList<>();
        headingAttendance.add("Take Attendance");
        headingAttendance.add("Show Attendance");

        List<String> headingSyllabus = new ArrayList<>();
        List<String> headingExam = new ArrayList<>();
        List<String> headingResult = new ArrayList<>();
        List<String> headingContact = new ArrayList<>();

        List<String> headingiCard = new ArrayList<>();
        headingiCard.add("Find Student");
        headingiCard.add("Entry Student");

        List<String> headingStudentList = new ArrayList<>();

        // Header, Child data
        listDataChild.put(listDataHeader.get(0), headingDashboard);
        listDataChild.put(listDataHeader.get(1), headingNotice);
        listDataChild.put(listDataHeader.get(2), headingRoutine);
        listDataChild.put(listDataHeader.get(3), headingAttendance);
        listDataChild.put(listDataHeader.get(4), headingSyllabus);
        listDataChild.put(listDataHeader.get(5), headingExam);
        listDataChild.put(listDataHeader.get(6), headingResult);
        listDataChild.put(listDataHeader.get(7), headingContact);
        listDataChild.put(listDataHeader.get(8), headingiCard);
        listDataChild.put(listDataHeader.get(9), headingStudentList);

        mMenuAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild, UserTypeID);
        expandableList.setAdapter(mMenuAdapter);

        expandableList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
//                Log.d("DEBUG", "heading clicked"+i+"--"+l);

                if((i == 0) && (l == 0)) {
                    Intent intent = new Intent(getApplicationContext(), TeacherMainScreen.class);
                    startActivity(intent);
                    finish();
                }

                if((i == 2) && (l == 2)) {
                    if(activityName.equals(RoutineMainScreen.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), RoutineMainScreen.class);
                        startActivity(intent);
                        finish();
                    }
                }

                if((i == 9) && (l == 9)) {
                    if(activityName.equals(ReportAllStudentMain.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), ReportAllStudentMain.class);
                        startActivity(intent);
                        finish();
                    }
                }
                return false;
            }
        });

        expandableList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {

                if((i == 3) && (i1 == 0) && (l == 0)) {
                    if(activityName.equals(TakeAttendance.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), TakeAttendance.class);
                        startActivity(intent);
                        finish();
                    }
                }

                if((i == 3) && (i1 == 1) && (l == 1)) {
                    if(activityName.equals(ShowAttendance.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), ShowAttendance.class);
                        startActivity(intent);
                        finish();
                    }
                }

                if((i == 8) && (i1 == 0) && (l == 0)) {
                    if(activityName.equals(StudentiCardMain.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), StudentiCardMain.class);
                        startActivity(intent);
                        finish();
                    }
                }

                if((i == 8) && (i1 == 1) && (l == 1)) {
                    if(activityName.equals(StudentiCardNewEntry.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), StudentiCardNewEntry.class);
                        startActivity(intent);
                        finish();
                    }
                }

                return false;
            }
        });
    }

    private void prepareInstituteAdminSideMenu() {

        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        // Adding data header
        ExpandedMenuModel menuDashboard = new ExpandedMenuModel();
        menuDashboard.setIconName("Dashboard");
        menuDashboard.setIconImg(R.drawable.ic_dashboard);
        listDataHeader.add(menuDashboard);

        ExpandedMenuModel menuNotice = new ExpandedMenuModel();
        menuNotice.setIconName("Notice");
        menuNotice.setIconImg(R.drawable.nav_notice);
        listDataHeader.add(menuNotice);

        ExpandedMenuModel menuRoutine = new ExpandedMenuModel();
        menuRoutine.setIconName("Routine");
        menuRoutine.setIconImg(R.drawable.nav_routine);
        listDataHeader.add(menuRoutine);

        ExpandedMenuModel menuAttendance = new ExpandedMenuModel();
        menuAttendance.setIconName("Attendance");
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
        List<String> headingDashboard = new ArrayList<>();
        List<String> headingNotice = new ArrayList<>();
        List<String> headingRoutine = new ArrayList<>();

        List<String> headingAttendance = new ArrayList<>();
        headingAttendance.add("Take Attendance");
        headingAttendance.add("Show Attendance");

        List<String> headingSyllabus = new ArrayList<>();
        List<String> headingExam = new ArrayList<>();
        List<String> headingResult = new ArrayList<>();
        List<String> headingContact = new ArrayList<>();

        List<String> headingiCard = new ArrayList<>();
        headingiCard.add("Find Student");
        headingiCard.add("Entry Student");

        List<String> headingStudentList = new ArrayList<>();

        // Header, Child data
        listDataChild.put(listDataHeader.get(0), headingDashboard);
        listDataChild.put(listDataHeader.get(1), headingNotice);
        listDataChild.put(listDataHeader.get(2), headingRoutine);
        listDataChild.put(listDataHeader.get(3), headingAttendance);
        listDataChild.put(listDataHeader.get(4), headingSyllabus);
        listDataChild.put(listDataHeader.get(5), headingExam);
        listDataChild.put(listDataHeader.get(6), headingResult);
        listDataChild.put(listDataHeader.get(7), headingContact);
        listDataChild.put(listDataHeader.get(8), headingiCard);
        listDataChild.put(listDataHeader.get(9), headingStudentList);

        mMenuAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild, UserTypeID);
        expandableList.setAdapter(mMenuAdapter);

        expandableList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
//                Log.d("DEBUG", "heading clicked"+i+"--"+l);

                if((i == 0) && (l == 0)) {
                    Intent intent = new Intent(getApplicationContext(), TeacherMainScreen.class);
                    startActivity(intent);
                    finish();
                }

                if((i == 2) && (l == 2)) {
                    if(activityName.equals(RoutineMainScreen.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), RoutineMainScreen.class);
                        startActivity(intent);
                        finish();
                    }
                }

                if((i == 9) && (l == 9)) {
                    if(activityName.equals(ReportAllStudentMain.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), ReportAllStudentMain.class);
                        startActivity(intent);
                        finish();
                    }
                }
                return false;
            }
        });

        expandableList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {

                if((i == 3) && (i1 == 0) && (l == 0)) {
                    if(activityName.equals(TakeAttendance.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), TakeAttendance.class);
                        startActivity(intent);
                        finish();
                    }
                }

                if((i == 3) && (i1 == 1) && (l == 1)) {
                    if(activityName.equals(ShowAttendance.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), ShowAttendance.class);
                        startActivity(intent);
                        finish();
                    }
                }

                if((i == 8) && (i1 == 0) && (l == 0)) {
                    if(activityName.equals(StudentiCardMain.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), StudentiCardMain.class);
                        startActivity(intent);
                        finish();
                    }
                }

                if((i == 8) && (i1 == 1) && (l == 1)) {
                    if(activityName.equals(StudentiCardNewEntry.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), StudentiCardNewEntry.class);
                        startActivity(intent);
                        finish();
                    }
                }

                return false;
            }
        });
    }

    private void prepareTeacherSideMenu() {

        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        // Adding data header
        ExpandedMenuModel menuDashboard = new ExpandedMenuModel();
        menuDashboard.setIconName("Dashboard");
        menuDashboard.setIconImg(R.drawable.ic_dashboard);
        listDataHeader.add(menuDashboard);

        ExpandedMenuModel menuNotice = new ExpandedMenuModel();
        menuNotice.setIconName("Notice");
        menuNotice.setIconImg(R.drawable.nav_notice);
        listDataHeader.add(menuNotice);

        ExpandedMenuModel menuRoutine = new ExpandedMenuModel();
        menuRoutine.setIconName("Routine");
        menuRoutine.setIconImg(R.drawable.nav_routine);
        listDataHeader.add(menuRoutine);

        ExpandedMenuModel menuAttendance = new ExpandedMenuModel();
        menuAttendance.setIconName("Attendance");
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
        List<String> headingDashboard = new ArrayList<>();
        List<String> headingNotice = new ArrayList<>();
        List<String> headingRoutine = new ArrayList<>();

        List<String> headingAttendance = new ArrayList<>();
        headingAttendance.add("Take Attendance");
        headingAttendance.add("Show Attendance");

        List<String> headingSyllabus = new ArrayList<>();
        List<String> headingExam = new ArrayList<>();
        List<String> headingResult = new ArrayList<>();
        List<String> headingContact = new ArrayList<>();

        List<String> headingiCard = new ArrayList<>();
        headingiCard.add("Find Student");
        headingiCard.add("Entry Student");

        List<String> headingStudentList = new ArrayList<>();

        // Header, Child data
        listDataChild.put(listDataHeader.get(0), headingDashboard);
        listDataChild.put(listDataHeader.get(1), headingNotice);
        listDataChild.put(listDataHeader.get(2), headingRoutine);
        listDataChild.put(listDataHeader.get(3), headingAttendance);
        listDataChild.put(listDataHeader.get(4), headingSyllabus);
        listDataChild.put(listDataHeader.get(5), headingExam);
        listDataChild.put(listDataHeader.get(6), headingResult);
        listDataChild.put(listDataHeader.get(7), headingContact);
        listDataChild.put(listDataHeader.get(8), headingiCard);
        listDataChild.put(listDataHeader.get(9), headingStudentList);

        mMenuAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild, UserTypeID);
        expandableList.setAdapter(mMenuAdapter);

        expandableList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
//                Log.d("DEBUG", "heading clicked"+i+"--"+l);

                if((i == 0) && (l == 0)) {
                    Intent intent = new Intent(getApplicationContext(), TeacherMainScreen.class);
                    startActivity(intent);
                    finish();
                }

                if((i == 2) && (l == 2)) {
                    if(activityName.equals(RoutineMainScreen.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), RoutineMainScreen.class);
                        startActivity(intent);
                        finish();
                    }
                }

                if((i == 9) && (l == 9)) {
                    if(activityName.equals(ReportAllStudentMain.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), ReportAllStudentMain.class);
                        startActivity(intent);
                        finish();
                    }
                }
                return false;
            }
        });

        expandableList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {

                if((i == 3) && (i1 == 0) && (l == 0)) {
                    if(activityName.equals(TakeAttendance.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), TakeAttendance.class);
                        startActivity(intent);
                        finish();
                    }
                }

                if((i == 3) && (i1 == 1) && (l == 1)) {
                    if(activityName.equals(ShowAttendance.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), ShowAttendance.class);
                        startActivity(intent);
                        finish();
                    }
                }

                if((i == 8) && (i1 == 0) && (l == 0)) {
                    if(activityName.equals(StudentiCardMain.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), StudentiCardMain.class);
                        startActivity(intent);
                        finish();
                    }
                }

                if((i == 8) && (i1 == 1) && (l == 1)) {
                    if(activityName.equals(StudentiCardNewEntry.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), StudentiCardNewEntry.class);
                        startActivity(intent);
                        finish();
                    }
                }

                return false;
            }
        });

    }

    private void prepareStudentSideMenu() {

        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        // Adding data header
        ExpandedMenuModel menuDashboard = new ExpandedMenuModel();
        menuDashboard.setIconName("Dashboard");
        menuDashboard.setIconImg(R.drawable.ic_dashboard);
        listDataHeader.add(menuDashboard);

        ExpandedMenuModel menuNotice = new ExpandedMenuModel();
        menuNotice.setIconName("Notice");
        menuNotice.setIconImg(R.drawable.nav_notice);
        listDataHeader.add(menuNotice);

        ExpandedMenuModel menuRoutine = new ExpandedMenuModel();
        menuRoutine.setIconName("Routine");
        menuRoutine.setIconImg(R.drawable.nav_routine);
        listDataHeader.add(menuRoutine);

        ExpandedMenuModel menuAttendance = new ExpandedMenuModel();
        menuAttendance.setIconName("Attendance");
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

        ExpandedMenuModel menuFee = new ExpandedMenuModel();
        menuFee.setIconName("Fee");
        menuFee.setIconImg(R.drawable.nav_fee);
        listDataHeader.add(menuFee);

        ExpandedMenuModel menuContact = new ExpandedMenuModel();
        menuContact.setIconName("Contact");
        menuContact.setIconImg(R.drawable.nav_contact);
        listDataHeader.add(menuContact);

        // Adding child data
        List<String> headingDashboard = new ArrayList<>();
        List<String> headingNotice = new ArrayList<>();
        List<String> headingRoutine = new ArrayList<>();
        List<String> headingAttendance = new ArrayList<>();
        List<String> headingSyllabus = new ArrayList<>();
        List<String> headingExam = new ArrayList<>();
        List<String> headingResult = new ArrayList<>();
        List<String> headingFee = new ArrayList<>();
        List<String> headingContact = new ArrayList<>();

        // Header, Child data
        listDataChild.put(listDataHeader.get(0), headingDashboard);
        listDataChild.put(listDataHeader.get(1), headingNotice);
        listDataChild.put(listDataHeader.get(2), headingRoutine);
        listDataChild.put(listDataHeader.get(3), headingAttendance);
        listDataChild.put(listDataHeader.get(4), headingSyllabus);
        listDataChild.put(listDataHeader.get(5), headingExam);
        listDataChild.put(listDataHeader.get(6), headingResult);
        listDataChild.put(listDataHeader.get(7), headingFee);
        listDataChild.put(listDataHeader.get(8), headingContact);

        mMenuAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild, UserTypeID);
        expandableList.setAdapter(mMenuAdapter);

        expandableList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
//                Log.d("DEBUG", "heading clicked"+i+"--"+l);

                if((i == 0) && (l == 0)) {
                    Intent intent = new Intent(getApplicationContext(), StudentMainScreen.class);
                    startActivity(intent);
                    finish();
                }

                if((i == 2) && (l == 2)) {
                    if(activityName.equals(RoutineMainScreen.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), RoutineMainScreen.class);
                        startActivity(intent);
                        finish();
                    }
                }

                if((i == 3) && (l == 3)) {
                    if(activityName.equals(StudentAttendanceReport.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), StudentAttendanceReport.class);
                        startActivity(intent);
                        finish();
                    }
                }

                if((i == 7) && (l == 7)) {
                    if(activityName.equals(Fee.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), Fee.class);
                        startActivity(intent);
                        finish();
                    }
                }
                return false;
            }
        });

        expandableList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {

//                if((i == 3) && (i1 == 0) && (l == 0)) {
//                    Intent intent = new Intent(getApplicationContext(), TakeAttendance.class);
//                    startActivity(intent);
//                    finish();
//                } else if(activityName.equals(TakeAttendance.class.getName())){
//                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//                    if (drawer.isDrawerOpen(GravityCompat.START)) {
//                        drawer.closeDrawer(GravityCompat.START);
//                    }
//                }
//
//                if((i == 3) && (i1 == 1) && (l == 1)) {
//                    Intent intent = new Intent(getApplicationContext(), ShowAttendance.class);
//                    startActivity(intent);
//                    finish();
//                } else if(activityName.equals(ShowAttendance.class.getName())){
//                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//                    if (drawer.isDrawerOpen(GravityCompat.START)) {
//                        drawer.closeDrawer(GravityCompat.START);
//                    }
//                }
//
//                if((i == 8) && (i1 == 0) && (l == 0)) {
//                    Intent intent = new Intent(getApplicationContext(), StudentiCardMain.class);
//                    startActivity(intent);
//                    finish();
//                } else if(activityName.equals(StudentiCardMain.class.getName())){
//                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//                    if (drawer.isDrawerOpen(GravityCompat.START)) {
//                        drawer.closeDrawer(GravityCompat.START);
//                    }
//                }
//
//                if((i == 8) && (i1 == 1) && (l == 1)) {
//                    Intent intent = new Intent(getApplicationContext(), StudentiCardNewEntry.class);
//                    startActivity(intent);
//                    finish();
//                } else if(activityName.equals(StudentiCardNewEntry.class.getName())){
//                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//                    if (drawer.isDrawerOpen(GravityCompat.START)) {
//                        drawer.closeDrawer(GravityCompat.START);
//                    }
//                }

                return false;
            }
        });

    }

    private void prepareGuardianSideMenu() {

        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        // Adding data header
        ExpandedMenuModel menuDashboard = new ExpandedMenuModel();
        menuDashboard.setIconName("Dashboard");
        menuDashboard.setIconImg(R.drawable.ic_dashboard);
        listDataHeader.add(menuDashboard);

        ExpandedMenuModel menuNotice = new ExpandedMenuModel();
        menuNotice.setIconName("Notice");
        menuNotice.setIconImg(R.drawable.nav_notice);
        listDataHeader.add(menuNotice);

        ExpandedMenuModel menuRoutine = new ExpandedMenuModel();
        menuRoutine.setIconName("Routine");
        menuRoutine.setIconImg(R.drawable.nav_routine);
        listDataHeader.add(menuRoutine);

        ExpandedMenuModel menuAttendance = new ExpandedMenuModel();
        menuAttendance.setIconName("Attendance");
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

        ExpandedMenuModel menuFee = new ExpandedMenuModel();
        menuFee.setIconName("Fee");
        menuFee.setIconImg(R.drawable.nav_fee);
        listDataHeader.add(menuFee);

        ExpandedMenuModel menuContact = new ExpandedMenuModel();
        menuContact.setIconName("Contact");
        menuContact.setIconImg(R.drawable.nav_contact);
        listDataHeader.add(menuContact);

        // Adding child data
        List<String> headingDashboard = new ArrayList<>();
        List<String> headingNotice = new ArrayList<>();
        List<String> headingRoutine = new ArrayList<>();
        List<String> headingAttendance = new ArrayList<>();
        List<String> headingSyllabus = new ArrayList<>();
        List<String> headingExam = new ArrayList<>();
        List<String> headingResult = new ArrayList<>();
        List<String> headingFee = new ArrayList<>();
        List<String> headingContact = new ArrayList<>();

        // Header, Child data
        listDataChild.put(listDataHeader.get(0), headingDashboard);
        listDataChild.put(listDataHeader.get(1), headingNotice);
        listDataChild.put(listDataHeader.get(2), headingRoutine);
        listDataChild.put(listDataHeader.get(3), headingAttendance);
        listDataChild.put(listDataHeader.get(4), headingSyllabus);
        listDataChild.put(listDataHeader.get(5), headingExam);
        listDataChild.put(listDataHeader.get(6), headingResult);
        listDataChild.put(listDataHeader.get(7), headingFee);
        listDataChild.put(listDataHeader.get(8), headingContact);

        mMenuAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild, UserTypeID);
        expandableList.setAdapter(mMenuAdapter);

        expandableList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
//                Log.d("DEBUG", "heading clicked"+i+"--"+l);

                if((i == 0) && (l == 0)) {
                    Intent intent = new Intent(getApplicationContext(), StudentMainScreen.class);
                    startActivity(intent);
                    finish();
                }

                if((i == 2) && (l == 2)) {
                    if(activityName.equals(RoutineMainScreen.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), RoutineMainScreen.class);
                        startActivity(intent);
                        finish();
                    }
                }

                if((i == 3) && (l == 3)) {
                    if(activityName.equals(StudentAttendanceReport.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), StudentAttendanceReport.class);
                        startActivity(intent);
                        finish();
                    }
                }

                if((i == 7) && (l == 7)) {
                    if(activityName.equals(Fee.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), Fee.class);
                        startActivity(intent);
                        finish();
                    }
                }
                return false;
            }
        });

        expandableList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {

//                if((i == 3) && (i1 == 0) && (l == 0)) {
//                    Intent intent = new Intent(getApplicationContext(), TakeAttendance.class);
//                    startActivity(intent);
//                    finish();
//                } else if(activityName.equals(TakeAttendance.class.getName())){
//                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//                    if (drawer.isDrawerOpen(GravityCompat.START)) {
//                        drawer.closeDrawer(GravityCompat.START);
//                    }
//                }
//
//                if((i == 3) && (i1 == 1) && (l == 1)) {
//                    Intent intent = new Intent(getApplicationContext(), ShowAttendance.class);
//                    startActivity(intent);
//                    finish();
//                } else if(activityName.equals(ShowAttendance.class.getName())){
//                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//                    if (drawer.isDrawerOpen(GravityCompat.START)) {
//                        drawer.closeDrawer(GravityCompat.START);
//                    }
//                }
//
//                if((i == 8) && (i1 == 0) && (l == 0)) {
//                    Intent intent = new Intent(getApplicationContext(), StudentiCardMain.class);
//                    startActivity(intent);
//                    finish();
//                } else if(activityName.equals(StudentiCardMain.class.getName())){
//                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//                    if (drawer.isDrawerOpen(GravityCompat.START)) {
//                        drawer.closeDrawer(GravityCompat.START);
//                    }
//                }
//
//                if((i == 8) && (i1 == 1) && (l == 1)) {
//                    Intent intent = new Intent(getApplicationContext(), StudentiCardNewEntry.class);
//                    startActivity(intent);
//                    finish();
//                } else if(activityName.equals(StudentiCardNewEntry.class.getName())){
//                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//                    if (drawer.isDrawerOpen(GravityCompat.START)) {
//                        drawer.closeDrawer(GravityCompat.START);
//                    }
//                }

                return false;
            }
        });
    }

}
