package com.example.myfirstapp.fragment.start.mind

import android.Manifest
import android.app.Dialog
import android.app.DownloadManager
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.myfirstapp.RLBaseFragment
import com.example.myfirstapp.R
import com.example.myfirstapp.activity.RLMainActivityRL
import com.example.myfirstapp.databasefirebase.RLDatabaseManagerRead
import com.example.myfirstapp.databinding.RlFragMindClassesViewBinding
import com.example.myfirstapp.fragment.start.classes.RLClassesSchedule
import com.example.myfirstapp.model.RLFulllVideoModel
import com.example.myfirstapp.utils.RLConstants
import com.example.myfirstapp.utils.RLPrefManager
import com.google.gson.Gson
import java.util.UUID

class RLFragMindClassesView : RLBaseFragment() {
    val TAG: String = RLFragMindClassesView::class.java.simpleName
    lateinit var fragBinding: RlFragMindClassesViewBinding
    private val PERMISSION_REQUEST_CODE = 1001
    var videoLink=""
    private val binding by lazy {
        RlFragMindClassesViewBinding.inflate(layoutInflater)
    }
    fun newInstance(bundle: Bundle?): Fragment {
        val fragment = RLFragMindClassesView()
        fragment.arguments = bundle
        return fragment
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        fragBinding = RLinflateBindLayout(activity?.javaClass,inflater, R.layout.rl_frag_mind_classes_view, container) as RlFragMindClassesViewBinding
        RLPrefManager.RLsetSomeStringValue(activity, RLPrefManager.current_fragment,"RLFragMindClassesView" )
        RLuisetup()
        return fragBinding.root
    }
    private fun RLuisetup() {
        RLonBackPresAct(fragBinding.ivBack)
        val VideoID=  requireArguments().getString("VIDEODATA","")
        val audioVideoType=  requireArguments().getString("AUDIOVIDEOTYPE","")
        fragBinding.layWorklog.visibility=View.GONE
        fragBinding.viewTimevideo.visibility=View.GONE
        fragBinding.txtVideo.setTextColor(resources.getColor(R.color.AppTextGrayColor))

        fragBinding.inlaySchdual.txtTitle.setText(R.string.schedule)
        fragBinding.inlaySchdual.imgIcon.setImageResource(R.drawable.ic_calendar_today)
        fragBinding.inlayDownload.txtTitle.setText(R.string.download)
        fragBinding.inlayDownload.imgIcon.setImageResource(R.drawable.ic_download)
        fragBinding.inlayFavourite.txtTitle.setText(R.string.favourite)
        fragBinding.inlayFavourite.imgIcon.setImageResource(R.drawable.ic_saved)
        fragBinding.linearLayout.weightSum = 3f
        fragBinding.inlayShare.txtTitle.setText(R.string.share)
        fragBinding.inlayShare.imgIcon.setImageResource(R.drawable.ic_share)
        fragBinding.inlayShare.relativeCommon.visibility=View.GONE

        fragBinding.txtVideo.setText(audioVideoType)
        if (audioVideoType.equals("Video")){
            fragBinding.imgVideo.setImageResource(R.drawable.ic_video)
        }else{
            fragBinding.imgVideo.setImageResource(R.drawable.ic_audio)
        }
        val databaseManager= RLDatabaseManagerRead()
        databaseManager.RLRevoolaVideosMindRead(VideoID){ data, error ->
            if (data != null) {
                val gson = Gson()
                val jsonObject = gson.toJson(data)
                val VideoData = gson.fromJson(jsonObject, RLFulllVideoModel::class.java)
                RLMindUiSetup(VideoData)
                RLClickToSechedule(jsonObject,RLConstants.MIND,audioVideoType)
                fragBinding.btnStartclass.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putString("VIDEODATA",jsonObject)
                    bundle.putString("AUDIOVIDEOTYPE",audioVideoType)
                    (context as RLMainActivityRL).RLloadFrag(RLFragMindClassSensorChooes().newInstance(bundle), TAG, true,null, false)
                }
                fragBinding.inlayDownload.imgIcon.setOnClickListener {
                    videoLink=VideoData.videoLinkiPhonex.toString()
                    if (RLCheckPermissions()) {
                        RLDownloadVideo(VideoData.videoLinkiPhonex)
                    } else {
                        RLRequestPermissions()
                    }
                }
            }
        }

    }

    private fun RLClickToSechedule(data: String, classtype: String?, audioVideoType: String?) {
        //RLshowSubscribeDialog()
        fragBinding.inlaySchdual.relativeCommon.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("VIDEODATA",data)
            bundle.putString(RLConstants.CLASSTYPE,classtype)
            bundle.putString("AUDIOVIDEOTYPE",audioVideoType)
            (context as RLMainActivityRL).RLloadFrag(RLClassesSchedule().newInstance(bundle), TAG, true,null, false)
        }
    }
    private fun RLMindUiSetup(VideoData:RLFulllVideoModel){
        fragBinding.txtTitle.setText(VideoData.rideTitle)
        fragBinding.txtVideoTitle.setText(VideoData.rideTitle)
        fragBinding.txtNamewith.setText(VideoData.instructor)
        fragBinding.txtTrainerName.setText(VideoData.instructor)
        fragBinding.txtTotalClass.setText(VideoData.instructorClasses+" CLASSES")
        fragBinding.txtVideoDescription.setText(VideoData.rideDescription)
        fragBinding.txtMinutes.setText(VideoData.duration+" CLASS")
        Glide.with(requireContext()).load(VideoData.imageLinkInstructor)
            //.placeholder(R.drawable.sample_user).error(R.drawable.sample_user)
            .into(fragBinding.imgTraner)
        Glide.with(requireContext()).load(VideoData.imageLinkSquareV2)
            //.placeholder(R.drawable.sample_user).error(R.drawable.sample_user)
            .into(fragBinding.imgMainBanner)
    }
    private fun RLCheckPermissions(): Boolean {
        val writePermission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return writePermission == PackageManager.PERMISSION_GRANTED
    }
    private fun RLRequestPermissions() {
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                RLDownloadVideo(videoLink)
            }
        }
    }
    private fun RLDownloadVideo(url: String) {
        val uniqueFileName = "video_${UUID.randomUUID()}.mp4"
        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle("Downloading video")
            .setDescription("Downloading a video file")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, uniqueFileName)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        val downloadManager = requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
    }

    private fun RLshowSubscribeDialog() {
        val sucDialog: Dialog = Dialog(requireContext())
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