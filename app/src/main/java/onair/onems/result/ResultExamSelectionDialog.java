package onair.onems.result;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;

import onair.onems.R;
import onair.onems.syllabus.ExamAdapter;

public class ResultExamSelectionDialog extends Dialog implements View.OnClickListener, ResultExamAdapter.ExamAdapterListener{
    private Context context;
    private Activity currentActivity;
    private String exams;
    private ResultExamAdapter.ExamAdapterListener selectedListener;
    ResultExamSelectionDialog(Activity currentActivity, ResultExamAdapter.ExamAdapterListener selectedListener, String exams, Context context) {
        super(currentActivity);
        this.context = context;
        this.currentActivity = currentActivity;
        this.exams = exams;
        this.selectedListener = selectedListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.selection_dialog);
        RecyclerView recyclerView = findViewById(R.id.recycler);
        TextView header = findViewById(R.id.header);
        header.setText("Select Exam");
        Button cross = (Button) findViewById(R.id.cross);
        cross.setOnClickListener(this);

        ResultExamAdapter mAdapter = new ResultExamAdapter(currentActivity, selectedListener, exams, this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cross:
                dismiss();
                break;
            default:
                break;
        }
    }

    @Override
    public void onExamSelected(JSONObject exam) {
        dismiss();
    }
}

