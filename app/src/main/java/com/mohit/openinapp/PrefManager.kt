package com.mohit.openinapp

import android.content.Context
import android.content.SharedPreferences

object PrefManager {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor : SharedPreferences.Editor

    fun initialize(context: Context) {
        sharedPreferences = context.getSharedPreferences(
            "Open In App",
            Context.MODE_PRIVATE
        )
        editor = sharedPreferences.edit()
    }

    fun getBearerToken(): String? {
        return sharedPreferences.getString("bearer_token", BuildConfig.BEARER_TOKEN)
    }

    fun setBearerToken(newToken:String){
        editor.putString("bearer_token", newToken)
        editor.apply()
    }

}