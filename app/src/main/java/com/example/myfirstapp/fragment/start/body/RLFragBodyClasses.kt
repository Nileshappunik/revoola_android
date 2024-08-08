package com.example.myfirstapp.fragment.start.body

import android.app.Dialog
import android.content.pm.ActivityInfo
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfirstapp.RLBaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.databasefirebase.RLDatabaseManagerRead
import com.example.myfirstapp.fragment.overview.adapter.RLOverviewSessionTitleListAdapter
import com.example.myfirstapp.databinding.RlDailogClassFilterBinding
import com.example.myfirstapp.databinding.RlFragMindClassesBinding
import com.example.myfirstapp.fragment.start.adapter.RLBodyClassListAdapter
import com.example.myfirstapp.interfaceall.RLItemClickListener
import com.google.gson.reflect.TypeToken
import com.example.myfirstapp.model.RLVideoModel
import com.example.myfirstapp.utils.RLConstants
import com.example.myfirstapp.utils.RLPrefManager
import com.google.gson.Gson
class RLFragBodyClasses : RLBaseFragment() , RLItemClickListener {
    val TAG: String = RLFragBodyClasses::class.java.simpleName
    lateinit var fragBinding: RlFragMindClassesBinding
    private val binding by lazy {
        RlFragMindClassesBinding.inflate(layoutInflater)
    }
    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragBodyClasses()
        fragment.arguments = bundle
        return fragment
    }
    //val valueslistBody = arrayOf("All", "HIIT","Ride","Yoga","Pilates","Dance","Warm")
    val valueslistBody = arrayOf("ALL", "HIIT","RIDE","YOGA","PILATES","DANCE","WARM")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        RLScreenSet(false)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_mind_classes, container) as RlFragMindClassesBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragBodyClasses" )
        RLuisetup()
        return fragBinding.root
    }
    private fun RLuisetup() {
        RLonBackPresAct(fragBinding.toolbar.ivBack)
      //  val tintColor = requireContext().getColor(R.color.AppDarkGrayColor)
      //  fragBinding.toolbar.ivBack.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN)
        fragBinding.toolbar.ivTitle.setText(R.string.bodyclasses)
        fragBinding.toolbar.ivDescription.setText(R.string.selectabodyclass)
        val typeface: Typeface? = ResourcesCompat.getFont(requireContext(), R.font.omnes_regular)
        fragBinding.toolbar.ivTitle.typeface = typeface
        fragBinding.toolbar.ivDescription.typeface = typeface
        fragBinding.inlayFilter.ivFilter.setOnClickListener {
            //filter click open dialog
            RLfilterdialogopen()
        }

        //do title
        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        fragBinding.toolbar.recyclerTitle.layoutManager = linearLayoutManager
        val adaptertitle = RLOverviewSessionTitleListAdapter("ALL",this,valueslistBody,activity)
        fragBinding.toolbar.recyclerTitle.adapter = adaptertitle

        //Main Recyclerview
        val linearLayoutMain = LinearLayoutManager(activity)
        fragBinding.rvItemmindclass.layoutManager = linearLayoutMain
        RLGetBodyVideoList(RLConstants.FORALL,false)
    }
    private fun RLGetBodyVideoList(videotype: String,ride:Boolean) {
        val databaseManager= RLDatabaseManagerRead()
        databaseManager.RLRevoolaVideoKeysRead(videotype){ data, error ->
            if (data != null) {
                val gson = Gson()
                val jsonObject = gson.toJson(data)
                val videoType = object : TypeToken<Map<String, RLVideoModel>>() {}.type
                val videoMap: Map<String, RLVideoModel> = gson.fromJson(jsonObject, videoType)
                val videoList = videoMap.values.toList()
                val adapter = RLBodyClassListAdapter(videoList,activity,ride)
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


        dialogMainBinding.txtxCancle.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
        //dialog.window!!.setBackgroundDrawableResource(R.color.transparent_dialog)

    }
    override fun onItemClick(position: Int) {
        val selectiontitle= valueslistBody[position]
        when(selectiontitle){
            "ALL"->{
                RLGetBodyVideoList(RLConstants.FORALL,false)
            }
            "HIIT"->{
                RLGetBodyVideoList(RLConstants.FORHIIT,false)
            }
            "RIDE"->{
                RLGetBodyVideoList(RLConstants.FORRIDE,true)
            }
            "YOGA"->{
                RLGetBodyVideoList(RLConstants.FORYOGA,false)
            }
            "PILATES"->{
                RLGetBodyVideoList(RLConstants.FORPILATES,false)
            }
            "DANCE"->{
                RLGetBodyVideoList(RLConstants.FORDANCE,false)
            }
            "WARM"->{
                RLGetBodyVideoList(RLConstants.FORWARMUP,false)
            }
        }
    }
}