package ub.edu.fitdiary.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

import ub.edu.fitdiary.R;
import ub.edu.fitdiary.model.Reminder;

public class ReminderCardAdapter extends RecyclerView.Adapter<ReminderCardAdapter.ViewHolder>{
    public interface OnClickSelectListener {
        void OnClickSelect(int position);
    }


    /** Definición de listener (interfaz)
     * para cuando alguien quiera escuchar un evento de OnClickHide, es decir,
     * cuando el usuario haga clic en la cruz (esconder) alguno de los items de la RecyclerView
     */
    public interface OnClickHideListener {
        void OnClickHide(int position);
    }

    // Instancias que necesitamos para poder gestionar los eventos card views
    private ArrayList<Reminder> mReminders;
    private ReminderCardAdapter.OnClickHideListener mOnClickHideListener;
    private ReminderCardAdapter.OnClickSelectListener mOnClickSelectListener;
    private Context context;
    // Constructor
    public ReminderCardAdapter(ArrayList<Reminder> remindersList, Context context) {
        this.mReminders = remindersList; // no hace new (La lista la mantiene el ViewModel)
        this.context = context;
    }

    public void setOnClickSelectListener(ReminderCardAdapter.OnClickSelectListener listener) {
        this.mOnClickSelectListener = listener;
    }

    // Sirve para poder esconder un evento al hacer click en la cruz desde calendar fragment
    public void setOnClickHideListener(ReminderCardAdapter.OnClickHideListener listener) {
        this.mOnClickHideListener = listener;
    }

    public void hideReminder(int position) {
        notifyItemRemoved(position);
    }

    public Reminder getReminder(int position){ return mReminders.get(position); }
    public void setReminders(ArrayList<Reminder> reminders) {
        this.mReminders = reminders;
        notifyDataSetChanged(); //repinta
    }

    @NonNull
    @Override
    public ReminderCardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        // Inflate crea una view genérica definida por el layout que le pasamos (l'user_card_layout)
        View view = inflater.inflate(R.layout.reminder_card_layout, parent, false);

        // La clase ViewHolder hará de puente entre la clase User del modelo y la view (UserCard).
        return new ReminderCardAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderCardAdapter.ViewHolder holder, int position) {
        holder.bind(mReminders.get(position), this.mOnClickHideListener, this.mOnClickSelectListener);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    /* Classe interna que representa cada una de les vistes de la llista */
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Atributs de la classe ViewHolder que representen els atributs de la vista
        private final TextView mDate;
        private final TextView mSport;
        private final TextView mDuration;
        private final TextView mTimeBefore;
        private final MaterialCardView mCard;
        private final ImageView mDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mCard = itemView.findViewById(R.id.card3);
            mDate = itemView.findViewById(R.id.newReminderDate);
            mSport = itemView.findViewById(R.id.newReminderSport);
            mDuration = itemView.findViewById(R.id.newReminderDuration);
            mTimeBefore = itemView.findViewById(R.id.newReminderTimeBefore);
            mDelete = itemView.findViewById(R.id.newReminderDelete);
        }

        public void bind(final Reminder reminder, ReminderCardAdapter.OnClickHideListener listener, ReminderCardAdapter.OnClickSelectListener listener2) {
            mDate.setText(reminder.getDate());
            mSport.setText(reminder.getSport());
            mDuration.setText(reminder.getDuration());
            mTimeBefore.setText(reminder.getTimeBefore());

            mDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Mostrar ventana para ver si de verdad quiere cancelar la acción
                    // Build the confirmation dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Confirm Delete Reminder");
                    builder.setMessage("Are you sure you want to delete this reminder?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Eliminar el recordatorio
                            listener.OnClickHide(getAdapterPosition());
                        }
                    });
                    builder.setNegativeButton("No", null);

                    // Show the confirmation dialog
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });

            // Setea el listener onClick del botón de esconder (hide), que a la vez
            // llame el método OnClickHide que implementan nuestros propios
            // listeners de tipo OnClickHideListener.
            mCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener2.OnClickSelect(getAdapterPosition());
                    //TODO Implementar cuando se le clica a un CardView de recodatorio
                }
            });

        }
    }
}
