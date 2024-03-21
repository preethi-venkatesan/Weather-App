package com.example.weatherapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

class SharePrefs internal constructor(private val context: Context) {

    companion object {
        private const val SHARED_PREFS_NAME = "my_prefs"
        private const val KEY_CITY = "city"


        @SuppressLint("StaticFieldLeak")
        private var instance: SharePrefs? = null

        fun getInstance(context: Context): SharePrefs {
            if (instance == null)
                instance = SharePrefs(context.applicationContext)

            return instance!!
        }
}

private val prefs: SharedPreferences by lazy {
    context.getSharedPreferences(SHARED_PREFS_NAME,MODE_PRIVATE)
}

fun setValue(key: String, value: String) {
    prefs.edit().putString(key, value)
}

fun getValue(key: String): String? {
    return prefs.getString(key, null)
}

fun getValueOrNull(key: String?, value: String?) {
    if (key == null && value == null) {

        prefs.edit().putString(key, value).apply()
    }
}

fun getValueOrNull(key: String?): String? {
    if (key != null) {

        return prefs.getString(key, null)
    }
    return null
}

// clear the sharePrefs
fun clearCityValue() {
    prefs.edit().remove(KEY_CITY).apply()
}
}