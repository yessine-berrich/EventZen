package com.example.eventzen;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

public class CreateTicketActivity extends BaseActivity {

    public static final String EXTRA_NEW_EVENT_ID = "extra_new_event_id";

    // NOTE: Il manque etDescription dans ce code pour correspondre à la DB
    private EditText etType, etPrice, etQuantity;
    private Button btnAddTicket, btnFinish;
    private TextView tvEventReference;

    private DBHelper dbHelper;
    private long eventId; // Conserver en long ici pour l'Intent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_ticket);

        dbHelper = new DBHelper(this);

        // 1. Récupérer l'ID de l'événement
        eventId = getIntent().getLongExtra(EXTRA_NEW_EVENT_ID, -1);

        if (eventId == -1) {
            Toast.makeText(this, "Erreur: ID d'événement manquant. Impossible d'ajouter des billets.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // 2. Configuration de la Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Ajouter des Billets");
        }

        // 3. Initialisation des vues
        tvEventReference = findViewById(R.id.tv_event_reference);
        etType = findViewById(R.id.et_ticket_type);
        etPrice = findViewById(R.id.et_ticket_price);
        etQuantity = findViewById(R.id.et_ticket_quantity);
        btnAddTicket = findViewById(R.id.btn_submit_ticket);
        btnFinish = findViewById(R.id.btn_finish_tickets);

        tvEventReference.setText("Ajouter un Billet pour l'événement #" + eventId);

        btnAddTicket.setOnClickListener(v -> addTicket());
        btnFinish.setOnClickListener(v -> finishActivityAndGoHome());
    }

    private void addTicket() {
        String type = etType.getText().toString().trim();
        // NOTE: La description est manquante ici mais est requise par DBHelper
        String description = ""; // Description vide par défaut pour la compatibilité
        String priceStr = etPrice.getText().toString().trim();
        String quantityStr = etQuantity.getText().toString().trim();

        if (type.isEmpty() || priceStr.isEmpty() || quantityStr.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs du billet.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double price = Double.parseDouble(priceStr);
            int quantity = Integer.parseInt(quantityStr);

            // 🔒 Validation des données
            if (price < 0 || quantity <= 0) {
                Toast.makeText(this, "Le prix ne peut être négatif et la quantité doit être supérieure à zéro.", Toast.LENGTH_LONG).show();
                return;
            }

            // 💥 CORRECTION DE L'ERREUR : La méthode insertTicket renvoie un LONG.
            // Nous devons le stocker et vérifier s'il est différent de -1.
            // On convertit l'ID de l'événement en int si la méthode DBHelper l'attend en int.
            long newRowId = dbHelper.insertTicket((int)eventId, type, description, price, quantity);

            if (newRowId != -1) { // Vérifie que l'insertion a réussi
                Toast.makeText(this, "Billet '" + type + "' ajouté avec succès!", Toast.LENGTH_SHORT).show();

                // Réinitialiser les champs pour ajouter un autre billet
                etType.setText("");
                etPrice.setText("");
                etQuantity.setText("");
                etType.requestFocus();
            } else {
                Toast.makeText(this, "Erreur lors de l'ajout du billet (DB Error).", Toast.LENGTH_LONG).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Veuillez entrer des valeurs numériques valides pour le prix et la quantité.", Toast.LENGTH_LONG).show();
        }
    }

    private void finishActivityAndGoHome() {
        // Rediriger vers l'accueil et vider la pile
        Intent intent = new Intent(this, BrowseEventsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}