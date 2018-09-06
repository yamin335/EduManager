package onair.onems.routine;

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
import onair.onems.syllabus.ExamAdapter;

public class ShiftSelectionDialog extends Dialog implements View.OnClickListener, ShiftSelectionAdapter.ShiftSelectionListener{
    private Context context;
    private Activity currentActivity;
    private String shifts;
    ShiftSelectionDialog(Activity currentActivity, String shifts, Context context) {
        super(currentActivity);
        this.context = context;
        this.currentActivity = currentActivity;
        this.shifts = shifts;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.selection_dialog);
        RecyclerView recyclerView = findViewById(R.id.recycler);
        TextView header = findViewById(R.id.header);
        header.setText("Select Shift");
        Button cross = (Button) findViewById(R.id.cross);
        cross.setOnClickListener(this);

        ShiftSelectionAdapter mAdapter = new ShiftSelectionAdapter(currentActivity, shifts, this);

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
    public void onShiftSelected(JSONObject shift) {
        dismiss();
    }
}

