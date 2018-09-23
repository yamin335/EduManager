package onair.onems.crm;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;

import onair.onems.R;

public class ImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<Bitmap> bitmapArray;
    private ArrayList<File> fileArray;
    private static final int VCARD = 1;
    private static final int PHOTO = 2;
    private int imageType;
    private DeleteImage deleteImage;
    private ViewImage viewImage;

    class MyViewHolder extends RecyclerView.ViewHolder {
        Button cross;
        ImageView image;

        MyViewHolder(View view) {
            super(view);
            cross = view.findViewById(R.id.cross);
            image = itemView.findViewById(R.id.image);
        }
    }


    public ImageAdapter(Context context, ArrayList<Bitmap> bitmapArray, ArrayList<File> fileArray, int imageType, DeleteImage deleteImage, ViewImage viewImage) {
        this.context = context;
        this.bitmapArray = bitmapArray;
        this.fileArray = fileArray;
        this.imageType = imageType;
        this.deleteImage = deleteImage;
        this.viewImage = viewImage;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.client_image_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyViewHolder viewHolder = (MyViewHolder)holder;
        viewHolder.image.setImageBitmap(bitmapArray.get(position));
        viewHolder.cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteImage.onImageDeleted(holder.getAdapterPosition(), imageType);
            }
        });
        viewHolder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewImage.onImageSelected(holder.getAdapterPosition(), imageType);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bitmapArray.size();
    }

    public interface DeleteImage {
        void onImageDeleted(int position, int type);
    }

    public interface ViewImage {
        void onImageSelected(int position, int type);
    }
}
