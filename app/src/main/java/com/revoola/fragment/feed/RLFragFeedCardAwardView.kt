package com.revoola.fragment.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.revoola.RLBaseFragment
import com.revoola.api.RLApiClientRet
import com.revoola.databasefirebase.RLAuthManager
import com.revoola.databinding.RlFragFeedCardAwardViewBinding
import com.revoola.model.RLTextOverview
import com.revoola.utils.RLConstants
import com.revoola.utils.RLPrefManager
import com.revoola.viewmodel.RLMainRepository
import com.revoola.viewmodel.RLMainViewModel
import com.revoola.viewmodel.RLMainViewModelFactory


class RLFragFeedCardAwardView : RLBaseFragment(){
    val TAG: String = RLFragFeedCardAwardView::class.java.simpleName

    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragFeedCardAwardView()
        fragment.arguments = bundle
        return fragment
    }

    private val fragBinding by lazy {
        RlFragFeedCardAwardViewBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rl_screenSet(false)
        rl_bottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        RLPrefManager.rl_setSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragFeedCardAwardView" )
        rl_uisetup()
        return fragBinding.root
    }

    private fun rl_uisetup() {
        fragBinding.ivBack.setOnClickListener {
            rl_closeFragment()
        }
        fragBinding.ivAwardDescription.setText("Congratulations Bronze Award For Achieving 20 Effort in Any Your Way Activity.")
        fragBinding.ivAwardSubDescription.setText("Congratulations You Have Achieved an Effort Score of 20")
        val  currentUser= RLAuthManager().rl_getCurrentUser()?.uid?:""
        val cardData = requireArguments().getSerializable(RLConstants.CardData) as RLTextOverview
    }
}