package onair.onems.customadapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import onair.onems.R;
import onair.onems.models.NoticeModel;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.MyViewHolder>
        implements Filterable {
    private Context context;
    private List<NoticeModel> noticeList;
    private List<NoticeModel> noticeListFiltered;
    private NoticeAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, subtitle, date, time;

        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.noticeTitle);
            subtitle = view.findViewById(R.id.noticeSubtitle);
            date = view.findViewById(R.id.noticeDate);
            time = view.findViewById(R.id.noticeTime);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onNoticeSelected(noticeListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }


    public NoticeAdapter(Context context, List<NoticeModel> noticeList, NoticeAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.noticeList = noticeList;
        this.noticeListFiltered = noticeList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notice_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final NoticeModel noticeModel = noticeListFiltered.get(position);
        holder.title.setText(noticeModel.getTitle());
        holder.subtitle.setText(noticeModel.getSubtitle());
        holder.date.setText(noticeModel.getDate());
        holder.time.setText(noticeModel.getTime());
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
                    List<NoticeModel> filteredList = new ArrayList<>();
                    for (NoticeModel row : noticeList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getTitle().toLowerCase().contains(charString.toLowerCase()) ||
                                row.getDate().toLowerCase().contains(charString.toLowerCase())||
                                row.getTime().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
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
                noticeListFiltered = (ArrayList<NoticeModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface NoticeAdapterListener {
        void onNoticeSelected(NoticeModel noticeModel);
    }
}
