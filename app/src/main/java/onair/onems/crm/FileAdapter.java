package onair.onems.crm;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import onair.onems.R;

public class FileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<Uri> uriArray;
    private DeleteUri deleteUri;
    private boolean forUpdate = false;
    private JSONArray fileUrls;
    private DownloadFile downloadFileListener;


    class MyViewHolder extends RecyclerView.ViewHolder {
        Button cross;
        ImageView image;
        TextView name;

        MyViewHolder(View view) {
            super(view);
            cross = view.findViewById(R.id.cross);
            image = view.findViewById(R.id.image);
            name = view.findViewById(R.id.name);
            if (forUpdate) {
                cross.setClickable(false);
                cross.setVisibility(View.GONE);
            }
        }
    }


    FileAdapter(Context context, ArrayList<Uri> uriArray, DeleteUri deleteUri) {
        this.context = context;
        this.uriArray = uriArray;
        this.deleteUri = deleteUri;
    }

    FileAdapter(Context context, JSONArray fileUrls, boolean forUpdate, DownloadFile downloadFileListener) {
        this.context = context;
        this.fileUrls = fileUrls;
        this.forUpdate = forUpdate;
        this.downloadFileListener = downloadFileListener;
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
        viewHolder.image.setOnClickListener(view-> {
            if (forUpdate) {
                try {
                    downloadFileListener.onFileSelected(fileUrls.getJSONObject(position));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        if (forUpdate) {
            try {
                String stringArray[] = fileUrls.getJSONObject(position).getString("DocumentUrl").replace("\\", "/").split("/");
                int length = stringArray.length;
                if (length != 0) {
                    viewHolder.name.setText(stringArray[length-1]);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            viewHolder.name.setText(getFileInformation(uriArray.get(position)));
        }
        viewHolder.cross.setOnClickListener(view-> deleteUri.onUriDeleted(holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        if (forUpdate) {
            return fileUrls.length();
        } else {
            return uriArray.size();
        }
    }

    public interface DeleteUri {
        void onUriDeleted(int position);
    }

    public interface DownloadFile {
        void onFileSelected(JSONObject jsonObject);
    }

    String getFileInformation(Uri uri) {
        String displayName = "";
        String size = "";
        // The query, since it only applies to a single document, will only return
        // one row. There's no need to filter, sort, or select fields, since we want
        // all fields for one document.
        Cursor cursor = context.getContentResolver()
                .query(uri, null, null, null, null, null);

        try {
            // moveToFirst() returns false if the cursor has 0 rows.  Very handy for
            // "if there's anything to look at, look at it" conditionals.
            if (cursor != null && cursor.moveToFirst()) {

                // Note it's called "Display Name".  This is
                // provider-specific, and might not necessarily be the file name.
                displayName = cursor.getString(
                        cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));

                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                // If the size is unknown, the value stored is null.  But since an
                // int can't be null in Java, the behavior is implementation-specific,
                // which is just a fancy term for "unpredictable".  So as
                // a rule, check if it's null before assigning to an int.  This will
                // happen often:  The storage API allows for remote files, whose
                // size might not be locally known.

                if (!cursor.isNull(sizeIndex)) {
                    // Technically the column stores an int, but cursor.getString()
                    // will do the conversion automatically.
                    size = cursor.getString(sizeIndex);
                } else {
                    size = "Unknown";
                }
            }
        } finally {
            Objects.requireNonNull(cursor).close();
        }
        return displayName+" Size: "+size;
    }
}
