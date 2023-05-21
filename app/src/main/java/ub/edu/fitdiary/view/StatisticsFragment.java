package ub.edu.fitdiary.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ub.edu.fitdiary.R;
import ub.edu.fitdiary.model.Event;
import ub.edu.fitdiary.model.EventRepository;
import ub.edu.fitdiary.model.User;
import ub.edu.fitdiary.viewmodel.CalendarFragmentViewModel;
import ub.edu.fitdiary.viewmodel.StatisticsFragmentViewModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;



public class StatisticsFragment extends Fragment {
    private ProgressBar progressBar;
    private TextView progressText;
    private int progress;
    private ImageView editGoal;
    private TextView txtGoal;
    private TextView date;
    private StatisticsFragmentViewModel statisticsFragmentViewModel;
    private int minGoal;
    private int actualDuration;
    private int mon;
    private int tue;
    private int wed;
    private int thu;
    private int fri;
    private int sat;
    private int sun;

    /*
    char
    */
    // variable for our bar chart
    private BarChart barChart;

    // variable for our bar data.
    private BarData barData;

    // variable for our bar data set.
    private BarDataSet barDataSet;

    // array list for storing entries.
    private ArrayList barEntriesArrayList;
    /*
    char
     */


    public StatisticsFragment() {
        // Required empty public constructor
        minGoal = 0;
        actualDuration = 0;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_statistics, container, false);

    }

    public void onViewCreated(View view, Bundle savedInstanceState){
        statisticsFragmentViewModel = new ViewModelProvider(this)
                .get(StatisticsFragmentViewModel.class);
        initView(view);
        inicializarListenerGoal();
        inicializarFecha();

        for (int i = 1; i < 8; i++) {
            actualizarMinutosDia(i);
        }

        chart();
        statisticsFragmentViewModel.loadGoal();

        statisticsFragmentViewModel.getGoalLiveData().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer goal) {
                // Actualizar la vista correspondiente (por ejemplo, el TextView "goal")
                txtGoal.setText("Goal: " + goal.toString()+ " min");
                minGoal = goal;
                updateProgress();

            }
        });


    }

    private void initView(View view) {
        //associate the views with the xml
        barChart = view.findViewById(R.id.idBarChart);
        progressBar = view.findViewById(R.id.progressBar);
        progressText = view.findViewById(R.id.txtProgress);
        editGoal = view.findViewById(R.id.goalEdit);
        txtGoal = view.findViewById(R.id.txtGoal);
        date = view.findViewById(R.id.txtDate);

    }

    private void inicializarListenerGoal(){
        editGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {// create a new AlertDialog builder
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Cambiar goal");

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
                        minGoal = Integer.parseInt(message);
                        statisticsFragmentViewModel.saveGoalValue(minGoal);
                        txtGoal.setText("Goal: "+ message+" min");

                        updateProgress();


                    }
                });
                builder.setNegativeButton("Cancel", null);

                // Show the confirmation dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    private void updateProgress(){
        if(actualDuration>=minGoal || minGoal == 0){
            progress = 100;
            progressBar.setProgress(progress);
            progressText.setText(progress+"%");
        }else{
            progress = (actualDuration*100)/minGoal;
            progressBar.setProgress(progress);
            progressText.setText(progress+"%");
        }
    }


    private void inicializarFecha(){
        Date fechaActual = new Date();

        // Definir el formato deseado
        SimpleDateFormat formato = new SimpleDateFormat("E MMMM d yyyy");

        // Formatear la fecha actual utilizando el formato deseado
        String fechaFormateada = formato.format(fechaActual);

        date.setText(fechaFormateada);
    }

    private void actualizarMinutosDia(int dia) {
        final Observer<Integer> durationObserver = new Observer<Integer>() {
            @Override
            public void onChanged(Integer duration) {
                switch (dia) {
                    case 1:
                        sun = duration;
                        break;
                    case 2:
                        mon = duration;
                        break;
                    case 3:
                        tue = duration;
                        break;
                    case 4:
                        wed = duration;
                        break;
                    case 5:
                        thu = duration;
                        break;
                    case 6:
                        fri = duration;
                        break;
                    case 7:
                        sat = duration;
                        break;
                }

                chart();
                actualDuration = statisticsFragmentViewModel.actualDuration();
                updateProgress();

            }

        };

        switch (dia) {
            case 1:
                statisticsFragmentViewModel.getDuracionDomingo().observe(getViewLifecycleOwner(), durationObserver);
                break;
            case 2:
                statisticsFragmentViewModel.getDuracionLunes().observe(getViewLifecycleOwner(), durationObserver);
                break;
            case 3:
                statisticsFragmentViewModel.getDuracionMartes().observe(getViewLifecycleOwner(), durationObserver);
                break;
            case 4:
                statisticsFragmentViewModel.getDuracionMiercoles().observe(getViewLifecycleOwner(), durationObserver);
                break;
            case 5:
                statisticsFragmentViewModel.getDuracionJueves().observe(getViewLifecycleOwner(), durationObserver);
                break;
            case 6:
                statisticsFragmentViewModel.getDuracionViernes().observe(getViewLifecycleOwner(), durationObserver);
                break;
            case 7:
                statisticsFragmentViewModel.getDuracionSabado().observe(getViewLifecycleOwner(), durationObserver);
                break;
        }

        statisticsFragmentViewModel.cargarDiaMinutos(dia);
        chart();
    }

    private void chart(){
        // calling method to get bar entries.
        getBarEntries();

        // creating a new bar data set.
        barDataSet = new BarDataSet(barEntriesArrayList, "");

        // creating a new bar data and
        // passing our bar data set.
        barData = new BarData(barDataSet);

        // below line is to set data
        // to our bar chart.
        barChart.setData(barData);

        // adding color to our bar data set.
        barDataSet.setColors(Color.argb(255, 234, 127,13));

        // setting text color.
        barDataSet.setValueTextColor(Color.BLACK);

        // setting text size
        barDataSet.setValueTextSize(10f);
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);


        final ArrayList<String> xAxisLabel = new ArrayList<>();

        xAxisLabel.add("Sun");
        xAxisLabel.add("Mon");
        xAxisLabel.add("Tue");
        xAxisLabel.add("Wed");
        xAxisLabel.add("Thu");
        xAxisLabel.add("Fri");
        xAxisLabel.add("Sat");


        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return xAxisLabel.get((int) value % xAxisLabel.size());
            }
        });

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setEnabled(false); // deshabilita el eje y en el lado derecho

    }


    private void getBarEntries() {
        // creating a new array list
        barEntriesArrayList = new ArrayList<>();

        // adding new entry to our array list with bar
        // entry and passing x and y axis value to it.
        barEntriesArrayList.add(new BarEntry(1f, mon));
        barEntriesArrayList.add(new BarEntry(2f, tue));
        barEntriesArrayList.add(new BarEntry(3f, wed));
        barEntriesArrayList.add(new BarEntry(4f, thu));
        barEntriesArrayList.add(new BarEntry(5f, fri));
        barEntriesArrayList.add(new BarEntry(6f, sat));
        barEntriesArrayList.add(new BarEntry(7f, sun));
    }

}