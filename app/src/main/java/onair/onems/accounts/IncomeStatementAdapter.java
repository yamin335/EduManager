package onair.onems.accounts;

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
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import onair.onems.R;
import onair.onems.customised.MyDividerItemDecoration;

public class IncomeStatementAdapter extends RecyclerView.Adapter<IncomeStatementAdapter.MyViewHolder> {
    private Context context;
    private JSONArray allHeadJsonArray;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView HeadType, Total;
        RecyclerView recyclerView;
        LinearLayout linearLayout;

        MyViewHolder(View view) {
            super(view);
            HeadType = view.findViewById(R.id.HeadType);
            Total = view.findViewById(R.id.Total);
            recyclerView = view.findViewById(R.id.recycler);
            linearLayout = view.findViewById(R.id.linearLayout);
        }
    }

    IncomeStatementAdapter(Context context, String incomeStatementData) {
        this.context = context;
        try {
            this.allHeadJsonArray = new JSONArray(incomeStatementData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    @NonNull
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.acc_income_statement_row, parent, false);

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
        try {
            JSONObject jsonObject = allHeadJsonArray.getJSONObject(position);
            holder.HeadType.setText(jsonObject.getString("HeadType"));
            holder.Total.setText(Double.toString(getTotal(jsonObject.getJSONArray("Ledger"))));

            IncomeStatementRowAdapter mAdapter = new IncomeStatementRowAdapter(context, jsonObject.getJSONArray("Ledger"));
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
            holder.recyclerView.setLayoutManager(mLayoutManager);
            holder.recyclerView.setItemAnimator(new DefaultItemAnimator());
            holder.recyclerView.addItemDecoration(new MyDividerItemDecoration(context, DividerItemDecoration.VERTICAL, 4));
            holder.recyclerView.setAdapter(mAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.linearLayout.setOnClickListener(v -> {
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
        return allHeadJsonArray.length();
    }

    private double getTotal(JSONArray jsonArray) {
        double total = 0.0;
        for (int i = 0; i<jsonArray.length(); i++) {
            try {
                total += jsonArray.getJSONObject(i).getDouble("Balance");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return total;
    }
}
