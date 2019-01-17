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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import onair.onems.R;
import onair.onems.Services.RetrofitNetworkService;
import onair.onems.Services.StaticHelperClass;
import onair.onems.mainactivities.CommonProgressDialog;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ForgotPassInputDialog extends Dialog implements View.OnClickListener {
    private Activity parentActivity;
    private Context context;
    private EditText input;
    private Disposable finalDisposer;
    public CommonProgressDialog dialog;

    @Override
    public void dismiss() {
        super.dismiss();
        if (!finalDisposer.isDisposed())
            finalDisposer.dispose();
    }

    ForgotPassInputDialog(Activity activity, Context context) {
        super(activity);
        this.parentActivity = activity;
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.forgot_pass_input_email);
        dialog = new CommonProgressDialog(parentActivity);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        input = findViewById(R.id.input);
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
                verifyInput(input.getText().toString());
                break;
            default:
                break;
        }
    }

    private void verifyInput(String input) {
        if(input.equals("")) {
            Toast.makeText(context,"Please input email, phone or username !!!",Toast.LENGTH_LONG).show();
        } else {
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
                        .getUserEmailPhoneIfExist(input)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .unsubscribeOn(Schedulers.io());

                observable
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .unsubscribeOn(Schedulers.io())
                        .subscribe(new Observer<String>() {

                            @Override
                            public void onSubscribe(Disposable d) {
                                finalDisposer = d;
                            }

                            @Override
                            public void onNext(String response) {
                                dialog.dismiss();
                                parseReturnData(response);
                            }

                            @Override
                            public void onComplete() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                dialog.dismiss();
                                dismiss();
                                Toast.makeText(context,"SERVER Error !!!",Toast.LENGTH_LONG).show();
                            }
                        });
            } else {
                Toast.makeText(context,"Please check your INTERNET connection !!!",Toast.LENGTH_LONG).show();
            }
        }
    }

    private void parseReturnData(String string) {
        try {
            JSONObject returnValue = new JSONArray(string).getJSONObject(0);
            int result = returnValue.getInt("result");
            if(result == 1) {
                dismiss();
                ForgotPassSelectDialog forgotPassSelectDialog = new ForgotPassSelectDialog(parentActivity, context, returnValue.toString());
                Objects.requireNonNull(forgotPassSelectDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                forgotPassSelectDialog.setCancelable(false);
                forgotPassSelectDialog.show();
            } else {
                dismiss();
                Toast.makeText(context,"please input valid email, phone or username !!!", Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            dismiss();
            Toast.makeText(context,"Error in JsonParsing !!!", Toast.LENGTH_LONG).show();
        }
    }
}
