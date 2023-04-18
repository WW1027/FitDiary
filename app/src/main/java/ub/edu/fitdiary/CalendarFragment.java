package ub.edu.fitdiary;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CalendarFragment extends Fragment {
    FloatingActionButton addButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflar el diseño del fragmento
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        // Buscar el botón en la vista raíz del fragmento
        addButton = view.findViewById(R.id.calendarFloatingActionButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Código para abrir la nueva actividad aquí
                Intent intent = new Intent(getActivity(), NewEventActivity.class);
                startActivity(intent);
            }
        });

        // Devolver la vista inflada
        return view;
    }


}