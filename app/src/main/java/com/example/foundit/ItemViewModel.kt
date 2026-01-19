package com.example.foundit

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.foundit.data.ItemRepository
import com.example.foundit.models.Item
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ItemViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ItemRepository = ItemRepository
    val allItems: LiveData<List<Item>> = repository.allItems
    val myItems: LiveData<List<Item>> = repository.myItems

    // *** FIX: Specify the correct Firebase Realtime Database URL ***
    private val database = FirebaseDatabase.getInstance("https://foundit-308f8-default-rtdb.asia-southeast1.firebasedatabase.app")

    fun startObservingItems() {
        Log.d("ItemViewModel", "startObservingItems called")
        repository.startObservingItems()
    }

    fun stopObservingItems() {
        Log.d("ItemViewModel", "stopObservingItems called")
        repository.stopObservingItems()
    }

    fun insert(item: Item, imageUri: Uri? = null) {
        repository.insert(item, imageUri)
    }

    fun update(item: Item) {
        repository.update(item)
    }

    fun deleteItem(item: Item) {
        repository.deleteItem(item)
        deleteChatsForItem(item.documentId) // Call to delete associated chats
    }

    // NEW: Function to delete chats associated with a deleted item
    fun deleteChatsForItem(itemId: String) {
        Log.d("ItemViewModel", "Attempting to delete chats for item: $itemId")
        val chatsRef = database.getReference("chats")

        chatsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var chatsDeletedCount = 0
                for (chatSnapshot in snapshot.children) {
                    val chatRoomId = chatSnapshot.key // Get the chat room ID
                    if (chatRoomId != null && chatRoomId.contains(itemId)) {
                        // Delete this chat room
                        chatSnapshot.ref.removeValue()
                            .addOnSuccessListener {
                                Log.d("ItemViewModel", "Chat room $chatRoomId deleted successfully for item $itemId")
                                chatsDeletedCount++
                            }
                            .addOnFailureListener { e ->
                                Log.e("ItemViewModel", "Failed to delete chat room $chatRoomId for item $itemId: ${e.message}", e)
                            }
                    }
                }
                if (chatsDeletedCount > 0) {
                    Log.d("ItemViewModel", "Total $chatsDeletedCount chats deleted for item $itemId.")
                } else {
                    Log.d("ItemViewModel", "No chats found or deleted for item $itemId.")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ItemViewModel", "Failed to read chat rooms for deletion: ${error.message}")
            }
        })
    }
}
