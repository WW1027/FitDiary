package ub.edu.fitdiary.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

import ub.edu.fitdiary.model.Date;
import ub.edu.fitdiary.model.DateRepository;

public class StatisticsFragmentViewModel extends AndroidViewModel {
    private final String TAG = "HomeFragmentViewModel";

    private final MutableLiveData<ArrayList<Date>> mDates; // Les dates que la RecyclerView mostra al home
    private DateRepository mDateRepository; // On es manté la informació dels usuaris

    public StatisticsFragmentViewModel(@NonNull Application application) {
        super(application);

        mDates = new MutableLiveData<>(new ArrayList<>());
        mDateRepository = DateRepository.getInstance();
    }

    public LiveData<ArrayList<Date>> getDates() {
        return mDates;
    }

    /* Mètode que crida a carregar dades dels usuaris */
    public void loadDatesFromRepository() {
        mDateRepository.loadDates(mDates.getValue());
    }

    public void setDates(ArrayList<Date> dates) {
        mDates.setValue(dates);

    }

    public void selectDate(int pos) {
        //TODO
    }


}
