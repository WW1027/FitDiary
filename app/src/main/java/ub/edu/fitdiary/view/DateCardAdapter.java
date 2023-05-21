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
    private static int selectedItemIndex = -1;
    private static RecyclerView mDateCardsRV;
    private ArrayList<Date> mDates; // Referència a la llista de Dates

    /** Definició de listener (interficie)
     *  per a quan algú vulgui escoltar un event de OnClickSelect, és a dir,
     *  quan l'usuari faci clic en algún dels items de la RecyclerView
     */
    public interface OnClickSelectListener {
        void OnClickSelect(int position);
    }

    //Constructor
    public DateCardAdapter(RecyclerView mDateCardsRV, ArrayList<Date> mDates) {
        this.mDateCardsRV = mDateCardsRV;
        this.mDates = mDates;
    }

    public static void setSelectedItemIndex(int selectedItemIndex) {
        DateCardAdapter.selectedItemIndex = selectedItemIndex;
    }

    public void setOnClickSelectListener(OnClickSelectListener listener) {
        this.mOnClickSelectListener = listener;
    }

    public static void scroll(){
        mDateCardsRV.scrollToPosition(selectedItemIndex);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        // Inflate crea una view genèrica definida pel layout que l'hi passem (l'date_card_layout)
        View view = inflater.inflate(R.layout.date_card_layout, parent, false);

        // La classe ViewHolder farà de pont entre la classe Date del model i la view (DateCard).
        return new DateCardAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // El ViewHolder té el mètode que s'encarrega de llegir els atributs del date (1r parametre),
        // i assignar-los a les variables del ViewHolder.
        // Qualsevol listener que volguem posar a un item, ha d'entrar com a paràmetre extra (2n).
        Date date = mDates.get(position);
        holder.bind(date, mOnClickSelectListener);

        // Establecer el color de fondo del CardView según si está seleccionado o no
        if (position == selectedItemIndex) {
            holder.mCard.setCardBackgroundColor(ContextCompat.getColor(holder.mCard.getContext(), R.color.orange));
            holder.mCardNumDate.setTextColor(Color.WHITE);
            holder.mCardDayDate.setTextColor(Color.WHITE);
        } else {
            holder.mCard.setCardBackgroundColor(ContextCompat.getColor(holder.mCard.getContext(), R.color.light_orange));
            holder.mCardNumDate.setTextColor(ContextCompat.getColor(holder.mCard.getContext(), R.color.orange));
            holder.mCardDayDate.setTextColor(ContextCompat.getColor(holder.mCard.getContext(), R.color.orange));
        }

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
     * Mètode que seteja de nou la llista de dates si s'hi han fet canvis de manera externa.
     * @param dates
     */
    public void setDates(ArrayList<Date> dates) {
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
    public class ViewHolder extends RecyclerView.ViewHolder {
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

                    int previousSelectedItemIndex = DateCardAdapter.selectedItemIndex;
                    DateCardAdapter.selectedItemIndex = getAdapterPosition();

                    // Restablecer el color de fondo del CardView previamente seleccionado
                    if (previousSelectedItemIndex != -1) {
                        notifyItemChanged(previousSelectedItemIndex);
                    }
                    // Cambiar el color de fondo del CardView seleccionado actualmente
                    notifyItemChanged(DateCardAdapter.selectedItemIndex);


                }
            });
        }

    }


}
