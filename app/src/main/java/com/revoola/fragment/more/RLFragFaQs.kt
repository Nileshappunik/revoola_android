package com.revoola.fragment.more

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.databinding.*
import com.revoola.utils.RLConstants
import com.revoola.utils.RLPrefManager

class RLFragFaQs : RLBaseFragment() {
    val TAG: String = RLFragFaQs::class.java.simpleName
    lateinit var fragBinding: RlFragFaqsBinding
    private var player: ExoPlayer? = null

    private val binding by lazy {
        RlFragFaqsBinding.inflate(layoutInflater)
    }

    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragFaQs()
        fragment.arguments = bundle
        return fragment
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         RLScreenSet(false)
        RLBottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_faqs, container) as RlFragFaqsBinding
        RLPrefManager.RLSetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragFaQs")
        RLuisetup()
        return fragBinding.root
    }

    private fun RLuisetup() {
        RLonBackPresAct(fragBinding.ivBack)
        val isFAqs=requireArguments().getBoolean("isFAqs")
        if (isFAqs){
            fragBinding.ivTitle.setText(getString(R.string.faqs))
            fragBinding.videoView.visibility=View.GONE
            fragBinding.webView.visibility=View.VISIBLE
            RLFAQSCode()
        }else{
            fragBinding.ivTitle.setText(getString(R.string.aquickintroduction))
            fragBinding.webView.visibility=View.GONE
            fragBinding.videoView.visibility=View.VISIBLE
            //RLQuickIntroductionCode()
            RLInitializePlayer()
        }

    }

    private fun RLInitializePlayer() {
        if (player == null) {
            player = ExoPlayer.Builder(requireContext())
                .setMediaSourceFactory(DefaultMediaSourceFactory(requireContext())) // HLS Supported
                .build()

            fragBinding.videoView.player = player

            val mediaItem = MediaItem.Builder()
                .setUri(Uri.parse(RLConstants.Quick_Introduction_Url))
                .setMimeType(MimeTypes.APPLICATION_M3U8) // HLS Format
                .build()

            player?.setMediaItem(mediaItem)
            player?.prepare()
            player?.playWhenReady = true
        }
    }

    private fun RLQuickIntroductionCode() {
        player = ExoPlayer.Builder(requireContext()).build().also { exoPlayer ->
            fragBinding.videoView.player = exoPlayer
            val mediaItem = MediaItem.fromUri(RLConstants.Quick_Introduction_Url)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
        }
    }

    override fun onPause() {
        super.onPause()
        player?.pause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        player?.release()
        player = null
    }

    private fun RLFAQSCode() {
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

        fragBinding.webView.loadUrl(RLConstants.FaQS_URL) // Load any URL
    }

}