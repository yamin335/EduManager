package onair.onems.customadapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import onair.onems.R;
import onair.onems.Services.GlideApp;
import onair.onems.icard.StudentiCardDetails;

public class RoutineAdapter extends RecyclerView.Adapter<RoutineAdapter.MyViewHolder> {
    private Context context;
    private JSONArray periodJsonArray;
    private JSONArray allPeriodJsonArray;
    private int periodNumber;
    private long UserTypeID;
    private Bitmap TeacherImage;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView periodName;
        LinearLayout parentLinearLayout;

        MyViewHolder(View view) {
            super(view);
            periodName = view.findViewById(R.id.periodName);
//            periodTime = view.findViewById(R.id.periodTime);
            parentLinearLayout = view.findViewById(R.id.classWiseListView);
        }
    }

    public RoutineAdapter(Context context, String periods, long UserTypeID) {
        this.context = context;
        this.UserTypeID = UserTypeID;
//        this.periodNumber = periodNumber;

        try {
            this.allPeriodJsonArray = new JSONArray(periods);
            if(allPeriodJsonArray.length()>0 && UserTypeID != 3) {
                GlideApp.with(context)
                        .asBitmap()
                        .load(context.getString(R.string.baseUrl)+"/"+allPeriodJsonArray.getJSONObject(0).getString("ImageUrl").replace("\\","/"))
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                if(resource != null) {
                                    TeacherImage = resource;
                                }
                            }
                            @Override
                            public void onLoadFailed(Drawable errorDrawable) {
                                super.onLoadFailed(errorDrawable);
                            }
                        });
            }
            countPeriodNumber(allPeriodJsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.routine_period_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        int PeriodID;
        try {
            String PeriodName = periodJsonArray.getJSONObject(position).getString("PeriodName");
            holder.periodName.setText(PeriodName);
            PeriodID = periodJsonArray.getJSONObject(position).getInt("PeriodID");
            for(int i = 0; i < allPeriodJsonArray.length(); i++) {
                if(allPeriodJsonArray.getJSONObject(i).getInt("PeriodID") == PeriodID) {
                    holder.parentLinearLayout.addView(getClassRowItemView(allPeriodJsonArray.getJSONObject(i)), holder.parentLinearLayout.getChildCount() - 1);
                }
            }
//            holder.periodTime.setText(periodJsonArray.getJSONObject(position).getString("StartTime")+" - "+periodJsonArray.getJSONObject(position).getString("EndTime"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.periodName.setOnClickListener(new View.OnClickListener() {
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
//        holder.parentLinearLayout.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return periodNumber;
    }

    private void countPeriodNumber(JSONArray jsonArray) {
        periodJsonArray = new JSONArray();
        ArrayList<Integer> arrayList = new ArrayList<>();
        for(int i = 0; i < jsonArray.length(); i++) {
            try {
                if(!arrayList.contains(jsonArray.getJSONObject(i).getInt("PeriodID"))) {
                    arrayList.add(jsonArray.getJSONObject(i).getInt("PeriodID"));
                    periodJsonArray.put(jsonArray.getJSONObject(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        periodNumber = arrayList.size();
    }

    private View getClassRowItemView(JSONObject object) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.routine_class_row_item, null);
        TextView subjectName = rowView.findViewById(R.id.subjectName);
        TextView classTime = rowView.findViewById(R.id.classTime);
        TextView teacherName = rowView.findViewById(R.id.teacherName);
        TextView className = rowView.findViewById(R.id.className);
        ImageView teacherImage = rowView.findViewById(R.id.teacherImage);
        teacherImage.setImageBitmap(TeacherImage);
        if(UserTypeID == 3) {
            className.setVisibility(View.GONE);
        } else if(UserTypeID == 4) {
            try {
                className.setText(object.getString("ClassName"));
                teacherName.setVisibility(View.GONE);
                teacherImage.setVisibility(View.GONE);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        try {
            subjectName.setText(object.getString("SubjectName"));
            classTime.setText(object.getString("StartTime")+" - "+object.getString("EndTime"));
            teacherName.setText(object.getString("TeacherName"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return rowView;
    }
}
