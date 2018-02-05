package onair.onems.mainactivities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import onair.onems.R;

/**
 * Created by User on 12/5/2017.
 */

public class TeacherMainScreen extends AppCompatActivity {

    private Button takeAttendance, iCard;
    TextView InstituteName;
    TextView userType;
    public static final String MyPREFERENCES = "LogInKey";
    public static SharedPreferences sharedPreferences;

//    @Override
//    protected void onRestart() {
//        sharedPreferences  = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
//        if(sharedPreferences.getBoolean("LogInState", false))
//        {
//            finish();
//        }
//        super.onRestart();
//    }

//    @Override
//    protected void onStart() {
//        sharedPreferences  = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
//        if(sharedPreferences.getBoolean("LogInState", false))
//        {
//            finish();
//        }
//        super.onStart();
//    }
//
//    @Override
//    protected void onResume() {
//        sharedPreferences  = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
//        if(sharedPreferences.getBoolean("LogInState", false))
//        {
//            finish();
//        }
//        super.onResume();
//    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        sharedPreferences  = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
//        if(sharedPreferences.getBoolean("LogInState", false))
//        {
//            finish();
//        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_dashboard);

        takeAttendance = (Button)findViewById(R.id.attendance);
        iCard = (Button)findViewById(R.id.iCard);
        InstituteName = (TextView) findViewById(R.id.InstituteName);
        userType = (TextView) findViewById(R.id.userType);

        SharedPreferences sharedPre = PreferenceManager.getDefaultSharedPreferences(this);
        String InstituteName = sharedPre.getString("InstituteName","");
        this.InstituteName.setText(InstituteName);
        long user = sharedPre.getLong("UserTypeID",0);
        if(user == 4)
        {
            userType.setText("Teacher");
        }
//        InstituteName.setText(name);

        takeAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(TeacherMainScreen.this,TakeAttendance.class);
                TeacherMainScreen.this.startActivity(mainIntent);
                finish();
            }
        });

        iCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(TeacherMainScreen.this,StudentiCardMain.class);
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
