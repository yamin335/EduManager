package onair.onems.crm;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import onair.onems.R;
import onair.onems.utils.FileUtils;

public class FileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<Uri> uriArray;
    private DeleteUri deleteUri;

    class MyViewHolder extends RecyclerView.ViewHolder {
        Button cross;
        ImageView image;
        TextView name;

        MyViewHolder(View view) {
            super(view);
            cross = view.findViewById(R.id.cross);
            image = view.findViewById(R.id.image);
            name = view.findViewById(R.id.name);
        }
    }


    public FileAdapter(Context context, ArrayList<Uri> uriArray, DeleteUri deleteUri) {
        this.context = context;
        this.uriArray = uriArray;
        this.deleteUri = deleteUri;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.client_file_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyViewHolder viewHolder = (MyViewHolder)holder;
        viewHolder.image.setImageResource(R.drawable.file_icon);
//        viewHolder.image.setImageBitmap(bitmapArray.get(position));
        viewHolder.name.setText(FileUtils.getFile(context, uriArray.get(position)).getName());
        viewHolder.cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUri.onUriDeleted(holder.getAdapterPosition());
            }
        });

    }

    @Override
    public int getItemCount() {
        return uriArray.size();
    }

    public interface DeleteUri {
        void onUriDeleted(int position);
    }
}
