package com.example.eduskunta

// 28.09.2024 by Arman Yerkeshev 2214297
// This class represents single data unit, fetched from the server as JSON
data class ParliamentMemberJSON(
    val hetekaId: Int,
    val firstname: String,
    val lastname: String,
    val party: String,
    val minister: Boolean,
    val pictureUrl: String,
    var twitter: String? = null,
    var bornYear: String? = null,
    var constituency: String? = null
)