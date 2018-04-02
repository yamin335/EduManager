package onair.onems.customadapters;

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

import java.util.ArrayList;

import onair.onems.R;

public class RoutineAdapter extends RecyclerView.Adapter<RoutineAdapter.MyViewHolder> {
    private Context context;
    private JSONArray periodJsonArray;
    private JSONArray allPeriodJsonArray;
    private int periodNumber;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView periodName;
        LinearLayout parentLinearLayout;

        MyViewHolder(View view) {
            super(view);
            periodName = view.findViewById(R.id.periodName);
//            periodTime = view.findViewById(R.id.periodTime);
            parentLinearLayout = view.findViewById(R.id.classWiseListView);
        }
    }

    public RoutineAdapter(Context context, String periods, int periodNumber) {
        this.context = context;
//        this.periodNumber = periodNumber;

        try {
            this.allPeriodJsonArray = new JSONArray(periods);
            countPeriodNumber(allPeriodJsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.routine_period_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        int PeriodID;
        try {
            String PeriodName = periodJsonArray.getJSONObject(position).getString("PeriodName");
            holder.periodName.setText(PeriodName);
            PeriodID = periodJsonArray.getJSONObject(position).getInt("PeriodID");
//            holder.periodTime.setText(periodJsonArray.getJSONObject(position).getString("StartTime")+" - "+periodJsonArray.getJSONObject(position).getString("EndTime"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for(int i = 0; i < allPeriodJsonArray.length(); i++) {

        }
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.routine_class_row_item, null);
        final View rowView1 = inflater.inflate(R.layout.routine_class_row_item, null);
        final View rowView2 = inflater.inflate(R.layout.routine_class_row_item, null);
        final View rowView3 = inflater.inflate(R.layout.routine_class_row_item, null);
        // Add the new row before the add field button.
        holder.parentLinearLayout.addView(rowView, holder.parentLinearLayout.getChildCount() - 1);
        holder.parentLinearLayout.addView(rowView1, holder.parentLinearLayout.getChildCount() - 1);
        holder.parentLinearLayout.addView(rowView2, holder.parentLinearLayout.getChildCount() - 1);
        holder.parentLinearLayout.addView(rowView3, holder.parentLinearLayout.getChildCount() - 1);
        holder.periodName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.parentLinearLayout.isShown()){
                    Animation slideUp = AnimationUtils.loadAnimation(context, R.anim.slide_up);
                    slideUp.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            holder.parentLinearLayout.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    if(slideUp != null){
                        slideUp.reset();
                        if(holder.parentLinearLayout != null){
                            holder.parentLinearLayout.clearAnimation();
                            holder.parentLinearLayout.startAnimation(slideUp);
                        }
                    }
                } else {
                    Animation slideDown = AnimationUtils.loadAnimation(context, R.anim.slide_down);
                    if(slideDown != null){
                        slideDown.reset();
                        if(holder.parentLinearLayout != null){
                            holder.parentLinearLayout.clearAnimation();
                            holder.parentLinearLayout.startAnimation(slideDown);
                        }
                    }
                    holder.parentLinearLayout.setVisibility(View.VISIBLE);
                }
            }
        });
        holder.parentLinearLayout.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return periodNumber;
    }

    private void countPeriodNumber(JSONArray jsonArray) {
        periodJsonArray = new JSONArray();
        ArrayList<Integer> arrayList = new ArrayList<>();
        for(int i = 0; i < jsonArray.length(); i++) {
            try {
                if(!arrayList.contains(jsonArray.getJSONObject(i).getInt("PeriodID"))) {
                    arrayList.add(jsonArray.getJSONObject(i).getInt("PeriodID"));
                    periodJsonArray.put(jsonArray.getJSONObject(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        periodNumber = arrayList.size();
    }

    private View getClassRowItem() {

    }
}
