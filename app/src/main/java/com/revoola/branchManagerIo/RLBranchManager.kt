package com.revoola.branchManagerIo

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth
import com.revoola.commonobject.RLTools
import io.branch.indexing.BranchUniversalObject
import io.branch.referral.util.LinkProperties
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream


class RLBranchManager (val context: Context) {
    private val TAG= RLBranchManager::class.java.simpleName

    private var page: String = ""
    private var pageType: String = ""
    private var classType: Int = 0
    private var id: String = ""
    private var userCode: String = ""
    private var company: String = ""
    private var fromLink: String = ""
    private var plan: String = ""
    private var inviteUserType: String = ""
    private var inviteUserSubsModel: String = ""
    private var commisionFlag: String = ""
    private var referrerLink: String? = null
    private var isFresh: Boolean = true
    private val auth = FirebaseAuth.getInstance()

     fun RLGenerateBranchLinkold(className:String,instructor:String,avatar: String) :String {
         var shareUrl=""
        val branchUniversalObject = BranchUniversalObject()
            .setTitle("${className} by ${instructor}")
            .setContentDescription("${className} is a great session led by ${instructor}. Check it out!")
            .setContentImageUrl(avatar)
            .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)

        val linkProperties = LinkProperties()
            .addTag("tag1")
            .addControlParameter("key", "value")

        branchUniversalObject.generateShortUrl(context, linkProperties) { url, error ->
            if (error == null) {
                shareUrl=url.toString()
               RLTools.RlLogEPrint(TAG,"Generated Branch Link: $url")
                RLShareUrl(shareUrl)
            } else {
               RLTools.RlLogEPrint(TAG,"Branch Error: ${error.message}")
            }
        }
         return shareUrl
    }

    fun RLGenerateBranchLink(className: String, instructor: String, avatar: String): String {
        var shareUrl = ""

        // Create a BranchUniversalObject with properties
        val branchUniversalObject = BranchUniversalObject()
            .setTitle("$className by $instructor")
            .setContentDescription("$className is a great session led by $instructor. Check it out!")
            .setContentImageUrl(avatar)
            .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)


        // Set link properties and control parameters
        val linkProperties = LinkProperties()
            .addTag("share")
            .addControlParameter("key", "value")
            .addControlParameter("\$deeplink_path", "https://www.revoola.com?user=userid&trialDays=60&isForExistingUser=true&inc_chall=mainTitle")


        // Generate the short URL asynchronously
        branchUniversalObject.generateShortUrl(context, linkProperties) { url, error ->
            if (error == null) {
                shareUrl = url.toString() // Get the generated short URL
               RLTools.RlLogEPrint(TAG, "Generated Branch Link: $url")
               // RLShareUrl(shareUrl) // You can call a function to handle the generated URL
                shareContent(className,instructor,shareUrl)
            } else {
               RLTools.RlLogEPrint(TAG, "Branch Error: ${error.message}")
            }
        }

        // Return the URL (may be empty or not updated immediately, depends on asynchronous call)
        return shareUrl
    }

    fun RLShareUrl(url: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, url)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share link via"))
    }

    fun RLCaptureScreen(activity: Activity): Bitmap {
        val view = activity.window.decorView
        view.isDrawingCacheEnabled = true
        val bitmap = Bitmap.createBitmap(view.drawingCache)
        view.isDrawingCacheEnabled = false
        return bitmap
    }

    fun RLCaptureSpecificView(view: CardView): Bitmap {
        // Create a Bitmap with the size of the view
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        // Render the view onto the canvas
        view.draw(canvas)
        return bitmap
    }

    fun RLSaveBitmapToInternalStorage(bitmap: Bitmap, filename: String): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$filename.png")
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MyApp") // Save in Pictures/MyApp folder
            }
        }

        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let {
            try {
                val outputStream: OutputStream? = context.contentResolver.openOutputStream(it)
                outputStream?.use { stream ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }
        return uri
    }

    fun RLBitmapToUri(bitmap: Bitmap): Uri? {
        val contentResolver = context.contentResolver
        var imageUri: Uri? = null

        try {
            // Prepare the ContentValues for the image metadata
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.TITLE, "image_${System.currentTimeMillis()}")
                put(MediaStore.Images.Media.DISPLAY_NAME, "image.jpg")
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MyApp") // Save in Pictures folder
            }

            // Insert the image into the MediaStore
            val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

            // If URI insertion is successful, write the image to it
            uri?.let {
                contentResolver.openOutputStream(it)?.use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    imageUri = it // Set the URI for the inserted image
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return imageUri
    }

    fun RLSaveImageToCache(bitmap: Bitmap): Uri? {
        val cachePath = File(context.cacheDir, "images")
        return try {
            cachePath.mkdirs()
            val file = File(cachePath, "screenshot.jpg")
            val fileOutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
            Uri.fromFile(file)
        } catch (e: IOException) {
           RLTools.RlLogEPrint(TAG, "Error saving image: ${e.message}")
            null
        }
    }

    fun RLShareImage(uri: Uri) {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/jpeg"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share Image"))
        } catch (e: Exception) {
           RLTools.RlLogEPrint("PhotoShot", "Error sharing image: ${e.message}")
        }
    }

    private fun shareContent(className: String,instructor:String, url: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "${className} by ${instructor}")
            putExtra(Intent.EXTRA_TEXT, "${className} is a great session led by ${instructor}. Check it out! $url")
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share via"))
    }

    private fun RLParseDeepLink(deepLink: String) {
        deepLink.split("&").forEach { param ->
            when {
                param.contains("user=") -> userCode = param.replace("user=", "").replace(Regex("[^A-Za-z0-9]"), "")
                param.contains("company=") -> company = param.replace("company=", "")
                param.contains("fromLink=") -> fromLink = param.replace("fromLink=", "")
                param.contains("pl=") -> plan = param.replace("pl=", "")
                param.contains("utp=") -> inviteUserType = param.replace("utp=", "")
                param.contains("usm=") -> inviteUserSubsModel = param.replace("usm=", "")
                param.contains("com=") -> commisionFlag = param.replace("com=", "")
                else -> RLTools.RlLogDPrint("BranchService", "Unknown deep link parameter: $param")
            }
        }
    }

    private fun showJoinChallengeAlert() {
        AlertDialog.Builder(context)
            .setTitle("Success")
            .setMessage("Challenge joined successfully.")
            .setPositiveButton("Ok") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    fun generateBranchLink() {
        val branchUniversalObject = BranchUniversalObject()
            .setTitle("Challenges")
            .setContentDescription("This is a great session led by the instructor!")
            .setContentImageUrl("https://example.com/image.jpg")
            .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)

        val linkProperties = LinkProperties()
            .addTag("Session Challenges")
            .addControlParameter("UserName", "Nilesh Bhutka") // Optional custom parameters
            .addControlParameter("\$deeplink_path", "https://www.revoola.com?user=userid&trialDays=60")

        // Generate the Branch URL
        branchUniversalObject.generateShortUrl(context, linkProperties) { url, error ->
            if (error == null) {
                RLTools.RlLogDPrint("Branch", "Generated Branch Link: $url")
                // You can share the URL or use it in your app
                shareLink(url.toString())
            } else {
               RLTools.RlLogEPrint("Branch", "Error generating Branch URL: ${error.message}")
            }
        }
    }

    fun shareLink(url: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Check this out!")
            putExtra(Intent.EXTRA_TEXT, "Here is a great offer: $url")
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share via"))
    }


}
