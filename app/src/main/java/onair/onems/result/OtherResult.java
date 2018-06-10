package onair.onems.result;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import java.util.HashMap;
import java.util.Map;

import onair.onems.R;
import onair.onems.Services.StaticHelperClass;
import onair.onems.models.ClassModel;
import onair.onems.models.DepartmentModel;
import onair.onems.models.ExamModel;
import onair.onems.models.MediumModel;
import onair.onems.models.SectionModel;
import onair.onems.models.SessionModel;
import onair.onems.models.ShiftModel;
import onair.onems.network.MySingleton;

public class OtherResult extends Fragment {

    private Spinner spinnerClass, spinnerShift, spinnerSection,
            spinnerMedium, spinnerDepartment, spinnerStudent,
            spinnerSession, spinnerExam;
    private ProgressDialog mShiftDialog, mMediumDialog, mClassDialog,
            mDepartmentDialog, mSectionDialog, mStudentListGetDialog,
            mSessionDialog, mExamDialog;

    private ArrayList<ClassModel> allClassArrayList;
    private ArrayList<ShiftModel> allShiftArrayList;
    private ArrayList<SectionModel> allSectionArrayList;
    private ArrayList<MediumModel> allMediumArrayList;
    private ArrayList<DepartmentModel> allDepartmentArrayList;
    private ArrayList<SessionModel> allSessionArrayList;
    private ArrayList<ExamModel> allExamArrayList;

    private String[] tempClassArray = {"Select Class"};
    private String[] tempShiftArray = {"Select Shift"};
    private String[] tempSectionArray = {"Select Section"};
    private String[] tempDepartmentArray = {"Select Department"};
    private String[] tempMediumArray = {"Select Medium"};
    private String[] tempStudentArray = {"Select Student"};
    private String[] tempSessionArray = {"Select Session"};
    private String[] tempExamArray = {"Select Exam"};

    private ClassModel selectedClass;
    private ShiftModel selectedShift;
    private SectionModel selectedSection;
    private MediumModel selectedMedium;
    private DepartmentModel selectedDepartment;
    private SessionModel selectedSession;
    private ExamModel selectedExam;
    private JSONObject selectedStudent;

    private int firstClass = 0, firstShift = 0, firstSection = 0, firstMedium = 0,
            firstDepartment = 0, firstSession = 0, firstExam = 0;

    private JSONArray allStudentJsonArray;
    private long InstituteID;
    private String LoggedUserID = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        InstituteID = bundle.getLong("InstituteID");
        LoggedUserID = bundle.getString("LoggedUserID");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.result_content_main, container, false);

        allSessionArrayList = new ArrayList<>();
        allShiftArrayList = new ArrayList<>();
        allMediumArrayList = new ArrayList<>();
        allClassArrayList = new ArrayList<>();
        allDepartmentArrayList = new ArrayList<>();
        allSectionArrayList = new ArrayList<>();
        allExamArrayList = new ArrayList<>();

        selectedClass = new ClassModel();
        selectedShift = new ShiftModel();
        selectedSection = new SectionModel();
        selectedMedium = new MediumModel();
        selectedDepartment = new DepartmentModel();
        selectedSession = new SessionModel();
        selectedExam = new ExamModel();
        selectedStudent = null;

        spinnerClass = (Spinner)rootView.findViewById(R.id.spinnerClass);
        spinnerShift = (Spinner)rootView.findViewById(R.id.spinnerShift);
        spinnerSection = (Spinner)rootView.findViewById(R.id.spinnerSection);
        spinnerMedium =(Spinner)rootView.findViewById(R.id.spinnerMedium);
        spinnerDepartment =(Spinner)rootView.findViewById(R.id.spinnerDepartment);
        spinnerStudent = (Spinner)rootView.findViewById(R.id.spinnerStudent);
        spinnerSession = (Spinner)rootView.findViewById(R.id.spinnerSession);
        spinnerExam = (Spinner)rootView.findViewById(R.id.spinnerExam);

        Button showResult = (Button)rootView.findViewById(R.id.showResult);

        ArrayAdapter<String> class_spinner_adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, tempClassArray);
        class_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClass.setAdapter(class_spinner_adapter);

        ArrayAdapter<String> shift_spinner_adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, tempShiftArray);
        shift_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerShift.setAdapter(shift_spinner_adapter);

        ArrayAdapter<String> section_spinner_adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, tempSectionArray);
        section_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSection.setAdapter(section_spinner_adapter);

        ArrayAdapter<String> department_spinner_adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, tempDepartmentArray);
        department_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDepartment.setAdapter(department_spinner_adapter);

        ArrayAdapter<String> medium_spinner_adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, tempMediumArray);
        medium_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMedium.setAdapter(medium_spinner_adapter);

        ArrayAdapter<String> student_spinner_adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, tempStudentArray);
        student_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStudent.setAdapter(student_spinner_adapter);

        ArrayAdapter<String> session_spinner_adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, tempSessionArray);
        session_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSession.setAdapter(session_spinner_adapter);

        ArrayAdapter<String> exam_spinner_adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, tempExamArray);
        exam_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerExam.setAdapter(exam_spinner_adapter);

        spinnerSession.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != 0) {
                    try {
                        selectedSession = allSessionArrayList.get(position-1);
                        selectedStudent = null;
                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(getActivity(),"No shift found !!!",Toast.LENGTH_LONG).show();
                    }
                } else {
                    if(firstSession++>1) {
                        selectedSession = new SessionModel();
                        selectedStudent = null;
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
                        selectedStudent = null;
                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(getActivity(),"No shift found !!!",Toast.LENGTH_LONG).show();
                    }
                } else {
                    if(firstShift++>1) {
                        selectedShift = new ShiftModel();
                        selectedStudent = null;
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
                        selectedExam = new ExamModel();
                        selectedStudent = null;
                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(getActivity(),"No medium found !!!",Toast.LENGTH_LONG).show();
                    }
                } else {
                    if(firstMedium++>1) {
                        selectedMedium = new MediumModel();
                        selectedClass = new ClassModel();
                        selectedDepartment = new DepartmentModel();
                        selectedSection = new SectionModel();
                        selectedExam = new ExamModel();
                        selectedStudent = null;
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
                        ExamDataGetRequest();
                        selectedExam = new ExamModel();
                        selectedStudent = null;
                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(getActivity(),"No class found !!!",Toast.LENGTH_LONG).show();
                    }
                } else {
                    if(firstClass++>1) {
                        selectedClass = new ClassModel();
                        selectedDepartment = new DepartmentModel();
                        selectedSection = new SectionModel();
                        selectedExam = new ExamModel();
                        selectedStudent = null;
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
                        selectedStudent = null;
                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(getActivity(),"No shift found !!!",Toast.LENGTH_LONG).show();
                    }
                } else {
                    if(firstDepartment++>1) {
                        selectedDepartment = new DepartmentModel();
                        selectedSection = new SectionModel();
                        selectedStudent = null;
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
                        selectedStudent = null;
                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(getActivity(),"No section found !!!",Toast.LENGTH_LONG).show();
                    }
                } else {
                    if(firstSection++>1) {
                        selectedSection = new SectionModel();
                        selectedStudent = null;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerExam.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != 0) {
                    try {
                        selectedExam = allExamArrayList.get(position-1);
                        GetStudentListPostRequest();
                        selectedStudent = null;
                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(getActivity(),"No Exam found !!!",Toast.LENGTH_LONG).show();
                    }
                } else {
                    if(firstExam++>1) {
                        selectedExam = new ExamModel();
                        selectedStudent = null;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerStudent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position != 0) {
                    try {
                        try {
                            selectedStudent = allStudentJsonArray.getJSONObject(position-1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(getActivity(),"No student found !!!",Toast.LENGTH_LONG).show();
                    }
                } else {
                    selectedStudent = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        showResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(StaticHelperClass.isNetworkAvailable(getActivity())) {
                    if(allSessionArrayList.size()>0 && selectedSession.getSessionID() == 0){
                        Toast.makeText(getActivity(),"Please select session!!! ",
                                Toast.LENGTH_LONG).show();
                    } else if(allMediumArrayList.size()>0 && (selectedMedium.getMediumID() == -2 || selectedMedium.getMediumID() == 0)) {
                        Toast.makeText(getActivity(),"Please select medium!!! ",
                                Toast.LENGTH_LONG).show();
                    } else if(allClassArrayList.size()>0 && (selectedClass.getClassID() == -2 || selectedClass.getClassID() == 0)) {
                        Toast.makeText(getActivity(),"Please select class!!! ",
                                Toast.LENGTH_LONG).show();
                    } else if(allDepartmentArrayList.size()>0 && (selectedDepartment.getDepartmentID() == -2 || selectedDepartment.getDepartmentID() == 0)) {
                        Toast.makeText(getActivity(),"Please select department!!! ",
                                Toast.LENGTH_LONG).show();
                    } else if(allSectionArrayList.size()>0 && (selectedSection.getSectionID() == -2 || selectedSection.getSectionID() == 0)) {
                        Toast.makeText(getActivity(),"Please select section!!! ",
                                Toast.LENGTH_LONG).show();
                    } else if(allExamArrayList.size()>0 && selectedExam.getExamID() == 0) {
                        Toast.makeText(getActivity(),"Please select exam!!! ",
                                Toast.LENGTH_LONG).show();
                    } else if(selectedStudent == null){
                        Toast.makeText(getActivity(),"Please select a student!!! ",
                                Toast.LENGTH_LONG).show();
                    } else {
                        try {
                            Intent intent = new Intent(getActivity(), SubjectWiseResult.class);
                            intent.putExtra("UserID", selectedStudent.getString("UserID").equalsIgnoreCase("null")? "0":selectedStudent.getString("UserID"));
                            intent.putExtra("ShiftID", selectedStudent.getString("ShiftID").equalsIgnoreCase("null")? "0":selectedStudent.getString("ShiftID"));
                            intent.putExtra("MediumID", selectedStudent.getString("MediumID").equalsIgnoreCase("null")? "0":selectedStudent.getString("MediumID"));
                            intent.putExtra("ClassID", selectedStudent.getString("ClassID").equalsIgnoreCase("null")? "0":selectedStudent.getString("ClassID"));
                            intent.putExtra("DepartmentID", selectedStudent.getString("DepartmentID").equalsIgnoreCase("null")? "0":selectedStudent.getString("DepartmentID"));
                            intent.putExtra("SectionID", selectedStudent.getString("SectionID").equalsIgnoreCase("null")? "0":selectedStudent.getString("SectionID"));
                            intent.putExtra("SessionID", selectedStudent.getString("SessionID").equalsIgnoreCase("null")? "0":selectedStudent.getString("SessionID"));
                            intent.putExtra("ExamID", Long.toString(selectedExam.getExamID()));
                            intent.putExtra("InstituteID", selectedStudent.getString("InstituteID").equalsIgnoreCase("null")? "0":selectedStudent.getString("InstituteID"));
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    Toast.makeText(getActivity(),"Please check your internet connection and select again!!! ",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        SessionDataGetRequest();
        ShiftDataGetRequest();
        MediumDataGetRequest();
        return rootView;
    }

    private void SessionDataGetRequest() {
        if (StaticHelperClass.isNetworkAvailable(getActivity())) {
            String sessionUrl = getString(R.string.baseUrl)+"/api/onEms/getallsession";

            mSessionDialog = new ProgressDialog(getActivity());
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
                    Toast.makeText(getActivity(),"Session not found!!! ",
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
            MySingleton.getInstance(getActivity()).addToRequestQueue(stringSessionRequest);
        } else {
            Toast.makeText(getActivity(),"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    void parseSessionJsonData(String jsonString) {
        try {
            allSessionArrayList = new ArrayList<>();
            JSONArray sessionJsonArray = new JSONArray(jsonString);
            ArrayList<String> sessionArrayList = new ArrayList<>();
            sessionArrayList.add("Select Session");
            for(int i = 0; i < sessionJsonArray.length(); ++i) {
                JSONObject sessionJsonObject = sessionJsonArray.getJSONObject(i);
                SessionModel sessionModel = new SessionModel(sessionJsonObject.getString("SessionID"), sessionJsonObject.getString("SessionName"));
                allSessionArrayList.add(sessionModel);
                sessionArrayList.add(sessionModel.getSessionName());
            }
//            if(allSessionArrayList.size() == 1){
//                selectedSession = allSessionArrayList.get(0);
//            }
            try {
                String[] strings = new String[sessionArrayList.size()];
                strings = sessionArrayList.toArray(strings);
                ArrayAdapter<String> session_spinner_adapter = new ArrayAdapter<>(getActivity(),R.layout.spinner_item, strings);
                session_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerSession.setAdapter(session_spinner_adapter);
            } catch (IndexOutOfBoundsException e) {
                Toast.makeText(getActivity(),"No section found !!!",Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            Toast.makeText(getActivity(),""+e,Toast.LENGTH_LONG).show();
        }
        mSessionDialog.dismiss();
    }

    private void ShiftDataGetRequest() {
        if (StaticHelperClass.isNetworkAvailable(getActivity())) {
            String shiftUrl = getString(R.string.baseUrl)+"/api/onEms/getInsShift/"+InstituteID;

            mShiftDialog = new ProgressDialog(getActivity());
            mShiftDialog.setTitle("Loading shift...");
            mShiftDialog.setMessage("Please Wait...");
            mShiftDialog.setCancelable(true);
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
                    Toast.makeText(getActivity(),"Shift not found!!! ",
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
            MySingleton.getInstance(getActivity()).addToRequestQueue(stringShiftRequest);
        } else {
            Toast.makeText(getActivity(),"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
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
                allShiftArrayList.add(shiftModel);
                shiftArrayList.add(shiftModel.getShiftName());
            }
//            if(allShiftArrayList.size() == 1){
//                selectedShift = allShiftArrayList.get(0);
//            }
            try {
                String[] strings = new String[shiftArrayList.size()];
                strings = shiftArrayList.toArray(strings);
                ArrayAdapter<String> shift_spinner_adapter = new ArrayAdapter<>(getActivity(),R.layout.spinner_item, strings);
                shift_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerShift.setAdapter(shift_spinner_adapter);
            } catch (IndexOutOfBoundsException e) {
                Toast.makeText(getActivity(),"No shift found !!!",Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            Toast.makeText(getActivity(),""+e,Toast.LENGTH_LONG).show();
        }
        mShiftDialog.dismiss();
    }

    private void MediumDataGetRequest() {
        if(StaticHelperClass.isNetworkAvailable(getActivity())) {
            String mediumUrl = getString(R.string.baseUrl)+"/api/onEms/getInstituteMediumDdl/"+InstituteID;

            mMediumDialog = new ProgressDialog(getActivity());
            mMediumDialog.setTitle("Loading medium...");
            mMediumDialog.setMessage("Please Wait...");
            mMediumDialog.setCancelable(true);
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
                    Toast.makeText(getActivity(),"Medium not found!!! ",
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
            MySingleton.getInstance(getActivity()).addToRequestQueue(stringMediumRequest);
        } else {
            Toast.makeText(getActivity(),"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
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
//            if(allMediumArrayList.size() == 1){
//                selectedMedium = allMediumArrayList.get(0);
//                ClassDataGetRequest();
//            }
            try {
                String[] strings = new String[mediumnArrayList.size()];
                strings = mediumnArrayList.toArray(strings);
                ArrayAdapter<String> medium_spinner_adapter = new ArrayAdapter<>(getActivity(),R.layout.spinner_item, strings);
                medium_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerMedium.setAdapter(medium_spinner_adapter);
            } catch (IndexOutOfBoundsException e) {
                Toast.makeText(getActivity(),"No medium found !!!",Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            Toast.makeText(getActivity(),""+e,Toast.LENGTH_LONG).show();
        }
        mMediumDialog.dismiss();
    }

    private void ClassDataGetRequest() {
        if(StaticHelperClass.isNetworkAvailable(getActivity())) {

            CheckSelectedData();

            String classUrl = getString(R.string.baseUrl)+"/api/onEms/MediumWiseClassDDL/"+InstituteID+"/"+selectedMedium.getMediumID();

            mClassDialog = new ProgressDialog(getActivity());
            mClassDialog.setTitle("Loading class...");
            mClassDialog.setMessage("Please Wait...");
            mClassDialog.setCancelable(true);
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
                    Toast.makeText(getActivity(),"Class not found!!! ",
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
            MySingleton.getInstance(getActivity()).addToRequestQueue(stringClassRequest);
        } else {
            Toast.makeText(getActivity(),"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
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
//            if(allClassArrayList.size() == 1) {
//                selectedClass = allClassArrayList.get(0);
//                DepartmentDataGetRequest();
//            }
            try {
                String[] strings = new String[classArrayList.size()];
                strings = classArrayList.toArray(strings);
                ArrayAdapter<String> class_spinner_adapter = new ArrayAdapter<>(getActivity(),R.layout.spinner_item, strings);
                class_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerClass.setAdapter(class_spinner_adapter);
            } catch (IndexOutOfBoundsException e) {
                Toast.makeText(getActivity(),"No class found !!!",Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            Toast.makeText(getActivity(),""+e,Toast.LENGTH_LONG).show();
        }
        mClassDialog.dismiss();
    }

    private void DepartmentDataGetRequest() {
        if(StaticHelperClass.isNetworkAvailable(getActivity())) {

            CheckSelectedData();

            String departmentUrl = getString(R.string.baseUrl)+"/api/onEms/ClassWiseDepartmentDDL/"+InstituteID+"/"+
                    selectedClass.getClassID()+"/"+selectedMedium.getMediumID();

            mDepartmentDialog = new ProgressDialog(getActivity());
            mDepartmentDialog.setTitle("Loading department...");
            mDepartmentDialog.setMessage("Please Wait...");
            mDepartmentDialog.setCancelable(true);
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
                    Toast.makeText(getActivity(),"Department not found!!! ",
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
            MySingleton.getInstance(getActivity()).addToRequestQueue(stringDepartmentRequest);
        } else {
            Toast.makeText(getActivity(),"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
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
//            if(allDepartmentArrayList.size() == 1){
//                selectedDepartment = allDepartmentArrayList.get(0);
//                SectionDataGetRequest();
//            }
            if(allDepartmentArrayList.size() == 0){
                SectionDataGetRequest();
            }
            try {
                String[] strings = new String[departmentArrayList.size()];
                strings = departmentArrayList.toArray(strings);
                ArrayAdapter<String> department_spinner_adapter = new ArrayAdapter<>(getActivity(),R.layout.spinner_item, strings);
                department_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerDepartment.setAdapter(department_spinner_adapter);
            } catch (IndexOutOfBoundsException e) {
                Toast.makeText(getActivity(),"No department found !!!",Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            Toast.makeText(getActivity(),""+e,Toast.LENGTH_LONG).show();
        }
        mDepartmentDialog.dismiss();
    }

    private void SectionDataGetRequest() {
        if(StaticHelperClass.isNetworkAvailable(getActivity())) {

            CheckSelectedData();

            String sectionUrl = getString(R.string.baseUrl)+"/api/onEms/getInsSection/"+InstituteID+"/"+
                    selectedClass.getClassID()+"/"+selectedDepartment.getDepartmentID();

            mSectionDialog = new ProgressDialog(getActivity());
            mSectionDialog.setTitle("Loading section...");
            mSectionDialog.setMessage("Please Wait...");
            mSectionDialog.setCancelable(true);
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
                    Toast.makeText(getActivity(),"Section not found!!! ",
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
            MySingleton.getInstance(getActivity()).addToRequestQueue(stringSectionRequest);
        } else {
            Toast.makeText(getActivity(),"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
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
//            if(allSectionArrayList.size() == 1){
//                selectedSection = allSectionArrayList.get(0);
//            }
            try {
                String[] strings = new String[sectionArrayList.size()];
                strings = sectionArrayList.toArray(strings);
                ArrayAdapter<String> section_spinner_adapter = new ArrayAdapter<>(getActivity(),R.layout.spinner_item, strings);
                section_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerSection.setAdapter(section_spinner_adapter);
            } catch (IndexOutOfBoundsException e) {
                Toast.makeText(getActivity(),"No section found !!!",Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            Toast.makeText(getActivity(),""+e,Toast.LENGTH_LONG).show();
        }
        mSectionDialog.dismiss();
    }

    private void ExamDataGetRequest() {
        if (StaticHelperClass.isNetworkAvailable(getActivity())) {

            String examUrl = getString(R.string.baseUrl)+"/api/onEms/getClassWiseInsExame/"
                    +InstituteID+"/"+selectedMedium.getMediumID()+"/"+selectedClass.getClassID();

            mExamDialog = new ProgressDialog(getActivity());
            mExamDialog.setTitle("Loading exam...");
            mExamDialog.setMessage("Please Wait...");
            mExamDialog.setCancelable(false);
            mExamDialog.setIcon(R.drawable.onair);
            mExamDialog.show();
            //Preparing Shift data from server
            StringRequest stringExamRequest = new StringRequest(Request.Method.GET, examUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            parseExamJsonData(response);

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mExamDialog.dismiss();
                    Toast.makeText(getActivity(),"Exam not found!!! ",
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
            MySingleton.getInstance(getActivity()).addToRequestQueue(stringExamRequest);
        } else {
            Toast.makeText(getActivity(),"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    void parseExamJsonData(String jsonString) {
        try {
            allExamArrayList = new ArrayList<>();
            JSONArray examJsonArray = new JSONArray(jsonString);
            ArrayList<String> examArrayList = new ArrayList<>();
            examArrayList.add("Select Exam");
            for(int i = 0; i < examJsonArray.length(); ++i) {
                JSONObject examJsonObject = examJsonArray.getJSONObject(i);
                if(Integer.parseInt(examJsonObject.getString("IsActive")) == 1) {
                    ExamModel examModel = new ExamModel(examJsonObject.getString("ExamID"), examJsonObject.getString("ExamName"),
                            examJsonObject.getString("InsExamID"), examJsonObject.getString("Sequence")
                            , examJsonObject.getString("IsActive"));
                    allExamArrayList.add(examModel);
                    examArrayList.add(examModel.getExamName());
                }
            }
//            if(allExamArrayList.size() == 1){
//                selectedExam = allExamArrayList.get(0);
//            }
            try {
                String[] strings = new String[examArrayList.size()];
                strings = examArrayList.toArray(strings);
                ArrayAdapter<String> exam_spinner_adapter = new ArrayAdapter<>(getActivity(),R.layout.spinner_item, strings);
                exam_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerExam.setAdapter(exam_spinner_adapter);
            } catch (IndexOutOfBoundsException e) {
                Toast.makeText(getActivity(),"No section found !!!",Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            Toast.makeText(getActivity(),""+e,Toast.LENGTH_LONG).show();
        }
        mExamDialog.dismiss();
    }

    private void GetStudentListPostRequest() {
        if(StaticHelperClass.isNetworkAvailable(getActivity())) {
            mStudentListGetDialog = new ProgressDialog(getActivity());
            mStudentListGetDialog.setTitle("Loading student...");
            mStudentListGetDialog.setMessage("Please Wait...");
            mStudentListGetDialog.setCancelable(false);
            mStudentListGetDialog.setIcon(R.drawable.onair);
            mStudentListGetDialog.show();

            String getStudentDataPostUrl = getString(R.string.baseUrl)+"/api/onEms/getStudentList";

            //Preparing Student data from server
            StringRequest stringStudentListPostRequest = new StringRequest(Request.Method.POST, getStudentDataPostUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            parseStudentJsonData(response);

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mStudentListGetDialog.dismiss();
                    Toast.makeText(getActivity(),"Student not found!!! ",
                            Toast.LENGTH_LONG).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    CheckSelectedData();
                    Map<String, String>  params = new HashMap<>();
                    params.put("pageNumber", "0");
                    params.put("pageSize", "10000");
                    params.put("IsPaging", "0");
                    params.put("InstituteID", Long.toString(InstituteID));
                    params.put("LoggedUserID", LoggedUserID);
                    params.put("UserID", "0");
                    params.put("ClassID", Long.toString(selectedClass.getClassID()));
                    params.put("SectionID", Long.toString(selectedSection.getSectionID()));
                    params.put("DepartmentID", Long.toString(selectedDepartment.getDepartmentID()));
                    params.put("MediumID", Long.toString(selectedMedium.getMediumID()));
                    params.put("ShiftID", Long.toString(selectedShift.getShiftID()));

                    return params;
                }
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String>  params = new HashMap<>();
                    params.put("Authorization", "Request_From_onEMS_Android_app");
                    return params;
                }
            };
            MySingleton.getInstance(getActivity()).addToRequestQueue(stringStudentListPostRequest);

        } else {
            Toast.makeText(getActivity(),"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    void parseStudentJsonData(String jsonString) {
        try {
            allStudentJsonArray = new JSONArray();
            JSONArray studentJsonArray = new JSONArray(jsonString);
            ArrayList<String> studentArrayList = new ArrayList<>();
            studentArrayList.add("Select Student");
            for(int i = 0; i < studentJsonArray.length(); ++i) {
                JSONObject studentJsonObject = studentJsonArray.getJSONObject(i);
                studentArrayList.add(studentJsonObject.getString("RollNo")+" - "+studentJsonObject.getString("UserName"));
                allStudentJsonArray.put(studentJsonObject);
            }
            try {
                String[] strings = new String[studentArrayList.size()];
                strings = studentArrayList.toArray(strings);
                ArrayAdapter<String> student_spinner_adapter = new ArrayAdapter<>(getActivity(),R.layout.spinner_item, strings);
                student_spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerStudent.setAdapter(student_spinner_adapter);
            } catch (IndexOutOfBoundsException e) {
                Toast.makeText(getActivity(),"No student found !!!",Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            Toast.makeText(getActivity(),""+e,Toast.LENGTH_LONG).show();
        }
        mStudentListGetDialog.dismiss();
    }

    private void CheckSelectedData(){
        if(selectedClass.getClassID() == -2) {
            selectedClass.setClassID("0");
        }
        if(selectedShift.getShiftID() == -2) {
            selectedShift.setShiftID("0");
        }
        if(selectedSection.getSectionID() == -2) {
            selectedSection.setSectionID("0");
        }
        if(selectedMedium.getMediumID() == -2) {
            selectedMedium.setMediumID("0");
        }
        if(selectedDepartment.getDepartmentID() == -2) {
            selectedDepartment.setDepartmentID("0");
        }
    }
}
