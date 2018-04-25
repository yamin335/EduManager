package onair.onems.customadapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import onair.onems.R;

public class ExamRoutineDialog extends Dialog implements View.OnClickListener, ExamRoutineExamListAdapter.ExamRoutineExamListAdapterListener {
    private ArrayList<JSONObject> examList;
    private Context context;
    public ExamRoutineDialog(Activity a, ArrayList<JSONObject> examList, Context context) {
        super(a);
        this.examList = examList;
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.exam_routine_select_dialog);
        RecyclerView recyclerView = findViewById(R.id.examList);
        Button ok = (Button) findViewById(R.id.btn_ok);
        ok.setOnClickListener(this);

        ExamRoutineExamListAdapter mAdapter = new ExamRoutineExamListAdapter(context, examList, this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }

    @Override
    public void onExamSelected(JSONObject object) {
        dismiss();
    }
}

