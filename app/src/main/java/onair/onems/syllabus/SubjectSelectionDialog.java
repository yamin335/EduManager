package onair.onems.syllabus;

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

import org.json.JSONObject;

import onair.onems.R;

public class SubjectSelectionDialog extends Dialog implements View.OnClickListener, SubjectAdapter.SubjectAdapterListener{
    private Context context;
    private String subjectData;
    private Activity currentActivity;

    SubjectSelectionDialog(Activity currentActivity, String subjectData, Context context) {
        super(currentActivity);
        this.context = context;
        this.subjectData = subjectData;
        this.currentActivity = currentActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.selection_dialog);
        RecyclerView recyclerView = findViewById(R.id.recycler);
        TextView header = findViewById(R.id.header);
        header.setText(R.string.select_subject);
        Button cross = findViewById(R.id.cross);
        cross.setOnClickListener(this);

        SubjectAdapter mAdapter = new SubjectAdapter(currentActivity, subjectData, this);

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
    public void onSubjectSelected(JSONObject subject) {
        dismiss();
    }
}

