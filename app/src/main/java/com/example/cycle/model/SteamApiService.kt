package com.example.cycle.model

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface SteamApiService {
    @GET("ISteamNews/GetNewsForApp/v0002/")
    suspend fun getNewsForApp(
        @Query("appid") appId: Int,
        @Query("count") count: Int = 3,
        @Query("maxlength") maxLength: Int = 300,
        @Query("format") format: String = "json"
    ): SteamNewsResponse

    companion object {
        private const val BASE_URL = "https://api.steampowered.com/"

        fun create(): SteamApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(SteamApiService::class.java)
        }
    }
}
