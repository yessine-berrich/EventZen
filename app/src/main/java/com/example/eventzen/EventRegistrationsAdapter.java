package com.example.eventzen;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class EventRegistrationsAdapter extends RecyclerView.Adapter<EventRegistrationsAdapter.RegistrationViewHolder> {

    private final Context context;
    private Cursor cursor;

    public EventRegistrationsAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    public static class RegistrationViewHolder extends RecyclerView.ViewHolder {
        public TextView tvParticipantName, tvTicketAndDate, tvRegistrationStatus;

        public RegistrationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvParticipantName = itemView.findViewById(R.id.tv_participant_name);
            tvTicketAndDate = itemView.findViewById(R.id.tv_ticket_and_date);
            tvRegistrationStatus = itemView.findViewById(R.id.tv_registration_status);
        }
    }

    @NonNull
    @Override
    public RegistrationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_event_registration, parent, false);
        return new RegistrationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RegistrationViewHolder holder, int position) {
        if (!cursor.moveToPosition(position)) {
            return;
        }

        try {
            // Récupération des données basées sur la requête dans DBHelper.getRegistrationsByEventId()
            String nom = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_NOM));
            String prenom = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PRENOM));
            String ticketType = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.T_TYPE));
            String registeredAt = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.R_REGISTERED_AT));
            String status = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.R_STATUS));

            // Affichage
            holder.tvParticipantName.setText(prenom + " " + nom);
            holder.tvTicketAndDate.setText(String.format("Billet: %s | Inscrit le: %s", ticketType, registeredAt));
            holder.tvRegistrationStatus.setText("Statut: " + status);

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