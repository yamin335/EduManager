package onair.onems.login;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import onair.onems.R;
import onair.onems.Services.RetrofitNetworkService;
import onair.onems.Services.StaticHelperClass;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ChangePasswordDialog extends Dialog implements View.OnClickListener {
    private Activity parentActivity;
    private Context context;
    private EditText currentPassword, newPassword, confirmPassword;
    private String LoggedUserID, UserName, Password, confirmP;
    private long InstituteID;
    private Disposable finalDisposer;

    @Override
    public void dismiss() {
        super.dismiss();
        if (!finalDisposer.isDisposed())
            finalDisposer.dispose();
    }

    public ChangePasswordDialog(Activity activity, Context context, String LoggedUserID, String UserName, long InstituteID) {
        super(activity);
        this.parentActivity = activity;
        this.context = context;
        this.LoggedUserID = LoggedUserID;
        this.UserName = UserName;
        this.InstituteID = InstituteID;
        this.Password = PreferenceManager.getDefaultSharedPreferences(context).getString("Password", "");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.change_password_dialog);
        Button cross = findViewById(R.id.cross);
        Button done = findViewById(R.id.done);
        currentPassword = findViewById(R.id.currentPassword);
        newPassword = findViewById(R.id.newPassword);
        confirmPassword = findViewById(R.id.confirmPassword);
        cross.setOnClickListener(this);
        done.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cross:
                dismiss();
                break;
            case R.id.done:
                changePassword();
                break;
            default:
                break;
        }
    }

    private void changePassword() {
        String currentP = currentPassword.getText().toString();
        String newP = newPassword.getText().toString();
        confirmP = confirmPassword.getText().toString();

        if(!currentP.equals(Password)) {
            if(currentP.equals("")) {
                currentPassword.requestFocus();
                Toast.makeText(context,"Please enter current password !!!",Toast.LENGTH_LONG).show();
            } else {
                currentPassword.setText("");
                currentPassword.requestFocus();
                Toast.makeText(context,"Current password is invalid !!!",Toast.LENGTH_LONG).show();
            }
        } else if(newP.equals("")) {
            newPassword.requestFocus();
            Toast.makeText(context,"Please enter new password !!!",Toast.LENGTH_LONG).show();
        } else if(confirmP.equals("")) {
            confirmPassword.requestFocus();
            Toast.makeText(context,"Please enter confirm password !!!",Toast.LENGTH_LONG).show();
        } else if(newP.equals(confirmP)) {
            if(StaticHelperClass.isNetworkAvailable(context)) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(context.getString(R.string.baseUrl))
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .build();

                Observable<String> observable = retrofit
                        .create(RetrofitNetworkService.class)
                        .resetPassword(LoggedUserID, UserName,
                                confirmP, Long.toString(InstituteID))
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
                                parseReturnData(response);
                            }

                            @Override
                            public void onComplete() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                dismiss();
                                Toast.makeText(context,"Error changing password !!!",Toast.LENGTH_LONG).show();
                            }
                        });
            } else {
                Toast.makeText(context,"Please check your INTERNET connection !!!",Toast.LENGTH_LONG).show();
            }
        } else {
            newPassword.setText("");
            confirmPassword.setText("");
            newPassword.requestFocus();
            Toast.makeText(context,"Password does not match !!!",Toast.LENGTH_LONG).show();
        }
    }

    private void parseReturnData(String string) {
        try {
            int result = new JSONArray(string).getJSONObject(0).getInt("result");
            if(result == 1) {
                PreferenceManager.getDefaultSharedPreferences(context).edit().putString("Password", confirmP).apply();
                dismiss();
                Toast.makeText(context,"Password changed successfully !!!",Toast.LENGTH_LONG).show();
            } else {
                dismiss();
                Toast.makeText(context,"Error changing password !!!",Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            dismiss();
            Toast.makeText(context,"Error changing password !!!",Toast.LENGTH_LONG).show();
        }
    }
}
