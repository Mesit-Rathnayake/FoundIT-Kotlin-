package com.example.foundit

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout // Import ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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

    private var currentUserId: String? = null
    private var receiverId: String? = null
    private var itemId: String? = null

    private lateinit var rootChatLayout: ConstraintLayout // Reference to the root layout
    private lateinit var inputContainer: ConstraintLayout // Reference to the input container

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        Log.d("ChatActivity", "onCreate started.")

        currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        receiverId = intent.getStringExtra("RECEIVER_ID")
        itemId = intent.getStringExtra("ITEM_ID")
        val itemName = intent.getStringExtra("ITEM_NAME") ?: "Unknown"

        Log.d("ChatActivity", "currentUserId: $currentUserId")
        Log.d("ChatActivity", "receiverId: $receiverId")
        Log.d("ChatActivity", "itemId: $itemId")

        // Set chat title
        val chatTitle = findViewById<TextView>(R.id.chat_title)
        chatTitle.text = "Chat about: $itemName"

        // Initialize views
        rootChatLayout = findViewById(R.id.root_chat_layout)
        inputContainer = findViewById(R.id.input_container)
        chatRecycler = findViewById(R.id.chat_recycler)
        messageInput = findViewById(R.id.chat_input)
        sendButton = findViewById(R.id.chat_send_button)

        // Setup RecyclerView
        chatAdapter = ChatAdapter(messagesList, currentUserId)
        chatRecycler.adapter = chatAdapter
        chatRecycler.layoutManager = LinearLayoutManager(this)

        // Validate necessary IDs
        if (currentUserId == null || receiverId == null || itemId == null) {
            Log.e("ChatActivity", "Missing currentUserId, receiverId, or itemId. Finishing activity.")
            finish()
            return
        }

        // Create a unique chat room ID for this specific conversation about this item
        val chatRoomId = if (currentUserId!! < receiverId!!) {
            "${currentUserId}_${receiverId}_${itemId}"
        } else {
            "${receiverId}_${currentUserId}_${itemId}"
        }

        Log.d("ChatActivity", "ChatRoomId generated: $chatRoomId")

        // *** FIX: Specify the correct Firebase Realtime Database URL ***
        val database = FirebaseDatabase.getInstance("https://foundit-308f8-default-rtdb.asia-southeast1.firebasedatabase.app")
        chatRef = database.getReference("chats").child(chatRoomId)

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

            override fun onCancelled(error: DatabaseError) {
                Log.e("ChatActivity", "Failed to read messages: ${error.message}")
            }
        })

        // Send button click listener
        sendButton.setOnClickListener {
            val messageText = messageInput.text.toString().trim()
            if (messageText.isNotEmpty()) {
                sendMessage(messageText)
                messageInput.text.clear()
            }
        }

        // --- Programmatic Window Insets Handling for Keyboard --- //
        ViewCompat.setOnApplyWindowInsetsListener(rootChatLayout) { view, insets ->
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            // Calculate the total bottom inset (keyboard + system navigation bar)
            val totalBottomInset = imeInsets.bottom.coerceAtLeast(systemBarsInsets.bottom)

            // Apply this as padding to the input container
            inputContainer.setPadding(0, 0, 0, totalBottomInset)

            // Consume the insets so they don't get dispatched further
            insets
        }
        // --- End Window Insets Handling --- //

        Log.d("ChatActivity", "onCreate finished successfully.")
    }

    // Function to send a message to Firebase
    private fun sendMessage(text: String) {
        if (currentUserId == null || receiverId == null || itemId == null) {
            Log.e("ChatActivity", "Cannot send message: Missing user, receiver, or item ID.")
            return
        }
        val message = Message(text, currentUserId!!, receiverId!!, itemId!!, System.currentTimeMillis())
        chatRef.push().setValue(message)
            .addOnSuccessListener { Log.d("ChatActivity", "Message sent successfully!") }
            .addOnFailureListener { e -> Log.e("ChatActivity", "Failed to send message: ${e.message}", e) }
    }
}

data class Message(
    val text: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val itemId: String = "",
    val timestamp: Long = 0
)
