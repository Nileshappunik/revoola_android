package com.revoola.fragment.more

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.revoola.R
import com.revoola.databasefirebase.RLDatabaseManagerRead
import com.revoola.databinding.RlFragGetStartedVideoPlayBinding
import com.revoola.firebaseModel.RLGetStartedHelpVideo


class RLFragGetStartedVideoPlay(private val HelpType: String)  : DialogFragment() {
    private val TAG: String = RLFragGetStartedVideoPlay::class.java.simpleName
    private lateinit var fragBinding: RlFragGetStartedVideoPlayBinding
    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        fragBinding = RlFragGetStartedVideoPlayBinding.inflate(inflater, container, false)
        setupUI()
        return fragBinding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    private fun setupUI() {
        fragBinding.ivBack.setOnClickListener {
            player?.release()
            player = null
            dismiss()
        }
        RLGetVideoLink()

    }

    private fun RLInitializePlayer(videoUrl:String) {
        player = ExoPlayer.Builder(requireContext()).build()
        fragBinding.videoView.player = player

        val mediaItem = MediaItem.Builder()
            .setUri(Uri.parse(videoUrl))
            .setMimeType(MimeTypes.APPLICATION_M3U8) // Set HLS Format
            .build()

        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.playWhenReady = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        player?.release()
        player = null
    }

    private fun RLGetVideoLink() {
        RLDatabaseManagerRead().RlHelpVideoGetDataRead(HelpType){ data, error ->
            if (data != null) {
                val gson = Gson()
                val jsonObject = gson.toJson(data)
                val videoData = gson.fromJson(jsonObject, RLGetStartedHelpVideo::class.java)
                if (!videoData.url.isNullOrEmpty()){
                    RLInitializePlayer(videoData.url)
                }
            }
        }

    }

}
