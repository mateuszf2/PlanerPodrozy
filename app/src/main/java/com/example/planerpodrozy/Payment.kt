package com.example.planerpodrozy

data class Payment(
    val eventId: String,
    val amountPayment: Double,
    val friendEmail: String,
    val userId: String
)