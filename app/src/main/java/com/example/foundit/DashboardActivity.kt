package com.example.foundit

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foundit.models.Item
import java.util.Date

class DashboardActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var itemAdapter: ItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        recyclerView = findViewById(R.id.recycler_view_items)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Create some dummy data to display
        val dummyItems = listOf(
            Item("item1", "Lost Wallet", "Black leather wallet", "accessories", "lost", "Library", Date()),
            Item("item2", "Found Keys", "Keys on a red lanyard", "keys", "found", "Cafeteria", Date()),
            Item("item3", "Lost Phone", "iPhone 13 with a blue case", "electronics", "lost", "Gym", Date())
        )

        itemAdapter = ItemAdapter(dummyItems)
        recyclerView.adapter = itemAdapter
    }
}
