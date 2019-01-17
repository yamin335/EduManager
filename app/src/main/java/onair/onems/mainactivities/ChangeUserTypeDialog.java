package onair.onems.mainactivities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import onair.onems.R;

public class ChangeUserTypeDialog extends Dialog implements
        ChangeUserTypeAdapter.ChangeUserTypeListener, View.OnClickListener {
    private Context context;
    private Activity currentActivity;
    ChangeUserTypeDialog(Activity activity, Context context) {
        super(activity);
        this.context = context;
        this.currentActivity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.selection_dialog);

        TextView header = findViewById(R.id.header);
        header.setText(R.string.select_user_type);
        Button cross = findViewById(R.id.cross);
        cross.setOnClickListener(this);

        String UserTypes = context.getSharedPreferences("USER_TYPES", Context.MODE_PRIVATE)
                .getString("userTypesList", "[]");

        ChangeUserTypeAdapter mAdapter = new ChangeUserTypeAdapter(context, UserTypes, this);

        RecyclerView recyclerView = findViewById(R.id.recycler);
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
    public void onUserTypeSelected(JSONObject UserType) {
        int UserTypeID = 0;
        try {
            UserTypeID = UserType.getInt("UserTypeID");
            PreferenceManager.getDefaultSharedPreferences(context)
                    .edit()
                    .putInt("UserTypeID", UserType.getInt("UserTypeID"))
                    .putString("UserName", UserType.getString("LoginID"))
                    .putString("Password", UserType.getString("Password"))
                    .apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Intent teacherIntent = new Intent(currentActivity, TeacherMainScreen.class);
        Intent studentIntent = new Intent(currentActivity, StudentMainScreen.class);
        switch (UserTypeID) {
            case 1:
                currentActivity.startActivity(teacherIntent);
                currentActivity.finish();
                break;
            case 2:
                currentActivity.startActivity(teacherIntent);
                currentActivity.finish();
                break;
            case 3:
                currentActivity.startActivity(studentIntent);
                currentActivity.finish();
                break;
            case 4:
                currentActivity.startActivity(teacherIntent);
                currentActivity.finish();
                break;
            case 5:
                currentActivity.startActivity(studentIntent);
                currentActivity.finish();
                break;
        }
        dismiss();
    }
}

