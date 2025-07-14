package com.revoola.fragment.start.yourway

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
import com.revoola.fragment.start.adapter.RLYourWayListAdapter
import com.revoola.databinding.RlFragYoueWayBinding
import com.revoola.enumclass.RLStartAllMenuModel
import com.revoola.utils.RLConstants
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.revoola.commonobject.RLTools

class RLFragYourWay : RLBaseFragment() {
    val TAG: String = RLFragYourWay::class.java.simpleName
    lateinit var fragBinding: RlFragYoueWayBinding

    private val binding by lazy {
        RlFragYoueWayBinding.inflate(layoutInflater)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         rl_screenSet(false)
        rl_bottomHideShowSet(true)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = rl_inflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_youe_way, container) as RlFragYoueWayBinding
        com.revoola.utils.RLPrefManager.rl_setSomeStringValue(activity, com.revoola.utils.RLPrefManager.current_fragment,"RLFragYourWay" )
        return fragBinding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragBinding.rvYourway.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                fragBinding.rvYourway.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val height =  fragBinding.rvYourway.height
                RLTools.rl_logDPrint(TAG,"RelativeLayout total height: $height pixels")
                RLYourwayList(height)
            }
        })
    }

    private fun RLYourwayList(height: Int) {
        rl_onBackPresAct(fragBinding.inlayTop.ivBack)
        fragBinding.inlayTop.ivTitle.setText(getString(R.string.yourway))
        fragBinding.inlayTop.ivDescription.setText(getString(R.string.youractivityyourway))
        fragBinding.inlayTop.ivhelp.visibility=View.VISIBLE

         val databaseManager= RLDatabaseManagerRead()
         databaseManager.rl_allMenuListRead(RLConstants.YOUR_WAY){ data, error ->
             if (data != null) {
                 try {
                     val gson = Gson()
                     val jsonArray = gson.toJson(data)
                     RLTools.rl_logDPrint(TAG,"Response:- $jsonArray")
                     val listType = object : TypeToken<List<RLStartAllMenuModel>>() {}.type
                     val dataList: List<RLStartAllMenuModel> = gson.fromJson(jsonArray, listType)
                    //Recyclerview Set
                     val linearLayoutMain = LinearLayoutManager(activity)
                     fragBinding.rvYourway.layoutManager = linearLayoutMain
                     val adapter = RLYourWayListAdapter(activity,dataList,height)
                     fragBinding.rvYourway.adapter=adapter
                 }catch (e:Exception){
                    RLTools.rl_logEPrint(TAG,"Catch:- ${e.message}")
                 }
             }
         }
    }

}