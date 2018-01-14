package onair.onems.mainactivities;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.system.ErrnoException;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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
import onair.onems.models.ClassModel;
import onair.onems.models.SectionModel;
import onair.onems.models.ShiftModel;
import onair.onems.models.StudentInformationEntry;

/**
 * Created by User on 12/18/2017.
 */

public class StudentiCardNewEntry extends AppCompatActivity {

    private Button cameraButton,searchButton,doneButton;
    private int PICK_IMAGE_REQUEST = 1;
    private String encImage;
    private String instituteID = "";
    private String authenticationID = "";
    private String className = "";
    private String sectionName = "";
    private String classId = "";
    private String sectionId = "";
    private int index;
    private String classID[] = {};
    private String sectionID[] = {};
    private String myClassArray[] = {};
    private String mySectionArray[] = {};
    private String sexArray[]={"Male","Female","Gender"};
    private String religionArray[]={"Islam","Hinduism","Christian","Buddhism","Others","Religion"};
    private ArrayList<String> roll_code_array;
    private EditText editname,editroll,editaddress,editparent,editparentnumber;
    private Spinner s_class, s_section,s_sex,s_religion;
    private static final String url = "jdbc:mysql://182.160.100.36:3306/ems";
    private static final String user = "yamin";
    private static final String pass = "yamin";
    private String name,roll,address,parentName,parentNumber;
    private String ImagePath;
    private DatePickerDialog datePickerDialog;
    private Uri URI;
    private File mediaStorageDir;
    private CropImageView mCropImageView;
    private CheckBox checkBox;
    private Uri mCropImageUri;
    ProgressDialog dialog;
    int state;
    private long InstituteID;
    private Spinner spinnerClass, spinnerShift,spinnerSection;
    private String classUrl, shiftUrl, sectionUrl, studentDataPostUrl;
    private ArrayList<ClassModel> allClassArrayList;
    private ArrayList<ShiftModel> allShiftArrayList;
    private ArrayList<SectionModel> allSectionArrayList;

    String[] tempSectionArray = {"Select Section"};
    ClassModel selectedClass = null;
    ShiftModel selectedShift = null;
    SectionModel selectedSection = null;

    int classSpinnerPosition, shiftSpinnerPosition, sectionSpinnerPosition;

    JSONObject jsonObjectStudentData;

    File file;

    Bitmap originalBitmap;

    FileFromBitmap fileFromBitmap = null;


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
        setContentView(R.layout.icard_student_new_entry);

        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading...");
        dialog.setMessage("Please Wait...");
        dialog.setCancelable(false);
        dialog.show();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        InstituteID = prefs.getLong("InstituteID",0);

//        Uri uri = Uri.parse("android.resource://onair.camera/drawable/image1");
//        mCropImageView.setImageUriAsync(uri);

        originalBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.image1);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mCropImageView = (CropImageView)findViewById(R.id.CropImageView);
        mCropImageView.setAspectRatio(1,1);
        mCropImageView.setAutoZoomEnabled(true);
        mCropImageView.setFixedAspectRatio(true);

        checkBox = (CheckBox)findViewById(R.id.checkbox);

        cameraButton=(Button)findViewById(R.id.camera);
        searchButton=(Button) findViewById(R.id.browse);
        doneButton=(Button) findViewById(R.id.done);

        spinnerClass = (Spinner)findViewById(R.id.spinnerClass);
        spinnerShift = (Spinner)findViewById(R.id.spinnerShift);
        spinnerSection = (Spinner)findViewById(R.id.spinnerSection);

        ArrayAdapter<String> section_spinner_adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, tempSectionArray);
        section_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSection.setAdapter(section_spinner_adapter);

        allClassArrayList = new ArrayList<ClassModel>();
        allShiftArrayList = new ArrayList<ShiftModel>();
        allSectionArrayList = new ArrayList<SectionModel>();

        classUrl = getString(R.string.baseUrlLocal)+"getInsClass/"+InstituteID;
        shiftUrl = getString(R.string.baseUrlLocal)+"getInsShift/"+InstituteID;
        studentDataPostUrl = getString(R.string.baseUrlLocal)+"setStudentBasicInfo";

        editname=(EditText) findViewById(R.id.edit_name);
        editroll=(EditText) findViewById(R.id.edit_roll);
        editaddress=(EditText) findViewById(R.id.edit_address);
        editparent=(EditText) findViewById(R.id.edit_parent);
        editparentnumber=(EditText) findViewById(R.id.edit_parentPhone);

        //Preparing claas data from server
        RequestQueue queueClass = Volley.newRequestQueue(this);
        StringRequest stringClassRequest = new StringRequest(Request.Method.GET, classUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        parseClassJsonData(response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.dismiss();
            }
        });
        queueClass.add(stringClassRequest);

        //Preparing Shift data from server
        RequestQueue queueShift = Volley.newRequestQueue(this);
        StringRequest stringShiftRequest = new StringRequest(Request.Method.GET, shiftUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        parseShiftJsonData(response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.dismiss();
            }
        });
        queueShift.add(stringShiftRequest);

        spinnerClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != classSpinnerPosition)
                {
                    selectedSection = null;
                    selectedClass = allClassArrayList.get(position);
                    long classId = selectedClass.getClassID();
                    sectionUrl = getString(R.string.baseUrlLocal)+"getInsSection/"+InstituteID+"/"+classId;
                    dialog.show();
                    //Preparing section data from server
                    RequestQueue queueSection = Volley.newRequestQueue(StudentiCardNewEntry.this);
                    StringRequest stringSectionRequest = new StringRequest(Request.Method.GET, sectionUrl,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    parseSectionJsonData(response);

                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            dialog.dismiss();
                        }
                    });
                    queueSection.add(stringSectionRequest);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerShift.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != shiftSpinnerPosition)
                {
                    selectedShift = allShiftArrayList.get(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerSection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != sectionSpinnerPosition)
                {
                    selectedSection = allSectionArrayList.get(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNetworkAvailable())
                {
                    if(editname.getText().toString().equals(""))
                    {
                        Toast.makeText(StudentiCardNewEntry.this,"Please enter student name!!!",Toast.LENGTH_LONG).show();
                    }
                    else if(editroll.getText().toString().equals(""))
                    {
                        Toast.makeText(StudentiCardNewEntry.this,"Please enter student roll!!!",Toast.LENGTH_LONG).show();
                    }
                    else if(editparent.getText().toString().equals(""))
                    {
                        Toast.makeText(StudentiCardNewEntry.this,"Please enter guardian name!!!",Toast.LENGTH_LONG).show();
                    }
                    else if(editparentnumber.getText().toString().equals(""))
                    {
                        Toast.makeText(StudentiCardNewEntry.this,"Please enter guardian's phone number!!!",Toast.LENGTH_LONG).show();
                    }
                    else if(editaddress.getText().toString().equals(""))
                    {
                        Toast.makeText(StudentiCardNewEntry.this,"Please enter student address!!!",Toast.LENGTH_LONG).show();
                    }
                    else if(selectedClass == null)
                    {
                        Toast.makeText(StudentiCardNewEntry.this,"Please select class!!!",Toast.LENGTH_LONG).show();
                    }
                    else if(selectedShift == null)
                    {
                        Toast.makeText(StudentiCardNewEntry.this,"Please select shift!!!",Toast.LENGTH_LONG).show();
                    }
                    else if(selectedSection == null)
                    {
                        Toast.makeText(StudentiCardNewEntry.this,"Please select section!!!",Toast.LENGTH_LONG).show();
                    }
                    else if((!(editname.getText().toString().equals("")))&&(!(editroll.getText().toString().equals("")))
                            && (!(editparent.getText().toString().equals(""))) && (!(editparentnumber.getText().toString().equals("")))
                            && (!(editaddress.getText().toString().equals(""))) && (!(selectedClass == null))
                            && (!(selectedShift == null)) && (!(selectedSection == null)))
                    {
                        dialog.show();
                        if(checkBox.isChecked())
                        {
                            originalBitmap = mCropImageView.getCroppedImage(500, 500);
                            if (originalBitmap != null)
                            {
                                mCropImageView.setImageBitmap(originalBitmap);
                                fileFromBitmap = new FileFromBitmap(originalBitmap, StudentiCardNewEntry.this);
                                fileFromBitmap.execute();
//                            encodeImage(cropped);
                            }


//                                Bitmap cropped =  mCropImageView.getCroppedImage(mCropImageView.getWidth(), mCropImageView.getHeight());
//                                if (cropped != null)
//                                {
//                                    mCropImageView.setImageBitmap(cropped);
//                                    encodeImage(cropped);
//                                }
//                                try
//                                {
//                                    ImagePath = MediaStore.Images.Media.insertImage(NewEntryActivity.this.getContentResolver(), cropped, "demo_image", "demo_image");
//                                }
//                                catch (Exception e)
//                                {
//                                }
                        }
                        else
                        {
                            fileFromBitmap = new FileFromBitmap(originalBitmap, StudentiCardNewEntry.this);
                            fileFromBitmap.execute();
                        }
                    }
                }
                else
                {
                    Toast.makeText(StudentiCardNewEntry.this,"Please check your INTERNET connection!!!",Toast.LENGTH_LONG).show();
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
                                Toast.makeText(StudentiCardNewEntry.this,jsonObject.getString("path"), Toast.LENGTH_LONG).show();
                                name = editname.getText().toString();
                                roll = editroll.getText().toString();
                                parentName = editparent.getText().toString();
                                parentNumber = editparentnumber.getText().toString();
                                address = editaddress.getText().toString();
                                StudentInformationEntry studentInformationEntry = new StudentInformationEntry();
                                studentInformationEntry.setUserName(name);
                                studentInformationEntry.setRollNo(roll);
                                studentInformationEntry.setGuardian(parentName);
                                studentInformationEntry.setGuardianPhone(parentNumber);
                                studentInformationEntry.setPreAddress(address);
                                studentInformationEntry.setInstituteID(Long.toString(InstituteID));
                                studentInformationEntry.setClassID(Long.toString(selectedClass.getClassID()));
                                studentInformationEntry.setShiftID(Long.toString(selectedShift.getShiftID()));
                                studentInformationEntry.setSectionID(Long.toString(selectedSection.getSectionID()));
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
                            Toast.makeText(StudentiCardNewEntry.this,"Data Successfully Updated with Response: "+response.getJSONObject(0).get("ReturnValue"),Toast.LENGTH_LONG).show();
                            finish();
                        }
                        catch (Exception e)
                        {

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(StudentiCardNewEntry.this,"Not Response: "+error.toString(),Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
        queuePost.add(customRequest);
    }

    void parseClassJsonData(String jsonString) {
        try {
            JSONArray classJsonArray = new JSONArray(jsonString);
            ArrayList classArrayList = new ArrayList();
            for(int i = 0; i < classJsonArray.length(); ++i) {
                JSONObject classJsonObject = classJsonArray.getJSONObject(i);
                ClassModel classModel = new ClassModel(classJsonObject.getString("ClassID"), classJsonObject.getString("ClassName"));
//                Toast.makeText(this,classJsonObject.getString("ClassID")+classJsonObject.getString("ClassName"),Toast.LENGTH_LONG).show();
                allClassArrayList.add(classModel);
                classArrayList.add(classModel.getClassName());
            }
            classArrayList.add("Select Class");
            classSpinnerPosition = classArrayList.indexOf("Select Class");
            selectedClass = allClassArrayList.get(0);
            ArrayAdapter<ArrayList> class_spinner_adapter = new ArrayAdapter<ArrayList>(this,R.layout.spinner_item, classArrayList){

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {

                    View v = super.getView(position, convertView, parent);
                    if (position == getCount()) {
//                    ((TextView)v.findViewById(android.R.id.text1)).setText("");
//                    ((TextView)v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
                    }

                    return v;
                }

                @Override
                public int getCount() {
                    return super.getCount()-1; // you dont display last item. It is used as hint.
                }

            };
            class_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerClass.setAdapter(class_spinner_adapter);
            spinnerClass.setSelection(class_spinner_adapter.getCount());
            //spinner.setSelectedIndex(1);
        } catch (JSONException e) {
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
            dialog.dismiss();
        }
    }

    void parseShiftJsonData(String jsonString) {
        try {
            JSONArray shiftJsonArray = new JSONArray(jsonString);
            ArrayList shiftArrayList = new ArrayList();
            for(int i = 0; i < shiftJsonArray.length(); ++i) {
                JSONObject shiftJsonObject = shiftJsonArray.getJSONObject(i);
                ShiftModel shiftModel = new ShiftModel(shiftJsonObject.getString("ShiftID"), shiftJsonObject.getString("ShiftName"));
//                Toast.makeText(this,classJsonObject.getString("ClassID")+classJsonObject.getString("ClassName"),Toast.LENGTH_LONG).show();
                allShiftArrayList.add(shiftModel);
                shiftArrayList.add(shiftModel.getShiftName());
            }
            shiftArrayList.add("Select Shift");
            shiftSpinnerPosition = shiftArrayList.indexOf("Select Shift");
            selectedShift = allShiftArrayList.get(0);
            ArrayAdapter<ArrayList> shift_spinner_adapter = new ArrayAdapter<ArrayList>(this,R.layout.spinner_item, shiftArrayList){

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {

                    View v = super.getView(position, convertView, parent);
                    if (position == getCount()) {
//                    ((TextView)v.findViewById(android.R.id.text1)).setText("");
//                    ((TextView)v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
                    }

                    return v;
                }

                @Override
                public int getCount() {
                    return super.getCount()-1; // you dont display last item. It is used as hint.
                }

            };
            shift_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerShift.setAdapter(shift_spinner_adapter);
            spinnerShift.setSelection(shift_spinner_adapter.getCount());
            dialog.dismiss();
            //spinner.setSelectedIndex(1);
        } catch (JSONException e) {
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
            dialog.dismiss();
        }
    }

    void parseSectionJsonData(String jsonString) {
        try {
            JSONArray sectionJsonArray = new JSONArray(jsonString);
            ArrayList sectionArrayList = new ArrayList();
            for(int i = 0; i < sectionJsonArray.length(); ++i) {
                JSONObject sectionJsonObject = sectionJsonArray.getJSONObject(i);
                SectionModel sectionModel = new SectionModel(sectionJsonObject.getString("SectionID"), sectionJsonObject.getString("SectionName"));
//                Toast.makeText(this,classJsonObject.getString("ClassID")+classJsonObject.getString("ClassName"),Toast.LENGTH_LONG).show();
                allSectionArrayList.add(sectionModel);
                sectionArrayList.add(sectionModel.getSectionName());
            }
            sectionArrayList.add("Select Section");
            sectionSpinnerPosition = sectionArrayList.indexOf("Select Section");
            selectedSection = allSectionArrayList.get(0);
            ArrayAdapter<ArrayList> section_spinner_adapter = new ArrayAdapter<ArrayList>(this,R.layout.spinner_item, sectionArrayList){

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {

                    View v = super.getView(position, convertView, parent);
                    if (position == getCount()) {
//                    ((TextView)v.findViewById(android.R.id.text1)).setText("");
//                    ((TextView)v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
                    }

                    return v;
                }

                @Override
                public int getCount() {
                    return super.getCount()-1; // you dont display last item. It is used as hint.
                }

            };
            section_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerSection.setAdapter(section_spinner_adapter);
            spinnerSection.setSelection(section_spinner_adapter.getCount());
            dialog.dismiss();
            //spinner.setSelectedIndex(1);
        } catch (JSONException e) {
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
            dialog.dismiss();
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
                    originalBitmap = bitmap;
                } catch (IOException e) {
                    e.printStackTrace();
                }

//                encodeImage(bitmap);
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

//                encodeImage(bitmap);
            }
        }

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                originalBitmap = bitmap;
//                encodeImage(bitmap);
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

    private String encodeImage(Bitmap bm)
    {
        bm = getResizedBitmap(bm, 500);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] b = baos.toByteArray();
        encImage = Base64.encodeToString(b, Base64.DEFAULT);

        return encImage;
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
