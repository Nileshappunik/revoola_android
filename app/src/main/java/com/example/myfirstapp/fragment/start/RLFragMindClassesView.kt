package com.example.myfirstapp.fragment.start

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.myfirstapp.RLBaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.RLMainActivityRL
import com.example.myfirstapp.databinding.RlFragMindClassesViewBinding
import com.example.myfirstapp.model.RLFulllVideoModel
import com.example.myfirstapp.utils.RLConstants
import com.example.myfirstapp.utils.RLPrefManager
import com.google.gson.Gson
class RLFragMindClassesView : RLBaseFragment() {
    val TAG: String = RLFragMindClassesView::class.java.simpleName
    lateinit var fragBinding: RlFragMindClassesViewBinding
    var classtype:String=""
    private val binding by lazy {
        RlFragMindClassesViewBinding.inflate(layoutInflater)
    }
    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragMindClassesView()
        fragment.arguments = bundle
        return fragment
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_mind_classes_view, container) as RlFragMindClassesViewBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragMindClassesView" )
        RLuisetup()
        return fragBinding.root
    }
    private fun RLuisetup() {
        classtype=  requireArguments().getString(RLConstants.CLASSTYPE,"")
        RLonBackPresAct(fragBinding.ivBack)
        val data=  requireArguments().getString("VIDEODATA","")
        val gson = Gson()
        val VideoCardData = gson.fromJson(data, RLFulllVideoModel::class.java)
        if (classtype.equals(RLConstants.MIND)){
            fragBinding.layWorklog.visibility=View.GONE
            fragBinding.viewTimevideo.visibility=View.GONE
            fragBinding.imgFavourite.setImageResource(R.drawable.ic_saved)
            fragBinding.txtVideo.setTextColor(resources.getColor(R.color.AppTextGrayColor))
            val audioVideoType=  requireArguments().getString("AUDIOVIDEOTYPE","")
            fragBinding.txtVideo.setText(audioVideoType)
            if (audioVideoType.equals("Video")){
                fragBinding.imgVideo.setImageResource(R.drawable.ic_video)
            }else{
                fragBinding.imgVideo.setImageResource(R.drawable.ic_audio)
            }
            RLMindUiSetup(VideoCardData)
        }else{
            fragBinding.layWorklog.visibility=View.VISIBLE
            fragBinding.viewTimevideo.visibility=View.VISIBLE
            fragBinding.imgFavourite.setImageResource(R.drawable.ic_saved_gray)
            RLBodyUiSetup(VideoCardData)
        }
        fragBinding.rlSchdual.setOnClickListener {
            (context as RLMainActivityRL).RLloadFrag(RLClassesSchedule(), TAG, true,null, false)
        }
    }
    private fun RLMindUiSetup(VideoData:RLFulllVideoModel){
        fragBinding.txtTitle.setText(VideoData.rideTitle)
        fragBinding.txtVideoTitle.setText(VideoData.rideTitle)
        fragBinding.txtNamewith.setText(VideoData.instructor)
        fragBinding.txtTrainerName.setText(VideoData.instructor)
        fragBinding.txtTotalClass.setText(VideoData.instructorClasses+" CLASSES")
        fragBinding.txtVideoDescription.setText(VideoData.rideDescription)
        fragBinding.txtMinutes.setText(VideoData.duration)
        Glide.with(requireContext()).load(VideoData.imageLinkInstructor)
            //.placeholder(R.drawable.wellcome).error(R.drawable.wellcome)
            .into(fragBinding.imgTraner)
        Glide.with(requireContext()).load(VideoData.imageLinkSquareV2)
            //.placeholder(R.drawable.wellcome).error(R.drawable.wellcome)
            .into(fragBinding.imgMainBanner)
    }
    private fun RLBodyUiSetup(VideoData:RLFulllVideoModel){
        fragBinding.txtTitle.setText(VideoData.rideTitle)
        fragBinding.txtVideoTitle.setText(VideoData.rideTitle)
        fragBinding.txtNamewith.setText(VideoData.instructor)
        fragBinding.txtTrainerName.setText(VideoData.instructor)
        fragBinding.txtMinutes.setText(VideoData.duration)
        fragBinding.txtVideoDescription.setText(VideoData.rideDescription)
        fragBinding.txtTotalClass.setText(VideoData.instructorClasses+" CLASSES")

        fragBinding.txtWarmupMinutes.setText(VideoData.minwarmup+" MIN")
        fragBinding.txtCooldownMinutes.setText(VideoData.mincooldown+" MIN")
        fragBinding.txtWorkoutMinutes.setText(VideoData.mininstruction+" MIN")

        Glide.with(requireContext()).load(VideoData.imageLinkInstructor)
            //.placeholder(R.drawable.wellcome).error(R.drawable.wellcome)
            .into(fragBinding.imgTraner)
        Glide.with(requireContext()).load(VideoData.imageLinkSquareV2)
            //.placeholder(R.drawable.wellcome).error(R.drawable.wellcome)
            .into(fragBinding.imgMainBanner)

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