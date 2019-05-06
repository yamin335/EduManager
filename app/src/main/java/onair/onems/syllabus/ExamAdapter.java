package onair.onems.syllabus;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import onair.onems.R;

public class ExamAdapter extends RecyclerView.Adapter<ExamAdapter.MyViewHolder> {
    private JSONArray exams;
    private ExamAdapterListener selectedListener;
    private ExamAdapterListener dismissListener;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView examName;

        MyViewHolder(View view) {
            super(view);
            examName = view.findViewById(R.id.teacherName);

            view.setOnClickListener(view1 -> {
                // send selected exam in callback
                try {
                    selectedListener.onExamSelected(exams.getJSONObject(getAdapterPosition()));
                    dismissListener.onExamSelected(exams.getJSONObject(getAdapterPosition()));
                } catch (JSONException e) {
                    e.printStackTrace();
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

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.selection_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        try {
            JSONObject exam = exams.getJSONObject(position);
            if (exam.getString("IsActive").equalsIgnoreCase("1")) {
                holder.examName.setText(exam.getString("ExamName"));
            }
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
