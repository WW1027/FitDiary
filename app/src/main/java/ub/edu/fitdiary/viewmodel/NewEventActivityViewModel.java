package ub.edu.fitdiary.viewmodel;

import android.app.Application;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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
    private FirebaseStorage mStorage;
    private final MutableLiveData<String> mPictureAux;

  public NewEventActivityViewModel(Application application){
        super(new Application());

        // Instacias generales
        eventRepository = EventRepository.getInstance();
        userRepository = UserRepository.getInstance();
        mEventData = new MutableLiveData<>();
        mStorage = FirebaseStorage.getInstance();
        mPictureAux = new MutableLiveData<>();

    }

    public void addEvent(String date, String sport, String duration, String pulse, String comment,String imageURL) {
        // Llamar al método de guardar de model
        eventRepository.addEvent(date, sport, duration, pulse, comment, imageURL);
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
    public void updateCompletion(String field, String text, String id) {
        eventRepository.updateCompletion(field, text, id);
    }
    public void deleteEvent(String id) {
        eventRepository.deleteEvent(id);
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

    public void setPictureUrlOfUser( Uri imageUri) {
        // Sejetar una foto d'usuari implica:
        // 1. Pujar-la a Firebase Storage (ho fa aquest mètode)
        // 2. Setejar la URL de la imatge com un dels camps de l'usuari a la base de dades
        //    (es delega al DatabaseAdapter.setPictureUrlOfUser)
        System.out.println(imageUri.toString());
        String userId =userRepository.getEmail();
        StorageReference storageRef = mStorage.getReference();
        StorageReference fileRef = storageRef.child("images")
                .child(imageUri.getLastPathSegment());

        // Crea una tasca de pujada de fitxer a FileStorage
        UploadTask uploadTask = fileRef.putFile(imageUri);

        // Listener per la pujada
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                Log.d(TAG, "Upload is " + progress + "% done");
            }
        });

        // La tasca en si: ves fent-la (pujant) i fins que s'hagi completat (onCompleteListener).
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@androidx.annotation.NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (task.isSuccessful()) {
                    // Continue with the task to get the download URL
                    return fileRef.getDownloadUrl();
                } else {
                    throw task.getException();
                }
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete (@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri uploadUrl = task.getResult();
                    // un cop pujat, passa-li la URL de la imatge a l'adapter de
                    // la Base de Dades per a que l'associï a l'usuari
                    Log.d(TAG, "DownloadTask: " + uploadUrl.toString());
                    mPictureAux.setValue(uploadUrl.toString());
                }
            }
        });
    }

    public LiveData<String> getPictureAux() {
        return mPictureAux;
    }

}
