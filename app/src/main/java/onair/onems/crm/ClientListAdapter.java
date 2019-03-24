package onair.onems.crm;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import onair.onems.R;

public class ClientListAdapter extends RecyclerView.Adapter<ClientListAdapter.MyViewHolder>
        implements Filterable {
    private Context context;
    private List<JsonObject> clientList;
    private List<JsonObject> clientListFiltered;
    private ClientListAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, institute, address, student, teacher, contact, date, instituteType, priority;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.teacherName);
            institute = view.findViewById(R.id.institute);
            address = view.findViewById(R.id.address);
            student = view.findViewById(R.id.student);
            teacher = view.findViewById(R.id.teacher);
            contact = view.findViewById(R.id.contact);
            date = view.findViewById(R.id.date);
            instituteType = view.findViewById(R.id.textView11);
            priority = view.findViewById(R.id.textView9);

            view.setOnClickListener(view1 -> {
                // send selected client in callback
                listener.onClientSelected(clientListFiltered.get(getAdapterPosition()).getAsJsonObject());
            });
        }
    }


    ClientListAdapter(Context context, List<JsonObject> clientList, ClientListAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.clientList = clientList;
        this.clientListFiltered = clientList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.client_list_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder( @NonNull MyViewHolder holder, final int position) {
        final JsonObject client = clientListFiltered.get(position);
        try {
            holder.name.setText(client.get("ContactPerson").getAsString());
            holder.institute.setText(client.get("InsName").getAsString());
            holder.address.setText(client.get("InstituteAddress").getAsString());
            holder.student.setText(client.get("NoOfStudent").getAsString());
            holder.teacher.setText(client.get("NoOfTeacher").getAsString());
            holder.contact.setText("Mobile: "+client.get("ContactNumber").getAsString());
            holder.date.setText(client.get("EntryDate").getAsString());
            holder.priority.setText(client.get("Priority").getAsString());
            holder.instituteType.setText(client.get("InstituteTypeName").getAsString());
        } catch (JsonIOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return clientListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    clientListFiltered = clientList;
                } else {
                    List<JsonObject> filteredList = new ArrayList<>();
                    for (JsonObject row : clientList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        try {
                            if (row.get("InsName").getAsString().toLowerCase().contains(charString.toLowerCase()) || row.get("ContactPerson").getAsString().contains(charSequence)) {
                                filteredList.add(row);
                            }
                        } catch (JsonIOException e) {
                            e.printStackTrace();
                        }
                    }

                    clientListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = clientListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                clientListFiltered = (ArrayList<JsonObject>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface ClientListAdapterListener {
        void onClientSelected(JsonObject client);
    }
}
