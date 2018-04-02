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
import onair.onems.mainactivities.Fees.Fee;
import onair.onems.mainactivities.Fees.StudentList;
import onair.onems.mainactivities.Routine.ClassRoutine;


public class StudentMainScreen extends AppCompatActivity {

    TextView InstituteName;
    TextView userType;
    long user;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_dashboard);
        InstituteName=(TextView) findViewById(R.id.InstituteName);
        userType = (TextView) findViewById(R.id.userType);
        SharedPreferences sharedPre = PreferenceManager.getDefaultSharedPreferences(this);
        String name = sharedPre.getString("InstituteName","School Name");
        SharedPreferences.Editor editor = sharedPre.edit();
        editor.putInt("monthselectindex",1);
        editor.putString("yearselectindex","2018");
        editor.apply();
        user = sharedPre.getLong("UserTypeID",0);
        if(user == 3)
        {
            userType.setText("Student");
        }
        else
        {
            userType.setText("Guardian");
        }

        InstituteName.setText(name);

        Button notice = (Button)findViewById(R.id.notice);
        Button Attendance = (Button)findViewById(R.id.attendance);
        Button Fees=(Button) findViewById(R.id.fee);
        Button Class_routine=(Button) findViewById(R.id.routine);
        Button result = (Button)findViewById(R.id.result);
        Button contact = (Button)findViewById(R.id.contact);


//        // Notice module start point
//        notice.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent mainIntent = new Intent(StudentMainScreen.this,NoticeMainScreen.class);
//                StudentMainScreen.this.startActivity(mainIntent);
//                finish();
//            }
//        });

        Class_routine.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(user == 3)
                {
                    Intent mainIntent = new Intent(StudentMainScreen.this, RoutineMainScreen.class);
                    startActivity(mainIntent);
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

        Attendance.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(StudentMainScreen.this, StudentAttendance.class);
                StudentMainScreen.this.startActivity(mainIntent);
                finish();
            }
        });

        // Result module start point
        result.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
//                if(user == 3)
//                {
//                    Intent mainIntent = new Intent(StudentMainScreen.this,ResultMainScreen.class);
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

        Fees.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(user == 3)
                {
                    Intent mainIntent = new Intent(StudentMainScreen.this,Fee.class);
                    StudentMainScreen.this.startActivity(mainIntent);
                    finish();
                }
                else
                {
                    Intent mainIntent = new Intent(StudentMainScreen.this,StudentList.class);
                    StudentMainScreen.this.startActivity(mainIntent);
                    finish();
                }
            }
        });

//        // Contact module start point
//        contact.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent mainIntent = new Intent(StudentMainScreen.this,ContactsMainScreen.class);
//                StudentMainScreen.this.startActivity(mainIntent);
//                finish();
//            }
//        });

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
