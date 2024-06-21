package com.example.myfirstapp.fragment.start

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import com.example.myfirstapp.RLBaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.RLMainActivityRL
import com.example.myfirstapp.databinding.RlFragClassesBinding
import com.example.myfirstapp.utils.RLConstants
import com.example.myfirstapp.utils.RLPrefManager


class RLFragClasses : RLBaseFragment() {
    val TAG: String = RLFragClasses::class.java.simpleName
    lateinit var fragBinding: RlFragClassesBinding

    private val binding by lazy {
        RlFragClassesBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_classes, container) as RlFragClassesBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragClasses" )
        RLuisetup()
        return fragBinding.root
    }
    private fun RLuisetup() {
        RLonBackPresAct(fragBinding.ivBack)

        fragBinding.layYourmind.imgClass.visibility=View.VISIBLE
        fragBinding.layYourmind.imgClass.setImageResource(R.drawable.ic_mind_read)
        fragBinding.layYourmind.imgFull.setImageResource(R.drawable.ride)
        fragBinding.layYourmind.txtName.visibility=View.GONE
        fragBinding.layYourmind.txtClassName.setText(R.string.foryourmind)
        fragBinding.layYourmind.txtClassName.visibility=View.VISIBLE


        fragBinding.layYourbody.imgClass.visibility=View.VISIBLE
        fragBinding.layYourbody.imgClass.setImageResource(R.drawable.ic_heart)
        fragBinding.layYourmind.imgFull.setImageResource(R.drawable.pilates)
        fragBinding.layYourbody.txtName.visibility=View.GONE
        fragBinding.layYourbody.txtClassName.setText(R.string.foryourbody)
        fragBinding.layYourbody.txtClassName.visibility=View.VISIBLE

        fragBinding.layYourmind.imgFull.setOnClickListener {
            (context as RLMainActivityRL).RLshowbottombarcolorwhite()
            (context as RLMainActivityRL).RLbottombarcolorwhite()
            val bundle = Bundle()
            bundle.putString(RLConstants.CLASSTYPE,RLConstants.MIND)
            (context as RLMainActivityRL).RLloadFrag(RLFragMindClasses().newInstance(bundle), TAG, true, RLFragMindClasses::class.java.simpleName, false)

        }
        fragBinding.layYourbody.imgFull.setOnClickListener {
            (context as RLMainActivityRL).RLshowbottombarcolorwhite()
            (context as RLMainActivityRL).RLbottombarcolorwhite()
            val bundle = Bundle()
            bundle.putString(RLConstants.CLASSTYPE,RLConstants.BODY)
            (context as RLMainActivityRL).RLloadFrag(RLFragMindClasses().newInstance(bundle), TAG, true, RLFragMindClasses::class.java.simpleName, false)

        }

    }
}