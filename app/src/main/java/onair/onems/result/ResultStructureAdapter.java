package onair.onems.result;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import onair.onems.R;

public class ResultStructureAdapter extends RecyclerView.Adapter<ResultStructureAdapter.MyViewHolder> {
    private Context context;
    private List<JSONObject> resultList;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView grade, gpa, markRange;

        MyViewHolder(View view) {
            super(view);
            grade = view.findViewById(R.id.grade);
            gpa = view.findViewById(R.id.gpa);
            markRange = view.findViewById(R.id.markRange);
        }
    }


    public ResultStructureAdapter(Context context, List<JSONObject> resultList) {
        this.context = context;
        this.resultList = resultList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.result_grade_structure_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        JSONObject data = resultList.get(position);
        try {
            holder.grade.setText(data.getString("GradeName"));
            holder.gpa.setText(data.getString("GPA"));
            holder.markRange.setText(data.getString("FromMarks")+" - "+data.getString("ToMarks"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }
}
