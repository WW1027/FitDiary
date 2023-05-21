package ub.edu.fitdiary.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SuggestionRepository {
    private static final String TAG = "SuggestionRepository";

    /* Autoinstancia, patrón del singleton*/
    private static final SuggestionRepository mInstance = new SuggestionRepository();

    /** Referencia a la Base de Datos */
    private FirebaseFirestore mDb;
    private FirebaseAuth mAuth;

    /**
     * Retorna esta instancia singleton
     * @return
     */
    public static SuggestionRepository getInstance() {return mInstance;}

    /**
     * Constructor privado para forzar la instanciación con getInstance(),
     * como marca el patrón de diseño Singleton class
     */
    public SuggestionRepository() {
        mDb = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();}

    public void addSuggestion(Date date, String suggestion) {
        // Obtenir informació personal de l'usuari
        Map<String, Object> suggestionMap = new HashMap<>();
        suggestionMap.put("suggestion", suggestion);
        suggestionMap.put("date", date);

        // Añadimos el evento a la base de datos
        FirebaseUser user = mAuth.getCurrentUser();
        DocumentReference docRef = mDb.collection("users").document(user.getEmail());

        // Obtener una referencia a la colección de eventos dentro del documento de usuario
        CollectionReference events = docRef.collection("suggestions");

        events.add(suggestionMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG, "Nuevo evento agregado con ID: " + documentReference.getId());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG,"Error al agregar nuevo evento", e);
            }
        });

    }
}
