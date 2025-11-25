package com.example.eventzen;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class EventRegistrationsActivity extends BaseActivity {

    public static final String EXTRA_EVENT_ID = "extra_event_id";
    public static final String EXTRA_EVENT_TITLE = "extra_event_title"; // Optionnel, pour le titre

    private DBHelper dbHelper;
    private EventRegistrationsAdapter adapter;
    private RecyclerView recyclerView;
    private TextView tvEmptyState, tvHeader;
    private int eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_registrations);

        dbHelper = new DBHelper(this);

        // 1. Récupération des extras
        eventId = getIntent().getIntExtra(EXTRA_EVENT_ID, -1);
        String eventTitle = getIntent().getStringExtra(EXTRA_EVENT_TITLE);

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
            getSupportActionBar().setTitle("Inscriptions");
        }

        // 3. Initialisation des vues
        tvEmptyState = findViewById(R.id.tv_empty_registrations_list);
        tvHeader = findViewById(R.id.tv_registration_header);
        recyclerView = findViewById(R.id.recycler_view_event_registrations);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Afficher le titre de l'événement dans le header
        if (eventTitle != null) {
            tvHeader.setText("Liste des inscriptions pour : " + eventTitle);
        } else {
            // Optionnel : charger le titre si EXTRA_EVENT_TITLE n'est pas envoyé
            tvHeader.setText("Liste des inscriptions (ID: " + eventId + ")");
        }

        loadRegistrations();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void loadRegistrations() {
        Cursor cursor = dbHelper.getRegistrationsByEventId(eventId);

        if (cursor.getCount() == 0) {
            tvEmptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            if (adapter == null) {
                adapter = new EventRegistrationsAdapter(this, cursor);
                recyclerView.setAdapter(adapter);
            } else {
                adapter.swapCursor(cursor);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapter != null) {
            adapter.swapCursor(null);
        }
    }
}