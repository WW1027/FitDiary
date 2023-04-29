package ub.edu.fitdiary.view;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

import ub.edu.fitdiary.R;
import ub.edu.fitdiary.model.Date;

public class DateCardAdapter extends RecyclerView.Adapter<DateCardAdapter.ViewHolder> {

    private OnClickSelectListener mOnClickSelectListener;
    private ArrayList<Date> mDates; // Referència a la llista de Dates
    private int selectedPosition = 0;

    /** Definició de listener (interficie)
     *  per a quan algú vulgui escoltar un event de OnClickSelect, és a dir,
     *  quan l'usuari faci clic en algún dels items de la RecyclerView
     */
    public interface OnClickSelectListener {
        void OnClickSelect(int position);
    }

    //Constructor
    public DateCardAdapter(ArrayList<Date> mDates) {
        this.mDates = mDates;
    }

    public void setOnClickSelectListener(OnClickSelectListener listener) {
        this.mOnClickSelectListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        // Inflate crea una view genèrica definida pel layout que l'hi passem (l'date_card_layout)
        View view = inflater.inflate(R.layout.date_card_layout, parent, false);

        // La classe ViewHolder farà de pont entre la classe Date del model i la view (DateCard).
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // El ViewHolder té el mètode que s'encarrega de llegir els atributs del User (1r parametre),
        // i assignar-los a les variables del ViewHolder.
        // Qualsevol listener que volguem posar a un item, ha d'entrar com a paràmetre extra (2n).
        holder.bind(mDates.get(position), this.mOnClickSelectListener);
    }

    /**
     * Retorna el número d'elements a la llista.
     * @return
     */
    @Override
    public int getItemCount() {
        return mDates.size();
    }

    /**
     * Mètode que seteja de nou la llista d'usuaris si s'hi han fet canvis de manera externa.
     * @param dates
     */
    public void setUsers(ArrayList<Date> dates) {
        this.mDates = dates; // no recicla/repinta res
    }

    /**
     * Mètode que repinta la RecyclerView sencera.
     */
    public void updateDates() {
        notifyDataSetChanged();
    }


    /**
     * Classe ViewHolder. No és més que un placeholder de la vista (user_card_list.xml)
     * dels items de la RecyclerView. Podem implementar-ho fora de RecyclerViewAdapter,
     * però es pot fer dins.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView mCard;
        private final TextView mCardNumDate;
        private final TextView mCardDayDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mCard = itemView.findViewById(R.id.card);
            mCardNumDate = itemView.findViewById(R.id.numDayCard);
            mCardDayDate = itemView.findViewById(R.id.weekDayCard);
        }

        public void bind(final Date date, OnClickSelectListener listener) {
            mCardNumDate.setText(date.getNumDate());
            mCardDayDate.setText(date.getDayDate());

            //Seteja el listener Select al clicar el card
            mCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.OnClickSelect(getAdapterPosition());

                    //TODO: Que solo cambie de color cuando esté seleccionado, es decir solo
                    // un dia a la vez.
                    mCard.setCardBackgroundColor(ContextCompat.getColor(mCard.getContext(), R.color.orange));
                    mCardNumDate.setTextColor(Color.WHITE);
                    mCardDayDate.setTextColor(Color.WHITE);

                }
            });
        }

    }


}
