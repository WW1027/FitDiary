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

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.squareup.picasso.Picasso;

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

        /* TODO: completar la información obtenida de la base de datos */

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
                    mComment.setText(event.getComment());
                    mCalories.setText(String.valueOf(Integer.parseInt(event.getPulse()) * Integer.parseInt(event.getDuration())));
                    if (event.getImageURL()!=""){
                        Picasso.get()
                                .load(event.getImageURL())
                                .resize(mImage.getWidth(), mImage.getHeight())
                                .centerCrop()
                                .into(mImage);}

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
}