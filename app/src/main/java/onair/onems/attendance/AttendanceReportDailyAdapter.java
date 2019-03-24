package onair.onems.attendance;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import onair.onems.R;

 class AttendanceReportDailyAdapter extends RecyclerView.Adapter<AttendanceReportDailyAdapter.MyViewHolder>
        implements Filterable {
    private Context context;
    private List<JsonObject> attendanceList;
    private List<JsonObject> attendanceListFiltered;
    private String isPresent, isAbsent, isLate;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView serial, name, roll, status, inTime;
        public ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            serial = view.findViewById(R.id.serial);
            name = view.findViewById(R.id.teacherName);
            roll = view.findViewById(R.id.roll);
            status = view.findViewById(R.id.status);
            inTime = view.findViewById(R.id.inTime);
        }
    }


    AttendanceReportDailyAdapter(Context context, List<JsonObject> attendanceList, String isPresent, String isAbsent, String isLate) {
        this.context = context;
        this.attendanceList = attendanceList;
        this.attendanceListFiltered = attendanceList;
        this.isPresent = isPresent;
        this.isAbsent = isAbsent;
        this.isLate = isLate;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.attendance_report_daily_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        JsonObject jsonObject = attendanceListFiltered.get(position);
        holder.serial.setText(Integer.toString(position+1));
        holder.name.setText(jsonObject.get("Name").getAsString());
        holder.roll.setText(jsonObject.get("RollNo").getAsString());
        holder.status.setText(jsonObject.get("APLStatus").getAsString());
        holder.inTime.setText(jsonObject.get("MarginTime").getAsString());
    }

    @Override
    public int getItemCount() {
        return attendanceListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                List<JsonObject> filteredList = new ArrayList<>();
                for (JsonObject row : attendanceList) {
                    // name match condition. this might differ depending on your requirement
                    // here we are looking for name or phone number match
                    String APLStatus = row.get("APLStatus").getAsString();
                    if (APLStatus.equalsIgnoreCase(isPresent)||APLStatus.equalsIgnoreCase(isAbsent)||APLStatus.equalsIgnoreCase(isLate)) {
                        filteredList.add(row);
                    }
                }

                attendanceListFiltered = filteredList;

                FilterResults filterResults = new FilterResults();
                filterResults.values = attendanceListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                attendanceListFiltered = (ArrayList<JsonObject>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    void Filter(String isPresent, String isAbsent, String isLate) {
        this.isPresent = isPresent;
        this.isAbsent = isAbsent;
        this.isLate = isLate;
        getFilter().filter("");
    }

    void refreshToMainList() {
        attendanceListFiltered = attendanceList;
        notifyDataSetChanged();
    }

 }
