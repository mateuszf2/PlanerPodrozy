package com.example.planerpodrozy

data class Planer(
    val data: String,
    val godzina: String,
    val nazwaAktywnosci: String,
    val eventId: String

)

data class PlanerDay(
    val data: String,
    val planerActivities: List<Planer>
)
