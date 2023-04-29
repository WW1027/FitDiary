package ub.edu.fitdiary.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ub.edu.fitdiary.R;
import ub.edu.fitdiary.model.Event;

public class EventCardAdapter extends RecyclerView.Adapter<EventCardAdapter.ViewHolder>{
    public void hideEvent(int position) {
        notifyItemRemoved(position);
    }

    /** Definición de listener (interfaz)
     * para cuando alguien quiera escuchar un evento de OnClickHide, es decir,
     * cuando el usuario haga clic en la cruz (esconder) alguno de los items de la RecyclerView
     */
    public interface OnClickHideListener {
        void OnClickHide(int position);
    }
    public interface OnClickPictureListener {
        void OnClickImageToast(String username);
    }
    // Instancias que necesitamos para poder gestionar los eventos card views
    private ArrayList<Event> mEvents;
    private OnClickHideListener mOnClickHideListener;
    private OnClickPictureListener mOnClickPictureListener;

    // Constructor
    public EventCardAdapter(ArrayList<Event> eventList) {
        this.mEvents = eventList; // no hace new (La lista la mantiene el ViewModel)
    }

    // Sirve para poder esconder un evento al hacer click en la cruz desde calendar fragment
    public void setOnClickHideListener(OnClickHideListener listener) {
        this.mOnClickHideListener = listener;
    }

    // Sirve para poder ver la información de un usuario al hacer click en la imagen desde calendar fragment
    public void setOnClickImageToastListener(OnClickPictureListener listener) {
        this.mOnClickPictureListener = listener;
    }

    @NonNull
    public EventCardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        // Inflate crea una view genérica definida por el layout que le pasamos (l'user_card_layout)
        View view = inflater.inflate(R.layout.event_card_layout, parent, false);

        // La clase ViewHolder hará de puente entre la clase User del modelo y la view (UserCard).
        return new EventCardAdapter.ViewHolder(view);
    }

    public void setmEvents(ArrayList<Event> mEvents) {
        this.mEvents = mEvents;
        notifyDataSetChanged(); //repinta
    }


    @Override
    public int getItemCount() { return mEvents.size(); }

    /* Método llamado para cada ViewHolder de la RecyclerView */
    @Override
    public void onBindViewHolder(@NonNull EventCardAdapter.ViewHolder holder, int position) {
        // El ViewHolder té el mètode que s'encarrega de llegir els atributs del User (1r parametre),
        // i assignar-los a les variables del ViewHolder.
        // Qualsevol listener que volguem posar a un item, ha d'entrar com a paràmetre extra (2n).
        holder.bind(mEvents.get(position), this.mOnClickHideListener, this.mOnClickPictureListener);
    }


    /* Classe interna que representa cada una de les vistes de la llista */
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Atributs de la classe ViewHolder que representen els atributs de la vista
        private final ImageView mCardPictureUrl;
        private final TextView mCardComment;
        private final ImageView mHideButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mCardPictureUrl = itemView.findViewById(R.id.avatar);
            mCardComment = itemView.findViewById(R.id.comment);
            mHideButton = itemView.findViewById(R.id.hideButton);
        }

        public void bind(final Event event, OnClickHideListener listener, OnClickPictureListener listener2) {
            mCardComment.setText(event.getComment());
            // Carga la imagen de la URL en el ImageView
            Picasso.get().load(event.getImageURL()).into(mCardPictureUrl);
            // Setea el listener onClick del botón de esconder (hide), que a la vez
            // llame el método OnClickHide que implementan nuestros propios
            // listeners de tipo OnClickHideListener.
            mHideButton.setOnClickListener(new View.OnClickListener() {
                @Override //el getAdapterPosition devuelve la posición de la caja user, 0 1 2 etc
                public void onClick(View view) {listener.OnClickHide(getAdapterPosition());}
            });
            /*** Nos interesa cuando se clicka nos muestre la info detallada ***/
            mCardPictureUrl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {listener2.OnClickImageToast(event.toString());}
            });
        }
    }
}
