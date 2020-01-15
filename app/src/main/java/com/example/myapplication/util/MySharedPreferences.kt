package com.example.myapplication.util

import android.content.Context

class MySharedPreferences(context: Context) {
    val PREFS_KEY = "token"
    val prefs = context.getSharedPreferences(PREFS_KEY, 0)

    var myToken: String
        get() = prefs.getString(PREFS_KEY, "").toString()
        set(value) = prefs.edit().putString(PREFS_KEY, value).apply()

    var myAgree: String
        get() = prefs.getString("agree", "").toString()
        set(value) = prefs.edit().putString("agree", value).apply()
}