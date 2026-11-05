package com.aqib.jarviscall

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var tts: TextToSpeech
    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CALL_PHONE, Manifest.permission.READ_CONTACTS),
            1
        )
        tts = TextToSpeech(this) {
            if (it == TextToSpeech.SUCCESS) tts.language = Locale("en", "IN")
        }
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(Color.BLACK)
            setPadding(48, 48, 48, 48)
        }
        status = TextView(this).apply {
            text = "Jarvis ready"
            setTextColor(Color.CYAN)
            textSize = 20f
            gravity = Gravity.CENTER
        }
        val btn = Button(this).apply {
            text = "Bolo"
            setOnClickListener { listen() }
        }
        layout.addView(status)
        layout.addView(btn)
        setContentView(layout)
    }

    private fun listen() {
        val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-IN")
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Bolo: Rahul ko call karo")
        }
        try {
            startActivityForResult(i, 100)
        } catch (e: Exception) {
            status.text = "Voice support nahi mila"
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            val text = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull() ?: return
            val reply = CallHelper.handle(this, text) ?: "Main sirf call kar sakta hu"
            status.text = "Tumne bola: $text\n\n$reply"
            tts.speak(reply, TextToSpeech.QUEUE_FLUSH, null, "j")
        }
    }
}
