package onair.onems.exam;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import onair.onems.R;

public class StudentListAdapter extends RecyclerView.Adapter<StudentListAdapter.MyViewHolder> {
    private Context context;
    private JSONArray studentList;
    private StudentListAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, roll;
        public ImageView done;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.teacherName);
            roll = view.findViewById(R.id.roll);
            done = view.findViewById(R.id.done);

            view.setOnClickListener(holderView -> {
                try {
                    listener.onStudentSelected(studentList.getJSONObject(getAdapterPosition()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        }
    }


    StudentListAdapter(Context context, JSONArray studentList, StudentListAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.studentList = studentList;
    }

    @NonNull
    @Override
    public StudentListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.exam_student_list_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentListAdapter.MyViewHolder holder, int position) {
        try {
            JSONObject student = studentList.getJSONObject(position);
            holder.roll.setText(student.getString("RollNo"));
            holder.name.setText(student.getString("UserName"));
            if (student.getLong("ExamMarkID") == 0) {
                holder.done.setVisibility(View.INVISIBLE);
            } else {
                holder.done.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return studentList.length();
    }

    public interface StudentListAdapterListener {
        void onStudentSelected(JSONObject result);
    }
}
