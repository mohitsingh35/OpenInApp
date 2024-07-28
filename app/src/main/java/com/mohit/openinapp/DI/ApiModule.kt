package com.mohit.openinapp.DI

import android.content.Context
import com.mohit.openinapp.API.ApiService
import com.mohit.openinapp.API.AuthInterceptor
import com.mohit.openinapp.HelperClasses.PrefManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun providePrefManager(@ApplicationContext context: Context): PrefManager {
        return PrefManager(context)
    }

    @Provides
    @Singleton
    fun provideOkHTTPClient(authInterceptor: AuthInterceptor): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .addInterceptor(authInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(okHttpClient: OkHttpClient, prefManager: PrefManager): ApiService {
        return Retrofit.Builder()
            .baseUrl(prefManager.getBaseUrl()!!)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

}