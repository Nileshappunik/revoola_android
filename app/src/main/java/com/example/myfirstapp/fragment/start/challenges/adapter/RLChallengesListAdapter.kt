package com.example.myfirstapp.fragment.start.challenges.adapter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.RLMainActivityRL
import com.example.myfirstapp.databinding.RlCommonChallengesTypeCardBinding
import com.example.myfirstapp.databinding.RlLayoutStartMenuBinding
import com.example.myfirstapp.enumclass.RLStartAllMenuModel
import com.example.myfirstapp.enumclass.RLTypeOfChallenges
import com.example.myfirstapp.fragment.start.challenges.RLFragSetYourGoal
import com.example.myfirstapp.utils.loadSvg
import kotlin.math.roundToInt

class RLChallengesListAdapter(val context: FragmentActivity?,
                              val  dataList: List<RLStartAllMenuModel>,
                              val heightTotal: Int) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TAG = "RLChallengesListAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutbinding: RlLayoutStartMenuBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.rl_layout_start_menu , parent, false)
        return MyViewHolder(layoutbinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder) {
            holder.bindData(position, holder.itemView)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class MyViewHolder(layoutBinding: RlLayoutStartMenuBinding) : RecyclerView.ViewHolder(layoutBinding.root) {
        private val layoutBinding: RlLayoutStartMenuBinding = layoutBinding
        fun bindData(position: Int, itemVIew: View) {
            val cardData = dataList[position]
            layoutBinding.txtTypename.setText(cardData.title)
            layoutBinding.txtDescription.setText(cardData.description)
            Glide.with(context!!).load(cardData.img).into(layoutBinding.imgType)
            layoutBinding.imgTypeicon.loadSvg(cardData.type)

            //RelativeLayout Height set
            val layoutParams: ViewGroup.LayoutParams = layoutBinding.relayStartNew.layoutParams
            layoutParams.height =  heightTotal/6
            layoutBinding.relayStartNew.layoutParams =layoutParams

            //Image Height Width set
            val layoutParamsImage: ViewGroup.LayoutParams = layoutBinding.imgType.layoutParams
            layoutParamsImage.height =  heightTotal/8
            layoutParamsImage.width =  heightTotal/6
           // layoutParamsImage.width =  (heightTotal/7 * 1.5).roundToInt()
            layoutBinding.imgType.layoutParams =layoutParamsImage

            layoutBinding.relayStartNew.setOnClickListener {
              when(cardData.title.toLowerCase()){
                 "steps"->{ RLNextViewOpen( "Steps" )}
                 "effort"->{RLNextViewOpen("Effort")}
                 "calories"->{ RLNextViewOpen("Calories")}
                 "distance"->{RLNextViewOpen("Distance")}
                 "climbed"->{ RLNextViewOpen("Climbed")}
                 "duration"->{RLNextViewOpen("Duration")}
              }
            }

        }
        private fun RLNextViewOpen(challengeType:String){
            val bundle: Bundle = Bundle()
            bundle.putString("ChallengeType",challengeType )
            (context as RLMainActivityRL).RLloadFrag(RLFragSetYourGoal().newInstance(bundle), TAG, true, null, false)
        }
    }

}
