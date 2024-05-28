package com.example.planerpodrozy

data class Payment(
    val userEmail: String,
    val amountPayment: Double,
    val friendEmail: String,
    val userId: String
)