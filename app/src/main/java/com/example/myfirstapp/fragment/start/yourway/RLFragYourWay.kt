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
    private fun RLuisetup(dataList: List<RLStartAllMenuModel>,height: Int) {

         fragBinding.inlayWalk.imgType.RLadjustWidthToHeight()
         fragBinding.inlayRun.imgType.RLadjustWidthToHeight()
         fragBinding.inlayRide.imgType.RLadjustWidthToHeight()
         fragBinding.inlayWorkout.imgType.RLadjustWidthToHeight()

         fragBinding.inlayWalk.txtTypename.setText(dataList[0].title)
         fragBinding.inlayWalk.txtDescription.setText(dataList[0].description)
         Glide.with(requireContext()).load(dataList[0].img).into(fragBinding.inlayWalk.imgType)
         fragBinding.inlayWalk.imgTypeicon.loadSvg(dataList[0].type)

         fragBinding.inlayRun.txtTypename.setText(dataList[1].title)
         fragBinding.inlayRun.txtDescription.setText(dataList[1].description)
         Glide.with(requireContext()).load(dataList[1].img).into(fragBinding.inlayRun.imgType)
         fragBinding.inlayRun.imgTypeicon.loadSvg(dataList[1].type)

         fragBinding.inlayRide.txtTypename.setText(dataList[2].title)
         fragBinding.inlayRide.txtDescription.setText(dataList[2].description)
         Glide.with(requireContext()).load(dataList[2].img).into(fragBinding.inlayRide.imgType)
         fragBinding.inlayRide.imgTypeicon.loadSvg(dataList[2].type)

         fragBinding.inlayWorkout.txtTypename.setText(dataList[3].title)
         fragBinding.inlayWorkout.txtDescription.setText(dataList[3].description)
         Glide.with(requireContext()).load(dataList[3].img).into(fragBinding.inlayWorkout.imgType)
         fragBinding.inlayWorkout.imgTypeicon.loadSvg(dataList[3].type)

         fragBinding.inlayWalk.relayStartNew.setOnClickListener {
             RLNextViewOpen("Walk")
         }
         fragBinding.inlayRun.relayStartNew.setOnClickListener {
             RLNextViewOpen("Run")
         }
         fragBinding.inlayRide.relayStartNew.setOnClickListener {
             RLNextViewOpen("Ride")
         }
         fragBinding.inlayWorkout.relayStartNew.setOnClickListener {
             RLNextViewOpen("Workout")
         }
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

    fun RLNextViewOpen(name:String){
         var bundle: Bundle = Bundle()
         bundle.putString("YourWayType",name)
         (context as RLMainActivityRL).RLhidebottombarcolorwhite()
         (context as RLMainActivityRL).RLloadFrag(RLFragChooseYourSensor().newInstance(bundle), TAG, true, null, true)

    }

    fun View.RLadjustWidthToHeight() {
         this.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
             override fun onGlobalLayout() {
                 val height = this@RLadjustWidthToHeight.height
                 if (height > 0) {
                     this@RLadjustWidthToHeight.layoutParams.width = height
                     this@RLadjustWidthToHeight.requestLayout()
                     this@RLadjustWidthToHeight.viewTreeObserver.removeOnGlobalLayoutListener(this)
                 }
             }
         })
    }
}