package com.example.foundit

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var itemViewModel: ItemViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        itemViewModel = ViewModelProvider(this).get(ItemViewModel::class.java)

        // --- ADDED LOG FOR DEBUGGING --- START
        val currentUser = auth.currentUser
        if (currentUser != null) {
            Log.d("MainActivity", "Current user UID: ${currentUser.uid}")
        } else {
            Log.d("MainActivity", "No user is logged in.")
        }
        // --- ADDED LOG FOR DEBUGGING --- END

        // --- Check if user is logged in ---
        if (currentUser == null) {
            // Not logged in, redirect to LoginActivity
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return // Stop further execution of onCreate
        }

        // --- Start Observing Items ---
        itemViewModel.startObservingItems()


        // --- Step 1: Find the buttons from our layout ---
        val lostButton = findViewById<Button>(R.id.lost_button)
        val foundButton = findViewById<Button>(R.id.found_button)
        val dashboardButton = findViewById<Button>(R.id.dashboard_button)
        val logoutButton = findViewById<Button>(R.id.logout_button)


        // --- Step 2: Set click listeners for each button ---

        lostButton.setOnClickListener {
            val intent = Intent(this, PostLostItemActivity::class.java)
            startActivity(intent)
        }

        foundButton.setOnClickListener {
            val intent = Intent(this, PostFoundItemActivity::class.java)
            startActivity(intent)
        }

        dashboardButton.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
        }

        logoutButton.setOnClickListener {
            itemViewModel.stopObservingItems()
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
