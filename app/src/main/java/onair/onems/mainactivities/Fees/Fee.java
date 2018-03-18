package onair.onems.mainactivities.Fees;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import onair.onems.R;
import onair.onems.customadapters.ExpandableListAdapterStudent;
import onair.onems.mainactivities.LoginScreen;
import onair.onems.mainactivities.Routine.ClassRoutine;
import onair.onems.mainactivities.StudentAttendance;
import onair.onems.mainactivities.StudentMainScreen;
import onair.onems.models.ExpandedMenuModel;


/**
 * Created by hp on 1/22/2018.
 */

public class Fee extends AppCompatActivity
{
    private DrawerLayout mDrawerLayout;
    ExpandableListAdapterStudent mMenuAdapter;
    ExpandableListView expandableList;
    List<ExpandedMenuModel> listDataHeader;
    HashMap<ExpandedMenuModel, List<String>> listDataChild;
    public static final String MyPREFERENCES = "LogInKey";
    public static SharedPreferences sharedPreferences;
    MaterialSpinner spinner,type_spinner;
    String monthUrl = "", monthFeesDetailsUrl="",monthFineDetailsUrl="",monthDueDetailsUrl="",monthScholarshipUrl="";
    ProgressDialog dialog;
    int MonthID[];
    int monthselectindex,TotalFineAmount=0;
    Button paymentButton;
    double total_fees=0, due_amount=10,total_amount=0,scholarship=0;
    long sectionID,classID,shiftID,mediumID,instituteID,depertmentID;
    String RFID,UserID;
    SharedPreferences  prefs;
    ListView listView;
    ArrayList<CardModel> cardModels;
    String type[]={"Select","Cadet Card","Papal","Bikash"};
    TextView Total_fine,Total_amount_Text,Total_due,Total_scholarship;
    @Override
    public void onResume()
    {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        super.onResume();
    }

    @Override
    public void onStart()
    {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        super.onStart();
    }

    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_fees);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(android.R.drawable.ic_menu_add);
        ab.setDisplayHomeAsUpEnabled(true);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        expandableList = (ExpandableListView) findViewById(R.id.navigationmenu);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View view = getLayoutInflater().inflate(R.layout.nav_header_student_attendance,null);
        ImageView profilePicture = (ImageView)view.findViewById(R.id.profilePicture);
        TextView userType = (TextView)view.findViewById(R.id.userType);
        TextView userName = (TextView)view.findViewById(R.id.userName);
        Total_amount_Text=(TextView) findViewById(R.id.total_amount);
        listView = (ListView) findViewById(R.id.list_cards);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        RFID= prefs.getString("RFID","");
        shiftID= prefs.getLong("ShiftID",0);
        mediumID= prefs.getLong("MediumID",0);
        classID= prefs.getLong("ClassID",0);
        sectionID= prefs.getLong("SectionID",0);
        instituteID=prefs.getLong("InstituteID",0);
        UserID=prefs.getString("UserID","");
        monthselectindex= prefs.getInt("monthselectindex",0);
        depertmentID=prefs.getLong("SDepartmentID",0);
        String imageUrl = prefs.getString("ImageUrl","");
        String name = prefs.getString("UserFullName","");
        paymentButton=(Button)findViewById(R.id.btn_payment);
        paymentButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent mainIntent = new Intent(Fee.this,payment.class);
                Fee.this.startActivity(mainIntent);

            }
        });

        long user = prefs.getLong("UserTypeID",0);
        userName.setText(name);
        if(user == 3)
        {
            userType.setText("Student");
        }
        else if(user == 5)
        {
            userType.setText("Guardian");
        }
        Glide.with(this).load(getString(R.string.baseUrlRaw)+imageUrl.replace("\\","/")).apply(RequestOptions.circleCropTransform()).into(profilePicture);
        navigationView.addHeaderView(view);

        if (navigationView != null)
        {
            setupDrawerContent(navigationView);
        }

        prepareListData();
        mMenuAdapter = new ExpandableListAdapterStudent(this, listDataHeader, listDataChild, expandableList);

        // setting list adapter
        expandableList.setAdapter(mMenuAdapter);

        expandableList.setOnChildClickListener(new ExpandableListView.OnChildClickListener()
        {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
             if(i==6&&i1==0)
             {
                 DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                 if (drawer.isDrawerOpen(GravityCompat.START))
                 {
                     drawer.closeDrawer(GravityCompat.START);

                 }
             }
                if(i==6&&i1==1)
                {
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    if (drawer.isDrawerOpen(GravityCompat.START))
                    {

                        drawer.closeDrawer(GravityCompat.START);
                        Intent mainIntent = new Intent(Fee.this,FeesHistory.class);
                        Fee.this.startActivity(mainIntent);

                    }
                }
                return false;
            }
        });
        expandableList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener()
        {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l)
            {
//
                if((i == 2) && (l == 2) )
                {
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

                    if (drawer.isDrawerOpen(GravityCompat.START))
                    {
                        drawer.closeDrawer(GravityCompat.START);

                        Intent mainIntent = new Intent(Fee.this,StudentAttendance.class);
                        Fee.this.startActivity(mainIntent);
                        finish();
                    }
                }
                if((i == 1) )
                {
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

                    if (drawer.isDrawerOpen(GravityCompat.START))
                    {
                        drawer.closeDrawer(GravityCompat.START);
                        Intent mainIntent = new Intent(Fee.this,ClassRoutine.class);
                        Fee.this.startActivity(mainIntent);
                        finish();
                    }
                }
                return false;
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(position==0)
            {
                Intent myIntent = new Intent(getApplicationContext(), FeesDetails.class);
                startActivityForResult(myIntent, 0);

            }
                if(position==2)
                {
                    Intent myIntent = new Intent(getApplicationContext(), FineDetails.class);
                    startActivityForResult(myIntent, 0);


                }

            }
        });

        monthUrl=getString(R.string.baseUrlLocal)+"getMonth";
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading....");
        dialog.show();


        RequestQueue queueMonth = Volley.newRequestQueue(this);
        StringRequest stringMonthRequest = new StringRequest(Request.Method.GET, monthUrl,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {

                       parseMonthJsonData(response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.dismiss();
            }
        });

        queueMonth.add(stringMonthRequest);
        spinner = (MaterialSpinner) findViewById(R.id.spinner_month);
        type_spinner = (MaterialSpinner) findViewById(R.id.payment_type);
        ArrayList al = new ArrayList();
        al.add("Select");
        al.add("Cadet Card");
        al.add("Bikash");
        al.add("Paypal");
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, al);
        type_spinner.setAdapter(adapter);
        monthFeesDetailsUrl = getString(R.string.baseUrlLocal)+"getMonthWiseFees/"+instituteID+"/"+shiftID+"/"+depertmentID+"/"+mediumID+"/"+classID+"/"+monthselectindex;
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        StringRequest stringRequestFee = new StringRequest(Request.Method.GET,  monthFeesDetailsUrl,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {

                        parseMonthlyFeesDetailsJsonData(response);


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.dismiss();
            }
        });
        queue.add( stringRequestFee);

//   Monthly Fine collection



        //   Monthly Fine collection finished


        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>()
        {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item)
            {
                int monthid=MonthID[position];
                monthselectindex = monthid;
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("monthselectindex",monthid);
                editor.commit();
                monthFeesDetailsUrl = getString(R.string.baseUrlLocal)+"getMonthWiseFees/"+instituteID+"/"+shiftID+"/"+depertmentID+"/"+mediumID+"/"+classID+"/"+monthselectindex;
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

                StringRequest stringRequest = new StringRequest(Request.Method.GET, monthFeesDetailsUrl,
                        new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response)
                            {
                                parseMonthlyFeesDetailsJsonData(response);

                            }
                        }, new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        dialog.dismiss();
                    }
                });

                queue.add(stringRequest);



            }
        });

    }
    void parseMonthJsonData(String jsonString)
    {

        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            ArrayList al = new ArrayList();
            MonthID=new int[jsonArray.length()];
            for(int i = 0; i < jsonArray.length(); ++i)
            {
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                String name=jsonObject.getString("MonthName");
                MonthID[i]=jsonObject.getInt("MonthID");
                al.add(name);
            }

            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, al);
            spinner.setAdapter(adapter);
            spinner.setSelectedIndex(monthselectindex-1);

        } catch (Exception e)
        {
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();
            dialog.dismiss();

        }

    }

    void parseMonthlyFeesDetailsJsonData(String jsonString)
    {

        cardModels = new ArrayList<CardModel>();
        total_amount=0;
        total_fees=0;
        try
        {
            JSONArray jsonArray = new JSONArray(jsonString);
            for(int i = 0; i < jsonArray.length(); ++i)
            {
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                String fee= jsonObject.getString("Fee");
                double d_fee;
                if(fee.equals("null"))
                {
                   d_fee=0;
                }
                else

                    d_fee=Double.parseDouble(fee);
                   total_fees=total_fees+d_fee;

            }

          }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),""+e,Toast.LENGTH_LONG).show();
            dialog.dismiss();
        }
        cardModels.add(new CardModel("Total Fees: ", Double.toString( total_fees)+" TK"));


        monthScholarshipUrl = getString(R.string.baseUrlLocal)+"getScholarship/"+instituteID+"/"+UserID+"/"+monthselectindex;

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        StringRequest stringRequestScholarship = new StringRequest(Request.Method.GET,  monthScholarshipUrl ,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        parseMonthlyScholarshipJsonData(response);


                    }
                }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.dismiss();
            }
        });

        queue.add(stringRequestScholarship);



        //custom adapter


    }

    void parseMonthlyScholarshipJsonData(String jsonString)
    {

        scholarship=0;
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            for(int i = 0; i < jsonArray.length(); ++i)
            {
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                boolean IsParcent=jsonObject.getBoolean("IsParcent");
                boolean IsOnTution=jsonObject.getBoolean("IsOnTution");
                boolean IsOnTotal=jsonObject.getBoolean("IsOnTotal");
                double Amount=jsonObject.getDouble("Amount");
                double TotalFee=jsonObject.getDouble("TotalFee");
                double TutionFee=jsonObject.getDouble("TutionFee");
                if(IsParcent)
                {
                   if(IsOnTution)
                   {
                       scholarship= (TutionFee*Amount)/100;
                   }
                   else if(IsOnTotal)
                   {
                       scholarship= (TotalFee*Amount)/100;
                   }
                }
                else
                    scholarship=Amount;

            }


        } catch (Exception e)
        {
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();


        }
        cardModels.add(new CardModel("Scholarship:", scholarship+" TK"));

        monthFineDetailsUrl = getString(R.string.baseUrlLocal)+"getLateAndAbsent/"+instituteID;
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        StringRequest stringRequestfine = new StringRequest(Request.Method.GET,  monthFineDetailsUrl,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        parseMonthlyFineDetailsJsonData(response);


                    }
                }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.dismiss();
            }
        });

        queue.add(stringRequestfine);


    }
    void   parseMonthlyFineDetailsJsonData(String jsonString)
    {
        try
        {
            JSONArray jsonArray = new JSONArray(jsonString);
            ArrayList al = new ArrayList();
            int minimumDays,fineAmount,Days,finableDays;
            TotalFineAmount=0;

            for(int i = 0; i < jsonArray.length(); ++i)
            {
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                minimumDays=jsonObject.getInt("MinimumDays");
                fineAmount=jsonObject.getInt("FineAmount");
                Days=jsonObject.getInt("Days");
                finableDays = Days/minimumDays;
                TotalFineAmount = TotalFineAmount+finableDays*fineAmount;

            }
            cardModels.add(new CardModel("Total Fine:", Double.toString( TotalFineAmount )+" TK"));

            monthDueDetailsUrl = getString(R.string.baseUrlLocal)+"getPreviousDue/"+instituteID+"/"+shiftID+"/"+depertmentID+"/"+mediumID+"/"+classID+"/"+UserID+"/"+monthselectindex;
            RequestQueue queue1 = Volley.newRequestQueue(getApplicationContext());

            StringRequest stringRequestfine = new StringRequest(Request.Method.GET,   monthDueDetailsUrl,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {

                            parseMonthlyDueJsonData(response);

                        }
                    }, new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError error) {

                    dialog.dismiss();
                }
            });

            queue1.add(stringRequestfine);

        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),""+e,Toast.LENGTH_LONG).show();
            dialog.dismiss();
        }


    }



    void parseMonthlyDueJsonData(String jsonString)
    {

        due_amount=0;
        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            for(int i = 0; i < jsonArray.length(); ++i)
            {
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                String due=jsonObject.getString("PreviousDue");

                if(due.equals("null"))
                {
                    due_amount=0;
                }
                else
                    due_amount=Double.parseDouble(due);

            }


        } catch (Exception e)
        {
            Toast.makeText(this,""+e,Toast.LENGTH_LONG).show();


        }
        cardModels.add(new CardModel("Previous Due/Advance:", Double.toString( due_amount )+" TK"));
        CardAdapter cardAdapter = new CardAdapter(getApplicationContext(),cardModels);
        listView.setAdapter(cardAdapter);
        total_amount=TotalFineAmount+total_fees-scholarship-due_amount;
        Total_amount_Text.setText(Double.toString(total_amount)+" TK");
        dialog.dismiss();

    }
    private void prepareListData()
    {
        listDataHeader = new ArrayList<ExpandedMenuModel>();
        listDataChild = new HashMap<ExpandedMenuModel, List<String>>();

        ExpandedMenuModel menuNotice = new ExpandedMenuModel();
        menuNotice.setIconName("Notice");
        menuNotice.setIconImg(R.drawable.nav_notice);
        // Adding data header
        listDataHeader.add(menuNotice);

        ExpandedMenuModel menuRoutine = new ExpandedMenuModel();
        menuRoutine.setIconName("Routine");
        menuRoutine.setIconImg(R.drawable.nav_routine);
        listDataHeader.add(menuRoutine);

        ExpandedMenuModel menuAttendance = new ExpandedMenuModel();
        menuAttendance.setIconName("Atendance");
        menuAttendance.setIconImg(R.drawable.nav_attendance);
        listDataHeader.add(menuAttendance);

        ExpandedMenuModel menuSyllabus = new ExpandedMenuModel();
        menuSyllabus.setIconName("Syllabus");
        menuSyllabus.setIconImg(R.drawable.nav_syllabus);
        listDataHeader.add(menuSyllabus);

        ExpandedMenuModel menuExam = new ExpandedMenuModel();
        menuExam.setIconName("Exam");
        menuExam.setIconImg(R.drawable.nav_exam);
        listDataHeader.add(menuExam);

        ExpandedMenuModel menuResult = new ExpandedMenuModel();
        menuResult.setIconName("Result");
        menuResult.setIconImg(R.drawable.nav_result);
        listDataHeader.add(menuResult);

        ExpandedMenuModel menuFee = new ExpandedMenuModel();
        menuFee.setIconName("Fee");
        menuFee.setIconImg(R.drawable.nav_fee);
        listDataHeader.add(menuFee);

        ExpandedMenuModel menuContact = new ExpandedMenuModel();
        menuContact.setIconName("Contact");
        menuContact.setIconImg(R.drawable.nav_contact);
        listDataHeader.add(menuContact);
        // Adding child data
        List<String> headingNotice = new ArrayList<String>();
        headingNotice.add("New Notice");
        headingNotice.add("Old Notice");
        List<String> headingRoutine = new ArrayList<String>();
//        headingRoutine.add("Mid Term Exam");
//        headingRoutine.add("Final Exam");
        List<String> headingFees = new ArrayList<String>();
        headingFees.add("Pay fee");
        headingFees.add("Fee History");
        List<String> headingAttendance = new ArrayList<String>();
        List<String> headingSyllabus = new ArrayList<String>();
        List<String> headingExam = new ArrayList<String>();
        List<String> headingResult = new ArrayList<String>();
//       List<String> headingFee = new ArrayList<String>();
        List<String> headingContact = new ArrayList<String>();

        listDataChild.put(listDataHeader.get(0), headingNotice);// Header, Child data
        listDataChild.put(listDataHeader.get(1), headingRoutine);
        listDataChild.put(listDataHeader.get(2), headingAttendance);
        listDataChild.put(listDataHeader.get(3), headingSyllabus);
        listDataChild.put(listDataHeader.get(4), headingExam);
        listDataChild.put(listDataHeader.get(5), headingResult);
        listDataChild.put(listDataHeader.get(6), headingFees);
        listDataChild.put(listDataHeader.get(7), headingContact);

    }
    private void setupDrawerContent(NavigationView navigationView)
    {

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }
    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent mainIntent = new Intent(Fee.this,StudentMainScreen.class);
            Fee.this.startActivity(mainIntent);
            finish();

        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout)
        {
            sharedPreferences  = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("LogInState", false);
            editor.commit();
            Intent intent = new Intent(Fee.this, LoginScreen.class);
            startActivity(intent);
            finish();
            return true;
        }

        switch (item.getItemId())
        {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }





}