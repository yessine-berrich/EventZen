package com.example.eventzen;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

// Note: Cette classe est abstraite car elle n'est pas censée être instanciée seule.
public abstract class BaseActivity extends AppCompatActivity {

    protected UserSessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialiser le gestionnaire de session pour qu'il soit disponible dans toutes les activités héritées
        sessionManager = new UserSessionManager(this);
    }

    // 1. Créer le menu (gonfler main_menu.xml)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Le getMenuInflater doit être appelé ici
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    // 2. Gérer le clic sur l'élément du menu (Logout)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            logoutUser();
            return true;
        }

        if (id == R.id.action_my_registrations) {
            startActivity(new Intent(this, UserRegistrationsActivity.class));
            return true;
        }

        if (id == R.id.action_manage_events) {
            // Vérifier le rôle avant de lancer l'activité
            if ("organizer".equals(sessionManager.getCurrentUserRole())) {
                startActivity(new Intent(this, OrganizerEventsActivity.class));
            } else {
                Toast.makeText(this, "Accès réservé aux organisateurs.", Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Logique de déconnexion
    private void logoutUser() {
        sessionManager.logoutUser();

        // Rediriger vers l'écran de connexion et fermer toutes les activités précédentes
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        Toast.makeText(this, "Déconnexion réussie.", Toast.LENGTH_SHORT).show();
    }


}