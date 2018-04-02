package onair.onems.customadapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import onair.onems.R;
import onair.onems.mainactivities.Routine.RoutineModel;

public class RoutineClassListAdapter extends ArrayAdapter<RoutineModel>
{
   Context context;
    public RoutineClassListAdapter(Context context, ArrayList<RoutineModel> objects)
    {
        super(context,0, objects);
        this.context=context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.routine_class_row_item, parent, false);
        }

        // Get the {@link AndroidFlavor} object located at this position in the list
        RoutineModel currentAndroidFlavor = getItem(position);

//        // Find the TextView in the list_item.xml layout with the ID version_name
//        TextView subjectTextView = (TextView) listItemView.findViewById(R.id.textView5);
//        // Get the version name from the current AndroidFlavor object and
//        // set this text on the name TextView
//        subjectTextView.setText(currentAndroidFlavor.getSubjectId());
//        TextView nameTextView = (TextView) listItemView.findViewById(R.id.name);
//        nameTextView.setText(currentAndroidFlavor.getTeacherId());
//
//        TextView numberTextView = (TextView) listItemView.findViewById(R.id.time);
//        // Get the version number from the current AndroidFlavor object and
//        // set this text on the number TextView
//        numberTextView.setText(currentAndroidFlavor.getTimeId());



        // Return the whole list item layout (containing 2 TextViews and an ImageView)
        // so that it can be shown in the ListView
        return listItemView;

    }
}
