package onair.onems.crm;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import onair.onems.R;
import onair.onems.utils.ImageUtils;
import onair.onems.mainactivities.SideNavigationMenuParentActivity;
import onair.onems.mainactivities.StudentMainScreen;
import onair.onems.mainactivities.TeacherMainScreen;

public class NewClientEntry extends SideNavigationMenuParentActivity implements ImageAdapter.DeleteImage, ImageAdapter.ViewImage{

    private Spinner spinnerInsType, spinnerPriorityType;
    private String[] tempInsType = {"Institute Type", "University", "College", "School"};
    private String[] tempPriorityType = {"Priority", "High", "Medium", "Low"};
    private RecyclerView recyclerCard, recyclerPhoto;
    private ArrayList<Bitmap> vCardBitmapArray;
    private ArrayList<Bitmap> photoBitmapArray;
    private ArrayList<File> vCardFileArray;
    private ArrayList<File> photoFileArray;
    private final int REQUEST_VCARD = 335;
    private final int REQUEST_PHOTO = 336;
    private Bitmap bitmap, backUpImage;
    private File backUpFile;
    private ImageAdapter vCardAdapter, photoAdapter;
    private static final int VCARD = 1;
    private static final int PHOTO = 2;
    private CoordinatorLayout coordinatorLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityName = NewClientEntry.class.getName();

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View childActivityLayout = inflater.inflate(R.layout.client_entry_new, null);
        LinearLayout parentActivityLayout = (LinearLayout) findViewById(R.id.contentMain);
        parentActivityLayout.addView(childActivityLayout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        coordinatorLayout = findViewById(R.id.coordinator_layout);

        vCardBitmapArray = new ArrayList<>();
        photoBitmapArray = new ArrayList<>();
        vCardFileArray = new ArrayList<>();
        photoFileArray = new ArrayList<>();

        recyclerCard = findViewById(R.id.recyclerVCard);
        recyclerPhoto = findViewById(R.id.recyclerPhoto);


        vCardAdapter = new ImageAdapter(this, vCardBitmapArray, vCardFileArray, VCARD, this, this);
        RecyclerView.LayoutManager vCardLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerCard.setLayoutManager(vCardLayoutManager);
        recyclerCard.setItemAnimator(new DefaultItemAnimator());
        recyclerCard.setAdapter(vCardAdapter);

        photoAdapter = new ImageAdapter(this, photoBitmapArray, photoFileArray, PHOTO, this, this);
        RecyclerView.LayoutManager photoLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerPhoto.setLayoutManager(photoLayoutManager);
        recyclerPhoto.setItemAnimator(new DefaultItemAnimator());
        recyclerPhoto.setAdapter(photoAdapter);

        ImageView addVisitingCard = findViewById(R.id.addVCard);
        addVisitingCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(ImageUtils.getPickImageChooserIntent(NewClientEntry.this), REQUEST_VCARD);
            }
        });



        ImageView addPhoto = findViewById(R.id.addPhoto);
        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(ImageUtils.getPickImageChooserIntent(NewClientEntry.this), REQUEST_PHOTO);
            }
        });

        spinnerInsType =(Spinner)findViewById(R.id.spinnerInstitute);
        spinnerPriorityType = (Spinner)findViewById(R.id.spinnerPriority);

        ArrayAdapter<String> InsTypeAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, tempInsType);
        InsTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerInsType.setAdapter(InsTypeAdapter);

        ArrayAdapter<String> PriorityTypeAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, tempPriorityType);
        PriorityTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriorityType.setAdapter(PriorityTypeAdapter);

        Button proceed = findViewById(R.id.proceed);

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewClientEntry.this,ClientCommunicationDetail.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            Uri imageUri = ImageUtils.getPickImageResultUri(data, this);

            // For API >= 23 we need to check specifically that we have permissions to read external storage,
            // but we don't know if we need to for the URI so the simplest is to try open the stream and see if we get error.
            boolean requirePermissions = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                    ImageUtils.isUriRequiresPermissions(imageUri, this)) {

                // request permissions and handle the result in onRequestPermissionsResult()
                requirePermissions = true;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    bitmap = ImageUtils.getResizedBitmap(bitmap, 500);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (requestCode == REQUEST_VCARD) {
                    vCardBitmapArray.add(bitmap);
                    vCardFileArray.add(ImageUtils.getFileFromBitmap(bitmap, this));
                    vCardAdapter.notifyDataSetChanged();
                } else if (requestCode == REQUEST_PHOTO) {
                    photoBitmapArray.add(bitmap);
                    photoFileArray.add(ImageUtils.getFileFromBitmap(bitmap, this));
                    photoAdapter.notifyDataSetChanged();
                }

                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            }

            if (!requirePermissions) {
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    bitmap = ImageUtils.getResizedBitmap(bitmap, 500);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (requestCode == REQUEST_VCARD) {
                    vCardBitmapArray.add(bitmap);
                    vCardFileArray.add(ImageUtils.getFileFromBitmap(bitmap, this));
                    vCardAdapter.notifyDataSetChanged();
                } else if (requestCode == REQUEST_PHOTO) {
                    photoBitmapArray.add(bitmap);
                    photoFileArray.add(ImageUtils.getFileFromBitmap(bitmap, this));
                    photoAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(UserTypeID == 1) {
            Intent mainIntent = new Intent(NewClientEntry.this,TeacherMainScreen.class);
            startActivity(mainIntent);
            finish();
        } else if(UserTypeID == 2) {
            Intent mainIntent = new Intent(NewClientEntry.this,TeacherMainScreen.class);
            startActivity(mainIntent);
            finish();
        } else if(UserTypeID == 3) {
            Intent mainIntent = new Intent(NewClientEntry.this,StudentMainScreen.class);
            startActivity(mainIntent);
            finish();
        } else if(UserTypeID == 4) {
            Intent mainIntent = new Intent(NewClientEntry.this,TeacherMainScreen.class);
            startActivity(mainIntent);
            finish();
        } else if(UserTypeID == 5) {
            Intent mainIntent = new Intent(NewClientEntry.this,StudentMainScreen.class);
            startActivity(mainIntent);
            finish();
        }
    }

    @Override
    public void onImageDeleted(int position, int type) {
        if (type == VCARD) {
            backUpImage = vCardBitmapArray.get(position);
            backUpFile = vCardFileArray.get(position);

            vCardBitmapArray.remove(position);
            vCardFileArray.remove(position);
            vCardAdapter.notifyItemRemoved(position);
        } else if (type == PHOTO) {
            backUpImage = photoBitmapArray.get(position);
            backUpFile = photoFileArray.get(position);

            photoBitmapArray.remove(position);
            photoFileArray.remove(position);
            photoAdapter.notifyItemRemoved(position);
        }

        // showing snack bar with Undo option
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, " UNDO image?", Snackbar.LENGTH_LONG);
        snackbar.setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // undo is selected, restore the deleted item
                if (type == VCARD) {
                    vCardBitmapArray.add(position, backUpImage);
                    vCardFileArray.add(position, backUpFile);
                    vCardAdapter.notifyItemInserted(position);
                } else if (type == PHOTO) {
                    photoBitmapArray.add(position, backUpImage);
                    photoFileArray.add(position, backUpFile);
                    photoAdapter.notifyItemInserted(position);
                }
            }
        });
        snackbar.setActionTextColor(Color.YELLOW);
        snackbar.show();
    }

    @Override
    public void onImageSelected(int position, int type) {
        if (type == VCARD) {
            FullScreenImageViewDialog fullScreenImageViewDialog = new FullScreenImageViewDialog(this, this, vCardBitmapArray.get(position));
//            fullScreenImageViewDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            fullScreenImageViewDialog.setCancelable(true);
            fullScreenImageViewDialog.show();
        } else if (type == PHOTO) {

        }
    }
}
