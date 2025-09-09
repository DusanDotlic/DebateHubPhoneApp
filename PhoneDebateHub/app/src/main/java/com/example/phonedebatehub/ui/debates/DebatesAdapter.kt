package com.example.phonedebatehub.ui.debates

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.phonedebatehub.R
import com.example.phonedebatehub.data.model.Debate


class DebatesAdapter(
    private val onClick: (Debate) -> Unit
) : ListAdapter<Debate, DebatesAdapter.VH>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Debate>() {
            override fun areItemsTheSame(oldItem: Debate, newItem: Debate) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Debate, newItem: Debate) = oldItem == newItem
        }
    }

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        private val title = view.findViewById<TextView>(R.id.textTitle)
        private val subtitle = view.findViewById<TextView>(R.id.textSubtitle)

        fun bind(item: Debate) {
            title.text = item.title
            subtitle.text = "Updated: ${item.updatedAt?.substring(0, 10) ?: "-"}"
            itemView.setOnClickListener { onClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row_debate, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))
}
