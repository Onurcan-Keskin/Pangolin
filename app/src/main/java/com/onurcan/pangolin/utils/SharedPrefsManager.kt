package com.onurcan.pangolin.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPrefsManager(context: Context) {
    private var mSharedPreferences: SharedPreferences =
        context.getSharedPreferences("filename", Context.MODE_PRIVATE)
    private var mSharedLanguagePrefs: SharedPreferences =
        context.getSharedPreferences("languageName", Context.MODE_PRIVATE)

    fun setNightModeState(state: Boolean) {
        val editor = mSharedPreferences.edit()
        editor.putBoolean("NightMode", state)
        editor.commit()
    }

    fun loadNightModeState(): Boolean {
        return mSharedPreferences.getBoolean("NightMode", false)
    }

    fun setLanguage(state: String) {
        val editor = mSharedLanguagePrefs.edit()
        editor.putString("tr", state)
        editor.commit()
    }

    fun loadLanguage(): String? {
        return mSharedLanguagePrefs.getString("tr","en")
    }
}