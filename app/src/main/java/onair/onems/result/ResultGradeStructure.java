package onair.onems.result;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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
import onair.onems.customised.MyDividerItemDecoration;
import onair.onems.mainactivities.SideNavigationMenuParentActivity;
import onair.onems.mainactivities.StudentMainScreen;
import onair.onems.mainactivities.TeacherMainScreen;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ResultGradeStructure extends SideNavigationMenuParentActivity
        implements MediumAdapter.MediumAdapterListener, ClassAdapter.ClassAdapterListener {

    private String MediumID = "";
    private FloatingActionButton floatingMenu, menuItemMedium, menuItemClass;
    private CardView cardMedium, cardClass;
    private Animation fab_open, fab_close, rotate_forward, rotate_backward;
    private Boolean isFabOpen = false;
    private ResultStructureAdapter mAdapter;
    private List<JSONObject> ResultGradeSheet;
    private CompositeDisposable finalDisposer = new CompositeDisposable();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!finalDisposer.isDisposed())
            finalDisposer.dispose();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityName = ResultGradeStructure.class.getName();

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View childActivityLayout = Objects.requireNonNull(inflater).inflate(R.layout.result_grade_structure, null);
        LinearLayout parentActivityLayout = findViewById(R.id.contentMain);
        parentActivityLayout.addView(childActivityLayout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        ResultGradeSheet = new ArrayList<>();
        mAdapter = new ResultStructureAdapter(this, ResultGradeSheet);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 0));
        recyclerView.setAdapter(mAdapter);

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_open.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                menuItemClass.setVisibility(View.VISIBLE);
                cardClass.setVisibility(View.VISIBLE);
                menuItemMedium.setVisibility(View.VISIBLE);
                cardMedium.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        fab_close.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardMedium.setVisibility(View.GONE);
                menuItemMedium.setVisibility(View.GONE);
                cardClass.setVisibility(View.GONE);
                menuItemClass.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);

        ConstraintLayout constraintLayout = findViewById(R.id.draggableFloatingMenu);
        constraintLayout.setOnClickListener(view-> animateFAB());

        cardMedium = findViewById(R.id.cardMedium);
        cardMedium.setOnClickListener(v -> MediumDataGetRequest());

        cardClass = findViewById(R.id.cardClass);
        cardClass.setOnClickListener(v -> {
            if(!MediumID.equalsIgnoreCase("")) {
                ClassDataGetRequest(MediumID);
            } else {
                Toast.makeText(ResultGradeStructure.this,"No class found, please select medium first!!! ",
                        Toast.LENGTH_LONG).show();
            }
        });

        floatingMenu = findViewById(R.id.floatingMenu);
        floatingMenu.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#5f27cd")));
        floatingMenu.setRippleColor(Color.parseColor("#341f97"));

        menuItemMedium = findViewById(R.id.medium);
        menuItemMedium.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#EE5A24")));
        menuItemMedium.setRippleColor(Color.parseColor("#e50b00"));

        menuItemMedium.setOnClickListener(v -> MediumDataGetRequest());

        menuItemClass = findViewById(R.id.classs);
        menuItemClass.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#009432")));
        menuItemClass.setRippleColor(Color.parseColor("#036625"));

        menuItemClass.setOnClickListener(v -> {
            if(!MediumID.equalsIgnoreCase("")) {
                ClassDataGetRequest(MediumID);
            } else {
                Toast.makeText(ResultGradeStructure.this,"No class found, please select medium first!!! ",
                        Toast.LENGTH_LONG).show();
            }
        });

        MediumDataGetRequest();
    }

    public void animateFAB(){

        if(isFabOpen){

            floatingMenu.startAnimation(rotate_backward);
            cardMedium.startAnimation(fab_close);
            menuItemMedium.startAnimation(fab_close);
            cardClass.startAnimation(fab_close);
            menuItemClass.startAnimation(fab_close);
            menuItemMedium.setClickable(false);
            menuItemClass.setClickable(false);
            cardMedium.setClickable(false);
            cardClass.setClickable(false);
            isFabOpen = false;

        } else {

            floatingMenu.startAnimation(rotate_forward);
            menuItemClass.startAnimation(fab_open);
            cardClass.startAnimation(fab_open);
            menuItemMedium.startAnimation(fab_open);
            cardMedium.startAnimation(fab_open);
            menuItemClass.setClickable(true);
            menuItemMedium.setClickable(true);
            cardClass.setClickable(true);
            cardMedium.setClickable(true);
            isFabOpen = true;
        }
    }

    private void ResultGradeDataGetRequest(String MediumID, String ClassID){
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
                    .getinsGradeForReport(InstituteID, MediumID, ClassID)
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
                            prepareResultGradeSheet(response);
                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                            Toast.makeText(ResultGradeStructure.this,"Grade sheet not found!!! ",
                                    Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    }));
        } else {
            Toast.makeText(ResultGradeStructure.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void prepareResultGradeSheet(String result) {
        try {
            ResultGradeSheet.clear();
            JSONArray resultJsonArray = new JSONArray(result);
            for(int i = 0; i<resultJsonArray.length(); i++) {
                ResultGradeSheet.add(resultJsonArray.getJSONObject(i));
            }
            mAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void MediumDataGetRequest() {
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
                    .getInstituteMediumDdl(InstituteID)
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
                            parseMediumJsonData(response);
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
            Toast.makeText(ResultGradeStructure.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    void parseMediumJsonData(String jsonString) {
        if(!jsonString.equalsIgnoreCase("[]")) {
            MediumSelectionDialog mediumSelectionDialog = new MediumSelectionDialog(this, jsonString, this);
            mediumSelectionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mediumSelectionDialog.setCancelable(false);
            mediumSelectionDialog.show();
        } else {
            Toast.makeText(this,"Medium not found!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void ClassDataGetRequest(String MediumID) {
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
                    .MediumWiseClassDDL(InstituteID, MediumID.equalsIgnoreCase("null")||
                            MediumID.equalsIgnoreCase("")||MediumID==null?0:Long.parseLong(MediumID))
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
                            parseClassJsonData(response);
                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                            Toast.makeText(ResultGradeStructure.this,"Class not found!!! ",
                                    Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    }));
        } else {
            Toast.makeText(ResultGradeStructure.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    void parseClassJsonData(String jsonString) {
        if(!jsonString.equalsIgnoreCase("[]")) {
            ClassSelectionDialog classSelectionDialog = new ClassSelectionDialog(this, jsonString, this);
            Objects.requireNonNull(classSelectionDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            classSelectionDialog.setCancelable(false);
            classSelectionDialog.show();
        } else {
            Toast.makeText(this,"Class not found!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(UserTypeID == 1) {
            Intent mainIntent = new Intent(ResultGradeStructure.this,TeacherMainScreen.class);
            startActivity(mainIntent);
            finish();
        } else if(UserTypeID == 2) {
            Intent mainIntent = new Intent(ResultGradeStructure.this,TeacherMainScreen.class);
            startActivity(mainIntent);
            finish();
        } else if(UserTypeID == 3) {
            Intent mainIntent = new Intent(ResultGradeStructure.this,StudentMainScreen.class);
            startActivity(mainIntent);
            finish();
        } else if(UserTypeID == 4) {
            Intent mainIntent = new Intent(ResultGradeStructure.this,TeacherMainScreen.class);
            startActivity(mainIntent);
            finish();
        } else if(UserTypeID == 5) {
            Intent mainIntent = new Intent(ResultGradeStructure.this,StudentMainScreen.class);
            startActivity(mainIntent);
            finish();
        }
    }

    @Override
    public void onClassSelected(JSONObject classObject) {
        try {
            ResultGradeDataGetRequest(MediumID, classObject.getString("ClassID"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMediumSelected(JSONObject medium) {
        try {
            MediumID = medium.getString("MediumID");
            ClassDataGetRequest(MediumID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
