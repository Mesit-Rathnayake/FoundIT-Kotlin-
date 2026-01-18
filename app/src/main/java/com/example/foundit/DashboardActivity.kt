package com.example.foundit

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView // Import SearchView
import androidx.core.content.ContextCompat // Import ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DashboardActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var itemAdapter: ItemAdapter
    private lateinit var itemViewModel: ItemViewModel
    private lateinit var searchView: SearchView // Declare SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        recyclerView = findViewById(R.id.recycler_view_items)
        recyclerView.layoutManager = LinearLayoutManager(this)

        itemAdapter = ItemAdapter(emptyList(),
            { item -> // onFoundItClickListener
                item.status = "found"
                itemViewModel.update(item)
            },
            { item -> // onItsMineClickListener
                item.status = "claimed"
                itemViewModel.update(item)
            },
            { item -> /* onDeleteItemClickListener - no-op for DashboardActivity */ }
        )
        recyclerView.adapter = itemAdapter

        // Use Application-scoped ViewModel for consistent listener management
        itemViewModel = ViewModelProvider.AndroidViewModelFactory
            .getInstance(application)
            .create(ItemViewModel::class.java)
        itemViewModel.allItems.observe(this) {
            items -> itemAdapter.setItems(items)
        }

        // Ensure the listener is started when this activity is created
        // This handles cases where DashboardActivity might be launched directly or after MyItemsActivity has stopped the listener
        itemViewModel.startObservingItems()

        // Initialize SearchView
        searchView = findViewById(R.id.search_view)
        
        // Set text color for the SearchView's internal EditText
        val searchEditText: EditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text)
        searchEditText.setTextColor(ContextCompat.getColor(this, android.R.color.white))
        searchEditText.setHintTextColor(ContextCompat.getColor(this, android.R.color.white))

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                itemAdapter.filter(newText.orEmpty())
                return true
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        // For an Application-scoped ViewModel, the listener should ideally not be stopped by individual activities,
        // unless the app is truly shutting down. However, given MainActivity also stops it on logout,
        // we'll keep a consistent approach. We'll adjust MainActivity next.
        // For now, let's ensure it's not stopped here, to avoid conflicting with MainActivity's potential management.
        // itemViewModel.stopObservingItems() // Removed to allow consistent management from MainActivity or app-level
    }
}
