package com.example.foundit

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ChatListActivity : AppCompatActivity() {

    private lateinit var chatRecycler: RecyclerView
    private lateinit var chatListAdapter: ChatListAdapter
    private val chatList = mutableListOf<String>() // store item names with chats

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_list)

        chatRecycler = findViewById(R.id.chat_list_recycler)
        chatListAdapter = ChatListAdapter(chatList) { itemName ->
            // open the specific chat when clicked
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("ITEM_NAME", itemName)
            startActivity(intent)
        }

        chatRecycler.adapter = chatListAdapter
        chatRecycler.layoutManager = LinearLayoutManager(this)

        // Fetch all chats for the user
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val chatsRef = FirebaseDatabase.getInstance().getReference("chats")
        chatsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()
                for (item in snapshot.children) {
                    // Optional: check if this user has messages in this chat
                    val messages = item.children
                    val hasMessage = messages.any { it.child("senderId").value == userId }
                    if (hasMessage) {
                        chatList.add(item.key ?: "Unknown")
                    }
                }
                chatListAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ChatListActivity, "Failed to load chats", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
