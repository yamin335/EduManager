package onair.onems.syllabus;

import android.Manifest;
import android.app.DownloadManager;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import onair.onems.R;
import onair.onems.Services.StaticHelperClass;
import onair.onems.mainactivities.SideNavigationMenuParentActivity;
import onair.onems.mainactivities.StudentMainScreen;
import onair.onems.mainactivities.TeacherMainScreen;
import onair.onems.network.MySingleton;

import static onair.onems.Services.StaticHelperClass.dpToPx;
import static onair.onems.Services.StaticHelperClass.isNetworkAvailable;

public class SyllabusMainScreen extends SideNavigationMenuParentActivity implements ExamAdapter.ExamAdapterListener, SubjectAdapter.SubjectAdapterListener,
        FloatingMenuDialog.FloatingMenuListener, DigitalContentAdapter.AddFileToDownloader{

    private ProgressDialog mExamDialog, mSyllabusDialog, mSubjectDialog, mContentDialog, mLessonContentDialog;
    private String selectedSubjectID = "", selectedExamID = "", selectedDate = "";
    private TextView error, lessonError, topicValue, detailValue, syllabusTime;
    private View shadow;
    private FloatingActionButton floatingMenu;
    private ArrayList<Long> refIdList = new ArrayList<>();
    private ArrayList<JSONObject> digitalContentUrls;
    private DigitalContentAdapter mAdapter;
    private DigitalContentAdapter mLessonAdapter;
    private ArrayList<JSONObject> lessonDigitalContentUrls;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityName = SyllabusMainScreen.class.getName();

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.syllabus_main_screen, null);
        LinearLayout parent = (LinearLayout) findViewById(R.id.contentMain);
        parent.addView(rowView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        intent = getIntent();

        error = findViewById(R.id.empty);
        lessonError = findViewById(R.id.lessonEmpty);
        topicValue = findViewById(R.id.topicValue);
        detailValue = findViewById(R.id.detailsValue);
        syllabusTime = findViewById(R.id.date);
        RecyclerView recyclerView = findViewById(R.id.recycler);
        RecyclerView lessonRecyclerView = findViewById(R.id.lessonRecycler);
        shadow = findViewById(R.id.dim);
        shadow.setVisibility(View.GONE);

        digitalContentUrls = new ArrayList<>();
        lessonDigitalContentUrls = new ArrayList<>();
        mAdapter = new DigitalContentAdapter(this, this, digitalContentUrls, DigitalContentAdapter.ContentType.SYLLABUS);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
//        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 4);
//        recyclerView.addItemDecoration(new GridSpacingItemDecoration(4, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        mLessonAdapter = new DigitalContentAdapter(this, this, lessonDigitalContentUrls, DigitalContentAdapter.ContentType.LESSON);
        RecyclerView.LayoutManager mLessonLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        lessonRecyclerView.setLayoutManager(mLessonLayoutManager);
        lessonRecyclerView.setItemAnimator(new DefaultItemAnimator());
        lessonRecyclerView.setAdapter(mLessonAdapter);

        floatingMenu = findViewById(R.id.showDate);
        floatingMenu.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF5722")));
        floatingMenu.setRippleColor(Color.parseColor("#e50b00"));

//        if (UserTypeID == 1 || UserTypeID == 2 || UserTypeID == 4) {
//
//            floatingMenu.setVisibility(View.GONE);
//            floatingMenu.setClickable(false);
//        }

        floatingMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatingMenu.setImageResource(R.drawable.ic_clear);
                shadow.setVisibility(View.VISIBLE);
                FloatingMenuDialog floatingMenuDialog = new FloatingMenuDialog(SyllabusMainScreen.this,
                        SyllabusMainScreen.this, SyllabusMainScreen.this);
                floatingMenuDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                floatingMenuDialog.setCancelable(true);
                floatingMenuDialog.getWindow().getAttributes().gravity = Gravity.TOP | Gravity.END;
                floatingMenuDialog.getWindow().getAttributes().x = dpToPx(5);
                floatingMenuDialog.getWindow().getAttributes().y = dpToPx(113);
                floatingMenuDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                floatingMenuDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        shadow.setVisibility(View.GONE);
                        floatingMenu.setImageResource(R.drawable.ic_keyboard_arrow_down);
                    }
                });
                floatingMenuDialog.show();
            }
        });

        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");
        selectedDate = df.format(date);

        if (UserTypeID == 1 || UserTypeID == 2 || UserTypeID == 4){
            LoggedUserMediumID = intent.getLongExtra("MediumID", 0);
            LoggedUserClassID = intent.getLongExtra("ClassID", 0);
            LoggedUserDepartmentID = intent.getLongExtra("DepartmentID", 0);
            LoggedUserSectionID = intent.getLongExtra("SectionID", 0);
            selectedSubjectID = Long.toString(intent.getLongExtra("SubjectID", 0));
            selectedExamID = Long.toString(intent.getLongExtra("ExamID", 0));
            selectedDate = intent.getStringExtra("Date");
        } else if(UserTypeID == 5) {
            try {
                JSONObject selectedStudent = new JSONObject(getSharedPreferences("CURRENT_STUDENT", Context.MODE_PRIVATE)
                        .getString("guardianSelectedStudent", "{}"));
                LoggedUserMediumID = selectedStudent.getLong("MediumID");
                LoggedUserClassID = selectedStudent.getLong("ClassID");
                LoggedUserDepartmentID = selectedStudent.getLong("DepartmentID");
                LoggedUserSectionID = selectedStudent.getLong("SectionID");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (UserTypeID != 1 && UserTypeID != 2 && UserTypeID != 4) {
            examDataGetRequest();
        } else {
            syllabusDataGetRequest(intent.getStringExtra("Date"));
        }
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    BroadcastReceiver onComplete = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {

            // get the refId from the download manager
            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

            // Open downloaded file
            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(referenceId);
            Cursor cursor = downloadManager.query(query);
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
                        new NotificationCompat.Builder(SyllabusMainScreen.this)
                                .setSmallIcon(R.drawable.onair)
                                .setContentTitle("Download Completed")
                                .setContentText("All Downloads are completed");


                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(455, mBuilder.build());
            }
        }
    };

    private void examDataGetRequest() {
        if (StaticHelperClass.isNetworkAvailable(this)) {

            String examUrl = getString(R.string.baseUrl)+"/api/onEms/getInsExamforDDL/"+InstituteID+
                    "/"+LoggedUserMediumID+"/"+LoggedUserClassID;

            mExamDialog = new ProgressDialog(this);
            mExamDialog.setTitle("Loading exam...");
            mExamDialog.setMessage("Please Wait...");
            mExamDialog.setCancelable(false);
            mExamDialog.setIcon(R.drawable.onair);
            mExamDialog.show();

            //Preparing exam
            StringRequest request = new StringRequest(Request.Method.GET, examUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            parseExamData(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mExamDialog.dismiss();
                    Toast.makeText(SyllabusMainScreen.this,"Exam data not found!!! ",
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
            MySingleton.getInstance(this).addToRequestQueue(request);
        } else {
            Toast.makeText(SyllabusMainScreen.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void parseExamData(String examData) {
        if(!examData.equalsIgnoreCase("[]")) {
            ExamSelectionDialog examSelectionDialog = new ExamSelectionDialog(this, examData, this);
            examSelectionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            examSelectionDialog.setCancelable(false);
            examSelectionDialog.show();
        } else {
            digitalContentUrls.clear();
            mAdapter.notifyDataSetChanged();
            error.setVisibility(View.VISIBLE);
            lessonDigitalContentUrls.clear();
            mLessonAdapter.notifyDataSetChanged();
            lessonError.setVisibility(View.VISIBLE);
            Toast.makeText(SyllabusMainScreen.this,"Exam data not found!!! ",
                    Toast.LENGTH_LONG).show();
        }
        mExamDialog.dismiss();
    }

    @Override
    public void onExamSelected(JSONObject exam) {
        try {
            selectedExamID = exam.getString("ExamID");
            subjectDataGetRequest();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void subjectDataGetRequest() {
        if (StaticHelperClass.isNetworkAvailable(this)) {

            String subjectUrl = getString(R.string.baseUrl)+"/api/onEms/GetClassWiseSubject/"
                    +InstituteID+"/"+LoggedUserDepartmentID
                    +"/"+LoggedUserMediumID+"/"+LoggedUserClassID;

            mSubjectDialog = new ProgressDialog(this);
            mSubjectDialog.setTitle("Loading subject...");
            mSubjectDialog.setMessage("Please Wait...");
            mSubjectDialog.setCancelable(false);
            mSubjectDialog.setIcon(R.drawable.onair);
            mSubjectDialog.show();

            //Preparing subject
            StringRequest request = new StringRequest(Request.Method.GET, subjectUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            parseSubjectData(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mSubjectDialog.dismiss();
                    Toast.makeText(SyllabusMainScreen.this,"Subject data not found!!! ",
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
            MySingleton.getInstance(this).addToRequestQueue(request);
        } else {
            Toast.makeText(this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void parseSubjectData(String subjectData) {
        if(!subjectData.equalsIgnoreCase("[]")) {
            SubjectSelectionDialog subjectSelectionDialog = new SubjectSelectionDialog(this, subjectData, this);
            subjectSelectionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            subjectSelectionDialog.setCancelable(false);
            subjectSelectionDialog.show();
        } else {
            digitalContentUrls.clear();
            mAdapter.notifyDataSetChanged();
            error.setVisibility(View.VISIBLE);
            lessonDigitalContentUrls.clear();
            mLessonAdapter.notifyDataSetChanged();
            lessonError.setVisibility(View.VISIBLE);
            Toast.makeText(SyllabusMainScreen.this,"Subject data not found!!! ",
                    Toast.LENGTH_LONG).show();
        }
        mSubjectDialog.dismiss();
    }

    @Override
    public void onSubjectSelected(JSONObject subject) {
        try {
            selectedSubjectID = subject.getString("SubjectID");
            if(selectedExamID!=null && selectedSubjectID!=null) {
                syllabusDataGetRequest(selectedDate);
            } else {
                Toast.makeText(SyllabusMainScreen.this,"Select exam and subject!!! ",
                        Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void syllabusDataGetRequest(String date) {
        if (StaticHelperClass.isNetworkAvailable(this)) {
            String syllabusUrl = getString(R.string.baseUrl)+"/api/onEms/getAcademicClassDayForMySyllabus/"+
                    InstituteID+"/"+LoggedUserMediumID+"/"+LoggedUserClassID+"/"+LoggedUserDepartmentID+"/"+
                    LoggedUserSectionID+"/"+selectedSubjectID+"/"+selectedExamID+"/"+date+"/"+date;

            mSyllabusDialog = new ProgressDialog(this);
            mSyllabusDialog.setTitle("Loading syllabus...");
            mSyllabusDialog.setMessage("Please Wait...");
            mSyllabusDialog.setCancelable(false);
            mSyllabusDialog.setIcon(R.drawable.onair);
            mSyllabusDialog.show();

            //Preparing syllabus
            StringRequest request = new StringRequest(Request.Method.GET, syllabusUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            parseSyllabusData(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mSyllabusDialog.dismiss();
                    Toast.makeText(SyllabusMainScreen.this,"Syllabus data not found!!! ",
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
            MySingleton.getInstance(this).addToRequestQueue(request);
        } else {
            Toast.makeText(SyllabusMainScreen.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void parseSyllabusData(String syllabus) {
        try {
            if(!syllabus.equalsIgnoreCase("[]")) {
                JSONObject jsonObject = new JSONArray(syllabus).getJSONObject(0);
                topicValue.setText(jsonObject.getString("Topic"));
                detailValue.setText(jsonObject.getString("TopicDetail"));
                syllabusTime.setText(jsonObject.getString("ClassDate")+", "+jsonObject.getString("ClassDay"));
                syllabusDigitalContentGetRequest(jsonObject.getString("SyllabusID"));
                lessonPlanDigitalContentGetRequest(jsonObject.getString("SyllabusID"), jsonObject.getString("SyllabusDetailID"));
            } else {
                topicValue.setText("");
                detailValue.setText("");
                syllabusTime.setText("");
                digitalContentUrls.clear();
                mAdapter.notifyDataSetChanged();
                error.setVisibility(View.VISIBLE);
                lessonDigitalContentUrls.clear();
                mLessonAdapter.notifyDataSetChanged();
                lessonError.setVisibility(View.VISIBLE);
                Toast.makeText(SyllabusMainScreen.this,"Syllabus data not found!!! ",
                        Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSyllabusDialog.dismiss();
    }

    private void syllabusDigitalContentGetRequest(String SyllabusID) {
        if (StaticHelperClass.isNetworkAvailable(this)) {

            String url = getString(R.string.baseUrl)+"/api/onEms/getUrlMasterByID/"+SyllabusID;

            mContentDialog = new ProgressDialog(this);
            mContentDialog.setTitle("Loading digital content...");
            mContentDialog.setMessage("Please Wait...");
            mContentDialog.setCancelable(false);
            mContentDialog.setIcon(R.drawable.onair);
//            mContentDialog.show();

            //Preparing digital content
            StringRequest request = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if(!response.equalsIgnoreCase("[]")){
                                parseDigitalContent(response);
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mContentDialog.dismiss();
                    Toast.makeText(SyllabusMainScreen.this,"Digital content not found!!! ",
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
            MySingleton.getInstance(this).addToRequestQueue(request);
        } else {
            Toast.makeText(SyllabusMainScreen.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void parseDigitalContent(String digitalContents){
        try {
            if(!digitalContents.equalsIgnoreCase("[]")) {
                error.setVisibility(View.GONE);
                JSONArray jsonArray = new JSONArray(digitalContents);
                digitalContentUrls.clear();
                for (int i = 0; i<jsonArray.length(); i++) {
                    digitalContentUrls.add(jsonArray.getJSONObject(i));
                }
                mAdapter.notifyDataSetChanged();
            } else {
                error.setVisibility(View.VISIBLE);
                digitalContentUrls.clear();
                mAdapter.notifyDataSetChanged();
                Toast.makeText(SyllabusMainScreen.this,"Digital content not found!!! ",
                        Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mContentDialog.dismiss();
    }

    private void lessonPlanDigitalContentGetRequest(String SyllabusID, String SyllabusDetailID) {
        if (StaticHelperClass.isNetworkAvailable(this)) {

            String url = getString(R.string.baseUrl)+"/api/onEms/getUrlTopicDetailByID/"+SyllabusID+"/"+SyllabusDetailID;

            mLessonContentDialog = new ProgressDialog(this);
            mLessonContentDialog.setTitle("Loading digital content...");
            mLessonContentDialog.setMessage("Please Wait...");
            mLessonContentDialog.setCancelable(false);
            mLessonContentDialog.setIcon(R.drawable.onair);
//            mLessonContentDialog.show();

            //Preparing digital content
            StringRequest request = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if(!response.equalsIgnoreCase("[]")){
                                parseLessonDigitalContent(response);
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mLessonContentDialog.dismiss();
                    Toast.makeText(SyllabusMainScreen.this,"Lesson plan digital content not found!!! ",
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
            MySingleton.getInstance(this).addToRequestQueue(request);
        } else {
            Toast.makeText(SyllabusMainScreen.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void parseLessonDigitalContent(String digitalContents){
        try {
            if(!digitalContents.equalsIgnoreCase("[]")) {
                lessonError.setVisibility(View.GONE);
                JSONArray jsonArray = new JSONArray(digitalContents);
                lessonDigitalContentUrls.clear();
                for (int i = 0; i<jsonArray.length(); i++) {
                    lessonDigitalContentUrls.add(jsonArray.getJSONObject(i));
                }
                mLessonAdapter.notifyDataSetChanged();
            } else {
                lessonError.setVisibility(View.VISIBLE);
                lessonDigitalContentUrls.clear();
                mLessonAdapter.notifyDataSetChanged();
                Toast.makeText(SyllabusMainScreen.this,"Lesson plan digital content not found!!! ",
                        Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mLessonContentDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(UserTypeID == 1) {
            Intent mainIntent = new Intent(SyllabusMainScreen.this, SyllabusMainScreenForAdmin.class);
            startActivity(mainIntent);
            finish();
        } else if(UserTypeID == 2) {
            Intent mainIntent = new Intent(SyllabusMainScreen.this, SyllabusMainScreenForAdmin.class);
            startActivity(mainIntent);
            finish();
        } else if(UserTypeID == 3) {
            Intent mainIntent = new Intent(SyllabusMainScreen.this, StudentMainScreen.class);
            startActivity(mainIntent);
            finish();
        } else if(UserTypeID == 4) {
            Intent mainIntent = new Intent(SyllabusMainScreen.this, SyllabusMainScreenForAdmin.class);
            startActivity(mainIntent);
            finish();
        } else if(UserTypeID == 5) {
            Intent mainIntent = new Intent(SyllabusMainScreen.this, StudentMainScreen.class);
            startActivity(mainIntent);
            finish();
        }
    }

    @Override
    public void onFloatingMenuItemSelected(int menuId, String date) {
        if(menuId == R.id.selectExam) {
            examDataGetRequest();
        } else if(menuId == R.id.selectDate) {
            selectedDate = date;
            syllabusDataGetRequest(selectedDate);
        }
    }

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

    @Override
    public void downloadFile(String url) {
        if(isNetworkAvailable(this)){
            if(isPermissionAllowed()){
                try {
                    String s[] = url.split("/");
                    String file = s[s.length-1];
                    String fileName = file.split("\\.")[0]+getFileExtension(url);

                    // get download service
                    DownloadManager manager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(getString(R.string.baseUrl)+"/"+url));
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
                    long refId = manager.enqueue(request);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(onComplete);
    }

    private void openFile(String filePath) throws IOException {
        File file = new File(filePath);
        // Create URI
//        Uri uri = FileProvider.getUriForFile(SyllabusMainScreen.this,
//                BuildConfig.APPLICATION_ID + ".provider",
//                file);
        Uri uri = Uri.parse(filePath);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        // Check what kind of file you are trying to open, by comparing the url with extensions.
        // When the if condition is matched, plugin sets the correct intent (mime) type,
        // so Android knew what application to use to open the file
        if (filePath.contains(".doc") || filePath.contains(".docx")) {
            // Word document
            intent.setDataAndType(uri, "application/msword");
        } else if(filePath.contains(".pdf")) {
            // PDF file
            intent.setDataAndType(uri, "application/pdf");
        } else if(filePath.contains(".ppt") || filePath.contains(".pptx")) {
            // Powerpoint file
            intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        } else if(filePath.contains(".xls") || filePath.contains(".xlsx")) {
            // Excel file
            intent.setDataAndType(uri, "application/vnd.ms-excel");
        } else if(filePath.contains(".zip") || filePath.contains(".rar")) {
            // WAV audio file
            intent.setDataAndType(uri, "application/x-wav");
        } else if(filePath.contains(".rtf")) {
            // RTF file
            intent.setDataAndType(uri, "application/rtf");
        } else if(filePath.contains(".wav") || filePath.contains(".mp3")) {
            // WAV audio file
            intent.setDataAndType(uri, "audio/x-wav");
        } else if(filePath.contains(".gif")) {
            // GIF file
            intent.setDataAndType(uri, "image/gif");
        } else if(filePath.contains(".jpg") || filePath.contains(".jpeg") || filePath.contains(".png")) {
            // JPG file
            intent.setDataAndType(uri, "image/jpeg");
        } else if(filePath.contains(".txt")) {
            // Text file
            intent.setDataAndType(uri, "text/plain");
        } else if(filePath.contains(".3gp") || filePath.contains(".mpg") || filePath.contains(".mpeg") || filePath.contains(".mpe") || filePath.contains(".mp4") || filePath.contains(".avi")) {
            // Video files
            intent.setDataAndType(uri, "video/*");
        } else {
            //if you want you can also define the intent type for any other file

            //additionally use else clause below, to manage other unknown extensions
            //in this case, Android will show all applications installed on the device
            //so you can choose which application to use
            intent.setDataAndType(uri, "*/*");
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
    }

}
