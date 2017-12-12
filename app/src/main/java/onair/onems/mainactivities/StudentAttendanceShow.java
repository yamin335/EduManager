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
import onair.onems.R;

/**
 * Created by hp on 12/4/2017.
 */

public class StudentAttendanceShow extends AppCompatActivity {
    TableView tableView;
    private static final String[][] DATA_TO_SHOW = {{"01", "MD. Bony Israil", "1235", "14"}, {"02", "MD. Bony Israil", "1235", "14"}, {"03", "MD. Bony Israil", "1235", "14"}, {"04", "MD. Bony Israil", "1235", "14"}, {"01", "Hasnat", "01", "01"}, {"01", "Hasnat", "01", "01"}, {"01", "Hasnat", "01", "01"}, {"01", "Hasnat", "01", "01"},
            {"02", "Bony", "02", "02"}, {"03", "Jony", "03", "03"}, {"04", "Yamin", "04", "04"}, {"05", "Alamin", "05", "05"}, {"05", "Alamin", "05", "05"}, {"05", "Alamin", "05", "05"}, {"05", "Alamin", "05", "05"}, {"05", "Alamin", "05", "05"}, {"05", "Alamin", "05", "05"}, {"05", "Alamin", "05", "05"}, {"05", "Alamin", "05", "05"}, {"05", "Alamin", "05", "05"}};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_student_attendance_show);
        tableView = (TableView) findViewById(R.id.tableView);
        final SimpleTableHeaderAdapter simpleTableHeaderAdapter = new SimpleTableHeaderAdapter(this, "SI", "Student Name", "ID", "Roll");
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
            simpleTableHeaderAdapter.setTextSize(16);
            simpleTabledataAdapter.setTextSize(14);
        } else {
            simpleTableHeaderAdapter.setTextSize(10);
            simpleTabledataAdapter.setTextSize(10);
        }

        tableView.addDataClickListener(new TableDataClickListener() {
            @Override
            public void onDataClicked(int rowIndex, Object clickedData)
            {
                Intent i = new Intent(StudentAttendanceShow.this, AllStudentAttendanceShow.class);
                startActivity(i);
            }
        });

    }
    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), StudentAttendance.class);
        startActivityForResult(myIntent, 0);
        finish();
        return true;

    }


}
