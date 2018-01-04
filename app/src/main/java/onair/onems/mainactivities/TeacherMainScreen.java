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

public class TeacherMainScreen extends AppCompatActivity {

    private Button takeAttendance;
    TextView InstituteName;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_teacher);

        takeAttendance = (Button)findViewById(R.id.attendance);
        InstituteName=(TextView) findViewById(R.id.InstituteName);

        SharedPreferences sharedPre = PreferenceManager.getDefaultSharedPreferences(this);
        String name=sharedPre.getString("InstituteName","");
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
