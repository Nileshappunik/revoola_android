package com.revoola.fragment.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.revoola.RLBaseFragment
import com.revoola.databinding.*
import com.revoola.R
import com.revoola.utils.RLPrefManager

class RLFragTermAndCondition : RLBaseFragment() {
    private val TAG: String = RLFragTermAndCondition::class.java.simpleName

    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragTermAndCondition()
        fragment.arguments = bundle
        return fragment
    }

    private val fragBinding by lazy {
        RlFragTermAndConditionBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rl_screenSet(false)
        rl_bottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        RLPrefManager.rl_setSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragTermAndCondition")
        rl_uisetup()
        return fragBinding.root
    }

    private fun rl_uisetup() {
        rl_onBackPresAct(fragBinding.ivBack)

        val isTermAndCondition = arguments?.getBoolean("isTermAndCondition", false) ?: false
        if (isTermAndCondition){
            // Load from assets
            fragBinding.pdfView.fromAsset("revoolaterms.pdf")
                .enableSwipe(true) // allows horizontal swiping
                .swipeHorizontal(false) // set to true if you want horizontal scroll
                .enableDoubletap(true) // double tap to zoom
                .defaultPage(0)
                .spacing(8) // space between pages in dp
                .load()
        }else{
            // Load from assets
            fragBinding.pdfView.fromAsset("revoolaprivacypolicy.pdf")
                .enableSwipe(true) // allows horizontal swiping
                .swipeHorizontal(false) // set to true if you want horizontal scroll
                .enableDoubletap(true) // double tap to zoom
                .defaultPage(0)
                .spacing(8) // space between pages in dp
                .load()
        }


    }

}