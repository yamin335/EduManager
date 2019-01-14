package onair.onems.crm;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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

import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import onair.onems.R;
import onair.onems.Services.RetrofitNetworkService;
import onair.onems.Services.StaticHelperClass;
import onair.onems.models.InstituteTypeModel;
import onair.onems.models.PriorityModel;
import onair.onems.utils.ImageUtils;
import onair.onems.mainactivities.SideNavigationMenuParentActivity;
import onair.onems.mainactivities.StudentMainScreen;
import onair.onems.mainactivities.TeacherMainScreen;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class NewClientEntry extends SideNavigationMenuParentActivity implements ImageAdapter.DeleteImage, ImageAdapter.ViewImage{

    private Spinner spinnerInsType, spinnerPriorityType;
    private ArrayList<InstituteTypeModel> allInstituteType;
    private ArrayList<PriorityModel> allPriority;
    private InstituteTypeModel selectedInstituteType;
    private PriorityModel selectedPriority;
    private String[] tempInsType = {"Institute Type"};
    private String[] tempPriorityType = {"Priority"};
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
    private JsonObject newClient;
    private ProgressBar progressBar;
    private View whiteBackground;
    private TextView instituteName, contactPerson, contactNumber, noOfStudent, noOfTeacher, instituteAddress, comment, ShowDate;
    private DatePickerDialog datePickerDialog;
    private String selectedDate = "", clientData = "";
    private boolean forUpdate = false;
    private int updatedPriority = 0, updatedInstituteType = 0;
    public static final int CLIENT_COMMUNICATION_DETAIL = 222;
    private CompositeDisposable finalDisposer = new CompositeDisposable();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!finalDisposer.isDisposed())
            finalDisposer.dispose();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityName = NewClientEntry.class.getName();

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View childActivityLayout = Objects.requireNonNull(inflater).inflate(R.layout.client_entry_new, null);
        LinearLayout parentActivityLayout = (LinearLayout) findViewById(R.id.contentMain);
        parentActivityLayout.addView(childActivityLayout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        newClient = new JsonObject();

        Button proceed = findViewById(R.id.proceed);
        instituteName = findViewById(R.id.InstituteName);
        contactPerson = findViewById(R.id.headMasterName);
        contactNumber = findViewById(R.id.contactNumber);
        noOfStudent = findViewById(R.id.studentNo);
        noOfTeacher = findViewById(R.id.teacherNo);
        instituteAddress = findViewById(R.id.address);
        comment = findViewById(R.id.comments);
        FloatingActionButton datePicker = findViewById(R.id.date);
        ShowDate = findViewById(R.id.showDate);

        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");
        selectedDate = df.format(date);

        datePicker.setOnClickListener(v -> {
            // calender class's instance and get current date , month and year from calender
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR); // current year
            int mMonth = c.get(Calendar.MONTH); // current month
            int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
            // date picker dialog
            datePickerDialog = new DatePickerDialog(NewClientEntry.this,
                    (view, year, monthOfYear, dayOfMonth) -> {
                        // set day of month , month and year value in the edit text
//                                selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
//                                selectedDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                        selectedDate = (monthOfYear + 1)+"-"+dayOfMonth+"-"+year;
                        ShowDate.setText(selectedDate);

                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        });

        try {
            newClient.addProperty("NewClientID", 0);
            newClient.addProperty("AgentID", Integer.parseInt(LoggedUserID));
            newClient.addProperty("EntryDate", selectedDate);
            newClient.addProperty("InstituteTypeName", "");
            newClient.addProperty("InstituteTypeID", 0);
            newClient.addProperty("Priority", "");
            newClient.addProperty("PriorityID", 0);
            newClient.addProperty("InsName", "");
            newClient.addProperty("ContactPerson", "");
            newClient.addProperty("ContactNumber", "");
            newClient.addProperty("NoOfStudent", 0);
            newClient.addProperty("NoOfTeacher", 0);
            newClient.addProperty("InstituteAddress", "");
            newClient.addProperty("Comments", "");
            newClient.addProperty("CreateBy", 0);
            newClient.addProperty("CreateOn", "");
            newClient.addProperty("CreatePc", "");
            newClient.addProperty("UpdateBy", 0);
            newClient.addProperty("UpdateOn", "");
            newClient.addProperty("UpdatePc", "");
            newClient.addProperty("IsDeleted", 0);
            newClient.addProperty("DeleteBy", 0);
            newClient.addProperty("DeleteOn", "");
            newClient.addProperty("DeletePc", "");
            newClient.add("VCard", new JsonArray());
            newClient.add("Photo", new JsonArray());
        } catch (JsonIOException e) {
            e.printStackTrace();
        }

        Intent intent = getIntent();
        if (intent.hasExtra("clientData")) {
            forUpdate = true;
            clientData = intent.getStringExtra("clientData");
            try {
                JSONObject jsonObject = new JSONObject(clientData);
                if (forUpdate) {
                    newClient.addProperty("NewClientID", jsonObject.getInt("NewClientID"));
                    newClient.addProperty("AgentID", jsonObject.getInt("AgentID"));
                    newClient.addProperty("EntryDate", jsonObject.getString("EntryDate"));
                    newClient.addProperty("InstituteTypeName", jsonObject.getString("InstituteTypeName"));
                    newClient.addProperty("InstituteTypeID", jsonObject.getInt("InstituteTypeID"));
                    newClient.addProperty("Priority", jsonObject.getString("Priority"));
                    newClient.addProperty("PriorityID", jsonObject.getInt("PriorityID"));
                    newClient.addProperty("InsName", jsonObject.getString("InsName"));
                    newClient.addProperty("ContactPerson", jsonObject.getString("ContactPerson"));
                    newClient.addProperty("ContactNumber", jsonObject.getString("ContactNumber"));
                    newClient.addProperty("NoOfStudent", jsonObject.getInt("NoOfStudent"));
                    newClient.addProperty("NoOfTeacher", jsonObject.getInt("NoOfTeacher"));
                    newClient.addProperty("InstituteAddress", jsonObject.getString("InstituteAddress"));
                    newClient.addProperty("Comments", jsonObject.getString("Comments"));
                    updatedPriority = jsonObject.getInt("PriorityID");
                    updatedInstituteType = jsonObject.getInt("InstituteTypeID");
                    ShowDate.setText(jsonObject.getString("EntryDate"));
                    selectedDate = jsonObject.getString("EntryDate");
                    instituteName.setText(jsonObject.getString("InsName"));
                    contactPerson.setText(jsonObject.getString("ContactPerson"));
                    contactNumber.setText(jsonObject.getString("ContactNumber"));
                    noOfStudent.setText(jsonObject.getString("NoOfStudent"));
                    noOfTeacher.setText(jsonObject.getString("NoOfTeacher"));
                    instituteAddress.setText(jsonObject.getString("InstituteAddress"));
                    comment.setText(jsonObject.getString("Comments"));
                    proceed.setText(R.string.update);
                    toolbar.setTitle("Update Client");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

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

        RecyclerView recyclerCard = findViewById(R.id.recyclerVCard);
        RecyclerView recyclerPhoto = findViewById(R.id.recyclerPhoto);


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

        if (forUpdate) {
            try {
                JSONObject jsonObject = new JSONObject(clientData);
                JSONArray vCardJsonArray = jsonObject.getJSONArray("VCard");
                ArrayList<JSONObject> vCardUrls = new ArrayList<>();
                for (int i = 0; i<vCardJsonArray.length(); i++) {
                    vCardUrls.add(vCardJsonArray.getJSONObject(i));
                }
                vCardAdapter = new ImageAdapter(this, vCardUrls,VCARD, forUpdate, this);
                RecyclerView.LayoutManager vCardManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
                recyclerCard.setLayoutManager(vCardManager);
                recyclerCard.setItemAnimator(new DefaultItemAnimator());
                recyclerCard.setAdapter(vCardAdapter);

                JSONArray photoJsonArray = jsonObject.getJSONArray("Photo");
                ArrayList<JSONObject> photoUrls = new ArrayList<>();
                for (int i = 0; i<photoJsonArray.length(); i++) {
                    photoUrls.add(photoJsonArray.getJSONObject(i));
                }

                photoAdapter = new ImageAdapter(this, photoUrls, PHOTO, forUpdate, this);
                RecyclerView.LayoutManager photoManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
                recyclerPhoto.setLayoutManager(photoManager);
                recyclerPhoto.setItemAnimator(new DefaultItemAnimator());
                recyclerPhoto.setAdapter(photoAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        ImageView addVisitingCard = findViewById(R.id.addVCard);
        addVisitingCard.setOnClickListener(v -> startActivityForResult(ImageUtils.getPickImageChooserIntent(NewClientEntry.this), REQUEST_VCARD));

        ImageView addPhoto = findViewById(R.id.addPhoto);
        addPhoto.setOnClickListener(v -> startActivityForResult(ImageUtils.getPickImageChooserIntent(NewClientEntry.this), REQUEST_PHOTO));

        if (forUpdate) {
            addVisitingCard.setVisibility(View.GONE);
            addVisitingCard.setClickable(false);

            addPhoto.setVisibility(View.GONE);
            addPhoto.setClickable(false);
        }
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

        proceed.setOnClickListener(v -> {
            if (selectedInstituteType.getInstituteTypeID() == 0) {
                Toast.makeText(NewClientEntry.this,"Select Institute Type",
                        Toast.LENGTH_LONG).show();
            } else if (selectedPriority.getPriorityID() == 0) {
                Toast.makeText(NewClientEntry.this,"Select Priority",
                        Toast.LENGTH_LONG).show();
            } else if (selectedDate == null || selectedDate.equals("")) {
                Toast.makeText(NewClientEntry.this,"Select date",
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
                    newClient.addProperty("EntryDate", selectedDate);
                    newClient.addProperty("InstituteTypeName", selectedInstituteType.getInstituteTypeName());
                    newClient.addProperty("InstituteTypeID", selectedInstituteType.getInstituteTypeID());
                    newClient.addProperty("Priority", selectedPriority.getPriority());
                    newClient.addProperty("PriorityID", selectedPriority.getPriorityID());
                    newClient.addProperty("InsName", instituteName.getText().toString());
                    newClient.addProperty("ContactPerson", contactPerson.getText().toString());
                    newClient.addProperty("ContactNumber", contactNumber.getText().toString());
                    newClient.addProperty("NoOfStudent", !noOfStudent.getText().toString().equals("")? Integer.parseInt(noOfStudent.getText().toString()):0);
                    newClient.addProperty("NoOfTeacher", !noOfTeacher.getText().toString().equals("")? Integer.parseInt(noOfTeacher.getText().toString()):0);
                    newClient.addProperty("InstituteAddress", instituteAddress.getText().toString());
                    newClient.addProperty("Comments", comment.getText().toString());
                    entryNewClientByPostingDataToServer();
                } catch (JsonIOException e) {
                    e.printStackTrace();
                }
            }
        });

        InstituteTypeGetRequest();
        PriorityDataGetRequest();
    }

    @NonNull
    private MultipartBody.Part prepareFilePart(File file) {
        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("image/jpeg"),
                        file
                );
        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData("userCrmPhoto", file.getName(), requestFile);
    }

    private void postNewClientToServer() {
//        progressBar.setVisibility(View.VISIBLE);
//        whiteBackground.setVisibility(View.VISIBLE);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.baseUrl))
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        Observable<String> observable = retrofit
                .create(RetrofitNetworkService.class)
                .postNewClient(newClient)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());

        finalDisposer.add( observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<String>() {

                    @Override
                    public void onNext(String response) {
                        if (response!= null) {
                            if (!response.equals("")&&!response.equals("[]")) {
                                Toast.makeText(NewClientEntry.this,"Success",Toast.LENGTH_LONG).show();
                                if (forUpdate && UserTypeID == 1) {
                                    Intent mainIntent = new Intent(NewClientEntry.this, ClientList.class);
                                    startActivity(mainIntent);
                                    finish();
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(NewClientEntry.this,"Not Successful !!!",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                }));
    }

    private void entryNewClientByPostingDataToServer() {
        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(getString(R.string.baseUrl))
                .baseUrl("http://172.16.1.2:4000")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        List<MultipartBody.Part> vCardParts = new ArrayList<>();
        List<MultipartBody.Part> photoParts = new ArrayList<>();

        if(vCardFileArray.size()<1 && photoFileArray.size()>=1) {
            for (int i=0; i<photoFileArray.size(); i++) {
                photoParts.add(prepareFilePart(photoFileArray.get(i)));
            }

            Observable<String> photoObservable = retrofit
                    .create(RetrofitNetworkService.class)
                    .uploadMultipleFilesDynamic(photoParts)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io());

            finalDisposer.add(photoObservable
                    .observeOn(Schedulers.io())
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io())
                    .subscribeWith(new DisposableObserver<String>() {

                        @Override
                        public void onNext(String combinedReturnValue) {
                            JsonParser parser = new JsonParser();
                            JsonArray photoUrls = parser.parse(combinedReturnValue).getAsJsonArray();
                            JsonArray parsedPhotoUrls = new JsonArray();
                            for(int i = 0; i<photoUrls.size(); i++) {
                                JsonObject jsonObject = new JsonObject();
                                if(photoUrls.get(i).isJsonObject()) {
                                    JsonObject tempJsonObject = photoUrls.get(i).getAsJsonObject();
                                    jsonObject.addProperty("PhotoID", 0);
                                    jsonObject.addProperty("PhotoURL", tempJsonObject.get("path").getAsString());
                                }
                                parsedPhotoUrls.add(jsonObject);
                            }
                            newClient.add("Photo", parsedPhotoUrls);
                            postNewClientToServer();
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e("RXANDROID", "onError: " + e.getMessage());
                        }

                        @Override
                        public void onComplete() {
                            Log.e("COMPLETE", "Complete: ");
                        }
                    }));

        } else if (photoFileArray.size()<1 && vCardFileArray.size()>=1) {
            for (int i=0; i<vCardFileArray.size(); i++) {
                vCardParts.add(prepareFilePart(vCardFileArray.get(i)));
            }

            Observable<String> vCardObservable = retrofit
                    .create(RetrofitNetworkService.class)
                    .uploadMultipleFilesDynamic(vCardParts)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io());

            finalDisposer.add( vCardObservable
                    .observeOn(Schedulers.io())
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io())
                    .subscribeWith(new DisposableObserver<String>() {

                        @Override
                        public void onNext(String combinedReturnValue) {
                            JsonParser parser = new JsonParser();
                            JsonArray vCardUrls = parser.parse(combinedReturnValue).getAsJsonArray();
                            JsonArray parsedVCardUrls = new JsonArray();
                            for(int i = 0; i<vCardUrls.size(); i++) {
                                JsonObject jsonObject = new JsonObject();
                                if(vCardUrls.get(i).isJsonObject()) {
                                    JsonObject tempJsonObject = vCardUrls.get(i).getAsJsonObject();
                                    jsonObject.addProperty("VCardID", 0);
                                    jsonObject.addProperty("VCardURL", tempJsonObject.get("path").getAsString());
                                }
                                parsedVCardUrls.add(jsonObject);
                            }
                            newClient.add("VCard", parsedVCardUrls);
                            postNewClientToServer();
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e("RXANDROID", "onError: " + e.getMessage());
                        }

                        @Override
                        public void onComplete() {
                            Log.e("COMPLETE", "Complete: ");
                        }
                    }));

        } else if (vCardFileArray.size()<1 && photoFileArray.size()<1){
            postNewClientToServer();
        } else if (vCardFileArray.size()>=1 && photoFileArray.size()>=1){
            for (int i=0; i<vCardFileArray.size(); i++) {
                vCardParts.add(prepareFilePart(vCardFileArray.get(i)));
            }

            for (int i=0; i<photoFileArray.size(); i++) {
                photoParts.add(prepareFilePart(photoFileArray.get(i)));
            }

            Observable<String> vCardObservable = retrofit
                    .create(RetrofitNetworkService.class)
                    .uploadMultipleFilesDynamic(vCardParts)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io());

            Observable<String> photoObservable = retrofit
                    .create(RetrofitNetworkService.class)
                    .uploadMultipleFilesDynamic(photoParts)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io());

            Observable<String> combinedReturnValueObservable = Observable.zip(vCardObservable, photoObservable, (vCardResponse, photoResponse) -> vCardResponse+"--"+photoResponse);

            finalDisposer.add(combinedReturnValueObservable
                    .observeOn(Schedulers.io())
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io())
                    .subscribeWith(new DisposableObserver<String>() {

                        @Override
                        public void onNext(String combinedReturnValue) {
                            String stringArray[] = combinedReturnValue.split("--");
                            JsonParser parser = new JsonParser();
                            JsonArray vCardUrls = parser.parse(stringArray[0]).getAsJsonArray();
                            JsonArray parsedVCardUrls = new JsonArray();
                            for(int i = 0; i<vCardUrls.size(); i++) {
                                JsonObject jsonObject = new JsonObject();
                                if(vCardUrls.get(i).isJsonObject()) {
                                    JsonObject tempJsonObject = vCardUrls.get(i).getAsJsonObject();
                                    jsonObject.addProperty("VCardID", 0);
                                    jsonObject.addProperty("VCardURL", tempJsonObject.get("path").getAsString());
                                }
                                parsedVCardUrls.add(jsonObject);
                            }
                            newClient.add("VCard", parsedVCardUrls);
                            JsonArray photoUrls = parser.parse(stringArray[1]).getAsJsonArray();
                            JsonArray parsedPhotoUrls = new JsonArray();
                            for(int i = 0; i<photoUrls.size(); i++) {
                                JsonObject jsonObject = new JsonObject();
                                if(photoUrls.get(i).isJsonObject()) {
                                    JsonObject tempJsonObject = photoUrls.get(i).getAsJsonObject();
                                    jsonObject.addProperty("PhotoID", 0);
                                    jsonObject.addProperty("PhotoURL", tempJsonObject.get("path").getAsString());
                                }
                                parsedPhotoUrls.add(jsonObject);
                            }
                            newClient.add("Photo", parsedPhotoUrls);
                            postNewClientToServer();
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e("RXANDROID", "onError: " + e.getMessage());
                        }

                        @Override
                        public void onComplete() {
                            Log.e("COMPLETE", "Complete: ");
                        }
                    }));
        }
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
                    try {
                        vCardFileArray.add(ImageUtils.getFileFromBitmap(bitmap, this));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    vCardAdapter.notifyDataSetChanged();
                } else if (requestCode == REQUEST_PHOTO) {
                    photoBitmapArray.add(bitmap);
                    try {
                        photoFileArray.add(ImageUtils.getFileFromBitmap(bitmap, this));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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
                    try {
                        vCardFileArray.add(ImageUtils.getFileFromBitmap(bitmap, this));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    vCardAdapter.notifyDataSetChanged();
                } else if (requestCode == REQUEST_PHOTO) {
                    photoBitmapArray.add(bitmap);
                    try {
                        photoFileArray.add(ImageUtils.getFileFromBitmap(bitmap, this));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    photoAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (forUpdate) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else if(UserTypeID == 1) {
                Intent mainIntent = new Intent(NewClientEntry.this, ClientList.class);
                startActivity(mainIntent);
                finish();
            }
        } else {
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
        snackbar.setAction("UNDO", view -> {

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
        });
        snackbar.setActionTextColor(Color.YELLOW);
        snackbar.show();
    }

    @Override
    public void onImageSelected(int position, int type, Bitmap bitmap) {
        if (forUpdate) {
            FullScreenImageViewDialog fullScreenImageViewDialog = new FullScreenImageViewDialog(this, this, bitmap);
            fullScreenImageViewDialog.setCancelable(true);
            fullScreenImageViewDialog.show();
        } else {
            if (type == VCARD) {
                FullScreenImageViewDialog fullScreenImageViewDialog = new FullScreenImageViewDialog(this, this, vCardBitmapArray.get(position));
                fullScreenImageViewDialog.setCancelable(true);
                fullScreenImageViewDialog.show();
            } else if (type == PHOTO) {
                FullScreenImageViewDialog fullScreenImageViewDialog = new FullScreenImageViewDialog(this, this, photoBitmapArray.get(position));
                fullScreenImageViewDialog.setCancelable(true);
                fullScreenImageViewDialog.show();
            }
        }
    }

    private void InstituteTypeGetRequest() {
        if(StaticHelperClass.isNetworkAvailable(this)) {
            progressBar.setVisibility(View.VISIBLE);
            whiteBackground.setVisibility(View.VISIBLE);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(getString(R.string.baseUrl))
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
            Observable<String> insTypeObservable = retrofit
                    .create(RetrofitNetworkService.class)
                    .getInstituteType()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io());

            finalDisposer.add(insTypeObservable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io())
                    .subscribeWith(new DisposableObserver<String>() {

                        @Override
                        public void onNext(String insTypeReturnValue) {
                            parseInstituteTypeData(insTypeReturnValue);
                        }

                        @Override
                        public void onError(Throwable e) {
                            progressBar.setVisibility(View.INVISIBLE);
                            whiteBackground.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onComplete() {
                            progressBar.setVisibility(View.INVISIBLE);
                            whiteBackground.setVisibility(View.INVISIBLE);
                        }
                    }));
        } else {
            Toast.makeText(NewClientEntry.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    void parseInstituteTypeData(String jsonString) {
        int instituteType = -12345;
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
                if(updatedInstituteType != 0 && jsonObject.getInt("InstituteTypeID") == updatedInstituteType) {
                    instituteType = i;
                }
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
        if (instituteType != -12345) {
            spinnerInsType.setSelection(instituteType+1);
            selectedInstituteType = allInstituteType.get(instituteType);
        }

    }

    private void PriorityDataGetRequest() {
        if(StaticHelperClass.isNetworkAvailable(this)) {
            progressBar.setVisibility(View.VISIBLE);
            whiteBackground.setVisibility(View.VISIBLE);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(getString(R.string.baseUrl))
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
            Observable<String> priorityObservable = retrofit
                    .create(RetrofitNetworkService.class)
                    .getCRMPriority()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io());

            finalDisposer.add(priorityObservable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io())
                    .subscribeWith(new DisposableObserver<String>() {

                        @Override
                        public void onNext(String priorityReturnValue) {
                            parsePriorityData(priorityReturnValue);
                        }

                        @Override
                        public void onError(Throwable e) {
                            progressBar.setVisibility(View.INVISIBLE);
                            whiteBackground.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onComplete() {
                            progressBar.setVisibility(View.INVISIBLE);
                            whiteBackground.setVisibility(View.INVISIBLE);
                        }
                    }));
        } else {
            Toast.makeText(NewClientEntry.this,"Please check your internet connection and select again!!! ",
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
                if(updatedPriority != 0 && jsonObject.getInt("PriorityID") == updatedPriority) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        switch (UserTypeID) {
            case 1:
                if (forUpdate) {
                    getMenuInflater().inflate(R.menu.main, menu);
                    menu.add(0, CLIENT_COMMUNICATION_DETAIL, 0, "Details")
                            .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                } else {
                    getMenuInflater().inflate(R.menu.main, menu);
                }
                break;
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        switch (UserTypeID) {
            case 1:
                break;
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (UserTypeID) {
            case 1:
                if (id == CLIENT_COMMUNICATION_DETAIL) {
                    Intent intent = new Intent(NewClientEntry.this, ClientCommunicationDetailList.class);
                    intent.putExtra("clientData", clientData);
                    startActivity(intent);
                    finish();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
