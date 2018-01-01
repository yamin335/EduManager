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
import onair.onems.mainactivities.AttendanceSheet;
import onair.onems.models.Student;
import onair.onems.models.StudentModel;

/**
 * Created by Ravi Tamada on 18/05/16.
 */
public class TakeAttendanceAdapter extends RecyclerView.Adapter<TakeAttendanceAdapter.MyViewHolder> {

    private Context mContext;
    private List<Student> attendanceSheetList;
    //    TakeAttendanceAdapter
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, roll;
        public ImageView thumbnail, overflow;
        public CheckBox present, absent, late, leave;
        public EditText lateInput;

        public Boolean isPresent, isAbsent, isLate, isLeave;
        public int lateTime;


        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.studentName);
            roll = (TextView) view.findViewById(R.id.studentRoll);
            present = (CheckBox) view.findViewById(R.id.present);
            absent = (CheckBox) view.findViewById(R.id.absent);
            late = (CheckBox) view.findViewById(R.id.lateCheck);
            leave = (CheckBox) view.findViewById(R.id.leave);
            lateInput = (EditText) view.findViewById(R.id.lateInput);
            //thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            //overflow = (ImageView) view.findViewById(R.id.overflow);
        }
    }


    public TakeAttendanceAdapter(Context mContext, List<Student> attendanceSheetList) {
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
        Student studentModel = attendanceSheetList.get(position);
        holder.name.setText(studentModel.getUserFullName());
        holder.roll.setText("Roll"+Integer.toString(studentModel.getRollNo()));
        if(holder.late.isChecked())
        {
            holder.lateInput.setEnabled(true);
        }
        else
        {
            holder.lateInput.setEnabled(false);
        }

        holder.present.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if ( isChecked )
                {
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
                    holder.lateInput.setEnabled(true);
                    holder.absent.setChecked(false);
                    holder.present.setChecked(false);
                    holder.leave.setChecked(false);
                }
                else
                {
                    holder.lateInput.setEnabled(false);
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
