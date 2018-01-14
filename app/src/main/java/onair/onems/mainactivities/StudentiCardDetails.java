package onair.onems.mainactivities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.system.ErrnoException;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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
import java.util.List;

import onair.onems.R;
import onair.onems.customadapters.CustomRequest;
import onair.onems.models.StudentInformationEntry;


/**
 * Created by User on 12/20/2017.
 */

public class StudentiCardDetails extends AppCompatActivity {

    private TextView t_name, t_class, t_section, t_birthDay, t_email, t_address,
            t_parent, t_parentsPhone,t_roll,t_department,t_sex,t_religion,t_medium,t_board,t_session;

    private Bundle bundle;

    private CropImageView mCropImageView;
    private CheckBox checkBox;
    private Uri mCropImageUri;
    ProgressDialog dialog;

    StudentInformationEntry studentInformationEntry;
    Button updateStudentPhoto, cameraButton,searchButton;

    JSONObject jsonObjectStudentData;

    File file;

    Bitmap originalBitmap;

    StudentiCardDetails.FileFromBitmap fileFromBitmap = null;

    String studentDataPostUrl;
    private int PICK_IMAGE_REQUEST = 1;
    private String ImagePath;
    private Uri URI;

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

        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading...");
        dialog.setMessage("Please Wait...");
        dialog.setCancelable(false);
        dialog.show();

//        originalBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.image1);

        mCropImageView = (CropImageView)findViewById(R.id.CropImageView);
        mCropImageView.setAspectRatio(1,1);
        mCropImageView.setAutoZoomEnabled(true);
        mCropImageView.setFixedAspectRatio(true);

        updateStudentPhoto = (Button)findViewById(R.id.updatephoto);

        checkBox = (CheckBox)findViewById(R.id.checkbox);

        cameraButton=(Button)findViewById(R.id.camera);
        searchButton=(Button) findViewById(R.id.browse);

        studentDataPostUrl = getString(R.string.baseUrlLocal)+"setStudentBasicInfo";

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

        Intent intent = getIntent();
        bundle = intent.getExtras();
        t_roll.setText(bundle.getString("RollNo"));
        t_name.setText(bundle.getString("UserName"));
        t_section.setText(bundle.getString("SectionName"));
        t_birthDay.setText(bundle.getString("DOB"));
        t_email.setText(bundle.getString("GuardianEmailID"));
        t_address.setText(bundle.getString("PreAddress"));
        t_parent.setText(bundle.getString("Guardian"));
        t_parentsPhone.setText(bundle.getString("GuardianPhone"));
        t_department.setText(bundle.getString("DepartmentName"));
        t_sex.setText(bundle.getString("Gender"));
        t_religion.setText(bundle.getString("Religion"));
        t_medium.setText(bundle.getString("MameName"));
        t_session.setText(bundle.getString("SessionName"));
        t_board.setText(bundle.getString("BoardName"));
        t_class.setText(bundle.getString("ClassName"));

        studentInformationEntry = new StudentInformationEntry();
        studentInformationEntry.setRollNo(bundle.getString("RollNo"));
        studentInformationEntry.setUserName(bundle.getString("UserName"));
        studentInformationEntry.setGuardian(bundle.getString("Guardian"));
        studentInformationEntry.setGuardianPhone(bundle.getString("GuardianPhone"));
        studentInformationEntry.setGuardianEmailID(bundle.getString("GuardianEmailID"));
        studentInformationEntry.setDOB(bundle.getString("DOB"));
        studentInformationEntry.setPreAddress(bundle.getString("PreAddress"));
        studentInformationEntry.setUserClassID(bundle.getString("UserClassID"));
        studentInformationEntry.setUserID(bundle.getString("UserID"));
        studentInformationEntry.setRFID(bundle.getString("RFID"));
        studentInformationEntry.setStudentNo(bundle.getString("StudentNo"));
        studentInformationEntry.setSectionID(bundle.getString("SectionID"));
        studentInformationEntry.setClassID(bundle.getString("ClassID"));
        studentInformationEntry.setBrunchID(bundle.getString("BrunchID"));
        studentInformationEntry.setShiftID(bundle.getString("ShiftID"));
        studentInformationEntry.setRemarks(bundle.getString("Remarks"));
        studentInformationEntry.setInstituteID(bundle.getString("InstituteID"));
        studentInformationEntry.setUserTypeID(bundle.getString("UserTypeID"));
        studentInformationEntry.setGenderID(bundle.getString("GenderID"));
        studentInformationEntry.setPhoneNo(bundle.getString("PhoneNo"));
        studentInformationEntry.setEmailID(bundle.getString("EmailID"));
        studentInformationEntry.setFingerUrl(bundle.getString("FingerUrl"));
        studentInformationEntry.setSignatureUrl(bundle.getString("SignatureUrl"));

        Glide.with(this)
                .asBitmap()
                .load(getString(R.string.baseUrlRaw)+bundle.getString("ImageUrl").replace("\\","/"))
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        mCropImageView.setImageBitmap(resource);

                    }
                });

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

        updateStudentPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNetworkAvailable())
                {
                    if(checkBox.isChecked())
                    {
                        originalBitmap = mCropImageView.getCroppedImage(500, 500);
                        if (originalBitmap != null)
                        {
                            mCropImageView.setImageBitmap(originalBitmap);
                            fileFromBitmap = new StudentiCardDetails.FileFromBitmap(originalBitmap, StudentiCardDetails.this);
                            fileFromBitmap.execute();
                        }
                    }
                    else
                    {
                        fileFromBitmap = new StudentiCardDetails.FileFromBitmap(originalBitmap, StudentiCardDetails.this);
                        fileFromBitmap.execute();
                    }
                }
                else
                {
                    Toast.makeText(StudentiCardDetails.this,"Please check your INTERNET connection!!!",Toast.LENGTH_LONG).show();
                }
            }
        });
        dialog.dismiss();

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
            bitmap = getResizedBitmap(bitmap, 500);
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
                dialog.dismiss();
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
                    .progressDialog(dialog)
                    .setMultipartParameter("name", "source")
                    .setMultipartFile("file", "image/jpeg", file)
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
                            //do stuff with result
                            try {
                                JSONObject jsonObject = new JSONObject(result);
                                Toast.makeText(StudentiCardDetails.this,jsonObject.getString("path"), Toast.LENGTH_LONG).show();
                                studentInformationEntry.setImageUrl(jsonObject.getString("path"));
                                Gson gson = new Gson();
                                String json = gson.toJson(studentInformationEntry);
                                postUsingVolley(json);
                                Log.d( "ImageUrl", jsonObject.getString("path"));
                            } catch (JSONException e1) {
                                dialog.dismiss();
                                e1.printStackTrace();
                            }
                        }
                    });

        }
    }


    public void postUsingVolley(String json)
    {
        try {
            jsonObjectStudentData = new JSONObject(json);
        } catch (JSONException e) {
            dialog.dismiss();
            e.printStackTrace();
        }
        RequestQueue queuePost = Volley.newRequestQueue(this);
        CustomRequest customRequest = new CustomRequest (Request.Method.POST, studentDataPostUrl, jsonObjectStudentData,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        dialog.dismiss();
                        try {
                            Toast.makeText(StudentiCardDetails.this,"Data Successfully Updated with Response: "+response.getJSONObject(0).get("ReturnValue"),Toast.LENGTH_LONG).show();
                            finish();
                        }
                        catch (Exception e)
                        {

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(StudentiCardDetails.this,"Not Response: "+error.toString(),Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
        queuePost.add(customRequest);
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
                    originalBitmap = bitmap;
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
                    originalBitmap = bitmap;
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                originalBitmap = bitmap;
                try {
                    ImagePath = MediaStore.Images.Media.insertImage(this.getContentResolver(), bitmap, "demo_image", "demo_image");
                    URI = Uri.parse(ImagePath);
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
        NavUtils.navigateUpFromSameTask(this);
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

}
