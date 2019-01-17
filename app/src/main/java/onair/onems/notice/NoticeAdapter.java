package onair.onems.notice;

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

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.MyViewHolder>
        implements Filterable {
    private Context context;
    private List<JSONObject> noticeList;
    private List<JSONObject> noticeListFiltered;
    private NoticeAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, subtitle, date;

        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.noticeTitle);
            subtitle = view.findViewById(R.id.noticeSubtitle);
            date = view.findViewById(R.id.noticeDate);

            view.setOnClickListener(view1 -> {
                // send selected contact in callback
                listener.onNoticeSelected(noticeListFiltered.get(getAdapterPosition()));
            });
        }
    }


    NoticeAdapter(Context context, List<JSONObject> noticeList, NoticeAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.noticeList = noticeList;
        this.noticeListFiltered = noticeList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notice_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final JSONObject notice = noticeListFiltered.get(position);
        try {
            holder.title.setText(notice.getString("NoticeHead"));
            holder.subtitle.setText(notice.getString("NoticeBody"));
            holder.date.setText(notice.getString("NoticeDate"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return noticeListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    noticeListFiltered = noticeList;
                } else {
                    List<JSONObject> filteredList = new ArrayList<>();
                    for (JSONObject row : noticeList) {

                        try{
                            if (row.getString("NoticeHead").toLowerCase().contains(charString.toLowerCase()) ||
                                    row.getString("NoticeDate").toLowerCase().contains(charString.toLowerCase())) {
                                filteredList.add(row);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    noticeListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = noticeListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                noticeListFiltered = (ArrayList<JSONObject>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface NoticeAdapterListener {
        void onNoticeSelected(JSONObject notice);
    }
}
