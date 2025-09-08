package de.clemensbartz.android.launcher.daos;

import android.content.SharedPreferences;

/* loaded from: classes.dex */
public final class SharedPreferencesDAO {
    private static final String KEY_VERSION = "version";
    private static final int VERSION = 1;
    private static SharedPreferencesDAO instance;
    private final SharedPreferences preferences;

    public static synchronized SharedPreferencesDAO getInstance(SharedPreferences sharedPreferences) {
        synchronized (SharedPreferencesDAO.class) {
            if (instance != null && instance.getPreferences() == sharedPreferences) {
                return instance;
            }
            instance = new SharedPreferencesDAO(sharedPreferences);
            return instance;
        }
    }

    private SharedPreferencesDAO(SharedPreferences sharedPreferences) {
        this.preferences = sharedPreferences;
    }

    private SharedPreferences getPreferences() {
        return this.preferences;
    }

    public void loadValues() {
        if (this.preferences.getInt(KEY_VERSION, 0) != 1) {
            this.preferences.edit().clear().apply();
            this.preferences.edit().putInt(KEY_VERSION, 1).apply();
        }
    }

    public int getInt(String str, int i) {
        return this.preferences.getInt(str, i);
    }

    public void putInt(String str, int i) {
        this.preferences.edit().putInt(str, i).apply();
    }

    public String getString(String str, String str2) {
        return this.preferences.getString(str, str2);
    }

    public void putString(String str, String str2) {
        this.preferences.edit().putString(str, str2).apply();
    }

    public boolean getBoolean(String str, boolean z) {
        return this.preferences.getBoolean(str, z);
    }

    public void putBoolean(String str, boolean z) {
        this.preferences.edit().putBoolean(str, z).apply();
    }

    public boolean contains(String str) {
        return this.preferences.contains(str);
    }

    public void remove(String str) {
        this.preferences.edit().remove(str).apply();
    }
}
