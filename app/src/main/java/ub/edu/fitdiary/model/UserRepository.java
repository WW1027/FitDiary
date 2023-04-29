package ub.edu.fitdiary.model;


import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/** Clase que hace de adaptador entre la base de datos (Cloud Firestore) i las clases del model
 * Sigue el patrón de diseño Singleton.
 */
public class UserRepository {
    private static final String TAG = "Repository";

    /** Autoinstancia, por el patrón singleton */
    private static final UserRepository mInstance = new UserRepository();

    /** Referencia a la Base de Datos */
    private FirebaseFirestore mDb;

    /** Definición de listener (interfaz),
     *  para escuchar cuando se hayan acabado de leer los usuarios de la BBDD  */
    public interface OnLoadUsersListener {
        void onLoadUsers(ArrayList<User> users);
    }

    public ArrayList<OnLoadUsersListener> mOnLoadUsersListeners = new ArrayList<>();

    /** Definición de listener (interfaz)
     * para poder escuchar cuando se haya terminado de leer la Url de la foto de perfil
     * de un usuario concreto */
    public interface OnLoadUserPictureUrlListener {
        void OnLoadUserPictureUrl(String pictureUrl);
    }

    public OnLoadUserPictureUrlListener mOnLoadUserPictureUrlListener;

    /**
     * Constructor privado para forzar la instanciación con getInstance(),
     * como marca el patrón de diseño Singleton class
     */
    private UserRepository() {
        mDb = FirebaseFirestore.getInstance();
    }

    /**
     * Devuelve esta instancia singleton
     * @return
     */
    public static UserRepository getInstance() {
        return mInstance;
    }

    /**
     * Añadir un listener de la operación OnLoadUsersListener.
     * Puede haber sólo uno. Hagamos lista, como ejemplo, para demostrar la flexibilidad
     * de este diseño.
     * @param listener
     */
    public void addOnLoadUsersListener(OnLoadUsersListener listener) {
        mOnLoadUsersListeners.add(listener);
    }

    /**
     * Setejamos un listener de la operación OnLoadUserPictureUrlListener.
     * En este caso, no es una lista de listeners. Sólo dejamos haber uno,
     * también a modo de ejemplo.
     * @param listener
     */
    public void setOnLoadUserPictureListener(OnLoadUserPictureUrlListener listener) {
        mOnLoadUserPictureUrlListener = listener;
    }

    /**
     * Método que lee los usuarios. Vendrá llamado desde fuera y cuando acabe,
     * avisará siempre a los listeners, invocando su OnLoadUsers.
     */
    public void loadUsers(ArrayList<User> users){
        users.clear();
        mDb.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                User user = new User(
                                        document.toString(), // ID = Email
                                        document.getString("name"),
                                        document.getString("surname"),
                                        document.getString("birthday"),
                                        document.getString("sex"),
                                        document.getString("picture_url")
                                );
                                users.add(user);
                            }
                            // Callback listeners
                            for (OnLoadUsersListener l: mOnLoadUsersListeners) {
                                l.onLoadUsers(users);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    /**
     * Método que lee la Url de una foto de perfil de un usuario indicado por su
     * email. Vendrá llamado desde fuera y cuando acabe, avisará siempre al listener,
     * invocando su OnLoadUserPictureUrl.
     */
    public void loadPictureOfUser(String email) {
        mDb.collection("users")
                .document(email)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null) {
                                mOnLoadUserPictureUrlListener.OnLoadUserPictureUrl(document.getString("picture_url"));
                            } else {
                                Log.d("LOGGER", "No such document");
                            }
                        } else {
                            Log.d("LOGGER", "get failed with ", task.getException());
                        }
                    }
                });
    }

    /**
     * Método que añade un nuevo usuario a la base de datos. Utilizado por la función
     * de Sign-Up (registro) de la SignUpActivity.
     *
     * @param email
     * @param name
     * @param surname
     * @param date
     */
    public void addUser(
            String email,
            String name,
            String surname,
            String date,
            String sex) {
        // Obtenir informació personal de l'usuari
        Map<String, Object> signedUpUser = new HashMap<>();
        signedUpUser.put("name", name);
        signedUpUser.put("surname", surname);
        signedUpUser.put("birthday", date);
        signedUpUser.put("sex", sex);
        signedUpUser.put("picture_url", null);

        // Afegir-la a la base de dades
        mDb.collection("users").document(email).set(signedUpUser)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Sign up completion succeeded");
                        } else {
                            Log.d(TAG, "Sign up completion failed");
                        }
                    }
                });
    }

    /**
     * Método que guarda la Url de una foto de perfil que un usuario haya subido
     * desde la MainActivity a la BBDD. Concretamente, es llamado por el MainActivityViewModel.
     * @param userId
     * @param pictureUrl
     */
    public void setPictureUrlOfUser(String userId, String pictureUrl) {
        Map<String, Object> userEntry = new HashMap<>();
        userEntry.put("picture_url", pictureUrl);

        mDb.collection("users")
                .document(userId)
                .set(userEntry, SetOptions.merge())
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Photo upload succeeded: " + pictureUrl);
                })
                .addOnFailureListener(exception -> {
                    Log.d(TAG, "Photo upload failed: " + pictureUrl);
                });
    }
}