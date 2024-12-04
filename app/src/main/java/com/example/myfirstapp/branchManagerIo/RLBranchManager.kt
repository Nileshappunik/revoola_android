package com.example.myfirstapp.branchManagerIo

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.core.content.FileProvider
import io.branch.indexing.BranchUniversalObject
import io.branch.referral.util.LinkProperties
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class RLBranchManager (val context: Context) {
    private val TAG=RLBranchManager::class.java.simpleName

     fun RLGenerateBranchLink() :String {
         var shareUrl=""
        val branchUniversalObject = BranchUniversalObject()
            .setTitle("Create Link Title")
            .setContentDescription("Description")
            .setContentImageUrl("https://dummyimage.com/300/09f/fff.png")
            .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)

        val linkProperties = LinkProperties()
            .addTag("tag1")
            .addControlParameter("key", "value")

        branchUniversalObject.generateShortUrl(context, linkProperties) { url, error ->
            if (error == null) {
                shareUrl=url.toString()
                Log.e(TAG,"Generated Branch Link: $url")
                RLShareUrl(shareUrl)
            } else {
                Log.e(TAG,"Branch Error: ${error.message}")
            }
        }
         return shareUrl
    }

     fun RLShareUrl(url: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, url)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share link via"))
    }

    fun captureScreen(activity: Activity): Bitmap {
        val view = activity.window.decorView
        view.isDrawingCacheEnabled = true
        val bitmap = Bitmap.createBitmap(view.drawingCache)
        view.isDrawingCacheEnabled = false
        return bitmap
    }

    fun saveImageToCache(bitmap: Bitmap): Uri? {
        val cachePath = File(context.cacheDir, "images")
        return try {
            cachePath.mkdirs()
            val file = File(cachePath, "screenshot.jpg")
            val fileOutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
            FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
            //Uri.fromFile(file)
        } catch (e: IOException) {
            Log.e("PhotoShot", "Error saving image: ${e.message}", e)
            null
        }
    }

     fun shareImage(uri: Uri) {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/jpeg"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share Image"))
        } catch (e: Exception) {
            Log.e("PhotoShot", "Error sharing image: ${e.message}", e)
        }
    }
}
