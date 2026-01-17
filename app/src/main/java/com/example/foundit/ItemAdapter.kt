package com.example.foundit

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.foundit.models.Item
import java.util.Locale

class ItemAdapter(
    private var allItems: List<Item>,
    private val onFoundItClickListener: (Item) -> Unit,
    private val onItsMineClickListener: (Item) -> Unit
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    private var filteredItems: List<Item> = allItems

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = filteredItems[position]
        holder.bind(item, onFoundItClickListener, onItsMineClickListener)
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
                        item.description.lowercase(Locale.getDefault()).contains(lowerCaseQuery)
            }
        }
        notifyDataSetChanged()
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Corrected IDs to match item_layout.xml
        private val titleTextView: TextView = itemView.findViewById(R.id.item_name)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.item_description)
        private val itemImageView: ImageView = itemView.findViewById(R.id.item_image)
        // Note: The buttons were not in the previous item_layout.xml. You might need to add them.
        // If you add them, you can uncomment these lines.
        // private val foundItButton: Button = itemView.findViewById(R.id.button_found_it)
        // private val itsMineButton: Button = itemView.findViewById(R.id.button_its_mine)


        fun bind(item: Item, onFoundItClickListener: (Item) -> Unit, onItsMineClickListener: (Item) -> Unit) {
            titleTextView.text = item.title
            descriptionTextView.text = item.description

            val cardView = itemView as CardView
            val context = itemView.context

            // TODO: Uncomment this block after adding buttons and color resources.
            /*
            when (item.status) {
                "active" -> {
                    // Make sure you have R.color.white in your colors.xml
                    cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))
                    foundItButton.visibility = View.VISIBLE
                    itsMineButton.visibility = View.VISIBLE
                    foundItButton.setOnClickListener { onFoundItClickListener(item) }
                    itsMineButton.setOnClickListener { onItsMineClickListener(item) }
                }
                "found", "claimed" -> {
                    // Make sure you have R.color.item_claimed_background in your colors.xml
                    cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.item_claimed_background))
                    foundItButton.visibility = View.GONE
                    itsMineButton.visibility = View.GONE
                }
                else -> {
                    cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))
                    foundItButton.visibility = View.GONE
                    itsMineButton.visibility = View.GONE
                }
            }
            */
        }
    }
}
