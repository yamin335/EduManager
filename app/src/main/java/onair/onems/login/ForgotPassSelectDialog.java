package onair.onems.login;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;
import java.util.Random;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import onair.onems.R;
import onair.onems.Services.RetrofitNetworkService;
import onair.onems.Services.StaticHelperClass;
import onair.onems.mainactivities.CommonProgressDialog;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ForgotPassSelectDialog extends Dialog implements View.OnClickListener {
    private Activity parentActivity;
    private Context context;
    private String returnValue, selectedWay, smail, sphone;
    private boolean isPhoneOk = false, isMailOk = false;
    private long InstituteID;
    private boolean isPhone = false, isMail = false;
    private CompositeDisposable finalDisposer = new CompositeDisposable();
    public CommonProgressDialog dialog;

    @Override
    public void dismiss() {
        super.dismiss();
        if (!finalDisposer.isDisposed())
            finalDisposer.dispose();
    }

    ForgotPassSelectDialog(Activity activity, Context context, String returnValue) {
        super(activity);
        this.parentActivity = activity;
        this.context = context;
        this.returnValue = returnValue;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.forgot_pass_select_dialog);
        dialog = new CommonProgressDialog(parentActivity);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        RadioButton mail = findViewById(R.id.mail);
        RadioButton phone = findViewById(R.id.phone);
        try {
            JSONObject jsonObject = new JSONObject(returnValue);
            smail = jsonObject.getString("LoginEmail");
            sphone = jsonObject.getString("LoginPhone");
            InstituteID = jsonObject.getLong("InstituteID");
            if(Patterns.EMAIL_ADDRESS.matcher(smail).matches()) {
                int a = smail.indexOf("@");
                int b = smail.lastIndexOf(".");
                String c = smail.substring(a+1, b);
                String radio = smail.replace(c, "***");
                String radioMail;
                if(a<2) {
                    String d = smail.substring(0, 0);
                    radioMail = radio.replace(d, "*****");
                } else if(a<3) {
                    String e = smail.substring(1, 1);
                    radioMail = radio.replace(e, "*****");
                } else if(a<4) {
                    String f = smail.substring(1, 2);
                    radioMail = radio.replace(f, "*****");
                } else {
                    String g = smail.substring(2, a);
                    radioMail = radio.replace(g, "*****");
                }
                mail.setText(radioMail);
                isMailOk = true;
            }

            if(sphone.length()>10) {
                String s = sphone.substring(3, 9);
                String radioPhone = sphone.replace(s, "******");
                phone.setText(radioPhone);
                isPhoneOk = true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.mail:
                    selectedWay = smail;
                    isMail = true;
                    isPhone = false;
                    break;
                case R.id.phone:
                    selectedWay = sphone;
                    isMail = false;
                    isPhone = true;
                    break;
                case R.id.dont:
                    selectedWay = "don't";
                    break;
            }
        });
        Button next = findViewById(R.id.next);
        Button cross = findViewById(R.id.cross);
        next.setOnClickListener(this);
        cross.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cross:
                dismiss();
                break;
            case R.id.next:
                sendVerifyCode();
                break;
            default:
                break;
        }
    }

    private void sendVerifyCode() {
        if(isMailOk && isMail) {
            sendVerifyCodeViaMail();
        } else if(isPhoneOk && isPhone) {
            getInstituteSmsToken();
        } else if(selectedWay.equals("don't")) {
            Toast.makeText(context,"Please try a valid authentication !!!",Toast.LENGTH_LONG).show();
            dismiss();
        }
    }

    private void sendVerifyCodeViaMail(){
        if(StaticHelperClass.isNetworkAvailable(context)) {
            dialog.show();
            int verificationCode = new Random().nextInt(900000)+100000;
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(context.getString(R.string.baseUrl))
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();

            Observable<String> observable = retrofit
                    .create(RetrofitNetworkService.class)
                    .sendCodeThrouMail(selectedWay, verificationCode)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io());

            finalDisposer.add( observable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io())
                    .subscribeWith(new DisposableObserver<String>() {

                        @Override
                        public void onNext(String response) {
                            dialog.dismiss();
                            parseMailReturnData(response);
                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                            dismiss();
                            Toast.makeText(context,"SERVER Error !!!",Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    }));
        } else {
            Toast.makeText(context,"Please check your INTERNET connection !!!",Toast.LENGTH_LONG).show();
        }
    }

    private void parseMailReturnData(String string) {
        try {
            JSONObject jsonObject = new JSONObject(string);
            String RandomNo = jsonObject.getString("RandomNo");
            String status = jsonObject.getString("Message");
            if(RandomNo.length()>3&&status.equalsIgnoreCase("sent")) {
                ForgotPassVerCodeInputDialog forgotPassVerCodeInputDialog = new ForgotPassVerCodeInputDialog(parentActivity, context, returnValue, selectedWay, RandomNo);
                forgotPassVerCodeInputDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                forgotPassVerCodeInputDialog.setCancelable(false);
                forgotPassVerCodeInputDialog.show();
                dismiss();
            } else {
                dismiss();
                Toast.makeText(context,"Error sending verification code !!!",Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            dismiss();
            Toast.makeText(context,"Error in json parsing !!!",Toast.LENGTH_LONG).show();
        }
    }

    private void getInstituteSmsToken(){
        if(StaticHelperClass.isNetworkAvailable(context)) {
            dialog.show();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(context.getString(R.string.baseUrl))
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();

            Observable<String> observable = retrofit
                    .create(RetrofitNetworkService.class)
                    .getInstituteAvailableSMS(InstituteID)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io());

            finalDisposer.add( observable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io())
                    .subscribeWith(new DisposableObserver<String>() {

                        @Override
                        public void onNext(String response) {
                            dialog.dismiss();
                            parseTokenData(response);
                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                            dismiss();
                            Toast.makeText(context,"Error getting sms token !!!",Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    }));
        } else {
            Toast.makeText(context,"Please check your INTERNET connection !!!",Toast.LENGTH_LONG).show();
        }
    }

    private void parseTokenData(String token) {
        try {
            JSONArray jsonArray = new JSONArray(token);
            token = jsonArray.getJSONObject(0).getString("InsToken");
            if (!token.equalsIgnoreCase("null")) {
                sendVerifyCodeViaSMS(token);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendVerifyCodeViaSMS(String token){
        if(StaticHelperClass.isNetworkAvailable(context)) {
            dialog.show();
            int verificationCode = new Random().nextInt(900000)+100000;
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://sms.greenweb.com.bd")
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();

            Observable<String> observable = retrofit
                    .create(RetrofitNetworkService.class)
                    .sendVerificationCodeViaSMS(token, selectedWay, verificationCode)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io());

            finalDisposer.add( observable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io())
                    .subscribeWith(new DisposableObserver<String>() {

                        @Override
                        public void onNext(String response) {
                            dialog.dismiss();
                            parseSmsReturnData(response);
                        }

                        @Override
                        public void onError(Throwable e) {
                            dialog.dismiss();
                            dismiss();
                            Toast.makeText(context,"Error sending verification code!!!",Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    }));
        } else {
            Toast.makeText(context,"Please check your INTERNET connection !!!",Toast.LENGTH_LONG).show();
        }
    }

    private void parseSmsReturnData(String string) {
        String returnMessage = string;
    }

}
