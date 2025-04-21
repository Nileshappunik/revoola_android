package com.revoola.fragment.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatDelegate
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.databinding.*
import com.revoola.utils.RLConstants

class RLFragTermAndCondition : RLBaseFragment() {
    val TAG: String = RLFragTermAndCondition::class.java.simpleName
    lateinit var fragBinding: RlFragTermAndConditionBinding

    
    private val binding by lazy {
        RlFragTermAndConditionBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         RLScreenSet(false)
        RLBottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_term_and_condition, container) as RlFragTermAndConditionBinding
        com.revoola.utils.RLPrefManager.RLSetSomeStringValue(activity, com.revoola.utils.RLPrefManager.current_fragment,"RLFragTermAndCondition")
        RLuisetup()
        return fragBinding.root
    }

    private fun RLuisetup() {
        RLonBackPresAct(fragBinding.ivBack)
        fragBinding.webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            useWideViewPort = true
            loadWithOverviewMode = true
        }

        fragBinding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                request?.url?.let { view?.loadUrl(it.toString()) }
                return true
            }
        }

        fragBinding.webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                // Handle progress updates (e.g., show a progress bar)
            }
        }

        fragBinding.webView.loadUrl(RLConstants.TermCondition) // Load any URL
    }

}