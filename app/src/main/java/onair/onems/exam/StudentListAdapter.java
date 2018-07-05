package onair.onems.exam;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import onair.onems.R;

public class StudentListAdapter extends RecyclerView.Adapter<StudentListAdapter.MyViewHolder> {
    private Context context;
    private JSONArray studentList;
    private StudentListAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, roll;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            roll = view.findViewById(R.id.roll);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        listener.onStudentSelected(studentList.getJSONObject(getAdapterPosition()));
//                        Toast.makeText(context,studentList.getJSONObject(getAdapterPosition()).getString("UserName")+" -- Clicked!!!",
//                                Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.exam_student_list_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        try {
            final JSONObject student = studentList.getJSONObject(position);
            holder.roll.setText(student.getString("RollNo"));
            holder.name.setText(student.getString("UserName"));
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
