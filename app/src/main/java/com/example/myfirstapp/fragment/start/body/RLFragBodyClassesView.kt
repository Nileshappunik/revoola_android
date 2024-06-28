package com.example.myfirstapp.fragment.start.body

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
import com.example.myfirstapp.fragment.start.classes.RLClassesSchedule
import com.example.myfirstapp.model.RLFulllVideoModel
import com.example.myfirstapp.utils.RLConstants
import com.example.myfirstapp.utils.RLPrefManager
import com.google.gson.Gson
class RLFragBodyClassesView : RLBaseFragment() {
    val TAG: String = RLFragBodyClassesView::class.java.simpleName
    lateinit var fragBinding: RlFragMindClassesViewBinding
    private val binding by lazy {
        RlFragMindClassesViewBinding.inflate(layoutInflater)
    }
    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragBodyClassesView()
        fragment.arguments = bundle
        return fragment
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_mind_classes_view, container) as RlFragMindClassesViewBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragBodyClassesView" )
        RLuisetup()
        return fragBinding.root
    }
    private fun RLuisetup() {
        RLonBackPresAct(fragBinding.ivBack)
        val data=  requireArguments().getString("VIDEODATA","")
        val gson = Gson()
        val VideoCardData = gson.fromJson(data, RLFulllVideoModel::class.java)
        fragBinding.layWorklog.visibility=View.VISIBLE
        fragBinding.viewTimevideo.visibility=View.VISIBLE
        fragBinding.imgFavourite.setImageResource(R.drawable.ic_saved_gray)
        RLBodyUiSetup(VideoCardData)
        RLClickToSechedule(data,RLConstants.BODY,"")
        fragBinding.btnStartclass.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("VIDEODATA",data)
            (context as RLMainActivityRL).RLloadFrag(RLFragBodyClassSensorChooes().newInstance(bundle), TAG, true,null, false)
        }
    }
    private fun RLClickToSechedule(data: String, classtype: String?, audioVideoType: String?) {
        fragBinding.rlSchdual.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("VIDEODATA",data)
            bundle.putString(RLConstants.CLASSTYPE,classtype)
            bundle.putString("AUDIOVIDEOTYPE",audioVideoType)
            (context as RLMainActivityRL).RLloadFrag(RLClassesSchedule().newInstance(bundle), TAG, true,null, false)
        }
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