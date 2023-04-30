package ub.edu.fitdiary.view;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import ub.edu.fitdiary.R;
import ub.edu.fitdiary.viewmodel.AuthenticationActivityViewModel;
import ub.edu.fitdiary.viewmodel.NewRemainderActivityViewModel;

public class AuthenticationActivity extends AppCompatActivity {

    /* Elementos de la vista de AuthenticationActivity */
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private Button mLoginButton;
    private TextView mSignUpClickText;
    private TextView mForgetPasswordText;
    private AuthenticationActivityViewModel authenticationActivityViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        getSupportActionBar().hide(); //hide the title bar

        authenticationActivityViewModel = new ViewModelProvider(this)
                .get(AuthenticationActivityViewModel.class);

        mEmailEditText = findViewById(R.id.authenticationEmailEditText);
        mPasswordEditText = findViewById(R.id.authenticationPasswordEditText);
        mLoginButton = findViewById(R.id.authenticationLoginButton);
        mSignUpClickText = findViewById(R.id.authenticationSignUpClickText);
        mForgetPasswordText = findViewById(R.id.authenticationForgetPasswordText);

        mLoginButton.setOnClickListener(view -> {
            authenticationActivityViewModel.authenticateUser(mEmailEditText.getText().toString(), mPasswordEditText.getText().toString());
        });

        // Abrir activity de sign up
        mSignUpClickText.setOnClickListener(view -> {
            Intent intent = new Intent(AuthenticationActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        // Abrir activity de reset password
        mForgetPasswordText.setOnClickListener(view -> {
            Intent intent = new Intent(AuthenticationActivity.this, ResetPasswordActivity.class);
            startActivity(intent);
        });

    }

    @Override
    public void onResume(){
        super.onResume();
        // Si encara estessim loguejats, podem anar directament a MainActivity
        if (authenticationActivityViewModel.isUserLogged()) {
            Intent intent = new Intent(AuthenticationActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }
}