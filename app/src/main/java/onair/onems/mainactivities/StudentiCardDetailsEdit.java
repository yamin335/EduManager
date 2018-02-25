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
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.system.ErrnoException;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
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
import java.util.Calendar;
import java.util.List;

import onair.onems.R;
import onair.onems.Services.GlideApp;
import onair.onems.customadapters.CustomRequest;
import onair.onems.models.BloodGroupModel;
import onair.onems.models.ClassModel;
import onair.onems.models.DepartmentModel;
import onair.onems.models.GenderModel;
import onair.onems.models.MediumModel;
import onair.onems.models.ReligionModel;
import onair.onems.models.SectionModel;
import onair.onems.models.ShiftModel;
import onair.onems.models.StudentInformationEntry;


/**
 * Created by User on 12/20/2017.
 */

public class StudentiCardDetailsEdit extends AppCompatActivity {

    private EditText t_name, t_email, t_address, t_parent, t_parentsPhone, t_roll,
            t_stuEmail, t_stuPhone, t_remarks;
    private TextView t_board, t_session, t_class, t_section, t_birthDay, t_department,
            t_sex, t_religion, t_medium, t_shift, t_branch, t_studentNo, t_rfid, t_bloodGroup;

    private Bundle bundle;

    private CropImageView mCropImageView;
    private CheckBox checkBox;
    private Uri mCropImageUri;
    private ProgressDialog dialog, glideProgress;

    private StudentInformationEntry studentInformationEntry;
    private Button updateStudentPhoto, cameraButton, searchButton, rotateRight, rotateLeft, datePicker;
    private Spinner spinnerClass, spinnerShift, spinnerSection, spinnerMedium, spinnerDepartment, spinnerGender, spinnerReligion, spinnerBloodGroup;
    private String classUrl, shiftUrl, sectionUrl, mediumUrl, departmentUrl, genderUrl, religionUrl, bloodGroupUrl;
    private ArrayList<ClassModel> allClassArrayList;
    private ArrayList<ShiftModel> allShiftArrayList;
    private ArrayList<SectionModel> allSectionArrayList;
    private ArrayList<MediumModel> allMediumArrayList;
    private ArrayList<DepartmentModel> allDepartmentArrayList;
    private ArrayList<GenderModel> allGenderArrayList;
    private ArrayList<ReligionModel> allReligionArrayList;
    private ArrayList<BloodGroupModel> allBloodGroupArrayList;

    private String[] tempClassArray = {"Select Class"};
    private String[] tempShiftArray = {"Select Shift"};
    private String[] tempSectionArray = {"Select Section"};
    private String[] tempDepartmentArray = {"Select Department"};
    private String[] tempMediumArray = {"Select Medium"};
    private String[] tempGenderArray = {"Select Gender"};
    private String[] tempReligionArray = {"Select Religion"};
    private String[] tempBloodGroupArray = {"Select Blood Group"};

    private ClassModel selectedClass = null;
    private ShiftModel selectedShift = null;
    private SectionModel selectedSection = null;
    private MediumModel selectedMedium = null;
    private DepartmentModel selectedDepartment = null;
    private GenderModel selectedGender = null;
    private ReligionModel selectedReligion = null;
    private BloodGroupModel selectedBloodGroup = null;

    private JSONObject jsonObjectStudentData;

    private File file;

    private Bitmap originalBitmap = null;
    private Bitmap tempBitmap = null;

    private FileFromBitmap fileFromBitmap = null;

    private String studentDataGetUrl, studentDataPostUrl, selected_Class, selected_Shift,
            selected_Section, selected_Medium, selected_Department, UserID,
            ClassName = "", SectionName = "", ShiftName = "", MameName = "", DepartmentName = "", Gender = "", Religion = "";
    private int PICK_IMAGE_REQUEST = 1;
    private String ImagePath;
    private Uri URI;

    private long InstituteID;
    private SeekBar brightImageSeekBar;
    private static int brightnessValue;
    private BrightnessProcessTask mBrightnessProcessTask = null;
    private RotateProcessTask mRotateProcessTask = null;
    private String selectedDate = "", ClassID = "", ShiftID = "", SectionID = "", MediumID = "", DepartmentID = "", GenderID = "", ReligionID = "";
    private DatePickerDialog datePickerDialog;
    private boolean imageChanged = false;
    private int firstClass = 0, firstShift = 0, firstSection = 0, firstMedium = 0,
            firstDepartment = 0, firstGender = 0, firstReligion = 0, firstBloodGroup = 0;

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
        setContentView(R.layout.icard_student_details_edit);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Intent intent = getIntent();
        bundle = intent.getExtras();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        InstituteID = prefs.getLong("InstituteID",0);
        selected_Class = bundle.getString("ClassID");
        selected_Shift = bundle.getString("ShiftID");
        selected_Section = bundle.getString("SectionID");
        selected_Medium = bundle.getString("MediumID");
        selected_Department = bundle.getString("DepartmentID");
        UserID = bundle.getString("UserID");

        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading...");
        dialog.setMessage("Please Wait...");
        dialog.setCancelable(false);
        dialog.show();

        glideProgress = new ProgressDialog(this);
        glideProgress.setTitle("Loading...");
        glideProgress.setMessage("Please Wait...");
        glideProgress.setCancelable(false);

        rotateLeft = (Button)findViewById(R.id.rotateLeft);
        rotateRight = (Button)findViewById(R.id.rotateRight);
        brightImageSeekBar = (SeekBar)findViewById(R.id.brightness);
        brightImageSeekBar.setProgress(100);

        studentInformationEntry = new StudentInformationEntry();

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
        studentDataGetUrl = getString(R.string.baseUrlLocal)+"getStudent"+"/"+InstituteID+"/"+
                selected_Class+"/"+selected_Section+"/"+
                selected_Department+"/"+selected_Medium+"/"+selected_Shift+"/"+UserID;

        //Preparing Student data from server
        RequestQueue queueStudent = Volley.newRequestQueue(StudentiCardDetailsEdit.this);
        StringRequest stringStudentRequest = new StringRequest(Request.Method.GET, studentDataGetUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        parseStudentJsonData(response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.dismiss();
            }
        });
        queueStudent.add(stringStudentRequest);

        t_roll = (EditText) findViewById(R.id.stuRoll);
        t_name = (EditText)findViewById(R.id.stuName);
        t_class = (TextView) findViewById(R.id.classs);
        t_section = (TextView)findViewById(R.id.section);
        t_birthDay = (TextView)findViewById(R.id.birthday);
        t_email = (EditText)findViewById(R.id.guardianEmail);
        t_address = (EditText)findViewById(R.id.presentAddress);
        t_parent = (EditText)findViewById(R.id.guardianName);
        t_parentsPhone = (EditText)findViewById(R.id.guardianPhoneNo);
        t_department = (TextView)findViewById(R.id.department);
        t_sex = (TextView)findViewById(R.id.sex);
        t_religion = (TextView)findViewById(R.id.religion);
        t_medium = (TextView)findViewById(R.id.medium);
        t_session = (TextView)findViewById(R.id.session);
        t_board = (TextView)findViewById(R.id.board);
        t_shift = (TextView)findViewById(R.id.shift);
        t_stuEmail = (EditText)findViewById(R.id.stuEmail);
        t_stuPhone = (EditText)findViewById(R.id.stuPhone);
        t_branch = (TextView)findViewById(R.id.branch);
        t_remarks = (EditText)findViewById(R.id.remarks);
        t_studentNo = (TextView)findViewById(R.id.studentNo);
        t_rfid = (TextView)findViewById(R.id.rfid);
        t_bloodGroup = (TextView)findViewById(R.id.bloodGroup);
        datePicker = (Button)findViewById(R.id.pickDate);

        spinnerClass = (Spinner)findViewById(R.id.spinnerClass);
        spinnerShift = (Spinner)findViewById(R.id.spinnerShift);
        spinnerSection = (Spinner)findViewById(R.id.spinnerSection);
        spinnerMedium =(Spinner)findViewById(R.id.spinnerMedium);
        spinnerDepartment =(Spinner)findViewById(R.id.spinnerDepartment);
        spinnerGender =(Spinner)findViewById(R.id.spinnerGender);
        spinnerReligion =(Spinner)findViewById(R.id.spinnerReligion);
        spinnerBloodGroup =(Spinner)findViewById(R.id.spinnerBloodGroup);

        selectedClass = new ClassModel();
        selectedShift = new ShiftModel();
        selectedSection = new SectionModel();
        selectedMedium = new MediumModel();
        selectedDepartment = new DepartmentModel();
        selectedGender = new GenderModel();
        selectedReligion = new ReligionModel();
        selectedBloodGroup = new BloodGroupModel();

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

        ArrayAdapter<String> gender_spinner_adapter = new ArrayAdapter<>(this, R.layout.spinner_item, tempGenderArray);
        gender_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(gender_spinner_adapter);

        ArrayAdapter<String> religion_spinner_adapter = new ArrayAdapter<>(this, R.layout.spinner_item, tempReligionArray);
        religion_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerReligion.setAdapter(religion_spinner_adapter);

        ArrayAdapter<String> bloodGroup_spinner_adapter = new ArrayAdapter<>(this, R.layout.spinner_item, tempBloodGroupArray);
        bloodGroup_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBloodGroup.setAdapter(bloodGroup_spinner_adapter);

        allClassArrayList = new ArrayList<>();
        allShiftArrayList = new ArrayList<>();
        allSectionArrayList = new ArrayList<>();
        allMediumArrayList = new ArrayList<>();
        allDepartmentArrayList = new ArrayList<>();
        allGenderArrayList = new ArrayList<>();
        allReligionArrayList = new ArrayList<>();
        allBloodGroupArrayList = new ArrayList<>();

        classUrl = getString(R.string.baseUrlLocal)+"getInsClass/"+InstituteID;
        shiftUrl = getString(R.string.baseUrlLocal)+"getInsShift/"+InstituteID;
        mediumUrl = getString(R.string.baseUrlLocal)+"getInsMedium/"+InstituteID;
        departmentUrl = getString(R.string.baseUrlLocal)+"getInsDepertment/"+InstituteID;
        genderUrl = getString(R.string.baseUrlLocal)+"getallgender";
        religionUrl = getString(R.string.baseUrlLocal)+"getreligion";
        bloodGroupUrl = getString(R.string.baseUrlLocal)+"getbloodgroups";

        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(StudentiCardDetailsEdit.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
//                                selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                selectedDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                                studentInformationEntry.setDOB(selectedDate);
                                t_birthDay.setText(selectedDate);
//                                datePicker.setText(selectedDate);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
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
                    dialog.show();
                    imageChanged = true;
                    mRotateProcessTask = new RotateProcessTask(originalBitmap, -90);
                    mRotateProcessTask.execute((Void) null);
                }
                else
                {
                    Toast.makeText(StudentiCardDetailsEdit.this,"No image found!!!",Toast.LENGTH_LONG).show();
                }
            }
        });

        rotateRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(originalBitmap != null)
                {
                    dialog.show();
                    imageChanged = true;
                    mRotateProcessTask = new RotateProcessTask(originalBitmap, 90);
                    mRotateProcessTask.execute((Void) null);
                }
                else
                {
                    Toast.makeText(StudentiCardDetailsEdit.this,"No image found!!!",Toast.LENGTH_LONG).show();
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
                    dialog.show();
                    imageChanged = true;
                    mBrightnessProcessTask = new BrightnessProcessTask(originalBitmap, brightnessValue);
                    mBrightnessProcessTask.execute((Void) null);
                }
                else
                {
                    Toast.makeText(StudentiCardDetailsEdit.this,"No image found!!!",Toast.LENGTH_LONG).show();
                }
            }
        });

        updateStudentPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNetworkAvailable())
                {
                    studentInformationEntry.setUserName(t_name.getText().toString());
                    studentInformationEntry.setRollNo(t_roll.getText().toString());
                    studentInformationEntry.setRFID(t_rfid.getText().toString());
                    studentInformationEntry.setStudentNo(t_studentNo.getText().toString());
                    studentInformationEntry.setEmailID(t_stuEmail.getText().toString());
                    studentInformationEntry.setPhoneNo(t_stuPhone.getText().toString());
                    studentInformationEntry.setGuardian(t_parent.getText().toString());
                    studentInformationEntry.setGuardianPhone(t_parentsPhone.getText().toString());
                    studentInformationEntry.setGuardianEmailID(t_email.getText().toString());
                    studentInformationEntry.setPreAddress(t_address.getText().toString());
                    studentInformationEntry.setRemarks(t_remarks.getText().toString());

                    if(imageChanged)
                    {
                        if(originalBitmap != null)
                        {
                            dialog.show();
                            if(checkBox.isChecked())
                            {
                                tempBitmap = mCropImageView.getCroppedImage(500, 500);
                                mCropImageView.setImageBitmap(tempBitmap);
                                fileFromBitmap = new FileFromBitmap(tempBitmap, StudentiCardDetailsEdit.this);
                                fileFromBitmap.execute();
                            }
                            else
                            {
                                fileFromBitmap = new FileFromBitmap(tempBitmap, StudentiCardDetailsEdit.this);
                                fileFromBitmap.execute();
                            }
                        }
                        else
                        {
                            dialog.dismiss();
                            Toast.makeText(StudentiCardDetailsEdit.this,"Take or choose a photo to update!!!",Toast.LENGTH_LONG).show();
                        }
                    }
                    else
                    {
                        dialog.show();
                        Gson gson = new Gson();
                        String json = gson.toJson(studentInformationEntry);
                        postUsingVolley(json);
                    }
                }
                else
                {
                    Toast.makeText(StudentiCardDetailsEdit.this,"Please check your INTERNET connection!!!",Toast.LENGTH_LONG).show();
                }
            }
        });

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

        dialog.show();
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

        dialog.show();
        //Preparing Medium data from server
        RequestQueue queueMedium = Volley.newRequestQueue(this);
        StringRequest stringMediumRequest = new StringRequest(Request.Method.GET, mediumUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        parseMediumJsonData(response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.dismiss();
            }
        });
        queueMedium.add(stringMediumRequest);

        dialog.show();
        //Preparing Department data from server
        RequestQueue queueDepartment = Volley.newRequestQueue(this);
        StringRequest stringDepartmentRequest = new StringRequest(Request.Method.GET, departmentUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        parseDepartmentJsonData(response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.dismiss();
            }
        });
        queueDepartment.add(stringDepartmentRequest);

        dialog.show();
        //Preparing Gender data from server
        RequestQueue queueGender = Volley.newRequestQueue(this);
        StringRequest stringGenderRequest = new StringRequest(Request.Method.GET, genderUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        parseGenderJsonData(response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.dismiss();
            }
        });
        queueGender.add(stringGenderRequest);

        dialog.show();
        //Preparing Religion data from server
        RequestQueue queueReligion = Volley.newRequestQueue(this);
        StringRequest stringReligionRequest = new StringRequest(Request.Method.GET, religionUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        parseReligionJsonData(response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.dismiss();
            }
        });
        queueReligion.add(stringReligionRequest);

        dialog.show();
        //Preparing Blood Group data from server
        RequestQueue queueBloodGroup = Volley.newRequestQueue(this);
        StringRequest stringBloodGroupRequest = new StringRequest(Request.Method.GET, bloodGroupUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        parseBloodGroupJsonData(response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.dismiss();
            }
        });
        queueBloodGroup.add(stringBloodGroupRequest);

        spinnerClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != 0)
                {
                    try {
                        selectedClass = allClassArrayList.get(position-1);
                        studentInformationEntry.setClassID(Long.toString(selectedClass.getClassID()));
                        t_class.setText(selectedClass.getClassName());
                    }
                    catch (IndexOutOfBoundsException e)
                    {
                        Toast.makeText(StudentiCardDetailsEdit.this,"No class found !!!",Toast.LENGTH_LONG).show();
                    }
                    sectionUrl = getString(R.string.baseUrlLocal)+"getInsSection/"+InstituteID+"/"+selectedClass.getClassID();
                    dialog.show();
                    //Preparing section data from server
                    RequestQueue queueSection = Volley.newRequestQueue(StudentiCardDetailsEdit.this);
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
                else
                {
                    if(firstClass++>1)
                    {
                        selectedClass = new ClassModel();
//                        studentInformationEntry.setClassID(ClassID);
//                        t_class.setText(ClassName);
                        studentInformationEntry.setClassID(Long.toString(selectedClass.getClassID()));
                        t_class.setText("NONE");
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerShift.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != 0)
                {
                    try {
                        selectedShift = allShiftArrayList.get(position-1);
                        studentInformationEntry.setShiftID(Long.toString(selectedShift.getShiftID()));
                        t_shift.setText(selectedShift.getShiftName());
                    }
                    catch (IndexOutOfBoundsException e)
                    {
                        Toast.makeText(StudentiCardDetailsEdit.this,"No shift found !!!",Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    if(firstShift++>1)
                    {
                        selectedShift = new ShiftModel();
//                        studentInformationEntry.setShiftID(ShiftID);
//                        t_shift.setText(ShiftName);
                        studentInformationEntry.setShiftID(Long.toString(selectedShift.getShiftID()));
                        t_shift.setText("NONE");
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

                if(position != 0)
                {
                    try {
                        selectedSection = allSectionArrayList.get(position-1);
                        studentInformationEntry.setSectionID(Long.toString(selectedSection.getSectionID()));
                        t_section.setText(selectedSection.getSectionName());
                    }
                    catch (IndexOutOfBoundsException e)
                    {
                        Toast.makeText(StudentiCardDetailsEdit.this,"No section found !!!",Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    if(firstSection++>1)
                    {
                        selectedSection = new SectionModel();
//                        studentInformationEntry.setSectionID(SectionID);
//                        t_section.setText(SectionName);
                        studentInformationEntry.setSectionID(Long.toString(selectedSection.getSectionID()));
                        t_section.setText("NONE");
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

                if(position != 0)
                {
                    try {
                        selectedMedium = allMediumArrayList.get(position-1);
                        studentInformationEntry.setMediumID(Long.toString(selectedMedium.getMediumID()));
                        t_medium.setText(selectedMedium.getMameName());
                    }
                    catch (IndexOutOfBoundsException e)
                    {
                        Toast.makeText(StudentiCardDetailsEdit.this,"No medium found !!!",Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    if(firstMedium++>1)
                    {
                        selectedMedium = new MediumModel();
//                        studentInformationEntry.setMediumID(MediumID);
//                        t_medium.setText(MameName);
                        studentInformationEntry.setMediumID(Long.toString(selectedMedium.getMediumID()));
                        t_medium.setText("NONE");
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

                if(position != 0)
                {
                    try {
                        selectedDepartment = allDepartmentArrayList.get(position-1);
                        studentInformationEntry.setDepartmentID(Long.toString(selectedDepartment.getDepartmentID()));
                        t_department.setText(selectedDepartment.getDepartmentName());
                    }
                    catch (IndexOutOfBoundsException e)
                    {
                        Toast.makeText(StudentiCardDetailsEdit.this,"No department found !!!",Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    if(firstDepartment++>1)
                    {
                        selectedDepartment = new DepartmentModel();
//                        studentInformationEntry.setDepartmentID(DepartmentID);
//                        t_department.setText(DepartmentName);
                        studentInformationEntry.setDepartmentID(Long.toString(selectedDepartment.getDepartmentID()));
                        t_department.setText("NONE");
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != 0)
                {
                    try {
                        selectedGender = allGenderArrayList.get(position-1);
                        studentInformationEntry.setGenderID(Long.toString(selectedGender.getGenderID()));
                        t_sex.setText(selectedGender.getGenderName());
                    }
                    catch (IndexOutOfBoundsException e)
                    {
                        Toast.makeText(StudentiCardDetailsEdit.this,"No gender found !!!",Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    if(firstGender++>1)
                    {
                        selectedGender = new GenderModel();
                        studentInformationEntry.setGenderID(Long.toString(selectedGender.getGenderID()));
                        t_sex.setText("NONE");
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerReligion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != 0)
                {
                    try {
                        selectedReligion = allReligionArrayList.get(position-1);
                        studentInformationEntry.setReligionID(Long.toString(selectedReligion.getReligionID()));
                        t_religion.setText(selectedReligion.getReligionName());
                    }
                    catch (IndexOutOfBoundsException e)
                    {
                        Toast.makeText(StudentiCardDetailsEdit.this,"No religion found !!!",Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    if(firstReligion++>1)
                    {
                        selectedReligion = new ReligionModel();
                        studentInformationEntry.setReligionID(Long.toString(selectedReligion.getReligionID()));
                        t_religion.setText("NONE");
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerBloodGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != 0)
                {
                    try {
                        selectedBloodGroup = allBloodGroupArrayList.get(position-1);
                        studentInformationEntry.setBloodGroupID(Long.toString(selectedBloodGroup.getBloodGroupID()));
                        t_bloodGroup.setText(selectedBloodGroup.getBloodGroupName());
                    }
                    catch (IndexOutOfBoundsException e)
                    {
                        Toast.makeText(StudentiCardDetailsEdit.this,"No religion found !!!",Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    if(firstBloodGroup++>1)
                    {
                        selectedBloodGroup = new BloodGroupModel();
                        studentInformationEntry.setBloodGroupID(Long.toString(selectedBloodGroup.getBloodGroupID()));
                        t_bloodGroup.setText("NONE");
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dialog.dismiss();
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
//                Toast.makeText(this,classJsonObject.getString("ClassID")+classJsonObject.getString("ClassName"),Toast.LENGTH_LONG).show();
                allClassArrayList.add(classModel);
                classArrayList.add(classModel.getClassName());
            }
            try {
                String[] strings = new String[classArrayList.size()];
                strings = classArrayList.toArray(strings);
                ArrayAdapter<String> class_spinner_adapter = new ArrayAdapter<>(this,R.layout.spinner_item, strings);
                class_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerClass.setAdapter(class_spinner_adapter);
                dialog.dismiss();
            }
            catch (IndexOutOfBoundsException e)
            {
                dialog.dismiss();
                Toast.makeText(this,"No class found !!!",Toast.LENGTH_LONG).show();
            }
            //spinner.setSelectedIndex(1);
        } catch (JSONException e) {
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
            dialog.dismiss();
        }
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
//                Toast.makeText(this,classJsonObject.getString("ClassID")+classJsonObject.getString("ClassName"),Toast.LENGTH_LONG).show();
                allShiftArrayList.add(shiftModel);
                shiftArrayList.add(shiftModel.getShiftName());
            }
            try {
                String[] strings = new String[shiftArrayList.size()];
                strings = shiftArrayList.toArray(strings);
                ArrayAdapter<String> shift_spinner_adapter = new ArrayAdapter<>(this,R.layout.spinner_item, strings);
                shift_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerShift.setAdapter(shift_spinner_adapter);
                dialog.dismiss();
            }
            catch (IndexOutOfBoundsException e)
            {
                dialog.dismiss();
                Toast.makeText(this,"No shift found !!!",Toast.LENGTH_LONG).show();
            }
            //spinner.setSelectedIndex(1);
        } catch (JSONException e) {
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
            dialog.dismiss();
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
//                Toast.makeText(this,classJsonObject.getString("ClassID")+classJsonObject.getString("ClassName"),Toast.LENGTH_LONG).show();
                allSectionArrayList.add(sectionModel);
                sectionArrayList.add(sectionModel.getSectionName());
            }
            try {
                String[] strings = new String[sectionArrayList.size()];
                strings = sectionArrayList.toArray(strings);
                ArrayAdapter<String> section_spinner_adapter = new ArrayAdapter<>(this,R.layout.spinner_item, strings);
                section_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerSection.setAdapter(section_spinner_adapter);
                dialog.dismiss();
            }
            catch (IndexOutOfBoundsException e)
            {
                dialog.dismiss();
                Toast.makeText(this,"No section found !!!",Toast.LENGTH_LONG).show();
            }
            //spinner.setSelectedIndex(1);
        } catch (JSONException e) {
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
            dialog.dismiss();
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
                        mediumJsonObject.getString("InstMediumID"), mediumJsonObject.getString("InstituteID"), mediumJsonObject.getString("IsActive"));
//                Toast.makeText(this,classJsonObject.getString("ClassID")+classJsonObject.getString("ClassName"),Toast.LENGTH_LONG).show();
                allMediumArrayList.add(mediumModel);
                mediumnArrayList.add(mediumModel.getMameName());
            }
            try {
                String[] strings = new String[mediumnArrayList.size()];
                strings = mediumnArrayList.toArray(strings);
                ArrayAdapter<String> medium_spinner_adapter = new ArrayAdapter<>(this,R.layout.spinner_item, strings);
                medium_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerMedium.setAdapter(medium_spinner_adapter);
                dialog.dismiss();
            }
            catch (IndexOutOfBoundsException e)
            {
                dialog.dismiss();
                Toast.makeText(this,"No medium found !!!",Toast.LENGTH_LONG).show();
            }
            //spinner.setSelectedIndex(1);
        } catch (JSONException e) {
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
            dialog.dismiss();
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
//                Toast.makeText(this,classJsonObject.getString("ClassID")+classJsonObject.getString("ClassName"),Toast.LENGTH_LONG).show();
                allDepartmentArrayList.add(departmentModel);
                departmentArrayList.add(departmentModel.getDepartmentName());
            }
            try {
                String[] strings = new String[departmentArrayList.size()];
                strings = departmentArrayList.toArray(strings);
                ArrayAdapter<String> department_spinner_adapter = new ArrayAdapter<>(this,R.layout.spinner_item, strings);
                department_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerDepartment.setAdapter(department_spinner_adapter);
                dialog.dismiss();
            }
            catch (IndexOutOfBoundsException e)
            {
                dialog.dismiss();
                Toast.makeText(this,"No department found !!!",Toast.LENGTH_LONG).show();
            }
            //spinner.setSelectedIndex(1);
        } catch (JSONException e) {
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
            dialog.dismiss();
        }
    }

    void parseGenderJsonData(String jsonString) {
        try {
            allGenderArrayList = new ArrayList<>();
            JSONArray genderJsonArray = new JSONArray(jsonString);
            ArrayList<String> genderArrayList = new ArrayList<>();
            genderArrayList.add("Select Gender");
            for(int i = 0; i < genderJsonArray.length(); ++i) {
                JSONObject genderJsonObject = genderJsonArray.getJSONObject(i);
                GenderModel genderModel = new GenderModel(genderJsonObject.getString("GenderID"), genderJsonObject.getString("GenderName"));
//                Toast.makeText(this,classJsonObject.getString("ClassID")+classJsonObject.getString("ClassName"),Toast.LENGTH_LONG).show();
                allGenderArrayList.add(genderModel);
                genderArrayList.add(genderModel.getGenderName());
            }
            try {
                String[] strings = new String[genderArrayList.size()];
                strings = genderArrayList.toArray(strings);
                ArrayAdapter<String> gender_spinner_adapter = new ArrayAdapter<>(this,R.layout.spinner_item, strings);
                gender_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerGender.setAdapter(gender_spinner_adapter);
                dialog.dismiss();
            }
            catch (IndexOutOfBoundsException e)
            {
                dialog.dismiss();
                Toast.makeText(this,"No gender found !!!",Toast.LENGTH_LONG).show();
            }
            //spinner.setSelectedIndex(1);
        } catch (JSONException e) {
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
            dialog.dismiss();
        }
    }

    void parseReligionJsonData(String jsonString) {
        try {
            allReligionArrayList = new ArrayList<>();
            JSONArray religionJsonArray = new JSONArray(jsonString);
            ArrayList<String> religionArrayList = new ArrayList<>();
            religionArrayList.add("Select Religion");
            for(int i = 0; i < religionJsonArray.length(); ++i) {
                JSONObject religionJsonObject = religionJsonArray.getJSONObject(i);
                ReligionModel religionModel = new ReligionModel(religionJsonObject.getString("ReligionID"), religionJsonObject.getString("ReligionName"));
//                Toast.makeText(this,classJsonObject.getString("ClassID")+classJsonObject.getString("ClassName"),Toast.LENGTH_LONG).show();
                allReligionArrayList.add(religionModel);
                religionArrayList.add(religionModel.getReligionName());
            }
            try {
                String[] strings = new String[religionArrayList.size()];
                strings = religionArrayList.toArray(strings);
                ArrayAdapter<String> religion_spinner_adapter = new ArrayAdapter<>(this,R.layout.spinner_item, strings);
                religion_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerReligion.setAdapter(religion_spinner_adapter);
                dialog.dismiss();
            }
            catch (IndexOutOfBoundsException e)
            {
                dialog.dismiss();
                Toast.makeText(this,"No religion found !!!",Toast.LENGTH_LONG).show();
            }
            //spinner.setSelectedIndex(1);
        } catch (JSONException e) {
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
            dialog.dismiss();
        }
    }

    void parseBloodGroupJsonData(String jsonString) {
        try {
            allBloodGroupArrayList = new ArrayList<>();
            JSONArray bloodGroupJsonArray = new JSONArray(jsonString);
            ArrayList<String> bloodGroupArrayList = new ArrayList<>();
            bloodGroupArrayList.add("Select Blood Group");
            for(int i = 0; i < bloodGroupJsonArray.length(); ++i) {
                JSONObject bloodGroupJsonObject = bloodGroupJsonArray.getJSONObject(i);
                BloodGroupModel bloodGroupModel = new BloodGroupModel(bloodGroupJsonObject.getString("BloodGroupID"), bloodGroupJsonObject.getString("BloodGroupName"));
//                Toast.makeText(this,classJsonObject.getString("ClassID")+classJsonObject.getString("ClassName"),Toast.LENGTH_LONG).show();
                allBloodGroupArrayList.add(bloodGroupModel);
                bloodGroupArrayList.add(bloodGroupModel.getBloodGroupName());
            }
            try {
                String[] strings = new String[bloodGroupArrayList.size()];
                strings = bloodGroupArrayList.toArray(strings);
                ArrayAdapter<String> bloodGroup_spinner_adapter = new ArrayAdapter<>(this,R.layout.spinner_item, strings);
                bloodGroup_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerBloodGroup.setAdapter(bloodGroup_spinner_adapter);
                dialog.dismiss();
            }
            catch (IndexOutOfBoundsException e)
            {
                dialog.dismiss();
                Toast.makeText(this,"No blood group found !!!",Toast.LENGTH_LONG).show();
            }
            //spinner.setSelectedIndex(1);
        } catch (JSONException e) {
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
            dialog.dismiss();
        }
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
//                                Toast.makeText(StudentiCardDetails.this,jsonObject.getString("path"), Toast.LENGTH_LONG).show();
                                studentInformationEntry.setImageUrl(jsonObject.getString("path"));
                                studentInformationEntry.setIsImageCaptured(true);
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
//                            Toast.makeText(StudentiCardDetails.this,"Data Successfully Updated with Response: "+response.getJSONObject(0).get("ReturnValue"),Toast.LENGTH_LONG).show();
                            Toast.makeText(StudentiCardDetailsEdit.this,"Successfully Updated",Toast.LENGTH_LONG).show();
//                            NavUtils.navigateUpFromSameTask(StudentiCardDetailsEdit.this);
                            StudentiCardDetailsEdit.super.onBackPressed();
                        }
                        catch (Exception e)
                        {
                            dialog.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(StudentiCardDetails.this,"Not Response: "+error.toString(),Toast.LENGTH_LONG).show();
                Toast.makeText(StudentiCardDetailsEdit.this,"Not Successfully Updated"+error.toString(),Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
        queuePost.add(customRequest);
    }

    void parseStudentJsonData(String jsonString) {
        try {
            JSONArray studentJsonArray = new JSONArray(jsonString);
            JSONObject studentJsonObject = studentJsonArray.getJSONObject(0);

            glideProgress.show();
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
                            glideProgress.dismiss();
                        }
                        @Override
                        public void onLoadFailed(Drawable errorDrawable) {
                            super.onLoadFailed(errorDrawable);
                            glideProgress.dismiss();
                            Toast.makeText(StudentiCardDetailsEdit.this,"No image found!!!",Toast.LENGTH_LONG).show();
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
            SectionID = studentJsonObject.getString("SectionID");
            studentInformationEntry.setClassID(studentJsonObject.getString("ClassID"));
            ClassID = studentJsonObject.getString("ClassID");
            studentInformationEntry.setBrunchID(studentJsonObject.getString("BrunchID"));
            studentInformationEntry.setShiftID(studentJsonObject.getString("ShiftID"));
            ShiftID = studentJsonObject.getString("ShiftID");
            studentInformationEntry.setRemarks(studentJsonObject.getString("Remarks"));
            studentInformationEntry.setInstituteID(studentJsonObject.getString("InstituteID"));
            studentInformationEntry.setUserTypeID(studentJsonObject.getString("UserTypeID"));
            studentInformationEntry.setGenderID(studentJsonObject.getString("GenderID"));
            GenderID = studentJsonObject.getString("GenderID");
            studentInformationEntry.setPhoneNo(studentJsonObject.getString("PhoneNo"));
            studentInformationEntry.setEmailID(studentJsonObject.getString("EmailID"));
            studentInformationEntry.setFingerUrl(studentJsonObject.getString("FingerUrl"));
            studentInformationEntry.setSignatureUrl(studentJsonObject.getString("SignatureUrl"));
            studentInformationEntry.setMediumID(studentJsonObject.getString("MediumID"));
            MediumID = studentJsonObject.getString("MediumID");
            studentInformationEntry.setDepartmentID(studentJsonObject.getString("DepartmentID"));
            DepartmentID = studentJsonObject.getString("DepartmentID");
            studentInformationEntry.setIsImageCaptured(studentJsonObject.getBoolean("IsImageCaptured"));
            studentInformationEntry.setImageUrl(studentJsonObject.getString("ImageUrl"));

            t_roll.setText(studentJsonObject.getString("RollNo"));
            t_name.setText(studentJsonObject.getString("UserName"));
            t_section.setText(studentJsonObject.getString("SectionName"));
            SectionName = studentJsonObject.getString("SectionName");
            t_birthDay.setText(studentJsonObject.getString("DOB"));
            t_email.setText(studentJsonObject.getString("GuardianEmailID"));
            t_address.setText(studentJsonObject.getString("PreAddress"));
            t_parent.setText(studentJsonObject.getString("Guardian"));
            t_parentsPhone.setText(studentJsonObject.getString("GuardianPhone"));
            t_department.setText(studentJsonObject.getString("DepartmentName"));
            DepartmentName = studentJsonObject.getString("DepartmentName");
            t_sex.setText(studentJsonObject.getString("Gender"));
            Gender = studentJsonObject.getString("Gender");
            t_religion.setText(studentJsonObject.getString("Religion"));
            Religion = studentJsonObject.getString("Religion");
            t_medium.setText(studentJsonObject.getString("MameName"));
            MameName = studentJsonObject.getString("MameName");
            t_session.setText(studentJsonObject.getString("SessionName"));
            t_board.setText(studentJsonObject.getString("BoardName"));
            t_class.setText(studentJsonObject.getString("ClassName"));
            ClassName = studentJsonObject.getString("ClassName");
            t_shift.setText(studentJsonObject.getString("ShiftName"));
            ShiftName = studentJsonObject.getString("ShiftName");
            t_rfid.setText(studentJsonObject.getString("RFID"));
            t_studentNo.setText(studentJsonObject.getString("StudentNo"));
            t_branch.setText(studentJsonObject.getString("BrunchName"));
            t_stuEmail.setText(studentJsonObject.getString("EmailID"));
            t_stuPhone.setText(studentJsonObject.getString("PhoneNo"));
            t_remarks.setText(studentJsonObject.getString("Remarks"));
            t_bloodGroup.setText(studentJsonObject.getString("BloodGroup"));

            sectionUrl = getString(R.string.baseUrlLocal)+"getInsSection/"+InstituteID+"/"+studentJsonObject.getString("ClassID");
            dialog.show();
            //Preparing section data from server
            RequestQueue queueSection = Volley.newRequestQueue(StudentiCardDetailsEdit.this);
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

        } catch (JSONException e) {
            Toast.makeText(this,"WARNING!!! "+e,Toast.LENGTH_LONG).show();
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
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
//            mBrightnessProcessTask = null;
            dialog.dismiss();
            if (success) {
                mCropImageView.setImageBitmap(tempBitmap);
            }
            else{
                mCropImageView.setImageBitmap(originalBitmap);
                Toast.makeText(StudentiCardDetailsEdit.this,"One ERROR occured!!!",Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
//            mBrightnessProcessTask = null;
            dialog.dismiss();
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
            dialog.dismiss();
            if (success) {
                mCropImageView.setImageBitmap(tempBitmap);
            }
            else{
                mCropImageView.setImageBitmap(originalBitmap);
                Toast.makeText(StudentiCardDetailsEdit.this,"One ERROR occured!!!",Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mRotateProcessTask = null;
            dialog.dismiss();
        }
    }


}
