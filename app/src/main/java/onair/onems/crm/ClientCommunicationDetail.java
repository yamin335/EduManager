package onair.onems.crm;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import onair.onems.R;
import onair.onems.mainactivities.SideNavigationMenuParentActivity;
import onair.onems.mainactivities.StudentMainScreen;
import onair.onems.mainactivities.TeacherMainScreen;
import onair.onems.utils.FileUtils;

public class ClientCommunicationDetail extends SideNavigationMenuParentActivity implements FileAdapter.DeleteUri {

    private Spinner spinnerCommunicationType, spinnerPriorityType;
    private String[] tempCommunicationType = {"Communication Type", "Phone call", "Visit", "Mail", "SMS"};
    private String[] tempPriorityType = {"Priority", "High", "Medium", "Low"};
    private static final String TAG = "ClientComDetail:";
    private static final int REQUEST_CODE = 3359;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 3369;
    private RecyclerView fileRecycler;
    private FileAdapter fileAdapter;
    private ArrayList<Uri> fileUriArray;
    private Uri backupFileUri;
    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityName = ClientCommunicationDetail.class.getName();

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View childActivityLayout = inflater.inflate(R.layout.client_communication_detail, null);
        LinearLayout parentActivityLayout = (LinearLayout) findViewById(R.id.contentMain);
        parentActivityLayout.addView(childActivityLayout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        coordinatorLayout = findViewById(R.id.coordinator_layout);

        fileRecycler = findViewById(R.id.recycler);
        fileUriArray = new ArrayList<>();
        fileAdapter = new FileAdapter(this, fileUriArray, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        fileRecycler.setLayoutManager(layoutManager);
        fileRecycler.setItemAnimator(new DefaultItemAnimator());
        fileRecycler.setAdapter(fileAdapter);

        spinnerCommunicationType =(Spinner)findViewById(R.id.spinnerCommunication);
        spinnerPriorityType = (Spinner)findViewById(R.id.spinnerPriority);

        ArrayAdapter<String> CommunicationTypeAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, tempCommunicationType);
        CommunicationTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCommunicationType.setAdapter(CommunicationTypeAdapter);

        ArrayAdapter<String> PriorityTypeAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, tempPriorityType);
        PriorityTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriorityType.setAdapter(PriorityTypeAdapter);

        ImageView addFile = findViewById(R.id.addFile);
        addFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermissionREAD_EXTERNAL_STORAGE(ClientCommunicationDetail.this)) {
                    // do your stuff..
                    // Display the file chooser dialog
                    showChooser();
                }
            }
        });
    }

    private void showChooser() {
        // Use the GET_CONTENT intent from the utility class
        Intent target = FileUtils.createGetContentIntent();
        // Create the chooser Intent
        Intent intent = Intent.createChooser(
                target, "Choose file from");
        try {
            startActivityForResult(intent, REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
            // The reason for the existence of aFileChooser
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE:
                // If the file selection was successful
                if (resultCode == RESULT_OK) {

                    if (data != null) {
                        // Get the URI of the selected file
                        final Uri uri = data.getData();
                        fileUriArray.add(uri);
                        fileAdapter.notifyDataSetChanged();
                        try {
                            // Get the file path from the URI
                            final String path = FileUtils.getPath(this, uri);
//                            Toast.makeText(ClientCommunicationDetail.this,
//                                    "File Selected: " + path, Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Log.e(TAG, "File select error", e);
                        }
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(UserTypeID == 1) {
            Intent mainIntent = new Intent(ClientCommunicationDetail.this,NewClientEntry.class);
            startActivity(mainIntent);
            finish();
        } else if(UserTypeID == 2) {
            Intent mainIntent = new Intent(ClientCommunicationDetail.this,NewClientEntry.class);
            startActivity(mainIntent);
            finish();
        } else if(UserTypeID == 3) {
            Intent mainIntent = new Intent(ClientCommunicationDetail.this,NewClientEntry.class);
            startActivity(mainIntent);
            finish();
        } else if(UserTypeID == 4) {
            Intent mainIntent = new Intent(ClientCommunicationDetail.this,NewClientEntry.class);
            startActivity(mainIntent);
            finish();
        } else if(UserTypeID == 5) {
            Intent mainIntent = new Intent(ClientCommunicationDetail.this,NewClientEntry.class);
            startActivity(mainIntent);
            finish();
        }
    }

    @Override
    public void onUriDeleted(int position) {
        backupFileUri = fileUriArray.get(position);
        fileUriArray.remove(position);
        fileAdapter.notifyItemRemoved(position);

        // showing snack bar with Undo option
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, " UNDO file?", Snackbar.LENGTH_LONG);
        snackbar.setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // undo is selected, restore the deleted item
                fileUriArray.add(position, backupFileUri);
                fileAdapter.notifyItemInserted(position);
            }
        });
        snackbar.setActionTextColor(Color.YELLOW);
        snackbar.show();
    }

    public boolean checkPermissionREAD_EXTERNAL_STORAGE(
            final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context,
                            Manifest.permission.READ_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // do your stuff
                    // Display the file chooser dialog
                    showChooser();
                } else {
                    Toast.makeText(this, "PERMISSION Denied",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }
    }

    public void showDialog(final String msg, final Context context,
                           final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[] { permission },
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }
}
