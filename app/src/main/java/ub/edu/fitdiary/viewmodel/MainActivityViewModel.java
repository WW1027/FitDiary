package ub.edu.fitdiary.viewmodel;

import android.app.Application;
import android.media.MediaParser;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import ub.edu.fitdiary.model.EventRepository;
import ub.edu.fitdiary.model.Sport;
import ub.edu.fitdiary.model.SportRepository;

public class MainActivityViewModel extends AndroidViewModel {
    private final static String TAG = "MainActivityViewModel";
    private SportRepository sportRepository;

    /*public LiveData<List<Sport>> getSports() {
        MutableLiveData<List<Sport>> sportsLiveData = new MutableLiveData<>();
        sportsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Sport> sportsList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Sport sport = document.toObject(Sport.class);
                    sportsList.add(sport);
                }
                sportsLiveData.postValue(sportsList);
            } else {
                Log.e(TAG, "Error getting sports.", task.getException());
            }
        });
        return sportsLiveData;
    }
    */


    public MainActivityViewModel(Application application){
        super(new Application());

        // Instacias generales
        sportRepository = SportRepository.getInstance();
    }

    /*
    public LiveData<List<String>> getSportNames() {
        MediatorLiveData<List<String>> sportNamesLiveData = new MediatorLiveData<>();
        sportNamesLiveData.addSource(getSports(), sportsList -> {
            List<String> sportNamesList = new ArrayList<>();
            for (Sport sport : sportsList) {
                sportNamesList.add(sport.getName());
            }
            sportNamesLiveData.postValue(sportNamesList);
        });
        return sportNamesLiveData;
    }
    */


    //public void initSports() { sportRepository.initSports();}
}
