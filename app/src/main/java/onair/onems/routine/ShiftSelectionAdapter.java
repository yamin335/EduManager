package onair.onems.routine;

import android.app.Activity;
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

public class ShiftSelectionAdapter extends RecyclerView.Adapter<ShiftSelectionAdapter.MyViewHolder> {
    private JSONArray shifts;
    private ShiftSelectionListener selectedListener;
    private ShiftSelectionListener dismissListener;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView shiftName;

        MyViewHolder(View view) {
            super(view);
            shiftName = view.findViewById(R.id.teacherName);

            view.setOnClickListener(view1 -> {
                // send selected exam in callback
                try {
                    selectedListener.onShiftSelected(shifts.getJSONObject(getAdapterPosition()));
                    dismissListener.onShiftSelected(shifts.getJSONObject(getAdapterPosition()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        }
    }


    ShiftSelectionAdapter(Activity currentActivity, String shifts, ShiftSelectionListener dismissListener) {
        this.dismissListener = dismissListener;
        this.selectedListener = (RoutineMainScreen)currentActivity;
        try {
            this.shifts = new JSONArray(shifts);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.selection_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        try {
            JSONObject shift = shifts.getJSONObject(position);
            holder.shiftName.setText(shift.getString("ShiftName"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return shifts.length();
    }

    public interface ShiftSelectionListener {
        void onShiftSelected(JSONObject shift);
    }
}
