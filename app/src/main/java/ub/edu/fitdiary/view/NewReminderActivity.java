package ub.edu.fitdiary.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import ub.edu.fitdiary.R;
import ub.edu.fitdiary.model.SportRepository;
import ub.edu.fitdiary.viewmodel.NewEventActivityViewModel;
import ub.edu.fitdiary.viewmodel.NewReminderActivityViewModel;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class NewReminderActivity extends AppCompatActivity {
    private final String TAG = "NewReminderActivity";
    // Atributos de la clase
    private EditText mDateText;
    private ImageView mDateSelectImage;
    private Spinner mSportSpinner;
    private EditText mDurationText;
    private EditText mPulseText;
    private Button mAcceptButton;
    private ImageView mCancelButton;

    private NewEventActivityViewModel newEventActivityViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_reminder);

        getSupportActionBar().hide(); //hide the title bar
        newEventActivityViewModel = new ViewModelProvider(this)
                .get(NewEventActivityViewModel.class);

        // Instanciar los atributos, buscando en el layout
        mDateText = findViewById(R.id.newReminderDateTextRectangle);
        mDateSelectImage = findViewById(R.id.newReminderDateImageSelector);
        mSportSpinner = findViewById(R.id.newReminderSportSpinner);
        mDurationText = findViewById(R.id.newReminderDurationTextRectangle);
        mPulseText = findViewById(R.id.newReminderPulseTextRectangle);
        mAcceptButton= findViewById(R.id.newReminderAcceptButton);
        mCancelButton = findViewById(R.id.newReminderCancelButton);

        // Setear el spinner de deportes
        // Recuperar lista de sports desde BBDD
        newEventActivityViewModel.getSports(new SportRepository.OnSportsLoadedListener() {
            @Override
            public void onSportsLoaded(List<String> sports) {
                // Set up Spinner adapter with sports list
                ArrayAdapter<String> adapter = new ArrayAdapter<>(NewReminderActivity.this, android.R.layout.simple_spinner_item, sports);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mSportSpinner.setAdapter(adapter);
            }
        });

        /* Listener del selector de calendario fecha */
        mDateSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //code to pick a date from the calendar
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                //pick a date with android date picker dialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(NewReminderActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        mDateText.setText(day + "-" + (month + 1) + "-" + year);
                    }
                }, year, month, day);

                // Add 1 day to the current date
                calendar.add(Calendar.DAY_OF_YEAR, 1);

                datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
                datePickerDialog.show();
            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Mostrar ventana para ver si de verdad quiere cancelar la acción
                // Build the confirmation dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(NewReminderActivity.this);
                builder.setTitle("Confirm Cancel Adding a new Reminder");
                builder.setMessage("Are you sure you want to cancel this action?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Volvemos a la página anterior
                        onBackPressed();
                    }
                });
                builder.setNegativeButton("No", null);

                // Show the confirmation dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        mAcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // En caso de que los campos de Date, Sport, Duration y pulsaciones estén vacíos, no se puede añadir evento
                if (mDateText.getText().toString().isEmpty() ||
                        mSportSpinner.getSelectedItem().toString().isEmpty() ||
                        mDurationText.getText().toString().isEmpty() ||
                        mPulseText.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Fields of Date, Sport, Duration and Pulse are compulsory",
                            Toast.LENGTH_SHORT).show();
                } else if (Integer.parseInt(mPulseText.getText().toString())<=50 ||
                        Integer.parseInt(mPulseText.getText().toString())>=300){ //Test de rango
                    Toast.makeText(getApplicationContext(), "Pulse should be between 50 and 300",
                            Toast.LENGTH_SHORT).show();}
                else { // Si están los tres campos obligatorios rellenados, se añade el recordatorio
                    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                    Date date = new Date();
                    String horaActual = dateFormat.format(date); // Hora actual en formato de cadena
                    newEventActivityViewModel.addEvent(
                            mDateText.getText().toString()+" "+horaActual,
                            mSportSpinner.getSelectedItem().toString(),
                            mDurationText.getText().toString(),
                            mPulseText.getText().toString(),
                            mSportSpinner.getSelectedItem().toString(),
                            null
                    );
                    finish();
                }
            }
        });

    }

}