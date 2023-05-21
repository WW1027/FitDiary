package ub.edu.fitdiary.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

import ub.edu.fitdiary.model.Date;
import ub.edu.fitdiary.model.DateRepository;
import ub.edu.fitdiary.model.Event;
import ub.edu.fitdiary.model.EventRepository;

public class CalendarFragmentViewModel extends AndroidViewModel {
    private final static String TAG = "CalendarFragmentViewModel";
    private final MutableLiveData<ArrayList<Date>> mDates; // Les dates que la RecyclerView mostra al home
    private DateRepository mDateRepository; // On es manté la informació dels usuaris
    private final MutableLiveData<ArrayList<Event>> mEvents; // Los eventos que la RecyclerView muestra en el Calendar section
    private final MutableLiveData<String> mPictureUrl; // URL de la foto del evento del usuario
    private EventRepository mEventRepository; // Repositorio de eventos
    public CalendarFragmentViewModel(Application application) {
        super(application);

        mDates = new MutableLiveData<>(new ArrayList<>());
        mDateRepository = DateRepository.getInstance();
        //Instancia de los atibutos
        mEvents = new MutableLiveData<>(new ArrayList<>());
        mEventRepository = EventRepository.getInstance();
        mPictureUrl = new MutableLiveData<>();

        mEventRepository.addOnLoadEventsListener(new EventRepository.OnLoadEventsListener() {
            @Override
            public void onLoadUsers(ArrayList<Event> events) {
                CalendarFragmentViewModel.this.setEvents(events);
            }
        });

        mEventRepository.setOnLoadEventPictureListener(new EventRepository.OnLoadEventPictureUrlListener() {
            @Override
            public void OnLoadEventPictureUrl(String pictureUrl) {
                // Log.d(TAG, "Loaded pictureUrl: " + pictureUrl);
                mPictureUrl.setValue(pictureUrl);
            }
        });
    }

    /*
    MÉTODOS PARA LA CLASE DATE
     */
    public LiveData<ArrayList<Date>> getDates() {
        return mDates;
    }

    /* Mètode que crida a carregar dades dels usuaris */
    public void loadDatesFromRepository(int month, int year) {
        mDateRepository.loadDates(mDates.getValue(), month, year);
    }

    public void setDates(ArrayList<Date> dates) {
        mDates.setValue(dates);

    }


    /*
    MÉTODOS PARA LA CLASE EVENTOS
     */
    /*
     * Devuelve a los usuarios para que la CalendarFragment pueda suscribir el observable.
     */
    public LiveData<ArrayList<Event>> getEvents() { return mEvents; }

    /*
     * Método que será invocado por el EventRepository.OnLoadEventsListener definido en
     * constructor (cuando el objeto UserRepository haya terminado de leer de la BBDD).
     */
    public void setEvents(ArrayList<Event> events) {
        mEvents.setValue(events);
    }

    /* Método que será invocado por la CalendarFragment para cargar los eventos del usuario
     * actual desde el repositorio */
    public void loadEventsFromRepository(String id) {
        mEventRepository.loadEvents(mEvents.getValue(), id);
    }

    public void removeFromRepository(int position) {
        mEvents.getValue().remove(position);
    }

}
