package com.example.eventzen;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

// Cet adaptateur est une version simplifiée de PublicEventAdapter
public class OrganizerEventAdapter extends RecyclerView.Adapter<OrganizerEventAdapter.OrganizerEventViewHolder> {

    private final Context context;
    private Cursor cursor;

    public OrganizerEventAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    // --- ViewHolder Class ---
    public static class OrganizerEventViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle, tvDates, tvLocation;

        public OrganizerEventViewHolder(@NonNull View itemView) {
            super(itemView);
            // Assurez-vous que ces IDs existent dans votre item_public_event.xml
            tvTitle = itemView.findViewById(R.id.tv_event_item_title);
            // tvDates et tvLocation peuvent être réutilisés du layout item_public_event
            tvDates = itemView.findViewById(R.id.tv_event_item_dates);
            tvLocation = itemView.findViewById(R.id.tv_event_item_location);
        }
    }

    @NonNull
    @Override
    public OrganizerEventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Réutilisation du layout public ou création d'un nouveau : item_public_event
        View view = LayoutInflater.from(context).inflate(R.layout.item_public_event, parent, false);
        return new OrganizerEventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrganizerEventViewHolder holder, int position) {
        if (!cursor.moveToPosition(position)) {
            return;
        }

        String title = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.E_TITLE));
        String location = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.E_LOCATION));
        String startDate = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.E_START_DATE));
        final int eventId = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.E_ID));

        holder.tvTitle.setText(title);
        holder.tvLocation.setText("Lieu: " + location);
        holder.tvDates.setText("Débute le: " + startDate);

        // Clic pour MODIFIER / GÉRER les billets et inscriptions
        holder.itemView.setOnClickListener(v -> {
            // TODO: Lancer OrganizerEventDetailActivity (à créer)
            Toast.makeText(context, "Gérer l'événement ID: " + eventId, Toast.LENGTH_SHORT).show();
             Intent intent = new Intent(context, OrganizerEventDetailActivity.class);
             intent.putExtra(OrganizerEventDetailActivity.EXTRA_EVENT_ID, eventId);
             context.startActivity(intent);
        });
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