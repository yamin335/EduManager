package onair.onems.lesson_plan;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import onair.onems.R;

public class LessonPlanAdapter extends RecyclerView.Adapter<LessonPlanAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<JSONObject> lessonPlanList;
    private LessonPlanAdapterListener listener;


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView LessonPlanName;

        MyViewHolder(View view) {
            super(view);
            LessonPlanName = view.findViewById(R.id.name);

            view.setOnClickListener(view1 -> {
                // send selected homework in callback
                listener.onLessonPlanSelected(lessonPlanList.get(getAdapterPosition()));
            });
        }
    }


    LessonPlanAdapter(Context context, ArrayList<JSONObject> lessonPlanList, LessonPlanAdapterListener listener) {
        this.context = context;
        this.lessonPlanList = lessonPlanList;
        this.listener = listener;
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
            holder.LessonPlanName.setText(lessonPlanList.get(position).getString("SubjectName"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return lessonPlanList.size();
    }

    public interface LessonPlanAdapterListener {
        void onLessonPlanSelected(JSONObject lessonPlan);
    }
}
