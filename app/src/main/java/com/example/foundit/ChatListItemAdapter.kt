package com.example.foundit

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatListItemAdapter(
    private val chatListItems: List<ChatListItem>,
    private val clickListener: (ChatListItem) -> Unit
) : RecyclerView.Adapter<ChatListItemAdapter.ChatListItemViewHolder>() {

    class ChatListItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemNameTextView: TextView = itemView.findViewById(R.id.chat_item_name)
        val otherUserNameTextView: TextView = itemView.findViewById(R.id.chat_other_user_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.chat_list_item, parent, false)
        return ChatListItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatListItemViewHolder, position: Int) {
        val chatListItem = chatListItems[position]

        Log.d("ChatListItemAdapter", "Binding item at position $position: ItemName = ${chatListItem.itemName}, OtherUserName = ${chatListItem.otherUserName}")

        holder.itemNameTextView.text = chatListItem.itemName
        holder.otherUserNameTextView.text = "Chat with: ${chatListItem.otherUserName}"
        holder.itemView.setOnClickListener { clickListener(chatListItem) }
    }

    override fun getItemCount(): Int = chatListItems.size
}
