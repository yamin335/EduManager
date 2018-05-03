package onair.onems.exam;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import onair.onems.R;

public class ExamDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exam_routine_exam_details_screen);

        try {
            JSONArray examList = new JSONArray(getIntent().getStringExtra("examList"));
            ArrayList<JSONObject> exams = new ArrayList<>();
            for (int i = 0; i < examList.length(); i++) {
                exams.add(examList.getJSONObject(i));
            }
            RecyclerView recyclerView = findViewById(R.id.routineDetails);
            ExamRoutineExamDetailsAdapter mAdapter = new ExamRoutineExamDetailsAdapter(this, exams, this);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(mAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
