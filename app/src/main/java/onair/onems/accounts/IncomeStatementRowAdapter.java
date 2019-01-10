package onair.onems.accounts;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import onair.onems.R;

public class IncomeStatementRowAdapter extends RecyclerView.Adapter<IncomeStatementRowAdapter.MyViewHolder> {
    private Context context;
    private JSONArray allCOAJsonArray;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView COAName, Balance;

        MyViewHolder(View view) {
            super(view);
            COAName = view.findViewById(R.id.COAName);
            Balance = view.findViewById(R.id.Balance);
        }
    }

    IncomeStatementRowAdapter(Context context, JSONArray allCOAJsonArray) {
        this.context = context;
        this.allCOAJsonArray = allCOAJsonArray;
    }

    @Override
    @NonNull
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.acc_income_statement_row_row, parent, false);

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
            JSONObject object = allCOAJsonArray.getJSONObject(position);
            holder.COAName.setText(object.getString("COAName"));
            holder.Balance.setText(object.getString("Balance"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return allCOAJsonArray.length();
    }

}
