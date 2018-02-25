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
import onair.onems.models.SubjectWiseResultModel;

public class SubjectWiseResultAdapter extends RecyclerView.Adapter<SubjectWiseResultAdapter.MyViewHolder>
        implements Filterable {
    private Context context;
    private List<SubjectWiseResultModel> subjectWiseResultModelList;
    private List<SubjectWiseResultModel> subjectWiseResultModelListFiltered;
    private SubjectWiseResultsAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView SubjectName, Mark, Grade, Comment;

        public MyViewHolder(View view) {
            super(view);
            SubjectName = view.findViewById(R.id.subject);
            Mark = view.findViewById(R.id.mark);
            Grade = view.findViewById(R.id.grade);
            Comment = view.findViewById(R.id.comment);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onSubjectWiseResultSelected(subjectWiseResultModelListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }


    public SubjectWiseResultAdapter(Context context, List<SubjectWiseResultModel> subjectWiseResultModelList, SubjectWiseResultsAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.subjectWiseResultModelList = subjectWiseResultModelList;
        this.subjectWiseResultModelListFiltered = subjectWiseResultModelList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.subject_wise_result_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final SubjectWiseResultModel subjectWiseResultModel = subjectWiseResultModelListFiltered.get(position);
        holder.SubjectName.setText(subjectWiseResultModel.getSubjectName());
        holder.Mark.setText(subjectWiseResultModel.getMark());
        holder.Grade.setText(subjectWiseResultModel.getGrade());
        holder.Comment.setText(subjectWiseResultModel.getComment());
    }

    @Override
    public int getItemCount() {
        return subjectWiseResultModelListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    subjectWiseResultModelListFiltered = subjectWiseResultModelList;
                } else {
                    List<SubjectWiseResultModel> filteredList = new ArrayList<>();
                    for (SubjectWiseResultModel row : subjectWiseResultModelList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getSubjectName().toLowerCase().contains(charString.toLowerCase()) ||
                                row.getMark().contains(charSequence)||
                                row.getGrade().toLowerCase().contains(charString.toLowerCase()) ||
                                row.getComment().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    subjectWiseResultModelListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = subjectWiseResultModelListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                subjectWiseResultModelListFiltered = (ArrayList<SubjectWiseResultModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface SubjectWiseResultsAdapterListener {
        void onSubjectWiseResultSelected(SubjectWiseResultModel subjectWiseResultModel);
    }
}
