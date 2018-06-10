package onair.onems.lesson_plan;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.DownloadManager;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import onair.onems.R;
import onair.onems.Services.StaticHelperClass;
import onair.onems.homework.HomeworkAdapter;
import onair.onems.homework.HomeworkMainScreen;
import onair.onems.mainactivities.SideNavigationMenuParentActivity;
import onair.onems.mainactivities.StudentMainScreen;
import onair.onems.mainactivities.TeacherMainScreen;
import onair.onems.network.MySingleton;
import onair.onems.syllabus.DigitalContentAdapter;
import onair.onems.syllabus.SyllabusMainScreen;

import static onair.onems.Services.StaticHelperClass.isNetworkAvailable;

public class LessonPlanMainScreen extends SideNavigationMenuParentActivity implements LessonPlanAdapter.LessonPlanAdapterListener,
        DigitalContentAdapter.AddFileToDownloader{

    private Button datePicker;
    private TextView topicTitle, topicDetails, lessonDigitalEmpty, lessonPlanEmpty, digitalContentEmpty, homeworkDate;
    private RecyclerView lessonPlanDigitalContentRecycler, lessonPlanRecycler, digitalContentRecycler;
    private ArrayList<Long> refIdList = new ArrayList<>();
    private ArrayList<JSONObject> digitalContentList, lessonPlanList, lessonPlanDigitalContentList;
    private DigitalContentAdapter mDigitalAdapter, mLessonPlanDigitalAdapter;
    private LessonPlanAdapter mAdapter;
    private DatePickerDialog datePickerDialog;
    private ProgressDialog mDigitalDialog, mLessonDialog, mLessonDigitalDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityName = LessonPlanMainScreen.class.getName();

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.lesson_plan_main_screen, null);
        LinearLayout parent = (LinearLayout) findViewById(R.id.contentMain);
        parent.addView(rowView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        mDigitalDialog = new ProgressDialog(this);
        mDigitalDialog.setTitle("Loading digital content...");
        mDigitalDialog.setMessage("Please Wait...");
        mDigitalDialog.setCancelable(false);
        mDigitalDialog.setIcon(R.drawable.onair);

        homeworkDate = findViewById(R.id.date);
        datePicker = findViewById(R.id.pickDate);
        topicTitle = findViewById(R.id.topicValue);
        topicDetails = findViewById(R.id.detailsValue);
        lessonDigitalEmpty = findViewById(R.id.lessonDigitalEmpty);
        lessonPlanEmpty = findViewById(R.id.lessonPlanEmpty);
        digitalContentEmpty = findViewById(R.id.digitalContentEmpty);

        digitalContentList = new ArrayList<>();
        lessonPlanList = new ArrayList<>();
        lessonPlanDigitalContentList = new ArrayList<>();

        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");
        String today = df.format(date);

        mAdapter = new LessonPlanAdapter(this, lessonPlanList, this);
        lessonPlanRecycler = findViewById(R.id.lessonRecycler);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        lessonPlanRecycler.setLayoutManager(mLayoutManager);
        lessonPlanRecycler.setItemAnimator(new DefaultItemAnimator());
        lessonPlanRecycler.setAdapter(mAdapter);

        mDigitalAdapter = new DigitalContentAdapter(this, this, digitalContentList, DigitalContentAdapter.ContentType.SYLLABUS);
        digitalContentRecycler = findViewById(R.id.digitalContentRecycler);
        RecyclerView.LayoutManager mDigitalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        digitalContentRecycler.setLayoutManager(mDigitalLayoutManager);
        digitalContentRecycler.setItemAnimator(new DefaultItemAnimator());
        digitalContentRecycler.setAdapter(mDigitalAdapter);

        mLessonPlanDigitalAdapter = new DigitalContentAdapter(this, this, lessonPlanDigitalContentList, DigitalContentAdapter.ContentType.LESSON);
        lessonPlanDigitalContentRecycler = findViewById(R.id.lessonDigitalRecycler);
        RecyclerView.LayoutManager mLessonDigitalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        lessonPlanDigitalContentRecycler.setLayoutManager(mLessonDigitalLayoutManager);
        lessonPlanDigitalContentRecycler.setItemAnimator(new DefaultItemAnimator());
        lessonPlanDigitalContentRecycler.setAdapter(mLessonPlanDigitalAdapter);

        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(LessonPlanMainScreen.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
//                                selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
//                                selectedDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                                String date = (monthOfYear + 1)+"-"+dayOfMonth+"-"+year;
                                datePicker.setText(date);
                                homeworkDate.setText(date);
                                lessonPlanGetRequest(date);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        lessonPlanGetRequest(today);
    }

    private void lessonPlanGetRequest(String date) {
        if (StaticHelperClass.isNetworkAvailable(this)) {

            String lessonPlanUrl = "";
            if(UserTypeID == 3) {
                lessonPlanUrl = getString(R.string.baseUrl)+"/api/onEms/getDateWiseLessonPlan/"+InstituteID+
                        "/"+LoggedUserMediumID+"/"+LoggedUserClassID+"/"+LoggedUserDepartmentID+"/"+
                        LoggedUserSectionID+"/"+date;
            } else if(UserTypeID == 5) {
                try {
                    JSONObject selectedStudent = new JSONObject(getSharedPreferences("CURRENT_STUDENT", Context.MODE_PRIVATE)
                            .getString("guardianSelectedStudent", "{}"));
                    lessonPlanUrl = getString(R.string.baseUrl)+"/api/onEms/getMyInsHomeWork/"+InstituteID+
                            "/"+selectedStudent.getString("MediumID")+"/"+selectedStudent.getString("ClassID")
                            +"/"+selectedStudent.getString("DepartmentID")+"/"+selectedStudent.getString("SectionID")
                            +"/"+date;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            mLessonDialog = new ProgressDialog(this);
            mLessonDialog.setTitle("Loading lesson plan...");
            mLessonDialog.setMessage("Please Wait...");
            mLessonDialog.setCancelable(false);
            mLessonDialog.setIcon(R.drawable.onair);
            mLessonDialog.show();

            //Preparing homework
            StringRequest request = new StringRequest(Request.Method.GET, lessonPlanUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            parseLessonPlan(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mLessonDialog.dismiss();
                    Toast.makeText(LessonPlanMainScreen.this,"Lesson plan not found!!! ",
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

    private void parseLessonPlan(String lessonPlan) {
        if(!lessonPlan.equalsIgnoreCase("[]")) {
            lessonPlanEmpty.setVisibility(View.GONE);
            try {
                JSONArray jsonArray = new JSONArray(lessonPlan);
                lessonPlanList.clear();
                digitalContentList.clear();
                for (int i = 0; i<jsonArray.length(); i++) {
                    lessonPlanList.add(jsonArray.getJSONObject(i));
                    digitalContentGetRequest(jsonArray.getJSONObject(i).getString("SyllabusID"));
                }
                mAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            lessonPlanList.clear();
            mAdapter.notifyDataSetChanged();
            lessonPlanEmpty.setVisibility(View.VISIBLE);
            digitalContentList.clear();
            mDigitalAdapter.notifyDataSetChanged();
            digitalContentEmpty.setVisibility(View.VISIBLE);
            Toast.makeText(this,"Lesson plan not found!!! ",
                    Toast.LENGTH_LONG).show();
        }
        mLessonDialog.dismiss();
    }

    @Override
    public void onLessonPlanSelected(JSONObject lessonPlan) {
        try {
            topicTitle.setText(lessonPlan.getString("Topic"));
            topicDetails.setText(lessonPlan.getString("TopicDetail"));
            lessonDigitalContentGetRequest(lessonPlan.getString("SyllabusDetailID"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void digitalContentGetRequest(String SyllabusID) {
        if (StaticHelperClass.isNetworkAvailable(this)) {

            String digitalContentUrl = getString(R.string.baseUrl)+"/api/onEms/getUrlMasterByID/"+SyllabusID;

            mDigitalDialog.show();

            //Preparing digital content
            StringRequest request = new StringRequest(Request.Method.GET, digitalContentUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            parseDigitalContent(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mDigitalDialog.dismiss();
                    Toast.makeText(LessonPlanMainScreen.this,"Digital content not found!!! ",
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

    private void parseDigitalContent(String digitalContent) {
        if(!digitalContent.equalsIgnoreCase("[]")) {
            digitalContentEmpty.setVisibility(View.GONE);
            try {
                JSONArray jsonArray = new JSONArray(digitalContent);
                for (int i = 0; i<jsonArray.length(); i++) {
                    digitalContentList.add(jsonArray.getJSONObject(i));
                }
                mDigitalAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
//        else {
//            digitalContentList.clear();
//            mDigitalAdapter.notifyDataSetChanged();
//            digitalContentEmpty.setVisibility(View.VISIBLE);
//            Toast.makeText(this,"Digital content not found!!! ",
//                    Toast.LENGTH_LONG).show();
//        }
        mDigitalDialog.dismiss();
    }

    private void lessonDigitalContentGetRequest(String SyllabusDetailID) {
        if (StaticHelperClass.isNetworkAvailable(this)) {

            String digitalContentUrl = getString(R.string.baseUrl)+"/api/onEms/getDateWiseLessonPlanDetail/"+SyllabusDetailID;

            mDigitalDialog.show();

            //Preparing digital content
            StringRequest request = new StringRequest(Request.Method.GET, digitalContentUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            parseLessonDigitalContent(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mDigitalDialog.dismiss();
                    Toast.makeText(LessonPlanMainScreen.this,"Digital content not found!!! ",
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

    private void parseLessonDigitalContent(String digitalContent) {
        if(!digitalContent.equalsIgnoreCase("[]")) {
            lessonDigitalEmpty.setVisibility(View.GONE);
            try {
                JSONArray jsonArray = new JSONArray(digitalContent);
                lessonPlanDigitalContentList.clear();
                for (int i = 0; i<jsonArray.length(); i++) {
                    lessonPlanDigitalContentList.add(jsonArray.getJSONObject(i));
                }
                mLessonPlanDigitalAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            lessonPlanDigitalContentList.clear();
            mLessonPlanDigitalAdapter.notifyDataSetChanged();
            lessonDigitalEmpty.setVisibility(View.VISIBLE);
            Toast.makeText(this,"Digital content not found!!! ",
                    Toast.LENGTH_LONG).show();
        }
        mDigitalDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(UserTypeID == 1) {
            Intent mainIntent = new Intent(LessonPlanMainScreen.this, TeacherMainScreen.class);
            startActivity(mainIntent);
            finish();
        } else if(UserTypeID == 2) {
            Intent mainIntent = new Intent(LessonPlanMainScreen.this, TeacherMainScreen.class);
            startActivity(mainIntent);
            finish();
        } else if(UserTypeID == 3) {
            Intent mainIntent = new Intent(LessonPlanMainScreen.this, StudentMainScreen.class);
            startActivity(mainIntent);
            finish();
        } else if(UserTypeID == 4) {
            Intent mainIntent = new Intent(LessonPlanMainScreen.this, TeacherMainScreen.class);
            startActivity(mainIntent);
            finish();
        } else if(UserTypeID == 5) {
            Intent mainIntent = new Intent(LessonPlanMainScreen.this, StudentMainScreen.class);
            startActivity(mainIntent);
            finish();
        }
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
                        new NotificationCompat.Builder(LessonPlanMainScreen.this)
                                .setSmallIcon(R.drawable.onair)
                                .setContentTitle("Download Completed")
                                .setContentText("All Downloads are completed");


                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(455, mBuilder.build());
            }
        }
    };


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
}
