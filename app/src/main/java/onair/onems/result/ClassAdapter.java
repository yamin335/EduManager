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

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.MyViewHolder> {
    private JSONArray classes;
    private ClassAdapterListener selectedListener;
    private ClassAdapterListener dismissListener;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView className;

        MyViewHolder(View view) {
            super(view);
            className = view.findViewById(R.id.name);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected exam in callback
                    try {
                        selectedListener.onClassSelected(classes.getJSONObject(getAdapterPosition()));
                        dismissListener.onClassSelected(classes.getJSONObject(getAdapterPosition()));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }


    ClassAdapter(Activity currentActivity, String classes, ClassAdapterListener dismissListener) {
        this.dismissListener = dismissListener;
        this.selectedListener = (ResultGradeStructure)currentActivity;
        try {
            this.classes = new JSONArray(classes);
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
            JSONObject classObject = classes.getJSONObject(position);
            holder.className.setText(classObject.getString("ClassName"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return classes.length();
    }

    public interface ClassAdapterListener {
        void onClassSelected(JSONObject classObject);
    }
}
