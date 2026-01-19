package com.example.foundit

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var itemViewModel: ItemViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        itemViewModel = ViewModelProvider.AndroidViewModelFactory
            .getInstance(application)
            .create(ItemViewModel::class.java)

        val currentUser = auth.currentUser
        if (currentUser != null) {
            Log.d("MainActivity", "Current user UID: ${currentUser.uid}")
        } else {
            Log.d("MainActivity", "No user is logged in.")
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }

        itemViewModel.startObservingItems()

        val lostButton = findViewById<Button>(R.id.lost_button)
        val foundButton = findViewById<Button>(R.id.found_button)
        val dashboardButton = findViewById<Button>(R.id.dashboard_button)
        val myItemsButton = findViewById<Button>(R.id.view_my_items_button)
        val logoutButton = findViewById<Button>(R.id.logout_button)

        val chatFab = findViewById<FloatingActionButton>(R.id.chat_fab)

        lostButton.setOnClickListener {
            startActivity(Intent(this, PostLostItemActivity::class.java))
        }

        foundButton.setOnClickListener {
            startActivity(Intent(this, PostFoundItemActivity::class.java))
        }

        dashboardButton.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
        }

        myItemsButton.setOnClickListener {
            startActivity(Intent(this, MyItemsActivity::class.java))
        }

        chatFab.setOnClickListener {
            // Changed to launch ChatListActivity
            startActivity(Intent(this, ChatListActivity::class.java))
        }

        logoutButton.setOnClickListener {
            itemViewModel.stopObservingItems()
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
