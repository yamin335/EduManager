package onair.onems.mainactivities;

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

    private Button takeAttendance;
    TextView InstituteName;
    TextView userType;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_student);

        InstituteName=(TextView) findViewById(R.id.InstituteName);
        userType = (TextView) findViewById(R.id.userType);

        SharedPreferences sharedPre = PreferenceManager.getDefaultSharedPreferences(this);
        String name=sharedPre.getString("InstituteName","School Name");
        SharedPreferences.Editor editor = sharedPre.edit();
        editor.putInt("monthselectindex",0);

        editor.commit();
        int user = sharedPre.getInt("UserTypeID",0);
        if(user == 3)
        {
            userType.setText("Student");
        }
        else if(user == 5)
        {
            userType.setText("Guardian");
        }
        InstituteName.setText(name);

        takeAttendance = (Button)findViewById(R.id.attendance);

        takeAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(StudentMainScreen.this,StudentAttendance.class);
                StudentMainScreen.this.startActivity(mainIntent);
            }
        });

    }
}
