package onair.onems.mainactivities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.listeners.TableDataClickListener;
import de.codecrafters.tableview.model.TableColumnWeightModel;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import de.codecrafters.tableview.toolkit.TableDataRowBackgroundProviders;
import de.hdodenhof.circleimageview.CircleImageView;
import onair.onems.R;

/**
 * Created by hp on 12/5/2017.
 */

public class AllStudentAttendanceShow extends AppCompatActivity
{
    TableView tableView;

    private static final String[][] DATA_TO_SHOW = {{ "01", "1/10/10", "Yes", "" },{ "02", "2/10/10", "Yes", "20" } ,{ "01", "12/10/10(weekend)", "", "" }, { "02", "13/10/10", "No", " " },{ "03", "14/10/10", "Yes", "20" },{ "04", "15/11/10", "Yes", "" },{ "05", "15/10/11", "Yes", "5" },{ "01", "12/10/10(weekend)", "Yes", "" },{ "02", "13/10/10", "No", " " },{ "03", "14/10/10", "Yes", "20" },{ "04", "15/11/10", "Yes", "" },{ "05", "15/10/11(Holiday)", "", "" },{ "01", "12/10/10(weekend)", "Yes", "" },{ "02", "13/10/10", "No", " " },{ "03", "14/10/10", "Yes", "20" },{ "04", "15/11/10", "Yes", "" },{ "05", "15/10/11", "Yes", "5" },{ "01", "12/10/10(weekend)", "Yes", "" },{ "02", "13/10/10", "No", " " },{ "03", "14/10/10", "Yes", "20" },{ "04", "15/11/10", "Yes", "" },{ "05", "15/10/11", "Yes", "5" },{ "01", "12/10/10(weekend)", "Yes", "" },{ "02", "13/10/10", "No", " " }};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.other_attendance_show);
        tableView = (TableView) findViewById(R.id.tableView);

        final SimpleTableHeaderAdapter simpleTableHeaderAdapter = new SimpleTableHeaderAdapter(this, "SI","Date","Present", "Late(m)");
        simpleTableHeaderAdapter.setTextColor(ContextCompat.getColor(this, R.color.table_header_text));
        tableView.setHeaderAdapter(simpleTableHeaderAdapter);
        final SimpleTableDataAdapter simpleTabledataAdapter = new SimpleTableDataAdapter(this, DATA_TO_SHOW);
        tableView.setDataAdapter(simpleTabledataAdapter);
        int colorEvenRows = getResources().getColor(R.color.table_data_row_even);
        int colorOddRows = getResources().getColor(R.color.table_data_row_odd);
        tableView.setDataRowBackgroundProvider(TableDataRowBackgroundProviders.alternatingRowColors(colorEvenRows, colorOddRows));
        TableColumnWeightModel columnModel = new TableColumnWeightModel(4);

        columnModel.setColumnWeight(1, 2);

        tableView.setColumnModel(columnModel);
        Configuration config = getResources().getConfiguration();
        if (config.smallestScreenWidthDp > 320) {
            simpleTableHeaderAdapter.setTextSize(14);
            simpleTabledataAdapter.setTextSize(12);
        } else {
            simpleTableHeaderAdapter.setTextSize(10);
            simpleTabledataAdapter.setTextSize(10);
        }

        tableView.addDataClickListener(new TableDataClickListener() {
            @Override
            public void onDataClicked(int rowIndex, Object clickedData)
            {
                Intent i = new Intent(AllStudentAttendanceShow.this,AllStudentSubjectWiseAttendance.class);
                startActivity(i);
            }
        });
    }
    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), StudentAttendanceShow.class);
        startActivityForResult(myIntent, 0);
        finish();
        return true;

    }

}
