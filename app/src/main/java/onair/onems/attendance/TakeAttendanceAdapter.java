package onair.onems.attendance;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import onair.onems.R;
import onair.onems.models.AttendanceStudentModel;

public class TakeAttendanceAdapter extends RecyclerView.Adapter<TakeAttendanceAdapter.MyViewHolder> {

    private Context mContext;
    private List<AttendanceStudentModel> attendanceSheetList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, roll;
        public ImageView thumbnail;
        CheckBox present, absent, late, leave;
        EditText lateInput;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.studentName);
            roll = view.findViewById(R.id.studentRoll);
            present = view.findViewById(R.id.present);
            absent = view.findViewById(R.id.absent);
            late = view.findViewById(R.id.lateCheck);
            leave = view.findViewById(R.id.leave);
            lateInput = view.findViewById(R.id.lateInput);
        }
    }


    TakeAttendanceAdapter(Context mContext, List<AttendanceStudentModel> attendanceSheetList) {
        this.mContext = mContext;
        this.attendanceSheetList = attendanceSheetList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.attendance_taking_row_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        AttendanceStudentModel attendanceStudentModel = attendanceSheetList.get(position);
        holder.name.setText(attendanceStudentModel.getUserFullName());
        holder.roll.setText("Roll: "+ attendanceStudentModel.getRollNo());

        if((!attendanceStudentModel.getSubAtdDetailID().equals("0"))&&
                (!attendanceStudentModel.getSubAtdID().equals("0"))) {
            if(attendanceStudentModel.getIsPresent() == 1) {
                holder.present.setChecked(true);
            } else {
                holder.present.setChecked(false);
            }

            if(attendanceStudentModel.getIsAbsent() == 1) {
                holder.absent.setChecked(true);
            } else {
                holder.absent.setChecked(false);
            }

            if(attendanceStudentModel.getIslate() == 1) {
                holder.lateInput.setText(Integer.toString(attendanceStudentModel.getLateTime()));
                holder.late.setChecked(true);
            } else {
                holder.late.setChecked(false);
                holder.lateInput.setText("");
            }

            if(attendanceStudentModel.getIsLeave() == 1) {
                holder.leave.setChecked(true);
            } else {
                holder.leave.setChecked(false);
            }
        } else if(attendanceStudentModel.getSubAtdDetailID().equals("0")&&
                attendanceStudentModel.getSubAtdID().equals("0")){

            if(attendanceStudentModel.getIsPresent() == 1) {
                holder.present.setChecked(true);
            } else {
                holder.present.setChecked(false);
            }

            if(attendanceStudentModel.getIsAbsent() == 1) {
                holder.absent.setChecked(true);
            } else {
                holder.absent.setChecked(false);
            }

            if(attendanceStudentModel.getIslate() == 1) {
                holder.lateInput.setText(Integer.toString(attendanceStudentModel.getLateTime()));
                holder.late.setChecked(true);
            } else {
                holder.late.setChecked(false);
                holder.lateInput.setText("");
            }

            if(attendanceStudentModel.getIsLeave() == 1) {
                holder.leave.setChecked(true);
            } else {
                holder.leave.setChecked(false);
            }
        }

        if(holder.late.isChecked()) {
            holder.lateInput.setEnabled(false);
        } else {
            holder.lateInput.setEnabled(true);
        }

        holder.present.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                attendanceSheetList.get(holder.getAdapterPosition()).setIsPresent("1");
                attendanceSheetList.get(holder.getAdapterPosition()).setIsAbsent("0");
                attendanceSheetList.get(holder.getAdapterPosition()).setIslate("0");
                attendanceSheetList.get(holder.getAdapterPosition()).setIsLeave("0");
                attendanceSheetList.get(holder.getAdapterPosition()).setLateTime("0");

                holder.lateInput.setText("");
                holder.absent.setChecked(false);
                holder.late.setChecked(false);
                holder.leave.setChecked(false);
            }

        });

        holder.absent.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                attendanceSheetList.get(holder.getAdapterPosition()).setIsPresent("0");
                attendanceSheetList.get(holder.getAdapterPosition()).setIsAbsent("1");
                attendanceSheetList.get(holder.getAdapterPosition()).setIslate("0");
                attendanceSheetList.get(holder.getAdapterPosition()).setIsLeave("0");
                attendanceSheetList.get(holder.getAdapterPosition()).setLateTime("0");

                holder.lateInput.setText("");
                holder.present.setChecked(false);
                holder.late.setChecked(false);
                holder.leave.setChecked(false);
            }

        });

        holder.late.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                attendanceSheetList.get(holder.getAdapterPosition()).setIsPresent("0");
                attendanceSheetList.get(holder.getAdapterPosition()).setIsAbsent("0");
                attendanceSheetList.get(holder.getAdapterPosition()).setIslate("1");
                attendanceSheetList.get(holder.getAdapterPosition()).setIsLeave("0");

                holder.lateInput.setEnabled(false);

                if(holder.lateInput.getText().toString().equalsIgnoreCase("")) {
                    attendanceSheetList.get(holder.getAdapterPosition()).setLateTime("0");
                    Toast.makeText(mContext,"Please input first !!!",Toast.LENGTH_LONG).show();
                } else {
                    attendanceSheetList.get(holder.getAdapterPosition()).setLateTime(holder.lateInput.getText().toString());
                }

                holder.absent.setChecked(false);
                holder.present.setChecked(false);
                holder.leave.setChecked(false);
            } else {
                holder.lateInput.setEnabled(true);
            }

        });

        holder.leave.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                attendanceSheetList.get(holder.getAdapterPosition()).setIsPresent("0");
                attendanceSheetList.get(holder.getAdapterPosition()).setIsAbsent("0");
                attendanceSheetList.get(holder.getAdapterPosition()).setIslate("0");
                attendanceSheetList.get(holder.getAdapterPosition()).setLateTime("0");
                attendanceSheetList.get(holder.getAdapterPosition()).setIsLeave("1");

                holder.lateInput.setText("");
                holder.absent.setChecked(false);
                holder.late.setChecked(false);
                holder.present.setChecked(false);
            }

        });
    }

    @Override
    public int getItemCount() {
        return attendanceSheetList.size();
    }
}
