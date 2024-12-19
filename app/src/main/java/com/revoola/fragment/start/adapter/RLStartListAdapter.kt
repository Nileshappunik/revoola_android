package com.revoola.fragment.start.adapter

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.revoola.R
import com.revoola.activity.RLMainActivityRL
import com.revoola.databinding.RlLayoutStartMenuBinding
import com.revoola.enumclass.RLStartAllMenuModel
import com.revoola.fragment.friends.RLFragFindOnRevoola
import com.revoola.fragment.friends.RLFragYourFriends
import com.revoola.fragment.friends.RLFragYourGroup
import com.revoola.fragment.start.body.RLFragBodyClasses
import com.revoola.fragment.start.challenges.RLFragChalengesType
import com.revoola.fragment.start.mind.RLFragMindClasses
import com.revoola.fragment.start.yourway.RLFragYourWay
import com.revoola.utils.loadSvg

class RLStartListAdapter(
    val context: FragmentActivity?,
    val dataList: List<RLStartAllMenuModel>,
    val heightTotal: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TAG = "RLStartListAdapter"
    var bundle: Bundle = Bundle()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutBinding: RlLayoutStartMenuBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.rl_layout_start_menu , parent, false)
        return MyViewHolder(layoutBinding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder) {
            holder.bindData(position, holder.itemView)
        }
    }

    override fun getItemCount(): Int {
        return  dataList.size
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
            layoutParams.height =  heightTotal/4
            layoutBinding.relayStartNew.layoutParams =layoutParams


            //Image Height Width set
            val layoutParamsImage: ViewGroup.LayoutParams = layoutBinding.imgType.layoutParams
            layoutParamsImage.height =  heightTotal/5
            layoutParamsImage.width =  heightTotal/5
            layoutBinding.imgType.layoutParams =layoutParamsImage

            layoutBinding.relayStartNew.setOnClickListener {
                if (cardData.title.toLowerCase().equals("challenges")){
                    
                    (context as RLMainActivityRL).RLloadFrag(RLFragChalengesType(), TAG, true, null, false)

                }else if (cardData.title.toLowerCase().equals("your way")){
                    
                    (context as RLMainActivityRL).RLloadFrag(RLFragYourWay(), TAG, true, null, false)

                }else if (cardData.title.toLowerCase().equals("mind classes")){
                    
                    (context as RLMainActivityRL).RLloadFrag(RLFragMindClasses(), TAG, true, null, false)

                }else if (cardData.title.toLowerCase().equals("body classes")){
                    
                    (context as RLMainActivityRL).RLloadFrag(RLFragBodyClasses(), TAG, true, null, false)

                }else if (cardData.title.toLowerCase().equals("find on revoola")){
                    
                    (context as RLMainActivityRL).RLloadFrag(RLFragFindOnRevoola(), TAG, true, null, false)

                }else if (cardData.title.toLowerCase().equals("your friends")){
                    val bundle =Bundle ()
                    bundle.putBoolean("reDirecDeepLinkPage",false)
                    (context as RLMainActivityRL).RLloadFrag(RLFragYourFriends().newInstance(bundle), TAG, true,null, false)

                }else if (cardData.title.toLowerCase().equals("your groups")){
                    
                    (context as RLMainActivityRL).RLloadFrag(RLFragYourGroup(), TAG, true, null, false)

                }else if (cardData.title.toLowerCase().equals("invite to join")){
                    val shareIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, "SHARE LINK")
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Share via"))
                  //  
                  //  (context as RLMainActivityRL).RLloadFrag(RLFragInviteFriends(), TAG, true, null, true)

                }

            }
        }
    }

}
