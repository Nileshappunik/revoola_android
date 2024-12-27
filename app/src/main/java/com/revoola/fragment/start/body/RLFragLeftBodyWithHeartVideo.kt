package com.revoola.fragment.start.body

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.databinding.RlFragLeftBodyHeartVideoBinding
import com.revoola.commonobject.RLTools
import kotlin.math.roundToInt

class RLFragLeftBodyWithHeartVideo : RLBaseFragment() {
    val TAG: String = RLFragLeftBodyWithHeartVideo::class.java.simpleName
    lateinit var fragBinding: RlFragLeftBodyHeartVideoBinding

    private val binding by lazy {
        RlFragLeftBodyHeartVideoBinding.inflate(layoutInflater)
    }

    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragLeftBodyWithHeartVideo()
        fragment.arguments = bundle
        return fragment
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        RLScreenSet(true)
        RLBottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_left_body_heart_video, container) as RlFragLeftBodyHeartVideoBinding
        com.revoola.utils.RLPrefManager.RLsetSomeStringValue(activity, com.revoola.utils.RLPrefManager.current_fragment,"RLFragLeftBodyWithHeartVideo" )
        RLuisetup()
        return fragBinding.root
    }
    private fun RLuisetup() {
        fragBinding.circularProgressBar.RLsetProgress(0)
        fragBinding.progresstext.setText("0%")
        fragBinding.circularProgressBar.RLsetMaxProgress(100)
        fragBinding.circularProgressBar.RLsetProgressColor(resources.getColor(R.color.AppZone5Color))
        fragBinding.circularProgressBar.setBackgroundColor(Color.LTGRAY)
        fragBinding.circularProgressBar.RLsetStrokeWidth(15f)

        fragBinding.inlayEffort.imgIcon.setImageResource(R.drawable.ic_heart)
        fragBinding.inlayEffort.txtName.setText(R.string.effort)
        fragBinding.inlayEffort.txtNumber.setText("0")

        fragBinding.inlayCalories.imgIcon.setImageResource(R.drawable.fd_calories_green)
        fragBinding.inlayCalories.txtName.setText(R.string.calories)
        fragBinding.inlayCalories.txtNumber.setText("0")

        fragBinding.inlayHeartrate.imgIcon.setImageResource(R.drawable.ic_heartrate)
        fragBinding.inlayHeartrate.txtName.setText(R.string.heartrate)
        fragBinding.inlayHeartrate.txtNumber.setText("0")

        fragBinding.inlayCadence.imgIcon.setImageResource(R.drawable.ic_cadence)
        fragBinding.inlayCadence.txtName.setText(R.string.cadence)
        fragBinding.inlayCadence.txtNumber.setText("--")

        fragBinding.inlayTime.imgIcon.setImageResource(R.drawable.fd_active_time_green)
        fragBinding.inlayTime.txtName.setText(R.string.time)
        fragBinding.inlayTime.progressView2.visibility=View.GONE

    }

    fun RLUpdateVideoTime(rLformatTime: String) {
        if (!rLformatTime.equals("00:00")){
            fragBinding.inlayTime.txtNumber.setText(rLformatTime)
        }

    }

    fun RLUpdateHRTime(Heartrate: String,calories:String,totalRev:Double) {
        fragBinding?.inlayHeartrate?.txtNumber?.setText(Heartrate)
        fragBinding?.inlayEffort?.txtNumber?.setText(totalRev.roundToInt().toString())
        fragBinding?.inlayCalories?.txtNumber?.setText(calories)
    }

    fun RLUpdateHRPersentage(REVPer : Int) {
        if (REVPer>=100){
            fragBinding.progresstext.setText("100%")
            fragBinding.circularProgressBar.RLsetProgress(100)
        }else if(REVPer>=0){
            fragBinding.progresstext.setText("$REVPer%")
            fragBinding.circularProgressBar.RLsetProgress(REVPer)
        }else{
            fragBinding.progresstext.setText("--%")
            fragBinding.circularProgressBar.RLsetProgress(0)
        }
        val progresscolor = RLTools.RLCalculateCircularGraph(REVPer)
        fragBinding.progresstext.setTextColor(Color.parseColor(progresscolor))
        fragBinding.circularProgressBar.RLsetProgressColor(Color.parseColor(progresscolor))
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            // Show the status bar and navigation bar again and set dark color
            @Suppress("DEPRECATION")
            requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            @Suppress("DEPRECATION")
            requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        }catch (e:Exception){
           RLTools.RlLogEPrint(TAG,"Exception:- "+e.message)
        }
    }

}