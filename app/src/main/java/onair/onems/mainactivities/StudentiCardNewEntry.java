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
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
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
import onair.onems.customadapters.CustomRequest;
import onair.onems.models.ClassModel;
import onair.onems.models.DepartmentModel;
import onair.onems.models.MediumModel;
import onair.onems.models.SectionModel;
import onair.onems.models.ShiftModel;
import onair.onems.models.StudentInformationEntry;
import onair.onems.network.MySingleton;

public class StudentiCardNewEntry extends AppCompatActivity {

    private Button rotateRight, rotateLeft;
    private int PICK_IMAGE_REQUEST = 1;
    private EditText editname,editroll,editaddress,editparent,editparentnumber;
    private String name,roll,address,parentName,parentNumber;
    private CropImageView mCropImageView;
    private CheckBox checkBox;
    private Uri mCropImageUri;
    private ProgressDialog mShiftDialog, mMediumDialog, mClassDialog, mDepartmentDialog, mSectionDialog, mStudentDataPostDialog, mRotateDialog, mBrightnessDialog;
    private long InstituteID;
    private Spinner spinnerClass, spinnerShift, spinnerSection, spinnerMedium, spinnerDepartment;
    private ArrayList<ClassModel> allClassArrayList;
    private ArrayList<ShiftModel> allShiftArrayList;
    private ArrayList<SectionModel> allSectionArrayList;
    private ArrayList<MediumModel> allMediumArrayList;
    private ArrayList<DepartmentModel> allDepartmentArrayList;

    private String[] tempClassArray = {"Select Class"};
    private String[] tempShiftArray = {"Select Shift"};
    private String[] tempSectionArray = {"Select Section"};
    private String[] tempDepartmentArray = {"Select Department"};
    private String[] tempMediumArray = {"Select Medium"};

    private ClassModel selectedClass = null;
    private ShiftModel selectedShift = null;
    private SectionModel selectedSection = null;
    private MediumModel selectedMedium = null;
    private DepartmentModel selectedDepartment = null;

    private boolean IsImageCaptured = false;

    private JSONObject jsonObjectStudentData;

    private File file;

    private Bitmap originalBitmap = null, tempBitmap = null;

    private FileFromBitmap fileFromBitmap = null;
    private SeekBar brightImageSeekBar;
    private static int brightnessValue;
    private BrightnessProcessTask mBrightnessProcessTask = null;
    private RotateProcessTask mRotateProcessTask = null;
    private StudentInformationEntry studentInformationEntry;
    private int firstClass = 0, firstShift = 0, firstSection = 0, firstMedium = 0,
            firstDepartment = 0;

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

        studentInformationEntry = new StudentInformationEntry();
        selectedClass = new ClassModel();
        selectedShift = new ShiftModel();
        selectedSection = new SectionModel();
        selectedMedium = new MediumModel();
        selectedDepartment = new DepartmentModel();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        InstituteID = prefs.getLong("InstituteID",0);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        rotateLeft = (Button)findViewById(R.id.rotateLeft);
        rotateRight = (Button)findViewById(R.id.rotateRight);
        Button cameraButton=(Button)findViewById(R.id.camera);
        Button searchButton=(Button) findViewById(R.id.browse);
        Button doneButton=(Button) findViewById(R.id.done);

        brightImageSeekBar = (SeekBar)findViewById(R.id.brightness);
        brightImageSeekBar.setProgress(100);
        brightImageSeekBar.setEnabled(false);

        mCropImageView = (CropImageView)findViewById(R.id.CropImageView);
        mCropImageView.setAspectRatio(1,1);
        mCropImageView.setAutoZoomEnabled(true);
        mCropImageView.setFixedAspectRatio(true);

        checkBox = (CheckBox)findViewById(R.id.checkbox);

        spinnerClass = (Spinner)findViewById(R.id.spinnerClass);
        spinnerShift = (Spinner)findViewById(R.id.spinnerShift);
        spinnerSection = (Spinner)findViewById(R.id.spinnerSection);
        spinnerMedium =(Spinner)findViewById(R.id.spinnerMedium);
        spinnerDepartment =(Spinner)findViewById(R.id.spinnerDepartment);

        editname=(EditText)findViewById(R.id.edited_name);
        editroll=(EditText)findViewById(R.id.edited_roll);
        editaddress=(EditText)findViewById(R.id.edited_address);
        editparent=(EditText)findViewById(R.id.edited_parent);
        editparentnumber=(EditText)findViewById(R.id.edited_parentPhone);

        ArrayAdapter<String> class_spinner_adapter = new ArrayAdapter<>(this, R.layout.spinner_item, tempClassArray);
        class_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClass.setAdapter(class_spinner_adapter);

        ArrayAdapter<String> shift_spinner_adapter = new ArrayAdapter<>(this, R.layout.spinner_item, tempShiftArray);
        shift_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerShift.setAdapter(shift_spinner_adapter);

        ArrayAdapter<String> section_spinner_adapter = new ArrayAdapter<>(this, R.layout.spinner_item, tempSectionArray);
        section_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSection.setAdapter(section_spinner_adapter);

        ArrayAdapter<String> department_spinner_adapter = new ArrayAdapter<>(this, R.layout.spinner_item, tempDepartmentArray);
        department_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDepartment.setAdapter(department_spinner_adapter);

        ArrayAdapter<String> medium_spinner_adapter = new ArrayAdapter<>(this, R.layout.spinner_item, tempMediumArray);
        medium_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMedium.setAdapter(medium_spinner_adapter);

        rotateLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRotateDialog = new ProgressDialog(StudentiCardNewEntry.this);
                mRotateDialog.setTitle("Loading...");
                mRotateDialog.setMessage("Please Wait...");
                mRotateDialog.setCancelable(false);
                mRotateDialog.setIcon(R.drawable.onair);
                mRotateDialog.show();
                mRotateProcessTask = new RotateProcessTask(originalBitmap, -90);
                mRotateProcessTask.execute((Void) null);
            }
        });

        rotateRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRotateDialog = new ProgressDialog(StudentiCardNewEntry.this);
                mRotateDialog.setTitle("Loading...");
                mRotateDialog.setMessage("Please Wait...");
                mRotateDialog.setCancelable(false);
                mRotateDialog.setIcon(R.drawable.onair);
                mRotateDialog.show();
                mRotateProcessTask = new RotateProcessTask(originalBitmap, 90);
                mRotateProcessTask.execute((Void) null);
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
                mBrightnessDialog = new ProgressDialog(StudentiCardNewEntry.this);
                mBrightnessDialog.setTitle("Loading...");
                mBrightnessDialog.setMessage("Please Wait...");
                mBrightnessDialog.setCancelable(false);
                mBrightnessDialog.setIcon(R.drawable.onair);
                mBrightnessDialog.show();
                mBrightnessProcessTask = new BrightnessProcessTask(originalBitmap, brightnessValue);
                mBrightnessProcessTask.execute((Void) null);
            }
        });

        ShiftDataGetRequest();
        MediumDataGetRequest();

        spinnerShift.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != 0) {
                    try {
                        selectedShift = allShiftArrayList.get(position-1);
                        studentInformationEntry.setShiftID(Long.toString(selectedShift.getShiftID()));
                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(StudentiCardNewEntry.this,"No shift found !!!",Toast.LENGTH_LONG).show();
                    }
                } else {
                    if(firstShift++>1) {
                        selectedShift = new ShiftModel();
                        studentInformationEntry.setShiftID(Long.toString(selectedShift.getShiftID()));
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerMedium.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != 0) {
                    try {
                        selectedMedium = allMediumArrayList.get(position-1);
                        selectedClass = new ClassModel();
                        selectedDepartment = new DepartmentModel();
                        selectedSection = new SectionModel();
                        studentInformationEntry.setMediumID(Long.toString(selectedMedium.getMediumID()));
                        studentInformationEntry.setClassID(Long.toString(selectedClass.getClassID()));
                        studentInformationEntry.setDepartmentID(Long.toString(selectedDepartment.getDepartmentID()));
                        studentInformationEntry.setSectionID(Long.toString(selectedSection.getSectionID()));
                        ClassDataGetRequest();
                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(StudentiCardNewEntry.this,"No medium found !!!",Toast.LENGTH_LONG).show();
                    }
                } else {
                    if(firstMedium++>1) {
                        selectedMedium = new MediumModel();
                        selectedClass = new ClassModel();
                        selectedDepartment = new DepartmentModel();
                        selectedSection = new SectionModel();
                        studentInformationEntry.setMediumID(Long.toString(selectedMedium.getMediumID()));
                        studentInformationEntry.setClassID(Long.toString(selectedClass.getClassID()));
                        studentInformationEntry.setDepartmentID(Long.toString(selectedDepartment.getDepartmentID()));
                        studentInformationEntry.setSectionID(Long.toString(selectedSection.getSectionID()));
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != 0) {
                    try {
                        selectedClass = allClassArrayList.get(position-1);
                        selectedDepartment = new DepartmentModel();
                        selectedSection = new SectionModel();
                        studentInformationEntry.setClassID(Long.toString(selectedClass.getClassID()));
                        studentInformationEntry.setDepartmentID(Long.toString(selectedDepartment.getDepartmentID()));
                        studentInformationEntry.setSectionID(Long.toString(selectedSection.getSectionID()));
                        DepartmentDataGetRequest();
                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(StudentiCardNewEntry.this,"No class found !!!",Toast.LENGTH_LONG).show();
                    }
                } else {
                    if(firstClass++>1) {
                        selectedClass = new ClassModel();
                        selectedDepartment = new DepartmentModel();
                        selectedSection = new SectionModel();
                        studentInformationEntry.setClassID(Long.toString(selectedClass.getClassID()));
                        studentInformationEntry.setDepartmentID(Long.toString(selectedDepartment.getDepartmentID()));
                        studentInformationEntry.setSectionID(Long.toString(selectedSection.getSectionID()));
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerDepartment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != 0) {
                    try {
                        selectedDepartment = allDepartmentArrayList.get(position-1);
                        selectedSection = new SectionModel();
                        studentInformationEntry.setDepartmentID(Long.toString(selectedDepartment.getDepartmentID()));
                        studentInformationEntry.setSectionID(Long.toString(selectedSection.getSectionID()));
                        SectionDataGetRequest();
                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(StudentiCardNewEntry.this,"No department found !!!",Toast.LENGTH_LONG).show();
                    }
                } else {
                    if(firstDepartment++>1) {
                        selectedDepartment = new DepartmentModel();
                        selectedSection = new SectionModel();
                        studentInformationEntry.setDepartmentID(Long.toString(selectedDepartment.getDepartmentID()));
                        studentInformationEntry.setSectionID(Long.toString(selectedSection.getSectionID()));
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerSection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position != 0) {
                    try {
                        selectedSection = allSectionArrayList.get(position-1);
                        studentInformationEntry.setSectionID(Long.toString(selectedSection.getSectionID()));
                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(StudentiCardNewEntry.this,"No section found !!!",Toast.LENGTH_LONG).show();
                    }
                } else {
                    if(firstSection++>1) {
                        selectedSection = new SectionModel();
                        studentInformationEntry.setSectionID(Long.toString(selectedSection.getSectionID()));
                    }
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
                if(isNetworkAvailable()) {
                    StudentiCardNewEntry.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                    if(editname.getText().toString().equals("")) {
                        Toast.makeText(StudentiCardNewEntry.this,"Please enter student name!!!",Toast.LENGTH_LONG).show();
                    } else if(editroll.getText().toString().equals("")) {
                        Toast.makeText(StudentiCardNewEntry.this,"Please enter student roll!!!",Toast.LENGTH_LONG).show();
                    } else if(editparent.getText().toString().equals("")) {
                        Toast.makeText(StudentiCardNewEntry.this,"Please enter guardian name!!!",Toast.LENGTH_LONG).show();
                    } else if(editparentnumber.getText().toString().equals("")) {
                        Toast.makeText(StudentiCardNewEntry.this,"Please enter guardian's phone number!!!",Toast.LENGTH_LONG).show();
                    } else if(editaddress.getText().toString().equals("")) {
                        Toast.makeText(StudentiCardNewEntry.this,"Please enter student address!!!",Toast.LENGTH_LONG).show();
                    } else if((studentInformationEntry.getClassID() == -2)) {
                        Toast.makeText(StudentiCardNewEntry.this,"Please select class!!!",Toast.LENGTH_LONG).show();
                    } else if((!(editname.getText().toString().equals(""))) && (!(editroll.getText().toString().equals("")))
                            && (!(editparent.getText().toString().equals(""))) && (!(editparentnumber.getText().toString().equals("")))
                            && (!(editaddress.getText().toString().equals(""))) && (!(studentInformationEntry.getClassID() == -2))) {
                        if(tempBitmap == null) {
                            mStudentDataPostDialog = new ProgressDialog(StudentiCardNewEntry.this);
                            mStudentDataPostDialog.setTitle("Loading...");
                            mStudentDataPostDialog.setMessage("Please Wait...");
                            mStudentDataPostDialog.setCancelable(false);
                            mStudentDataPostDialog.setIcon(R.drawable.onair);
                            mStudentDataPostDialog.show();
                            name = editname.getText().toString();
                            roll = editroll.getText().toString();
                            parentName = editparent.getText().toString();
                            parentNumber = editparentnumber.getText().toString();
                            address = editaddress.getText().toString();
                            studentInformationEntry.setUserName(name);
                            studentInformationEntry.setRollNo(roll);
                            studentInformationEntry.setGuardian(parentName);
                            studentInformationEntry.setGuardianPhone(parentNumber);
                            studentInformationEntry.setPreAddress(address);
                            studentInformationEntry.setInstituteID(Long.toString(InstituteID));
                            Gson gson = new Gson();
                            String json = gson.toJson(studentInformationEntry);
                            postUsingVolley(json);
                        } else {
                            mStudentDataPostDialog = new ProgressDialog(StudentiCardNewEntry.this);
                            mStudentDataPostDialog.setTitle("Loading...");
                            mStudentDataPostDialog.setMessage("Please Wait...");
                            mStudentDataPostDialog.setCancelable(false);
                            mStudentDataPostDialog.setIcon(R.drawable.onair);
                            mStudentDataPostDialog.show();
                            if(checkBox.isChecked()) {
                                tempBitmap = mCropImageView.getCroppedImage(500, 500);
                                fileFromBitmap = new FileFromBitmap(tempBitmap, StudentiCardNewEntry.this);
                                fileFromBitmap.execute();
                            } else {
                                fileFromBitmap = new FileFromBitmap(tempBitmap, StudentiCardNewEntry.this);
                                fileFromBitmap.execute();
                            }
                        }
                    }
                } else {
                    Toast.makeText(StudentiCardNewEntry.this,"Please check your INTERNET connection!!!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public class FileFromBitmap extends AsyncTask<Void, Integer, String> {

        Context context;
        Bitmap bitmap;

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
                    .load(getString(R.string.baseUrl)+"/api/onEms/Mobile/uploads")
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
                                name = editname.getText().toString();
                                roll = editroll.getText().toString();
                                parentName = editparent.getText().toString();
                                parentNumber = editparentnumber.getText().toString();
                                address = editaddress.getText().toString();
                                studentInformationEntry = new StudentInformationEntry();
                                studentInformationEntry.setUserName(name);
                                studentInformationEntry.setRollNo(roll);
                                studentInformationEntry.setGuardian(parentName);
                                studentInformationEntry.setGuardianPhone(parentNumber);
                                studentInformationEntry.setPreAddress(address);
                                studentInformationEntry.setInstituteID(Long.toString(InstituteID));
                                studentInformationEntry.setImageUrl(jsonObject.getString("path"));
                                studentInformationEntry.setIsImageCaptured(IsImageCaptured);
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
        String studentDataPostUrl = getString(R.string.baseUrl)+"/api/onEms/setStudentBasicInfo";
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
                        try {
                            editname.setText("");
                            editroll.setText("");
                            editparent.setText("");
                            editparentnumber.setText("");
                            editaddress.setText("");
                            IsImageCaptured = false;
                            checkBox.setEnabled(false);
                            rotateLeft.setEnabled(false);
                            rotateRight.setEnabled(false);
                            brightImageSeekBar.setProgress(100);
                            brightImageSeekBar.setEnabled(false);
                            tempBitmap = null;
                            originalBitmap = null;
                            mCropImageView.clearImage();
                            checkBox.setChecked(false);
                            mStudentDataPostDialog.dismiss();
                            Toast.makeText(StudentiCardNewEntry.this,"Successful",Toast.LENGTH_LONG).show();
                        }
                        catch (Exception e) {
                            mStudentDataPostDialog.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mStudentDataPostDialog.dismiss();
                Toast.makeText(StudentiCardNewEntry.this,"Not Successful"+error,Toast.LENGTH_LONG).show();
                Log.e("VOLLEY POST ERROR:",error.toString());
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

    void parseShiftJsonData(String jsonString) {
        try {
            allShiftArrayList = new ArrayList<>();
            JSONArray shiftJsonArray = new JSONArray(jsonString);
            ArrayList<String> shiftArrayList = new ArrayList<>();
            shiftArrayList.add("Select Shift");
            for(int i = 0; i < shiftJsonArray.length(); ++i) {
                JSONObject shiftJsonObject = shiftJsonArray.getJSONObject(i);
                ShiftModel shiftModel = new ShiftModel(shiftJsonObject.getString("ShiftID"), shiftJsonObject.getString("ShiftName"));
                allShiftArrayList.add(shiftModel);
                shiftArrayList.add(shiftModel.getShiftName());
            }
            if(allShiftArrayList.size() == 1){
                selectedShift = allShiftArrayList.get(0);
            }
            try {
                String[] strings = new String[shiftArrayList.size()];
                strings = shiftArrayList.toArray(strings);
                ArrayAdapter<String> shift_spinner_adapter = new ArrayAdapter<>(this,R.layout.spinner_item, strings);
                shift_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerShift.setAdapter(shift_spinner_adapter);
                mShiftDialog.dismiss();
            } catch (IndexOutOfBoundsException e) {
                mShiftDialog.dismiss();
                Toast.makeText(this,"No shift found !!!",Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            mShiftDialog.dismiss();
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
        }
    }

    void parseMediumJsonData(String jsonString) {
        try {
            allMediumArrayList = new ArrayList<>();
            JSONArray mediumJsonArray = new JSONArray(jsonString);
            ArrayList<String> mediumnArrayList = new ArrayList<>();
            mediumnArrayList.add("Select Medium");
            for(int i = 0; i < mediumJsonArray.length(); ++i) {
                JSONObject mediumJsonObject = mediumJsonArray.getJSONObject(i);
                MediumModel mediumModel = new MediumModel(mediumJsonObject.getString("MediumID"), mediumJsonObject.getString("MameName"),
                        mediumJsonObject.getString("IsDefault"));
                allMediumArrayList.add(mediumModel);
                mediumnArrayList.add(mediumModel.getMameName());
            }
            if(allMediumArrayList.size() == 1){
                selectedMedium = allMediumArrayList.get(0);
                ClassDataGetRequest();
            }
            try {
                String[] strings = new String[mediumnArrayList.size()];
                strings = mediumnArrayList.toArray(strings);
                ArrayAdapter<String> medium_spinner_adapter = new ArrayAdapter<>(this,R.layout.spinner_item, strings);
                medium_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerMedium.setAdapter(medium_spinner_adapter);
                mMediumDialog.dismiss();
            } catch (IndexOutOfBoundsException e) {
                mMediumDialog.dismiss();
                Toast.makeText(this,"No medium found !!!",Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            mMediumDialog.dismiss();
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
        }
    }

    void parseClassJsonData(String jsonString) {
        try {
            allClassArrayList = new ArrayList<>();
            JSONArray classJsonArray = new JSONArray(jsonString);
            ArrayList<String> classArrayList = new ArrayList<>();
            classArrayList.add("Select Class");
            for(int i = 0; i < classJsonArray.length(); ++i) {
                JSONObject classJsonObject = classJsonArray.getJSONObject(i);
                ClassModel classModel = new ClassModel(classJsonObject.getString("ClassID"), classJsonObject.getString("ClassName"));
                allClassArrayList.add(classModel);
                classArrayList.add(classModel.getClassName());
            }
            if(allClassArrayList.size() == 1) {
                selectedClass = allClassArrayList.get(0);
                DepartmentDataGetRequest();
            }
            try {
                String[] strings = new String[classArrayList.size()];
                strings = classArrayList.toArray(strings);
                ArrayAdapter<String> class_spinner_adapter = new ArrayAdapter<>(this,R.layout.spinner_item, strings);
                class_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerClass.setAdapter(class_spinner_adapter);
                mClassDialog.dismiss();
            } catch (IndexOutOfBoundsException e) {
                mClassDialog.dismiss();
                Toast.makeText(this,"No class found !!!",Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            mClassDialog.dismiss();
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
        }
    }

    void parseDepartmentJsonData(String jsonString) {
        try {
            allDepartmentArrayList = new ArrayList<>();
            JSONArray departmentJsonArray = new JSONArray(jsonString);
            ArrayList<String> departmentArrayList = new ArrayList<>();
            departmentArrayList.add("Select Department");
            for(int i = 0; i < departmentJsonArray.length(); ++i) {
                JSONObject departmentJsonObject = departmentJsonArray.getJSONObject(i);
                DepartmentModel departmentModel = new DepartmentModel(departmentJsonObject.getString("DepartmentID"), departmentJsonObject.getString("DepartmentName"));
                allDepartmentArrayList.add(departmentModel);
                departmentArrayList.add(departmentModel.getDepartmentName());
            }
            if(allDepartmentArrayList.size() == 1){
                selectedDepartment = allDepartmentArrayList.get(0);
                SectionDataGetRequest();
            }
            if(allDepartmentArrayList.size() == 0){
                SectionDataGetRequest();
            }
            try {
                String[] strings = new String[departmentArrayList.size()];
                strings = departmentArrayList.toArray(strings);
                ArrayAdapter<String> department_spinner_adapter = new ArrayAdapter<>(this,R.layout.spinner_item, strings);
                department_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerDepartment.setAdapter(department_spinner_adapter);
                mDepartmentDialog.dismiss();
            } catch (IndexOutOfBoundsException e) {
                mDepartmentDialog.dismiss();
                Toast.makeText(this,"No department found !!!",Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            mDepartmentDialog.dismiss();
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
        }
    }

    void parseSectionJsonData(String jsonString) {
        try {
            allSectionArrayList = new ArrayList<>();
            JSONArray sectionJsonArray = new JSONArray(jsonString);
            ArrayList<String> sectionArrayList = new ArrayList<>();
            sectionArrayList.add("Select Section");
            for(int i = 0; i < sectionJsonArray.length(); ++i) {
                JSONObject sectionJsonObject = sectionJsonArray.getJSONObject(i);
                SectionModel sectionModel = new SectionModel(sectionJsonObject.getString("SectionID"), sectionJsonObject.getString("SectionName"));
                allSectionArrayList.add(sectionModel);
                sectionArrayList.add(sectionModel.getSectionName());
            }
            if(allSectionArrayList.size() == 1){
                selectedSection = allSectionArrayList.get(0);
            }
            try {
                String[] strings = new String[sectionArrayList.size()];
                strings = sectionArrayList.toArray(strings);
                ArrayAdapter<String> section_spinner_adapter = new ArrayAdapter<>(this,R.layout.spinner_item, strings);
                section_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerSection.setAdapter(section_spinner_adapter);
                mSectionDialog.dismiss();
            } catch (IndexOutOfBoundsException e) {
                mSectionDialog.dismiss();
                Toast.makeText(this,"No section found !!!",Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            mSectionDialog.dismiss();
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
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
                Bitmap bitmap;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    originalBitmap = getResizedBitmap(bitmap, 500);
                    tempBitmap =originalBitmap;
                    IsImageCaptured = true;
                    checkBox.setEnabled(true);
                    rotateLeft.setEnabled(true);
                    rotateRight.setEnabled(true);
                    brightImageSeekBar.setEnabled(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }

//                encodeImage(bitmap);
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            }

            if (!requirePermissions) {
                mCropImageView.setImageUriAsync(imageUri);
                Bitmap bitmap;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    originalBitmap = getResizedBitmap(bitmap, 500);
                    tempBitmap =originalBitmap;
                    IsImageCaptured = true;
                    checkBox.setEnabled(true);
                    rotateLeft.setEnabled(true);
                    rotateRight.setEnabled(true);
                    brightImageSeekBar.setEnabled(true);
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
                originalBitmap = getResizedBitmap(bitmap, 500);
                tempBitmap =originalBitmap;
                IsImageCaptured = true;
                checkBox.setEnabled(true);
                rotateLeft.setEnabled(true);
                rotateRight.setEnabled(true);
                brightImageSeekBar.setEnabled(true);
//                encodeImage(bitmap);
                try {
                    String ImagePath = MediaStore.Images.Media.insertImage(this.getContentResolver(), bitmap, "demo_image", "demo_image");
                    Uri URI = Uri.parse(ImagePath);
                    mCropImageView.setImageUriAsync(URI);
                    checkBox.setEnabled(true);
                }
                catch (Exception e)
                {
                    mCropImageView.setImageBitmap(bitmap);
                    checkBox.setEnabled(true);
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
            checkBox.setEnabled(true);
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
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);

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
        }
        else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }

    public static Bitmap rotateImage(Bitmap sourceImage, float angle) {
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
                Toast.makeText(StudentiCardNewEntry.this,"One ERROR occurred!!!",Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mBrightnessProcessTask = null;
            mBrightnessDialog.dismiss();
            Toast.makeText(StudentiCardNewEntry.this,"One ERROR occurred!!!",Toast.LENGTH_LONG).show();
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
                mRotateDialog.dismiss();
                Toast.makeText(StudentiCardNewEntry.this,"ERROR occurred while rotating image!!!",Toast.LENGTH_LONG).show();
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
                Toast.makeText(StudentiCardNewEntry.this,"ERROR occurred while rotating image!!!",Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mRotateProcessTask = null;
            mRotateDialog.dismiss();
            Toast.makeText(StudentiCardNewEntry.this,"ERROR occurred while rotating image!!!",Toast.LENGTH_LONG).show();
        }
    }

    private void ShiftDataGetRequest() {
        if (isNetworkAvailable()) {
            String shiftUrl = getString(R.string.baseUrl)+"/api/onEms/getInsShift/"+InstituteID;

            mShiftDialog = new ProgressDialog(this);
            mShiftDialog.setTitle("Loading...");
            mShiftDialog.setMessage("Please Wait...");
            mShiftDialog.setCancelable(false);
            mShiftDialog.setIcon(R.drawable.onair);
            mShiftDialog.show();
            //Preparing Shift data from server
            StringRequest stringShiftRequest = new StringRequest(Request.Method.GET, shiftUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            parseShiftJsonData(response);

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mShiftDialog.dismiss();
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
            MySingleton.getInstance(this).addToRequestQueue(stringShiftRequest);
        } else {
            Toast.makeText(StudentiCardNewEntry.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void MediumDataGetRequest() {
        if(isNetworkAvailable()) {
            String mediumUrl = getString(R.string.baseUrl)+"/api/onEms/getInstituteMediumDdl/"+InstituteID;

            mMediumDialog = new ProgressDialog(this);
            mMediumDialog.setTitle("Loading...");
            mMediumDialog.setMessage("Please Wait...");
            mMediumDialog.setCancelable(false);
            mMediumDialog.setIcon(R.drawable.onair);
            mMediumDialog.show();
            //Preparing Medium data from server
            StringRequest stringMediumRequest = new StringRequest(Request.Method.GET, mediumUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            parseMediumJsonData(response);

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mMediumDialog.dismiss();
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
            MySingleton.getInstance(this).addToRequestQueue(stringMediumRequest);
        } else {
            Toast.makeText(StudentiCardNewEntry.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void ClassDataGetRequest() {
        if(isNetworkAvailable()) {

            CheckSelectedData();

            String classUrl = getString(R.string.baseUrl)+"/api/onEms/MediumWiseClassDDL/"+InstituteID+"/"+selectedMedium.getMediumID();

            mClassDialog = new ProgressDialog(this);
            mClassDialog.setTitle("Loading...");
            mClassDialog.setMessage("Please Wait...");
            mClassDialog.setCancelable(false);
            mClassDialog.setIcon(R.drawable.onair);
            mClassDialog.show();
            //Preparing claas data from server
            StringRequest stringClassRequest = new StringRequest(Request.Method.GET, classUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            parseClassJsonData(response);

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mClassDialog.dismiss();
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
            MySingleton.getInstance(this).addToRequestQueue(stringClassRequest);
        } else {
            Toast.makeText(StudentiCardNewEntry.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void DepartmentDataGetRequest() {
        if(isNetworkAvailable()) {

            CheckSelectedData();

            String departmentUrl = getString(R.string.baseUrl)+"/api/onEms/ClassWiseDepartmentDDL/"+InstituteID+"/"+
                    selectedClass.getClassID()+"/"+selectedMedium.getMediumID();

            mDepartmentDialog = new ProgressDialog(this);
            mDepartmentDialog.setTitle("Loading...");
            mDepartmentDialog.setMessage("Please Wait...");
            mDepartmentDialog.setCancelable(false);
            mDepartmentDialog.setIcon(R.drawable.onair);
            mDepartmentDialog.show();
            //Preparing Department data from server
            StringRequest stringDepartmentRequest = new StringRequest(Request.Method.GET, departmentUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            parseDepartmentJsonData(response);

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mDepartmentDialog.dismiss();
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
            MySingleton.getInstance(this).addToRequestQueue(stringDepartmentRequest);
        } else {
            Toast.makeText(StudentiCardNewEntry.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void SectionDataGetRequest() {
        if(isNetworkAvailable()) {

            CheckSelectedData();

            String sectionUrl = getString(R.string.baseUrl)+"/api/onEms/getInsSection/"+InstituteID+"/"+
                    selectedClass.getClassID()+"/"+selectedDepartment.getDepartmentID();

            mSectionDialog = new ProgressDialog(this);
            mSectionDialog.setTitle("Loading...");
            mSectionDialog.setMessage("Please Wait...");
            mSectionDialog.setCancelable(false);
            mSectionDialog.setIcon(R.drawable.onair);
            mSectionDialog.show();
            //Preparing section data from server
            StringRequest stringSectionRequest = new StringRequest(Request.Method.GET, sectionUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            parseSectionJsonData(response);

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mSectionDialog.dismiss();
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
            MySingleton.getInstance(this).addToRequestQueue(stringSectionRequest);
        } else {
            Toast.makeText(StudentiCardNewEntry.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void CheckSelectedData(){
        if(selectedClass.getClassID() == -2)
        {
            selectedClass.setClassID("0");
        }
        if(selectedShift.getShiftID() == -2)
        {
            selectedShift.setShiftID("0");
        }
        if(selectedSection.getSectionID() == -2)
        {
            selectedSection.setSectionID("0");
        }
        if(selectedMedium.getMediumID() == -2)
        {
            selectedMedium.setMediumID("0");
        }
        if(selectedDepartment.getDepartmentID() == -2)
        {
            selectedDepartment.setDepartmentID("0");
        }
    }
}