package onair.onems.customadapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import onair.onems.R;

public class GuardianStudentSelectionAdapter extends RecyclerView.Adapter<GuardianStudentSelectionAdapter.MyViewHolder> {
    private Context context;
    private GuardianStudentSelectionListener listener;
    private JSONArray students;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView studentName;

        MyViewHolder(View view) {
            super(view);
            studentName = view.findViewById(R.id.studentName);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    try {
                        listener.onStudentSelected(students.getJSONObject(getAdapterPosition()));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }


    public GuardianStudentSelectionAdapter(Context context, JSONArray students, GuardianStudentSelectionListener listener) {
        this.context = context;
        this.listener = listener;
        this.students = students;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.guardian_student_selection_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        try {
            final JSONObject student = students.getJSONObject(position);
            holder.studentName.setText(student.getString("UserFullName"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return students.length();
    }

    public interface GuardianStudentSelectionListener {
        void onStudentSelected(JSONObject student);
    }

}
