package ub.edu.fitdiary.view;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import ub.edu.fitdiary.R;

public class NewEventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        getSupportActionBar().hide(); //hide the title bar
    }
}