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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import onair.onems.R;
import onair.onems.Services.RetrofitNetworkService;
import onair.onems.mainactivities.CommonToolbarParentActivity;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class SubjectWiseMarksEntryInputs extends CommonToolbarParentActivity {

    private JSONObject student;
    private int totalMarks = 0, mcqMark = 0, writtenMark = 0, attendanceMark = 0, practicalMark = 0;
    private long SubjectID;
    private Disposable finalDisposer;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!finalDisposer.isDisposed())
            finalDisposer.dispose();
    }

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

        Button save = findViewById(R.id.save);
        save.setOnClickListener(view -> {
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
                .setMarks(jsonObject)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());

        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(new Observer<String>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        finalDisposer = d;
                    }

                    @Override
                    public void onNext(String returnData) {
                        Toast.makeText(getApplicationContext(),"Successful!!!",Toast.LENGTH_LONG).show();
                        finish();
                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getApplicationContext(),"ERROR posting token",Toast.LENGTH_LONG).show();
                    }
                });

    }
}
