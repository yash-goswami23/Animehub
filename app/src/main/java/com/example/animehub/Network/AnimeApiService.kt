package com.example.animehub.Network

import com.example.animehub.Model.AnimeResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface AnimeApiService {
    @GET("anime")
    suspend fun getAnime(
        @Query("q") query: String,
        @Query("sfw") sfw: Boolean
    ):AnimeResponse?
}