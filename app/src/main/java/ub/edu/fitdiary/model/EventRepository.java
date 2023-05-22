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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EventRepository {
    private final static String TAG = "EventRepository";

    /* Autoinstancia, patroón del singleton*/
    private static final EventRepository mInstance = new EventRepository();

    /** Referencia a la Base de Datos */
    private FirebaseFirestore mDb;
    private FirebaseAuth mAuth;
    /**
     * Retorna esta instancia singleton
     * @return
     */
    public static EventRepository getInstance() {return mInstance;}

    /**
     * Constructor privado para forzar la instanciación con getInstance(),
     * como marca el patrón de diseño Singleton class
     */
    public EventRepository() {
        mDb = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    /** Definición de listener (interfaz),
     * para escuchar cuando se hayan acabado de leer los usuarios de la BBDD */
    public interface OnLoadEventsListener {
        void onLoadEvents(ArrayList<Event> events);
    }

    private ArrayList<OnLoadEventsListener> mOnLoadEventsListeners = new ArrayList<>();

    /** Definición de listener (interfaz)
     * para poder escuchar cuando se haya acabado de leer la Url de la foto de evento
     * del usuario */
    public interface OnLoadEventPictureUrlListener {
        void OnLoadEventPictureUrl(String pictureUrl);
    }

    private OnLoadEventPictureUrlListener mOnLoadEventPictureUrlListener;

    /**
     * Añadir un listener de la operación OnLoadEventsListener.
     * Puede haber sólo uno. Hagamos lista, como ejemplo, para demostrar la flexibilidad
     * de este diseño.
     * @param listener
     */
    public void addOnLoadEventsListener(OnLoadEventsListener listener) {
        mOnLoadEventsListeners.add(listener);
    }

    /**
     * Setejamos un listener de la operación OnLoadEventPictureUrlListener.
     * En este caso, no es una lista de listeners. Sólo dejamos haber uno,
     * también a modo de ejemplo.
     * @param listener
     */
    public void setOnLoadEventPictureListener(OnLoadEventPictureUrlListener listener) {
        mOnLoadEventPictureUrlListener = listener;
    }


    /**
     * Método para añadir un nuevo evento a la base de datos
     * @param date
     * @param sport
     * @param duration
     * @param pulse
     * @param comment
     */
    public void addEvent(
            String date,
            String sport,
            String duration,
            String pulse,
            String comment,
            String imageURL) {
        // Creamos un nuevo evento con los datos recibidos
        Map<String, Object> newEvent = new HashMap<>();
        newEvent.put("date", date);
        newEvent.put("sport", sport);
        newEvent.put("duration", duration);
        newEvent.put("pulse", pulse);
        newEvent.put("comment", comment);
        newEvent.put("imageURL", imageURL);

        // Añadimos el evento a la base de datos
        FirebaseUser user = mAuth.getCurrentUser();
        DocumentReference docRef = mDb.collection("users").document(user.getEmail());

        // Obtener una referencia a la colección de eventos dentro del documento de usuario
        CollectionReference events = docRef.collection("events");

        events.document(date).set(newEvent).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "Nuevo evento agregado con ID: " + date);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG,"Error al agregar nuevo evento", e);
            }
        });
    }

    /**
     * Método para obtener todos los eventos de un usuario
     */
    public void loadEvents(ArrayList<Event> events, String id) {
        events.clear();
        FirebaseUser user = mAuth.getCurrentUser();
        DocumentReference docRef = mDb.collection("users").document(user.getEmail());
        docRef.collection("events").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());

                        // Obtener el ID del documento actual
                        String documentId = document.getId();

                        // Verificar si el ID del documento comienza igual que el valor de 'id'
                        if (documentId.startsWith(id)) {
                            Event event = new Event(
                                    document.getString("date"),
                                    document.getString("sport"),
                                    document.getString("duration"),
                                    document.getString("pulse"),
                                    document.getString("imageURL"),
                                    document.getString("comment")
                            );
                            events.add(event);
                        }
                    }
                    /* Callback listeners */
                    for (OnLoadEventsListener l: mOnLoadEventsListeners) {
                        l.onLoadEvents(events);
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public void updateCompletion(String field, String text, String id) {
        FirebaseUser user = mAuth.getCurrentUser();
        DocumentReference docRef = mDb.collection("users").document(user.getEmail()).collection("events").document(id);
        Map<String, Object> event = new HashMap<>();
        event.put(field, text);
        docRef.update(event);
    }

    public void deleteEvent(String id){
        FirebaseUser user = mAuth.getCurrentUser();
        DocumentReference docRef = mDb.collection("users").document(user.getEmail()).collection("events").document(id);
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
