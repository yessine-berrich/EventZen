package com.example.eventzen;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class UserRegistrationsActivity extends BaseActivity {

    private DBHelper dbHelper;
    private RecyclerView recyclerView;
    private RegistrationAdapter adapter;
    private TextView tvEmptyState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registrations); // Créer ce layout

        dbHelper = new DBHelper(this);

        // Configuration de la Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Mes Inscriptions");
        }

        tvEmptyState = findViewById(R.id.tv_empty_registrations); // Créer cet ID dans le layout
        recyclerView = findViewById(R.id.recycler_view_registrations); // Créer cet ID
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadUserRegistrations();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void loadUserRegistrations() {
        int userId = sessionManager.getCurrentUserId();

        if (userId == -1) {
            Toast.makeText(this, "Session expirée.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Cursor cursor = dbHelper.getUserRegistrations(userId);

        if (cursor.getCount() == 0) {
            tvEmptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            if (adapter == null) {
                // TODO: Vous devez créer RegistrationAdapter
                adapter = new RegistrationAdapter(this, cursor);
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