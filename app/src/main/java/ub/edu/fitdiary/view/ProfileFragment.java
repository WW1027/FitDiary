package ub.edu.fitdiary.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ub.edu.fitdiary.R;
import ub.edu.fitdiary.model.User;
import ub.edu.fitdiary.viewmodel.NewRemainderActivityViewModel;
import ub.edu.fitdiary.viewmodel.ProfileFragmentViewModel;

public class ProfileFragment extends Fragment {

    //Get all the views in the fragment_profile.xml

    //private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    //private FirebaseFirestore mDb = FirebaseFirestore.getInstance();
    //private FirebaseUser user = mAuth.getCurrentUser();
    private TextView usernameTextView;
    private EditText nameEditText, surnameEditText, dateEditText, emailEditText, suggestionsEditText;
    private Button   sendButton,logOutButton, themeButton;
    private Spinner sexSpinner;
    private ImageView profileImageView, birthSelectorImageView, usernameButtonEdit,nameButtonEdit, surnameButtonEdit, cameraImageView;

    //Esta variable sirve para que el primer onClickListener del sexspinner de editar no haga nada, para que no lo setea a "Man"
    private boolean isInitialSelection = true;

    private Uri mPhotoUri;

    private ProfileFragmentViewModel profileFragmentViewModel;
    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_profile, container, false);
        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState){
        profileFragmentViewModel = new ViewModelProvider(this)
                .get(ProfileFragmentViewModel.class);
        initView(view);
    }

    public void onResume() {
        super.onResume();
        isInitialSelection = true;

    }

    private void initView(View view) {
        //associete the views with the xml

        usernameTextView = view.findViewById(R.id.profileUsernameText);
        nameEditText= view.findViewById(R.id.profileNameEditText);
        surnameEditText= view.findViewById(R.id.profileSurnameEditText);
        dateEditText= view.findViewById(R.id.profileBirthRectangle);
        emailEditText = view.findViewById(R.id.profileEmailEditText);
        suggestionsEditText= view.findViewById(R.id.profileSuggestionsEditText);
        usernameButtonEdit= view.findViewById(R.id.profileUsernameImageEdit);
        nameButtonEdit= view.findViewById(R.id.profileNameImageEdit);
        surnameButtonEdit= view.findViewById(R.id.profileSurnameImageEdit);
        sendButton= view.findViewById(R.id.profileSendButton);
        logOutButton= view.findViewById(R.id.profileLogOutButton);
        themeButton= view.findViewById(R.id.profileThemeButton);
        sexSpinner= view.findViewById(R.id.profileSexSpinner);
        birthSelectorImageView= view.findViewById(R.id.profileBirthSelector);
        cameraImageView = view.findViewById(R.id.profileCameraImageView);

        profileImageView = view.findViewById(R.id.profileImageView);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.sex_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        sexSpinner.setAdapter(adapter);

        /**
         * Inicializar a partir de base de datos
         */
        //initData();
        profileFragmentViewModel.getUserData().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user != null) {
                    usernameTextView.setText(user.getUsername());
                    nameEditText.setText(user.getName());
                    surnameEditText.setText(user.getSurname());
                    dateEditText.setText(user.getBirthday());
                    emailEditText.setText(profileFragmentViewModel.getEmail());
                    sexSpinner.setSelection(adapter.getPosition(user.getSex()));
                }
            }
        });

        themeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });


        sexSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!isInitialSelection)
                    profileFragmentViewModel.updateCompletion("sex", sexSpinner.getSelectedItem().toString());
                else isInitialSelection = false;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        birthSelectorImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //code to pick a date from the calendar
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                //pick a date with and

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        dateEditText.setText(day + "/" + (month + 1) + "/" + year);
                        updateCompletion("birthday", dateEditText.getText().toString());
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        nameButtonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modifyData(nameEditText, "Modify Name", "name");
            }
        });

        surnameButtonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modifyData(surnameEditText, "Modify Surname", "surname");
            }
        });


        usernameButtonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modifyData(usernameTextView, "Modify Username", "username");
            }
        });

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Build the confirmation dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Confirm Logout");
                builder.setMessage("Are you sure you want to log out?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Logout
                        profileFragmentViewModel.signOut();
                        Intent intent = new Intent(getContext(), AuthenticationActivity.class);
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("No", null);

                // Show the confirmation dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String texto = suggestionsEditText.getText().toString();
                if (texto.isEmpty()) {
                    Toast.makeText(getContext(), "Please enter text", Toast.LENGTH_SHORT).show();
                }

                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Confirm Suggestion");
                    builder.setMessage("Are you sure to send the suggestion?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Llamar al método de su propio model view
                            profileFragmentViewModel.sendSuggestion(new Date(), suggestionsEditText.getText().toString());
                            suggestionsEditText.setText("");
                        }
                    });
                    builder.setNegativeButton("No", null);

                    // Show the confirmation dialog
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });


        setTakeCameraPictureListener(cameraImageView);
        setChoosePictureListener(profileImageView);

        final Observer<String> observerPictureUrl = new Observer<String>() {
            @Override
            public void onChanged(String pictureUrl) {
                if (pictureUrl == "") System.out.println("pictureUrl is null");
                    else {
                    Picasso.get()
                            .load(pictureUrl)
                            .resize(profileImageView.getWidth(), profileImageView.getHeight())
                            .centerCrop()
                            .into(profileImageView);
                }
            }
        };
        profileFragmentViewModel.getPictureUrl().observe(getViewLifecycleOwner(), observerPictureUrl);

        profileFragmentViewModel.loadPictureOfUser();


    }

    private void modifyData(TextView textView, String text, String field){

        // create a new AlertDialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(text);

        // create a layout for the dialog
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        // create an EditText view for user input
        final EditText editText = new EditText(getContext());
        layout.addView(editText);

        // add the layout to the dialog builder
        builder.setView(layout);

        builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // retrieve the user's message from the EditText view
                String message = editText.getText().toString();
                textView.setText(message);
                /********************************************************
                 Modificar en la base de datos //Se tiene que pasar por parámetro qué campo se va a modificar
                 *********************************************************/
                updateCompletion(field, message);

            }
        });
        builder.setNegativeButton("Cancel", null);

        // Show the confirmation dialog
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    protected void updateCompletion(String field, String text) {
        // Como es cambio en la base de datos, se lo pedimos a viewmodel
        profileFragmentViewModel.updateCompletion(field, text);
    }

    /*public void initData(){
        String email = this.user.getEmail();
        DocumentReference docRef = mDb.collection("users").document(email);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot document) {
                if (document.exists()) {
                    emailEditText.setText(email);
                    usernameTextView.setText(email);
                    nameEditText.setText(document.getString("name"));
                    surnameEditText.setText(document.getString("surname"));
                    dateEditText.setText(document.getString("birthday"));
                    if(document.getString("sex").equals("Man")) {
                        sexSpinner.setSelection(0);}
                    else{
                        sexSpinner.setSelection(1);}
                } else {
                    Toast.makeText(getContext(),"No document",Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
            }});
    }*/



    private void setTakeCameraPictureListener(@NonNull View takePictureView) {
        // Codi que s'encarrega de rebre el resultat de l'intent de fer foto des de càmera
        // i que es llençarà des del listener que definirem a baix.
        ActivityResultLauncher<Intent> takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            profileFragmentViewModel.setPictureUrlOfUser(
                                     mPhotoUri
                            );
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
            File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

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
            Uri photoUri = FileProvider.getUriForFile(getContext(),
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

    private void setChoosePictureListener(@NonNull View choosePicture) {
        // Codi que s'encarrega de rebre el resultat de l'intent de seleccionar foto de galeria
        // i que es llençarà des del listener que definirem a baix.
        ActivityResultLauncher<Intent> startActivityForResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Uri contentUri = data.getData(); // En aquest intent, sí que hi arriba la URI
                        /*
                         * [Exercici 2: Aquí hi manca 1 línia de codi]
                         */
                        profileFragmentViewModel.setPictureUrlOfUser(
                                contentUri);
                    }
                });

        // Listener del botó de seleccionar imatge, que llençarà l'intent amb l'ActivityResultLauncher.
        choosePicture.setOnClickListener(view -> {
            Intent data = new Intent(Intent.ACTION_GET_CONTENT);
            data.addCategory(Intent.CATEGORY_OPENABLE);
            data.setType("image/*");
            Intent intent = Intent.createChooser(data, "Choose a file");
            /*
             * [Exercici 2: Aquí hi manca 1 línia de codi]
             */
            startActivityForResult.launch(intent);
        });
    }





}