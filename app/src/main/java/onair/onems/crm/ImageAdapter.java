package onair.onems.crm;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import onair.onems.R;
import onair.onems.Services.GlideApp;

public class ImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<Bitmap> bitmapArray;
    private ArrayList<File> fileArray;
    private static final int VCARD = 1;
    private static final int PHOTO = 2;
    private int imageType;
    private DeleteImage deleteImage;
    private ViewImage viewImage;
    private boolean forUpdate = false;
    private ArrayList <JSONObject> imageUrls;

    class MyViewHolder extends RecyclerView.ViewHolder {
        Button cross;
        ImageView image;

        MyViewHolder(View view) {
            super(view);
            cross = view.findViewById(R.id.cross);
            image = itemView.findViewById(R.id.image);
            if (forUpdate) {
                cross.setVisibility(View.GONE);
                cross.setClickable(false);
            }
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

    public ImageAdapter(Context context, ArrayList <JSONObject> imageUrls, int imageType, boolean forUpdate, ViewImage viewImage) {
        this.context = context;
        this.imageUrls = imageUrls;
        this.forUpdate = forUpdate;
        this.viewImage = viewImage;
        this.imageType = imageType;
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
        if (forUpdate) {
            String imageUrl = "";
            try {
                if (imageType == VCARD) {
                    imageUrl = imageUrls.get(position).getString("VCardURL");
                } else if (imageType == PHOTO) {
                    imageUrl = imageUrls.get(position).getString("PhotoURL");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            GlideApp.with(context)
                    .asBitmap()
                    .load("http://172.16.1.2:4000"+"/"+imageUrl.replace("\\","/"))
//                    .load(context.getString(R.string.baseUrl)+"/"+imageUrl.replace("\\","/"))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .skipMemoryCache(false)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, Transition<? super Bitmap> transition) {
                            viewHolder.image.setImageBitmap(resource);
                        }
                        @Override
                        public void onLoadFailed(Drawable errorDrawable){
                            super.onLoadFailed(errorDrawable);
                        }
                    });
            viewHolder.image.setOnClickListener(v -> viewImage.onImageSelected(holder.getAdapterPosition(), imageType, ((BitmapDrawable)viewHolder.image.getDrawable()).getBitmap()));
        } else {
            viewHolder.image.setImageBitmap(bitmapArray.get(position));
            viewHolder.cross.setOnClickListener(v -> deleteImage.onImageDeleted(holder.getAdapterPosition(), imageType));
            Bitmap bitmap = null;
            viewHolder.image.setOnClickListener(v -> viewImage.onImageSelected(holder.getAdapterPosition(), imageType, bitmap));
        }
    }

    @Override
    public int getItemCount() {
        if (forUpdate) {
            return imageUrls.size();
        } else {
            return bitmapArray.size();
        }

    }

    public interface DeleteImage {
        void onImageDeleted(int position, int type);
    }

    public interface ViewImage {
        void onImageSelected(int position, int type, Bitmap bitmap);
    }
}
