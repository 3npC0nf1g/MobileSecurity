package com.insecureshop

import android.net.Uri
import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.insecureshop.util.CustomWebViewClient
import com.insecureshop.util.Prefs
import kotlinx.android.synthetic.main.activity_product_list.*

class WebViewActivity : AppCompatActivity() {

    private val USER_AGENT =
        "Mozilla/5.0 (Linux; Android 4.1.1; Galaxy Nexus Build/JRO03C) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.65 Mobile Safari/537.36"

    private val allowedDomains = listOf(
        "https://www.insecureshopapp.com",
        "https://docs.insecureshopapp.com"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)
        setSupportActionBar(toolbar)
        title = getString(R.string.webview)

        val webview = findViewById<WebView>(R.id.webview)

        webview.settings.javaScriptEnabled = false
        webview.settings.allowUniversalAccessFromFileURLs = false
        webview.settings.loadWithOverviewMode = true
        webview.settings.useWideViewPort = true
        webview.settings.userAgentString = USER_AGENT

        webview.webViewClient = CustomWebViewClient()

        val uri: Uri? = intent.data
        uri?.let {
            val data = intent.data?.getQueryParameter("url")

            if (data == null || !isValidUrl(data)) {
                val warningHtml = """
        <html>
            <head>
                <style>
                    body {
                        font-family: sans-serif;
                        background-color: #ffe6e6;
                        color: #a30000;
                        padding: 40px;
                        text-align: center;
                    }
                    .warning {
                        border: 2px solid #ff4d4d;
                        background-color: #ffe6e6;
                        padding: 30px;
                        border-radius: 12px;
                        display: inline-block;
                        max-width: 80%;
                        font-size: 1.4em;
                        box-shadow: 0 0 10px rgba(255, 0, 0, 0.2);
                    }
                    h2 {
                        font-size: 2em;
                        color: #cc0000;
                        margin-bottom: 20px;
                    }
                    p {
                        font-size: 1.2em;
                    }
                </style>
            </head>
            <body>
                <div class="warning">
                    <h2>Security Warning</h2>
                    <p>The URL you tried to open is not authorized and has been blocked for your safety.</p>
                </div>
            </body>
        </html>
    """.trimIndent()

                webview.loadDataWithBaseURL(null, warningHtml, "text/html", "utf-8", null)
                return
            }

            webview.loadUrl(data)
            Prefs.getInstance(this).data = data
        }
    }

    private fun isValidUrl(url: String): Boolean {
        return url.startsWith("https://") && allowedDomains.any { url.startsWith(it) }
    }
}
