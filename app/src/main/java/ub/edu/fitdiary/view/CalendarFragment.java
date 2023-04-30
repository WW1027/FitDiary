package ub.edu.fitdiary.view;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import ub.edu.fitdiary.R;
import ub.edu.fitdiary.model.Event;
import ub.edu.fitdiary.viewmodel.CalendarFragmentViewModel;


public class CalendarFragment extends Fragment {

    /*
    DATE ATRIBUTES
     */
    private RecyclerView mDateCardsRV;
    private DateCardAdapter mDateCardRVAdapter;

    /*
    EVENT ATRIBUTES
     */
    private FloatingActionButton addButton;
    private CalendarFragmentViewModel mCalendarFragmentViewModel;
    private RecyclerView mEventsCardsRV;
    private EventCardAdapter mEventCardAdapter;
    private ImageView mDateSelector;
    private TextView mMonthYear;

    public CalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState){
        // Inicialitza el ViewModel d'aquesta activity (HomeActivity)
        mCalendarFragmentViewModel = new ViewModelProvider(this)
                .get(CalendarFragmentViewModel.class);

        // Anem a buscar la RecyclerView i fem dues coses:
        mDateCardsRV = view.findViewById(R.id.userCardDate);

        // (1) Li assignem un layout manager.
        RecyclerView.LayoutManager manager = new LinearLayoutManager(
                view.getContext(), LinearLayoutManager.HORIZONTAL, false
        );
        mDateCardsRV.setLayoutManager(manager);

        // (2) Inicialitza el RecyclerViewAdapter i li assignem a la RecyclerView.
        mDateCardRVAdapter = new DateCardAdapter(
                mCalendarFragmentViewModel.getDates().getValue() // Passem-li referencia llista usuaris
        );
        mDateCardRVAdapter.setOnClickSelectListener(new DateCardAdapter.OnClickSelectListener() {
            // Listener que escoltarà quan interactuem amb un item en una posició donada
            // dins de la recicler view.
            @Override
            public void OnClickSelect(int pos) {
                mCalendarFragmentViewModel.selectDate(pos);

            }
        });

        mDateCardsRV.setAdapter(mDateCardRVAdapter); // Associa l'adapter amb la ReciclerView


        mMonthYear = view.findViewById(R.id.monthYearCalendar);
        Calendar calendar = Calendar.getInstance();
        // Inicialment mostrarà el mes i any actual
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        mMonthYear.setText(dateFormat.format(calendar.getTime()));

        mCalendarFragmentViewModel.loadDatesFromRepository(mMonthYear.getText());  // Internament pobla les dates



        /*
        MÉTODOS PARA EVENTOS
         */
        // Instanciar el ViewModel
        mCalendarFragmentViewModel = new ViewModelProvider(this).get(CalendarFragmentViewModel.class);

        // Buscar el botón de añadir nuevo evento en la vista raíz del fragmento
        addButton = view.findViewById(R.id.calendarFloatingActionButton);
        mDateSelector = view.findViewById(R.id.DateSelector);;

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Código para abrir la nueva actividad de añadir evento nuevo
                Intent intent = new Intent(getActivity(), NewEventActivity.class);
                startActivity(intent);
            }
        });

        // Observer en CalendarFragment para ver si la lista de Events (observable MutableLiveData)
        // en CalendarFragmentViewModel ha cambiado.
        final Observer<ArrayList<Event>> eventListObserver = new Observer<ArrayList<Event>>() {
            @Override
            public void onChanged(ArrayList<Event> events) {
                // Actualizar la UI
                // ...
            }
        };
        mCalendarFragmentViewModel.getEvents().observe(getViewLifecycleOwner(), eventListObserver);

        // Cargar los eventos del repositorio
        mCalendarFragmentViewModel.loadEventsFromRepository();

        // Vamos a buscar el RecyclerView y hacer dos cosas
        mEventsCardsRV = view.findViewById(R.id.eventCardEvent);

        // 1. Asignar el LayoutManager
        LinearLayoutManager manager2 = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mEventsCardsRV.setLayoutManager(manager2);

        // 2. Initializar el RecyclerViewAdapter y asignarlo al RecyclerView
        mEventCardAdapter = new EventCardAdapter(mCalendarFragmentViewModel.getEvents().getValue());
        mEventCardAdapter.setOnClickHideListener(new EventCardAdapter.OnClickHideListener() {
            @Override
            public void OnClickHide(int position) {
                // Código para ocultar el evento en la posición position
                mCalendarFragmentViewModel.removeFromRepository(position);
                mEventCardAdapter.hideEvent(position);
            }
        });
        mEventsCardsRV.setAdapter(mEventCardAdapter);

        // Observer en CalendarFragment para ver si la lista de Event (observable MutableLiveData)
        // en CalendarFragmentViewModel ha cambiado.
        final Observer<ArrayList<Event>> observerEvent = new Observer<ArrayList<Event>>() {
            @Override
            public void onChanged(ArrayList<Event> events) {
                mEventCardAdapter.notifyDataSetChanged();
            }
        };
        mCalendarFragmentViewModel.getEvents().observe(getViewLifecycleOwner(), observerEvent);

        mEventCardAdapter.setOnClickSelectListener(new EventCardAdapter.OnClickSelectListener() {
            // Listener que escoltarà quan interactuem amb un item en una posició donada
            // dins de la recicler view.
            @Override
            public void OnClickSelect(int pos) {
                //TODO Dejar ver información detallada de cada evento

            }
        });

        mDateSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //code to pick a date from the calendar
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                //pick a date with android date picker dialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        String[] monthNames = new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
                        mMonthYear.setText(monthNames[month] + " " + year);
                        mCalendarFragmentViewModel.loadDatesFromRepository(mMonthYear.getText());
                    }
                }, year, month, day);
                datePickerDialog.show();


            }
        });

        // A partir d'aquí, en cas que es faci cap canvi a la llista d'usuaris, HomeActivity ho sabrá
        //mCalendarFragmentViewModel.loadEventsFromRepository();  // Internament pobla els usuaris de la BBDD

        // Si hi ha usuari logat i seteja una foto de perfil, mostra-la.
        /*if (mAuth.getCurrentUser() != null) {
            final Observer<String> observerPictureUrl = new Observer<String>() {
                @Override
                public void onChanged(String pictureUrl) { //Con Picasso es poner la imagen
                    Picasso.get()
                            .load(pictureUrl)
                            .resize(mLoggedPictureImageView.getWidth(), mLoggedPictureImageView.getHeight())
                            .centerCrop()
                            .into(mLoggedPictureImageView);
                }
            };
            mCalendarFragmentViewModel.getPictureUrl().observe(this, observerPictureUrl);

            mCalendarFragmentViewModel.loadPictureOfUser(mAuth.getCurrentUser().getEmail());
        }*/



    }


}