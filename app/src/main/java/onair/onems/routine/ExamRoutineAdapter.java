package onair.onems.routine;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import onair.onems.R;

public class ExamRoutineAdapter extends RecyclerView.Adapter<ExamRoutineAdapter.MyViewHolder> {
    private Context context;
    private JSONArray routine;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView date, subject;

        MyViewHolder(View view) {
            super(view);
            date = view.findViewById(R.id.date);
            subject = view.findViewById(R.id.subject);
        }
    }

    ExamRoutineAdapter(Context context, JSONArray routine) {
        this.context = context;
        this.routine = routine;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.exam_routine_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        try {
            holder.date.setText(routine.getJSONObject(position).getString("ExamDate")+", "+routine.getJSONObject(position).getString("NameDay"));
            holder.subject.setText(routine.getJSONObject(position).getString("SubjectName"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return routine.length();
    }
}
