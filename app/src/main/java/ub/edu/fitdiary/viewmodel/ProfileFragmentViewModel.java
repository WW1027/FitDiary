package ub.edu.fitdiary.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Date;

import ub.edu.fitdiary.model.SuggestionRepository;

public class ProfileFragmentViewModel extends AndroidViewModel {
    private final static String TAG = "ProfileFragmentViewModel";
    private SuggestionRepository suggestionRepository;

    public ProfileFragmentViewModel(Application application) {
        super(new Application());
        suggestionRepository = SuggestionRepository.getInstance();
    }

    public void sendSuggestion(Date date, String suggestion) {
        // Llamar al método de guardar de model
        suggestionRepository.addSuggestion(date, suggestion);
    }
}
