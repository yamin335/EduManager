package onair.onems.fee;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.craftman.cardform.Card;
import com.craftman.cardform.CardForm;
import com.craftman.cardform.OnPayBtnClickListner;

import onair.onems.R;

/**
 * Created by hp on 1/23/2018.
 */

public class payment extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment);
        CardForm cardForm=(CardForm)findViewById(R.id.card);
        TextView textdev=(TextView)findViewById(R.id.payment_amount);
        Button btnpay=(Button)findViewById(R.id.btn_pay);
        textdev.setText("$11172");
        btnpay.setText(String.format("Payer %s",textdev.getText()));

        cardForm.setPayBtnClickListner(new OnPayBtnClickListner() {
            @Override
            public void onClick(Card card) {
                Toast.makeText(payment.this,"Transaction Complete",Toast.LENGTH_LONG).show();
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
//        NavUtils.navigateUpFromSameTask(this);
        super.onBackPressed();
    }
    // Back parent Page code end
}
