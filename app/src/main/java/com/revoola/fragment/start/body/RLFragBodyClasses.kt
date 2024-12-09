package com.revoola.fragment.start.body

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.revoola.RLBaseFragment
import com.revoola.R
import com.revoola.databasefirebase.RLDatabaseManagerRead
import com.revoola.fragment.overview.adapter.RLOverviewSessionTitleListAdapter
import com.revoola.databinding.RlDailogClassFilterBinding
import com.revoola.databinding.RlFragMindClassesBinding
import com.revoola.fragment.start.adapter.RLBodyClassListAdapter
import com.revoola.interfaceall.RLItemClickListener
import com.google.gson.reflect.TypeToken
import com.revoola.model.RLVideoModel
import com.revoola.utils.RLConstants
import com.google.gson.Gson
class RLFragBodyClasses : RLBaseFragment() , RLItemClickListener {
    val TAG: String = RLFragBodyClasses::class.java.simpleName
    lateinit var fragBinding: RlFragMindClassesBinding
    //var heightScreen=1177
    private val binding by lazy {
        RlFragMindClassesBinding.inflate(layoutInflater)
    }
    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragBodyClasses()
        fragment.arguments = bundle
        return fragment
    }
    //val valueslistBody = arrayOf("All", "HIIT","Ride","Yoga","Pilates","Dance","Warm")
    val valueslistBody = arrayOf("ALL", "HIIT","RIDE","YOGA","PILATES","DANCE","WARM",
        "ALL", "HIIT","RIDE","YOGA","PILATES","DANCE","WARM",
        "ALL", "HIIT","RIDE","YOGA","PILATES","DANCE","WARM",
        "ALL", "HIIT","RIDE","YOGA","PILATES","DANCE","WARM",
        "ALL", "HIIT","RIDE","YOGA","PILATES","DANCE","WARM")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        RLScreenSet(false)
        RLBottomHideShowSet(true)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_mind_classes, container) as RlFragMindClassesBinding
        com.revoola.utils.RLPrefManager.RLsetSomeStringValue(activity, com.revoola.utils.RLPrefManager.current_fragment,"RLFragBodyClasses" )
        RLuisetup()
        return fragBinding.root
    }
    private fun RLuisetup() {
        RLonBackPresAct(fragBinding.toolbar.ivBack)
        RLHelpHideShowSet(true, fragBinding.toolbar.ivhelp, com.revoola.utils.RLPrefManager.start_help_content)
        fragBinding.toolbar.ivTitle.setText(R.string.bodyclasses)
        fragBinding.toolbar.ivDescription.setText(R.string.selectabodyclass)
        fragBinding.inlayFilter.ivFilter.setOnClickListener {
            //filter click open dialog
            RLfilterdialogopen()
        }
        //do title
        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        fragBinding.toolbar.recyclerTitle.layoutManager = linearLayoutManager
        val adaptertitle = RLOverviewSessionTitleListAdapter("ALL",this,valueslistBody,activity)
        fragBinding.toolbar.recyclerTitle.adapter = adaptertitle
        // click to show center
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(fragBinding.toolbar.recyclerTitle)
        // Initially move the first item to the center
        RLMoveToCenter(14)

        //Main Recyclerview
        val linearLayoutMain = LinearLayoutManager(activity)
        fragBinding.rvItemMindClass.layoutManager = linearLayoutMain
        RLGetBodyVideoList(RLConstants.FORALL,false)

    }
    private fun RLGetBodyVideoList(videoType: String,ride:Boolean) {
        val databaseManager= RLDatabaseManagerRead()
        databaseManager.RLRevoolaVideoKeysRead(videoType){ data, error ->
            if (data != null) {
                val gson = Gson()
                val jsonObject = gson.toJson(data)
                val videoTypeObject = object : TypeToken<Map<String, RLVideoModel>>() {}.type
                val videoMap: Map<String, RLVideoModel> = gson.fromJson(jsonObject, videoTypeObject)
                val videoList = videoMap.values.toList()
                val height =  fragBinding.rvItemMindClass.height
                val adapter = RLBodyClassListAdapter(videoList,activity,ride,height)
                fragBinding.rvItemMindClass.adapter = adapter
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
        RLMoveToCenter(position)
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

    private fun RLMoveToCenter(position: Int) {
        val layoutManager = fragBinding.toolbar.recyclerTitle.layoutManager as LinearLayoutManager

        fragBinding.toolbar.recyclerTitle.post {
            // Scroll to the desired position first
            layoutManager.scrollToPositionWithOffset(position, fragBinding.toolbar.recyclerTitle.width / 2)
            fragBinding.toolbar.recyclerTitle.viewTreeObserver.addOnGlobalLayoutListener(
                object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        fragBinding.toolbar.recyclerTitle.viewTreeObserver.removeOnGlobalLayoutListener(this)

                        val view = layoutManager.findViewByPosition(position)
                        if (view != null) {
                            val viewLeft = view.left
                            val viewWidth = view.width

                            val scrollDistance = viewLeft - (fragBinding.toolbar.recyclerTitle.width / 2 - viewWidth / 2)
                            fragBinding.toolbar.recyclerTitle.smoothScrollBy(scrollDistance, 0)
                        }
                    }
                })
        }
    }
}