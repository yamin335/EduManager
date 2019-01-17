package onair.onems.mainactivities;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import onair.onems.R;
import onair.onems.login.LoginScreen;

public class SplashScreen extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 4000;
    private TextView AppName, CopyRight;
    private ImageView Logo;
    private Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // If the Android version is lower than Jellybean, use this call to hide
        // the status bar.
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.splash_activity);
        AppName = findViewById(R.id.splashText);
        CopyRight = findViewById(R.id.copyright);
        Logo = findViewById(R.id.splashImage);
        if (savedInstanceState == null){
            flyIn();
        }

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                endSplash();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    private void flyIn() {
        animation = AnimationUtils.loadAnimation(this, R.anim.logo_animation);
        Logo.startAnimation(animation);

        animation = AnimationUtils.loadAnimation(this,
                R.anim.app_name_animation);
        AppName.startAnimation(animation);

        animation = AnimationUtils.loadAnimation(this, R.anim.pro_animation);
        CopyRight.startAnimation(animation);
    }

    private void endSplash() {
        animation = AnimationUtils.loadAnimation(this,
                R.anim.logo_animation_back);
        Logo.startAnimation(animation);

        animation = AnimationUtils.loadAnimation(this,
                R.anim.app_name_animation_back);
        AppName.startAnimation(animation);

        animation = AnimationUtils.loadAnimation(this,
                R.anim.pro_animation_back);
        CopyRight.startAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
                Intent mainIntent = new Intent(SplashScreen.this, LoginScreen.class);
                SplashScreen.this.startActivity(mainIntent);
                SplashScreen.this.finish();
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationStart(Animation arg0) {
            }
        });

    }

}
