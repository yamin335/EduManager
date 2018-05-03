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

import onair.onems.exam.ExamRoutineMainScreen;
import onair.onems.fee.FeeMainScreen;
import onair.onems.R;
import onair.onems.attendance.StudentAttendanceReport;
import onair.onems.notice.NoticeMainScreen;
import onair.onems.result.ResultMainScreen;
import onair.onems.routine.RoutineMainScreen;


public class StudentMainScreen extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_student);
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
        Button exam = findViewById(R.id.exam);
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
                StudentMainScreen.this.startActivity(mainIntent);
                finish();
            }
        });

        // Exam module start point
        exam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(StudentMainScreen.this, ExamRoutineMainScreen.class);
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
//                if(user == 3)
//                {
//                    Intent mainIntent = new Intent(StudentMainScreen.this,FeeMainScreen.class);
//                    StudentMainScreen.this.startActivity(mainIntent);
//                    finish();
//                }
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
