package com.Cabmeri3.Nasxil.data.local

import android.content.Context
import android.content.SharedPreferences

class RunPreferences(context: Context) {

    private val store: SharedPreferences =
        context.getSharedPreferences("rooster_signal_run", Context.MODE_PRIVATE)

    fun int(key: String, fallback: Int): Int = store.getInt(key, fallback)

    fun putInt(key: String, value: Int) {
        store.edit().putInt(key, value).apply()
    }

    fun bool(key: String, fallback: Boolean): Boolean = store.getBoolean(key, fallback)

    fun putBool(key: String, value: Boolean) {
        store.edit().putBoolean(key, value).apply()
    }

    fun text(key: String, fallback: String): String = store.getString(key, fallback) ?: fallback

    fun putText(key: String, value: String) {
        store.edit().putString(key, value).apply()
    }

    fun remove(keys: List<String>) {
        val editor = store.edit()
        keys.forEach { editor.remove(it) }
        editor.apply()
    }
}
