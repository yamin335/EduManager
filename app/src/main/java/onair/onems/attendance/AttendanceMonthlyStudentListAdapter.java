package onair.onems.attendance;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import onair.onems.R;
import onair.onems.models.AllStudentAttendanceModel;
import onair.onems.models.DailyAttendanceModel;

public class AttendanceMonthlyStudentListAdapter extends RecyclerView.Adapter<AttendanceMonthlyStudentListAdapter.MyViewHolder> {

    private Context mContext;
    private List<AllStudentAttendanceModel> attendanceSheetList;
    private AttendanceMonthlyStudentListAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView serial, name, rfid, roll;

        public MyViewHolder(View view) {
            super(view);
            serial = view.findViewById(R.id.serial);
            name = view.findViewById(R.id.name);
            rfid = view.findViewById(R.id.rfid);
            roll = view.findViewById(R.id.roll);

            view.setOnClickListener(view1 -> {
                // send selected contact in callback
                listener.onStudentSelected(attendanceSheetList.get(getAdapterPosition()));
            });
        }
    }


    AttendanceMonthlyStudentListAdapter(Context mContext, List<AllStudentAttendanceModel> attendanceSheetList, AttendanceMonthlyStudentListAdapterListener listener) {
        this.mContext = mContext;
        this.attendanceSheetList = attendanceSheetList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AttendanceMonthlyStudentListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.attendance_monthly_student_list_header, parent, false);
            return new MyViewHolder(itemView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.attendance_monthly_student_list_row, parent, false);
            return new MyViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceMonthlyStudentListAdapter.MyViewHolder holder, int position) {
        AllStudentAttendanceModel attendanceModel = attendanceSheetList.get(position);
        if (position != 0) {
            holder.serial.setText(Integer.toString(position));
            holder.name.setText(attendanceModel.getUserFullName());
            holder.rfid.setText(attendanceModel.getRFID());
            holder.roll.setText(attendanceModel.getRollNo());
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

    public interface AttendanceMonthlyStudentListAdapterListener {
        void onStudentSelected(AllStudentAttendanceModel object);
    }
}
