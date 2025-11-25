package com.example.eventzen;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

public class EventPublicDetailActivity extends BaseActivity {

    public static final String EXTRA_EVENT_ID = "extra_event_id";

    private DBHelper dbHelper;
    private int eventId;

    private TextView tvTitle, tvOrganizer, tvDateTime, tvLocation, tvDescription;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_public_detail);

        dbHelper = new DBHelper(this);

        // 1. Configuration de la Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Activer le bouton de retour
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Détails de l'Événement");
        }

        // 2. Récupérer l'ID de l'événement
        Intent intent = getIntent();
        eventId = intent.getIntExtra(EXTRA_EVENT_ID, -1);

        if (eventId == -1) {
            Toast.makeText(this, "Erreur: ID d'événement manquant.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // 3. Initialiser les vues
        tvTitle = findViewById(R.id.tv_detail_title);
        tvOrganizer = findViewById(R.id.tv_detail_organizer);
        tvDateTime = findViewById(R.id.tv_detail_datetime);
        tvLocation = findViewById(R.id.tv_detail_location);
        tvDescription = findViewById(R.id.tv_detail_description);
        btnRegister = findViewById(R.id.btn_register_event);

        // 4. Charger les données
        loadEventDetails(eventId);

        // 5. Gérer le clic d'inscription
        btnRegister.setOnClickListener(v -> handleRegistration(eventId));
    }

    // Gère le bouton de retour de la Toolbar
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void loadEventDetails(int id) {
        Cursor cursor = dbHelper.getPublicEventDetailById(id);

        if (cursor != null && cursor.moveToFirst()) {
            try {
                // Récupération des données
                String title = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.E_TITLE));
                String location = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.E_LOCATION));
                String startDate = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.E_START_DATE));
                String endDate = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.E_END_DATE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.E_DESCRIPTION));

                String nom = cursor.getString(cursor.getColumnIndexOrThrow("organizer_nom"));
                String prenom = cursor.getString(cursor.getColumnIndexOrThrow("organizer_prenom"));

                String organizerName = prenom + " " + nom;

                // Affichage des données
                tvTitle.setText(title);
                tvOrganizer.setText("Organisé par : " + organizerName);
                tvDateTime.setText("Du " + startDate + " au " + endDate);
                tvLocation.setText(location);
                tvDescription.setText(description);

            } catch (Exception e) {
                Toast.makeText(this, "Erreur de lecture des données: " + e.getMessage(), Toast.LENGTH_LONG).show();
            } finally {
                cursor.close();
            }
        } else {
            Toast.makeText(this, "Événement non trouvé.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void handleRegistration(int eventId) {
        if (sessionManager.isLoggedIn()) {
            // Lancer la nouvelle activité pour choisir le billet
            Intent intent = new Intent(this, TicketSelectionActivity.class);
            intent.putExtra(TicketSelectionActivity.EXTRA_EVENT_ID, eventId);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Veuillez vous connecter pour vous inscrire.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}