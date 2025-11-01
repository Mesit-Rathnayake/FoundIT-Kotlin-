package com.example.foundit.models


data class User(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    var phone: String = "",
    val role: String = "student"
)
