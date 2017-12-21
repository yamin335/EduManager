package onair.onems.mainactivities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import onair.onems.R;
import onair.onems.customadapters.AttendanceSheetAdapter;

/**
 * Created by User on 12/20/2017.
 */

public class TakeAttendanceDetails extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AttendanceSheetAdapter adapter;
    private List<AttendanceSheet> attendanceSheetList;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_attendance_screen);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        attendanceSheetList = new ArrayList<>();
        adapter = new AttendanceSheetAdapter(this, attendanceSheetList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        //recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        prepareAlbums();

    }

    private void prepareAlbums() {
        int[] covers = new int[]{
                R.drawable.album1,
                R.drawable.album2,
                R.drawable.album3,
                R.drawable.album4,
                R.drawable.album5,
                R.drawable.album6,
                R.drawable.album7,
                R.drawable.album8,
                R.drawable.album9,
                R.drawable.album10,
                R.drawable.album11};

        AttendanceSheet a = new AttendanceSheet("True Romance", 13);
        attendanceSheetList.add(a);

        a = new AttendanceSheet("Xscpae", 8);
        attendanceSheetList.add(a);

        a = new AttendanceSheet("Maroon 5", 11);
        attendanceSheetList.add(a);

        a = new AttendanceSheet("Born to Die", 12);
        attendanceSheetList.add(a);

        a = new AttendanceSheet("Honeymoon", 14);
        attendanceSheetList.add(a);

        a = new AttendanceSheet("I Need a Doctor", 1);
        attendanceSheetList.add(a);

        a = new AttendanceSheet("Loud", 11);
        attendanceSheetList.add(a);

        a = new AttendanceSheet("Legend", 14);
        attendanceSheetList.add(a);

        a = new AttendanceSheet("Hello", 11);
        attendanceSheetList.add(a);

        a = new AttendanceSheet("Greatest Hits", 17);
        attendanceSheetList.add(a);

        adapter.notifyDataSetChanged();
    }
}
