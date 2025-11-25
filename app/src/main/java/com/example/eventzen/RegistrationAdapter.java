package com.example.eventzen;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RegistrationAdapter extends RecyclerView.Adapter<RegistrationAdapter.RegistrationViewHolder> {

    private final Context context;
    private Cursor cursor;

    // Le constructeur nécessaire pour UserRegistrationsActivity
    public RegistrationAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    // --- ViewHolder Class ---
    public static class RegistrationViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle, tvDate, tvTicketType, tvStatus;

        public RegistrationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_reg_event_title);
            tvDate = itemView.findViewById(R.id.tv_reg_event_date);
            tvTicketType = itemView.findViewById(R.id.tv_reg_ticket_type);
            tvStatus = itemView.findViewById(R.id.tv_reg_status);
        }
    }

    @NonNull
    @Override
    public RegistrationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_registration, parent, false);
        return new RegistrationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RegistrationViewHolder holder, int position) {
        if (!cursor.moveToPosition(position)) {
            return;
        }

        try {
            // Récupération des données basées sur la requête dans DBHelper.getUserRegistrations()
            String title = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.E_TITLE));
            String startDate = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.E_START_DATE));
            String ticketType = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.T_TYPE));
            double ticketPrice = cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.T_PRICE));
            String status = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.R_STATUS));

            // Affichage
            holder.tvTitle.setText(title);
            holder.tvDate.setText("Date: " + startDate);
            holder.tvTicketType.setText(String.format("Billet: %s (%.2f €)", ticketType, ticketPrice));
            holder.tvStatus.setText("Statut: " + status);

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            // Ceci indique une erreur de nom de colonne dans la requête SQL ou ici.
        }
    }

    @Override
    public int getItemCount() {
        return cursor != null ? cursor.getCount() : 0;
    }

    /**
     * Permet d'échanger le curseur pour mettre à jour la liste.
     */
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