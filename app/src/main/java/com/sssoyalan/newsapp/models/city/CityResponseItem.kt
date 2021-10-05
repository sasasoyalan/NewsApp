package com.sssoyalan.newsapp.models.city

data class Cities (
    val cities: List<CityResponseItem>
)

data class CityResponseItem(
    val coord: Coord,
    val country: String,
    val id: Int,
    val name: String,
    val state: String
)