package onair.onems.mainactivities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.model.TableColumnWeightModel;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import de.codecrafters.tableview.toolkit.TableDataRowBackgroundProviders;
import onair.onems.R;

/**
 * Created by hp on 12/5/2017.
 */

public class AllStudentSubjectWiseAttendance extends AppCompatActivity
{

    TableView tableView;
    private static final String[][] DATA_TO_SHOW = {{"Bangla", "012", "Present", "Mr. Bony Israil"}, {"English", "014", "Late 20 min", "Mr. Alamin Hossain"}, {"Social Science", "016", "Absent", "Mr. Yamin Mollah"}, {"Math", "018", "Present", "Mr. Abdul Kalam"}, {"Bangla", "01", "Late 15 min", "Mr. Shibli"},{"Biology", "20", "Present", "Mr. Kalam"} ,{"Biology", "20", "Present", "Mr. Kalam"},{"Chemistry", "20", "Absent", "Mr. Bony"},{"Biology", "20", "Present", "Mr. Kalam"}};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_subject_wise_attendance);
        tableView = (TableView) findViewById(R.id.tableView);
        final SimpleTableHeaderAdapter simpleTableHeaderAdapter = new SimpleTableHeaderAdapter(this, "Subject Name", "Code ", "Status", "Teacher");
        simpleTableHeaderAdapter.setTextColor(ContextCompat.getColor(this, R.color.table_header_text));
        tableView.setHeaderAdapter(simpleTableHeaderAdapter);
        final SimpleTableDataAdapter simpleTabledataAdapter = new SimpleTableDataAdapter(this, DATA_TO_SHOW);
        tableView.setDataAdapter(simpleTabledataAdapter);
        int colorEvenRows = getResources().getColor(R.color.table_data_row_even);
        int colorOddRows = getResources().getColor(R.color.table_data_row_odd);
        tableView.setDataRowBackgroundProvider(TableDataRowBackgroundProviders.alternatingRowColors(colorEvenRows, colorOddRows));
        TableColumnWeightModel columnModel = new TableColumnWeightModel(4);
        columnModel.setColumnWeight(3, 5);
        columnModel.setColumnWeight(1, 3);
        columnModel.setColumnWeight(2, 4);
        columnModel.setColumnWeight(0, 4);
        tableView.setColumnModel(columnModel);
        Configuration config = getResources().getConfiguration();
        if (config.smallestScreenWidthDp > 320) {
            simpleTableHeaderAdapter.setTextSize(14);
            simpleTabledataAdapter.setTextSize(12);
        } else {
            simpleTableHeaderAdapter.setTextSize(10);
            simpleTabledataAdapter.setTextSize(10);
        }


    }
    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), AllStudentAttendanceShow.class);
        startActivityForResult(myIntent, 0);
        finish();
        return true;

    }

}
