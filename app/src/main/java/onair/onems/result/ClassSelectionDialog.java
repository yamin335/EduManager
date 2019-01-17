package onair.onems.result;

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

public class ClassSelectionDialog extends Dialog implements View.OnClickListener, ClassAdapter.ClassAdapterListener{
    private Context context;
    private Activity currentActivity;
    private String classes;
    ClassSelectionDialog(Activity currentActivity, String classes, Context context) {
        super(currentActivity);
        this.context = context;
        this.currentActivity = currentActivity;
        this.classes = classes;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.selection_dialog);
        RecyclerView recyclerView = findViewById(R.id.recycler);
        TextView header = findViewById(R.id.header);
        header.setText(R.string.select_class);
        Button cross = findViewById(R.id.cross);
        cross.setOnClickListener(this);

        ClassAdapter mAdapter = new ClassAdapter(currentActivity, classes, this);

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
    public void onClassSelected(JSONObject exam) {
        dismiss();
    }
}

