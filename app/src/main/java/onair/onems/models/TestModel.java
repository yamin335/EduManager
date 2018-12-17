package onair.onems.models;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public class TestModel extends ViewModel {
    private MutableLiveData<String> name;
    public TestModel() {
        this.name = new MutableLiveData<>();
    }

    public MutableLiveData<String> getName() {
        if (name == null) {
            name = new MutableLiveData<>();
        }
        return name;
    }
}
