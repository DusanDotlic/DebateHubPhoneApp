package com.example.phonedebatehub.ui.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.phonedebatehub.R
import com.example.phonedebatehub.data.model.Message

class MessagesAdapter(
    private val onLike: (Message) -> Unit
) : ListAdapter<Message, MessagesAdapter.VH>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Message>() {
            override fun areItemsTheSame(oldItem: Message, newItem: Message) =
                oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Message, newItem: Message) =
                oldItem == newItem
        }
    }

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        private val author = view.findViewById<TextView>(R.id.textAuthor)
        private val content = view.findViewById<TextView>(R.id.textContent)
        private val created = view.findViewById<TextView>(R.id.textCreated)
        private val buttonLike = view.findViewById<AppCompatImageButton>(R.id.buttonLike)
        private val textLikes = view.findViewById<TextView>(R.id.textLikes)

        fun bind(item: Message) {
            // content
            author.text = item.author
            content.text = item.content
            created.text = item.createdAt ?: ""

            // like UI
            textLikes.text = item.likes.toString()
            buttonLike.isSelected = item.userLiked
            buttonLike.contentDescription = itemView.context.getString(
                if (item.userLiked) R.string.like_button_unlike else R.string.like_button_like)

            // click
            buttonLike.isEnabled = true
            buttonLike.setOnClickListener { onLike(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row_message, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))
}
