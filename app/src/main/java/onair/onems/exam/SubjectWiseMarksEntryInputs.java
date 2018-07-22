package onair.onems.exam;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import onair.onems.R;
import onair.onems.attendance.TakeAttendanceDetails;
import onair.onems.customised.CustomRequest;
import onair.onems.mainactivities.CommonToolbarParentActivity;
import onair.onems.network.MySingleton;

public class SubjectWiseMarksEntryInputs extends CommonToolbarParentActivity {

    private JSONObject student;
    private int totalMarks = 0, mcqMark = 0, writtenMark = 0, attendanceMark = 0, practicalMark = 0;
    private long SubjectID;
    private ProgressDialog dialog;

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

        Intent intent = getIntent();
        SubjectID = intent.getLongExtra("SubjectID", 0);

        final EditText mcq = findViewById(R.id.mcq);
        final EditText written = findViewById(R.id.written);
        final EditText practical = findViewById(R.id.practical);
        final EditText attendance = findViewById(R.id.attendance);
        final EditText total = findViewById(R.id.total);

        CheckBox absent = findViewById(R.id.absent);
        absent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    try {
                        student.put("IsAbsent", 1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        student.put("IsAbsent", 0);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        Button save = findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonObject = new JSONObject();
                try {
                    student.put("MCQ", mcqMark);
                    student.put("Written", writtenMark);
                    student.put("Precticle", practicalMark);
                    student.put("Attendance", attendanceMark);
                    student.put("Total", totalMarks);
                    jsonObject.put("ExamMarkID", student.get("ExamMarkID"));
                    jsonObject.put("ExamID", student.get("examID"));
                    jsonObject.put("SectionID", student.get("SectionID"));
                    jsonObject.put("ShiftID", student.get("ShiftID"));
                    jsonObject.put("UserID", student.get("UserID"));
                    jsonObject.put("DepartmentID", student.get("DepartmentID"));
                    jsonObject.put("MeduimID", student.get("MediumID"));
                    jsonObject.put("ClassID", student.get("ClassID"));
                    jsonObject.put("SubjectID", SubjectID);
                    jsonObject.put("MCQ", student.get("MCQ"));
                    jsonObject.put("Written", student.get("Written"));
//                    jsonObject.put("Precticle", student.get("Precticle"));
//                    jsonObject.put("Attendance", student.get("Attendance"));
                    jsonObject.put("Total", student.get("Total"));
                    jsonObject.put("InstituteID", student.get("InstituteID"));
                    jsonObject.put("IsAbsent", student.get("IsAbsent"));
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SubjectWiseMarksEntryInputs.this);
                    String LoggedUserID = prefs.getString("UserID", "0");
                    jsonObject.put("LoggedUserID", LoggedUserID);
                    jsonObject.put("IsDeleted", 0);
                    JSONArray jsonArray = new JSONArray();
                    jsonArray.put(student);
                    jsonObject.put("examMarks", jsonArray);
                    String s = jsonObject.toString();
                    postUsingVolley(jsonObject);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            mcq.setText(student.getString("MCQ"));
            written.setText(student.getString("Written"));
            practical.setText(student.getString("Precticle"));
            attendance.setText(student.getString("Attendance"));
            total.setText(student.getString("Total"));
            mcqMark = student.getInt("MCQ");
            writtenMark = student.getInt("Written");
            practicalMark = student.getInt("Precticle");
            attendanceMark = student.getInt("Attendance");
            totalMarks = student.getInt("Total");
            if(student.getString("IsAbsent").equalsIgnoreCase("0")) {
                absent.setChecked(false);
            } else if(student.getString("IsAbsent").equalsIgnoreCase("1")) {
                absent.setChecked(true);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        TextWatcher mcqTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equalsIgnoreCase("")){
                    mcqMark = Integer.parseInt(mcq.getText().toString());
                } else {
                    mcqMark = 0;
                }
                totalMarks = mcqMark+ writtenMark+ practicalMark+ attendanceMark;
                total.setText(Integer.toString(totalMarks));
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
                    writtenMark = Integer.parseInt(written.getText().toString());
                } else {
                    writtenMark = 0;
                }
                totalMarks = mcqMark+ writtenMark+ practicalMark+ attendanceMark;
                total.setText(Integer.toString(totalMarks));
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
                    practicalMark = Integer.parseInt(practical.getText().toString());
                } else {
                    practicalMark = 0;
                }
                totalMarks = mcqMark+ writtenMark+ practicalMark+ attendanceMark;
                total.setText(Integer.toString(totalMarks));
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
                    attendanceMark = Integer.parseInt(attendance.getText().toString());
                } else {
                    attendanceMark = 0;
                }
                totalMarks = mcqMark+ writtenMark+ practicalMark+ attendanceMark;
                total.setText(Integer.toString(totalMarks));
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

    public void postUsingVolley(JSONObject jsonObject) {
        dialog = new ProgressDialog(this);
        dialog.setTitle("Processing...");
        dialog.setMessage("Please Wait...");
        dialog.setCancelable(true);
        dialog.setIcon(R.drawable.onair);
        dialog.show();

        String postUrl = getString(R.string.baseUrl)+"/api/onEms/setMarks";

        CustomRequest customRequest = new CustomRequest (Request.Method.POST, postUrl, jsonObject,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        dialog.dismiss();
                        try {
                            Toast.makeText(SubjectWiseMarksEntryInputs.this,"Successful",Toast.LENGTH_LONG).show();
                            finish();
                        } catch (Exception e) {
                            dialog.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                finish();
                Toast.makeText(SubjectWiseMarksEntryInputs.this,"Not Response: "+error.toString(),Toast.LENGTH_LONG).show();
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<>();
                params.put("Authorization", "Request_From_onEMS_Android_app");
                return params;
            }
        };
        MySingleton.getInstance(this).addToRequestQueue(customRequest);
    }
}
