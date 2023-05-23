package ub.edu.fitdiary.viewmodel;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.EventListener;

import ub.edu.fitdiary.model.Event;
import ub.edu.fitdiary.model.EventRepository;
import ub.edu.fitdiary.model.User;
import ub.edu.fitdiary.model.UserRepository;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

public class StatisticsFragmentViewModel extends AndroidViewModel {
    private final static String TAG = "StatisticsFragmentViewModel";
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private MutableLiveData<Integer> goalLiveData;

    private MutableLiveData<User> mUserData;
    private EventRepository eventRepository;
    private UserRepository userRepository;
    private final MutableLiveData<ArrayList<Event>> eventosDiaActual;
    private MutableLiveData<Integer> duracionLunes;
    private MutableLiveData<Integer> duracionMartes;
    private MutableLiveData<Integer> duracionMiercoles;
    private MutableLiveData<Integer> duracionJueves;
    private MutableLiveData<Integer> duracionViernes;
    private MutableLiveData<Integer> duracionSabado;
    private MutableLiveData<Integer> duracionDomingo;
    private MutableLiveData<Integer> duracionDiaria;

    public StatisticsFragmentViewModel(Application application) {
        super(new Application());
        eventRepository = EventRepository.getInstance();
        eventosDiaActual = new MutableLiveData<>(new ArrayList<>());
        duracionLunes = new MutableLiveData<>();
        duracionLunes.setValue(0);
        duracionMartes = new MutableLiveData<>();
        duracionMartes.setValue(0);
        duracionMiercoles = new MutableLiveData<>();
        duracionMiercoles.setValue(0);
        duracionJueves = new MutableLiveData<>();
        duracionJueves.setValue(0);
        duracionViernes = new MutableLiveData<>();
        duracionViernes.setValue(0);
        duracionSabado = new MutableLiveData<>();
        duracionSabado.setValue(0);
        duracionDomingo = new MutableLiveData<>();
        duracionDomingo.setValue(0);
        duracionDiaria = new MutableLiveData<>();
        duracionDiaria.setValue(0);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        goalLiveData = new MutableLiveData<>();
        userRepository = UserRepository.getInstance();
        mUserData = new MutableLiveData<>();


        eventRepository.addOnLoadEventsListener(new EventRepository.OnLoadEventsListener() {
            @Override
            public void onLoadEvents(ArrayList<Event> events) {
                StatisticsFragmentViewModel.this.duracionDiaria(events);
            }
        });
    }

    public LiveData<Integer> getDuracionLunes() {
        return duracionLunes;
    }

    public LiveData<Integer> getDuracionMartes() {
        return duracionMartes;
    }

    public LiveData<Integer> getDuracionMiercoles() {
        return duracionMiercoles;
    }

    public LiveData<Integer> getDuracionJueves() {
        return duracionJueves;
    }

    public LiveData<Integer> getDuracionViernes() {
        return duracionViernes;
    }

    public LiveData<Integer> getDuracionSabado() {
        return duracionSabado;
    }

    public LiveData<Integer> getDuracionDomingo() {
        return duracionDomingo;
    }

    public int actualDuration(){
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        int diaActual = calendar.get(Calendar.DAY_OF_WEEK);
        int suma = 0;

        if(diaActual == 1){
            suma += duracionDomingo.getValue();
            diaActual = 7;
        }


        if (diaActual >= Calendar.MONDAY) {
            suma += duracionLunes.getValue();
        }
        if (diaActual >= Calendar.TUESDAY) {
            suma += duracionMartes.getValue();
        }
        if (diaActual >= Calendar.WEDNESDAY) {
            suma += duracionMiercoles.getValue();
        }
        if (diaActual >= Calendar.THURSDAY) {
            suma += duracionJueves.getValue();
        }
        if (diaActual >= Calendar.FRIDAY) {
            suma += duracionViernes.getValue();
        }
        if (diaActual >= Calendar.SATURDAY) {
            suma += duracionSabado.getValue();
        }
        if (diaActual >= Calendar.SUNDAY) {
            suma += duracionDomingo.getValue();
        }

        return suma;
    }

    public void duracionDiaria(ArrayList<Event> events) {
        eventosDiaActual.setValue(events);
        duracionDiaria.setValue(0);
        duracionLunes.setValue(0);
        duracionMartes.setValue(0);
        duracionMiercoles.setValue(0);
        duracionJueves.setValue(0);
        duracionViernes.setValue(0);
        duracionSabado.setValue(0);
        duracionDomingo.setValue(0);

        // Calcular la duración total de los eventos del día actual
        for (Event event : eventosDiaActual.getValue()) {
            if(!isDateInFuture(event.getDate())){
                String duracionStr = event.getDuration();
                String date = event.getDate();
                int time = Integer.parseInt(duracionStr);
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                Date fecha = null;
                try {
                    fecha = sdf.parse(date);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(fecha);
                int diaSemana = calendar.get(Calendar.DAY_OF_WEEK);
                duracionDiaria.setValue(time);
                switch (diaSemana) {
                    case Calendar.MONDAY:
                        duracionLunes.setValue(duracionLunes.getValue() + duracionDiaria.getValue());
                        break;
                    case Calendar.TUESDAY:
                        duracionMartes.setValue(duracionMartes.getValue() + duracionDiaria.getValue());
                        break;
                    case Calendar.WEDNESDAY:
                        duracionMiercoles.setValue(duracionMiercoles.getValue() + duracionDiaria.getValue());
                        break;
                    case Calendar.THURSDAY:
                        duracionJueves.setValue(duracionJueves.getValue() + duracionDiaria.getValue());
                        break;
                    case Calendar.FRIDAY:
                        duracionViernes.setValue(duracionViernes.getValue() + duracionDiaria.getValue());
                        break;
                    case Calendar.SATURDAY:
                        duracionSabado.setValue(duracionSabado.getValue() + duracionDiaria.getValue());
                        break;
                    case Calendar.SUNDAY:
                        duracionDomingo.setValue(duracionDomingo.getValue() + duracionDiaria.getValue());
                        break;
                }
            }
        }

    }


    public void cargarDiaMinutos(int dia) {
        // Obtener la fecha actual
        Date fechaActual = new Date();

        // Obtener el calendario actual
        Calendar calendario = Calendar.getInstance();
        calendario.setTime(fechaActual);

        // Establecer el calendario para el primer día de la semana
        calendario.setFirstDayOfWeek(Calendar.MONDAY);
        calendario.set(Calendar.DAY_OF_WEEK, dia);


        String id;

        // Obtener los eventos de la semana actual
        // Obtener el día, mes y año del calendario
        int day = calendario.get(Calendar.DAY_OF_MONTH);
        int mes = calendario.get(Calendar.MONTH) + 1; // Los meses en Calendar empiezan en 0, se suma 1
        int anio = calendario.get(Calendar.YEAR);

        // Obtener el ID para buscar los eventos correspondientes a ese día
        id = day + "-" + mes + "-" + anio;

        // Obtener los eventos del día y agregarlos a la lista
        if(!isDateInFuture(id)){
            eventRepository.loadEvents(eventosDiaActual.getValue(), id);
        }

    }

    public MutableLiveData<Integer> getGoalLiveData() {
        return goalLiveData;
    }

    public void setGoalLiveData(MutableLiveData<Integer> goalLiveData) {
        this.goalLiveData = goalLiveData;
    }

    public void saveGoalValue(int goalValue) {
        String userId = auth.getCurrentUser().getEmail();

        db.collection("users").document(userId)
                .update("goal", goalValue)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // El valor del goal se ha guardado exitosamente en Firebase
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Ocurrió un error al guardar el valor del goal en Firebase
                    }
                });
    }

    public void loadGoal() {
        String userId = auth.getCurrentUser().getEmail();
        DocumentReference userRef = db.collection("users").document(userId);
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if(documentSnapshot.contains("goal")){
                        Integer goal = documentSnapshot.getLong("goal").intValue();
                        goalLiveData.setValue(goal);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Manejar el error de carga del campo "goal"
            }
        });
    }


    private boolean isDateInFuture(String dateString) {
        // Create a SimpleDateFormat object to parse the date string
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy HH:mm");

        try {
            // Parse the date string into a Date object
            Date date = dateFormat.parse(dateString);

            // Get the current date and time
            Date currentDate = new Date();

            // Compare the dates
            if (date.after(currentDate)) {
                // Date is in the future
                return true;
            } else {
                // Date is in the past or equal to the current date
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return false; // Return false if there's an error parsing the date string
        }
    }


}
