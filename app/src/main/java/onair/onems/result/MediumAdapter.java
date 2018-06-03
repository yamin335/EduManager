package onair.onems.result;

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
import onair.onems.syllabus.SyllabusMainScreen;

public class MediumAdapter extends RecyclerView.Adapter<MediumAdapter.MyViewHolder> {
    private JSONArray mediums;
    private MediumAdapterListener selectedListener;
    private MediumAdapterListener dismissListener;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView mediumName;

        MyViewHolder(View view) {
            super(view);
            mediumName = view.findViewById(R.id.name);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected exam in callback
                    try {
                        selectedListener.onMediumSelected(mediums.getJSONObject(getAdapterPosition()));
                        dismissListener.onMediumSelected(mediums.getJSONObject(getAdapterPosition()));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }


    MediumAdapter(Activity currentActivity, String mediums, MediumAdapterListener dismissListener) {
        this.dismissListener = dismissListener;
        this.selectedListener = (ResultGradeStructure)currentActivity;
        try {
            this.mediums = new JSONArray(mediums);
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
            JSONObject medium = mediums.getJSONObject(position);
            holder.mediumName.setText(medium.getString("MameName"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mediums.length();
    }

    public interface MediumAdapterListener {
        void onMediumSelected(JSONObject medium);
    }
}
