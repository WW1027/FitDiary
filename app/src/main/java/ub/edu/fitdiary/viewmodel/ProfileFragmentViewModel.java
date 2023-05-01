package ub.edu.fitdiary.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;

import ub.edu.fitdiary.model.SuggestionRepository;
import ub.edu.fitdiary.model.User;
import ub.edu.fitdiary.model.UserRepository;

public class ProfileFragmentViewModel extends AndroidViewModel {
    private final static String TAG = "ProfileFragmentViewModel";
    private SuggestionRepository suggestionRepository;
    private UserRepository userRepository;

    private MutableLiveData<User> mUserData = new MutableLiveData<>();

    private FirebaseFirestore mDb;

    public ProfileFragmentViewModel(Application application) {
        super(new Application());
        suggestionRepository = SuggestionRepository.getInstance();
        userRepository = UserRepository.getInstance();
        mDb = FirebaseFirestore.getInstance();
    }

    public void sendSuggestion(Date date, String suggestion) {
        // Llamar al método de guardar de model
        suggestionRepository.addSuggestion(date, suggestion);
    }

    public LiveData<User> getUserData() {
        return mUserData;
    }

    public void loadUserData(String email) {
        userRepository.getUser(email).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot document) {
                if (document.exists()) {
                    User user = document.toObject(User.class);
                    mUserData.setValue(user);
                } else {
                    mUserData.setValue(null);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mUserData.setValue(null);
            }
        });
    }


    public void updateCompletion(String user, String field, String text) {
        userRepository.updateCompletion(user, field, text);
    }

    public void signOut() {
        userRepository.signOut();
    }

}
