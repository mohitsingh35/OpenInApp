package com.mohit.openinapp.API

import com.mohit.openinapp.Models.LinksData
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("api/v1/dashboardNew")
    suspend fun getLinksData(): Response<LinksData>
}