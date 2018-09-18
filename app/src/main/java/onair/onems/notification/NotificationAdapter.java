package onair.onems.notification;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import onair.onems.R;
import onair.onems.mainactivities.SideNavigationMenuParentActivity;
import onair.onems.models.Contact;

/**
 * Created by ravi on 16/11/17.
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder>
        implements Filterable {
    private Context context;
    private int notificationCounter;
    private ArrayList<JSONObject> notificationList;
    private ArrayList<JSONObject> notificationListFiltered;
    private NotificationAdapterListener listener;
    private DecreaseCounterListener decreaseCounterListener;
    private IncreaseCounterListener increaseCounterListener;
    private NotificationSaverListener notificationSaverListener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, detail, icon;
        public RelativeLayout viewBackground;
        public ConstraintLayout viewForeground;

        public MyViewHolder(View view) {
            super(view);
            icon = view.findViewById(R.id.icon);
            title = view.findViewById(R.id.title);
            detail = view.findViewById(R.id.detail);
            viewBackground = view.findViewById(R.id.view_background);
            viewForeground = view.findViewById(R.id.view_foreground);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onNotificationSelected(notificationListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }


    public NotificationAdapter(Context context, ArrayList<JSONObject> notificationList, NotificationAdapterListener listener,
                               DecreaseCounterListener decreaseCounterListener, IncreaseCounterListener increaseCounterListener,
                               NotificationSaverListener notificationSaverListener) {
        this.context = context;
        this.listener = listener;
        this.notificationCounter = context.getSharedPreferences("UNSEEN_NOTIFICATIONS", Context.MODE_PRIVATE)
                .getInt("unseen", 0);
        this.notificationList = notificationList;
        this.notificationListFiltered = notificationList;
        this.decreaseCounterListener = decreaseCounterListener;
        this.increaseCounterListener = increaseCounterListener;
        this.notificationSaverListener = notificationSaverListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final JSONObject jsonObject = notificationListFiltered.get(position);
        try {
            holder.title.setText(jsonObject.getString("title").toUpperCase());
            holder.detail.setText(jsonObject.getString("body"));
            if(jsonObject.getInt("seen") == 0) {
                holder.title.setTypeface(Typeface.DEFAULT_BOLD);
            } else {
                holder.title.setTypeface(Typeface.DEFAULT);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        Glide.with(context)
//                .load(R.drawable.image1)
//                .apply(RequestOptions.circleCropTransform())
//                .into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return notificationListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    notificationListFiltered = notificationList;
                } else {
                    ArrayList<JSONObject> filteredList = new ArrayList<>();
                    for (JSONObject row : notificationList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        try {
                            if (row.getString("title").toLowerCase().contains(charString.toLowerCase())) {
                                filteredList.add(row);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    notificationListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = notificationListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                notificationListFiltered = (ArrayList<JSONObject>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface NotificationAdapterListener {
        void onNotificationSelected(JSONObject jsonObject);
    }

    public interface NotificationSaverListener {
        void onNotificationChanged(ArrayList<JSONObject> notificationListFiltered, int counter);
    }

    public void removeItem(int position) {
        try {
            if(notificationListFiltered.get(position).getInt("seen") == 0) {
                if(notificationCounter>0){
                    notificationCounter--;
                }
                decreaseCounterListener.onNotificationDeleted(notificationCounter);
                context.getSharedPreferences("UNSEEN_NOTIFICATIONS", Context.MODE_PRIVATE)
                        .edit()
                        .putInt("unseen", notificationCounter)
                        .apply();
            }
            notificationListFiltered.remove(position);
            // notify the item removed by position
            // to perform recycler view delete animations
            // NOTE: don't call notifyDataSetChanged()
            notifyItemRemoved(position);
            notificationSaverListener.onNotificationChanged(notificationListFiltered, notificationCounter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void restoreItem(JSONObject jsonObject, int position) {
        try {
            if(jsonObject.getInt("seen") == 0) {
                notificationCounter++;
                increaseCounterListener.onNotificationUndo(notificationCounter);
                context.getSharedPreferences("UNSEEN_NOTIFICATIONS", Context.MODE_PRIVATE)
                        .edit()
                        .putInt("unseen", notificationCounter)
                        .apply();
            }
            notificationListFiltered.add(position, jsonObject);
            // notify item added by position
            notifyItemInserted(position);
            notificationSaverListener.onNotificationChanged(notificationListFiltered, notificationCounter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public interface DecreaseCounterListener {
        void onNotificationDeleted(int i);
    }

    public interface IncreaseCounterListener {
        void onNotificationUndo(int i);
    }
    
}
