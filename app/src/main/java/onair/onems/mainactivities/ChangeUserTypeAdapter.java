package onair.onems.mainactivities;

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

public class ChangeUserTypeAdapter extends RecyclerView.Adapter<ChangeUserTypeAdapter.MyViewHolder> {
    private Context context;
    private ChangeUserTypeListener listener;
    private JSONArray UserTypes;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView UserTypeName;

        MyViewHolder(View view) {
            super(view);
            UserTypeName = view.findViewById(R.id.name);

            view.setOnClickListener(view1 -> {
                // send selected contact in callback
                try {
                    listener.onUserTypeSelected(UserTypes.getJSONObject(getAdapterPosition()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        }
    }


    public ChangeUserTypeAdapter(Context context, String UserTypes, ChangeUserTypeListener listener) {
        this.context = context;
        this.listener = listener;
        try {
            this.UserTypes = new JSONArray(UserTypes);
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
            final JSONObject user_type = UserTypes.getJSONObject(position);
            holder.UserTypeName.setText(user_type.getString("UserTypeName"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return UserTypes.length();
    }

    public interface ChangeUserTypeListener {
        void onUserTypeSelected(JSONObject UserType);
    }

}
