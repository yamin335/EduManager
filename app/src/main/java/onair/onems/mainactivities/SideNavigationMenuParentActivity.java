package onair.onems.mainactivities;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.internal.BottomNavigationPresenter;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
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
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import onair.onems.R;
import onair.onems.Services.StaticHelperClass;
import onair.onems.app.Config;
import onair.onems.attendance.AttendanceAdminDashboard;
import onair.onems.contacts.ContactsMainScreen;
import onair.onems.crm.NewClientEntry;
import onair.onems.customised.GuardianStudentSelectionDialog;
import onair.onems.exam.SubjectWiseMarksEntryMain;
import onair.onems.fees_report.FeeCollectionReportMain;
import onair.onems.homework.HomeworkMainScreenForAdmin;
import onair.onems.lesson_plan.LessonPlanMainScreenForAdmin;
import onair.onems.network.MySingleton;
import onair.onems.notification.NotificationAdapter;
import onair.onems.notification.NotificationDetails;
import onair.onems.notification.NotificationMainScreen;
import onair.onems.routine.ExamRoutineMainScreen;
import onair.onems.fee.FeeMainScreen;
import onair.onems.fee.FeesHistory;
import onair.onems.homework.HomeworkMainScreen;
import onair.onems.lesson_plan.LessonPlanMainScreen;
import onair.onems.login.LoginScreen;
import onair.onems.notice.NoticeMainScreen;
import onair.onems.result.ResultGradeStructure;
import onair.onems.result.ResultMainScreen;
import onair.onems.routine.RoutineMainScreen;
import onair.onems.Services.GlideApp;
import onair.onems.attendance.StudentAttendanceReport;
import onair.onems.attendance.TakeAttendance;
import onair.onems.customised.ExpandableListAdapter;
import onair.onems.attendance.ShowAttendance;
import onair.onems.icard.StudentiCardMain;
import onair.onems.icard.StudentiCardNewEntry;
import onair.onems.models.ExpandedMenuModel;
import onair.onems.studentlist.ReportAllStudentMain;
import onair.onems.syllabus.SyllabusMainScreen;
import onair.onems.syllabus.SyllabusMainScreenForAdmin;
import onair.onems.user.Profile;
import onair.onems.utils.NotificationUtils;

public class SideNavigationMenuParentActivity extends AppCompatActivity implements NotificationAdapter.DecreaseCounterListener,
        NotificationAdapter.IncreaseCounterListener {
    private DrawerLayout mDrawerLayout;
    private List<ExpandedMenuModel> listDataHeader;
    private HashMap<ExpandedMenuModel, List<String>> listDataChild;
    private static final String MyPREFERENCES = "LogInKey";
    private ExpandableListAdapter mMenuAdapter;
    private ExpandableListView expandableList;
    public int UserTypeID;
    public long InstituteID, LoggedUserShiftID, LoggedUserMediumID, LoggedUserClassID,
            LoggedUserDepartmentID, LoggedUserSectionID, LoggedUserSessionID;
    public String activityName = "", LoggedUserID, UserName;
    public static final int MENU_ITEM_CHANGE_STUDENT = 4;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private ConstraintLayout dashboard, profile, notification, contacts;
    private TextView textDashboard, textProfile, textNotification, textContacts, notificationCounter;
    private ImageView iconDashboard, iconProfile, iconNotification, iconContacts;
    private NotificationReceiverListener notificationReceiverListener;
    private ProgressDialog dialog;
    private boolean returnValue = false;

    @Override
    protected void onStart() {
        super.onStart();
//        MenuItem item = bottomNavigationView.getMenu().findItem(selectedBottomNavItem);
//        item.setChecked(true);
        int i = getSharedPreferences("UNSEEN_NOTIFICATIONS", Context.MODE_PRIVATE)
                .getInt("unseen", 0);
        if(i != 0) {
            notificationCounter.setVisibility(View.VISIBLE);
            notificationCounter.setText(Integer.toString(i));
        } else {
            notificationCounter.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
//        NotificationUtils.clearNotifications(getApplicationContext());

        int i = getSharedPreferences("UNSEEN_NOTIFICATIONS", Context.MODE_PRIVATE)
                .getInt("unseen", 0);
        if(i != 0) {
            notificationCounter.setVisibility(View.VISIBLE);
            notificationCounter.setText(Integer.toString(i));
        } else {
            notificationCounter.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onems_activity_main);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        UserTypeID = prefs.getInt("UserTypeID",0);
        InstituteID = prefs.getLong("InstituteID",0);
        LoggedUserID = prefs.getString("UserID", "0");
        UserName = prefs.getString("UserName", "0");
        LoggedUserShiftID = prefs.getLong("ShiftID",0);
        LoggedUserMediumID = prefs.getLong("MediumID",0);
        LoggedUserClassID = prefs.getLong("ClassID",0);
        if(UserTypeID == 3) {
            LoggedUserDepartmentID = prefs.getLong("SDepartmentID",0);
        } else {
            LoggedUserDepartmentID = prefs.getLong("DepartmentID",0);
        }
        LoggedUserSectionID = prefs.getLong("SectionID",0);
        LoggedUserSessionID = prefs.getLong("SessionID",0);

        expandableList = (ExpandableListView) findViewById(R.id.navigationmenu);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        notificationCounter = findViewById(R.id.notificationCounter);
        int i = getSharedPreferences("UNSEEN_NOTIFICATIONS", Context.MODE_PRIVATE)
                .getInt("unseen", 0);
        if(i != 0) {
            notificationCounter.setVisibility(View.VISIBLE);
            notificationCounter.setText(Integer.toString(i));
        } else {
            notificationCounter.setVisibility(View.INVISIBLE);
        }
        dashboard = findViewById(R.id.dashboard);
        profile = findViewById(R.id.profile);
        notification = findViewById(R.id.notification);
        contacts = findViewById(R.id.contacts);
        iconDashboard = findViewById(R.id.iconDashboard);
        iconProfile = findViewById(R.id.iconProfile);
        iconNotification = findViewById(R.id.iconNotification);
        iconContacts = findViewById(R.id.iconContact);
        textDashboard = findViewById(R.id.textDashboard);
        textProfile = findViewById(R.id.textProfile);
        textNotification = findViewById(R.id.textNotification);
        textContacts = findViewById(R.id.textContact);

        if(activityName.equalsIgnoreCase(Profile.class.getName())){
            profile.setBackgroundColor(Color.parseColor("#f9f7f7"));
            iconProfile.setColorFilter(Color.parseColor("#0550b7"));
            textProfile.setTextColor(Color.parseColor("#0550b7"));
        }

        if(activityName.equalsIgnoreCase(NotificationMainScreen.class.getName())){
            notification.setBackgroundColor(Color.parseColor("#f9f7f7"));
            iconNotification.setColorFilter(Color.parseColor("#0550b7"));
            textNotification.setTextColor(Color.parseColor("#0550b7"));
        }

        if(activityName.equalsIgnoreCase(ContactsMainScreen.class.getName())){
            contacts.setBackgroundColor(Color.parseColor("#f9f7f7"));
            iconContacts.setColorFilter(Color.parseColor("#0550b7"));
            textContacts.setTextColor(Color.parseColor("#0550b7"));
        }

        dashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshBottomNavBar();
                dashboard.setBackgroundColor(Color.parseColor("#f9f7f7"));
                iconDashboard.setColorFilter(Color.parseColor("#0550b7"));
                textDashboard.setTextColor(Color.parseColor("#0550b7"));

                if(UserTypeID == 1) {
                    Intent mainIntent = new Intent(getApplicationContext(),TeacherMainScreen.class);
                    startActivity(mainIntent);
                    finish();
                } else if(UserTypeID == 2) {
                    Intent mainIntent = new Intent(getApplicationContext(),TeacherMainScreen.class);
                    startActivity(mainIntent);
                    finish();
                } else if(UserTypeID == 3) {
                    Intent mainIntent = new Intent(getApplicationContext(),StudentMainScreen.class);
                    startActivity(mainIntent);
                    finish();
                } else if(UserTypeID == 4) {
                    Intent mainIntent = new Intent(getApplicationContext(),TeacherMainScreen.class);
                    startActivity(mainIntent);
                    finish();
                } else if(UserTypeID == 5) {
                    Intent mainIntent = new Intent(getApplicationContext(),StudentMainScreen.class);
                    startActivity(mainIntent);
                    finish();
                }
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshBottomNavBar();
                profile.setBackgroundColor(Color.parseColor("#f9f7f7"));
                iconProfile.setColorFilter(Color.parseColor("#0550b7"));
                textProfile.setTextColor(Color.parseColor("#0550b7"));
                if(!activityName.equalsIgnoreCase(Profile.class.getName())){
                    Intent mainIntent = new Intent(getApplicationContext(), Profile.class);
                    startActivity(mainIntent);
                    finish();
                }
            }
        });

        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshBottomNavBar();
                notification.setBackgroundColor(Color.parseColor("#f9f7f7"));
                iconNotification.setColorFilter(Color.parseColor("#0550b7"));
                textNotification.setTextColor(Color.parseColor("#0550b7"));
                if(!activityName.equalsIgnoreCase(NotificationMainScreen.class.getName())){
                    Intent mainIntent = new Intent(getApplicationContext(), NotificationMainScreen.class);
                    startActivity(mainIntent);
                    finish();
                }
            }
        });

        contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshBottomNavBar();
                contacts.setBackgroundColor(Color.parseColor("#f9f7f7"));
                iconContacts.setColorFilter(Color.parseColor("#0550b7"));
                textContacts.setTextColor(Color.parseColor("#0550b7"));
                if(!activityName.equalsIgnoreCase(ContactsMainScreen.class.getName())){
                    Intent mainIntent = new Intent(getApplicationContext(), ContactsMainScreen.class);
                    startActivity(mainIntent);
                    finish();
                }
            }
        });

//        bottomNavigationView = findViewById(R.id.navigation);
//        bottomNavigationView.getMenu().getItem(0).setCheckable(false);
//        bottomNavigationView.getMenu().getItem(1).setCheckable(false);
//        bottomNavigationView.getMenu().getItem(2).setCheckable(false);
//        bottomNavigationView.getMenu().getItem(3).setCheckable(false);
//        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                item.setCheckable(true);
//                bottomNavigationView.postDelayed(() -> {
//                    int itemId = item.getItemId();
//                    if (itemId == R.id.navigation_dashboard) {
//                        switch (UserTypeID) {
//                            case 1: startActivity(new Intent(SideNavigationMenuParentActivity.this, TeacherMainScreen.class));
//                                break;
//                            case 2: startActivity(new Intent(SideNavigationMenuParentActivity.this, TeacherMainScreen.class));
//                                break;
//                            case 3: startActivity(new Intent(SideNavigationMenuParentActivity.this, StudentMainScreen.class));
//                                break;
//                            case 4: startActivity(new Intent(SideNavigationMenuParentActivity.this, TeacherMainScreen.class));
//                                break;
//                            case 5: startActivity(new Intent(SideNavigationMenuParentActivity.this, StudentMainScreen.class));
//                                break;
//                        }
//                    } else if (itemId == R.id.navigation_profile) {
//
//                    } else if (itemId == R.id.navigation_notification) {
////                        startActivity(new Intent(this, NotificationsActivity.class));
//                    } else if(itemId == R.id.navigation_contact) {
//
//                    }
////                    finish();
//                }, 300);
//                return true;
//            }
//        });
//        BottomNavigationMenuView bottomNavigationMenuView = (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
//        View v = bottomNavigationMenuView.getChildAt(2);
//        BottomNavigationItemView itemView = (BottomNavigationItemView) v;
//        View badge = LayoutInflater.from(this).inflate(R.layout.notification_badge_container, bottomNavigationMenuView, false);
//        itemView.addView(badge);
//
//        TextView textView = itemView.findViewById(R.id.notificationCounter);
//        textView.setText("20");

        //
//        notificationMenuItem = menu.findItem(R.id.notificationCounterMenuId);
//        View actionView = notificationMenuItem.getActionView();
//        if(actionView!=null){
//            notificationBell = actionView.findViewById(R.id.notificationBell);
//            notificationCounter = actionView.findViewById(R.id.notificationCounter);
//            notificationBell.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SideNavigationMenuParentActivity.this);
//                    SharedPreferences.Editor editor = prefs.edit();
//                    editor.putInt("Notification", 0);
//                    editor.apply();
//                    notificationCounter.setText("");
//                    notificationCounter.setVisibility(View.INVISIBLE);
//                }
//            });
//
//            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
//            SharedPreferences.Editor editor = prefs.edit();
//            int number = prefs.getInt("Notification",0);
//            if(number>0){
//                notificationCounter.setVisibility(View.VISIBLE);
//                notificationCounter.setText(Integer.toString(number));
//            } else {
//                notificationCounter.setVisibility(View.INVISIBLE);
//            }
//        }

//        final ActionBar actionBar = getSupportActionBar();
//        actionBar.setHomeAsUpIndicator(android.R.drawable.ic_menu_add);
//        actionBar.setDisplayHomeAsUpEnabled(true);

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
            userType.setText("Staff");
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

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
                    displayFirebaseRegId();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SideNavigationMenuParentActivity.this);
                    SharedPreferences.Editor editor = prefs.edit();
                    int number = prefs.getInt("Notification",0);
//                    notificationCounter.setVisibility(View.VISIBLE);
//                    notificationCounter.setText(Integer.toString(++number));
                    editor.putInt("Notification", number);
                    editor.apply();
                    String message = intent.getStringExtra("notification");
                    try {
                        JSONObject messageJSONObject = new JSONObject(message);
                        if (activityName.contains("NotificationMainScreen")) {
                            notificationReceiverListener = (NotificationMainScreen)SideNavigationMenuParentActivity.this;
                            notificationReceiverListener.onNotificationReceived(messageJSONObject);
                        }
                        //Show the notification when app is in foreground
                        // notification icon
                        final int icon = R.drawable.onair;
                        Intent resultIntent = new Intent(getApplicationContext(), NotificationDetails.class);
                        resultIntent.putExtra("notification", message);
                        int requestCode = new Random().nextInt(900000)+100000;
                        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        PendingIntent resultPendingIntent = PendingIntent.getActivity(getApplicationContext(), requestCode, resultIntent, PendingIntent.FLAG_IMMUTABLE);
                        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), Config.NOTIFICATION_CHANNEL);
                        Notification notification = mBuilder.setSmallIcon(icon).setTicker(messageJSONObject.getString("title"))
                                .setAutoCancel(true)
                                .setContentTitle(messageJSONObject.getString("title"))
                                .setContentIntent(resultPendingIntent)
                                .setWhen(System.currentTimeMillis())
                                .setStyle(new NotificationCompat.BigTextStyle()
                                        .bigText(messageJSONObject.getString("body")))
                                .setContentText(messageJSONObject.getString("body"))
                                .setPriority(NotificationCompat.PRIORITY_MAX)
                                .build();
                        int uniqueNotificationId = new Random().nextInt(900000)+100000;
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                        notificationManager.notify(uniqueNotificationId, notification);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    int i = getSharedPreferences("UNSEEN_NOTIFICATIONS", Context.MODE_PRIVATE)
                            .getInt("unseen", 0);
                    if(i != 0) {
                        notificationCounter.setVisibility(View.VISIBLE);
                        notificationCounter.setText(Integer.toString(i));
                    } else {
                        notificationCounter.setVisibility(View.INVISIBLE);
                    }
                }
            }
        };
    }

    private void refreshBottomNavBar(){
        dashboard.setBackgroundColor(Color.WHITE);
        iconDashboard.setColorFilter(Color.parseColor("#757575"));
        textDashboard.setTextColor(Color.parseColor("#757575"));

        profile.setBackgroundColor(Color.WHITE);
        iconProfile.setColorFilter(Color.parseColor("#757575"));
        textProfile.setTextColor(Color.parseColor("#757575"));

        notification.setBackgroundColor(Color.WHITE);
        iconNotification.setColorFilter(Color.parseColor("#757575"));
        textNotification.setTextColor(Color.parseColor("#757575"));

        contacts.setBackgroundColor(Color.WHITE);
        iconContacts.setColorFilter(Color.parseColor("#757575"));
        textContacts.setTextColor(Color.parseColor("#757575"));

    }

    // Fetches reg id from shared preferences
    // and displays on the screen
    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);

        Log.e(SideNavigationMenuParentActivity.class.getSimpleName(), "Firebase reg id: " + regId);

        if (!TextUtils.isEmpty(regId))
            Toast.makeText(getApplicationContext(), "Firebase Reg Id: " + regId, Toast.LENGTH_LONG).show();
        else
        Toast.makeText(getApplicationContext(), "Firebase Reg Id is not received yet!", Toast.LENGTH_LONG).show();
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
        switch (UserTypeID) {
            case 1: getMenuInflater().inflate(R.menu.main, menu);
                break;

            case 2: getMenuInflater().inflate(R.menu.main, menu);
                break;

            case 3: getMenuInflater().inflate(R.menu.main, menu);
                break;

            case 4: getMenuInflater().inflate(R.menu.main, menu);
                break;

            case 5: getMenuInflater().inflate(R.menu.main, menu);
                menu.add(0, MENU_ITEM_CHANGE_STUDENT, 3, "Change Student")
                        .setIcon(R.drawable.nav_attendance)
                        .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
                break;
        }
//
//        notificationMenuItem = menu.findItem(R.id.notificationCounterMenuId);
//        View actionView = notificationMenuItem.getActionView();
//        if(actionView!=null){
//            notificationBell = actionView.findViewById(R.id.notificationBell);
//            notificationCounter = actionView.findViewById(R.id.notificationCounter);
//            notificationBell.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SideNavigationMenuParentActivity.this);
//                    SharedPreferences.Editor editor = prefs.edit();
//                    editor.putInt("Notification", 0);
//                    editor.apply();
//                    notificationCounter.setText("");
//                    notificationCounter.setVisibility(View.INVISIBLE);
//                }
//            });
//
//            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
//            SharedPreferences.Editor editor = prefs.edit();
//            int number = prefs.getInt("Notification",0);
//            if(number>0){
//                notificationCounter.setVisibility(View.VISIBLE);
//                notificationCounter.setText(Integer.toString(number));
//            } else {
//                notificationCounter.setVisibility(View.INVISIBLE);
//            }
//        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        switch (UserTypeID) {
            case 1:
                break;

            case 2:
                break;

            case 3:
                break;

            case 4:
                break;

            case 5:
                break;
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (UserTypeID) {
            case 1:
                break;

            case 2:
                break;

            case 3:
                break;

            case 4:
                break;

            case 5:
                if(id == MENU_ITEM_CHANGE_STUDENT) {
                    String string = getSharedPreferences("GUARDIAN_STUDENTS", Context.MODE_PRIVATE)
                            .getString("guardianStudentList", "[]");
                    try {
                        if(!string.equalsIgnoreCase("")&&!string.equalsIgnoreCase("[]")) {
                            JSONArray studentList = new JSONArray(string);
                            GuardianStudentSelectionDialog selectStudent = new GuardianStudentSelectionDialog(this, studentList, this);
                            selectStudent.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            selectStudent.setCancelable(false);
                            selectStudent.show();
                        } else {
                            Toast.makeText(this,"No student found!!! ",
                                    Toast.LENGTH_LONG).show();
                        }
                        return true;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            return logOut();
        } else if(id == R.id.changePassword) {
            ChangePasswordDialog changePasswordDialog = new ChangePasswordDialog(this, this, LoggedUserID, UserName, InstituteID);
            changePasswordDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            changePasswordDialog.setCancelable(false);
            changePasswordDialog.show();
        } else if(id == R.id.changeUserType) {
            ChangeUserTypeDialog changeUserTypeDialog = new ChangeUserTypeDialog(this, this);
            changeUserTypeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            changeUserTypeDialog.setCancelable(false);
            changeUserTypeDialog.show();
        }

        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean logOut() {
        if (StaticHelperClass.isNetworkAvailable(this)) {

            String logOutUrl = getString(R.string.baseUrl)+"/api/onEms/deleteFcmToken/"+LoggedUserID+"/"+"android"+"/"+getSharedPreferences("UNIQUE_ID", Context.MODE_PRIVATE)
                    .getString("uuid", "");

            dialog = new ProgressDialog(this);
            dialog.setTitle("Logging Out...");
            dialog.setMessage("Please Wait...");
            dialog.setCancelable(false);
            dialog.setIcon(R.drawable.onair);
            dialog.show();
            //Preparing Shift data from server
            StringRequest loginRequest = new StringRequest(Request.Method.DELETE, logOutUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            doLogOut(response);

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    returnValue = false;
                    dialog.dismiss();
                    Toast.makeText(SideNavigationMenuParentActivity.this,"Server error while logging out!!! ",
                            Toast.LENGTH_LONG).show();
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
            MySingleton.getInstance(this).addToRequestQueue(loginRequest);
        } else {
            Toast.makeText(SideNavigationMenuParentActivity.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
        return returnValue;
    }

    private void doLogOut(String returnValueFromServer) {
        try {
            if (new JSONArray(returnValueFromServer).getJSONObject(0).getInt("ReturnValue") == 1) {
                SharedPreferences sharedPreferences  = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("LogInState", false);
                editor.apply();

                getSharedPreferences("PUSH_NOTIFICATIONS", Context.MODE_PRIVATE)
                        .edit()
                        .putString("notifications", "[]")
                        .apply();

                returnValue = true;
                Intent intent = new Intent(getApplicationContext(), LoginScreen.class);
                startActivity(intent);
                finish();
            } else {
                returnValue = false;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
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

        ExpandedMenuModel menuHomework = new ExpandedMenuModel();
        menuHomework.setIconName("Homework");
        menuHomework.setIconImg(R.drawable.nav_homework);
        listDataHeader.add(menuHomework);

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

        ExpandedMenuModel menuReport = new ExpandedMenuModel();
        menuReport.setIconName("Reports");
        menuReport.setIconImg(R.drawable.nav_fee);
        listDataHeader.add(menuReport);

        ExpandedMenuModel menuCrm = new ExpandedMenuModel();
        menuCrm.setIconName("CRM");
        menuCrm.setIconImg(R.drawable.ic_person_add);
        listDataHeader.add(menuCrm);

        // Adding child data
        List<String> headingDashboard = new ArrayList<>();
        List<String> headingNotice = new ArrayList<>();

        List<String> headingRoutine = new ArrayList<>();
        headingRoutine.add("Class Routine");
        headingRoutine.add("Exam Routine");

        List<String> headingAttendance = new ArrayList<>();
        headingAttendance.add("Total Report");
        headingAttendance.add("Take Attendance");
        headingAttendance.add("Show Attendance");

        List<String> headingSyllabus = new ArrayList<>();

        List<String> headingHomework = new ArrayList<>();
        headingHomework.add("Homework");
        headingHomework.add("Lesson Plan");

        List<String> headingExam = new ArrayList<>();
        headingExam.add("Subject Wise Marks Entry");

        List<String> headingResult = new ArrayList<>();
        headingResult.add("Show Result");
        headingResult.add("Grading System");

        List<String> headingContact = new ArrayList<>();

        List<String> headingiCard = new ArrayList<>();
        headingiCard.add("Find Student");
        headingiCard.add("Entry Student");

        List<String> headingStudentList = new ArrayList<>();

        List<String> headingReport = new ArrayList<>();
        headingReport.add("Fees Collection Report");
        headingReport.add("Fees Summary Report");

        List<String> headingCRM = new ArrayList<>();

        // Header, Child data
        listDataChild.put(listDataHeader.get(0), headingDashboard);
        listDataChild.put(listDataHeader.get(1), headingNotice);
        listDataChild.put(listDataHeader.get(2), headingRoutine);
        listDataChild.put(listDataHeader.get(3), headingAttendance);
        listDataChild.put(listDataHeader.get(4), headingSyllabus);
        listDataChild.put(listDataHeader.get(5), headingHomework);
        listDataChild.put(listDataHeader.get(6), headingExam);
        listDataChild.put(listDataHeader.get(7), headingResult);
        listDataChild.put(listDataHeader.get(8), headingContact);
        listDataChild.put(listDataHeader.get(9), headingiCard);
        listDataChild.put(listDataHeader.get(10), headingStudentList);
        listDataChild.put(listDataHeader.get(11), headingReport);
        listDataChild.put(listDataHeader.get(12), headingCRM);

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

                if((i == 1) && (l == 1)) {
                    if(activityName.equals(NoticeMainScreen.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), NoticeMainScreen.class);
                        startActivity(intent);
                        finish();
                    }
                }

                if((i == 4) && (l == 4)) {
                    if(activityName.equals(SyllabusMainScreenForAdmin.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), SyllabusMainScreenForAdmin.class);
                        startActivity(intent);
                        finish();
                    }
                }

                if((i == 10) && (l == 10)) {
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

                if((i == 12) && (l == 12)) {
                    if(activityName.equals(NewClientEntry.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), NewClientEntry.class);
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

                if((i == 2) && (i1 == 0) && (l == 0)) {
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

                if((i == 2) && (i1 == 1) && (l == 1)) {
                    if(activityName.equals(ExamRoutineMainScreen.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), ExamRoutineMainScreen.class);
                        startActivity(intent);
                        finish();
                    }
                }

                if((i == 3) && (i1 == 0) && (l == 0)) {
                    if(activityName.equals(AttendanceAdminDashboard.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), AttendanceAdminDashboard.class);
                        startActivity(intent);
                        finish();
                    }
                }

                if((i == 3) && (i1 == 1) && (l == 1)) {
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

                if((i == 3) && (i1 == 2) && (l == 2)) {
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

                if((i == 5) && (i1 == 0) && (l == 0)) {
                    if(activityName.equals(HomeworkMainScreenForAdmin.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), HomeworkMainScreenForAdmin.class);
                        startActivity(intent);
                        finish();
                    }
                }

                if((i == 5) && (i1 == 1) && (l == 1)) {
                    if(activityName.equals(LessonPlanMainScreenForAdmin.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), LessonPlanMainScreenForAdmin.class);
                        startActivity(intent);
                        finish();
                    }
                }

                if((i == 6) && (i1 == 0) && (l == 0)) {
                    if(activityName.equals(SubjectWiseMarksEntryMain.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), SubjectWiseMarksEntryMain.class);
                        startActivity(intent);
                        finish();
                    }
                }

                if((i == 7) && (i1 == 0) && (l == 0)) {
                    if(activityName.equals(ResultMainScreen.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), ResultMainScreen.class);
                        startActivity(intent);
                        finish();
                    }
                }

                if((i == 7) && (i1 == 1) && (l == 1)) {
                    if(activityName.equals(ResultGradeStructure.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), ResultGradeStructure.class);
                        startActivity(intent);
                        finish();
                    }
                }

                if((i == 9) && (i1 == 0) && (l == 0)) {
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

                if((i == 9) && (i1 == 1) && (l == 1)) {
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

                if((i == 11) && (i1 == 0) && (l == 0)) {
                    if(activityName.equals(FeeCollectionReportMain.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), FeeCollectionReportMain.class);
                        intent.putExtra("Report Type", 1);
                        startActivity(intent);
                        finish();
                    }
                }

                if((i == 11) && (i1 == 1) && (l == 1)) {
                    if(activityName.equals(FeeCollectionReportMain.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), FeeCollectionReportMain.class);
                        intent.putExtra("Report Type", 2);
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

        ExpandedMenuModel menuHomework = new ExpandedMenuModel();
        menuHomework.setIconName("Homework");
        menuHomework.setIconImg(R.drawable.nav_homework);
        listDataHeader.add(menuHomework);

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

        ExpandedMenuModel menuReport = new ExpandedMenuModel();
        menuReport.setIconName("Reports");
        menuReport.setIconImg(R.drawable.nav_fee);
        listDataHeader.add(menuReport);

        ExpandedMenuModel menuCrm = new ExpandedMenuModel();
        menuCrm.setIconName("CRM");
        menuCrm.setIconImg(R.drawable.ic_person_add);
        listDataHeader.add(menuCrm);

        // Adding child data
        List<String> headingDashboard = new ArrayList<>();
        List<String> headingNotice = new ArrayList<>();
        List<String> headingRoutine = new ArrayList<>();
        headingRoutine.add("Class Routine");
        headingRoutine.add("Exam Routine");

        List<String> headingAttendance = new ArrayList<>();
        headingAttendance.add("Total Report");
        headingAttendance.add("Take Attendance");
        headingAttendance.add("Show Attendance");

        List<String> headingSyllabus = new ArrayList<>();

        List<String> headingHomework = new ArrayList<>();
        headingHomework.add("Homework");
        headingHomework.add("Lesson Plan");

        List<String> headingExam = new ArrayList<>();
        headingExam.add("Subject Wise Marks Entry");

        List<String> headingResult = new ArrayList<>();
        headingResult.add("Show Result");
        headingResult.add("Grading System");

        List<String> headingContact = new ArrayList<>();

        List<String> headingiCard = new ArrayList<>();
        headingiCard.add("Find Student");
        headingiCard.add("Entry Student");

        List<String> headingStudentList = new ArrayList<>();

        List<String> headingReport = new ArrayList<>();
        headingReport.add("Fees Collection Report");
        headingReport.add("Fees Summary Report");

        List<String> headingCRM = new ArrayList<>();

        // Header, Child data
        listDataChild.put(listDataHeader.get(0), headingDashboard);
        listDataChild.put(listDataHeader.get(1), headingNotice);
        listDataChild.put(listDataHeader.get(2), headingRoutine);
        listDataChild.put(listDataHeader.get(3), headingAttendance);
        listDataChild.put(listDataHeader.get(4), headingSyllabus);
        listDataChild.put(listDataHeader.get(5), headingHomework);
        listDataChild.put(listDataHeader.get(6), headingExam);
        listDataChild.put(listDataHeader.get(7), headingResult);
        listDataChild.put(listDataHeader.get(8), headingContact);
        listDataChild.put(listDataHeader.get(9), headingiCard);
        listDataChild.put(listDataHeader.get(10), headingStudentList);
        listDataChild.put(listDataHeader.get(11), headingReport);
        listDataChild.put(listDataHeader.get(12), headingCRM);

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

                if((i == 1) && (l == 1)) {
                    if(activityName.equals(NoticeMainScreen.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), NoticeMainScreen.class);
                        startActivity(intent);
                        finish();
                    }
                }

                if((i == 4) && (l == 4)) {
                    if(activityName.equals(SyllabusMainScreenForAdmin.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), SyllabusMainScreenForAdmin.class);
                        startActivity(intent);
                        finish();
                    }
                }

                if((i == 10) && (l == 10)) {
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

                if((i == 12) && (l == 12)) {
                    if(activityName.equals(NewClientEntry.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), NewClientEntry.class);
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

                if((i == 2) && (i1 == 0) && (l == 0)) {
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

                if((i == 2) && (i1 == 1) && (l == 1)) {
                    if(activityName.equals(ExamRoutineMainScreen.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), ExamRoutineMainScreen.class);
                        startActivity(intent);
                        finish();
                    }
                }

                if((i == 3) && (i1 == 0) && (l == 0)) {
                    if(activityName.equals(AttendanceAdminDashboard.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), AttendanceAdminDashboard.class);
                        startActivity(intent);
                        finish();
                    }
                }

                if((i == 3) && (i1 == 1) && (l == 1)) {
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

                if((i == 3) && (i1 == 2) && (l == 2)) {
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

                if((i == 5) && (i1 == 0) && (l == 0)) {
                    if(activityName.equals(HomeworkMainScreenForAdmin.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), HomeworkMainScreenForAdmin.class);
                        startActivity(intent);
                        finish();
                    }
                }

                if((i == 5) && (i1 == 1) && (l == 1)) {
                    if(activityName.equals(LessonPlanMainScreenForAdmin.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), LessonPlanMainScreenForAdmin.class);
                        startActivity(intent);
                        finish();
                    }
                }

                if((i == 6) && (i1 == 0) && (l == 0)) {
                    if(activityName.equals(SubjectWiseMarksEntryMain.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), SubjectWiseMarksEntryMain.class);
                        startActivity(intent);
                        finish();
                    }
                }

                if((i == 7) && (i1 == 0) && (l == 0)) {
                    if(activityName.equals(ResultMainScreen.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), ResultMainScreen.class);
                        startActivity(intent);
                        finish();
                    }
                }

                if((i == 7) && (i1 == 1) && (l == 1)) {
                    if(activityName.equals(ResultGradeStructure.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), ResultGradeStructure.class);
                        startActivity(intent);
                        finish();
                    }
                }

                if((i == 9) && (i1 == 0) && (l == 0)) {
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

                if((i == 9) && (i1 == 1) && (l == 1)) {
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

                if((i == 11) && (i1 == 0) && (l == 0)) {
                    if(activityName.equals(FeeCollectionReportMain.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                            Intent intent = new Intent(getApplicationContext(), FeeCollectionReportMain.class);
                            intent.putExtra("Report Type", 1);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), FeeCollectionReportMain.class);
                        intent.putExtra("Report Type", 1);
                        startActivity(intent);
                        finish();
                    }
                }

                if((i == 11) && (i1 == 1) && (l == 1)) {
                    if(activityName.equals(FeeCollectionReportMain.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                            Intent intent = new Intent(getApplicationContext(), FeeCollectionReportMain.class);
                            intent.putExtra("Report Type", 2);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), FeeCollectionReportMain.class);
                        intent.putExtra("Report Type", 2);
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

        ExpandedMenuModel menuHomework = new ExpandedMenuModel();
        menuHomework.setIconName("Homework");
        menuHomework.setIconImg(R.drawable.nav_homework);
        listDataHeader.add(menuHomework);

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

        ExpandedMenuModel menuReport = new ExpandedMenuModel();
        menuReport.setIconName("Reports");
        menuReport.setIconImg(R.drawable.nav_fee);
        listDataHeader.add(menuReport);

        // Adding child data
        List<String> headingDashboard = new ArrayList<>();
        List<String> headingNotice = new ArrayList<>();
        List<String> headingRoutine = new ArrayList<>();
        headingRoutine.add("Class Routine");
        headingRoutine.add("Exam Routine");

        List<String> headingAttendance = new ArrayList<>();
        headingAttendance.add("Take Attendance");
        headingAttendance.add("Show Attendance");

        List<String> headingSyllabus = new ArrayList<>();

        List<String> headingHomework = new ArrayList<>();
        headingHomework.add("Homework");
        headingHomework.add("Lesson Plan");

        List<String> headingExam = new ArrayList<>();
        headingExam.add("Subject Wise Marks Entry");

        List<String> headingResult = new ArrayList<>();
        headingResult.add("Show Result");
        headingResult.add("Grading System");

        List<String> headingContact = new ArrayList<>();

        List<String> headingiCard = new ArrayList<>();
        headingiCard.add("Find Student");
        headingiCard.add("Entry Student");

        List<String> headingStudentList = new ArrayList<>();

        List<String> headingReport = new ArrayList<>();
        headingReport.add("Fees Collection Report");
        headingReport.add("Fees Summary Report");

        // Header, Child data
        listDataChild.put(listDataHeader.get(0), headingDashboard);
        listDataChild.put(listDataHeader.get(1), headingNotice);
        listDataChild.put(listDataHeader.get(2), headingRoutine);
        listDataChild.put(listDataHeader.get(3), headingAttendance);
        listDataChild.put(listDataHeader.get(4), headingSyllabus);
        listDataChild.put(listDataHeader.get(5), headingHomework);
        listDataChild.put(listDataHeader.get(6), headingExam);
        listDataChild.put(listDataHeader.get(7), headingResult);
        listDataChild.put(listDataHeader.get(8), headingContact);
        listDataChild.put(listDataHeader.get(9), headingiCard);
        listDataChild.put(listDataHeader.get(10), headingStudentList);
        listDataChild.put(listDataHeader.get(11), headingReport);

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

                if((i == 1) && (l == 1)) {
                    if(activityName.equals(NoticeMainScreen.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), NoticeMainScreen.class);
                        startActivity(intent);
                        finish();
                    }
                }

                if((i == 4) && (l == 4)) {
                    if(activityName.equals(SyllabusMainScreenForAdmin.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), SyllabusMainScreenForAdmin.class);
                        startActivity(intent);
                        finish();
                    }
                }

                if((i == 10) && (l == 10)) {
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

                if((i == 2) && (i1 == 0) && (l == 0)) {
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

                if((i == 2) && (i1 == 1) && (l == 1)) {
                    if(activityName.equals(ExamRoutineMainScreen.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), ExamRoutineMainScreen.class);
                        startActivity(intent);
                        finish();
                    }
                }

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

                if((i == 5) && (i1 == 0) && (l == 0)) {
                    if(activityName.equals(HomeworkMainScreenForAdmin.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), HomeworkMainScreenForAdmin.class);
                        startActivity(intent);
                        finish();
                    }
                }

                if((i == 5) && (i1 == 1) && (l == 1)) {
                    if(activityName.equals(LessonPlanMainScreenForAdmin.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), LessonPlanMainScreenForAdmin.class);
                        startActivity(intent);
                        finish();
                    }
                }

                if((i == 6) && (i1 == 0) && (l == 0)) {
                    if(activityName.equals(SubjectWiseMarksEntryMain.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), SubjectWiseMarksEntryMain.class);
                        startActivity(intent);
                        finish();
                    }
                }

                if((i == 7) && (i1 == 0) && (l == 0)) {
                    if(activityName.equals(ResultMainScreen.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), ResultMainScreen.class);
                        startActivity(intent);
                        finish();
                    }
                }

                if((i == 7) && (i1 == 1) && (l == 1)) {
                    if(activityName.equals(ResultGradeStructure.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), ResultGradeStructure.class);
                        startActivity(intent);
                        finish();
                    }
                }

                if((i == 9) && (i1 == 0) && (l == 0)) {
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

                if((i == 9) && (i1 == 1) && (l == 1)) {
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

                if((i == 11) && (i1 == 0) && (l == 0)) {
                    if(activityName.equals(FeeCollectionReportMain.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), FeeCollectionReportMain.class);
                        intent.putExtra("Report Type", 1);
                        startActivity(intent);
                        finish();
                    }
                }

                if((i == 11) && (i1 == 1) && (l == 1)) {
                    if(activityName.equals(FeeCollectionReportMain.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), FeeCollectionReportMain.class);
                        intent.putExtra("Report Type", 2);
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

        ExpandedMenuModel menuHomework = new ExpandedMenuModel();
        menuHomework.setIconName("Homework");
        menuHomework.setIconImg(R.drawable.nav_homework);
        listDataHeader.add(menuHomework);

        ExpandedMenuModel menuResult = new ExpandedMenuModel();
        menuResult.setIconName("Result");
        menuResult.setIconImg(R.drawable.nav_result);
        listDataHeader.add(menuResult);

        ExpandedMenuModel menuFee = new ExpandedMenuModel();
        menuFee.setIconName("fee");
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
        headingRoutine.add("Class Routine");
        headingRoutine.add("Exam Routine");

        List<String> headingAttendance = new ArrayList<>();
        List<String> headingSyllabus = new ArrayList<>();

        List<String> headingHomework = new ArrayList<>();
        headingHomework.add("Homework");
        headingHomework.add("Lesson Plan");

        List<String> headingResult = new ArrayList<>();
        headingResult.add("Show Result");
        headingResult.add("Grading System");

        List<String> headingFee = new ArrayList<>();
        headingFee.add("Fee Payment");
        headingFee.add("Fee History");

        List<String> headingContact = new ArrayList<>();

        // Header, Child data
        listDataChild.put(listDataHeader.get(0), headingDashboard);
        listDataChild.put(listDataHeader.get(1), headingNotice);
        listDataChild.put(listDataHeader.get(2), headingRoutine);
        listDataChild.put(listDataHeader.get(3), headingAttendance);
        listDataChild.put(listDataHeader.get(4), headingSyllabus);
        listDataChild.put(listDataHeader.get(5), headingHomework);
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

                if((i == 1) && (l == 1)) {
                    if(activityName.equals(NoticeMainScreen.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), NoticeMainScreen.class);
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

                if((i == 4) && (l == 4)) {
                    if(activityName.equals(SyllabusMainScreen.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), SyllabusMainScreen.class);
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

                if((i == 2) && (i1 == 0) && (l == 0)) {
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

                if((i == 2) && (i1 == 1) && (l == 1)) {
                    if(activityName.equals(ExamRoutineMainScreen.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), ExamRoutineMainScreen.class);
                        startActivity(intent);
                        finish();
                    }
                }

                if((i == 5) && (i1 == 0) && (l == 0)) {
                    if(activityName.equals(HomeworkMainScreen.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), HomeworkMainScreen.class);
                        startActivity(intent);
                        finish();
                    }
                }

                if((i == 5) && (i1 == 1) && (l == 1)) {
                    if(activityName.equals(LessonPlanMainScreen.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), LessonPlanMainScreen.class);
                        startActivity(intent);
                        finish();
                    }
                }

                if((i == 6) && (i1 == 0) && (l == 0)) {
                    if(activityName.equals(ResultMainScreen.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), ResultMainScreen.class);
                        startActivity(intent);
                        finish();
                    }
                }

                if((i == 6) && (i1 == 1) && (l == 1)) {
                    if(activityName.equals(ResultGradeStructure.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), ResultGradeStructure.class);
                        startActivity(intent);
                        finish();
                    }
                }

                if((i == 7) && (i1 == 0) && (l == 0)) {
                    if(activityName.equals(FeeMainScreen.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), FeeMainScreen.class);
                        startActivity(intent);
                        finish();
                    }
                }

                if((i == 7) && (i1 == 1) && (l == 1)) {
                    if(activityName.equals(FeesHistory.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), FeesHistory.class);
                        startActivity(intent);
                        finish();
                    }
                }
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

        ExpandedMenuModel menuHomework = new ExpandedMenuModel();
        menuHomework.setIconName("Homework");
        menuHomework.setIconImg(R.drawable.nav_homework);
        listDataHeader.add(menuHomework);

        ExpandedMenuModel menuResult = new ExpandedMenuModel();
        menuResult.setIconName("Result");
        menuResult.setIconImg(R.drawable.nav_result);
        listDataHeader.add(menuResult);

        ExpandedMenuModel menuFee = new ExpandedMenuModel();
        menuFee.setIconName("fee");
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
        headingRoutine.add("Class Routine");
        headingRoutine.add("Exam Routine");

        List<String> headingAttendance = new ArrayList<>();
        List<String> headingSyllabus = new ArrayList<>();

        List<String> headingHomework = new ArrayList<>();
        headingHomework.add("Homework");
        headingHomework.add("Lesson Plan");

        List<String> headingResult = new ArrayList<>();
        headingResult.add("Show Result");
        headingResult.add("Grading System");

        List<String> headingFee = new ArrayList<>();
        headingFee.add("Fee Payment");
        headingFee.add("Fee History");

        List<String> headingContact = new ArrayList<>();

        // Header, Child data
        listDataChild.put(listDataHeader.get(0), headingDashboard);
        listDataChild.put(listDataHeader.get(1), headingNotice);
        listDataChild.put(listDataHeader.get(2), headingRoutine);
        listDataChild.put(listDataHeader.get(3), headingAttendance);
        listDataChild.put(listDataHeader.get(4), headingSyllabus);
        listDataChild.put(listDataHeader.get(5), headingHomework);
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

                if((i == 1) && (l == 1)) {
                    if(activityName.equals(NoticeMainScreen.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), NoticeMainScreen.class);
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

                if((i == 4) && (l == 4)) {
                    if(activityName.equals(SyllabusMainScreen.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), SyllabusMainScreen.class);
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

                if((i == 2) && (i1 == 0) && (l == 0)) {
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

                if((i == 2) && (i1 == 1) && (l == 1)) {
                    if(activityName.equals(ExamRoutineMainScreen.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), ExamRoutineMainScreen.class);
                        startActivity(intent);
                        finish();
                    }
                }

                if((i == 5) && (i1 == 0) && (l == 0)) {
                    if(activityName.equals(HomeworkMainScreen.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), HomeworkMainScreen.class);
                        startActivity(intent);
                        finish();
                    }
                }

                if((i == 5) && (i1 == 1) && (l == 1)) {
                    if(activityName.equals(LessonPlanMainScreen.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), LessonPlanMainScreen.class);
                        startActivity(intent);
                        finish();
                    }
                }

                if((i == 6) && (i1 == 0) && (l == 0)) {
                    if(activityName.equals(ResultMainScreen.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), ResultMainScreen.class);
                        startActivity(intent);
                        finish();
                    }
                }

                if((i == 6) && (i1 == 1) && (l == 1)) {
                    if(activityName.equals(ResultGradeStructure.class.getName())){
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                    } else {
                        Intent intent = new Intent(getApplicationContext(), ResultGradeStructure.class);
                        startActivity(intent);
                        finish();
                    }
                }

                if((i == 7) && (i1 == 0) && (l == 0)) {
//                    if(activityName.equals(FeeMainScreen.class.getName())){
//                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//                        if (drawer.isDrawerOpen(GravityCompat.START)) {
//                            drawer.closeDrawer(GravityCompat.START);
//                        }
//                    } else {
//                        Intent intent = new Intent(getApplicationContext(), FeeMainScreen.class);
//                        startActivity(intent);
//                        finish();
//                    }
                }

                if((i == 7) && (i1 == 1) && (l == 1)) {
//                    if(activityName.equals(FeesHistory.class.getName())){
//                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//                        if (drawer.isDrawerOpen(GravityCompat.START)) {
//                            drawer.closeDrawer(GravityCompat.START);
//                        }
//                    } else {
//                        Intent intent = new Intent(getApplicationContext(), FeesHistory.class);
//                        startActivity(intent);
//                        finish();
//                    }
                }
                return false;
            }
        });
    }

    @Override
    public void onNotificationDeleted(int i) {
        if(i != 0) {
            notificationCounter.setVisibility(View.VISIBLE);
            notificationCounter.setText(Integer.toString(i));
        } else {
            notificationCounter.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onNotificationUndo(int i) {
        if(i != 0) {
            notificationCounter.setVisibility(View.VISIBLE);
            notificationCounter.setText(Integer.toString(i));
        } else {
            notificationCounter.setVisibility(View.INVISIBLE);
        }
    }

    public interface NotificationReceiverListener {
        void onNotificationReceived(JSONObject jsonObject);
    }
}
