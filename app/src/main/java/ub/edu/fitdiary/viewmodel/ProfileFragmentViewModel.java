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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;
import ub.edu.fitdiary.model.SuggestionRepository;
import ub.edu.fitdiary.model.User;
import ub.edu.fitdiary.model.UserRepository;

public class ProfileFragmentViewModel extends AndroidViewModel {
    private final static String TAG = "ProfileFragmentViewModel";
    private SuggestionRepository suggestionRepository;
    private UserRepository userRepository;

    private MutableLiveData<User> mUserData;
    private final MutableLiveData<String> mPictureUrl;
    private FirebaseStorage mStorage;


    public ProfileFragmentViewModel(Application application) {
        super(new Application());
        suggestionRepository = SuggestionRepository.getInstance();
        userRepository = UserRepository.getInstance();
        mUserData = new MutableLiveData<>();
        mPictureUrl = new MutableLiveData<>();
        mStorage = FirebaseStorage.getInstance();

        loadUserData(getEmail());


        userRepository.setOnLoadUserPictureListener(new UserRepository.OnLoadUserPictureUrlListener() {
            @Override
            public void OnLoadUserPictureUrl(String pictureUrl) {
                // Log.d(TAG, "Loaded pictureUrl: " + pictureUrl);
                mPictureUrl.setValue(pictureUrl);
            }
        });
    }
    public LiveData<String> getPictureUrl() {
        return mPictureUrl;
    }
    public void loadPictureOfUser() {
        userRepository.loadPictureOfUser();
    }


    public void sendSuggestion(Date date, String suggestion) {
        // Llamar al método de guardar de model
        suggestionRepository.addSuggestion(date, suggestion);
    }

    public LiveData<User> getUserData() {
        return mUserData;
    }

    private void loadUserData(String email) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection("users").document(email);
        documentReference.addSnapshotListener(new com.google.firebase.firestore.EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    mUserData.setValue(null);
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    User user = documentSnapshot.toObject(User.class);
                    mUserData.setValue(user);
                } else {
                    mUserData.setValue(null);
                }
            }
        });

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
                    userRepository.setPictureUrlOfUser(userId, uploadUrl.toString());
                    mPictureUrl.setValue(uploadUrl.toString());
                }
            }
        });
    }


}


