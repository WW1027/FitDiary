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

import ub.edu.fitdiary.model.Event;
import ub.edu.fitdiary.model.EventRepository;
import ub.edu.fitdiary.model.SportRepository;
import ub.edu.fitdiary.model.User;
import ub.edu.fitdiary.model.UserRepository;
import ub.edu.fitdiary.view.EventCardAdapter;

public class NewEventActivityViewModel extends AndroidViewModel {
    private final static String TAG = "NewEventActivtyViewModel";
    private EventRepository eventRepository;
    private UserRepository userRepository;
    private MutableLiveData<Event> mEventData;
    private EventCardAdapter eventCardAdapter;

  public NewEventActivityViewModel(Application application){
        super(new Application());

        // Instacias generales
        eventRepository = EventRepository.getInstance();
        userRepository = UserRepository.getInstance();
        mEventData = new MutableLiveData<>();

        //loadEventData("17-5-202316:18");
    }

    public void addEvent(String date, String sport, String duration, String comment, String pulse) {
        // Llamar al método de guardar de model
        eventRepository.addEvent(date, sport, duration, comment, pulse, null);
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
    public void updateCompletion(String field, String text) {
        eventRepository.updateCompletion(field, text);
    }

    public void deleteEvent() {

    }

    public LiveData<Event> getEventData() {
        return mEventData;
    }

    public void loadEventData(String date){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection("users").document(getEmail());
        documentReference.collection("events").document(date).addSnapshotListener(new com.google.firebase.firestore.EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    mEventData.setValue(null);
                    return;
                }
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    Event event = documentSnapshot.toObject(Event.class);
                    mEventData.setValue(event);
                } else {
                    mEventData.setValue(null);
                }
            }
        });

    }

    public String getEmail() {
        return userRepository.getEmail();
    }

}
