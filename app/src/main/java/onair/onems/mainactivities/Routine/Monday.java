package onair.onems.mainactivities.Routine;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import onair.onems.R;

public class Monday extends Fragment
{

    private View rootView;
    ListView listView;
    ArrayList<RoutineModel> cardModels;
    public Monday()
    {

    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.card_routine, container, false);
        listView = (ListView) rootView.findViewById(R.id.list_cards);
        return  rootView;
    }
}
