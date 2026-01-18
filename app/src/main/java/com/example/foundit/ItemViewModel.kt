package com.example.foundit

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.foundit.data.ItemRepository
import com.example.foundit.models.Item

class ItemViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ItemRepository = ItemRepository
    val allItems: LiveData<List<Item>> = repository.allItems
    val myItems: LiveData<List<Item>> = repository.myItems

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
    }
}
