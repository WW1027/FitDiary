package ub.edu.fitdiary.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class DateRepository {

    private static final String TAG = "Date_Repository";

    /** Autoinstància, pel patró singleton */
    private static final DateRepository mInstance = new DateRepository();

    public interface OnLoadDatesListener {
        void onLoadDates(ArrayList<Date> dates);
    }

    public ArrayList<OnLoadDatesListener> mOnLoadDatesListener = new ArrayList<>();

    public void addOnLoadDatesListener(OnLoadDatesListener listener) {
        mOnLoadDatesListener.add(listener);
    }

    /**
     * Retorna aqusta instancia singleton
     * @return
     */
    public static DateRepository getInstance() {
        return mInstance;
    }

    /**
     * Mètode que llegeix els usuaris. Vindrà cridat des de fora i quan acabi,
     * avisarà sempre als listeners, invocant el seu OnLoadUsers.
     */
    public void loadDates(ArrayList<Date> dates,int month,int year) {
        dates.clear();

        // Crear objeto GregorianCalendar para el mes y año seleccionados
        // Crear objeto GregorianCalendar para el mes y año seleccionados
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        // Obtener último día del mes
        int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Crear objetos Date para cada día del mes
        for (int i = 1; i <= lastDay; i++) {
            calendar.set(Calendar.DAY_OF_MONTH, i);
            String dayOfWeek = new SimpleDateFormat("EEE").format(calendar.getTime());
            Date date = new Date(String.valueOf(i), dayOfWeek, month, year);
            dates.add(date);
        }

    }

}
