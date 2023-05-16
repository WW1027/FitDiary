package ub.edu.fitdiary;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;


import ub.edu.fitdiary.view.CalendarFragment;
import ub.edu.fitdiary.view.StatisticsFragment;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //chart();   //chart

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        ActionBar actionBar = getSupportActionBar();

        getSupportActionBar().hide(); //hide the title bar



        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.statisticsFragment:
                        loadFragment(new StatisticsFragment(),true);
                        break;
                    case R.id.calendarFragment:
                        loadFragment(new CalendarFragment(),false);
                        break;
                    case R.id.profileFragment:
                        loadFragment(new ProfileFragment(),false);
                        break;
                }
                return true;
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.statisticsFragment);
    }

    public void loadFragment(Fragment fragment, boolean flag){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if(flag)
            ft.add(R.id.container, fragment);
        else
            ft.replace(R.id.container, fragment);
        ft.commit();
    }


}