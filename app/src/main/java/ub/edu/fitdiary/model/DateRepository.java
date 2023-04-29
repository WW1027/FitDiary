package ub.edu.fitdiary.model;

import java.util.ArrayList;

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
    public void loadDates(ArrayList<Date> dates) {
        dates.clear();
        //TODO load

        //provisional para provar
        for (int i = 1; i < 31; i++) {
            String a = String.valueOf(i);
            Date date = new Date(a, "Mon");
            dates.add(date);
        }

    }

}
