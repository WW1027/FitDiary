package ub.edu.fitdiary.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import ub.edu.fitdiary.model.Date;
import ub.edu.fitdiary.model.DateRepository;
import ub.edu.fitdiary.model.Event;
import ub.edu.fitdiary.model.EventRepository;
import ub.edu.fitdiary.model.Reminder;
import ub.edu.fitdiary.model.ReminderRepository;

public class CalendarFragmentViewModel extends AndroidViewModel {
    private final static String TAG = "CalendarFragmentViewModel";
    private final MutableLiveData<ArrayList<Date>> mDates; // Les dates que la RecyclerView mostra al home
    private DateRepository mDateRepository; // On es manté la informació dels usuaris
    private final MutableLiveData<ArrayList<Event>> mEvents; // Los eventos que la RecyclerView muestra en el Calendar section
    private final MutableLiveData<ArrayList<Reminder>> mReminders; // Los reminders que la RecyclerView muestra en el Calendar section
    private MutableLiveData<ArrayList<Object>> mItems;
    private final MutableLiveData<String> mPictureUrl; // URL de la foto del evento del usuario
    private EventRepository mEventRepository; // Repositorio de eventos
    private static ReminderRepository mReminderRepository; // Repositorio de reminders
    public CalendarFragmentViewModel(Application application) {
        super(application);

        mDates = new MutableLiveData<>(new ArrayList<>());
        mDateRepository = DateRepository.getInstance();
        //Instancia de los atibutos
        mEvents = new MutableLiveData<>(new ArrayList<>());
        mEventRepository = EventRepository.getInstance();
        mReminders = new MutableLiveData<>(new ArrayList<>());
        mItems = new MutableLiveData<>(new ArrayList<>());
        mReminderRepository = ReminderRepository.getInstance();
        mPictureUrl = new MutableLiveData<>();

        mEventRepository.addOnLoadEventsListener(new EventRepository.OnLoadEventsListener() {
            @Override
            public void onLoadEvents(ArrayList<Event> events) {
                CalendarFragmentViewModel.this.setEvents(events);
            }
        });

        mReminderRepository.addOnLoadRemindersListener(new ReminderRepository.OnLoadRemindersListener() {
            @Override
            public void onLoadReminders(ArrayList<Reminder> reminders) {
                CalendarFragmentViewModel.this.setReminders(reminders);
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
    MÉTODOS PARA LA CLASE REMINDER
     */
    /*
     * Devuelve a los usuarios para que la CalendarFragment pueda suscribir el observable.
     */
    public LiveData<ArrayList<Reminder>> getReminders() { return mReminders; }

    /*
     * Método que será invocado por el ReminderRepository.OnLoadEventsListener definido en
     * constructor (cuando el objeto UserRepository haya terminado de leer de la BBDD).
     */
    public void setReminders(ArrayList<Reminder> reminders) {
        mReminders.setValue(reminders);
        if (reminders != null) {
            mItems.setValue(new ArrayList<>(reminders));
        }
    }

    /* Método que será invocado por la CalendarFragment para cargar los reminders del usuario
     * actual desde el repositorio */
    public void loadRemindersFromRepository(String id) {
        mReminderRepository.loadReminders(mReminders.getValue(), id);
    }

    public void removeReminderFromRepository(int position) {
        mReminders.getValue().remove(position);
    }

    public static void deleteReminder(String id){
        mReminderRepository.deleteReminder(id);
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
        if (events != null) {
            mItems.setValue(new ArrayList<>(events));
        }
    }
    public void setItems(ArrayList<Object> items) {
        mItems.setValue(items);
    }

    /* Método que será invocado por la CalendarFragment para cargar los eventos del usuario
     * actual desde el repositorio */
    public void loadEventsFromRepository(String id) {
        mEventRepository.loadEvents(mEvents.getValue(), id);
    }

    public void removeFromRepository(int position) {
        mEvents.getValue().remove(position);
    }

    public MutableLiveData<ArrayList<Object>> getItems() {
        return mItems;
    }

    public void loadItemsFromRepository(String id) {

        // Cargar eventos desde el repositorio de eventos
        mEventRepository.loadEvents(mEvents.getValue(), id);

        // Cargar recordatorios desde el repositorio de recordatorios
        mReminderRepository.loadReminders(mReminders.getValue(), id);

        ArrayList<Event> events = mEvents.getValue();
        if (events != null) {
            mItems.setValue(new ArrayList<>(events));
        }

        ArrayList<Reminder> reminders = mReminders.getValue();
        if (reminders != null) {
            mItems.setValue(new ArrayList<>(reminders));
        }

    }

    private String getDateFromObject(Object object) {
        if (object instanceof Event) {
            return ((Event) object).getDate();
        } else if (object instanceof Reminder) {
            return ((Reminder) object).getDate();
        }
        return null;
    }

}
