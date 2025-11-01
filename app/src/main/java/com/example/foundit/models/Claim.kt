package com.example.foundit.models

import java.util.Date


data class Claim(
    val claimId: String = "",
    val itemId: String = "",
    val claimedById: String = "",
    val claimDate: Date? = null,
    var status: String = "pending"
)
