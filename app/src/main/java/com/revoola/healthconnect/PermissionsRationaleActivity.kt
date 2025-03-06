package com.revoola.healthconnect

import android.app.Activity
import android.os.Bundle
import android.util.Patterns
import android.webkit.WebView


class PermissionsRationaleActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val myWebView = WebView(applicationContext)
        setContentView(myWebView)
        myWebView.loadUrl(url)
    }

    companion object {
        /**
         * URL that will be opened when the activity starts
         */
        private var url = "file:///android_asset/www/privacypolicy.html"

        /**
         * Used to set the URL to be opened by the activity
         * @param newUrl URL to be opened
         * @return true if URL is valid
         */
        fun setUrl(newUrl: String?): Boolean {
            return if (newUrl != null && Patterns.WEB_URL.matcher(newUrl).matches()) {
                url = newUrl
                true
            } else false
        }
    }
}