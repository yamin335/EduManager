package onair.onems.crm;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import onair.onems.PrivacyPolicy;
import onair.onems.R;
import onair.onems.Services.RetrofitNetworkService;
import onair.onems.attendance.TakeAttendance;
import onair.onems.customised.GuardianStudentSelectionDialog;
import onair.onems.customised.MyDividerItemDecoration;
import onair.onems.mainactivities.ChangePasswordDialog;
import onair.onems.mainactivities.ChangeUserTypeDialog;
import onair.onems.mainactivities.SideNavigationMenuParentActivity;
import onair.onems.mainactivities.TeacherMainScreen;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ClientList extends SideNavigationMenuParentActivity implements ClientListAdapter.ClientListAdapterListener {

    private ArrayList<JsonObject> clientList;
    private ClientListAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        activityName = ClientList.class.getName();

        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View childActivityLayout = inflater.inflate(R.layout.client_list, null);
        LinearLayout parentActivityLayout = (LinearLayout) findViewById(R.id.contentMain);
        parentActivityLayout.addView(childActivityLayout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        clientList = new ArrayList<>();
        RecyclerView recyclerView = findViewById(R.id.recycler);
        mAdapter = new ClientListAdapter(this, clientList, this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 10));
        recyclerView.setAdapter(mAdapter);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.baseUrl))
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitNetworkService retrofitNetworkService = retrofit.create(RetrofitNetworkService.class);

        // finally, execute the request
        Call<String> networkCall = retrofitNetworkService.getClientList(LoggedUserID);
        networkCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull retrofit2.Response<String> response) {
                String responseData = "";
                responseData = response.body();
                if (responseData!= null) {
                    if (!responseData.equals("")&&!responseData.equals("[]")) {
                        try {
                            JsonParser parser = new JsonParser();
                            JsonArray dataJsonArray = parser.parse(responseData).getAsJsonArray();
                            for (int i = 0; i<dataJsonArray.size(); i++) {
                                clientList.add(i, dataJsonArray.get(i).getAsJsonObject());
                            }
                            mAdapter.notifyDataSetChanged();
                        } catch (JsonIOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.e("Request error:", t.getMessage());
            }
        });

    }

    @Override
    public void onClientSelected(JsonObject client) {
        Intent mainIntent = new Intent(ClientList.this, NewClientEntry.class);
        mainIntent.putExtra("clientData", client.toString());
        startActivity(mainIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent mainIntent = new Intent(ClientList.this, TeacherMainScreen.class);
            startActivity(mainIntent);
            finish();
        }
    }
}
