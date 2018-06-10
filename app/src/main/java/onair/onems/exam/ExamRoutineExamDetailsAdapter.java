package onair.onems.exam;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import onair.onems.R;

public class ExamRoutineExamDetailsAdapter extends RecyclerView.Adapter<ExamRoutineExamDetailsAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<JSONObject> examList;
    private Activity activity;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView exam, date, shift, medium, classs, department, section, session;
        Button details;

        MyViewHolder(View view) {
            super(view);
            exam = view.findViewById(R.id.homework);
            date = view.findViewById(R.id.date);
            shift = view.findViewById(R.id.shift);
            medium = view.findViewById(R.id.medium);
            classs = view.findViewById(R.id.classs);
            department = view.findViewById(R.id.department);
            section = view.findViewById(R.id.section);
            session = view.findViewById(R.id.session);
            details = view.findViewById(R.id.details);

        }
    }


    ExamRoutineExamDetailsAdapter(Context context, ArrayList<JSONObject> examList, Activity activity) {
        this.context = context;
        this.examList = examList;
        this.activity = activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.exam_routine_exam_details_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final JSONObject exam = examList.get(position);
        try {
            holder.exam.setText(exam.getString("CustomName"));
            holder.date.setText(exam.getString("StartDate")+" - "+exam.getString("EndDate"));
            holder.shift.setText(exam.getString("ShiftName"));
            holder.medium.setText(exam.getString("MameName"));
            holder.classs.setText(exam.getString("ClassName"));
            holder.department.setText(exam.getString("DepartmentName"));
            holder.section.setText(exam.getString("SectionName"));
            holder.session.setText(exam.getString("SessionName"));
            holder.details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, SubjectWiseExamRoutine.class);
                    try {
                        intent.putExtra("InstituteID", exam.getString("InstituteID"));
                        intent.putExtra("ExamRoutineID", exam.getString("ExamRoutineID"));
                        activity.startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return examList.size();
    }
}
