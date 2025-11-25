package com.example.eventzen;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ManageUsersActivity extends BaseActivity implements UserAdapter.UserClickListener {

    private DBHelper dbHelper;
    private UserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        dbHelper = new DBHelper(this);

        // 1. Configuration de la Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Gestion des Utilisateurs");
        }

        // 2. Configuration de la RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recycler_view_users);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new UserAdapter(this, null, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUsers(); // Recharger la liste
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapter != null) {
            adapter.swapCursor(null); // Fermer le curseur
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void loadUsers() {
        Cursor cursor = dbHelper.getAllUsers();
        if (cursor != null) {
            adapter.swapCursor(cursor);
        } else {
            Toast.makeText(this, "Impossible de charger les utilisateurs.", Toast.LENGTH_SHORT).show();
        }
    }

    // --- Implémentation de l'interface UserClickListener ---

    @Override
    public void onUserClick(int userId, String currentRole, String userName) {
        // Options de rôle disponibles
        final String[] roles = {"client", "organisateur", "admin"};
        int checkedItem = 0;

        // Trouver l'index du rôle actuel
        for (int i = 0; i < roles.length; i++) {
            if (roles[i].equals(currentRole)) {
                checkedItem = i;
                break;
            }
        }

        new AlertDialog.Builder(this)
                .setTitle("Modifier le rôle de " + userName)
                .setSingleChoiceItems(roles, checkedItem, (dialog, which) -> {
                    // L'utilisateur a sélectionné un nouveau rôle
                    String newRole = roles[which];

                    if (dbHelper.updateUserRole(userId, newRole)) {
                        Toast.makeText(ManageUsersActivity.this, userName + " est maintenant : " + newRole, Toast.LENGTH_SHORT).show();
                        loadUsers(); // Rafraîchir la liste
                    } else {
                        Toast.makeText(ManageUsersActivity.this, "Échec de la mise à jour du rôle.", Toast.LENGTH_LONG).show();
                    }
                    dialog.dismiss(); // Fermer la boîte de dialogue après la sélection
                })
                .setNegativeButton("Annuler", null)
                .show();
    }
}