package ub.edu.fitdiary.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import ub.edu.fitdiary.R;
import ub.edu.fitdiary.model.SportRepository;
import ub.edu.fitdiary.viewmodel.NewRemainderActivityViewModel;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;

import java.util.Calendar;
import java.util.List;

public class NewRemainderActivity extends AppCompatActivity {
    private final String TAG = "NewRecordatorioActivity";
    // Atributos de la clase
    private EditText mDateText;
    private ImageView mDateSelectImage;
    private Spinner mSportSpinner;
    private EditText mDurationText;
    private Spinner mDurationSpinner;
    private Switch mRemainderSwitch;
    private Spinner mTimeBeforeSpinner;
    private Button mAcceptButton;
    private ImageView mCancelButton;


    private NewRemainderActivityViewModel newRemainderActivityViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_remainder);

        getSupportActionBar().hide(); //hide the title bar

        newRemainderActivityViewModel = new ViewModelProvider(this)
                .get(NewRemainderActivityViewModel.class);

        // Instanciar los atributos, buscando en el layout
        mDateText = findViewById(R.id.newRemainderDateTextRectangle);
        mDateSelectImage = findViewById(R.id.newRemainderDateImageSelector);
        mSportSpinner = findViewById(R.id.newRemainderSportSpinner);
        mDurationText = findViewById(R.id.newRemainderDurationTextRectangle);
        mDurationSpinner = findViewById(R.id.newRemainderDurationSpinner);
        mRemainderSwitch = findViewById(R.id.newRemainderSwitch);
        mTimeBeforeSpinner = findViewById(R.id.newRemainderTimeBeforeSpinner);
        mAcceptButton= findViewById(R.id.newEventAcceptButton);
        mCancelButton = findViewById(R.id.newEventCancelButton);

        // Setear el spinner de deportes
        // Recuperar lista de sports desde BBDD
        newRemainderActivityViewModel.getSports(new SportRepository.OnSportsLoadedListener() {
            @Override
            public void onSportsLoaded(List<String> sports) {
                // Set up Spinner adapter with sports list
                ArrayAdapter<String> adapter = new ArrayAdapter<>(NewRemainderActivity.this, android.R.layout.simple_spinner_item, sports);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mSportSpinner.setAdapter(adapter);
            }
        });

        /* Ajustar Spinner de tipo de configuración de tiempo */
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.time_array, android.R.layout.simple_spinner_item);
        // Especificar el layout de uso cuando la lista de elecciones aparece
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Aplicar el adaptador al spinner
        mDurationSpinner.setAdapter(adapter2);

        /* Ajustar Spinner de tiempo de notificación con antelación */
        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(this,
                R.array.time_before_array, android.R.layout.simple_spinner_item);
        // Especificar el layout de uso cuando la lista de elecciones aparece
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Aplicar el adaptador al spinner
        mTimeBeforeSpinner.setAdapter(adapter3);

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
                DatePickerDialog datePickerDialog = new DatePickerDialog(NewRemainderActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        mDateText.setText(day + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day);
                datePickerDialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
                datePickerDialog.show();
            }
        });

        // Listener del switch de notificación
        mRemainderSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mRemainderSwitch.isChecked()) {
                    mTimeBeforeSpinner.setEnabled(true);
                } else {
                    mTimeBeforeSpinner.setEnabled(false);
                }
            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Mostrar ventana para ver si de verdad quiere cancelar la acción
                // Build the confirmation dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(NewRemainderActivity.this);
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

    }

}