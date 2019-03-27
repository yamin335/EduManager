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
import onair.onems.models.DailyAttendanceModel;

public class DateWiseAttendanceAdapter extends RecyclerView.Adapter<DateWiseAttendanceAdapter.MyViewHolder> {

    private Context mContext;
    private List<DailyAttendanceModel> attendanceSheetList;
    private DateWiseAttendanceAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView serial, date, present, late;

        public MyViewHolder(View view) {
            super(view);
            serial = view.findViewById(R.id.serial);
            date = view.findViewById(R.id.date);
            present = view.findViewById(R.id.present);
            late = view.findViewById(R.id.late);

            view.setOnClickListener(view1 -> {
                // send selected contact in callback
                listener.onDateSelected(attendanceSheetList.get(getAdapterPosition()));
            });
        }
    }


    DateWiseAttendanceAdapter(Context mContext, List<DailyAttendanceModel> attendanceSheetList, DateWiseAttendanceAdapterListener listener) {
        this.mContext = mContext;
        this.attendanceSheetList = attendanceSheetList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DateWiseAttendanceAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.attendance_report_self_attendance_header, parent, false);
            return new MyViewHolder(itemView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.attendance_report_self_attendance_row, parent, false);
            return new MyViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull DateWiseAttendanceAdapter.MyViewHolder holder, int position) {
        DailyAttendanceModel attendanceModel = attendanceSheetList.get(position);
        if (position != 0) {
            holder.serial.setText(Integer.toString(position));
            holder.date.setText(attendanceModel.getDate());
            holder.present.setText(attendanceModel.getPresent() == 1 ? "YES" : "NO");
            holder.late.setText(Integer.toString(attendanceModel.getLate()));
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

    public interface DateWiseAttendanceAdapterListener {
        void onDateSelected(DailyAttendanceModel object);
    }
}
