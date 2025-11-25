package com.example.eventzen;

import android.content.Context;
import android.content.SharedPreferences;

public class UserSessionManager {

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;

    // Nom du fichier SharedPreferences
    private static final String PREF_NAME = "EventAppPref";
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USER_ROLE = "user_role";

    public UserSessionManager(Context context) {
        this._context = context;
        // Mode privé : seul cette application peut y accéder
        pref = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    /**
     * Crée une session de connexion.
     */
    public void createLoginSession(int userId, String role) {
        editor.putBoolean(IS_LOGIN, true);
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_USER_ROLE, role);
        editor.commit(); // Applique les changements
    }

    /**
     * Vérifie si l'utilisateur est connecté.
     */
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }

    /**
     * Efface les données de session (Déconnexion).
     */
    public void logoutUser() {
        editor.clear();
        editor.commit();

        // Optionnel : Rediriger vers la page de connexion après déconnexion
        // Intent i = new Intent(_context, LoginActivity.class);
        // ... (ajouter les flags et startActivity)
    }

    /**
     * Récupère l'ID de l'utilisateur connecté.
     */
    public int getCurrentUserId() {
        return pref.getInt(KEY_USER_ID, -1);
    }

    /**
     * Récupère le rôle de l'utilisateur connecté.
     */
    public String getCurrentUserRole() {
        return pref.getString(KEY_USER_ROLE, null);
    }
}