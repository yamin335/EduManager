package onair.onems.routine;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import onair.onems.R;
import onair.onems.mainactivities.CommonToolbarParentActivity;

public class ExamDetails extends CommonToolbarParentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View childActivityLayout = inflater.inflate(R.layout.exam_routine_exam_details_screen, null);
        LinearLayout parentActivityLayout = findViewById(R.id.contentMain);
        parentActivityLayout.addView(childActivityLayout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

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
