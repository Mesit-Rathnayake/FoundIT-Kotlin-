package com.example.foundit

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foundit.models.Item 
import com.example.foundit.models.User // Import the User data class
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore 
import java.util.concurrent.atomic.AtomicInteger

// Data class to represent an item in the chat list displayed in ChatListActivity
data class ChatListItem(
    val chatRoomId: String,
    val otherUserId: String, // The ID of the other user in this chat
    val otherUserName: String, // NEW: Name of the other user
    val itemId: String,      // The ID of the item being discussed
    val itemName: String     // The name/title of the item
)

class ChatListActivity : AppCompatActivity() {

    private lateinit var chatRecycler: RecyclerView
    private lateinit var chatRoomListAdapter: ChatListItemAdapter 
    private val chatList = mutableListOf<ChatListItem>() 
    private val tempFetchedChatItems = mutableListOf<ChatListItem>() // FIXED: Declared tempFetchedChatItems here

    private lateinit var auth: FirebaseAuth
    private lateinit var realtimeDatabase: FirebaseDatabase 
    private lateinit var firestore: FirebaseFirestore 

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_list)

        auth = FirebaseAuth.getInstance()
        realtimeDatabase = FirebaseDatabase.getInstance("https://foundit-308f8-default-rtdb.asia-southeast1.firebasedatabase.app")
        firestore = FirebaseFirestore.getInstance() 

        chatRecycler = findViewById(R.id.chat_list_recycler)
        
        chatRoomListAdapter = ChatListItemAdapter(chatList) { clickedChatListItem ->
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("ITEM_ID", clickedChatListItem.itemId)
            intent.putExtra("RECEIVER_ID", clickedChatListItem.otherUserId)
            intent.putExtra("ITEM_NAME", clickedChatListItem.itemName) // Pass the resolved item name
            Log.d("ChatListActivity", "Launching ChatActivity with ITEM_ID: ${clickedChatListItem.itemId}, RECEIVER_ID: ${clickedChatListItem.otherUserId}, ITEM_NAME: ${clickedChatListItem.itemName}")
            startActivity(intent)
        }

        chatRecycler.adapter = chatRoomListAdapter
        chatRecycler.layoutManager = LinearLayoutManager(this)

        val currentUserId = auth.currentUser?.uid
        if (currentUserId == null) {
            Toast.makeText(this, "User not authenticated.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val chatsRef = realtimeDatabase.getReference("chats")
        chatsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Clear chatList here to prevent duplicates if data changes in Realtime DB
                // and ensure tempFetchedChatItems is fresh for this snapshot.
                tempFetchedChatItems.clear()
                val chatRoomsToProcess = mutableListOf<Triple<String, String, String>>() // chatRoomId, otherUserId, itemId

                for (chatRoomSnapshot in snapshot.children) {
                    val chatRoomId = chatRoomSnapshot.key ?: continue
                    val parts = chatRoomId.split("_")

                    if (parts.size == 3) {
                        val user1Id = parts[0]
                        val user2Id = parts[1]
                        val itemId = parts[2] 

                        val otherUserId: String? = when {
                            user1Id == currentUserId -> user2Id
                            user2Id == currentUserId -> user1Id
                            else -> null 
                        }

                        if (otherUserId != null) {
                            chatRoomsToProcess.add(Triple(chatRoomId, otherUserId, itemId))
                        }
                    } else {
                        Log.w("ChatListActivity", "Malformed chatRoomId: $chatRoomId")
                    }
                }

                Log.d("ChatListActivity", "Total chat rooms to process: ${chatRoomsToProcess.size}")

                if (chatRoomsToProcess.isEmpty()) {
                    chatList.clear() // Clear list if no chats
                    chatRoomListAdapter.notifyDataSetChanged()
                    Log.d("ChatListActivity", "No chat rooms for current user, or no valid item IDs found.") 
                    return
                }

                val latch = AtomicInteger(chatRoomsToProcess.size)

                for ((chatRoomId, otherUserId, itemId) in chatRoomsToProcess) {
                    // Fetch Item details from Firestore
                    firestore.collection("items").document(itemId).get()
                        .addOnSuccessListener { itemDocumentSnapshot ->
                            val item = itemDocumentSnapshot.toObject(Item::class.java)
                            val itemName = item?.title ?: "Item: $itemId"
                            Log.d("ChatListActivity", "Fetched Item for itemId: $itemId, title: ${item?.title}. Final itemName: $itemName")

                            // Now fetch User details from Firestore
                            firestore.collection("users").document(otherUserId).get()
                                .addOnSuccessListener { userDocumentSnapshot ->
                                    val user = userDocumentSnapshot.toObject(User::class.java)
                                    val otherUserName = user?.name ?: "Unknown User"
                                    Log.d("ChatListActivity", "Fetched User for otherUserId: $otherUserId, name: ${user?.name}. Final otherUserName: $otherUserName")

                                    val chatItem = ChatListItem(chatRoomId, otherUserId, otherUserName, itemId, itemName)
                                    tempFetchedChatItems.add(chatItem)

                                    if (latch.decrementAndGet() == 0) {
                                        // Ensure chatList is completely refreshed after all async calls complete
                                        chatList.clear()
                                        chatList.addAll(tempFetchedChatItems.sortedByDescending { it.chatRoomId }) 
                                        chatRoomListAdapter.notifyDataSetChanged()
                                        Log.d("ChatListActivity", "Chat list fully updated with ${chatList.size} items.")
                                    }
                                }
                                .addOnFailureListener { e ->
                                    Log.e("ChatListActivity", "Failed to fetch user details from Firestore for $otherUserId: ${e.message}")
                                    val chatItem = ChatListItem(chatRoomId, otherUserId, "Unknown User (Error)", itemId, itemName) // Fallback for user name
                                    tempFetchedChatItems.add(chatItem)
                                    if (latch.decrementAndGet() == 0) {
                                        chatList.clear()
                                        chatList.addAll(tempFetchedChatItems.sortedByDescending { it.chatRoomId }) 
                                        chatRoomListAdapter.notifyDataSetChanged()
                                        Log.d("ChatListActivity", "Chat list fully updated with errors (user fetch). Items: ${chatList.size}")
                                    }
                                }
                        }
                        .addOnFailureListener { e ->
                            Log.e("ChatListActivity", "Failed to fetch item details from Firestore for $itemId: ${e.message}")
                            // Fallback for item name and proceed to fetch user
                            val itemName = "Item: $itemId (Error)"
                            firestore.collection("users").document(otherUserId).get()
                                .addOnSuccessListener { userDocumentSnapshot ->
                                    val user = userDocumentSnapshot.toObject(User::class.java)
                                    val otherUserName = user?.name ?: "Unknown User"
                                    val chatItem = ChatListItem(chatRoomId, otherUserId, otherUserName, itemId, itemName)
                                    tempFetchedChatItems.add(chatItem)
                                    if (latch.decrementAndGet() == 0) {
                                        chatList.clear()
                                        chatList.addAll(tempFetchedChatItems.sortedByDescending { it.chatRoomId }) 
                                        chatRoomListAdapter.notifyDataSetChanged()
                                        Log.d("ChatListActivity", "Chat list fully updated with errors (item fetch). Items: ${chatList.size}")
                                    }
                                }
                                .addOnFailureListener { eUser ->
                                    Log.e("ChatListActivity", "Failed to fetch user details AND item details for $itemId / $otherUserId: ${eUser.message}")
                                    val chatItem = ChatListItem(chatRoomId, otherUserId, "Unknown User (Error)", itemId, itemName)
                                    tempFetchedChatItems.add(chatItem)
                                    if (latch.decrementAndGet() == 0) {
                                        chatList.clear()
                                        chatList.addAll(tempFetchedChatItems.sortedByDescending { it.chatRoomId }) 
                                        chatRoomListAdapter.notifyDataSetChanged()
                                        Log.d("ChatListActivity", "Chat list fully updated with double errors. Items: ${chatList.size}")
                                    }
                                }
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ChatListActivity", "Failed to load chat list from Realtime Database: ${error.message}")
                Toast.makeText(this@ChatListActivity, "Failed to load chats", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
