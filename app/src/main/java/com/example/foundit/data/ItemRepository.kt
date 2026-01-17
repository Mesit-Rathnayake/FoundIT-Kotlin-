package com.example.foundit.data

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.foundit.models.Item
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

object ItemRepository { // CHANGED from class to object

    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val itemsCollection = firestore.collection("items")

    private val _allItems = MutableLiveData<List<Item>>()
    val allItems: LiveData<List<Item>> = _allItems

    private var itemsListener: ListenerRegistration? = null

    fun startObservingItems() {
        if (itemsListener != null) {
            Log.d("ItemRepository", "Observer is already attached.")
            return
        }

        Log.d("ItemRepository", "Attaching items listener.")
        itemsListener = itemsCollection.orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("ItemRepository", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val items = snapshot.toObjects(Item::class.java)
                    for (i in items.indices) {
                        items[i].documentId = snapshot.documents[i].id
                    }
                    _allItems.value = items
                    Log.d("ItemRepository", "Items updated. Count: ${items.size}")
                } else {
                    Log.d("ItemRepository", "Current data: null")
                }
            }
    }

    fun stopObservingItems() {
        Log.d("ItemRepository", "Removing items listener.")
        itemsListener?.remove()
        itemsListener = null
        _allItems.postValue(emptyList())
    }

    fun insert(item: Item, imageUri: Uri? = null) {
        Log.d("ItemRepository", "Insert called for item: ${item.title}")
        if (imageUri != null) {
            val fileName = "images/${UUID.randomUUID()}.jpg"
            val storageRef = storage.reference.child(fileName)

            Log.d("ItemRepository", "Uploading image to: $fileName")
            storageRef.putFile(imageUri)
                .addOnSuccessListener {
                    Log.d("ItemRepository", "Image upload successful.")
                    storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                        Log.d("ItemRepository", "Got download URL: $downloadUrl")
                        item.imageUrl = downloadUrl.toString()
                        saveToFirestore(item)
                    }.addOnFailureListener { e ->
                        Log.e("ItemRepository", "Failed to get download URL, saving text only", e)
                        saveToFirestore(item)
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("ItemRepository", "Image upload failed, saving text only", e)
                    saveToFirestore(item)
                }
        } else {
            Log.d("ItemRepository", "No image to upload, saving text only.")
            saveToFirestore(item)
        }
    }

    private fun saveToFirestore(item: Item) {
        Log.d("ItemRepository", "Saving to Firestore: $item")
        itemsCollection.add(item)
            .addOnSuccessListener { documentReference ->
                Log.d("ItemRepository", "Item saved successfully! Document ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.e("ItemRepository", "Error saving item to Firestore", e)
            }
    }

    fun update(item: Item) {
        if (item.documentId.isNotEmpty()) {
            itemsCollection.document(item.documentId).set(item)
                .addOnSuccessListener { Log.d("ItemRepository", "Item updated successfully!") }
                .addOnFailureListener { e -> Log.e("ItemRepository", "Error updating item", e) }
        } else {
            Log.w("ItemRepository", "Update failed: item has empty documentId.")
        }
    }
}