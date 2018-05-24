package onair.onems.exam;

import android.content.Context;
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

public class ExamRoutineExamListAdapter extends RecyclerView.Adapter<ExamRoutineExamListAdapter.MyViewHolder> {
    private Context context;
    private ExamRoutineExamListAdapterListener listener;
    private int examNumber;
    private ArrayList<JSONObject> differentExams;
    private ArrayList<Integer> differentExamId;
    private ArrayList<ArrayList<JSONObject>> examWiseExamResult;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView examName;

        MyViewHolder(View view) {
            super(view);
            examName = view.findViewById(R.id.exam);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onExamSelected(examWiseExamResult.get(getAdapterPosition()));
                }
            });
        }
    }


    public ExamRoutineExamListAdapter(Context context, ArrayList<JSONObject> examList, ExamRoutineExamListAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        countDifferentExams(examList);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.exam_routine_exam_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final JSONObject exam = differentExams.get(position);
        try {
            holder.examName.setText(exam.getString("CustomName"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return examNumber;
    }

    public interface ExamRoutineExamListAdapterListener {
        void onExamSelected(ArrayList<JSONObject> examList);
    }

    private void countDifferentExams(ArrayList<JSONObject> examList) {
        differentExams = new ArrayList<>();
        differentExamId = new ArrayList<>();
        for(int i = 0; i < examList.size(); i++) {
            try {
                if(!differentExamId.contains(examList.get(i).getInt("ExamID"))) {
                    differentExamId.add(examList.get(i).getInt("ExamID"));
                    differentExams.add(examList.get(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        examNumber = differentExamId.size();

        examWiseExamResult = new ArrayList<>();

        for (int i = 0; i < examNumber; i++) {
            examWiseExamResult.add(i, new ArrayList<JSONObject>());
        }

        for (int i =0; i < examList.size(); i++) {
            try {
                examWiseExamResult.get(differentExamId.indexOf(examList.get(i).getInt("ExamID"))).add(examList.get(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
