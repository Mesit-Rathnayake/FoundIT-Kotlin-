package com.example.foundit.data

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.foundit.models.Item
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

object ItemRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val itemsCollection = firestore.collection("items")

    private val _allItems = MutableLiveData<List<Item>>()
    val allItems: LiveData<List<Item>> = _allItems

    private val _myItems = MutableLiveData<List<Item>>()
    val myItems: LiveData<List<Item>> = _myItems

    private var itemsListener: ListenerRegistration? = null

    // --- Start observing items safely ---
    fun startObservingItems() {
        // Stop old listener first to prevent duplicates
        stopObservingItems()

        itemsListener = itemsCollection
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener

                if (snapshot != null) {
                    val items = snapshot.toObjects(Item::class.java)
                    for (i in items.indices) {
                        items[i].documentId = snapshot.documents[i].id
                    }

                    _allItems.value = items

                    // Only filter for myItems if user is still logged in
                    val currentUser = FirebaseAuth.getInstance().currentUser
                    val filtered = if (currentUser != null) {
                        items.filter { it.userId == currentUser.uid }
                    } else {
                        emptyList()
                    }
                    _myItems.value = filtered
                }
            }
    }

    // --- Stop observing items safely ---
    fun stopObservingItems() {
        itemsListener?.remove()
        itemsListener = null
        _allItems.postValue(emptyList())
        _myItems.postValue(emptyList())
    }

    // --- Insert item with optional image ---
    fun insert(item: Item, imageUri: Uri? = null) {
        // Generate a document ID upfront
        val newDocumentRef = itemsCollection.document()
        val newDocumentId = newDocumentRef.id
        item.documentId = newDocumentId

        if (imageUri != null) {
            val fileName = "images/${UUID.randomUUID()}.jpg"
            val storageRef = storage.reference.child(fileName)

            storageRef.putFile(imageUri)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { url ->
                        item.imageUrl = url.toString()
                        // Save to Firestore with image URL
                        newDocumentRef.set(item)
                            .addOnSuccessListener { Log.d("ItemRepository", "Item saved with image: $newDocumentId") }
                            .addOnFailureListener { e -> Log.e("ItemRepository", "Error saving item with image: ${e.message}", e) }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("ItemRepository", "Failed to upload image. Saving item without image: ${e.message}", e)
                    // Save to Firestore without image URL if upload fails
                    newDocumentRef.set(item)
                        .addOnSuccessListener { Log.d("ItemRepository", "Item saved without image after upload failure: $newDocumentId") }
                        .addOnFailureListener { eFirestore -> Log.e("ItemRepository", "Error saving item without image after upload failure: ${eFirestore.message}", eFirestore) }
                }
        } else {
            // Save to Firestore directly if no image URI
            newDocumentRef.set(item)
                .addOnSuccessListener { Log.d("ItemRepository", "Item saved without image: $newDocumentId") }
                .addOnFailureListener { e -> Log.e("ItemRepository", "Error saving item without image: ${e.message}", e) }
        }
    }

    fun update(item: Item) {
        if (item.documentId.isNotEmpty()) {
            itemsCollection.document(item.documentId).set(item)
        } else {
            Log.w("ItemRepository", "Attempted to update item with empty documentId. Item not updated.")
        }
    }

    // --- Delete item from Firestore and Storage ---
    fun deleteItem(item: Item) {
        if (item.documentId.isNotEmpty()) {
            itemsCollection.document(item.documentId).delete()
                .addOnSuccessListener {
                    Log.d("ItemRepository", "Item ${item.documentId} deleted from Firestore.")
                    
                    item.imageUrl?.let { imageUrlToDelete -> 
                        if (imageUrlToDelete.isNotEmpty()) {
                            try {
                                val imageRef = storage.getReferenceFromUrl(imageUrlToDelete) 
                                imageRef.delete()
                                    .addOnSuccessListener {
                                        Log.d("ItemRepository", "Image for item ${item.documentId} deleted from Storage.")
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("ItemRepository", "Error deleting image for item ${item.documentId}: ${e.message}")
                                    }
                            } catch (e: Exception) {
                                Log.e("ItemRepository", "Error getting storage reference from URL for item ${item.documentId}: ${e.message}")
                            }
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("ItemRepository", "Error deleting item ${item.documentId} from Firestore: ${e.message}")
                }
        } else {
            Log.w("ItemRepository", "Attempted to delete item with empty documentId.")
        }
    }
}
