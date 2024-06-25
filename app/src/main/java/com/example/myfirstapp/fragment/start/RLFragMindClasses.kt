package com.example.myfirstapp.fragment.start

import android.app.Dialog
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfirstapp.RLBaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.databasefirebase.RLDatabaseManagerRead
import com.example.myfirstapp.fragment.overview.adapter.RLOverviewSessionTitleListAdapter
import com.example.myfirstapp.fragment.start.adapter.RLMindClassListAdapter
import com.example.myfirstapp.databinding.RlDailogClassFilterBinding
import com.example.myfirstapp.databinding.RlFragMindClassesBinding
import com.example.myfirstapp.interfaceall.RLItemClickListener
import com.google.gson.reflect.TypeToken
import com.example.myfirstapp.model.RLVideoModel
import com.example.myfirstapp.utils.RLConstants
import com.example.myfirstapp.utils.RLPrefManager
import com.google.gson.Gson
class RLFragMindClasses : RLBaseFragment() , RLItemClickListener {
    val TAG: String = RLFragMindClasses::class.java.simpleName
    lateinit var fragBinding: RlFragMindClassesBinding
    var classtype:String=""
    private val binding by lazy {
        RlFragMindClassesBinding.inflate(layoutInflater)
    }
    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragMindClasses()
        fragment.arguments = bundle
        return fragment
    }
    //val valueslistMind = arrayOf("All", "Relax","Sleep","Happiness","Focus","Energise","Mindful Movement")
    val valueslistMind = arrayOf("ALL", "RELAX","SLEEP","HAPPINESS","FOCUS","ENERGISE","MINDFUL MOVEMENT")
    //val valueslistBody = arrayOf("All", "HIIT","Ride","Yoga","Pilates","Dance","Warm")
    val valueslistBody = arrayOf("ALL", "HIIT","RIDE","YOGA","PILATES","DANCE","WARM")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_mind_classes, container) as RlFragMindClassesBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragMindClasses" )
        RLuisetup()
        return fragBinding.root
    }
    private fun RLuisetup() {
        classtype=  requireArguments().getString(RLConstants.CLASSTYPE,"")
        fragBinding.toolbar.tvTitle.setTextColor(resources.getColor(R.color.AppBlackColor))
        RLonBackPresAct(fragBinding.toolbar.ivBack)
        fragBinding.toolbar.ivNotification.visibility=View.VISIBLE
        fragBinding.toolbar.ivNotification.setImageResource(R.drawable.ic_filter)
        fragBinding.toolbar.ivNotification.setOnClickListener {
           //filter click open dialog
            RLfilterdialogopen()
        }
        //do title
        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        fragBinding.recycleSessionTitle.layoutManager = linearLayoutManager
        if (classtype.equals(RLConstants.MIND)){
            fragBinding.toolbar.tvTitle.setText(R.string.mindclasses)
            RLGetMindVideoList(RLConstants.FORALL)
            val adaptertitle = RLOverviewSessionTitleListAdapter("ALL",this,valueslistMind,activity)
            fragBinding.recycleSessionTitle.adapter = adaptertitle
        }else{
            fragBinding.toolbar.tvTitle.setText(R.string.bodyclasses)
            RLGetBodyVideoList(RLConstants.FORALL)
            val adaptertitle = RLOverviewSessionTitleListAdapter("ALL",this,valueslistBody,activity)
            fragBinding.recycleSessionTitle.adapter = adaptertitle
        }
        val linearLayoutMain = LinearLayoutManager(activity)
        fragBinding.rvItemmindclass.layoutManager = linearLayoutMain
    }
    private fun RLGetMindVideoList(videotype: String) {
        val databaseManager= RLDatabaseManagerRead()
        databaseManager.RLREVOOLAVIDEOKEYSMINDRead(videotype){ data, error ->
            if (data != null) {
                val gson = Gson()
                val jsonObject = gson.toJson(data)
                val videoType = object : TypeToken<Map<String, RLVideoModel>>() {}.type
                val videoMap: Map<String, RLVideoModel> = gson.fromJson(jsonObject, videoType)
                val videoList = videoMap.values.toList()
                val adapter = RLMindClassListAdapter(true,videoList,activity,classtype)
                fragBinding.rvItemmindclass.adapter = adapter
            }
        }
    }
    private fun RLGetBodyVideoList(videotype: String) {
        val databaseManager= RLDatabaseManagerRead()
        databaseManager.RLRevoolaVideoKeysRead(videotype){ data, error ->
            if (data != null) {
                val gson = Gson()
                val jsonObject = gson.toJson(data)
                val videoType = object : TypeToken<Map<String, RLVideoModel>>() {}.type
                val videoMap: Map<String, RLVideoModel> = gson.fromJson(jsonObject, videoType)
                val videoList = videoMap.values.toList()
                val adapter = RLMindClassListAdapter(false,videoList,activity,classtype)
                fragBinding.rvItemmindclass.adapter = adapter
            }
        }
    }
    //rl_dailog_class_filter
    fun RLfilterdialogopen() {
        val  dialog: Dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogMainBinding: RlDailogClassFilterBinding = RlDailogClassFilterBinding.inflate(getLayoutInflater())
        dialog.setContentView(dialogMainBinding.getRoot())
        dialog.setCancelable(true)

        val window: Window = dialog.getWindow()!!
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        window.attributes = lp
        dialogMainBinding.cardInstructor.txtCardTitle.setText(R.string.instructor)
        dialogMainBinding.cardDuration.txtCardTitle.setText(R.string.duration)
        dialogMainBinding.cardTakenbyme.txtCardTitle.setText(R.string.takenbyme)
        dialogMainBinding.cardClasstype.txtCardTitle.setText(R.string.classtype)

        dialogMainBinding.cardInstructor.txtCardTitle.setOnClickListener {
            dialogMainBinding.cardInstructor.lay1.visibility=View.VISIBLE
            dialogMainBinding.cardInstructor.lay2.visibility=View.VISIBLE
            dialogMainBinding.cardInstructor.txtSubtitle4.visibility=View.GONE
            dialogMainBinding.cardInstructor.txtSubtitle3.setText("Marcus Bain")
        }

        dialogMainBinding.cardDuration.txtCardTitle.setOnClickListener {
            dialogMainBinding.cardDuration.lay1.visibility=View.VISIBLE
            dialogMainBinding.cardDuration.lay2.visibility=View.VISIBLE
            dialogMainBinding.cardDuration.lay3.visibility=View.VISIBLE
            dialogMainBinding.cardDuration.lay4.visibility=View.VISIBLE

            dialogMainBinding.cardDuration.txtSubtitle1.setText("6")
            dialogMainBinding.cardDuration.txtSubtitle2.setText("7")
            dialogMainBinding.cardDuration.txtSubtitle3.setText("8")
            dialogMainBinding.cardDuration.txtSubtitle4.setText("9")
        }
        dialogMainBinding.cardTakenbyme.txtCardTitle.setOnClickListener {
            dialogMainBinding.cardTakenbyme.lay1.visibility=View.VISIBLE
            dialogMainBinding.cardTakenbyme.txtSubtitle1.setText(R.string.takenbyme)
            dialogMainBinding.cardTakenbyme.txtSubtitle2.visibility=View.GONE
        }
        dialogMainBinding.cardClasstype.txtCardTitle.setOnClickListener {
            dialogMainBinding.cardClasstype.lay1.visibility=View.VISIBLE
            dialogMainBinding.cardClasstype.txtSubtitle1.setText("Video")
            dialogMainBinding.cardClasstype.txtSubtitle2.setText("Audio")

        }
        dialogMainBinding.txtxCancle.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
        //dialog.window!!.setBackgroundDrawableResource(R.color.transparent_dialog)

    }
    override fun onItemClick(position: Int) {
        if (classtype.equals(RLConstants.MIND)){
           val selectiontitle= valueslistMind[position]
            when(selectiontitle){
                "ALL"->{
                    RLGetMindVideoList(RLConstants.FORALL)
                }
                "RELAX"->{
                    RLGetMindVideoList(RLConstants.FORRELAX)
                }
                "SLEEP"->{
                    RLGetMindVideoList(RLConstants.FORSLEEP)
                }
                "HAPPINESS"->{
                    RLGetMindVideoList(RLConstants.FORHAPPINESS)
                }
                "FOCUS"->{
                    RLGetMindVideoList(RLConstants.FORFOCUS)
                }
                "ENERGISE"->{
                    RLGetMindVideoList(RLConstants.FORENERGISE)
                }
                "MINDFUL"->{
                    RLGetMindVideoList(RLConstants.FORMINDFULKMOVEMENT)
                }
                "MOVEMENT"->{
                    RLGetMindVideoList(RLConstants.FORMINDFULKMOVEMENT)
                }
            }

        }
        else{
            val selectiontitle= valueslistBody[position]
            when(selectiontitle){
                "ALL"->{
                    RLGetBodyVideoList(RLConstants.FORALL)
                }
                "HIIT"->{
                    RLGetBodyVideoList(RLConstants.FORHIIT)
                }
                "RIDE"->{
                    RLGetBodyVideoList(RLConstants.FORRIDE)
                }
                "YOGA"->{
                    RLGetBodyVideoList(RLConstants.FORYOGA)
                }
                "PILATES"->{
                    RLGetBodyVideoList(RLConstants.FORPILATES)
                }
                "DANCE"->{
                    RLGetBodyVideoList(RLConstants.FORDANCE)
                }
                "WARM"->{
                    RLGetBodyVideoList(RLConstants.FORWARMUP)
                }
            }
        }
    }
}