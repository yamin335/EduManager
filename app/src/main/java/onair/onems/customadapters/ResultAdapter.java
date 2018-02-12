package onair.onems.customadapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import onair.onems.R;
import onair.onems.models.ResultModel;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.MyViewHolder>
        implements Filterable {
    private Context context;
    private List<ResultModel> resultList;
    private List<ResultModel> resultListFiltered;
    private ResultsAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView ExamName, PublishedDate;

        public MyViewHolder(View view) {
            super(view);
            ExamName = view.findViewById(R.id.examName);
            PublishedDate = view.findViewById(R.id.publishedDate);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onResultSelected(resultListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }


    public ResultAdapter(Context context, List<ResultModel> resultList, ResultsAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.resultList = resultList;
        this.resultListFiltered = resultList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.result_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final ResultModel resultModel = resultListFiltered.get(position);
        holder.ExamName.setText(resultModel.getExamName());
        holder.PublishedDate.setText(resultModel.getPublishDate());
    }

    @Override
    public int getItemCount() {
        return resultListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    resultListFiltered = resultList;
                } else {
                    List<ResultModel> filteredList = new ArrayList<>();
                    for (ResultModel row : resultList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getExamName().toLowerCase().contains(charString.toLowerCase()) || row.getPublishDate().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    resultListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = resultListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                resultListFiltered = (ArrayList<ResultModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface ResultsAdapterListener {
        void onResultSelected(ResultModel resultModel);
    }
}
