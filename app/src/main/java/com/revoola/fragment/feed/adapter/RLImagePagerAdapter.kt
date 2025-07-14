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
        val layoutBinding:RlItemPageBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.rl_item_page , container, false)
        if (imageList.get(position).equals("CHART")){
            layoutBinding.imageView.visibility=View.GONE
            layoutBinding.inlayChart.layAll.visibility=View.VISIBLE
        }else{
            layoutBinding.imageView.visibility=View.VISIBLE
            layoutBinding.inlayChart.layAll.visibility=View.GONE
        }
        Glide.with(context!!).load(imageList.get(position)).into(layoutBinding.imageView)

        layoutBinding.imageView.setOnClickListener {
            listener.onImageClick(position, imageList[position])
        }

        //Chart Set
        layoutBinding.inlayChart.layEffortZone.txtName.setText(R.string.effortzone)
        layoutBinding.inlayChart.layEffortZone.txtNumber.setText(ZoneTextData.efforZoneText)
        layoutBinding.inlayChart.layEffortZone.txtNumber.setTextColor(Color.parseColor(ZoneTextData.efforZoneTxtClr))
        layoutBinding.inlayChart.layAll.setBackgroundColor(Color.parseColor(ZoneTextData.efforZoneBgrClr))
        layoutBinding.inlayChart.relayChart.setBackgroundColor(Color.parseColor(ZoneTextData.efforZoneBgrClr))
        layoutBinding.inlayChart.webViewChart.setBackgroundColor(Color.parseColor(ZoneTextData.efforZoneBgrClr))
        val efforZoneBgrClr =  ZoneTextData.efforZoneBgrClr

        layoutBinding.inlayChart.layEffort.txtName.setText("EFFORT %")
        layoutBinding.inlayChart.layEffort.txtNumber.setText(effort)

        layoutBinding.inlayChart.layEffortScore.txtName.setText("EFFORT SCORE")
        layoutBinding.inlayChart.layEffortScore.txtNumber.setText(effortScore)

        layoutBinding.inlayChart.layMaxEffort.txtName.setText("MAX EFFORT %")
        layoutBinding.inlayChart.layMaxEffort.txtNumber.setText(maxEffort)

        RLTools.rl_heightsetdisplaywebview(layoutBinding.inlayChart.webViewChart,context)
        layoutBinding.inlayChart.webViewChart.webViewClient = WebViewClient()

        val webSettings: WebSettings = layoutBinding.inlayChart.webViewChart.settings
        webSettings.javaScriptEnabled = true
        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE
        webSettings.domStorageEnabled = true
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true

        layoutBinding.inlayChart.webViewChart.loadDataWithBaseURL(null,
            RLAllHTMLChart.rl_getNewZoneChartHtml(zoneData,efforZoneBgrClr), "text/html", "UTF-8", null)

        container.addView(layoutBinding.root)
        return layoutBinding.root
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