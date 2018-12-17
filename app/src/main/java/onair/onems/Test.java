package onair.onems;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import onair.onems.databinding.TestBinding;
import onair.onems.mainactivities.SideNavigationMenuParentActivity;
import onair.onems.models.TestModel;

public class Test extends SideNavigationMenuParentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        final View childActivityLayout = inflater.inflate(R.layout.client_entry_new, null);
        LinearLayout parentActivityLayout = (LinearLayout) findViewById(R.id.contentMain);
//        parentActivityLayout.addView(childActivityLayout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        TestBinding binding = DataBindingUtil.inflate(inflater, R.layout.test, parentActivityLayout, true);
        TestModel testModel = new TestModel();
        binding.setTestModel(testModel);
        binding.setLifecycleOwner(this);

        testModel.getName().setValue("Yamin Mollah");

        EditText textInputEditText = findViewById(R.id.input);
        textInputEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0)
                    testModel.getName().setValue(s.toString());
            }
        });

    }
}
