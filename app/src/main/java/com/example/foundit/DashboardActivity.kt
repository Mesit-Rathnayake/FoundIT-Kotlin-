package com.example.foundit

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DashboardActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var itemAdapter: ItemAdapter
    private lateinit var itemViewModel: ItemViewModel

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

        )
        recyclerView.adapter = itemAdapter

        itemViewModel = ViewModelProvider(this).get(ItemViewModel::class.java)
        itemViewModel.allItems.observe(this) {
            items -> itemAdapter.setItems(items)
        }
    }
}
