package com.example.eventzen;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "EventDB.db";
    private static final int DATABASE_VERSION = 3;

    // --- Table USERS (Utilisateurs) ---
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_ID = "_id"; // ID primaire
    public static final String COLUMN_NOM = "nom";
    public static final String COLUMN_PRENOM = "prenom";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_ROLE = "role"; // 'client' ou 'organizer'

    // --- Table EVENTS (Événements) ---
    public static final String TABLE_EVENTS = "events";
    public static final String E_ID = "_id";
    public static final String E_ORGANIZER_ID = "organizer_id"; // Clé étrangère vers users
    public static final String E_TITLE = "title";
    public static final String E_DESCRIPTION = "description";
    public static final String E_LOCATION = "location";
    public static final String E_START_DATE = "start_date";
    public static final String E_END_DATE = "end_date";

    // --- Table TICKETS (Billets) ---
    public static final String TABLE_TICKETS = "tickets";
    public static final String T_ID = "_id";
    public static final String T_EVENT_ID = "event_id"; // Clé étrangère vers events
    public static final String T_TYPE = "type"; // Ex: Standard, VIP
    public static final String T_DESCRIPTION = "ticket_description";
    public static final String T_PRICE = "price";
    public static final String T_QUANTITY = "quantity";

    // --- Table REGISTRATIONS (Inscriptions) ---
    public static final String TABLE_REGISTRATIONS = "registrations";
    public static final String R_ID = "_id";
    public static final String R_USER_ID = "user_id"; // Clé étrangère vers users
    public static final String R_EVENT_ID = "event_id"; // Clé étrangère vers events
    public static final String R_TICKET_ID = "ticket_id"; // Clé étrangère vers tickets
    public static final String R_STATUS = "status"; // Ex: Confirmé, Annulé
    public static final String R_REGISTERED_AT = "registered_at"; // Date d'inscription

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL pour créer la table USERS
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NOM + " TEXT,"
                + COLUMN_PRENOM + " TEXT,"
                + COLUMN_EMAIL + " TEXT UNIQUE,"
                + COLUMN_PASSWORD + " TEXT,"
                + COLUMN_ROLE + " TEXT DEFAULT 'client'"
                + ")";
        db.execSQL(CREATE_USERS_TABLE);

        // SQL pour créer la table EVENTS
        String CREATE_EVENTS_TABLE = "CREATE TABLE " + TABLE_EVENTS + "("
                + E_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + E_ORGANIZER_ID + " INTEGER,"
                + E_TITLE + " TEXT,"
                + E_DESCRIPTION + " TEXT,"
                + E_LOCATION + " TEXT,"
                + E_START_DATE + " TEXT,"
                + E_END_DATE + " TEXT,"
                + "FOREIGN KEY(" + E_ORGANIZER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + ")"
                + ")";
        db.execSQL(CREATE_EVENTS_TABLE);

        // SQL pour créer la table TICKETS
        String CREATE_TICKETS_TABLE = "CREATE TABLE " + TABLE_TICKETS + "("
                + T_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + T_EVENT_ID + " INTEGER,"
                + T_TYPE + " TEXT,"
                + T_DESCRIPTION + " TEXT,"
                + T_PRICE + " REAL,"
                + T_QUANTITY + " INTEGER,"
                + "FOREIGN KEY(" + T_EVENT_ID + ") REFERENCES " + TABLE_EVENTS + "(" + E_ID + ")"
                + ")";
        db.execSQL(CREATE_TICKETS_TABLE);

        // SQL pour créer la table REGISTRATIONS
        String CREATE_REGISTRATIONS_TABLE = "CREATE TABLE " + TABLE_REGISTRATIONS + "("
                + R_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + R_USER_ID + " INTEGER,"
                + R_EVENT_ID + " INTEGER,"
                + R_TICKET_ID + " INTEGER,"
                + R_STATUS + " TEXT,"
                + R_REGISTERED_AT + " TEXT,"
                + "FOREIGN KEY(" + R_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + "),"
                + "FOREIGN KEY(" + R_EVENT_ID + ") REFERENCES " + TABLE_EVENTS + "(" + E_ID + "),"
                + "FOREIGN KEY(" + R_TICKET_ID + ") REFERENCES " + TABLE_TICKETS + "(" + T_ID + ")"
                + ")";
        db.execSQL(CREATE_REGISTRATIONS_TABLE);

        // --- Insertion de données initiales (pour le test) ---
        insertInitialData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Ceci est important pour les mises à jour en production, mais pour l'instant :
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REGISTRATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TICKETS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
//        if (oldVersion < 2) { // Utilisez la version appropriée
//            db.execSQL("ALTER TABLE " + TABLE_TICKETS + " ADD COLUMN " + T_DESCRIPTION + " TEXT DEFAULT ''");
//        }
        onCreate(db);
    }

    // --- Méthode d'insertion de données initiales ---
    private void insertInitialData(SQLiteDatabase db) {
        // 1. Organisateur
        ContentValues organizer = new ContentValues();
        organizer.put(COLUMN_NOM, "Durand");
        organizer.put(COLUMN_PRENOM, "Alice");
        organizer.put(COLUMN_EMAIL, "organizer@test.com");
        organizer.put(COLUMN_PASSWORD, "pass"); // Utiliser le hachage en production!
        organizer.put(COLUMN_ROLE, "organizer");
        db.insert(TABLE_USERS, null, organizer);

        // 2. Client
        ContentValues client = new ContentValues();
        client.put(COLUMN_NOM, "Martin");
        client.put(COLUMN_PRENOM, "Bob");
        client.put(COLUMN_EMAIL, "client@test.com");
        client.put(COLUMN_PASSWORD, "pass");
        client.put(COLUMN_ROLE, "client");
        db.insert(TABLE_USERS, null, client);

        // 3. admin
        ContentValues admin = new ContentValues();
        client.put(COLUMN_NOM, "Alex");
        client.put(COLUMN_PRENOM, "lupin");
        client.put(COLUMN_EMAIL, "admin@test.com");
        client.put(COLUMN_PASSWORD, "pass");
        client.put(COLUMN_ROLE, "admin");
        db.insert(TABLE_USERS, null, admin);
    }

    // --- Méthodes Clés d'Authentification ---

    // Dans DBHelper.java

    /**
     * Insère un nouvel utilisateur dans la base de données.
     * Le rôle est 'client' par défaut.
     * @return L'ID de la nouvelle ligne insérée, ou -1 en cas d'échec.
     */
    public long registerUser(String nom, String prenom, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Utilisez la méthode de hachage de mot de passe si vous en avez une, sinon utilisez la chaîne brute pour l'instant.
        // values.put(COLUMN_PASSWORD, hashPassword(password));

        values.put(COLUMN_NOM, nom);
        values.put(COLUMN_PRENOM, prenom);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_ROLE, "client"); // Rôle par défaut

        long newRowId = db.insert(TABLE_USERS, null, values);
        db.close();
        return newRowId;
    }

    /**
     * Vérifie les identifiants de l'utilisateur.
     * Retourne un Cursor si l'utilisateur est trouvé.
     */
    public Cursor authenticateUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {COLUMN_ID, COLUMN_NOM, COLUMN_PRENOM, COLUMN_ROLE};
        String selection = COLUMN_EMAIL + " = ? AND " + COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {email, password};

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs,
                null, null, null);

        // Ne pas fermer le curseur ici, il doit être géré par l'appelant
        return cursor;
    }

    // NOTE : Vous devrez ajouter ici toutes les méthodes pour les événements, tickets, et inscriptions.

    /**
     * Récupère tous les événements publics (avec les détails de l'organisateur)
     * pour l'affichage dans BrowseEventsActivity.
     * @return Cursor contenant les colonnes d'événement et le nom de l'organisateur.
     */
    public Cursor getAllPublicEvents() {
        SQLiteDatabase db = this.getReadableDatabase();

        // Jointure pour récupérer le titre, la date, et le nom de l'organisateur
        String query = "SELECT " +
                "E." + E_ID + " AS " + E_ID + ", " + // Alias obligatoire pour le CursorAdapter
                "E." + E_TITLE + ", " +
                "E." + E_LOCATION + ", " +
                "E." + E_START_DATE + ", " +
                "U." + COLUMN_NOM + " AS organizer_nom, " +
                "U." + COLUMN_PRENOM + " AS organizer_prenom " +
                "FROM " + TABLE_EVENTS + " E " +
                "JOIN " + TABLE_USERS + " U ON E." + E_ORGANIZER_ID + " = U." + COLUMN_ID +
                " ORDER BY E." + E_START_DATE + " ASC";

        // Exécute la requête brute
        Cursor cursor = db.rawQuery(query, null);

        // Ne fermez pas le curseur ici, il doit être géré par l'appelant (BrowseEventsActivity)
        return cursor;
    }

    /**
     * Récupère les détails complets d'un événement public par son ID.
     */
    public Cursor getPublicEventDetailById(int eventId) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Jointure pour obtenir tous les détails de l'événement et le nom de l'organisateur
        String query = "SELECT " +
                "E.*, " + // Sélectionne toutes les colonnes de la table EVENTS
                "U." + COLUMN_NOM + " AS organizer_nom, " +
                "U." + COLUMN_PRENOM + " AS organizer_prenom " +
                "FROM " + TABLE_EVENTS + " E " +
                "JOIN " + TABLE_USERS + " U ON E." + E_ORGANIZER_ID + " = U." + COLUMN_ID +
                " WHERE E." + E_ID + " = ?";

        String[] selectionArgs = {String.valueOf(eventId)};

        // Exécute la requête brute
        Cursor cursor = db.rawQuery(query, selectionArgs);

        return cursor;
    }

    /**
     * Enregistre l'inscription d'un utilisateur à un événement avec un billet spécifique.
     */
    public boolean registerForEvent(int userId, int eventId, int ticketId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // 1. Ajouter l'enregistrement
        values.put(R_USER_ID, userId);
        values.put(R_EVENT_ID, eventId);
        values.put(R_TICKET_ID, ticketId);
        values.put(R_STATUS, "Confirmé"); // Statut par défaut
        values.put(R_REGISTERED_AT, java.time.LocalDateTime.now().toString()); // Utiliser une date/heure standard

        long result = db.insert(TABLE_REGISTRATIONS, null, values);

        // 2. Décrémenter la quantité de billets
        if (result != -1) {
            // Logique de décrémentation (simplifiée pour l'exemple)
            db.execSQL("UPDATE " + TABLE_TICKETS +
                    " SET " + T_QUANTITY + " = " + T_QUANTITY + " - 1 " +
                    " WHERE " + T_ID + " = " + ticketId);
        }

        db.close();
        return result != -1;
    }

    // Dans DBHelper.java

    /**
     * Insère un nouvel événement dans la base de données.
     * @return L'ID du nouvel événement, ou -1 en cas d'échec.
     */
    public long insertEvent(int organizerId, String title, String description, String location, String startDate, String endDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(E_ORGANIZER_ID, organizerId);
        values.put(E_TITLE, title);
        values.put(E_DESCRIPTION, description);
        values.put(E_LOCATION, location);
        values.put(E_START_DATE, startDate);
        values.put(E_END_DATE, endDate);

        long newId = db.insert(TABLE_EVENTS, null, values);
        db.close();
        return newId;
    }

    /**
     * Insère un nouveau billet dans la base de données.
     * Ancienne signature (probablement) : public long insertTicket(long eventId, String type, double price, int quantity)
     * Nouvelle signature :
     */
    public long insertTicket(int eventId, String type, String description, double price, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(T_EVENT_ID, eventId);
        values.put(T_TYPE, type);
        values.put(T_DESCRIPTION, description); // 👈 AJOUTEZ CETTE LIGNE
        values.put(T_PRICE, price);
        values.put(T_QUANTITY, quantity);

        long newRowId = db.insert(TABLE_TICKETS, null, values);
        db.close();
        return newRowId;
    }

    /**
     * Récupère tous les événements auxquels un utilisateur spécifique est inscrit.
     * Jointure REGISTRATIONS -> TICKETS -> EVENTS
     */
    public Cursor getUserRegistrations(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT " +
                "R." + R_ID + " AS _id, " + // Nécessaire pour les CursorAdapters
                "E." + E_TITLE + ", " +
                "E." + E_START_DATE + ", " +
                "R." + R_STATUS + ", " +
                "T." + T_TYPE + ", " +
                "T." + T_PRICE + " " +
                "FROM " + TABLE_REGISTRATIONS + " R " +
                "JOIN " + TABLE_EVENTS + " E ON R." + R_EVENT_ID + " = E." + E_ID +
                " JOIN " + TABLE_TICKETS + " T ON R." + R_TICKET_ID + " = T." + T_ID +
                " WHERE R." + R_USER_ID + " = ?";

        String[] selectionArgs = {String.valueOf(userId)};

        Cursor cursor = db.rawQuery(query, selectionArgs);
        return cursor;
    }

    /**
     * Récupère tous les événements créés par un organisateur spécifique.
     * @param organizerId L'ID de l'utilisateur organisateur.
     * @return Cursor contenant les événements créés.
     */
    public Cursor getOrganizerEvents(int organizerId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {E_ID, E_TITLE, E_START_DATE, E_LOCATION};
        String selection = E_ORGANIZER_ID + " = ?"; // 👈 C'est la ligne clé
        String[] selectionArgs = {String.valueOf(organizerId)};

        // Assurez-vous que le nom de la colonne E_ORGANIZER_ID est correct
        // (cela doit correspondre à la constante utilisée lors de l'insertion dans insertEvent)

        return db.query(TABLE_EVENTS, columns, selection, selectionArgs, null, null, E_START_DATE + " ASC");
    }

    /**
     * Récupère les inscriptions pour un événement spécifique (vu par l'organisateur).
     * Jointure REGISTRATIONS -> USERS -> TICKETS
     */
    public Cursor getRegistrationsByEventId(int eventId) {
        SQLiteDatabase db = this.getReadableDatabase();

        // R.id as _id est nécessaire pour les CursorAdapters/RecyclerView avec Cursor
        String query = "SELECT " +
                "R." + R_ID + " AS _id, " +
                "U." + COLUMN_NOM + ", " +
                "U." + COLUMN_PRENOM + ", " +
                "T." + T_TYPE + ", " +
                "R." + R_REGISTERED_AT + ", " +
                "R." + R_STATUS + " " +
                "FROM " + TABLE_REGISTRATIONS + " R " +
                "JOIN " + TABLE_USERS + " U ON R." + R_USER_ID + " = U." + COLUMN_ID +
                " JOIN " + TABLE_TICKETS + " T ON R." + R_TICKET_ID + " = T." + T_ID +
                " WHERE R." + R_EVENT_ID + " = ?";

        String[] selectionArgs = {String.valueOf(eventId)};

        // Exécute la requête brute
        Cursor cursor = db.rawQuery(query, selectionArgs);
        return cursor;
    }

    /**
     * Met à jour les détails d'un événement existant.
     */
    public boolean updateEvent(int eventId, String title, String description, String location, String startDate, String endDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(E_TITLE, title);
        values.put(E_DESCRIPTION, description);
        values.put(E_LOCATION, location);
        values.put(E_START_DATE, startDate);
        values.put(E_END_DATE, endDate);

        // Le critère de sélection est l'ID de l'événement
        String selection = E_ID + " = ?";
        String[] selectionArgs = {String.valueOf(eventId)};

        // Exécute la mise à jour et retourne le nombre de lignes affectées
        int rowsAffected = db.update(TABLE_EVENTS, values, selection, selectionArgs);
        db.close();

        // Si rowsAffected > 0, la mise à jour a réussi
        return rowsAffected > 0;
    }

    /**
     * Met à jour un billet existant.
     */
    public boolean updateTicket(int ticketId, String type, String description, double price, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(T_TYPE, type);
        values.put(T_DESCRIPTION, description);
        values.put(T_PRICE, price);
        values.put(T_QUANTITY, quantity);

        String selection = T_ID + " = ?";
        String[] selectionArgs = {String.valueOf(ticketId)};

        int rowsAffected = db.update(TABLE_TICKETS, values, selection, selectionArgs);
        db.close();

        return rowsAffected > 0;
    }

    /**
     * Supprime un billet par son ID.
     */
    public boolean deleteTicket(int ticketId) {
        SQLiteDatabase db = this.getWritableDatabase();

        String selection = T_ID + " = ?";
        String[] selectionArgs = {String.valueOf(ticketId)};

        // On suppose que la suppression du billet est bloquée si des inscriptions y sont liées (gestion par la DB)
        int rowsAffected = db.delete(TABLE_TICKETS, selection, selectionArgs);
        db.close();

        return rowsAffected > 0;
    }

    /**
     * Récupère un seul billet par son ID.
     */
    public Cursor getTicketById(int ticketId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {T_ID, T_EVENT_ID, T_TYPE, T_DESCRIPTION, T_PRICE, T_QUANTITY};
        String selection = T_ID + " = ?";
        String[] selectionArgs = {String.valueOf(ticketId)};

        Cursor cursor = db.query(TABLE_TICKETS, columns, selection, selectionArgs, null, null, null);

        // Note: on ne ferme pas la DB ici, c'est au code appelant de fermer le curseur
        return cursor;
    }

    // Dans DBHelper.java, ajoutez cette méthode :

    /**
     * Récupère tous les billets liés à un événement.
     */
    public Cursor getTicketsByEventId(int eventId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {T_ID, T_EVENT_ID, T_TYPE, T_DESCRIPTION, T_PRICE, T_QUANTITY};
        String selection = T_EVENT_ID + " = ?";
        String[] selectionArgs = {String.valueOf(eventId)};

        // Retourne tous les billets pour cet événement
        return db.query(TABLE_TICKETS, columns, selection, selectionArgs, null, null, T_PRICE + " ASC");
    }

    /**
     * Compte le nombre total d'utilisateurs.
     */
    public int getTotalUsersCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_USERS, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    /**
     * Compte le nombre total d'événements.
     */
    public int getTotalEventsCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_EVENTS, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    /**
     * Compte le nombre total d'inscriptions.
     */
    public int getTotalRegistrationsCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_REGISTRATIONS, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    /**
     * Récupère tous les utilisateurs (ID, Nom, Prénom, Email, Rôle).
     */
    public Cursor getAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        // Exclusion du mot de passe pour la sécurité
        String[] columns = {COLUMN_ID, COLUMN_NOM, COLUMN_PRENOM, COLUMN_EMAIL, COLUMN_ROLE};

        // Trier par Rôle puis par Nom pour une meilleure lisibilité
        return db.query(TABLE_USERS, columns, null, null, null, null, COLUMN_ROLE + " DESC, " + COLUMN_NOM + " ASC");
    }

    /**
     * Met à jour le rôle d'un utilisateur spécifique.
     */
    public boolean updateUserRole(int userId, String newRole) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_ROLE, newRole);

        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(userId)};

        int rowsAffected = db.update(TABLE_USERS, values, selection, selectionArgs);
        db.close();
        return rowsAffected > 0;
    }
}