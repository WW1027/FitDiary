package ub.edu.fitdiary.viewmodel;

import android.app.Application;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import ub.edu.fitdiary.model.Event;
import ub.edu.fitdiary.model.EventRepository;
import ub.edu.fitdiary.model.SportRepository;

public class NewEventActivtyViewModel extends AndroidViewModel {
    private final static String TAG = "NewEventActivtyViewModel";
    private EventRepository eventRepository;

    public NewEventActivtyViewModel(Application application){
        super(new Application());

        // Instacias generales
        eventRepository = EventRepository.getInstance();
    }

    public void addEvent(String date, String sport, String duration, String comment, String pulse) {
        // Llamar al método de guardar de model
        eventRepository.addEvent(date, sport, duration, comment, pulse);
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

}
