package onair.onems.syllabus;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import onair.onems.R;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.MyViewHolder> {
    private JSONArray subjects;
    private SubjectAdapterListener selectedListener;
    private SubjectAdapterListener dismissListener;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView subjectName;

        MyViewHolder(View view) {
            super(view);
            subjectName = view.findViewById(R.id.name);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    try {
                        selectedListener.onSubjectSelected(subjects.getJSONObject(getAdapterPosition()));
                        dismissListener.onSubjectSelected(subjects.getJSONObject(getAdapterPosition()));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }


    SubjectAdapter(Activity currentActivity, String subjects, SubjectAdapterListener dismissListener) {
        this.selectedListener = (SyllabusMainScreen)currentActivity;
        this.dismissListener = dismissListener;
        try {
            this.subjects = new JSONArray(subjects);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.selection_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        try {
            JSONObject subject = subjects.getJSONObject(position);
            holder.subjectName.setText(subject.getString("SubjectName"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return subjects.length();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public interface SubjectAdapterListener {
        void onSubjectSelected(JSONObject subject);
    }
}
