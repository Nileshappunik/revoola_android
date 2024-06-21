package com.example.myfirstapp.fragment.feed.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.fragment.app.FragmentActivity
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.example.myfirstapp.R
import java.util.Objects

class RLImagePagerAdapter(val context: FragmentActivity?, private val imageList: List<String>) :PagerAdapter(){

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as RelativeLayout
    }
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val mLayoutInflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val itemView: View = mLayoutInflater.inflate(R.layout.rl_item_page, container, false)
        val imageView: ImageView = itemView.findViewById<View>(R.id.imageView) as ImageView

        Glide.with(context).load(imageList.get(position)).into(imageView)
        Objects.requireNonNull(container).addView(itemView)

        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as RelativeLayout)
    }

    override fun getCount(): Int {
        return imageList.size
    }

}