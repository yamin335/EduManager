package onair.onems.result;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;
import onair.onems.R;

public class SubjectWiseResultAdapter extends RecyclerView.Adapter<SubjectWiseResultAdapter.MyViewHolder> {
    private Context context;
    private List<JSONObject> subjectWiseResultList;
    private SubjectWiseResultsAdapterListener listener;

     class MyViewHolder extends RecyclerView.ViewHolder {

         TextView SubjectName, Mark, Grade, GradePoint;

         MyViewHolder(View view) {
            super(view);
            SubjectName = view.findViewById(R.id.subject);
            Mark = view.findViewById(R.id.mark);
            Grade = view.findViewById(R.id.grade);
            GradePoint = view.findViewById(R.id.comment);

            view.setOnClickListener(view1 -> listener.onSubjectWiseResultSelected(subjectWiseResultList.get(getAdapterPosition())));
         }
    }


    SubjectWiseResultAdapter(Context context, List<JSONObject> subjectWiseResultModelList, SubjectWiseResultsAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.subjectWiseResultList = subjectWiseResultModelList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.result_subject_wise_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        JSONObject subjectWiseResult = subjectWiseResultList.get(position);
        try {
            if(subjectWiseResult.getString("SubjectName").equalsIgnoreCase("Total")) {
                holder.SubjectName.setTextColor(Color.BLACK);
                holder.SubjectName.setTypeface(null, Typeface.BOLD);

                holder.Mark.setTextColor(Color.BLACK);
                holder.Mark.setTypeface(null, Typeface.BOLD);

                holder.Grade.setTextColor(Color.BLACK);
                holder.Grade.setTypeface(null, Typeface.BOLD);

                holder.GradePoint.setTextColor(Color.BLACK);
                holder.GradePoint.setTypeface(null, Typeface.BOLD);
            } else {
                holder.SubjectName.setTextColor(Color.parseColor("#01466c"));
                holder.SubjectName.setTypeface(null, Typeface.NORMAL);

                holder.Mark.setTextColor(Color.parseColor("#01466c"));
                holder.Mark.setTypeface(null, Typeface.NORMAL);

                holder.Grade.setTextColor(Color.parseColor("#01466c"));
                holder.Grade.setTypeface(null, Typeface.NORMAL);

                holder.GradePoint.setTextColor(Color.parseColor("#01466c"));
                holder.GradePoint.setTypeface(null, Typeface.NORMAL);
            }

            holder.SubjectName.setText(subjectWiseResult.getString("SubjectName")
                    .equalsIgnoreCase("null")?"--":subjectWiseResult.getString("SubjectName"));

            holder.Mark.setText(subjectWiseResult.getString("Total")
                    .equalsIgnoreCase("null")?"--":subjectWiseResult.getString("Total"));

            holder.Grade.setText(subjectWiseResult.getString("Grade")
                    .equalsIgnoreCase("null")?"--":subjectWiseResult.getString("Grade"));
            holder.GradePoint.setText(subjectWiseResult.getString("GradePoint")
                    .equalsIgnoreCase("null")?"--":subjectWiseResult.getString("GradePoint"));
            if (subjectWiseResult.getString("GradePoint").equalsIgnoreCase("0.0")
                    && subjectWiseResult.getString("SubjectName").equalsIgnoreCase("Total")) {
                holder.GradePoint.setText("F");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return subjectWiseResultList.size();
    }

    public interface SubjectWiseResultsAdapterListener {
        void onSubjectWiseResultSelected(JSONObject subjectWiseResult);
    }
}
