package com.example.myfirstapp.fragment.start.yourway

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.myfirstapp.RLBaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.RLMainActivityRL
import com.example.myfirstapp.databasefirebase.RLDatabaseManagerRead
import com.example.myfirstapp.fragment.start.adapter.RLYourWayListAdapter
import com.example.myfirstapp.databinding.RlFragYoueWayBinding
import com.example.myfirstapp.enumclass.RLStartAllMenuModel
import com.example.myfirstapp.utils.RLConstants
import com.example.myfirstapp.utils.RLPrefManager
import com.example.myfirstapp.utils.loadSvg
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class RLFragYourWay : RLBaseFragment() {
    val TAG: String = RLFragYourWay::class.java.simpleName
    lateinit var fragBinding: RlFragYoueWayBinding

    private val binding by lazy {
        RlFragYoueWayBinding.inflate(layoutInflater)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_youe_way, container) as RlFragYoueWayBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragYourWay" )
        return fragBinding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragBinding.rvYourway.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                fragBinding.rvYourway.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val height =  fragBinding.rvYourway.height
                Log.d(TAG,"RelativeLayout total height: $height pixels")
                RLYourwayList(height)
            }
        })
    }

    private fun RLYourwayList(height: Int) {
        (context as RLMainActivityRL).RLshowbottombarcolorwhite()
        (context as RLMainActivityRL).RLbottombarcolorwhite()
        RLonBackPresAct(fragBinding.inlayTop.ivBack)
        fragBinding.inlayTop.ivTitle.setText(getString(R.string.yourway))
        fragBinding.inlayTop.ivDescription.setText(getString(R.string.youractivityyourway))

         val databaseManager= RLDatabaseManagerRead()
         databaseManager.RLALLMENULISTRead(RLConstants.YOURWAY){ data, error ->
             if (data != null) {
                 try {
                     val gson = Gson()
                     val jsonArray = gson.toJson(data)
                     Log.d(TAG,"Response:- $jsonArray")
                     val listType = object : TypeToken<List<RLStartAllMenuModel>>() {}.type
                     val dataList: List<RLStartAllMenuModel> = gson.fromJson(jsonArray, listType)
                    //Recyclerview Set
                     val linearLayoutMain = LinearLayoutManager(activity)
                     fragBinding.rvYourway.layoutManager = linearLayoutMain
                     val adapter = RLYourWayListAdapter(activity,dataList,height)
                     fragBinding.rvYourway.adapter=adapter
                 }catch (e:Exception){
                     Log.e(TAG,"Catch:- ${e.message}")
                 }
             }
         }
    }

}