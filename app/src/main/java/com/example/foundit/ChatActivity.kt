package com.example.foundit

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ChatActivity : AppCompatActivity() {

    private lateinit var chatRecycler: RecyclerView
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var messageInput: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var chatRef: DatabaseReference
    private val messagesList = mutableListOf<Message>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // Get item name from intent
        val itemName = intent.getStringExtra("ITEM_NAME") ?: "Unknown"

        // Set chat title
        val chatTitle = findViewById<TextView>(R.id.chat_title)
        chatTitle.text = "Chat about: $itemName"

        // Initialize views
        chatRecycler = findViewById(R.id.chat_recycler)
        messageInput = findViewById(R.id.chat_input)
        sendButton = findViewById(R.id.chat_send_button)

        // Setup RecyclerView
        chatAdapter = ChatAdapter(messagesList)
        chatRecycler.adapter = chatAdapter
        chatRecycler.layoutManager = LinearLayoutManager(this)

        // Firebase reference for this item
        val database = FirebaseDatabase.getInstance()
        chatRef = database.getReference("chats").child(itemName)

        // Listen for messages in real-time
        chatRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messagesList.clear()
                for (data in snapshot.children) {
                    val message = data.getValue(Message::class.java)
                    if (message != null) messagesList.add(message)
                }
                chatAdapter.notifyDataSetChanged()
                chatRecycler.scrollToPosition(messagesList.size - 1)
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        // Send button click listener
        sendButton.setOnClickListener {
            val messageText = messageInput.text.toString().trim()
            if (messageText.isNotEmpty()) {
                sendMessage(messageText)
                messageInput.text.clear()
            }
        }
    }

    // Function to send a message to Firebase
    private fun sendMessage(text: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "anonymous"
        val message = Message(text, userId, System.currentTimeMillis())
        chatRef.push().setValue(message)
    }
}

// Data class for a chat message
data class Message(
    val text: String = "",
    val senderId: String = "",
    val timestamp: Long = 0
)
