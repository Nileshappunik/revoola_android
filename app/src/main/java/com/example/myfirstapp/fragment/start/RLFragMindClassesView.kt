package com.example.myfirstapp.fragment.start

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.myfirstapp.RLBaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.databinding.RlFragMindClassesViewBinding
import com.example.myfirstapp.utils.RLConstants
import com.example.myfirstapp.utils.RLPrefManager


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
        if (classtype.equals(RLConstants.MIND)){
            fragBinding.layWorklog.visibility=View.GONE
            fragBinding.viewTimevideo.visibility=View.GONE
            fragBinding.imgFavourite.setImageResource(R.drawable.ic_saved)
            fragBinding.imgVideo.setImageResource(R.drawable.ic_video)
            fragBinding.txtVideo.setText(R.string.video)
            fragBinding.txtVideo.setTextColor(resources.getColor(R.color.AppTextGrayColor))
            fragBinding.txtTitle.setText(R.string.introducinglaughteryouga)
            fragBinding.txtVideoTitle.setText(R.string.introducinglaughteryouga)
            fragBinding.txtNamewith.setText("SAM REHAN")
            fragBinding.txtTrainerName.setText("SAM REHAN")
        }else{
            fragBinding.layWorklog.visibility=View.VISIBLE
            fragBinding.viewTimevideo.visibility=View.VISIBLE
            fragBinding.imgFavourite.setImageResource(R.drawable.ic_saved_gray)
            fragBinding.imgVideo.setImageResource(R.drawable.ic_medium)
            fragBinding.txtVideo.setText("INTERMEDI...")
            fragBinding.txtVideo.setTextColor(resources.getColor(R.color.AppOrangeColor))
            fragBinding.txtTitle.setText(R.string.mindbodyharmony)
            fragBinding.txtVideoTitle.setText(R.string.mindbodyharmony)
            fragBinding.txtNamewith.setText("SALLYKING")
            fragBinding.txtTrainerName.setText("SALLYKING")

        }

    }
}