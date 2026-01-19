package com.example.foundit

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView // Import BottomNavigationView

class MyItemsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var itemAdapter: ItemAdapter
    private lateinit var itemViewModel: ItemViewModel
    private lateinit var bottomNavigationView: BottomNavigationView // Declare BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_myitemsactivity)

        recyclerView = findViewById(R.id.recycler_view_my_items)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Pass the onDeleteItemClickListener and new onMarkAsClaimedClickListener to the adapter
        itemAdapter = ItemAdapter(
            emptyList(), 
            onFoundItClickListener = { item -> /* onFoundItClickListener - not used in MyItemsActivity, but required by adapter */ }, 
            onItsMineClickListener = { item -> /* onItsMineClickListener - not used in MyItemsActivity, but required by adapter */ },
            onDeleteItemClickListener = { item -> // onDeleteItemClickListener
                itemViewModel.deleteItem(item)
            },
            onMarkAsClaimedClickListener = { item -> // NEW: Handles marking item as claimed
                item.status = "claimed"
                itemViewModel.update(item)
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

        // Initialize BottomNavigationView and set listener
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_my_items // Set My Items as selected
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_dashboard -> {
                    startActivity(Intent(this, DashboardActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_my_items -> {
                    // Already on MyItemsActivity, do nothing or refresh if needed
                    true
                }
                else -> false
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // For an Application-scoped ViewModel, the listener should ideally not be stopped by individual activities,
        // unless the app is truly shutting down. We'll manage the listener from MainActivity for consistency.
        // itemViewModel.stopObservingItems() // Removed to avoid conflicting with global listener management
    }
}
