package onair.onems.fee;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import onair.onems.R;
import onair.onems.mainactivities.SideNavigationMenuParentActivity;
import onair.onems.mainactivities.StudentMainScreen;

public class FeeMainScreen extends SideNavigationMenuParentActivity {

    MaterialSpinner spinner,type_spinner;
    String monthUrl = "", monthFeesDetailsUrl="",monthFineDetailsUrl="",monthDueDetailsUrl="",monthScholarshipUrl="";
    ProgressDialog dialog;
    int MonthID[];
    int monthselectindex,TotalFineAmount=0;
    Button paymentButton;
    double total_fees=0, due_amount=10,total_amount=0,scholarship=0;
    String RFID,UserID;
    SharedPreferences  prefs;
    ListView listView;
    ArrayList<CardModel> cardModels;
    TextView Total_amount_Text;

    private int UserTypeID;

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

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityName = FeeMainScreen.class.getName();

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View childActivityLayout = inflater.inflate(R.layout.fee_content_main, null);
        LinearLayout parentActivityLayout = (LinearLayout) findViewById(R.id.contentMain);
        parentActivityLayout.addView(childActivityLayout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        Total_amount_Text=(TextView) findViewById(R.id.total_amount);
        listView = (ListView) findViewById(R.id.list_cards);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        RFID= prefs.getString("RFID","");
        UserID=prefs.getString("UserID","");
        monthselectindex= prefs.getInt("monthselectindex",0);
        paymentButton=(Button)findViewById(R.id.btn_payment);
        paymentButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent mainIntent = new Intent(FeeMainScreen.this,payment.class);
                FeeMainScreen.this.startActivity(mainIntent);

            }
        });

        UserTypeID = prefs.getInt("UserTypeID",0);

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

        monthUrl=getString(R.string.baseUrl)+"/api/onEms/getMonth";
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
        monthFeesDetailsUrl = getString(R.string.baseUrl)+"/api/onEms/getMonthWiseFees/"+InstituteID+"/"
                +LoggedUserShiftID+"/"+LoggedUserDepartmentID+"/"+LoggedUserMediumID+"/"+LoggedUserClassID+"/"+monthselectindex;
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
                monthFeesDetailsUrl = getString(R.string.baseUrl)+"/api/onEms/getMonthWiseFees/"+
                        InstituteID+"/"+LoggedUserShiftID+"/"+LoggedUserDepartmentID+"/"+LoggedUserMediumID+
                        "/"+LoggedUserClassID+"/"+monthselectindex;
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
                String fee= jsonObject.getString("fee");
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


        monthScholarshipUrl = getString(R.string.baseUrl)+"/api/onEms/getScholarship/"+InstituteID+"/"+UserID+"/"+monthselectindex;

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

        monthFineDetailsUrl = getString(R.string.baseUrl)+"/api/onEms/getLateAndAbsent/"+InstituteID;
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

            monthDueDetailsUrl = getString(R.string.baseUrl)+"/api/onEms/getPreviousDue/"+
                    InstituteID+"/"+LoggedUserShiftID+"/"+LoggedUserDepartmentID+"/"+LoggedUserMediumID
                    +"/"+LoggedUserClassID+"/"+UserID+"/"+monthselectindex;
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(UserTypeID == 3) {
            Intent mainIntent = new Intent(FeeMainScreen.this,StudentMainScreen.class);
            startActivity(mainIntent);
            finish();
        } else if(UserTypeID == 5) {
            Intent mainIntent = new Intent(FeeMainScreen.this,StudentMainScreen.class);
            startActivity(mainIntent);
            finish();
        }
    }
}