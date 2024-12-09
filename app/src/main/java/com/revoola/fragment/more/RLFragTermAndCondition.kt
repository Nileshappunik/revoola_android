package com.revoola.fragment.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.databinding.*

class RLFragTermAndCondition : RLBaseFragment() {
    val TAG: String = RLFragTermAndCondition::class.java.simpleName
    lateinit var fragBinding: RlFragTermAndConditionBinding

    
    private val binding by lazy {
        RlFragTermAndConditionBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         RLScreenSet(false)
        RLBottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_term_and_condition, container) as RlFragTermAndConditionBinding
        com.revoola.utils.RLPrefManager.RLsetSomeStringValue(activity, com.revoola.utils.RLPrefManager.current_fragment,"RLFragTermAndCondition")
        RLuisetup()
        return fragBinding.root
    }

    private fun RLuisetup() {
        RLonBackPresAct(fragBinding.ivBack)
    }

}