package com.example.foundit

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView // Import SearchView
import androidx.core.content.ContextCompat // Import ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView // Import BottomNavigationView

class DashboardActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var itemAdapter: ItemAdapter
    private lateinit var itemViewModel: ItemViewModel
    private lateinit var searchView: SearchView // Declare SearchView
    private lateinit var bottomNavigationView: BottomNavigationView // Declare BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        recyclerView = findViewById(R.id.recycler_view_items)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize ItemViewModel before ItemAdapter
        itemViewModel = ViewModelProvider.AndroidViewModelFactory
            .getInstance(application)
            .create(ItemViewModel::class.java)

        itemAdapter = ItemAdapter(
            emptyList(),
            onFoundItClickListener = { item -> // Found It now only initiates chat; status change is separate
                // You might want to log something here or keep it empty if no specific action needed in DashboardActivity
                // For example:
                // Log.d("DashboardActivity", "Found It clicked for item: ${item.title}")
            },
            onItsMineClickListener = { item -> // It's Mine now only initiates chat; status change is separate
                // Similar to onFoundItClickListener, keep it empty or log
                // Log.d("DashboardActivity", "It's Mine clicked for item: ${item.title}")
            },
            onDeleteItemClickListener = { item -> // Delete remains the same
                itemViewModel.deleteItem(item)
            },
            onMarkAsClaimedClickListener = { item -> // NEW: This handles the status change to 'claimed'
                item.status = "claimed"
                itemViewModel.update(item)
            }
        )
        recyclerView.adapter = itemAdapter

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

        // Initialize BottomNavigationView and set listener
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_dashboard // Set Dashboard as selected
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_dashboard -> {
                    // Already on DashboardActivity, do nothing or refresh if needed
                    true
                }
                R.id.nav_my_items -> {
                    startActivity(Intent(this, MyItemsActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
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