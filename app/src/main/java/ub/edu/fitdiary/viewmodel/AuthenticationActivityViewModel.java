package ub.edu.fitdiary.viewmodel;

import android.app.Application;
import android.content.Intent;
import android.widget.Toast;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;

import ub.edu.fitdiary.model.UserRepository;
import ub.edu.fitdiary.view.MainActivity;

public class AuthenticationActivityViewModel extends AndroidViewModel {
    private final static String TAG = "AuthentificationActivityViewModel";
    private UserRepository userRepository;
    private FirebaseAuth mAuth;

    public AuthenticationActivityViewModel(Application application) {
        super(application);

        userRepository = UserRepository.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    public void authenticateUser(String email, String password) {
        if(password.isEmpty()){
            Toast.makeText(getApplication(), "Password is Empty", Toast.LENGTH_SHORT).show();
        }else{
            userRepository.authenticateUser(email, password, task -> {
                if (task.isSuccessful()) {
                    // Autenticación exitosa
                    // Actualizar la vista en consecuencia
                    Intent intent = new Intent(getApplication(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    getApplication().startActivity(intent);
                } else {
                    // Autenticación fallida
                    // Actualizar la vista en consecuencia
                    Toast.makeText(getApplication(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public boolean isUserLogged() {
        return mAuth.getCurrentUser() != null;
    }

    public LiveData<Boolean> sendResetPasswordEmail(String email) {
        MutableLiveData<Boolean> resultLiveData = new MutableLiveData<>();
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        resultLiveData.setValue(true);
                    } else {
                        resultLiveData.setValue(false);
                    }
                });
        return resultLiveData;
    }

    public LiveData<Boolean> signUp(String email, String password, String firstName, String lastName, String dateOfBirth, String gender) {
        MutableLiveData<Boolean> resultLiveData = new MutableLiveData<>();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        userRepository.addUser(email, email, firstName, lastName, dateOfBirth, gender);
                        resultLiveData.setValue(true);
                    } else {
                        resultLiveData.setValue(false);
                    }
                });

        return resultLiveData;
    }
}
