package onair.onems.mainactivities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.system.ErrnoException;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.theartofdev.edmodo.cropper.CropImageView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import onair.onems.R;
import onair.onems.Services.GlideApp;
import onair.onems.customadapters.CustomRequest;
import onair.onems.models.StudentInformationEntry;
import onair.onems.network.MySingleton;

public class StudentiCardDetails extends AppCompatActivity {

    private TextView t_name, t_class, t_section, t_birthDay, t_email, t_address,
            t_parent, t_parentsPhone,t_roll,t_department,t_sex,t_religion,t_medium,t_board,
            t_session, t_shift,t_branch, t_stuEmail, t_stuPhone, t_remarks, t_studentNo, t_rfid;

    private CropImageView mCropImageView;
    private CheckBox checkBox;
    private Uri mCropImageUri;
    private ProgressDialog mStudentDataPostDialog, mRotateDialog, mBrightnessDialog, mStudentDataGetDialog;

    private StudentInformationEntry studentInformationEntry;

    private JSONObject jsonObjectStudentData;

    private File file;

    private Bitmap originalBitmap = null;
    private Bitmap tempBitmap = null;

    private FileFromBitmap fileFromBitmap = null;

    private String selectedClass, selectedShift,
            selectedSection, selectedMedium, selectedDepartment, UserID;
    private int PICK_IMAGE_REQUEST = 1;

    private long InstituteID;
    private static int brightnessValue;
    private BrightnessProcessTask mBrightnessProcessTask = null;
    private RotateProcessTask mRotateProcessTask = null;
    private boolean imageChanged = false;
    private ProgressBar progressBar;

    @Override
    public void onResume() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        super.onResume();
    }

    @Override
    public void onStart() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        super.onStart();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.icard_student_details);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        InstituteID = prefs.getLong("InstituteID",0);
        selectedClass = bundle.getString("ClassID");
        selectedShift = bundle.getString("ShiftID");
        selectedSection = bundle.getString("SectionID");
        selectedMedium = bundle.getString("MediumID");
        selectedDepartment = bundle.getString("DepartmentID");
        UserID = bundle.getString("UserID");

        Button rotateLeft = (Button)findViewById(R.id.rotateLeft);
        Button rotateRight = (Button)findViewById(R.id.rotateRight);

        SeekBar brightImageSeekBar = (SeekBar)findViewById(R.id.brightness);
        brightImageSeekBar.setProgress(100);
        progressBar = (ProgressBar)findViewById(R.id.spin_kit);

        mCropImageView = (CropImageView)findViewById(R.id.CropImageView);
        mCropImageView.setAspectRatio(1,1);
        mCropImageView.setAutoZoomEnabled(true);
        mCropImageView.setFixedAspectRatio(true);

        Button updateStudentPhoto = (Button)findViewById(R.id.updatephoto);

        checkBox = (CheckBox)findViewById(R.id.checkbox);

        Button cameraButton=(Button)findViewById(R.id.camera);
        Button searchButton=(Button) findViewById(R.id.browse);

        t_roll = (TextView)findViewById(R.id.roll);
        t_name = (TextView)findViewById(R.id.name);
        t_class = (TextView)findViewById(R.id.classs);
        t_section = (TextView)findViewById(R.id.section);
        t_birthDay = (TextView)findViewById(R.id.birthday);
        t_email = (TextView)findViewById(R.id.guardianemail);
        t_address = (TextView)findViewById(R.id.address);
        t_parent = (TextView)findViewById(R.id.guardian);
        t_parentsPhone = (TextView)findViewById(R.id.guardianPhone);
        t_department = (TextView)findViewById(R.id.department);
        t_sex = (TextView)findViewById(R.id.sex);
        t_religion = (TextView)findViewById(R.id.religion);
        t_medium = (TextView)findViewById(R.id.medium);
        t_session = (TextView)findViewById(R.id.session);
        t_board = (TextView)findViewById(R.id.board);
        t_shift = (TextView)findViewById(R.id.shift);
        t_branch = (TextView)findViewById(R.id.branch);
        t_stuEmail = (TextView)findViewById(R.id.stuEmail);
        t_stuPhone = (TextView)findViewById(R.id.stuPhone);
        t_remarks = (TextView)findViewById(R.id.remarks);
        t_studentNo = (TextView)findViewById(R.id.studentNo);
        t_rfid = (TextView)findViewById(R.id.rfid);

        StudentDataGetRequest();

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(getPickImageChooserIntent(), 200);
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                // Show only images, no videos or anything else
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                // Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (originalBitmap != null)
                {
                    imageChanged = true;
                }

            }
        });

        rotateLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(originalBitmap != null)
                {
                    mRotateDialog = new ProgressDialog(StudentiCardDetails.this);
                    mRotateDialog.setTitle("Loading...");
                    mRotateDialog.setMessage("Please Wait...");
                    mRotateDialog.setCancelable(false);
                    mRotateDialog.setIcon(R.drawable.onair);
                    mRotateDialog.show();
                    imageChanged = true;
                    mRotateProcessTask = new RotateProcessTask(originalBitmap, -90);
                    mRotateProcessTask.execute((Void) null);
                }
                else
                {
                    Toast.makeText(StudentiCardDetails.this,"No image found!!!",Toast.LENGTH_LONG).show();
                }
            }
        });

        rotateRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(originalBitmap != null)
                {
                    mRotateDialog = new ProgressDialog(StudentiCardDetails.this);
                    mRotateDialog.setTitle("Loading...");
                    mRotateDialog.setMessage("Please Wait...");
                    mRotateDialog.setCancelable(false);
                    mRotateDialog.setIcon(R.drawable.onair);
                    mRotateDialog.show();
                    imageChanged = true;
                    mRotateProcessTask = new RotateProcessTask(originalBitmap, 90);
                    mRotateProcessTask.execute((Void) null);
                }
                else
                {
                    Toast.makeText(StudentiCardDetails.this,"No image found!!!",Toast.LENGTH_LONG).show();
                }
            }
        });

        brightImageSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                brightnessValue = progress - 100;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(originalBitmap != null)
                {
                    mBrightnessDialog = new ProgressDialog(StudentiCardDetails.this);
                    mBrightnessDialog.setTitle("Loading...");
                    mBrightnessDialog.setMessage("Please Wait...");
                    mBrightnessDialog.setCancelable(false);
                    mBrightnessDialog.setIcon(R.drawable.onair);
                    mBrightnessDialog.show();
                    imageChanged = true;
                    mBrightnessProcessTask = new BrightnessProcessTask(originalBitmap, brightnessValue);
                    mBrightnessProcessTask.execute((Void) null);
                }
                else
                {
                    Toast.makeText(StudentiCardDetails.this,"No image found!!!",Toast.LENGTH_LONG).show();
                }
            }
        });

        updateStudentPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNetworkAvailable())
                {
                    if(imageChanged)
                    {
                        mStudentDataPostDialog = new ProgressDialog(StudentiCardDetails.this);
                        mStudentDataPostDialog.setTitle("Loading...");
                        mStudentDataPostDialog.setMessage("Please Wait...");
                        mStudentDataPostDialog.setCancelable(false);
                        mStudentDataPostDialog.setIcon(R.drawable.onair);
                        mStudentDataPostDialog.show();
                        if(checkBox.isChecked())
                        {
                            tempBitmap = mCropImageView.getCroppedImage(500, 500);
                            mCropImageView.setImageBitmap(tempBitmap);
                            fileFromBitmap = new StudentiCardDetails.FileFromBitmap(tempBitmap, StudentiCardDetails.this);
                            fileFromBitmap.execute();
                        }
                        else
                        {
                            fileFromBitmap = new StudentiCardDetails.FileFromBitmap(tempBitmap, StudentiCardDetails.this);
                            fileFromBitmap.execute();
                        }
                    }
                    else
                    {
                        mStudentDataPostDialog.dismiss();
                        Toast.makeText(StudentiCardDetails.this,"Take or choose a photo to update!!!",Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    Toast.makeText(StudentiCardDetails.this,"Please check your INTERNET connection!!!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public class FileFromBitmap extends AsyncTask<Void, Integer, String> {

        Context context;
        Bitmap bitmap;
//        String path_external = Environment.getExternalStorageDirectory() + File.separator + "temporary_file.jpg";

        public FileFromBitmap(Bitmap bitmap, Context context) {
            this.bitmap = bitmap;
            this.context= context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // before executing doInBackground
            // update your UI
            // exp; make progressbar visible
        }

        @Override
        protected String doInBackground(Void... params) {
//            bitmap = getResizedBitmap(bitmap, 500);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//            file = new File(Environment.getExternalStorageDirectory() + File.separator + "temporary_file.jpg");
            file  = new File(context.getCacheDir(), "temporary_file.jpg");
            try {
                FileOutputStream fo = new FileOutputStream(file);
                fo.write(bytes.toByteArray());
                fo.flush();
                fo.close();
            } catch (IOException e) {
                mStudentDataPostDialog.dismiss();
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // back to main thread after finishing doInBackground
            // update your UI or take action after
            // exp; make progressbar gone
            Ion.with(getApplicationContext())
                    .load(getString(R.string.baseUrlLocal)+"Mobile/uploads")
                    .progressDialog(mStudentDataPostDialog)
                    .setMultipartParameter("name", "source")
                    .setMultipartFile("file", "image/jpeg", file)
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
                            //do stuff with result
                            try {
                                JSONObject jsonObject = new JSONObject(result);
//                                Toast.makeText(StudentiCardDetails.this,jsonObject.getString("path"), Toast.LENGTH_LONG).show();
                                studentInformationEntry.setImageUrl(jsonObject.getString("path"));
                                studentInformationEntry.setIsImageCaptured(true);
                                Gson gson = new Gson();
                                String json = gson.toJson(studentInformationEntry);
                                postUsingVolley(json);
                                Log.d( "ImageUrl", jsonObject.getString("path"));
                            } catch (JSONException e1) {
                                mStudentDataPostDialog.dismiss();
                                e1.printStackTrace();
                            }
                        }
                    });

        }
    }

    public void postUsingVolley(String json)
    {
        String studentDataPostUrl = getString(R.string.baseUrlLocal)+"setStudentBasicInfo";
        try {
            jsonObjectStudentData = new JSONObject(json);
        } catch (JSONException e) {
            mStudentDataPostDialog.dismiss();
            e.printStackTrace();
        }
        CustomRequest customRequest = new CustomRequest (Request.Method.POST, studentDataPostUrl, jsonObjectStudentData,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        mStudentDataPostDialog.dismiss();
                        try {
                            Toast.makeText(StudentiCardDetails.this,"Successfully Updated",Toast.LENGTH_LONG).show();
                            StudentiCardDetails.super.onBackPressed();
                        }
                        catch (Exception e)
                        {
                            mStudentDataPostDialog.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mStudentDataPostDialog.dismiss();
                Toast.makeText(StudentiCardDetails.this,"Not Successfully Updated"+error.toString(),Toast.LENGTH_LONG).show();
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

    void parseStudentJsonData(String jsonString) {
        try {
            JSONArray studentJsonArray = new JSONArray(jsonString);
            JSONObject studentJsonObject = studentJsonArray.getJSONObject(0);
            if(studentJsonObject.getString("ImageUrl").equals("null")){
                progressBar.setVisibility(View.GONE);
                Toast.makeText(StudentiCardDetails.this,"No image found!!!",Toast.LENGTH_LONG).show();
            }
            GlideApp.with(this)
                    .asBitmap()
                    .load(getString(R.string.baseUrlRaw)+studentJsonObject.getString("ImageUrl").replace("\\","/"))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            mCropImageView.setImageBitmap(resource);
                            originalBitmap = resource;
                            tempBitmap = resource;
                            progressBar.setVisibility(View.GONE);
                        }
                        @Override
                        public void onLoadFailed(Drawable errorDrawable) {
                            super.onLoadFailed(errorDrawable);
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(StudentiCardDetails.this,"No image found!!!",Toast.LENGTH_LONG).show();
                        }
                    });

            studentInformationEntry = new StudentInformationEntry();
            studentInformationEntry.setRollNo(studentJsonObject.getString("RollNo"));
            studentInformationEntry.setUserName(studentJsonObject.getString("UserName"));
            studentInformationEntry.setGuardian(studentJsonObject.getString("Guardian"));
            studentInformationEntry.setGuardianPhone(studentJsonObject.getString("GuardianPhone"));
            studentInformationEntry.setGuardianEmailID(studentJsonObject.getString("GuardianEmailID"));
            studentInformationEntry.setDOB(studentJsonObject.getString("DOB"));
            studentInformationEntry.setPreAddress(studentJsonObject.getString("PreAddress"));
            studentInformationEntry.setUserClassID(studentJsonObject.getString("UserClassID"));
            studentInformationEntry.setUserID(studentJsonObject.getString("UserID"));
            studentInformationEntry.setRFID(studentJsonObject.getString("RFID"));
            studentInformationEntry.setStudentNo(studentJsonObject.getString("StudentNo"));
            studentInformationEntry.setSectionID(studentJsonObject.getString("SectionID"));
            studentInformationEntry.setClassID(studentJsonObject.getString("ClassID"));
            studentInformationEntry.setBrunchID(studentJsonObject.getString("BrunchID"));
            studentInformationEntry.setShiftID(studentJsonObject.getString("ShiftID"));
            studentInformationEntry.setRemarks(studentJsonObject.getString("Remarks"));
            studentInformationEntry.setInstituteID(studentJsonObject.getString("InstituteID"));
            studentInformationEntry.setUserTypeID(studentJsonObject.getString("UserTypeID"));
            studentInformationEntry.setGenderID(studentJsonObject.getString("GenderID"));
            studentInformationEntry.setPhoneNo(studentJsonObject.getString("PhoneNo"));
            studentInformationEntry.setEmailID(studentJsonObject.getString("EmailID"));
            studentInformationEntry.setFingerUrl(studentJsonObject.getString("FingerUrl"));
            studentInformationEntry.setSignatureUrl(studentJsonObject.getString("SignatureUrl"));
            studentInformationEntry.setMediumID(studentJsonObject.getString("MediumID"));
            studentInformationEntry.setDepartmentID(studentJsonObject.getString("DepartmentID"));
            studentInformationEntry.setIsImageCaptured(studentJsonObject.getBoolean("IsImageCaptured"));
            studentInformationEntry.setImageUrl(studentJsonObject.getString("ImageUrl"));

            t_roll.setText(studentJsonObject.getString("RollNo"));
            t_name.setText(studentJsonObject.getString("UserName"));
            t_section.setText(studentJsonObject.getString("SectionName"));
            t_birthDay.setText(studentJsonObject.getString("DOB"));
            t_email.setText(studentJsonObject.getString("GuardianEmailID"));
            t_address.setText(studentJsonObject.getString("PreAddress"));
            t_parent.setText(studentJsonObject.getString("Guardian"));
            t_parentsPhone.setText(studentJsonObject.getString("GuardianPhone"));
            t_department.setText(studentJsonObject.getString("DepartmentName"));
            t_sex.setText(studentJsonObject.getString("Gender"));
            t_religion.setText(studentJsonObject.getString("Religion"));
            t_medium.setText(studentJsonObject.getString("MameName"));
            t_session.setText(studentJsonObject.getString("SessionName"));
            t_board.setText(studentJsonObject.getString("BoardName"));
            t_class.setText(studentJsonObject.getString("ClassName"));
            t_shift.setText(studentJsonObject.getString("ShiftName"));
            t_branch.setText(studentJsonObject.getString("BrunchName"));
            t_stuEmail.setText(studentJsonObject.getString("EmailID"));
            t_stuPhone.setText(studentJsonObject.getString("PhoneNo"));
            t_remarks.setText(studentJsonObject.getString("Remarks"));
            t_studentNo.setText(studentJsonObject.getString("StudentNo"));
            t_rfid.setText(studentJsonObject.getString("RFID"));
            mStudentDataGetDialog.dismiss();

        } catch (JSONException e) {
            mStudentDataGetDialog.dismiss();
            Toast.makeText(this,"WARNING!!! "+e,Toast.LENGTH_LONG).show();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            Uri imageUri = getPickImageResultUri(data);

            // For API >= 23 we need to check specifically that we have permissions to read external storage,
            // but we don't know if we need to for the URI so the simplest is to try open the stream and see if we get error.
            boolean requirePermissions = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                    isUriRequiresPermissions(imageUri)) {

                // request permissions and handle the result in onRequestPermissionsResult()
                requirePermissions = true;
                mCropImageUri = imageUri;
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    originalBitmap = getResizedBitmap(bitmap, 500);
                    tempBitmap = originalBitmap;
                    imageChanged = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }

                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            }

            if (!requirePermissions) {
                mCropImageView.setImageUriAsync(imageUri);
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    originalBitmap = getResizedBitmap(bitmap, 500);
                    tempBitmap = originalBitmap;
                    imageChanged = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                originalBitmap = getResizedBitmap(bitmap, 500);
                tempBitmap = originalBitmap;
                imageChanged = true;
                try {
                    String ImagePath = MediaStore.Images.Media.insertImage(this.getContentResolver(), bitmap, "demo_image", "demo_image");
                    Uri URI = Uri.parse(ImagePath);
                    mCropImageView.setImageUriAsync(URI);
                }
                catch (Exception e)
                {
                    mCropImageView.setImageBitmap(bitmap);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mCropImageView.setImageUriAsync(mCropImageUri);
        } else {
            Toast.makeText(this, "Required permissions are not granted", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Create a chooser intent to select the source to get image from.<br/>
     * The source can be camera's (ACTION_IMAGE_CAPTURE) or gallery's (ACTION_GET_CONTENT).<br/>
     * All possible sources are added to the intent chooser.
     */

    public Intent getPickImageChooserIntent() {

        // Determine Uri of camera image to save.
        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getPackageManager();

        // collect all camera intents
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        // collect all gallery intents
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

        // the main intent is the last in the list (fucking android) so pickup the useless one
        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        // Create a chooser from the main intent
        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");

        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

    @Override
    public void onBackPressed() {
//        NavUtils.navigateUpFromSameTask(this);
        super.onBackPressed();
    }

    /**
     * Get URI to image received from capture by camera.
     */



    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "pickImageResult.jpeg"));
        }
        return outputFileUri;
    }

    /**
     * Get the URI of the selected image from {@link #getPickImageChooserIntent()}.<br/>
     * Will return the correct URI for camera and gallery image.
     *
     * @param data the returned data of the activity result
     */

    public Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        if (data != null && data.getData() != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera ? getCaptureImageOutputUri() : data.getData();
    }

    /**
     * Test if we can open the given Android URI to test if permission required error is thrown.<br>
     */

    public boolean isUriRequiresPermissions(Uri uri) {
        try {
            ContentResolver resolver = getContentResolver();
            InputStream stream = resolver.openInputStream(uri);
            stream.close();
            return false;
        } catch (FileNotFoundException e) {
            if (e.getCause() instanceof ErrnoException) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public static Bitmap rotateImage(Bitmap sourceImage, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(sourceImage, 0, 0, sourceImage.getWidth(), sourceImage.getHeight(), matrix, true);
    }

    public static Bitmap doBrightness(Bitmap src, int value) {
        // image size
        int width = src.getWidth();
        int height = src.getHeight();
        // create output bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
        // color information
        int A, R, G, B;
        int pixel;

        // scan through all pixels
        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                // get pixel color
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);

                // increase/decrease each channel
                R += value;
                if(R > 255) { R = 255; }
                else if(R < 0) { R = 0; }

                G += value;
                if(G > 255) { G = 255; }
                else if(G < 0) { G = 0; }

                B += value;
                if(B > 255) { B = 255; }
                else if(B < 0) { B = 0; }

                // apply new pixel color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }

        // return final image
        return bmOut;
    }

    public class BrightnessProcessTask extends AsyncTask<Void, Void, Boolean> {

        private  Bitmap image;
        private  int progressValue;

        private BrightnessProcessTask(Bitmap image, int progressValue) {
            this.image = image;
            this.progressValue = progressValue;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                tempBitmap = doBrightness(image, progressValue);
                Thread.sleep(100);
                return true;
            } catch (InterruptedException e) {
                mBrightnessDialog.dismiss();
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mBrightnessProcessTask = null;
            mBrightnessDialog.dismiss();
            if (success) {
                mCropImageView.setImageBitmap(tempBitmap);
            }
            else{
                mCropImageView.setImageBitmap(originalBitmap);
                Toast.makeText(StudentiCardDetails.this,"One ERROR occurred !!!",Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mBrightnessProcessTask = null;
            mBrightnessDialog.dismiss();
        }
    }

    public class RotateProcessTask extends AsyncTask<Void, Void, Boolean> {
        private  Bitmap image;
        private  int angle;

        private RotateProcessTask(Bitmap image, int angle) {
            this.image = image;
            this.angle = angle;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                originalBitmap = rotateImage(image, angle);
                tempBitmap = rotateImage(tempBitmap, angle);
                Thread.sleep(100);
                return true;
            } catch (InterruptedException e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mRotateProcessTask = null;
            mRotateDialog.dismiss();
            if (success) {
                mCropImageView.setImageBitmap(tempBitmap);
            }
            else{
                mCropImageView.setImageBitmap(originalBitmap);
                Toast.makeText(StudentiCardDetails.this,"One ERROR occured!!!",Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mRotateProcessTask = null;
            mRotateDialog.dismiss();
        }
    }

    public void StudentDataGetRequest(){

        String studentDataGetUrl = getString(R.string.baseUrlLocal)+"getStudent"+"/"+InstituteID+"/"+
                selectedClass+"/"+selectedSection+"/"+
                selectedDepartment+"/"+selectedMedium+"/"+selectedShift+"/"+UserID;

        mStudentDataGetDialog = new ProgressDialog(this);
        mStudentDataGetDialog.setTitle("Loading...");
        mStudentDataGetDialog.setMessage("Please Wait...");
        mStudentDataGetDialog.setCancelable(false);
        mStudentDataGetDialog.setIcon(R.drawable.onair);
        mStudentDataGetDialog.show();
        //Preparing Student data from server
        StringRequest stringStudentRequest = new StringRequest(Request.Method.GET, studentDataGetUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        parseStudentJsonData(response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mStudentDataGetDialog.dismiss();
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
        MySingleton.getInstance(this).addToRequestQueue(stringStudentRequest);
    }
}
