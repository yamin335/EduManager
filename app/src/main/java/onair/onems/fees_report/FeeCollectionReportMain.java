package onair.onems.fees_report;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import onair.onems.R;
import onair.onems.Services.StaticHelperClass;
import onair.onems.exam.SubjectWiseMarksEntryMain;
import onair.onems.icard.StudentiCardMain;
import onair.onems.mainactivities.SideNavigationMenuParentActivity;
import onair.onems.mainactivities.TeacherMainScreen;
import onair.onems.models.BranchModel;
import onair.onems.models.ClassModel;
import onair.onems.models.DepartmentModel;
import onair.onems.models.ExamModel;
import onair.onems.models.MediumModel;
import onair.onems.models.MonthModel;
import onair.onems.models.SectionModel;
import onair.onems.models.SessionModel;
import onair.onems.models.ShiftModel;
import onair.onems.models.SubjectModel;
import onair.onems.models.YearModel;
import onair.onems.network.MySingleton;
import onair.onems.result.ResultMainScreen;

public class FeeCollectionReportMain extends SideNavigationMenuParentActivity {

    private Spinner spinnerBranch, spinnerShift, spinnerMedium, spinnerClass, spinnerDepartment, spinnerSection, spinnerSession, spinnerMonth, spinnerStatus;
    private ProgressDialog mBranchDialog, mShiftDialog, mMediumDialog, mClassDialog, mDepartmentDialog, mSectionDialog, mSessionDialog, mMonthDialog;

    private ArrayList<BranchModel> allBranchArrayList;
    private ArrayList<ShiftModel> allShiftArrayList;
    private ArrayList<MediumModel> allMediumArrayList;
    private ArrayList<ClassModel> allClassArrayList;
    private ArrayList<DepartmentModel> allDepartmentArrayList;
    private ArrayList<SectionModel> allSectionArrayList;
    private ArrayList<SessionModel> allSessionArrayList;
    private ArrayList<MonthModel> allMonthArrayList;
    private ArrayList<String> allStatusArrayList;

    private String[] tempBranchArray = {"Select Branch"};
    private String[] tempShiftArray = {"Select Shift"};
    private String[] tempMediumArray = {"Select Medium"};
    private String[] tempClassArray = {"Select Class"};
    private String[] tempDepartmentArray = {"Select Department"};
    private String[] tempSectionArray = {"Select Section"};
    private String[] tempSessionArray = {"Select Session"};
    private String[] tempMonthArray = {"Select Month"};
    private String[] tempStatusArray = {"Select Status", "Due", "Advance"};

    private BranchModel selectedBranch;
    private ShiftModel selectedShift;
    private MediumModel selectedMedium;
    private ClassModel selectedClass;
    private DepartmentModel selectedDepartment;
    private SectionModel selectedSection;
    private SessionModel selectedSession;
    private MonthModel selectedMonth;
    private String selectedStatus;

    private int firstBranch = 0, firstShift = 0, firstMedium = 0, firstClass = 0, firstDepartment = 0,
            firstSection = 0, firstSession = 0, firstMonth = 0, firstStatus = 0;

    private int  ReportType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityName = FeeCollectionReportMain.class.getName();

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View childActivityLayout = inflater.inflate(R.layout.fee_collection_report_main, null);
        LinearLayout parentActivityLayout = (LinearLayout) findViewById(R.id.contentMain);
        parentActivityLayout.addView(childActivityLayout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        ReportType = getIntent().getIntExtra("Report Type", 0);

        selectedBranch = new BranchModel();
        selectedShift = new ShiftModel();
        selectedMedium = new MediumModel();
        selectedClass = new ClassModel();
        selectedDepartment = new DepartmentModel();
        selectedSection = new SectionModel();
        selectedSession = new SessionModel();
        selectedMonth = new MonthModel();
        selectedStatus = "0";

        allStatusArrayList = new ArrayList<>();
        allStatusArrayList.add("1");
        allStatusArrayList.add("2");

        spinnerBranch = (Spinner)findViewById(R.id.spinnerBranch);
        spinnerShift = (Spinner)findViewById(R.id.spinnerShift);
        spinnerMedium =(Spinner)findViewById(R.id.spinnerMedium);
        spinnerClass = (Spinner)findViewById(R.id.spinnerClass);
        spinnerDepartment =(Spinner)findViewById(R.id.spinnerDepartment);
        spinnerSection = (Spinner)findViewById(R.id.spinnerSection);
        spinnerSession = (Spinner)findViewById(R.id.spinnerSession);
        spinnerMonth = (Spinner)findViewById(R.id.spinnerMonth);
        spinnerStatus = (Spinner)findViewById(R.id.spinnerStatus);

        Button showList = (Button)findViewById(R.id.showList);
        showList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckSelectedData();
                Intent intent = new Intent(FeeCollectionReportMain.this, FeeCollectionReportList.class);
                intent.putExtra("InstituteID", Long.toString(InstituteID));
                intent.putExtra("BranchID", Long.toString(selectedBranch.getBrunchID()));
                intent.putExtra("MediumID", Long.toString(selectedMedium.getMediumID()));
                intent.putExtra("ClassID", Long.toString(selectedClass.getClassID()));
                intent.putExtra("DepartmentID", Long.toString(selectedDepartment.getDepartmentID()));
                intent.putExtra("SectionID", Long.toString(selectedSection.getSectionID()));
                intent.putExtra("ShiftID", Long.toString(selectedShift.getShiftID()));
                intent.putExtra("MonthID", Long.toString(selectedMonth.getMonthID()));
                intent.putExtra("StatusID", selectedStatus);
                intent.putExtra("SessionID", Long.toString(selectedSession.getSessionID()));
                intent.putExtra("Report Type", ReportType);
                startActivity(intent);
            }
        });

        ArrayAdapter<String> branch_spinner_adapter = new ArrayAdapter<>(this, R.layout.spinner_item, tempBranchArray);
        branch_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBranch.setAdapter(branch_spinner_adapter);

        ArrayAdapter<String> shift_spinner_adapter = new ArrayAdapter<>(this, R.layout.spinner_item, tempShiftArray);
        shift_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerShift.setAdapter(shift_spinner_adapter);

        ArrayAdapter<String> medium_spinner_adapter = new ArrayAdapter<>(this, R.layout.spinner_item, tempMediumArray);
        medium_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMedium.setAdapter(medium_spinner_adapter);

        ArrayAdapter<String> class_spinner_adapter = new ArrayAdapter<>(this, R.layout.spinner_item, tempClassArray);
        class_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClass.setAdapter(class_spinner_adapter);

        ArrayAdapter<String> department_spinner_adapter = new ArrayAdapter<>(this, R.layout.spinner_item, tempDepartmentArray);
        department_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDepartment.setAdapter(department_spinner_adapter);

        ArrayAdapter<String> section_spinner_adapter = new ArrayAdapter<>(this, R.layout.spinner_item, tempSectionArray);
        section_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSection.setAdapter(section_spinner_adapter);

        ArrayAdapter<String> session_spinner_adapter = new ArrayAdapter<>(this, R.layout.spinner_item, tempSessionArray);
        session_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSession.setAdapter(session_spinner_adapter);

        ArrayAdapter<String> month_spinner_adapter = new ArrayAdapter<>(this, R.layout.spinner_item, tempMonthArray);
        month_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(month_spinner_adapter);

        ArrayAdapter<String> status_spinner_adapter = new ArrayAdapter<>(this, R.layout.spinner_item, tempStatusArray);
        status_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(status_spinner_adapter);

        spinnerBranch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != 0) {
                    try {
                        selectedBranch = allBranchArrayList.get(position-1);
                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(FeeCollectionReportMain.this,"No shift found !!!",Toast.LENGTH_LONG).show();
                    }
                } else {
                    if(firstBranch++>1) {
                        selectedBranch = new BranchModel();
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

                if(position != 0) {
                    try {
                        selectedShift = allShiftArrayList.get(position-1);
                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(FeeCollectionReportMain.this,"No shift found !!!",Toast.LENGTH_LONG).show();
                    }
                } else {
                    if(firstShift++>1) {
                        selectedShift = new ShiftModel();
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
                        ClassDataGetRequest();
                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(FeeCollectionReportMain.this,"No medium found !!!",Toast.LENGTH_LONG).show();
                    }
                } else {
                    if(firstMedium++>1) {
                        selectedMedium = new MediumModel();
                        selectedClass = new ClassModel();
                        selectedDepartment = new DepartmentModel();
                        selectedSection = new SectionModel();
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
                        DepartmentDataGetRequest();
                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(FeeCollectionReportMain.this,"No class found !!!",Toast.LENGTH_LONG).show();
                    }
                } else {
                    if(firstClass++>1) {
                        selectedClass = new ClassModel();
                        selectedDepartment = new DepartmentModel();
                        selectedSection = new SectionModel();
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
                        SectionDataGetRequest();
                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(FeeCollectionReportMain.this,"No department found !!!",Toast.LENGTH_LONG).show();
                    }
                } else {
                    if(firstDepartment++>1) {
                        selectedDepartment = new DepartmentModel();
                        selectedSection = new SectionModel();
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
                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(FeeCollectionReportMain.this,"No section found !!!",Toast.LENGTH_LONG).show();
                    }
                } else {
                    if(firstSection++>1) {
                        selectedSection = new SectionModel();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerSession.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != 0) {
                    try {
                        selectedSession = allSessionArrayList.get(position-1);
                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(FeeCollectionReportMain.this,"No shift found !!!",Toast.LENGTH_LONG).show();
                    }
                } else {
                    if(firstSession++>1) {
                        selectedSession = new SessionModel();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != 0) {
                    try {
                        selectedMonth = allMonthArrayList.get(position-1);
                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(FeeCollectionReportMain.this,"No section found !!!",Toast.LENGTH_LONG).show();
                    }
                } else {
                    selectedMonth = new MonthModel();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != 0) {
                    try {
                        selectedStatus = allStatusArrayList.get(position-1);
                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(FeeCollectionReportMain.this,"No section found !!!",Toast.LENGTH_LONG).show();
                    }
                } else {
                    if(firstStatus++>1) {
                        selectedStatus = "0";
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        BranchDataGetRequest();
        ShiftDataGetRequest();
        MediumDataGetRequest();
        SessionDataGetRequest();
        MonthDataGetRequest();
    }

    private void BranchDataGetRequest() {
        if (StaticHelperClass.isNetworkAvailable(this)) {
            String branchUrl = getString(R.string.baseUrl)+"/api/onEms/getBranchByInsID/"+InstituteID;

            mBranchDialog = new ProgressDialog(this);
            mBranchDialog.setTitle("Loading Branch...");
            mBranchDialog.setMessage("Please Wait...");
            mBranchDialog.setCancelable(false);
            mBranchDialog.setIcon(R.drawable.onair);
            mBranchDialog.show();
            //Preparing Shift data from server
            StringRequest stringShiftRequest = new StringRequest(Request.Method.GET, branchUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            parseBranchJsonData(response);

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mBranchDialog.dismiss();
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
            Toast.makeText(FeeCollectionReportMain.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    void parseBranchJsonData(String jsonString) {
        ArrayList<String> branchArrayList = new ArrayList<>();
        try {
            allBranchArrayList = new ArrayList<>();
            JSONArray branchJsonArray = new JSONArray(jsonString);
            branchArrayList.add("Select Branch");
            for(int i = 0; i < branchJsonArray.length(); ++i) {
                JSONObject branchJsonObject = branchJsonArray.getJSONObject(i);
                BranchModel branchModel = new BranchModel(branchJsonObject.getString("BrunchID"), branchJsonObject.getString("BrunchNo")
                        , branchJsonObject.getString("BrunchName"), branchJsonObject.getString("ParentID"), branchJsonObject.getString("InstituteID")
                        , branchJsonObject.getString("ParentBranch"), branchJsonObject.getString("Institute"));
                allBranchArrayList.add(branchModel);
                branchArrayList.add(branchModel.getBrunchName());
            }
        } catch (JSONException e) {
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
        }
        try {
            String[] strings = new String[branchArrayList.size()];
            strings = branchArrayList.toArray(strings);
            ArrayAdapter<String> branch_spinner_adapter = new ArrayAdapter<>(this,R.layout.spinner_item, strings);
            branch_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerBranch.setAdapter(branch_spinner_adapter);
        } catch (IndexOutOfBoundsException e) {
            Toast.makeText(this,"No branch found !!!",Toast.LENGTH_LONG).show();
        }
        mBranchDialog.dismiss();
    }

    private void ShiftDataGetRequest() {
        if (StaticHelperClass.isNetworkAvailable(this)) {
            String shiftUrl = getString(R.string.baseUrl)+"/api/onEms/getInsShift/"+InstituteID;

            mShiftDialog = new ProgressDialog(this);
            mShiftDialog.setTitle("Loading Shift...");
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
            Toast.makeText(FeeCollectionReportMain.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    void parseShiftJsonData(String jsonString) {
        ArrayList<String> shiftArrayList = new ArrayList<>();
        try {
            allShiftArrayList = new ArrayList<>();
            JSONArray shiftJsonArray = new JSONArray(jsonString);
            shiftArrayList.add("Select Shift");
            for(int i = 0; i < shiftJsonArray.length(); ++i) {
                JSONObject shiftJsonObject = shiftJsonArray.getJSONObject(i);
                ShiftModel shiftModel = new ShiftModel(shiftJsonObject.getString("ShiftID"), shiftJsonObject.getString("ShiftName"));
                allShiftArrayList.add(shiftModel);
                shiftArrayList.add(shiftModel.getShiftName());
            }
        } catch (JSONException e) {
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
        }
        try {
            String[] strings = new String[shiftArrayList.size()];
            strings = shiftArrayList.toArray(strings);
            ArrayAdapter<String> shift_spinner_adapter = new ArrayAdapter<>(this,R.layout.spinner_item, strings);
            shift_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerShift.setAdapter(shift_spinner_adapter);
        } catch (IndexOutOfBoundsException e) {
            Toast.makeText(this,"No shift found !!!",Toast.LENGTH_LONG).show();
        }
        mShiftDialog.dismiss();
    }

    private void MediumDataGetRequest() {
        if(StaticHelperClass.isNetworkAvailable(this)) {
            String mediumUrl = getString(R.string.baseUrl)+"/api/onEms/getInstituteMediumDdl/"+InstituteID;

            mMediumDialog = new ProgressDialog(this);
            mMediumDialog.setTitle("Loading Medium...");
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
            Toast.makeText(FeeCollectionReportMain.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    void parseMediumJsonData(String jsonString) {
        ArrayList<String> mediumArrayList = new ArrayList<>();
        try {
            allMediumArrayList = new ArrayList<>();
            JSONArray mediumJsonArray = new JSONArray(jsonString);
            mediumArrayList.add("Select Medium");
            for(int i = 0; i < mediumJsonArray.length(); ++i) {
                JSONObject mediumJsonObject = mediumJsonArray.getJSONObject(i);
                MediumModel mediumModel = new MediumModel(mediumJsonObject.getString("MediumID"), mediumJsonObject.getString("MameName"),
                        mediumJsonObject.getString("IsDefault"));
                allMediumArrayList.add(mediumModel);
                mediumArrayList.add(mediumModel.getMameName());
            }
        } catch (JSONException e) {
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
        }

        try {
            String[] strings = new String[mediumArrayList.size()];
            strings = mediumArrayList.toArray(strings);
            ArrayAdapter<String> medium_spinner_adapter = new ArrayAdapter<>(this,R.layout.spinner_item, strings);
            medium_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerMedium.setAdapter(medium_spinner_adapter);
        } catch (IndexOutOfBoundsException e) {
            Toast.makeText(this,"No medium found !!!",Toast.LENGTH_LONG).show();
        }
        mMediumDialog.dismiss();
    }

    private void ClassDataGetRequest() {
        if(StaticHelperClass.isNetworkAvailable(this)) {

            CheckSelectedData();

            String classUrl = getString(R.string.baseUrl)+"/api/onEms/MediumWiseClassDDL/"+InstituteID+"/"+selectedMedium.getMediumID();

            mClassDialog = new ProgressDialog(this);
            mClassDialog.setTitle("Loading Class...");
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
            Toast.makeText(FeeCollectionReportMain.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    void parseClassJsonData(String jsonString) {
        ArrayList<String> classArrayList = new ArrayList<>();
        try {
            allClassArrayList = new ArrayList<>();
            JSONArray classJsonArray = new JSONArray(jsonString);
            classArrayList.add("Select Class");
            for(int i = 0; i < classJsonArray.length(); ++i) {
                JSONObject classJsonObject = classJsonArray.getJSONObject(i);
                ClassModel classModel = new ClassModel(classJsonObject.getString("ClassID"), classJsonObject.getString("ClassName"));
                allClassArrayList.add(classModel);
                classArrayList.add(classModel.getClassName());
            }
        } catch (JSONException e) {
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
        }

        try {
            String[] strings = new String[classArrayList.size()];
            strings = classArrayList.toArray(strings);
            ArrayAdapter<String> class_spinner_adapter = new ArrayAdapter<>(this,R.layout.spinner_item, strings);
            class_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerClass.setAdapter(class_spinner_adapter);
        } catch (IndexOutOfBoundsException e) {
            Toast.makeText(this,"No class found !!!",Toast.LENGTH_LONG).show();
        }
        mClassDialog.dismiss();
    }

    private void DepartmentDataGetRequest() {
        if(StaticHelperClass.isNetworkAvailable(this)) {

            CheckSelectedData();

            String departmentUrl = getString(R.string.baseUrl)+"/api/onEms/ClassWiseDepartmentDDL/"+InstituteID+"/"+
                    selectedClass.getClassID()+"/"+selectedMedium.getMediumID();

            mDepartmentDialog = new ProgressDialog(this);
            mDepartmentDialog.setTitle("Loading Department...");
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
            Toast.makeText(FeeCollectionReportMain.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    void parseDepartmentJsonData(String jsonString) {
        ArrayList<String> departmentArrayList = new ArrayList<>();
        try {
            allDepartmentArrayList = new ArrayList<>();
            JSONArray departmentJsonArray = new JSONArray(jsonString);
            departmentArrayList.add("Select Department");
            for(int i = 0; i < departmentJsonArray.length(); ++i) {
                JSONObject departmentJsonObject = departmentJsonArray.getJSONObject(i);
                DepartmentModel departmentModel = new DepartmentModel(departmentJsonObject.getString("DepartmentID"), departmentJsonObject.getString("DepartmentName"));
                allDepartmentArrayList.add(departmentModel);
                departmentArrayList.add(departmentModel.getDepartmentName());
            }
        } catch (JSONException e) {
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
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
        } catch (IndexOutOfBoundsException e) {
            Toast.makeText(this,"No department found !!!",Toast.LENGTH_LONG).show();
        }
        mDepartmentDialog.dismiss();
    }

    private void SectionDataGetRequest() {
        if(StaticHelperClass.isNetworkAvailable(this)) {

            CheckSelectedData();

            String sectionUrl = getString(R.string.baseUrl)+"/api/onEms/getInsSection/"+InstituteID+"/"+
                    selectedClass.getClassID()+"/"+selectedDepartment.getDepartmentID();

            mSectionDialog = new ProgressDialog(this);
            mSectionDialog.setTitle("Loading Section...");
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
            Toast.makeText(FeeCollectionReportMain.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    void parseSectionJsonData(String jsonString) {
        ArrayList<String> sectionArrayList = new ArrayList<>();
        try {
            allSectionArrayList = new ArrayList<>();
            JSONArray sectionJsonArray = new JSONArray(jsonString);
            sectionArrayList.add("Select Section");
            for(int i = 0; i < sectionJsonArray.length(); ++i) {
                JSONObject sectionJsonObject = sectionJsonArray.getJSONObject(i);
                SectionModel sectionModel = new SectionModel(sectionJsonObject.getString("SectionID"), sectionJsonObject.getString("SectionName"));
                allSectionArrayList.add(sectionModel);
                sectionArrayList.add(sectionModel.getSectionName());
            }
        } catch (JSONException e) {
//            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
        }
        try {
            String[] strings = new String[sectionArrayList.size()];
            strings = sectionArrayList.toArray(strings);
            ArrayAdapter<String> section_spinner_adapter = new ArrayAdapter<>(this,R.layout.spinner_item, strings);
            section_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerSection.setAdapter(section_spinner_adapter);
        } catch (IndexOutOfBoundsException e) {
            Toast.makeText(this,"No section found !!!",Toast.LENGTH_LONG).show();
        }
        mSectionDialog.dismiss();
    }

    private void SessionDataGetRequest() {
        if (StaticHelperClass.isNetworkAvailable(this)) {
            String sessionUrl = getString(R.string.baseUrl)+"/api/onEms/getallsession";

            mSessionDialog = new ProgressDialog(this);
            mSessionDialog.setTitle("Loading session...");
            mSessionDialog.setMessage("Please Wait...");
            mSessionDialog.setCancelable(false);
            mSessionDialog.setIcon(R.drawable.onair);
            mSessionDialog.show();
            //Preparing Shift data from server
            StringRequest stringSessionRequest = new StringRequest(Request.Method.GET, sessionUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            parseSessionJsonData(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mSessionDialog.dismiss();
                    Toast.makeText(FeeCollectionReportMain.this,"Session not found!!! ",
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
            MySingleton.getInstance(this).addToRequestQueue(stringSessionRequest);
        } else {
            Toast.makeText(FeeCollectionReportMain.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    void parseSessionJsonData(String jsonString) {
        ArrayList<String> sessionArrayList = new ArrayList<>();
        try {
            allSessionArrayList = new ArrayList<>();
            JSONArray sessionJsonArray = new JSONArray(jsonString);
            sessionArrayList.add("Select Session");
            for(int i = 0; i < sessionJsonArray.length(); ++i) {
                JSONObject sessionJsonObject = sessionJsonArray.getJSONObject(i);
                SessionModel sessionModel = new SessionModel(sessionJsonObject.getString("SessionID"), sessionJsonObject.getString("SessionName"));
                allSessionArrayList.add(sessionModel);
                sessionArrayList.add(sessionModel.getSessionName());
            }
        } catch (JSONException e) {
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
        }
        try {
            String[] strings = new String[sessionArrayList.size()];
            strings = sessionArrayList.toArray(strings);
            ArrayAdapter<String> session_spinner_adapter = new ArrayAdapter<>(this,R.layout.spinner_item, strings);
            session_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerSession.setAdapter(session_spinner_adapter);
        } catch (IndexOutOfBoundsException e) {
            Toast.makeText(this,"No session found !!!",Toast.LENGTH_LONG).show();
        }
        mSessionDialog.dismiss();
    }

    void MonthDataGetRequest(){
        if(StaticHelperClass.isNetworkAvailable(this)) {
            mMonthDialog = new ProgressDialog(this);
            mMonthDialog.setTitle("Loading Month...");
            mMonthDialog.setMessage("Please Wait...");
            mMonthDialog.setCancelable(false);
            mMonthDialog.setIcon(R.drawable.onair);
            mMonthDialog.show();
            String monthUrl=getString(R.string.baseUrl)+"/api/onEms/getMonth";
            StringRequest stringMonthRequest = new StringRequest(Request.Method.GET, monthUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            parseMonthJsonData(response);

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mMonthDialog.dismiss();
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
            MySingleton.getInstance(this).addToRequestQueue(stringMonthRequest);
        } else {
            Toast.makeText(this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    void parseMonthJsonData(String jsonString) {
        ArrayList<String> monthArrayList = new ArrayList<>();
        try {
            allMonthArrayList = new ArrayList<>();
            JSONArray MonthJsonArray = new JSONArray(jsonString);
            monthArrayList.add("Select Month");
            for(int i = 0; i < MonthJsonArray.length(); ++i) {
                JSONObject monthJsonObject = MonthJsonArray.getJSONObject(i);
                MonthModel monthModel = new MonthModel(monthJsonObject.getString("MonthID"), monthJsonObject.getString("MonthName"));
                allMonthArrayList.add(monthModel);
                monthArrayList.add(monthModel.getMonthName());
            }
        } catch (JSONException e) {
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
        }
        try {
            String[] strings = new String[monthArrayList.size()];
            strings = monthArrayList.toArray(strings);
            ArrayAdapter<String> month_spinner_adapter = new ArrayAdapter<>(this,R.layout.spinner_item, strings);
            month_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerMonth.setAdapter(month_spinner_adapter);
            Calendar c = Calendar.getInstance();
            spinnerMonth.setSelection(c.get(Calendar.MONTH)+1);
        } catch (IndexOutOfBoundsException e) {
            Toast.makeText(this,"No class found !!!",Toast.LENGTH_LONG).show();
        }
        mMonthDialog.dismiss();
    }

    private void CheckSelectedData(){
        if(selectedShift.getShiftID() == -2) {
            selectedShift.setShiftID("0");
        }
        if(selectedMedium.getMediumID() == -2) {
            selectedMedium.setMediumID("0");
        }
        if(selectedClass.getClassID() == -2) {
            selectedClass.setClassID("0");
        }
        if(selectedDepartment.getDepartmentID() == -2) {
            selectedDepartment.setDepartmentID("0");
        }
        if(selectedSection.getSectionID() == -2) {
            selectedSection.setSectionID("0");
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent mainIntent = new Intent(FeeCollectionReportMain.this,TeacherMainScreen.class);
            startActivity(mainIntent);
            finish();
        }
    }
}
