package com.example.pulseplay.activity

import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.example.pulseplay.R

class Workout : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)

        // Initialize WebView
        val webView = findViewById<WebView>(R.id.webView)

        // Enable JavaScript
        webView.settings.javaScriptEnabled = true
        webView.webChromeClient = WebChromeClient()

        // Corrected YouTube embed URL with proper escaping
        val video = """
            <html>
            <body style="margin:0;padding:0;">
                <iframe width="100%" height="100%"
                    src="https://www.youtube.com/embed/q_Z29u7nglQ?si=dgIv8zNDJVVBx1ch"
                    frameborder="0"
                    allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share"
                    allowfullscreen>
                </iframe>
            </body>
            </html>
        """.trimIndent()

        // Load HTML content in WebView
        webView.loadData(video, "text/html", "utf-8")
    }
}
