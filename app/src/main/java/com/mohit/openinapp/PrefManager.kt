package com.mohit.openinapp

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrefManager @Inject constructor(@ApplicationContext context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("Open In App", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    fun getBaseUrl(): String? {
        return sharedPreferences.getString("api_url", Constants.BASE_URL)
    }

    fun setBaseUrl(baseUrl: String) {
        editor.putString("api_url", baseUrl)
        editor.apply()
    }

    fun getBearerToken(): String? {
        return sharedPreferences.getString("bearer_token", Constants.BEARER_TOKEN)
    }

    fun setBearerToken(newToken: String) {
        editor.putString("bearer_token", newToken)
        editor.apply()
    }
}
