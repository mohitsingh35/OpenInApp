package com.mohit.openinapp

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(private val prefManager: PrefManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = prefManager.getBearerToken()
        val newRequest = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()
        return chain.proceed(newRequest)
    }
}
