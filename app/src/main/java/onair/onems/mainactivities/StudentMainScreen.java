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

/**
 * Created by User on 12/5/2017.
 */

public class StudentMainScreen extends AppCompatActivity {

    private Button Attendance;
    TextView InstituteName;
    TextView userType;
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
        editor.putInt("monthselectindex",0);

        editor.commit();
        long user = sharedPre.getLong("UserTypeID",0);
        if(user == 3)
        {
            userType.setText("Student");
        }
        else if(user == 5)
        {
            userType.setText("Guardian");
        }
        InstituteName.setText(name);

        Attendance = (Button)findViewById(R.id.attendance);

        Attendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(StudentMainScreen.this,StudentAttendance.class);
                StudentMainScreen.this.startActivity(mainIntent);
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
