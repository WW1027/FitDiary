package ub.edu.fitdiary.view;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import ub.edu.fitdiary.R;

public class AuthenticationActivity extends AppCompatActivity {

    /* Elementos de la vista de AuthenticationActivity */

    private ImageView mIconImage;
    private TextView mAppName;
    private TextView mLoginText;
    private TextView mEmailTextView;
    private EditText mEmailEditText;
    private TextView mPasswordTextView;
    private EditText mPasswordEditText;
    private Button mLoginButton;
    private TextView mSignUpTextView;
    private TextView mSignUpClickText;
    private TextView mForgetPasswordText;

    /* Mòdul autenticació de Firebase */

    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        mIconImage = findViewById(R.id.authenticationIcon);
        mAppName = findViewById(R.id.authenticationAppName);
        mLoginText = findViewById(R.id.authenticationLoginText);
        mEmailTextView = findViewById(R.id.authenticationEmailTextView);
        mEmailEditText = findViewById(R.id.authenticationEmailEditText);
        mPasswordTextView = findViewById(R.id.authenticationPasswordTextView);
        mPasswordEditText = findViewById(R.id.authenticationPasswordEditText);
        mLoginButton = findViewById(R.id.authenticationLoginButton);
        mSignUpTextView = findViewById(R.id.authenticationSignUpText);
        mSignUpClickText = findViewById(R.id.authenticationSignUpClickText);
        mForgetPasswordText = findViewById(R.id.authenticationForgetPasswordText);

        mLoginButton.setOnClickListener(view -> {
            // Prueba de hacer sign-in (aka login)
            mAuth.signInWithEmailAndPassword(mEmailEditText.getText().toString(), mPasswordEditText.getText().toString())
                    .addOnCompleteListener(AuthenticationActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) { // Si es pot loguejar, passa a la Home
                                Intent intent = new Intent(AuthenticationActivity.this, MainActivity.class);
                                startActivity(intent);
                            } else {
                                // Si falla el logueig, fes un Toast
                                //Log.d(TAG, "Sign up create user succeeded");
                                Toast.makeText(getApplicationContext(), "Login failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
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
        if (mAuth.getCurrentUser() != null) {
            Intent intent = new Intent(AuthenticationActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }
}