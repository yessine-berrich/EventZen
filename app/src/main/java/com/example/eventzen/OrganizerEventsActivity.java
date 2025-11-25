package com.example.eventzen;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class OrganizerEventsActivity extends BaseActivity {

    private DBHelper dbHelper;
    private OrganizerEventAdapter adapter; // TODO: Créer cet adaptateur
    private RecyclerView recyclerView;
    private TextView tvEmptyState;
    private int organizerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_events);

        dbHelper = new DBHelper(this);

        // 1. Configuration de la Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Mes Événements Managés");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // 2. Vérification du rôle et de l'ID
        organizerId = sessionManager.getCurrentUserId();
        String role = sessionManager.getCurrentUserRole();

        if (organizerId == -1 || !"organizer".equals(role)) {
            Toast.makeText(this, "Accès non autorisé.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // 3. Initialisation des vues
        tvEmptyState = findViewById(R.id.tv_empty_organizer_events);
        recyclerView = findViewById(R.id.recycler_view_organizer_events);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fabAddEvent = findViewById(R.id.fab_add_event);
        fabAddEvent.setOnClickListener(v -> {
            startActivity(new Intent(OrganizerEventsActivity.this, CreateEventActivity.class));
        });

        loadEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadEvents(); // Rafraîchir la liste après un retour (ex: après création d'un événement)
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void loadEvents() {
        Cursor cursor = dbHelper.getOrganizerEvents(organizerId);

        if (cursor.getCount() == 0) {
            tvEmptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            if (adapter == null) {
                // L'adaptateur doit gérer l'ouverture d'une activité de MODIFICATION/GESTION
                 adapter = new OrganizerEventAdapter(this, cursor);
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