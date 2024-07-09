package com.example.myfirstapp.fragment.start.adapter

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.RLMainActivityRL
import com.example.myfirstapp.databasefirebase.RLDatabaseManagerRead
import com.example.myfirstapp.databinding.RlLayoutMindClassesListBinding
import com.example.myfirstapp.fragment.start.body.RLFragBodyClassesView
import com.example.myfirstapp.model.RLFulllVideoModel
import com.example.myfirstapp.model.RLVideoModel
import com.google.gson.Gson

class RLBodyClassListAdapter(private val dataList: List<RLVideoModel>, val context: FragmentActivity?,val ride:Boolean) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TAG = "RLBodyClassListAdapter"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutbinding: RlLayoutMindClassesListBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.rl_layout_mind_classes_list , parent, false)
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
    inner class MyViewHolder(layoutBinding: RlLayoutMindClassesListBinding) : RecyclerView.ViewHolder(layoutBinding.root) {
        private val layoutBinding: RlLayoutMindClassesListBinding = layoutBinding
        fun bindData(position: Int, itemVIew: View) {
            val cardData = dataList[position]
            try{
              //  RLGetVideoListBody(cardData.key,itemVIew)
                layoutBinding.txtClasstime.setText(cardData.duration)
                layoutBinding.txtUsername.setText(cardData.instructor)
                layoutBinding.txtClassname.setText(cardData.rideTitle)
                layoutBinding.txtVideoaudio.setText(cardData.difficulty)
                Glide.with(context!!).load(cardData.imageLinkInstructor)
                    //.placeholder(R.drawable.wellcome).error(R.drawable.wellcome)
                    .into(layoutBinding.imgUser)
                Glide.with(context).load(cardData.imageLinkrectangleV2)
                    // .placeholder(R.drawable.wellcome).error(R.drawable.wellcome)
                    .into(layoutBinding.imgMind)
                itemVIew.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putString("VIDEODATA",cardData.key)
                    bundle.putBoolean("Ride",ride)
                    (context as RLMainActivityRL).RLloadFrag(RLFragBodyClassesView().newInstance(bundle), TAG, true, null, false)
                }




            }catch (e:Exception){
                Log.e(TAG,"Exception:- ${e.message}")
            }
        }
      private fun RLGetVideoListBody(videoID:String,itemVIew: View) {
            val databaseManager= RLDatabaseManagerRead()
            databaseManager.RLRevoolaVideosRead(videoID){ data, error ->
                if (data != null) {
                    val gson = Gson()
                    val jsonObject = gson.toJson(data)
                    val VideoData = gson.fromJson(jsonObject, RLFulllVideoModel::class.java)
                    layoutBinding.txtClasstime.setText(VideoData.duration)
                    layoutBinding.txtUsername.setText(VideoData.instructor)
                    layoutBinding.txtClassname.setText(VideoData.rideTitle)
                    layoutBinding.txtVideoaudio.setText(VideoData.difficulty)
                    Glide.with(context!!).load(VideoData.imageLinkInstructor)
                        //.placeholder(R.drawable.wellcome).error(R.drawable.wellcome)
                        .into(layoutBinding.imgUser)
                    Glide.with(context).load(VideoData.imageLinkSquareV2)
                       // .placeholder(R.drawable.wellcome).error(R.drawable.wellcome)
                        .into(layoutBinding.imgMind)
                    itemVIew.setOnClickListener {
                        val bundle = Bundle()
                        bundle.putString("VIDEODATA",jsonObject)
                        bundle.putBoolean("Ride",ride)
                        (context as RLMainActivityRL).RLloadFrag(RLFragBodyClassesView().newInstance(bundle), TAG, true, null, false)
                    }
                }
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
