package onair.onems.mainactivities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import onair.onems.R;
import onair.onems.attendance.AttendanceAdminDashboard;
import onair.onems.exam.ExamRoutineMainScreen;
import onair.onems.homework.HomeworkMainScreen;
import onair.onems.notice.NoticeMainScreen;
import onair.onems.result.ResultMainScreen;
import onair.onems.routine.RoutineMainScreen;
import onair.onems.attendance.TakeAttendance;
import onair.onems.icard.StudentiCardMain;
import onair.onems.syllabus.SyllabusMainScreen;

public class TeacherMainScreen extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_teacher);

        Button attendance = (Button)findViewById(R.id.attendance);
        Button iCard = (Button)findViewById(R.id.iCard);
        Button notice = (Button)findViewById(R.id.notice);
        TextView InstituteName = (TextView) findViewById(R.id.InstituteName);
        TextView userType = (TextView) findViewById(R.id.userType);
        Button result = (Button)findViewById(R.id.result);
        Button contact = (Button)findViewById(R.id.contact);
        Button routine = (Button)findViewById(R.id.routine);
        Button homework = (Button)findViewById(R.id.homework);
        Button syllabus = findViewById(R.id.syllabus);

        SharedPreferences sharedPre = PreferenceManager.getDefaultSharedPreferences(this);
        String InstituteNameString = sharedPre.getString("InstituteName","");
        InstituteName.setText(InstituteNameString);
        final int user = sharedPre.getInt("UserTypeID",0);
        if(user == 4) {
            userType.setText("Teacher");
        } else if(user == 1){
            userType.setText("Super Admin");
        } else if(user == 2){
            userType.setText("Institute Admin");
        }

        // Notice module start point
        notice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(TeacherMainScreen.this,NoticeMainScreen.class);
                TeacherMainScreen.this.startActivity(mainIntent);
                finish();
            }
        });

        // Routine module start point
        routine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(TeacherMainScreen.this, RoutineMainScreen.class);
                TeacherMainScreen.this.startActivity(mainIntent);
                finish();
            }
        });

        // Attendance module start point
        attendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user == 1||user == 2) {
                    Intent mainIntent = new Intent(TeacherMainScreen.this,AttendanceAdminDashboard.class);
                    TeacherMainScreen.this.startActivity(mainIntent);
                    finish();
                } else {
                    Intent mainIntent = new Intent(TeacherMainScreen.this,TakeAttendance.class);
                    TeacherMainScreen.this.startActivity(mainIntent);
                    finish();
                }
            }
        });

        // Syllabus module start point
        syllabus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(TeacherMainScreen.this, SyllabusMainScreen.class);
                startActivity(mainIntent);
                finish();
            }
        });

        homework.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(TeacherMainScreen.this,HomeworkMainScreen.class);
                TeacherMainScreen.this.startActivity(mainIntent);
                finish();
            }
        });

        // Result module start point
        result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(TeacherMainScreen.this, ResultMainScreen.class);
                TeacherMainScreen.this.startActivity(mainIntent);
                finish();
            }
        });

        // Contact module start point
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent mainIntent = new Intent(TeacherMainScreen.this, SyllabusMainScreen.class);
//                startActivity(mainIntent);
//                finish();
//                Intent mainIntent = new Intent(TeacherMainScreen.this,ContactsMainScreen.class);
//                TeacherMainScreen.this.startActivity(mainIntent);
//                finish();
            }
        });

        iCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(TeacherMainScreen.this, StudentiCardMain.class);
                TeacherMainScreen.this.startActivity(mainIntent);
                finish();
            }
        });

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
