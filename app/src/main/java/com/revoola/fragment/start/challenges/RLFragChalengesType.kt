package com.revoola.fragment.start.challenges

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.databasefirebase.RLDatabaseManagerRead
import com.revoola.databinding.RlFragChalengesTypeBinding
import com.revoola.enumclass.RLStartAllMenuModel
import com.revoola.fragment.start.challenges.adapter.RLChallengesListAdapter
import com.revoola.utils.RLConstants
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.revoola.commonobject.RLTools
import com.revoola.utils.RLPrefManager

class RLFragChalengesType : RLBaseFragment() {
    val TAG: String = RLFragChalengesType::class.java.simpleName
   // lateinit var fragBinding: RlFragChalengesTypeBinding

    private val fragBinding by lazy {
        RlFragChalengesTypeBinding.inflate(layoutInflater)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rl_screenSet(false)
        rl_bottomHideShowSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
       // fragBinding = rl_inflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_chalenges_type, container) as RlFragChalengesTypeBinding
        RLPrefManager.rl_setSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragChalengesType" )
        return fragBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragBinding.rvChallenges.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                fragBinding.rvChallenges.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val height =  fragBinding.rvChallenges.height
                RLTools.rl_logDPrint(TAG,"RelativeLayout total height: $height pixels")
                RLChallengesList(height)
            }
        })
    }

    private fun RLChallengesList(height: Int) {
        rl_onBackPresAct(fragBinding.inlayTop.ivBack)
        fragBinding.inlayTop.ivBack.setOnClickListener {
            parentFragmentManager.popBackStack()
            rl_bottomHideShowSet(true)
        }
        fragBinding.inlayTop.ivTitle.setText(getString(R.string.challengessmall))
        fragBinding.inlayTop.ivDescription.setText(getString(R.string.typeofchallenge))
        RLTools.RLhideShowHelpDialog(requireContext(), "challenge_selectType",  fragBinding.inlayTop.ivhelp)
        val databaseManager= RLDatabaseManagerRead()
        databaseManager.rl_allMenuListRead(RLConstants.CHALLENGES){ data, error ->
            if (data != null) {
                try {
                    val gson = Gson()
                    val jsonArray = gson.toJson(data)
                    val listType = object : TypeToken<List<RLStartAllMenuModel>>() {}.type
                    val dataList: List<RLStartAllMenuModel> = gson.fromJson(jsonArray, listType)
                    //Recyclerview Set
                    val linearLayoutMain = LinearLayoutManager(activity)
                    fragBinding.rvChallenges.layoutManager = linearLayoutMain
                    val adapter = RLChallengesListAdapter(activity,dataList,height)
                    fragBinding.rvChallenges.adapter=adapter

                }catch (e:Exception){
                   RLTools.rl_logEPrint(TAG,"Catch:- ${e.message}")
                }
            }else{
               RLTools.rl_logEPrint(TAG,"Null Data:- $data")
            }
        }
    }

}