package com.example.betlink.data;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Persists user session across app restarts using SharedPreferences.
 * Must be initialised once with a Context (e.g. from Application.onCreate).
 */
public class SessionManager {

    private static final String PREFS_NAME   = "betlink_session";
    private static final String KEY_TOKEN    = "access_token";
    private static final String KEY_USER_ID  = "user_id";
    private static final String KEY_EMAIL    = "user_email";
    private static final String KEY_NAME     = "user_name";
    private static final String KEY_ROLE     = "user_role";

    private static SessionManager instance;
    private SharedPreferences prefs;

    private SessionManager() { }

    /** Call once from Application.onCreate() or any Activity before first use. */
    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void init(Context context) {
        if (prefs == null) {
            prefs = context.getApplicationContext()
                           .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        }
    }

    // -------------------------------------------------------------------------
    // Save / clear
    // -------------------------------------------------------------------------

    public void saveSession(String accessToken, String userId, String email,
                            String fullName, String role) {
        ensureInit();
        prefs.edit()
             .putString(KEY_TOKEN,   accessToken)
             .putString(KEY_USER_ID, userId)
             .putString(KEY_EMAIL,   email)
             .putString(KEY_NAME,    fullName)
             .putString(KEY_ROLE,    role)
             .apply();
    }

    public void clear() {
        ensureInit();
        prefs.edit().clear().apply();
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    public boolean isLoggedIn() {
        ensureInit();
        return getAccessToken() != null;
    }

    public String getAccessToken() {
        ensureInit();
        return prefs.getString(KEY_TOKEN, null);
    }

    public String getUserId() {
        ensureInit();
        return prefs.getString(KEY_USER_ID, null);
    }

    public String getUserEmail() {
        ensureInit();
        return prefs.getString(KEY_EMAIL, null);
    }

    public String getUserName() {
        ensureInit();
        String name = prefs.getString(KEY_NAME, null);
        return name != null ? name : "Guest";
    }

    public String getUserRole() {
        ensureInit();
        return prefs.getString(KEY_ROLE, "Traveler");
    }

    // -------------------------------------------------------------------------
    // Legacy compat (kept so old callers don't break)
    // -------------------------------------------------------------------------

    /** @deprecated use saveSession() instead */
    @Deprecated
    public void startSession(String name, String role) {
        ensureInit();
        prefs.edit()
             .putString(KEY_NAME, name)
             .putString(KEY_ROLE, role)
             .apply();
    }

    // -------------------------------------------------------------------------

    private void ensureInit() {
        if (prefs == null) {
            throw new IllegalStateException(
                "SessionManager not initialised. Call SessionManager.getInstance().init(context) first.");
        }
    }
}
