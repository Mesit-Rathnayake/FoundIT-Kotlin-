package com.example.foundit.data

import androidx.lifecycle.LiveData
import com.example.foundit.models.Item

class ItemRepository(private val itemDao: ItemDao) {

    val allItems: LiveData<List<Item>> = itemDao.getAllItems()

    suspend fun insert(item: Item) {
        itemDao.insertItem(item)
    }
}
