package com.revoola.fragment.start.mind

import android.app.Dialog
import android.os.Bundle
import android.util.Log
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
import com.revoola.fragment.start.adapter.RLMindClassListAdapter
import com.revoola.databinding.RlDailogClassFilterBinding
import com.revoola.databinding.RlFragMindClassesBinding
import com.revoola.interfaceall.RLItemClickListener
import com.google.gson.reflect.TypeToken
import com.revoola.model.RLVideoModel
import com.revoola.utils.RLConstants
import com.google.gson.Gson
import com.revoola.fragment.start.adapter.RlMindBodyFilterExpandableListAdapter
import com.revoola.model.RLMindBodyFilterGroupItemModel
import com.revoola.services.RELDynamicLink
import com.revoola.services.RELDynamicLinkManager
import com.revoola.services.RLClassFilterService
import com.revoola.utils.RLTools
import java.util.Locale

class RLFragMindClasses : RLBaseFragment() , RLItemClickListener {
    val TAG: String = RLFragMindClasses::class.java.simpleName
    lateinit var fragBinding: RlFragMindClassesBinding

    var instructorList:MutableList<String> = mutableListOf()
    var classTypeList:MutableList<String> = mutableListOf()
    var durationList:MutableList<String> = mutableListOf()
    var videoList_Filter:MutableList<RLVideoModel> = mutableListOf()

    private val binding by lazy {
        RlFragMindClassesBinding.inflate(layoutInflater)
    }
    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragMindClasses()
        fragment.arguments = bundle
        return fragment
    }
    //val valueslistMind = arrayOf("All", "Relax","Sleep","Happiness","Focus","Energise","Mindful Movement")
    val valueslistMind = arrayOf("ALL", "RELAX","SLEEP","HAPPINESS","FOCUS","ENERGISE","MINDFUL MOVEMENT",
        "ALL", "RELAX","SLEEP","HAPPINESS","FOCUS","ENERGISE","MINDFUL MOVEMENT",
        "ALL", "RELAX","SLEEP","HAPPINESS","FOCUS","ENERGISE","MINDFUL MOVEMENT",
        "ALL", "RELAX","SLEEP","HAPPINESS","FOCUS","ENERGISE","MINDFUL MOVEMENT",
        "ALL", "RELAX","SLEEP","HAPPINESS","FOCUS","ENERGISE","MINDFUL MOVEMENT")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         RLScreenSet(false)
        RLBottomHideShowSet(true)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_mind_classes, container) as RlFragMindClassesBinding
        com.revoola.utils.RLPrefManager.RLsetSomeStringValue(activity, com.revoola.utils.RLPrefManager.current_fragment,"RLFragMindClasses" )
        RLuisetup()
        return fragBinding.root
    }
    private fun RLuisetup() {
        RLHelpHideShowSet(true, fragBinding.toolbar.ivhelp, com.revoola.utils.RLPrefManager.start_help_content)
        RLonBackPresAct(fragBinding.toolbar.ivBack)
        fragBinding.toolbar.ivTitle.setText(R.string.mindclasses)
        fragBinding.toolbar.ivDescription.setText(R.string.selectamindfulclass)

        fragBinding.inlayFilter.ivFilter.setOnClickListener {
            //filter click open dialog
            RLfilterdialogopen()
        }
        //do title
        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        fragBinding.toolbar.recyclerTitle.layoutManager = linearLayoutManager
        val adaptertitle = RLOverviewSessionTitleListAdapter("ALL",this,valueslistMind,activity)

        fragBinding.toolbar.recyclerTitle.adapter = adaptertitle
        // click to show center
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(fragBinding.toolbar.recyclerTitle)
        // Initially move the first item to the center

        RLMoveToCenter(14)

        //Main Recyclerview
        val linearLayoutMain = LinearLayoutManager(activity)
        fragBinding.rvItemMindClass.layoutManager = linearLayoutMain
        RLGetMindVideoList(RLConstants.FORALL)

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
                 videoList_Filter = videoList.toMutableList()
                val height =  fragBinding.rvItemMindClass.height
                val adapter = RLMindClassListAdapter(videoList,activity,height)
                fragBinding.rvItemMindClass.adapter = adapter

                instructorList = emptyList<String>().toMutableList()
                durationList = emptyList<String>().toMutableList()
                classTypeList = emptyList<String>().toMutableList()

                // Remove null or empty values from the lists
                durationList = videoList.mapNotNull { it.duration }.distinct().toMutableList()
                instructorList = videoList.mapNotNull { it.instructor }.filter { it.isNotEmpty() }.distinct().toMutableList()
                classTypeList = videoList.mapNotNull { it.classtype }.filter { it.isNotEmpty() }.distinct().toMutableList()


                /*val adapter = RLMindClassListAdapter(videoList,activity)
                fragBinding.rvItemmindclass.adapter = adapter*/

            }
        }
    }

    //rl_dailog_class_filter
   private fun RLfilterdialogopen() {
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

        // Prepare the data
        val groupList = listOf(
            RLMindBodyFilterGroupItemModel( getString(R.string.instructor), instructorList.distinct()),
            RLMindBodyFilterGroupItemModel(getString(R.string.duration),durationList.distinct()),
            RLMindBodyFilterGroupItemModel(getString(R.string.takenbyme), listOf("ALL", "TAKEN BY ME")),
            RLMindBodyFilterGroupItemModel(getString(R.string.classtype), classTypeList.distinct()))

        val adapter = RlMindBodyFilterExpandableListAdapter(requireContext(), groupList.distinct()) { selectedItems ->
            // This is your callback function where you handle the selected items
            if (selectedItems.isNullOrEmpty()){
                dialogMainBinding.txtShowAllClasses.setText("Show All Classes")
            }else{
                val filterVideoList = RLClassFilterService.RLGetFilterVideoList(selectedItems,videoList_Filter)
                dialogMainBinding.txtShowAllClasses.setText("Show ${filterVideoList.size} Classes")
            }
        }


        dialogMainBinding.expandableListViewFilter.setAdapter(adapter)

        dialogMainBinding.txtShowAllClasses.setOnClickListener {
            // Get multi-child selection from adapter
            val selectedItems: List<RLMindBodyFilterGroupItemModel> = adapter.RLGetSelectedItems()
            val filterVideoList = RLClassFilterService.RLGetFilterVideoList(selectedItems,videoList_Filter)
            val height =  fragBinding.rvItemMindClass.height
            val adapter = RLMindClassListAdapter(filterVideoList,activity,height)
            fragBinding.rvItemMindClass.adapter = adapter
            dialog.dismiss()
        }

        dialogMainBinding.txtxCancle.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
        //dialog.window!!.setBackgroundDrawableResource(R.color.transparent_dialog)

    }

    override fun onItemClick(position: Int) {
        RLMoveToCenter(position)
        val selectiontitle= valueslistMind[position]
        when(selectiontitle){
            "ALL"->{ RLGetMindVideoList(RLConstants.FORALL) }
            "RELAX"->{ RLGetMindVideoList(RLConstants.FORRELAX) }
            "SLEEP"->{ RLGetMindVideoList(RLConstants.FORSLEEP) }
            "HAPPINESS"->{ RLGetMindVideoList(RLConstants.FORHAPPINESS) }
            "FOCUS"->{ RLGetMindVideoList(RLConstants.FORFOCUS) }
            "ENERGISE"->{ RLGetMindVideoList(RLConstants.FORENERGISE) }
            "MINDFUL"->{ RLGetMindVideoList(RLConstants.FORMINDFULKMOVEMENT) }
            "MOVEMENT"->{ RLGetMindVideoList(RLConstants.FORMINDFULKMOVEMENT) }
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