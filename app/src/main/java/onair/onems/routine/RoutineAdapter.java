package onair.onems.routine;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import onair.onems.R;
import onair.onems.customised.MyDividerItemDecoration;

public class RoutineAdapter extends RecyclerView.Adapter<RoutineAdapter.MyViewHolder> {
    private Context context;
    private JSONArray allPeriodJsonArray;
    private int periodNumber;
    private long UserTypeID;
    private JSONArray allClassJsonArray;
    private ArrayList<String> nameArray;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView periodName;
        RecyclerView recyclerView;

        MyViewHolder(View view) {
            super(view);
            periodName = view.findViewById(R.id.periodName);
            recyclerView = view.findViewById(R.id.routinePeriods);
        }
    }

    RoutineAdapter(Context context, String periods, long UserTypeID) {
        this.context = context;
        this.UserTypeID = UserTypeID;

        try {
            this.allPeriodJsonArray = new JSONArray(periods);
            countPeriodNumber(allPeriodJsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.routine_period_row_item, parent, false);

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
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.periodName.setText(nameArray.get(position));
        try {
            RoutineClassAdapter mAdapter = new RoutineClassAdapter(context, allClassJsonArray.getJSONArray(position).toString(), UserTypeID);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
            holder.recyclerView.setLayoutManager(mLayoutManager);
            holder.recyclerView.setItemAnimator(new DefaultItemAnimator());
            holder.recyclerView.addItemDecoration(new MyDividerItemDecoration(context, DividerItemDecoration.VERTICAL, 20));
            holder.recyclerView.setAdapter(mAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.periodName.setOnClickListener(v -> {
            if(holder.recyclerView.isShown()){
                Animation slideUp = AnimationUtils.loadAnimation(context, R.anim.slide_up);
                slideUp.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        holder.recyclerView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                if(slideUp != null){
                    slideUp.reset();
                    if(holder.recyclerView != null){
                        holder.recyclerView.clearAnimation();
                        holder.recyclerView.startAnimation(slideUp);
                    }
                }
            } else {
                Animation slideDown = AnimationUtils.loadAnimation(context, R.anim.slide_down);
                if(slideDown != null){
                    slideDown.reset();
                    if(holder.recyclerView != null){
                        holder.recyclerView.clearAnimation();
                        holder.recyclerView.startAnimation(slideDown);
                    }
                }
                holder.recyclerView.setVisibility(View.VISIBLE);
                holder.recyclerView.requestFocus();
            }
        });
    }

    @Override
    public int getItemCount() {
        return periodNumber;
    }

    private void countPeriodNumber(JSONArray jsonArray) {
        ArrayList<Integer> arrayList = new ArrayList<>();
        nameArray = new ArrayList<>();
        for(int i = 0; i < jsonArray.length(); i++) {
            try {
                if(!arrayList.contains(jsonArray.getJSONObject(i).getInt("PeriodID"))) {
                    arrayList.add(jsonArray.getJSONObject(i).getInt("PeriodID"));
                    nameArray.add(jsonArray.getJSONObject(i).getString("PeriodName"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        periodNumber = arrayList.size();

        allClassJsonArray = new JSONArray();
        for (int i = 0; i<periodNumber; i++) {
            allClassJsonArray.put(new JSONArray());
        }

        for (int i = 0; i<allPeriodJsonArray.length(); i++) {
            try {
                allClassJsonArray.getJSONArray(arrayList.indexOf(allPeriodJsonArray.getJSONObject(i)
                        .getInt("PeriodID"))).put(allPeriodJsonArray.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
