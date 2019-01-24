package onair.onems.login;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class ForgotPassVerCodeInputDialog extends Dialog implements View.OnClickListener {
    private Activity parentActivity;
    private Context context;
    private String returnValue, selectedWay, sentVerificationCode;
    private EditText input;
    private boolean isPhone = false, isMail = false;
    private long InstituteID;
    private CompositeDisposable finalDisposer = new CompositeDisposable();
    public CommonProgressDialog dialog;

    @Override
    public void dismiss() {
        super.dismiss();
        if (!finalDisposer.isDisposed())
            finalDisposer.dispose();
    }

    ForgotPassVerCodeInputDialog(Activity activity, Context context, long InstituteID, String returnValue, String selectedWay, boolean isPhone, boolean isMail, String sentVerificationCode) {
        super(activity);
        this.parentActivity = activity;
        this.context = context;
        this.returnValue = returnValue;
        this.selectedWay = selectedWay;
        this.isPhone = isPhone;
        this.isMail = isMail;
        this.sentVerificationCode = sentVerificationCode;
        this.InstituteID = InstituteID;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.forgot_pass_verification);
        dialog = new CommonProgressDialog(parentActivity);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        input = findViewById(R.id.input);
        TextView resendTextView = findViewById(R.id.resend);
        Button next = findViewById(R.id.next);
        Button cross = findViewById(R.id.cross);
        resendTextView.setOnClickListener(this);
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
                verifyCode();
                break;
            case R.id.resend:
                resendVerifyCode(isPhone, isMail);
            default:
                break;
        }
    }

    private void verifyCode() {
        if(sentVerificationCode.equals(input.getText().toString())) {
            ForgotPassChangePassDialog forgotPassChangePassDialog = new ForgotPassChangePassDialog(parentActivity, context, returnValue);
            Objects.requireNonNull(forgotPassChangePassDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            forgotPassChangePassDialog.setCancelable(false);
            forgotPassChangePassDialog.show();
            dismiss();
        } else {
            Toast.makeText(context,"Invalid verification code !!!",Toast.LENGTH_LONG).show();
        }
    }

    private void resendVerifyCode(boolean isPhone, boolean isMail) {
        if(StaticHelperClass.isNetworkAvailable(context)) {
            dialog.show();
            int verificationCode = new Random().nextInt(900000)+100000;
            sentVerificationCode = Integer.toString(verificationCode);
            if (isPhone) {
                getInstituteSmsToken(verificationCode);
            } else if (isMail) {
                sendVerifyCodeViaMail(verificationCode);
            }
        } else {
            Toast.makeText(context,"Please check your INTERNET connection !!!",Toast.LENGTH_LONG).show();
        }
    }

    private void sendVerifyCodeViaMail(int verificationCode){
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
                sentVerificationCode = RandomNo;
                Toast.makeText(context,"Verification code sent !!!",Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            dismiss();
            Toast.makeText(context,"Error in json parsing !!!",Toast.LENGTH_LONG).show();
        }
    }

    private void getInstituteSmsToken(int verificationCode){
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
                            parseTokenData(response, verificationCode);
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

    private void parseTokenData(String token, int verificationCode) {
        try {
            JSONArray jsonArray = new JSONArray(token);
            token = jsonArray.getJSONObject(0).getString("InsToken");
            if (!token.equalsIgnoreCase("null")) {
                sendVerifyCodeViaSMS(token, verificationCode);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendVerifyCodeViaSMS(String token, int verificationCode){
        if(StaticHelperClass.isNetworkAvailable(context)) {
            dialog.show();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://sms.greenweb.com.bd")
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();

            Observable<String> observable = retrofit
                    .create(RetrofitNetworkService.class)
                    .sendVerificationCodeViaSMS(token, selectedWay, "Your onEMS password reset code is "+verificationCode)
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

    private void parseSmsReturnData(String response) {
        if(response.contains("Ok: SMS Sent Successfully")) {
            Toast.makeText(context,"Verification code sent !!!", Toast.LENGTH_LONG).show();
        } else {
            dismiss();
            Toast.makeText(context,"Error sending verification code !!!", Toast.LENGTH_LONG).show();
        }
    }
}
