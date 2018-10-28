package onair.onems.crm;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import onair.onems.R;
import onair.onems.Services.RetrofitNetworkService;
import onair.onems.Services.StaticHelperClass;
import onair.onems.mainactivities.CommonToolbarParentActivity;
import onair.onems.models.CommunicationDetailModel;
import onair.onems.models.CommunicationTypeModel;
import onair.onems.models.PriorityModel;
import onair.onems.network.MySingleton;
import onair.onems.utils.FileUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ClientCommunicationDetail extends CommonToolbarParentActivity implements FileAdapter.DeleteUri {

    private Spinner spinnerCommunicationType, spinnerPriorityType;
    private String[] tempCommunicationType = {"Communication Type"};
    private String[] tempPriorityType = {"Priority"};
    private ArrayList<CommunicationTypeModel> allCommunicationType;
    private ArrayList<PriorityModel> allPriority;
    private CommunicationTypeModel selectedCommunicationType;
    private PriorityModel selectedPriority;
    private static final String TAG = "ClientComDetail:";
    private static final int REQUEST_CODE = 3359;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 3369;
    private RecyclerView fileRecycler;
    private FileAdapter fileAdapter;
    private ArrayList<Uri> fileUriArray;
    private Uri backupFileUri;
    private CoordinatorLayout coordinatorLayout;
    private ProgressBar progressBar;
    private View whiteBackground;
    private String clientData = "", selectedDate = "", selectedNextMeetingDate = "";
    private CommunicationDetailModel communicationDetailModel;
    private Button proceed;
    private LinearLayout nextMeetingDate;
    private DatePickerDialog datePickerDialog;
    private boolean forUpdate = false;
    private int updatedCommunicationType = -1, updatedPriority = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View childActivityLayout = inflater.inflate(R.layout.client_communication_detail, null);
        LinearLayout parentActivityLayout = findViewById(R.id.contentMain);
        parentActivityLayout.addView(childActivityLayout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        TextView comDate = findViewById(R.id.comDate);
        ImageView dateIcon = findViewById(R.id.dateIcon);
        CheckBox yes = findViewById(R.id.yes);
        TextView nextComDate = findViewById(R.id.comDate1);
        ImageView nextDateIcon = findViewById(R.id.dateIcon1);
        EditText details = findViewById(R.id.details);
        proceed = findViewById(R.id.proceed);
        nextMeetingDate = findViewById(R.id.nextMeetingDate);

        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");
        selectedDate = df.format(date);
        comDate.setText(selectedDate);

        yes.setOnCheckedChangeListener((buttonView, isChecked)-> {
            if (isChecked) {
                showNextMeetingDate();
            } else {
                showNextMeetingDate();
            }
        });

        communicationDetailModel = new CommunicationDetailModel();
        Intent extraIntent = getIntent();
        if (extraIntent.hasExtra("clientData")) {
            clientData = extraIntent.getStringExtra("clientData");
            try {
                JSONObject jsonObject = new JSONObject(clientData);
                communicationDetailModel.setNewClientID(Integer.toString(jsonObject.getInt("NewClientID")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (extraIntent.hasExtra("forUpdate")) {
            forUpdate = true;
            try {
                JSONObject jsonObject = new JSONObject(extraIntent.getStringExtra("forUpdate"));
                communicationDetailModel.setComDetailID(jsonObject.getString("ComDetailID"));
                communicationDetailModel.setNewClientID(jsonObject.getString("NewClientID"));
                communicationDetailModel.setCommunicationType(jsonObject.getString("CommunicationType"));
                communicationDetailModel.setCommunicationTypeID(jsonObject.getString("CommunicationTypeID"));
                updatedCommunicationType = jsonObject.getInt("CommunicationTypeID");
                communicationDetailModel.setPriority(jsonObject.getString("Priority"));
                communicationDetailModel.setPriorityID(jsonObject.getString("PriorityID"));
                updatedPriority = jsonObject.getInt("PriorityID");
                communicationDetailModel.setCommunicationDate(jsonObject.getString("CommunicationDate"));
                comDate.setText(jsonObject.getString("CommunicationDate"));
                String nextMeeting = jsonObject.getString("NextMeetingDate");
                if (!nextMeeting.equalsIgnoreCase("")&&!nextMeeting.equalsIgnoreCase("null")) {
                    communicationDetailModel.setNextMeetingDate(nextMeeting);
                    yes.setChecked(true);
                    nextComDate.setText(nextMeeting);
                }
                communicationDetailModel.setCommunicationDetails(jsonObject.getString("CommunicationDetails"));
                details.setText(jsonObject.getString("CommunicationDetails"));
                proceed.setText("Update");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        dateIcon.setOnClickListener(v -> {
            // calender class's instance and get current date , month and year from calender
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR); // current year
            int mMonth = c.get(Calendar.MONTH); // current month
            int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
            // date picker dialog
            datePickerDialog = new DatePickerDialog(ClientCommunicationDetail.this,
                    (view, year, monthOfYear, dayOfMonth) -> {
                        // set day of month , month and year value in the edit text
//                                selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
//                                selectedDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                        selectedDate = (monthOfYear + 1)+"-"+dayOfMonth+"-"+year;
                        comDate.setText(selectedDate);

                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        });

        nextDateIcon.setOnClickListener(v -> {
            // calender class's instance and get current date , month and year from calender
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR); // current year
            int mMonth = c.get(Calendar.MONTH); // current month
            int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
            // date picker dialog
            datePickerDialog = new DatePickerDialog(ClientCommunicationDetail.this,
                    (view, year, monthOfYear, dayOfMonth) -> {
                        // set day of month , month and year value in the edit text
//                                selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
//                                selectedDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                        selectedNextMeetingDate = (monthOfYear + 1)+"-"+dayOfMonth+"-"+year;
                        nextComDate.setText(selectedNextMeetingDate);

                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        });


        coordinatorLayout = findViewById(R.id.coordinator_layout);
        whiteBackground = findViewById(R.id.whiteBackground);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        whiteBackground.setVisibility(View.INVISIBLE);

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

        spinnerCommunicationType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != 0) {
                    try {
                        selectedCommunicationType = allCommunicationType.get(position-1);
                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(ClientCommunicationDetail.this,"No communication type found !!!",Toast.LENGTH_LONG).show();
                    }
                } else {
                    selectedCommunicationType = new CommunicationTypeModel();
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
                        Toast.makeText(ClientCommunicationDetail.this,"No priority found !!!",Toast.LENGTH_LONG).show();
                    }
                } else {
                    selectedPriority = new PriorityModel();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        proceed.setOnClickListener(view-> {
            if (selectedCommunicationType.getCommunicationTypeID() == 0) {
                Toast.makeText(ClientCommunicationDetail.this,"Select communication type !!!",Toast.LENGTH_LONG).show();
            } else if (selectedPriority.getPriorityID() == 0) {
                Toast.makeText(ClientCommunicationDetail.this,"Select priority !!!",Toast.LENGTH_LONG).show();
            } else if (selectedDate.equals("")) {
                Toast.makeText(ClientCommunicationDetail.this,"Select communication date !!!",Toast.LENGTH_LONG).show();
            } else if (yes.isChecked() && selectedNextMeetingDate.equals("")) {
                Toast.makeText(ClientCommunicationDetail.this,"Select meeting date !!!",Toast.LENGTH_LONG).show();
            } else if (details.getText().toString().equals("")){
                details.setError("Add details");
                details.requestFocus();
            } else {
                communicationDetailModel.setCommunicationType(selectedCommunicationType.getCommunicationType());
                communicationDetailModel.setCommunicationTypeID(Integer.toString(selectedCommunicationType.getCommunicationTypeID()));
                communicationDetailModel.setPriority(selectedPriority.getPriority());
                communicationDetailModel.setPriorityID(Integer.toString(selectedPriority.getPriorityID()));
                communicationDetailModel.setCommunicationDate(selectedDate);
                communicationDetailModel.setNextMeetingDate(selectedNextMeetingDate);
                communicationDetailModel.setCommunicationDetails(details.getText().toString());
                postToServer(communicationDetailModel);
            }

        });

        ImageView addFile = findViewById(R.id.addFile);
        addFile.setOnClickListener((view)-> {
            if (checkPermissionREAD_EXTERNAL_STORAGE(ClientCommunicationDetail.this)) {
                // do your stuff..
                // Display the file chooser dialog
                showChooser();
            }
        });
        CommunicationTypeGetRequest();
        PriorityDataGetRequest();
    }

    private void postToServer(CommunicationDetailModel communicationDetailModel) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.baseUrl))
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitNetworkService retrofitNetworkService = retrofit.create(RetrofitNetworkService.class);

        // finally, execute the request
        Call<String> networkCall = retrofitNetworkService.postCommunicationDetail(communicationDetailModel);
        networkCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull retrofit2.Response<String> response) {
                String responseData = "";
                responseData = response.body();
                if (responseData!= null) {
                    if (!responseData.equals("")&&!responseData.equals("[]")) {
//                        try {
//                            JSONArray jsonArray = new JSONArray(responseData);
//                            for (int i = 0; i<jsonArray.length(); i++) {
//                                clientList.add(i, jsonArray.getJSONObject(i));
//                            }
//                            mAdapter.notifyDataSetChanged();
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
                        Toast.makeText(ClientCommunicationDetail.this,"Successful with return value: "+responseData,Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.e("Request error:", t.getMessage());
                Toast.makeText(ClientCommunicationDetail.this,"Not Successful !!!",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showNextMeetingDate() {
        if(nextMeetingDate.isShown()){
            Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
            slideUp.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    nextMeetingDate.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            if(slideUp != null){
                slideUp.reset();
                if(nextMeetingDate != null){
                    nextMeetingDate.clearAnimation();
                    nextMeetingDate.startAnimation(slideUp);
                }
            }
        } else {
            Animation slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_down);
            if(slideDown != null){
                slideDown.reset();
                if(nextMeetingDate != null){
                    nextMeetingDate.clearAnimation();
                    nextMeetingDate.startAnimation(slideDown);
                }
            }
            nextMeetingDate.setVisibility(View.VISIBLE);
            nextMeetingDate.requestFocus();
        }
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
        super.onBackPressed();
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

    private void CommunicationTypeGetRequest() {
        if(StaticHelperClass.isNetworkAvailable(this)) {
            progressBar.setVisibility(View.VISIBLE);
            whiteBackground.setVisibility(View.VISIBLE);
            String url = getString(R.string.baseUrl)+"/api/onEms/spGetCRMCommunicationType";

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            parseCommunicationTypeData(response);

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressBar.setVisibility(View.INVISIBLE);
                    whiteBackground.setVisibility(View.INVISIBLE);
                    Toast.makeText(ClientCommunicationDetail.this,"Error: "+error,
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
            Toast.makeText(ClientCommunicationDetail.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    void parseCommunicationTypeData(String jsonString) {
        int communicationTypeIndex = -1234;
        try {
            allCommunicationType= new ArrayList<>();
            JSONArray jsonArray = new JSONArray(jsonString);
            ArrayList<String> arrayList = new ArrayList<>();
            arrayList.add("Communication Type");
            for(int i = 0; i < jsonArray.length(); ++i) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                CommunicationTypeModel communicationTypeModel = new CommunicationTypeModel(jsonObject.getString("CommunicationTypeID"),
                        jsonObject.getString("CommunicationType"));
                allCommunicationType.add(communicationTypeModel);
                arrayList.add(communicationTypeModel.getCommunicationType());
                if(updatedCommunicationType != -1 && jsonObject.getInt("CommunicationTypeID") == updatedCommunicationType) {
                    communicationTypeIndex = i;
                }
            }
            try {
                String[] strings = new String[arrayList.size()];
                strings = arrayList.toArray(strings);
                ArrayAdapter<String> spinner_adapter = new ArrayAdapter<>(this,R.layout.spinner_item, strings);
                spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCommunicationType.setAdapter(spinner_adapter);
            } catch (IndexOutOfBoundsException e) {
                Toast.makeText(this,"No Communication Type found !!!",Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
        }
        progressBar.setVisibility(View.INVISIBLE);
        whiteBackground.setVisibility(View.INVISIBLE);
        if (communicationTypeIndex != -1234) {
            spinnerCommunicationType.setSelection(communicationTypeIndex+1);
            selectedCommunicationType = allCommunicationType.get(communicationTypeIndex);
        }
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
                    Toast.makeText(ClientCommunicationDetail.this,"Error: "+error,
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
            Toast.makeText(ClientCommunicationDetail.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    void parsePriorityData(String jsonString) {
        int priority = -12345;
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
                if(updatedPriority != -1 && jsonObject.getInt("PriorityID") == updatedPriority) {
                    priority = i;
                }
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
        if (priority != -12345) {
            spinnerPriorityType.setSelection(priority+1);
            selectedPriority = allPriority.get(priority);
        }
    }
}
