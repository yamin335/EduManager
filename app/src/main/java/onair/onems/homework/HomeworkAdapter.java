package onair.onems.homework;

import android.app.Activity;
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

import java.util.ArrayList;

import onair.onems.R;

public class HomeworkAdapter extends RecyclerView.Adapter<HomeworkAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<JSONObject> homeworkList;
    private HomeworkAdapterListener listener;


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView homeworkName;

        MyViewHolder(View view) {
            super(view);
            homeworkName = view.findViewById(R.id.name);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected homework in callback
                    listener.onHomeworkSelected(homeworkList.get(getAdapterPosition()));
                }
            });
        }
    }


    HomeworkAdapter(Context context, ArrayList<JSONObject> homeworkList, HomeworkAdapterListener listener) {
        this.context = context;
        this.homeworkList = homeworkList;
        this.listener = listener;
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
            holder.homeworkName.setText(homeworkList.get(position).getString("SubjectName"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return homeworkList.size();
    }

    public interface HomeworkAdapterListener {
        void onHomeworkSelected(JSONObject homework);
    }
}
