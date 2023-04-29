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
import com.google.firebase.auth.FirebaseAuth;

import ub.edu.fitdiary.R;

public class ResetPasswordActivity extends AppCompatActivity {

    private final String TAG = "ResetPasswordSendEmailActivity";

    private ImageView mCancelButton;
    private EditText mEmailEditText;
    private Button mAcceptButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        getSupportActionBar().hide(); //hide the title bar

        mCancelButton = findViewById(R.id.resetPasswordCancel);
        mEmailEditText = findViewById(R.id.resetPasswordEmailEditText);
        mAcceptButton = findViewById(R.id.resetPasswordAcceptButton);

        mAcceptButton.setOnClickListener(view -> {
            FirebaseAuth.getInstance().sendPasswordResetEmail(mEmailEditText.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Email Sent Successfully!!!",
                                        Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ResetPasswordActivity.this, AuthenticationActivity.class);
                                startActivity(intent);
                            }else{
                                Toast.makeText(getApplicationContext(), "User does not exit",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        });

        mCancelButton.setOnClickListener(view -> {
            Intent intent = new Intent(ResetPasswordActivity.this, AuthenticationActivity.class);
            startActivity(intent);
        });
    }
}