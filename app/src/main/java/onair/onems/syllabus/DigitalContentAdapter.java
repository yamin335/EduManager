package onair.onems.syllabus;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import onair.onems.R;

public class DigitalContentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<JSONObject> contentList;
    private AddFileToDownloader downloader;
    private ViewImageInFullScreen viewImageInFullScreen;
    private static final int IMAGE_TYPE = 1;
    private static final int FILE_TYPE = 2;
    private static final int AUDIO_TYPE = 3;
    private static final int VIDEO_TYPE = 4;
    public enum ContentType {SYLLABUS, LESSON, HOMEWORK}
    private ContentType contentType;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView contentName;
        ImageView play, download;

        MyViewHolder(View view) {
            super(view);
            contentName = view.findViewById(R.id.filename);
            play = itemView.findViewById(R.id.play);
            download = itemView.findViewById(R.id.download);
        }
    }


    public DigitalContentAdapter(Context context, AddFileToDownloader downloader, ViewImageInFullScreen viewImageInFullScreen, ArrayList<JSONObject> contentList, ContentType contentType) {
        this.contentType = contentType;
        this.context = context;
        this.contentList = contentList;
        this.downloader = downloader;
        this.viewImageInFullScreen = viewImageInFullScreen;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.syllabus_grid_item, parent, false);
        ImageView image = itemView.findViewById(R.id.image);
        ImageView play = itemView.findViewById(R.id.play);
        ImageView download = itemView.findViewById(R.id.download);
        if(viewType == IMAGE_TYPE){
            image.setImageResource(R.drawable.image_icon);
            play.setImageResource(R.drawable.preview_icon);
            play.setClickable(true);
            download.setImageResource(R.drawable.download_icon);
            return new MyViewHolder(itemView);
        } else if(viewType == AUDIO_TYPE) {
            image.setImageResource(R.drawable.audio_icon);
            play.setImageResource(R.drawable.play_icon);
            download.setImageResource(R.drawable.download_icon);
            return new MyViewHolder(itemView);
        } else if(viewType == VIDEO_TYPE) {
            image.setImageResource(R.drawable.video_icon);
            play.setImageResource(R.drawable.play_icon);
            download.setImageResource(R.drawable.download_icon);
            return new MyViewHolder(itemView);
        } else if(viewType == FILE_TYPE) {
            image.setImageResource(R.drawable.file_icon);
//            play.setImageResource(R.drawable.preview_icon);
            download.setImageResource(R.drawable.download_icon);
            return new MyViewHolder(itemView);
        } else {
            return new MyViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String title = "";
        String url = "";
        try {
            JSONObject jsonObject = contentList.get(position);
            title = jsonObject.getString("FileName");
            switch (contentType){
                case SYLLABUS:
                    url = jsonObject.getString("SyllabusUrl");
                    break;
                case LESSON:
                    url = jsonObject.getString("ContentUrl");
                    break;
                case HOMEWORK:
                    url = jsonObject.getString("FileURL");
                    break;
            }
            url = url.replace("\\","/");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(holder.getItemViewType() == IMAGE_TYPE) {
            MyViewHolder viewHolder = (MyViewHolder)holder;
            viewHolder.contentName.setText(title);
            final String finalUrl = url;
            viewHolder.download.setOnClickListener(v -> {
                if(!finalUrl.equalsIgnoreCase("null")){
                    downloader.downloadFile(finalUrl);
                }
            });
            viewHolder.play.setOnClickListener(v -> {
                if(!finalUrl.equalsIgnoreCase("null")){
                    viewImageInFullScreen.onViewPressed(finalUrl);
                }
            });
        } else if(holder.getItemViewType() == AUDIO_TYPE) {
            MyViewHolder viewHolder = (MyViewHolder)holder;
            viewHolder.contentName.setText(title);
            final String finalUrl = url;
            viewHolder.download.setOnClickListener(v -> {
                if(!finalUrl.equalsIgnoreCase("null")){
                    downloader.downloadFile(finalUrl);
                }
            });
        } else if(holder.getItemViewType() == VIDEO_TYPE) {
            MyViewHolder viewHolder = (MyViewHolder)holder;
            viewHolder.contentName.setText(title);
            final String finalUrl = url;
            viewHolder.download.setOnClickListener(v -> {
                if(!finalUrl.equalsIgnoreCase("null")){
                    downloader.downloadFile(finalUrl);
                }
            });
        } else if(holder.getItemViewType() == FILE_TYPE) {
            MyViewHolder viewHolder = (MyViewHolder)holder;
            viewHolder.contentName.setText(title);
            final String finalUrl = url;
            viewHolder.download.setOnClickListener(v -> {
                if(!finalUrl.equalsIgnoreCase("null")){
                    downloader.downloadFile(finalUrl);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return contentList.size();
    }

    @Override
    public int getItemViewType(int position) {
        String SyllabusUrl = "";
        try {
            JSONObject jsonObject = contentList.get(position);
            switch (contentType){
                case SYLLABUS:
                    SyllabusUrl = jsonObject.getString("SyllabusUrl");
                    break;
                case LESSON:
                    SyllabusUrl = jsonObject.getString("ContentUrl");
                    break;
                case HOMEWORK:
                    SyllabusUrl = jsonObject.getString("FileURL");
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (isImage(SyllabusUrl)){
            return IMAGE_TYPE;
        } else if (isAudio(SyllabusUrl)){
            return AUDIO_TYPE;
        } else if (isVideo(SyllabusUrl)){
            return VIDEO_TYPE;
        } else if (isFile(SyllabusUrl)){
            return FILE_TYPE;
        } else {
            return -1;
        }
    }

    private boolean isImage(String file) {
        return file.contains(".png")||file.contains(".PNG")||file.contains(".jpg")
                ||file.contains(".JPG")||file.contains(".jpeg")||file.contains(".JPEG")
                ||file.contains(".gif")||file.contains(".GIF")||file.contains(".bmp")
                ||file.contains(".BMP");
    }

    private boolean isAudio(String file) {
        return file.contains(".mp3")||file.contains(".MP3")||file.contains(".amr")
                ||file.contains(".AMR")||file.contains(".wav")||file.contains(".WAV")
                ||file.contains(".aac")||file.contains(".AAC");
    }

    private boolean isVideo(String file) {
        return file.contains(".mp4")||file.contains(".MP4")||file.contains(".wmv")
                ||file.contains(".WMV")||file.contains(".avi")||file.contains(".AVI")
                ||file.contains(".flv")||file.contains(".FLV")||file.contains(".mov")
                ||file.contains(".MOV")||file.contains(".vob")||file.contains(".VOB")
                ||file.contains(".mpeg")||file.contains(".MPEG")||file.contains(".3gp")
                ||file.contains(".3GP")||file.contains(".mpg")||file.contains(".MPG")
                ||file.contains(".wmv")||file.contains(".WMV");
    }

    private boolean isFile(String file) {
        return file.contains(".octet-stream")||file.contains(".vnd.openxmlformats-officedocument.wo")
                ||file.contains(".plain")||file.contains(".vnd.openxmlformats-officedocument.sp")
                ||file.contains(".x-zip-compressed")||file.contains(".vnd.openxmlformats-officedocument.presentationml.p")
                ||file.contains(".pdf")||file.contains(".PDF");
    }

    public interface AddFileToDownloader {
        void downloadFile(String url);
    }

    public interface ViewImageInFullScreen {
        void onViewPressed(String url);
    }

}
