package com.example.eventzen;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegisterLink;

    private DBHelper dbHelper;
    private UserSessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DBHelper(this);
        sessionManager = new UserSessionManager(this);

        // Si l'utilisateur est déjà connecté, le rediriger immédiatement
        if (sessionManager.isLoggedIn()) {
            // Remplacer BrowseEventsActivity.class par l'activité de tableau de bord appropriée
            startActivity(new Intent(LoginActivity.this, BrowseEventsActivity.class));
            finish();
        }

        etEmail = findViewById(R.id.et_login_email);
        etPassword = findViewById(R.id.et_login_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegisterLink = findViewById(R.id.tv_register_link);

        btnLogin.setOnClickListener(v -> attemptLogin());

        tvRegisterLink.setOnClickListener(v -> {
            // TODO: Créer et lancer l'activité RegisterActivity
            Toast.makeText(this, "Redirection vers l'inscription...", Toast.LENGTH_SHORT).show();
            // startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void attemptLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Veuillez entrer l'e-mail et le mot de passe.", Toast.LENGTH_SHORT).show();
            return;
        }

        Cursor cursor = dbHelper.authenticateUser(email, password);

        if (cursor != null && cursor.moveToFirst()) {
            // Utilisateur trouvé et authentifié
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_ID));
            String role = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_ROLE));
            String prenom = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_PRENOM));

            // Créer la session
            sessionManager.createLoginSession(userId, role);
            Toast.makeText(this, "Bienvenue, " + prenom + " (" + role + ")!", Toast.LENGTH_LONG).show();

            // Rediriger vers l'activité principale
            Intent intent = new Intent(LoginActivity.this, BrowseEventsActivity.class);
            startActivity(intent);
            finish();

        } else {
            Toast.makeText(this, "Identifiants invalides ou utilisateur non trouvé.", Toast.LENGTH_LONG).show();
        }

        if (cursor != null) {
            cursor.close();
        }
    }
}