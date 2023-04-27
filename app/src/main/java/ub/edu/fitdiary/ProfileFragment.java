package ub.edu.fitdiary;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    //Get all the views in the fragment_profile.xml

    private EditText nameEditText, surnameEditText, dateEditText, emailEditText, sexEditText, suggestionsEditText;
    private Button   sendButton,logOutButton, themeButton;
    private Spinner sexSpinner;
    private ImageView profileImageView, birthSelectorImageView, usernameButtonEdit,nameButtonEdit, surnameButtonEdit;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view= inflater.inflate(R.layout.fragment_profile, container, false);

        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState){
        initView(view);

    }



    private void initView(View view) {
        //associete the views with the xml

        nameEditText= view.findViewById(R.id.profileNameEditText);
        surnameEditText= view.findViewById(R.id.profileSurnameEditText);
        dateEditText= view.findViewById(R.id.profileBirthRectangle);
        emailEditText= view.findViewById(R.id.profileEmailEditText);
        suggestionsEditText= view.findViewById(R.id.profileSuggestionsEditText);
        usernameButtonEdit= view.findViewById(R.id.profileUsernameImageEdit);
        nameButtonEdit= view.findViewById(R.id.profileNameImageEdit);
        surnameButtonEdit= view.findViewById(R.id.profileSurnameImageEdit);
        sendButton= view.findViewById(R.id.profileSendButton);
        logOutButton= view.findViewById(R.id.profileLogOutButton);
        themeButton= view.findViewById(R.id.profileThemeButton);
        sexSpinner= view.findViewById(R.id.profileSexSpinner);
        birthSelectorImageView= view.findViewById(R.id.profileBirthSelector);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.sex_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        sexSpinner.setAdapter(adapter);

        /**
         * Falta iniciar el spinner con el valor de la base de datos
         */


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
                    }
                }, year, month, day);
                datePickerDialog.show();




            }
        });

        nameButtonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modifyData(nameEditText, "Modify Name");
            }
        });

        surnameButtonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modifyData(surnameEditText, "Modify Surname");
            }
        });


        usernameButtonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modifyData(nameEditText, "Modify Username");
            }
        });

/*
        sexSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



            }
        });

*/

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
                        /********************************************************
                         Poner la actividad a transicionar
                         *********************************************************/
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

                            /********************************************************
                             Poner donde se envia la sugerencia
                             *********************************************************/
                        }
                    });
                    builder.setNegativeButton("No", null);

                    // Show the confirmation dialog
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }



            }
        });

    }

    private void modifyData( TextView textView, String text){

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
            }
        });
        builder.setNegativeButton("Cancel", null);

// Show the confirmation dialog
        AlertDialog dialog = builder.create();
        dialog.show();

    }




    }

