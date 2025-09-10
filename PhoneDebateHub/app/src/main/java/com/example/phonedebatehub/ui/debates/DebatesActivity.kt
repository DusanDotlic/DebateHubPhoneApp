package com.example.phonedebatehub.ui.debates

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.phonedebatehub.R
import com.example.phonedebatehub.data.local.Repository
import com.example.phonedebatehub.ui.detail.DebateDetailActivity
import com.example.phonedebatehub.util.IntentKeys
import kotlinx.coroutines.launch
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View


class DebatesActivity : AppCompatActivity() {
    private var userId: Long = -1L
    private var userName: String = "Guest"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debates)

        //Progress Bar
        val progress = findViewById<android.widget.ProgressBar>(R.id.progressBar)
        val emptyView = findViewById<android.widget.TextView>(R.id.textEmpty)


// Receive extras from Auth
        userId = intent.getLongExtra(IntentKeys.USER_ID, -1L)
        userName = intent.getStringExtra(IntentKeys.USER_NAME) ?: "Guest"

        val welcome = findViewById<TextView>(R.id.textWelcome)
        welcome.text = "Welcome, $userName (id=$userId)"

// RecyclerView setup
        val recycler = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerDebates)
        val adapter = DebatesAdapter { debate ->
            // on item click -> open detail
            val intent = Intent(this, com.example.phonedebatehub.ui.detail.DebateDetailActivity::class.java).apply {
                putExtra(IntentKeys.DEBATE_ID, debate.id)
                putExtra(IntentKeys.DEBATE_TITLE, debate.title)
            }
            startActivity(intent)
        }
        recycler.adapter = adapter
        recycler.setHasFixedSize(true)
        recycler.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)

// Load data
        lifecycleScope.launch {
            val repo = com.example.phonedebatehub.data.local.Repository(this@DebatesActivity)
            try {
                progress.visibility = View.VISIBLE
                emptyView.visibility = View.GONE
                recycler.visibility = View.GONE

                // local cache
                val local = repo.getDebates(forceRefresh = false)
                adapter.submitList(local)

                // show cache if not empty
                if (local.isNotEmpty()) {
                    recycler.visibility = View.VISIBLE
                } else {
                    emptyView.visibility = View.VISIBLE
                }

                // then network
                val fresh = repo.getDebates(forceRefresh = true)
                adapter.submitList(fresh)
                progress.visibility = View.GONE

                if (fresh.isNotEmpty()) {
                    recycler.visibility = View.VISIBLE
                    emptyView.visibility = View.GONE
                } else {
                    recycler.visibility = View.GONE
                    emptyView.visibility = View.VISIBLE
                }
            } catch (t: Throwable) {
                progress.visibility = View.GONE
                if (adapter.currentList.isEmpty()) {
                    recycler.visibility = View.GONE
                    emptyView.visibility = View.VISIBLE
                }
                android.widget.Toast
                    .makeText(this@DebatesActivity, "Network error: ${t.localizedMessage}", android.widget.Toast.LENGTH_LONG)
                    .show()
            }
        }

    }
}
