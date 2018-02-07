package onair.onems.mainactivities.Fees;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import onair.onems.R;

/**
 * Created by hp on 1/24/2018.
 */

public class MontlyHistory extends AppCompatActivity
{
    String monthUrl = "", monthFeesDetailsUrl="",monthFineDetailsUrl="",monthDueDetailsUrl="",monthScholarshipUrl="";
    ProgressDialog dialog;
    int MonthID[];
    String[][] Fees_collection;
    int monthselectindex,TotalFineAmount=0;
    Button paymentButton;
    double total_fees=0, due_amount=10,total_amount=0,scholarship=0;
    long sectionID,classID,shiftID,mediumID,instituteID,depertmentID;
    String RFID;
    SharedPreferences prefs;
    ListView listView;
    ArrayList<CardModel> cardModels;
    TextView balance_text,fees_text,payment_text;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monlty_history);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        RFID= prefs.getString("RFID","");
        shiftID= prefs.getLong("ShiftID",0);
        mediumID= prefs.getLong("MediumID",0);
        classID= prefs.getLong("ClassID",0);
        sectionID= prefs.getLong("SectionID",0);
        instituteID=prefs.getLong("InstituteID",0);
        monthselectindex= prefs.getInt("monthselectindex",0);
        depertmentID=prefs.getLong("SDepartmentID",0);
        listView = (ListView) findViewById(R.id.list_cards);
        fees_text=(TextView) findViewById(R.id.amount_fee);
        payment_text=(TextView) findViewById(R.id.payment_amount);
        balance_text=(TextView) findViewById(R.id.due_amount);
        cardModels = new ArrayList<CardModel>();
        cardModels.add(new CardModel("Fees: ", prefs.getString("fee","")+" TK"));
        cardModels.add(new CardModel("Fine:", prefs.getString("fine","")+" TK"));
        cardModels.add(new CardModel("Scholarship:", prefs.getString("scholarship","")+" TK"));
        cardModels.add(new CardModel("Discount:", prefs.getString("discount","")+" TK"));
        cardModels.add(new CardModel("Previous Due/Advance:", prefs.getString("due","")+" TK"));
        CardAdapter cardAdapter = new CardAdapter(getApplicationContext(),cardModels);
        fees_text.setText(prefs.getString("total_amount","")+" TK");
        payment_text.setText(prefs.getString("payment","")+" TK");
        balance_text.setText(prefs.getString("balance","")+" TK");

        listView.setAdapter(cardAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


            }
        });

    }

    // Back parent Page code
    public boolean onOptionsItemSelected(MenuItem item){
        super.onBackPressed();
        NavUtils.navigateUpFromSameTask(this);
        super.onBackPressed();
        return true;

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        NavUtils.navigateUpFromSameTask(this);
        super.onBackPressed();

    }

    // Back parent Page code end
}
