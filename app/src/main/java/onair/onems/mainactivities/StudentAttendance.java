package onair.onems.mainactivities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import onair.onems.R;
import onair.onems.customadapters.ExpandableListAdapter;
import onair.onems.customadapters.ExpandableListAdapterStudent;
import onair.onems.fragment.OneFragment;
import onair.onems.fragment.TwoFragment;
import onair.onems.mainactivities.Fees.Fee;
import onair.onems.mainactivities.Fees.FeesHistory;
import onair.onems.mainactivities.Routine.ClassRoutine;
import onair.onems.models.ExpandedMenuModel;

/**
 * Created by User on 12/6/2017.
 */

public class StudentAttendance extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    ExpandableListAdapterStudent mMenuAdapter;
    ExpandableListView expandableList;
    CircleImageView circleImageView;
    List<ExpandedMenuModel> listDataHeader;
    HashMap<ExpandedMenuModel, List<String>> listDataChild;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    public static final String MyPREFERENCES = "LogInKey";
    public static SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_attendance);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = (ViewPager) findViewById(R.id.pager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(android.R.drawable.ic_menu_add);
        ab.setDisplayHomeAsUpEnabled(true);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        expandableList = (ExpandableListView) findViewById(R.id.navigationmenu);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View view = getLayoutInflater().inflate(R.layout.nav_header_student_attendance,null);
        ImageView profilePicture = (ImageView)view.findViewById(R.id.profilePicture);
        TextView userType = (TextView)view.findViewById(R.id.userType);
        TextView userName = (TextView)view.findViewById(R.id.userName);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String imageUrl = prefs.getString("ImageUrl","");
        String name = prefs.getString("UserFullName","");

        long user = prefs.getLong("UserTypeID",0);



        userName.setText(name);
        if(user == 3)
        {
            userType.setText("Student");
        }
        else if(user == 5)
        {
            userType.setText("Guardian");
        }
        Glide.with(this).load(getString(R.string.baseUrlRaw)+imageUrl.replace("\\","/")).apply(RequestOptions.circleCropTransform()).into(profilePicture);
        navigationView.addHeaderView(view);

        if (navigationView != null)
        {
            setupDrawerContent(navigationView);
        }

        prepareListData();
        mMenuAdapter = new ExpandableListAdapterStudent(this, listDataHeader, listDataChild, expandableList);

        // setting list adapter
        expandableList.setAdapter(mMenuAdapter);


        expandableList.setOnChildClickListener(new ExpandableListView.OnChildClickListener()
        {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                if(i==6&&i1==0)
                {
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    if (drawer.isDrawerOpen(GravityCompat.START))
                    {
                        drawer.closeDrawer(GravityCompat.START);
                        Intent mainIntent = new Intent(StudentAttendance.this,Fee.class);
                        StudentAttendance.this.startActivity(mainIntent);

                    }
                }
                if(i==6&&i1==1)
                {
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    if (drawer.isDrawerOpen(GravityCompat.START))
                    {
                        drawer.closeDrawer(GravityCompat.START);
                        Intent mainIntent = new Intent(StudentAttendance.this,FeesHistory.class);
                       StudentAttendance.this.startActivity(mainIntent);

                    }
                }
                return false;
            }
        });
        expandableList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
//                Log.d("DEBUG", "heading clicked"+i+"--"+l);
                if((i == 1) )
                {
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                        Intent mainIntent = new Intent(StudentAttendance.this,ClassRoutine.class);
                       StudentAttendance.this.startActivity(mainIntent);
                        finish();
                    }
                }
                if((i == 2) )
                {
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);

                    }
                }
                return false;
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();


    }

    private void prepareListData()
    {
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

        ExpandedMenuModel menuFee = new ExpandedMenuModel();
        menuFee.setIconName("Fee");
        menuFee.setIconImg(R.drawable.nav_fee);
        listDataHeader.add(menuFee);

        ExpandedMenuModel menuContact = new ExpandedMenuModel();
        menuContact.setIconName("Contact");
        menuContact.setIconImg(R.drawable.nav_contact);
        listDataHeader.add(menuContact);

        // Adding child data
        List<String> headingNotice = new ArrayList<String>();
        headingNotice.add("New Notice");
        headingNotice.add("Old Notice");

        List<String> headingRoutine = new ArrayList<String>();
        headingRoutine.add("Mid Term Exam");
        headingRoutine.add("Final Exam");

        List<String> headingAttendance = new ArrayList<String>();
        List<String> headingSyllabus = new ArrayList<String>();
        List<String> headingExam = new ArrayList<String>();
        List<String> headingResult = new ArrayList<String>();
        List<String> headingFees = new ArrayList<String>();
        headingFees.add("Pay fee");
        headingFees.add("Fee History");
        List<String> headingContact = new ArrayList<String>();

        listDataChild.put(listDataHeader.get(0), headingNotice);// Header, Child data
        listDataChild.put(listDataHeader.get(1), headingRoutine);
        listDataChild.put(listDataHeader.get(2), headingAttendance);
        listDataChild.put(listDataHeader.get(3), headingSyllabus);
        listDataChild.put(listDataHeader.get(4), headingExam);
        listDataChild.put(listDataHeader.get(5), headingResult);
        listDataChild.put(listDataHeader.get(6), headingFees);
        listDataChild.put(listDataHeader.get(7), headingContact);

    }

    private void setupDrawerContent(NavigationView navigationView)
    {

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
            Intent mainIntent = new Intent(StudentAttendance.this,StudentMainScreen.class);
            StudentAttendance.this.startActivity(mainIntent);
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
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            sharedPreferences  = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("LogInState", false);
            editor.commit();
            Intent intent = new Intent(StudentAttendance.this, LoginScreen.class);
            startActivity(intent);
            finish();
            return true;
        }

        switch (item.getItemId())
        {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }



    private void setupViewPager(ViewPager viewPager)
    {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());


            adapter.addFragment(new OneFragment(), "SELF ATTENDANCE");
            adapter.addFragment(new TwoFragment(), "OTHERS ATTENDANCE");



        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter
    {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager)
        {
            super(manager);
        }

        @Override
        public Fragment getItem(int position)
        {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title)
        {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            return mFragmentTitleList.get(position);
        }
    }
}
