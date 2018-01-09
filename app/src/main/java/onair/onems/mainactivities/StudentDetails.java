package onair.onems.mainactivities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.ArrayList;

import onair.onems.R;


/**
 * Created by User on 12/20/2017.
 */

public class StudentDetails extends AppCompatActivity {

    private TextView t_name, t_class, t_section, t_birthDay, t_email, t_address,
            t_parent, t_parentsPhone,t_roll,t_department,t_sex,t_religion,t_medium,t_board,t_session;

    private String instituteID = "";
    private String className = "";
    private String sectionName = "";
    private String classId = "";
    private String sectionId = "";
    private int index;
    private String classID[] = {};
    private String sectionID[] = {};
    private String myClassArray[] = {};
    private String mySectionArray[] = {};
    private ArrayList<String> roll_code_array;
    private String encImage;
    private Bitmap photo;
    private String name,roll,blood,email,address,birthday,parent,parentnumber,image,sex,religion,
        classs,section,roll_temp,phone,group,session;
    private int ID =0;
    private Bundle bundle;
    String ImagePath;
    Uri URI,file;
    File mediaStorageDir;

    private CropImageView mCropImageView;
    private CheckBox checkBox;
    private Uri mCropImageUri;

    Bitmap studentImage;

    ProgressDialog progress;

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

        mCropImageView = (CropImageView)findViewById(R.id.cropImageView);
        mCropImageView.setAspectRatio(1,1);
        mCropImageView.setAutoZoomEnabled(true);
        mCropImageView.setFixedAspectRatio(true);

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


        progress = new ProgressDialog(this);
        progress.setTitle("Loading...");
        progress.setMessage("Please Wait...");
        progress.setCancelable(false);

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

        Glide.with(this)
                .asBitmap()
                .load(getString(R.string.baseUrlRaw)+bundle.getString("ImageUrl").replace("\\","/"))
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        mCropImageView.setImageBitmap(resource);
                    }
                });

//        checkBox = (CheckBox)findViewById(R.id.checkbox);
//        s_class=(Spinner) findViewById(R.id.s_class);
//        s_section=(Spinner) findViewById(R.id.s_section);
//        s_sex=(Spinner)findViewById(R.id.s_sex);
//        s_religion=(Spinner)findViewById(R.id.s_religion);
//        cameraButton=(Button)findViewById(R.id.camera);
//        searchButton=(Button) findViewById(R.id.browse);
        //imageView=(ImageView)rootView.findViewById(R.id.image);

//        update=(Button)findViewById(R.id.update);
//        dateButton=(Button) findViewById(R.id.btn_date);

//        ArrayAdapter<String> sex_spinner_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sexArray);
//        sex_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        s_sex.setAdapter(sex_spinner_adapter);

//        ArrayAdapter<String> religion_spinner_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, religionArray);
//        religion_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        s_religion.setAdapter(religion_spinner_adapter);
//        dateButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // calender class's instance and get current date , month and year from calender
//                final Calendar c = Calendar.getInstance();
//                int mYear = c.get(Calendar.YEAR); // current year
//                int mMonth = c.get(Calendar.MONTH); // current month
//                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
//                // date picker dialog
//                datePickerDialog = new DatePickerDialog(DetailsActivity.this,
//                        new DatePickerDialog.OnDateSetListener() {
//
//                            @Override
//                            public void onDateSet(DatePicker view, int year,
//                                                  int monthOfYear, int dayOfMonth) {
//                                // set day of month , month and year value in the edit text
//                                t_birthDay.setText(dayOfMonth + "/"
//                                        + (monthOfYear + 1) + "/" + year);
//
//                            }
//                        }, mYear, mMonth, mDay);
//                datePickerDialog.show();
//            }
//        });

//        s_sex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if(position>0)
//                {
//                    sex = s_sex.getSelectedItem().toString();
//                    t_sex.setText(sex);
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

//        s_religion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if(position>0)
//                {
//                    religion = s_religion.getSelectedItem().toString();
//                    t_religion.setText(religion);
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

//        SharedPreferences sharedPre = PreferenceManager.getDefaultSharedPreferences(this);
//        instituteID=sharedPre.getString("InstituteID","");
//        authenticationID=sharedPre.getString("AuthenticationID","");

//        try {
//            StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
//            StrictMode.setThreadPolicy(policy);
//            Class.forName("com.mysql.jdbc.Driver");
//            Connection con= DriverManager.getConnection(url,user,pass);
//            Statement st=con.createStatement();
//            ResultSet rs = st.executeQuery("select name_numeric,class_id from class where InstituteID= "+instituteID);
//            ArrayList<String> name_array = new ArrayList<String>();
//            ArrayList<String> index_array = new ArrayList<String>();
//            name_array.add("SELECT CLASS");
//            index_array.add("SELECT CLASS");
//            while(rs.next())
//            {
//                name_array.add(rs.getString(1));
//                index_array.add(rs.getString(2));
//            }
//            classID = new String[index_array.size()];
//            myClassArray = new String[name_array.size()];
//            myClassArray = name_array.toArray(myClassArray);
//            classID = index_array.toArray(classID);
//        } catch (Exception e)
//        {
//
//        }
//        ArrayAdapter<String> class_spinner_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, myClassArray);
//        class_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        s_class.setAdapter(class_spinner_adapter);

//        s_class.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if(position>0)
//                {
//                    className = s_class.getSelectedItem().toString();
//                    t_class.setText(className);
//                    for(int i=0;i<myClassArray.length;i++)
//                    {
//                        if(myClassArray[i].equals(className))
//                        {
//                            index=i;
//                            break;
//                        }
//                    }
//                    classId = classID[index];
//                    try {
//                        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
//                        StrictMode.setThreadPolicy(policy);
//                        Class.forName("com.mysql.jdbc.Driver");
//                        Connection con= DriverManager.getConnection(url,user,pass);
//                        Statement st=con.createStatement();
//                        ResultSet rs = st.executeQuery("select section_id,name from section where class_id= "+classId);
//                        ArrayList<String> name_array = new ArrayList<String>();
//                        ArrayList<String> index_array = new ArrayList<String>();
//                        name_array.add("SELECT SECTION");
//                        index_array.add("SELECT SECTION");
//                        while(rs.next())
//                        {
//                            index_array.add(rs.getString(1));
//                            name_array.add(rs.getString(2));
//                        }
//                        mySectionArray = new String[name_array.size()];
//                        sectionID = new String[index_array.size()];
//                        mySectionArray = name_array.toArray(mySectionArray);
//                        sectionID = index_array.toArray(sectionID);
//
//                    } catch (Exception e)
//                    {
//
//                    }
//                    ArrayAdapter<String> section_spinner_adapter = new ArrayAdapter<String>(DetailsActivity.this, android.R.layout.simple_spinner_item, mySectionArray);
//                    section_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                    s_section.setAdapter(section_spinner_adapter);
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

//        s_section.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if(position>0)
//                {
//                    sectionName = s_section.getSelectedItem().toString();
//                    t_section.setText(sectionName);
//                    for(int i=0;i<mySectionArray.length;i++)
//                    {
//                        if(mySectionArray[i].equals(sectionName))
//                        {
//                            index=i;
//                            break;
//                        }
//                    }
//                    sectionId = sectionID[index];
//
//                    try {
//                        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
//                        StrictMode.setThreadPolicy(policy);
//                        Class.forName("com.mysql.jdbc.Driver");
//                        Connection con= DriverManager.getConnection(url,user,pass);
//                        Statement st=con.createStatement();
//                        ResultSet rs = st.executeQuery("select student_code from student where SectionID= "+sectionId);
//                        roll_code_array = new ArrayList<String>();
//                        while(rs.next())
//                        {
//                            roll_code_array.add(rs.getString(1));
//                        }
//                    } catch (Exception e)
//                    {
//
//                    }
//                }
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

//        update.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                phone = t_phone.getText().toString();
//                sex=t_sex.getText().toString();
//                religion=t_religion.getText().toString();
//                roll = t_roll.getText().toString();
//                blood=t_blood.getText().toString();
//                email=t_email.getText().toString();
//                address=t_address.getText().toString();
//                birthday=t_birthDay.getText().toString();
//                parent=t_parent.getText().toString();
//                parentnumber=t_parentsPhone.getText().toString();
//                name=t_name.getText().toString();
//                classs = t_class.getText().toString();
//                section = t_section.getText().toString();
//                group=t_group.getText().toString();
//                session=t_session.getText().toString();
//                if(isNetworkAvailable())
//                {
//                    if(checkBox.isChecked())
//                    {
//                        Bitmap cropped =  mCropImageView.getCroppedImage(mCropImageView.getWidth(), mCropImageView.getHeight());
//                        if (cropped != null)
//                            mCropImageView.setImageBitmap(cropped);
//                        encImage=encodeImage(cropped);
//                        try
//                        {
//                            ImagePath = MediaStore.Images.Media.insertImage(DetailsActivity.this.getContentResolver(), cropped, "demo_image", "demo_image");
//                        }
//                        catch (Exception e)
//                        {
//                        }
//                    }
//
//                    try {
//                        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
//                        StrictMode.setThreadPolicy(policy);
//                        Class.forName("com.mysql.jdbc.Driver");
//                        Connection con= DriverManager.getConnection(url,user,pass);
//                        Statement st=con.createStatement();
//                        ResultSet rs = st.executeQuery("select class_id from class where name_numeric = "+"'"+classs+"'"+" and InstituteID = "+instituteID);
//                        //ResultSet rs = st.executeQuery("select name,birthday,email,address,parent_name,phone,image,sex,religion,blood_group,student_id from student where student_code= "+rollNo+" and SectionID = "+sectionId);
//                        while(rs.next())
//                        {
//                            classId = rs.getString(1);
//                        }
//                        Statement st1=con.createStatement();
//                        ResultSet rs1 = st1.executeQuery("select section_id from section where class_id = "+classId+" and name = "+"'"+section+"'");
//                        while(rs1.next())
//                        {
//                            sectionId = rs1.getString(1);
//                        }
//                        String query = "UPDATE student SET student_code=?,name=?," +
//                                "birthday=?,sex=?,religion=?,blood_group=?,address=?," +
//                                "phone=?,parent_phone=?,email=?,parent_name=?,ClassID=?,SectionID=?,image=?,student_group=?,session=? WHERE student_id=?";
//                        PreparedStatement preparedStmt = con.prepareStatement(query);
//
//                        int c_id = Integer.parseInt(classId);
//                        int s_id = Integer.parseInt(sectionId);
//                        preparedStmt.setString(1, roll);
//                        preparedStmt.setString (2,name);
//                        preparedStmt.setString(3, birthday);
//                        preparedStmt.setString(4, sex);
//                        preparedStmt.setString(5, religion);
//                        preparedStmt.setString(6,blood);
//                        preparedStmt.setString(7,address);
//                        preparedStmt.setString(8, phone);
//                        preparedStmt.setString(9, parentnumber);
//                        preparedStmt.setString(10,email);
//                        preparedStmt.setString(11,parent );
//                        preparedStmt.setInt(12,c_id);
//                        preparedStmt.setInt(13,s_id);
//                        preparedStmt.setString(14,encImage);
//                        preparedStmt.setString(15,group);
//                        preparedStmt.setString(16,session);
//                        preparedStmt.setInt(17,ID);
//                        preparedStmt.execute();
//                        Toast.makeText(DetailsActivity.this, "Data successfully UPDATED!!! " , Toast.LENGTH_SHORT).show();
//                        getFragmentManager().popBackStack();
//
//                    } catch (Exception e)
//                    {
//                        Toast.makeText(DetailsActivity.this,"Exception:"+e+"WARNING!!!", Toast.LENGTH_SHORT).show();
//                    }
//                }
//                else
//                {
//                    Toast.makeText(DetailsActivity.this,"Please check your INTERNET connection!!!",Toast.LENGTH_LONG).show();
//                }
//
//            }
//        });
//
//        cameraButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivityForResult(getPickImageChooserIntent(), 200);
//
////                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
////                file = Uri.fromFile(getOutputMediaFile());
////                intent.putExtra(MediaStore.EXTRA_OUTPUT, file);
////                startActivityForResult(intent, 100);
//            }
//        });
//
//        searchButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent intent = new Intent();
//                // Show only images, no videos or anything else
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                // Always show the chooser (if there are multiple options available)
//                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
//            }
//        });

//        t_phone = (TextView) findViewById(R.id.editPhone);
//        t_sex = (TextView)findViewById(R.id.editSex);
//        t_religion = (TextView)findViewById(R.id.editReligion);
//        t_blood = (TextView)findViewById(R.id.editBlood);
//        t_roll = (TextView)findViewById(R.id.editRoll);
//        t_name = (TextView)findViewById(R.id.editName);
//        t_class = (TextView)findViewById(R.id.editClass);
//        t_section = (TextView)findViewById(R.id.editSection);
//        t_birthDay = (TextView)findViewById(R.id.editBirthday);
//        t_email = (TextView)findViewById(R.id.editEmail);
//        t_address = (TextView)findViewById(R.id.editAddress);
//        t_parent = (TextView)findViewById(R.id.editparent);
//        t_parentsPhone = (TextView)findViewById(R.id.editParentNumber);
//        t_group=(TextView) findViewById(R.id.editGroup);
//        t_session=(TextView) findViewById(R.id.editSession);

//        if(prepareDetailsData == null)
//        {
//            progress.show();
//            prepareDetailsData = new PrepareDetailsData();
//            prepareDetailsData.execute((Void) null);
//        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */


//    private File getOutputMediaFile(){
//        mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_PICTURES), "CameraDemo");
//        if (!mediaStorageDir.exists()){
//            if (!mediaStorageDir.mkdirs()){
//                return null;
//            }
//        }
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        return new File(mediaStorageDir.getPath() + File.separator +
//                "IMG_"+ timeStamp + ".jpg");
//    }

//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//        if (resultCode == Activity.RESULT_OK) {
//            Uri imageUri = getPickImageResultUri(data);
//
//            // For API >= 23 we need to check specifically that we have permissions to read external storage,
//            // but we don't know if we need to for the URI so the simplest is to try open the stream and see if we get error.
//            boolean requirePermissions = false;
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
//                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
//                    isUriRequiresPermissions(imageUri)) {
//
//                // request permissions and handle the result in onRequestPermissionsResult()
//                requirePermissions = true;
//                mCropImageUri = imageUri;
//                Bitmap bitmap = null;
//                try {
//                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                encodeImage(bitmap);
//                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
//            }
//
//            if (!requirePermissions) {
//                mCropImageView.setImageUriAsync(imageUri);
//                Bitmap bitmap = null;
//                try {
//                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                encodeImage(bitmap);
//            }
//        }
//
////        if (requestCode == 100&& resultCode == RESULT_OK)
////        {
////            try {
////                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),file);
////                Bitmap bitmap2 = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth()-100 ,bitmap.getHeight()-100, true);
////                mCropImageView.setImageBitmap(bitmap2);
////                encodeImage(bitmap2);
////
////            } catch (IOException e) {
////                e.printStackTrace();
////            }
////        }
//
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
//            Uri uri = data.getData();
//            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
//                encodeImage(bitmap);
//                try {
//                    ImagePath = MediaStore.Images.Media.insertImage(this.getContentResolver(), bitmap, "demo_image", "demo_image");
//                    URI = Uri.parse(ImagePath);
//                    mCropImageView.setImageUriAsync(URI);
//                }
//                catch (Exception e)
//                {
//                    mCropImageView.setImageBitmap(bitmap);
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            mCropImageView.setImageUriAsync(mCropImageUri);
//        } else {
//            Toast.makeText(this, "Required permissions are not granted", Toast.LENGTH_LONG).show();
//        }
//    }


    /**
     * Create a chooser intent to select the source to get image from.<br/>
     * The source can be camera's (ACTION_IMAGE_CAPTURE) or gallery's (ACTION_GET_CONTENT).<br/>
     * All possible sources are added to the intent chooser.
     */
//    public Intent getPickImageChooserIntent() {
//
//        // Determine Uri of camera image to save.
//        Uri outputFileUri = getCaptureImageOutputUri();
//
//        List<Intent> allIntents = new ArrayList<>();
//        PackageManager packageManager = getPackageManager();
//
//        // collect all camera intents
//        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
//        for (ResolveInfo res : listCam) {
//            Intent intent = new Intent(captureIntent);
//            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
//            intent.setPackage(res.activityInfo.packageName);
//            if (outputFileUri != null) {
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
//            }
//            allIntents.add(intent);
//        }
//
//        // collect all gallery intents
//        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
//        galleryIntent.setType("image/*");
//        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
//        for (ResolveInfo res : listGallery) {
//            Intent intent = new Intent(galleryIntent);
//            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
//            intent.setPackage(res.activityInfo.packageName);
//            allIntents.add(intent);
//        }
//
//        // the main intent is the last in the list (fucking android) so pickup the useless one
//        Intent mainIntent = allIntents.get(allIntents.size() - 1);
//        for (Intent intent : allIntents) {
//            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
//                mainIntent = intent;
//                break;
//            }
//        }
//        allIntents.remove(mainIntent);
//
//        // Create a chooser from the main intent
//        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");
//
//        // Add all other intents
//        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));
//
//        return chooserIntent;
//    }

    /**
     * Get URI to image received from capture by camera.
     */
//    private Uri getCaptureImageOutputUri() {
//        Uri outputFileUri = null;
//        File getImage = getExternalCacheDir();
//        if (getImage != null) {
//            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "pickImageResult.jpeg"));
//        }
//        return outputFileUri;
//    }

    /**
     * Get the URI of the selected image from {@link #getPickImageChooserIntent()}.<br/>
     * Will return the correct URI for camera and gallery image.
     *
     * @param data the returned data of the activity result
     */
//    public Uri getPickImageResultUri(Intent data) {
//        boolean isCamera = true;
//        if (data != null && data.getData() != null) {
//            String action = data.getAction();
//            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
//        }
//        return isCamera ? getCaptureImageOutputUri() : data.getData();
//    }

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
//
//    private String encodeImage(Bitmap bm)
//    {
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bm.compress(Bitmap.CompressFormat.JPEG,100,baos);
//        byte[] b = baos.toByteArray();
//        encImage = Base64.encodeToString(b, Base64.DEFAULT);
//
//        return encImage;
//    }
    public static Bitmap decodeImage(String input)
    {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
//    private boolean isNetworkAvailable() {
//        ConnectivityManager connectivityManager
//                = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
//        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
//    }

//    public class PrepareDetailsData extends AsyncTask<Void, Void, Boolean> {
//
//        @Override
//        protected Boolean doInBackground(Void... params) {
//            // TODO: attempt authentication against a network service.
//
//            try {
//                // Simulate network access.
//
//                try {
//                    String rollNo = bundle.getString("Roll");
//                    String sectionid = bundle.getString("SectionID");
//                    StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
//                    StrictMode.setThreadPolicy(policy);
//                    Class.forName("com.mysql.jdbc.Driver");
//                    Connection con= DriverManager.getConnection(url,user,pass);
//                    Statement st=con.createStatement();
//                    ResultSet rs = st.executeQuery("select name,birthday,email,address,parent_name,phone,parent_phone,image,sex,religion,blood_group,student_id,student_group,session from student where student_code= "+"'"+rollNo+"'"+" and SectionID = "+sectionid);
//                    while(rs.next())
//                    {
//                        name = rs.getString(1);
//                        birthday = rs.getString(2);
//                        email = rs.getString(3);
//                        address = rs.getString(4);
//                        parent = rs.getString(5);
//                        phone = rs.getString(6);
//                        parentnumber = rs.getString(7);
//                        image = rs.getString(8);
//                        sex = rs.getString(9);
//                        religion = rs.getString(10);
//                        blood = rs.getString(11);
//                        ID = Integer.parseInt(rs.getString(12));
//                        group = rs.getString(13);
//                        session = rs.getString(14);
//                        return true;
//
//                    }
//
//                } catch (Exception e)
//                {
//                    Toast.makeText(StudentDetails.this, "WARNING!!!"+e , Toast.LENGTH_SHORT).show();
//                }
//
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                return false;
//            }
//
//            return false;
//        }
//
//        @Override
//        protected void onPostExecute(final Boolean success) {
//
//            try {
//                photo = decodeImage(image);
//                mCropImageView.setImageBitmap(photo);
//                encImage=image;
//            } catch (Exception e){
//                Toast.makeText(StudentDetails.this,"No image found in database!!!",Toast.LENGTH_LONG).show();
//            }
//            t_blood.setText(blood);
//            t_roll.setText(bundle.getString("Roll"));
//            t_name.setText(name);
//            t_sex.setText(sex);
//            t_religion.setText(religion);
//            t_class.setText(bundle.getString("Class"));
//            t_section.setText(bundle.getString("Section"));
//            t_birthDay.setText(birthday);
//            t_email.setText(email);
//            t_address.setText(address);
//            t_parent.setText(parent);
//            t_parentsPhone.setText(parentnumber);
//            t_phone.setText(phone);
//            t_group.setText(group);
//            t_session.setText(session);
//            progress.dismiss();
//            prepareDetailsData = null;
//        }
//
//        @Override
//        protected void onCancelled() {
//            progress.dismiss();
//        }
//    }
}
