package com.example.animehub.data

import com.example.animehub.Model.AnimeResponse
import com.example.animehub.Network.AnimeApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnimeRepository @Inject constructor(
    private val apiService:AnimeApiService
) {
    suspend fun getAnime(query: String,sfw:Boolean):AnimeResponse?{
        return apiService.getAnime(query,sfw)
    }
}