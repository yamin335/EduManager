package onair.onems.mainactivities;

import android.content.Context;
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

public class TeacherMainScreen extends AppCompatActivity {

    private Button takeAttendance;
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
        setContentView(R.layout.activity_main_teacher);

        takeAttendance = (Button)findViewById(R.id.attendance);
        InstituteName = (TextView) findViewById(R.id.InstituteName);
        userType = (TextView) findViewById(R.id.userType);

        SharedPreferences sharedPre = PreferenceManager.getDefaultSharedPreferences(this);
        String InstituteName = sharedPre.getString("InstituteName","");
        this.InstituteName.setText(InstituteName);
        int user = sharedPre.getInt("UserTypeID",0);
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
            }
        });

    }
}
