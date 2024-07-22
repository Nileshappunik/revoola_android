package com.example.myfirstapp.utils

import android.graphics.drawable.PictureDrawable
import android.os.AsyncTask
import android.widget.ImageView
import com.caverock.androidsvg.SVG
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

fun ImageView.loadSvg(url: String) {
    SvgLoaderTask(this).execute(url)
}

private class SvgLoaderTask(private val imageView: ImageView) : AsyncTask<String, Void, PictureDrawable?>() {
    override fun doInBackground(vararg params: String): PictureDrawable? {
        val url = params[0]
        return try {
            val inputStream = fetchSvg(url)
            val svg = SVG.getFromInputStream(inputStream)
            val picture = svg.renderToPicture()
            PictureDrawable(picture)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun onPostExecute(drawable: PictureDrawable?) {
        if (drawable != null) {
            imageView.setLayerType(ImageView.LAYER_TYPE_SOFTWARE, null)
            imageView.setImageDrawable(drawable)
        }
    }

    private fun fetchSvg(url: String): InputStream {
        val urlConnection = URL(url).openConnection() as HttpURLConnection
        urlConnection.connect()
        return urlConnection.inputStream
    }
}
