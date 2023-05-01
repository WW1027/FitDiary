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

    private MutableLiveData<User> mUserData;

    private FirebaseFirestore mDb;

    public ProfileFragmentViewModel(Application application) {
        super(new Application());
        suggestionRepository = SuggestionRepository.getInstance();
        userRepository = UserRepository.getInstance();
        mDb = FirebaseFirestore.getInstance();
        mUserData = new MutableLiveData<>();

        loadUserData(getEmail());
    }

    public void sendSuggestion(Date date, String suggestion) {
        // Llamar al método de guardar de model
        suggestionRepository.addSuggestion(date, suggestion);
    }

    public LiveData<User> getUserData() {
        return mUserData;
    }

    private void loadUserData(String email) {
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

   /* public String getEmail() {
        return mEmail;
    }

    public LiveData<String> getName() {
        return mName;
    }

    public LiveData<String> getSurname() {
        return mSurname;
    }

    public LiveData<String> getBirthday() {
        return mBirthday;
    }

    public LiveData<String> getSex() {
        return mSex;
    }

    public void loadData() {
        userRepository.getUser().observeForever(new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user != null) {
                    mName.setValue(user.getName());
                    mSurname.setValue(user.getSurname());
                    mBirthday.setValue(user.getBirthday());
                    mSex.setValue(user.getSex());
                }
            }
        });
    }*/

    /*private void loadData() {
        DocumentReference docRef = mDb.collection("users").document(email);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot document) {
                if (document.exists()) {
                    name.setValue(document.getString("name"));
                    surname.setValue(document.getString("surname"));
                    birthday.setValue(document.getString("birthday"));
                    sex.setValue(document.getString("sex"));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Manejar errores
            }
        });
    }*/

    public void updateSex(String newSex) {
        mDb.collection("users").document(getEmail()).update("sex", newSex);
    }

    public void updateCompletion(String field, String text) {
        userRepository.updateCompletion(field, text);
    }

    public void signOut() {
        userRepository.signOut();
    }

    public String getEmail() {
        return userRepository.getEmail();
    }
}
