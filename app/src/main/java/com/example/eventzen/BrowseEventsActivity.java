package com.example.eventzen;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar; // Import pour la Toolbar
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

// IMPORTANT : Hérite maintenant de BaseActivity
public class BrowseEventsActivity extends BaseActivity {

    private DBHelper dbHelper;
    // sessionManager est hérité de BaseActivity
    private PublicEventAdapter publicEventAdapter;
    private RecyclerView recyclerViewPublicEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_events);

        dbHelper = new DBHelper(this);

        // Initialisation du FAB
        FloatingActionButton fabAddEvent = findViewById(R.id.fab_add_event);

        // Logique d'affichage/masquage basée sur le rôle
        String userRole = sessionManager.getCurrentUserRole();
        if ("organizer".equals(userRole)) {
            fabAddEvent.setVisibility(View.VISIBLE);
        } else {
            fabAddEvent.setVisibility(View.GONE);
        }

        // Gestion du clic pour lancer l'activité de création
        fabAddEvent.setOnClickListener(v -> {
            startActivity(new Intent(BrowseEventsActivity.this, CreateEventActivity.class));
        });

        // 1. Initialisation et configuration de la Toolbar héritée
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Définir le titre (optionnel)
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Explorer les Événements");
        }

        // 2. Initialisation du RecyclerView
        recyclerViewPublicEvents = findViewById(R.id.recycler_view_public_events);
        recyclerViewPublicEvents.setLayoutManager(new LinearLayoutManager(this));

        // 3. Charger les données
        loadPublicEvents();
    }

    // Les méthodes onCreateOptionsMenu et onOptionsItemSelected sont gérées par BaseActivity.

    @Override
    protected void onResume() {
        super.onResume();
        loadPublicEvents();
    }

    private void loadPublicEvents() {
        Cursor cursor = dbHelper.getAllPublicEvents();

        if (publicEventAdapter == null) {
            publicEventAdapter = new PublicEventAdapter(this, cursor);
            recyclerViewPublicEvents.setAdapter(publicEventAdapter);
        } else {
            publicEventAdapter.swapCursor(cursor);
        }

        if (cursor.getCount() == 0) {
            Toast.makeText(this, "Aucun événement public n'est actuellement disponible.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (publicEventAdapter != null) {
            publicEventAdapter.swapCursor(null);
        }
    }
}