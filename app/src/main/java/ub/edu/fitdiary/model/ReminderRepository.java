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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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
        void onLoadReminders(ArrayList<Reminder> reminders);
    }

    private ArrayList<OnLoadRemindersListener> mOnLoadRemindersListeners = new ArrayList<>();


    /**
     * Añadir un listener de la operación OnLoadRemindersListener.
     * Puede haber sólo uno. Hagamos lista, como ejemplo, para demostrar la flexibilidad
     * de este diseño.
     * @param listener
     */
    public void addOnLoadRemindersListener(OnLoadRemindersListener listener) {
        mOnLoadRemindersListeners.add(listener);
    }



    /**
     * Método para añadir un nuevo reminder a la base de datos
     * @param date
     * @param sport
     * @param duration
     * @param alarm
     */
    public void addReminder(
            String date,
            String sport,
            String duration,
            String alarm) {
        // Creamos un nuevo reminder con los datos recibidos
        Map<String, Object> newReminder = new HashMap<>();
        newReminder.put("date", date);
        newReminder.put("sport", sport);
        newReminder.put("duration", duration);
        if(!(alarm.equals("-1"))) {
            newReminder.put("alarm", alarm);
        }

        // Añadimos el reminder a la base de datos
        FirebaseUser user = mAuth.getCurrentUser();
        DocumentReference docRef = mDb.collection("users").document(user.getEmail());

        // Obtener una referencia a la colección de reminders dentro del documento de usuario
        CollectionReference reminders = docRef.collection("reminders");

        reminders.document(date).set(newReminder).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "Nuevo reminder agregado con ID: " + date);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG,"Error al agregar nuevo reminder", e);
            }
        });
    }

    /**
     * Método para obtener todos los reminders de un usuario
     */
    public void loadReminders(ArrayList<Reminder> reminders, String id) {
        reminders.clear();
        FirebaseUser user = mAuth.getCurrentUser();
        DocumentReference docRef = mDb.collection("users").document(user.getEmail());
        docRef.collection("reminders").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());

                        // Obtener el ID del documento actual
                        String documentId = document.getId();

                        // Verificar si el ID del documento comienza igual que el valor de 'id'
                        if (documentId.startsWith(id)) {
                            Reminder reminder;
                            if(documentId.contains("alarm")){
                                reminder = new Reminder(
                                        document.getString("date"),
                                        document.getString("sport"),
                                        document.getString("duration"),
                                        document.getString("alarm")
                                );
                            }else{
                                reminder = new Reminder(
                                        document.getString("date"),
                                        document.getString("sport"),
                                        document.getString("duration")
                            );
                            }
                            reminders.add(reminder);
                        }
                    }
                    /* Callback listeners */
                    for (OnLoadRemindersListener l: mOnLoadRemindersListeners) {
                        l.onLoadReminders(reminders);
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public void updateCompletion(String field, String text, String id) {
        FirebaseUser user = mAuth.getCurrentUser();
        DocumentReference docRef = mDb.collection("users").document(user.getEmail()).collection("reminders").document(id);
        Map<String, Object> reminder = new HashMap<>();
        reminder.put(field, text);
        docRef.update(reminder);
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
