package com.example.phonedebatehub.ui.detail

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.phonedebatehub.R
import com.example.phonedebatehub.util.IntentKeys

class DebateDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debate_detail)

        // One-press back: always finish this Activity
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })

        if (savedInstanceState == null) {
            val debateId = intent.getLongExtra(IntentKeys.DEBATE_ID, -1L)
            val debateTitle = intent.getStringExtra(IntentKeys.DEBATE_TITLE) ?: "Untitled"

            val fragment = DebateDetailFragment.newInstance(debateId, debateTitle)

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment) // no addToBackStack
                .commit()
        }
    }
}
