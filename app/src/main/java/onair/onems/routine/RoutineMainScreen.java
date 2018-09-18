package onair.onems.routine;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
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
import java.util.List;
import java.util.Map;

import onair.onems.R;
import onair.onems.Services.StaticHelperClass;
import onair.onems.mainactivities.SideNavigationMenuParentActivity;
import onair.onems.mainactivities.StudentMainScreen;
import onair.onems.mainactivities.TeacherMainScreen;
import onair.onems.models.ShiftModel;
import onair.onems.network.MySingleton;
import onair.onems.result.ResultMainScreen;
import onair.onems.syllabus.ExamSelectionDialog;

public class RoutineMainScreen extends SideNavigationMenuParentActivity implements ShiftSelectionAdapter.ShiftSelectionListener{

    private ProgressDialog dialog;
    private JSONArray saturdayJsonArray, sundayJsonArray, mondayJsonArray, tuesdayJsonArray, wednesdayJsonArray, thursdayJsonArray, fridayJsonArray;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private FloatingActionButton fabShift;
    private ProgressDialog mShiftDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityName = RoutineMainScreen.class.getName();

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View childActivityLayout = inflater.inflate(R.layout.routine_content_main, null);
        LinearLayout parentActivityLayout = (LinearLayout) findViewById(R.id.contentMain);
        parentActivityLayout.addView(childActivityLayout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        viewPager = (ViewPager) findViewById(R.id.pager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        saturdayJsonArray = new JSONArray();
        sundayJsonArray = new JSONArray();
        mondayJsonArray = new JSONArray();
        tuesdayJsonArray = new JSONArray();
        wednesdayJsonArray = new JSONArray();
        thursdayJsonArray = new JSONArray();
        fridayJsonArray = new JSONArray();

        fabShift = findViewById(R.id.fabShift);
        fabShift.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#9C077EF5")));
        fabShift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShiftDataGetRequest();
            }
        });

        if(UserTypeID==3 || UserTypeID==4 || UserTypeID==5){
            fabShift.setVisibility(View.GONE);
        }

        if (UserTypeID == 1 || UserTypeID == 2) {
            ShiftDataGetRequest();
        }

        if(UserTypeID == 4){
            RoutineDataGetRequest(0);
        }

        if(UserTypeID!=1 && UserTypeID!=2 && UserTypeID!=4){
            setupViewPager(viewPager);
            tabLayout.setupWithViewPager(viewPager);
        }
    }

    @Override
    public void onShiftSelected(JSONObject shift) {
        try {
            RoutineDataGetRequest(shift.getLong("ShiftID"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private List<Fragment> mFragmentList = new ArrayList<>();
        private List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            return super.instantiateItem(container, position);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(UserTypeID == 1) {
            Intent mainIntent = new Intent(RoutineMainScreen.this,TeacherMainScreen.class);
            startActivity(mainIntent);
            finish();
        } else if(UserTypeID == 2) {
            Intent mainIntent = new Intent(RoutineMainScreen.this,TeacherMainScreen.class);
            startActivity(mainIntent);
            finish();
        } else if(UserTypeID == 3) {
            Intent mainIntent = new Intent(RoutineMainScreen.this,StudentMainScreen.class);
            startActivity(mainIntent);
            finish();
        } else if(UserTypeID == 4) {
            Intent mainIntent = new Intent(RoutineMainScreen.this,TeacherMainScreen.class);
            startActivity(mainIntent);
            finish();
        } else if(UserTypeID == 5) {
            Intent mainIntent = new Intent(RoutineMainScreen.this,StudentMainScreen.class);
            startActivity(mainIntent);
            finish();
        }
    }

    private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        if(UserTypeID==1||UserTypeID==2||UserTypeID==4) {
            Bundle saturdayBundle = new Bundle();
            saturdayBundle.putString("saturdayJsonArray", saturdayJsonArray.toString());
            Saturday saturday = new Saturday();
            saturday.setArguments(saturdayBundle);
            adapter.addFragment(saturday, "Sat");

            Bundle sundayBundle = new Bundle();
            sundayBundle.putString("sundayJsonArray", sundayJsonArray.toString());
            Sunday sunday = new Sunday();
            sunday.setArguments(sundayBundle);
            adapter.addFragment(sunday, "Sun");

            Bundle mondayBundle = new Bundle();
            mondayBundle.putString("mondayJsonArray", mondayJsonArray.toString());
            Monday monday = new Monday();
            monday.setArguments(mondayBundle);
            adapter.addFragment(monday, "Mon");

            Bundle tuesdayBundle = new Bundle();
            tuesdayBundle.putString("tuesdayJsonArray", tuesdayJsonArray.toString());
            Tuesday tuesday = new Tuesday();
            tuesday.setArguments(tuesdayBundle);
            adapter.addFragment(tuesday, "Tue");

            Bundle wednesdayBundle = new Bundle();
            wednesdayBundle.putString("wednesdayJsonArray", wednesdayJsonArray.toString());
            Wednesday wednesday = new Wednesday();
            wednesday.setArguments(wednesdayBundle);
            adapter.addFragment(wednesday, "Wed");

            Bundle thursdayBundle = new Bundle();
            thursdayBundle.putString("thursdayJsonArray", thursdayJsonArray.toString());
            Thursday thursday = new Thursday();
            thursday.setArguments(thursdayBundle);
            adapter.addFragment(thursday, "Thu");

            Bundle fridayBundle = new Bundle();
            fridayBundle.putString("fridayJsonArray", fridayJsonArray.toString());
            Friday friday = new Friday();
            friday.setArguments(fridayBundle);
            adapter.addFragment(friday, "Fri");
        } else if(UserTypeID==3||UserTypeID==5) {
            adapter.addFragment(new Saturday(), "Sat");
            adapter.addFragment(new Sunday(), "Sun");
            adapter.addFragment(new Monday(), "Mon");
            adapter.addFragment(new Tuesday(), "Tue");
            adapter.addFragment(new Wednesday(), "Wed");
            adapter.addFragment(new Thursday(), "Thu");
            adapter.addFragment(new Friday(), "Fri");
        }
        viewPager.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        Calendar c = Calendar.getInstance();
        int currentDay = c.get(Calendar.DAY_OF_WEEK);
        viewPager.setCurrentItem(currentDay);
    }

    private void RoutineDataGetRequest(long ShiftID) {
        if(StaticHelperClass.isNetworkAvailable(this)) {
            dialog = new ProgressDialog(this);
            dialog.setTitle("Loading Routine...");
            dialog.setMessage("Please Wait...");
            dialog.setCancelable(false);
            dialog.setIcon(R.drawable.onair);
            dialog.show();

            String routineUrl = "";

            if(UserTypeID == 1||UserTypeID == 2) {
                routineUrl = getString(R.string.baseUrl)+"/api/onEms/spGetCommonClassRoutine/"
                        +InstituteID+"/"+ShiftID;
            } else if(UserTypeID == 4) {
                routineUrl = getString(R.string.baseUrl)+"/api/onEms/spGetTeacherStudentMyClassRoutine/"
                        +InstituteID+"/"+LoggedUserClassID+"/"+LoggedUserID;
            }

            StringRequest request = new StringRequest(Request.Method.GET, routineUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            parseReturnData(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dialog.dismiss();
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
            Toast.makeText(this,"Please check your INTERNET connection !!!",Toast.LENGTH_LONG).show();
        }
    }

    private void parseReturnData(String routine) {
        try {
            JSONArray routineJsonArray = new JSONArray(routine);
            saturdayJsonArray = new JSONArray();
            sundayJsonArray = new JSONArray();
            mondayJsonArray = new JSONArray();
            tuesdayJsonArray = new JSONArray();
            wednesdayJsonArray = new JSONArray();
            thursdayJsonArray = new JSONArray();
            fridayJsonArray = new JSONArray();
            for(int i = 0; i<routineJsonArray.length(); i++) {
                int DayID = routineJsonArray.getJSONObject(i).getInt("DayID");
                switch (DayID) {
                    case 1:
                        saturdayJsonArray.put(routineJsonArray.getJSONObject(i));
                        break;
                    case 2:
                        sundayJsonArray.put(routineJsonArray.getJSONObject(i));
                        break;
                    case 3:
                        mondayJsonArray.put(routineJsonArray.getJSONObject(i));
                        break;
                    case 4:
                        tuesdayJsonArray.put(routineJsonArray.getJSONObject(i));
                        break;
                    case 5:
                        wednesdayJsonArray.put(routineJsonArray.getJSONObject(i));
                        break;
                    case 6:
                        thursdayJsonArray.put(routineJsonArray.getJSONObject(i));
                        break;
                    case 7:
                        fridayJsonArray.put(routineJsonArray.getJSONObject(i));
                        break;
                }
            }
            setupViewPager(viewPager);
            tabLayout.setupWithViewPager(viewPager);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        dialog.dismiss();
    }

    private void ShiftDataGetRequest() {
        if (StaticHelperClass.isNetworkAvailable(this)) {
            String shiftUrl = getString(R.string.baseUrl)+"/api/onEms/getInsShift/"+InstituteID;

            mShiftDialog = new ProgressDialog(this);
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
                    Toast.makeText(RoutineMainScreen.this,"Shift not found!!! ",
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
            MySingleton.getInstance(this).addToRequestQueue(stringShiftRequest);
        } else {
            Toast.makeText(RoutineMainScreen.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    void parseShiftJsonData(String jsonString) {
        if (jsonString.equals("")) {
            Toast.makeText(RoutineMainScreen.this,"Shift not found!!! ",
                    Toast.LENGTH_LONG).show();
        } else {
            ShiftSelectionDialog shiftSelectionDialog = new ShiftSelectionDialog(this, jsonString, this);
            shiftSelectionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            shiftSelectionDialog.setCancelable(false);
            shiftSelectionDialog.show();
        }
        mShiftDialog.dismiss();
    }
}
