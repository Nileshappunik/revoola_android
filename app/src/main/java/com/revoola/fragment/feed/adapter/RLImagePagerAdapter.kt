package com.revoola.fragment.feed.adapter

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
import org.json.JSONArray
import org.json.JSONObject

class RLImagePagerAdapter(val context: FragmentActivity?, private val imageList: List<String>) :PagerAdapter(){

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

        layoutbinding.inlayChart.layEffortZone.txtName.setText(R.string.effortzone)
        layoutbinding.inlayChart.layEffortZone.txtNumber.setText(R.string.cardio)
        layoutbinding.inlayChart.layEffortZone.txtNumber.setTextColor(context.getColor(R.color.AppMainColor))

        layoutbinding.inlayChart.layEffort.txtName.setText("EFFORT %")
        layoutbinding.inlayChart.layEffort.txtNumber.setText("57%")

        layoutbinding.inlayChart.layEffortScore.txtName.setText("EFFORT SCORE")
        layoutbinding.inlayChart.layEffortScore.txtNumber.setText("468")

        layoutbinding.inlayChart.layMaxEffort.txtName.setText("MAX EFFORT %")
        layoutbinding.inlayChart.layMaxEffort.txtNumber.setText("84%")

        RLTools.RLheightsetdisplaywebview(layoutbinding.inlayChart.webViewChart,context)
        layoutbinding.inlayChart.webViewChart.webViewClient = WebViewClient()

        val webSettings: WebSettings = layoutbinding.inlayChart.webViewChart.settings
        webSettings.javaScriptEnabled = true
        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE
        webSettings.domStorageEnabled = true
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true

        val jsonArray = createJsonArray()
        layoutbinding.inlayChart.webViewChart.loadDataWithBaseURL(null,
            RLAllHTMLChart.RLGetNewZoneChartHtml1(jsonArray), "text/html", "UTF-8", null)

        container.addView(layoutbinding.root)
        return layoutbinding.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as RelativeLayout)
    }

    override fun getCount(): Int {
        return imageList.size
    }

    fun createJsonArray(): JSONArray {
        val data = listOf(
            mapOf("zone" to "Zone1", "value" to 1829, "color" to "rgb(241, 119, 160)"),
            mapOf("zone" to "Zone2", "value" to 2345, "color" to "rgb(255, 207, 47)"),
            mapOf("zone" to "Zone3", "value" to 1230, "color" to "rgb(44, 174, 44)"),
            mapOf("zone" to "Zone4", "value" to 2310, "color" to "rgb(0, 153, 218)"),
            mapOf("zone" to "Zone5", "value" to 560, "color" to "rgb(254, 105, 02)"),
            mapOf("zone" to "Zone6", "value" to 680, "color" to "rgb(153, 0, 204)"),
            mapOf("zone" to "Zone7", "value" to 120, "color" to "rgb(237, 69, 65)")
        )

        val jsonArray = JSONArray()

        for (item in data) {
            val jsonObject = JSONObject()
            jsonObject.put("zone", item["zone"])
            jsonObject.put("value", item["value"])
            jsonObject.put("color", item["color"])
            jsonArray.put(jsonObject)
        }

        return jsonArray
    }

}