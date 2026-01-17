package com.example.foundit.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "items")
data class Item(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var documentId: String = "",
    var title: String = "",
    var description: String = "",
    var category: String = "",
    var type: String = "",
    var location: String = "",
    var date: Date? = null,
    var imageUrl: String? = null,
    var status: String = "active",
    var userId: String? = null
) {
    // Required empty constructor for Firestore
    constructor() : this(0, "", "", "", "", "", "", null, null, "active", null)
}
