package onair.onems.attendance;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.JsonObject;

import java.util.List;

import onair.onems.R;

public class AttendanceTeacherDailyAdapter extends RecyclerView.Adapter<AttendanceTeacherDailyAdapter.MyViewHolder> {

    private Context mContext;
    private List<JsonObject> attendanceSheetList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView serial, name, id, designation, phone, inTime, outTime, status, lateTime, leave;
        private ConstraintLayout rootLayout;

        public MyViewHolder(View view) {
            super(view);
            serial = view.findViewById(R.id.serial);
            name = view.findViewById(R.id.teacherName);
            id = view.findViewById(R.id.teacherId);
            designation = view.findViewById(R.id.designation);
            phone = view.findViewById(R.id.pnoneNo);
            inTime = view.findViewById(R.id.inTime);
            outTime = view.findViewById(R.id.outTime);
            status = view.findViewById(R.id.status);
            lateTime = view.findViewById(R.id.lateTime);
            leave = view.findViewById(R.id.isLeave);
        }
    }


    AttendanceTeacherDailyAdapter(Context mContext, List<JsonObject> attendanceSheetList) {
        this.mContext = mContext;
        this.attendanceSheetList = attendanceSheetList;
    }

    @NonNull
    @Override
    public AttendanceTeacherDailyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.attendance_teacher_daily_table_header, parent, false);
            return new MyViewHolder(itemView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.attendance_teacher_daily_row, parent, false);
            return new MyViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceTeacherDailyAdapter.MyViewHolder holder, int position) {
        JsonObject jsonObject = attendanceSheetList.get(position);
        if (position != 0) {
            holder.serial.setText(Integer.toString(position));
            if (!jsonObject.get("Name").isJsonNull()) {
                holder.name.setText(jsonObject.get("Name").getAsString());
            }
            if (!jsonObject.get("RFID").isJsonNull()) {
                holder.id.setText(jsonObject.get("RFID").getAsString());
            }
            if (!jsonObject.get("DesignationName").isJsonNull()) {
                holder.designation.setText(jsonObject.get("DesignationName").getAsString());
            }
            if (!jsonObject.get("PhoneNo").isJsonNull()) {
                holder.phone.setText(jsonObject.get("PhoneNo").getAsString());
            }
            if (!jsonObject.get("Intime").isJsonNull()) {
                holder.inTime.setText(jsonObject.get("Intime").getAsString());
            }
            if (!jsonObject.get("Outtime").isJsonNull()) {
                holder.outTime.setText(jsonObject.get("Outtime").getAsString());
            }
            if (!jsonObject.get("APLStatus").isJsonNull()) {
                holder.status.setText(jsonObject.get("APLStatus").getAsString());
//                switch (jsonObject.get("APLStatus").getAsString()) {
//                    case "P":
//                        holder.status.setBackgroundColor(mContext.getResources().getColor(R.color.present));
//                        break;
//                    case "A":
//                        holder.status.setBackgroundColor(mContext.getResources().getColor(R.color.absent));
//                        break;
//                    case "L":
//                        holder.status.setBackgroundColor(mContext.getResources().getColor(R.color.late));
//                        break;
//
//                }
            }
            if (!jsonObject.get("Leave").isJsonNull()) {
                holder.leave.setText(jsonObject.get("Leave").getAsString().equalsIgnoreCase("0")?"NO":"YES");
            }
            if (!jsonObject.get("LateInMin").isJsonNull()) {
                holder.lateTime.setText(jsonObject.get("LateInMin").getAsInt()>1?jsonObject.get("LateInMin").getAsString():"");
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
}
