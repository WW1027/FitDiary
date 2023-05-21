package ub.edu.fitdiary.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import ub.edu.fitdiary.model.SportRepository;

public class MainActivityViewModel extends AndroidViewModel {
    private final static String TAG = "MainActivityViewModel";
    private SportRepository sportRepository;
    public MainActivityViewModel(Application application){
        super(new Application());

        // Instacias generales
        sportRepository = SportRepository.getInstance();
    }
}
