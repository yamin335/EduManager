package onair.onems.mainactivities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Objects;
import onair.onems.R;
import onair.onems.attendance.AttendanceAdminDashboard;
import onair.onems.exam.SubjectWiseMarksEntryMain;
import onair.onems.homework.HomeworkMainScreenForAdmin;
import onair.onems.login.ChangePasswordDialog;
import onair.onems.login.LoginScreen;
import onair.onems.notice.NoticeMainScreen;
import onair.onems.result.ResultMainScreen;
import onair.onems.routine.RoutineMainScreen;
import onair.onems.attendance.TakeAttendance;
import onair.onems.icard.StudentiCardMain;
import onair.onems.syllabus.SyllabusMainScreenForAdmin;

import static onair.onems.login.LoginScreen.MyPREFERENCES;

public class TeacherMainScreen extends SideNavigationMenuParentActivity {

    private FloatingActionButton fabMenu, fabLogout, fabChangePassword, fabChangeUserType;
    private CardView cardLogout, cardChangePassword, cardChangeUserType;
    private Animation fab_open, fab_close, rotate_forward, rotate_backward;
    private Boolean isFabOpen = false;
    private long InstituteID;
    private String UserName, LoggedUserID;
    private View dimView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        activityName = TeacherMainScreen.class.getName();
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View childActivityLayout = Objects.requireNonNull(inflater).inflate(R.layout.dashboard_teacher, null);
        LinearLayout parentActivityLayout = findViewById(R.id.contentMain);
        parentActivityLayout.addView(childActivityLayout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        InstituteID = prefs.getLong("InstituteID",0);
        LoggedUserID = prefs.getString("UserID", "0");
        UserName = prefs.getString("UserName", "0");
        dimView = findViewById(R.id.dim);
        dimView.setOnClickListener(v -> {
            if(isFabOpen){
                animateFAB();
            }
        });
        initializeFabAnimations();
        fabMenu = findViewById(R.id.floatingMenu);
//        fabMenu.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#5f27cd")));
        fabMenu.setRippleColor(Color.parseColor("#341f97"));
        fabMenu.setOnClickListener(v -> animateFAB());

        fabLogout = findViewById(R.id.log_out);
//        fabLogout.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#5f27cd")));
        fabLogout.setRippleColor(Color.parseColor("#341f97"));
        fabLogout.setOnClickListener(v -> doLogOut());

        fabChangePassword = findViewById(R.id.change_password);
//        fabChangePassword.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#5f27cd")));
        fabChangePassword.setRippleColor(Color.parseColor("#341f97"));
        fabChangePassword.setOnClickListener(v -> {
            ChangePasswordDialog changePasswordDialog = new ChangePasswordDialog(TeacherMainScreen.this,
                    TeacherMainScreen.this, LoggedUserID, UserName, InstituteID);
            Objects.requireNonNull(changePasswordDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            changePasswordDialog.setCancelable(false);
            changePasswordDialog.show();
        });

        fabChangeUserType = findViewById(R.id.change_user_type);
//        fabChangeUserType.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#5f27cd")));
        fabChangeUserType.setRippleColor(Color.parseColor("#341f97"));
        fabChangeUserType.setOnClickListener(v -> {
            ChangeUserTypeDialog changeUserTypeDialog = new ChangeUserTypeDialog(TeacherMainScreen.this,
                    TeacherMainScreen.this);
            Objects.requireNonNull(changeUserTypeDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            changeUserTypeDialog.setCancelable(false);
            changeUserTypeDialog.show();
        });

        cardLogout = findViewById(R.id.card_log_out);
        cardLogout.setOnClickListener(v -> {
            SharedPreferences sharedPreferences  = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("LogInState", false);
            editor.apply();
            Intent intent = new Intent(getApplicationContext(), LoginScreen.class);
            startActivity(intent);
            finish();
        });

        cardChangePassword = findViewById(R.id.card_change_password);
        cardChangePassword.setOnClickListener(v -> {
            ChangePasswordDialog changePasswordDialog = new ChangePasswordDialog(TeacherMainScreen.this,
                    TeacherMainScreen.this, LoggedUserID, UserName, InstituteID);
            Objects.requireNonNull(changePasswordDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            changePasswordDialog.setCancelable(false);
            changePasswordDialog.show();
        });

        cardChangeUserType = findViewById(R.id.card_change_user_type);
        cardChangeUserType.setOnClickListener(v -> {
            ChangeUserTypeDialog changeUserTypeDialog = new ChangeUserTypeDialog(TeacherMainScreen.this,
                    TeacherMainScreen.this);
            Objects.requireNonNull(changeUserTypeDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            changeUserTypeDialog.setCancelable(false);
            changeUserTypeDialog.show();
        });

        CardView attendance = findViewById(R.id.attendance);
        CardView iCard = findViewById(R.id.iCard);
        CardView notice = findViewById(R.id.notice);
        TextView InstituteName = (TextView) findViewById(R.id.InstituteName);
        TextView userType = (TextView) findViewById(R.id.userType);
        CardView result = findViewById(R.id.result);
        CardView exam = findViewById(R.id.exam);
        CardView routine = findViewById(R.id.routine);
        CardView homework = findViewById(R.id.homework);
        CardView syllabus = findViewById(R.id.syllabus);

        SharedPreferences sharedPre = PreferenceManager.getDefaultSharedPreferences(this);
        String InstituteNameString = sharedPre.getString("InstituteName","");
        InstituteName.setText(InstituteNameString);
        final int user = sharedPre.getInt("UserTypeID",0);
        if(user == 4) {
            userType.setText(R.string.staff);
        } else if(user == 1){
            userType.setText(R.string.super_admin);
        } else if(user == 2){
            userType.setText(R.string.admin);
        }

        // Notice module start point
        notice.setOnClickListener(v -> {
            Intent mainIntent = new Intent(TeacherMainScreen.this,NoticeMainScreen.class);
            startActivity(mainIntent);
            finish();
        });

        // Routine module start point
        routine.setOnClickListener(v -> {
            Intent mainIntent = new Intent(TeacherMainScreen.this, RoutineMainScreen.class);
            startActivity(mainIntent);
            finish();
        });

        // Attendance module start point
        attendance.setOnClickListener(v -> {
            if(user == 1||user == 2) {
                Intent mainIntent = new Intent(TeacherMainScreen.this, AttendanceAdminDashboard.class);
                startActivity(mainIntent);
                finish();
            } else if (user == 4) {
                Intent mainIntent = new Intent(TeacherMainScreen.this, TakeAttendance.class);
                mainIntent.putExtra("fromDashBoard", false);
                mainIntent.putExtra("fromSideMenu", true);
                startActivity(mainIntent);
                finish();
            }
        });

        // Syllabus module start point
        syllabus.setOnClickListener(v -> {
            Intent mainIntent = new Intent(TeacherMainScreen.this, SyllabusMainScreenForAdmin.class);
            startActivity(mainIntent);
            finish();
        });

        homework.setOnClickListener(v -> {
            Intent mainIntent = new Intent(TeacherMainScreen.this, HomeworkMainScreenForAdmin.class);
            startActivity(mainIntent);
            finish();
        });

        // Result module start point
        result.setOnClickListener(v -> {
            Intent mainIntent = new Intent(TeacherMainScreen.this, ResultMainScreen.class);
            startActivity(mainIntent);
            finish();
        });

        // Contact module start point
        exam.setOnClickListener(v -> {
            Intent mainIntent = new Intent(TeacherMainScreen.this, SubjectWiseMarksEntryMain.class);
            startActivity(mainIntent);
            finish();
//
//                Intent mainIntent = new Intent(TeacherMainScreen.this, PrivacyPolicy.class);
//                startActivity(mainIntent);
//                finish();
        });

        iCard.setOnClickListener(v -> {
            Intent mainIntent = new Intent(TeacherMainScreen.this, StudentiCardMain.class);
            startActivity(mainIntent);
            finish();
        });

    }

    private void initializeFabAnimations() {
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_open.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                dimView.setVisibility(View.VISIBLE);
                dimView.setClickable(true);
                fabChangeUserType.setVisibility(View.VISIBLE);
                cardChangeUserType.setVisibility(View.VISIBLE);
                fabChangePassword.setVisibility(View.VISIBLE);
                cardChangePassword.setVisibility(View.VISIBLE);
                fabLogout.setVisibility(View.VISIBLE);
                cardLogout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        fab_close.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardLogout.setVisibility(View.GONE);
                fabLogout.setVisibility(View.GONE);
                cardChangePassword.setVisibility(View.GONE);
                fabChangePassword.setVisibility(View.GONE);
                cardChangeUserType.setVisibility(View.GONE);
                fabChangeUserType.setVisibility(View.GONE);
                dimView.setVisibility(View.GONE);
                dimView.setClickable(false);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);
    }

    public void animateFAB(){

        if(isFabOpen){
            dimView.startAnimation(fab_close);
            fabMenu.startAnimation(rotate_backward);
            cardLogout.startAnimation(fab_close);
            fabLogout.startAnimation(fab_close);
            cardChangePassword.startAnimation(fab_close);
            fabChangePassword.startAnimation(fab_close);
            cardChangeUserType.startAnimation(fab_close);
            fabChangeUserType.startAnimation(fab_close);
            cardLogout.setClickable(false);
            fabLogout.setClickable(false);
            cardChangePassword.setClickable(false);
            fabChangePassword.setClickable(false);
            cardChangeUserType.setClickable(false);
            fabChangeUserType.setClickable(false);
            isFabOpen = false;

        } else {
            dimView.startAnimation(fab_open);
            fabMenu.startAnimation(rotate_forward);
            fabChangeUserType.startAnimation(fab_open);
            cardChangeUserType.startAnimation(fab_open);
            fabChangePassword.startAnimation(fab_open);
            cardChangePassword.startAnimation(fab_open);
            fabLogout.startAnimation(fab_open);
            cardLogout.startAnimation(fab_open);
            fabChangeUserType.setClickable(true);
            cardChangeUserType.setClickable(true);
            fabChangePassword.setClickable(true);
            cardChangePassword.setClickable(true);
            fabLogout.setClickable(true);
            cardLogout.setClickable(true);
            isFabOpen = true;
        }
    }

    @Override
    public void onBackPressed() {
        if(isFabOpen){
            animateFAB();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.app_name);
            builder.setIcon(R.drawable.onair);
            builder.setMessage("Do you want to exit?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, id) -> finish())
                    .setNegativeButton("No", (dialog, id) -> dialog.cancel());
            AlertDialog alert = builder.create();
            alert.show();
        }
    }


//    private boolean logOut() {
//        if (StaticHelperClass.isNetworkAvailable(this)) {
//            dialog.show();
//            Retrofit retrofit = new Retrofit.Builder()
//                    .baseUrl(getString(R.string.baseUrl))
//                    .addConverterFactory(ScalarsConverterFactory.create())
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                    .build();
//
//            Observable<String> observable = retrofit
//                    .create(RetrofitNetworkService.class)
//                    .deleteFcmToken(LoggedUserID, "android", getSharedPreferences("UNIQUE_ID", Context.MODE_PRIVATE).getString("uuid", ""))
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .unsubscribeOn(Schedulers.io());
//
//            finalDisposer.add( observable
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .unsubscribeOn(Schedulers.io())
//                    .subscribeWith(new DisposableObserver<String>() {
//
//                        @Override
//                        public void onNext(String response) {
//                            dialog.dismiss();
//                            doLogOut(response);
//                        }
//
//                        @Override
//                        public void onError(Throwable e) {
//                            dialog.dismiss();
//                            returnValue = false;
//                            Toast.makeText(TeacherMainScreen.this,"Server error while logging out!!! ",
//                                    Toast.LENGTH_LONG).show();
//                        }
//
//                        @Override
//                        public void onComplete() {
//
//                        }
//                    }));
//        } else {
//            Toast.makeText(TeacherMainScreen.this,"Please check your internet connection and select again!!! ",
//                    Toast.LENGTH_LONG).show();
//        }
//        return returnValue;
//    }
//
//    private void doLogOut(String returnValueFromServer) {
//        try {
//            if (new JSONArray(returnValueFromServer).getJSONObject(0).getInt("ReturnValue") == 1) {
//                SharedPreferences sharedPreferences  = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putBoolean("LogInState", false);
//                editor.apply();
//
//                getSharedPreferences("PUSH_NOTIFICATIONS", Context.MODE_PRIVATE)
//                        .edit()
//                        .putString("notifications", "[]")
//                        .apply();
//
//                returnValue = true;
//                Intent intent = new Intent(getApplicationContext(), LoginScreen.class);
//                startActivity(intent);
//                finish();
//            } else {
//                returnValue = false;
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

    private void doLogOut() {
        SharedPreferences sharedPreferences  = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("LogInState", false);
        editor.apply();

        getSharedPreferences("PUSH_NOTIFICATIONS", Context.MODE_PRIVATE)
                .edit()
                .putString("notifications", "[]")
                .apply();

        Intent intent = new Intent(getApplicationContext(), LoginScreen.class);
        startActivity(intent);
        finish();
    }
}
