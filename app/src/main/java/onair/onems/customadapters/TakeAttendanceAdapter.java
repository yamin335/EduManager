package onair.onems.customadapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import onair.onems.R;
import onair.onems.models.AttendanceStudentModel;

/**
 * Created by Ravi Tamada on 18/05/16.
 */
public class TakeAttendanceAdapter extends RecyclerView.Adapter<TakeAttendanceAdapter.MyViewHolder> {

    private Context mContext;
    private List<AttendanceStudentModel> attendanceSheetList;
    //    TakeAttendanceAdapter
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, roll;
        public ImageView thumbnail, overflow;
        public CheckBox present, absent, late, leave;
        public EditText lateInput;
        public int isPresent, isAbsent, isLate, isLeave, lateTime;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.studentName);
            roll = (TextView) view.findViewById(R.id.studentRoll);
            present = (CheckBox) view.findViewById(R.id.present);
            absent = (CheckBox) view.findViewById(R.id.absent);
            late = (CheckBox) view.findViewById(R.id.lateCheck);
            leave = (CheckBox) view.findViewById(R.id.leave);
            lateInput = (EditText) view.findViewById(R.id.lateInput);
        }
    }


    public TakeAttendanceAdapter(Context mContext, List<AttendanceStudentModel> attendanceSheetList) {
        this.mContext = mContext;
        this.attendanceSheetList = attendanceSheetList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_card, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        AttendanceStudentModel attendanceStudentModel = attendanceSheetList.get(position);
        holder.name.setText(attendanceStudentModel.getUserFullName());
        holder.roll.setText("Roll: "+attendanceStudentModel.getRollNo());

        holder.isPresent = 1;
        holder.isAbsent = 0;
        holder.isLate = 0;
        holder.isLeave = 0;

        if((!attendanceStudentModel.getSubAtdDetailID().equals("0"))&&
                (!attendanceStudentModel.getSubAtdID().equals("0")))
        {
            holder.isPresent = attendanceStudentModel.getIsPresent();
            holder.isAbsent = attendanceStudentModel.getIsAbsent();
            holder.isLate = attendanceStudentModel.getIslate();
            holder.lateTime = attendanceStudentModel.getLateTime();
            holder.isLeave = attendanceStudentModel.getIsLeave();

            if(holder.isPresent == 1)
            {
                holder.present.setChecked(true);
                holder.lateInput.setText("");
                holder.absent.setChecked(false);
                holder.late.setChecked(false);
                holder.leave.setChecked(false);
            }
            else
            {
                holder.present.setChecked(false);
            }

            if(holder.isAbsent == 1)
            {
                holder.absent.setChecked(true);
                holder.lateInput.setText("");
                holder.present.setChecked(false);
                holder.late.setChecked(false);
                holder.leave.setChecked(false);
            }
            else
            {
                holder.absent.setChecked(false);
            }

            if(holder.isLate == 1)
            {
                holder.late.setChecked(true);
                holder.lateInput.setText(Integer.toString(holder.lateTime));
                holder.absent.setChecked(false);
                holder.present.setChecked(false);
                holder.leave.setChecked(false);
            }
            else
            {
                holder.late.setChecked(false);
                holder.lateInput.setText("");
            }

            if(holder.isLeave == 1)
            {
                holder.leave.setChecked(true);
                holder.lateInput.setText("");
                holder.absent.setChecked(false);
                holder.late.setChecked(false);
                holder.present.setChecked(false);
            }
            else
            {
                holder.leave.setChecked(false);
            }
        }

        if(holder.late.isChecked())
        {
            holder.lateInput.setEnabled(false);
        }
        else
        {
            holder.lateInput.setEnabled(true);
        }

        holder.present.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if ( isChecked )
                {
                    holder.isPresent = 1;
                    holder.isAbsent = 0;
                    holder.isLate = 0;
                    holder.isLeave = 0;
                    attendanceSheetList.get(position).setIsPresent(Integer.toString(holder.isPresent));
                    attendanceSheetList.get(position).setIsAbsent(Integer.toString(holder.isAbsent));
                    attendanceSheetList.get(position).setIslate(Integer.toString(holder.isLate));
                    attendanceSheetList.get(position).setIsLeave(Integer.toString(holder.isLeave));

                    holder.lateInput.setText("");
                    holder.absent.setChecked(false);
                    holder.late.setChecked(false);
                    holder.leave.setChecked(false);
                }

            }
        });

        holder.absent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if ( isChecked )
                {
                    holder.isPresent = 0;
                    holder.isAbsent = 1;
                    holder.isLate = 0;
                    holder.isLeave = 0;
                    attendanceSheetList.get(position).setIsPresent(Integer.toString(holder.isPresent));
                    attendanceSheetList.get(position).setIsAbsent(Integer.toString(holder.isAbsent));
                    attendanceSheetList.get(position).setIslate(Integer.toString(holder.isLate));
                    attendanceSheetList.get(position).setIsLeave(Integer.toString(holder.isLeave));

                    holder.lateInput.setText("");
                    holder.present.setChecked(false);
                    holder.late.setChecked(false);
                    holder.leave.setChecked(false);
                }

            }
        });
        holder.late.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if ( isChecked )
                {
                    holder.isPresent = 0;
                    holder.isAbsent = 0;
                    holder.isLate = 1;
                    holder.isLeave = 0;
                    attendanceSheetList.get(position).setIsPresent(Integer.toString(holder.isPresent));
                    attendanceSheetList.get(position).setIsAbsent(Integer.toString(holder.isAbsent));
                    attendanceSheetList.get(position).setIslate(Integer.toString(holder.isLate));
                    attendanceSheetList.get(position).setIsLeave(Integer.toString(holder.isLeave));

                    holder.lateInput.setEnabled(false);

//                    holder.lateInput.setEnabled(true);
                    if(holder.lateInput.getText().toString().isEmpty())
                    {
                        holder.lateTime = 0;
                        Toast.makeText(mContext,"Please input first !!!",Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        holder.lateTime = Integer.parseInt(holder.lateInput.getText().toString());
                    }
                    attendanceSheetList.get(position).setLateTime(Integer.toString(holder.lateTime));


                    holder.absent.setChecked(false);
                    holder.present.setChecked(false);
                    holder.leave.setChecked(false);
                }
                else
                {
                    holder.lateInput.setEnabled(true);
                }

            }
        });
        holder.leave.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if ( isChecked )
                {
                    holder.isPresent = 0;
                    holder.isAbsent = 0;
                    holder.isLate = 0;
                    holder.isLeave = 1;
                    attendanceSheetList.get(position).setIsPresent(Integer.toString(holder.isPresent));
                    attendanceSheetList.get(position).setIsAbsent(Integer.toString(holder.isAbsent));
                    attendanceSheetList.get(position).setIslate(Integer.toString(holder.isLate));
                    attendanceSheetList.get(position).setIsLeave(Integer.toString(holder.isLeave));

                    holder.lateInput.setText("");
                    holder.absent.setChecked(false);
                    holder.late.setChecked(false);
                    holder.present.setChecked(false);
                }

            }
        });

        // loading attendanceSheet cover using Glide library
        // Glide.with(mContext).load(attendanceSheet.getThumbnail()).into(holder.thumbnail);

//        holder.overflow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showPopupMenu(holder.overflow);
//            }
//        });

    }

    /**
     * Showing popup menu when tapping on 3 dots
     */
//    private void showPopupMenu(View view) {
//        // inflate menu
//        PopupMenu popup = new PopupMenu(mContext, view);
//        MenuInflater inflater = popup.getMenuInflater();
//        inflater.inflate(R.menu.menu_album, popup.getMenu());
//        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
//        popup.show();
//    }

    /**
     * Click listener for popup menu items
     */
//    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
//
//        public MyMenuItemClickListener() {
//        }
//
//        @Override
//        public boolean onMenuItemClick(MenuItem menuItem) {
//            switch (menuItem.getItemId()) {
//                case R.id.action_add_favourite:
//                    Toast.makeText(mContext, "Add to favourite", Toast.LENGTH_SHORT).show();
//                    return true;
//                case R.id.action_play_next:
//                    Toast.makeText(mContext, "Play next", Toast.LENGTH_SHORT).show();
//                    return true;
//                default:
//            }
//            return false;
//        }
//    }

    @Override
    public int getItemCount() {
        return attendanceSheetList.size();
    }
}
