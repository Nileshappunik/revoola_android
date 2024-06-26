package com.example.myfirstapp.fragment.start

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.MediaController
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.myfirstapp.RLBaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.RLMainActivityRL
import com.example.myfirstapp.databinding.RlStartClassesMindBodyBinding
import com.example.myfirstapp.model.RLFulllVideoModel
import com.example.myfirstapp.utils.RLConstants
import com.example.myfirstapp.utils.RLPrefManager
import com.google.gson.Gson

class RLStartClassesMindBody : RLBaseFragment() {
    val TAG: String = RLStartClassesMindBody::class.java.simpleName
    lateinit var fragBinding: RlStartClassesMindBodyBinding
    var classtype:String=""
    var pauseVideo:Boolean=true
    var pauseStopVideoView:Boolean=true

    private val binding by lazy {
        RlStartClassesMindBodyBinding.inflate(layoutInflater)
    }
    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLStartClassesMindBody()
        fragment.arguments = bundle
        return fragment
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
       // activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_start_classes_mind_body, container) as RlStartClassesMindBodyBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLStartClassesMindBody" )
        RLuisetup()
        return fragBinding.root
    }
    private fun RLuisetup() {
        RLstartCountdown()
        (context as RLMainActivityRL).RLhidebottombarcolorwhite()
        classtype=  requireArguments().getString(RLConstants.CLASSTYPE,"")
        val data=  requireArguments().getString("VIDEODATA","")
        val gson = Gson()
        val VideoCardData = gson.fromJson(data, RLFulllVideoModel::class.java)

        RLMindBodyUISet(VideoCardData)
        RLVideoUISet(VideoCardData,data)
    }

    private fun RLVideoUISet(VideoCardData: RLFulllVideoModel, data: String){
        val videoUri = Uri.parse(VideoCardData.streamingUrl)
        // Set the media controller for the VideoView
        val mediaController = MediaController(requireContext())
        mediaController.setAnchorView(fragBinding.videoView)
        fragBinding.videoView.setMediaController(mediaController)
        // Set the URI for the VideoView
        fragBinding.videoView.setVideoURI(videoUri)

        // Start playing the video
        fragBinding.videoView.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.start()
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
            bundle.putString(RLConstants.CLASSTYPE,classtype)
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
        //RLFullScreenVideoView()

    }
    private fun RLMindBodyUISet(VideoData: RLFulllVideoModel) {
        fragBinding.txtTitle.setText(VideoData.rideTitle)
        fragBinding.txtNamewith.setText(VideoData.instructor)
        if (classtype.equals(RLConstants.MIND)){
            //MIND
            fragBinding.rlBodyTimenumber.visibility=View.GONE
            fragBinding.rlMindTimenumber.visibility=View.VISIBLE
            fragBinding.txtMinutes.setText(VideoData.duration+" Class")
        }else{
            //BODY
            fragBinding.rlBodyTimenumber.visibility=View.VISIBLE
            fragBinding.rlMindTimenumber.visibility=View.GONE
            fragBinding.txtMinutesMind.setText(VideoData.duration+" Class")
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
    private fun RLFullScreenVideoView() {
        /*try {
            val metrics = DisplayMetrics()
            activity?.windowManager?.defaultDisplay?.getMetrics(metrics)
            val params = fragBinding.videoView.layoutParams as LinearLayout.LayoutParams
            params.width = metrics.widthPixels
            params.height = metrics.heightPixels
            params.leftMargin = 0
            fragBinding.videoView.layoutParams = params
        }catch (e:Exception){
            Log.e(TAG,"EXCEPTION:- ${e.message}")
        }*/

    }

}