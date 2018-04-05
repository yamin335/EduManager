package onair.onems.mainactivities;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import onair.onems.R;

public class SyllabusMainScreen extends SideNavigationMenuParentActivity {
    int i = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.test_row_item, null);
        LinearLayout parent = (LinearLayout) findViewById(R.id.contentMain);
        parent.addView(rowView);

        TextView textViewHeader = (TextView)findViewById(R.id.periodName);
        final TextView textViewDetails = (TextView)findViewById(R.id.test);
        textViewDetails.setVisibility(View.GONE);
        textViewHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(textViewDetails.isShown()){
                    textViewDetails.setVisibility(View.GONE);
                } else {
                    Animation a = AnimationUtils.loadAnimation(SyllabusMainScreen.this, R.anim.slide_down);
                    if(a != null){
                        a.reset();
                        if(textViewDetails != null){
                            textViewDetails.clearAnimation();
                            textViewDetails.startAnimation(a);
                        }
                    }
                    textViewDetails.setVisibility(View.VISIBLE);
                }
            }
        });

        final TextView textView = (TextView) findViewById(R.id.periodDetails);
        textView.setText("Got it");
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i++;
                textView.setText("Clicked!!"+i);
            }
        });
    }
}
