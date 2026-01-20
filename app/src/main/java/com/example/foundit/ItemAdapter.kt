package com.example.foundit

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide // Import Glide
import com.example.foundit.models.Item
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale

class ItemAdapter(
    private var allItems: List<Item>,
    private val onFoundItClickListener: (Item) -> Unit,
    private val onItsMineClickListener: (Item) -> Unit,
    private val onDeleteItemClickListener: (Item) -> Unit,
    private val onMarkAsClaimedClickListener: (Item) -> Unit // NEW: Callback for marking as claimed
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    private var filteredItems: List<Item> = allItems

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = filteredItems[position]
        holder.bind(item, onFoundItClickListener, onItsMineClickListener, onDeleteItemClickListener, onMarkAsClaimedClickListener)
    }

    override fun getItemCount(): Int = filteredItems.size

    fun setItems(newItems: List<Item>) {
        Log.d("ItemAdapter", "setItems called. New item count: ${newItems.size}")
        allItems = newItems
        filteredItems = newItems
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        filteredItems = if (query.isEmpty()) {
            allItems
        } else {
            val lowerCaseQuery = query.lowercase(Locale.getDefault())
            allItems.filter { item ->
                item.title.lowercase(Locale.getDefault()).contains(lowerCaseQuery) ||
                        item.description.lowercase(Locale.getDefault()).contains(lowerCaseQuery) ||
                        item.location.lowercase(Locale.getDefault()).contains(lowerCaseQuery)
            }
        }
        notifyDataSetChanged()
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val titleTextView: TextView = itemView.findViewById(R.id.item_name)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.item_description)
        private val locationTextView: TextView = itemView.findViewById(R.id.item_location)
        private val itemImageView: ImageView = itemView.findViewById(R.id.item_image)
        private val foundItButton: Button = itemView.findViewById(R.id.button_found_it)
        private val itsMineButton: Button = itemView.findViewById(R.id.button_its_mine)
        private val deleteButton: Button = itemView.findViewById(R.id.button_delete)
        private val itemTypeLabel: TextView = itemView.findViewById(R.id.item_type_label)
        private val markAsClaimedButton: Button = itemView.findViewById(R.id.button_mark_as_claimed)
        private val itemClaimedLabel: TextView = itemView.findViewById(R.id.item_claimed_label)

        fun bind(
            item: Item,
            onFoundItClickListener: (Item) -> Unit,
            onItsMineClickListener: (Item) -> Unit,
            onDeleteItemClickListener: (Item) -> Unit,
            onMarkAsClaimedClickListener: (Item) -> Unit
        ) {
            titleTextView.text = item.title
            descriptionTextView.text = item.description
            locationTextView.text = item.location

            // --- Image Loading with Glide (now with placeholder logic) ---
            itemImageView.visibility = View.VISIBLE // Always make image view visible
            if (!item.imageUrl.isNullOrEmpty()) {
                Glide.with(itemView.context)
                    .load(item.imageUrl)
                    .into(itemImageView)
            } else {
                // Load a default placeholder image
                Glide.with(itemView.context)
                    .load(android.R.drawable.ic_menu_gallery) // Using a generic system drawable as placeholder
                    .into(itemImageView)
            }

            val cardView = itemView as CardView
            val context = itemView.context

            val currentUser = FirebaseAuth.getInstance().currentUser
            val isOwner = currentUser != null && item.userId == currentUser.uid

            // --- Explicitly manage visibility for ALL buttons and labels based on status ---
            when (item.status) {
                "active" -> {
                    cardView.setCardBackgroundColor(
                        ContextCompat.getColor(context, R.color.item_active_background)
                    )

                    // Buttons for active items
                    foundItButton.visibility = View.VISIBLE
                    itsMineButton.visibility = View.VISIBLE
                    markAsClaimedButton.visibility = if (isOwner) View.VISIBLE else View.GONE

                    // Labels for active items
                    itemClaimedLabel.visibility = View.GONE // Ensure claimed label is hidden
                    when (item.type?.lowercase(Locale.getDefault())) {
                        "lost" -> {
                            itemTypeLabel.text = "LOST"
                            itemTypeLabel.setBackgroundColor(ContextCompat.getColor(context, R.color.item_type_lost_background))
                            itemTypeLabel.visibility = View.VISIBLE
                        }
                        "found" -> {
                            itemTypeLabel.text = "FOUND"
                            itemTypeLabel.setBackgroundColor(ContextCompat.getColor(context, R.color.item_type_found_background))
                            itemTypeLabel.visibility = View.VISIBLE
                        }
                        else -> {
                            itemTypeLabel.visibility = View.GONE
                        }
                    }

                    // Click listeners for active items
                    foundItButton.setOnClickListener {
                        val intent = Intent(context, ChatActivity::class.java)
                        intent.putExtra("ITEM_ID", item.documentId)
                        intent.putExtra("RECEIVER_ID", item.userId)
                        intent.putExtra("ITEM_NAME", item.title)
                        intent.putExtra("ACTION_TYPE", "Found It")
                        Log.d("ItemAdapter", "Attempting to start ChatActivity (Found It) for item documentId: ${item.documentId}")
                        context.startActivity(intent)
                        onFoundItClickListener(item)
                    }

                    itsMineButton.setOnClickListener {
                        val intent = Intent(context, ChatActivity::class.java)
                        intent.putExtra("ITEM_ID", item.documentId)
                        intent.putExtra("RECEIVER_ID", item.userId)
                        intent.putExtra("ITEM_NAME", item.title)
                        intent.putExtra("ACTION_TYPE", "It's Mine")
                        Log.d("ItemAdapter", "Attempting to start ChatActivity (It's Mine) for item documentId: ${item.documentId}")
                        context.startActivity(intent)
                        onItsMineClickListener(item)
                    }

                    if (isOwner) {
                        markAsClaimedButton.setOnClickListener { onMarkAsClaimedClickListener(item) }
                    }
                }

                "found", "claimed" -> {
                    cardView.setCardBackgroundColor(
                        ContextCompat.getColor(context, R.color.item_claimed_background)
                    )

                    // Buttons for claimed/found items (all hidden)
                    foundItButton.visibility = View.GONE
                    itsMineButton.visibility = View.GONE
                    markAsClaimedButton.visibility = View.GONE

                    // Labels for claimed/found items
                    itemTypeLabel.visibility = View.GONE // Ensure type label is hidden
                    itemClaimedLabel.text = "CLAIMED"
                    itemClaimedLabel.setBackgroundColor(ContextCompat.getColor(context, R.color.item_type_claimed_background))
                    itemClaimedLabel.visibility = View.VISIBLE
                }

                else -> {
                    cardView.setCardBackgroundColor(
                        ContextCompat.getColor(context, R.color.item_claimed_background) // Fallback background
                    )

                    // Buttons for unknown/other states (all hidden)
                    foundItButton.visibility = View.GONE
                    itsMineButton.visibility = View.GONE
                    markAsClaimedButton.visibility = View.GONE

                    // Labels for unknown/other states (all hidden)
                    itemTypeLabel.visibility = View.GONE
                    itemClaimedLabel.visibility = View.GONE
                }
            }

            // 🗑 Delete logic - only owner can delete (independent of other buttons/labels)
            if (isOwner) {
                deleteButton.visibility = View.VISIBLE
                deleteButton.setOnClickListener { onDeleteItemClickListener(item) }
            } else {
                deleteButton.visibility = View.GONE
            }
        }
    }
}
