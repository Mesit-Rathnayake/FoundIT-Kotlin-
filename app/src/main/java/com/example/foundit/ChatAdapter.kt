package com.example.foundit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class ChatAdapter(private val messages: List<Message>) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    // ViewHolder for each message
    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageText: TextView = itemView.findViewById(R.id.message_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.chat_item, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = messages[position]

        // Set message text
        holder.messageText.text = message.text

        // Align message based on sender
        val params = holder.messageText.layoutParams as ViewGroup.MarginLayoutParams

        if (message.senderId == FirebaseAuth.getInstance().currentUser?.uid) {
            // Message sent by current user → align right
            params.marginStart = 50
            params.marginEnd = 0
            holder.messageText.textAlignment = View.TEXT_ALIGNMENT_TEXT_END
            holder.messageText.setBackgroundResource(R.drawable.chat_bubble_bg) // your bubble
        } else {
            // Message from others → align left
            params.marginStart = 0
            params.marginEnd = 50
            holder.messageText.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
            holder.messageText.setBackgroundResource(R.drawable.chat_bubble_bg) // same bubble
        }

        holder.messageText.layoutParams = params
    }

    override fun getItemCount(): Int = messages.size
}
