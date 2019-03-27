package onair.onems.fee;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import onair.onems.R;

public class payment extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment);
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
//        NavUtils.navigateUpFromSameTask(this);
        super.onBackPressed();
    }
    // Back parent Page code end
}
