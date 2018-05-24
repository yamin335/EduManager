package onair.onems.syllabus;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.Calendar;

import onair.onems.R;
import onair.onems.attendance.TakeAttendance;

public class FloatingMenuDialog extends Dialog implements View.OnClickListener{
    private Context context;
    private Activity currentActivity;
    private FloatingMenuListener listener;
    private String selectedDate;
    public FloatingMenuDialog(Activity a, Context context, FloatingMenuListener listener) {
        super(a);
        this.context = context;
        this.currentActivity = a;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.syllabus_floating_menu_dialog);
        RelativeLayout exam = findViewById(R.id.selectExam);
        RelativeLayout date = findViewById(R.id.selectDate);

        exam.setOnClickListener(this);
        date.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.selectExam:
                listener.onFloatingMenuItemSelected(R.id.selectExam, "");
                break;
            case R.id.selectDate:
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
//                                selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
//                                selectedDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                                selectedDate = (monthOfYear + 1)+"-"+dayOfMonth+"-"+year;
                                listener.onFloatingMenuItemSelected(R.id.selectDate, selectedDate);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
                break;
            default:
                break;
        }
        dismiss();
    }

    public interface FloatingMenuListener {
        void onFloatingMenuItemSelected(int menuId, String date);
    }
}

