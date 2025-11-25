package com.example.eventzen; // Assurez-vous que le package est correct

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

// Assurez-vous d'avoir le DBHelper pour les constantes des colonnes
import com.example.eventzen.DBHelper;

// L'adaptateur doit hériter de RecyclerView.Adapter<ViewHolder>
public class PublicEventAdapter extends RecyclerView.Adapter<PublicEventAdapter.EventViewHolder> {

    private Context context;
    private Cursor cursor;

    // **********************************************
    // * LA CLÉ DE LA CORRECTION : LE CONSTRUCTEUR *
    // **********************************************
    public PublicEventAdapter(Context context, Cursor cursor) {
        this.context = context;
        // Le curseur sera null lors de la première initialisation si aucune donnée n'est trouvée
        this.cursor = cursor;
    }

    // --- ViewHolder Class ---
    public static class EventViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle, tvOrganizer, tvDates, tvLocation;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            // Remplacez les IDs par les IDs réels de votre item_public_event.xml
            tvTitle = itemView.findViewById(R.id.tv_event_item_title);
            tvOrganizer = itemView.findViewById(R.id.tv_event_item_organizer);
            tvDates = itemView.findViewById(R.id.tv_event_item_dates);
            tvLocation = itemView.findViewById(R.id.tv_event_item_location);
        }
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Remplacez l'ID par votre layout d'élément de liste
        View view = LayoutInflater.from(context).inflate(R.layout.item_public_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        if (cursor == null || cursor.getCount() == 0) {
            return;
        }

        // Déplacer le curseur à la position demandée
        if (!cursor.moveToPosition(position)) {
            return;
        }

        // Récupération des données du curseur (utilisez try/catch pour la robustesse)
        try {
            String title = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.E_TITLE));
            String location = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.E_LOCATION));
            String startDate = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.E_START_DATE));

            // Les colonnes organizer_nom et organizer_prenom viennent de la jointure dans DBHelper
            String nom = cursor.getString(cursor.getColumnIndexOrThrow("organizer_nom"));
            String prenom = cursor.getString(cursor.getColumnIndexOrThrow("organizer_prenom"));

            String organizerName = prenom + " " + nom;

            // Affichage des données
            holder.tvTitle.setText(title);
            holder.tvLocation.setText(location);
            holder.tvDates.setText("Date: " + startDate);
            holder.tvOrganizer.setText("Organisé par: " + organizerName);

            // Ajoutez le gestionnaire de clic ici pour ouvrir EventPublicDetailActivity
            final int eventId = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.E_ID));
            holder.itemView.setOnClickListener(v -> {
                // Lancer EventPublicDetailActivity
                Intent intent = new Intent(context, EventPublicDetailActivity.class);
                intent.putExtra(EventPublicDetailActivity.EXTRA_EVENT_ID, eventId);
                context.startActivity(intent);
            });

        } catch (IllegalArgumentException e) {
            // Gérer les erreurs de colonne (si une constante DBHelper est fausse)
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return cursor != null ? cursor.getCount() : 0;
    }

    /**
     * Méthode pour échanger le curseur et mettre à jour les données (appellée dans onResume).
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