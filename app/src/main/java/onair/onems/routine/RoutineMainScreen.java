package onair.onems.routine;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import onair.onems.R;
import onair.onems.Services.RetrofitNetworkService;
import onair.onems.Services.StaticHelperClass;
import onair.onems.mainactivities.SideNavigationMenuParentActivity;
import onair.onems.mainactivities.StudentMainScreen;
import onair.onems.mainactivities.TeacherMainScreen;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RoutineMainScreen extends SideNavigationMenuParentActivity implements ShiftSelectionAdapter.ShiftSelectionListener{

    private JSONArray saturdayJsonArray, sundayJsonArray, mondayJsonArray, tuesdayJsonArray, wednesdayJsonArray, thursdayJsonArray, fridayJsonArray;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private FloatingActionButton fabShift;
    private CompositeDisposable finalDisposer = new CompositeDisposable();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (finalDisposer != null && !finalDisposer.isDisposed())
            finalDisposer.dispose();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityName = RoutineMainScreen.class.getName();

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View childActivityLayout = Objects.requireNonNull(inflater).inflate(R.layout.routine_content_main, null);
        LinearLayout parentActivityLayout = findViewById(R.id.contentMain);
        parentActivityLayout.addView(childActivityLayout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        viewPager = findViewById(R.id.pager);
        tabLayout = findViewById(R.id.tabLayout);

        saturdayJsonArray = new JSONArray();
        sundayJsonArray = new JSONArray();
        mondayJsonArray = new JSONArray();
        tuesdayJsonArray = new JSONArray();
        wednesdayJsonArray = new JSONArray();
        thursdayJsonArray = new JSONArray();
        fridayJsonArray = new JSONArray();

        fabShift = findViewById(R.id.fabShift);
        fabShift.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#9C077EF5")));
        fabShift.setOnClickListener(v -> ShiftDataGetRequest());

        if(UserTypeID==3 || UserTypeID==5) {
            fabShift.setVisibility(View.GONE);
        }

        if (UserTypeID == 1 || UserTypeID == 2 || UserTypeID == 4) {
            ShiftDataGetRequest();
        }

        if(UserTypeID!=1 && UserTypeID!=2 && UserTypeID!=4){
            setupViewPager(viewPager);
            tabLayout.setupWithViewPager(viewPager);
        }
    }

    @Override
    public void onShiftSelected(JSONObject shift) {
        try {
            if(UserTypeID == 1||UserTypeID == 2) {
                RoutineDataGetRequestForAdmin(shift.getLong("ShiftID"));
            } else if(UserTypeID == 4) {
                RoutineDataGetRequest(shift.getLong("ShiftID"));
            }
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

        @NonNull
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            return super.instantiateItem(container, position);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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
        switch (currentDay) {
            case Calendar.SATURDAY:
                viewPager.setCurrentItem(0);
                break;
            case Calendar.SUNDAY:
                viewPager.setCurrentItem(1);
                break;
            case Calendar.MONDAY:
                viewPager.setCurrentItem(2);
                break;
            case Calendar.TUESDAY:
                viewPager.setCurrentItem(3);
                break;
            case Calendar.WEDNESDAY:
                viewPager.setCurrentItem(4);
                break;
            case Calendar.THURSDAY:
                viewPager.setCurrentItem(5);
                break;
            case Calendar.FRIDAY:
                viewPager.setCurrentItem(6);
                break;
        }
    }

    private void RoutineDataGetRequestForAdmin(long ShiftID) {
        if(StaticHelperClass.isNetworkAvailable(this)) {
            dialog.show();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(getString(R.string.baseUrl))
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();

            Observable<String> observable = retrofit
                    .create(RetrofitNetworkService.class)
                    .spGetCommonClassRoutine(InstituteID, ShiftID)
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
                            parseReturnData(response);
                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                            Toast.makeText(RoutineMainScreen.this,"Routine not found!!! ",
                                    Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    }));
        } else {
            Toast.makeText(this,"Please check your INTERNET connection !!!",Toast.LENGTH_LONG).show();
        }
    }

    private void RoutineDataGetRequest(long ShiftID) {
        if(StaticHelperClass.isNetworkAvailable(this)) {
            dialog.show();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(getString(R.string.baseUrl))
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();

            Observable<String> observable = retrofit
                    .create(RetrofitNetworkService.class)
                    .spGetTeacherStudentMyClassRoutine(InstituteID, LoggedUserClassID, LoggedUserID, ShiftID, LoggedUserSectionID, LoggedUserMediumID)
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
                            parseReturnData(response);
                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                            Toast.makeText(RoutineMainScreen.this,"Routine not found!!! ",
                                    Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    }));
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
    }

    private void ShiftDataGetRequest() {
        if (StaticHelperClass.isNetworkAvailable(this)) {
            dialog.show();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(getString(R.string.baseUrl))
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();

            Observable<String> observable = retrofit
                    .create(RetrofitNetworkService.class)
                    .getInsShift(InstituteID)
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
                            parseShiftJsonData(response);
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
            Toast.makeText(RoutineMainScreen.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    void parseShiftJsonData(String jsonString) {
        if (jsonString.equals("")||jsonString.equals("[]")) {
            Toast.makeText(RoutineMainScreen.this,"Shift not found!!! ",
                    Toast.LENGTH_LONG).show();
            fabShift.setVisibility(View.GONE);
        } else {
            ShiftSelectionDialog shiftSelectionDialog = new ShiftSelectionDialog(this, jsonString, this);
            Objects.requireNonNull(shiftSelectionDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            shiftSelectionDialog.setCancelable(false);
            shiftSelectionDialog.show();
        }
    }
}
