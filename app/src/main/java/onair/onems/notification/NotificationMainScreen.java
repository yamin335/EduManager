package onair.onems.notification;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import onair.onems.R;
import onair.onems.customised.MyDividerItemDecoration;
import onair.onems.mainactivities.SideNavigationMenuParentActivity;
import onair.onems.mainactivities.StudentMainScreen;
import onair.onems.mainactivities.TeacherMainScreen;

public class NotificationMainScreen extends SideNavigationMenuParentActivity implements NotificationAdapter.NotificationAdapterListener,
        RecyclerItemTouchHelper.RecyclerItemTouchHelperListener, NotificationAdapter.NotificationSaverListener, SideNavigationMenuParentActivity.NotificationReceiverListener{

    private ArrayList<JSONObject> notificationList;
    private NotificationAdapter mAdapter;
    private CoordinatorLayout coordinatorLayout;
    private RecyclerView  recyclerView;

    @Override
    protected void onStart() {
        super.onStart();
        mAdapter = new NotificationAdapter(this, notificationList, this, this, this, this);
        recyclerView.setAdapter(mAdapter);
        prepareNotifications();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdapter = new NotificationAdapter(this, notificationList, this, this, this, this);
        recyclerView.setAdapter(mAdapter);
        prepareNotifications();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        activityName = NotificationMainScreen.class.getName();

        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View childActivityLayout = Objects.requireNonNull(inflater).inflate(R.layout.notification_main_screen, null);
        LinearLayout parentActivityLayout = findViewById(R.id.contentMain);
        parentActivityLayout.addView(childActivityLayout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        coordinatorLayout = findViewById(R.id.coordinator_layout);

        recyclerView = findViewById(R.id.recycler);
        notificationList = new ArrayList<>();
        mAdapter = new NotificationAdapter(this, notificationList, this, this, this, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 0));
        recyclerView.setAdapter(mAdapter);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        prepareNotifications();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(UserTypeID == 1) {
            Intent mainIntent = new Intent(NotificationMainScreen.this,TeacherMainScreen.class);
            startActivity(mainIntent);
            finish();
        } else if(UserTypeID == 2) {
            Intent mainIntent = new Intent(NotificationMainScreen.this,TeacherMainScreen.class);
            startActivity(mainIntent);
            finish();
        } else if(UserTypeID == 3) {
            Intent mainIntent = new Intent(NotificationMainScreen.this,StudentMainScreen.class);
            startActivity(mainIntent);
            finish();
        } else if(UserTypeID == 4) {
            Intent mainIntent = new Intent(NotificationMainScreen.this,TeacherMainScreen.class);
            startActivity(mainIntent);
            finish();
        } else if(UserTypeID == 5) {
            Intent mainIntent = new Intent(NotificationMainScreen.this,StudentMainScreen.class);
            startActivity(mainIntent);
            finish();
        }
    }

    @Override
    public void onNotificationSelected(JSONObject jsonObject) {
        Intent intent = new Intent(this, NotificationDetails.class);
        intent.putExtra("notification", jsonObject.toString());
        intent.putExtra("from", "activity");
        startActivity(intent);
    }

    private void prepareNotifications() {
        notificationList.clear();
        String string = getSharedPreferences("PUSH_NOTIFICATIONS", Context.MODE_PRIVATE)
                .getString("notifications", "[]");
        try {
            JSONArray jsonArray = new JSONArray(string);
            for (int i = 0; i<jsonArray.length(); i++) {
                notificationList.add(jsonArray.getJSONObject(i));
            }
            mAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof NotificationAdapter.MyViewHolder) {
            // backup of removed item for undo purpose
            final JSONObject deletedItem = notificationList.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from recycler view
            mAdapter.removeItem(viewHolder.getAdapterPosition());

            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, " UNDO deleted notification?", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // undo is selected, restore the deleted item
                    mAdapter.restoreItem(deletedItem, deletedIndex);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    @Override
    public void onNotificationChanged(ArrayList<JSONObject> notificationListFiltered, int counter) {
        this.notificationList = notificationListFiltered;
    }

    @Override
    protected void onPause() {
        super.onPause();
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i<notificationList.size(); i++) {
            try {
                jsonArray.put(i, notificationList.get(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        getSharedPreferences("PUSH_NOTIFICATIONS", Context.MODE_PRIVATE)
                .edit()
                .putString("notifications", jsonArray.toString())
                .apply();
    }

    @Override
    public void onNotificationReceived(JSONObject jsonObject) {
        notificationList.add(jsonObject);
        mAdapter.notifyItemInserted(notificationList.size()-1);
        recyclerView.smoothScrollToPosition(notificationList.size()-1);
    }
}
