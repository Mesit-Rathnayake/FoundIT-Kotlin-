package com.example.foundit

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class PostFoundItemActivity : AppCompatActivity() {

    private lateinit var itemImageView: ImageView
    private var selectedImageUri: Uri? = null

    // ActivityResultLauncher for picking an image
    private val pickImageLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                selectedImageUri = uri
                itemImageView.setImageURI(selectedImageUri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // This links the activity to its layout file
        setContentView(R.layout.activity_post_found_item)

        // Get references to the views from the layout
        val itemTitleEditText = findViewById<EditText>(R.id.edit_text_item_title)
        val itemDescriptionEditText = findViewById<EditText>(R.id.edit_text_item_description)
        val itemLocationEditText = findViewById<EditText>(R.id.edit_text_item_location)
        itemImageView = findViewById(R.id.image_view_item_photo)
        val addPhotoButton = findViewById<Button>(R.id.button_add_photo)
        val postItemButton = findViewById<Button>(R.id.button_post_item)

        // Set a click listener for the "Add Photo" button
        addPhotoButton.setOnClickListener {
            // Launch the image picker
            pickImageLauncher.launch("image/*")
        }

        // Set a click listener for the "Post Found Item" button
        postItemButton.setOnClickListener {
            // Get the text the user entered
            val title = itemTitleEditText.text.toString()
            val description = itemDescriptionEditText.text.toString()
            val location = itemLocationEditText.text.toString()

            // For now, we'll just show a Toast message to confirm it works
            // In the future, this will save the data to a database
            if (title.isNotEmpty() && location.isNotEmpty()) {
                val message = "Posting: '$title' found at '$location'"
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()

                // TODO: Add code to save the item (including the image URI) to a database

                // Close this screen and go back to the main screen
                finish()
            } else {
                // Show an error if the title or location is empty
                Toast.makeText(this, "Please fill in at least the title and location.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
