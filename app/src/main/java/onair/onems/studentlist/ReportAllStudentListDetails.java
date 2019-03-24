package onair.onems.studentlist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import onair.onems.R;
import onair.onems.Services.GlideApp;
import onair.onems.Services.RetrofitNetworkService;
import onair.onems.Services.StaticHelperClass;
import onair.onems.mainactivities.CommonToolbarParentActivity;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ReportAllStudentListDetails extends CommonToolbarParentActivity {
    private CompositeDisposable finalDisposer = new CompositeDisposable();
    private String RFID = "0", UserID = "0", studentImageUrl = "";
    private TextView name, designation, instituteName, studentName, session, medium, shift, classs, department, section, roll, rfid;
    private ConstraintLayout studentOtherInfo;
    private TableLayout attendanceInfoTable;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!finalDisposer.isDisposed())
            finalDisposer.dispose();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View childActivityLayout = Objects.requireNonNull(inflater).inflate(R.layout.profile, null);
        LinearLayout parentActivityLayout = findViewById(R.id.contentMain);
        parentActivityLayout.addView(childActivityLayout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        ImageView imageProfile, cover;
        attendanceInfoTable = findViewById(R.id.tableLayout1);
        studentOtherInfo = findViewById(R.id.studentOtherInfo);
        imageProfile = findViewById(R.id.profilePic);
        cover = findViewById(R.id.cover);
        name = findViewById(R.id.teacherName);
        designation = findViewById(R.id.designation);
        instituteName = findViewById(R.id.institute);
        studentName = findViewById(R.id.studentName);
        session = findViewById(R.id.session);
        medium = findViewById(R.id.medium);
        shift = findViewById(R.id.shift);
        classs = findViewById(R.id.classs);
        department = findViewById(R.id.department);
        section = findViewById(R.id.section);
        roll = findViewById(R.id.roll);
        rfid = findViewById(R.id.rfid);

        SharedPreferences sharedPre = PreferenceManager.getDefaultSharedPreferences(this);
        String InstituteNameString = sharedPre.getString("InstituteName","");
        instituteName.setText(InstituteNameString);
        designation.setText(R.string.student1);
        Intent intent = getIntent();
        UserID = intent.getStringExtra("UserID");
        RFID = intent.getStringExtra("RFID");
        studentImageUrl = intent.getStringExtra("ImageUrl");
        name.setText(intent.getStringExtra("UserName"));

        try {
            GlideApp.with(this)
                    .asBitmap()
                    .error(getResources().getDrawable(R.drawable.profileavater))
                    .load(getString(R.string.baseUrl)+"/"+studentImageUrl.replace("\\","/"))
                    .apply(RequestOptions.circleCropTransform())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, Transition<? super Bitmap> transition) {
                            if(resource != null) {
                                imageProfile.setImageBitmap(resource);
                            }
                        }
                        @Override
                        public void onLoadFailed(Drawable errorDrawable) {
                            super.onLoadFailed(errorDrawable);
                            imageProfile.setImageDrawable(errorDrawable);
                            Toast.makeText(ReportAllStudentListDetails.this,"No image found!!!",Toast.LENGTH_LONG).show();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

        cover.setImageDrawable(getResources().getDrawable(R.drawable.cover));
        attendanceInfoTable.setVisibility(View.VISIBLE);
        studentOtherInfo.setVisibility(View.VISIBLE);
        studentInformationGetRequest(RFID, UserID);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void studentInformationGetRequest( String RFID, String UserID) {
        if (StaticHelperClass.isNetworkAvailable(this)) {
            dialog.show();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(getString(R.string.baseUrl))
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();

            Observable<String> observable = retrofit
                    .create(RetrofitNetworkService.class)
                    .getStudentInformation(RFID, UserID)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io());

            finalDisposer.add( observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io())
                    .subscribeWith(new DisposableObserver<String>() {

                        @Override
                        public void onNext(String response) {
                            dialog.dismiss();
                            parseStudentInformation(response);
                        }

                        @Override
                        public void onComplete() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                            Toast.makeText(ReportAllStudentListDetails.this,"Error: getting student data!!!",Toast.LENGTH_LONG).show();
                        }
                    }));
        } else {
            Toast.makeText(ReportAllStudentListDetails.this,"Please check your internet connection and select again!!! ",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void parseStudentInformation(String response) {
        if (!response.equalsIgnoreCase("null") && !response.equalsIgnoreCase("[]")) {
            try {
                JsonParser parser = new JsonParser();
                JsonArray jsonArray = parser.parse(response).getAsJsonArray();
                if (jsonArray.size() > 0) {
                    JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
                    if (!jsonObject.get("SessionName").isJsonNull()) {
                        session.setText(jsonObject.get("SessionName").getAsString());
                    }
                    if (!jsonObject.get("MameName").isJsonNull()) {
                        medium.setText(jsonObject.get("MameName").getAsString());
                    }
                    if (!jsonObject.get("ShiftName").isJsonNull()) {
                        shift.setText(jsonObject.get("ShiftName").getAsString());
                    }
                    if (!jsonObject.get("ClassName").isJsonNull()) {
                        classs.setText(jsonObject.get("ClassName").getAsString());
                    }
                    if (!jsonObject.get("DepartmentName").isJsonNull()) {
                        department.setText(jsonObject.get("DepartmentName").getAsString());
                    }
                    if (!jsonObject.get("SectionName").isJsonNull()) {
                        section.setText(jsonObject.get("SectionName").getAsString());
                    }
                    if (!jsonObject.get("RollNo").isJsonNull()) {
                        roll.setText(jsonObject.get("RollNo").getAsString());
                    }
                    if (!jsonObject.get("RFID").isJsonNull()) {
                        rfid.setText(jsonObject.get("RFID").getAsString());
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
