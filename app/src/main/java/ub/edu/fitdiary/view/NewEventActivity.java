package ub.edu.fitdiary.view;


import android.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ub.edu.fitdiary.R;
import ub.edu.fitdiary.model.SportRepository;
import ub.edu.fitdiary.viewmodel.NewEventActivityViewModel;

public class NewEventActivity extends AppCompatActivity {
    // Atributos de la interface newEventActivity
    private EditText mDateText;
    private ImageView mDateImage;
    private Spinner mSportSpinner;
    private EditText mDurationText;
    private Spinner mDurationSpinner;
    private EditText mPulseText;
    private ImageView mEventImage;
    private EditText mCommentText;
    private Button mSaveButton;
    private ImageView mCancelButton;
    private ImageView mHintPulseImage;

    // Atributos del view model o model del view
    private NewEventActivityViewModel newEventActivtyViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        // Instanciamos su propio view model
        newEventActivtyViewModel = new ViewModelProvider(this)
                .get(NewEventActivityViewModel.class);

        getSupportActionBar().hide(); //hide the title bar

        // Relacionamos el id con la variable para referirnos a ella
        mDateText = findViewById(R.id.newEventDateTextRectangle);
        mDateImage = findViewById(R.id.newEventDateImageSelector);
        mSportSpinner = findViewById(R.id.newEventSportSpinner);
        mDurationText = findViewById(R.id.newEventDurationTextRectangle);
        mDurationSpinner = findViewById(R.id.newEventDurationSpinner);
        mPulseText = findViewById(R.id.newEventPulseTextRectangle);
        mEventImage = findViewById(R.id.newEventImageRectangle);
        mSaveButton = findViewById(R.id.newEventAcceptButton);
        mCancelButton = findViewById(R.id.newEventCancelButton);
        mCommentText = findViewById(R.id.newEventCommentRectangle);
        mHintPulseImage = findViewById(R.id.newEventDateImagePulse);

        /* Añadimos listener al botón de añadir */
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // En caso de que los campos de Date, Sport y Duration estén vacíos, no se puede añadir evento
                if (mDateText.getText().toString().isEmpty() ||
                        mSportSpinner.getSelectedItem().toString().isEmpty() ||
                        mDurationText.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Campos de Date, Sport y Duration son obligatorios",
                            Toast.LENGTH_SHORT).show();
                } else { // Si están los tres campos obligatorios rellenados, se añade el evento
                    newEventActivtyViewModel.addEvent(
                            mDateText.getText().toString(),
                            mSportSpinner.getSelectedItem().toString(),
                            mDurationText.getText().toString(),
                            mPulseText.getText().toString(),
                            mCommentText.getText().toString()
                    );
                    finish();
                }
            }
        });

        /* Ajustar Spinner de tipo de configuración de tiempo */
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.time_array, android.R.layout.simple_spinner_item);
        // Especificar el layout de uso cuando la lista de elecciones aparece
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Aplicar el adaptador al spinner
        mDurationSpinner.setAdapter(adapter2);

        // Recuperar lista de sports desde BBDD
        newEventActivtyViewModel.getSports(new SportRepository.OnSportsLoadedListener() {
            @Override
            public void onSportsLoaded(List<String> sports) {
                // Set up Spinner adapter with sports list
                ArrayAdapter<String> adapter = new ArrayAdapter<>(NewEventActivity.this, android.R.layout.simple_spinner_item, sports);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mSportSpinner.setAdapter(adapter);
            }
        });


        /* Listener del selector de calendario fecha */
        mDateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //code to pick a date from the calendar
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                Date date = new Date();
                String horaActual = dateFormat.format(date); // Hora actual en formato de cadena

                //pick a date with android date picker dialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(NewEventActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        mDateText.setText(day + "-" + (month + 1) + "-" + year + horaActual);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        /* Listener del hint de qué es el pulso */
        mHintPulseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Mostrar ventana para ver si de verdad quiere cancelar la acción
                // Build the confirmation dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(NewEventActivity.this);
                builder.setTitle("Pulse");
                builder.setMessage(R.string.pulseHint);
                builder.setPositiveButton("Ok", null);

                // Show the confirmation dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        /* Listener del botón cancel x */
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Mostrar ventana para ver si de verdad quiere cancelar la acción
                // Build the confirmation dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(NewEventActivity.this);
                builder.setTitle("Confirm Cancel Adding a new Event");
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
    }
}
