package ub.edu.fitdiary.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import ub.edu.fitdiary.R;

public class NewRecordatorioActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_recordatorio);

        getSupportActionBar().hide(); //hide the title bar
    }
}