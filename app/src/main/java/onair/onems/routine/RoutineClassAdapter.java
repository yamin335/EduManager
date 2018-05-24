package onair.onems.routine;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import onair.onems.R;

public class RoutineClassAdapter extends RecyclerView.Adapter<RoutineClassAdapter.MyViewHolder> {
    private Context context;
    private JSONArray classJsonArray;
    private long UserTypeID;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView subjectName;
        TextView classTime;
        TextView teacherName;
        TextView className;

        MyViewHolder(View view) {
            super(view);
            subjectName = view.findViewById(R.id.subjectName);
            classTime = view.findViewById(R.id.classTime);
            teacherName = view.findViewById(R.id.teacherName);
            className = view.findViewById(R.id.className);

        }
    }

    RoutineClassAdapter(Context context, String classes, long UserTypeID) {
        this.context = context;
        this.UserTypeID = UserTypeID;

        try {
            this.classJsonArray = new JSONArray(classes);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.routine_class_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        JSONObject object = null;
        try {
            object = classJsonArray.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(UserTypeID == 1){
            holder.teacherName.setVisibility(View.GONE);
            try {
                if(!object.getBoolean("IsBreak")){
                    String value = object.getString("CSTName").split(":")[1];
                    String[] s = value.split("_");
                    holder.className.setText("Class: "+s[0]);
                    String sbjctName = "";
                    for (int i = 0; i<s.length; i++) {
                        if(i!=0) {
                            sbjctName = sbjctName.concat(s[i]).concat("\n");
                        }
                    }
                    holder.subjectName.setText(sbjctName);
                } else {
                    holder.className.setText("Break");
                }
                holder.classTime.setText(object.getString("StartTime")+" - "+object.getString("EndTime"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if(UserTypeID == 2){
            holder.teacherName.setVisibility(View.GONE);
            try {
                if(!object.getBoolean("IsBreak")){
                    String value = object.getString("CSTName").split(":")[1];
                    String[] s = value.split("_");
                    holder.className.setText("Class: "+s[0]);
                    String sbjctName = "";
                    for (int i = 0; i<s.length; i++) {
                        if(i!=0) {
                            sbjctName = sbjctName.concat(s[i]).concat("\n");
                        }
                    }
                    holder.subjectName.setText(sbjctName);
                } else {
                    holder.className.setText("Break");
                }
                holder.classTime.setText(object.getString("StartTime")+" - "+object.getString("EndTime"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if(UserTypeID == 3) {
            holder.className.setVisibility(View.GONE);
            try {
                holder.subjectName.setText(object.getString("SubjectName").replace(",", ",\n"));
                holder.classTime.setText(object.getString("StartTime")+" - "+object.getString("EndTime"));
                holder.teacherName.setText("Teacher: "+object.getString("TeacherName").replace("_", ",\n"));
//                if(!object.getBoolean("IsBreak")) {
//
//                } else {
//                    holder.teacherName.setText("Break");
//                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if(UserTypeID == 4) {
            holder.teacherName.setVisibility(View.GONE);
            try {
                holder.className.setText("Class: "+object.getString("ClassName").replace(",", ",\n"));
                holder.subjectName.setText(object.getString("SubjectName"));
                holder.classTime.setText(object.getString("StartTime")+" - "+object.getString("EndTime"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if(UserTypeID == 5){
            holder.className.setVisibility(View.GONE);
            try {
                holder.subjectName.setText(object.getString("SubjectName").replace(",", ",\n"));
                holder.classTime.setText(object.getString("StartTime")+" - "+object.getString("EndTime"));
                holder.teacherName.setText("Teacher: "+object.getString("TeacherName").replace("_", ",\n"));
//                if(!object.getBoolean("IsBreak")){
//
//                } else {
//                    holder.teacherName.setText("Break");
//                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return classJsonArray.length();
    }

}
