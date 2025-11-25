package com.example.eventzen;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TicketSelectionActivity extends BaseActivity {

    public static final String EXTRA_EVENT_ID = "extra_event_id";

    private DBHelper dbHelper;
    private TicketAdapter adapter;
    private int eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_selection);

        dbHelper = new DBHelper(this);

        // 1. Configuration de la Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Sélection des Billets");
        }

        // 2. Récupérer l'ID de l'événement
        eventId = getIntent().getIntExtra(EXTRA_EVENT_ID, -1);
        if (eventId == -1) {
            Toast.makeText(this, "Erreur: ID d'événement manquant.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // 3. Configuration de la RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recycler_view_tickets);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 4. Charger les données initiales
        Cursor cursor = dbHelper.getTicketsByEventId(eventId);
        adapter = new TicketAdapter(this, cursor, eventId);
        recyclerView.setAdapter(adapter);
    }

    // Gère le bouton de retour de la Toolbar
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    /**
     * Appelé par l'adaptateur après une inscription réussie pour rafraîchir la liste.
     */
    public void refreshData() {
        Cursor newCursor = dbHelper.getTicketsByEventId(eventId);
        adapter.swapCursor(newCursor);
    }
}