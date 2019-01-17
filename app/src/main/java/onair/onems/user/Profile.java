package onair.onems.user;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;

import java.util.Objects;

import onair.onems.R;
import onair.onems.Services.GlideApp;
import onair.onems.mainactivities.SideNavigationMenuParentActivity;
import onair.onems.mainactivities.StudentMainScreen;
import onair.onems.mainactivities.TeacherMainScreen;

public class Profile extends SideNavigationMenuParentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        activityName = Profile.class.getName();

        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View childActivityLayout = Objects.requireNonNull(inflater).inflate(R.layout.profile, null);
        LinearLayout parentActivityLayout = findViewById(R.id.contentMain);
        parentActivityLayout.addView(childActivityLayout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        ImageView imageProfile, cover;
        TextView name, designation;
        imageProfile = findViewById(R.id.imageView6);
        cover = findViewById(R.id.cover);
        name = findViewById(R.id.textView);
        designation = findViewById(R.id.textView7);

        name.setText(UserFullName);
        GlideApp.with(this)
                .load(getString(R.string.baseUrl)+"/"+ImageUrl.replace("\\","/")).apply(RequestOptions.circleCropTransform())
                .into(imageProfile);
        cover.setImageDrawable(getResources().getDrawable(R.drawable.cover));

        switch (UserTypeID) {
            case 1:
                designation.setText(R.string.super_admin);
                break;
            case 2:
                designation.setText(R.string.admin);
                break;
            case 3:
                designation.setText(R.string.student1);
                break;
            case 4:
                designation.setText(R.string.teacher);
                break;
            case 5:
                designation.setText(R.string.guardian1);
                break;
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(UserTypeID == 1) {
            Intent mainIntent = new Intent(Profile.this,TeacherMainScreen.class);
            startActivity(mainIntent);
            finish();
        } else if(UserTypeID == 2) {
            Intent mainIntent = new Intent(Profile.this,TeacherMainScreen.class);
            startActivity(mainIntent);
            finish();
        } else if(UserTypeID == 3) {
            Intent mainIntent = new Intent(Profile.this,StudentMainScreen.class);
            startActivity(mainIntent);
            finish();
        } else if(UserTypeID == 4) {
            Intent mainIntent = new Intent(Profile.this,TeacherMainScreen.class);
            startActivity(mainIntent);
            finish();
        } else if(UserTypeID == 5) {
            Intent mainIntent = new Intent(Profile.this,StudentMainScreen.class);
            startActivity(mainIntent);
            finish();
        }
    }
}
