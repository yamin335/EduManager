package onair.onems.icard;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.theartofdev.edmodo.cropper.CropImageView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
import onair.onems.Services.GlideApp;
import onair.onems.Services.RetrofitNetworkService;
import onair.onems.Services.StaticHelperClass;
import onair.onems.mainactivities.CommonToolbarParentActivity;
import onair.onems.models.StudentInformationEntry;
import onair.onems.utils.ImageUtils;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class StudentiCardDetails extends CommonToolbarParentActivity {

    private TextView t_name, t_class, t_section, t_birthDay, t_email, t_address,
            t_parent, t_parentsPhone,t_roll,t_department,t_sex,t_religion,t_medium,t_board,
            t_session, t_shift,t_branch, t_stuEmail, t_stuPhone, t_remarks, t_studentNo, t_rfid;

    private CropImageView mCropImageView;
    private CheckBox checkBox;
    private Uri mCropImageUri;
    private StudentInformationEntry studentInformationEntry;

    private JsonObject jsonObjectStudentData;

    private File file;

    private Bitmap originalBitmap = null;
    private Bitmap tempBitmap = null;

    private FileFromBitmap fileFromBitmap = null;

    private String selectedClass, selectedShift,
            selectedSection, selectedMedium, selectedDepartment, UserID, selectedSession;
    private int PICK_IMAGE_REQUEST = 1;

    private long InstituteID;
    private static int brightnessValue;
    private BrightnessProcessTask mBrightnessProcessTask = null;
    private RotateProcessTask mRotateProcessTask = null;
    private boolean imageChanged = false;
    private ProgressBar progressBar;
    private Button rotateLeft, rotateRight;
    private SeekBar brightImageSeekBar;
    private CompositeDisposable finalDisposer = new CompositeDisposable();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!finalDisposer.isDisposed())
            finalDisposer.dispose();
    }

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

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View childActivityLayout = Objects.requireNonNull(inflater).inflate(R.layout.icard_student_details, null);
        LinearLayout parentActivityLayout = findViewById(R.id.contentMain);
        parentActivityLayout.addView(childActivityLayout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        InstituteID = prefs.getLong("InstituteID",0);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        selectedClass = Objects.requireNonNull(bundle).getString("ClassID");
        selectedSession = bundle.getString("SessionID");
        selectedShift = bundle.getString("ShiftID");
        selectedSection = bundle.getString("SectionID");
        selectedMedium = bundle.getString("MediumID");
        selectedDepartment = bundle.getString("DepartmentID");
        UserID = bundle.getString("UserID");

        rotateLeft = findViewById(R.id.rotateLeft);
        rotateRight = findViewById(R.id.rotateRight);
        Button updateStudentPhoto = findViewById(R.id.updatephoto);
        Button cameraButton= findViewById(R.id.camera);
        Button searchButton= findViewById(R.id.browse);

        brightImageSeekBar = findViewById(R.id.brightness);
        brightImageSeekBar.setProgress(100);
        progressBar = findViewById(R.id.spin_kit);
        mCropImageView = findViewById(R.id.CropImageView);
        mCropImageView.setAspectRatio(1,1);
        mCropImageView.setAutoZoomEnabled(true);
        mCropImageView.setFixedAspectRatio(true);
        checkBox = findViewById(R.id.checkbox);

        t_roll = findViewById(R.id.roll);
        t_name = findViewById(R.id.teacherName);
        t_class = findViewById(R.id.classs);
        t_section = findViewById(R.id.section);
        t_birthDay = findViewById(R.id.birthday);
        t_email = findViewById(R.id.guardianemail);
        t_address = findViewById(R.id.address);
        t_parent = findViewById(R.id.guardian);
        t_parentsPhone = findViewById(R.id.guardianPhone);
        t_department = findViewById(R.id.department);
        t_sex = findViewById(R.id.sex);
        t_religion = findViewById(R.id.religion);
        t_medium = findViewById(R.id.medium);
        t_session = findViewById(R.id.session);
        t_board = findViewById(R.id.board);
        t_shift = findViewById(R.id.shift);
        t_branch = findViewById(R.id.branch);
        t_stuEmail = findViewById(R.id.stuEmail);
        t_stuPhone = findViewById(R.id.stuPhone);
        t_remarks = findViewById(R.id.remarks);
        t_studentNo = findViewById(R.id.studentNo);
        t_rfid = findViewById(R.id.rfid);

        rotateLeft.setEnabled(false);
        rotateRight.setEnabled(false);
        brightImageSeekBar.setEnabled(false);
        checkBox.setEnabled(false);

        StudentDataGetRequest();

        cameraButton.setOnClickListener(v -> startActivityForResult(getPickImageChooserIntent(), 200));

        searchButton.setOnClickListener(v -> {
            Intent intent1 = new Intent();
            // Show only images, no videos or anything else
            intent1.setType("image/*");
            intent1.setAction(Intent.ACTION_GET_CONTENT);
            // Always show the chooser (if there are multiple options available)
            startActivityForResult(Intent.createChooser(intent1, "Select Picture"), PICK_IMAGE_REQUEST);
        });

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (originalBitmap != null) {
                imageChanged = true;
            }
        });

        rotateLeft.setOnClickListener(v -> {
            imageChanged = true;
            mRotateProcessTask = new RotateProcessTask(originalBitmap, -90);
            mRotateProcessTask.execute((Void) null);
        });

        rotateRight.setOnClickListener(v -> {
            imageChanged = true;
            mRotateProcessTask = new RotateProcessTask(originalBitmap, 90);
            mRotateProcessTask.execute((Void) null);
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
                imageChanged = true;
                mBrightnessProcessTask = new BrightnessProcessTask(originalBitmap, brightnessValue);
                mBrightnessProcessTask.execute((Void) null);
            }
        });

        updateStudentPhoto.setOnClickListener(view -> {
            if(StaticHelperClass.isNetworkAvailable(StudentiCardDetails.this)) {
                if(imageChanged) {
                    if(checkBox.isChecked()) {
                        tempBitmap = mCropImageView.getCroppedImage(500, 500);
                        mCropImageView.setImageBitmap(tempBitmap);
                        fileFromBitmap = new FileFromBitmap(tempBitmap, StudentiCardDetails.this);
                        fileFromBitmap.execute();
                    } else {
                        fileFromBitmap = new FileFromBitmap(tempBitmap, StudentiCardDetails.this);
                        fileFromBitmap.execute();
                    }
                } else {
                    Toast.makeText(StudentiCardDetails.this,"Take or choose a photo to update!!!",Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(StudentiCardDetails.this,"Please check your INTERNET connection!!!",Toast.LENGTH_LONG).show();
            }
        });
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
        return MultipartBody.Part.createFormData("iCardPhoto", file.getName(), requestFile);
    }

    class FileFromBitmap extends AsyncTask<Void, Integer, String> {

        Context context;
        Bitmap bitmap;

        FileFromBitmap(Bitmap bitmap, Context context) {
            this.bitmap = bitmap;
            this.context= context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // before executing doInBackground
            // update your UI
            // exp; make progressbar visible
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                file = ImageUtils.getFileFromBitmap(bitmap, context);
            } catch (IOException e) {
                dialog.dismiss();
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            dialog.dismiss();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // back to main thread after finishing doInBackground
            // update your UI or take action after
            // exp; make progressbar gone
            dialog.dismiss();

            if (StaticHelperClass.isNetworkAvailable(StudentiCardDetails.this)) {
                dialog.show();
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(getString(R.string.baseUrl))
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .build();

                Observable<String> photoObservable = retrofit
                        .create(RetrofitNetworkService.class)
                        .uploadSingleImage(prepareFilePart(file))
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .unsubscribeOn(Schedulers.io());

                finalDisposer.add(photoObservable
                        .observeOn(Schedulers.io())
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .unsubscribeOn(Schedulers.io())
                        .subscribeWith(new DisposableObserver<String>() {
                            @Override
                            public void onNext(String response) {
                                dialog.dismiss();
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    studentInformationEntry.setImageUrl(jsonObject.getString("path"));
                                    studentInformationEntry.setIsImageCaptured(true);
                                    Gson gson = new Gson();
                                    String json = gson.toJson(studentInformationEntry);
                                    postUsingVolley(json);
                                    Log.d( "ImageUrl", jsonObject.getString("path"));
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                dialog.dismiss();
                                Log.e("RXANDROID", "onError: " + e.getMessage());
                            }

                            @Override
                            public void onComplete() {
                                Log.e("COMPLETE", "Complete: ");
                            }
                        }));
            } else {
                Toast.makeText(StudentiCardDetails.this,"Please check your internet connection and select again!!! ",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    public void postUsingVolley(String json) {
        if(StaticHelperClass.isNetworkAvailable(StudentiCardDetails.this)) {
            try {
                JsonParser parser = new JsonParser();
                jsonObjectStudentData = parser.parse(json).getAsJsonObject();
            } catch (JsonIOException e) {
                e.printStackTrace();
            }
            dialog.show();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(getString(R.string.baseUrl))
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();

            Observable<String> observable = retrofit
                    .create(RetrofitNetworkService.class)
                    .setStudentBasicInfo(jsonObjectStudentData)
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
                            dialog.dismiss();
                            Toast.makeText(StudentiCardDetails.this,"Successfully Updated",Toast.LENGTH_LONG).show();
                            StudentiCardDetails.super.onBackPressed();
                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                            Toast.makeText(StudentiCardDetails.this,"Error Occurred",Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    }));
        } else {
            Toast.makeText(StudentiCardDetails.this,"Please check your internet connection!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    void parseStudentJsonData(String jsonString) {
        try {
            JSONArray studentJsonArray = new JSONArray(jsonString);
            JSONObject studentJsonObject = studentJsonArray.getJSONObject(0);
            if(studentJsonObject.getString("ImageUrl").equals("null")){
                Toast.makeText(StudentiCardDetails.this,"No image found!!!",Toast.LENGTH_LONG).show();
            }
            GlideApp.with(this)
                    .asBitmap()
                    .load(getString(R.string.baseUrl)+"/"+studentJsonObject.getString("ImageUrl").replace("\\","/"))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, Transition<? super Bitmap> transition) {
                            mCropImageView.setImageBitmap(resource);
                            originalBitmap = resource;
                            tempBitmap = resource;
                            progressBar.setVisibility(View.INVISIBLE);
                            if(resource != null) {
                                checkBox.setEnabled(true);
                                rotateLeft.setEnabled(true);
                                rotateRight.setEnabled(true);
                                brightImageSeekBar.setEnabled(true);
                            }
                        }
                        @Override
                        public void onLoadFailed(Drawable errorDrawable) {
                            super.onLoadFailed(errorDrawable);
                            progressBar.setVisibility(View.INVISIBLE);
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
            studentInformationEntry.setBloodGroupID(studentJsonObject.getString("BloodGroupID"));
            studentInformationEntry.setReligionID(studentJsonObject.getString("ReligionID"));
            studentInformationEntry.setSessionID(studentJsonObject.getString("SessionID"));
            studentInformationEntry.setBoardID(studentJsonObject.getString("BoardID"));
            studentInformationEntry.setPhoneNo(studentJsonObject.getString("PhoneNo"));
            studentInformationEntry.setEmailID(studentJsonObject.getString("EmailID"));
            studentInformationEntry.setFingerUrl(studentJsonObject.getString("FingerUrl"));
            studentInformationEntry.setSignatureUrl(studentJsonObject.getString("SignatureUrl"));
            studentInformationEntry.setMediumID(studentJsonObject.getString("MediumID"));
            studentInformationEntry.setDepartmentID(studentJsonObject.getString("DepartmentID"));
            studentInformationEntry.setIsImageCaptured(studentJsonObject.getBoolean("IsImageCaptured"));
            studentInformationEntry.setImageUrl(studentJsonObject.getString("ImageUrl"));
            studentInformationEntry.setUserNo(studentJsonObject.getString("UserNo"));
            studentInformationEntry.setUserTitleID(studentJsonObject.getString("UserTitleID"));
            studentInformationEntry.setUserFirstName(studentJsonObject.getString("UserFirstName"));
            studentInformationEntry.setUserMiddleName(studentJsonObject.getString("UserMiddleName"));
            studentInformationEntry.setUserLastName(studentJsonObject.getString("UserLastName"));
            studentInformationEntry.setNickName(studentJsonObject.getString("NickName"));
            studentInformationEntry.setSkypeID(studentJsonObject.getString("SkypeID"));
            studentInformationEntry.setFacebookID(studentJsonObject.getString("FacebookID"));
            studentInformationEntry.setWhatsApp(studentJsonObject.getString("WhatsApp"));
            studentInformationEntry.setViber(studentJsonObject.getString("Viber"));
            studentInformationEntry.setLinkedIN(studentJsonObject.getString("LinkedIN"));
            studentInformationEntry.setParAddress(studentJsonObject.getString("ParAddress"));
            studentInformationEntry.setParThana(studentJsonObject.getString("ParThana"));
            studentInformationEntry.setParPostCode(studentJsonObject.getString("ParPostCode"));
            studentInformationEntry.setParCountryID(studentJsonObject.getString("ParCountryID"));
            studentInformationEntry.setParStateID(studentJsonObject.getString("ParStateID"));
            studentInformationEntry.setParCityID(studentJsonObject.getString("ParCityID"));
            studentInformationEntry.setPreThana(studentJsonObject.getString("PreThana"));
            studentInformationEntry.setPrePostCode(studentJsonObject.getString("PrePostCode"));
            studentInformationEntry.setPreCountryID(studentJsonObject.getString("PreCountryID"));
            studentInformationEntry.setPreStateID(studentJsonObject.getString("PreStateID"));
            studentInformationEntry.setPreCityID(studentJsonObject.getString("PreCityID"));
            studentInformationEntry.setMobileNo(studentJsonObject.getString("MobileNo"));
            studentInformationEntry.setUniqueIdentity(studentJsonObject.getString("UniqueIdentity"));
            studentInformationEntry.setBloodGroupID(studentJsonObject.getString("BloodGroupID"));
            studentInformationEntry.setWeigth(studentJsonObject.getString("Weigth"));
            studentInformationEntry.setHeight(studentJsonObject.getString("Height"));
            studentInformationEntry.setBirthCertificate(studentJsonObject.getString("BirthCertificate"));
            studentInformationEntry.setPassportNO(studentJsonObject.getString("PassportNO"));
            studentInformationEntry.setNID(studentJsonObject.getString("NID"));
            studentInformationEntry.setIsActive(studentJsonObject.getString("IsActive"));
            studentInformationEntry.setStatusID(studentJsonObject.getString("StatusID"));
            studentInformationEntry.setGuardianMobileNo(studentJsonObject.getString("GuardianMobileNo"));
            studentInformationEntry.setGuardianUserFirstName(studentJsonObject.getString("GuardianUserFirstName"));
            studentInformationEntry.setGuardianUserMiddleName(studentJsonObject.getString("GuardianUserMiddleName"));
            studentInformationEntry.setGuardianUserLastName(studentJsonObject.getString("GuardianUserLastName"));
            studentInformationEntry.setGuardianNickName(studentJsonObject.getString("GuardianNickName"));
            studentInformationEntry.setGuardianUniqueIdentity(studentJsonObject.getString("GuardianUniqueIdentity"));
            studentInformationEntry.setGuardianBloodGroupID(studentJsonObject.getString("GuardianBloodGroupID"));
            studentInformationEntry.setGuardianPassportNO(studentJsonObject.getString("GuardianPassportNO"));
            studentInformationEntry.setGuardianNID(studentJsonObject.getString("GuardianNID"));
            studentInformationEntry.setRelationID(studentJsonObject.getString("RelationID"));
            studentInformationEntry.setIsLocalGuardian(studentJsonObject.getString("IsLocalGuardian"));
            studentInformationEntry.setIsActiveFamily(studentJsonObject.getString("IsActiveFamily"));
            studentInformationEntry.setIsActiveStudent(studentJsonObject.getString("IsActiveStudent"));

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
        } catch (JSONException e) {
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
                    ImageUtils.isUriRequiresPermissions(imageUri, this)) {

                // request permissions and handle the result in onRequestPermissionsResult()
                requirePermissions = true;
                mCropImageUri = imageUri;
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    originalBitmap = getResizedBitmap(bitmap, 500);
                    tempBitmap = originalBitmap;
                    imageChanged = true;
                    checkBox.setEnabled(true);
                    rotateLeft.setEnabled(true);
                    rotateRight.setEnabled(true);
                    brightImageSeekBar.setEnabled(true);
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
                    checkBox.setEnabled(true);
                    rotateLeft.setEnabled(true);
                    rotateRight.setEnabled(true);
                    brightImageSeekBar.setEnabled(true);
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
                checkBox.setEnabled(true);
                rotateLeft.setEnabled(true);
                rotateRight.setEnabled(true);
                brightImageSeekBar.setEnabled(true);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
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
            if (Objects.requireNonNull(intent.getComponent()).getClassName().equals("com.android.documentsui.DocumentsActivity")) {
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

//    public boolean isUriRequiresPermissions(Uri uri) {
//        try {
//            ContentResolver resolver = getContentResolver();
//            InputStream stream = resolver.openInputStream(uri);
//            stream.close();
//            return false;
//        } catch (FileNotFoundException e) {
//            if (e.getCause() instanceof ErrnoException) {
//                return true;
//            }
//        } catch (Exception e) {
//        }
//        return false;
//    }

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
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                tempBitmap = doBrightness(image, progressValue);
                Thread.sleep(100);
                return true;
            } catch (InterruptedException e) {
                dialog.dismiss();
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            dialog.dismiss();
            mBrightnessProcessTask = null;
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
            dialog.dismiss();
            mBrightnessProcessTask = null;
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
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                originalBitmap = rotateImage(image, angle);
                tempBitmap = rotateImage(tempBitmap, angle);
                Thread.sleep(100);
                return true;
            } catch (InterruptedException e) {
                dialog.dismiss();
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mRotateProcessTask = null;
            dialog.dismiss();
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
            dialog.dismiss();
        }
    }

    public void StudentDataGetRequest(){
        if(StaticHelperClass.isNetworkAvailable(StudentiCardDetails.this)) {
            dialog.show();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(getString(R.string.baseUrl))
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();

            Observable<String> observable = retrofit
                    .create(RetrofitNetworkService.class)
                    .getStudent(InstituteID, selectedClass, selectedSection,
                            selectedDepartment, selectedMedium,
                            selectedShift, UserID, selectedSession)
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
                            dialog.dismiss();
                            parseStudentJsonData(response);
                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                        }

                        @Override
                        public void onComplete() {

                        }
                    }));
        } else {
            Toast.makeText(StudentiCardDetails.this,"Please check your internet connection!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }
}
