package ub.edu.fitdiary.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ub.edu.fitdiary.R;
import ub.edu.fitdiary.model.Event;
import ub.edu.fitdiary.viewmodel.NewEventActivityViewModel;

public class DetailedInformationActivity extends AppCompatActivity {
    // Atributos del layout
    private TextView mSport, mDate, mDuration, mPulse, mCalories, mComment;
    private ImageView mBackButton, mImage, mEditDurationButton, mEditPulseButton, mEditCommentButton, mCamera;
    private Button mDeleteButton;
    private Uri mPhotoUri, auxiliarA, auxiliarB;

    // Atributos del view model o model del view
    private NewEventActivityViewModel newEventActivityViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_information);

        Intent intent = getIntent();
        String date = intent.getStringExtra("date");


        // Instanciamos su propio view model
        newEventActivityViewModel = new ViewModelProvider(this)
                .get(NewEventActivityViewModel.class);

        newEventActivityViewModel.loadEventData(date);
        getSupportActionBar().hide(); //hide the title bar

        // Relacionamos el id con la variable para referirnos a ella
        mBackButton = findViewById(R.id.detailedInformationBackButton);
        mImage = findViewById(R.id.detailedInformationImage);
        mSport = findViewById(R.id.detailedInformationSport);
        mDate = findViewById(R.id.detailedInformationDate);
        mDuration = findViewById(R.id.detailedInformationDuration);
        mPulse = findViewById(R.id.detailedInformationPulse);
        mCalories = findViewById(R.id.detailedInformationCalories);
        mComment = findViewById(R.id.detailedInformationComment);
        mEditDurationButton = findViewById(R.id.detailedInformationEditDurationButton);
        mDeleteButton = findViewById(R.id.detailedInformationDeleteButton);
        mEditPulseButton = findViewById(R.id.detailedInformationEditPulseButton);
        mEditCommentButton = findViewById(R.id.detailedInformationEditCommentButton);
        mCamera = findViewById(R.id.newEventCameraImageView3);

        /* TODO: completar la información obtenida de la base de datos */

        boolean isFuture = isDateInFuture(date);
        if (isFuture) {
            // Date is in the future
            // Disable and hide the Edit buttons
            EnableEditButtons(false, View.GONE);
        } else {
            // Date is in the past or equal to the current date
            // Enable and show the Edit buttons
            EnableEditButtons(true, View.VISIBLE);
        }

        // Cargar los datos del evento
        newEventActivityViewModel.getEventData().observe(DetailedInformationActivity.this, new Observer<Event>() {
            @Override
            public void onChanged(Event event) {
                Log.d("TAG", "observed");
                if (event != null) {
                    mDate.setText(event.getDate());
                    mSport.setText(event.getSport());
                    mDuration.setText(event.getDuration());
                    mPulse.setText(event.getPulse());
                    if(isFuture){ mComment.setText(null); }
                    else { mComment.setText(event.getComment());}
                    if(!mDuration.getText().toString().isEmpty() && !mPulse.getText().toString().isEmpty()){
                        mCalories.setText(String.valueOf(Integer.parseInt(event.getPulse()) * Integer.parseInt(event.getDuration())));}
                    else{
                        mCalories.setText(null);
                    }
                    if (event.getImageURL()!=""){
                        Picasso.get().load(event.getImageURL()).into(mImage);}

                    updateCompletion("sport", event.getSport(), event.getDate());
                }
            }
        });

        // Botón para editar la duración
        mEditDurationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modifyData(mDuration, "Modify Duration", "duration", date);
            }
        });

        //Botón para editar la pulsación
        mEditPulseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modifyData(mPulse, "Modify Pulse", "pulse", date);
            }
        });

        //Botón para editar el commentario
        mEditCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modifyData(mComment, "Modify Comment", "comment", date);
            }
        });

        // Botón para eliminar el evento
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Build the confirmation dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailedInformationActivity.this);
                builder.setTitle("Delete Event");
                builder.setMessage("Are you sure you want to delete that event?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Delete Event
                        newEventActivityViewModel.deleteEvent(date);

                        //El date està en format "dd-MM-yyyy i després hora"
                        //Però només volem el "dd-MM-yyyy"
                        int firstSpaceIndex = date.indexOf(" ");
                        String formattedDate = date.substring(0, firstSpaceIndex);

                        loadFragment(new CalendarFragment(),true,formattedDate);

                       finish();
                    }
                });
                builder.setNegativeButton("No", null);

                // Show the confirmation dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        // Botón para volver a la pantalla anterior
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new CalendarFragment(),true,mDate.getText().toString());
                finish();
            }
        });

        setTakeCameraPictureListener(mCamera);

    }

    private void modifyData(TextView textView, String text, String field, String id){

        // create a new AlertDialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(text);

        // create a layout for the dialog
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        // create an EditText view for user input
        final EditText editText = new EditText(this);
        layout.addView(editText);

        // add the layout to the dialog builder
        builder.setView(layout);

        if(textView.equals(mDuration) || textView.equals(mPulse)){
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        }else{
            editText.setInputType(InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE);
        }

        builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // retrieve the user's message from the EditText view
                String message = editText.getText().toString();
                if(textView.equals(mPulse)){
                    if(Integer.parseInt(message)<50 ||
                            Integer.parseInt(message)>300){
                        Toast.makeText(getApplicationContext(), "Pulse should be between 50 and 300",
                                Toast.LENGTH_SHORT).show();
                    }else{
                        textView.setText(message);
                        /********************************************************
                         Modificar en la base de datos //Se tiene que pasar por parámetro qué campo se va a modificar
                         *********************************************************/
                        updateCompletion(field, message, id);
                    }
                }else{
                    textView.setText(message);
                    /********************************************************
                     Modificar en la base de datos //Se tiene que pasar por parámetro qué campo se va a modificar
                     *********************************************************/
                    updateCompletion(field, message, id);
                }
            }
        });
        builder.setNegativeButton("Cancel", null);

        // Show the confirmation dialog
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    //Update los datos del Firebase
    protected void updateCompletion(String field, String text, String id) {
        // Como es cambio en la base de datos, se lo pedimos a viewmodel
        newEventActivityViewModel.updateCompletion(field, text, id);
    }

    //Comprobar si el date es futuro
    private boolean isDateInFuture(String dateString) {
        // Create a SimpleDateFormat object to parse the date string
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy");

        try {
            // Parse the date string into a Date object
            Date date = dateFormat.parse(dateString);

            // Get the current date and time
            Date currentDate = new Date();

            // Compare the dates
            if (date.after(currentDate) || date.equals(currentDate)) {
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

    private void EnableEditButtons(boolean enabled, int view){
        mEditDurationButton.setEnabled(enabled);
        mEditDurationButton.setVisibility(view);
        mEditPulseButton.setEnabled(enabled);
        mEditPulseButton.setVisibility(view);
        mEditCommentButton.setEnabled(enabled);
        mEditCommentButton.setVisibility(view);
        mCamera.setVisibility(view);
        mCamera.setEnabled(enabled);
    }


    public void loadFragment(Fragment fragment, boolean flag, String date) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        if (flag) {
            ft.add(R.id.detailedInfomationLayout, fragment);
        } else {
            ft.replace(R.id.detailedInfomationLayout, fragment);
        }

        // Pass the string parameter to the fragment's arguments bundle
        Bundle bundle = new Bundle();
        bundle.putString("date", date);
        fragment.setArguments(bundle);

        ft.commit();
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
                            updateCompletion("imageURL", mPhotoUri.toString(), mDate.getText().toString());
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
}