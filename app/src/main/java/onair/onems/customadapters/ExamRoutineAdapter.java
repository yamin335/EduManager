package onair.onems.customadapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import onair.onems.R;

public class ExamRoutineAdapter extends RecyclerView.Adapter<ExamRoutineAdapter.MyViewHolder> {
    private Activity parentActivity;
    private Context context;
    private JSONArray periodJsonArray;
    private JSONArray allPeriodJsonArray;
    private int classNumber;
    private int sessionNumber;
    private ArrayList<String> differentClassName;
    private ArrayList<Integer> differentClassId;
    private ArrayList<ArrayList<JSONObject>> classWiseExamRoutine;
    private ArrayList<ArrayList<ArrayList<JSONObject>>> sessionWiseExamRoutine;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView className;
        LinearLayout parentLinearLayout;

        MyViewHolder(View view) {
            super(view);
            className = view.findViewById(R.id.className);
            parentLinearLayout = view.findViewById(R.id.classWiseListView);
        }
    }

    public ExamRoutineAdapter(Context context, String classes, Activity parentActivity) {
        this.context = context;
        this.parentActivity = parentActivity;
        try {
            JSONArray allClassJsonArray = new JSONArray(classes);
            prepareExamRoutine(allClassJsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.exam_routine_class_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        try {
            String className = differentClassName.get(position);
            holder.className.setText(className);

            for(int i = 0; i < sessionWiseExamRoutine.get(position).size(); i++) {
                View sessionView = getSessionRowItemView(sessionWiseExamRoutine.get(position).get(i).get(0).getString("SessionName"));

                final ArrayList<JSONObject> exams = sessionWiseExamRoutine.get(position).get(i);

                sessionView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ExamRoutineDialog examRoutineDialog = new ExamRoutineDialog(parentActivity, exams, context);
                        examRoutineDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        examRoutineDialog.setCancelable(false);
                        examRoutineDialog.show();
                    }
                });

                holder.parentLinearLayout.addView(sessionView, holder.parentLinearLayout.getChildCount() - 1);
            }
//            holder.periodTime.setText(periodJsonArray.getJSONObject(position).getString("StartTime")+" - "+periodJsonArray.getJSONObject(position).getString("EndTime"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.className.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.parentLinearLayout.isShown()){
                    Animation slideUp = AnimationUtils.loadAnimation(context, R.anim.slide_up);
                    slideUp.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            holder.parentLinearLayout.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    if(slideUp != null){
                        slideUp.reset();
                        if(holder.parentLinearLayout != null){
                            holder.parentLinearLayout.clearAnimation();
                            holder.parentLinearLayout.startAnimation(slideUp);
                        }
                    }
                } else {
                    Animation slideDown = AnimationUtils.loadAnimation(context, R.anim.slide_down);
                    if(slideDown != null){
                        slideDown.reset();
                        if(holder.parentLinearLayout != null){
                            holder.parentLinearLayout.clearAnimation();
                            holder.parentLinearLayout.startAnimation(slideDown);
                        }
                    }
                    holder.parentLinearLayout.setVisibility(View.VISIBLE);
                    holder.parentLinearLayout.requestFocus();
                }
            }
        });
        holder.parentLinearLayout.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return classNumber;
    }

    private void prepareExamRoutine(JSONArray jsonArray) {
        differentClassName = new ArrayList<>();
        differentClassId = new ArrayList<>();
        for(int i = 0; i < jsonArray.length(); i++) {
            try {
                if((!differentClassId.contains(jsonArray.getJSONObject(i).getInt("ClassID")))
                        &&(jsonArray.getJSONObject(i).getString("IsActive").equalsIgnoreCase("true"))) {
                    differentClassId.add(jsonArray.getJSONObject(i).getInt("ClassID"));
                    differentClassName.add(jsonArray.getJSONObject(i).getString("ClassName"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        classNumber = differentClassId.size();
        classWiseExamRoutine = new ArrayList<>();
        for(int i = 0; i < classNumber; i++) {
            classWiseExamRoutine.add(i, new ArrayList<JSONObject>());
        }
        for(int i = 0; i < jsonArray.length(); i++) {
            try {
                if(jsonArray.getJSONObject(i).getString("IsActive").equalsIgnoreCase("true")) {
                    classWiseExamRoutine.get(differentClassId.indexOf(jsonArray.getJSONObject(i).getInt("ClassID"))).add(jsonArray.getJSONObject(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        sessionWiseExamRoutine = new ArrayList<>();
        for(int i = 0; i < classNumber; i++) {
            sessionWiseExamRoutine.add(i, new ArrayList<ArrayList<JSONObject>>());
            ArrayList<Integer> differentSessionId = new ArrayList<>();
            for(int j = 0; j < classWiseExamRoutine.get(i).size(); j++) {
                try {
                    if((!differentSessionId.contains(classWiseExamRoutine.get(i).get(j).getInt("SessionID")))) {
                        differentSessionId.add(classWiseExamRoutine.get(i).get(j).getInt("SessionID"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            sessionNumber = differentSessionId.size();
            for(int j = 0; j < sessionNumber; j++) {
                sessionWiseExamRoutine.get(i).add(j, new ArrayList<JSONObject>());
            }

            for(int j = 0; j < classWiseExamRoutine.get(i).size(); j++) {
                try {
                    sessionWiseExamRoutine.get(i).get(differentSessionId.indexOf(classWiseExamRoutine.get(i).get(j).getInt("SessionID"))).add(classWiseExamRoutine.get(i).get(j));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
//        int number = sessionWiseExamRoutine.size();
    }

    private View getSessionRowItemView(String string) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.exam_routine_session_row_item, null);
        TextView sessionName = rowView.findViewById(R.id.sessionName);
        sessionName.setText(string);

        return rowView;
    }
}
