package com.example.socialmediaapp.data;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesHelper {

    private static final String PREFS_NAME = "MyPreferences";
    private final SharedPreferences sharedPreferences;

    public SharedPreferencesHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveSelectedOption(String selectedOption) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("selectedOption", selectedOption);
        editor.apply();
    }

    public String getSelectedOption() {
        return sharedPreferences.getString("selectedOption", "Bayern Munchen");
    }
}
