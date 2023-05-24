package ub.edu.fitdiary.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ub.edu.fitdiary.R;
import ub.edu.fitdiary.model.Event;
import ub.edu.fitdiary.viewmodel.NewEventActivityViewModel;

public class DetailedInformationActivity extends AppCompatActivity {
    // Atributos del layout
    private ImageView mBackButton;
    private ImageView mImage;
    private TextView mSport;
    private TextView mDate;
    private TextView mDuration;
    private TextView mPulse;
    private TextView mCalories;
    private TextView mComment;
    private ImageView mEditDurationButton;
    private ImageView mEditPulseButton;
    private ImageView mEditCommentButton;
    private Button mDeleteButton;

    // Atributos del view model o model del view
    private NewEventActivityViewModel newEventActivityViewModel;

    private boolean isInitialSelection = true;
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
                finish();
            }
        });

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

        builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // retrieve the user's message from the EditText view
                String message = editText.getText().toString();
                textView.setText(message);
                /********************************************************
                 Modificar en la base de datos //Se tiene que pasar por parámetro qué campo se va a modificar
                 *********************************************************/
                updateCompletion(field, message, id);
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

    private void EnableEditButtons(boolean enabled, int view){
        mEditDurationButton.setEnabled(enabled);
        mEditDurationButton.setVisibility(view);
        mEditPulseButton.setEnabled(enabled);
        mEditPulseButton.setVisibility(view);
        mEditCommentButton.setEnabled(enabled);
        mEditCommentButton.setVisibility(view);
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
}