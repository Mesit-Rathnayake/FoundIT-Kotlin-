package com.example.foundit.models

import java.util.Date

data class Item(
    val itemId: String = "",
    var title: String = "",
    var description: String = "",
    var category: String = "",
    var type: String = "",
    var location: String = "",
    val date: Date? = null,
    var imageUrl: String? = null,
    var status: String = "active"
)
