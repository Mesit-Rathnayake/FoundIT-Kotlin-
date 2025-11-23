package com.example.foundit

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PostLostItemActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // This links the activity to its layout file
        setContentView(R.layout.activity_post_lost_item)

        // Get references to the views from the layout
        val itemTitleEditText = findViewById<EditText>(R.id.edit_text_item_title)
        val itemDescriptionEditText = findViewById<EditText>(R.id.edit_text_item_description)
        val itemLocationEditText = findViewById<EditText>(R.id.edit_text_item_location)
        val postItemButton = findViewById<Button>(R.id.button_post_item)

        // Set a click listener for the "Post Lost Item" button
        postItemButton.setOnClickListener {
            // Get the text the user entered
            val title = itemTitleEditText.text.toString()
            val description = itemDescriptionEditText.text.toString()
            val location = itemLocationEditText.text.toString()

            // For now, we'll just show a Toast message to confirm it works
            // In the future, this will save the data to a database
            if (title.isNotEmpty() && location.isNotEmpty()) {
                val message = "Posting: '$title' last seen at '$location'"
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()

                // TODO: Add code to save the item to a database

                // Close this screen and go back to the main screen
                finish()
            } else {
                // Show an error if the title or location is empty
                Toast.makeText(this, "Please fill in at least the title and location.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
