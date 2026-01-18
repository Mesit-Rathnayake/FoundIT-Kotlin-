package com.example.foundit

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MyItemsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var itemAdapter: ItemAdapter
    private lateinit var itemViewModel: ItemViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_myitemsactivity)

        recyclerView = findViewById(R.id.recycler_view_my_items)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Pass the onDeleteItemClickListener to the adapter
        itemAdapter = ItemAdapter(
            emptyList(), 
            { item -> /* onFoundItClickListener - not used in MyItemsActivity, but required by adapter */ }, 
            { item -> /* onItsMineClickListener - not used in MyItemsActivity, but required by adapter */ },
            { item -> // onDeleteItemClickListener
                itemViewModel.deleteItem(item)
            }
        )
        recyclerView.adapter = itemAdapter

        // ✅ Shared ViewModel across the app using Application scope
        itemViewModel = ViewModelProvider.AndroidViewModelFactory
            .getInstance(application)
            .create(ItemViewModel::class.java)

        // Observe my items
        itemViewModel.myItems.observe(this) { items ->
            itemAdapter.setItems(items)
        }

        // Start observing items
        itemViewModel.startObservingItems()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Stop observing items when activity is destroyed
        itemViewModel.stopObservingItems()
    }
}
