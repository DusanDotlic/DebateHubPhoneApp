package com.example.phonedebatehub.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.phonedebatehub.R
import com.example.phonedebatehub.data.local.Repository
import com.example.phonedebatehub.util.IntentKeys
import kotlinx.coroutines.launch
import androidx.core.os.bundleOf
import com.example.phonedebatehub.data.model.Message
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout


class DebateDetailFragment : Fragment(R.layout.fragment_debate_detail) {
    private lateinit var adapter: MessagesAdapter

    companion object {
        fun newInstance(debateId: Long, debateTitle: String): DebateDetailFragment {
            return DebateDetailFragment().apply {
                arguments = bundleOf(
                    IntentKeys.DEBATE_ID to debateId,
                    IntentKeys.DEBATE_TITLE to debateTitle
                )
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_debate_detail, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val debateId = requireArguments().getLong(IntentKeys.DEBATE_ID, -1L)
        val debateTitle = requireArguments().getString(IntentKeys.DEBATE_TITLE) ?: "Debate"

        view.findViewById<TextView>(R.id.textHeader).text = "Messages • $debateTitle"

        val recycler = view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerMessages)
        adapter = MessagesAdapter { message ->
            // Optimistic toggle in the list
            val idx = adapter.currentList.indexOfFirst { it.id == message.id }
            if (idx != -1) {
                val toggled = message.copy(
                    userLiked = !message.userLiked,
                    likes = (message.likes + if (!message.userLiked) 1 else -1).coerceAtLeast(0)
                )
                val newList = adapter.currentList.toMutableList()
                newList[idx] = toggled
                adapter.submitList(newList)
            }

            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val repo = Repository(requireContext())
                    val saved = repo.toggleLike(message.id)
                    val pos = adapter.currentList.indexOfFirst { it.id == saved.id }
                    if (pos != -1) {
                        val fixed = adapter.currentList.toMutableList()
                        fixed[pos] = saved
                        adapter.submitList(fixed)
                    }
                } catch (t: Throwable) {
                    val pos = adapter.currentList.indexOfFirst { it.id == message.id }
                    if (pos != -1) {
                        val revert = adapter.currentList.toMutableList()
                        revert[pos] = message
                        adapter.submitList(revert)
                    }
                    android.widget.Toast.makeText(
                        requireContext(),
                        "Failed to like: ${t.localizedMessage}",
                        android.widget.Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.setHasFixedSize(true)

        fun load(force: Boolean) = viewLifecycleOwner.lifecycleScope.launch {
            val repo = Repository(requireContext())
            try {
                if (!force) {
                    val local = repo.getMessages(debateId, forceRefresh = false)
                    adapter.submitList(local)
                }
                val fresh = repo.getMessages(debateId, forceRefresh = true)
                adapter.submitList(fresh)
            } catch (t: Throwable) {
                android.widget.Toast.makeText(
                    requireContext(),
                    "Could not refresh messages: ${t.localizedMessage}",
                    android.widget.Toast.LENGTH_LONG
                ).show()
            } finally {
                view.findViewById<SwipeRefreshLayout>(R.id.swipeRefresh).isRefreshing = false
            }
        }

        // initial load
        load(force = false)

        // swipe to refresh
        view.findViewById<SwipeRefreshLayout>(R.id.swipeRefresh).setOnRefreshListener {
            load(force = true)
        }

        val input = view.findViewById<android.widget.EditText>(R.id.inputMessage)
        val sendBtn = view.findViewById<android.widget.Button>(R.id.buttonSend)
        sendBtn.setOnClickListener {
            val text = input.text.toString().trim()
            if (text.isNotEmpty()) {
                viewLifecycleOwner.lifecycleScope.launch {
                    val repo = Repository(requireContext())
                    try {
                        val newMsg = repo.sendMessage(debateId, "You", text)
                        val current = adapter.currentList.toMutableList()
                        current.add(newMsg)
                        adapter.submitList(current)
                        recycler.scrollToPosition(current.size - 1)
                        input.text.clear()
                    } catch (t: Throwable) {
                        android.widget.Toast.makeText(
                            requireContext(),
                            "Failed to send: ${t.localizedMessage}",
                            android.widget.Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }
}
