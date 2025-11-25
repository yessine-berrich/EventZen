package com.example.eventzen;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

// Note: Le layout activity_create_ticket.xml est utilisé, mais il serait plus clair
// de le renommer en activity_ticket_form.xml si possible.

public class TicketFormActivity extends BaseActivity {

    // Renommage des clés pour plus de clarté dans le contexte du formulaire
    public static final String EXTRA_EVENT_ID = "event_id_for_ticket";
    public static final String EXTRA_TICKET_ID = "ticket_id_to_edit"; // NOUVELLE CLE

    private DBHelper dbHelper;
    private int eventId;
    private int ticketIdToEdit = 0; // 0 signifie création

    private EditText etType, etDescription, etPrice, etQuantity;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_ticket);

        dbHelper = new DBHelper(this);

        // 1. Récupération des extras
        eventId = getIntent().getIntExtra(EXTRA_EVENT_ID, -1);
        ticketIdToEdit = getIntent().getIntExtra(EXTRA_TICKET_ID, 0); // Récupère l'ID du billet à modifier

        if (eventId == -1) {
            Toast.makeText(this, "Erreur: Événement non spécifié.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // 2. Configuration de la Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            // Définir le titre en fonction du mode (Création ou Modification)
            if (ticketIdToEdit > 0) {
                getSupportActionBar().setTitle("Modifier Billet #" + ticketIdToEdit);
                loadTicketData(ticketIdToEdit);
            } else {
                getSupportActionBar().setTitle("Créer un Nouveau Billet");
            }
        }

        // 3. Initialisation des vues
        etType = findViewById(R.id.et_ticket_type);
        etDescription = findViewById(R.id.et_ticket_description);
        etPrice = findViewById(R.id.et_ticket_price);
        etQuantity = findViewById(R.id.et_ticket_quantity);
        btnSave = findViewById(R.id.btn_submit_ticket); // Assurez-vous que l'ID est correct dans le layout

        btnSave.setOnClickListener(v -> saveTicket());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void loadTicketData(int ticketId) {
        // NOTE: Il faut une méthode getTicketById dans DBHelper
        Cursor cursor = dbHelper.getTicketById(ticketId);

        if (cursor != null && cursor.moveToFirst()) {
            try {
                // Récupération des données
                String type = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.T_TYPE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.T_DESCRIPTION));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.T_PRICE));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.T_QUANTITY));

                // Pré-remplissage
                etType.setText(type);
                etDescription.setText(description);
                etPrice.setText(String.valueOf(price));
                etQuantity.setText(String.valueOf(quantity));

                btnSave.setText("Sauvegarder les Modifications");

            } catch (Exception e) {
                Toast.makeText(this, "Erreur de chargement des données du billet.", Toast.LENGTH_LONG).show();
                finish();
            } finally {
                cursor.close();
            }
        } else {
            Toast.makeText(this, "Billet non trouvé.", Toast.LENGTH_LONG).show();
            finish();
        }
    }


    private void saveTicket() {
        String type = etType.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        // Validation basique (à améliorer avec des try-catch pour les nombres)
        if (type.isEmpty() || etPrice.getText().toString().isEmpty() || etQuantity.getText().toString().isEmpty()) {
            Toast.makeText(this, "Veuillez remplir au moins le type, le prix et la quantité.", Toast.LENGTH_LONG).show();
            return;
        }

        double price = Double.parseDouble(etPrice.getText().toString());
        int quantity = Integer.parseInt(etQuantity.getText().toString());

        boolean success;

        if (ticketIdToEdit > 0) {
            // MODE MODIFICATION
            success = dbHelper.updateTicket(ticketIdToEdit, type, description, price, quantity);
        } else {
            // MODE CRÉATION
            success = (dbHelper.insertTicket(eventId, type, description, price, quantity) != -1);
        }

        if (success) {
            Toast.makeText(this, "Billet " + (ticketIdToEdit > 0 ? "modifié" : "créé") + " avec succès.", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Échec de l'opération sur le billet.", Toast.LENGTH_LONG).show();
        }
    }
}