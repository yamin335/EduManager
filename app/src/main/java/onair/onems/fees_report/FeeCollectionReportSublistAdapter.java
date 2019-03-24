package onair.onems.fees_report;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import onair.onems.R;

public class FeeCollectionReportSublistAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private JSONArray reportRowJsonArray;
    private int  ReportType;

    class CollectionViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView roll;
        TextView id;
        TextView previousBalance;
        TextView dueAmount;
        TextView paidAmount;
        TextView balance;
        CollectionViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.teacherName);
            roll = view.findViewById(R.id.roll);
            id = view.findViewById(R.id.rfid);
            previousBalance = view.findViewById(R.id.previousBalance);
            dueAmount = view.findViewById(R.id.dueAmount);
            paidAmount = view.findViewById(R.id.paidAmount);
            balance = view.findViewById(R.id.balance);
        }
    }

    class SummaryViewHolder extends RecyclerView.ViewHolder {
        TextView medium;
        TextView classs;
        TextView department;
        TextView section;
        TextView totalStudent;
        TextView previousBalance;
        TextView dueAmount;
        TextView paidAmount;
        TextView balance;
        SummaryViewHolder(View view) {
            super(view);
            medium = view.findViewById(R.id.medium);
            classs = view.findViewById(R.id.classs);
            department = view.findViewById(R.id.dept);
            section = view.findViewById(R.id.section);
            totalStudent = view.findViewById(R.id.totalStudent);
            previousBalance = view.findViewById(R.id.previousBalance);
            dueAmount = view.findViewById(R.id.dueAmount);
            paidAmount = view.findViewById(R.id.paidAmount);
            balance = view.findViewById(R.id.balance);
        }
    }

    FeeCollectionReportSublistAdapter(Context context, JSONArray reportRowJsonArray, int  ReportType) {
        this.context = context;
        this.ReportType = ReportType;
        this.reportRowJsonArray = reportRowJsonArray;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case 1:
                View collectionReportView = inflater.inflate(R.layout.fee_collection_report_row, parent, false);
                viewHolder = new CollectionViewHolder(collectionReportView);
                break;
            case 2:
                View summaryReportView = inflater.inflate(R.layout.fee_summary_report_row, parent, false);
                viewHolder = new SummaryViewHolder(summaryReportView);
                break;
            default:
                View defaultReportView = inflater.inflate(R.layout.fee_collection_report_row, parent, false);
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
                    collectionViewHolder.name.setText(reportRowJsonArray.getJSONObject(position).getString("StudentName"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    collectionViewHolder.roll.setText("Roll: "+reportRowJsonArray.getJSONObject(position).getString("RollNo"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    collectionViewHolder.id.setText("ID: "+reportRowJsonArray.getJSONObject(position).getString("RFID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    collectionViewHolder.previousBalance.setText(reportRowJsonArray.getJSONObject(position).getString("PreviousBalance"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    collectionViewHolder.dueAmount.setText(reportRowJsonArray.getJSONObject(position).getString("DueAmount"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    collectionViewHolder.paidAmount.setText(reportRowJsonArray.getJSONObject(position).getString("CollectedAmount"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    collectionViewHolder.balance.setText(reportRowJsonArray.getJSONObject(position).getString("Balance"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case 2:
                SummaryViewHolder summaryViewHolder = (SummaryViewHolder)holder;
                try {
                    summaryViewHolder.medium.setText(reportRowJsonArray.getJSONObject(position).getString("MameName"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    summaryViewHolder.classs.setText(reportRowJsonArray.getJSONObject(position).getString("ClassName"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    summaryViewHolder.department.setText(reportRowJsonArray.getJSONObject(position).getString("DepartmentName"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                summaryViewHolder.section.setText("Roll: "+reportRowJsonArray.getJSONObject(position).getString("S"));
                try {
                    summaryViewHolder.totalStudent.setText(reportRowJsonArray.getJSONObject(position).getString("totalStudent"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    summaryViewHolder.previousBalance.setText(reportRowJsonArray.getJSONObject(position).getString("PreviousBalance"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    summaryViewHolder.dueAmount.setText(reportRowJsonArray.getJSONObject(position).getString("DueAmount"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    summaryViewHolder.paidAmount.setText(reportRowJsonArray.getJSONObject(position).getString("CollectedAmount"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    summaryViewHolder.balance.setText(reportRowJsonArray.getJSONObject(position).getString("Balance"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return reportRowJsonArray.length();
    }

}
