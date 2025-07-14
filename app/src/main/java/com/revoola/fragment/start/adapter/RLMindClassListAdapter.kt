package com.revoola.fragment.start.adapter

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.revoola.R
import com.revoola.activity.RLMainActivityRL
import com.revoola.databinding.RlLayoutMindClassesListBinding
import com.revoola.fragment.start.mind.RLFragMindClassesView
import com.revoola.model.RLVideoModel
import com.revoola.commonobject.RLTools

class RLMindClassListAdapter(private val dataList: List<RLVideoModel>,
                             val context: FragmentActivity?,
                             val totalScreenHeight: Int
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TAG = "RLMindClassListAdapter"



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutBinding: RlLayoutMindClassesListBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.rl_layout_mind_classes_list , parent, false)
        return MyViewHolder(layoutBinding)
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is MyViewHolder) {

            holder.bindData(position, holder.itemView)
        }
    }
    override fun getItemCount(): Int {
       return dataList.size
    }
    inner class MyViewHolder(layoutBinding: RlLayoutMindClassesListBinding) : RecyclerView.ViewHolder(layoutBinding.root) {
        private val layoutBinding: RlLayoutMindClassesListBinding = layoutBinding
        fun bindData(position: Int, itemVIew: View) {
            val cardData = dataList[position]
            try{
                layoutBinding.txtClasstime.setText(cardData.duration+" CLASS")
                layoutBinding.txtUsername.setText(cardData.instructor)
                layoutBinding.txtClassname.setText(cardData.rideTitle)
                layoutBinding.txtVideoaudio.setText(cardData.difficulty)
                Glide.with(context!!).load(cardData.imageLinkInstructor)
                    //.placeholder(R.drawable.sample_user).error(R.drawable.sample_user)
                    .into(layoutBinding.imgUser)
                Glide.with(context).load(cardData.imageLinkrectangleV2)
                    //.placeholder(R.drawable.sample_user).error(R.drawable.sample_user)
                    .into(layoutBinding.imgMind)

                itemVIew.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putString("AUDIOVIDEOTYPE", cardData.classtype)
                    bundle.putString("VIDEODATA",cardData.key)
                    (context as RLMainActivityRL).rl_loadFrag(RLFragMindClassesView().newInstance(bundle), TAG, true, null, true)
                }

                /*val layoutParams: ViewGroup.LayoutParams = layoutBinding.relayNew.layoutParams
                layoutParams.height =  1176/6
                layoutBinding.relayNew.layoutParams =layoutParams*/

                //RelativeLayout Height set
                val layoutParams: ViewGroup.LayoutParams = layoutBinding.relayNew.layoutParams
                layoutParams.height =  totalScreenHeight/6
                layoutBinding.relayNew.layoutParams =layoutParams


                //Image Height Width set
                val layoutParamsImage: ViewGroup.LayoutParams = layoutBinding.imgMind.layoutParams
                layoutParamsImage.height =  totalScreenHeight/6
                layoutParamsImage.width =  totalScreenHeight/4
                layoutBinding.imgMind.layoutParams =layoutParamsImage



            }catch (e:Exception){
               RLTools.rl_logEPrint(TAG,"Exception:- ${e.message}")
            }
        }

        private fun RLshowSubscribeDialog() {
            val sucDialog: Dialog = Dialog(context!!)
            sucDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            sucDialog.setContentView(R.layout.rl_dialog_subscribe)
            sucDialog.setCancelable(true)
            val tvCancel: TextView = sucDialog.findViewById(R.id.tvCancel)
            val tvSubscribe: TextView = sucDialog.findViewById(R.id.tvSubscribe)
            tvCancel.setOnClickListener(View.OnClickListener {
                sucDialog.dismiss()
            })
            tvSubscribe.setOnClickListener(View.OnClickListener {
                sucDialog.dismiss()
            })
            sucDialog.show()
            sucDialog.window!!.setBackgroundDrawableResource(R.drawable.rounded_dialog_background)
        }
    }
}
