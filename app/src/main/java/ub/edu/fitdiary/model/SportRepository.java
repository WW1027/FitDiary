package ub.edu.fitdiary.model;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class SportRepository {
    private final static String TAG = "SportRepository";

    /* Autoinstancia, patrón del singleton*/
    private static final SportRepository mInstance = new SportRepository();

    /** Referencia a la Base de Datos */
    private FirebaseFirestore mDb;

    /**
     * Retorna esta instancia singleton
     * @return
     */
    public static SportRepository getInstance() {return mInstance;}

    /**
     * Constructor privado para forzar la instanciación con getInstance(),
     * como marca el patrón de diseño Singleton class
     */
    public SportRepository() {
        mDb = FirebaseFirestore.getInstance();
    }

    public interface OnSportsLoadedListener {
        void onSportsLoaded(List<String> sports);
    }


    public void initSports() {
        //Quiero recuperar la lista de deportes que tengo en la base de datos
        //Buscamos en la base de datos los deportes que hay
        CollectionReference docRef = mDb.collection("sports");

    }

    private void addSport(Sport sport) {
        CollectionReference docRef = mDb.collection("sports");
        docRef.document(sport.getName()).set(sport);
    }
}
