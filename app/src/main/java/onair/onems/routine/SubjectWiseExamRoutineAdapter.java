package onair.onems.routine;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import onair.onems.R;

public class SubjectWiseExamRoutineAdapter extends RecyclerView.Adapter<SubjectWiseExamRoutineAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<JSONObject> subjects;

     class MyViewHolder extends RecyclerView.ViewHolder {

         TextView SubjectName, date, time;

         MyViewHolder(View view) {
            super(view);
            SubjectName = view.findViewById(R.id.subject);
            date = view.findViewById(R.id.date);
            time = view.findViewById(R.id.time);
        }
    }


    public SubjectWiseExamRoutineAdapter(Context context, ArrayList<JSONObject> subjects) {
        this.context = context;
        this.subjects = subjects;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.exam_routine_subject_wise_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        try {
            JSONObject subject = subjects.get(position);
            holder.SubjectName.setText(subject.getString("SubjectName"));
            String date = subject.getString("ExamDate").split("T")[0];
            holder.date.setText(date);
            String startTime = subject.getString("StartTime").split("T")[1].split("\\.")[0];
            String endTime = subject.getString("EndTime").split("T")[1].split("\\.")[0];
            holder.time.setText(startTime+" - "+endTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return subjects.size();
    }
}
