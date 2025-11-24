package com.example.foundit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foundit.models.Item

class ItemAdapter(private val items: List<Item>) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.text_view_item_title)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.text_view_item_description)
        private val locationTextView: TextView = itemView.findViewById(R.id.text_view_item_location)

        fun bind(item: Item) {
            titleTextView.text = item.title
            descriptionTextView.text = item.description
            locationTextView.text = "Location: ${item.location}"
        }
    }
}
