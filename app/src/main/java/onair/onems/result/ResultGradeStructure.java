package onair.onems.result;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import onair.onems.R;
import onair.onems.Services.StaticHelperClass;
import onair.onems.customised.MyDividerItemDecoration;
import onair.onems.mainactivities.SideNavigationMenuParentActivity;
import onair.onems.mainactivities.StudentMainScreen;
import onair.onems.mainactivities.TeacherMainScreen;
import onair.onems.models.ClassModel;
import onair.onems.models.MediumModel;
import onair.onems.network.MySingleton;
import onair.onems.syllabus.FloatingMenuDialog;
import onair.onems.syllabus.SyllabusMainScreen;

import static onair.onems.Services.StaticHelperClass.dpToPx;

public class ResultGradeStructure extends SideNavigationMenuParentActivity
        implements View.OnTouchListener, MediumAdapter.MediumAdapterListener, ClassAdapter.ClassAdapterListener {

    private RecyclerView recyclerView;
    private ProgressDialog mResultDialog, mMediumDialog, mClassDialog;
    private String MediumID = "";
    private float dX;
    private float dY;
    private int lastAction;
    private FloatingActionButton floatingMenu, menuItemMedium, menuItemClass;
    private ConstraintLayout constraintLayout;
    private CardView cardMedium, cardClass;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    private Boolean isFabOpen = false;
    private ResultStructureAdapter mAdapter;
    private List<JSONObject> ResultGradeSheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityName = ResultGradeStructure.class.getName();

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View childActivityLayout = inflater.inflate(R.layout.result_grade_structure, null);
        LinearLayout parentActivityLayout = (LinearLayout) findViewById(R.id.contentMain);
        parentActivityLayout.addView(childActivityLayout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        recyclerView = findViewById(R.id.recycler_view);
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

        constraintLayout = findViewById(R.id.draggableFloatingMenu);
        constraintLayout.setOnTouchListener(this);

        cardMedium = findViewById(R.id.cardMedium);
        cardMedium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediumDataGetRequest();
            }
        });

        cardClass = findViewById(R.id.cardClass);
        cardClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!MediumID.equalsIgnoreCase("")) {
                    ClassDataGetRequest(MediumID);
                } else {
                    Toast.makeText(ResultGradeStructure.this,"No class found, please select medium first!!! ",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        floatingMenu = findViewById(R.id.floatingMenu);
        floatingMenu.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#5f27cd")));
        floatingMenu.setRippleColor(Color.parseColor("#341f97"));

        menuItemMedium = findViewById(R.id.medium);
        menuItemMedium.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#EE5A24")));
        menuItemMedium.setRippleColor(Color.parseColor("#e50b00"));

        menuItemMedium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediumDataGetRequest();
            }
        });

        menuItemClass = findViewById(R.id.classs);
        menuItemClass.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#009432")));
        menuItemClass.setRippleColor(Color.parseColor("#036625"));

        menuItemClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!MediumID.equalsIgnoreCase("")) {
                    ClassDataGetRequest(MediumID);
                } else {
                    Toast.makeText(ResultGradeStructure.this,"No class found, please select medium first!!! ",
                            Toast.LENGTH_LONG).show();
                }
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

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                dX = view.getX() - event.getRawX();
                dY = view.getY() - event.getRawY();
                lastAction = MotionEvent.ACTION_DOWN;
                break;

            case MotionEvent.ACTION_MOVE:
                view.setY(event.getRawY() + dY);
                view.setX(event.getRawX() + dX);
                lastAction = MotionEvent.ACTION_MOVE;
                break;

            case MotionEvent.ACTION_UP:
                if (lastAction == MotionEvent.ACTION_DOWN){
                    animateFAB();
                }
                break;

            default:
                return false;
        }
        return true;
    }

    private void ResultGradeDataGetRequest(String MediumID, String ClassID){
        if (StaticHelperClass.isNetworkAvailable(this)) {

            String resultGradeDataGetUrl = getString(R.string.baseUrl)+"/api/onEms/getinsGradeForReport/"+InstituteID+"/"+MediumID+"/"+ClassID;

            mResultDialog = new ProgressDialog(this);
            mResultDialog.setTitle("Loading Grade Sheet...");
            mResultDialog.setMessage("Please Wait...");
            mResultDialog.setCancelable(false);
            mResultDialog.setIcon(R.drawable.onair);

            //Preparing Shift data from server
            StringRequest stringResultRequest = new StringRequest(Request.Method.GET, resultGradeDataGetUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            prepareResultGradeSheet(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mResultDialog.dismiss();
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
            MySingleton.getInstance(this).addToRequestQueue(stringResultRequest);
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
            mResultDialog.dismiss();
        } catch (JSONException e) {
            e.printStackTrace();
            mResultDialog.dismiss();
        }
    }

    private void MediumDataGetRequest() {
        if(StaticHelperClass.isNetworkAvailable(this)) {
            String mediumUrl = getString(R.string.baseUrl)+"/api/onEms/getInstituteMediumDdl/"+InstituteID;

            mMediumDialog = new ProgressDialog(this);
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
                    Toast.makeText(ResultGradeStructure.this,"Medium not found!!! ",
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
            MySingleton.getInstance(this).addToRequestQueue(stringMediumRequest);
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
        mMediumDialog.dismiss();
    }

    private void ClassDataGetRequest(String MediumID) {
        if(StaticHelperClass.isNetworkAvailable(this)) {

            String classUrl = getString(R.string.baseUrl)+"/api/onEms/MediumWiseClassDDL/"+InstituteID+"/"+MediumID;

            mClassDialog = new ProgressDialog(this);
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
                    Toast.makeText(ResultGradeStructure.this,"Class not found!!! ",
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
            MySingleton.getInstance(this).addToRequestQueue(stringClassRequest);
        } else {
            Toast.makeText(ResultGradeStructure.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    void parseClassJsonData(String jsonString) {
        if(!jsonString.equalsIgnoreCase("[]")) {
            ClassSelectionDialog classSelectionDialog = new ClassSelectionDialog(this, jsonString, this);
            classSelectionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            classSelectionDialog.setCancelable(false);
            classSelectionDialog.show();
        } else {
            Toast.makeText(this,"Class not found!!! ",
                    Toast.LENGTH_LONG).show();
        }
        mClassDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
