package ub.edu.fitdiary.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;


import ub.edu.fitdiary.R;
import ub.edu.fitdiary.viewmodel.AuthenticationActivityViewModel;

public class ResetPasswordActivity extends AppCompatActivity {

    private final String TAG = "ResetPasswordSendEmailActivity";
    private ImageView mCancelButton;
    private EditText mEmailEditText;
    private Button mAcceptButton;
    private AuthenticationActivityViewModel authenticationActivityViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        getSupportActionBar().hide(); //hide the title bar

        authenticationActivityViewModel = new ViewModelProvider(this)
                .get(AuthenticationActivityViewModel.class);

        mCancelButton = findViewById(R.id.resetPasswordCancel);
        mEmailEditText = findViewById(R.id.resetPasswordEmailEditText);
        mAcceptButton = findViewById(R.id.resetPasswordAcceptButton);

        mAcceptButton.setOnClickListener(view -> {
            authenticationActivityViewModel.sendResetPasswordEmail(mEmailEditText.getText().toString()).observe(this, result -> {
                if (result) {
                    Toast.makeText(getApplicationContext(), "Email Sent Successfully!!!",
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ResetPasswordActivity.this, AuthenticationActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Email Sent Failed!!!",
                            Toast.LENGTH_SHORT).show();
                }
            });
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Mostrar ventana para ver si de verdad quiere cancelar la acción
                // Build the confirmation dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(ResetPasswordActivity.this);
                builder.setTitle("Confirm Cancel Resetting Password");
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