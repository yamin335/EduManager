package onair.onems.crm;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DownloadManager;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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
import onair.onems.mainactivities.CommonToolbarParentActivity;
import onair.onems.models.DocumentModel;
import onair.onems.models.PaymentMethodModel;
import onair.onems.models.WorkOrderModel;
import onair.onems.utils.FileUtils;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static onair.onems.Services.StaticHelperClass.isNetworkAvailable;

public class WorkOrder extends CommonToolbarParentActivity implements FileAdapter.DeleteUri, FileAdapter.DownloadFile{

    private WorkOrderModel newWorkOrder;
    private Spinner paymentSpinner;
    private EditText workOrderNo, workOrderValue, noOfInstallment;
    private TextView workOrderDate;
    private TextView installmentStartDate;
    private String[] tempPaymentType = {"Payment Type"};
    private ArrayList<PaymentMethodModel> allPaymentType;
    private PaymentMethodModel selectedPaymentType;
    private String orderDate = "", installmentDate = "";
    private int updatedPaymentType = 0;
    private static final int REQUEST_CODE = 3359;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 3369;
    private FileAdapter fileAdapter;
    private ArrayList<Uri> fileUriArray;
    private Uri backupFileUri;
    private CoordinatorLayout coordinatorLayout;
    private boolean forUpdate = false;
    private DatePickerDialog datePickerDialog;
    private static final String TAG = "ClientComDetail:";
    private ArrayList<Long> refIdList = new ArrayList<>();
    private CompositeDisposable finalDisposer = new CompositeDisposable();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(onComplete);
        if (!finalDisposer.isDisposed())
            finalDisposer.dispose();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View childActivityLayout = Objects.requireNonNull(inflater).inflate(R.layout.client_work_order, null);
        LinearLayout parentActivityLayout = findViewById(R.id.contentMain);
        parentActivityLayout.addView(childActivityLayout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        paymentSpinner = findViewById(R.id.spinnerPriority);
        workOrderNo = findViewById(R.id.workOrderNo);
        workOrderValue = findViewById(R.id.workOrderValue);
        noOfInstallment = findViewById(R.id.noOfInstallment);
        workOrderDate = findViewById(R.id.comDate);
        installmentStartDate = findViewById(R.id.comDate1);
        TextView salesPerson = findViewById(R.id.textView17);
        ImageView orderDateIcon = findViewById(R.id.dateIcon);
        ImageView installmentDateIcon = findViewById(R.id.dateIcon1);
        RecyclerView fileRecycler = findViewById(R.id.recycler);
        coordinatorLayout = findViewById(R.id.coordinator_layout);
        Button proceed = findViewById(R.id.proceed);
        View whiteBackground = findViewById(R.id.whiteBackground);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        whiteBackground.setVisibility(View.INVISIBLE);

        newWorkOrder = new WorkOrderModel();
        allPaymentType = new ArrayList<>();
        selectedPaymentType = new PaymentMethodModel();

        fileUriArray = new ArrayList<>();
        fileAdapter = new FileAdapter(this, fileUriArray, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        fileRecycler.setLayoutManager(layoutManager);
        fileRecycler.setItemAnimator(new DefaultItemAnimator());
        fileRecycler.setAdapter(fileAdapter);

        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
        orderDate = df.format(date);
        installmentDate = orderDate;

        workOrderDate.setText(orderDate);
        installmentStartDate.setText(installmentDate);

        Intent extraIntent = getIntent();
        if (extraIntent.hasExtra("clientData")) {
            String clientData = extraIntent.getStringExtra("clientData");
            try {
                JSONObject jsonObject = new JSONObject(clientData);
                newWorkOrder.setAgentID(jsonObject.getString("AgentID"));
                newWorkOrder.setNewClientID(jsonObject.getString("NewClientID"));
                newWorkOrder.setSalesPerson(UserFullName);
                salesPerson.setText(UserFullName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (extraIntent.hasExtra("forUpdate")) {
            forUpdate = true;
            try {
                JSONObject jsonObject = new JSONArray(extraIntent.getStringExtra("forUpdate")).getJSONObject(0);
                newWorkOrder.setWrkOrdrID(jsonObject.getString("WrkOrdrID"));
                newWorkOrder.setWrkOrdrNo(jsonObject.getString("WrkOrdrNo"));
                workOrderNo.setText(jsonObject.getString("WrkOrdrNo"));
                newWorkOrder.setAgentID(jsonObject.getString("AgentID"));
                newWorkOrder.setNewClientID(jsonObject.getString("NewClientID"));
                newWorkOrder.setWrkOrdrDate(jsonObject.getString("WrkOrdrDate"));
                orderDate = jsonObject.getString("WrkOrdrDate");
                workOrderDate.setText(jsonObject.getString("WrkOrdrDate"));
                newWorkOrder.setWrkOrdrValue(jsonObject.getString("WrkOrdrValue"));
                workOrderValue.setText(jsonObject.getString("WrkOrdrValue"));
                newWorkOrder.setInstlmntStrtDate(jsonObject.getString("InstlmntStrtDate"));
                installmentStartDate.setText(jsonObject.getString("InstlmntStrtDate"));
                installmentDate = jsonObject.getString("InstlmntStrtDate");
                newWorkOrder.setNoOfInstlmnt(jsonObject.getString("NoOfInstlmnt"));
                noOfInstallment.setText(jsonObject.getString("NoOfInstlmnt"));
                newWorkOrder.setPaymentMethodID(jsonObject.getString("PaymentMethodID"));
                updatedPaymentType = jsonObject.getInt("PaymentMethodID");
                newWorkOrder.setPaymentMethodName(jsonObject.getString("PaymentMethodName"));
                newWorkOrder.setSalesPerson(jsonObject.getString("SalesPerson"));
                salesPerson.setText(jsonObject.getString("SalesPerson"));

                fileAdapter = new FileAdapter(this, jsonObject.getJSONArray("DocumentList"), forUpdate, this);
                fileRecycler.setLayoutManager(layoutManager);
                fileRecycler.setItemAnimator(new DefaultItemAnimator());
                fileRecycler.setAdapter(fileAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        ImageView addFile = findViewById(R.id.addFile);
        if (forUpdate) {
            addFile.setClickable(false);
            addFile.setVisibility(View.GONE);
            proceed.setText(R.string.update);

        }

        addFile.setOnClickListener((view)-> {
            if (checkPermissionREAD_EXTERNAL_STORAGE(WorkOrder.this)) {
                // do your stuff..
                // Display the file chooser dialog
                showChooser();
            }
        });

        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        orderDateIcon.setOnClickListener(v -> {
            // calender class's instance and get current date , month and year from calender
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR); // current year
            int mMonth = c.get(Calendar.MONTH); // current month
            int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
            // date picker dialog
            datePickerDialog = new DatePickerDialog(WorkOrder.this,
                    (view, year, monthOfYear, dayOfMonth) -> {
                        // set day of month , month and year value in the edit text
//                                selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
//                                selectedDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                        orderDate = (monthOfYear + 1)+"-"+dayOfMonth+"-"+year;
                        workOrderDate.setText(orderDate);

                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        });

        installmentDateIcon.setOnClickListener(v -> {
            // calender class's instance and get current date , month and year from calender
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR); // current year
            int mMonth = c.get(Calendar.MONTH); // current month
            int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
            // date picker dialog
            datePickerDialog = new DatePickerDialog(WorkOrder.this,
                    (view, year, monthOfYear, dayOfMonth) -> {
                        // set day of month , month and year value in the edit text
//                                selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
//                                selectedDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                        installmentDate = (monthOfYear + 1)+"-"+dayOfMonth+"-"+year;
                        installmentStartDate.setText(orderDate);

                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        });

        ArrayAdapter<String> PaymentTypeAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, tempPaymentType);
        PaymentTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paymentSpinner.setAdapter(PaymentTypeAdapter);

        PaymentTypeGetRequest();

        paymentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != 0) {
                    try {
                        selectedPaymentType = allPaymentType.get(position-1);
                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(WorkOrder.this,"No payment method found !!!",Toast.LENGTH_LONG).show();
                    }
                } else {
                    selectedPaymentType = new PaymentMethodModel();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        proceed.setOnClickListener(view -> {
            if (workOrderNo.getText().toString().equals("")) {
                workOrderNo.setError("Insert Work Order No.");
                workOrderNo.requestFocus();
            } else if (orderDate.equals("")) {
                Toast.makeText(WorkOrder.this,"Select Work Order Date",
                        Toast.LENGTH_LONG).show();
            } else if (workOrderValue.getText().toString().equals("")) {
                workOrderValue.setError("Insert Work Order Value");
                workOrderValue.requestFocus();
            } else if (installmentDate.equals("")) {
                Toast.makeText(WorkOrder.this,"Select Installment Start Date",
                        Toast.LENGTH_LONG).show();
            } else if (noOfInstallment.getText().toString().equals("")) {
                noOfInstallment.setError("Insert Installment No.");
                noOfInstallment.requestFocus();
            } else if (selectedPaymentType.getPaymentMethodID() == 0) {
                Toast.makeText(WorkOrder.this,"Select Payment Type",
                        Toast.LENGTH_LONG).show();
            } else {
                newWorkOrder.setWrkOrdrNo(workOrderNo.getText().toString());
                newWorkOrder.setWrkOrdrDate(orderDate);
                newWorkOrder.setWrkOrdrValue(workOrderValue.getText().toString());
                newWorkOrder.setInstlmntStrtDate(installmentDate);
                newWorkOrder.setNoOfInstlmnt(noOfInstallment.getText().toString());
                newWorkOrder.setPaymentMethodID(Integer.toString(selectedPaymentType.getPaymentMethodID()));
                newWorkOrder.setPaymentMethodName(selectedPaymentType.getPaymentMethodName());
                postDocumentsAndData();
            }
        });
    }

    BroadcastReceiver onComplete = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {

            // get the refId from the download manager
            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

            // Open downloaded file
            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(referenceId);
            Cursor cursor = Objects.requireNonNull(downloadManager).query(query);
            if (cursor.moveToFirst()) {
                int downloadStatus = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                String downloadLocalUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                String downloadMimeType = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_MEDIA_TYPE));
                if ((downloadStatus == DownloadManager.STATUS_SUCCESSFUL) && downloadLocalUri != null) {
//                    try {
//                        downloadManager.openDownloadedFile(referenceId);
//                        openFile(downloadLocalUri);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                }
            }
            cursor.close();

            // remove it from our list
            refIdList.remove(referenceId);

            // if list is empty means all downloads completed
            if (refIdList.isEmpty()) {
                // show a notification
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(WorkOrder.this)
                                .setSmallIcon(R.drawable.onair)
                                .setContentTitle("Download Completed")
                                .setContentText("All Downloads are completed");


                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                Objects.requireNonNull(notificationManager).notify(455, mBuilder.build());
            }
        }
    };

    private boolean isPermissionAllowed() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    @NonNull
    private String getFileExtension(String string) {
        if(string.contains(".png")||string.contains(".PNG")){
            return ".png";
        } else if(string.contains(".jpg")||string.contains(".JPG")){
            return ".jpg";
        } else if(string.contains(".jpeg")||string.contains(".JPEG")){
            return ".jpeg";
        } else if(string.contains(".gif")||string.contains(".GIF")){
            return ".gif";
        } else if(string.contains(".bmp")||string.contains(".BMP")){
            return ".bmp";
        } else if(string.contains(".mp3")||string.contains(".MP3")){
            return ".mp3";
        } else if(string.contains(".amr")||string.contains(".AMR")){
            return ".amr";
        } else if(string.contains(".wav")||string.contains(".WAV")){
            return ".wav";
        } else if(string.contains(".aac")||string.contains(".AAC")){
            return ".aac";
        } else if(string.contains(".mp4")||string.contains(".MP4")){
            return ".mp4";
        } else if(string.contains(".wmv")||string.contains(".WMV")){
            return ".wmv";
        } else if(string.contains(".avi")||string.contains(".AVI")){
            return ".avi";
        } else if(string.contains(".flv")||string.contains(".FLV")){
            return ".flv";
        } else if(string.contains(".mov")||string.contains(".MOV")){
            return ".mov";
        } else if(string.contains(".vob")||string.contains(".VOB")){
            return ".vob";
        } else if(string.contains(".mpeg")||string.contains(".MPEG")){
            return ".mpeg";
        } else if(string.contains(".3gp")||string.contains(".3GP")){
            return ".3gp";
        } else if(string.contains(".mpg")||string.contains(".MPG")){
            return ".mpg";
        } else if(string.contains(".wmv")||string.contains(".WMV")){
            return ".wmv";
        } else if(string.contains(".octet-stream")){
            return ".rar";
        } else if(string.contains(".vnd.openxmlformats-officedocument.wo")){
            return ".doc";
        } else if(string.contains(".plain")){
            return ".txt";
        } else if(string.contains(".vnd.openxmlformats-officedocument.sp")){
            return ".xls";
        } else if(string.contains(".x-zip-compressed")){
            return ".zip";
        } else if(string.contains(".vnd.openxmlformats-officedocument.presentationml.p")){
            return ".ppt";
        } else if(string.contains(".pdf")||string.contains(".PDF")){
            return ".pdf";
        } else {
            return "UnknownFileType";
        }
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

    private void postDocumentsAndData() {
        if (fileUriArray.size()>0) {
            Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(getString(R.string.baseUrl))
                    .baseUrl("http://172.16.1.2:4000")
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();

            List<MultipartBody.Part> documentParts = new ArrayList<>();

            for (int i=0; i<fileUriArray.size(); i++) {
                documentParts.add(prepareFilePart(FileUtils.getFile(this, fileUriArray.get(i))));
            }

            Observable<String> documentObservable = retrofit
                    .create(RetrofitNetworkService.class)
                    .uploadMultipleFilesDynamic(documentParts)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io());

            finalDisposer.add( documentObservable
                    .observeOn(Schedulers.io())
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io())
                    .subscribeWith(new DisposableObserver<String>() {

                        @Override
                        public void onNext(String returnValue) {
                            JsonParser parser = new JsonParser();
                            JsonArray documentUrls = parser.parse(returnValue).getAsJsonArray();
                            ArrayList<DocumentModel> parsedDocumentUrls = new ArrayList<>();
                            for(int i = 0; i<documentUrls.size(); i++) {
                                if(documentUrls.get(i).isJsonObject()) {
                                    JsonObject tempJsonObject = documentUrls.get(i).getAsJsonObject();
                                    DocumentModel documentModel = new DocumentModel("0", tempJsonObject.get("path").getAsString());
                                    parsedDocumentUrls.add(documentModel);
                                }
                            }
                            newWorkOrder.setDocumentList(parsedDocumentUrls);
                            workOrderPostRequest();
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

        } else {
            workOrderPostRequest();
        }
    }

    private void workOrderPostRequest() {
        if(StaticHelperClass.isNetworkAvailable(this)) {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(getString(R.string.baseUrl))
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
            Observable<String> orderPostObservable = retrofit
                    .create(RetrofitNetworkService.class)
                    .postWorkOrder(newWorkOrder)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io());

            finalDisposer.add(orderPostObservable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io())
                    .subscribeWith(new DisposableObserver<String>() {

                        @Override
                        public void onNext(String paymentTypeReturnValue) {
                            Toast.makeText(WorkOrder.this,"Successful",
                                    Toast.LENGTH_LONG).show();
                            finish();
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(WorkOrder.this,"Not Successful",
                                    Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    }));

        } else {
            Toast.makeText(WorkOrder.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void PaymentTypeGetRequest() {
        if(StaticHelperClass.isNetworkAvailable(this)) {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(getString(R.string.baseUrl))
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
            Observable<String> paymentTypeObservable = retrofit
                    .create(RetrofitNetworkService.class)
                    .getPaymentMethod()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io());

            finalDisposer.add(paymentTypeObservable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io())
                    .subscribeWith(new DisposableObserver<String>() {

                        @Override
                        public void onNext(String paymentTypeReturnValue) {
                            parsePaymentTypeData(paymentTypeReturnValue);
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    }));

        } else {
            Toast.makeText(WorkOrder.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    void parsePaymentTypeData(String jsonString) {
        int paymentType = -12345;
        try {
            allPaymentType = new ArrayList<>();
            JSONArray jsonArray = new JSONArray(jsonString);
            ArrayList<String> arrayList = new ArrayList<>();
            arrayList.add("Payment Type");
            for(int i = 0; i < jsonArray.length(); ++i) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                PaymentMethodModel paymentMethodModel = new PaymentMethodModel(jsonObject.getString("PaymentMethodID"),
                        jsonObject.getString("PaymentMethodName"),
                        jsonObject.getString("IsActive"),
                        jsonObject.getString("IsDeleted"));
                allPaymentType.add(paymentMethodModel);
                arrayList.add(paymentMethodModel.getPaymentMethodName());
                if(updatedPaymentType != 0 && jsonObject.getInt("PaymentMethodID") == updatedPaymentType) {
                    paymentType = i;
                }
            }
            try {
                String[] strings = new String[arrayList.size()];
                strings = arrayList.toArray(strings);
                ArrayAdapter<String> spinner_adapter = new ArrayAdapter<>(this,R.layout.spinner_item, strings);
                spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                paymentSpinner.setAdapter(spinner_adapter);
            } catch (IndexOutOfBoundsException e) {
                Toast.makeText(this,"No Payment Type found !!!",Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
        }
        if (paymentType != -12345) {
            paymentSpinner.setSelection(paymentType+1);
            selectedPaymentType = allPaymentType.get(paymentType);
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
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
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

    @Override
    public void onFileSelected(JSONObject jsonObject) {
        if(isNetworkAvailable(this)){
            if(isPermissionAllowed()){
                try {
                    String url = jsonObject.getString("DocumentUrl");
                    url = url.replace("\\","/");
                    String s[] = url.split("/");
                    String file = s[s.length-1];
                    String fileName = file.split("\\.")[0]+getFileExtension(url);

                    // get download service
                    DownloadManager manager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
//                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(getString(R.string.baseUrl)+"/"+url));
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse("http://172.16.1.2:4000"+"/"+url));
                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                    request.setVisibleInDownloadsUi(true);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
                    request.setDescription("Some descrition");
                    request.setTitle(fileName);
                    // in order for this if to run, you must use the android 3.2 to compile your app
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        request.allowScanningByMediaScanner();
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    }
                    long refId = Objects.requireNonNull(manager).enqueue(request);
                    refIdList.add(refId);

                } catch (Exception e){
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "You denied to download!!!", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Please check your INTERNET connection!!!", Toast.LENGTH_LONG).show();
        }
    }
}
