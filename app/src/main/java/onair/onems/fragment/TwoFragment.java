package onair.onems.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.dd.morphingbutton.MorphingButton;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import de.codecrafters.tableview.TableView;
import onair.onems.R;
import onair.onems.mainactivities.StudentAttendanceShow;
//import onair.onems.Student_Attendance_Show;


public class TwoFragment extends Fragment {
    private View rootView;
    MaterialSpinner classSpinner,sectionSpinner,mediumSpinner,monthSpinner,shiftSpinner;
    TableView tableView;

    Fragment fragment;
    FragmentManager fragmentManager;
    Handler handler;
    Button student_find_button;
    private boolean shouldRefreshOnResume = false;
    private static int SPLASH_TIME_OUT = 1000;
    Runnable refresh;
    List<String> dataClass = new LinkedList<>(Arrays.asList("One", "Two", "Three", "Four", "Five","Six","Seven","Eight","Nine","Ten"));
    List<String> dataShift = new LinkedList<>(Arrays.asList("Morning","Day"));
    List<String> dataSection = new LinkedList<>(Arrays.asList("A","B"));
    List<String> dataMedium = new LinkedList<>(Arrays.asList("Bangla","English"));
    private static final String[][] DATA_TO_SHOW = { { "01", "Hasnat", "01", "01" },{ "01", "Hasnat", "01", "01" },{ "01", "Hasnat", "01", "01" },{ "01", "Hasnat", "01", "01" },{ "01", "Hasnat", "01", "01" },
            { "02", "Bony", "02", "02" },{"03","Jony","03","03"},{"04","Yamin","04","04"},{"05","Alamin","05","05"},{"05","Alamin","05","05"},{"05","Alamin","05","05"},{"05","Alamin","05","05"},{"05","Alamin","05","05"},{"05","Alamin","05","05"},{"05","Alamin","05","05"},{"05","Alamin","05","05"},{"05","Alamin","05","05"} };
    public TwoFragment() {
        // Required empty public constructor
    }
    public static TwoFragment newInstance() {
        TwoFragment fragment = new TwoFragment();
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {


        rootView = inflater.inflate(R.layout.others_attendence, container, false);
        classSpinner = (MaterialSpinner) rootView.findViewById(R.id.spinner_Class);
        shiftSpinner = (MaterialSpinner) rootView.findViewById(R.id.spinner_Shift);
        mediumSpinner= (MaterialSpinner) rootView.findViewById(R.id.spinner_medium);
        sectionSpinner = (MaterialSpinner) rootView.findViewById(R.id.spinner_Section);
        monthSpinner = (MaterialSpinner) rootView.findViewById(R.id.spinner_Month);

        student_find_button=(Button) rootView.findViewById(R.id.show_button);
               student_find_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), StudentAttendanceShow.class);
                startActivity(i);
                getActivity().finish();

            }
        });

          classSpinner.setItems(dataClass);
          shiftSpinner.setItems(dataShift);
          mediumSpinner.setItems(dataMedium);
          sectionSpinner.setItems(dataSection);
          monthSpinner.setItems("January", "February", "March", "April", "May","June","July","August","September","October","November","December");

        tableView = (TableView)rootView.findViewById(R.id.tableView);

        classSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



            }
        });
        return rootView;
    }


    @Override
    public void onStop() {
        super.onStop();
        shouldRefreshOnResume = true;
    }

}
