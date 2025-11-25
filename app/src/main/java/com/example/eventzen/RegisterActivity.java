package com.example.eventzen;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText etNom, etPrenom, etEmail, etPassword;
    private Button btnRegister;
    private TextView tvLoginLink;

    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new DBHelper(this);

        // 1. Initialisation des vues
        etNom = findViewById(R.id.et_register_nom);
        etPrenom = findViewById(R.id.et_register_prenom);
        etEmail = findViewById(R.id.et_register_email);
        etPassword = findViewById(R.id.et_register_password);
        btnRegister = findViewById(R.id.btn_register);
        tvLoginLink = findViewById(R.id.tv_login_link);

        // 2. Écouteurs de clic
        btnRegister.setOnClickListener(v -> attemptRegistration());
        tvLoginLink.setOnClickListener(v -> {
            // Lancer l'activité de connexion et fermer l'activité d'inscription
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void attemptRegistration() {
        String nom = etNom.getText().toString().trim();
        String prenom = etPrenom.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // 🔒 Validation simple des champs non vides
        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs.", Toast.LENGTH_LONG).show();
            return;
        }

        // 🔒 Validation basique du mot de passe
        if (password.length() < 6) {
            Toast.makeText(this, "Le mot de passe doit contenir au moins 6 caractères.", Toast.LENGTH_LONG).show();
            return;
        }

        // 3. Appel à la DB pour l'enregistrement
        long result = dbHelper.registerUser(nom, prenom, email, password);

        if (result != -1) {
            // Inscription réussie. Redirection vers la page de connexion.
            Toast.makeText(this, "Inscription réussie ! Veuillez vous connecter.", Toast.LENGTH_LONG).show();

            // Démarrer LoginActivity pour que l'utilisateur se connecte
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();

        } else {
            // Échec, probablement dû à un email déjà existant (colonne UNIQUE dans la DB)
            Toast.makeText(this, "Erreur d'inscription. L'e-mail est peut-être déjà utilisé.", Toast.LENGTH_LONG).show();
        }
    }
}