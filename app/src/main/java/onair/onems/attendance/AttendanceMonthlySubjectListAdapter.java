package onair.onems.attendance;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.JsonObject;

import java.util.List;

import onair.onems.R;

public class AttendanceMonthlySubjectListAdapter extends RecyclerView.Adapter<AttendanceMonthlySubjectListAdapter.MyViewHolder> {

    private Context mContext;
    private List<JsonObject> attendanceSheetList;
    private AttendanceMonthlySubjectListAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView serial, subject, teacherName, status;

        public MyViewHolder(View view) {
            super(view);
            serial = view.findViewById(R.id.serial);
            subject = view.findViewById(R.id.subject);
            teacherName = view.findViewById(R.id.teacherName);
            status = view.findViewById(R.id.status);

            view.setOnClickListener(view1 -> {
                // send selected contact in callback
                listener.onSubjectSelected(attendanceSheetList.get(getAdapterPosition()));
            });
        }
    }


    AttendanceMonthlySubjectListAdapter(Context mContext, List<JsonObject> attendanceSheetList, AttendanceMonthlySubjectListAdapterListener listener) {
        this.mContext = mContext;
        this.attendanceSheetList = attendanceSheetList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AttendanceMonthlySubjectListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.attendance_report_subject_list_header, parent, false);
            return new MyViewHolder(itemView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.attendance_report_subject_list_row, parent, false);
            return new MyViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceMonthlySubjectListAdapter.MyViewHolder holder, int position) {
        JsonObject object = attendanceSheetList.get(position);
        if (position != 0) {
            holder.serial.setText(Integer.toString(position));
            if (!object.get("Subject").isJsonNull()) {
                holder.subject.setText(object.get("Subject").getAsString());
            }
            if (!object.get("ClassTeacher").isJsonNull()) {
                holder.teacherName.setText(object.get("ClassTeacher").getAsString());
            }
            if (!object.get("Status").isJsonNull()) {
                switch (object.get("Status").getAsInt()) {
                    case 0: holder.status.setText("Leave");
                        break;
                    case 1: holder.status.setText("Present");
                        break;
                    case 2: holder.status.setText("Absent");
                        break;
                    case 3: holder.status.setText("Late");
                        break;
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return attendanceSheetList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        } else {
            return 1;
        }
    }

    public interface AttendanceMonthlySubjectListAdapterListener {
        void onSubjectSelected(JsonObject object);
    }
}
