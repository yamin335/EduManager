package onair.onems.exam;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import onair.onems.R;
import onair.onems.Services.RetrofitNetworkService;
import onair.onems.Services.StaticHelperClass;
import onair.onems.mainactivities.CommonToolbarParentActivity;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class SubjectWiseMarksEntryInputs extends CommonToolbarParentActivity {

    private JSONObject student;
    private int totalMarks = 0, mcqMark = 0, writtenMark = 0, attendanceMark = 0, practicalMark = 0;
    private long SubjectID;
    private CompositeDisposable finalDisposer = new CompositeDisposable();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!finalDisposer.isDisposed())
            finalDisposer.dispose();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View childActivityLayout = Objects.requireNonNull(inflater).inflate(R.layout.exam_subject_wise_marks_entry_inputs, null);
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
        absent.setOnCheckedChangeListener((buttonView, isChecked) -> {
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
        });

        try {
            if (!student.getString("PassMCQ").equalsIgnoreCase("null") && !student.getString("PassMCQ").equalsIgnoreCase("0")) {
                mcq.setEnabled(true);
            } else {
                mcq.setEnabled(false);
            }
            if (!student.getString("PassWritten").equalsIgnoreCase("null") && !student.getString("PassWritten").equalsIgnoreCase("0")) {
                written.setEnabled(true);
            } else {
                written.setEnabled(false);
            }
            if (!student.getString("PassPrecticle").equalsIgnoreCase("null") && !student.getString("PassPrecticle").equalsIgnoreCase("0")) {
                practical.setEnabled(true);
            } else {
                practical.setEnabled(false);
            }
            if (!student.getString("PassAttendance").equalsIgnoreCase("null") && !student.getString("PassAttendance").equalsIgnoreCase("0")) {
                attendance.setEnabled(true);
            } else {
                attendance.setEnabled(false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Button save = findViewById(R.id.show);
        save.setOnClickListener(view -> {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("MCQ", mcqMark);
                jsonObject.put("Written", writtenMark);
                jsonObject.put("Precticle", practicalMark);
                jsonObject.put("Attendance", attendanceMark);
                jsonObject.put("Total", totalMarks);
                jsonObject.put("ExamMarkID", student.getString("ExamMarkID").equalsIgnoreCase("null")?"null":student.get("ExamMarkID"));
                jsonObject.put("ExamID", intent.getLongExtra("ExamID", 0));
                jsonObject.put("SectionID", student.getString("SectionID").equalsIgnoreCase("null")?"null":student.get("SectionID"));
                jsonObject.put("ShiftID", student.getString("ShiftID").equalsIgnoreCase("null")?"null":student.get("ShiftID"));
                jsonObject.put("UserID", student.getString("UserID").equalsIgnoreCase("null")?"null":student.get("UserID"));
                jsonObject.put("DepartmentID", student.getString("DepartmentID").equalsIgnoreCase("null")?"null":student.get("DepartmentID"));
                jsonObject.put("MeduimID", student.getString("MediumID").equalsIgnoreCase("null")?"null":student.get("MediumID"));
                jsonObject.put("ClassID", student.getString("ClassID").equalsIgnoreCase("null")?"null":student.get("ClassID"));
                jsonObject.put("SubjectID", SubjectID);
                jsonObject.put("InstituteID", student.getString("InstituteID").equalsIgnoreCase("null")?"null":student.get("InstituteID"));
                jsonObject.put("SessionID", student.getString("SessionID").equalsIgnoreCase("null")?"null":student.get("SessionID"));
                jsonObject.put("IsAbsent", student.getString("IsAbsent").equalsIgnoreCase("null")?"null":student.get("IsAbsent"));
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SubjectWiseMarksEntryInputs.this);
                String LoggedUserID = prefs.getString("UserID", "0");
                jsonObject.put("LoggedUserID", LoggedUserID);
                jsonObject.put("IsDeleted", 0);
                String stringObject = jsonObject.toString();
                postMarks(stringObject);

            } catch (JSONException e) {
                e.printStackTrace();
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

    public void postMarks(String stringObject) {
        if (StaticHelperClass.isNetworkAvailable(this)) {
            dialog.show();
            JsonParser parser = new JsonParser();
            JsonObject jsonObject = parser.parse(stringObject).getAsJsonObject();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(getString(R.string.baseUrl))
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();

            Observable<String> observable = retrofit
                    .create(RetrofitNetworkService.class)
                    .indivisualSetMark(jsonObject)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io());

            finalDisposer.add(observable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io())
                    .subscribeWith(new DisposableObserver<String>() {

                        @Override
                        public void onNext(String returnData) {
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Successful!!!",Toast.LENGTH_LONG).show();
                            finish();
                        }

                        @Override
                        public void onComplete() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(),"ERROR posting data",Toast.LENGTH_LONG).show();
                        }
                    }));
        } else {
            Toast.makeText(this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }
}
