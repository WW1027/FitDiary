package ub.edu.fitdiary.view;


import android.app.Activity;
import android.app.AlertDialog;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
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
    private EditText mPulseText;
    private ImageView mEventImage;
    private EditText mCommentText;
    private Button mSaveButton;
    private ImageView mCancelButton, mHintPulseImage, mcameraButton;

    private Uri mPhotoUri, auxiliarA, auxiliarB;



    // Atributos del view model o model del view
    private NewEventActivityViewModel newEventActivityViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        // Instanciamos su propio view model
        newEventActivityViewModel = new ViewModelProvider(this)
                .get(NewEventActivityViewModel.class);

        getSupportActionBar().hide(); //hide the title bar

        // Relacionamos el id con la variable para referirnos a ella
        mDateText = findViewById(R.id.newEventDateTextRectangle);
        mDateImage = findViewById(R.id.newEventDateImageSelector);
        mSportSpinner = findViewById(R.id.newEventSportSpinner);
        mDurationText = findViewById(R.id.newEventDurationTextRectangle);
        mPulseText = findViewById(R.id.newEventPulseTextRectangle);
        mEventImage = findViewById(R.id.newEventImageRectangle);
        mSaveButton = findViewById(R.id.newEventAcceptButton);
        mCancelButton = findViewById(R.id.newEventCancelButton);
        mCommentText = findViewById(R.id.newEventCommentRectangle);
        mHintPulseImage = findViewById(R.id.newEventDateImagePulse);
        mcameraButton = findViewById(R.id.newEventCameraImageView);
        auxiliarA=null;
        auxiliarB=null;

        mDateText.setText(getIntent().getStringExtra("date"));

        /* Añadimos listener al botón de añadir */
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // En caso de que los campos de Date, Sport, Duration y pulsaciones estén vacíos, no se puede añadir evento
                if (mDateText.getText().toString().isEmpty() ||
                        mSportSpinner.getSelectedItem().toString().isEmpty() ||
                        mDurationText.getText().toString().isEmpty() ||
                        mPulseText.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Fields of Date, Sport, Duration and Pulse are compulsory",
                            Toast.LENGTH_SHORT).show();
                }/*else if (mDurationText.getText().toString().matches("\\d+")==false ||
                            mPulseText.getText().toString().matches("\\d+")==false){ //Para comprobar que sean números
                    Toast.makeText(getApplicationContext(), "Durantion and Pulse must be numbers",
                            Toast.LENGTH_SHORT).show();}*/
                else if (Integer.parseInt(mPulseText.getText().toString())<50 ||
                        Integer.parseInt(mPulseText.getText().toString())>300){ //Test de rango
                    Toast.makeText(getApplicationContext(), "Pulse should be between 50 and 300",
                            Toast.LENGTH_SHORT).show();}
                else { // Si están los tres campos obligatorios rellenados, se añade el evento
                    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                    Date date = new Date();
                    String horaActual = dateFormat.format(date); // Hora actual en formato de cadena
                    String URL=null;
                    if (mPhotoUri!=null){URL=mPhotoUri.toString();}
                    newEventActivityViewModel.addEvent(
                            mDateText.getText().toString()+" "+horaActual,
                            mSportSpinner.getSelectedItem().toString(),
                            mDurationText.getText().toString(),
                            mPulseText.getText().toString(),
                            mCommentText.getText().toString(),
                            URL
                    );
                    loadFragment(new CalendarFragment(),true,mDateText.getText().toString());
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

        // Recuperar lista de sports desde BBDD
        newEventActivityViewModel.getSports(new SportRepository.OnSportsLoadedListener() {
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
                /*SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                Date date = new Date();
                String horaActual = dateFormat.format(date); // Hora actual en formato de cadena*/

                //pick a date with android date picker dialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(NewEventActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        mDateText.setText(day + "-" + (month + 1) + "-" + year);
                    }
                }, year, month, day);
                datePickerDialog.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis());
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

        setTakeCameraPictureListener(mcameraButton);
        final Observer<String> observerPictureUrl = new Observer<String>() {
            @Override
            public void onChanged(String pictureUrl) {
                if (pictureUrl == "") System.out.println("pictureUrl is null");
                else {
                    Picasso.get()
                            .load(pictureUrl)
                            .resize(mEventImage.getWidth(), mEventImage.getHeight())
                            .centerCrop()
                            .into(mEventImage);
                }
            }
        };
        newEventActivityViewModel.getPictureAux().observe(this, observerPictureUrl);

        //newEventActivtyViewModel.loadPictureOfUser();



    }
    private void setTakeCameraPictureListener(@NonNull View takePictureView) {
        // Codi que s'encarrega de rebre el resultat de l'intent de fer foto des de càmera
        // i que es llençarà des del listener que definirem a baix.
        ActivityResultLauncher<Intent> takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            newEventActivityViewModel.setPictureUrlOfUser(
                                    mPhotoUri
                            );
                        }else{
                            //Serveix per controlar si l'usuari ha cancel·lat l'acció de fer la foto
                            auxiliarB=mPhotoUri;
                            mPhotoUri=auxiliarA;
                            auxiliarA=auxiliarB;
                        }
                    }
                }
        );

        // Listener del botó de fer foto, que llençarà l'intent amb l'ActivityResultLauncher.
        takePictureView.setOnClickListener(view -> {
            // Crearem un nom de fitxer d'imatge temporal amb una data i hora i format JPEG
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            // Anem a buscar el directori extern (del sistema) especificat per la variable
            // d'entorn Environment.DIRECTORY_PICTURES (pren per valor "Pictures").
            // Se li afageix, com a sufix, el directori del sistema on es guarden els fitxers.
            File storageDir = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

            // Creem el fitxer
            File image = null;
            try {
                image = File.createTempFile(
                        imageFileName,  /* Prefix */
                        ".jpg",         /* Sufix */
                        storageDir      /* Directori on es guarda la imatge */
                );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // Recuperem la Uri definitiva del fitxer amb FileProvider (obligatori per seguretat)
            // Per a fer-ho:
            // 1. Especifiquem a res>xml>paths.xml el directori on es guardarà la imatge
            //    de manera definitiva.
            // 2. Afegir al manifest un provider que apunti a paths.xml del pas 1
            Uri photoUri = FileProvider.getUriForFile(this,
                    "edu.ub.pis.fitdiary.fileprovider",
                    image);

            // Per tenir accés a la URI de la foto quan es llenci l'intent de la camara.
            // Perquè encara que li passem la photoUri com a dades extra a l'intent, aquestes
            // no tornen com a resultat de l'Intent.
            mPhotoUri = photoUri;

            // Llancem l'intent amb el launcher declarat al començament d'aquest mateix mètode
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mPhotoUri);
            takePictureLauncher.launch(intent);
        });
    }
    public void loadFragment(Fragment fragment, boolean flag, String date) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        if (flag) {
            ft.add(R.id.newEventLayout, fragment);
        } else {
            ft.replace(R.id.newEventLayout, fragment);
        }

        // Pass the string parameter to the fragment's arguments bundle
        Bundle bundle = new Bundle();
        bundle.putString("date", date);
        fragment.setArguments(bundle);

        ft.commit();
    }
}
