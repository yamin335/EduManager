package onair.onems.mainactivities.Result;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import onair.onems.R;
import onair.onems.customadapters.CustomDialog;
import onair.onems.customadapters.MyDividerItemDecoration;
import onair.onems.customadapters.ResultAdapter;
import onair.onems.customadapters.SubjectWiseResultAdapter;
import onair.onems.models.ResultModel;
import onair.onems.models.SubjectWiseResultModel;

/**
 * Created by onAir on 12-Feb-18.
 */

public class SubjectWiseResult extends AppCompatActivity implements SubjectWiseResultAdapter.SubjectWiseResultsAdapterListener{

    public static final String MyPREFERENCES = "LogInKey";
    public static SharedPreferences sharedPreferences;
    long InstituteID;
    private RecyclerView recyclerView;
    private List<SubjectWiseResultModel> subjectWiseResultModelListList;
    private SubjectWiseResultAdapter mAdapter;
    private SearchView searchView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subject_wise_result);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        InstituteID = prefs.getLong("InstituteID",0);

        if(!isNetworkAvailable())
        {
            Toast.makeText(this,"Please check your internet connection and open app again!!! ",Toast.LENGTH_LONG).show();
        }
        recyclerView = findViewById(R.id.recycler_view);
        subjectWiseResultModelListList = new ArrayList<>();
        mAdapter = new SubjectWiseResultAdapter(this, subjectWiseResultModelListList, this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 20));
        recyclerView.setAdapter(mAdapter);

        prepareResultList();

    }
    private void prepareResultList() {
        for(int i = 0; i < 50; ++i) {
            SubjectWiseResultModel subjectWiseResultModel = new SubjectWiseResultModel();
            subjectWiseResultModel.setSubjectName(subjectWiseResultModel.getSubjectName()+"--"+i);
            subjectWiseResultModelListList.add(subjectWiseResultModel);
        }
        // refreshing recycler view
        SubjectWiseResultModel subjectWiseResultModel = new SubjectWiseResultModel();
        subjectWiseResultModel.setSubjectName("Total = ");
        subjectWiseResultModelListList.add(subjectWiseResultModel);
        mAdapter.notifyDataSetChanged();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onSubjectWiseResultSelected(SubjectWiseResultModel subjectWiseResultModel) {
        Toast.makeText(getApplicationContext(), "Selected: " + subjectWiseResultModel.getSubjectName()
                +", " + subjectWiseResultModel.getGrade(), Toast.LENGTH_LONG).show();
        CustomDialog cdd=new CustomDialog(this);
        cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        cdd.setCancelable(false);
        cdd.show();



    }
}
