package onair.onems.studentlist;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;
import java.util.List;
import onair.onems.R;
import onair.onems.Services.GlideApp;
import onair.onems.models.ReportAllStudentRowModel;

public class ReportAllStudentShowListAdapter extends RecyclerView.Adapter<ReportAllStudentShowListAdapter.MyViewHolder>
        implements Filterable {
    private Context context;
    private List<ReportAllStudentRowModel> studentList;
    private List<ReportAllStudentRowModel> studentListFiltered;
    private ReportAllStudentShowListAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView studentName, studentRoll, studentClass, studentRFID;
        private ImageView thumbnail;
        private ProgressBar progressBar;

        public MyViewHolder(View view) {
            super(view);
            studentName = view.findViewById(R.id.teacherName);
            studentRoll = view.findViewById(R.id.roll);
            studentClass = view.findViewById(R.id.classs);
            studentRFID = view.findViewById(R.id.rfid);
            thumbnail = view.findViewById(R.id.thumbnail);
            progressBar = view.findViewById(R.id.spin_kit);
            view.setOnClickListener( v -> listener.onStudentSelected(studentListFiltered.get(this.getAdapterPosition())));
        }
    }


    ReportAllStudentShowListAdapter(Context context, List<ReportAllStudentRowModel> studentList, ReportAllStudentShowListAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.studentList = studentList;
        this.studentListFiltered = studentList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.report_all_student_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        ReportAllStudentRowModel reportAllStudentRowModel = studentListFiltered.get(position);
        holder.studentName.setText("Name: "+reportAllStudentRowModel.getUserName());
        holder.studentRoll.setText("Roll: "+reportAllStudentRowModel.getRollNo());
        String s = "", d = "";
        if(reportAllStudentRowModel.getSectionName().equals("null")||reportAllStudentRowModel.getSectionName().isEmpty()||reportAllStudentRowModel.getSectionName().equals("")) {
            s = " ";
        } else {
            s = " (Sec:"+reportAllStudentRowModel.getSectionName()+")";
        }

        if(reportAllStudentRowModel.getDepartmentName().equals("null")||reportAllStudentRowModel.getDepartmentName().isEmpty()||reportAllStudentRowModel.getDepartmentName().equals("")) {
            d = " ";
        } else {
            d = " ("+reportAllStudentRowModel.getDepartmentName()+")";
        }
        holder.studentClass.setText("Class: "+reportAllStudentRowModel.getClassName()+s+d);
        holder.studentRFID.setText("RFID: "+reportAllStudentRowModel.getRFID());

        try {
            GlideApp.with(context)
                    .asBitmap()
                    .error(context.getResources().getDrawable(R.drawable.profileavater))
                    .load(context.getString(R.string.baseUrl)+"/"+reportAllStudentRowModel.getImageUrl().replace("\\","/"))
                    .apply(RequestOptions.circleCropTransform())
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, Transition<? super Bitmap> transition) {
                            holder.progressBar.setVisibility(View.GONE);
                            if(resource != null) {
                                holder.thumbnail.setImageBitmap(resource);
                            }
                        }
                        @Override
                        public void onLoadFailed(Drawable errorDrawable) {
                            super.onLoadFailed(errorDrawable);
                            holder.thumbnail.setImageDrawable(errorDrawable);
                            holder.progressBar.setVisibility(View.GONE);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return studentListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    studentListFiltered = studentList;
                } else {
                    List<ReportAllStudentRowModel> filteredList = new ArrayList<>();
                    for (ReportAllStudentRowModel row : studentList) {
                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getUserName().toLowerCase().contains(charString.toLowerCase()) ||
                                row.getRollNo().contains(charSequence)|| row.getDepartmentName().toLowerCase().contains(charString.toLowerCase())||
                                row.getClassName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    studentListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = studentListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                studentListFiltered = (ArrayList<ReportAllStudentRowModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface ReportAllStudentShowListAdapterListener {
        void onStudentSelected(ReportAllStudentRowModel reportAllStudentRowModel);
    }
}
