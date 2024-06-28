package com.example.myfirstapp.fragment.start.body

import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.VideoView
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.myfirstapp.RLBaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.RLMainActivityRL
import com.example.myfirstapp.databinding.RlFragBodyClassesNormalVideoStartBinding
import com.example.myfirstapp.fragment.start.classes.RLFragClassWorkoutComplete
import com.example.myfirstapp.model.RLFulllVideoModel
import com.example.myfirstapp.utils.RLConstants
import com.example.myfirstapp.utils.RLPrefManager
import com.google.gson.Gson
import java.util.concurrent.TimeUnit

class RLFragBodyClassesNormalVideoStart : RLBaseFragment() {
    val TAG: String = RLFragBodyClassesNormalVideoStart::class.java.simpleName
    lateinit var fragBinding: RlFragBodyClassesNormalVideoStartBinding
    var pauseVideo:Boolean=true
    var pauseStopVideoView:Boolean=true
    private val handler = Handler(Looper.getMainLooper())

    private val binding by lazy {
        RlFragBodyClassesNormalVideoStartBinding.inflate(layoutInflater)
    }
    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragBodyClassesNormalVideoStart()
        fragment.arguments = bundle
        return fragment
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_body_classes_normal_video_start, container) as RlFragBodyClassesNormalVideoStartBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragBodyClassesNormalVideoStart" )
        RLuisetup()
        @Suppress("DEPRECATION")
        requireActivity().window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        return fragBinding.root
    }
    private fun RLuisetup() {
        RLstartCountdown()
        (context as RLMainActivityRL).RLhidebottombarcolorwhite()
        val data=  requireArguments().getString("VIDEODATA","")
        val gson = Gson()
        val VideoCardData = gson.fromJson(data, RLFulllVideoModel::class.java)

        RLMindBodyUISet(VideoCardData)
        RLVideoUISet(VideoCardData,data)
    }
    private fun RLVideoUISet(VideoCardData: RLFulllVideoModel, data: String){
        fragBinding.inlayTime.progressView1.visibility=View.GONE
        fragBinding.inlayRelaxation.progressView1.visibility=View.GONE
        fragBinding.inlayRelaxed.progressView1.visibility=View.GONE

        fragBinding.inlayTime.progressView2.visibility=View.VISIBLE
        fragBinding.inlayRelaxation.progressView2.visibility=View.VISIBLE
        fragBinding.inlayRelaxed.progressView2.visibility=View.VISIBLE

        fragBinding.inlayRelaxation.imgIcon.setImageResource(R.drawable.ic_mind_read)
        fragBinding.inlayRelaxation.txtName.setText("RELAXATION")
        fragBinding.inlayRelaxation.txtNumber.setText("0")

        fragBinding.inlayRelaxed.imgIcon.setImageResource(R.drawable.ic_mind_read)
        fragBinding.inlayRelaxed.txtName.setText("RELAXED")
        fragBinding.inlayRelaxed.txtNumber.setText("0%")

        //val videoUri = Uri.parse(VideoCardData.streamingUrl)
        val videoUri = Uri.parse(VideoCardData.streamingUrlIphonex)

        /*val mediaController = MediaController(requireContext())
        mediaController.setAnchorView(fragBinding.videoView)
        fragBinding.videoView.setMediaController(mediaController)*/

        // Set the URI for the VideoView
        fragBinding.videoView.setVideoURI(videoUri)

        // Start playing the video
        fragBinding.videoView.setOnPreparedListener { mediaPlayer ->
            RLAdjustAspectRatio( fragBinding.videoView, mediaPlayer.videoWidth, mediaPlayer.videoHeight)
            fragBinding.seekbarVideo.max=fragBinding.videoView.duration
            mediaPlayer.start()
            fragBinding.layPlayStop.visibility=View.GONE
            fragBinding.inlayTime.txtNumber.setText(RLformatTime(fragBinding.videoView.duration))
            handler.post(RLupdateSeekBarRunnable)
        }

        // Handle errors
        fragBinding.videoView.setOnErrorListener { mediaPlayer, what, extra ->
            // Handle the error
            true
        }

        fragBinding.btnStop.setOnClickListener {
            fragBinding.videoView.stopPlayback()
            val bundle = Bundle()
            bundle.putString("VIDEODATA",data)
            bundle.putString(RLConstants.CLASSTYPE,RLConstants.BODY)
            (context as RLMainActivityRL).RLhidebottombarcolorwhite()
            (context as RLMainActivityRL).RLloadFrag(RLFragClassWorkoutComplete().newInstance(bundle), TAG, true, null, false)

        }
        fragBinding.btnPauseResume.setOnClickListener {
            if (pauseVideo){
                fragBinding.videoView.pause()
                pauseVideo=false
                fragBinding.txtPauseResume.setText("RESUME")
                fragBinding.btnPauseResume.setImageResource(R.drawable.ic_playbutton2)
            }else{
                pauseVideo=true
                fragBinding.videoView.start()
                fragBinding.txtPauseResume.setText("PAUSE")
                fragBinding.btnPauseResume.setImageResource(R.drawable.ic_pause_button)
            }
        }
        fragBinding.relayVideoplay.setOnClickListener {
            if (pauseStopVideoView){
                pauseStopVideoView=false
                fragBinding.layPlayStop.visibility=View.GONE
            }else{
                pauseStopVideoView=true
                fragBinding.layPlayStop.visibility=View.VISIBLE
            }
        }

    }
    private val RLupdateSeekBarRunnable = object : Runnable {
        override fun run() {
            fragBinding.seekbarVideo.progress = fragBinding.videoView.currentPosition
            handler.postDelayed(this, 1000)
            Log.d(TAG,"TIME:- ${fragBinding.videoView.currentPosition}")
            fragBinding.txtVideoTime.setText(RLformatTime(fragBinding.videoView.currentPosition))
        }
    }
    private fun RLformatTime(milliseconds: Int): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds.toLong())
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds.toLong()) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
    private fun RLMindBodyUISet(VideoData: RLFulllVideoModel) {
        fragBinding.txtTitle.setText(VideoData.rideTitle)
        fragBinding.txtNamewith.setText(VideoData.instructor)
        fragBinding.txtVideo.setText(VideoData.difficulty)
        if(VideoData.difficulty.equals("Beginner")){
            fragBinding.imgVideo.setImageResource(R.drawable.ic_easy)
            fragBinding.txtVideo.setTextColor(resources.getColor(R.color.AppMainColor))
        }else if (VideoData.difficulty.equals("Advanced")){
            fragBinding.imgVideo.setImageResource(R.drawable.ic_hard)
            fragBinding.txtVideo.setTextColor(resources.getColor(R.color.AppRedColor))
        }else{
            fragBinding.imgVideo.setImageResource(R.drawable.ic_medium)
            fragBinding.txtVideo.setTextColor(resources.getColor(R.color.AppOrangeColor))
        }
    }
    private fun RLstartCountdown() {
        var count = 5
        var countDownTimer: CountDownTimer = object : CountDownTimer(5000, 1000) { // Countdown from 5 seconds
            override fun onTick(millisUntilFinished: Long) {
                fragBinding.txtCountdown.text = "$count" // Display current count
                count--
            }
            override fun onFinish() {
                fragBinding.relayCountdown.visibility=View.GONE
            }
        }.start()
    }
    private fun RLAdjustAspectRatio(videoView: VideoView, videoWidth: Int, videoHeight: Int) {
        val layoutParams = videoView.layoutParams
        val viewWidth = videoView.width.toFloat()
        val viewHeight = videoView.height.toFloat()
        val aspectRatio = videoWidth.toFloat() / videoHeight

        if (viewWidth / viewHeight > aspectRatio) {
            // Adjust height to fit the width
            layoutParams.height = (viewWidth / aspectRatio).toInt()
            layoutParams.width = viewWidth.toInt()
        } else {
            // Adjust width to fit the height
            layoutParams.width = (viewHeight * aspectRatio).toInt()
            layoutParams.height = viewHeight.toInt()
        }

        videoView.layoutParams = layoutParams
    }
    override fun onDestroyView() {
        super.onDestroyView()
        fragBinding.videoView.stopPlayback()
        // Show the status bar and navigation bar again and set dark color
        @Suppress("DEPRECATION")
        requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        @Suppress("DEPRECATION")
        requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }
}