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
        rl_screenSet(true)
        rl_bottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = rl_inflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_left_body_heart_video, container) as RlFragLeftBodyHeartVideoBinding
        com.revoola.utils.RLPrefManager.rl_setSomeStringValue(activity, com.revoola.utils.RLPrefManager.current_fragment,"RLFragLeftBodyWithHeartVideo" )
        RLuisetup()
        return fragBinding.root
    }
    private fun RLuisetup() {
        fragBinding.circularProgressBar.rl_setProgress(0)
        fragBinding.progresstext.setText("0%")
        fragBinding.circularProgressBar.rl_setMaxProgress(100)
        fragBinding.circularProgressBar.rl_setProgressColor(resources.getColor(R.color.AppZone5Color))
        fragBinding.circularProgressBar.setBackgroundColor(Color.LTGRAY)
        fragBinding.circularProgressBar.rl_setStrokeWidth(15f)

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

    fun RLUpdateHRPersentage(REVPer: Int, heartRate: Int, burntCalories: Double, totalRev: Double) {
        try {
            if (heartRate>0){
                fragBinding?.inlayHeartrate?.txtNumber?.setText(heartRate.toString())
            }
            if (burntCalories>0){
                fragBinding?.inlayCalories?.txtNumber?.setText(burntCalories.roundToInt().toString())
            }
            if (totalRev>0){
                fragBinding?.inlayEffort?.txtNumber?.setText(totalRev.roundToInt().toString())
            }

        if (REVPer>=100){
            fragBinding.progresstext.setText("100%")
            fragBinding.circularProgressBar.rl_setProgress(100)
        }else if(REVPer>=0){
            fragBinding.progresstext.setText("$REVPer%")
            fragBinding.circularProgressBar.rl_setProgress(REVPer)
        }else{
            fragBinding.progresstext.setText("--%")
            fragBinding.circularProgressBar.rl_setProgress(0)
        }
        val progresscolor = RLTools.rl_calculateCircularGraph(REVPer)
        fragBinding.progresstext.setTextColor(Color.parseColor(progresscolor))
        fragBinding.circularProgressBar.rl_setProgressColor(Color.parseColor(progresscolor))
        }catch (e:Exception){
            RLTools.rl_logEPrint(TAG,"Exception: ${e.message}")
        }


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
           RLTools.rl_logEPrint(TAG,"Exception:- "+e.message)
        }
    }

}