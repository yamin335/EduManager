package onair.onems.crm;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import onair.onems.R;
import onair.onems.Services.StaticHelperClass;
import onair.onems.attendance.TakeAttendanceDetails;
import onair.onems.customised.CustomRequest;
import onair.onems.models.InstituteTypeModel;
import onair.onems.models.MediumModel;
import onair.onems.models.PriorityModel;
import onair.onems.models.SessionModel;
import onair.onems.network.MySingleton;
import onair.onems.routine.ExamRoutineMainScreen;
import onair.onems.utils.ImageUtils;
import onair.onems.mainactivities.SideNavigationMenuParentActivity;
import onair.onems.mainactivities.StudentMainScreen;
import onair.onems.mainactivities.TeacherMainScreen;

public class NewClientEntry extends SideNavigationMenuParentActivity implements ImageAdapter.DeleteImage, ImageAdapter.ViewImage{

    private Spinner spinnerInsType, spinnerPriorityType;
    private ArrayList<InstituteTypeModel> allInstituteType;
    private ArrayList<PriorityModel> allPriority;
    private InstituteTypeModel selectedInstituteType;
    private PriorityModel selectedPriority;
    private String[] tempInsType = {"Institute Type"};
    private String[] tempPriorityType = {"Priority"};
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
    private JSONObject newClient;
    private ProgressBar progressBar;
    private View whiteBackground;
    private TextView instituteName, contactPerson, contactNumber, noOfStudent, noOfTeacher, instituteAddress, comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityName = NewClientEntry.class.getName();

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View childActivityLayout = inflater.inflate(R.layout.client_entry_new, null);
        LinearLayout parentActivityLayout = (LinearLayout) findViewById(R.id.contentMain);
        parentActivityLayout.addView(childActivityLayout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        instituteName = findViewById(R.id.InstituteName);
        contactPerson = findViewById(R.id.headMasterName);
        contactNumber = findViewById(R.id.contactNumber);
        noOfStudent = findViewById(R.id.studentNo);
        noOfTeacher = findViewById(R.id.teacherNo);
        instituteAddress = findViewById(R.id.address);
        comment = findViewById(R.id.comments);

        newClient = new JSONObject();
        try {
            newClient.put("NewClientID", 0);
            newClient.put("InstituteTypeID", 0);
            newClient.put("PriorityID", 0);
            newClient.put("InsName", "");
            newClient.put("ContactPerson", "");
            newClient.put("ContactNumber", "");
            newClient.put("NoOfStudent", 0);
            newClient.put("NoOfTeacher", 0);
            newClient.put("InstituteAddress", "");
            newClient.put("Comments", "");
            newClient.put("CreateBy", 0);
            newClient.put("CreateOn", null);
            newClient.put("CreatePc", null);
            newClient.put("UpdateBy", null);
            newClient.put("UpdateOn", null);
            newClient.put("UpdatePc", null);
            newClient.put("IsDeleted", 0);
            newClient.put("DeleteBy", null);
            newClient.put("DeleteOn", null);
            newClient.put("DeletePc", null);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        coordinatorLayout = findViewById(R.id.coordinator_layout);
        whiteBackground = findViewById(R.id.whiteBackground);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        whiteBackground.setVisibility(View.INVISIBLE);

        allInstituteType= new ArrayList<>();
        allPriority = new ArrayList<>();
        selectedInstituteType = new InstituteTypeModel();
        selectedPriority = new PriorityModel();

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

        spinnerInsType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != 0) {
                    try {
                        selectedInstituteType = allInstituteType.get(position-1);
                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(NewClientEntry.this,"No shift found !!!",Toast.LENGTH_LONG).show();
                    }
                } else {
                    selectedInstituteType = new InstituteTypeModel();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerPriorityType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != 0) {
                    try {
                        selectedPriority = allPriority.get(position-1);
                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(NewClientEntry.this,"No shift found !!!",Toast.LENGTH_LONG).show();
                    }
                } else {
                    selectedPriority = new PriorityModel();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        Button proceed = findViewById(R.id.proceed);

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedInstituteType.getInstituteTypeID() == 0) {
                    Toast.makeText(NewClientEntry.this,"Select Institute Type",
                            Toast.LENGTH_LONG).show();
                } else if (selectedPriority.getPriorityID() == 0) {
                    Toast.makeText(NewClientEntry.this,"Select Priority",
                            Toast.LENGTH_LONG).show();
                } else if (instituteName.getText().toString().equals("")) {
                    instituteName.setError("This is required!");
                    instituteName.requestFocus();
                } else if (contactPerson.getText().toString().equals("")) {
                    contactPerson.setError("This is required!");
                    contactPerson.requestFocus();
                } else if (contactNumber.getText().toString().equals("")) {
                    contactNumber.setError("This is required!");
                    contactNumber.requestFocus();
                } else if (noOfStudent.getText().toString().equals("")) {
                    noOfStudent.setError("This is required!");
                    noOfStudent.requestFocus();
                } else if (noOfTeacher.getText().toString().equals("")) {
                    noOfTeacher.setError("This is required!");
                    noOfTeacher.requestFocus();
                } else if (instituteAddress.getText().toString().equals("")) {
                    instituteAddress.setError("This is required!");
                    instituteAddress.requestFocus();
                } else if (comment.getText().toString().equals("")) {
                    comment.setError("This is required!");
                    comment.requestFocus();
                } else {
                    try {
                        newClient.put("InstituteTypeID", selectedInstituteType.getInstituteTypeID());
                        newClient.put("PriorityID", selectedPriority.getPriorityID());
                        newClient.put("InsName", instituteName.getText().toString());
                        newClient.put("ContactPerson", contactPerson.getText().toString());
                        newClient.put("ContactNumber", contactNumber.getText().toString());
                        newClient.put("NoOfStudent", !noOfStudent.getText().toString().equals("")? Integer.parseInt(noOfStudent.getText().toString()):0);
                        newClient.put("NoOfTeacher", !noOfTeacher.getText().toString().equals("")? Integer.parseInt(noOfTeacher.getText().toString()):0);
                        newClient.put("InstituteAddress", instituteAddress.getText().toString());
                        newClient.put("Comments", comment.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                // , , , ,
//                Intent intent = new Intent(NewClientEntry.this,ClientCommunicationDetail.class);
//                startActivity(intent);
//                finish();
            }
        });

        InstituteTypeGetRequest();
        PriorityDataGetRequest();
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

    private void InstituteTypeGetRequest() {
        if(StaticHelperClass.isNetworkAvailable(this)) {
            progressBar.setVisibility(View.VISIBLE);
            whiteBackground.setVisibility(View.VISIBLE);
            String url = getString(R.string.baseUrl)+"/api/onEms/getInstituteType";

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            parseInstituteTypeData(response);

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressBar.setVisibility(View.INVISIBLE);
                    whiteBackground.setVisibility(View.INVISIBLE);
                    Toast.makeText(NewClientEntry.this,"Error: "+error,
                            Toast.LENGTH_LONG).show();
                }
            })
            {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String>  params = new HashMap<>();
                    params.put("Authorization", "Request_From_onEMS_Android_app");
                    return params;
                }
            };
            MySingleton.getInstance(this).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(NewClientEntry.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    void parseInstituteTypeData(String jsonString) {
        try {
            allInstituteType = new ArrayList<>();
            JSONArray jsonArray = new JSONArray(jsonString);
            ArrayList<String> arrayList = new ArrayList<>();
            arrayList.add("Institute Type");
            for(int i = 0; i < jsonArray.length(); ++i) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                InstituteTypeModel instituteTypeModel = new InstituteTypeModel(jsonObject.getString("InstituteTypeID"),
                        jsonObject.getString("InstituteTypeName"));
                allInstituteType.add(instituteTypeModel);
                arrayList.add(instituteTypeModel.getInstituteTypeName());
            }
            try {
                String[] strings = new String[arrayList.size()];
                strings = arrayList.toArray(strings);
                ArrayAdapter<String> spinner_adapter = new ArrayAdapter<>(this,R.layout.spinner_item, strings);
                spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerInsType.setAdapter(spinner_adapter);
            } catch (IndexOutOfBoundsException e) {
                Toast.makeText(this,"No Institute Type found !!!",Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
        }
        progressBar.setVisibility(View.INVISIBLE);
        whiteBackground.setVisibility(View.INVISIBLE);
    }

    private void PriorityDataGetRequest() {
        if(StaticHelperClass.isNetworkAvailable(this)) {
            String url = getString(R.string.baseUrl)+"/api/onEms/spGetCRMPriority";
            progressBar.setVisibility(View.VISIBLE);
            whiteBackground.setVisibility(View.VISIBLE);
            //Preparing Medium data from server
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            parsePriorityData(response);

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressBar.setVisibility(View.INVISIBLE);
                    whiteBackground.setVisibility(View.INVISIBLE);
                    Toast.makeText(NewClientEntry.this,"Error: "+error,
                            Toast.LENGTH_LONG).show();
                }
            })
            {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String>  params = new HashMap<>();
                    params.put("Authorization", "Request_From_onEMS_Android_app");
                    return params;
                }
            };
            MySingleton.getInstance(this).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(NewClientEntry.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    void parsePriorityData(String jsonString) {
        try {
            allPriority = new ArrayList<>();
            JSONArray jsonArray = new JSONArray(jsonString);
            ArrayList<String> arrayList = new ArrayList<>();
            arrayList.add("Priority");
            for(int i = 0; i < jsonArray.length(); ++i) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                PriorityModel priorityModel = new PriorityModel(jsonObject.getString("PriorityID"),
                        jsonObject.getString("Priority"));
                allPriority.add(priorityModel);
                arrayList.add(priorityModel.getPriority());
            }
            try {
                String[] strings = new String[arrayList.size()];
                strings = arrayList.toArray(strings);
                ArrayAdapter<String> spinner_adapter = new ArrayAdapter<>(this,R.layout.spinner_item, strings);
                spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerPriorityType.setAdapter(spinner_adapter);
            } catch (IndexOutOfBoundsException e) {
                Toast.makeText(this,"No Priority found !!!",Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
        }
        progressBar.setVisibility(View.INVISIBLE);
        whiteBackground.setVisibility(View.INVISIBLE);
    }

    public void entryNewClient() {
        progressBar.setVisibility(View.VISIBLE);
        String url = getString(R.string.baseUrl)+"/api/onEms/spSetCRMNewClientEntry";
        CustomRequest customRequest = new CustomRequest (Request.Method.POST, url, newClient,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            if (response.getJSONObject(0).getInt("ReturnValue") == 1) {
                                Toast.makeText(NewClientEntry.this,"Successfully done",Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(NewClientEntry.this,"Error: "+e,Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(NewClientEntry.this,"Not Successful "+error.toString(),Toast.LENGTH_LONG).show();
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<>();
                params.put("Authorization", "Request_From_onEMS_Android_app");
                return params;
            }
        };
        MySingleton.getInstance(this).addToRequestQueue(customRequest);
    }
}
