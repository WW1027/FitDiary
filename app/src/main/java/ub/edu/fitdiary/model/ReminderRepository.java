package ub.edu.fitdiary.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReminderRepository {
    private final static String TAG = "ReminderRepository";

    /* Autoinstancia, patroón del singleton*/
    private static final ReminderRepository mInstance = new ReminderRepository();

    /** Referencia a la Base de Datos */
    private FirebaseFirestore mDb;
    private FirebaseAuth mAuth;
    /**
     * Retorna esta instancia singleton
     * @return
     */
    public static ReminderRepository getInstance() {return mInstance;}

    /**
     * Constructor privado para forzar la instanciación con getInstance(),
     * como marca el patrón de diseño Singleton class
     */
    public ReminderRepository() {
        mDb = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    /** Definición de listener (interfaz),
     * para escuchar cuando se hayan acabado de leer los usuarios de la BBDD */
    public interface OnLoadRemindersListener {
        void onLoadReminders(ArrayList<Event> events);
    }

    private ArrayList<ReminderRepository.OnLoadRemindersListener> mOnLoadRemindersListeners = new ArrayList<>();


    /**
     * Añadir un listener de la operación OnLoadRemindersListener.
     * Puede haber sólo uno. Hagamos lista, como ejemplo, para demostrar la flexibilidad
     * de este diseño.
     * @param listener
     */
    public void addOnLoadRemindersListener(ReminderRepository.OnLoadRemindersListener listener) {
        mOnLoadRemindersListeners.add(listener);
    }

    /**
     * Método para añadir un nuevo evento a la base de datos
     * @param date
     * @param sport
     * @param duration
     * @param timeBefore
     */
    public void addReminder(
            String date,
            String sport,
            String duration,
            String timeBefore) {
        // Creamos un nuevo evento con los datos recibidos
        Map<String, Object> newReminder = new HashMap<>();
        newReminder.put("date", date);
        newReminder.put("sport", sport);
        newReminder.put("duration", duration);
        newReminder.put("timeBefore", timeBefore);

        // Añadimos el evento a la base de datos
        FirebaseUser user = mAuth.getCurrentUser();
        DocumentReference docRef = mDb.collection("users").document(user.getEmail());

        // Obtener una referencia a la colección de recordatorios dentro del documento de usuario
        CollectionReference events = docRef.collection("reminders");

        events.document(date).set(newReminder).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "Nuevo recordatorio agregado con ID: " + date);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG,"Error al agregar nuevo recordatorio", e);
            }
        });
    }

    public void updateCompletion(String field, String text, String id) {
        FirebaseUser user = mAuth.getCurrentUser();
        DocumentReference docRef = mDb.collection("users").document(user.getEmail()).collection("reminders").document(id);
        Map<String, Object> event = new HashMap<>();
        event.put(field, text);
        docRef.update(event);
    }

    public void deleteReminder(String id){
        FirebaseUser user = mAuth.getCurrentUser();
        DocumentReference docRef = mDb.collection("users").document(user.getEmail()).collection("reminders").document(id);
        docRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Document successfully deleted
                        Log.d("TAG", "Document deleted successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error occurred while deleting the document
                        Log.w("TAG", "Error deleting document", e);
                    }
                });
    }
}
