package onair.onems.customadapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.HashMap;
import java.util.List;
import onair.onems.R;
import onair.onems.models.ExpandedMenuModel;

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Context mContext;
    private List<ExpandedMenuModel> mListDataHeader;
    private int UserTypeID;
    // child data in format of header title, child title
    private HashMap<ExpandedMenuModel, List<String>> mListDataChild;
//    ExpandableListView expandList;

    public ExpandableListAdapter(Context context, List<ExpandedMenuModel> listDataHeader,
                                 HashMap<ExpandedMenuModel, List<String>> listChildData, int UserTypeID) {
        this.mContext = context;
        this.mListDataHeader = listDataHeader;
        this.mListDataChild = listChildData;
        this.UserTypeID = UserTypeID;
//        this.expandList = mView;
//        expandList.setGroupIndicator(null);
    }

    @Override
    public int getGroupCount() {
        int i = mListDataHeader.size();
        Log.d("GROUP COUNT", String.valueOf(i));
        return this.mListDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        int childCount = 0;
        if (groupPosition != 0) {
            childCount = this.mListDataChild.get(this.mListDataHeader.get(groupPosition))
                    .size();
        }
        return childCount;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.mListDataHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        Log.d("CHILD", mListDataChild.get(this.mListDataHeader.get(groupPosition))
                .get(childPosition).toString());
        return this.mListDataChild.get(this.mListDataHeader.get(groupPosition))
                .get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ExpandedMenuModel headerTitle = (ExpandedMenuModel) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.side_menu_row_item, null);
        }
        TextView lblListHeader = (TextView) convertView.findViewById(R.id.submenu);
        ImageView headerIcon = (ImageView) convertView.findViewById(R.id.iconimage);
        ImageView indicator = (ImageView) convertView.findViewById(R.id.arrow);

        if(UserTypeID == 1) {

        } else if(UserTypeID == 2) {

        } else if(UserTypeID == 3) {
            switch (groupPosition) {
                case 0:
                    indicator.setVisibility(View.INVISIBLE);
                    break;
                default:
                    indicator.setVisibility(View.INVISIBLE);
            }
        } else if(UserTypeID == 4) {
            switch (groupPosition) {
                case 3:
                    indicator.setVisibility(View.VISIBLE);
                    break;
                case 8:
                    indicator.setVisibility(View.VISIBLE);
                    break;
                default:
                    indicator.setVisibility(View.INVISIBLE);
            }
        } else if(UserTypeID == 5) {

        }

        if (isExpanded) {
            indicator.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_expand_less));
        } else {
            indicator.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_expand_more));
        }

        // lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle.getIconName());
        headerIcon.setImageResource(headerTitle.getIconImg());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.side_submenu_row_item, null);
        }

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.submenu);

        txtListChild.setText(childText);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}