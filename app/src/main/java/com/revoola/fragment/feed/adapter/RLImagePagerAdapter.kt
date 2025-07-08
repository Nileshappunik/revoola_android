package com.revoola.fragment.feed.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebViewClient
import android.widget.RelativeLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.revoola.R
import com.revoola.databinding.RlItemPageBinding
import com.revoola.services.RLAllHTMLChart
import com.revoola.commonobject.RLTools
import com.revoola.model.EffortZoneFeedModel
import com.revoola.model.RLZoneChartData

class RLImagePagerAdapter(
    val context: FragmentActivity?, private val imageList: List<String>,
    private val zoneData: List<RLZoneChartData>,
    private val ZoneTextData: EffortZoneFeedModel,
    private val effort: String,
    private val effortScore: String,
    private val maxEffort: String,
    private val listener: OnImageClickListener
) :PagerAdapter(){

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as RelativeLayout
    }
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutbinding:RlItemPageBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.rl_item_page , container, false)
        if (imageList.get(position).equals("CHART")){
            layoutbinding.imageView.visibility=View.GONE
            layoutbinding.inlayChart.layAll.visibility=View.VISIBLE
        }else{
            layoutbinding.imageView.visibility=View.VISIBLE
            layoutbinding.inlayChart.layAll.visibility=View.GONE
        }
        Glide.with(context!!).load(imageList.get(position)).into(layoutbinding.imageView)

        layoutbinding.imageView.setOnClickListener {
            listener.onImageClick(position, imageList[position])
        }

        //Chart Set
        layoutbinding.inlayChart.layEffortZone.txtName.setText(R.string.effortzone)
        layoutbinding.inlayChart.layEffortZone.txtNumber.setText(ZoneTextData.efforZoneText)
        layoutbinding.inlayChart.layEffortZone.txtNumber.setTextColor(Color.parseColor(ZoneTextData.efforZoneTxtClr))
        layoutbinding.inlayChart.layAll.setBackgroundColor(Color.parseColor(ZoneTextData.efforZoneBgrClr))
        layoutbinding.inlayChart.relayChart.setBackgroundColor(Color.parseColor(ZoneTextData.efforZoneBgrClr))
        layoutbinding.inlayChart.webViewChart.setBackgroundColor(Color.parseColor(ZoneTextData.efforZoneBgrClr))
        val efforZoneBgrClr =  ZoneTextData.efforZoneBgrClr

        layoutbinding.inlayChart.layEffort.txtName.setText("EFFORT %")
        layoutbinding.inlayChart.layEffort.txtNumber.setText(effort)

        layoutbinding.inlayChart.layEffortScore.txtName.setText("EFFORT SCORE")
        layoutbinding.inlayChart.layEffortScore.txtNumber.setText(effortScore)

        layoutbinding.inlayChart.layMaxEffort.txtName.setText("MAX EFFORT %")
        layoutbinding.inlayChart.layMaxEffort.txtNumber.setText(maxEffort)

        RLTools.RLheightsetdisplaywebview(layoutbinding.inlayChart.webViewChart,context)
        layoutbinding.inlayChart.webViewChart.webViewClient = WebViewClient()

        val webSettings: WebSettings = layoutbinding.inlayChart.webViewChart.settings
        webSettings.javaScriptEnabled = true
        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE
        webSettings.domStorageEnabled = true
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true

        layoutbinding.inlayChart.webViewChart.loadDataWithBaseURL(null,
            RLAllHTMLChart.RLGetNewZoneChartHtml(zoneData,efforZoneBgrClr), "text/html", "UTF-8", null)

        container.addView(layoutbinding.root)
        return layoutbinding.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as RelativeLayout)
    }

    override fun getCount(): Int {
        return imageList.size
    }

}
interface OnImageClickListener {
    fun onImageClick(position: Int, imageUrl: String)
}