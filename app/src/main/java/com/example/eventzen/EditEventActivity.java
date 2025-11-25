package com.example.eventzen;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditEventActivity extends BaseActivity {

    public static final String EXTRA_EVENT_ID = "event_id_to_edit";

    private DBHelper dbHelper;
    private int eventId;

    private EditText etTitle, etDescription, etLocation, etStartDate, etEndDate;
    private Button btnSaveEvent;

    private Calendar startDateTime;
    private Calendar endDateTime;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event); // 👈 Réutilisation du layout de création

        dbHelper = new DBHelper(this);

        // 1. Récupération de l'ID à modifier
        eventId = getIntent().getIntExtra(EXTRA_EVENT_ID, -1);
        if (eventId == -1) {
            Toast.makeText(this, "Erreur: ID d'événement non spécifié pour la modification.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // 2. Configuration de la Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Modifier l'Événement");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // 3. Initialisation des vues
        etTitle = findViewById(R.id.et_event_title);
        etDescription = findViewById(R.id.et_event_description);
        etLocation = findViewById(R.id.et_event_location);
        etStartDate = findViewById(R.id.et_event_start_date);
        etEndDate = findViewById(R.id.et_event_end_date);
        btnSaveEvent = findViewById(R.id.btn_submit_event);

        // Mettre à jour le texte du bouton
        btnSaveEvent.setText("Sauvegarder les Modifications");

        // 4. Initialisation des calendriers
        startDateTime = Calendar.getInstance();
        endDateTime = Calendar.getInstance();

        // 5. Charger les données existantes
        loadEventData();

        // 6. Configurer les sélecteurs de date/heure (identique à CreateEventActivity)
        etStartDate.setFocusable(false);
        etStartDate.setOnClickListener(v -> showDateTimePicker(etStartDate, startDateTime));

        etEndDate.setFocusable(false);
        etEndDate.setOnClickListener(v -> showDateTimePicker(etEndDate, endDateTime));

        // 7. Écouteur de clic pour la sauvegarde
        btnSaveEvent.setOnClickListener(v -> updateEvent());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    /**
     * Charge les détails de l'événement depuis la DB et pré-remplit les champs.
     */
    private void loadEventData() {
        Cursor cursor = dbHelper.getPublicEventDetailById(eventId);

        if (cursor != null && cursor.moveToFirst()) {
            try {
                // 1. Récupération des données
                String title = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.E_TITLE));
                String location = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.E_LOCATION));
                String startDateStr = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.E_START_DATE));
                String endDateStr = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.E_END_DATE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.E_DESCRIPTION));

                // 2. Pré-remplissage des champs
                etTitle.setText(title);
                etLocation.setText(location);
                etDescription.setText(description);
                etStartDate.setText(startDateStr);
                etEndDate.setText(endDateStr);

                // 3. Initialisation des objets Calendar à partir des dates chargées
                try {
                    Date start = dateFormat.parse(startDateStr);
                    startDateTime.setTime(start);
                    Date end = dateFormat.parse(endDateStr);
                    endDateTime.setTime(end);
                } catch (ParseException e) {
                    Toast.makeText(this, "Erreur de format de date : " + e.getMessage(), Toast.LENGTH_LONG).show();
                    // Les calendriers restent sur l'heure actuelle, mais les champs texte sont remplis.
                }

            } catch (Exception e) {
                Toast.makeText(this, "Erreur de lecture des données: " + e.getMessage(), Toast.LENGTH_LONG).show();
            } finally {
                cursor.close();
            }
        } else {
            Toast.makeText(this, "Événement à modifier non trouvé.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    /**
     * Met à jour l'événement dans la base de données.
     */
    private void updateEvent() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String startDate = etStartDate.getText().toString().trim();
        String endDate = etEndDate.getText().toString().trim();

        if (title.isEmpty() || location.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs obligatoires.", Toast.LENGTH_LONG).show();
            return;
        }

        boolean success = dbHelper.updateEvent(eventId, title, description, location, startDate, endDate);

        if (success) {
            Toast.makeText(this, "Événement modifié avec succès !", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Échec de la modification de l'événement.", Toast.LENGTH_LONG).show();
        }
    }

    // --- LOGIQUE DES DATE/TIME PICKERS (COPIÉE DE CreateEventActivity) ---
    private void showDateTimePicker(final EditText editText, final Calendar targetCalendar) {
        final Calendar currentDate = targetCalendar;

        // 1. AFFICHER LE SÉLECTEUR DE DATE
        new DatePickerDialog(this, (view, year, monthOfYear, dayOfMonth) -> {
            targetCalendar.set(year, monthOfYear, dayOfMonth);

            // 2. AFFICHER LE SÉLECTEUR D'HEURE (immédiatement après la date)
            new TimePickerDialog(this, (view2, hourOfDay, minute) -> {
                targetCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                targetCalendar.set(Calendar.MINUTE, minute);

                // 3. METTRE À JOUR LE CHAMP TEXTE
                updateLabel(editText, targetCalendar);
            }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), true).show();

        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateLabel(EditText editText, Calendar calendar) {
        // Le format doit correspondre au SimpleDateFormat défini en haut
        editText.setText(dateFormat.format(calendar.getTime()));
    }
    // --------------------------------------------------------------------
}