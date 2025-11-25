package com.example.eventzen;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

// Implémente l'interface de l'adaptateur pour gérer les clics
public class ManageTicketsActivity extends BaseActivity implements EventTicketAdapter.TicketActionListener {

    public static final String EXTRA_EVENT_ID = "event_id_to_manage";
    public static final String EXTRA_TICKET_ID = "ticket_id_to_edit";

    private DBHelper dbHelper;
    private EventTicketAdapter adapter;
    private int eventId;

    // Pour l'affichage d'un nom d'événement si passé en extra
    private String eventTitle = "Événement";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_tickets);

        dbHelper = new DBHelper(this);

        // 1. Récupération des extras
        eventId = getIntent().getIntExtra(EXTRA_EVENT_ID, -1);
        String titleExtra = getIntent().getStringExtra("EXTRA_EVENT_TITLE_FOR_TICKET_MGMT");
        if (titleExtra != null) eventTitle = titleExtra;

        if (eventId == -1) {
            Toast.makeText(this, "Erreur: ID d'événement manquant pour la gestion des billets.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // 2. Configuration de la Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Gestion des Billets");
        }

        // 3. Affichage du header
        TextView tvHeader = findViewById(R.id.tv_ticket_header);
        tvHeader.setText("Billets disponibles pour : " + eventTitle);

        // 4. Configuration de la RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recycler_view_tickets);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Le curseur sera initialisé dans onResume
        adapter = new EventTicketAdapter(this, null, this);
        recyclerView.setAdapter(adapter);

        // 5. Bouton Ajouter
        FloatingActionButton fabAddTicket = findViewById(R.id.fab_add_ticket);
        fabAddTicket.setOnClickListener(v -> launchTicketForm(0)); // 0 = création
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTickets(); // Recharger la liste après un retour (création/modification)
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapter != null) {
            adapter.swapCursor(null); // Fermer le curseur pour éviter les fuites de mémoire
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void loadTickets() {
        // NOTE: Il faut ajouter une méthode getTicketsByEventId(eventId) dans DBHelper.
        // Pour l'instant, on utilise une méthode hypothétique. Si elle n'existe pas, ajoutez-la.
        // public Cursor getTicketsByEventId(int eventId) { ... }
        Cursor cursor = dbHelper.getTicketsByEventId(eventId);

        if (cursor != null) {
            adapter.swapCursor(cursor);
        } else {
            Toast.makeText(this, "Impossible de charger les billets.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Lance le formulaire de billet. Si ticketId est > 0, c'est une modification.
     */
    private void launchTicketForm(int ticketId) {
        Intent intent = new Intent(this, TicketFormActivity.class); // Nouvelle activité formulaire
        intent.putExtra(TicketFormActivity.EXTRA_EVENT_ID, eventId);

        if (ticketId > 0) {
            // Modification
            intent.putExtra(TicketFormActivity.EXTRA_TICKET_ID, ticketId);
        }
        startActivity(intent);
    }

    // --- Implémentation de l'interface TicketActionListener ---

    @Override
    public void onEditTicket(int ticketId) {
        launchTicketForm(ticketId); // Lancer le formulaire en mode modification
    }

    @Override
    public void onDeleteTicket(int ticketId) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmation de Suppression")
                .setMessage("Êtes-vous sûr de vouloir supprimer ce type de billet ? Les inscriptions existantes pourraient être affectées.")
                .setPositiveButton("Oui, Supprimer", (dialog, which) -> {
                    if (dbHelper.deleteTicket(ticketId)) {
                        Toast.makeText(ManageTicketsActivity.this, "Billet supprimé.", Toast.LENGTH_SHORT).show();
                        loadTickets(); // Rafraîchir la liste
                    } else {
                        Toast.makeText(ManageTicketsActivity.this, "Échec de la suppression. Peut-être des inscriptions sont liées.", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Annuler", null)
                .show();
    }
}