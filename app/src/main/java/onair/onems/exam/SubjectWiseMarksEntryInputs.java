package onair.onems.exam;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import onair.onems.R;
import onair.onems.mainactivities.CommonToolbarParentActivity;

public class SubjectWiseMarksEntryInputs extends CommonToolbarParentActivity {

    private JSONObject student;
    private double totalMarks = 0, mcqMark = 0, writtenMark = 0, attendanceMark = 0, practicalMark = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View childActivityLayout = inflater.inflate(R.layout.exam_subject_wise_marks_entry_inputs, null);
        LinearLayout parentActivityLayout = findViewById(R.id.contentMain);
        parentActivityLayout.addView(childActivityLayout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        try {
            student = new JSONObject(getIntent().getStringExtra("student"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final EditText mcq = findViewById(R.id.mcq);
        final EditText written = findViewById(R.id.written);
        final EditText practical = findViewById(R.id.practical);
        final EditText attendance = findViewById(R.id.attendance);
        final EditText total = findViewById(R.id.total);
        CheckBox absent = findViewById(R.id.absent);
        Button save = findViewById(R.id.save);

        TextWatcher mcqTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equalsIgnoreCase("")){
                    mcqMark = Double.parseDouble(mcq.getText().toString());
                } else {
                    mcqMark = 0;
                }
                totalMarks = mcqMark+ writtenMark+ practicalMark+ attendanceMark;
                total.setText(Double.toString(totalMarks));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        TextWatcher writtenTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equalsIgnoreCase("")){
                    writtenMark = Double.parseDouble(written.getText().toString());
                } else {
                    writtenMark = 0;
                }
                totalMarks = mcqMark+ writtenMark+ practicalMark+ attendanceMark;
                total.setText(Double.toString(totalMarks));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        TextWatcher practicalTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equalsIgnoreCase("")){
                    practicalMark = Double.parseDouble(practical.getText().toString());
                } else {
                    practicalMark = 0;
                }
                totalMarks = mcqMark+ writtenMark+ practicalMark+ attendanceMark;
                total.setText(Double.toString(totalMarks));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        TextWatcher attendanceTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equalsIgnoreCase("")){
                    attendanceMark = Double.parseDouble(attendance.getText().toString());
                } else {
                    attendanceMark = 0;
                }
                totalMarks = mcqMark+ writtenMark+ practicalMark+ attendanceMark;
                total.setText(Double.toString(totalMarks));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        mcq.addTextChangedListener(mcqTextWatcher);
        written.addTextChangedListener(writtenTextWatcher);
        practical.addTextChangedListener(practicalTextWatcher);
        attendance.addTextChangedListener(attendanceTextWatcher);
    }
}
