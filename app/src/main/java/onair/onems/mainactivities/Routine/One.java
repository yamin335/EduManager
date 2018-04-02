package onair.onems.mainactivities.Routine;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import java.util.ArrayList;

import onair.onems.R;

/**
 * Created by hp on 2/3/2018.
 */

public class One extends Fragment
{

    private View rootView;
    ListView listView;
    ArrayList<RoutineModel> cardModels;
    public One()
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
        cardModels = new ArrayList<RoutineModel>();
        cardModels.add(new RoutineModel("Bangla","Md. Bony Israil", "10.30am-10.50am" ));
        cardModels.add(new RoutineModel("English","Md. Yamin Mollah", "10.30am-10.50am" ));
        cardModels.add(new RoutineModel("Bangla","Md. Bony Israil", "10.30am-10.50am" ));
        cardModels.add(new RoutineModel("English","Md. Rony", "10.30am-10.50am" ));
        cardModels.add(new RoutineModel("Bangla","Md. Bony Israil", "10.30am-10.50am" ));
        RoutineListAdapter cardAdapter = new RoutineListAdapter(getActivity(), cardModels);
        listView.setAdapter(cardAdapter);
        return  rootView;
    }
}