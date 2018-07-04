package onair.onems.mainactivities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import onair.onems.fee.FeeMainScreen;
import onair.onems.R;
import onair.onems.attendance.StudentAttendanceReport;
import onair.onems.homework.HomeworkMainScreen;
import onair.onems.login.LoginScreen;
import onair.onems.notice.NoticeMainScreen;
import onair.onems.result.ResultMainScreen;
import onair.onems.routine.RoutineMainScreen;
import onair.onems.syllabus.SyllabusMainScreen;

import static onair.onems.login.LoginScreen.MyPREFERENCES;


public class StudentMainScreen extends AppCompatActivity {

    private FloatingActionButton fabMenu, fabLogout, fabChangePassword, fabChangeUserType;
    private CardView cardLogout, cardChangePassword, cardChangeUserType;
    private Animation fab_open, fab_close, rotate_forward, rotate_backward;
    private Boolean isFabOpen = false;
    private long InstituteID;
    private String UserName, LoggedUserID;
    private View dimView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_student);


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        InstituteID = prefs.getLong("InstituteID",0);
        LoggedUserID = prefs.getString("UserID", "0");
        UserName = prefs.getString("UserName", "0");
        dimView = findViewById(R.id.dim);
        initializeFabAnimations();
        fabMenu = findViewById(R.id.floatingMenu);
//        fabMenu.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#5f27cd")));
        fabMenu.setRippleColor(Color.parseColor("#341f97"));
        fabMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFAB();
            }
        });

        fabLogout = findViewById(R.id.log_out);
//        fabLogout.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#5f27cd")));
        fabLogout.setRippleColor(Color.parseColor("#341f97"));
        fabLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences  = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("LogInState", false);
                editor.apply();
                Intent intent = new Intent(getApplicationContext(), LoginScreen.class);
                startActivity(intent);
                finish();
            }
        });

        fabChangePassword = findViewById(R.id.change_password);
//        fabChangePassword.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#5f27cd")));
        fabChangePassword.setRippleColor(Color.parseColor("#341f97"));
        fabChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangePasswordDialog changePasswordDialog = new ChangePasswordDialog(StudentMainScreen.this,
                        StudentMainScreen.this, LoggedUserID, UserName, InstituteID);
                changePasswordDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                changePasswordDialog.setCancelable(false);
                changePasswordDialog.show();
            }
        });

        fabChangeUserType = findViewById(R.id.change_user_type);
//        fabChangeUserType.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#5f27cd")));
        fabChangeUserType.setRippleColor(Color.parseColor("#341f97"));
        fabChangeUserType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeUserTypeDialog changeUserTypeDialog = new ChangeUserTypeDialog(StudentMainScreen.this,
                        StudentMainScreen.this);
                changeUserTypeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                changeUserTypeDialog.setCancelable(false);
                changeUserTypeDialog.show();
            }
        });

        cardLogout = findViewById(R.id.card_log_out);
        cardLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences  = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("LogInState", false);
                editor.apply();
                Intent intent = new Intent(getApplicationContext(), LoginScreen.class);
                startActivity(intent);
                finish();
            }
        });

        cardChangePassword = findViewById(R.id.card_change_password);
        cardChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangePasswordDialog changePasswordDialog = new ChangePasswordDialog(StudentMainScreen.this,
                        StudentMainScreen.this, LoggedUserID, UserName, InstituteID);
                changePasswordDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                changePasswordDialog.setCancelable(false);
                changePasswordDialog.show();
            }
        });

        cardChangeUserType = findViewById(R.id.card_change_user_type);
        cardChangeUserType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeUserTypeDialog changeUserTypeDialog = new ChangeUserTypeDialog(StudentMainScreen.this,
                        StudentMainScreen.this);
                changeUserTypeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                changeUserTypeDialog.setCancelable(false);
                changeUserTypeDialog.show();
            }
        });

        TextView InstituteName =(TextView) findViewById(R.id.InstituteName);
        TextView userType = (TextView) findViewById(R.id.userType);

        SharedPreferences sharedPre = PreferenceManager.getDefaultSharedPreferences(this);
        String name = sharedPre.getString("InstituteName","School Name");
        final int user = sharedPre.getInt("UserTypeID",0);

        if(user == 3) {
            userType.setText("Student");
        } else if(user == 5){
            userType.setText("Guardian");
        }
        InstituteName.setText(name);
        Button notice = findViewById(R.id.notice);
        Button routine= findViewById(R.id.routine);
        Button Attendance = findViewById(R.id.attendance);
        Button syllabus = findViewById(R.id.syllabus);
        Button homework = findViewById(R.id.homework);
        Button result = findViewById(R.id.result);
        Button Fees = findViewById(R.id.fee);
        Button contact = findViewById(R.id.contact);

        // Notice module start point
        notice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(StudentMainScreen.this, NoticeMainScreen.class);
                StudentMainScreen.this.startActivity(mainIntent);
                finish();
            }
        });

        // Routine module start point
        routine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(StudentMainScreen.this, RoutineMainScreen.class);
                startActivity(mainIntent);
                finish();
            }
        });

        // Attendance module start point
        Attendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(StudentMainScreen.this, StudentAttendanceReport.class);
                startActivity(mainIntent);
                finish();
            }
        });

        // Syllabus module start point
        syllabus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(StudentMainScreen.this, SyllabusMainScreen.class);
                startActivity(mainIntent);
                finish();
            }
        });

        // Exam module start point
        homework.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(StudentMainScreen.this, HomeworkMainScreen.class);
                StudentMainScreen.this.startActivity(mainIntent);
                finish();
            }
        });

        // Result module start point
        result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(StudentMainScreen.this, ResultMainScreen.class);
                StudentMainScreen.this.startActivity(mainIntent);
                finish();
            }
        });

        // Fees module start point
        Fees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user == 3)
                {
                    Intent mainIntent = new Intent(StudentMainScreen.this, FeeMainScreen.class);
                    StudentMainScreen.this.startActivity(mainIntent);
                    finish();
                }
//                else
//                {
//                    Intent mainIntent = new Intent(StudentMainScreen.this,StudentList.class);
//                    StudentMainScreen.this.startActivity(mainIntent);
//                    finish();
//                }
            }
        });

        // Contact module start point
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent mainIntent = new Intent(StudentMainScreen.this, SyllabusMainScreen.class);
//                StudentMainScreen.this.startActivity(mainIntent);
//                finish();
            }
        });

    }

    private void initializeFabAnimations() {
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_open.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                dimView.setVisibility(View.VISIBLE);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name);
        builder.setIcon(R.drawable.onair);
        builder.setMessage("Do you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
