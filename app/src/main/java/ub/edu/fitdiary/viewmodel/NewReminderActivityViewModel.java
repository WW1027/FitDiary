package ub.edu.fitdiary.viewmodel;

import android.app.Application;
import android.support.annotation.Nullable;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import ub.edu.fitdiary.model.Reminder;
import ub.edu.fitdiary.model.ReminderRepository;
import ub.edu.fitdiary.model.SportRepository;
import ub.edu.fitdiary.model.UserRepository;

public class NewReminderActivityViewModel extends AndroidViewModel {
    private final static String TAG = "NewReminderActivityViewModel";
    private ReminderRepository reminderRepository;
    private UserRepository userRepository;
    private MutableLiveData<Reminder> mReminderData;

    public NewReminderActivityViewModel(Application application){
        super(new Application());

        // Instacias generales
        reminderRepository = ReminderRepository.getInstance();
        userRepository = UserRepository.getInstance();
        mReminderData = new MutableLiveData<>();
    }

    public LiveData<Reminder> getReminderData(){ return mReminderData; }
    public String getEmail() {
        return userRepository.getEmail();
    }

    public void getSports(SportRepository.OnSportsLoadedListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("sports")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> sports = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String sport = document.getString("name");
                            sports.add(sport);
                        }
                        listener.onSportsLoaded(sports);
                    } else {
                        // Handle error
                    }
                });
    }

    public void addReminder(String date, String sport, String duration, String timeBefore) {
        // Llamar al método de guardar de model
        reminderRepository.addReminder(date, sport, duration, timeBefore);
    }

    public void updateCompletion(String field, String text, String id) {
        reminderRepository.updateCompletion(field, text, id);
    }

    public void deleteReminder(String id){
        reminderRepository.deleteReminder(id);
    }

    public void loadReminderData(String date){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection("users").document(getEmail());
        documentReference.collection("reminders").document(date).addSnapshotListener(new com.google.firebase.firestore.EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    mReminderData.setValue(null);
                    return;
                }
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    Reminder reminder = documentSnapshot.toObject(Reminder.class);
                    mReminderData.setValue(reminder);
                } else {
                    mReminderData.setValue(null);
                }
            }
        });

    }
}
