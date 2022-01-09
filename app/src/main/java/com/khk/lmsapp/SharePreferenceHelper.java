package com.khk.lmsapp;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePreferenceHelper {
    private SharedPreferences sharedPreference;

    private static String SHARE_PREFERENCE = "AppPref";
    public static final String KEY_LOGIN = "isLogin";
    public static final String KEY_ENTRY = "entry";
    public static final String KEY_PASSWORD = "password";
    public static final String IS_LOCK = "lock";

//	private SharePreferenceHelper instance;

    public SharePreferenceHelper(Context context) {
        sharedPreference = context.getSharedPreferences(SHARE_PREFERENCE, Context.MODE_PRIVATE);
    }

//	public synchronized SharePreferenceHelper getInstance(Context context) {
//		if (instance == null) {
//			instance = new SharePreferenceHelper(context);
//		}
//		return instance;
//	}

    public void delete(String key) {
        if (sharedPreference.contains(key)) {
            getEditor().remove(key).commit();
        }
    }

    public void save(String key, Object value) {
        SharedPreferences.Editor editor = getEditor();
        if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Enum) {
            editor.putString(key, value.toString());
        } else if (value != null) {
            throw new RuntimeException("Attempting to save non-supported preference");
        }

        editor.commit();
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) sharedPreference.getAll().get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, T defValue) {
        T returnValue = (T) sharedPreference.getAll().get(key);
        return returnValue == null ? defValue : returnValue;
    }

    private boolean has(String key) {
        return sharedPreference.contains(key);
    }

    private SharedPreferences.Editor getEditor() {
        return sharedPreference.edit();
    }

    public void logoutSharePreference() {
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.clear();
        editor.commit();
    }

    //////////////////////// END OF COMMON METHODS /////////////////////////
}
