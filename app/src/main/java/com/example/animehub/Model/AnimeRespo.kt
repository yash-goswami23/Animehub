package com.example.animehub.Model


data class AnimeResponse(
    val `data`: List<Anime>?,
    val pagination: Pagination?
)