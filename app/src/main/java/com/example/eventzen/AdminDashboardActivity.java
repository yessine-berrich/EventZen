package com.example.eventzen;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

public class AdminDashboardActivity extends BaseActivity {

    private DBHelper dbHelper;
    private UserSessionManager sessionManager;

    private TextView tvWelcome, tvTotalUsers, tvTotalEvents, tvTotalRegistrations;
    private Button btnManageUsers, btnModerateContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        dbHelper = new DBHelper(this);
        sessionManager = new UserSessionManager(this);

        // 1. Configuration de la Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Afficher l'icône de déconnexion si nécessaire (géré dans BaseActivity si vous l'avez)

        // 2. Initialisation des vues
        tvWelcome = findViewById(R.id.tv_welcome_admin);
        tvTotalUsers = findViewById(R.id.tv_total_users);
        tvTotalEvents = findViewById(R.id.tv_total_events);
        tvTotalRegistrations = findViewById(R.id.tv_total_registrations);
        btnManageUsers = findViewById(R.id.btn_manage_users);
        btnModerateContent = findViewById(R.id.btn_moderate_content);

        // 3. Charger les statistiques
        loadStatistics();

        // 4. Gestion des clics
        btnManageUsers.setOnClickListener(v -> {
            // TODO: Lancer l'activité de gestion des utilisateurs (ManageUsersActivity)
//            Toast.makeText(this, "Fonctionnalité de gestion des utilisateurs à implémenter.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(AdminDashboardActivity.this, ManageUsersActivity.class));
        });

        btnModerateContent.setOnClickListener(v -> {
            Toast.makeText(this, "Fonctionnalité de modération future.", Toast.LENGTH_SHORT).show();
        });

        // Optionnel: Afficher le nom de l'admin
        // String prenom = sessionManager.getUserPrenom(); // Assurez-vous d'avoir une méthode pour récupérer le prénom
        // if (prenom != null) {
        //     tvWelcome.setText("Bienvenue, " + prenom + " (Admin)!");
        // }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStatistics(); // Rafraîchir les stats si des données ont été modifiées ailleurs
    }

    private void loadStatistics() {
        int totalUsers = dbHelper.getTotalUsersCount();
        int totalEvents = dbHelper.getTotalEventsCount();
        int totalRegistrations = dbHelper.getTotalRegistrationsCount();

        tvTotalUsers.setText("Total Utilisateurs : " + totalUsers);
        tvTotalEvents.setText("Total Événements : " + totalEvents);
        tvTotalRegistrations.setText("Total Inscriptions : " + totalRegistrations);
    }
}