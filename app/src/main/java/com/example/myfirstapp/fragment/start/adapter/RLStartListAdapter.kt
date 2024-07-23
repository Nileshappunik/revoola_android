package com.example.myfirstapp.fragment.start.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myfirstapp.R
import com.example.myfirstapp.databinding.RlLayoutStartListBinding
import com.example.myfirstapp.enumclass.RLStartType

class RLStartListAdapter(val context: FragmentActivity?, val  dataList: List<RLStartType>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TAG = "RLStartListAdapter"
    var bundle: Bundle = Bundle()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutbinding: RlLayoutStartListBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.rl_layout_start_list , parent, false)
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


    inner class MyViewHolder(layoutBinding: RlLayoutStartListBinding) : RecyclerView.ViewHolder(layoutBinding.root) {
        private val layoutBinding: RlLayoutStartListBinding = layoutBinding
        fun bindData(position: Int, itemVIew: View) {

           /* itemVIew.post{
                val width = itemVIew.width
                val newHeight = width * 1
                // Set the new height to the itemView
                val layoutParams =itemVIew.layoutParams
                layoutParams.height = newHeight
                itemVIew.layoutParams = layoutParams
            }*/
            val cardData = dataList[position]
            layoutBinding.txtTypename.setText(cardData.title)
            layoutBinding.txtDescription.setText(context!!.getString(cardData.description))
            Glide.with(context).load(cardData.img).into(layoutBinding.imgType)
            Glide.with(context).load(cardData.type).into(layoutBinding.imgTypeicon)
            layoutBinding.relayStart.visibility=View.GONE
            layoutBinding.relayStartNew.visibility=View.VISIBLE

           /* if (name.equals("end")){
                layoutBinding.txtName.visibility=View.GONE
                layoutBinding.imgFull.visibility=View.GONE
                layoutBinding.txtLast.visibility=View.VISIBLE
            }
            else{
                layoutBinding.txtName.visibility=View.VISIBLE
                layoutBinding.imgFull.visibility=View.VISIBLE
                layoutBinding.txtLast.visibility=View.GONE
                layoutBinding.txtName.setText(name.toUpperCase())
                if (name.equals("Classes")){
                    Glide.with(context!!).load(R.drawable.classes).into(layoutBinding.imgFull)
                }else{
                    Glide.with(context!!).load(image).into(layoutBinding.imgFull)
                }

                layoutBinding.relayStart.setOnClickListener {
                    if (name.equals("Challenges")){
                        (context as RLMainActivityRL).RLhidebottombarcolorwhite()
                        (context as RLMainActivityRL).RLloadFrag(RLFragChalengesType(), TAG, true, RLFragChalengesType::class.java.simpleName, false)

                    }else if (name.equals("Your Way")){
                        (context as RLMainActivityRL).RLshowbottombarcolorwhite()
                        (context as RLMainActivityRL).RLloadFrag(RLFragYourWay(), TAG, true, RLFragYourWay::class.java.simpleName, false)

                    }else if (name.equals("Classes")){
                        (context as RLMainActivityRL).RLshowbottombarcolorwhite()
                        (context as RLMainActivityRL).RLloadFrag(RLFragClasses(), TAG, true, RLFragClasses::class.java.simpleName, false)
                    }

                }
            }*/

        }
    }


}
