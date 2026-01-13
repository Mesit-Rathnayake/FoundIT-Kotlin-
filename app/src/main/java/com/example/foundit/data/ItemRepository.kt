package com.example.foundit.data

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.foundit.models.Item
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class ItemRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val itemsCollection = firestore.collection("items")

    private val _allItems = MutableLiveData<List<Item>>()
    val allItems: LiveData<List<Item>> = _allItems

    init {
        observeItems()
    }

    private fun observeItems() {
        itemsCollection.orderBy("date", Query.Direction.DESCENDING)
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
                }
            }
    }

    /**
     * Uploads image to Storage (if it exists) then saves the item to Firestore.
     */
    fun insert(item: Item, imageUri: Uri? = null) {
        if (imageUri != null) {
            val fileName = "images/${UUID.randomUUID()}.jpg"
            val storageRef = storage.reference.child(fileName)

            storageRef.putFile(imageUri)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                        item.imageUrl = downloadUrl.toString()
                        saveToFirestore(item)
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("ItemRepository", "Image upload failed, saving text only", e)
                    saveToFirestore(item)
                }
        } else {
            saveToFirestore(item)
        }
    }

    private fun saveToFirestore(item: Item) {
        itemsCollection.add(item)
            .addOnSuccessListener { Log.d("ItemRepository", "Item saved!") }
            .addOnFailureListener { e -> Log.e("ItemRepository", "Save failed", e) }
    }

    fun update(item: Item) {
        if (item.documentId.isNotEmpty()) {
            itemsCollection.document(item.documentId).set(item)
        }
    }
}
