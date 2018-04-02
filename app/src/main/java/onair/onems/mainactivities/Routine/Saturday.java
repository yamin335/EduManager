package onair.onems.mainactivities.Routine;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import onair.onems.R;
import onair.onems.customadapters.RoutineAdapter;
import onair.onems.models.Contact;

public class Saturday extends Fragment {
    private View rootView;
    private RecyclerView recyclerView;
    private ArrayList<Contact> contactList;
    private RoutineAdapter mAdapter;
    public Saturday() {

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.routine_day_pager_item, container, false);
        recyclerView = rootView.findViewById(R.id.routinePeriods);

        Bundle bundle = getArguments();
        String saturdayJsonArrayString = bundle.getString("saturdayJsonArray");
        int saturdayPeriodNumber = bundle.getInt("saturdayPeriodNumber");

        contactList = new ArrayList<>();
        mAdapter = new RoutineAdapter(getActivity(), saturdayJsonArrayString, saturdayPeriodNumber);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        fetchRoutine();
        return  rootView;
    }

    private void fetchRoutine() {
        for(int i = 0; i < 5; ++i) {
            Contact contact = new Contact();
            contact.setName("Person"+"--"+i);
            contactList.add(contact);
        }
        // refreshing recycler view
        mAdapter.notifyDataSetChanged();
    }
}
