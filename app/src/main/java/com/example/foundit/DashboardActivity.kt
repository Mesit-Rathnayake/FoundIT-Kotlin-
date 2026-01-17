package com.example.foundit

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DashboardActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var itemAdapter: ItemAdapter
    private lateinit var itemViewModel: ItemViewModel
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("DashboardActivity", "onCreate started") // ADDED THIS LINE
        setContentView(R.layout.activity_dashboard)

        searchView = findViewById(R.id.search_view)
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
            }
        )
        recyclerView.adapter = itemAdapter

        itemViewModel = ViewModelProvider(this).get(ItemViewModel::class.java)
        itemViewModel.allItems.observe(this) { items ->
            Log.d("DashboardActivity", "Received items update. Count: ${items.size}")
            itemAdapter.setItems(items)
        }

        // Temporarily comment out search setup to rule out interference
        // setupSearch()
    }

    private fun setupSearch() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                itemAdapter.filter(newText ?: "")
                return true
            }
        })
    }
}
