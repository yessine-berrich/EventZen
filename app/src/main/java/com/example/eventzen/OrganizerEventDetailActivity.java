package com.example.eventzen;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

public class OrganizerEventDetailActivity extends BaseActivity {

    public static final String EXTRA_EVENT_ID = "extra_event_id";

    private DBHelper dbHelper;
    private int eventId;

    private TextView tvTitle, tvDateTime, tvLocation, tvDescription;
    private Button btnEdit, btnManageTickets, btnViewRegistrations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_event_detail);

        dbHelper = new DBHelper(this);

        // 1. Récupération de l'ID
        eventId = getIntent().getIntExtra(EXTRA_EVENT_ID, -1);
        if (eventId == -1) {
            Toast.makeText(this, "Erreur: ID d'événement manquant.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // 2. Configuration de la Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Gestion Événement #" + eventId);
        }

        // 3. Initialisation des vues
        tvTitle = findViewById(R.id.tv_detail_title);
        tvDateTime = findViewById(R.id.tv_detail_datetime);
        tvLocation = findViewById(R.id.tv_detail_location);
        tvDescription = findViewById(R.id.tv_detail_description);
        btnEdit = findViewById(R.id.btn_edit_event);
        btnManageTickets = findViewById(R.id.btn_manage_tickets);
        btnViewRegistrations = findViewById(R.id.btn_view_registrations);

        // 4. Gestion des clics
        btnEdit.setOnClickListener(v -> handleEditEvent());
        btnManageTickets.setOnClickListener(v -> handleManageTickets());
        btnViewRegistrations.setOnClickListener(v -> handleViewRegistrations());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadEventDetails(eventId); // Rafraîchir les détails en cas de modification
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void loadEventDetails(int id) {
        // NOTE: On réutilise la méthode du DBHelper qui récupère un seul événement
        // Vous pouvez réutiliser getPublicEventDetailById si elle retourne toutes les colonnes nécessaires
        Cursor cursor = dbHelper.getPublicEventDetailById(id);

        if (cursor != null && cursor.moveToFirst()) {
            try {
                // Récupération des données
                String title = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.E_TITLE));
                String location = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.E_LOCATION));
                String startDate = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.E_START_DATE));
                String endDate = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.E_END_DATE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.E_DESCRIPTION));

                // Affichage des données
                tvTitle.setText(title);
                tvDateTime.setText("Du " + startDate + " au " + endDate);
                tvLocation.setText("Lieu : " + location);
                tvDescription.setText(description);

                getSupportActionBar().setTitle("Gérer : " + title);

            } catch (Exception e) {
                Toast.makeText(this, "Erreur de lecture des données: " + e.getMessage(), Toast.LENGTH_LONG).show();
            } finally {
                cursor.close();
            }
        } else {
            Toast.makeText(this, "Événement non trouvé ou supprimé.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void handleEditEvent() {
        Intent intent = new Intent(this, EditEventActivity.class);
        // Passer l'ID de l'événement en cours de gestion
        intent.putExtra(EditEventActivity.EXTRA_EVENT_ID, eventId);
        startActivity(intent);
    }

    private void handleManageTickets() {
        String title = tvTitle.getText().toString(); // Récupérer le titre affiché

        Intent intent = new Intent(this, ManageTicketsActivity.class);
        // Passer l'ID de l'événement et son titre (pour le header)
        intent.putExtra(ManageTicketsActivity.EXTRA_EVENT_ID, eventId);
        intent.putExtra("EXTRA_EVENT_TITLE_FOR_TICKET_MGMT", title);
        startActivity(intent);
    }

    private void handleViewRegistrations() {
        // Récupérer le titre affiché pour le passer dans l'Intent (pour le header)
        String title = tvTitle.getText().toString();

        Intent intent = new Intent(this, EventRegistrationsActivity.class);
        intent.putExtra(EventRegistrationsActivity.EXTRA_EVENT_ID, eventId);
        intent.putExtra(EventRegistrationsActivity.EXTRA_EVENT_TITLE, title);
        startActivity(intent);
    }
}