package com.example.foundit // <-- CORRECTED to match your project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log // This is used to print messages to the "Logcat" window
import com.example.foundit.models.Item // <-- CORRECTED to match your project
import com.example.foundit.models.User // <-- CORRECTED to match your project
import java.util.Date

class MainActivity : AppCompatActivity() {

    // This is where you can store your "current user"
    // We'll make it nullable (null) for now.
    private var currentUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // --- THIS IS WHERE YOUR "FUNCTIONS" START ---

        // Let's test by creating a sample user and logging them in.
        // This is what your login() function will do.
        currentUser = User(
            userId = "EG-2022-5435",
            name = "S.T. Yapa",
            email = "yapa@example.com",
            phone = "123456789",
            role = "student"
        )

        // Now, let's test the postItem() idea.
        // First, we create a new item object:
        val newItem = Item(
            itemId = "item123",
            title = "Found: Blue Water Bottle",
            description = "Found a blue water bottle near the library.",
            category = "accessories",
            type = "found",
            location = "Library Entrance",
            date = Date() // This sets the date to *now*
        )

        // Now, we call a function to "post" it.
        // This is the function from your diagram!
        postItem(newItem)
    }

    /**
     * This is the 'postItem()' function from your class diagram.
     * It lives inside your MainActivity, not the User class.
     * It takes the Item to be posted as a parameter.
     */
    private fun postItem(itemToPost: Item) {
        // Check if a user is actually logged in
        if (currentUser != null) {
            Log.d("MainActivity", "User ${currentUser!!.name} is posting item: ${itemToPost.title}")

            // --- THIS IS WHERE YOUR REAL LOGIC GOES ---
            // In the future, you will add code here to:
            // 1. Save 'itemToPost' to your Firebase/database.
            // 2. Update the user's list of posted items.
            // 3. Show a "Success!" message on the screen.

        } else {
            Log.e("MainActivity", "Cannot post item. No user is logged in.")
        }
    }

    /**
     * This is the 'claimItem()' function from your class diagram.
     *
     * In the future, you would call this when a user
     * clicks a "Claim" button on an item.
     */
    private fun claimItem(itemToClaim: Item) {
        if (currentUser != null) {
            Log.d("MainActivity", "User ${currentUser!!.name} is claiming item: ${itemToClaim.title}")

            // --- THIS IS WHERE YOUR REAL LOGIC GOES ---
            // 1. Create a new Claim object.
            // 2. Save that Claim to your database.
            // 3. Send a notification to the item's poster.
            // 4. Update the item's status.

        } else {
            Log.e("MainActivity", "Cannot claim item. No user is logged in.")
        }
    }
}
