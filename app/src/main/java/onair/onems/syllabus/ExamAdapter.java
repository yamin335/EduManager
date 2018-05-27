package onair.onems.syllabus;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import onair.onems.R;

public class ExamAdapter extends RecyclerView.Adapter<ExamAdapter.MyViewHolder> {
    private JSONArray exams;
    private ExamAdapterListener selectedListener;
    private ExamAdapterListener dismissListener;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView examName;

        MyViewHolder(View view) {
            super(view);
            examName = view.findViewById(R.id.name);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected exam in callback
                    try {
                        selectedListener.onExamSelected(exams.getJSONObject(getAdapterPosition()));
                        dismissListener.onExamSelected(exams.getJSONObject(getAdapterPosition()));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }


    ExamAdapter(Activity currentActivity, String exams, ExamAdapterListener dismissListener) {
        this.dismissListener = dismissListener;
        this.selectedListener = (SyllabusMainScreen)currentActivity;
        try {
            this.exams = new JSONArray(exams);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.syllabus_exam_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        try {
            JSONObject exam = exams.getJSONObject(position);
            holder.examName.setText(exam.getString("CustomName"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return exams.length();
    }

    public interface ExamAdapterListener {
        void onExamSelected(JSONObject exam);
    }
}
