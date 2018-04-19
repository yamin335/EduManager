package onair.onems.customadapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

import onair.onems.R;
import onair.onems.models.ResultModel;

public class ResultStructureAdapter extends RecyclerView.Adapter<ResultStructureAdapter.MyViewHolder> {
    private Context context;
    private List<ResultModel> resultList;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView ExamName, PublishedDate;

        MyViewHolder(View view) {
            super(view);
            ExamName = view.findViewById(R.id.examName);
            PublishedDate = view.findViewById(R.id.publishedDate);
        }
    }


    public ResultStructureAdapter(Context context, List<ResultModel> resultList) {
        this.context = context;
        this.resultList = resultList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.result_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final ResultModel resultModel = resultList.get(position);
        holder.ExamName.setText(resultModel.getExamName());
        holder.PublishedDate.setText(resultModel.getPublishDate());
    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }
}
