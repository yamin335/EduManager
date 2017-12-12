package onair.onems.mainactivities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import onair.onems.R;

/**
 * Created by User on 12/5/2017.
 */

public class LoginScreen extends AppCompatActivity {

    private Button loginButton;
    private EditText takeId;
    private EditText takePassword;
    private String id = "",password = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //SERVER Test...
        //YAMIN Test...
        //BONY Test...

        loginButton = (Button)findViewById(R.id.login_button);
        takeId = (EditText)findViewById(R.id.id);
        takePassword = (EditText) findViewById(R.id.password);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                id = takeId.getText().toString();
                password = takePassword.getText().toString();
                if((id.equals("11"))&&(password.equals("11")))
                {
                    Intent mainIntent = new Intent(LoginScreen.this,StudentMainScreen.class);
                    LoginScreen.this.startActivity(mainIntent);
                    LoginScreen.this.finish();
                }
                else if((id.equals("22"))&&(password.equals("22")))
                {
                    Intent mainIntent = new Intent(LoginScreen.this,TeacherMainScreen.class);
                    LoginScreen.this.startActivity(mainIntent);
                    LoginScreen.this.finish();
                }
                else
                {
                    Toast.makeText(LoginScreen.this,"Please enter valid id",Toast.LENGTH_LONG).show();
                }

//                Intent mainIntent = new Intent(LoginScreen.this,TeacherMainScreen.class);
//                LoginScreen.this.startActivity(mainIntent);
//                LoginScreen.this.finish();
            }
        });



    }
}
