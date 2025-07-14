package com.revoola.fragment.start.classes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.activity.RLMainActivityRL
import com.revoola.databinding.RlFragClassesBinding
import com.revoola.fragment.start.body.RLFragBodyClasses
import com.revoola.fragment.start.mind.RLFragMindClasses


class RLFragClasses : RLBaseFragment() {
    val TAG: String = RLFragClasses::class.java.simpleName
    lateinit var fragBinding: RlFragClassesBinding

    private val binding by lazy {
        RlFragClassesBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         rl_screenSet(false)
        rl_bottomHideShowSet(true)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = rl_inflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_classes, container) as RlFragClassesBinding
        com.revoola.utils.RLPrefManager.rl_setSomeStringValue(activity, com.revoola.utils.RLPrefManager.current_fragment,"RLFragClasses" )
        RLuisetup()
        return fragBinding.root
    }
    private fun RLuisetup() {
        rl_onBackPresAct(fragBinding.ivBack)

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
            (context as RLMainActivityRL).rl_loadFrag(RLFragMindClasses(), TAG, true, null, false)

        }
        fragBinding.layYourbody.imgFull.setOnClickListener {
            (context as RLMainActivityRL).rl_loadFrag(RLFragBodyClasses(), TAG, true, null, false)

        }



    }
}