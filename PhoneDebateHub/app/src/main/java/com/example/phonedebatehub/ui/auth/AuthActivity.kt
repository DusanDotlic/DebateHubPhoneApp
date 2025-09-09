package com.example.phonedebatehub.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.phonedebatehub.R
import com.example.phonedebatehub.ui.debates.DebatesActivity
import com.example.phonedebatehub.util.IntentKeys

class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        val name = findViewById<EditText>(R.id.inputName)
        val login = findViewById<Button>(R.id.buttonLogin)

        login.setOnClickListener {
            val userId = 1L
            val userName = name.text?.toString()?.ifBlank { "Guest" } ?: "Guest"

            val intent = Intent(this, DebatesActivity::class.java).apply {
                putExtra(IntentKeys.USER_ID, userId)
                putExtra(IntentKeys.USER_NAME, userName)
            }
            startActivity(intent)
            finish()
        }
    }
}
