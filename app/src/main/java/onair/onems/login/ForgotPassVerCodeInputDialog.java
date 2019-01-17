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
    private CompositeDisposable finalDisposer = new CompositeDisposable();
    public CommonProgressDialog dialog;

    @Override
    public void dismiss() {
        super.dismiss();
        if (!finalDisposer.isDisposed())
            finalDisposer.dispose();
    }

    ForgotPassVerCodeInputDialog(Activity activity, Context context, String returnValue, String selectedWay, String sentVerificationCode) {
        super(activity);
        this.parentActivity = activity;
        this.context = context;
        this.returnValue = returnValue;
        this.selectedWay = selectedWay;
        this.sentVerificationCode = sentVerificationCode;
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
                resendVerifyCode();
            default:
                break;
        }
    }

    private void verifyCode() {
        if(sentVerificationCode.equals(input.getText().toString())) {
            dismiss();
            ForgotPassChangePassDialog forgotPassChangePassDialog = new ForgotPassChangePassDialog(parentActivity, context, returnValue);
            forgotPassChangePassDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            forgotPassChangePassDialog.setCancelable(false);
            forgotPassChangePassDialog.show();
        } else {
            Toast.makeText(context,"Invalid verification code !!!",Toast.LENGTH_LONG).show();
        }
    }

    private void resendVerifyCode() {
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
                            parseReturnData(response);
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

    private void parseReturnData(String string) {
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
}
