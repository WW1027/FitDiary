package ub.edu.fitdiary.view;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import ub.edu.fitdiary.R;
import ub.edu.fitdiary.viewmodel.AuthenticationActivityViewModel;

public class SignUpActivity extends AppCompatActivity {

    private final String TAG = "SignUpActivity";

    private ImageView mCancelButton;
    private EditText mNameEditText;
    private EditText mSurnameEditText;
    private EditText mDateEditText;
    private ImageView mDateImageView;
    private Spinner mSexSpinner;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private CheckBox mAgreeCheckBox;
    private TextView mServiceText;
    private TextView mPrivacyText;
    private Button mAcceptButton;
    private TextView mSignInClickText;

    private AuthenticationActivityViewModel authenticationActivityViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        getSupportActionBar().hide(); //hide the title bar

        authenticationActivityViewModel =  new ViewModelProvider(this)
                .get(AuthenticationActivityViewModel.class);

        mCancelButton = findViewById(R.id.signUpbtnCancel);
        mNameEditText = findViewById(R.id.signUpNameEditText);
        mSurnameEditText = findViewById(R.id.signUpSurnameEditText);
        mDateEditText = findViewById(R.id.signUpDateEditText);
        mDateImageView = findViewById(R.id.signUpDateImageView);
        mSexSpinner = findViewById(R.id.signUpSexSpinner);
        mEmailEditText = findViewById(R.id.signUpEmailEditText);
        mPasswordEditText = findViewById(R.id.signUpPasswordEditText);
        mAgreeCheckBox = findViewById(R.id.signUpAgreeCheckBox);
        //mServiceText = findViewById(R.id.signUpServiceTextView);
        //mPrivacyText = findViewById(R.id.signUpPrivacyTextView);
        mAcceptButton = findViewById(R.id.signUpAcceptButton);
        mSignInClickText = findViewById(R.id.signUpSignInClickText);

        mAcceptButton.setOnClickListener(view -> {
            if(!mAgreeCheckBox.isChecked()){
                Toast.makeText(getApplicationContext(), "You must agree the conditions",
                        Toast.LENGTH_SHORT).show();
            }else if(!isDataEmpty(new ArrayList<>(Arrays.asList(mNameEditText, mSurnameEditText, mDateEditText)))) {
                try{
                    signUp(mEmailEditText.getText().toString(), mPasswordEditText.getText().toString());}
                catch(Exception e){
                    Toast.makeText(getApplicationContext(), e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Abrir activity de sign in
        mSignInClickText.setOnClickListener(view -> {
            Intent intent = new Intent(SignUpActivity.this, AuthenticationActivity.class);
            startActivity(intent);
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(SignUpActivity.this, R.array.sex_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mSexSpinner.setAdapter(adapter);

        mDateImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //code to pick a date from the calendar
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                //pick a date with and

                DatePickerDialog datePickerDialog = new DatePickerDialog(SignUpActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        mDateEditText.setText(day + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        // Calcelar acción
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Mostrar ventana para ver si de verdad quiere cancelar la acción
                // Build the confirmation dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                builder.setTitle("Confirm Cancel Signing Up");
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

    protected void signUp(String email, String password) {
        authenticationActivityViewModel.signUp(
                email,
                password,
                mNameEditText.getText().toString(),
                mSurnameEditText.getText().toString(),
                mDateEditText.getText().toString(),
                mSexSpinner.getSelectedItem().toString()).observe(this, result -> {
            if (result) {
                Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Account creation failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected boolean isEmpty(EditText text){
        if (text.getText().toString().isEmpty()){
            text.setHintTextColor(Color.parseColor("#FF0000"));
            return true;
        }else return false;
    }

    private boolean isDataEmpty(ArrayList<EditText> view) {
        boolean empty=false;
        for(EditText e: view){
            if(isEmpty(e)){
                Toast.makeText(getApplicationContext(), "Introduce missing data",
                        Toast.LENGTH_SHORT).show();
                empty=true;
            }
        }
        return empty;

    }
}