package com.example.myfirstapp.fragment.start

import android.app.Dialog
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myfirstapp.BaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.MainActivity
import com.example.myfirstapp.adapter.FeedGroupNameAdapter
import com.example.myfirstapp.adapter.FeedListAdapter
import com.example.myfirstapp.adapter.MindClassListAdapter
import com.example.myfirstapp.databinding.DailogClassFilterBinding
import com.example.myfirstapp.databinding.DialogHelpSetyourgoalBinding
import com.example.myfirstapp.databinding.FragFeedBinding
import com.example.myfirstapp.databinding.FragMindClassesBinding
import com.example.myfirstapp.utils.PrefManager

class FragMindClasses : BaseFragment() {
    val TAG: String = FragMindClasses::class.java.simpleName
    lateinit var fragBinding: FragMindClassesBinding
    private val binding by lazy {
        FragMindClassesBinding.inflate(layoutInflater)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = inflateBindLayout(activity?.javaClass,inflater, R.layout.frag_mind_classes, container) as FragMindClassesBinding
        PrefManager.setSomeStringValue(activity, PrefManager.current_fragment,"FragMindClasses" )
        uisetup()
        return fragBinding.root
    }
    private fun uisetup() {
        fragBinding.toolbar.tvTitle.setText(R.string.mindclasses)
        fragBinding.toolbar.tvTitle.setTextColor(resources.getColor(R.color.black))
        onBackPresAct(fragBinding.toolbar.ivBack)
        fragBinding.toolbar.ivNotification.visibility=View.VISIBLE
        fragBinding.toolbar.ivNotification.setImageResource(R.drawable.ic_filter)
        fragBinding.toolbar.ivNotification.setOnClickListener {
           //filter click open dialog
            filterdialogopen()
        }

        val linearLayoutManager = LinearLayoutManager(activity)
        fragBinding.rvItemmindclass.layoutManager = linearLayoutManager
        val adapter = MindClassListAdapter(activity)
        //val data: List<String> =ArrayList<String>()
        //adapter.setList(data)
        fragBinding.rvItemmindclass.adapter = adapter
        
        fragBinding.layAll.setOnClickListener {
            fragBinding.txtAll.setTextColor(resources.getColor(R.color.green))
            fragBinding.viewAll.visibility=View.VISIBLE

            fragBinding.txtRelax.setTextColor(resources.getColor(R.color.Gray))
            fragBinding.viewRelax.visibility=View.GONE

            fragBinding.txtSleep.setTextColor(resources.getColor(R.color.Gray))
            fragBinding.viewSleep.visibility=View.GONE

            fragBinding.txtHappiness.setTextColor(resources.getColor(R.color.Gray))
            fragBinding.viewHappiness.visibility=View.GONE


        }
        fragBinding.laySleep.setOnClickListener {
            fragBinding.txtAll.setTextColor(resources.getColor(R.color.Gray))
            fragBinding.viewAll.visibility=View.GONE

            fragBinding.txtRelax.setTextColor(resources.getColor(R.color.Gray))
            fragBinding.viewRelax.visibility=View.GONE

            fragBinding.txtSleep.setTextColor(resources.getColor(R.color.green))
            fragBinding.viewSleep.visibility=View.VISIBLE

            fragBinding.txtHappiness.setTextColor(resources.getColor(R.color.Gray))
            fragBinding.viewHappiness.visibility=View.GONE

        }
        fragBinding.layRelax.setOnClickListener {
            fragBinding.txtAll.setTextColor(resources.getColor(R.color.Gray))
            fragBinding.viewAll.visibility=View.GONE

            fragBinding.txtRelax.setTextColor(resources.getColor(R.color.green))
            fragBinding.viewRelax.visibility=View.VISIBLE

            fragBinding.txtSleep.setTextColor(resources.getColor(R.color.Gray))
            fragBinding.viewSleep.visibility=View.GONE

            fragBinding.txtHappiness.setTextColor(resources.getColor(R.color.Gray))
            fragBinding.viewHappiness.visibility=View.GONE



        }
        fragBinding.layHappiness.setOnClickListener {
            fragBinding.txtAll.setTextColor(resources.getColor(R.color.Gray))
            fragBinding.viewAll.visibility=View.GONE

            fragBinding.txtRelax.setTextColor(resources.getColor(R.color.Gray))
            fragBinding.viewRelax.visibility=View.GONE

            fragBinding.txtSleep.setTextColor(resources.getColor(R.color.Gray))
            fragBinding.viewSleep.visibility=View.GONE

            fragBinding.txtHappiness.setTextColor(resources.getColor(R.color.green))
            fragBinding.viewHappiness.visibility=View.VISIBLE

        }
    }//dailog_class_filter
    fun filterdialogopen() {
        val  dialog: Dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogMainBinding: DailogClassFilterBinding = DailogClassFilterBinding.inflate(getLayoutInflater())
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
}