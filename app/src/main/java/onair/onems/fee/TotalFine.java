package onair.onems.fee;

/**
 * Created by hp on 1/24/2018.
 */

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
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

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import de.codecrafters.tableview.toolkit.TableDataRowBackgroundProviders;
import onair.onems.R;

/**
 * Created by hp on 1/23/2018.
 */

public class TotalFine extends AppCompatActivity
{
    TableView tableView;
    SimpleTableHeaderAdapter simpleTableHeaderAdapter;
    SimpleTableDataAdapter simpleTabledataAdapter;
    Configuration config;
    String[][] DATA_TO_SHOW;
    String monthFineDetailsUrl;
    long sectionID,classID,shiftID,mediumID,instituteID,depertmentID;
    String RFID;
    SharedPreferences prefs;
    int monthselectindex;
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.total_fine);
        tableView = (TableView) findViewById(R.id.tableView);
        simpleTableHeaderAdapter = new SimpleTableHeaderAdapter(this, "FINE HEAD","FINE TYPES", "FINE AMOUNT");
        simpleTableHeaderAdapter.setTextColor(ContextCompat.getColor(this, R.color.table_header_text));
        tableView.setHeaderAdapter(simpleTableHeaderAdapter);
        int colorEvenRows = getResources().getColor(R.color.table_data_row_even);
        int colorOddRows = getResources().getColor(R.color.table_data_row_odd);
        tableView.setDataRowBackgroundProvider(TableDataRowBackgroundProviders.alternatingRowColors(colorEvenRows, colorOddRows));

        // colour of the Even and Odd row of the table End

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        RFID= prefs.getString("RFID","");
        shiftID= prefs.getLong("ShiftID",0);
        mediumID= prefs.getLong("MediumID",0);
        classID= prefs.getLong("ClassID",0);
        sectionID= prefs.getLong("SectionID",0);
        instituteID=prefs.getLong("InstituteID",0);
        monthselectindex= prefs.getInt("monthselectindex",0);
        depertmentID=prefs.getLong("SDepartmentID",0);

        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading....");
        dialog.show();
        monthFineDetailsUrl = getString(R.string.baseUrl)+"/api/onEms/getLateAndAbsent/"+instituteID;
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.GET,  monthFineDetailsUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        parseMonthlyFineDetailsJsonData(response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.dismiss();
            }
        });

        queue.add(stringRequest);

        config = getResources().getConfiguration();

    }
    void    parseMonthlyFineDetailsJsonData(String jsonString)
    {
        try
        {
            JSONArray jsonArray = new JSONArray(jsonString);
            ArrayList al = new ArrayList();
            int minimumDays,fineAmount,Days,finableDays,TotalFineAmount;
            DATA_TO_SHOW = new String[jsonArray.length()][3];
            for(int i = 0; i < jsonArray.length(); ++i) {
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                DATA_TO_SHOW[i][0]= jsonObject.getString("FineHead");
                DATA_TO_SHOW [i][1]=DATA_TO_SHOW[i][0]+" "+jsonObject.getString("Days")+" Days";
                minimumDays=jsonObject.getInt("MinimumDays");
                fineAmount=jsonObject.getInt("FineAmount");
                Days=jsonObject.getInt("Days");
                finableDays = Days/minimumDays;
                TotalFineAmount = finableDays*fineAmount;
                DATA_TO_SHOW [i][2]=Integer.toString( TotalFineAmount)+" TK";
                al.add(DATA_TO_SHOW[i][0]);
                al.add(DATA_TO_SHOW[i][1]);
                al.add(DATA_TO_SHOW[i][2]);

            }

            simpleTabledataAdapter = new SimpleTableDataAdapter(getApplicationContext(),DATA_TO_SHOW);
            tableView.setDataAdapter(simpleTabledataAdapter);
            if (config.smallestScreenWidthDp >320) {
                simpleTableHeaderAdapter.setTextSize(14);
                simpleTabledataAdapter.setTextSize(14);

            } else {
                simpleTableHeaderAdapter.setTextSize(12);
                simpleTabledataAdapter.setTextSize(12);

            }

            dialog.dismiss();
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),""+e,Toast.LENGTH_LONG).show();
        }

        dialog.dismiss();
    }
    // Back parent Page code
    public boolean onOptionsItemSelected(MenuItem item)
    {
        super.onBackPressed();
        NavUtils.navigateUpFromSameTask(this);
        super.onBackPressed();
        return true;

    }
    @Override
    public void onBackPressed()
    {
        NavUtils.navigateUpFromSameTask(this);
        super.onBackPressed();
    }

}