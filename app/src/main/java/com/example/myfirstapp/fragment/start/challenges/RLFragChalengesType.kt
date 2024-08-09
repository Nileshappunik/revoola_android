package com.example.myfirstapp.fragment.start.challenges

import android.app.Dialog
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.Window
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.myfirstapp.RLBaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.RLMainActivityRL
import com.example.myfirstapp.databasefirebase.RLDatabaseManagerRead
import com.example.myfirstapp.databinding.RlDialogHelpChallengesBinding
import com.example.myfirstapp.databinding.RlFragChalengesTypeBinding
import com.example.myfirstapp.enumclass.RLStartAllMenuModel
import com.example.myfirstapp.enumclass.RLStartType
import com.example.myfirstapp.fragment.start.challenges.adapter.RLChallengesListAdapter
import com.example.myfirstapp.utils.RLConstants
import com.example.myfirstapp.utils.RLPrefManager
import com.example.myfirstapp.utils.loadSvg
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.math.roundToInt

class RLFragChalengesType : RLBaseFragment() {
    val TAG: String = RLFragChalengesType::class.java.simpleName
    lateinit var fragBinding: RlFragChalengesTypeBinding

    private val binding by lazy {
        RlFragChalengesTypeBinding.inflate(layoutInflater)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         RLScreenSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_chalenges_type, container) as RlFragChalengesTypeBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragChalengesType" )
        (context as RLMainActivityRL).RLhidebottombarcolorwhite()
        return fragBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragBinding.rvChallenges.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                fragBinding.rvChallenges.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val height =  fragBinding.rvChallenges.height
                Log.d(TAG,"RelativeLayout total height: $height pixels")
                RLChallengesList(height)
            }
        })
    }

    private fun RLChallengesList(height: Int) {
        RLonBackPresAct(fragBinding.inlayTop.ivBack)
        fragBinding.inlayTop.ivTitle.setText(getString(R.string.challengessmall))
        fragBinding.inlayTop.ivDescription.setText(getString(R.string.typeofchallenge))
        fragBinding.inlayTop.ivhelp.setOnClickListener {
            RLshowHelpDialog()
        }
        val databaseManager= RLDatabaseManagerRead()
        databaseManager.RLALLMENULISTRead(RLConstants.CHALLENGES){ data, error ->
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
                   // RLuisetupNew(dataList)
                }catch (e:Exception){
                    Log.e(TAG,"Catch:- ${e.message}")
                }
            }else{
                Log.e(TAG,"Null Data:- $data")
            }
        }
    }
    private fun RLshowHelpDialog() {
        val dialog: Dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogMainBinding: RlDialogHelpChallengesBinding =
            RlDialogHelpChallengesBinding.inflate(getLayoutInflater())
        dialog.setContentView(dialogMainBinding.getRoot())
        dialog.setCancelable(true)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))

        dialogMainBinding.tvClose.setOnClickListener {
            dialog.dismiss()
        }

        dialogMainBinding.layStep.txtHeader.setText(R.string.stepdot)

        dialogMainBinding.layEffort.txtHeader.setText(R.string.effortdot)
        dialogMainBinding.layEffort.txtHeaderDescription.setText(R.string.revoolauniqueeffort)
        dialogMainBinding.layEffort.imgHelpChallenges.setImageResource(R.drawable.ic_heart)

        dialogMainBinding.layCalories.txtHeader.setText(R.string.caloriesdot)
        dialogMainBinding.layCalories.txtHeaderDescription.setText(R.string.asimplecountcallery)
        dialogMainBinding.layCalories.imgHelpChallenges.setImageResource(R.drawable.fd_calories_green)

        dialogMainBinding.layDistance.txtHeader.setText(R.string.distancedot)
        dialogMainBinding.layDistance.txtHeaderDescription.setText(R.string.measureinkmormiles)
        dialogMainBinding.layDistance.imgHelpChallenges.setImageResource(R.drawable.ic_distance)

        dialogMainBinding.layClimbed.txtHeader.setText(R.string.climbeddot)
        dialogMainBinding.layClimbed.txtHeaderDescription.setText(R.string.measureinmeterorfeet)
        dialogMainBinding.layClimbed.imgHelpChallenges.setImageResource(R.drawable.ic_climb)

        dialogMainBinding.layDuration.txtHeader.setText(R.string.durationdot)
        dialogMainBinding.layDuration.txtHeaderDescription.setText(R.string.measureindaysandhours)
        dialogMainBinding.layDuration.imgHelpChallenges.setImageResource(R.drawable.fd_active_time_green)

        dialog.show()

    }

}