package com.mohit.openinapp

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers

interface ApiService {
    @Headers("Authorization: Bearer ${BuildConfig.BEARER_TOKEN}")
    @GET("api/v1/dashboardNew")
    suspend fun getLinksData(): Response<LinksData>
}