package com.example.myfirstapp.fragment.feed

import android.app.Dialog
import android.content.pm.ActivityInfo
import android.os.Bundle
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
import com.example.myfirstapp.adapter.FeedGroupNameAdapter
import com.example.myfirstapp.adapter.FeedListAdapter
import com.example.myfirstapp.databinding.FragFeedBinding
import com.example.myfirstapp.utils.PrefManager

class FragFeed : BaseFragment() {
    val TAG: String = FragFeed::class.java.simpleName
    lateinit var fragBinding: FragFeedBinding
    private val binding by lazy {
        FragFeedBinding.inflate(layoutInflater)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = inflateBindLayout(activity?.javaClass,inflater, R.layout.frag_feed, container) as FragFeedBinding
        PrefManager.setSomeStringValue(activity, PrefManager.current_fragment,"FragFeed" )
        uisetup()
        return fragBinding.root
    }
    private fun uisetup() {
        val linearLayoutManager = LinearLayoutManager(activity)
        fragBinding.rvItemfeed.layoutManager = linearLayoutManager
        val adapter = FeedListAdapter(activity)
        //val data: List<String> =ArrayList<String>()
        //adapter.setList(data)
        fragBinding.rvItemfeed.adapter = adapter
        
        fragBinding.layFriends.setOnClickListener {
            fragBinding.txtFriends.setTextColor(resources.getColor(R.color.green))
            fragBinding.viewFriends.visibility=View.VISIBLE

            fragBinding.txtGroups.setTextColor(resources.getColor(R.color.Gray))
            fragBinding.viewGroups.visibility=View.GONE

            fragBinding.txtYou.setTextColor(resources.getColor(R.color.Gray))
            fragBinding.viewYou.visibility=View.GONE

            fragBinding.txtChallenges.setTextColor(resources.getColor(R.color.Gray))
            fragBinding.viewChallenges.visibility=View.GONE

            fragBinding.relayGroupname.visibility=View.GONE
            fragBinding.relayListview.visibility=View.VISIBLE
            fragBinding.relayChallenges.cardChalengis.visibility=View.GONE

        }
        fragBinding.layYou.setOnClickListener {
            fragBinding.txtFriends.setTextColor(resources.getColor(R.color.Gray))
            fragBinding.viewFriends.visibility=View.GONE

            fragBinding.txtGroups.setTextColor(resources.getColor(R.color.Gray))
            fragBinding.viewGroups.visibility=View.GONE

            fragBinding.txtYou.setTextColor(resources.getColor(R.color.green))
            fragBinding.viewYou.visibility=View.VISIBLE

            fragBinding.txtChallenges.setTextColor(resources.getColor(R.color.Gray))
            fragBinding.viewChallenges.visibility=View.GONE

            fragBinding.relayGroupname.visibility=View.GONE
            fragBinding.relayListview.visibility=View.GONE
            fragBinding.relayChallenges.cardChalengis.visibility=View.GONE
        }
        fragBinding.layGroups.setOnClickListener {
            fragBinding.txtFriends.setTextColor(resources.getColor(R.color.Gray))
            fragBinding.viewFriends.visibility=View.GONE

            fragBinding.txtGroups.setTextColor(resources.getColor(R.color.green))
            fragBinding.viewGroups.visibility=View.VISIBLE

            fragBinding.txtYou.setTextColor(resources.getColor(R.color.Gray))
            fragBinding.viewYou.visibility=View.GONE

            fragBinding.txtChallenges.setTextColor(resources.getColor(R.color.Gray))
            fragBinding.viewChallenges.visibility=View.GONE

            fragBinding.relayGroupname.visibility=View.VISIBLE
            fragBinding.relayListview.visibility=View.VISIBLE
            fragBinding.relayChallenges.cardChalengis.visibility=View.GONE

            groupnamelistdialogopen()
        }
        fragBinding.layChallenges.setOnClickListener {
            fragBinding.txtFriends.setTextColor(resources.getColor(R.color.Gray))
            fragBinding.viewFriends.visibility=View.GONE

            fragBinding.txtGroups.setTextColor(resources.getColor(R.color.Gray))
            fragBinding.viewGroups.visibility=View.GONE

            fragBinding.txtYou.setTextColor(resources.getColor(R.color.Gray))
            fragBinding.viewYou.visibility=View.GONE

            fragBinding.txtChallenges.setTextColor(resources.getColor(R.color.green))
            fragBinding.viewChallenges.visibility=View.VISIBLE

            fragBinding.relayGroupname.visibility=View.GONE
            fragBinding.relayListview.visibility=View.GONE
            fragBinding.relayChallenges.cardChalengis.visibility=View.VISIBLE

            fragBinding.relayChallenges.layStepssofar.viewCommon.visibility=View.VISIBLE
            fragBinding.relayChallenges.layStepssofar.txtTime.setText("STEPS SO FAR")
            fragBinding.relayChallenges.layStepssofar.txtTimeNumber.setText("1,500")

            fragBinding.relayChallenges.layTargetsteps.viewCommon.visibility=View.VISIBLE
            fragBinding.relayChallenges.layTargetsteps.txtTime.setText("TARGET STEPS")
            fragBinding.relayChallenges.layTargetsteps.txtTimeNumber.setText("2,888")

            fragBinding.relayChallenges.layDaysremaining.viewCommon.visibility=View.VISIBLE
            fragBinding.relayChallenges.layDaysremaining.txtTime.setText("DAYS REMAINING")
            fragBinding.relayChallenges.layDaysremaining.txtTimeNumber.setText("0 DAYS")
        }
    }
    fun groupnamelistdialogopen() {
        val  dialog: Dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dailog_group_name)
        dialog.setCancelable(true)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        val recyclerSelectAssign =  dialog.findViewById(R.id.listItems) as RecyclerView
        val btClear : TextView = dialog.findViewById(R.id.txtx_cancle)
        val linearLayoutManager = LinearLayoutManager(context)
        recyclerSelectAssign.layoutManager = linearLayoutManager
        val  dialogAdapter = FeedGroupNameAdapter(requireActivity())
       // dialogAdapter.setListPData(groupnameList)
        recyclerSelectAssign.adapter = dialogAdapter
        dialogAdapter.seOnClickListners(object : FeedGroupNameAdapter.ClickListner {
            override fun onSelectClick(selectioncName: String, selectionID: String) {
                dialog.dismiss()
            }
        })
        btClear.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
        dialog.window!!.setBackgroundDrawableResource(R.color.transparent_dialog)
    }
}