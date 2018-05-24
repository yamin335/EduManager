package onair.onems.exam;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import onair.onems.R;

public class SessionRoutineAdapter extends RecyclerView.Adapter<SessionRoutineAdapter.MyViewHolder> {
    private Activity parentActivity;
    private Context context;
    private ArrayList<ArrayList<JSONObject>> sessionWiseExamRoutine;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView sessionName;

        MyViewHolder(View view) {
            super(view);
            sessionName = view.findViewById(R.id.sessionName);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ArrayList<JSONObject> exams = sessionWiseExamRoutine.get(getAdapterPosition());
                    ExamRoutineDialog examRoutineDialog = new ExamRoutineDialog(parentActivity, exams, context);
                    examRoutineDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    examRoutineDialog.setCancelable(false);
                    examRoutineDialog.show();
                }
            });
        }
    }

    SessionRoutineAdapter(Activity parentActivity, ArrayList<ArrayList<JSONObject>> sessionWiseExamRoutine, Context context) {
        this.context = context;
        this.parentActivity = parentActivity;
        this.sessionWiseExamRoutine = sessionWiseExamRoutine;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.exam_routine_session_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        try {
            holder.sessionName.setText(sessionWiseExamRoutine.get(position).get(0).getString("SessionName"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return sessionWiseExamRoutine.size();
    }

}
