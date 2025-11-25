package com.example.eventzen;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {

    private final Context context;
    private Cursor cursor;
    private final DBHelper dbHelper;
    private final UserSessionManager sessionManager;
    private final int eventId;

    public TicketAdapter(Context context, Cursor cursor, int eventId) {
        this.context = context;
        this.cursor = cursor;
        this.eventId = eventId;
        this.dbHelper = new DBHelper(context);
        this.sessionManager = new UserSessionManager(context);
    }

    public static class TicketViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTicketType, tvTicketPrice, tvTicketQuantity;
        public Button btnRegisterTicket;

        public TicketViewHolder(@NonNull View itemView) {
            super(itemView);
            // Assurez-vous que ces IDs existent dans item_ticket.xml
            tvTicketType = itemView.findViewById(R.id.tv_ticket_type);
            tvTicketPrice = itemView.findViewById(R.id.tv_ticket_price);
            tvTicketQuantity = itemView.findViewById(R.id.tv_ticket_quantity);
            btnRegisterTicket = itemView.findViewById(R.id.btn_select_ticket);
        }
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Le layout de l'élément de liste de billet
        View view = LayoutInflater.from(context).inflate(R.layout.item_ticket, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        if (!cursor.moveToPosition(position)) {
            return;
        }

        int ticketId = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.T_ID));
        String type = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.T_TYPE));
        double price = cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.T_PRICE));
        int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.T_QUANTITY));

        holder.tvTicketType.setText(type);
        holder.tvTicketPrice.setText(String.format("Prix: %.2f €", price));
        holder.tvTicketQuantity.setText("Restant: " + quantity);
        holder.btnRegisterTicket.setText("S'inscrire");

        if (quantity <= 0) {
            holder.btnRegisterTicket.setEnabled(false);
            holder.btnRegisterTicket.setText("Épuisé");
        } else {
            holder.btnRegisterTicket.setEnabled(true);
        }

        // Gérer le clic d'inscription
        holder.btnRegisterTicket.setOnClickListener(v -> {
            int userId = sessionManager.getCurrentUserId();

            if (userId != -1) {
                boolean success = dbHelper.registerForEvent(userId, eventId, ticketId);
                if (success) {
                    Toast.makeText(context, "Inscription confirmée pour " + type + "!", Toast.LENGTH_LONG).show();
                    // Rafraîchir l'adapter pour mettre à jour la quantité
                    ((TicketSelectionActivity) context).refreshData();
                } else {
                    Toast.makeText(context, "Erreur d'inscription. Veuillez réessayer.", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(context, "Erreur de session utilisateur.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cursor != null ? cursor.getCount() : 0;
    }

    // Méthode de rafraîchissement
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