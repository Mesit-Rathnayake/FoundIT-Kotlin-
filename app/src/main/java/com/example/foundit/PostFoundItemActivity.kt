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
import androidx.lifecycle.ViewModelProvider
import com.example.foundit.models.Item
import com.google.firebase.auth.FirebaseAuth
import java.util.Date

class PostFoundItemActivity : AppCompatActivity() {

    private lateinit var itemImageView: ImageView
    private var selectedImageUri: Uri? = null
    private lateinit var itemViewModel: ItemViewModel
    private lateinit var auth: FirebaseAuth

    private val pickImageLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                selectedImageUri = uri
                itemImageView.setImageURI(selectedImageUri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_found_item)

        itemViewModel = ViewModelProvider(this).get(ItemViewModel::class.java)
        auth = FirebaseAuth.getInstance()

        val itemTitleEditText = findViewById<EditText>(R.id.edit_text_item_title)
        val itemDescriptionEditText = findViewById<EditText>(R.id.edit_text_item_description)
        val itemLocationEditText = findViewById<EditText>(R.id.edit_text_item_location)
        itemImageView = findViewById(R.id.image_view_item_photo)
        val addPhotoButton = findViewById<Button>(R.id.button_add_photo)
        val postItemButton = findViewById<Button>(R.id.button_post_item)

        addPhotoButton.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        postItemButton.setOnClickListener {
            val title = itemTitleEditText.text.toString()
            val description = itemDescriptionEditText.text.toString()
            val location = itemLocationEditText.text.toString()
            val userId = auth.currentUser?.uid

            if (title.isNotEmpty() && location.isNotEmpty() && userId != null) {
                val newItem = Item(
                    title = title,
                    description = description,
                    location = location,
                    type = "found",
                    date = Date(),
                    userId = userId
                )
                
                itemViewModel.insert(newItem, selectedImageUri)
                
                Toast.makeText(this, "Posting your found item...", Toast.LENGTH_SHORT).show()
                finish()
            } else if (userId == null) {
                 Toast.makeText(this, "You must be logged in to post an item.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please fill in title and location.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
