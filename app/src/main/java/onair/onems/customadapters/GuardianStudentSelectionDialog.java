package onair.onems.customadapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import onair.onems.R;
import onair.onems.mainactivities.LoginScreen;
import onair.onems.mainactivities.StudentMainScreen;

public class GuardianStudentSelectionDialog extends Dialog implements
        GuardianStudentSelectionAdapter.GuardianStudentSelectionListener {
    private JSONArray students;
    private Context context;
    private Activity currentActivity;
    public GuardianStudentSelectionDialog(Activity a, JSONArray students, Context context) {
        super(a);
        this.students = students;
        this.context = context;
        this.currentActivity = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.guardian_student_selection_dialog);
        RecyclerView recyclerView = findViewById(R.id.studentList);

        GuardianStudentSelectionAdapter mAdapter = new GuardianStudentSelectionAdapter(context, students, this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

    }

    @Override
    public void onStudentSelected(JSONObject student) {
        String currentActivityName = currentActivity.getClass().getSimpleName();
        if(currentActivityName.equalsIgnoreCase("LoginScreen")) {
            context.getSharedPreferences("CURRENT_STUDENT", Context.MODE_PRIVATE)
                    .edit()
                    .putString("guardianSelectedStudent", student.toString())
                    .apply();
            Intent mainIntent = new Intent(currentActivity, StudentMainScreen.class);
            currentActivity.startActivity(mainIntent);
            currentActivity.finish();
        } else {
            context.getSharedPreferences("CURRENT_STUDENT", Context.MODE_PRIVATE)
                    .edit()
                    .putString("guardianSelectedStudent", student.toString())
                    .apply();
            currentActivity.recreate();
        }
        dismiss();
    }
}

