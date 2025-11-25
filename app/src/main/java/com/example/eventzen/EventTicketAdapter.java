package com.example.eventzen;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Locale;

public class EventTicketAdapter extends RecyclerView.Adapter<EventTicketAdapter.TicketViewHolder> {

    private final Context context;
    private Cursor cursor;
    private final TicketActionListener listener;

    // Interface pour gérer les actions de l'activité
    public interface TicketActionListener {
        void onEditTicket(int ticketId);
        void onDeleteTicket(int ticketId);
    }

    public EventTicketAdapter(Context context, Cursor cursor, TicketActionListener listener) {
        this.context = context;
        this.cursor = cursor;
        this.listener = listener;
    }

    public static class TicketViewHolder extends RecyclerView.ViewHolder {
        public TextView tvType, tvDescription, tvPrice, tvQuantity;
        public Button btnEdit, btnDelete;

        public TicketViewHolder(@NonNull View itemView) {
            super(itemView);
            tvType = itemView.findViewById(R.id.tv_ticket_type);
            tvDescription = itemView.findViewById(R.id.tv_ticket_description);
            tvPrice = itemView.findViewById(R.id.tv_ticket_price);
            tvQuantity = itemView.findViewById(R.id.tv_ticket_quantity);
            btnEdit = itemView.findViewById(R.id.btn_edit_ticket);
            btnDelete = itemView.findViewById(R.id.btn_delete_ticket);
        }
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_event_ticket, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        if (!cursor.moveToPosition(position)) {
            return;
        }

        try {
            // Récupération des données (basée sur la structure standard de la TABLE_TICKETS)
            final int ticketId = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.T_ID));
            String type = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.T_TYPE));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.T_DESCRIPTION));
            double price = cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.T_PRICE));
            int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.T_QUANTITY));

            // NOTE : Vous n'avez pas encore de colonne pour la quantité vendue, on affiche la quantité totale

            // Affichage
            holder.tvType.setText(type);
            holder.tvDescription.setText(description);
            holder.tvPrice.setText(String.format(Locale.FRANCE, "%.2f €", price)); // Formatage du prix
            holder.tvQuantity.setText("Quantité totale : " + quantity);

            // Gestion des clics
            holder.btnEdit.setOnClickListener(v -> listener.onEditTicket(ticketId));
            holder.btnDelete.setOnClickListener(v -> listener.onDeleteTicket(ticketId));

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return cursor != null ? cursor.getCount() : 0;
    }

    public void swapCursor(Cursor newCursor) {
        if (cursor != null) {
            cursor.close();
        }
        cursor = newCursor;
        if (newCursor != null) {
            notifyDataSetChanged();
        }
    }
}