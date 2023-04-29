package ub.edu.fitdiary.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

public class ProfileFragmentViewModel extends AndroidViewModel {
    private final static String TAG = "ProfileFragmentViewModel";

    public ProfileFragmentViewModel(Application application) {
        super(new Application());
    }
}
