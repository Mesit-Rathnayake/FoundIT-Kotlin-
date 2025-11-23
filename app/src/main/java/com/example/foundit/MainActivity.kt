package com.example.foundit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.example.foundit.models.Item
import com.example.foundit.models.User
import java.util.Date
import android.content.Intent // Make sure this import is here

class MainActivity : AppCompatActivity() {

    private var currentUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // --- Step 1: Find the buttons from our layout ---
        val lostButton = findViewById<Button>(R.id.lost_button)
        val foundButton = findViewById<Button>(R.id.found_button)

        // --- Step 2: Set click listeners for each button ---

        lostButton.setOnClickListener {
            // This is the updated part: It now opens the new screen.
            val intent = Intent(this, PostLostItemActivity::class.java)
            startActivity(intent)
        }

        foundButton.setOnClickListener {
            // This code runs when the "I Found Something" button is clicked
            Toast.makeText(this, "The 'I Found Something' button was clicked!", Toast.LENGTH_SHORT).show()

            // In the future, this will navigate to a new screen
            // to create a "found item" post.
        }


        // We are moving the test logic out of onCreate so it doesn't run automatically.
        // You can call these test functions from a button click if you want.
        // testPostingAnItem()
    }

    /**
     * A test function to simulate a user posting an item.
     * You can call this from a button listener to test it.
     */
    private fun testPostingAnItem() {
        // Log in a sample user
        currentUser = User(
            userId = "EG-2022-5435",
            name = "S.T. Yapa",
            email = "yapa@example.com",
            phone = "123456789",
            role = "student"
        )

        // Create a new item object
        val newItem = Item(
            itemId = "item123",
            title = "Found: Blue Water Bottle",
            description = "Found a blue water bottle near the library.",
            category = "accessories",
            type = "found",
            location = "Library Entrance",
            date = Date()
        )

        // Call the postItem function
        postItem(newItem)
    }

    /**
     * This is the 'postItem()' function from your class diagram.
     */
    private fun postItem(itemToPost: Item) {
        if (currentUser != null) {
            Log.d("MainActivity", "User ${currentUser!!.name} is posting item: ${itemToPost.title}")
            // In the future, you will add code here to save to a database.
        } else {
            Log.e("MainActivity", "Cannot post item. No user is logged in.")
        }
    }

    /**
     * This is the 'claimItem()' function from your class diagram.
     */
    private fun claimItem(itemToClaim: Item) {
        if (currentUser != null) {
            Log.d("MainActivity", "User ${currentUser!!.name} is claiming item: ${itemToClaim.title}")
            // In the future, you will add code here to save a claim to a database.
        } else {
            Log.e("MainActivity", "Cannot claim item. No user is logged in.")
        }
    }
}
