package com.example.foundit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// This adapter is specifically for displaying individual messages within the ChatActivity
class ChatListAdapter(
    private val chats: List<String>, // Note: this is still a List<String>, should be List<Message>
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<ChatListAdapter.ChatViewHolder>() {

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val chatItemText: TextView = itemView.findViewById(R.id.chat_item_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            // Correctly inflating the message_item.xml layout
            .inflate(R.layout.message_item, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val itemName = chats[position]
        holder.chatItemText.text = itemName
        holder.itemView.setOnClickListener { onClick(itemName) }
    }

    override fun getItemCount(): Int = chats.size
}
