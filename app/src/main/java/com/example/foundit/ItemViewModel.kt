package com.example.foundit

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.foundit.data.ItemRepository
import com.example.foundit.models.Item

class ItemViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ItemRepository = ItemRepository()
    val allItems: LiveData<List<Item>> = repository.allItems

    fun insert(item: Item, imageUri: Uri? = null) {
        repository.insert(item, imageUri)
    }

    fun update(item: Item) {
        repository.update(item)
    }
}
