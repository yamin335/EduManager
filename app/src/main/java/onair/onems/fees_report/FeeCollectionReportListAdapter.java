package onair.onems.fees_report;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import onair.onems.R;
import onair.onems.customised.MyDividerItemDecoration;
import onair.onems.routine.RoutineClassAdapter;

public class FeeCollectionReportListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private JSONArray allReportJsonArray;
    private int  ReportType;

    class CollectionViewHolder extends RecyclerView.ViewHolder {
        TextView mediumView;
        TextView classView;
        TextView departmentView;
        TextView sectionView;
        TextView totalStudentView;
        RecyclerView recyclerView;
        CollectionViewHolder(View view) {
            super(view);
            mediumView = view.findViewById(R.id.medium);
            classView = view.findViewById(R.id.classs);
            departmentView = view.findViewById(R.id.department);
            sectionView = view.findViewById(R.id.section);
            totalStudentView = view.findViewById(R.id.totalStudent);
            recyclerView = view.findViewById(R.id.recycler);
        }
    }

    class SummaryViewHolder extends RecyclerView.ViewHolder {
        TextView branchView;
        RecyclerView recyclerView;
        SummaryViewHolder(View view) {
            super(view);
            branchView = view.findViewById(R.id.branchName);
            recyclerView = view.findViewById(R.id.recycler);
        }
    }

    FeeCollectionReportListAdapter(Context context, String reports, int  ReportType) {
        this.context = context;
        this.ReportType = ReportType;
        try {
            this.allReportJsonArray = new JSONArray(reports);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case 1:
                View collectionReportView = inflater.inflate(R.layout.fee_collection_report_sublist, parent, false);
                viewHolder = new CollectionViewHolder(collectionReportView);
                break;
            case 2:
                View summaryReportView = inflater.inflate(R.layout.fee_summary_report_sublist, parent, false);
                viewHolder = new SummaryViewHolder(summaryReportView);
                break;
            default:
                View defaultReportView = inflater.inflate(R.layout.fee_collection_report_sublist, parent, false);
                viewHolder = new CollectionViewHolder(defaultReportView);
                break;
        }
        return viewHolder;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return ReportType;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {

        switch (holder.getItemViewType()){
            case 1:
                CollectionViewHolder collectionViewHolder = (CollectionViewHolder)holder;
                try {
                    collectionViewHolder.mediumView.setText("Medium: "+allReportJsonArray.getJSONObject(position).getString("Medium"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    collectionViewHolder.mediumView.setText("Medium: ");
                }

                try {
                    collectionViewHolder.classView.setText("Class: "+allReportJsonArray.getJSONObject(position).getString("Class"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    collectionViewHolder.classView.setText("Class: ");
                }

                try {
                    collectionViewHolder.departmentView.setText("Department: "+allReportJsonArray.getJSONObject(position).getString("Department"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    collectionViewHolder.departmentView.setText("Department: ");
                }

                try {
                    collectionViewHolder.sectionView.setText("Section: "+allReportJsonArray.getJSONObject(position).getString("Section"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    collectionViewHolder.sectionView.setText("Section: ");
                }

                try {
                    collectionViewHolder.totalStudentView.setText("Total Student: "+allReportJsonArray.getJSONObject(position).getString("TotalStudent"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    collectionViewHolder.totalStudentView.setText("Total Student: ");
                }

                try {
                    FeeCollectionReportSublistAdapter mAdapter = new FeeCollectionReportSublistAdapter(context,
                            allReportJsonArray.getJSONObject(position).getJSONArray("Student"), holder.getItemViewType());
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
                    collectionViewHolder.recyclerView.setLayoutManager(mLayoutManager);
                    collectionViewHolder.recyclerView.setItemAnimator(new DefaultItemAnimator());
                    collectionViewHolder.recyclerView.setAdapter(mAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case 2:
                SummaryViewHolder summaryViewHolder = (SummaryViewHolder)holder;
                try {
                    summaryViewHolder.branchView.setText("Branch: "+allReportJsonArray.getJSONObject(position).getString("Branch"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    summaryViewHolder.branchView.setText("Branch: ");
                }

                try {
                    FeeCollectionReportSublistAdapter mAdapter = new FeeCollectionReportSublistAdapter(context,
                            allReportJsonArray.getJSONObject(position).getJSONArray("Student"), holder.getItemViewType());
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
                    summaryViewHolder.recyclerView.setLayoutManager(mLayoutManager);
                    summaryViewHolder.recyclerView.setItemAnimator(new DefaultItemAnimator());
                    summaryViewHolder.recyclerView.setAdapter(mAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return allReportJsonArray.length();
    }
}
