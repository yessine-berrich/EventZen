package com.example.eventzen;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import java.util.Calendar;
import java.util.Locale; // Pour un format de date/heure cohérent
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

// Hérite de BaseActivity pour la Toolbar et le Logout
public class CreateEventActivity extends BaseActivity {

    private EditText etTitle, etDescription, etLocation, etStartDate, etEndDate;
    private Button btnCreateEvent;
    private DBHelper dbHelper;
    private Calendar startDateTime;
    private Calendar endDateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        // Initialisation de la Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Créer un Nouvel Événement");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialisation des calendriers pour stocker les dates/heures
        startDateTime = Calendar.getInstance();
        endDateTime = Calendar.getInstance();

        // Initialisation des vues (comme avant)
        // ...
        etStartDate = findViewById(R.id.et_event_start_date);
        etEndDate = findViewById(R.id.et_event_end_date);
        // ...

        // Rendre les EditText non modifiables manuellement et ajouter le clic
        etStartDate.setFocusable(false);
        etStartDate.setOnClickListener(v -> showDateTimePicker(etStartDate, startDateTime));

        etEndDate.setFocusable(false);
        etEndDate.setOnClickListener(v -> showDateTimePicker(etEndDate, endDateTime));

        // Vérification du rôle : seul l'organisateur est autorisé
        String role = sessionManager.getCurrentUserRole();
        if (role == null || !role.equals("organizer")) {
            Toast.makeText(this, "Accès refusé. Réservé aux organisateurs.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        dbHelper = new DBHelper(this);

        // Initialisation des vues (assurez-vous des IDs corrects)
        etTitle = findViewById(R.id.et_event_title);
        etDescription = findViewById(R.id.et_event_description);
        etLocation = findViewById(R.id.et_event_location);
        etStartDate = findViewById(R.id.et_event_start_date);
        etEndDate = findViewById(R.id.et_event_end_date);
        btnCreateEvent = findViewById(R.id.btn_submit_event);

        btnCreateEvent.setOnClickListener(v -> createEvent());
    }

    /**
     * Affiche successivement le DatePicker puis le TimePicker.
     */
    private void showDateTimePicker(final EditText editText, final Calendar targetCalendar) {
        final Calendar currentDate = targetCalendar;

        // 1. AFFICHER LE SÉLECTEUR DE DATE
        new DatePickerDialog(this, (view, year, monthOfYear, dayOfMonth) -> {
            // Mettre à jour le calendrier cible
            targetCalendar.set(year, monthOfYear, dayOfMonth);

            // 2. AFFICHER LE SÉLECTEUR D'HEURE (immédiatement après la date)
            new TimePickerDialog(this, (view2, hourOfDay, minute) -> {
                // Mettre à jour le calendrier cible avec l'heure
                targetCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                targetCalendar.set(Calendar.MINUTE, minute);

                // 3. METTRE À JOUR LE CHAMP TEXTE
                updateLabel(editText, targetCalendar);
            }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), true).show(); // 'true' pour le format 24h

        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH)).show();
    }

    /**
     * Formatte l'objet Calendar en chaîne de caractères AAAA-MM-JJ HH:MM et met à jour l'EditText.
     */
    private void updateLabel(EditText editText, Calendar calendar) {
        // Utilisation d'un format simple et stockable (ex: "2025-11-17 20:30")
        String format = "yyyy-MM-dd HH:mm";
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(format, Locale.getDefault());

        editText.setText(sdf.format(calendar.getTime()));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void createEvent() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String location = etLocation.getText().toString().trim();

        // Récupère les chaînes formatées depuis les EditText
        String startDate = etStartDate.getText().toString().trim();
        String endDate = etEndDate.getText().toString().trim();

        int organizerId = sessionManager.getCurrentUserId();

        if (title.isEmpty() || location.isEmpty() || startDate.isEmpty() || endDate.isEmpty() || organizerId == -1) {
            Toast.makeText(this, "Veuillez remplir tous les champs obligatoires, y compris les dates.", Toast.LENGTH_LONG).show();
            return;
        }

        long newEventId = dbHelper.insertEvent(organizerId, title, description, location, startDate, endDate);

        if (newEventId != -1) {
            Toast.makeText(this, "Événement créé avec succès! Veuillez ajouter des billets.", Toast.LENGTH_LONG).show();

            // REDIRECTION VERS LA CRÉATION DE BILLETS
            Intent intent = new Intent(this, CreateTicketActivity.class);
            intent.putExtra(CreateTicketActivity.EXTRA_NEW_EVENT_ID, newEventId);
            startActivity(intent);
            finish();

        } else {
            Toast.makeText(this, "Erreur lors de la création de l'événement.", Toast.LENGTH_LONG).show();
        }
    }
}