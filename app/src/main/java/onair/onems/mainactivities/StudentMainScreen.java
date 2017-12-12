package onair.onems.mainactivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import onair.onems.R;

/**
 * Created by User on 12/5/2017.
 */

public class StudentMainScreen extends AppCompatActivity {

    private Button takeAttendance;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_student);

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
