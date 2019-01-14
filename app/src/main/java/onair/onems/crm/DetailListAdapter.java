package onair.onems.crm;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import onair.onems.R;

public class DetailListAdapter extends RecyclerView.Adapter<DetailListAdapter.MyViewHolder>
        implements Filterable {
    private Context context;
    private List<JSONObject> detailList;
    private List<JSONObject> detailListFiltered;
    private DetailListAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView meetingDate, nextMeetingDate, detailPriority, detailMedium, nextMeetingDateTitle;

        public MyViewHolder(View view) {
            super(view);
            meetingDate = view.findViewById(R.id.date);
            nextMeetingDate = view.findViewById(R.id.textView14);
            detailPriority = view.findViewById(R.id.priority);
            detailMedium = view.findViewById(R.id.medium);
            nextMeetingDateTitle = view.findViewById(R.id.textView12);

            view.setOnClickListener(view1 -> {
                // send selected client in callback
                listener.onDetailSelected(detailListFiltered.get(getAdapterPosition()));
            });
        }
    }


    DetailListAdapter(Context context, List<JSONObject> detailList, DetailListAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.detailList = detailList;
        this.detailListFiltered = detailList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.client_communication_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder( @NonNull MyViewHolder holder, final int position) {
        final JSONObject detailObject = detailListFiltered.get(position);
        try {
            holder.meetingDate.setText(detailObject.getString("CommunicationDate"));
            holder.detailPriority.setText(detailObject.getString("Priority"));
            holder.detailMedium.setText(detailObject.getString("CommunicationType"));
            if (!detailObject.getString("NextMeetingDate").equalsIgnoreCase("")
                    &&!detailObject.getString("NextMeetingDate").equalsIgnoreCase("null")
                    &&!detailObject.getString("NextMeetingDate").equalsIgnoreCase("1900-01-01T00:00:00.000Z")
                    &&!detailObject.getString("NextMeetingDate").equalsIgnoreCase("1900-01-01")) {
                holder.nextMeetingDateTitle.setVisibility(View.VISIBLE);
                holder.nextMeetingDate.setVisibility(View.VISIBLE);
            }
            holder.nextMeetingDate.setText(detailObject.getString("NextMeetingDate").equalsIgnoreCase("")
                    ||detailObject.getString("NextMeetingDate").equalsIgnoreCase("null")
                    ||detailObject.getString("NextMeetingDate").equalsIgnoreCase("1900-01-01T00:00:00.000Z")
                    ||detailObject.getString("NextMeetingDate").equalsIgnoreCase("1900-01-01")? "" : detailObject.getString("NextMeetingDate"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return detailListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    detailListFiltered = detailList;
                } else {
                    List<JSONObject> filteredList = new ArrayList<>();
                    for (JSONObject row : detailList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        try {
                            if (row.getString("CommunicationDate").toLowerCase().contains(charString.toLowerCase()) || row.getString("NextMeetingDate").contains(charSequence)) {
                                filteredList.add(row);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    detailListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = detailListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                detailListFiltered = (ArrayList<JSONObject>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface DetailListAdapterListener {
        void onDetailSelected(JSONObject detail);
    }
}
